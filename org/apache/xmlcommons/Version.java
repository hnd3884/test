package org.apache.xmlcommons;

public class Version
{
    public static String getVersion() {
        return getProduct() + " " + getVersionNum();
    }
    
    public static String getProduct() {
        return "XmlCommonsExternal";
    }
    
    public static String getVersionNum() {
        return "1.4.01";
    }
    
    public static void main(final String[] array) {
        System.out.println(getVersion());
    }
}
