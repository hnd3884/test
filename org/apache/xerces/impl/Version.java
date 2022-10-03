package org.apache.xerces.impl;

public class Version
{
    public static String fVersion;
    private static final String fImmutableVersion = "Xerces-J 2.12.1-xml-schema-1.1";
    
    public static String getVersion() {
        return "Xerces-J 2.12.1-xml-schema-1.1";
    }
    
    public static void main(final String[] array) {
        System.out.println(Version.fVersion);
    }
    
    static {
        Version.fVersion = "Xerces-J 2.12.1-xml-schema-1.1";
    }
}
