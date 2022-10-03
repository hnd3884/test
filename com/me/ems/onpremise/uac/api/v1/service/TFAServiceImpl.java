package com.me.ems.onpremise.uac.api.v1.service;

import com.me.devicemanagement.framework.server.alerts.DCEMailAlertsHandler;
import com.me.devicemanagement.onpremise.server.twofactor.GoogleAuthAction;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.Properties;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.onpremise.server.creator.CreatorDataPost;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import com.me.ems.onpremise.uac.core.TFAUtil;
import com.me.devicemanagement.framework.server.authentication.UserMgmtUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.me.ems.onpremise.uac.api.v1.service.factory.TFAService;

public class TFAServiceImpl implements TFAService
{
    private static Logger logger;
    public static final Integer TFA_ENABLED;
    public static final Integer TFA_DISABLED_FOR_SOME_DAYS;
    public static final Integer TFA_DISABLED;
    public static final Integer NOT_APPLICABLE;
    
    @Override
    public Map<String, Object> getTFAEnforcementDetails() throws APIException {
        Map<String, Object> tfaEnforcementDetails = new HashMap<String, Object>();
        try {
            final Map tfaDbData = UserMgmtUtil.getUserMgmtParams((Object[])new String[] { "isTfaPermanentDisable", "TFADisableExpiry", "isTFAExtended" });
            tfaEnforcementDetails = this.setTFAEnforcementDetails(tfaEnforcementDetails, tfaDbData);
            tfaEnforcementDetails.put("isTFAExtended", Boolean.valueOf(tfaDbData.getOrDefault("isTFAExtended", "false")));
            tfaEnforcementDetails.put("isSupportContacted", TFAUtil.isSupportContacted());
        }
        catch (final Exception e) {
            TFAServiceImpl.logger.log(Level.SEVERE, "BasicAuth: Error occurred in  getTFAEnforcementDetails() method", e);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "Internal Server Error");
        }
        return tfaEnforcementDetails;
    }
    
    @Override
    public void extendOrDisableTFA(final Map tfaExtensionDetail, final String userName) throws APIException {
        try {
            final String type = tfaExtensionDetail.get("type");
            TFAServiceImpl.logger.log(Level.INFO, "tfaExtensionDetail type is : " + type);
            final Map tfaParams = UserMgmtUtil.getUserMgmtParams((Object[])new String[] { "isTFAExtended", "isTfaPermanentDisable" });
            final boolean isTfaExtendAllowed = !Boolean.valueOf(tfaParams.getOrDefault("isTfaPermanentDisable", tfaParams.getOrDefault("isTFAExtended", "false")));
            if (type.equals("extension") && isTfaExtendAllowed) {
                final Map dataMap = new HashMap();
                dataMap.put("TFADisableExpiry", String.valueOf(System.currentTimeMillis() + 259200000L));
                dataMap.put("isTFAExtended", "true");
                ApiFactoryProvider.getCacheAccessAPI().putCache("isTFAToBeEnabled", (Object)false);
                UserMgmtUtil.addOrUpdateUserMgmtParameters(dataMap);
            }
            else if (type.equals("permanent")) {
                final String tfaDisablingKey = TFAUtil.getTFADisableKey();
                tfaExtensionDetail.put("tfaDisablingKey", tfaDisablingKey);
                this.postTfaDisablingDataToCreator(tfaExtensionDetail, userName);
                SecurityUtil.updateSecurityParameter("TFADisablingKey", tfaDisablingKey);
            }
        }
        catch (final APIException apiException) {
            throw apiException;
        }
        catch (final Exception exception) {
            TFAServiceImpl.logger.log(Level.SEVERE, "extendOrDisableTFA():- Exception occurred while extendOrDisablingTFA:- ", exception);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "TFA003", "ems.tfa.disable_to_creator_failure_errorlog");
        }
    }
    
    public void postTfaDisablingDataToCreator(final Map tfaExtensionDetail, final String userName) throws Exception {
        TFAServiceImpl.logger.log(Level.INFO, "postTfaDisablingDataToCreator() action method called...!");
        final JSONObject inputData = new JSONObject();
        inputData.put("Name", (Object)tfaExtensionDetail.get("name"));
        inputData.put("Email", (Object)tfaExtensionDetail.get("email"));
        inputData.put("CISOMail", (Object)tfaExtensionDetail.get("cisoMail"));
        inputData.put("ContactNo", (Object)tfaExtensionDetail.get("contactNum"));
        inputData.put("Reason", (Object)this.getTfaPermanentDisablingReason(tfaExtensionDetail.get("reason")));
        inputData.put("Comments", (Object)((tfaExtensionDetail.get("comments") != null) ? tfaExtensionDetail.get("comments") : ""));
        inputData.put("Type", (Object)tfaExtensionDetail.get("type"));
        inputData.put("DisablingKey", (Object)tfaExtensionDetail.get("tfaDisablingKey"));
        inputData.put("ProductName", (Object)ProductUrlLoader.getInstance().getValue("productcode"));
        final String creatorPostUrl = "https://creatorapp.zohopublic.com/publishapi/v2/adventnetwebmaster/ems-security-management/form/TFADisableForm?privatelink=zT1KhEYn0vBBqFSp407sGXK1vFsVr60rwXZ2Vushva2Q8yOG5ZD2WuURRFsgZ9QQh6skYjSM3TmDFhyA6wXY7OaD9j423wz7Zzny";
        final Properties proxyDetails = CreatorDataPost.getProxyProps("https://creatorapp.zohopublic.com");
        final JSONObject creatorResponse = CreatorDataPost.getCreatorResponse(creatorPostUrl, proxyDetails, new JSONObject().put("data", (Object)inputData));
        if (creatorResponse != null && (int)creatorResponse.get("code") == 3000) {
            TFAServiceImpl.logger.log(Level.INFO, "TFA Permanent Disable data submitted successfully to creator");
            DCEventLogUtil.getInstance().addEvent(715, userName, (HashMap)null, "ems.tfa.disable_to_creator_success_eventlog", (Object)null, true);
            return;
        }
        TFAServiceImpl.logger.log(Level.WARNING, "Error while submitting TFA Permanent Disable data to creator. " + creatorResponse);
        DCEventLogUtil.getInstance().addEvent(715, userName, (HashMap)null, "ems.tfa.disable_to_creator_failure_eventlog", (Object)null, true);
        throw new APIException(Response.Status.SERVICE_UNAVAILABLE, "TFA003", "ems.tfa.disable_to_creator_failure_errorlog");
    }
    
    private String getTfaPermanentDisablingReason(final String disablingReason) {
        switch (disablingReason) {
            case "1": {
                return "The server web console access is restricted to internal network only";
            }
            case "2": {
                return "Enabled SAML authentication for the product users";
            }
            case "3": {
                return "Need more time to implement 2FA in my organization";
            }
            case "4": {
                return "The product does not support the 2FA mode used in my organization";
            }
            default: {
                return "Others";
            }
        }
    }
    
    private Map setTFAEnforcementDetails(final Map tfaEnforcementDetails, final Map tfaDbData) throws Exception {
        int tfaStatus = TFAServiceImpl.NOT_APPLICABLE;
        final boolean isPermanentlyDisabled = Boolean.valueOf(tfaDbData.getOrDefault("isTfaPermanentDisable", "false"));
        final int tfaExpiryInDays = TFAUtil.calculateTFAExpiryInDays(Long.parseLong(tfaDbData.getOrDefault("TFADisableExpiry", "0")));
        if (tfaExpiryInDays <= 10 && !isPermanentlyDisabled && !LicenseProvider.getInstance().getLicenseType().equalsIgnoreCase("T") && !ApiFactoryProvider.getDemoUtilAPI().isDemoMode() && TFAUtil.isTFAEnforcementEnabled()) {
            tfaStatus = TFAServiceImpl.TFA_DISABLED;
            if (TFAUtil.isTwoFactorEnabled()) {
                tfaStatus = TFAServiceImpl.TFA_ENABLED;
                tfaEnforcementDetails.put("tfaStatus", tfaStatus);
                return tfaEnforcementDetails;
            }
            if (tfaExpiryInDays > 0) {
                tfaStatus = TFAServiceImpl.TFA_DISABLED_FOR_SOME_DAYS;
                tfaEnforcementDetails.put("tfaExpiryInDays", tfaExpiryInDays);
            }
            else {
                ApiFactoryProvider.getCacheAccessAPI().putCache("isTFAToBeEnabled", (Object)true);
            }
        }
        tfaEnforcementDetails.put("tfaStatus", tfaStatus);
        return tfaEnforcementDetails;
    }
    
    @Override
    public Map<String, Object> getTwoFactorDetails() throws APIException {
        final Map<String, Object> twoFactorDetails = new HashMap<String, Object>();
        try {
            final String authType = TFAUtil.getTwoFactorAuthType();
            twoFactorDetails.put("isTFAEnabled", false);
            twoFactorDetails.put("mailNotProvided", TFAUtil.isMailNotProvided());
            if (authType.equalsIgnoreCase("disabled")) {
                return twoFactorDetails;
            }
            twoFactorDetails.put("isTFAEnabled", true);
            twoFactorDetails.put("authType", authType);
            twoFactorDetails.put("otpValidity", TFAUtil.getOTPValidity());
        }
        catch (final Exception e) {
            TFAServiceImpl.logger.log(Level.SEVERE, "TwoFactorAuth: Error occurred in  getTwoFactorDetails() method", e);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "Internal Server Error");
        }
        return twoFactorDetails;
    }
    
    @Override
    public Response saveTwoFactorDetails(final Map twoFactorDetails, final User user, final HttpServletRequest httpServletRequest) throws APIException {
        final String authType = twoFactorDetails.get("authType");
        final String otpValidity = String.valueOf(twoFactorDetails.get("otpValidity"));
        final Boolean isTFAEnabled = twoFactorDetails.get("isTFAEnabled");
        if (isTFAEnabled != null && isTFAEnabled) {
            TFAUtil.setTwoFactorAuth();
            return TFAUtil.enableTwoFactor(authType, otpValidity, user.getName());
        }
        TFAServiceImpl.logger.log(Level.SEVERE, "TwoFactorAuth: Error occurred in  setTwoFactorDetails() method");
        throw new APIException(Response.Status.BAD_REQUEST, "IAM0006", "Invalid data");
    }
    
    @Override
    public Map<String, String> getQRCode(final long loginID, final Map<String, String> responseTypeDetails) throws APIException {
        final Map<String, String> result = new HashMap<String, String>();
        boolean isEmailSent = false;
        final String option = responseTypeDetails.get("responseType");
        if (option == null) {
            throw new APIException(Response.Status.BAD_REQUEST, "IAM0006", "invalid data");
        }
        final Long userId = DMUserHandler.getUserIdForLoginId(Long.valueOf(loginID));
        final boolean isMailServerNotConfigured = !ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured();
        final String userEmail = DMUserHandler.getUserEmailID(userId);
        String msg = "internal server error";
        try {
            final String authType = TFAUtil.getTwoFactorAuthType();
            if (authType.equalsIgnoreCase("googleApp")) {
                final GoogleAuthAction googleAuthAction = new GoogleAuthAction(userId);
                if (option.equalsIgnoreCase("rawData")) {
                    final String barUrl = googleAuthAction.getQRBarPath();
                    result.put("base64ConvertedImage", barUrl);
                }
                else if (option.equalsIgnoreCase("mail")) {
                    if (isMailServerNotConfigured || userEmail == null || userEmail.isEmpty()) {
                        TFAServiceImpl.logger.log(Level.WARNING, "mail server settings not configured or user email not available");
                        msg = "mail server settings not configured or user email not available";
                    }
                    final JSONObject obj = new JSONObject();
                    obj.put("userID", (Object)userId);
                    obj.put("Email", (Object)userEmail);
                    obj.put("TaskID", (Object)"googleAuthentication");
                    DCEMailAlertsHandler.getInstance().sendEMailAlerts("google-authentication-alert", "googleAuthentication", obj);
                    isEmailSent = true;
                    TFAServiceImpl.logger.log(Level.INFO, "Email Invitation sent successfully for " + userEmail);
                }
            }
            else {
                TFAServiceImpl.logger.log(Level.SEVERE, "Auth App not configured " + userEmail);
                msg = "Auth App not configured";
            }
        }
        catch (final Exception e) {
            TFAServiceImpl.logger.log(Level.SEVERE, "Exception while getting QR Code", e);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", msg);
        }
        if (!msg.equalsIgnoreCase("internal server error")) {
            throw new APIException(Response.Status.BAD_REQUEST, "GENERIC0009", msg);
        }
        if (option.equalsIgnoreCase("mail")) {
            result.put("emailStatus", isEmailSent ? "success" : "failure");
        }
        return result;
    }
    
    static {
        TFAServiceImpl.logger = Logger.getLogger("UserManagementLogger");
        TFA_ENABLED = 0;
        TFA_DISABLED_FOR_SOME_DAYS = 1;
        TFA_DISABLED = 2;
        NOT_APPLICABLE = 3;
    }
}
