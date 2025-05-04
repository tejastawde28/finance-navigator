package com.csye6220.financenavigator.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

@Component
public class ReportValidator implements Validator {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public boolean supports(Class<?> clazz) {
        return true; // This validator doesn't validate a specific class but parameters
    }

    @Override
    public void validate(Object target, Errors errors) {
        // This method is required by the interface but we'll use validateDates instead
    }

    /**
     * Validates date parameters for reports
     * @return Map of field errors (fieldName -> errorMessage)
     */
    public Map<String, String> validateDates(String startDate, String endDate) {
        Map<String, String> errors = new HashMap<>();

        // Check if dates are provided
        if (startDate == null || startDate.trim().isEmpty()) {
            errors.put("startDate", "Start date is required");
            return errors;
        }

        if (endDate == null || endDate.trim().isEmpty()) {
            errors.put("endDate", "End date is required");
            return errors;
        }

        // Validate date formats and range
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            Date today = new Date();

            // Validate date range
            if (start.after(end)) {
                errors.put("startDate", "Start date cannot be after end date");
            }

            // Validate dates are not in the future
            if (start.after(today)) {
                errors.put("startDate", "Start date cannot be in the future");
            }

            if (end.after(today)) {
                errors.put("endDate", "End date cannot be in the future");
            }

        } catch (ParseException e) {
            errors.put("startDate", "Invalid date format");
        }

        return errors;
    }
}