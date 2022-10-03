package com.sun.corba.se.impl.util;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.corba.se.impl.orbutil.GetPropertyAction;

public class JDKBridge
{
    private static final String LOCAL_CODEBASE_KEY = "java.rmi.server.codebase";
    private static final String USE_CODEBASE_ONLY_KEY = "java.rmi.server.useCodebaseOnly";
    private static String localCodebase;
    private static boolean useCodebaseOnly;
    
    public static String getLocalCodebase() {
        return JDKBridge.localCodebase;
    }
    
    public static boolean useCodebaseOnly() {
        return JDKBridge.useCodebaseOnly;
    }
    
    public static Class loadClass(final String s, final String s2, final ClassLoader classLoader) throws ClassNotFoundException {
        if (classLoader == null) {
            return loadClassM(s, s2, JDKBridge.useCodebaseOnly);
        }
        try {
            return loadClassM(s, s2, JDKBridge.useCodebaseOnly);
        }
        catch (final ClassNotFoundException ex) {
            return classLoader.loadClass(s);
        }
    }
    
    public static Class loadClass(final String s, final String s2) throws ClassNotFoundException {
        return loadClass(s, s2, null);
    }
    
    public static Class loadClass(final String s) throws ClassNotFoundException {
        return loadClass(s, null, null);
    }
    
    public static final void main(final String[] array) {
        System.out.println("1.2 VM");
    }
    
    public static synchronized void setCodebaseProperties() {
        final String localCodebase = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.rmi.server.codebase"));
        if (localCodebase != null && localCodebase.trim().length() > 0) {
            JDKBridge.localCodebase = localCodebase;
        }
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.rmi.server.useCodebaseOnly"));
        if (s != null && s.trim().length() > 0) {
            JDKBridge.useCodebaseOnly = Boolean.valueOf(s);
        }
    }
    
    public static synchronized void setLocalCodebase(final String localCodebase) {
        JDKBridge.localCodebase = localCodebase;
    }
    
    private static Class loadClassM(String string, final String s, final boolean b) throws ClassNotFoundException {
        try {
            return JDKClassLoader.loadClass(null, string);
        }
        catch (final ClassNotFoundException ex) {
            try {
                if (!b && s != null) {
                    return RMIClassLoader.loadClass(s, string);
                }
                return RMIClassLoader.loadClass(string);
            }
            catch (final MalformedURLException ex2) {
                string = string + ": " + ex2.toString();
                throw new ClassNotFoundException(string);
            }
        }
    }
    
    static {
        JDKBridge.localCodebase = null;
        setCodebaseProperties();
    }
}
