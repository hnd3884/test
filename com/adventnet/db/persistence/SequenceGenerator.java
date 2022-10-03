package com.adventnet.db.persistence;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.PersistenceException;

public interface SequenceGenerator
{
    void init(final String p0) throws PersistenceException;
    
    Object nextValue();
    
    Object setValue(final Object p0) throws DataAccessException;
    
    void cleanup() throws PersistenceException;
    
    void remove() throws PersistenceException;
    
    Object getCurrentValue();
    
    int getBatchSize();
    
    Object getStartValue();
    
    Object getMaxValue();
    
    String getName();
    
    void renameTo(final String p0) throws PersistenceException;
}
