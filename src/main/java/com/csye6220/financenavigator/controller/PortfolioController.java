package com.csye6220.financenavigator.controller;

import com.csye6220.financenavigator.model.PortfolioHolder;
import com.csye6220.financenavigator.service.PortfolioService;
import com.csye6220.financenavigator.validator.PortfolioHolderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class PortfolioController {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioController.class);

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private PortfolioHolderValidator portfolioHolderValidator;

    @InitBinder("portfolioHolder")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(portfolioHolderValidator);
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/access";
    }

    @GetMapping("/access")
    public String accessPage() {
        return "access";
    }

    @PostMapping("/access")
    public String access(@RequestParam String clientId,
                         @RequestParam String passcode,
                         HttpSession ledger,
                         RedirectAttributes redirectAttributes) {
        try {
            // Basic validation
            if (clientId == null || clientId.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Client ID is required");
                return "redirect:/access";
            }

            if (passcode == null || passcode.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Passcode is required");
                return "redirect:/access";
            }

            if (portfolioService.validateCredentials(clientId, passcode)) {
                PortfolioHolder portfolioHolder = portfolioService.findByClientId(clientId);
                ledger.setAttribute("portfolioHolder", portfolioHolder);
                return "redirect:/dashboard";
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid client ID or passcode");
                return "redirect:/access";
            }
        } catch (Exception e) {
            logger.error("Error during login", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred during login. Please try again.");
            return "redirect:/access";
        }
    }

    @GetMapping("/enroll")
    public String enrollPage(Model model) {
        model.addAttribute("portfolioHolder", new PortfolioHolder());
        return "enroll";
    }

    @PostMapping("/enroll")
    public String enroll(@ModelAttribute("portfolioHolder") PortfolioHolder portfolioHolder,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        try {
            // Validate the portfolioHolder
            portfolioHolderValidator.validate(portfolioHolder, bindingResult);

            if (bindingResult.hasErrors()) {
                logger.info("Validation errors in enrollment form");
                return "enroll"; // Return to form with errors
            }

            portfolioService.save(portfolioHolder);
            redirectAttributes.addFlashAttribute("success", "Enrollment successful. Please access your portfolio.");
            return "redirect:/access";
        } catch (Exception e) {
            logger.error("Error during enrollment", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred during enrollment: " + e.getMessage());
            return "redirect:/enroll";
        }
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession ledger, Model model) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        model.addAttribute("portfolioHolder", portfolioHolder);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute PortfolioHolder updatedPortfolioHolder,
                                BindingResult bindingResult,
                                HttpSession ledger,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            PortfolioHolder currentPortfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
            if (currentPortfolioHolder == null) {
                return "redirect:/access";
            }

            // Validate essential fields
            if (updatedPortfolioHolder.getFullName() == null || updatedPortfolioHolder.getFullName().trim().isEmpty()) {
                bindingResult.rejectValue("fullName", "error.required", "Full name is required");
            }

            if (updatedPortfolioHolder.getEmail() == null || updatedPortfolioHolder.getEmail().trim().isEmpty()) {
                bindingResult.rejectValue("email", "error.required", "Email is required");
            } else if (!updatedPortfolioHolder.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                bindingResult.rejectValue("email", "error.invalid", "Please enter a valid email address");
            } else if (!currentPortfolioHolder.getEmail().equals(updatedPortfolioHolder.getEmail()) &&
                    !portfolioService.isEmailAvailable(updatedPortfolioHolder.getEmail())) {
                bindingResult.rejectValue("email", "error.exists", "Email already exists");
            }

            // Passcode validation
            if (updatedPortfolioHolder.getPasscode() != null && !updatedPortfolioHolder.getPasscode().isEmpty() &&
                    updatedPortfolioHolder.getPasscode().length() < 6) {
                bindingResult.rejectValue("passcode", "error.length", "Passcode must be at least 6 characters");
            }

            if (bindingResult.hasErrors()) {
                model.addAttribute("portfolioHolder", updatedPortfolioHolder);
                return "profile";
            }

            // Update only allowed fields
            currentPortfolioHolder.setFullName(updatedPortfolioHolder.getFullName());
            currentPortfolioHolder.setContactNumber(updatedPortfolioHolder.getContactNumber());

            // If email is changed, it's already validated above
            if (!currentPortfolioHolder.getEmail().equals(updatedPortfolioHolder.getEmail())) {
                currentPortfolioHolder.setEmail(updatedPortfolioHolder.getEmail());
            }

            // If passcode is changed
            if (updatedPortfolioHolder.getPasscode() != null && !updatedPortfolioHolder.getPasscode().isEmpty()) {
                currentPortfolioHolder.setPasscode(updatedPortfolioHolder.getPasscode());
            }

            portfolioService.update(currentPortfolioHolder);
            ledger.setAttribute("portfolioHolder", currentPortfolioHolder);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
            return "redirect:/profile";
        } catch (Exception e) {
            logger.error("Error updating profile", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
            return "redirect:/profile";
        }
    }

    @GetMapping("/signout")
    public String signOut(HttpSession ledger) {
        ledger.invalidate();
        return "redirect:/access";
    }
}