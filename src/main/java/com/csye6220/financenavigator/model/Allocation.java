package com.csye6220.financenavigator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "allocations")
public class Allocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "instrument_type", nullable = false)
    private InstrumentType instrumentType;

    @Column(name = "icon_class", length = 50)
    private String iconClass;

    @OneToMany(mappedBy = "allocation", fetch = FetchType.LAZY)
    private Set<LedgerEntry> ledgerEntries = new HashSet<>();

    // Constructors
    public Allocation() {
    }

    public Allocation(String name, InstrumentType instrumentType) {
        this.name = name;
        this.instrumentType = instrumentType;
    }

    public Allocation(String name, String description, InstrumentType instrumentType) {
        this.name = name;
        this.description = description;
        this.instrumentType = instrumentType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InstrumentType getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(InstrumentType instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
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
        ledgerEntry.setAllocation(this);
    }

    public void removeLedgerEntry(LedgerEntry ledgerEntry) {
        ledgerEntries.remove(ledgerEntry);
        ledgerEntry.setAllocation(null);
    }

    @Override
    public String toString() {
        return "Allocation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", instrumentType=" + instrumentType +
                '}';
    }
}