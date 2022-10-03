package com.adventnet.sym.server.mdm.chrome.payload;

import org.json.JSONObject;
import java.util.List;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONException;

public class ChromeRestrictionsPayload extends ChromePayload
{
    public ChromeRestrictionsPayload() {
    }
    
    public ChromeRestrictionsPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Restrictions", payloadIdentifier, payloadDisplayName);
    }
    
    public void setGuestMode(final Integer value) throws JSONException {
        this.getPayloadJSON().put("GuestMode", (Object)value);
    }
    
    public void setEphemeralMode(final Integer value) throws JSONException {
        this.getPayloadJSON().put("IsEphemeralModeEnabled", (Object)value);
    }
    
    public void setForcedReEnrollment(final Integer value) throws JSONException {
        this.getPayloadJSON().put("ForcedReEnrollmentEnabled", (Object)value);
        this.getPayloadJSON().put("forcedReenrollment", (Object)value);
    }
    
    public void setAllowedUsersToSignIn(final String value) throws JSONException {
        final List allowedUsersList = MDMUtil.getInstance().getStringList(value, ",");
        this.getPayloadJSON().put("AllowedUsersToSignin", (Collection)allowedUsersList);
    }
    
    public void setAutoCompleteDomainName(final String value) throws JSONException {
        this.getPayloadJSON().put("AutoCompleteDomainName", (Object)value);
    }
    
    public void setRedirectToSamlIdpAllowed(final Integer value) throws JSONException {
        this.getPayloadJSON().put("RedirectToSamlIdpAllowed", (Object)value);
    }
    
    public void setTransferSamlCookies(final Integer value) throws JSONException {
        this.getPayloadJSON().put("TransferSamlCookies", (Object)value);
    }
    
    public void setTimeSettings(final int timezoneMode, final String timeZone, final int timeZoneDetection) throws JSONException {
        final JSONObject timeJSON = new JSONObject();
        timeJSON.put("TimeZoneMode", timezoneMode);
        if (timezoneMode == 0) {
            timeJSON.put("TimeZone", (Object)timeZone);
        }
        else {
            timeJSON.put("DetectionType", timeZoneDetection);
        }
        this.getPayloadJSON().put("TimezoneSettings", (Object)timeJSON);
    }
    
    public void setVirtualMachinesAllowed(final boolean value) throws JSONException {
        this.getPayloadJSON().put("AllowVirtualMachines", value);
    }
    
    public void setShowUserNamesOnSignInScreen(final boolean value) throws JSONException {
        this.getPayloadJSON().put("ShowUserNamesOnSignInScreen", value);
    }
}
