package com.me.mdm.chrome.agent.commands.profiles.payloads;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import java.util.logging.Level;
import java.util.Set;
import com.google.chromedevicemanagement.v1.model.SystemTimezoneSettings;
import com.google.chromedevicemanagement.v1.model.SamlSettings;
import com.google.chromedevicemanagement.v1.model.RedirectToSamlIdpAllowed;
import com.google.chromedevicemanagement.v1.model.LoginScreenDomainAutoComplete;
import java.util.List;
import com.google.chromedevicemanagement.v1.model.UserAllowlist;
import com.google.chromedevicemanagement.v1.model.ForcedReenrollment;
import com.google.chromedevicemanagement.v1.model.EphemeralUsersEnabled;
import com.google.chromedevicemanagement.v1.model.ShowUserNamesOnSignin;
import com.google.chromedevicemanagement.v1.model.DeviceUnaffiliatedCrostiniAllowed;
import com.google.chromedevicemanagement.v1.model.VirtualMachinesAllowed;
import com.google.chromedevicemanagement.v1.model.GuestModeDisabled;
import com.google.chromedevicemanagement.v1.model.DevicePolicy;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequest;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequestHandler;

public class RestrictionPayloadRequestHandler extends PayloadRequestHandler
{
    private static final int POLICY_UNDEFINED = 0;
    private static final int POLICY_ALLOWED = 1;
    private static final int POLICY_RESTRICTED = 2;
    private static final String GUEST_MODE = "GuestMode";
    private static final String EPHEMERAL_MODE = "IsEphemeralModeEnabled";
    private static final String FORCED_REENROLLMENT = "forcedReenrollment";
    private static final String ALLOWED_USERS_TO_SIGNIN = "AllowedUsersToSignin";
    private static final String AUTOCOMPLETE_DOMAIN_NAME = "AutoCompleteDomainName";
    private static final String REDIRECT_TO_SAML = "RedirectToSamlIdpAllowed";
    private static final String TRANSFER_SAML_COOKIES = "TransferSamlCookies";
    private static final String DISABLE_TASK_MANAGER = "DisableTaskManager";
    private static final String TIMEZONE_SETTINGS = "TimezoneSettings";
    private static final String VIRTUAL_MACHINES_ALLOWED = "AllowVirtualMachines";
    private static final String SHOW_USERNAMES = "ShowUserNamesOnSignInScreen";
    
    @Override
    public void processInstallPayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            final Context context = request.getContainer().getContext();
            final JSONObject payloadData = payloadReq.getPayloadData();
            this.logger.info("Restriction Payload : " + payloadData);
            final int guestMode = payloadData.optInt("GuestMode", 0);
            final int ephermeralMode = payloadData.optInt("IsEphemeralModeEnabled", 0);
            final int forcedReenrollment = payloadData.optInt("forcedReenrollment", 0);
            final JSONArray allowedUsersToSignIn = payloadData.optJSONArray("AllowedUsersToSignin");
            final String autoCompleteDomainName = payloadData.optString("AutoCompleteDomainName", (String)null);
            final int redirectToSAML = payloadData.optInt("RedirectToSamlIdpAllowed", 0);
            final int transferCookies = payloadData.optInt("TransferSamlCookies", 0);
            final JSONObject timeZoneSettings = payloadData.optJSONObject("TimezoneSettings");
            final boolean virtualMachinesAllowed = payloadData.optBoolean("AllowVirtualMachines", true);
            final boolean showUsersName = payloadData.optBoolean("ShowUserNamesOnSignInScreen", true);
            final DevicePolicy devicePolicy = new DevicePolicy();
            if (guestMode != 0) {
                devicePolicy.setGuestModeDisabled(new GuestModeDisabled().setGuestModeDisabled(Boolean.valueOf(guestMode == 2)));
            }
            devicePolicy.setVirtualMachinesAllowed(new VirtualMachinesAllowed().setVirtualMachinesAllowed(Boolean.valueOf(virtualMachinesAllowed)));
            devicePolicy.setDeviceUnaffiliatedCrostiniAllowed(new DeviceUnaffiliatedCrostiniAllowed().setDeviceUnaffiliatedCrostiniAllowed(Boolean.valueOf(virtualMachinesAllowed)));
            devicePolicy.setShowUserNames(new ShowUserNamesOnSignin().setShowUserNames(Boolean.valueOf(showUsersName)));
            if (ephermeralMode != 0) {
                devicePolicy.setEphemeralUsersEnabled(new EphemeralUsersEnabled().setEphemeralUsersEnabled(Boolean.valueOf(ephermeralMode == 1)));
            }
            ForcedReenrollment forcedReenrollmentPolicy = new ForcedReenrollment().setForcedReenrollmentMode("FORCED_REENROLLMENT_MODE_UNSPECIFIED");
            if (forcedReenrollment == 1) {
                forcedReenrollmentPolicy = new ForcedReenrollment().setForcedReenrollmentMode("FORCED_REENROLLMENT_MODE_FORCED");
            }
            else {
                forcedReenrollmentPolicy = new ForcedReenrollment().setForcedReenrollmentMode("FORCED_REENROLLMENT_MODE_DISABLED");
            }
            devicePolicy.setForcedReenrollment(forcedReenrollmentPolicy);
            if (allowedUsersToSignIn != null) {
                devicePolicy.setUserAllowlist(new UserAllowlist().setUserAllowlist((List)this.jsonArrayToList(allowedUsersToSignIn)));
            }
            if (autoCompleteDomainName != null) {
                devicePolicy.setLoginScreenDomainAutoComplete(new LoginScreenDomainAutoComplete().setLoginScreenDomainAutoComplete(autoCompleteDomainName));
            }
            if (redirectToSAML != 0) {
                devicePolicy.setRedirectToSamlIdpAllowed(new RedirectToSamlIdpAllowed().setRedirectToSamlIdpAllowed(Boolean.valueOf(redirectToSAML == 1)));
            }
            if (transferCookies != 0) {
                devicePolicy.setSamlSettings(new SamlSettings().setTransferSamlCookies(Boolean.valueOf(transferCookies == 1)));
            }
            if (timeZoneSettings != null) {
                final String TIMEZONE_MODE = "TimeZoneMode";
                final String TIME_ZONE = "TimeZone";
                final String DETECTION_TYPE = "DetectionType";
                final SystemTimezoneSettings systemTimezoneSettings = new SystemTimezoneSettings();
                final int timeZonemode = timeZoneSettings.optInt(TIMEZONE_MODE, 1);
                if (timeZonemode == 0) {
                    final String timeZone = timeZoneSettings.optString(TIME_ZONE);
                    systemTimezoneSettings.setSystemTimezone(timeZone);
                }
                else if (timeZonemode == 1) {
                    final int detectiontype = timeZoneSettings.optInt(DETECTION_TYPE, 0);
                    if (detectiontype == 0) {
                        systemTimezoneSettings.setAutomaticTimezoneDetectionType("AUTOMATIC_TIMEZONE_DETECTION_TYPE_UNSPECIFIED");
                    }
                    else if (detectiontype == 1) {
                        systemTimezoneSettings.setAutomaticTimezoneDetectionType("AUTOMATIC_TIMEZONE_DETECTION_USERS_DECIDE");
                    }
                    else if (detectiontype == 2) {
                        systemTimezoneSettings.setAutomaticTimezoneDetectionType("AUTOMATIC_TIMEZONE_DETECTION_DISABLED");
                    }
                    else if (detectiontype == 3) {
                        systemTimezoneSettings.setAutomaticTimezoneDetectionType("AUTOMATIC_TIMEZONE_DETECTION_IP_ONLY");
                    }
                    else if (detectiontype == 4) {
                        systemTimezoneSettings.setAutomaticTimezoneDetectionType("AUTOMATIC_TIMEZONE_DETECTION_SEND_WIFI_ACCESS_POINTS");
                    }
                    else if (detectiontype == 5) {
                        systemTimezoneSettings.setAutomaticTimezoneDetectionType("AUTOMATIC_TIMEZONE_DETECTION_SEND_ALL_LOCATION_INFO");
                    }
                }
                devicePolicy.setSystemTimezoneSettings(systemTimezoneSettings);
            }
            final String updateMask = this.getUpdateMask(devicePolicy.keySet());
            context.getCMPAService().enterprises().devices().updateDevicePolicy(context.getCMPAEnterpriseAndUDID(), devicePolicy).setUpdateMask(updateMask).execute();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, true);
            payloadResp.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResp.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
    }
    
    @Override
    public void processModifyPayload(final Request request, final Response response, final PayloadRequest oldPayloadReq, final PayloadRequest modifyPayloadReq, final PayloadResponse payloadResp) {
        this.processInstallPayload(request, response, oldPayloadReq, payloadResp);
    }
    
    @Override
    public void processRemovePayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            final Context context = request.getContainer().getContext();
            final DevicePolicy devicePolicy = (DevicePolicy)context.getCMPAService().enterprises().devices().getDevicePolicy(context.getCMPAEnterpriseAndUDID()).execute();
            devicePolicy.setGuestModeDisabled(new GuestModeDisabled());
            devicePolicy.setEphemeralUsersEnabled(new EphemeralUsersEnabled());
            devicePolicy.setForcedReenrollment(new ForcedReenrollment());
            devicePolicy.setUserAllowlist(new UserAllowlist());
            devicePolicy.setLoginScreenDomainAutoComplete(new LoginScreenDomainAutoComplete());
            devicePolicy.setSamlSettings(new SamlSettings());
            devicePolicy.setRedirectToSamlIdpAllowed(new RedirectToSamlIdpAllowed());
            devicePolicy.setVirtualMachinesAllowed(new VirtualMachinesAllowed());
            devicePolicy.setDeviceUnaffiliatedCrostiniAllowed(new DeviceUnaffiliatedCrostiniAllowed());
            devicePolicy.setShowUserNames(new ShowUserNamesOnSignin());
            final String updateMask = this.getUpdateMask(devicePolicy.keySet());
            context.getCMPAService().enterprises().devices().updateDevicePolicy(context.getCMPAEnterpriseAndUDID(), devicePolicy).setUpdateMask(updateMask).execute();
        }
        catch (final IOException ex) {
            this.logger.log(Level.SEVERE, "Exception : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, false);
            payloadResp.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResp.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
    }
}
