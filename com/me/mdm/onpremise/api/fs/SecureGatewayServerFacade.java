package com.me.mdm.onpremise.api.fs;

import java.util.Hashtable;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import org.json.JSONException;
import com.me.mdm.onpremise.server.util.MDMPFwsUtil;
import java.util.Iterator;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.devicemanagement.onpremise.server.util.FwsUtil;
import com.adventnet.sym.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;

public class SecureGatewayServerFacade
{
    public Logger logger;
    public Properties natProps;
    public static boolean fsCheckStopped;
    
    public SecureGatewayServerFacade() {
        this.logger = Logger.getLogger(SecureGatewayServerFacade.class.getName());
        this.natProps = new Properties();
    }
    
    public JSONObject showSettings() {
        try {
            this.natProps = NATHandler.getNATConfigurationProperties();
            final String checkStopped = SyMUtil.getSyMParameter("fsCheckStopped");
            if (checkStopped != null && checkStopped.equalsIgnoreCase("true")) {
                SecureGatewayServerFacade.fsCheckStopped = Boolean.TRUE;
            }
            final String configuredStatus = SyMUtil.getSyMParameter("forwarding_server_config");
            final JSONObject resultJSON = new JSONObject();
            if (configuredStatus != null && configuredStatus.equalsIgnoreCase("true")) {
                resultJSON.put("is_fs_configured", (Object)configuredStatus);
                if (FwsUtil.fsProps != null && FwsUtil.fsProps.getProperty("buildNumber") != null && FwsUtil.isSecureGatewayServerUpToDate()) {
                    String serverName = null;
                    if (FwsUtil.fsProps != null && FwsUtil.fsProps.getProperty("publicIP") != null) {
                        serverName = FwsUtil.fsProps.getProperty("publicIP");
                    }
                    else {
                        serverName = this.natProps.getProperty("NAT_ADDRESS");
                    }
                    final int httpsPort = ((Hashtable<K, Integer>)this.natProps).get("NAT_HTTPS_PORT");
                    resultJSON.put("url", (Object)("https://" + serverName + ":" + httpsPort));
                    resultJSON.put("https_port", httpsPort);
                    resultJSON.put("show_upgrade", (Object)"false");
                    resultJSON.put("email_info", (Object)this.getEmailInfo());
                    final long installedTimeinLong = DateTimeUtil.dateInLong(FwsUtil.fsProps.getProperty("installationTime"), "yyyy-MM-dd HH:mm:ss");
                    final JSONObject fsDetails = new JSONObject();
                    for (final String name : FwsUtil.fsProps.stringPropertyNames()) {
                        fsDetails.put(name, (Object)FwsUtil.fsProps.getProperty(name));
                    }
                    fsDetails.put("installation_time", (Object)DateTimeUtil.longdateToString(installedTimeinLong, SyMUtil.getUserTimeFormat()));
                    resultJSON.put("fs_details", (Object)fsDetails);
                }
                else {
                    resultJSON.put("show_upgrade", (Object)"true");
                }
                return resultJSON;
            }
            MessageProvider.getInstance().hideMessage("MDMP_FWS_NOT_REACHABLE_ENABLE");
            resultJSON.put("is_fs_configured", (Object)"false");
            resultJSON.put("server_props", (Object)this.getDCServerDetails());
            return resultJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in showSettings ", e);
            throw new APIHTTPException("SGS005", new Object[0]);
        }
    }
    
    public JSONObject checkFwsServersStatus() {
        try {
            this.natProps = NATHandler.getNATConfigurationProperties();
            Boolean ServerStatus = false;
            final String checkStopped = SyMUtil.getSyMParameter("fsCheckStopped");
            if (checkStopped != null && checkStopped.equalsIgnoreCase("true")) {
                SecureGatewayServerFacade.fsCheckStopped = Boolean.TRUE;
            }
            if (SecureGatewayServerFacade.fsCheckStopped) {
                final JSONObject resultJSON = new JSONObject();
                resultJSON.put("is_server_reachable", (Object)ServerStatus);
                return resultJSON;
            }
            String serverName = null;
            final JSONObject resultJSON2 = new JSONObject();
            if (FwsUtil.fsProps.getProperty("publicIP") != null) {
                serverName = FwsUtil.fsProps.getProperty("publicIP");
            }
            else {
                serverName = this.natProps.getProperty("NAT_ADDRESS");
            }
            MDMPFwsUtil.checkFwServerUp();
            final int httpsPort = ((Hashtable<K, Integer>)this.natProps).get("NAT_HTTPS_PORT");
            final JSONObject fwsStatus = FwsUtil.getFwsServerStatus(serverName, httpsPort);
            if (fwsStatus == null) {
                MessageProvider.getInstance().unhideMessage("MDMP_FWS_NOT_REACHABLE_ENABLE");
            }
            else {
                ServerStatus = true;
                MessageProvider.getInstance().hideMessage("MDMP_FWS_NOT_REACHABLE_ENABLE");
            }
            resultJSON2.put("is_server_reachable", (Object)ServerStatus);
            return resultJSON2;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in checkFwsServersStatus ", e);
            throw new APIHTTPException("SGS002", new Object[0]);
        }
    }
    
    public JSONObject checkCertificateSync() {
        try {
            this.natProps = NATHandler.getNATConfigurationProperties();
            final String checkStopped = SyMUtil.getSyMParameter("fsCheckStopped");
            if (checkStopped != null && checkStopped.equalsIgnoreCase("true")) {
                SecureGatewayServerFacade.fsCheckStopped = Boolean.TRUE;
            }
            if (SecureGatewayServerFacade.fsCheckStopped) {
                final JSONObject resultJSON = new JSONObject();
                resultJSON.put("is_cert_synced", (Object)"false");
                return resultJSON;
            }
            String serverName = null;
            if (FwsUtil.fsProps.getProperty("publicIP") != null) {
                serverName = FwsUtil.fsProps.getProperty("publicIP");
            }
            else {
                serverName = this.natProps.getProperty("NAT_ADDRESS");
            }
            final int httpsPort = ((Hashtable<K, Integer>)this.natProps).get("NAT_HTTPS_PORT");
            final Boolean crtSynced = FwsUtil.getCertSyncDetails(serverName, httpsPort);
            final JSONObject resultJSON2 = new JSONObject();
            resultJSON2.put("is_cert_synced", (Object)crtSynced);
            return resultJSON2;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in checkCertificateSync ", e);
            throw new APIHTTPException("SGS001", new Object[0]);
        }
    }
    
    public void updateMail(final JSONObject request) {
        try {
            final JSONObject body = request.getJSONObject("msg_body");
            final String emailAddr = String.valueOf(body.get("email"));
            FwsUtil.addOrUpdateEmailAddr("FwServer", true, emailAddr);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in updateMail ", e);
            if (e instanceof JSONException) {
                throw new APIHTTPException("SGS006", new Object[0]);
            }
            throw new APIHTTPException("SGS003", new Object[0]);
        }
    }
    
    public void saveIP(final JSONObject request) {
        try {
            final JSONObject body = request.getJSONObject("msg_body");
            final String fwsIP = String.valueOf(body.get("public_ip"));
            FwsUtil.fsProps.setProperty("publicIP", fwsIP);
            final String confFile = System.getProperty("server.home") + File.separator + FwsUtil.fsPropsFile;
            FileAccessUtil.storeProperties(FwsUtil.fsProps, confFile, true);
            FwsUtil.regenerateProps();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in saveIP ", e);
            if (e instanceof JSONException) {
                throw new APIHTTPException("SGS006", new Object[0]);
            }
            throw new APIHTTPException("SGS004", new Object[0]);
        }
    }
    
    public JSONObject getEmailInfo() throws Exception {
        final JSONObject responseJSON = new JSONObject();
        String EmailAddr = "";
        try {
            final Row EmailInfo = DBUtil.getRowFromDB("EMailAddr", "MODULE", (Object)"FwServer");
            if (EmailInfo != null) {
                EmailAddr = (String)EmailInfo.get("EMAIL_ADDR");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getEmailInfo ", e);
        }
        responseJSON.put("is_mail_configured", ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured());
        responseJSON.put("email", (Object)EmailAddr);
        return responseJSON;
    }
    
    public JSONObject getDCServerDetails() throws Exception {
        final Properties webServerProps = WebServerUtil.getWebServerSettings();
        final JSONObject serverDetails = new JSONObject();
        serverDetails.put("SERVER_NAME", (Object)webServerProps.getProperty("server.fqdn"));
        serverDetails.put("INTERNAL_SERVER_NAME", (Object)SyMUtil.getServerName());
        serverDetails.put("HTTP_PORT", (Object)webServerProps.getProperty("http.port"));
        serverDetails.put("HTTPS_PORT", (Object)webServerProps.getProperty("https.port"));
        if (webServerProps.containsKey("ui.fws.enabled") && webServerProps.getProperty("ui.fws.enabled").equalsIgnoreCase("true")) {
            serverDetails.put("uiEnabled", true);
        }
        else {
            serverDetails.put("uiEnabled", false);
        }
        return serverDetails;
    }
    
    static {
        SecureGatewayServerFacade.fsCheckStopped = Boolean.FALSE;
    }
}
