package com.csye6220.financenavigator.service;

import com.csye6220.financenavigator.dao.AllocationDAO;
import com.csye6220.financenavigator.model.Allocation;
import com.csye6220.financenavigator.model.InstrumentType;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@Transactional
public class AllocationServiceImpl implements AllocationService {

    private static final Logger logger = LoggerFactory.getLogger(AllocationServiceImpl.class);
    private final AllocationDAO AllocationDAO;

    public AllocationServiceImpl(AllocationDAO AllocationDAO) {
        this.AllocationDAO = AllocationDAO;
    }

    @Override
    public void save(Allocation allocation) {
        AllocationDAO.save(allocation);
    }

    @Override
    public void update(Allocation allocation) {
        AllocationDAO.update(allocation);
    }

    @Override
    @Transactional(readOnly = true)
    public Allocation findById(Long id) {
        return AllocationDAO.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Allocation findByName(String name) {
        return AllocationDAO.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Allocation> findByInstrumentType(InstrumentType instrumentType) {
        return AllocationDAO.findByInstrumentType(instrumentType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Allocation> findAll() {
        return AllocationDAO.findAll();
    }

    @Override
    public boolean delete(Long id) {
        return AllocationDAO.delete(id);
    }

    @Override
    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void initDefaultAllocations() {
        logger.info("Initializing default allocations");
        try {
            // Try to check if allocations already exist
            List<Allocation> existingAllocations = null;
            try {
                existingAllocations = AllocationDAO.findAll();
            } catch (Exception e) {
                logger.info("Cannot check existing allocations. Table might not be created yet. Will proceed with creation.");
                existingAllocations = null;
            }

            if (existingAllocations == null || existingAllocations.isEmpty()) {
                logger.info("Creating default allocations");

                // Asset Categories
                Allocation salary = new Allocation("Salary", "Regular income from employment", InstrumentType.ASSET);
                salary.setIconClass("fas fa-briefcase");

                Allocation dividends = new Allocation("Dividends", "Income from investments", InstrumentType.ASSET);
                dividends.setIconClass("fas fa-chart-line");

                Allocation interest = new Allocation("Interest", "Bank interest and returns from deposits", InstrumentType.ASSET);
                interest.setIconClass("fas fa-university");

                Allocation bonus = new Allocation("Bonus", "Additional income from employment", InstrumentType.ASSET);
                bonus.setIconClass("fas fa-gift");

                Allocation other = new Allocation("Other Income", "Miscellaneous income sources", InstrumentType.ASSET);
                other.setIconClass("fas fa-plus-circle");

                // Liability Categories
                Allocation housing = new Allocation("Housing", "Rent, mortgage, property taxes", InstrumentType.LIABILITY);
                housing.setIconClass("fas fa-home");

                Allocation utilities = new Allocation("Utilities", "Electricity, water, gas, internet", InstrumentType.LIABILITY);
                utilities.setIconClass("fas fa-bolt");

                Allocation groceries = new Allocation("Groceries", "Food and household supplies", InstrumentType.LIABILITY);
                groceries.setIconClass("fas fa-shopping-cart");

                Allocation transportation = new Allocation("Transportation", "Fuel, public transit, vehicle maintenance", InstrumentType.LIABILITY);
                transportation.setIconClass("fas fa-car");

                Allocation healthcare = new Allocation("Healthcare", "Medical expenses, insurance", InstrumentType.LIABILITY);
                healthcare.setIconClass("fas fa-heartbeat");

                Allocation entertainment = new Allocation("Entertainment", "Dining out, movies, subscriptions", InstrumentType.LIABILITY);
                entertainment.setIconClass("fas fa-film");

                Allocation debt = new Allocation("Debt Payments", "Credit card, loans, other debt", InstrumentType.LIABILITY);
                debt.setIconClass("fas fa-credit-card");

                // Save all default categories
                try {
                    AllocationDAO.save(salary);
                    AllocationDAO.save(dividends);
                    AllocationDAO.save(interest);
                    AllocationDAO.save(bonus);
                    AllocationDAO.save(other);
                    AllocationDAO.save(housing);
                    AllocationDAO.save(utilities);
                    AllocationDAO.save(groceries);
                    AllocationDAO.save(transportation);
                    AllocationDAO.save(healthcare);
                    AllocationDAO.save(entertainment);
                    AllocationDAO.save(debt);
                    logger.info("Default allocations created successfully");
                } catch (Exception e) {
                    logger.error("Error saving default allocations", e);
                }
            } else {
                logger.info("Default allocations already exist. Skipping initialization.");
            }
        } catch (Exception e) {
            logger.error("Error during allocation initialization", e);
        }
    }
}