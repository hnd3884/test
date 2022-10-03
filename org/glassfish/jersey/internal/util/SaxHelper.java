package org.glassfish.jersey.internal.util;

import java.security.AccessController;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

public final class SaxHelper
{
    private SaxHelper() {
    }
    
    public static boolean isXdkParserFactory(final SAXParserFactory parserFactory) {
        return isXdkFactory(parserFactory, "oracle.xml.jaxp.JXSAXParserFactory");
    }
    
    public static boolean isXdkDocumentBuilderFactory(final DocumentBuilderFactory builderFactory) {
        return isXdkFactory(builderFactory, "oracle.xml.jaxp.JXDocumentBuilderFactory");
    }
    
    private static boolean isXdkFactory(final Object factory, final String className) {
        final Class<?> xdkFactoryClass = AccessController.doPrivileged(ReflectionHelper.classForNamePA(className, null));
        return xdkFactoryClass != null && xdkFactoryClass.isAssignableFrom(factory.getClass());
    }
}
