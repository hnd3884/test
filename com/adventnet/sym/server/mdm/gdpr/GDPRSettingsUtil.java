package com.adventnet.sym.server.mdm.gdpr;

import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.consents.ConsentsUtil;
import com.me.devicemanagement.framework.server.consents.ConsentStatusUtil;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import java.util.logging.Logger;

public class GDPRSettingsUtil
{
    private Logger logger;
    private static GDPRSettingsUtil gdprSettingsutil;
    private static final int APP_CONFIG_NONE = 0;
    private static final int APP_CONFIG_PARTIAL = 1;
    private static final int APP_CONFIG_FULL = 2;
    public static final int SUCCESS = 0;
    private static final String REMARK_DELIMITER = "@@@";
    private static final String AUTOMATIC_LOG_UPLOAD_CONSENT_GROUP = "LogUpload";
    private static final String ME_TRACKING_CONSENT_GROUP = "CollectUsage";
    
    public GDPRSettingsUtil() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static GDPRSettingsUtil getInstance() {
        if (GDPRSettingsUtil.gdprSettingsutil == null) {
            GDPRSettingsUtil.gdprSettingsutil = new GDPRSettingsUtil();
        }
        return GDPRSettingsUtil.gdprSettingsutil;
    }
    
    JSONObject setIntegrationSettingsData() throws JSONException {
        final JSONObject json = new JSONObject();
        final boolean isSDPPluginEnabled = this.isSDPPluginEnabled();
        json.put("isSDPPluginEnabled", isSDPPluginEnabled);
        final JSONArray integratedAppsList = MDMApiFactoryProvider.getMDMGDPRSettingsAPI().getIntegratedAppsProtocol();
        final int size = integratedAppsList.length();
        try {
            if (size > 0) {
                int httpsCount = 0;
                for (int i = 0; i < size; ++i) {
                    final JSONObject app = integratedAppsList.getJSONObject(i);
                    final String protocol = String.valueOf(app.get("PROTOCOL"));
                    if (protocol != null && protocol.equalsIgnoreCase("HTTPS")) {
                        ++httpsCount;
                    }
                }
                if (httpsCount == 0) {
                    json.put("IntegrationSettings", 1);
                }
                else if (httpsCount == size) {
                    json.put("IntegrationSettings", 2);
                }
                json.put("IntegrationSettingsData", (Object)integratedAppsList);
                json.put("showIntegrationSettings", true);
            }
            else {
                json.put("IntegrationSettings", 0);
                this.logger.info("Integration app list empty");
                json.put("showIntegrationSettings", false);
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Error while getting Integration app list", (Throwable)e);
        }
        return json;
    }
    
    JSONObject setSecuritySettingsData(final Long customerId) {
        final JSONObject json = new JSONObject();
        Map data = null;
        try {
            data = MDMApiFactoryProvider.getMDMGDPRSettingsAPI().getSecureSettings(customerId);
            final long securePerc = data.get("SECURE_PERCENTAGE");
            int securitySettingsStatus = 0;
            if (securePerc == 100L) {
                securitySettingsStatus = 2;
            }
            else if (securePerc > 0L) {
                securitySettingsStatus = 1;
            }
            json.put("SecuritySetting", securitySettingsStatus);
            json.put("showSecuritySettings", true);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return json;
    }
    
    boolean isFileSecuritySettingsAvailable() {
        CustomerInfoUtil.getInstance();
        return !CustomerInfoUtil.isSAS();
    }
    
    void changeServerSettings(final JSONObject consent, final boolean consentStatus) {
        String consentGroup = "";
        try {
            consentGroup = String.valueOf(consent.get("CONSENT_GROUP_NAME"));
            if (consentGroup.equalsIgnoreCase("LogUpload")) {
                final boolean isDblocksUpload = MDMApiFactoryProvider.getMDMGDPRSettingsAPI().getDefaultDblocksUploadSettings();
                if (isDblocksUpload != consentStatus) {
                    MDMApiFactoryProvider.getMDMGDPRSettingsAPI().setAutomaticUpload(consentStatus);
                    this.logger.log(Level.INFO, "Automatic DB lock log upload with ConsentGroup {0} status changed:{1}", new Object[] { consentGroup, consentStatus });
                }
            }
            if (consentGroup.equalsIgnoreCase("CollectUsage")) {
                final boolean isMETrackEnabled = this.getMETrackSettings();
                if (isMETrackEnabled != consentStatus) {
                    this.setMETrackSettings(consentStatus);
                    this.logger.log(Level.INFO, "ME Tracking with ConsentGroup {0} status changed:{1}", new Object[] { consentGroup, consentStatus });
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error occured saving the Consent(Server) Setting, ConsentGroup={0}, Acceptance status={1}", new Object[] { consentGroup, consentStatus });
        }
    }
    
    boolean getMETrackSettings() {
        boolean isTrackingEnabled = true;
        String setting = SyMUtil.getSyMParameter("ME_TRACK_SETTINGS");
        if (setting == null) {
            SyMUtil.updateSyMParameter("ME_TRACK_SETTINGS", "true");
            setting = "true";
        }
        isTrackingEnabled = Boolean.valueOf(setting);
        return isTrackingEnabled;
    }
    
    void setMETrackSettings(final boolean isTrackingEnabled) {
        String setting = "true";
        if (!isTrackingEnabled) {
            setting = "false";
            MDMApiFactoryProvider.getMDMGDPRSettingsAPI().disableTracking();
        }
        else {
            MDMApiFactoryProvider.getMDMGDPRSettingsAPI().enableTracking();
        }
        SyMUtil.updateSyMParameter("ME_TRACK_SETTINGS", setting);
    }
    
    boolean isSDPPluginEnabled() {
        boolean isSDPenabled = false;
        try {
            isSDPenabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ServiceDeskPlusUI");
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getSDPIntegrationStatus ", exp);
        }
        return isSDPenabled;
    }
    
    void writeResponse(final HttpServletResponse response, final JSONObject responseJSON) throws IOException {
        response.getWriter().write(responseJSON.toString());
        response.addHeader("Content-Type", "application/plain");
        response.flushBuffer();
    }
    
    JSONObject saveSecuritySettings(final JSONObject json) throws JSONException {
        final String securePercent = String.valueOf(json.get("securepercent"));
        int securityScore = Integer.parseInt(securePercent);
        securityScore = (int)MDMApiFactoryProvider.getMDMGDPRSettingsAPI().findScore(securityScore);
        if (json.has("enablehttps")) {
            final boolean enableHttps = json.getBoolean("enablehttps");
            if (enableHttps != Boolean.parseBoolean(SyMUtil.getSyMParameter("ENABLE_HTTPS"))) {
                SyMUtil.updateSyMParameter("ENABLE_HTTPS", Boolean.toString(enableHttps));
                if (enableHttps) {
                    ++securityScore;
                }
                else {
                    --securityScore;
                }
            }
        }
        securityScore += (int)MDMApiFactoryProvider.getMDMGDPRSettingsAPI().setSecureSettings(json);
        securityScore = (int)MDMApiFactoryProvider.getMDMGDPRSettingsAPI().calculatePercentageSecure(securityScore);
        securityScore = ((securityScore > 0) ? securityScore : 0);
        final JSONObject responseJSON = new JSONObject();
        final int status = 0;
        responseJSON.put("SECURE_PERCENT", securityScore);
        responseJSON.put("STATUS_CODE", status);
        return responseJSON;
    }
    
    boolean saveConsentList(final JSONObject json) throws Exception {
        final JSONArray consentList = json.getJSONArray("consentList");
        final String userName = String.valueOf(json.get("userName"));
        final Long userId = json.getLong("userId");
        final Long customerId = json.getLong("customerId");
        boolean isError = false;
        for (int i = 0; i < consentList.length(); ++i) {
            final JSONObject consent = consentList.getJSONObject(i);
            final Long consentId = consent.getLong("id");
            final boolean status = consent.getBoolean("status");
            final boolean isDelete = consent.getBoolean("isDelete");
            if (isDelete) {
                final int resultCode = ConsentStatusUtil.deleteConsentStatusDetails(consentId);
                if (resultCode != 1001) {
                    this.logger.log(Level.INFO, "Consent Id {0} Deleted", consentId);
                }
                else {
                    this.logger.log(Level.INFO, "Unable to Delete Consent Id{0}", consentId);
                }
            }
            else {
                final JSONObject con = ConsentsUtil.getConsent(consentId);
                int consentStatus;
                int eventId;
                String consentState;
                if (status) {
                    consentStatus = 1;
                    eventId = 2201;
                    consentState = I18N.getMsg("dc.common.APPROVED", new Object[0]);
                }
                else {
                    consentStatus = 2;
                    eventId = 2202;
                    consentState = I18N.getMsg("mdm.privacy.denied", new Object[0]);
                }
                String consentName = "";
                if (con != null) {
                    if (con.getInt("CONSENT_CATEGORY") == 1) {
                        getInstance().changeServerSettings(con, status);
                    }
                    consentName = String.valueOf(con.get("CONSENT_DESCRIPTION"));
                    final String remarksParam = I18N.getMsg(consentName, new Object[0]) + "@@@" + consentState + "@@@" + userName;
                    final JSONObject consentEventDetails = new JSONObject();
                    consentEventDetails.put("event_id", eventId);
                    consentEventDetails.put("remarks", (Object)"mdm.privacy.consent.remark.update");
                    consentEventDetails.put("remarksArgs", (Object)remarksParam);
                    final int code = ConsentStatusUtil.saveConsentStatus(userId, consentStatus, consentId, consentEventDetails, customerId);
                    if (1001 == code) {
                        this.logger.log(Level.SEVERE, "Error while saving the consent({0}) with status({1})", new Object[] { consentId, status });
                        isError = true;
                    }
                    else {
                        this.logger.info("Consent(" + consentId + ") with status(" + status + ") Saved Successfully");
                    }
                }
                else {
                    this.logger.log(Level.SEVERE, "Unable to retrive consent({0}) details", consentId);
                    isError = true;
                }
            }
        }
        return isError;
    }
    
    boolean updatetoggleGdprWidgetParamater(final String showWidgetStr, final long customerid) throws Exception {
        CustomerParamsHandler.getInstance().addOrUpdateParameter("showGdprWidget", showWidgetStr, customerid);
        return true;
    }
    
    static {
        GDPRSettingsUtil.gdprSettingsutil = null;
    }
}
