package com.me.devicemanagement.onpremise.server.creator;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.net.Authenticator;
import com.me.tools.zcutil.ProxyAuthenticator;
import javax.net.ssl.HttpsURLConnection;
import sun.misc.BASE64Encoder;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;

public class CreatorDataPost
{
    private static Logger logger;
    public static final String JSONAPI_KEY_DATA = "data";
    public static final String JSONAPI_KEY_CODE = "code";
    public static final int HTTP_SUCCESS_CODE = 200;
    public static final int CREATOR_POST_SUCCESS_CODE = 3000;
    
    public static JSONObject getCreatorResponse(final String url, final Properties proxyDetails, final JSONObject inputData) {
        DataOutputStream wr = null;
        HttpsURLConnection httpsConn = null;
        try {
            final URL httpUrl = new URL(url);
            if (isValidProxy(proxyDetails)) {
                final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyDetails.getProperty("host"), Integer.parseInt(proxyDetails.getProperty("port"))));
                final BASE64Encoder encoder = new BASE64Encoder();
                final String encodedUserPwd = encoder.encode((proxyDetails.getProperty("username") + ":" + proxyDetails.getProperty("password")).getBytes());
                httpsConn = (HttpsURLConnection)httpUrl.openConnection(proxy);
                httpsConn.setRequestProperty("Proxy-Authorization", "Basic " + encodedUserPwd);
                Authenticator.setDefault((Authenticator)new ProxyAuthenticator(proxyDetails.getProperty("username"), proxyDetails.getProperty("password")));
            }
            else {
                httpsConn = (HttpsURLConnection)httpUrl.openConnection();
            }
            httpsConn.setConnectTimeout(30000);
            httpsConn.setDoInput(true);
            httpsConn.setDoOutput(true);
            httpsConn.setUseCaches(false);
            httpsConn.setRequestMethod("POST");
            httpsConn.setRequestProperty("Accept", "application/json");
            httpsConn.addRequestProperty("Connection", "close");
            httpsConn.addRequestProperty("Content-Encoding", "gzip");
            httpsConn.setRequestProperty("Content-Type", "application/json");
            httpsConn.addRequestProperty("Content-Length", String.valueOf(inputData.toString().length()));
            httpsConn.connect();
            wr = new DataOutputStream(httpsConn.getOutputStream());
            wr.writeBytes(inputData.toString());
            final int responseCode = httpsConn.getResponseCode();
            final StringBuilder builder = new StringBuilder();
            try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsConn.getInputStream(), "utf-8"))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
            }
            if (responseCode == 200) {
                return new JSONObject(builder.toString());
            }
            return null;
        }
        catch (final Exception ex) {
            CreatorDataPost.logger.log(Level.SEVERE, "updateCreatorForm():- Exception is :", ex);
            try {
                if (wr != null) {
                    wr.flush();
                    wr.close();
                }
                if (httpsConn != null) {
                    httpsConn.disconnect();
                }
            }
            catch (final Exception ex) {
                CreatorDataPost.logger.log(Level.SEVERE, "updateCreatorForm():- Exception is :", ex);
            }
        }
        finally {
            try {
                if (wr != null) {
                    wr.flush();
                    wr.close();
                }
                if (httpsConn != null) {
                    httpsConn.disconnect();
                }
            }
            catch (final Exception ex2) {
                CreatorDataPost.logger.log(Level.SEVERE, "updateCreatorForm():- Exception is :", ex2);
            }
        }
        return null;
    }
    
    private static boolean isValidProxy(final Properties proxyProp) {
        try {
            if (proxyProp == null || proxyProp.isEmpty()) {
                return false;
            }
            if (isValid(proxyProp.getProperty("host")) && isValid(proxyProp.getProperty("port")) && isValid(proxyProp.getProperty("username")) && isValid(proxyProp.getProperty("password"))) {
                final Integer port = Integer.parseInt(proxyProp.getProperty("port"));
                return port >= 0 && port <= 65535;
            }
            return false;
        }
        catch (final Exception exp) {
            CreatorDataPost.logger.log(Level.SEVERE, "isValidProxy():- Exception is :", exp);
            return false;
        }
    }
    
    private static boolean isValid(final String strValue) {
        return strValue != null && !strValue.trim().isEmpty();
    }
    
    public static Properties getProxyProps(final String url) {
        Properties proxyProps = new Properties();
        try {
            final Properties proxyProperties = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            final int proxyType = DownloadManager.proxyType;
            if (proxyType == 4) {
                final Properties pocProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(url, proxyProperties);
                proxyProps = new Properties();
                ((Hashtable<String, Object>)proxyProps).put("host", ((Hashtable<K, Object>)pocProps).get("proxyHost"));
                ((Hashtable<String, Object>)proxyProps).put("port", ((Hashtable<K, Object>)pocProps).get("proxyPort"));
                ((Hashtable<String, Object>)proxyProps).put("username", ((Hashtable<K, Object>)pocProps).get("proxyUser"));
                ((Hashtable<String, Object>)proxyProps).put("password", ((Hashtable<K, Object>)pocProps).get("proxyPass"));
            }
            else if (proxyProperties != null && !proxyProperties.isEmpty()) {
                proxyProps = new Properties();
                ((Hashtable<String, Object>)proxyProps).put("host", ((Hashtable<K, Object>)proxyProperties).get("proxyHost"));
                ((Hashtable<String, Object>)proxyProps).put("port", ((Hashtable<K, Object>)proxyProperties).get("proxyPort"));
                ((Hashtable<String, Object>)proxyProps).put("username", ((Hashtable<K, Object>)proxyProperties).get("proxyUser"));
                ((Hashtable<String, Object>)proxyProps).put("password", ((Hashtable<K, Object>)proxyProperties).get("proxyPass"));
            }
        }
        catch (final Exception exp) {
            CreatorDataPost.logger.log(Level.SEVERE, "getProxyProps():- Exception is : ", exp);
        }
        return proxyProps;
    }
    
    static {
        CreatorDataPost.logger = Logger.getLogger(CreatorDataPost.class.getName());
    }
}
