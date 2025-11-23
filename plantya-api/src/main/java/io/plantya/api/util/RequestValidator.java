package io.plantya.api.util;

import io.plantya.api.exception.BadRequestException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RequestValidator {

    public static void validate(String deviceId) {
        if (deviceId == null ||  deviceId.trim().isEmpty()) {
            throw new BadRequestException("Device ID must not be null");
        }
    }

    public static void validate(String deviceId, LocalDate from, LocalDate to) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new BadRequestException("Device ID must not be null or empty");
        }

        if (from == null || to == null) {
            throw new BadRequestException("Parameter 'from' and 'to' are required");
        }

        if (from.isAfter(to)) {
            throw new BadRequestException("Parameter 'from' cannot be after 'to'");
        }

        long totalDays = ChronoUnit.DAYS.between(from, to) + 1;

        if (totalDays > 30) {
            throw new BadRequestException("Cannot get history more than 30 days");
        }
    }

}
