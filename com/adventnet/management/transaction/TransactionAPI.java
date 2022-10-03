package com.adventnet.management.transaction;

import java.rmi.RemoteException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.HeuristicMixedException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.NotSupportedException;
import java.util.Hashtable;
import javax.transaction.UserTransaction;

public class TransactionAPI implements UserTransaction
{
    private ConnectionPool cspool;
    private int timeOutValue;
    private Hashtable timeoutHandlerRunnables;
    
    public TransactionAPI() {
        this.timeOutValue = 20000;
        this.timeoutHandlerRunnables = new Hashtable();
    }
    
    public void begin() throws NotSupportedException, SystemException {
        if (!this.cspool.isTransactionEnabled()) {
            throw new NotSupportedException("Transaction not supported.");
        }
        if (this.cspool.isNestedTransaction()) {
            this.cspool.updateNestingLevel(true);
            return;
        }
        if (this.cspool.isInTimedOutThreads()) {
            throw new SystemException("Transaction timed out for the thread " + Thread.currentThread());
        }
        if (this.cspool.lockConnectionForTransaction(new Double(this.timeOutValue * 1.25).intValue())) {
            final Thread currentThread = Thread.currentThread();
            this.timeoutHandlerRunnables.put(currentThread, new TransactionTimeoutHandler(this, currentThread, this.cspool.getScheduler(), this.timeOutValue));
            return;
        }
        throw new SystemException("Free connection not available.");
    }
    
    public void begin(final int n) throws NotSupportedException, SystemException {
        if (!this.cspool.isTransactionEnabled()) {
            throw new NotSupportedException("Transaction not supported.");
        }
        if (this.cspool.isNestedTransaction()) {
            this.cspool.updateNestingLevel(true);
            return;
        }
        if (this.cspool.isInTimedOutThreads()) {
            throw new SystemException("Transaction timed out for the thread " + Thread.currentThread());
        }
        final boolean lockConnectionForTransaction = this.cspool.lockConnectionForTransaction(new Double(n * 1.25).intValue());
        if (n != -1 && lockConnectionForTransaction) {
            final Thread currentThread = Thread.currentThread();
            this.timeoutHandlerRunnables.put(currentThread, new TransactionTimeoutHandler(this, currentThread, this.cspool.getScheduler(), n));
        }
        else if (!lockConnectionForTransaction) {
            throw new SystemException("Free connection not available.");
        }
    }
    
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
        if (!this.cspool.isTransactionEnabled()) {
            return;
        }
        if (this.cspool.getNestingLevel() > 0) {
            this.cspool.updateNestingLevel(false);
            return;
        }
        final Connection connectionForTransaction = this.cspool.getConnectionForTransaction();
        if (connectionForTransaction != null) {
            this.removeTimeOutRunnable(Thread.currentThread());
            try {
                connectionForTransaction.commit();
            }
            catch (final SQLException ex) {
                this.rollback();
                throw new RollbackException("Exception while commiting the changes to database and so transaction is rolled back.");
            }
            this.end(Thread.currentThread());
            return;
        }
        throw new SystemException("Cannot commit , transaction rolled back.");
    }
    
    void handleTimeout(final Thread thread) throws SystemException {
        this.cspool.addTimedOutThread(thread);
        try {
            this.rollback(thread);
        }
        catch (final SystemException ex) {
            throw ex;
        }
    }
    
    private void removeTimeOutRunnable(final Thread thread) {
        final Runnable runnable = this.timeoutHandlerRunnables.remove(thread);
        if (runnable != null) {
            this.cspool.getScheduler().removeTask(runnable);
        }
    }
    
    public void rollback() throws SystemException {
        final Thread currentThread = Thread.currentThread();
        try {
            this.rollback(currentThread);
        }
        catch (final SystemException ex) {
            throw ex;
        }
    }
    
    public void rollback(final String s) throws RemoteException {
        if (!this.cspool.isTransactionEnabled()) {
            return;
        }
        final Thread currentThread = Thread.currentThread();
        final RollbackException ex = new RollbackException(s);
        try {
            this.rollback(currentThread);
        }
        catch (final SystemException ex2) {
            throw new RemoteException("Transaction rollback failure", (Throwable)ex);
        }
        throw new RemoteException("Transaction Rolled back", (Throwable)ex);
    }
    
    void rollback(final Thread thread) throws SystemException {
        if (!this.cspool.isTransactionEnabled()) {
            return;
        }
        this.cspool.resetNestingLevel(thread);
        final Connection connectionForTransaction = this.cspool.getConnectionForTransaction(thread);
        if (connectionForTransaction != null) {
            this.removeTimeOutRunnable(thread);
            try {
                connectionForTransaction.rollback();
            }
            catch (final SQLException ex) {
                throw new SystemException("Exception while rolling back the changes.");
            }
            this.end(thread);
        }
    }
    
    private void end(final Thread thread) {
        this.cspool.freeConnectionForTransaction(thread);
    }
    
    public void setTransactionTimeout(final int timeOutValue) {
        this.timeOutValue = timeOutValue;
    }
    
    public int getTransactionTimeOut() {
        return this.timeOutValue;
    }
    
    public int getStatus() {
        return -1;
    }
    
    public void setRollbackOnly() {
    }
    
    public void setConnectionPool(final ConnectionPool cspool) {
        this.cspool = cspool;
        this.setTransactionTimeout(this.cspool.getTransactionTimeOut());
    }
    
    public ConnectionPool getConnectionPool() {
        return this.cspool;
    }
}
