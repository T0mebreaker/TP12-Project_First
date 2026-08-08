package com.monash.sensoryaware.service;

import static com.monash.sensoryaware.dto.ApiDtos.*;

import com.monash.sensoryaware.model.HourlyReading;
import com.monash.sensoryaware.model.SensorLocation;
import com.monash.sensoryaware.repository.DatasetRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {
    private final DatasetRepository repository;

    public HistoryService(DatasetRepository repository) {
        this.repository = repository;
    }

    public HistoricalTrendResult trend(String idText) {
        int id = parseId(idText);
        SensorLocation sensor = repository.sensor(id).orElseThrow(() -> new IllegalArgumentException("Unsupported location."));
        List<HourlyReading> all = repository.hourlyForSensor(id);
        if (all.size() < 4) {
            return new HistoricalTrendResult(String.valueOf(id), sensor.description(), false, List.of(), null, null,
                    "Insufficient historical data.", null,
                    "Historical insight is not generated when there is insufficient usable data.",
                    "City of Melbourne cleaned pedestrian hourly-count dataset");
        }

        List<HistoricalDataPoint> points = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            final int h = hour;
            List<HourlyReading> bucket = all.stream().filter(r -> r.hour() == h).toList();
            if (bucket.isEmpty()) continue;
            double avgPerMinute = bucket.stream().mapToDouble(HourlyReading::total).average().orElseThrow() / 60.0;
            points.add(new HistoricalDataPoint(hour, String.format("%02d:00", hour), round1(avgPerMinute), bucket.size()));
        }
        HistoricalDataPoint max = points.stream().max(Comparator.comparingDouble(HistoricalDataPoint::averagePedestriansPerMinute)).orElseThrow();
        HistoricalDataPoint min = points.stream().min(Comparator.comparingDouble(HistoricalDataPoint::averagePedestriansPerMinute)).orElseThrow();
        String higher = max.label() + " · average " + max.averagePedestriansPerMinute() + " pedestrians/min";
        String lower = min.label() + " · average " + min.averagePedestriansPerMinute() + " pedestrians/min";
        String summary = "Across the available historical sample, the highest hourly average occurs around " + max.label()
                + " and the lowest around " + min.label() + ". These are historical patterns, not live conditions.";
        String quieter = "Potentially quieter time: around " + min.label()
                + " based on the lowest historical hourly average in this dataset. Calculation method is illustrative and not a final production algorithm.";

        return new HistoricalTrendResult(
                String.valueOf(id), sensor.description(), true, points, higher, lower, summary, quieter,
                "Historical patterns are guidance only and do not guarantee future pedestrian or sensory conditions.",
                "City of Melbourne cleaned pedestrian hourly-count dataset"
        );
    }

    private int parseId(String id) {
        try { return Integer.parseInt(id); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Location ID must be numeric."); }
    }

    private static double round1(double n) { return Math.round(n * 10.0) / 10.0; }
}
