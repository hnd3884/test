package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.databind.PropertyName;

public class ObjectIdInfo
{
    protected final PropertyName _propertyName;
    protected final Class<? extends ObjectIdGenerator<?>> _generator;
    protected final Class<? extends ObjectIdResolver> _resolver;
    protected final Class<?> _scope;
    protected final boolean _alwaysAsId;
    private static final ObjectIdInfo EMPTY;
    
    public ObjectIdInfo(final PropertyName name, final Class<?> scope, final Class<? extends ObjectIdGenerator<?>> gen, final Class<? extends ObjectIdResolver> resolver) {
        this(name, scope, gen, false, resolver);
    }
    
    protected ObjectIdInfo(final PropertyName prop, final Class<?> scope, final Class<? extends ObjectIdGenerator<?>> gen, final boolean alwaysAsId) {
        this(prop, scope, gen, alwaysAsId, (Class<? extends ObjectIdResolver>)SimpleObjectIdResolver.class);
    }
    
    protected ObjectIdInfo(final PropertyName prop, final Class<?> scope, final Class<? extends ObjectIdGenerator<?>> gen, final boolean alwaysAsId, Class<? extends ObjectIdResolver> resolver) {
        this._propertyName = prop;
        this._scope = scope;
        this._generator = gen;
        this._alwaysAsId = alwaysAsId;
        if (resolver == null) {
            resolver = (Class<? extends ObjectIdResolver>)SimpleObjectIdResolver.class;
        }
        this._resolver = resolver;
    }
    
    public static ObjectIdInfo empty() {
        return ObjectIdInfo.EMPTY;
    }
    
    public ObjectIdInfo withAlwaysAsId(final boolean state) {
        if (this._alwaysAsId == state) {
            return this;
        }
        return new ObjectIdInfo(this._propertyName, this._scope, this._generator, state, this._resolver);
    }
    
    public PropertyName getPropertyName() {
        return this._propertyName;
    }
    
    public Class<?> getScope() {
        return this._scope;
    }
    
    public Class<? extends ObjectIdGenerator<?>> getGeneratorType() {
        return this._generator;
    }
    
    public Class<? extends ObjectIdResolver> getResolverType() {
        return this._resolver;
    }
    
    public boolean getAlwaysAsId() {
        return this._alwaysAsId;
    }
    
    @Override
    public String toString() {
        return "ObjectIdInfo: propName=" + this._propertyName + ", scope=" + ClassUtil.nameOf(this._scope) + ", generatorType=" + ClassUtil.nameOf(this._generator) + ", alwaysAsId=" + this._alwaysAsId;
    }
    
    static {
        EMPTY = new ObjectIdInfo(PropertyName.NO_NAME, Object.class, null, false, null);
    }
}
