package com.adventnet.sym.server.mdm.chrome.payload;

import org.json.JSONArray;
import org.json.JSONException;

public class ChromeApplicationPolicyPayload extends ChromePayload
{
    public ChromeApplicationPolicyPayload() throws JSONException {
    }
    
    public ChromeApplicationPolicyPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "ExtensionInstallSources", payloadIdentifier, payloadDisplayName);
    }
    
    public void setAllowedURLs(final JSONArray urlArray) throws JSONException {
        this.getPayloadJSON().put("AllowedUrls", (Object)urlArray);
    }
}
