package com.csye6220.financenavigator.controller;

import com.csye6220.financenavigator.model.Allocation;
import com.csye6220.financenavigator.model.InstrumentType;
import com.csye6220.financenavigator.model.LedgerEntry;
import com.csye6220.financenavigator.model.PortfolioHolder;
import com.csye6220.financenavigator.service.AllocationService;
import com.csye6220.financenavigator.service.LedgerService;
import com.csye6220.financenavigator.validator.LedgerEntryValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/ledger")
public class LedgerEntryController {

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private LedgerEntryValidator ledgerEntryValidator;

    @InitBinder("ledgerEntry")
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        binder.setValidator(ledgerEntryValidator);
    }

    @GetMapping
    public String listEntries(HttpSession ledger, Model model) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        List<LedgerEntry> entries = ledgerService.findByPortfolioHolder(portfolioHolder);
        model.addAttribute("entries", entries);
        model.addAttribute("portfolioHolder", portfolioHolder);
        return "ledger-entries";
    }

    @GetMapping("/add")
    public String addEntryForm(HttpSession ledger, Model model) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        // Create a new ledger entry with today's date
        LedgerEntry ledgerEntry = new LedgerEntry();
        ledgerEntry.setEntryDate(new Date());

        model.addAttribute("ledgerEntry", ledgerEntry);
        model.addAttribute("assetAllocations", allocationService.findByInstrumentType(InstrumentType.ASSET));
        model.addAttribute("liabilityAllocations", allocationService.findByInstrumentType(InstrumentType.LIABILITY));
        model.addAttribute("portfolioHolder", portfolioHolder);
        return "add-ledger-entry";
    }

    @PostMapping("/add")
    public String addEntry(@ModelAttribute("ledgerEntry") LedgerEntry ledgerEntry,
                           BindingResult bindingResult,
                           @RequestParam Long allocationId,
                           HttpSession ledger,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        // Basic validation
        if (ledgerEntry.getEntryDate() == null) {
            bindingResult.rejectValue("entryDate", "error.required", "Entry date is required");
        } else if (ledgerEntry.getEntryDate().after(new Date())) {
            bindingResult.rejectValue("entryDate", "error.future", "Entry date cannot be in the future");
        }

        if (ledgerEntry.getDescription() == null || ledgerEntry.getDescription().trim().isEmpty()) {
            bindingResult.rejectValue("description", "error.required", "Description is required");
        } else if (ledgerEntry.getDescription().length() > 255) {
            bindingResult.rejectValue("description", "error.length", "Description cannot exceed 255 characters");
        }

        if (ledgerEntry.getAmount() == null) {
            bindingResult.rejectValue("amount", "error.required", "Amount is required");
        } else if (ledgerEntry.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            bindingResult.rejectValue("amount", "error.positive", "Amount must be greater than zero");
        }

        Allocation allocation = allocationService.findById(allocationId);
        if (allocation == null) {
            bindingResult.rejectValue("allocation", "error.required", "Valid allocation is required");
        }

        if (ledgerEntry.getNotes() != null && ledgerEntry.getNotes().length() > 500) {
            bindingResult.rejectValue("notes", "error.length", "Notes cannot exceed 500 characters");
        }

        if (ledgerEntry.getReference() != null && ledgerEntry.getReference().length() > 50) {
            bindingResult.rejectValue("reference", "error.length", "Reference cannot exceed 50 characters");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("assetAllocations", allocationService.findByInstrumentType(InstrumentType.ASSET));
            model.addAttribute("liabilityAllocations", allocationService.findByInstrumentType(InstrumentType.LIABILITY));
            model.addAttribute("portfolioHolder", portfolioHolder);
            model.addAttribute("selectedAllocationId", allocationId);
            return "add-ledger-entry";
        }

        ledgerEntry.setPortfolioHolder(portfolioHolder);
        ledgerEntry.setAllocation(allocation);
        ledgerEntry.setCreatedAt(new Date());
        ledgerService.save(ledgerEntry);

        redirectAttributes.addFlashAttribute("success", "Ledger entry added successfully");
        return "redirect:/ledger";
    }

    @GetMapping("/edit/{id}")
    public String editEntryForm(@PathVariable Long id, HttpSession ledger, Model model) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

            LedgerEntry ledgerEntry = ledgerService.findById(id);
            if (ledgerEntry == null || !ledgerEntry.getPortfolioHolder().getId().equals(portfolioHolder.getId())) {
                return "redirect:/ledger";
            }


        model.addAttribute("ledgerEntry", ledgerEntry);
        model.addAttribute("assetAllocations", allocationService.findByInstrumentType(InstrumentType.ASSET));
        model.addAttribute("liabilityAllocations", allocationService.findByInstrumentType(InstrumentType.LIABILITY));
        model.addAttribute("selectedAllocationId", ledgerEntry.getAllocation().getId());
        model.addAttribute("portfolioHolder", portfolioHolder);
        return "edit-ledger-entry";
    }

    @PostMapping("/edit/{id}")
    public String updateEntry(@PathVariable Long id,
                              @ModelAttribute("ledgerEntry") LedgerEntry ledgerEntry,
                              BindingResult bindingResult,
                              @RequestParam Long allocationId,
                              HttpSession ledger,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        LedgerEntry existingEntry = ledgerService.findById(id);
        if (existingEntry == null || !existingEntry.getPortfolioHolder().getId().equals(portfolioHolder.getId())) {
            return "redirect:/ledger";
        }

        // Validate inputs
        if (ledgerEntry.getEntryDate() == null) {
            bindingResult.rejectValue("entryDate", "error.required", "Entry date is required");
        } else if (ledgerEntry.getEntryDate().after(new Date())) {
            bindingResult.rejectValue("entryDate", "error.future", "Entry date cannot be in the future");
        }

        if (ledgerEntry.getDescription() == null || ledgerEntry.getDescription().trim().isEmpty()) {
            bindingResult.rejectValue("description", "error.required", "Description is required");
        } else if (ledgerEntry.getDescription().length() > 255) {
            bindingResult.rejectValue("description", "error.length", "Description cannot exceed 255 characters");
        }

        if (ledgerEntry.getAmount() == null) {
            bindingResult.rejectValue("amount", "error.required", "Amount is required");
        } else if (ledgerEntry.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            bindingResult.rejectValue("amount", "error.positive", "Amount must be greater than zero");
        }

        Allocation allocation = allocationService.findById(allocationId);
        if (allocation == null) {
            bindingResult.rejectValue("allocation", "error.required", "Valid allocation is required");
        }

        if (ledgerEntry.getNotes() != null && ledgerEntry.getNotes().length() > 500) {
            bindingResult.rejectValue("notes", "error.length", "Notes cannot exceed 500 characters");
        }

        if (ledgerEntry.getReference() != null && ledgerEntry.getReference().length() > 50) {
            bindingResult.rejectValue("reference", "error.length", "Reference cannot exceed 50 characters");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("assetAllocations", allocationService.findByInstrumentType(InstrumentType.ASSET));
            model.addAttribute("liabilityAllocations", allocationService.findByInstrumentType(InstrumentType.LIABILITY));
            model.addAttribute("selectedAllocationId", allocationId);
            model.addAttribute("portfolioHolder", portfolioHolder);
            return "edit-ledger-entry";
        }

        // Update the entry with the validated data
        existingEntry.setAmount(ledgerEntry.getAmount());
        existingEntry.setEntryDate(ledgerEntry.getEntryDate());
        existingEntry.setDescription(ledgerEntry.getDescription());
        existingEntry.setNotes(ledgerEntry.getNotes());
        existingEntry.setReference(ledgerEntry.getReference());
        existingEntry.setAllocation(allocation);
        existingEntry.setUpdatedAt(new Date()); // Set the update timestamp

        ledgerService.update(existingEntry);
        redirectAttributes.addFlashAttribute("success", "Ledger entry updated successfully");
        return "redirect:/ledger";
    }

    @PatchMapping("/edit/{id}")
    public String patchEntry(@PathVariable Long id,
                             @ModelAttribute("ledgerEntry") LedgerEntry ledgerEntry,
                             BindingResult bindingResult,
                             @RequestParam Long allocationId,
                             HttpSession ledger,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        LedgerEntry existingEntry = ledgerService.findById(id);
        if (existingEntry == null || !existingEntry.getPortfolioHolder().getId().equals(portfolioHolder.getId())) {
            return "redirect:/ledger";
        }

        // Validate inputs
        if (ledgerEntry.getEntryDate() == null) {
            bindingResult.rejectValue("entryDate", "error.required", "Entry date is required");
        } else if (ledgerEntry.getEntryDate().after(new Date())) {
            bindingResult.rejectValue("entryDate", "error.future", "Entry date cannot be in the future");
        }

        if (ledgerEntry.getDescription() == null || ledgerEntry.getDescription().trim().isEmpty()) {
            bindingResult.rejectValue("description", "error.required", "Description is required");
        } else if (ledgerEntry.getDescription().length() > 255) {
            bindingResult.rejectValue("description", "error.length", "Description cannot exceed 255 characters");
        }

        if (ledgerEntry.getAmount() == null) {
            bindingResult.rejectValue("amount", "error.required", "Amount is required");
        } else if (ledgerEntry.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            bindingResult.rejectValue("amount", "error.positive", "Amount must be greater than zero");
        }

        Allocation allocation = allocationService.findById(allocationId);
        if (allocation == null) {
            bindingResult.rejectValue("allocation", "error.required", "Valid allocation is required");
        }

        if (ledgerEntry.getNotes() != null && ledgerEntry.getNotes().length() > 500) {
            bindingResult.rejectValue("notes", "error.length", "Notes cannot exceed 500 characters");
        }

        if (ledgerEntry.getReference() != null && ledgerEntry.getReference().length() > 50) {
            bindingResult.rejectValue("reference", "error.length", "Reference cannot exceed 50 characters");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("assetAllocations", allocationService.findByInstrumentType(InstrumentType.ASSET));
            model.addAttribute("liabilityAllocations", allocationService.findByInstrumentType(InstrumentType.LIABILITY));
            model.addAttribute("selectedAllocationId", allocationId);
            model.addAttribute("portfolioHolder", portfolioHolder);
            return "edit-ledger-entry";
        }

        // Ensure ID matches path variable
        ledgerEntry.setId(id);

        // Preserve original creation date and portfolio holder
        ledgerEntry.setCreatedAt(existingEntry.getCreatedAt());
        ledgerEntry.setPortfolioHolder(portfolioHolder);
        ledgerEntry.setAllocation(allocation);

        // Set update timestamp
        ledgerEntry.setUpdatedAt(new Date());

        ledgerService.update(ledgerEntry);
        redirectAttributes.addFlashAttribute("success", "Ledger entry updated successfully");
        return "redirect:/ledger";
    }

    @GetMapping("/delete/{id}")
    public String deleteEntry(@PathVariable Long id,
                              HttpSession ledger,
                              RedirectAttributes redirectAttributes) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        LedgerEntry ledgerEntry = ledgerService.findById(id);
        if (ledgerEntry == null || !ledgerEntry.getPortfolioHolder().getId().equals(portfolioHolder.getId())) {
            return "redirect:/ledger";
        }

        ledgerService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Ledger entry deleted successfully");
        return "redirect:/ledger";
    }
}