package com.sun.xml.internal.bind.v2.model.impl;

import java.util.Collection;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import java.util.Set;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

class RuntimeReferencePropertyInfoImpl extends ReferencePropertyInfoImpl<Type, Class, Field, Method> implements RuntimeReferencePropertyInfo
{
    private final Accessor acc;
    
    public RuntimeReferencePropertyInfoImpl(final RuntimeClassInfoImpl classInfo, final PropertySeed<Type, Class, Field, Method> seed) {
        super(classInfo, seed);
        Accessor rawAcc = ((RuntimeClassInfoImpl.RuntimePropertySeed)seed).getAccessor();
        if (this.getAdapter() != null && !this.isCollection()) {
            rawAcc = rawAcc.adapt(((PropertyInfoImpl<Type, Class, F, M>)this).getAdapter());
        }
        this.acc = rawAcc;
    }
    
    @Override
    public Set<? extends RuntimeElement> getElements() {
        return (Set<? extends RuntimeElement>)super.getElements();
    }
    
    @Override
    public Set<? extends RuntimeElement> ref() {
        return (Set<? extends RuntimeElement>)super.ref();
    }
    
    @Override
    public Accessor getAccessor() {
        return this.acc;
    }
    
    @Override
    public boolean elementOnlyContent() {
        return !this.isMixed();
    }
}
