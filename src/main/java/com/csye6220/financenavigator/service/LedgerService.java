package com.csye6220.financenavigator.service;

import com.csye6220.financenavigator.model.Allocation;
import com.csye6220.financenavigator.model.InstrumentType;
import com.csye6220.financenavigator.model.LedgerEntry;
import com.csye6220.financenavigator.model.PortfolioHolder;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LedgerService {

    void save(LedgerEntry ledgerEntry);

    void update(LedgerEntry ledgerEntry);

    LedgerEntry findById(Long id);

    List<LedgerEntry> findByPortfolioHolder(PortfolioHolder portfolioHolder);

    List<LedgerEntry> findByAllocation(Allocation allocation);

    List<LedgerEntry> findByPortfolioHolderAndAllocation(PortfolioHolder portfolioHolder, Allocation allocation);

    List<LedgerEntry> findByPortfolioHolderAndInstrumentType(PortfolioHolder portfolioHolder, InstrumentType instrumentType);

    List<LedgerEntry> findByPortfolioHolderAndDateRange(PortfolioHolder portfolioHolder, Date startDate, Date endDate);

    boolean delete(Long id);

    // Summary methods
    Map<String, Object> getPortfolioSummary(PortfolioHolder portfolioHolder);

    Map<String, Object> getPortfolioSummaryByDateRange(PortfolioHolder portfolioHolder, Date startDate, Date endDate);

    List<LedgerEntry> findByPortfolioHolderAndDateRangeWithAllocations(PortfolioHolder portfolioHolder, Date startDate, Date endDate);


}
