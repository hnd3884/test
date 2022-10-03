package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.ParserData;

public abstract class ParserDataBase implements ParserData
{
    private String propertyName;
    private Operation operation;
    private String fieldName;
    private Object defaultValue;
    private Object testValue;
    
    protected ParserDataBase(final String propertyName, final Operation operation, final String fieldName, final Object defaultValue, final Object testValue) {
        this.propertyName = propertyName;
        this.operation = operation;
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.testValue = testValue;
    }
    
    @Override
    public String getPropertyName() {
        return this.propertyName;
    }
    
    @Override
    public Operation getOperation() {
        return this.operation;
    }
    
    @Override
    public String getFieldName() {
        return this.fieldName;
    }
    
    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }
    
    @Override
    public Object getTestValue() {
        return this.testValue;
    }
}
