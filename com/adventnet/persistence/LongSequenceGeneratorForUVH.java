package com.adventnet.persistence;

import com.adventnet.db.persistence.LongSequenceGenerator;

public class LongSequenceGeneratorForUVH extends LongSequenceGenerator
{
    protected long startValue;
    
    public LongSequenceGeneratorForUVH() {
        this.startValue = 9007199254740993L;
    }
    
    public Object getStartValue() {
        return 9007199254740993L;
    }
    
    public synchronized Object nextValue() throws RuntimeException {
        if (this.batchEnd <= this.currVal) {
            try {
                this.getNextBatch();
            }
            catch (final PersistenceException var2) {
                throw new RuntimeException(var2.getMessage(), (Throwable)var2);
            }
        }
        ++this.currVal;
        ++this.currVal;
        return this.currVal;
    }
}
