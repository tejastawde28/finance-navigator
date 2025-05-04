package com.csye6220.financenavigator.validator;

import com.csye6220.financenavigator.model.Allocation;
import com.csye6220.financenavigator.service.AllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class AllocationValidator implements Validator {

    @Autowired
    private AllocationService allocationService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Allocation.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Allocation allocation = (Allocation) target;

        // Name validation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.required", "Name is required");
        if (allocation.getName() != null) {
            if (allocation.getName().length() < 2) {
                errors.rejectValue("name", "error.short", "Name must be at least 2 characters");
            }
            if (allocation.getName().length() > 50) {
                errors.rejectValue("name", "error.long", "Name cannot exceed 50 characters");
            }

            // Check name uniqueness for new allocations (when ID is null)
            if (allocation.getId() == null) {
                Allocation existingAllocation = allocationService.findByName(allocation.getName());
                if (existingAllocation != null) {
                    errors.rejectValue("name", "error.exists", "Allocation name already exists");
                }
            }
            // For existing allocations, only check uniqueness if name changed
            else {
                Allocation existingAllocation = allocationService.findByName(allocation.getName());
                if (existingAllocation != null && !existingAllocation.getId().equals(allocation.getId())) {
                    errors.rejectValue("name", "error.exists", "Allocation name already exists");
                }
            }
        }

        // Description validation (optional)
        if (allocation.getDescription() != null && allocation.getDescription().length() > 255) {
            errors.rejectValue("description", "error.long", "Description cannot exceed 255 characters");
        }

        // Instrument Type validation
        if (allocation.getInstrumentType() == null) {
            errors.rejectValue("instrumentType", "error.required", "Instrument type is required");
        }

//        // Icon class validation (optional)
//        if (allocation.getIconClass() != null && allocation.getIconClass().length() > 50) {
//            errors.rejectValue("iconClass", "error.long", "Icon class cannot exceed 50 characters");
//        }
    }
}