package com.sun.corba.se.impl.activation;

import java.util.Hashtable;
import com.sun.corba.se.spi.activation.Server;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import java.util.Properties;
import org.omg.CORBA.ORB;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.util.Date;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;

public class ServerMain
{
    public static final int OK = 0;
    public static final int MAIN_CLASS_NOT_FOUND = 1;
    public static final int NO_MAIN_METHOD = 2;
    public static final int APPLICATION_ERROR = 3;
    public static final int UNKNOWN_ERROR = 4;
    public static final int NO_SERVER_ID = 5;
    public static final int REGISTRATION_FAILED = 6;
    private static final boolean debug = false;
    
    public static String printResult(final int n) {
        switch (n) {
            case 0: {
                return "Server terminated normally";
            }
            case 1: {
                return "main class not found";
            }
            case 2: {
                return "no main method";
            }
            case 3: {
                return "application error";
            }
            case 5: {
                return "server ID not defined";
            }
            case 6: {
                return "server registration failed";
            }
            default: {
                return "unknown error";
            }
        }
    }
    
    private void redirectIOStreams() {
        try {
            final String string = System.getProperty("com.sun.CORBA.activation.DbDir") + System.getProperty("file.separator") + "logs" + System.getProperty("file.separator");
            final File file = new File(string);
            final String property = System.getProperty("com.sun.CORBA.POA.ORBServerId");
            final FileOutputStream fileOutputStream = new FileOutputStream(string + property + ".out", true);
            final FileOutputStream fileOutputStream2 = new FileOutputStream(string + property + ".err", true);
            final PrintStream out = new PrintStream(fileOutputStream, true);
            final PrintStream err = new PrintStream(fileOutputStream2, true);
            System.setOut(out);
            System.setErr(err);
            logInformation("Server started");
        }
        catch (final Exception ex) {}
    }
    
    private static void writeLogMessage(final PrintStream printStream, final String s) {
        printStream.print("[" + new Date().toString() + "] " + s + "\n");
    }
    
    public static void logInformation(final String s) {
        writeLogMessage(System.out, "        " + s);
    }
    
    public static void logError(final String s) {
        writeLogMessage(System.out, "ERROR:  " + s);
        writeLogMessage(System.err, "ERROR:  " + s);
    }
    
    public static void logTerminal(final String s, final int n) {
        if (n == 0) {
            writeLogMessage(System.out, "        " + s);
        }
        else {
            writeLogMessage(System.out, "FATAL:  " + printResult(n) + ": " + s);
            writeLogMessage(System.err, "FATAL:  " + printResult(n) + ": " + s);
        }
        System.exit(n);
    }
    
    private Method getMainMethod(final Class clazz) {
        final Class[] array = { String[].class };
        Method declaredMethod = null;
        try {
            declaredMethod = clazz.getDeclaredMethod("main", (Class[])array);
        }
        catch (final Exception ex) {
            logTerminal(ex.getMessage(), 2);
        }
        if (!this.isPublicStaticVoid(declaredMethod)) {
            logTerminal("", 2);
        }
        return declaredMethod;
    }
    
    private boolean isPublicStaticVoid(final Method method) {
        final int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
            logError(method.getName() + " is not public static");
            return false;
        }
        if (method.getExceptionTypes().length != 0) {
            logError(method.getName() + " declares exceptions");
            return false;
        }
        if (!method.getReturnType().equals(Void.TYPE)) {
            logError(method.getName() + " does not have a void return type");
            return false;
        }
        return true;
    }
    
    private Method getNamedMethod(final Class clazz, final String s) {
        final Class[] array = { ORB.class };
        Method declaredMethod;
        try {
            declaredMethod = clazz.getDeclaredMethod(s, (Class[])array);
        }
        catch (final Exception ex) {
            return null;
        }
        if (!this.isPublicStaticVoid(declaredMethod)) {
            return null;
        }
        return declaredMethod;
    }
    
    private void run(final String[] array) {
        try {
            this.redirectIOStreams();
            final String property = System.getProperty("com.sun.CORBA.POA.ORBServerName");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            Class<?> clazz;
            try {
                clazz = Class.forName(property);
            }
            catch (final ClassNotFoundException ex) {
                clazz = Class.forName(property, true, classLoader);
            }
            final Method mainMethod = this.getMainMethod(clazz);
            if (Boolean.getBoolean("com.sun.CORBA.activation.ORBServerVerify")) {
                if (mainMethod == null) {
                    logTerminal("", 2);
                }
                else {
                    logTerminal("", 0);
                }
            }
            this.registerCallback(clazz);
            mainMethod.invoke(null, array);
        }
        catch (final ClassNotFoundException ex2) {
            logTerminal("ClassNotFound exception: " + ex2.getMessage(), 1);
        }
        catch (final Exception ex3) {
            logTerminal("Exception: " + ex3.getMessage(), 3);
        }
    }
    
    public static void main(final String[] array) {
        new ServerMain().run(array);
    }
    
    private int getServerId() {
        final Integer integer = Integer.getInteger("com.sun.CORBA.POA.ORBServerId");
        if (integer == null) {
            logTerminal("", 5);
        }
        return integer;
    }
    
    private void registerCallback(final Class clazz) {
        final Method namedMethod = this.getNamedMethod(clazz, "install");
        final Method namedMethod2 = this.getNamedMethod(clazz, "uninstall");
        final Method namedMethod3 = this.getNamedMethod(clazz, "shutdown");
        final Properties properties = new Properties();
        ((Hashtable<String, String>)properties).put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
        ((Hashtable<String, String>)properties).put("com.sun.CORBA.POA.ORBActivated", "false");
        final ORB init = ORB.init((String[])null, properties);
        final ServerCallback serverCallback = new ServerCallback(init, namedMethod, namedMethod2, namedMethod3);
        final int serverId = this.getServerId();
        try {
            ActivatorHelper.narrow(init.resolve_initial_references("ServerActivator")).active(serverId, serverCallback);
        }
        catch (final Exception ex) {
            logTerminal("exception " + ex.getMessage(), 6);
        }
    }
}
