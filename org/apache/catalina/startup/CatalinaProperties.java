package org.apache.catalina.startup;

import org.apache.juli.logging.LogFactory;
import java.util.Enumeration;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.net.URL;
import java.util.Properties;
import org.apache.juli.logging.Log;

public class CatalinaProperties
{
    private static final Log log;
    private static Properties properties;
    
    public static String getProperty(final String name) {
        return CatalinaProperties.properties.getProperty(name);
    }
    
    private static void loadProperties() {
        InputStream is = null;
        try {
            final String configUrl = System.getProperty("catalina.config");
            if (configUrl != null) {
                is = new URL(configUrl).openStream();
            }
        }
        catch (final Throwable t) {
            handleThrowable(t);
        }
        if (is == null) {
            try {
                final File home = new File(Bootstrap.getCatalinaBase());
                final File conf = new File(home, "conf");
                final File propsFile = new File(conf, "catalina.properties");
                is = new FileInputStream(propsFile);
            }
            catch (final Throwable t) {
                handleThrowable(t);
            }
        }
        if (is == null) {
            try {
                is = CatalinaProperties.class.getResourceAsStream("/org/apache/catalina/startup/catalina.properties");
            }
            catch (final Throwable t) {
                handleThrowable(t);
            }
        }
        if (is != null) {
            try {
                (CatalinaProperties.properties = new Properties()).load(is);
            }
            catch (final Throwable t) {
                handleThrowable(t);
                CatalinaProperties.log.warn((Object)t);
                try {
                    is.close();
                }
                catch (final IOException ioe) {
                    CatalinaProperties.log.warn((Object)"Could not close catalina.properties", (Throwable)ioe);
                }
            }
            finally {
                try {
                    is.close();
                }
                catch (final IOException ioe2) {
                    CatalinaProperties.log.warn((Object)"Could not close catalina.properties", (Throwable)ioe2);
                }
            }
        }
        if (is == null) {
            CatalinaProperties.log.warn((Object)"Failed to load catalina.properties");
            CatalinaProperties.properties = new Properties();
        }
        final Enumeration<?> enumeration = CatalinaProperties.properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            final String name = (String)enumeration.nextElement();
            final String value = CatalinaProperties.properties.getProperty(name);
            if (value != null) {
                System.setProperty(name, value);
            }
        }
    }
    
    private static void handleThrowable(final Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError)t;
        }
    }
    
    static {
        log = LogFactory.getLog((Class)CatalinaProperties.class);
        CatalinaProperties.properties = null;
        loadProperties();
    }
}
