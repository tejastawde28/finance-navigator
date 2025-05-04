package com.csye6220.financenavigator.service;

import com.csye6220.financenavigator.dao.PortfolioHolderDAO;
import com.csye6220.financenavigator.model.PortfolioHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    private PortfolioHolderDAO portfolioHolderDAO;

    @Override
    public void save(PortfolioHolder portfolioHolder) {
        portfolioHolderDAO.save(portfolioHolder);
    }

    @Override
    public void update(PortfolioHolder portfolioHolder) {
        portfolioHolderDAO.update(portfolioHolder);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioHolder findById(Long id) {
        return portfolioHolderDAO.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioHolder findByClientId(String clientId) {
        return portfolioHolderDAO.findByClientId(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioHolder findByEmail(String email) {
        return portfolioHolderDAO.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioHolder> findAll() {
        return portfolioHolderDAO.findAll();
    }

    @Override
    public boolean delete(Long id) {
        return portfolioHolderDAO.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isClientIdAvailable(String clientId) {
        return portfolioHolderDAO.isClientIdAvailable(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return portfolioHolderDAO.isEmailAvailable(email);
    }

    @Override
    @Transactional
    public boolean validateCredentials(String clientId, String passcode) {
        PortfolioHolder portfolioHolder = portfolioHolderDAO.findByClientId(clientId);
        if (portfolioHolder != null && portfolioHolder.getPasscode().equals(passcode)) {
            // Update last accessed timestamp
            portfolioHolder.setLastAccessed(new Date());
            portfolioHolderDAO.update(portfolioHolder);
            return true;
        }
        return false;
    }
}