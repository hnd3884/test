package com.zoho.cp;

import javax.transaction.Synchronization;
import javax.transaction.xa.XAResource;
import javax.transaction.SystemException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.RollbackException;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.cp.MultiDSUtil;
import com.adventnet.cp.ClientFilter;
import javax.transaction.Transaction;

public class WrappedTxn implements Transaction
{
    private int i;
    private MultiTxMgrTxn[] transactions;
    
    public WrappedTxn() {
        this.transactions = new MultiTxMgrTxn[3];
        this.i = 0;
        for (int i = 0; i < 3; ++i) {
            this.transactions[i] = null;
        }
    }
    
    public void addTxn(final MultiTxMgrTxn txn) {
        this.transactions[this.i++] = txn;
    }
    
    public MultiTxMgrTxn getTxn(final int i) {
        return this.transactions[i];
    }
    
    private MultiTxMgrTxn getDefaultTxn() {
        final Integer readDBIndex = ClientFilter.getThreadLocalDB();
        if (readDBIndex != null && MultiDSUtil.isMultiDataSourceEnabled()) {
            return this.transactions[readDBIndex];
        }
        return this.transactions[PersistenceInitializer.getMWSRConfigCount() - 1];
    }
    
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                this.transactions[i].commit();
            }
        }
        else {
            this.getDefaultTxn().commit();
        }
    }
    
    public boolean delistResource(final XAResource arg0, final int arg1) throws IllegalStateException, SystemException {
        return this.getDefaultTxn().delistResource(arg0, arg1);
    }
    
    public boolean enlistResource(final XAResource arg0) throws RollbackException, IllegalStateException, SystemException {
        return this.getDefaultTxn().enlistResource(arg0);
    }
    
    public int getStatus() throws SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                if (this.transactions[i].getStatus() == 1) {
                    return 1;
                }
            }
        }
        return this.getDefaultTxn().getStatus();
    }
    
    public void registerSynchronization(final Synchronization arg0) throws RollbackException, IllegalStateException, SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                this.transactions[i].registerSynchronization(arg0);
            }
        }
        else {
            this.getDefaultTxn().registerSynchronization(arg0);
        }
    }
    
    public void rollback() throws IllegalStateException, SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                this.transactions[i].rollback();
            }
        }
        else {
            this.getDefaultTxn().rollback();
        }
    }
    
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                this.transactions[i].setRollbackOnly();
            }
        }
        else {
            this.getDefaultTxn().setRollbackOnly();
        }
    }
}
