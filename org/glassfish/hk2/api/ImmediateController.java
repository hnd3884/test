package org.glassfish.hk2.api;

import java.util.concurrent.Executor;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface ImmediateController
{
    Executor getExecutor();
    
    void setExecutor(final Executor p0) throws IllegalStateException;
    
    long getThreadInactivityTimeout();
    
    void setThreadInactivityTimeout(final long p0) throws IllegalArgumentException;
    
    ImmediateServiceState getImmediateState();
    
    void setImmediateState(final ImmediateServiceState p0);
    
    public enum ImmediateServiceState
    {
        SUSPENDED, 
        RUNNING;
    }
}
