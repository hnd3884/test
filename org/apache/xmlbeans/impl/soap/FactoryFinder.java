package org.apache.xmlbeans.impl.soap;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.File;
import org.apache.xmlbeans.SystemProperties;

class FactoryFinder
{
    private static Object newInstance(final String factoryClassName) throws SOAPException {
        ClassLoader classloader = null;
        try {
            classloader = Thread.currentThread().getContextClassLoader();
        }
        catch (final Exception exception) {
            throw new SOAPException(exception.toString(), exception);
        }
        try {
            Class factory = null;
            if (classloader == null) {
                factory = Class.forName(factoryClassName);
            }
            else {
                try {
                    factory = classloader.loadClass(factoryClassName);
                }
                catch (final ClassNotFoundException ex) {}
            }
            if (factory == null) {
                classloader = FactoryFinder.class.getClassLoader();
                factory = classloader.loadClass(factoryClassName);
            }
            return factory.newInstance();
        }
        catch (final ClassNotFoundException classnotfoundexception) {
            throw new SOAPException("Provider " + factoryClassName + " not found", classnotfoundexception);
        }
        catch (final Exception exception) {
            throw new SOAPException("Provider " + factoryClassName + " could not be instantiated: " + exception, exception);
        }
    }
    
    static Object find(final String factoryPropertyName, final String defaultFactoryClassName) throws SOAPException {
        try {
            final String factoryClassName = SystemProperties.getProperty(factoryPropertyName);
            if (factoryClassName != null) {
                return newInstance(factoryClassName);
            }
        }
        catch (final SecurityException ex) {}
        try {
            final String propertiesFileName = SystemProperties.getProperty("java.home") + File.separator + "lib" + File.separator + "jaxm.properties";
            final File file = new File(propertiesFileName);
            if (file.exists()) {
                final FileInputStream fileInput = new FileInputStream(file);
                final Properties properties = new Properties();
                properties.load(fileInput);
                fileInput.close();
                final String factoryClassName2 = properties.getProperty(factoryPropertyName);
                return newInstance(factoryClassName2);
            }
        }
        catch (final Exception ex2) {}
        final String factoryResource = "META-INF/services/" + factoryPropertyName;
        try {
            final InputStream inputstream = getResource(factoryResource);
            if (inputstream != null) {
                final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));
                final String factoryClassName3 = bufferedreader.readLine();
                bufferedreader.close();
                if (factoryClassName3 != null && !"".equals(factoryClassName3)) {
                    return newInstance(factoryClassName3);
                }
            }
        }
        catch (final Exception ex3) {}
        if (defaultFactoryClassName == null) {
            throw new SOAPException("Provider for " + factoryPropertyName + " cannot be found", null);
        }
        return newInstance(defaultFactoryClassName);
    }
    
    private static InputStream getResource(final String factoryResource) {
        ClassLoader classloader = null;
        try {
            classloader = Thread.currentThread().getContextClassLoader();
        }
        catch (final SecurityException ex) {}
        InputStream inputstream;
        if (classloader == null) {
            inputstream = ClassLoader.getSystemResourceAsStream(factoryResource);
        }
        else {
            inputstream = classloader.getResourceAsStream(factoryResource);
        }
        if (inputstream == null) {
            inputstream = FactoryFinder.class.getResourceAsStream(factoryResource);
        }
        if (inputstream == null && FactoryFinder.class.getClassLoader() != null) {
            inputstream = FactoryFinder.class.getClassLoader().getResourceAsStream(factoryResource);
        }
        return inputstream;
    }
}
