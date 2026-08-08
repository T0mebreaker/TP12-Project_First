package com.monash.sensoryaware.model;

import java.time.LocalDate;

public record HourlyReading(int locationId, LocalDate date, int hour, double total) {}
