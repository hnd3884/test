package org.apache.coyote;

public interface AsyncContextCallback
{
    void fireOnComplete();
    
    boolean isAvailable();
    
    void incrementInProgressAsyncCount();
    
    void decrementInProgressAsyncCount();
}
