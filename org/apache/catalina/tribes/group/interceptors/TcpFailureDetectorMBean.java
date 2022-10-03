package org.apache.catalina.tribes.group.interceptors;

public interface TcpFailureDetectorMBean
{
    int getOptionFlag();
    
    long getConnectTimeout();
    
    boolean getPerformSendTest();
    
    boolean getPerformReadTest();
    
    long getReadTestTimeout();
    
    int getRemoveSuspectsTimeout();
    
    void setPerformReadTest(final boolean p0);
    
    void setPerformSendTest(final boolean p0);
    
    void setReadTestTimeout(final long p0);
    
    void setConnectTimeout(final long p0);
    
    void setRemoveSuspectsTimeout(final int p0);
    
    void checkMembers(final boolean p0);
}
