package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

public class PropertyBasedObjectIdGenerator extends ObjectIdGenerators.PropertyGenerator
{
    private static final long serialVersionUID = 1L;
    protected final BeanPropertyWriter _property;
    
    public PropertyBasedObjectIdGenerator(final ObjectIdInfo oid, final BeanPropertyWriter prop) {
        this(oid.getScope(), prop);
    }
    
    protected PropertyBasedObjectIdGenerator(final Class<?> scope, final BeanPropertyWriter prop) {
        super((Class)scope);
        this._property = prop;
    }
    
    public boolean canUseFor(final ObjectIdGenerator<?> gen) {
        if (gen.getClass() == this.getClass()) {
            final PropertyBasedObjectIdGenerator other = (PropertyBasedObjectIdGenerator)gen;
            if (other.getScope() == this._scope) {
                return other._property == this._property;
            }
        }
        return false;
    }
    
    public Object generateId(final Object forPojo) {
        try {
            return this._property.get(forPojo);
        }
        catch (final RuntimeException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new IllegalStateException("Problem accessing property '" + this._property.getName() + "': " + e2.getMessage(), e2);
        }
    }
    
    public ObjectIdGenerator<Object> forScope(final Class<?> scope) {
        return (ObjectIdGenerator<Object>)((scope == this._scope) ? this : new PropertyBasedObjectIdGenerator(scope, this._property));
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
