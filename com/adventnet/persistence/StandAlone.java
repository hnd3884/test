package com.adventnet.persistence;

public interface StandAlone
{
    void startDB() throws Exception;
    
    void loadModule(final String p0) throws Exception;
    
    void populateServerStatus() throws Exception;
    
    void stopDB() throws Exception;
    
    void startServer() throws Exception;
    
    void runStandAlone(final String... p0) throws Exception;
    
    void postPopulation() throws Exception;
    
    void prePopulation() throws Exception;
}
