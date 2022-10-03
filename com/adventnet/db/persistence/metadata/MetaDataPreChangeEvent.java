package com.adventnet.db.persistence.metadata;

public class MetaDataPreChangeEvent
{
    private Object object;
    private int operationType;
    
    public MetaDataPreChangeEvent(final Object object, final int operationType) {
        this.object = null;
        this.operationType = 0;
        this.object = object;
        this.operationType = operationType;
    }
    
    public Object getObject() {
        return this.object;
    }
    
    public int getOperationType() {
        return this.operationType;
    }
}
