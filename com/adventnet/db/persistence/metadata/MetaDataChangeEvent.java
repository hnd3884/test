package com.adventnet.db.persistence.metadata;

public class MetaDataChangeEvent
{
    private Object object;
    private int operationType;
    
    public MetaDataChangeEvent(final Object object, final int operationType) {
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
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("\noperation-type : " + this.operationType);
        b.append("\nObject : " + this.object);
        return b.toString();
    }
}
