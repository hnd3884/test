package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.ValueSource;

public abstract class ConstNumberSource extends ValueSource
{
    public abstract int getInt();
    
    public abstract long getLong();
    
    public abstract float getFloat();
    
    public abstract double getDouble();
    
    public abstract Number getNumber();
    
    public abstract boolean getBool();
}
