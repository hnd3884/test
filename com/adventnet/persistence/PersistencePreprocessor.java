package com.adventnet.persistence;

public interface PersistencePreprocessor
{
    void initialize() throws PersistenceException;
    
    void initialize(final boolean p0) throws PersistenceException;
    
    void preReady() throws PersistenceException;
    
    void preMetaDataFetch() throws PersistenceException;
    
    void prePersonalityFetch() throws PersistenceException;
    
    void postReady() throws PersistenceException;
    
    void postMetaDataFetch() throws PersistenceException;
    
    void postPersonalityFetch() throws PersistenceException;
}
