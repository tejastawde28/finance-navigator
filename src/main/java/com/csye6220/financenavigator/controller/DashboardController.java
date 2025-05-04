package com.csye6220.financenavigator.controller;

import com.csye6220.financenavigator.model.InstrumentType;
import com.csye6220.financenavigator.model.LedgerEntry;
import com.csye6220.financenavigator.model.PortfolioHolder;
import com.csye6220.financenavigator.service.AllocationService;
import com.csye6220.financenavigator.service.LedgerService;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private AllocationService allocationService;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping("/dashboard")
    public String dashboard(HttpSession ledger, Model model) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        // Get portfolio summary
        Map<String, Object> summary = ledgerService.getPortfolioSummary(portfolioHolder);
        model.addAttribute("summary", summary);

        // Get recent transactions
        List<LedgerEntry> recentEntries = ledgerService.findByPortfolioHolder(portfolioHolder);
        if (recentEntries.size() > 5) {
            recentEntries = recentEntries.subList(0, 5);
        }
        model.addAttribute("recentEntries", recentEntries);
        model.addAttribute("portfolioHolder", portfolioHolder);

        // Get assets and liabilities
        List<LedgerEntry> assets = ledgerService.findByPortfolioHolderAndInstrumentType(portfolioHolder, InstrumentType.ASSET);
        List<LedgerEntry> liabilities = ledgerService.findByPortfolioHolderAndInstrumentType(portfolioHolder, InstrumentType.LIABILITY);
        model.addAttribute("assets", assets);
        model.addAttribute("liabilities", liabilities);

        return "dashboard";
    }

    @GetMapping("/report")
    public String report(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession ledger,
            Model model) throws ParseException {

        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            return "redirect:/access";
        }

        // Add portfolioHolder to the model (needed for the header)
        model.addAttribute("portfolioHolder", portfolioHolder);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);


        model.addAttribute("netTotal", BigDecimal.ZERO);
        model.addAttribute("totalAssetsAmount", BigDecimal.ZERO);
        model.addAttribute("totalLiabilitiesAmount", BigDecimal.ZERO);

        // Add an empty periodSummary map to avoid null
        Map<String, Object> emptySummary = new HashMap<>();
        emptySummary.put("totalAssets", BigDecimal.ZERO);
        emptySummary.put("totalLiabilities", BigDecimal.ZERO);
        emptySummary.put("netChange", BigDecimal.ZERO);
        model.addAttribute("periodSummary", emptySummary);

        // Don't process anything if dates are not provided
        if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty()) {
            return "report";
        }

        // Validate dates manually
        Map<String, String> dateErrors = validateDates(startDate, endDate);

        // If validation errors, add them to the model
        if (!dateErrors.isEmpty()) {
            if (dateErrors.containsKey("startDate")) {
                model.addAttribute("startDateError", dateErrors.get("startDate"));
            }
            if (dateErrors.containsKey("endDate")) {
                model.addAttribute("endDateError", dateErrors.get("endDate"));
            }
            return "report";
        }

        // Process dates when provided (now validated)
        Date start = dateFormat.parse(startDate);
        Date end = dateFormat.parse(endDate);

        // Get data for the period
        Map<String, Object> periodSummary = ledgerService.getPortfolioSummaryByDateRange(portfolioHolder, start, end);

        // Ensure summary values are not null
        ensureNonNullBigDecimals(periodSummary);
        model.addAttribute("periodSummary", periodSummary);

        List<LedgerEntry> periodEntries = ledgerService.findByPortfolioHolderAndDateRangeWithAllocations(portfolioHolder, start, end);
        model.addAttribute("periodEntries", periodEntries);

        // Calculate totals for the table footer
        BigDecimal totalAssetsAmount = BigDecimal.ZERO;
        BigDecimal totalLiabilitiesAmount = BigDecimal.ZERO;

        for (LedgerEntry entry : periodEntries) {
            if (entry.getAllocation().getInstrumentType() == InstrumentType.ASSET) {
                totalAssetsAmount = totalAssetsAmount.add(entry.getAmount());
            } else {
                totalLiabilitiesAmount = totalLiabilitiesAmount.add(entry.getAmount());
            }
        }

        BigDecimal netTotal = totalAssetsAmount.subtract(totalLiabilitiesAmount);

        // Add calculated values to model
        model.addAttribute("totalAssetsAmount", totalAssetsAmount);
        model.addAttribute("totalLiabilitiesAmount", totalLiabilitiesAmount);
        model.addAttribute("netTotal", netTotal);

        // Compute the color class for the net total to avoid JSP calculation errors
        String netTotalColorClass = (netTotal.compareTo(BigDecimal.ZERO) >= 0) ? "text-success" : "text-danger";
        model.addAttribute("netTotalColorClass", netTotalColorClass);

        // Get asset and liability data
        model.addAttribute("assets", ledgerService.findByPortfolioHolderAndInstrumentType(portfolioHolder, InstrumentType.ASSET));
        model.addAttribute("liabilities", ledgerService.findByPortfolioHolderAndInstrumentType(portfolioHolder, InstrumentType.LIABILITY));

        return "report";
    }

    @GetMapping("/report/pdf")
    public void generateReportPdf(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession ledger,
            HttpServletResponse response,
            Model model) throws Exception {

        PortfolioHolder portfolioHolder = (PortfolioHolder) ledger.getAttribute("portfolioHolder");
        if (portfolioHolder == null) {
            response.sendRedirect("/access");
            return;
        }

        // Validate dates manually
        Map<String, String> dateErrors = validateDates(startDate, endDate);

        // Handle validation errors
        if (!dateErrors.isEmpty()) {
            response.setContentType("text/html");
            StringBuilder errorMessages = new StringBuilder();
            errorMessages.append("<html><body><h2>Validation Error</h2><ul>");

            dateErrors.values().forEach(error ->
                    errorMessages.append("<li>").append(error).append("</li>")
            );

            errorMessages.append("</ul><p><a href='/report'>Back to Reports</a></p></body></html>");
            response.getWriter().write(errorMessages.toString());
            return;
        }

        // Parse dates (now safe after validation)
        Date start = dateFormat.parse(startDate);
        Date end = dateFormat.parse(endDate);

        // Get data for the period
        Map<String, Object> periodSummary = ledgerService.getPortfolioSummaryByDateRange(portfolioHolder, start, end);

        // Ensure summary values are not null
        ensureNonNullBigDecimals(periodSummary);

        List<LedgerEntry> periodEntries = ledgerService.findByPortfolioHolderAndDateRangeWithAllocations(portfolioHolder, start, end);

        // Calculate totals
        BigDecimal totalAssetsAmount = BigDecimal.ZERO;
        BigDecimal totalLiabilitiesAmount = BigDecimal.ZERO;

        for (LedgerEntry entry : periodEntries) {
            if (entry.getAllocation().getInstrumentType() == InstrumentType.ASSET) {
                totalAssetsAmount = totalAssetsAmount.add(entry.getAmount());
            } else {
                totalLiabilitiesAmount = totalLiabilitiesAmount.add(entry.getAmount());
            }
        }

        BigDecimal netTotal = totalAssetsAmount.subtract(totalLiabilitiesAmount);

        // Set response headers
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=financial_report_" +
                dateFormat.format(start) + "_to_" + dateFormat.format(end) + ".pdf");

        // Create PDF document
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Add title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Financial Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Add date range
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
        Paragraph dateRange = new Paragraph("Period: " + dateFormat.format(start) + " to " + dateFormat.format(end), normalFont);
        dateRange.setAlignment(Element.ALIGN_CENTER);
        document.add(dateRange);
        document.add(new Paragraph(" ")); // Empty line

        // Add summary
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        document.add(new Paragraph("Summary", headerFont));
        document.add(new Paragraph(" "));

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);

        // Assets
        summaryTable.addCell("Total Assets");
        summaryTable.addCell("$" + periodSummary.get("totalAssets"));

        // Liabilities
        summaryTable.addCell("Total Liabilities");
        summaryTable.addCell("$" + periodSummary.get("totalLiabilities"));

        // Net Change
        summaryTable.addCell("Net Change");
        summaryTable.addCell("$" + periodSummary.get("netChange"));

        document.add(summaryTable);
        document.add(new Paragraph(" "));

        // Add transactions table
        document.add(new Paragraph("Transactions", headerFont));
        document.add(new Paragraph(" "));

        // Create table with proper column widths
        PdfPTable transactionTable = new PdfPTable(6);
        transactionTable.setWidthPercentage(100);
        float[] columnWidths = {1, 2.5f, 1.5f, 1, 1, 1};
        transactionTable.setWidths(columnWidths);

        // Add headers
        Font headerCellFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        String[] headers = {"Date", "Description", "Allocation", "Type", "Reference", "Amount"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerCellFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            transactionTable.addCell(cell);
        }

        // Add data
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 11);
        for (LedgerEntry entry : periodEntries) {
            // Date
            PdfPCell dateCell = new PdfPCell(new Phrase(displayDateFormat.format(entry.getEntryDate()), dataFont));
            dateCell.setPadding(5);
            transactionTable.addCell(dateCell);

            // Description
            PdfPCell descCell = new PdfPCell(new Phrase(entry.getDescription(), dataFont));
            descCell.setPadding(5);
            transactionTable.addCell(descCell);

            // Allocation
            PdfPCell allocCell = new PdfPCell(new Phrase(entry.getAllocation().getName(), dataFont));
            allocCell.setPadding(5);
            transactionTable.addCell(allocCell);

            // Type
            PdfPCell typeCell = new PdfPCell(new Phrase(entry.getAllocation().getInstrumentType().getDisplayName(), dataFont));
            typeCell.setPadding(5);
            transactionTable.addCell(typeCell);

            // Reference
            PdfPCell refCell = new PdfPCell(new Phrase(entry.getReference() != null ? entry.getReference() : "", dataFont));
            refCell.setPadding(5);
            transactionTable.addCell(refCell);

            // Amount
            String amountStr = "$" + (entry.getAllocation().getInstrumentType() == InstrumentType.ASSET ?
                    entry.getAmount().toString() : "-" + entry.getAmount().toString());
            Font amountFont = new Font(Font.FontFamily.HELVETICA, 11,
                    Font.NORMAL,
                    entry.getAllocation().getInstrumentType() == InstrumentType.ASSET ? BaseColor.GREEN : BaseColor.RED);
            PdfPCell amountCell = new PdfPCell(new Phrase(amountStr, amountFont));
            amountCell.setPadding(5);
            amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionTable.addCell(amountCell);
        }

        // Add a separator
        PdfPCell separator = new PdfPCell(new Phrase(""));
        separator.setColspan(6);
        separator.setBorderWidthTop(2);
        separator.setBorderWidthBottom(0);
        separator.setBorderWidthLeft(0);
        separator.setBorderWidthRight(0);
        transactionTable.addCell(separator);

        // Add total rows
        Font totalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

        // Assets Total
        PdfPCell assetTotalLabel = new PdfPCell(new Phrase("Total Assets", totalFont));
        assetTotalLabel.setColspan(5);
        assetTotalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
        assetTotalLabel.setPadding(5);
        assetTotalLabel.setBorderWidth(0);
        transactionTable.addCell(assetTotalLabel);

        PdfPCell assetAmountCell = new PdfPCell(new Phrase("$" + totalAssetsAmount,
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.GREEN)));
        assetAmountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        assetAmountCell.setPadding(5);
        assetAmountCell.setBorderWidth(0);
        transactionTable.addCell(assetAmountCell);

        // Liabilities Total
        PdfPCell liabilityTotalLabel = new PdfPCell(new Phrase("Total Liabilities", totalFont));
        liabilityTotalLabel.setColspan(5);
        liabilityTotalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
        liabilityTotalLabel.setPadding(5);
        liabilityTotalLabel.setBorderWidth(0);
        transactionTable.addCell(liabilityTotalLabel);

        PdfPCell liabilityAmountCell = new PdfPCell(new Phrase("$" + totalLiabilitiesAmount,
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.RED)));
        liabilityAmountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        liabilityAmountCell.setPadding(5);
        liabilityAmountCell.setBorderWidth(0);
        transactionTable.addCell(liabilityAmountCell);

        // Net Total
        PdfPCell netTotalLabel = new PdfPCell(new Phrase("Net Total",
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        netTotalLabel.setColspan(5);
        netTotalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
        netTotalLabel.setPadding(5);
        netTotalLabel.setBorderWidthTop(2);
        netTotalLabel.setBorderWidthBottom(0);
        netTotalLabel.setBorderWidthLeft(0);
        netTotalLabel.setBorderWidthRight(0);
        transactionTable.addCell(netTotalLabel);

        BaseColor netTotalColor = netTotal.compareTo(BigDecimal.ZERO) >= 0 ? BaseColor.GREEN : BaseColor.RED;
        PdfPCell netAmountCell = new PdfPCell(new Phrase("$" + netTotal.abs(),
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, netTotalColor)));
        netAmountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        netAmountCell.setPadding(5);
        netAmountCell.setBorderWidthTop(2);
        netAmountCell.setBorderWidthBottom(0);
        netAmountCell.setBorderWidthLeft(0);
        netAmountCell.setBorderWidthRight(0);
        transactionTable.addCell(netAmountCell);

        document.add(transactionTable);

        // Add footer with generation date
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Report generated on " +
                displayDateFormat.format(new Date()),
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
    }

    /**
     * Ensure all BigDecimal values in the period summary are not null
     */
    private void ensureNonNullBigDecimals(Map<String, Object> periodSummary) {
        if (periodSummary != null) {
            // Check and set default values for map entries
            if (periodSummary.get("totalAssets") == null) {
                periodSummary.put("totalAssets", BigDecimal.ZERO);
            }
            if (periodSummary.get("totalLiabilities") == null) {
                periodSummary.put("totalLiabilities", BigDecimal.ZERO);
            }
            if (periodSummary.get("netChange") == null) {
                periodSummary.put("netChange", BigDecimal.ZERO);
            }
        }
    }

    /**
     * Validate date parameters for reports
     * @return Map of field errors (fieldName -> errorMessage)
     */
    private Map<String, String> validateDates(String startDate, String endDate) {
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