package com.zoho.cp;

import javax.transaction.InvalidTransactionException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.RollbackException;
import javax.transaction.Transaction;
import javax.transaction.NotSupportedException;
import java.util.logging.Level;
import javax.transaction.SystemException;
import java.util.logging.Logger;
import javax.transaction.TransactionManager;

public class TxMgr implements TransactionManager
{
    private ThreadLocal<TxDetail> tl;
    private int transactionTimeout;
    private static TxMgr txnManager;
    private static final Logger LOGGER;
    
    public TxMgr() {
        this.tl = new ThreadLocal<TxDetail>();
        this.transactionTimeout = 300000;
    }
    
    public static TransactionManager getInstance() {
        return (TransactionManager)TxMgr.txnManager;
    }
    
    public void begin() throws NotSupportedException, SystemException {
        if (this.getStatus() != 6) {
            throw new SystemException("Already associated with a transaction. Cannot begin new transaction");
        }
        final TxDetail details = this.getThreadLocalDetails();
        TxnTimeOutImpl.register(details.txn = new Txn(details.transactionTimeout));
        TxMgr.LOGGER.log(Level.FINE, "DEBUG :: Transaction :: {0} :: begin", details.txn);
    }
    
    public void commit() throws RollbackException, IllegalStateException, SystemException, HeuristicMixedException, HeuristicRollbackException {
        final Transaction txn = this.getTransaction();
        if (txn == null) {
            throw new SystemException("No Transaction to commit");
        }
        TxMgr.LOGGER.log(Level.FINE, "DEBUG :: Transaction :: {0} :: commit", txn);
        txn.commit();
    }
    
    public int getStatus() throws SystemException {
        final Transaction txn = this.getTransaction();
        return (txn == null) ? 6 : txn.getStatus();
    }
    
    public Transaction getTransaction() throws SystemException {
        return (Transaction)this.getThreadLocalDetails().txn;
    }
    
    public void resume(final Transaction transaction) throws InvalidTransactionException, IllegalStateException, SystemException {
        final Txn txn = (Txn)transaction;
        if (this.getStatus() != 6) {
            throw new IllegalStateException("Already a Txn is active.Cannot resume other Txn");
        }
        this.getThreadLocalDetails().txn = txn;
        TxMgr.LOGGER.log(Level.FINE, "DEBUG :: Transaction :: {0} :: resume", txn);
    }
    
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        final Txn txn = (Txn)this.getTransaction();
        if (txn == null) {
            throw new SystemException("No Transaction to rollback");
        }
        TxMgr.LOGGER.log(Level.FINE, "DEBUG :: Transaction :: {0} :: rollback", txn);
        txn.rollback();
    }
    
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        final Txn txn = (Txn)this.getTransaction();
        if (txn == null) {
            throw new SystemException("No Transaction to mark rollback");
        }
        txn.setRollbackOnly();
    }
    
    public void setTransactionTimeout(final int transactionTimeout) throws SystemException {
        this.getThreadLocalDetails().transactionTimeout = transactionTimeout;
    }
    
    public void setDefaultTxnTimeout(final int transactionTimeout) {
        this.transactionTimeout = transactionTimeout;
    }
    
    public Transaction suspend() throws SystemException {
        final TxDetail detail = this.getThreadLocalDetails();
        this.clearTxDetailInThread();
        TxMgr.LOGGER.log(Level.FINE, "DEBUG :: Transaction :: {0} :: suspend", detail.txn);
        return (Transaction)detail.txn;
    }
    
    protected TxDetail getThreadLocalDetails() {
        TxDetail details = this.tl.get();
        if (details == null) {
            details = new TxDetail();
            details.transactionTimeout = this.transactionTimeout;
            this.tl.set(details);
        }
        return details;
    }
    
    void clearTxDetailInThread() {
        this.tl.remove();
    }
    
    static {
        TxMgr.txnManager = new TxMgr();
        LOGGER = Logger.getLogger(Txn.class.getName());
    }
    
    static class TxDetail
    {
        int transactionTimeout;
        Txn txn;
    }
}
