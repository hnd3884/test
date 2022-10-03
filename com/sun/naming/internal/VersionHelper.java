package com.sun.naming.internal;

import java.util.Vector;
import java.util.StringTokenizer;
import java.net.URL;
import java.io.IOException;
import javax.naming.NamingEnumeration;
import java.io.InputStream;
import java.net.MalformedURLException;

public abstract class VersionHelper
{
    private static VersionHelper helper;
    static final String[] PROPS;
    public static final int INITIAL_CONTEXT_FACTORY = 0;
    public static final int OBJECT_FACTORIES = 1;
    public static final int URL_PKG_PREFIXES = 2;
    public static final int STATE_FACTORIES = 3;
    public static final int PROVIDER_URL = 4;
    public static final int DNS_URL = 5;
    public static final int CONTROL_FACTORIES = 6;
    
    VersionHelper() {
    }
    
    public static VersionHelper getVersionHelper() {
        return VersionHelper.helper;
    }
    
    public abstract Class<?> loadClass(final String p0) throws ClassNotFoundException;
    
    abstract Class<?> loadClass(final String p0, final ClassLoader p1) throws ClassNotFoundException;
    
    public abstract Class<?> loadClass(final String p0, final String p1) throws ClassNotFoundException, MalformedURLException;
    
    abstract String getJndiProperty(final int p0);
    
    abstract String[] getJndiProperties();
    
    abstract InputStream getResourceAsStream(final Class<?> p0, final String p1);
    
    abstract InputStream getJavaHomeLibStream(final String p0);
    
    abstract NamingEnumeration<InputStream> getResources(final ClassLoader p0, final String p1) throws IOException;
    
    abstract ClassLoader getContextClassLoader();
    
    protected static URL[] getUrlArray(final String s) throws MalformedURLException {
        final StringTokenizer stringTokenizer = new StringTokenizer(s);
        final Vector vector = new Vector(10);
        while (stringTokenizer.hasMoreTokens()) {
            vector.addElement(stringTokenizer.nextToken());
        }
        final String[] array = new String[vector.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (String)vector.elementAt(i);
        }
        final URL[] array2 = new URL[array.length];
        for (int j = 0; j < array2.length; ++j) {
            array2[j] = new URL(array[j]);
        }
        return array2;
    }
    
    static {
        VersionHelper.helper = null;
        PROPS = new String[] { "java.naming.factory.initial", "java.naming.factory.object", "java.naming.factory.url.pkgs", "java.naming.factory.state", "java.naming.provider.url", "java.naming.dns.url", "java.naming.factory.control" };
        VersionHelper.helper = new VersionHelper12();
    }
}
