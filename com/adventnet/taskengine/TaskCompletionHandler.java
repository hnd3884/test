package com.adventnet.taskengine;

public interface TaskCompletionHandler
{
    void onSuccess(final TaskContext p0) throws Exception;
    
    void onFailure(final TaskContext p0) throws Exception;
}
