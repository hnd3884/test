package com.sun.org.apache.xerces.internal.impl;

public class Version
{
    @Deprecated
    public static final String fVersion;
    private static final String fImmutableVersion = "Xerces-J 2.7.1";
    
    public static String getVersion() {
        return "Xerces-J 2.7.1";
    }
    
    public static void main(final String[] argv) {
        System.out.println(Version.fVersion);
    }
    
    static {
        fVersion = getVersion();
    }
}
