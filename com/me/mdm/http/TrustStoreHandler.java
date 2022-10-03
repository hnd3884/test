package com.me.mdm.http;

import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.security.cert.CertificateFactory;
import java.io.InputStream;
import javax.net.ssl.TrustManager;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.HttpsURLConnection;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.net.URL;
import java.net.Proxy;

public class TrustStoreHandler
{
    private Proxy proxy;
    private String userName;
    private String password;
    
    public TrustStoreHandler() {
        this.proxy = null;
        this.userName = null;
        this.password = null;
    }
    
    public HttpURLConnection getHttpURLConnection(final URL url, final ArrayList<String> certificatesPath) throws Exception {
        final SSLSocketFactory sslSocketFactory = getSSLSocketFactory(certificatesPath);
        final Proxy proxy = this.getProxyDetails(url.getHost());
        if (!MDMStringUtils.isEmpty(this.userName)) {
            final Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(TrustStoreHandler.this.userName, TrustStoreHandler.this.password.toCharArray());
                }
            };
        }
        final HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection(proxy);
        httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
        return httpsURLConnection;
    }
    
    public static SSLSocketFactory getSSLSocketFactory(final ArrayList<String> certificatesPath) throws Exception {
        final KeyStore keyStore = getTrustStore(certificatesPath);
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(new KeyManager[0], trustManagers, null);
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        return sslSocketFactory;
    }
    
    private static KeyStore getTrustStore(final ArrayList<String> certificatesPath) throws Exception {
        final KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        for (final String certFilePath : certificatesPath) {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final InputStream certstream = getFullStream(certFilePath);
            final Certificate certificate = cf.generateCertificate(certstream);
            keyStore.setCertificateEntry(getAlias(certFilePath), certificate);
        }
        return keyStore;
    }
    
    private static InputStream getFullStream(final String fname) throws IOException {
        FileInputStream fileInputStream = null;
        DataInputStream dataInputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            fileInputStream = new FileInputStream(fname);
            dataInputStream = new DataInputStream(fileInputStream);
            final byte[] bytes = new byte[dataInputStream.available()];
            dataInputStream.readFully(bytes);
            byteArrayInputStream = new ByteArrayInputStream(bytes);
        }
        catch (final IOException e) {
            throw e;
        }
        finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (dataInputStream != null) {
                dataInputStream.close();
            }
        }
        return byteArrayInputStream;
    }
    
    private static String getAlias(final String certificatefileName) {
        final File file = new File(certificatefileName);
        final String fileName = removeFileExtension(file.getName());
        return fileName;
    }
    
    private static String removeFileExtension(final String fileName) {
        final int extensionPos = fileName.lastIndexOf(46);
        final int seperatorPos = fileName.lastIndexOf(File.separator);
        if (extensionPos > seperatorPos) {
            return fileName.substring(0, extensionPos);
        }
        return null;
    }
    
    private Proxy getProxyDetails(final String connectionURL) throws Exception {
        final Properties proxyConf = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        String proxyHost = null;
        String proxyPort = null;
        String proxyScript = null;
        if (proxyConf != null) {
            if (proxyConf.containsKey("proxyScriptEna") && ((Hashtable<K, Object>)proxyConf).get("proxyScriptEna").toString().equals("1")) {
                proxyScript = ((Hashtable<K, String>)proxyConf).get("proxyScript");
            }
            else {
                proxyHost = ((Hashtable<K, String>)proxyConf).get("proxyHost");
                proxyPort = ((Hashtable<K, String>)proxyConf).get("proxyPort");
            }
            this.userName = ((Hashtable<K, String>)proxyConf).get("proxyUser");
            this.password = ((Hashtable<K, String>)proxyConf).get("proxyPass");
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
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.valueOf(proxyPort)));
    }
}
