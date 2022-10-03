package com.adventnet.db.persistence;

import com.adventnet.persistence.DataAccessException;
import javax.transaction.Transaction;
import com.adventnet.persistence.DataAccess;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.persistence.PersistenceException;
import java.util.logging.Logger;

public class IntegerSequenceGenerator implements SequenceGenerator
{
    private static final Logger OUT;
    protected int batchEnd;
    protected int currVal;
    protected int batchSize;
    protected int startValue;
    protected String name;
    protected static SequenceGeneratorBean seqApi;
    
    public IntegerSequenceGenerator() {
        this.batchSize = 300;
        this.startValue = 0;
    }
    
    protected static void initSeqApi() throws PersistenceException {
        IntegerSequenceGenerator.seqApi = new SequenceGeneratorBean();
    }
    
    @Override
    public void init(final String name) throws PersistenceException {
        this.name = name;
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("SeqName cannot be null or empty");
        }
        final String seqGenBatchSize = PersistenceInitializer.getConfigurationValue("SeqGenBatchSize");
        final String seqGenStartValue = PersistenceInitializer.getConfigurationValue("SeqGenStartValue");
        if (seqGenBatchSize != null) {
            this.batchSize = Integer.parseInt(seqGenBatchSize);
        }
        if (seqGenStartValue != null) {
            this.startValue = Integer.parseInt(seqGenStartValue);
        }
        final int sv = (int)this.getStartValue();
        final int mv = (int)this.getMaxValue();
        if (sv > mv) {
            throw new IllegalArgumentException("startValue :: [" + sv + "] cannot be more than maxValue :: [" + mv + "] in the sequence generator :: [" + name + "]");
        }
        if (IntegerSequenceGenerator.seqApi == null) {
            initSeqApi();
        }
        this.getNextBatch();
        IntegerSequenceGenerator.OUT.log(Level.FINEST, "{0} : after init", this);
    }
    
    protected void getNextBatch() throws PersistenceException {
        IntegerSequenceGenerator.OUT.log(Level.FINEST, "{0} Inside getNextBatch...", this);
        Long[] newState = null;
        Transaction oldTransaction = null;
        try {
            oldTransaction = suspendTransaction();
            try {
                DataAccess.getTransactionManager().begin();
                newState = IntegerSequenceGenerator.seqApi.getNextBatch(this);
                DataAccess.getTransactionManager().commit();
            }
            catch (final Exception ex) {
                try {
                    if (DataAccess.getTransactionManager().getTransaction() != null) {
                        DataAccess.getTransactionManager().rollback();
                    }
                }
                catch (final Exception e) {
                    IntegerSequenceGenerator.OUT.log(Level.INFO, "Error while rollback : ", e);
                }
                throw new PersistenceException(ex.getMessage(), ex);
            }
        }
        finally {
            resumeTransaction(oldTransaction);
        }
        this.batchEnd = newState[1].intValue();
        this.currVal = newState[0].intValue() - 1;
        IntegerSequenceGenerator.OUT.log(Level.FINEST, "{0} Returning from getNextBatch...", this);
    }
    
    @Override
    public synchronized Object nextValue() throws RuntimeException {
        if (this.batchEnd <= this.currVal) {
            try {
                this.getNextBatch();
            }
            catch (final PersistenceException pe) {
                throw new RuntimeException(pe.getMessage(), pe);
            }
        }
        ++this.currVal;
        IntegerSequenceGenerator.OUT.log(Level.FINEST, "{0} :  RETURNING {1}", new Object[] { this, this.currVal });
        return this.currVal;
    }
    
    @Override
    public synchronized Object setValue(final Object val) throws DataAccessException {
        IntegerSequenceGenerator.OUT.log(Level.FINE, " Current Val {0} Set Val {1}", new Object[] { new Long(this.currVal), val });
        final Integer intVal = (Integer)val;
        final int pintVal = intVal;
        if (this.batchEnd < pintVal) {
            Transaction oldTransaction = null;
            try {
                oldTransaction = suspendTransaction();
                try {
                    DataAccess.getTransactionManager().begin();
                    long multiplier = pintVal / this.getBatchSize();
                    ++multiplier;
                    this.batchEnd = new Long(this.getBatchSize() * multiplier).intValue();
                    IntegerSequenceGenerator.OUT.log(Level.FINER, "{0} BatchEnd calculated as {1}", new Object[] { this, this.batchEnd });
                    IntegerSequenceGenerator.seqApi.setBatchEnd(this.name, (long)this.batchEnd);
                    DataAccess.getTransactionManager().commit();
                }
                catch (final Exception ex) {
                    try {
                        if (DataAccess.getTransactionManager().getTransaction() != null) {
                            DataAccess.getTransactionManager().rollback();
                        }
                    }
                    catch (final Exception e) {
                        IntegerSequenceGenerator.OUT.log(Level.INFO, "Error while rollback : ", e);
                    }
                    throw new DataAccessException(ex.getMessage(), ex);
                }
            }
            catch (final PersistenceException pe) {
                throw new DataAccessException(pe.getMessage(), pe);
            }
            finally {
                try {
                    resumeTransaction(oldTransaction);
                }
                catch (final PersistenceException e2) {
                    throw new DataAccessException(e2.getMessage(), e2);
                }
            }
        }
        this.currVal = ((this.currVal < pintVal) ? (pintVal + 1) : this.currVal);
        IntegerSequenceGenerator.OUT.log(Level.FINEST, " Assigned Current Val is {0}", this.currVal);
        return this.currVal;
    }
    
    @Override
    public void cleanup() throws PersistenceException {
        IntegerSequenceGenerator.OUT.log(Level.FINEST, "{0} : cleanup called.", this);
    }
    
    @Override
    public void remove() throws PersistenceException {
        IntegerSequenceGenerator.OUT.log(Level.FINEST, "{0} : remove called.", this);
        Transaction oldTransaction = null;
        try {
            oldTransaction = suspendTransaction();
            try {
                DataAccess.getTransactionManager().begin();
                IntegerSequenceGenerator.seqApi.removeSequence(this.name, "INTEGER");
                DataAccess.getTransactionManager().commit();
            }
            catch (final Exception ex) {
                try {
                    if (DataAccess.getTransactionManager().getTransaction() != null) {
                        DataAccess.getTransactionManager().rollback();
                    }
                }
                catch (final Exception exc) {
                    IntegerSequenceGenerator.OUT.log(Level.INFO, "Error while rollback : ", exc);
                }
                throw new PersistenceException(ex.getMessage(), ex);
            }
            IntegerSequenceGenerator.OUT.log(Level.FINEST, "{0} : Successfully removed sequence", this);
        }
        catch (final Exception e) {
            throw new PersistenceException("Exception when removing sequence", e);
        }
        finally {
            resumeTransaction(oldTransaction);
        }
    }
    
    @Override
    public String toString() {
        return "<" + super.toString() + " NAME=\"" + this.name + "\" CURRENT_VALUE=\"" + this.currVal + "\" BATCH_END=\"" + this.batchEnd + "\" />";
    }
    
    private static Transaction suspendTransaction() throws PersistenceException {
        try {
            return DataAccess.getTransactionManager().suspend();
        }
        catch (final Exception exc) {
            IntegerSequenceGenerator.OUT.log(Level.WARNING, exc.getMessage(), exc);
            throw new PersistenceException(exc.getMessage());
        }
    }
    
    private static void resumeTransaction(final Transaction oldTransaction) throws PersistenceException {
        if (oldTransaction != null) {
            try {
                DataAccess.getTransactionManager().resume(oldTransaction);
            }
            catch (final Exception exc) {
                IntegerSequenceGenerator.OUT.log(Level.WARNING, exc.getMessage(), exc);
                throw new PersistenceException(exc.getMessage());
            }
        }
    }
    
    @Override
    public Object getCurrentValue() {
        return this.currVal;
    }
    
    @Override
    public int getBatchSize() {
        return this.batchSize;
    }
    
    @Override
    public Object getStartValue() {
        return this.startValue;
    }
    
    @Override
    public Object getMaxValue() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void renameTo(final String newName) throws PersistenceException {
        try {
            IntegerSequenceGenerator.seqApi.renameSequenceName(this.name, newName);
        }
        catch (final DataAccessException dae) {
            throw new PersistenceException(dae.getMessage(), dae);
        }
        this.name = newName;
    }
    
    static {
        OUT = Logger.getLogger(IntegerSequenceGenerator.class.getName());
    }
}
