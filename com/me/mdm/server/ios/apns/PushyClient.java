package com.me.mdm.server.ios.apns;

import java.util.Hashtable;
import org.json.JSONException;
import java.util.concurrent.ExecutionException;
import java.util.Date;
import io.netty.util.concurrent.Future;
import com.turo.pushy.apns.PushNotificationResponse;
import com.turo.pushy.apns.ApnsPushNotification;
import org.json.JSONObject;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import java.io.IOException;
import javax.net.ssl.SSLException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.turo.pushy.apns.proxy.ProxyHandlerFactory;
import java.net.SocketAddress;
import com.turo.pushy.apns.proxy.HttpProxyHandlerFactory;
import java.net.InetSocketAddress;
import com.turo.pushy.apns.ApnsClientBuilder;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.turo.pushy.apns.ApnsClient;
import java.util.logging.Logger;

public class PushyClient
{
    static final Logger logger;
    private String proxyHost;
    private String proxyPort;
    private String proxyUserName;
    private String proxyPassword;
    private String certPath;
    private String certPassword;
    private Boolean isProduction;
    private ApnsClient client;
    private long disconnectTimeout;
    
    public PushyClient() {
        this.proxyHost = null;
        this.proxyPort = null;
        this.proxyUserName = null;
        this.proxyPassword = null;
        this.certPath = null;
        this.certPassword = null;
        this.isProduction = true;
        this.client = null;
        this.disconnectTimeout = 60000L;
    }
    
    public void setCertificate(final String fullPath, final String password) {
        this.certPath = fullPath;
        this.certPassword = password;
    }
    
    public void setHTTPProxy(final String proxyHost, final String proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }
    
    public void setDefaultDMFProxy() throws Exception {
        final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        if (proxyDetails != null && MDMApiFactoryProvider.getMDMUtilAPI().useProxyForApns("APNSv2")) {
            this.proxyHost = null;
            this.proxyPort = null;
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            final int proxyType = DownloadManager.proxyType;
            if (proxyType == 4) {
                final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(this.getApiURL(), proxyDetails);
                this.proxyHost = ((Hashtable<K, String>)pacProps).get("proxyHost");
                this.proxyPort = ((Hashtable<K, String>)pacProps).get("proxyPort");
            }
            else {
                this.proxyHost = ((Hashtable<K, String>)proxyDetails).get("proxyHost");
                this.proxyPort = ((Hashtable<K, String>)proxyDetails).get("proxyPort");
            }
            this.proxyUserName = ((Hashtable<K, String>)proxyDetails).get("proxyUser");
            this.proxyPassword = ((Hashtable<K, String>)proxyDetails).get("proxyPass");
        }
        PushyClient.logger.log(Level.INFO, "PushyClient: initializeDefaultDMFProxy(): {0} {1}", new Object[] { this.proxyHost, this.proxyPort });
    }
    
    public void build() throws SSLException, IOException, Throwable {
        if (this.certPath == null) {
            throw new NullPointerException("PushyClient: Unable to build(), push certificate cannot be null!");
        }
        final ApnsClientBuilder apnsClientBuilder = new ApnsClientBuilder();
        if (this.proxyHost != null) {
            if (this.proxyUserName != null) {
                apnsClientBuilder.setProxyHandlerFactory((ProxyHandlerFactory)new HttpProxyHandlerFactory((SocketAddress)new InetSocketAddress(this.proxyHost, Integer.parseInt(this.proxyPort)), this.proxyUserName, this.proxyPassword));
            }
            else {
                apnsClientBuilder.setProxyHandlerFactory((ProxyHandlerFactory)new HttpProxyHandlerFactory((SocketAddress)new InetSocketAddress(this.proxyHost, Integer.parseInt(this.proxyPort))));
            }
        }
        else {
            apnsClientBuilder.setProxyHandlerFactory((ProxyHandlerFactory)null);
        }
        final byte[] certFileByteArray = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(this.certPath);
        final InputStream certFileStream = new ByteArrayInputStream(certFileByteArray);
        this.client = apnsClientBuilder.setClientCredentials(certFileStream, this.certPassword).setApnsServer("api.push.apple.com").build();
    }
    
    public void disconnect() throws InterruptedException {
        if (this.client != null) {
            this.client.close().await(this.disconnectTimeout);
        }
    }
    
    private String getApiURL() {
        if (this.isProduction) {
            return "https://api.push.apple.com:443";
        }
        return "https://api.push.apple.com:443";
    }
    
    public JSONObject sendNotificationWithOneRetry(final SimpleApnsPushNotification pushNotification) throws Throwable {
        JSONObject result = null;
        try {
            result = this.sendNotification(pushNotification);
        }
        catch (final Throwable ex) {
            PushyClient.logger.log(Level.INFO, "PushyClient: sendNotificationWithOneRetry retrying once more...");
            try {
                this.disconnect();
                this.build();
                result = this.sendNotification(pushNotification);
            }
            catch (final Throwable e) {
                PushyClient.logger.log(Level.SEVERE, "PushyClient sendNotificationWithOneRetry exception or throwable : ", e);
                try {
                    this.disconnect();
                }
                catch (final Exception exception) {
                    PushyClient.logger.log(Level.WARNING, "PushyClient sendNotificationWithOneRetry exception while disconnect : ", exception.getMessage());
                }
                this.client = null;
                throw e;
            }
        }
        return result;
    }
    
    private JSONObject sendNotification(final SimpleApnsPushNotification pushNotification) throws InterruptedException, ExecutionException, JSONException {
        this.checkAndThrowNullClientException();
        JSONObject result = new JSONObject();
        final Future<PushNotificationResponse<SimpleApnsPushNotification>> sendNotificationFuture = (Future<PushNotificationResponse<SimpleApnsPushNotification>>)this.client.sendNotification((ApnsPushNotification)pushNotification);
        try {
            final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationReponse = (PushNotificationResponse<SimpleApnsPushNotification>)sendNotificationFuture.get();
            if (pushNotificationReponse.isAccepted()) {
                PushyClient.logger.log(Level.INFO, "PushyClient: sendNotification successful!");
                result = this.populateResultJson("Success", null, null);
            }
            else {
                final String rejectionReason = pushNotificationReponse.getRejectionReason();
                final Date invalidationDate = pushNotificationReponse.getTokenInvalidationTimestamp();
                result.put("Status", (Object)"Failed");
                result.put("Reason", (Object)((rejectionReason == null) ? "Unknown" : rejectionReason));
                PushyClient.logger.log(Level.INFO, "PushyClient: sendNotification rejected! {0}", rejectionReason);
                if (invalidationDate != null) {
                    result.put("AdditionalData", invalidationDate.getTime());
                    PushyClient.logger.log(Level.INFO, "Token invalidation timestamp is {0}", invalidationDate.toString());
                }
            }
        }
        catch (final Exception e) {
            PushyClient.logger.log(Level.SEVERE, "PushyClient sendNotification Exception: ", e);
            throw e;
        }
        return result;
    }
    
    public Boolean isConnected() {
        if (this.client != null) {
            return true;
        }
        return false;
    }
    
    private void checkAndThrowNullClientException() {
        if (this.client == null) {
            throw new NullPointerException("PushyClient: client is null! Build it before using the api!");
        }
    }
    
    private JSONObject populateResultJson(final String status, final String errorReason, final String additionalData) {
        final JSONObject json = new JSONObject();
        try {
            json.put("Status", (Object)status);
            if (errorReason != null) {
                json.put("Reason", (Object)errorReason);
            }
            if (additionalData != null) {
                json.put("AdditionalData", (Object)additionalData);
            }
        }
        catch (final Exception e) {
            PushyClient.logger.log(Level.SEVERE, "PushyClient: Error while populateResultJson() ", e);
        }
        return json;
    }
    
    static {
        logger = Logger.getLogger("MDMLogger");
    }
}
