package com.csye6220.financenavigator.service;

import com.csye6220.financenavigator.dao.LedgerEntryDAO;
import com.csye6220.financenavigator.model.Allocation;
import com.csye6220.financenavigator.model.InstrumentType;
import com.csye6220.financenavigator.model.LedgerEntry;
import com.csye6220.financenavigator.model.PortfolioHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LedgerServiceImpl implements LedgerService {

    private final LedgerEntryDAO LedgerEntryDAO;

    public LedgerServiceImpl(LedgerEntryDAO LedgerEntryDAO) {
        this.LedgerEntryDAO = LedgerEntryDAO;
    }

    @Override
    public void save(LedgerEntry ledgerEntry) {
        LedgerEntryDAO.save(ledgerEntry);
    }

    @Override
    @Transactional
    public void update(LedgerEntry ledgerEntry) {
        LedgerEntryDAO.update(ledgerEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public LedgerEntry findById(Long id) {
        return LedgerEntryDAO.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntry> findByPortfolioHolder(PortfolioHolder portfolioHolder) {
        return LedgerEntryDAO.findByPortfolioHolderWithAllocations(portfolioHolder);
    }


    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntry> findByAllocation(Allocation allocation) {
        return LedgerEntryDAO.findByAllocation(allocation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntry> findByPortfolioHolderAndAllocation(PortfolioHolder portfolioHolder, Allocation allocation) {
        return LedgerEntryDAO.findByPortfolioHolderAndAllocation(portfolioHolder, allocation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntry> findByPortfolioHolderAndInstrumentType(PortfolioHolder portfolioHolder, InstrumentType instrumentType) {
        return LedgerEntryDAO.findByPortfolioHolderAndInstrumentType(portfolioHolder, instrumentType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntry> findByPortfolioHolderAndDateRange(PortfolioHolder portfolioHolder, Date startDate, Date endDate) {
        return LedgerEntryDAO.findByPortfolioHolderAndDateRange(portfolioHolder, startDate, endDate);
    }

    @Override
    public boolean delete(Long id) {
        return LedgerEntryDAO.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getPortfolioSummary(PortfolioHolder portfolioHolder) {
        return LedgerEntryDAO.getSummaryByPortfolioHolder(portfolioHolder);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getPortfolioSummaryByDateRange(PortfolioHolder portfolioHolder, Date startDate, Date endDate) {
        return LedgerEntryDAO.getSummaryByPortfolioHolderAndDateRange(portfolioHolder, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntry> findByPortfolioHolderAndDateRangeWithAllocations(PortfolioHolder portfolioHolder, Date startDate, Date endDate) {
        return LedgerEntryDAO.findByPortfolioHolderAndDateRangeWithAllocations(portfolioHolder, startDate, endDate);
    }
}
