package com.csye6220.financenavigator.model;

public enum InstrumentType {
    ASSET("Asset"),     // Income, investments, etc.
    LIABILITY("Liability");  // Expenses, debt, etc.

    private final String displayName;

    InstrumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
