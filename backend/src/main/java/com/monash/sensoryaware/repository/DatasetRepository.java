package com.monash.sensoryaware.repository;

import com.monash.sensoryaware.model.HourlyReading;
import com.monash.sensoryaware.model.Landmark;
import com.monash.sensoryaware.model.MinuteReading;
import com.monash.sensoryaware.model.SensorLocation;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

@Repository
public class DatasetRepository {
    private static final CSVFormat FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .build();

    private final Map<Integer, SensorLocation> sensors = new HashMap<>();
    private final List<HourlyReading> hourlyReadings = new ArrayList<>();
    private final List<MinuteReading> minuteReadings = new ArrayList<>();
    private final List<Landmark> landmarks = new ArrayList<>();

    @PostConstruct
    void load() throws IOException {
        loadSensors();
        loadHourly();
        loadMinute();
        loadLandmarks();
        minuteReadings.sort(Comparator.comparing(MinuteReading::timestamp));
    }

    private Reader reader(String name) throws IOException {
        return new InputStreamReader(new ClassPathResource("data/" + name).getInputStream(), StandardCharsets.UTF_8);
    }

    private CSVParser parser(Reader reader) throws IOException {
        return FORMAT.parse(reader);
    }

    private void loadSensors() throws IOException {
        try (Reader source = reader("sensor_location.csv"); CSVParser csv = parser(source)) {
            for (CSVRecord r : csv) {
                int id = Integer.parseInt(r.get("location_id"));
                sensors.put(id, new SensorLocation(
                        id,
                        r.get("sensor_description"),
                        r.get("sensor_name"),
                        Double.parseDouble(r.get("latitude")),
                        Double.parseDouble(r.get("longitude"))
                ));
            }
        }
    }

    private void loadHourly() throws IOException {
        try (Reader source = reader("pedestrian_hour_count.csv"); CSVParser csv = parser(source)) {
            for (CSVRecord r : csv) {
                String total = r.get("total_of_directions");
                if (total == null || total.isBlank()) continue;
                hourlyReadings.add(new HourlyReading(
                        Integer.parseInt(r.get("location_id")),
                        LocalDate.parse(r.get("sensing_date")),
                        Integer.parseInt(r.get("hour_of_day")),
                        Double.parseDouble(total)
                ));
            }
        }
    }

    private void loadMinute() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        try (Reader source = reader("pedestrian_minute_count.csv"); CSVParser csv = parser(source)) {
            for (CSVRecord r : csv) {
                String total = r.get("total_of_directions");
                if (total == null || total.isBlank()) continue;
                minuteReadings.add(new MinuteReading(
                        Integer.parseInt(r.get("location_id")),
                        OffsetDateTime.parse(r.get("sensing_datetime"), formatter),
                        Double.parseDouble(total)
                ));
            }
        }
    }

    private void loadLandmarks() throws IOException {
        try (Reader source = reader("landmark.csv"); CSVParser csv = parser(source)) {
            for (CSVRecord r : csv) {
                landmarks.add(new Landmark(
                        Integer.parseInt(r.get("landmark_id")),
                        r.get("feature_name"),
                        Integer.parseInt(r.get("category_id")),
                        Double.parseDouble(r.get("latitude")),
                        Double.parseDouble(r.get("longitude"))
                ));
            }
        }
    }

    public List<SensorLocation> sensors() {
        return sensors.values().stream().sorted(Comparator.comparingInt(SensorLocation::id)).toList();
    }

    public Optional<SensorLocation> sensor(int id) {
        return Optional.ofNullable(sensors.get(id));
    }

    public List<HourlyReading> hourlyForSensor(int id) {
        return hourlyReadings.stream().filter(r -> r.locationId() == id).toList();
    }

    public Optional<HourlyReading> exactHourly(int id, LocalDate date, int hour) {
        return hourlyReadings.stream()
                .filter(r -> r.locationId() == id && r.date().equals(date) && r.hour() == hour)
                .findFirst();
    }

    public Optional<HourlyReading> latestHourly(int id) {
        return hourlyReadings.stream()
                .filter(r -> r.locationId() == id)
                .max(Comparator.comparing(HourlyReading::date).thenComparingInt(HourlyReading::hour));
    }

    public List<MinuteReading> minuteForSensor(int id) {
        return minuteReadings.stream().filter(r -> r.locationId() == id).toList();
    }

    public Optional<MinuteReading> latestMinute(int id) {
        return minuteReadings.stream().filter(r -> r.locationId() == id).max(Comparator.comparing(MinuteReading::timestamp));
    }

    public List<Landmark> landmarks() {
        return List.copyOf(landmarks);
    }

    public Map<Integer, List<HourlyReading>> hourlyGroupedBySensor() {
        return hourlyReadings.stream().collect(Collectors.groupingBy(HourlyReading::locationId));
    }
}
