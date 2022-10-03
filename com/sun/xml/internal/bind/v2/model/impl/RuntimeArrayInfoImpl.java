package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeArrayInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

final class RuntimeArrayInfoImpl extends ArrayInfoImpl<Type, Class, Field, Method> implements RuntimeArrayInfo
{
    RuntimeArrayInfoImpl(final RuntimeModelBuilder builder, final Locatable upstream, final Class arrayType) {
        super((ModelBuilder<Class, Object, Object, Object>)builder, upstream, arrayType);
    }
    
    @Override
    public Class getType() {
        return super.getType();
    }
    
    @Override
    public RuntimeNonElement getItemType() {
        return (RuntimeNonElement)super.getItemType();
    }
    
    @Override
    public <V> Transducer<V> getTransducer() {
        return null;
    }
}
