package com.csye6220.financenavigator.dao;

import com.csye6220.financenavigator.model.Allocation;
import com.csye6220.financenavigator.model.InstrumentType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Repository
public class AllocationDAOImpl implements AllocationDAO {

    private final SessionFactory sessionFactory;

    public AllocationDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(Allocation allocation) {
        getCurrentSession().persist(allocation);
    }

    @Override
    public void update(Allocation allocation) {
        getCurrentSession().merge(allocation);
    }

    @Override
    public Allocation findById(Long id) {
        return getCurrentSession().get(Allocation.class, id);
    }

    @Override
    public Allocation findByName(String name) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Allocation> query = builder.createQuery(Allocation.class);
        Root<Allocation> root = query.from(Allocation.class);
        query.select(root).where(builder.equal(root.get("name"), name));

        Query<Allocation> q = getCurrentSession().createQuery(query);
        List<Allocation> results = q.getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Allocation> findByInstrumentType(InstrumentType instrumentType) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Allocation> query = builder.createQuery(Allocation.class);
        Root<Allocation> root = query.from(Allocation.class);
        query.select(root).where(builder.equal(root.get("instrumentType"), instrumentType));
        query.orderBy(builder.asc(root.get("name")));

        return getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public List<Allocation> findAll() {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Allocation> query = builder.createQuery(Allocation.class);
        Root<Allocation> root = query.from(Allocation.class);
        query.select(root);
        query.orderBy(builder.asc(root.get("name")));

        return getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public boolean delete(Long id) {
        Allocation allocation = findById(id);
        if (allocation != null) {
            getCurrentSession().remove(allocation);
            return true;
        }
        return false;
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}