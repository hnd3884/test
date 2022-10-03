package com.adventnet.persistence;

import java.util.Map;
import com.adventnet.persistence.cache.CacheStatsUtil;
import com.adventnet.persistence.cache.LRUMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.adventnet.ds.query.UpdateQuery;
import javax.transaction.Transaction;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.logging.Level;
import com.adventnet.persistence.cache.SyncMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.logging.Logger;
import javax.transaction.Synchronization;

public class Counter implements Synchronization
{
    private static final Logger OUT;
    private ThreadLocal<Long> counterVal;
    private String context;
    private static final ReadWriteLock RWL;
    private static SyncMap instance_cache;
    private long currVal;
    boolean isMarkedForDelete;
    
    private Counter(final String context, final long initialValue) throws DataAccessException {
        this.counterVal = new ThreadLocal<Long>();
        this.context = "default";
        this.init(context, initialValue);
    }
    
    public static long nextValue(final String context) throws DataAccessException {
        return nextValue(context, 0L);
    }
    
    public static long nextValue(final String context, final long initialValue) throws DataAccessException {
        Counter c = null;
        Counter.RWL.readLock().lock();
        try {
            c = Counter.instance_cache.get(context);
        }
        finally {
            Counter.RWL.readLock().unlock();
        }
        if (c == null) {
            Counter.RWL.writeLock().lock();
            try {
                c = new Counter(context, initialValue);
                Counter.instance_cache.put(context, c);
            }
            finally {
                Counter.RWL.writeLock().unlock();
            }
        }
        return c.nextValue();
    }
    
    public static long currentValue(final String context) throws DataAccessException {
        return currentValue(context, 0L);
    }
    
    public static long currentValue(final String context, final long initialValue) throws DataAccessException {
        Counter c = null;
        Counter.RWL.readLock().lock();
        try {
            c = Counter.instance_cache.get(context);
        }
        finally {
            Counter.RWL.readLock().unlock();
        }
        if (c == null) {
            Counter.RWL.writeLock().lock();
            try {
                c = new Counter(context, initialValue);
                Counter.instance_cache.put(context, c);
            }
            finally {
                Counter.RWL.writeLock().unlock();
            }
        }
        return c.currVal;
    }
    
    public void beforeCompletion() {
        if (!this.isMarkedForDelete) {
            try {
                final DataObject dob = this.getDO(this.context);
                final Row row = dob.getRow("SeqGenState");
                final Long retVal = this.counterVal.get();
                if (retVal == null) {
                    return;
                }
                Counter.OUT.log(Level.FINER, "Before completion, value to be updated in counter table is {0}", retVal);
                row.set(2, retVal);
                dob.updateRow(row);
                DataAccess.update(dob);
                this.currVal = retVal;
            }
            catch (final Exception exc) {
                Counter.OUT.log(Level.SEVERE, exc.getMessage(), exc);
            }
        }
        else {
            Counter.OUT.log(Level.FINER, " Nothing to do in beforeCompletion for removing SequenceName from DB/HashMap ");
        }
    }
    
    public void afterCompletion(final int status) {
        if (!this.isMarkedForDelete) {
            this.counterVal.set(null);
        }
        else {
            Counter.RWL.writeLock().lock();
            try {
                Counter.instance_cache.remove(this.context);
            }
            finally {
                Counter.RWL.writeLock().unlock();
            }
        }
    }
    
    private DataObject getDO(final String context) throws DataAccessException {
        final Row contextRow = new Row("SeqGenState");
        contextRow.set(1, context);
        return DataAccess.get("SeqGenState", contextRow);
    }
    
    private void init(final String name, final long initialValue) throws DataAccessException {
        this.context = name;
        final DataObject contextDO = this.getDO(this.context);
        Counter.OUT.log(Level.FINER, "ContextDO fetched is {0}", contextDO);
        if (contextDO.isEmpty()) {
            final Row contextRow = new Row("SeqGenState");
            contextRow.set(1, this.context);
            contextRow.set(2, initialValue);
            contextDO.addRow(contextRow);
            DataAccess.add(contextDO);
            this.currVal = initialValue;
        }
        else {
            this.currVal = (long)contextDO.getRow("SeqGenState").get("CURRENTBATCHEND");
        }
    }
    
    private long nextValue() {
        if (!this.isMarkedForDelete) {
            boolean inTxn = false;
            try {
                Long retVal = this.counterVal.get();
                if (retVal == null) {
                    final Transaction txn = DataAccess.getTransactionManager().getTransaction();
                    if (txn != null && txn.getStatus() == 0) {
                        inTxn = true;
                        txn.registerSynchronization((Synchronization)this);
                    }
                    else {
                        this.beginTransaction();
                    }
                    final UpdateQuery uq = new UpdateQueryImpl("SeqGenState");
                    final Column seqGenCol = Column.getColumn("SeqGenState", "SEQNAME");
                    uq.setUpdateColumn("SEQNAME", this.context);
                    final Criteria ct = new Criteria(seqGenCol, this.context, 0);
                    uq.setCriteria(ct);
                    DataAccess.update(uq);
                    final DataObject dob = this.getDO(this.context);
                    if (dob.isEmpty()) {
                        Counter.OUT.log(Level.SEVERE, "Exception occured due to the given SequenceName is not present in DB");
                        throw new DataAccessException("Exception occured due to the given SequenceName is not present in DB");
                    }
                    final Row row = dob.getRow("SeqGenState");
                    Long genVal = (Long)row.get(2);
                    ++genVal;
                    if (!inTxn) {
                        Counter.OUT.log(Level.FINER, "Not in transcation so updating the counter value {0} directly ", genVal);
                        row.set(2, genVal);
                        dob.updateRow(row);
                        DataAccess.update(dob);
                        retVal = genVal;
                        this.counterVal.set(null);
                        this.commitTransaction();
                    }
                    else {
                        retVal = genVal;
                        this.counterVal.set(retVal);
                    }
                }
                else {
                    ++retVal;
                    this.counterVal.set(retVal);
                }
                this.currVal = retVal;
                return retVal;
            }
            catch (final Exception exc) {
                if (!inTxn) {
                    this.rollbackTransaction();
                }
                Counter.OUT.log(Level.SEVERE, "Exception occured during nextValue generation", exc);
                throw new RuntimeException(exc.getMessage());
            }
        }
        Counter.OUT.log(Level.SEVERE, "Exception occured due to context name was already removed by some thread.");
        throw new RuntimeException("Exception occured due to context name was already by some thread.");
    }
    
    private void beginTransaction() {
        try {
            DataAccess.getTransactionManager().begin();
        }
        catch (final Exception exc) {
            Counter.OUT.log(Level.SEVERE, "Exception during transaction begin", exc);
        }
    }
    
    private void commitTransaction() {
        try {
            DataAccess.getTransactionManager().commit();
        }
        catch (final Exception exc) {
            Counter.OUT.log(Level.SEVERE, "Exception during transaction begin", exc);
        }
    }
    
    private void rollbackTransaction() {
        try {
            DataAccess.getTransactionManager().rollback();
        }
        catch (final Exception exc) {
            Counter.OUT.log(Level.SEVERE, "Exception during transaction begin", exc);
        }
    }
    
    public static long getCurrentValue(final String context, final long initialValue) throws DataAccessException {
        Counter c = null;
        Counter.RWL.readLock().lock();
        try {
            c = Counter.instance_cache.get(context);
        }
        finally {
            Counter.RWL.readLock().unlock();
        }
        if (c == null) {
            Counter.RWL.writeLock().lock();
            try {
                c = new Counter(context, initialValue);
                Counter.instance_cache.put(context, c);
            }
            finally {
                Counter.RWL.writeLock().unlock();
            }
        }
        return c.currVal;
    }
    
    public static void remove(final String context) throws DataAccessException {
        Counter c = null;
        Counter.RWL.writeLock().lock();
        try {
            c = Counter.instance_cache.get(context);
            if (c == null) {
                Counter.OUT.log(Level.SEVERE, "No such SequenceName present in the HashMap/DB [{0}]", context);
                throw new DataAccessException("No such SequenceName present in the HashMap/DB " + context);
            }
            c.removeCounter(context);
        }
        finally {
            Counter.RWL.writeLock().unlock();
        }
    }
    
    private void removeCounter(final String context) throws DataAccessException {
        boolean inTxn = false;
        try {
            final Transaction txn = DataAccess.getTransactionManager().getTransaction();
            if (txn != null && txn.getStatus() == 0) {
                inTxn = true;
                txn.registerSynchronization((Synchronization)this);
            }
            else {
                this.beginTransaction();
            }
            final Row contextRow = new Row("SeqGenState");
            contextRow.set("SEQNAME", context);
            DataAccess.delete(contextRow);
            Counter.OUT.log(Level.FINER, "removeSequence completed for sequence name [{0}]", context);
            this.isMarkedForDelete = true;
            if (!inTxn) {
                this.commitTransaction();
                Counter.instance_cache.remove(context);
            }
        }
        catch (final Exception exc) {
            if (!inTxn) {
                this.rollbackTransaction();
            }
            Counter.OUT.log(Level.SEVERE, "Exception occured during removing sequencename from DB", exc);
            throw new RuntimeException(exc.getMessage());
        }
    }
    
    public static void renameSequenceName(final String oldName, final String newName) throws DataAccessException {
        Counter.OUT.log(Level.INFO, "Going to update SeqName '{0}' to '{1}' in SeqGenState table.", new String[] { oldName, newName });
        final UpdateQuery uq = new UpdateQueryImpl("SeqGenState");
        uq.setUpdateColumn("SEQNAME", newName);
        final Criteria criteria = new Criteria(Column.getColumn("SeqGenState", "SEQNAME"), oldName, 0);
        uq.setCriteria(criteria);
        DataAccess.update(uq);
        final Object lastValue = Counter.instance_cache.remove(oldName);
        Counter.instance_cache.put(newName, lastValue);
    }
    
    static {
        OUT = Logger.getLogger(Counter.class.getName());
        RWL = new ReentrantReadWriteLock();
        Counter.instance_cache = new SyncMap((Map<K, V>)new LRUMap(2500, null), Counter.RWL);
    }
}
