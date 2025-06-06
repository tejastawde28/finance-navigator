package com.csye6220.financenavigator.service;

import com.csye6220.financenavigator.model.PortfolioHolder;
import java.util.List;

public interface PortfolioService {

    void save(PortfolioHolder portfolioHolder);

    void update(PortfolioHolder portfolioHolder);

    PortfolioHolder findById(Long id);

    PortfolioHolder findByClientId(String clientId);

    PortfolioHolder findByEmail(String email);

    List<PortfolioHolder> findAll();

    boolean delete(Long id);

    boolean isClientIdAvailable(String clientId);

    boolean isEmailAvailable(String email);

    boolean validateCredentials(String clientId, String passcode);
}