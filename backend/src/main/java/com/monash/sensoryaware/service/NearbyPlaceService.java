package com.monash.sensoryaware.service;

import static com.monash.sensoryaware.dto.ApiDtos.*;

import com.monash.sensoryaware.model.Landmark;
import com.monash.sensoryaware.model.SensorLocation;
import com.monash.sensoryaware.repository.DatasetRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class NearbyPlaceService {
    private final DatasetRepository repository;

    public NearbyPlaceService(DatasetRepository repository) {
        this.repository = repository;
    }

    public NearbyPlacesResult nearby(String idText) {
        int id = parseId(idText);
        SensorLocation sensor = repository.sensor(id).orElseThrow(() -> new IllegalArgumentException("Unsupported location."));
        List<NearbyPlace> places = repository.landmarks().stream()
                .map(l -> toEligiblePlace(l, sensor))
                .filter(p -> p != null)
                .sorted(Comparator.comparingInt(NearbyPlace::approximateDistanceMetres))
                .limit(3)
                .toList();

        return new NearbyPlacesResult(
                String.valueOf(id),
                sensor.description(),
                places,
                "These places are suggested using public category data and have not been verified as quiet or sensory-friendly.",
                "City of Melbourne Landmarks & Places of Interest dataset · category-based selection",
                true
        );
    }

    private NearbyPlace toEligiblePlace(Landmark landmark, SensorLocation sensor) {
        String category = category(landmark);
        if (category == null) return null;
        int distance = (int) Math.round(haversineKm(sensor.latitude(), sensor.longitude(), landmark.latitude(), landmark.longitude()) * 1000);
        if (distance > 3500) return null;
        return new NearbyPlace(String.valueOf(landmark.id()), landmark.name(), category, landmark.latitude(), landmark.longitude(), distance, false);
    }

    private String category(Landmark l) {
        if (l.categoryId() == 2) return "Library";
        String name = l.name().toLowerCase(Locale.ROOT);
        if (name.contains("skate") || name.contains("sport") || name.contains("football")) return null;
        if (name.contains("garden")) return "Garden";
        if (name.contains("reserve")) return "Reserve";
        if (name.contains("park") || name.contains("domain")) return "Park";
        return null;
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
}
