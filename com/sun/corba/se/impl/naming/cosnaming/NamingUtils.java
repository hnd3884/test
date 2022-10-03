package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CosNaming.NameComponent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.PrintStream;

public class NamingUtils
{
    public static boolean debug;
    public static PrintStream debugStream;
    public static PrintStream errStream;
    
    private NamingUtils() {
    }
    
    public static void dprint(final String s) {
        if (NamingUtils.debug && NamingUtils.debugStream != null) {
            NamingUtils.debugStream.println(s);
        }
    }
    
    public static void errprint(final String s) {
        if (NamingUtils.errStream != null) {
            NamingUtils.errStream.println(s);
        }
        else {
            System.err.println(s);
        }
    }
    
    public static void printException(final Exception ex) {
        if (NamingUtils.errStream != null) {
            ex.printStackTrace(NamingUtils.errStream);
        }
        else {
            ex.printStackTrace();
        }
    }
    
    public static void makeDebugStream(final File file) throws IOException {
        (NamingUtils.debugStream = new PrintStream(new DataOutputStream(new FileOutputStream(file)))).println("Debug Stream Enabled.");
    }
    
    public static void makeErrStream(final File file) throws IOException {
        if (NamingUtils.debug) {
            NamingUtils.errStream = new PrintStream(new DataOutputStream(new FileOutputStream(file)));
            dprint("Error stream setup completed.");
        }
    }
    
    static String getDirectoryStructuredName(final NameComponent[] array) {
        final StringBuffer sb = new StringBuffer("/");
        for (int i = 0; i < array.length; ++i) {
            sb.append(array[i].id + "." + array[i].kind);
        }
        return sb.toString();
    }
    
    static {
        NamingUtils.debug = false;
    }
}
