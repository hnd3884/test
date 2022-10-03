package com.azul.crs.com.fasterxml.jackson.databind.ext;

import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import com.azul.crs.com.fasterxml.jackson.databind.PropertyName;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.Annotated;

public abstract class Java7Support
{
    private static final Java7Support IMPL;
    
    public static Java7Support instance() {
        return Java7Support.IMPL;
    }
    
    public abstract Boolean findTransient(final Annotated p0);
    
    public abstract Boolean hasCreatorAnnotation(final Annotated p0);
    
    public abstract PropertyName findConstructorName(final AnnotatedParameter p0);
    
    static {
        Java7Support impl = null;
        try {
            final Class<?> cls = Class.forName("com.azul.crs.com.fasterxml.jackson.databind.ext.Java7SupportImpl");
            impl = ClassUtil.createInstance(cls, false);
        }
        catch (final Throwable t) {}
        IMPL = impl;
    }
}
