package com.me.devicemanagement.framework.server.downloadmgr;

import java.util.Hashtable;
import HTTPClient.NVPair;
import HTTPClient.AuthorizationHandler;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.net.URL;
import HTTPClient.DefaultAuthHandler;
import HTTPClient.AuthorizationInfo;
import HTTPClient.AuthorizationPrompter;
import HTTPClient.NTLMAuthorizationHandler;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import javax.net.ssl.SSLSocketFactory;
import java.util.Properties;
import HTTPClient.HTTPConnection;
import HTTPClient.CookiePolicyHandler;
import HTTPClient.CookieModule;
import java.util.logging.Level;
import java.security.Security;
import java.security.Provider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class SSLUtil
{
    private static Logger logger;
    private static boolean isProviderSet;
    private static SSLUtil sslUtil;
    
    private SSLUtil() {
        ApiFactoryProvider.getUtilAccessAPI().initSSLUtil();
        initSecurityProvider();
        removeEncodingModule();
    }
    
    public static SSLUtil getInstance() {
        if (SSLUtil.sslUtil == null) {
            SSLUtil.sslUtil = new SSLUtil();
        }
        return SSLUtil.sslUtil;
    }
    
    private static void initSecurityProvider() {
        if (!SSLUtil.isProviderSet) {
            final String securityProviderClassName = "cryptix.jce.provider.CryptixCrypto";
            try {
                final Class securityProvider = Class.forName(securityProviderClassName);
                Security.addProvider(securityProvider.newInstance());
                SSLUtil.logger.log(Level.INFO, "Provider :: {0}", Security.getProvider(securityProvider.newInstance().getName()));
            }
            catch (final Exception e) {
                SSLUtil.logger.log(Level.WARNING, "Error occurred during initializing the security providers : {0}", e);
            }
            SSLUtil.isProviderSet = true;
            CookieModule.setCookiePolicyHandler((CookiePolicyHandler)new DCCookiePolicyHandler());
            ((Hashtable<String, String>)System.getProperties()).put("java.protocol.handler.pkgs", "HTTPClient");
        }
    }
    
    private static void removeEncodingModule() {
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
    
    public HTTPConnection getConnection(final String connectionURL, final Properties proxyConf) throws Exception {
        return this.getConnection(connectionURL, proxyConf, HTTPConnection.getDefaultSSLSocketFactory());
    }
    
    public HTTPConnection getConnection(final String connectionURL, final Properties proxyConf, final SSLSocketFactory sslSocketFactory) throws Exception {
        String proxyHost = null;
        String proxyPort = null;
        String userName = null;
        String password = null;
        String proxyScript = null;
        if (proxyConf != null) {
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            final int proxyType = DownloadManager.proxyType;
            if (proxyConf.containsKey("proxyScriptEna") && ((Hashtable<K, Object>)proxyConf).get("proxyScriptEna").toString().equals("1")) {
                proxyScript = ((Hashtable<K, String>)proxyConf).get("proxyScript");
            }
            else {
                proxyHost = ((Hashtable<K, String>)proxyConf).get("proxyHost");
                proxyPort = ((Hashtable<K, String>)proxyConf).get("proxyPort");
            }
            userName = ((Hashtable<K, String>)proxyConf).get("proxyUser");
            password = ((Hashtable<K, String>)proxyConf).get("proxyPass");
            if (proxyScript != null) {
                final PacProxySelector pacProxySelector = new PacProxySelector((PacScriptSource)new UrlPacScriptSource(proxyScript));
                final List<Proxy> proxyList = pacProxySelector.select(new URI(connectionURL));
                if (proxyList != null && !proxyList.isEmpty()) {
                    for (final Proxy proxy : proxyList) {
                        final SocketAddress address = proxy.address();
                        if (address != null) {
                            proxyHost = ((InetSocketAddress)address).getHostName();
                            proxyPort = Integer.toString(((InetSocketAddress)address).getPort());
                        }
                    }
                }
            }
        }
        final AuthorizationHandler ntlm = (AuthorizationHandler)new NTLMAuthorizationHandler();
        NTLMAuthorizationHandler.setAuthorizationPrompter((AuthorizationPrompter)new DCAuthorizationPrompter(userName, password));
        AuthorizationInfo.setAuthHandler(ntlm);
        DefaultAuthHandler.setAuthorizationPrompter((AuthorizationPrompter)new DCAuthorizationPrompter(userName, password));
        final URL url = new URL(connectionURL);
        final HTTPConnection connection = new HTTPConnection(url);
        connection.setSSLSocketFactory(sslSocketFactory);
        connection.setDefaultHeaders(this.getDefaultHeaders(connection));
        if (proxyHost != null && proxyPort != null) {
            SSLUtil.logger.log(Level.INFO, "Connection {0},{1},{2}", new Object[] { connection, proxyHost, proxyPort });
            if (proxyHost != null) {
                connection.setCurrentProxy(proxyHost, (int)new Integer(proxyPort));
            }
        }
        return connection;
    }
    
    private NVPair[] getDefaultHeaders(final HTTPConnection connection) {
        NVPair[] defaultHeaders = connection.getDefaultHeaders();
        final NVPair clearCache = new NVPair("Cache-Control", "no-cache");
        final NVPair acceptHeader = new NVPair("Accept", "*/*");
        if (defaultHeaders != null) {
            if (defaultHeaders.length > 0) {
                defaultHeaders[defaultHeaders.length + 1] = clearCache;
                defaultHeaders[defaultHeaders.length + 1] = acceptHeader;
            }
            else {
                defaultHeaders = new NVPair[] { clearCache, acceptHeader };
            }
        }
        return defaultHeaders;
    }
    
    static {
        SSLUtil.logger = Logger.getLogger("DownloadManager");
        SSLUtil.isProviderSet = false;
        SSLUtil.sslUtil = null;
    }
}
