package com.fasterxml.jackson.databind.ext;

import java.util.logging.Logger;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.JsonDeserializer;

public abstract class Java7Handlers
{
    private static final Java7Handlers IMPL;
    
    public static Java7Handlers instance() {
        return Java7Handlers.IMPL;
    }
    
    public abstract Class<?> getClassJavaNioFilePath();
    
    public abstract JsonDeserializer<?> getDeserializerForJavaNioFilePath(final Class<?> p0);
    
    public abstract JsonSerializer<?> getSerializerForJavaNioFilePath(final Class<?> p0);
    
    static {
        Java7Handlers impl = null;
        try {
            final Class<?> cls = Class.forName("com.fasterxml.jackson.databind.ext.Java7HandlersImpl");
            impl = ClassUtil.createInstance(cls, false);
        }
        catch (final Throwable t) {
            Logger.getLogger(Java7Handlers.class.getName()).warning("Unable to load JDK7 types (java.nio.file.Path): no Java7 type support added");
        }
        IMPL = impl;
    }
}
