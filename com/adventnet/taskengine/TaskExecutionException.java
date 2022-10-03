package com.adventnet.taskengine;

public class TaskExecutionException extends Exception
{
    public TaskExecutionException() {
    }
    
    public TaskExecutionException(final String msg) {
        super(msg);
    }
    
    public TaskExecutionException(final String msg, final Throwable thr) {
        super(msg, thr);
    }
    
    public TaskExecutionException(final Throwable msg) {
        super(msg);
    }
}
