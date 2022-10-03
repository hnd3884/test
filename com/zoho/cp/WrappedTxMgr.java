package com.zoho.cp;

import javax.transaction.InvalidTransactionException;
import javax.transaction.Transaction;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.NotSupportedException;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.cp.MultiDSUtil;
import com.adventnet.cp.ClientFilter;
import javax.transaction.TransactionManager;

public class WrappedTxMgr implements TransactionManager
{
    private int i;
    private MultiTxMgr[] transactionManagers;
    
    public WrappedTxMgr() {
        this.transactionManagers = new MultiTxMgr[3];
        this.i = 0;
        for (int i = 0; i < 3; ++i) {
            this.transactionManagers[i] = null;
        }
    }
    
    public void addTransactionManager(final TransactionManager txMgr) {
        this.transactionManagers[this.i++] = (MultiTxMgr)txMgr;
    }
    
    public MultiTxMgr getTransactionManager(final int i) {
        return this.transactionManagers[i];
    }
    
    private MultiTxMgr getDefaultTxMgr() {
        final Integer readDBIndex = ClientFilter.getThreadLocalDB();
        if (readDBIndex != null && MultiDSUtil.isMultiDataSourceEnabled()) {
            return this.transactionManagers[readDBIndex];
        }
        return this.transactionManagers[PersistenceInitializer.getMWSRConfigCount() - 1];
    }
    
    public void begin() throws NotSupportedException, SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                this.transactionManagers[i].begin();
            }
        }
        else {
            this.getDefaultTxMgr().begin();
        }
    }
    
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                this.transactionManagers[i].commit();
            }
        }
        else {
            this.getDefaultTxMgr().commit();
        }
    }
    
    public int getStatus() throws SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                if (this.transactionManagers[i].getStatus() == 1) {
                    return 1;
                }
            }
        }
        return this.getDefaultTxMgr().getStatus();
    }
    
    public Transaction getTransaction() throws SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            final WrappedTxn wTxn = new WrappedTxn();
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                final Transaction txn = this.transactionManagers[i].getTransaction();
                if (txn == null) {
                    return null;
                }
                wTxn.addTxn((MultiTxMgrTxn)txn);
            }
            return (Transaction)wTxn;
        }
        return this.getDefaultTxMgr().getTransaction();
    }
    
    public void resume(final Transaction arg0) throws InvalidTransactionException, IllegalStateException, SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                this.transactionManagers[i].resume((Transaction)((WrappedTxn)arg0).getTxn(i));
            }
        }
        else {
            this.getDefaultTxMgr().resume(arg0);
        }
    }
    
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                this.transactionManagers[i].rollback();
            }
        }
        else {
            this.getDefaultTxMgr().rollback();
        }
    }
    
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                this.transactionManagers[i].setRollbackOnly();
            }
        }
        else {
            this.getDefaultTxMgr().setRollbackOnly();
        }
    }
    
    public void setTransactionTimeout(final int arg0) throws SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                this.transactionManagers[i].setTransactionTimeout(arg0);
            }
        }
        else {
            this.getDefaultTxMgr().setTransactionTimeout(arg0);
        }
    }
    
    public Transaction suspend() throws SystemException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            final WrappedTxn wTxn = new WrappedTxn();
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                wTxn.addTxn((MultiTxMgrTxn)this.transactionManagers[i].suspend());
            }
            return (Transaction)wTxn;
        }
        return this.getDefaultTxMgr().suspend();
    }
}
