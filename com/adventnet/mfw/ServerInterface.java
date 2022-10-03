package com.adventnet.mfw;

import java.util.Properties;

public interface ServerInterface
{
    void startServer(final Properties p0) throws Throwable;
    
    void sendStartUpNotification(final Properties p0) throws Exception;
    
    void shutDown(final boolean p0) throws Exception;
    
    int reinitialize(final boolean p0);
    
    int restoreDB(final String p0, final String p1) throws Exception;
    
    int fileBackup(final String p0, final String p1) throws Exception;
    
    void startDB() throws Exception;
    
    void stopDB() throws Exception;
    
    int backupDB(final String p0, final String p1, final int p2, final String p3) throws Exception;
    
    void runStandAlone() throws Exception;
    
    long getServerID();
    
    void runStandAlone(final String[] p0) throws Exception;
}
