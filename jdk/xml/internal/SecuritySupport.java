package jdk.xml.internal;

import java.security.PrivilegedActionException;
import java.io.FileNotFoundException;
import java.security.PrivilegedExceptionAction;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

class SecuritySupport
{
    static final Properties cacheProps;
    static volatile boolean firstTime;
    
    private SecuritySupport() {
    }
    
    public static String getSystemProperty(final String propName) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty(propName);
            }
        });
    }
    
    public static <T> T getJAXPSystemProperty(final Class<T> type, final String propName, final String defValue) {
        String value = getJAXPSystemProperty(propName);
        if (value == null) {
            value = defValue;
        }
        if (Integer.class.isAssignableFrom(type)) {
            return type.cast(Integer.parseInt(value));
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return type.cast(Boolean.parseBoolean(value));
        }
        return type.cast(value);
    }
    
    public static String getJAXPSystemProperty(final String propName) {
        String value = getSystemProperty(propName);
        if (value == null) {
            value = readJAXPProperty(propName);
        }
        return value;
    }
    
    public static String readJAXPProperty(final String propName) {
        String value = null;
        InputStream is = null;
        try {
            if (SecuritySupport.firstTime) {
                synchronized (SecuritySupport.cacheProps) {
                    if (SecuritySupport.firstTime) {
                        final String configFile = getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
                        final File f = new File(configFile);
                        if (getFileExists(f)) {
                            is = getFileInputStream(f);
                            SecuritySupport.cacheProps.load(is);
                        }
                        SecuritySupport.firstTime = false;
                    }
                }
            }
            value = SecuritySupport.cacheProps.getProperty(propName);
        }
        catch (final IOException ex) {}
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (final IOException ex2) {}
            }
        }
        return value;
    }
    
    static boolean getFileExists(final File f) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return f.exists() ? Boolean.TRUE : Boolean.FALSE;
            }
        });
    }
    
    static FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<FileInputStream>)new PrivilegedExceptionAction<FileInputStream>() {
                @Override
                public FileInputStream run() throws Exception {
                    return new FileInputStream(file);
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw (FileNotFoundException)e.getException();
        }
    }
    
    static {
        cacheProps = new Properties();
        SecuritySupport.firstTime = true;
    }
}
