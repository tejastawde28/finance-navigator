package com.csye6220.financenavigator.dao;

import com.csye6220.financenavigator.model.Allocation;
import com.csye6220.financenavigator.model.InstrumentType;
import com.csye6220.financenavigator.model.LedgerEntry;
import com.csye6220.financenavigator.model.PortfolioHolder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.*;

@Repository
public class LedgerEntryDAOImpl implements LedgerEntryDAO {

    private final SessionFactory sessionFactory;

    public LedgerEntryDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(LedgerEntry ledgerEntry) {
        getCurrentSession().persist(ledgerEntry);
    }

    @Override
    public void update(LedgerEntry ledgerEntry) {
        getCurrentSession().merge(ledgerEntry);
    }

    @Override
    public LedgerEntry findById(Long id) {
        return getCurrentSession().get(LedgerEntry.class, id);
    }

    @Override
    public List<LedgerEntry> findByPortfolioHolder(PortfolioHolder portfolioHolder) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<LedgerEntry> query = builder.createQuery(LedgerEntry.class);
        Root<LedgerEntry> root = query.from(LedgerEntry.class);
        query.select(root).where(builder.equal(root.get("portfolioHolder"), portfolioHolder));
        query.orderBy(builder.desc(root.get("entryDate")));

        return getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public List<LedgerEntry> findByPortfolioHolderWithAllocations(PortfolioHolder portfolioHolder) {
        // Use a join fetch to eagerly load allocations
        String hql = "SELECT le FROM LedgerEntry le JOIN FETCH le.allocation WHERE le.portfolioHolder = :portfolioHolder ORDER BY le.entryDate DESC";
        Query<LedgerEntry> query = getCurrentSession().createQuery(hql, LedgerEntry.class);
        query.setParameter("portfolioHolder", portfolioHolder);
        return query.getResultList();
    }

    @Override
    public List<LedgerEntry> findByAllocation(Allocation allocation) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<LedgerEntry> query = builder.createQuery(LedgerEntry.class);
        Root<LedgerEntry> root = query.from(LedgerEntry.class);
        query.select(root).where(builder.equal(root.get("allocation"), allocation));
        query.orderBy(builder.desc(root.get("entryDate")));

        return getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public List<LedgerEntry> findByPortfolioHolderAndAllocation(PortfolioHolder portfolioHolder, Allocation allocation) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<LedgerEntry> query = builder.createQuery(LedgerEntry.class);
        Root<LedgerEntry> root = query.from(LedgerEntry.class);

        Predicate portfolioPredicate = builder.equal(root.get("portfolioHolder"), portfolioHolder);
        Predicate allocationPredicate = builder.equal(root.get("allocation"), allocation);
        query.select(root).where(builder.and(portfolioPredicate, allocationPredicate));
        query.orderBy(builder.desc(root.get("entryDate")));

        return getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public List<LedgerEntry> findByPortfolioHolderAndInstrumentType(PortfolioHolder portfolioHolder, InstrumentType instrumentType) {
        String hql = "SELECT le FROM LedgerEntry le JOIN le.allocation a WHERE le.portfolioHolder = :portfolioHolder AND a.instrumentType = :instrumentType ORDER BY le.entryDate DESC";
        Query<LedgerEntry> query = getCurrentSession().createQuery(hql, LedgerEntry.class);
        query.setParameter("portfolioHolder", portfolioHolder);
        query.setParameter("instrumentType", instrumentType);

        return query.getResultList();
    }

    @Override
    public List<LedgerEntry> findByPortfolioHolderAndDateRange(PortfolioHolder portfolioHolder, Date startDate, Date endDate) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<LedgerEntry> query = builder.createQuery(LedgerEntry.class);
        Root<LedgerEntry> root = query.from(LedgerEntry.class);

        Predicate portfolioPredicate = builder.equal(root.get("portfolioHolder"), portfolioHolder);
        Predicate startDatePredicate = builder.greaterThanOrEqualTo(root.get("entryDate"), startDate);
        Predicate endDatePredicate = builder.lessThanOrEqualTo(root.get("entryDate"), endDate);

        query.select(root).where(builder.and(portfolioPredicate, startDatePredicate, endDatePredicate));
        query.orderBy(builder.desc(root.get("entryDate")));

        return getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public List<LedgerEntry> findAll() {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<LedgerEntry> query = builder.createQuery(LedgerEntry.class);
        Root<LedgerEntry> root = query.from(LedgerEntry.class);
        query.select(root);
        query.orderBy(builder.desc(root.get("entryDate")));

        return getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public boolean delete(Long id) {
        LedgerEntry ledgerEntry = findById(id);
        if (ledgerEntry != null) {
            getCurrentSession().remove(ledgerEntry);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> getSummaryByPortfolioHolder(PortfolioHolder portfolioHolder) {
        Map<String, Object> summary = new HashMap<>();

        // Total assets
        String assetHql = "SELECT SUM(le.amount) FROM LedgerEntry le JOIN le.allocation a WHERE le.portfolioHolder = :portfolioHolder AND a.instrumentType = :instrumentType";
        Query<BigDecimal> assetQuery = getCurrentSession().createQuery(assetHql, BigDecimal.class);
        assetQuery.setParameter("portfolioHolder", portfolioHolder);
        assetQuery.setParameter("instrumentType", InstrumentType.ASSET);
        BigDecimal totalAssets = assetQuery.uniqueResult();
        if (totalAssets == null) totalAssets = BigDecimal.ZERO;

        // Total liabilities
        Query<BigDecimal> liabilityQuery = getCurrentSession().createQuery(assetHql, BigDecimal.class);
        liabilityQuery.setParameter("portfolioHolder", portfolioHolder);
        liabilityQuery.setParameter("instrumentType", InstrumentType.LIABILITY);
        BigDecimal totalLiabilities = liabilityQuery.uniqueResult();
        if (totalLiabilities == null) totalLiabilities = BigDecimal.ZERO;

        // Net worth
        BigDecimal netWorth = totalAssets.subtract(totalLiabilities);

        summary.put("totalAssets", totalAssets);
        summary.put("totalLiabilities", totalLiabilities);
        summary.put("netWorth", netWorth);

        // Get allocation breakdown
        String allocationHql = "SELECT a.name, SUM(le.amount) FROM LedgerEntry le JOIN le.allocation a WHERE le.portfolioHolder = :portfolioHolder GROUP BY a.name";
        Query<Object[]> allocationQuery = getCurrentSession().createQuery(allocationHql, Object[].class);
        allocationQuery.setParameter("portfolioHolder", portfolioHolder);
        List<Object[]> allocationResults = allocationQuery.getResultList();

        Map<String, BigDecimal> allocationBreakdown = new HashMap<>();
        for (Object[] row : allocationResults) {
            String allocationName = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            allocationBreakdown.put(allocationName, amount);
        }
        summary.put("allocationBreakdown", allocationBreakdown);

        return summary;
    }

    @Override
    public Map<String, Object> getSummaryByPortfolioHolderAndDateRange(PortfolioHolder portfolioHolder, Date startDate, Date endDate) {
        Map<String, Object> summary = new HashMap<>();

        // Total assets within date range
        String assetHql = "SELECT SUM(le.amount) FROM LedgerEntry le JOIN le.allocation a WHERE le.portfolioHolder = :portfolioHolder AND a.instrumentType = :instrumentType AND le.entryDate BETWEEN :startDate AND :endDate";
        Query<BigDecimal> assetQuery = getCurrentSession().createQuery(assetHql, BigDecimal.class);
        assetQuery.setParameter("portfolioHolder", portfolioHolder);
        assetQuery.setParameter("instrumentType", InstrumentType.ASSET);
        assetQuery.setParameter("startDate", startDate);
        assetQuery.setParameter("endDate", endDate);
        var totalAssets = assetQuery.uniqueResult();
        if (totalAssets == null) totalAssets = BigDecimal.ZERO;

        // Total liabilities within date range
        Query<BigDecimal> liabilityQuery = getCurrentSession().createQuery(assetHql, BigDecimal.class);
        liabilityQuery.setParameter("portfolioHolder", portfolioHolder);
        liabilityQuery.setParameter("instrumentType", InstrumentType.LIABILITY);
        liabilityQuery.setParameter("startDate", startDate);
        liabilityQuery.setParameter("endDate", endDate);
        BigDecimal totalLiabilities = liabilityQuery.uniqueResult();
        if (totalLiabilities == null) totalLiabilities = BigDecimal.ZERO;

        // Net change
        BigDecimal netChange = totalAssets.subtract(totalLiabilities);

        summary.put("totalAssets", totalAssets);
        summary.put("totalLiabilities", totalLiabilities);
        summary.put("netChange", netChange);

        return summary;
    }

    @Override
    public List<LedgerEntry> findByPortfolioHolderAndDateRangeWithAllocations(PortfolioHolder portfolioHolder, Date startDate, Date endDate) {
        String hql = "SELECT le FROM LedgerEntry le JOIN FETCH le.allocation WHERE le.portfolioHolder = :portfolioHolder AND le.entryDate BETWEEN :startDate AND :endDate ORDER BY le.entryDate DESC";
        Query<LedgerEntry> query = getCurrentSession().createQuery(hql, LedgerEntry.class);
        query.setParameter("portfolioHolder", portfolioHolder);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        return query.getResultList();
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}