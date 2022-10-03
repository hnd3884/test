package com.adventnet.sym.server.mdm.android;

import java.util.Hashtable;
import java.io.IOException;
import sun.misc.BASE64Encoder;
import java.net.URL;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.HttpURLConnection;
import java.util.Properties;
import com.google.android.gcm.server.Sender;

public class ProxySender extends Sender
{
    Properties proxyDetails;
    
    public ProxySender(final String key, final Properties proxyProperties) {
        super(key);
        this.proxyDetails = null;
        this.proxyDetails = proxyProperties;
    }
    
    protected HttpURLConnection getConnection(final String url) throws IOException {
        final String proxyHost = ((Hashtable<K, String>)this.proxyDetails).get("proxyHost");
        final String proxyPort = ((Hashtable<K, String>)this.proxyDetails).get("proxyPort");
        final String proxyUser = ((Hashtable<K, String>)this.proxyDetails).get("proxyUser");
        final String proxyPass = ((Hashtable<K, String>)this.proxyDetails).get("proxyPass");
        HttpURLConnection conn;
        if (proxyHost != null && proxyPort != null) {
            final int portNumber = Integer.parseInt(proxyPort);
            final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, portNumber));
            conn = (HttpURLConnection)new URL(url).openConnection(proxy);
            if (proxyUser != null && proxyPass != null) {
                final String encodedUserPwd = new BASE64Encoder().encode((proxyUser + ":" + proxyPass).getBytes());
                conn.setRequestProperty("Proxy-Authorization", "Basic " + encodedUserPwd);
            }
        }
        else {
            conn = (HttpURLConnection)new URL(url).openConnection();
        }
        return conn;
    }
}
