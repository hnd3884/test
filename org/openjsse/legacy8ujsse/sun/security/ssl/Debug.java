package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.io.PrintStream;
import java.util.Locale;

public class Debug
{
    private String prefix;
    private static String args;
    
    public static void Help() {
        System.err.println();
        System.err.println("all            turn on all debugging");
        System.err.println("ssl            turn on ssl debugging");
        System.err.println();
        System.err.println("The following can be used with ssl:");
        System.err.println("\trecord       enable per-record tracing");
        System.err.println("\thandshake    print each handshake message");
        System.err.println("\tkeygen       print key generation data");
        System.err.println("\tsession      print session activity");
        System.err.println("\tdefaultctx   print default SSL initialization");
        System.err.println("\tsslctx       print SSLContext tracing");
        System.err.println("\tsessioncache print session cache tracing");
        System.err.println("\tkeymanager   print key manager tracing");
        System.err.println("\ttrustmanager print trust manager tracing");
        System.err.println("\tpluggability print pluggability tracing");
        System.err.println();
        System.err.println("\thandshake debugging can be widened with:");
        System.err.println("\tdata         hex dump of each handshake message");
        System.err.println("\tverbose      verbose handshake message printing");
        System.err.println();
        System.err.println("\trecord debugging can be widened with:");
        System.err.println("\tplaintext    hex dump of record plaintext");
        System.err.println("\tpacket       print raw SSL/TLS packets");
        System.err.println();
        System.exit(0);
    }
    
    public static Debug getInstance(final String option) {
        return getInstance(option, option);
    }
    
    public static Debug getInstance(final String option, final String prefix) {
        if (isOn(option)) {
            final Debug d = new Debug();
            d.prefix = prefix;
            return d;
        }
        return null;
    }
    
    public static boolean isOn(String option) {
        if (Debug.args == null) {
            return false;
        }
        int n = 0;
        option = option.toLowerCase(Locale.ENGLISH);
        return Debug.args.indexOf("all") != -1 || ((n = Debug.args.indexOf("ssl")) != -1 && Debug.args.indexOf("sslctx", n) == -1 && !option.equals("data") && !option.equals("packet") && !option.equals("plaintext")) || Debug.args.indexOf(option) != -1;
    }
    
    public void println(final String message) {
        System.err.println(this.prefix + ": " + message);
    }
    
    public void println() {
        System.err.println(this.prefix + ":");
    }
    
    public static void println(final String prefix, final String message) {
        System.err.println(prefix + ": " + message);
    }
    
    public static void println(final PrintStream s, final String name, final byte[] data) {
        s.print(name + ":  { ");
        if (data == null) {
            s.print("null");
        }
        else {
            for (int i = 0; i < data.length; ++i) {
                if (i != 0) {
                    s.print(", ");
                }
                s.print(data[i] & 0xFF);
            }
        }
        s.println(" }");
    }
    
    static boolean getBooleanProperty(final String propName, final boolean defaultValue) {
        final String b = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(propName));
        if (b == null) {
            return defaultValue;
        }
        if (b.equalsIgnoreCase("false")) {
            return false;
        }
        if (b.equalsIgnoreCase("true")) {
            return true;
        }
        throw new RuntimeException("Value of " + propName + " must either be 'true' or 'false'");
    }
    
    static String toString(final byte[] b) {
        return sun.security.util.Debug.toString(b);
    }
    
    static {
        Debug.args = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("javax.net.debug", ""));
        Debug.args = Debug.args.toLowerCase(Locale.ENGLISH);
        if (Debug.args.equals("help")) {
            Help();
        }
    }
}
