package com.monash.sensoryaware.model;

import java.time.OffsetDateTime;

public record MinuteReading(int locationId, OffsetDateTime timestamp, double total) {}
