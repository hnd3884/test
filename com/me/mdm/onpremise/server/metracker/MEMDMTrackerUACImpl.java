package com.me.mdm.onpremise.server.metracker;

import java.util.Hashtable;
import com.me.devicemanagement.onpremise.server.util.UpdatesParamUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.me.devicemanagement.onpremise.server.twofactor.TwoFactorAction;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import java.util.Map;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUACUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;
import com.me.mdm.server.metracker.MEMDMTrackerConstants;

public class MEMDMTrackerUACImpl extends MEMDMTrackerConstants implements MEDMTracker
{
    private Properties uacTrackerProperties;
    private Logger logger;
    private String sourceClass;
    private static final String MDM_ACTIVE_USER = "Active_User_Count";
    private static final String MDM_LOCAL_AUTH_USER_COUNT = "Local_User_Count";
    private static final String MDM_AD_AUTH_USER_COUNT = "AD_User_Count";
    private static final String MDM_USER_CREATE_ROLE = "User_Create_Role";
    private static final String MDM_LOGIN_COUNT = "Login_Count";
    private static final String MDM_LAST_LOGIN_AT = "User_Last_Login_At";
    public final String passwordConfigured = "Configured";
    public final String passwordDetails = "PasswordProfileDetails";
    public final String twoFactorAuthentication = "TwoFactorAuthenticationDetails";
    public static final String IS_COMPLEMENT_LICENSE_ENABLED = "IS_COMPLEMENT_LICENSE_ENABLED";
    
    public MEMDMTrackerUACImpl() {
        this.uacTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEMDMUACImpl";
    }
    
    public Properties getTrackerProperties() {
        SyMLogger.info(this.logger, this.sourceClass, "getProperties", "UAC params implementation starts...");
        return this.getUACTrackerProperties();
    }
    
    private Properties getUACTrackerProperties() {
        this.updateUACProperties();
        return this.uacTrackerProperties;
    }
    
    private void updateUACProperties() {
        try {
            if (!this.uacTrackerProperties.isEmpty()) {
                this.uacTrackerProperties = new Properties();
            }
            this.trackUserDetails();
            this.trackLoginDetails();
            this.trackPasswordProfile();
            this.addFlashMessageDetails();
            final int userCreateRoleCount = METrackerUACUtil.trackuserCreateRoleDetails();
            this.uacTrackerProperties.setProperty("User_Create_Role", String.valueOf(userCreateRoleCount));
            final JSONObject localisationJson = METrackerUACUtil.getLocalizationDetails();
            this.uacTrackerProperties.setProperty("Localization_Details", localisationJson.toString());
            this.uacTrackerProperties.setProperty("NonEngUser", String.valueOf(localisationJson.get("nonEnglishUsers")));
            final String usertype = LicenseProvider.getInstance().getLicenseUserType();
            if (usertype != null && usertype.length() > 0 && usertype.equalsIgnoreCase("complement")) {
                this.uacTrackerProperties.setProperty("IS_COMPLEMENT_LICENSE_ENABLED", "true");
            }
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.uacTrackerProperties);
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "UAC params implementation  ends");
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "updateCommonProperties", "Exception : ", (Throwable)e);
        }
    }
    
    private void trackUserDetails() {
        try {
            final Map userDtlsMap = METrackerUACUtil.getUserDetailsMap();
            this.uacTrackerProperties.setProperty("Active_User_Count", userDtlsMap.get("activeUser"));
            this.uacTrackerProperties.setProperty("Local_User_Count", userDtlsMap.get("localAuthUser"));
            this.uacTrackerProperties.setProperty("AD_User_Count", userDtlsMap.get("adAuthUser"));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "trackUserDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private void trackLoginDetails() {
        try {
            Properties loginProperties = new Properties();
            loginProperties = METrackerUtil.getMETrackParam(loginProperties, "TotUserLoginCount", "LstLoginAt");
            if (!loginProperties.isEmpty()) {
                final String loginCount = loginProperties.getProperty("TotUserLoginCount");
                final String lastLogin = loginProperties.getProperty("LstLoginAt");
                if (loginCount != null) {
                    this.uacTrackerProperties.setProperty("Login_Count", loginCount);
                }
                if (lastLogin != null) {
                    this.uacTrackerProperties.setProperty("User_Last_Login_At", lastLogin);
                }
            }
            final JSONObject twoFactor = new JSONObject();
            final String twoFactorAuthType = TwoFactorAction.getTwoFactorAuthType();
            twoFactor.put("TwoFactorAuth", (Object)twoFactorAuthType);
            ((Hashtable<String, JSONObject>)this.uacTrackerProperties).put("TwoFactorAuthenticationDetails", twoFactor);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "trackSDPUserDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private void trackPasswordProfile() {
        try {
            final JSONObject passwordPolicyDetails = DMOnPremiseUserUtil.getPasswordPolicyDetails();
            if (passwordPolicyDetails != null && passwordPolicyDetails.length() != 0) {
                passwordPolicyDetails.put("Configured", (Object)Boolean.TRUE);
                this.uacTrackerProperties.setProperty("PasswordProfileDetails", passwordPolicyDetails.toString());
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "trackPasswordProfile", "Exception While fetching Password Details for METrack:", (Throwable)e);
        }
    }
    
    private void addFlashMessageDetails() {
        final JSONObject flashMsgJSON = new JSONObject();
        try {
            flashMsgJSON.put("FlashMsgFailed", (Object)UpdatesParamUtil.getUpdParameter("FLASH_MSG_FAILED_COUNT"));
            flashMsgJSON.put("FlashMsgImgFailed", (Object)UpdatesParamUtil.getUpdParameter("FLASH_MSG_IMG_FAILED_COUNT"));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addFlashMessageDetails", "Exception While fetching updates json failed Details for METrack:", (Throwable)e);
        }
        this.uacTrackerProperties.setProperty("FlashMsg_Details", flashMsgJSON.toString());
    }
}
