package com.csye6220.financenavigator.controller;

import com.csye6220.financenavigator.model.Allocation;
import com.csye6220.financenavigator.model.InstrumentType;
import com.csye6220.financenavigator.model.PortfolioHolder;
import com.csye6220.financenavigator.service.AllocationService;
import com.csye6220.financenavigator.service.LedgerService;
import com.csye6220.financenavigator.validator.AllocationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/allocations")
public class AllocationController {

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private AllocationValidator allocationValidator;

    @InitBinder("allocation")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(allocationValidator);
    }

    @GetMapping
    public String listAllocations(HttpSession ledger, Model model) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        List<Allocation> assetAllocations = allocationService.findByInstrumentType(InstrumentType.ASSET);
        List<Allocation> liabilityAllocations = allocationService.findByInstrumentType(InstrumentType.LIABILITY);

        model.addAttribute("assetAllocations", assetAllocations);
        model.addAttribute("liabilityAllocations", liabilityAllocations);
        model.addAttribute("portfolioHolder", portfolioHolder);
        return "allocations";
    }

    @GetMapping("/add")
    public String addAllocationForm(HttpSession ledger, Model model) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        model.addAttribute("allocation", new Allocation());
        model.addAttribute("instrumentTypes", InstrumentType.values());
        model.addAttribute("portfolioHolder", portfolioHolder);
        return "add-allocation";
    }

    @PostMapping("/add")
    public String addAllocation(@ModelAttribute("allocation") Allocation allocation,
                                BindingResult bindingResult,
                                HttpSession ledger,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        // Validate the allocation
        validateAllocation(allocation, null, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("instrumentTypes", InstrumentType.values());
            model.addAttribute("portfolioHolder", portfolioHolder);
            return "add-allocation";
        }

        allocationService.save(allocation);
        redirectAttributes.addFlashAttribute("success", "Allocation added successfully");
        return "redirect:/allocations";
    }

    @GetMapping("/edit/{id}")
    public String editAllocationForm(@PathVariable Long id, HttpSession ledger, Model model) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        Allocation allocation = allocationService.findById(id);
        if (allocation == null) {
            return "redirect:/allocations";
        }

        model.addAttribute("allocation", allocation);
        model.addAttribute("instrumentTypes", InstrumentType.values());
        model.addAttribute("portfolioHolder", portfolioHolder);
        return "edit-allocation";
    }

    @PostMapping("/edit/{id}")
    public String updateAllocation(@PathVariable Long id,
                                   @ModelAttribute("allocation") Allocation allocation,
                                   BindingResult bindingResult,
                                   HttpSession ledger,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        Allocation existingAllocation = allocationService.findById(id);
        if (existingAllocation == null) {
            return "redirect:/allocations";
        }

        // Validate the allocation
        validateAllocation(allocation, id, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("instrumentTypes", InstrumentType.values());
            model.addAttribute("portfolioHolder", portfolioHolder);
            return "edit-allocation";
        }

        // Update the allocation
        existingAllocation.setName(allocation.getName());
        existingAllocation.setDescription(allocation.getDescription());
        existingAllocation.setInstrumentType(allocation.getInstrumentType());
        existingAllocation.setIconClass(allocation.getIconClass());

        allocationService.update(existingAllocation);
        redirectAttributes.addFlashAttribute("success", "Allocation updated successfully");
        return "redirect:/allocations";
    }

    @PatchMapping("/edit/{id}")
    public String patchAllocation(@PathVariable Long id,
                                  @ModelAttribute("allocation") Allocation allocation,
                                  BindingResult bindingResult,
                                  HttpSession ledger,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        // Use the same validation and update logic as in the POST method
        return updateAllocation(id, allocation, bindingResult, ledger, model, redirectAttributes);
    }

    @GetMapping("/delete/{id}")
    public String deleteAllocation(@PathVariable Long id,
                                   HttpSession ledger,
                                   RedirectAttributes redirectAttributes) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        Allocation allocation = allocationService.findById(id);
        if (allocation == null) {
            return "redirect:/allocations";
        }

        // Check if allocation is in use
        if (!ledgerService.findByAllocation(allocation).isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete allocation as it is in use by ledger entries");
            return "redirect:/allocations";
        }

        allocationService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Allocation deleted successfully");
        return "redirect:/allocations";
    }

    /**
     * Helper method to validate Allocation objects
     */
    private void validateAllocation(Allocation allocation, Long id, BindingResult bindingResult) {
        // Name validation
        if (allocation.getName() == null || allocation.getName().trim().isEmpty()) {
            bindingResult.rejectValue("name", "error.required", "Name is required");
        } else if (allocation.getName().length() < 2) {
            bindingResult.rejectValue("name", "error.tooShort", "Name must be at least 2 characters");
        } else if (allocation.getName().length() > 50) {
            bindingResult.rejectValue("name", "error.tooLong", "Name cannot exceed 50 characters");
        } else {
            // Check name uniqueness
            Allocation existingAllocation = allocationService.findByName(allocation.getName());
            if (existingAllocation != null && (id == null || !existingAllocation.getId().equals(id))) {
                bindingResult.rejectValue("name", "error.duplicate", "Allocation name already exists");
            }
        }

        // Description validation (optional)
        if (allocation.getDescription() != null && allocation.getDescription().length() > 255) {
            bindingResult.rejectValue("description", "error.tooLong", "Description cannot exceed 255 characters");
        }

        // Instrument Type validation
        if (allocation.getInstrumentType() == null) {
            bindingResult.rejectValue("instrumentType", "error.required", "Instrument type is required");
        }

        // Icon Class validation (optional)
        if (allocation.getIconClass() != null && allocation.getIconClass().length() > 50) {
            bindingResult.rejectValue("iconClass", "error.tooLong", "Icon class cannot exceed 50 characters");
        }
    }
}