package com.me.devicemanagement.onpremise.server.admin;

import java.util.Hashtable;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.ems.onpremise.security.securegatewayserver.constants.SecureGatewayServerConstants;
import com.me.devicemanagement.onpremise.server.util.FwsUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.certificate.client.ClientCertAuthBean;
import java.util.HashMap;
import java.util.Map;
import com.me.devicemanagement.onpremise.server.certificate.client.ClientCertificateGenerator;
import java.nio.file.Files;
import com.me.ems.onpremise.security.securegatewayserver.core.SecureGatewayServerPropertiesUtils;
import com.me.devicemanagement.onpremise.server.certificate.client.ClientCertificateUtil;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.api.util.APIRequest;
import java.util.logging.Logger;

public class SecureGatewayServerAPI
{
    private static final Logger LOGGER;
    
    public JSONObject getCertificates(final APIRequest request) {
        JSONObject certificateObject = null;
        try {
            certificateObject = new JSONObject();
            final String webServerName = WebServerUtil.getWebServerName().trim();
            final String serverHome = System.getProperty("server.home");
            final String serverCertificate = serverHome + File.separator + webServerName + File.separator + "conf" + File.separator + "server.crt";
            final String serverKey = serverHome + File.separator + webServerName + File.separator + "conf" + File.separator + "server.key";
            final String intermediateCert = serverHome + File.separator + webServerName + File.separator + "conf" + File.separator + "intermediate.crt";
            certificateObject.put("webserverName", (Object)webServerName);
            certificateObject.put("server.crt", (Object)ApiFactoryProvider.getFileAccessAPI().readFileIntoString(serverCertificate));
            certificateObject.put("server.key", (Object)ApiFactoryProvider.getFileAccessAPI().readFileIntoString(serverKey));
            if (webServerName.equalsIgnoreCase("apache") && ApiFactoryProvider.getFileAccessAPI().isFileExists(intermediateCert)) {
                certificateObject.put("intermediate.crt", (Object)ApiFactoryProvider.getFileAccessAPI().readFileIntoString(intermediateCert));
            }
            final boolean isClientCertAuthEnabledInWebSettings = ClientCertificateUtil.getInstance().isClientCertAuthEnabledFromWebSettings();
            final boolean isClientCertAuthForceDisabled = ClientCertificateUtil.getInstance().isClientCertAuthForceDisabledInWebSettings();
            final Long sgsBuildNumber = new Long(SecureGatewayServerPropertiesUtils.getSGSProperty("buildNumber"));
            final boolean isClientCertAuthEnabled = (sgsBuildNumber < 90097L) ? (!isClientCertAuthForceDisabled) : isClientCertAuthEnabledInWebSettings;
            certificateObject.put("isClientCertificateVerificationEnabled", isClientCertAuthEnabled);
            certificateObject.put("isClientCertAuthForceDisabled", isClientCertAuthForceDisabled);
            certificateObject.put("ClientRootCA.crt", (Object)new String(Files.readAllBytes(ClientCertificateUtil.getInstance().getClientRootCACertificatePath())));
            certificateObject.put("sgsCertificateKeyPair", (Map)ClientCertificateGenerator.getInstance().getClientCertificateAndKeyForSecureGatewayServer());
            final Map httpHeadersMap = ClientCertAuthBean.getInstance().getClientCertAuthConfig().get("httpHeaders");
            certificateObject.put("sgsAgentClientCertificateHeaderName", (Object)httpHeadersMap.get("inSecureGatewayServer").toString());
        }
        catch (final Exception e) {
            SecureGatewayServerAPI.LOGGER.log(Level.WARNING, "Exception occured while copying certificates", e);
            try {
                certificateObject.put("error", (Object)e);
            }
            catch (final JSONException jsonException) {
                SecureGatewayServerAPI.LOGGER.log(Level.WARNING, "Exception occured ", (Throwable)jsonException);
            }
        }
        return certificateObject;
    }
    
    public JSONObject isFSLicenseValid(final APIRequest request) throws JSONException {
        final JSONObject licenseResponse = new JSONObject();
        final String fs_license_valid = SyMUtil.getSyMParameter("fs_license_valid");
        if (!LicenseProvider.getInstance().isFwsEnabled() && (fs_license_valid == null || !fs_license_valid.equalsIgnoreCase("true")) && (!FwsUtil.isFwsTrialFlagEnabled() || FwsUtil.getFwsTrialExpiryPeriod() <= 0L)) {
            licenseResponse.put("isValid", false);
        }
        else {
            licenseResponse.put("isValid", true);
        }
        return licenseResponse;
    }
    
    public JSONObject saveSGSDetails(final APIRequest request) {
        final JSONObject responseObject = new JSONObject();
        try {
            final HashMap parameterList = request.getParameterList();
            if (parameterList.containsKey("sgs_details")) {
                final JSONObject sgsDetails = new JSONObject(parameterList.get("sgs_details").toString());
                SecureGatewayServerAPI.LOGGER.log(Level.INFO, "Size of SendSGSDetails is : {0}", sgsDetails.length());
                if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(SecureGatewayServerConstants.SGS_SERVER_DETAILS_FILE)) {
                    ApiFactoryProvider.getFileAccessAPI().createNewFile(SecureGatewayServerConstants.SGS_SERVER_DETAILS_FILE);
                }
                final boolean fileStatus = FileAccessUtil.writeDataInFile(SecureGatewayServerConstants.SGS_SERVER_DETAILS_FILE, sgsDetails.toString(4));
                SecureGatewayServerAPI.LOGGER.log(Level.INFO, "File write status of SendSGSDetails is : {0}", fileStatus);
                responseObject.put("operation_status", (Object)("File write status of SendSGSDetails is : " + fileStatus));
            }
            else {
                SecureGatewayServerAPI.LOGGER.log(Level.INFO, "Request for Save SGS Details is Empty ");
                responseObject.put("operation_status", (Object)"No details found in request");
            }
        }
        catch (final Exception ex) {
            SecureGatewayServerAPI.LOGGER.log(Level.SEVERE, "Exception in saveSGSDetails ", ex);
            responseObject.put("operation_status", (Object)"Failed to store data");
        }
        return responseObject;
    }
    
    public JSONObject syncData(final APIRequest request) throws JSONException, Exception {
        final JSONObject fwsDetailedData = new JSONObject();
        fwsDetailedData.put("uiEnable", (Object)this.getUIAccessEnableData());
        fwsDetailedData.put("securityConfig", (Object)this.getSecurityConfigurations());
        fwsDetailedData.put("certificate", (Object)this.getCertificates(null));
        String nginxProxyFileData = ApiFactoryProvider.getSGSProxyFileDataHandler().getProxyFileData();
        final Long sgsBuildNumber = new Long(SecureGatewayServerPropertiesUtils.getSGSProperty("buildNumber"));
        if (sgsBuildNumber < 90097L) {
            nginxProxyFileData = this.replaceClientCertAuthLevel(nginxProxyFileData);
        }
        fwsDetailedData.put("proxyFileData", (Object)nginxProxyFileData);
        fwsDetailedData.put("serverBuildNumber", (Object)SyMUtil.getProductProperty("buildnumber"));
        return fwsDetailedData;
    }
    
    private String replaceClientCertAuthLevel(final String locationString) {
        String replacedLocationString = locationString;
        try {
            final String clientCertAuthLevel = WebServerUtil.getWebServerSettings().getProperty("client.cert.auth.level");
            if (clientCertAuthLevel != null && !clientCertAuthLevel.isEmpty()) {
                replacedLocationString = replacedLocationString.replace("%client.cert.auth.level%", clientCertAuthLevel);
                SecureGatewayServerAPI.LOGGER.log(Level.INFO, "Client cert auth level replaced for SGS");
            }
            return replacedLocationString;
        }
        catch (final Exception e) {
            SecureGatewayServerAPI.LOGGER.log(Level.SEVERE, "Unable to replace client cert auth level for SGS", e);
            return locationString;
        }
    }
    
    private JSONObject getSecurityConfigurations() throws JSONException {
        final JSONObject securityConfigurations = new JSONObject();
        try {
            final Properties webServerProps = WebServerUtil.getWebServerSettings();
            if (webServerProps.containsKey("IsTLSV2Enabled")) {
                securityConfigurations.put("isTLSV2Enabled", ((Hashtable<K, Object>)webServerProps).get("IsTLSV2Enabled"));
            }
            if (webServerProps.containsKey("webserver.cipheroption")) {
                if (((Hashtable<K, Object>)webServerProps).get("webserver.cipheroption").equals("webserver.commoncipher")) {
                    securityConfigurations.put("ciphers", ((Hashtable<K, Object>)webServerProps).get("webserver.commoncipher"));
                }
                else if (((Hashtable<K, Object>)webServerProps).get("webserver.cipheroption").equals("webserver.winxpcipher")) {
                    securityConfigurations.put("ciphers", ((Hashtable<K, Object>)webServerProps).get("webserver.winxpcipher"));
                }
            }
            if (webServerProps.containsKey("apache.sslhonorcipherorder")) {
                securityConfigurations.put("sslHonorCipherOrder", ((Hashtable<K, Object>)webServerProps).get("apache.sslhonorcipherorder"));
            }
        }
        catch (final JSONException e) {
            SecureGatewayServerAPI.LOGGER.log(Level.INFO, "SecureGatewayServerAPI.getSecurityConfigurations - ", (Throwable)e);
        }
        catch (final Exception e2) {
            SecureGatewayServerAPI.LOGGER.log(Level.INFO, "SecureGatewayServerAPI.getSecurityConfigurations - Exception", e2);
        }
        SecureGatewayServerAPI.LOGGER.log(Level.INFO, "Security Config SYNC - " + securityConfigurations);
        return securityConfigurations;
    }
    
    private JSONObject getUIAccessEnableData() {
        final JSONObject uiAccessdata = new JSONObject();
        try {
            final Properties webServerProps = WebServerUtil.getWebServerSettings();
            if (webServerProps.containsKey("ui.fws.enabled") && webServerProps.getProperty("ui.fws.enabled").equalsIgnoreCase("true")) {
                uiAccessdata.put("enable.ui", true);
            }
            else {
                uiAccessdata.put("enable.ui", false);
            }
        }
        catch (final JSONException e) {
            SecureGatewayServerAPI.LOGGER.log(Level.INFO, "SecureGatewayServerAPI.getSecurityConfigurations - ", (Throwable)e);
        }
        catch (final Exception e2) {
            SecureGatewayServerAPI.LOGGER.log(Level.INFO, "SecureGatewayServerAPI.getUIAccessEnableData - ", e2);
        }
        SecureGatewayServerAPI.LOGGER.log(Level.INFO, "UI Access Data SYNC - " + uiAccessdata);
        return uiAccessdata;
    }
    
    static {
        LOGGER = Logger.getLogger("SecurityLogger");
    }
}
