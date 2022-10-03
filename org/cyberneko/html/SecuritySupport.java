package org.cyberneko.html;

import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.File;

class SecuritySupport
{
    private static final Object securitySupport;
    
    static SecuritySupport getInstance() {
        return (SecuritySupport)SecuritySupport.securitySupport;
    }
    
    ClassLoader getContextClassLoader() {
        return null;
    }
    
    ClassLoader getSystemClassLoader() {
        return null;
    }
    
    ClassLoader getParentClassLoader(final ClassLoader cl) {
        return null;
    }
    
    String getSystemProperty(final String propName) {
        return System.getProperty(propName);
    }
    
    FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }
    
    InputStream getResourceAsStream(final ClassLoader cl, final String name) {
        InputStream ris;
        if (cl == null) {
            ris = ClassLoader.getSystemResourceAsStream(name);
        }
        else {
            ris = cl.getResourceAsStream(name);
        }
        return ris;
    }
    
    boolean getFileExists(final File f) {
        return f.exists();
    }
    
    long getLastModified(final File f) {
        return f.lastModified();
    }
    
    static {
        SecuritySupport ss = null;
        try {
            final Class c = Class.forName("java.security.AccessController");
            ss = new SecuritySupport12();
        }
        catch (final Exception ex) {}
        finally {
            if (ss == null) {
                ss = new SecuritySupport();
            }
            securitySupport = ss;
        }
    }
}
