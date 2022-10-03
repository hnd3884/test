package com.sun.corba.se.impl.util;

public final class PackagePrefixChecker
{
    private static final String PACKAGE_PREFIX = "org.omg.stub.";
    
    public static String packagePrefix() {
        return "org.omg.stub.";
    }
    
    public static String correctPackageName(final String s) {
        if (s == null) {
            return s;
        }
        if (hasOffendingPrefix(s)) {
            return "org.omg.stub." + s;
        }
        return s;
    }
    
    public static boolean isOffendingPackage(final String s) {
        return s != null && hasOffendingPrefix(s);
    }
    
    public static boolean hasOffendingPrefix(final String s) {
        return s.startsWith("java.") || s.equals("java") || s.startsWith("net.jini.") || s.equals("net.jini") || s.startsWith("jini.") || s.equals("jini") || s.startsWith("javax.") || s.equals("javax");
    }
    
    public static boolean hasBeenPrefixed(final String s) {
        return s.startsWith(packagePrefix());
    }
    
    public static String withoutPackagePrefix(final String s) {
        if (hasBeenPrefixed(s)) {
            return s.substring(packagePrefix().length());
        }
        return s;
    }
}
