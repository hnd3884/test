package com.sun.corba.se.impl.orb;

import java.util.Properties;
import com.sun.corba.se.spi.orb.Operation;

public abstract class ParserActionBase implements ParserAction
{
    private String propertyName;
    private boolean prefix;
    private Operation operation;
    private String fieldName;
    
    @Override
    public int hashCode() {
        return this.propertyName.hashCode() ^ this.operation.hashCode() ^ this.fieldName.hashCode() ^ (this.prefix ? 0 : 1);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ParserActionBase)) {
            return false;
        }
        final ParserActionBase parserActionBase = (ParserActionBase)o;
        return this.propertyName.equals(parserActionBase.propertyName) && this.prefix == parserActionBase.prefix && this.operation.equals(parserActionBase.operation) && this.fieldName.equals(parserActionBase.fieldName);
    }
    
    public ParserActionBase(final String propertyName, final boolean prefix, final Operation operation, final String fieldName) {
        this.propertyName = propertyName;
        this.prefix = prefix;
        this.operation = operation;
        this.fieldName = fieldName;
    }
    
    @Override
    public String getPropertyName() {
        return this.propertyName;
    }
    
    @Override
    public boolean isPrefix() {
        return this.prefix;
    }
    
    @Override
    public String getFieldName() {
        return this.fieldName;
    }
    
    @Override
    public abstract Object apply(final Properties p0);
    
    protected Operation getOperation() {
        return this.operation;
    }
}
