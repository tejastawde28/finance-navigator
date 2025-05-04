package com.csye6220.financenavigator.validator;

import com.csye6220.financenavigator.model.LedgerEntry;
import com.csye6220.financenavigator.service.AllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class LedgerEntryValidator implements Validator {

    @Autowired
    private AllocationService allocationService;

    @Override
    public boolean supports(Class<?> clazz) {
        return LedgerEntry.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LedgerEntry ledgerEntry = (LedgerEntry) target;

        // Amount validation
        if (ledgerEntry.getAmount() == null) {
            errors.rejectValue("amount", "error.required", "Amount is required");
        } else if (ledgerEntry.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.rejectValue("amount", "error.positive", "Amount must be greater than 0");
        }

        // Date validation
        if (ledgerEntry.getEntryDate() == null) {
            errors.rejectValue("entryDate", "error.required", "Date is required");
        } else if (ledgerEntry.getEntryDate().after(new Date())) {
            errors.rejectValue("entryDate", "error.future", "Date cannot be in the future");
        }

        // Description validation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "error.required", "Description is required");
        if (ledgerEntry.getDescription() != null && ledgerEntry.getDescription().length() > 255) {
            errors.rejectValue("description", "error.long", "Description cannot exceed 255 characters");
        }

        // Notes validation (optional)
        if (ledgerEntry.getNotes() != null && ledgerEntry.getNotes().length() > 500) {
            errors.rejectValue("notes", "error.long", "Notes cannot exceed 500 characters");
        }

        // Reference validation (optional)
//        if (ledgerEntry.getReference() != null && ledgerEntry.getReference().length() > 50) {
//            errors.rejectValue("reference", "error.long", "Reference cannot exceed 50 characters");
//        }

        // Allocation validation
        if (ledgerEntry.getAllocation() != null && ledgerEntry.getAllocation().getId() != null) {
            if (allocationService.findById(ledgerEntry.getAllocation().getId()) == null) {
                errors.rejectValue("allocation", "error.invalid", "Invalid allocation");
            }
        }
    }
}