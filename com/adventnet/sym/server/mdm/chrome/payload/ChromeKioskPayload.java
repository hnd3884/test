package com.adventnet.sym.server.mdm.chrome.payload;

import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Arrays;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class ChromeKioskPayload extends ChromePayload
{
    public ChromeKioskPayload() throws JSONException {
    }
    
    public ChromeKioskPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Kiosk", payloadIdentifier, payloadDisplayName);
    }
    
    public void setMode(final boolean mode) throws JSONException {
        this.getPayloadJSON().put("IsSingleAppKiosk", mode);
    }
    
    public void setBailOut(final Boolean bailout) throws JSONException {
        this.getPayloadJSON().put("IsBailoutEnabled", (Object)bailout);
    }
    
    public void setPromptNetwork(final Boolean promptNetwork) throws JSONException {
        this.getPayloadJSON().put("promptForNetworkWhenOffline", (Object)promptNetwork);
    }
    
    public void setHealthMonitoring(final Boolean healthMonitoring) throws JSONException {
        this.getPayloadJSON().put("IsDeviceHealthMonitoringEnabled", (Object)healthMonitoring);
    }
    
    public void setLogsEnabled(final Boolean logsEnabled) throws JSONException {
        this.getPayloadJSON().put("IsSystemLogsUploadEnabled", (Object)logsEnabled);
    }
    
    public void setOSUpdatePermission(final Boolean osUpdate) throws JSONException {
        this.getPayloadJSON().put("AllowKioskToControlChromeVersion", (Object)osUpdate);
    }
    
    public void setApps(final JSONArray kioskAppDetails) throws JSONException {
        this.getPayloadJSON().put("KioskAppsList", (Object)kioskAppDetails);
    }
    
    public void initAlertDetails() throws JSONException {
        this.getPayloadJSON().put("DeviceStatusAlertDetails", (Object)new JSONObject());
    }
    
    public JSONObject getAlertJSON() throws JSONException {
        return this.getPayloadJSON().getJSONObject("DeviceStatusAlertDetails");
    }
    
    public void setAlertType(final String alertMode) throws JSONException {
        this.getAlertJSON().put("DeviceStatusAlertType", (Object)alertMode);
    }
    
    public void setEmailIDs(final String emailIDS) throws JSONException {
        final List<String> emailList = Arrays.asList(emailIDS.split(","));
        this.getAlertJSON().put("DeviceSTatusAlertEmails", (Object)JSONUtil.getInstance().convertListToJSONArray(emailList));
    }
    
    public void setPhoneNumbers(final String phoneNumbers) throws JSONException {
        final List<String> phoneNoList = Arrays.asList(phoneNumbers.split(","));
        this.getAlertJSON().put("DeviceSTatusAlertPhoneNo", (Object)JSONUtil.getInstance().convertListToJSONArray(phoneNoList));
    }
}
