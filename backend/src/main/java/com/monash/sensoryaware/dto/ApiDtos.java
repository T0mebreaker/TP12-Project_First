package com.monash.sensoryaware.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public final class ApiDtos {
    private ApiDtos() {}

    public record Coordinates(double latitude, double longitude) {}

    public record LocationReference(
            String id,
            String name,
            double latitude,
            double longitude,
            String sensorDescription,
            String source
    ) {}

    public record RouteRequest(
            @NotBlank String originId,
            @NotBlank String destinationId,
            @NotNull LocalDateTime departureTime,
            String scenario
    ) {}

    public record RouteGeometry(
            List<Coordinates> coordinates,
            String geometrySource,
            String navigationAccuracy
    ) {}

    public record PeakHourContext(
            boolean isPeakHour,
            String label,
            LocalDateTime departureTime,
            String timezone
    ) {}

    public record PredictionResult(
            boolean eligible,
            String status,
            int comparableReadingCount,
            Double predictedAveragePedestriansPerMinute,
            String predictedHour,
            String dayType,
            String affectedArea,
            String timeframe,
            boolean isIllustrative,
            String limitation
    ) {}

    public record NearbyTransportConnection(
            String name,
            String type,
            int approximateDistanceMetres
    ) {}

    public record RouteOption(
            String id,
            String name,
            RouteGeometry geometry,
            int walkingTimeMinutes,
            double distanceKm,
            String sensoryClassification,
            Double averagePedestriansPerMinute,
            String classificationThreshold,
            String sensorId,
            String sensorName,
            boolean isLowerStimulationAlternative,
            String dataStatus,
            String highCongestionSegment,
            PredictionResult prediction,
            NearbyTransportConnection nearbyTransport,
            List<String> limitations,
            boolean isIllustrative
    ) {}

    public record RouteResponse(
            RouteRequest request,
            LocationReference origin,
            LocationReference destination,
            PeakHourContext peakHour,
            List<RouteOption> routes,
            String dataSource,
            String generatedAt,
            String limitation,
            String scenario
    ) {}

    public record LocationDetail(
            String id,
            String name,
            Coordinates coordinates,
            Double latestPedestriansPerMinute,
            String latestObservedAt,
            String dataFreshness,
            boolean stale,
            String interpretation,
            String dataSource,
            boolean sampleData,
            String sensoryLimitation,
            NearbyTransportConnection nearbyTransport
    ) {}

    public record HistoricalDataPoint(
            int hour,
            String label,
            double averagePedestriansPerMinute,
            int sampleCount
    ) {}

    public record HistoricalTrendResult(
            String locationId,
            String locationName,
            boolean available,
            List<HistoricalDataPoint> points,
            String higherActivityPeriod,
            String lowerActivityPeriod,
            String summary,
            String quieterTimeInsight,
            String limitation,
            String dataSource
    ) {}

    public record NearbyPlace(
            String id,
            String name,
            String category,
            double latitude,
            double longitude,
            int approximateDistanceMetres,
            boolean sampleData
    ) {}

    public record NearbyPlacesResult(
            String locationId,
            String locationName,
            List<NearbyPlace> places,
            String limitation,
            String dataSource,
            boolean guidanceOnly
    ) {}

    public record ErrorResponse(String message, String code, String details) {}
}
