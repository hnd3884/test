package com.adventnet.sym.server.mdm.chrome.payload;

import org.json.JSONException;

public class ChromeUserRestrictionPayload extends ChromePayload
{
    public ChromeUserRestrictionPayload() throws JSONException {
    }
    
    public ChromeUserRestrictionPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "UserRestrictions", payloadIdentifier, payloadDisplayName);
    }
    
    public void setAllowIncognito(final boolean incognioto) throws JSONException {
        this.getPayloadJSON().put("ALLOW_INCOGNITO", incognioto);
    }
    
    public void setShowHomeButton(final int showHomeButton) throws JSONException {
        this.getPayloadJSON().put("showHomeButtonMode", showHomeButton);
    }
    
    public void setAllowPrinting(final boolean allowPrinting) throws JSONException {
        this.getPayloadJSON().put("printingDisabled", !allowPrinting);
    }
    
    public void setEndProcess(final boolean endProcess) throws JSONException {
        this.getPayloadJSON().put("taskManagerEndProcessDisabled", endProcess);
    }
    
    public void setExternalStorageAccess(final int storageAccess) throws JSONException {
        this.getPayloadJSON().put("accessMode", storageAccess);
    }
    
    public void setDisableScreenLock(final boolean disableScreenLock) throws JSONException {
        this.getPayloadJSON().put("DisableScreenLock", disableScreenLock);
    }
}
