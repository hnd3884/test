package com.sun.xml.internal.bind.v2.model.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

final class RuntimeEnumConstantImpl extends EnumConstantImpl<Type, Class, Field, Method>
{
    public RuntimeEnumConstantImpl(final RuntimeEnumLeafInfoImpl owner, final String name, final String lexical, final EnumConstantImpl<Type, Class, Field, Method> next) {
        super(owner, name, lexical, next);
    }
}
