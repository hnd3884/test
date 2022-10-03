package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import java.util.Map;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

final class RuntimeTypeInfoSetImpl extends TypeInfoSetImpl<Type, Class, Field, Method> implements RuntimeTypeInfoSet
{
    public RuntimeTypeInfoSetImpl(final AnnotationReader<Type, Class, Field, Method> reader) {
        super(Utils.REFLECTION_NAVIGATOR, reader, RuntimeBuiltinLeafInfoImpl.LEAVES);
    }
    
    @Override
    protected RuntimeNonElement createAnyType() {
        return RuntimeAnyTypeImpl.theInstance;
    }
    
    @Override
    public RuntimeNonElement getTypeInfo(final Type type) {
        return (RuntimeNonElement)super.getTypeInfo(type);
    }
    
    @Override
    public RuntimeNonElement getAnyTypeInfo() {
        return (RuntimeNonElement)super.getAnyTypeInfo();
    }
    
    @Override
    public RuntimeNonElement getClassInfo(final Class clazz) {
        return (RuntimeNonElement)super.getClassInfo(clazz);
    }
    
    @Override
    public Map<Class, RuntimeClassInfoImpl> beans() {
        return (Map<Class, RuntimeClassInfoImpl>)super.beans();
    }
    
    @Override
    public Map<Type, RuntimeBuiltinLeafInfoImpl<?>> builtins() {
        return (Map<Type, RuntimeBuiltinLeafInfoImpl<?>>)super.builtins();
    }
    
    @Override
    public Map<Class, RuntimeEnumLeafInfoImpl<?, ?>> enums() {
        return (Map<Class, RuntimeEnumLeafInfoImpl<?, ?>>)super.enums();
    }
    
    @Override
    public Map<Class, RuntimeArrayInfoImpl> arrays() {
        return (Map<Class, RuntimeArrayInfoImpl>)super.arrays();
    }
    
    @Override
    public RuntimeElementInfoImpl getElementInfo(final Class scope, final QName name) {
        return (RuntimeElementInfoImpl)super.getElementInfo(scope, name);
    }
    
    @Override
    public Map<QName, RuntimeElementInfoImpl> getElementMappings(final Class scope) {
        return (Map<QName, RuntimeElementInfoImpl>)super.getElementMappings(scope);
    }
    
    @Override
    public Iterable<RuntimeElementInfoImpl> getAllElements() {
        return (Iterable<RuntimeElementInfoImpl>)super.getAllElements();
    }
}
