package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

public class PropertyBasedObjectIdGenerator extends ObjectIdGenerators.PropertyGenerator
{
    private static final long serialVersionUID = 1L;
    
    public PropertyBasedObjectIdGenerator(final Class<?> scope) {
        super((Class)scope);
    }
    
    public Object generateId(final Object forPojo) {
        throw new UnsupportedOperationException();
    }
    
    public ObjectIdGenerator<Object> forScope(final Class<?> scope) {
        return (ObjectIdGenerator<Object>)((scope == this._scope) ? this : new PropertyBasedObjectIdGenerator(scope));
    }
    
    public ObjectIdGenerator<Object> newForSerialization(final Object context) {
        return (ObjectIdGenerator<Object>)this;
    }
    
    public ObjectIdGenerator.IdKey key(final Object key) {
        if (key == null) {
            return null;
        }
        return new ObjectIdGenerator.IdKey((Class)this.getClass(), this._scope, key);
    }
}
