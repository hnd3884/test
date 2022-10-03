package org.apache.commons.logging;

import org.apache.commons.logging.impl.NoOpLog;
import java.lang.reflect.Constructor;
import java.util.Hashtable;

public class LogSource
{
    protected static Hashtable logs;
    protected static boolean log4jIsAvailable;
    protected static boolean jdk14IsAvailable;
    protected static Constructor logImplctor;
    
    static {
        LogSource.logs = new Hashtable();
        LogSource.log4jIsAvailable = false;
        LogSource.jdk14IsAvailable = false;
        LogSource.logImplctor = null;
        try {
            if (Class.forName("org.apache.log4j.Logger") != null) {
                LogSource.log4jIsAvailable = true;
            }
            else {
                LogSource.log4jIsAvailable = false;
            }
        }
        catch (final Throwable t) {
            LogSource.log4jIsAvailable = false;
        }
        try {
            if (Class.forName("java.util.logging.Logger") != null && Class.forName("org.apache.commons.logging.impl.Jdk14Logger") != null) {
                LogSource.jdk14IsAvailable = true;
            }
            else {
                LogSource.jdk14IsAvailable = false;
            }
        }
        catch (final Throwable t2) {
            LogSource.jdk14IsAvailable = false;
        }
        String name = null;
        try {
            name = System.getProperty("org.apache.commons.logging.log");
            if (name == null) {
                name = System.getProperty("org.apache.commons.logging.Log");
            }
        }
        catch (final Throwable t3) {}
        if (name != null) {
            try {
                setLogImplementation(name);
                return;
            }
            catch (final Throwable t4) {
                try {
                    setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
                }
                catch (final Throwable t5) {}
            }
        }
        try {
            if (LogSource.log4jIsAvailable) {
                setLogImplementation("org.apache.commons.logging.impl.Log4JLogger");
            }
            else if (LogSource.jdk14IsAvailable) {
                setLogImplementation("org.apache.commons.logging.impl.Jdk14Logger");
            }
            else {
                setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
            }
        }
        catch (final Throwable t6) {
            try {
                setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
            }
            catch (final Throwable t7) {}
        }
    }
    
    private LogSource() {
    }
    
    public static Log getInstance(final Class clazz) {
        return getInstance(clazz.getName());
    }
    
    public static Log getInstance(final String name) {
        Log log = LogSource.logs.get(name);
        if (log == null) {
            log = makeNewLogInstance(name);
            LogSource.logs.put(name, log);
        }
        return log;
    }
    
    public static String[] getLogNames() {
        return (String[])LogSource.logs.keySet().toArray(new String[LogSource.logs.size()]);
    }
    
    public static Log makeNewLogInstance(final String name) {
        Log log = null;
        try {
            final Object[] args = { name };
            log = LogSource.logImplctor.newInstance(args);
        }
        catch (final Throwable t) {
            log = null;
        }
        if (log == null) {
            log = new NoOpLog(name);
        }
        return log;
    }
    
    public static void setLogImplementation(final Class logclass) throws LinkageError, ExceptionInInitializerError, NoSuchMethodException, SecurityException {
        final Class[] argtypes = { "".getClass() };
        LogSource.logImplctor = logclass.getConstructor((Class[])argtypes);
    }
    
    public static void setLogImplementation(final String classname) throws LinkageError, ExceptionInInitializerError, NoSuchMethodException, SecurityException, ClassNotFoundException {
        try {
            final Class logclass = Class.forName(classname);
            final Class[] argtypes = { "".getClass() };
            LogSource.logImplctor = logclass.getConstructor((Class[])argtypes);
        }
        catch (final Throwable t) {
            LogSource.logImplctor = null;
        }
    }
}
