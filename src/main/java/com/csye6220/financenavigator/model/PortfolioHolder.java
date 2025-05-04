package com.csye6220.financenavigator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "portfolio_holders")
public class PortfolioHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", unique = true, nullable = false, length = 50)
    private String clientId;

    @Column(name = "passcode", nullable = false, length = 100)
    private String passcode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(name = "creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "last_accessed")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAccessed;

    @OneToMany(mappedBy = "portfolioHolder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LedgerEntry> ledgerEntries = new HashSet<>();

    // Constructors
    public PortfolioHolder() {
        this.creationDate = new Date();
    }

    public PortfolioHolder(String clientId, String passcode, String fullName, String email) {
        this();
        this.clientId = clientId;
        this.passcode = passcode;
        this.fullName = fullName;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public Set<LedgerEntry> getLedgerEntries() {
        return ledgerEntries;
    }

    public void setLedgerEntries(Set<LedgerEntry> ledgerEntries) {
        this.ledgerEntries = ledgerEntries;
    }

    // Helper methods
    public void addLedgerEntry(LedgerEntry ledgerEntry) {
        ledgerEntries.add(ledgerEntry);
        ledgerEntry.setPortfolioHolder(this);
    }

    public void removeLedgerEntry(LedgerEntry ledgerEntry) {
        ledgerEntries.remove(ledgerEntry);
        ledgerEntry.setPortfolioHolder(null);
    }

    @Override
    public String toString() {
        return "PortfolioHolder{" +
                "id=" + id +
                ", clientId='" + clientId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}