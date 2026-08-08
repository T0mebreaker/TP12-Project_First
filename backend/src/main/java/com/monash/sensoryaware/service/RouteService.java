package com.monash.sensoryaware.service;

import static com.monash.sensoryaware.dto.ApiDtos.*;

import com.monash.sensoryaware.exception.OutsideCoverageException;
import com.monash.sensoryaware.model.HourlyReading;
import com.monash.sensoryaware.model.SensorLocation;
import com.monash.sensoryaware.repository.DatasetRepository;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RouteService {
    public static final double HIGH_THRESHOLD = 60.0;
    public static final String CLASSIFICATION_THRESHOLD = "High > 60 pedestrians/min; Low ≤ 60";
    public static final String RESULT_LIMITATION = "This result is based on available pedestrian data and historical patterns only. Actual conditions may differ.";
    public static final String PREDICTION_LIMITATION = "This prediction is based on historical pedestrian patterns and is not a guarantee of future conditions.";
    private static final ZoneId MELBOURNE = ZoneId.of("Australia/Melbourne");

    private final DatasetRepository repository;
    private final boolean demoMode;

    public RouteService(DatasetRepository repository, @Value("${app.demo-mode:true}") boolean demoMode) {
        this.repository = repository;
        this.demoMode = demoMode;
    }

    public List<LocationReference> supportedLocations() {
        return repository.sensors().stream().map(this::toLocationReference).toList();
    }

    public RouteResponse generate(RouteRequest request) {
        if (request.originId().equals(request.destinationId())) {
            throw new IllegalArgumentException("Origin and destination must be different supported locations.");
        }
        String scenario = normalizeScenario(request.scenario());
        if ("OUTSIDE_COVERAGE".equals(scenario)) {
            throw new OutsideCoverageException("No supported pedestrian sensor coverage is available for this prototype search. Revise the origin or destination.");
        }
        SensorLocation originSensor = repository.sensor(parseId(request.originId()))
                .orElseThrow(() -> new OutsideCoverageException("Origin is outside the supported Melbourne CBD sensor coverage."));
        SensorLocation destinationSensor = repository.sensor(parseId(request.destinationId()))
                .orElseThrow(() -> new OutsideCoverageException("Destination is outside the supported Melbourne CBD sensor coverage."));
        PeakHourContext peak = new PeakHourContext(
                isPeakHour(request.departureTime()),
                isPeakHour(request.departureTime()) ? "Peak-hour route check" : "Route check",
                request.departureTime(),
                MELBOURNE.getId()
        );

        List<RouteOption> routes = new ArrayList<>();
        routes.add(buildRoute("route-a", "Route A · Direct walk", originSensor, originSensor, destinationSensor, request.departureTime(), 1.08, 0.0, scenario, 0));
        routes.add(buildRoute("route-b", "Route B · Alternative walk", destinationSensor, originSensor, destinationSensor, request.departureTime(), 1.22, 0.0017, scenario, 1));
        routes.add(buildRoute("route-c", "Route C · Via alternative street", null, originSensor, destinationSensor, request.departureTime(), 1.16, -0.0015, scenario, 2));

        boolean hasHigh = routes.stream().anyMatch(r -> "High".equals(r.sensoryClassification()));
        if (hasHigh) {
            for (int i = 0; i < routes.size(); i++) {
                RouteOption r = routes.get(i);
                if ("Low".equals(r.sensoryClassification())) {
                    String lowerName = r.name().contains("Lower-stimulation alternative")
                            ? r.name()
                            : r.name() + " · Lower-stimulation alternative";
                    routes.set(i, new RouteOption(
                            r.id(), lowerName, r.geometry(), r.walkingTimeMinutes(), r.distanceKm(),
                            r.sensoryClassification(), r.averagePedestriansPerMinute(), r.classificationThreshold(), r.sensorId(), r.sensorName(),
                            true, r.dataStatus(), r.highCongestionSegment(), r.prediction(), r.nearbyTransport(), r.limitations(), r.isIllustrative()
                    ));
                    break;
                }
            }
        }

        String dataSource = "DATA_DRIVEN".equals(scenario)
                ? "City of Melbourne cleaned FIT5120 pedestrian datasets packaged with this prototype"
                : "Illustrative demo scenario override layered on the packaged City of Melbourne dataset";

        return new RouteResponse(
                request,
                toLocationReference(originSensor),
                toLocationReference(destinationSensor),
                peak,
                routes,
                dataSource,
                LocalDateTime.now(MELBOURNE).toString(),
                RESULT_LIMITATION,
                scenario
        );
    }

    private RouteOption buildRoute(
            String id,
            String name,
            SensorLocation associatedSensor,
            SensorLocation origin,
            SensorLocation destination,
            LocalDateTime departure,
            double distanceFactor,
            double lateralOffset,
            String scenario,
            int routeIndex
    ) {
        Double average = associatedSensor == null ? null : routeAverage(associatedSensor.id(), departure).orElse(null);
        if (demoMode) {
            average = overrideAverage(scenario, routeIndex, average);
        }
        String classification = average == null ? "Data unavailable" : average > HIGH_THRESHOLD ? "High" : "Low";
        String dataStatus = average == null ? "unavailable" : "available";
        String highSegment = "High".equals(classification) && associatedSensor != null ? associatedSensor.description() + " segment" : null;
        PredictionResult prediction = prediction(associatedSensor, departure, scenario, routeIndex);
        double distanceKm = round2(haversineKm(origin.latitude(), origin.longitude(), destination.latitude(), destination.longitude()) * distanceFactor);
        int minutes = Math.max(4, (int) Math.round(distanceKm / 4.8 * 60));
        List<Coordinates> geometry = geometry(origin, destination, lateralOffset, routeIndex);
        List<String> limitations = average == null
                ? List.of("Data unavailable — no usable pedestrian data was found for this route.", RESULT_LIMITATION)
                : List.of(RESULT_LIMITATION, "Route geometry is a prototype polyline and is not turn-by-turn navigation.");

        return new RouteOption(
                id,
                name,
                new RouteGeometry(geometry, "Prototype route geometry generated by the Spring Boot backend", "prototype"),
                minutes,
                distanceKm,
                classification,
                average == null ? null : round1(average),
                average == null ? "No usable pedestrian data → Data unavailable" : CLASSIFICATION_THRESHOLD,
                associatedSensor == null ? null : String.valueOf(associatedSensor.id()),
                associatedSensor == null ? null : associatedSensor.description(),
                false,
                dataStatus,
                highSegment,
                prediction,
                associatedSensor == null ? null : transportFor(associatedSensor.id()),
                limitations,
                !"DATA_DRIVEN".equals(scenario)
        );
    }

    private Optional<Double> routeAverage(int sensorId, LocalDateTime departure) {
        Optional<HourlyReading> exact = repository.exactHourly(sensorId, departure.toLocalDate(), departure.getHour());
        if (exact.isPresent()) return exact.map(r -> r.total() / 60.0);
        boolean weekend = isWeekend(departure.getDayOfWeek());
        return repository.hourlyForSensor(sensorId).stream()
                .filter(r -> r.hour() == departure.getHour())
                .filter(r -> isWeekend(r.date().getDayOfWeek()) == weekend)
                .mapToDouble(HourlyReading::total)
                .average()
                .stream()
                .boxed()
                .findFirst()
                .map(total -> total / 60.0);
    }

    private PredictionResult prediction(SensorLocation sensor, LocalDateTime departure, String scenario, int routeIndex) {
        LocalDateTime predicted = departure.plusHours(1);
        String predictedHour = predicted.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String timeframe = predictedHour + "–" + predicted.plusHours(1).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String dayType = isWeekend(predicted.getDayOfWeek()) ? "weekend" : "weekday";
        String area = sensor == null ? "Selected route" : sensor.description();

        if (sensor == null || (demoMode && "PREDICTION_UNAVAILABLE".equals(scenario))) {
            return unavailablePrediction(predictedHour, dayType, area, timeframe);
        }

        List<HourlyReading> comparable = repository.hourlyForSensor(sensor.id()).stream()
                .filter(r -> r.hour() == predicted.getHour())
                .filter(r -> isWeekend(r.date().getDayOfWeek()) == isWeekend(predicted.getDayOfWeek()))
                .toList();

        if (comparable.size() < 4) return unavailablePrediction(predictedHour, dayType, area, timeframe);
        double predictedAverage = comparable.stream().mapToDouble(HourlyReading::total).average().orElse(0) / 60.0;
        boolean illustrativeOverride = false;
        if (demoMode && "PREDICTION_HIGH".equals(scenario) && routeIndex == 0) {
            predictedAverage = 74.0;
            illustrativeOverride = true;
        }
        String status = predictedAverage > HIGH_THRESHOLD ? "Higher pedestrian activity likely" : "Lower pedestrian activity likely";
        return new PredictionResult(
                true,
                status,
                comparable.size(),
                round1(predictedAverage),
                predictedHour,
                dayType,
                area,
                timeframe,
                illustrativeOverride,
                PREDICTION_LIMITATION
        );
    }

    private PredictionResult unavailablePrediction(String predictedHour, String dayType, String area, String timeframe) {
        return new PredictionResult(false, "Prediction unavailable", 0, null, predictedHour, dayType, area, timeframe, false,
                "Prediction unavailable insufficient comparable historical data");
    }

    private Double overrideAverage(String scenario, int routeIndex, Double original) {
        return switch (scenario) {
            case "HIGH_LOW" -> routeIndex == 0 ? 91.9 : routeIndex == 1 ? 42.2 : null;
            case "NO_ALTERNATIVE" -> routeIndex < 2 ? (routeIndex == 0 ? 82.0 : 69.0) : null;
            case "DATA_UNAVAILABLE" -> routeIndex == 0 ? null : original;
            default -> original;
        };
    }

    private String normalizeScenario(String raw) {
        if (!demoMode || raw == null || raw.isBlank()) return "DATA_DRIVEN";
        String normalized = raw.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        return switch (normalized) {
            case "HIGH_LOW", "NO_ALTERNATIVE", "DATA_UNAVAILABLE", "PREDICTION_HIGH", "PREDICTION_UNAVAILABLE", "OUTSIDE_COVERAGE" -> normalized;
            default -> "DATA_DRIVEN";
        };
    }

    private List<Coordinates> geometry(SensorLocation a, SensorLocation b, double offset, int routeIndex) {
        double midLat = (a.latitude() + b.latitude()) / 2.0;
        double midLon = (a.longitude() + b.longitude()) / 2.0;
        if (routeIndex == 0) {
            return List.of(
                    new Coordinates(a.latitude(), a.longitude()),
                    new Coordinates(midLat + 0.0004, midLon - 0.0002),
                    new Coordinates(b.latitude(), b.longitude())
            );
        }
        return List.of(
                new Coordinates(a.latitude(), a.longitude()),
                new Coordinates(midLat + offset, midLon - offset * 0.6),
                new Coordinates(midLat + offset * 0.4, midLon + offset),
                new Coordinates(b.latitude(), b.longitude())
        );
    }

    public static boolean isPeakHour(LocalDateTime dt) {
        if (isWeekend(dt.getDayOfWeek())) return false;
        LocalTime t = dt.toLocalTime();
        return betweenInclusive(t, LocalTime.of(7, 0), LocalTime.of(10, 0))
                || betweenInclusive(t, LocalTime.of(16, 0), LocalTime.of(19, 0));
    }

    private static boolean betweenInclusive(LocalTime t, LocalTime start, LocalTime end) {
        return !t.isBefore(start) && !t.isAfter(end);
    }

    private static boolean isWeekend(DayOfWeek d) {
        return d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY;
    }

    private LocationReference toLocationReference(SensorLocation s) {
        return new LocationReference(String.valueOf(s.id()), s.description(), s.latitude(), s.longitude(), s.description(), "sensor");
    }

    private NearbyTransportConnection transportFor(int sensorId) {
        return switch (sensorId) {
            case 3 -> new NearbyTransportConnection("Melbourne Central Station", "Train station", 120);
            case 5 -> new NearbyTransportConnection("Flinders Street Station", "Train station", 250);
            case 9 -> new NearbyTransportConnection("Southern Cross Station", "Train station", 90);
            case 51 -> new NearbyTransportConnection("Queen Victoria Market / Elizabeth St tram stop", "Tram stop", 180);
            case 53, 9001 -> new NearbyTransportConnection("Swanston Street tram stop", "Tram stop", 220);
            default -> null;
        };
    }

    private int parseId(String id) {
        try { return Integer.parseInt(id); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Location ID must be numeric."); }
    }

    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double r = 6371.0088;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return r * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private static double round1(double n) { return Math.round(n * 10.0) / 10.0; }
    private static double round2(double n) { return Math.round(n * 100.0) / 100.0; }
}
