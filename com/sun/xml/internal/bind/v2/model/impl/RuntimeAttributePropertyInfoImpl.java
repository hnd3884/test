package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import java.util.Collection;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import java.util.List;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeAttributePropertyInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

class RuntimeAttributePropertyInfoImpl extends AttributePropertyInfoImpl<Type, Class, Field, Method> implements RuntimeAttributePropertyInfo
{
    RuntimeAttributePropertyInfoImpl(final RuntimeClassInfoImpl classInfo, final PropertySeed<Type, Class, Field, Method> seed) {
        super(classInfo, seed);
    }
    
    @Override
    public boolean elementOnlyContent() {
        return true;
    }
    
    @Override
    public RuntimeNonElement getTarget() {
        return (RuntimeNonElement)super.getTarget();
    }
    
    @Override
    public List<? extends RuntimeNonElement> ref() {
        return (List<? extends RuntimeNonElement>)super.ref();
    }
    
    @Override
    public RuntimePropertyInfo getSource() {
        return this;
    }
    
    @Override
    public void link() {
        this.getTransducer();
        super.link();
    }
}
