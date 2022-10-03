package com.me.webclient.admin;

import java.util.Hashtable;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.onpremise.server.metrack.METrackerHandler;
import java.util.Properties;
import com.me.mdm.onpremise.server.integration.sdp.MDMSDPIntegrationUtil;
import org.json.JSONArray;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.onpremise.webclient.admin.certificate.CertificateUtil;
import org.json.JSONObject;
import com.me.mdm.server.factory.MDMGDPRSettingAPI;

public class MDMGDPRSettingsImpl implements MDMGDPRSettingAPI
{
    public JSONObject getGDPRSettings() {
        final JSONObject json = new JSONObject();
        try {
            final CertificateUtil certificateUtil = CertificateUtil.getInstance();
            final String serverCertificatePath = certificateUtil.getServerCertificateWebSettingsFilePath();
            if (serverCertificatePath != null) {
                if (SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
                    json.put("THIRD_PARTY_CRT", true);
                }
                else {
                    json.put("THIRD_PARTY_CRT", false);
                }
                final Map certificateDetails = certificateUtil.getCertificateDetails(serverCertificatePath);
                if (certificateDetails != null) {
                    json.put("certificateDetails", certificateDetails);
                }
            }
            final Long defaultAdminUVHLoginID = DBUtil.getUVHValue("AaaLogin:login_id:0");
            final Boolean defaultAdminDisabled = DMOnPremiseUserUtil.isDefaultAdminDisabled(defaultAdminUVHLoginID);
            if (defaultAdminDisabled) {
                json.put("DEFAULT_ADMIN_DISABLED", true);
            }
            else {
                json.put("DEFAULT_ADMIN_DISABLED", false);
            }
            final JSONObject policyDetails = DMOnPremiseUserUtil.getPasswordPolicyDetails();
            if (policyDetails != null && policyDetails.length() > 0 && policyDetails.getBoolean("IS_COMPLEX_PASSWORD")) {
                json.put("PWD_POLICY_ENABLED", true);
            }
            else {
                json.put("PWD_POLICY_ENABLED", false);
            }
            final String fwsConfigured = SyMUtil.getSyMParameter("forwarding_server_config");
            if (fwsConfigured != null && fwsConfigured.equalsIgnoreCase("true")) {
                json.put("FWS_CONFIGURED", true);
            }
            else {
                json.put("FWS_CONFIGURED", false);
            }
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                final Object termsObject = DBUtil.getValueFromDB("TermsOfUse", "CUSTOMER_ID", (Object)CustomerInfoUtil.getInstance().getCustomerId(), "TERMS_ID");
                if (termsObject != null) {
                    json.put("TERMS_CONFIGURED", true);
                }
                else {
                    json.put("TERMS_CONFIGURED", false);
                }
                final Object privacyObject = DBUtil.getValueFromDB("MDPrivacyToOwnedBy", "CUSTOMER_ID", (Object)CustomerInfoUtil.getInstance().getCustomerId(), "PRIVACY_SETTINGS_ID");
                if (privacyObject != null) {
                    json.put("PRIVACY_CONFIGURED", true);
                }
                else {
                    json.put("PRIVACY_CONFIGURED", false);
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger(MDMGDPRSettingsImpl.class.getName()).log(Level.INFO, "Exception while getting GDPR Settings : {0}", e);
        }
        return json;
    }
    
    public JSONArray getIntegratedAppsProtocol() {
        final JSONArray integrationProps = new JSONArray();
        try {
            final Properties sdpSettingsProps = MDMSDPIntegrationUtil.getInstance().getServerSettings("HelpDesk");
            if (sdpSettingsProps != null) {
                final Boolean isSDPEnabled = ((Hashtable<K, Boolean>)sdpSettingsProps).get("IS_ENABLED");
                if (isSDPEnabled) {
                    final JSONObject integrationValueProps = new JSONObject();
                    integrationValueProps.put("APPNAME", (Object)"HelpDesk");
                    integrationValueProps.put("PROTOCOL", (Object)sdpSettingsProps.getProperty("PROTOCOL"));
                    integrationValueProps.put("URL_LINK", (Object)"/webclient#/uems/mdm/admin/integration/sdp");
                    integrationProps.put((Object)integrationValueProps);
                }
            }
            final Properties aeSettingsProps = MDMSDPIntegrationUtil.getInstance().getServerSettings("AssetExplorer");
            if (aeSettingsProps != null) {
                final Boolean isSDPEnabled2 = ((Hashtable<K, Boolean>)aeSettingsProps).get("IS_ENABLED");
                if (isSDPEnabled2) {
                    final JSONObject integrationValueProps2 = new JSONObject();
                    integrationValueProps2.put("APPNAME", (Object)"AssetExplorer");
                    integrationValueProps2.put("PROTOCOL", (Object)aeSettingsProps.getProperty("PROTOCOL"));
                    integrationValueProps2.put("URL_LINK", (Object)"/webclient#/uems/mdm/admin/integration/assetexplorer");
                    integrationProps.put((Object)integrationValueProps2);
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMGDPRSettingsImpl.class.getName()).log(Level.SEVERE, "Exception in getIntegratedAppsProtocol ", ex);
        }
        return integrationProps;
    }
    
    public void enableTracking() {
        METrackerHandler.enableTracking();
    }
    
    public void disableTracking() {
        METrackerHandler.disableTracking();
    }
    
    public boolean isTwoFactorEnabled() {
        boolean isEnabled = false;
        try {
            isEnabled = (boolean)DBUtil.getFirstValueFromDBWithOutCriteria("AaaUserTwoFactorDetails", "ENABLED");
        }
        catch (final Exception exp) {
            Logger.getLogger(MDMGDPRSettingsImpl.class.getName()).log(Level.SEVERE, "Exception in isTwoFactorEnabled", exp);
        }
        return isEnabled;
    }
    
    public boolean getDefaultDblocksUploadSettings() {
        try {
            final DataObject dbLockSettingsDo = SyMUtil.getPersistence().get("DbLockSettings", (Criteria)null);
            final Row settingsRow = dbLockSettingsDo.getRow("DbLockSettings");
            final Boolean automatic_mail = (Boolean)settingsRow.get("IS_AUTOMATIC");
            return automatic_mail;
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(MDMGDPRSettingsImpl.class.getName()).log(Level.SEVERE, "Exception while retrieving dblock settings table", (Throwable)ex);
            return false;
        }
    }
    
    public Map getSecureSettings(final Long customerId) {
        final Map settings = new HashMap();
        try {
            int secureScore = 1;
            final String httpsEnabled = SyMUtil.getSyMParameter("ENABLE_HTTPS");
            if (httpsEnabled != null) {
                settings.put("ENABLE_HTTPS_LOGIN", Boolean.valueOf(httpsEnabled));
                if (Boolean.valueOf(httpsEnabled)) {
                    ++secureScore;
                    settings.put("ENABLE_HTTPS_LOGIN", true);
                }
                else {
                    settings.put("ENABLE_HTTPS_LOGIN", false);
                }
            }
            else {
                settings.put("ENABLE_HTTPS_LOGIN", false);
            }
            JSONObject settingsJson = new JSONObject();
            settingsJson = MDMApiFactoryProvider.getMDMGDPRSettingsAPI().getGDPRSettings();
            if (settingsJson.optBoolean("THIRD_PARTY_CRT")) {
                ++secureScore;
                settings.put("THIRD_PARTY_CRT", true);
            }
            else {
                settings.put("THIRD_PARTY_CRT", false);
            }
            if (settingsJson.opt("certificateDetails") != null) {
                settings.put("certificateDetails", settingsJson.get("certificateDetails"));
            }
            if (settingsJson.getBoolean("DEFAULT_ADMIN_DISABLED")) {
                ++secureScore;
                settings.put("DEFAULT_ADMIN_DISABLED", true);
            }
            else {
                settings.put("DEFAULT_ADMIN_DISABLED", false);
            }
            if (settingsJson.getBoolean("FWS_CONFIGURED")) {
                ++secureScore;
                settings.put("FWS_CONFIGURED", true);
            }
            else {
                settings.put("FWS_CONFIGURED", false);
            }
            if (settingsJson.getBoolean("PWD_POLICY_ENABLED")) {
                ++secureScore;
                settings.put("PWD_POLICY_ENABLED", true);
            }
            else {
                settings.put("PWD_POLICY_ENABLED", false);
            }
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                if (settingsJson.getBoolean("TERMS_CONFIGURED")) {
                    ++secureScore;
                    settings.put("TERMS_CONFIGURED", true);
                }
                else {
                    settings.put("TERMS_CONFIGURED", false);
                }
                if (settingsJson.getBoolean("PRIVACY_CONFIGURED")) {
                    ++secureScore;
                    settings.put("PRIVACY_CONFIGURED", true);
                }
                else {
                    settings.put("PRIVACY_CONFIGURED", false);
                }
            }
            if (MDMApiFactoryProvider.getMDMGDPRSettingsAPI().isTwoFactorEnabled()) {
                ++secureScore;
                settings.put("TWO_FACTOR_ENABLED", true);
            }
            else {
                settings.put("TWO_FACTOR_ENABLED", false);
            }
            if (this.isOlderTLSDisabled()) {
                ++secureScore;
                settings.put("DISABLE_OLDER_TLS", true);
            }
            else {
                settings.put("DISABLE_OLDER_TLS", false);
            }
            settings.put("SECURE_PERCENTAGE", this.calculatePercentageSecure(secureScore));
        }
        catch (final Exception e) {
            Logger.getLogger(MDMGDPRSettingsImpl.class.getName()).log(Level.SEVERE, "Exception in getSecureSettings", e);
        }
        return settings;
    }
    
    public long setSecureSettings(final JSONObject json) {
        long scoreChange = 0L;
        if (json.has("disableoldertls")) {
            final boolean disableOlderTls = json.getBoolean("disableoldertls");
            try {
                final Properties webServerProps = WebServerUtil.getWebServerSettings();
                if (disableOlderTls != Boolean.parseBoolean(webServerProps.getProperty("IsTLSV2Enabled"))) {
                    webServerProps.setProperty("IsTLSV2Enabled", Boolean.toString(disableOlderTls));
                    WebServerUtil.storeProperWebServerSettings(webServerProps);
                    if (disableOlderTls) {
                        ++scoreChange;
                    }
                    else {
                        --scoreChange;
                    }
                }
            }
            catch (final Exception ex) {
                Logger.getLogger(MDMGDPRSettingsImpl.class.getName()).log(Level.SEVERE, "Exception while updating web server settings ...", ex);
            }
        }
        return scoreChange;
    }
    
    public long calculatePercentageSecure(final int secureScore) {
        double divisor = 10.0;
        if (CustomerInfoUtil.getInstance().isMSP()) {
            divisor = 7.0;
        }
        final double multiplier = 100.0 / divisor;
        final double result = secureScore * multiplier;
        return Math.round(result);
    }
    
    public long findScore(final long securityPercentage) {
        double divisor = 10.0;
        if (CustomerInfoUtil.getInstance().isMSP()) {
            divisor = 7.0;
        }
        final double result = divisor * securityPercentage / 100.0;
        return Math.round(result);
    }
    
    public void setAutomaticUpload(final boolean isDblocksUpload) {
        try {
            final DataObject dbLockSettingsDo = SyMUtil.getPersistence().get("DbLockSettings", (Criteria)null);
            final Row settingsRow = dbLockSettingsDo.getRow("DbLockSettings");
            settingsRow.set("IS_AUTOMATIC", (Object)isDblocksUpload);
            dbLockSettingsDo.updateRow(settingsRow);
            SyMUtil.getPersistence().update(dbLockSettingsDo);
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(MDMGDPRSettingsImpl.class.getName()).log(Level.SEVERE, "Exception while updating dblock settings table..", (Throwable)ex);
        }
    }
    
    private boolean isOlderTLSDisabled() {
        boolean disableOlderTLS = false;
        try {
            final Properties webServerProps = WebServerUtil.getWebServerSettings();
            disableOlderTLS = Boolean.parseBoolean(webServerProps.getProperty("IsTLSV2Enabled"));
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMGDPRSettingsImpl.class.getName()).log(Level.SEVERE, "Exception while getting web server settings ...", ex);
        }
        return disableOlderTLS;
    }
}
