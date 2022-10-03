package com.adventnet.client.properties;

public class ClientProperties
{
    public static String jsVersion;
    public static String cssVersion;
    public static String cssHost;
    public static String jsHost;
    public static boolean useApache;
    public static boolean useCompression;
    public static boolean prependversion;
    
    static {
        ClientProperties.jsVersion = System.getProperty("static.js.version");
        ClientProperties.cssVersion = System.getProperty("static.css.version");
        ClientProperties.cssHost = "";
        ClientProperties.jsHost = "";
        ClientProperties.useApache = Boolean.getBoolean("use.apache");
        ClientProperties.useCompression = Boolean.getBoolean("use.compression");
        ClientProperties.prependversion = Boolean.getBoolean("prependversion");
        ClientProperties.jsVersion = ((ClientProperties.jsVersion != null && ClientProperties.jsVersion.length() > 0) ? (ClientProperties.jsVersion + "/") : "");
        ClientProperties.cssVersion = ((ClientProperties.cssVersion != null && ClientProperties.cssVersion.length() > 0) ? (ClientProperties.cssVersion + "/") : "");
        if (ClientProperties.useApache) {
            ClientProperties.cssHost = System.getProperty("static.css.host");
            ClientProperties.jsHost = System.getProperty("static.js.host");
        }
        else {
            ClientProperties.cssHost = System.getProperty("rootcontext");
            ClientProperties.jsHost = System.getProperty("rootcontext");
        }
    }
}
