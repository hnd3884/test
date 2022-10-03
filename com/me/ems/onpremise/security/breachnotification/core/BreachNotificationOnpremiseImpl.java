package com.me.ems.onpremise.security.breachnotification.core;

import java.util.Hashtable;
import java.util.Map;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.Property;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import org.json.JSONObject;
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
import java.util.Properties;
import com.me.ems.framework.security.breachnotification.core.BreachNotificationCoreUtil;
import java.util.logging.Logger;
import com.me.ems.framework.security.breachnotification.core.BreachNotificationAPI;

public class BreachNotificationOnpremiseImpl implements BreachNotificationAPI
{
    private static Logger logger;
    BreachNotificationCoreUtil breachNotificationCoreUtil;
    
    public BreachNotificationOnpremiseImpl() {
        this.breachNotificationCoreUtil = new BreachNotificationCoreUtil();
    }
    
    public boolean updateCreatorForm(final String url, final Properties proxyDetails, final Properties userProp) {
        DataOutputStream wr = null;
        HttpsURLConnection httpsConn = null;
        boolean status = false;
        final JSONObject data = this.convertCreatorFormat(userProp);
        try {
            final URL httpUrl = new URL(url);
            if (this.isValidProxy(proxyDetails)) {
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
            httpsConn.addRequestProperty("Content-Length", String.valueOf(data.toString().length()));
            httpsConn.connect();
            wr = new DataOutputStream(httpsConn.getOutputStream());
            wr.writeBytes(data.toString());
            final int responseCode = httpsConn.getResponseCode();
            final StringBuilder builder = new StringBuilder();
            try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsConn.getInputStream(), "utf-8"))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
            }
            if (responseCode == 200) {
                final String responseJSONString = builder.toString();
                final JSONObject responseJSON = new JSONObject(responseJSONString);
                final int Code = responseJSON.getInt("code");
                if (Code == 3000) {
                    status = true;
                    final JSONObject dataJSON = responseJSON.getJSONObject("data");
                    final String uniqueFormRowId = dataJSON.getString("ID");
                    BreachNotificationCoreUtil.getInstance().updateUniqueFormRowID(uniqueFormRowId);
                    DCEventLogUtil.getInstance().addEvent(11102, this.breachNotificationCoreUtil.getUserName(), (HashMap)null, "dc.common.security.breachnotification.creator.success", (Object)null, true);
                }
                else {
                    DCEventLogUtil.getInstance().addEvent(11103, this.breachNotificationCoreUtil.getUserName(), (HashMap)null, "dc.common.security.breachnotification.creator.fail", (Object)null, true);
                }
            }
            else {
                DCEventLogUtil.getInstance().addEvent(11103, this.breachNotificationCoreUtil.getUserName(), (HashMap)null, "dc.common.security.breachnotification.creator.fail", (Object)null, true);
            }
        }
        catch (final UnknownHostException ex) {
            try {
                DCEventLogUtil.getInstance().addEvent(11103, this.breachNotificationCoreUtil.getUserName(), (HashMap)null, "dc.common.security.breachnotification.creator.fail", (Object)null, true);
                if (wr != null) {
                    wr.flush();
                    wr.close();
                }
                if (httpsConn != null) {
                    httpsConn.disconnect();
                }
            }
            catch (final Exception exp) {
                BreachNotificationOnpremiseImpl.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationOnpremiseImpl", "UnknownHostException exception occurred ");
            }
        }
        catch (final Exception exp2) {
            DCEventLogUtil.getInstance().addEvent(11103, this.breachNotificationCoreUtil.getUserName(), (HashMap)null, "dc.common.security.breachnotification.creator.fail", (Object)null, true);
            BreachNotificationOnpremiseImpl.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationOnpremiseImpl", exp2);
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
            catch (final Exception exp3) {
                BreachNotificationOnpremiseImpl.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationOnpremiseImpl", exp3);
            }
        }
        return status;
    }
    
    public JSONObject convertCreatorFormat(final Properties userProp) {
        final JSONObject jsonObject = Property.toJSONObject(userProp);
        final JSONObject json = new JSONObject();
        json.put("data", (Object)jsonObject);
        return json;
    }
    
    public Properties getProxyProps() {
        Properties proxyProps = new Properties();
        try {
            final Properties proxyProperties = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            final int proxyType = DownloadManager.proxyType;
            if (proxyType == 4) {
                final String url = "http://creator.zoho.com";
                final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(url, proxyProperties);
                proxyProps = new Properties();
                ((Hashtable<String, Object>)proxyProps).put("host", ((Hashtable<K, Object>)pacProps).get("proxyHost"));
                ((Hashtable<String, Object>)proxyProps).put("port", ((Hashtable<K, Object>)pacProps).get("proxyPort"));
                ((Hashtable<String, Object>)proxyProps).put("username", ((Hashtable<K, Object>)pacProps).get("proxyUser"));
                ((Hashtable<String, Object>)proxyProps).put("password", ((Hashtable<K, Object>)pacProps).get("proxyPass"));
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
            BreachNotificationOnpremiseImpl.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationOnpremiseImpl", exp);
        }
        return proxyProps;
    }
    
    public String getBuildVersion() {
        String buildVersion = "";
        try {
            buildVersion = SyMUtil.getProductProperty("buildnumber");
        }
        catch (final Exception exp) {
            BreachNotificationOnpremiseImpl.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationOnpremiseImpl", exp);
        }
        return buildVersion;
    }
    
    public String getProductCode() {
        String productCode = "";
        try {
            productCode = ProductUrlLoader.getInstance().getValue("productcode");
        }
        catch (final Exception exp) {
            BreachNotificationOnpremiseImpl.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationOnpremiseImpl", exp);
        }
        return productCode;
    }
    
    private boolean isValid(final String strValue) {
        return this.breachNotificationCoreUtil.isValid(strValue);
    }
    
    public boolean isValidProxy(final Properties proxyProp) {
        try {
            return proxyProp != null && !proxyProp.isEmpty() && (this.isValid(proxyProp.getProperty("host")) && this.isValid(proxyProp.getProperty("port")) && this.isValid(proxyProp.getProperty("username")) && this.isValid(proxyProp.getProperty("password"))) && Integer.parseInt(proxyProp.getProperty("port")) >= 0;
        }
        catch (final Exception exp) {
            BreachNotificationOnpremiseImpl.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationOnpremiseImpl", exp);
            return false;
        }
    }
    
    public boolean routineUserNotification(final Properties postDetails) {
        boolean updateStatus = false;
        try {
            final Properties userProp = new Properties();
            ((Hashtable<String, String>)userProp).put("Customer_Email", ((Hashtable<K, String>)postDetails).get("Customer_Email"));
            ((Hashtable<String, String>)userProp).put("Build_Version", ((Hashtable<K, String>)postDetails).get("Build_Version"));
            ((Hashtable<String, String>)userProp).put("Domain_Address", ((Hashtable<K, String>)postDetails).get("Domain_Address"));
            ((Hashtable<String, String>)userProp).put("IP_Address", this.breachNotificationCoreUtil.getDCServerIPAddressDetail());
            ((Hashtable<String, String>)userProp).put("Product_Name", this.getProductCode());
            ((Hashtable<String, String>)userProp).put("Last_Update_Time", this.breachNotificationCoreUtil.getTimeCreatorFormat(SyMUtil.getCurrentTime()));
            final String uniqueFormRowId = BreachNotificationCoreUtil.getInstance().getUniqueFormRowID();
            if (!uniqueFormRowId.equals("--")) {
                ((Hashtable<String, String>)userProp).put("UniqueID", uniqueFormRowId);
            }
            updateStatus = this.updateCreatorForm(this.breachNotificationCoreUtil.getUploadURL(), this.getProxyProps(), userProp);
            if (updateStatus) {
                this.breachNotificationCoreUtil.updateMailServiceDB(userProp, 2);
            }
            else {
                this.breachNotificationCoreUtil.updateMailServiceDB(userProp, 1);
            }
        }
        catch (final Exception exp) {
            BreachNotificationOnpremiseImpl.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationOnpremiseImpl", exp);
        }
        return updateStatus;
    }
    
    public boolean schedulerUpdate() {
        boolean changeStatus = false;
        boolean updateStatus = false;
        final Properties postDetails = new Properties();
        String dbNATAddress = "";
        String buildVersion = "";
        String emailAddress = "";
        int mailModifiedStatus = 0;
        boolean emailStatus = false;
        try {
            final Map userInfo = this.breachNotificationCoreUtil.getSchedulerUserInfo();
            dbNATAddress = userInfo.get("natAddress");
            buildVersion = userInfo.get("buildVersion");
            emailAddress = userInfo.get("emailAddress");
            mailModifiedStatus = userInfo.get("updateStatus");
        }
        catch (final Exception exp) {
            BreachNotificationOnpremiseImpl.logger.log(Level.SEVERE, "Exception occurred at BreachNotificationOnpremiseImpl", exp);
        }
        emailStatus = (mailModifiedStatus != 0);
        if (emailStatus) {
            final String serverNATAddress = this.breachNotificationCoreUtil.getServerNATAddressDetail();
            final boolean statusNATAddress = this.breachNotificationCoreUtil.compareNATDetails(dbNATAddress, serverNATAddress);
            final long currentBuildVersion = Long.parseLong(buildVersion);
            final Integer buildNumFromDB = Integer.parseInt(this.getBuildVersion());
            final boolean statusBuildVersion = this.breachNotificationCoreUtil.isBuildChanged(currentBuildVersion, buildNumFromDB);
            changeStatus = (statusNATAddress || statusBuildVersion);
            ((Hashtable<String, String>)postDetails).put("Domain_Address", serverNATAddress);
            ((Hashtable<String, String>)postDetails).put("Build_Version", buildNumFromDB.toString());
            ((Hashtable<String, String>)postDetails).put("Customer_Email", emailAddress);
            if (changeStatus) {
                final BreachNotificationAPI breachNotificationAPI = ApiFactoryProvider.getBreachNotificationAPI();
                updateStatus = breachNotificationAPI.routineUserNotification(postDetails);
            }
        }
        return updateStatus;
    }
    
    public String getUploadURL() {
        final String uniqueFormRowId = BreachNotificationCoreUtil.getInstance().getUniqueFormRowID();
        if (uniqueFormRowId.equals("--")) {
            return "https://creatorapp.zoho.com/publishapi/v2/adventnetwebmaster/ems-security-management/form/Security_Advisory_Email_Collection?privatelink=JhCFH94fPYTpGnaT3PNPXagZ3f1q7ZffvpnTR7Sk50PTWnONrJWCfj495h91u7sEK209hb0a0G5U4MA16OEYBVMKH5awkBQvjusv";
        }
        return "https://creatorapp.zoho.com/publishapi/v2/adventnetwebmaster/ems-security-management/form/inputSecurityAdvisoryEmailCollection?privatelink=CxGu0hUMPZaOSJSDHOxpKCxq4eeS54Rk1d8CUb3qnCOXxKGJZCOn48wk2MazjCMxekrZuJR5y0jx4GV3R0skTTFTKs2xw6nehnpE";
    }
    
    public String getFormURL() {
        return "";
    }
    
    static {
        BreachNotificationOnpremiseImpl.logger = Logger.getLogger("SecurityLogger");
    }
}
