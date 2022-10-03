package com.me.mdm.server.ios.pushNotification;

import java.util.Hashtable;
import sun.misc.BASE64Encoder;
import java.net.URL;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.io.IOException;
import java.util.logging.Level;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.HttpURLConnection;
import java.util.Properties;
import java.util.logging.Logger;
import com.google.android.gcm.server.Sender;

public class MDMApnsSender extends Sender
{
    private static final String MUTABLE_CONTENT_PARAM = "mutable_content";
    private Logger logger;
    Properties proxyDetails;
    
    public MDMApnsSender(final String key, final Properties proxyProperties) {
        super(key);
        this.logger = Logger.getLogger(MDMApnsSender.class.getName());
        this.proxyDetails = null;
        this.proxyDetails = proxyProperties;
    }
    
    protected HttpURLConnection post(final String url, final String contentType, String body) throws IOException {
        final JSONParser parser = new JSONParser();
        try {
            if (!body.isEmpty()) {
                final JSONObject modifiedJSON = (JSONObject)parser.parse(body);
                if (!modifiedJSON.isEmpty()) {
                    modifiedJSON.put((Object)"mutable_content", (Object)true);
                    body = JSONValue.toJSONString((Object)modifiedJSON);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error occurred while parsing iOS Notification body: ", ex);
        }
        return super.post(url, contentType, body);
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
