package sun.security.util;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.regex.Matcher;
import java.util.Locale;
import java.util.regex.Pattern;
import java.math.BigInteger;

public class Debug
{
    private String prefix;
    private static String args;
    private static final char[] hexDigits;
    
    public static void Help() {
        System.err.println();
        System.err.println("all           turn on all debugging");
        System.err.println("access        print all checkPermission results");
        System.err.println("certpath      PKIX CertPathBuilder and");
        System.err.println("              CertPathValidator debugging");
        System.err.println("combiner      SubjectDomainCombiner debugging");
        System.err.println("gssloginconfig");
        System.err.println("              GSS LoginConfigImpl debugging");
        System.err.println("configfile    JAAS ConfigFile loading");
        System.err.println("configparser  JAAS ConfigFile parsing");
        System.err.println("jar           jar verification");
        System.err.println("logincontext  login context results");
        System.err.println("jca           JCA engine class debugging");
        System.err.println("policy        loading and granting");
        System.err.println("provider      security provider debugging");
        System.err.println("pkcs11        PKCS11 session manager debugging");
        System.err.println("pkcs11keystore");
        System.err.println("              PKCS11 KeyStore debugging");
        System.err.println("sunpkcs11     SunPKCS11 provider debugging");
        System.err.println("scl           permissions SecureClassLoader assigns");
        System.err.println("ts            timestamping");
        System.err.println();
        System.err.println("The following can be used with access:");
        System.err.println();
        System.err.println("stack         include stack trace");
        System.err.println("domain        dump all domains in context");
        System.err.println("failure       before throwing exception, dump stack");
        System.err.println("              and domain that didn't have permission");
        System.err.println();
        System.err.println("The following can be used with stack and domain:");
        System.err.println();
        System.err.println("permission=<classname>");
        System.err.println("              only dump output if specified permission");
        System.err.println("              is being checked");
        System.err.println("codebase=<URL>");
        System.err.println("              only dump output if specified codebase");
        System.err.println("              is being checked");
        System.err.println();
        System.err.println("The following can be used with provider:");
        System.err.println();
        System.err.println("engine=<engines>");
        System.err.println("              only dump output for the specified list");
        System.err.println("              of JCA engines. Supported values:");
        System.err.println("              Cipher, KeyAgreement, KeyGenerator,");
        System.err.println("              KeyPairGenerator, KeyStore, Mac,");
        System.err.println("              MessageDigest, SecureRandom, Signature.");
        System.err.println();
        System.err.println("Note: Separate multiple options with a comma");
        System.exit(0);
    }
    
    public static Debug getInstance(final String s) {
        return getInstance(s, s);
    }
    
    public static Debug getInstance(final String s, final String prefix) {
        if (isOn(s)) {
            final Debug debug = new Debug();
            debug.prefix = prefix;
            return debug;
        }
        return null;
    }
    
    public static boolean isOn(final String s) {
        return Debug.args != null && (Debug.args.indexOf("all") != -1 || Debug.args.indexOf(s) != -1);
    }
    
    public void println(final String s) {
        System.err.println(this.prefix + ": " + s);
    }
    
    public void println() {
        System.err.println(this.prefix + ":");
    }
    
    public static void println(final String s, final String s2) {
        System.err.println(s + ": " + s2);
    }
    
    public static String toHexString(final BigInteger bigInteger) {
        String s = bigInteger.toString(16);
        final StringBuffer sb = new StringBuffer(s.length() * 2);
        if (s.startsWith("-")) {
            sb.append("   -");
            s = s.substring(1);
        }
        else {
            sb.append("    ");
        }
        if (s.length() % 2 != 0) {
            s = "0" + s;
        }
        int i = 0;
        while (i < s.length()) {
            sb.append(s.substring(i, i + 2));
            i += 2;
            if (i != s.length()) {
                if (i % 64 == 0) {
                    sb.append("\n    ");
                }
                else {
                    if (i % 8 != 0) {
                        continue;
                    }
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }
    
    private static String marshal(final String s) {
        if (s != null) {
            final StringBuffer sb = new StringBuffer();
            final StringBuffer sb2 = new StringBuffer(s);
            final String s2 = "[Pp][Ee][Rr][Mm][Ii][Ss][Ss][Ii][Oo][Nn]=";
            final String s3 = "permission=";
            final Matcher matcher = Pattern.compile(s2 + "[a-zA-Z_$][a-zA-Z0-9_$]*([.][a-zA-Z_$][a-zA-Z0-9_$]*)*").matcher(sb2);
            final StringBuffer sb3 = new StringBuffer();
            while (matcher.find()) {
                sb.append(matcher.group().replaceFirst(s2, s3));
                sb.append("  ");
                matcher.appendReplacement(sb3, "");
            }
            matcher.appendTail(sb3);
            final StringBuffer sb4 = sb3;
            final String s4 = "[Cc][Oo][Dd][Ee][Bb][Aa][Ss][Ee]=";
            final String s5 = "codebase=";
            final Matcher matcher2 = Pattern.compile(s4 + "[^, ;]*").matcher(sb4);
            final StringBuffer sb5 = new StringBuffer();
            while (matcher2.find()) {
                sb.append(matcher2.group().replaceFirst(s4, s5));
                sb.append("  ");
                matcher2.appendReplacement(sb5, "");
            }
            matcher2.appendTail(sb5);
            sb.append(sb5.toString().toLowerCase(Locale.ENGLISH));
            return sb.toString();
        }
        return null;
    }
    
    public static String toString(final byte[] array) {
        if (array == null) {
            return "(null)";
        }
        final StringBuilder sb = new StringBuilder(array.length * 3);
        for (int i = 0; i < array.length; ++i) {
            final int n = array[i] & 0xFF;
            if (i != 0) {
                sb.append(':');
            }
            sb.append(Debug.hexDigits[n >>> 4]);
            sb.append(Debug.hexDigits[n & 0xF]);
        }
        return sb.toString();
    }
    
    static {
        Debug.args = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.security.debug"));
        final String args = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.security.auth.debug"));
        if (Debug.args == null) {
            Debug.args = args;
        }
        else if (args != null) {
            Debug.args = Debug.args + "," + args;
        }
        if (Debug.args != null) {
            Debug.args = marshal(Debug.args);
            if (Debug.args.equals("help")) {
                Help();
            }
        }
        hexDigits = "0123456789abcdef".toCharArray();
    }
}
