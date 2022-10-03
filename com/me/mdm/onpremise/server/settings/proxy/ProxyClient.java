package com.me.mdm.onpremise.server.settings.proxy;

import java.util.Hashtable;
import org.apache.commons.httpclient.Credentials;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import java.net.URL;
import javax.net.ssl.SSLContext;

class ProxyClient
{
    private final SSLContext sslContext;
    
    public ProxyClient(final SSLContext sslContext) {
        this.sslContext = sslContext;
    }
    
    public void connect(final String domain) throws Exception {
        final URL url = new URL(domain);
        if (url.getProtocol().equalsIgnoreCase("https")) {
            final ExtendedProtocolSocketFactory psf = new ExtendedProtocolSocketFactory(this.sslContext.getSocketFactory());
            int port = url.getPort();
            if (port == -1) {
                port = url.getDefaultPort();
            }
            final Protocol protocol = new Protocol("https", (SecureProtocolSocketFactory)psf, port);
            final HttpClient httpClient = new HttpClient();
            httpClient.getHostConfiguration().setHost(url.getHost(), port, protocol);
            this.setProxyDetails(domain, httpClient);
            final GetMethod getMethod = new GetMethod("/");
            httpClient.executeMethod((HttpMethod)getMethod);
        }
    }
    
    private void setProxyDetails(final String connectionURL, final HttpClient httpClient) throws Exception {
        final Properties proxyConf = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        if (proxyConf != null && proxyConf.size() > 0) {
            String proxyHost = null;
            String proxyPort = null;
            String proxyScript = null;
            if (proxyConf.containsKey("proxyScriptEna") && ((Hashtable<K, Object>)proxyConf).get("proxyScriptEna").toString().equals("1")) {
                proxyScript = ((Hashtable<K, String>)proxyConf).get("proxyScript");
            }
            else {
                proxyHost = ((Hashtable<K, String>)proxyConf).get("proxyHost");
                proxyPort = ((Hashtable<K, String>)proxyConf).get("proxyPort");
            }
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
            httpClient.getHostConfiguration().setProxy(proxyHost, Integer.parseInt(proxyPort));
            final String userName = ((Hashtable<K, String>)proxyConf).get("proxyUser");
            final String passPhrase = ((Hashtable<K, String>)proxyConf).get("proxyPass");
            if (userName != null && !userName.equalsIgnoreCase("") && passPhrase != null && !passPhrase.equalsIgnoreCase("")) {
                final Credentials credentials = (Credentials)new UsernamePasswordCredentials(userName, passPhrase);
                httpClient.getState().setCredentials(AuthScope.ANY, credentials);
                httpClient.getState().setProxyCredentials(AuthScope.ANY, credentials);
                httpClient.getParams().setAuthenticationPreemptive(true);
            }
        }
    }
}
