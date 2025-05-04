package com.csye6220.financenavigator.dao;

import com.csye6220.financenavigator.model.Allocation;
import com.csye6220.financenavigator.model.InstrumentType;

import java.util.List;

public interface AllocationDAO {
    void save(Allocation allocation);

    void update(Allocation allocation);

    Allocation findById(Long id);

    Allocation findByName(String name);

    List<Allocation> findByInstrumentType(InstrumentType instrumentType);

    List<Allocation> findAll();

    boolean delete(Long id);
}
