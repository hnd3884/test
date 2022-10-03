package org.apache.tomcat.websocket;

public interface BackgroundProcess
{
    void backgroundProcess();
    
    void setProcessPeriod(final int p0);
    
    int getProcessPeriod();
}
