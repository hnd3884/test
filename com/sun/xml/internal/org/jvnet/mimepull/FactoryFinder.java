package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

class FactoryFinder
{
    private static ClassLoader cl;
    
    static Object find(final String factoryId) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final String systemProp = System.getProperty(factoryId);
        if (systemProp != null) {
            return newInstance(systemProp);
        }
        final String providerName = findJarServiceProviderName(factoryId);
        if (providerName != null && providerName.trim().length() > 0) {
            return newInstance(providerName);
        }
        return null;
    }
    
    static Object newInstance(final String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final Class providerClass = FactoryFinder.cl.loadClass(className);
        final Object instance = providerClass.newInstance();
        return instance;
    }
    
    private static String findJarServiceProviderName(final String factoryId) {
        final String serviceId = "META-INF/services/" + factoryId;
        final InputStream is = FactoryFinder.cl.getResourceAsStream(serviceId);
        if (is == null) {
            return null;
        }
        BufferedReader rd = null;
        String factoryClassName;
        try {
            try {
                rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            }
            catch (final UnsupportedEncodingException e) {
                rd = new BufferedReader(new InputStreamReader(is));
            }
            try {
                factoryClassName = rd.readLine();
            }
            catch (final IOException x) {
                return null;
            }
        }
        finally {
            if (rd != null) {
                try {
                    rd.close();
                }
                catch (final IOException ex) {
                    Logger.getLogger(FactoryFinder.class.getName()).log(Level.INFO, null, ex);
                }
            }
        }
        return factoryClassName;
    }
    
    static {
        FactoryFinder.cl = FactoryFinder.class.getClassLoader();
    }
}
