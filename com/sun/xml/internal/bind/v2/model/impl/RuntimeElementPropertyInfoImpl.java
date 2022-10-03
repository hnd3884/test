package com.sun.xml.internal.bind.v2.model.impl;

import java.util.Collection;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import java.util.List;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

class RuntimeElementPropertyInfoImpl extends ElementPropertyInfoImpl<Type, Class, Field, Method> implements RuntimeElementPropertyInfo
{
    private final Accessor acc;
    
    RuntimeElementPropertyInfoImpl(final RuntimeClassInfoImpl classInfo, final PropertySeed<Type, Class, Field, Method> seed) {
        super(classInfo, seed);
        Accessor rawAcc = ((RuntimeClassInfoImpl.RuntimePropertySeed)seed).getAccessor();
        if (this.getAdapter() != null && !this.isCollection()) {
            rawAcc = rawAcc.adapt(((PropertyInfoImpl<Type, Class, F, M>)this).getAdapter());
        }
        this.acc = rawAcc;
    }
    
    @Override
    public Accessor getAccessor() {
        return this.acc;
    }
    
    @Override
    public boolean elementOnlyContent() {
        return true;
    }
    
    @Override
    public List<? extends RuntimeTypeInfo> ref() {
        return (List<? extends RuntimeTypeInfo>)super.ref();
    }
    
    @Override
    protected RuntimeTypeRefImpl createTypeRef(final QName name, final Type type, final boolean isNillable, final String defaultValue) {
        return new RuntimeTypeRefImpl(this, name, type, isNillable, defaultValue);
    }
    
    @Override
    public List<RuntimeTypeRefImpl> getTypes() {
        return (List<RuntimeTypeRefImpl>)super.getTypes();
    }
}
