package com.me.devicemanagement.onpremise.server.downloadmgr;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.logging.Level;
import HTTPClient.HTTPConnection;
import javax.net.ssl.KeyManager;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;
import java.util.logging.Logger;

public class SSLUtil
{
    private static Logger logger;
    
    public static void initSSLFactory() {
        final TrustManager[] trustAllCerts = { new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                
                public boolean isClientTrusted(final X509Certificate[] certs) {
                    return true;
                }
                
                public boolean isServerTrusted(final X509Certificate[] certs) {
                    return true;
                }
                
                @Override
                public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
                }
                
                @Override
                public void checkServerTrusted(final X509Certificate[] certs, final String authtype) {
                }
            } };
        try {
            final SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HTTPConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (final Exception e) {
            SSLUtil.logger.log(Level.WARNING, "Error occurred during initializing the ssl factories : {0}", e);
        }
    }
    
    public static void removeEncodingModule() {
        try {
            final String encodingModule = System.getProperty("enable.content.encoding", null);
            if (encodingModule != null) {
                final boolean enableEncoding = Boolean.valueOf(encodingModule);
                if (!enableEncoding) {
                    SSLUtil.logger.log(Level.INFO, "\"enable.content.encoding\" java system property is false. Hence, removing default module : HTTPClient.ContentEncodingModule ");
                    HTTPConnection.removeDefaultModule((Class)Class.forName("HTTPClient.ContentEncodingModule"));
                }
            }
        }
        catch (final ClassNotFoundException e) {
            SSLUtil.logger.log(Level.WARNING, "Exception while removing module \"HTTPClient.ContentEncodingModule\":: ", e);
        }
    }
    
    public static void initSecurityProperties() {
        try {
            System.setProperty("java.protocol.handler.pkgs", "HTTPClient");
            final String os = System.getProperty("os.name");
            final InetAddress localHost = InetAddress.getLocalHost();
            System.setProperty("HTTPClient.host", localHost.getHostName());
            if (os.indexOf("Windows") != -1) {
                String domain = localHost.getCanonicalHostName();
                final int ind = domain.indexOf(46);
                if (ind != -1) {
                    domain = domain.substring(ind + 1);
                }
                System.setProperty("HTTPClient.hostDomain", domain);
            }
            else {
                System.setProperty("HTTPClient.hostDomain", "");
            }
        }
        catch (final UnknownHostException ex) {
            SSLUtil.logger.log(Level.WARNING, "UnknownHostException while initSecurityProperties", ex);
        }
    }
    
    static {
        SSLUtil.logger = Logger.getLogger("DownloadManager");
    }
}
