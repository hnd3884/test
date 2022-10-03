package com.adventnet.management.transaction;

import javax.transaction.SystemException;
import com.adventnet.management.scheduler.Scheduler;

public class TransactionTimeoutHandler implements Runnable
{
    private TransactionAPI tapi;
    private Thread threadObj;
    private int timeOutValue;
    private int timeOutRetries;
    private Scheduler sch;
    
    public TransactionTimeoutHandler(final TransactionAPI tapi, final Thread threadObj, final Scheduler sch, final int timeOutValue) {
        this.tapi = null;
        this.threadObj = null;
        this.timeOutValue = 5000;
        this.timeOutRetries = 10;
        this.tapi = tapi;
        this.threadObj = threadObj;
        this.timeOutValue = timeOutValue;
        (this.sch = sch).scheduleTask(this, System.currentTimeMillis() + this.timeOutValue);
    }
    
    public void run() {
        System.err.println("Transaction operation timed out on thread " + this.threadObj + " and this may result in undesirable result.");
        try {
            this.tapi.handleTimeout(this.threadObj);
        }
        catch (final SystemException ex) {
            System.err.println("Exception in handling timeout  " + ex);
        }
    }
}
