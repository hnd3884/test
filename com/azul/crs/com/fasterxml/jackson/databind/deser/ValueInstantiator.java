package com.azul.crs.com.fasterxml.jackson.databind.deser;

import java.io.Serializable;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationFeature;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.BeanDescription;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;

public abstract class ValueInstantiator
{
    public ValueInstantiator createContextual(final DeserializationContext ctxt, final BeanDescription beanDesc) throws JsonMappingException {
        return this;
    }
    
    public Class<?> getValueClass() {
        return Object.class;
    }
    
    public String getValueTypeDesc() {
        final Class<?> cls = this.getValueClass();
        if (cls == null) {
            return "UNKNOWN";
        }
        return cls.getName();
    }
    
    public boolean canInstantiate() {
        return this.canCreateUsingDefault() || this.canCreateUsingDelegate() || this.canCreateUsingArrayDelegate() || this.canCreateFromObjectWith() || this.canCreateFromString() || this.canCreateFromInt() || this.canCreateFromLong() || this.canCreateFromDouble() || this.canCreateFromBoolean();
    }
    
    public boolean canCreateFromString() {
        return false;
    }
    
    public boolean canCreateFromInt() {
        return false;
    }
    
    public boolean canCreateFromLong() {
        return false;
    }
    
    public boolean canCreateFromBigInteger() {
        return false;
    }
    
    public boolean canCreateFromDouble() {
        return false;
    }
    
    public boolean canCreateFromBigDecimal() {
        return false;
    }
    
    public boolean canCreateFromBoolean() {
        return false;
    }
    
    public boolean canCreateUsingDefault() {
        return this.getDefaultCreator() != null;
    }
    
    public boolean canCreateUsingDelegate() {
        return false;
    }
    
    public boolean canCreateUsingArrayDelegate() {
        return false;
    }
    
    public boolean canCreateFromObjectWith() {
        return false;
    }
    
    public SettableBeanProperty[] getFromObjectArguments(final DeserializationConfig config) {
        return null;
    }
    
    public JavaType getDelegateType(final DeserializationConfig config) {
        return null;
    }
    
    public JavaType getArrayDelegateType(final DeserializationConfig config) {
        return null;
    }
    
    public Object createUsingDefault(final DeserializationContext ctxt) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no default no-arguments constructor found", new Object[0]);
    }
    
    public Object createFromObjectWith(final DeserializationContext ctxt, final Object[] args) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no creator with arguments specified", new Object[0]);
    }
    
    public Object createFromObjectWith(final DeserializationContext ctxt, final SettableBeanProperty[] props, final PropertyValueBuffer buffer) throws IOException {
        return this.createFromObjectWith(ctxt, buffer.getParameters(props));
    }
    
    public Object createUsingDelegate(final DeserializationContext ctxt, final Object delegate) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no delegate creator specified", new Object[0]);
    }
    
    public Object createUsingArrayDelegate(final DeserializationContext ctxt, final Object delegate) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no array delegate creator specified", new Object[0]);
    }
    
    public Object createFromString(final DeserializationContext ctxt, final String value) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, ctxt.getParser(), "no String-argument constructor/factory method to deserialize from String value ('%s')", value);
    }
    
    public Object createFromInt(final DeserializationContext ctxt, final int value) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no int/Int-argument constructor/factory method to deserialize from Number value (%s)", value);
    }
    
    public Object createFromLong(final DeserializationContext ctxt, final long value) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no long/Long-argument constructor/factory method to deserialize from Number value (%s)", value);
    }
    
    public Object createFromBigInteger(final DeserializationContext ctxt, final BigInteger value) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no BigInteger-argument constructor/factory method to deserialize from Number value (%s)", value);
    }
    
    public Object createFromDouble(final DeserializationContext ctxt, final double value) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no double/Double-argument constructor/factory method to deserialize from Number value (%s)", value);
    }
    
    public Object createFromBigDecimal(final DeserializationContext ctxt, final BigDecimal value) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no BigDecimal/double/Double-argument constructor/factory method to deserialize from Number value (%s)", value);
    }
    
    public Object createFromBoolean(final DeserializationContext ctxt, final boolean value) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no boolean/Boolean-argument constructor/factory method to deserialize from boolean value (%s)", value);
    }
    
    public AnnotatedWithParams getDefaultCreator() {
        return null;
    }
    
    public AnnotatedWithParams getDelegateCreator() {
        return null;
    }
    
    public AnnotatedWithParams getArrayDelegateCreator() {
        return null;
    }
    
    public AnnotatedWithParams getWithArgsCreator() {
        return null;
    }
    
    @Deprecated
    protected Object _createFromStringFallbacks(final DeserializationContext ctxt, final String value) throws IOException {
        if (value.isEmpty() && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            return null;
        }
        if (this.canCreateFromBoolean() && ctxt.findCoercionAction(LogicalType.Boolean, Boolean.class, CoercionInputShape.String) == CoercionAction.TryConvert) {
            final String str = value.trim();
            if ("true".equals(str)) {
                return this.createFromBoolean(ctxt, true);
            }
            if ("false".equals(str)) {
                return this.createFromBoolean(ctxt, false);
            }
        }
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, ctxt.getParser(), "no String-argument constructor/factory method to deserialize from String value ('%s')", value);
    }
    
    public static class Base extends ValueInstantiator implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected final Class<?> _valueType;
        
        public Base(final Class<?> type) {
            this._valueType = type;
        }
        
        public Base(final JavaType type) {
            this._valueType = type.getRawClass();
        }
        
        @Override
        public String getValueTypeDesc() {
            return this._valueType.getName();
        }
        
        @Override
        public Class<?> getValueClass() {
            return this._valueType;
        }
    }
    
    public static class Delegating extends ValueInstantiator implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected final ValueInstantiator _delegate;
        
        protected Delegating(final ValueInstantiator delegate) {
            this._delegate = delegate;
        }
        
        @Override
        public ValueInstantiator createContextual(final DeserializationContext ctxt, final BeanDescription beanDesc) throws JsonMappingException {
            final ValueInstantiator d = this._delegate.createContextual(ctxt, beanDesc);
            return (d == this._delegate) ? this : new Delegating(d);
        }
        
        protected ValueInstantiator delegate() {
            return this._delegate;
        }
        
        @Override
        public Class<?> getValueClass() {
            return this.delegate().getValueClass();
        }
        
        @Override
        public String getValueTypeDesc() {
            return this.delegate().getValueTypeDesc();
        }
        
        @Override
        public boolean canInstantiate() {
            return this.delegate().canInstantiate();
        }
        
        @Override
        public boolean canCreateFromString() {
            return this.delegate().canCreateFromString();
        }
        
        @Override
        public boolean canCreateFromInt() {
            return this.delegate().canCreateFromInt();
        }
        
        @Override
        public boolean canCreateFromLong() {
            return this.delegate().canCreateFromLong();
        }
        
        @Override
        public boolean canCreateFromDouble() {
            return this.delegate().canCreateFromDouble();
        }
        
        @Override
        public boolean canCreateFromBoolean() {
            return this.delegate().canCreateFromBoolean();
        }
        
        @Override
        public boolean canCreateUsingDefault() {
            return this.delegate().canCreateUsingDefault();
        }
        
        @Override
        public boolean canCreateUsingDelegate() {
            return this.delegate().canCreateUsingDelegate();
        }
        
        @Override
        public boolean canCreateUsingArrayDelegate() {
            return this.delegate().canCreateUsingArrayDelegate();
        }
        
        @Override
        public boolean canCreateFromObjectWith() {
            return this.delegate().canCreateFromObjectWith();
        }
        
        @Override
        public SettableBeanProperty[] getFromObjectArguments(final DeserializationConfig config) {
            return this.delegate().getFromObjectArguments(config);
        }
        
        @Override
        public JavaType getDelegateType(final DeserializationConfig config) {
            return this.delegate().getDelegateType(config);
        }
        
        @Override
        public JavaType getArrayDelegateType(final DeserializationConfig config) {
            return this.delegate().getArrayDelegateType(config);
        }
        
        @Override
        public Object createUsingDefault(final DeserializationContext ctxt) throws IOException {
            return this.delegate().createUsingDefault(ctxt);
        }
        
        @Override
        public Object createFromObjectWith(final DeserializationContext ctxt, final Object[] args) throws IOException {
            return this.delegate().createFromObjectWith(ctxt, args);
        }
        
        @Override
        public Object createFromObjectWith(final DeserializationContext ctxt, final SettableBeanProperty[] props, final PropertyValueBuffer buffer) throws IOException {
            return this.delegate().createFromObjectWith(ctxt, props, buffer);
        }
        
        @Override
        public Object createUsingDelegate(final DeserializationContext ctxt, final Object delegate) throws IOException {
            return this.delegate().createUsingDelegate(ctxt, delegate);
        }
        
        @Override
        public Object createUsingArrayDelegate(final DeserializationContext ctxt, final Object delegate) throws IOException {
            return this.delegate().createUsingArrayDelegate(ctxt, delegate);
        }
        
        @Override
        public Object createFromString(final DeserializationContext ctxt, final String value) throws IOException {
            return this.delegate().createFromString(ctxt, value);
        }
        
        @Override
        public Object createFromInt(final DeserializationContext ctxt, final int value) throws IOException {
            return this.delegate().createFromInt(ctxt, value);
        }
        
        @Override
        public Object createFromLong(final DeserializationContext ctxt, final long value) throws IOException {
            return this.delegate().createFromLong(ctxt, value);
        }
        
        @Override
        public Object createFromBigInteger(final DeserializationContext ctxt, final BigInteger value) throws IOException {
            return this.delegate().createFromBigInteger(ctxt, value);
        }
        
        @Override
        public Object createFromDouble(final DeserializationContext ctxt, final double value) throws IOException {
            return this.delegate().createFromDouble(ctxt, value);
        }
        
        @Override
        public Object createFromBigDecimal(final DeserializationContext ctxt, final BigDecimal value) throws IOException {
            return this.delegate().createFromBigDecimal(ctxt, value);
        }
        
        @Override
        public Object createFromBoolean(final DeserializationContext ctxt, final boolean value) throws IOException {
            return this.delegate().createFromBoolean(ctxt, value);
        }
        
        @Override
        public AnnotatedWithParams getDefaultCreator() {
            return this.delegate().getDefaultCreator();
        }
        
        @Override
        public AnnotatedWithParams getDelegateCreator() {
            return this.delegate().getDelegateCreator();
        }
        
        @Override
        public AnnotatedWithParams getArrayDelegateCreator() {
            return this.delegate().getArrayDelegateCreator();
        }
        
        @Override
        public AnnotatedWithParams getWithArgsCreator() {
            return this.delegate().getWithArgsCreator();
        }
    }
    
    public interface Gettable
    {
        ValueInstantiator getValueInstantiator();
    }
}
