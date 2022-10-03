package com.adventnet.sym.server.mdm.chrome.payload;

import org.json.JSONObject;
import org.json.JSONException;

public class ChromePowerMgmtPayload extends ChromePayload
{
    public ChromePowerMgmtPayload() throws JSONException {
    }
    
    public ChromePowerMgmtPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "PowerIdleManagement", payloadIdentifier, payloadDisplayName);
    }
    
    public void initPayloadData() throws JSONException {
        JSONObject delays = new JSONObject();
        final JSONObject acSettings = new JSONObject().put("Delays", (Object)delays);
        delays = new JSONObject();
        final JSONObject dcSettings = new JSONObject().put("Delays", (Object)delays);
        final JSONObject payloadData = new JSONObject();
        payloadData.put("AC", (Object)acSettings);
        payloadData.put("Battery", (Object)dcSettings);
        this.getPayloadJSON().put("PayloadData", (Object)payloadData);
    }
    
    public JSONObject getPayloadDataJSON() throws JSONException {
        return this.getPayloadJSON().getJSONObject("PayloadData");
    }
    
    public JSONObject getDelayObject(final int type) throws JSONException {
        return this.getSettings(type).getJSONObject("Delays");
    }
    
    public JSONObject getSettings(final int type) throws JSONException {
        return this.getPayloadDataJSON().getJSONObject((type == 1) ? "AC" : "Battery");
    }
    
    public void setWarningTimeout(final int value, final int type) throws JSONException {
        this.setDelayTimeout("IdleWarning", value, type);
    }
    
    public void setScreenOffTimeout(final int value, final int type) throws JSONException {
        this.setDelayTimeout("ScreenOff", value, type);
    }
    
    public void setIdleTimeout(final int value, final int type) throws JSONException {
        this.setDelayTimeout("Idle", value, type);
    }
    
    public void setScreenDimTimeout(final int value, final int type) throws JSONException {
        this.setDelayTimeout("ScreenDim", value, type);
    }
    
    private void setDelayTimeout(final String key, final int value, final int type) throws JSONException {
        if (value != -1) {
            final long milliSec = value * 1000L;
            this.getDelayObject(type).put(key, milliSec);
        }
    }
    
    public void setIdleAction(final int value, final int type) throws JSONException {
        String idleAction = "Suspend";
        switch (value) {
            case 1: {
                idleAction = "Logout";
                break;
            }
            case 2: {
                idleAction = "Shutdown";
                break;
            }
            case 3: {
                idleAction = "DoNothing";
                break;
            }
        }
        this.getSettings(type).put("IdleAction", (Object)idleAction);
    }
}
