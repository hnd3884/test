package com.sun.jndi.ldap;

import java.net.URL;
import java.net.MalformedURLException;

abstract class VersionHelper
{
    private static VersionHelper helper;
    
    static VersionHelper getVersionHelper() {
        return VersionHelper.helper;
    }
    
    abstract ClassLoader getURLClassLoader(final String[] p0) throws MalformedURLException;
    
    protected static URL[] getUrlArray(final String[] array) throws MalformedURLException {
        final URL[] array2 = new URL[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = new URL(array[i]);
        }
        return array2;
    }
    
    abstract Class<?> loadClass(final String p0) throws ClassNotFoundException;
    
    abstract Thread createThread(final Runnable p0);
    
    static {
        VersionHelper.helper = null;
        try {
            Class.forName("java.net.URLClassLoader");
            Class.forName("java.security.PrivilegedAction");
            VersionHelper.helper = (VersionHelper)Class.forName("com.sun.jndi.ldap.VersionHelper12").newInstance();
        }
        catch (final Exception ex) {}
        if (VersionHelper.helper == null) {
            try {
                VersionHelper.helper = (VersionHelper)Class.forName("com.sun.jndi.ldap.VersionHelper11").newInstance();
            }
            catch (final Exception ex2) {}
        }
    }
}
