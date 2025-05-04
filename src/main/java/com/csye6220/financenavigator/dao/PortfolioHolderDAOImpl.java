package com.csye6220.financenavigator.dao;

import com.csye6220.financenavigator.model.PortfolioHolder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Repository
@Transactional
public class PortfolioHolderDAOImpl implements PortfolioHolderDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(PortfolioHolder portfolioHolder) {
        getCurrentSession().persist(portfolioHolder);
    }

    @Override
    public void update(PortfolioHolder portfolioHolder) {
        getCurrentSession().merge(portfolioHolder);
    }

    @Override
    public PortfolioHolder findById(Long id) {
        return getCurrentSession().get(PortfolioHolder.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioHolder findByClientId(String clientId) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<PortfolioHolder> query = builder.createQuery(PortfolioHolder.class);
        Root<PortfolioHolder> root = query.from(PortfolioHolder.class);
        query.select(root).where(builder.equal(root.get("clientId"), clientId));

        Query<PortfolioHolder> q = getCurrentSession().createQuery(query);
        List<PortfolioHolder> results = q.getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioHolder findByEmail(String email) {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<PortfolioHolder> query = builder.createQuery(PortfolioHolder.class);
        Root<PortfolioHolder> root = query.from(PortfolioHolder.class);
        query.select(root).where(builder.equal(root.get("email"), email));

        Query<PortfolioHolder> q = getCurrentSession().createQuery(query);
        List<PortfolioHolder> results = q.getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioHolder> findAll() {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<PortfolioHolder> query = builder.createQuery(PortfolioHolder.class);
        Root<PortfolioHolder> root = query.from(PortfolioHolder.class);
        query.select(root);

        return getCurrentSession().createQuery(query).getResultList();
    }

    @Override
    public boolean delete(Long id) {
        PortfolioHolder portfolioHolder = findById(id);
        if (portfolioHolder != null) {
            getCurrentSession().remove(portfolioHolder);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isClientIdAvailable(String clientId) {
        return findByClientId(clientId) == null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return findByEmail(email) == null;
    }
}