package javapns.communication;

import sun.misc.BASE64Encoder;

public class ProxyManager
{
    private static final String LOCAL_PROXY_HOST_PROPERTY = "javapns.communication.proxyHost";
    private static final String LOCAL_PROXY_PORT_PROPERTY = "javapns.communication.proxyPort";
    private static final String LOCAL_PROXY_AUTHORIZATION_PROPERTY = "javapns.communication.proxyAuthorization";
    private static final String JVM_PROXY_HOST_PROPERTY = "https.proxyHost";
    private static final String JVM_PROXY_PORT_PROPERTY = "https.proxyPort";
    private static final String JVM_PROXY_AUTHORIZATION_PROPERTY = "https.proxyAuthorization";
    
    private ProxyManager() {
    }
    
    public static void setProxy(final String host, final String port) {
        System.setProperty("javapns.communication.proxyHost", host);
        System.setProperty("javapns.communication.proxyPort", port);
    }
    
    public static void clearProxy() {
        System.clearProperty("javapns.communication.proxyHost");
        System.clearProperty("javapns.communication.proxyPort");
    }
    
    public static void setProxyBasicAuthorization(final String username, final String password) {
        setProxyAuthorization(encodeProxyAuthorization(username, password));
    }
    
    public static void clearProxyAuthorization() {
        System.clearProperty("javapns.communication.proxyAuthorization");
    }
    
    public static void setProxyAuthorization(final String authorization) {
        System.setProperty("javapns.communication.proxyAuthorization", authorization);
    }
    
    public static String encodeProxyAuthorization(final String username, final String password) {
        final BASE64Encoder encoder = new BASE64Encoder();
        final String pwd = "USER:PASSWORD";
        final String encodedUserPwd = encoder.encode(pwd.getBytes());
        final String authorization = "Basic " + encodedUserPwd;
        return authorization;
    }
    
    public static void setJVMProxy(final String host, final String port) {
        System.setProperty("https.proxyHost", host);
        System.setProperty("https.proxyPort", port);
    }
    
    static String getProxyHost(final AppleServer server) {
        String host = (server != null) ? server.getProxyHost() : null;
        if (host != null && host.length() > 0) {
            return host;
        }
        host = System.getProperty("javapns.communication.proxyHost");
        if (host != null && host.length() > 0) {
            return host;
        }
        host = System.getProperty("https.proxyHost");
        if (host != null && host.length() > 0) {
            return host;
        }
        return null;
    }
    
    public static String getProxyAuthorization(final AppleServer server) {
        String authorization = (server != null) ? server.getProxyAuthorization() : null;
        if (authorization != null && authorization.length() > 0) {
            return authorization;
        }
        authorization = System.getProperty("javapns.communication.proxyHost");
        if (authorization != null && authorization.length() > 0) {
            return authorization;
        }
        authorization = System.getProperty("https.proxyHost");
        if (authorization != null && authorization.length() > 0) {
            return authorization;
        }
        return null;
    }
    
    static int getProxyPort(final AppleServer server) {
        String host = (server != null) ? server.getProxyHost() : null;
        if (host != null && host.length() > 0) {
            return server.getProxyPort();
        }
        host = System.getProperty("javapns.communication.proxyHost");
        if (host != null && host.length() > 0) {
            return Integer.parseInt(System.getProperty("javapns.communication.proxyPort"));
        }
        host = System.getProperty("https.proxyHost");
        if (host != null && host.length() > 0) {
            return Integer.parseInt(System.getProperty("https.proxyPort"));
        }
        return 0;
    }
    
    static boolean isUsingProxy(final AppleServer server) {
        final String proxyHost = getProxyHost(server);
        return proxyHost != null && proxyHost.length() > 0;
    }
}
