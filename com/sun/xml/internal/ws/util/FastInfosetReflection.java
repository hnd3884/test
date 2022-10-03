package com.sun.xml.internal.ws.util;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public class FastInfosetReflection
{
    public static final Constructor fiStAXDocumentParser_new;
    public static final Method fiStAXDocumentParser_setInputStream;
    public static final Method fiStAXDocumentParser_setStringInterning;
    
    static {
        Constructor tmp_new = null;
        Method tmp_setInputStream = null;
        Method tmp_setStringInterning = null;
        try {
            final Class clazz = Class.forName("com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser");
            tmp_new = clazz.getConstructor((Class[])new Class[0]);
            tmp_setInputStream = clazz.getMethod("setInputStream", InputStream.class);
            tmp_setStringInterning = clazz.getMethod("setStringInterning", Boolean.TYPE);
        }
        catch (final Exception ex) {}
        fiStAXDocumentParser_new = tmp_new;
        fiStAXDocumentParser_setInputStream = tmp_setInputStream;
        fiStAXDocumentParser_setStringInterning = tmp_setStringInterning;
    }
}
