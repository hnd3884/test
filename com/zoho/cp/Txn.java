package com.zoho.cp;

import java.sql.Connection;
import java.util.concurrent.Executor;
import com.zoho.mickey.cp.ConnectionInfoFactory;
import javax.transaction.xa.XAResource;
import java.util.Iterator;
import java.util.Collection;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import java.util.logging.Level;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.concurrent.locks.Lock;
import java.util.Set;
import java.util.Map;
import javax.transaction.Synchronization;
import java.util.List;
import javax.transaction.Transaction;

class Txn implements Transaction
{
    private int status;
    private List<Synchronization> listeners;
    private Map<TxDataSource, ConnectionDetail> ds_vs_detail;
    private Set<ConnectionDetail> enlistedDetail;
    private long transactionExpireTime;
    private Lock lock;
    Bucket bucket;
    int posInBucket;
    private static final Logger LOGGER;
    
    Txn(final int transactionTimeout) {
        this.status = 0;
        this.listeners = new LinkedList<Synchronization>();
        this.ds_vs_detail = new HashMap<TxDataSource, ConnectionDetail>();
        this.enlistedDetail = new HashSet<ConnectionDetail>();
        this.lock = new ReentrantLock();
        this.transactionExpireTime = System.currentTimeMillis() + transactionTimeout * 1000L;
        Txn.LOGGER.log(Level.FINE, "DEBUG :: Transaction :: {0} :: Constructor", this);
    }
    
    public int getStatus() throws SystemException {
        return this.status;
    }
    
    public void registerSynchronization(final Synchronization syn) throws RollbackException, IllegalStateException, SystemException {
        this.lock.lock();
        try {
            if (this.status != 0) {
                throw new IllegalStateException("Cannot register synchronization when the Txn status is " + getStatus(this.status));
            }
            this.listeners.add(syn);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, SystemException {
        if (this.status == 6) {
            throw new SystemException("No Transaction to commit");
        }
        try {
            this.commitTransaction();
        }
        finally {
            ((TxMgr)TxMgr.getInstance()).clearTxDetailInThread();
        }
    }
    
    public void rollback() throws IllegalStateException, SystemException {
        if (this.status == 6) {
            throw new SystemException("No Transaction to rollback");
        }
        try {
            this.rollbackTransaction();
        }
        finally {
            ((TxMgr)TxMgr.getInstance()).clearTxDetailInThread();
        }
    }
    
    protected void commitTransaction() throws RollbackException {
        this.lock.lock();
        try {
            this.status = ((this.status == 0) ? this.doBeforeCompletion() : 1);
            if (this.status == 1) {
                this.rollbackTransaction();
                throw new RollbackException("Transaction Already marked as Rollback.Txn rollbacked");
            }
            this.commitConnections();
            this.status = ((this.status == 8) ? 3 : 4);
            this.returnEnlistedConnToPool();
            TxnTimeOutImpl.unregister(this);
            this.doAfterCompletion();
            this.log(Level.FINE, "Txn commit ends with a status {0}", getStatus(this.status));
            this.status = 6;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private void commitConnections() {
        final Collection<ConnectionDetail> details = this.ds_vs_detail.values();
        try {
            for (final ConnectionDetail detail : details) {
                detail.physicalConnection.commit();
            }
        }
        catch (final Exception ex) {
            this.log(Level.SEVERE, "Exception during connection commit.Partial transaction commit and rollback may occurs", ex);
            Txn.LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            this.status = 1;
            this.rollbackConnections();
        }
    }
    
    protected void rollbackTransaction() {
        this.lock.lock();
        try {
            this.status = 4;
            this.rollbackConnections();
            this.returnEnlistedConnToPool();
            TxnTimeOutImpl.unregister(this);
            this.doAfterCompletion();
            this.log(Level.SEVERE, "Txn rollback occurs", new Object[0]);
            this.status = 6;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private void rollbackConnections() {
        final Collection<ConnectionDetail> details = this.ds_vs_detail.values();
        for (final ConnectionDetail detail : details) {
            try {
                detail.physicalConnection.rollback();
            }
            catch (final Exception exc) {
                this.log(Level.SEVERE, "exception during connection rollback", exc);
                Txn.LOGGER.log(Level.SEVERE, exc.getMessage(), exc);
                detail.setIsValid(false);
            }
        }
    }
    
    private int doBeforeCompletion() {
        this.status = 8;
        this.lock.unlock();
        try {
            for (final Synchronization syn : this.listeners) {
                syn.beforeCompletion();
            }
        }
        catch (final Exception exc) {
            this.log(Level.SEVERE, "Exception during beforeCompletion.This will lead to txn rollback.", exc);
            this.status = 1;
        }
        finally {
            this.lock.lock();
        }
        return this.status;
    }
    
    private void doAfterCompletion() {
        for (final Synchronization syn : this.listeners) {
            try {
                syn.afterCompletion(this.status);
            }
            catch (final Exception ex) {
                this.log(Level.SEVERE, "Exception during txn after completion", ex);
            }
        }
    }
    
    void enlistConnectionDetail(final TxDataSource dataSource, final ConnectionDetail detail) {
        this.ds_vs_detail.put(dataSource, detail);
        this.enlistedDetail.add(detail);
    }
    
    ConnectionDetail getEnlistedConnectionDetail(final TxDataSource dataSource) {
        return this.ds_vs_detail.get(dataSource);
    }
    
    boolean isEnlisted(final ConnectionDetail detail) {
        return this.enlistedDetail.contains(detail);
    }
    
    private void returnEnlistedConnToPool() {
        for (final ConnectionDetail detail : this.ds_vs_detail.values()) {
            detail.closeAndRemoveChildren();
            detail.returnToPool();
        }
        this.ds_vs_detail.clear();
    }
    
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        this.lock.lock();
        try {
            if (this.status == 6) {
                throw new IllegalStateException("No Transaction to mark as rollback");
            }
            this.status = 1;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean timedOut() {
        if (!this.lock.tryLock()) {
            return false;
        }
        try {
            this.abortAndFlushConnectionsEnlistedInTxn();
            this.status = 1;
            Txn.LOGGER.log(Level.FINE, "DEBUG :: Transaction :: {0} :: timedOut", this);
            this.log(Level.SEVERE, "Transaction timed out. Status changed to STATUS_MARKED_ROLLBACK", new Object[0]);
            return true;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    long getExpiryTime() {
        return this.transactionExpireTime;
    }
    
    void setExpriyTime(final long newExpiryTime) {
        this.transactionExpireTime = newExpiryTime;
    }
    
    private void log(final Level level, final String message, final Object... obj) {
        Txn.LOGGER.log(level, message, obj);
    }
    
    public boolean delistResource(final XAResource arg0, final int arg1) throws IllegalStateException, SystemException {
        throw new SystemException("delistResource not supported");
    }
    
    public boolean enlistResource(final XAResource arg0) throws RollbackException, IllegalStateException, SystemException {
        throw new SystemException("enlistResource not supported");
    }
    
    private void abortAndFlushConnectionsEnlistedInTxn() {
        final Collection<ConnectionDetail> details = this.ds_vs_detail.values();
        for (final ConnectionDetail detail : details) {
            try {
                final Connection conn = detail.physicalConnection;
                Txn.LOGGER.log(Level.FINE, "DEBUG :: Transaction :: {0} :: abortAndFlushConnectionsEnlistedInTxn", this);
                if (ConnectionInfoFactory.getConnectionInfo(conn) != null) {
                    this.log(Level.SEVERE, "Transaction timed out for Connection created in thread {0}.", ConnectionInfoFactory.getConnectionInfo(conn).getThreadId());
                }
                else {
                    this.log(Level.SEVERE, "Transaction timed out for Connection {0}", conn);
                }
                conn.abort(ConnectionPool.executors);
                detail.setIsValid(false);
            }
            catch (final Throwable ex) {
                this.log(Level.SEVERE, "Exception while aborting connections enlisted in txn {0}", ex);
                detail.setIsValid(false);
                detail.abort(ConnectionPool.executors);
            }
        }
        this.returnEnlistedConnToPool();
    }
    
    @Override
    public String toString() {
        return "{@" + Integer.toHexString(this.hashCode()) + ", Txn_Status:" + getStatus(this.status) + ", Expire_Time :" + this.transactionExpireTime + "}";
    }
    
    public static String getStatus(final int status) {
        String str = null;
        switch (status) {
            case 0: {
                str = "STATUS_ACTIVE";
                break;
            }
            case 1: {
                str = "STATUS_MARKED_ROLLBACK";
                break;
            }
            case 2: {
                str = "STATUS_PREPARED";
                break;
            }
            case 3: {
                str = "STATUS_COMMITTED";
                break;
            }
            case 4: {
                str = "STATUS_ROLLEDBACK";
                break;
            }
            case 5: {
                str = "STATUS_UNKNOWN";
                break;
            }
            case 6: {
                str = "STATUS_NO_TRANSACTION";
                break;
            }
            case 7: {
                str = "STATUS_PREPARING";
                break;
            }
            case 8: {
                str = "STATUS_COMMITTING";
                break;
            }
            case 9: {
                str = "STATUS_ROLLING_BACK";
                break;
            }
        }
        return str;
    }
    
    static {
        LOGGER = Logger.getLogger(Txn.class.getName());
    }
}
