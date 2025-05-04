package com.csye6220.financenavigator.validator;

import com.csye6220.financenavigator.model.PortfolioHolder;
import com.csye6220.financenavigator.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component
public class PortfolioHolderValidator implements Validator {

    private static final Pattern CLIENT_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{4,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Autowired
    private PortfolioService portfolioService;

    @Override
    public boolean supports(Class<?> clazz) {
        return PortfolioHolder.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) target;

        // Client ID validation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "clientId", "error.required", "Client ID is required");
        if (portfolioHolder.getClientId() != null && !CLIENT_ID_PATTERN.matcher(portfolioHolder.getClientId()).matches()) {
            errors.rejectValue("clientId", "error.invalid", "Client ID must be 4-50 characters and contain only letters, numbers, dots, underscores, and hyphens");
        }
        if (portfolioHolder.getClientId() != null && !portfolioService.isClientIdAvailable(portfolioHolder.getClientId())) {
            errors.rejectValue("clientId", "error.exists", "Client ID already exists");
        }

        // Passcode validation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passcode", "error.required", "Passcode is required");
        if (portfolioHolder.getPasscode() != null && portfolioHolder.getPasscode().length() < 6) {
            errors.rejectValue("passcode", "error.short", "Passcode must be at least 6 characters");
        }

        // Full Name validation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fullName", "error.required", "Full name is required");
        if (portfolioHolder.getFullName() != null && portfolioHolder.getFullName().length() > 100) {
            errors.rejectValue("fullName", "error.long", "Full name cannot exceed 100 characters");
        }

        // Email validation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.required", "Email is required");
        if (portfolioHolder.getEmail() != null && !EMAIL_PATTERN.matcher(portfolioHolder.getEmail()).matches()) {
            errors.rejectValue("email", "error.invalid", "Please provide a valid email address");
        }
        if (portfolioHolder.getEmail() != null && !portfolioService.isEmailAvailable(portfolioHolder.getEmail())) {
            errors.rejectValue("email", "error.exists", "Email already exists");
        }

        // Contact number validation (optional field)
        if (portfolioHolder.getContactNumber() != null && !portfolioHolder.getContactNumber().isEmpty()) {
            if (!portfolioHolder.getContactNumber().matches("^[0-9+()\\-\\s]+$")) {
                errors.rejectValue("contactNumber", "error.invalid", "Invalid contact number format");
            }
            if (portfolioHolder.getContactNumber().length() < 10) {
                errors.rejectValue("contactNumber", "error.short", "Contact number cannot be less than 10 digits");
            }
            if (portfolioHolder.getContactNumber().length() > 20) {
                errors.rejectValue("contactNumber", "error.long", "Contact number cannot exceed 20 digits");
            }
        }
    }
}