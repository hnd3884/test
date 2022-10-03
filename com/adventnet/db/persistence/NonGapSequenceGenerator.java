package com.adventnet.db.persistence;

import com.adventnet.persistence.Counter;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.PersistenceException;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.logging.Logger;

public class NonGapSequenceGenerator implements SequenceGenerator
{
    private static Logger out;
    protected String name;
    protected long startValue;
    
    public NonGapSequenceGenerator() {
        this.startValue = 0L;
    }
    
    @Override
    public void init(final String name) throws PersistenceException {
        this.name = name;
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("SeqName cannot be null or empty");
        }
        final String seqGenStartValue = PersistenceInitializer.getConfigurationValue("SeqGenStartValue");
        if (seqGenStartValue != null) {
            this.startValue = Long.parseLong(seqGenStartValue);
        }
        final long sv = (long)this.getStartValue();
        final long mv = (long)this.getMaxValue();
        if (sv > mv) {
            throw new IllegalArgumentException("startValue :: [" + sv + "] cannot be more than maxValue :: [" + mv + "] in the sequence generator :: [" + name + "]");
        }
        NonGapSequenceGenerator.out.log(Level.FINEST, "{0} : after init", this);
    }
    
    @Override
    public Object setValue(final Object val) throws DataAccessException {
        NonGapSequenceGenerator.out.log(Level.WARNING, "Ignoring setValue for {0}", this.name);
        return val;
    }
    
    @Override
    public Object nextValue() throws RuntimeException {
        try {
            return Counter.nextValue(this.name, this.startValue);
        }
        catch (final DataAccessException dae) {
            NonGapSequenceGenerator.out.log(Level.SEVERE, "Exception in nextValue for context {0}", this.name);
            NonGapSequenceGenerator.out.log(Level.SEVERE, "", dae);
            throw new RuntimeException(dae);
        }
    }
    
    @Override
    public void cleanup() throws PersistenceException {
        NonGapSequenceGenerator.out.log(Level.FINEST, "{0} : cleanup called.", this);
    }
    
    @Override
    public void remove() throws PersistenceException {
        try {
            Counter.remove(this.name);
        }
        catch (final DataAccessException dae) {
            NonGapSequenceGenerator.out.log(Level.SEVERE, "Exception Occured while removing sequence generator \" " + this.name + " \" ", dae);
            throw new PersistenceException(dae.getMessage(), dae);
        }
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public Object getMaxValue() {
        return Long.MAX_VALUE;
    }
    
    @Override
    public Object getCurrentValue() {
        try {
            return Counter.getCurrentValue(this.name, this.startValue);
        }
        catch (final DataAccessException dae) {
            NonGapSequenceGenerator.out.log(Level.SEVERE, "Exception Occured while fetching current value for the sequence generator \" " + this.name + " \" ", dae);
            return null;
        }
    }
    
    @Override
    public int getBatchSize() {
        NonGapSequenceGenerator.out.log(Level.WARNING, "Ignoring getBatchSize for {0}", this.name);
        return 0;
    }
    
    @Override
    public Object getStartValue() {
        return this.startValue;
    }
    
    @Override
    public void renameTo(final String newName) throws PersistenceException {
        try {
            Counter.renameSequenceName(this.name, newName);
        }
        catch (final DataAccessException dae) {
            throw new PersistenceException(dae.getMessage(), dae);
        }
        this.name = newName;
    }
    
    static {
        NonGapSequenceGenerator.out = Logger.getLogger(NonGapSequenceGenerator.class.getName());
    }
}
