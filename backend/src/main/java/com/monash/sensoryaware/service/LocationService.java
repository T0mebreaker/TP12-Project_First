package com.monash.sensoryaware.service;

import static com.monash.sensoryaware.dto.ApiDtos.*;

import com.monash.sensoryaware.model.MinuteReading;
import com.monash.sensoryaware.model.SensorLocation;
import com.monash.sensoryaware.repository.DatasetRepository;
import org.springframework.stereotype.Service;

@Service
public class LocationService {
    private final DatasetRepository repository;

    public LocationService(DatasetRepository repository) {
        this.repository = repository;
    }

    public LocationDetail detail(String idText) {
        int id = parseId(idText);
        SensorLocation sensor = repository.sensor(id).orElseThrow(() -> new IllegalArgumentException("Unsupported location."));
        MinuteReading latest = repository.latestMinute(id).orElse(null);
        Double perMinute = latest == null ? null : round1(latest.total());
        String latestAt = latest == null ? null : latest.timestamp().toString();

        return new LocationDetail(
                String.valueOf(id),
                sensor.description(),
                new Coordinates(sensor.latitude(), sensor.longitude()),
                perMinute,
                latestAt,
                "Historical dataset snapshot · not live",
                true,
                "This is an observed one-minute pedestrian count, not an official sensory or crowd-level classification.",
                "City of Melbourne cleaned pedestrian minute-count dataset",
                false,
                "Pedestrian counts do not represent every sensory condition.",
                transportFor(id)
        );
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

    private static double round1(double n) { return Math.round(n * 10.0) / 10.0; }
}
