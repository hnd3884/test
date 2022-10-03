package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONArray;
import org.json.JSONException;

public class AndroidHttpProxyPayload extends AndroidPayload
{
    public AndroidHttpProxyPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "GlobalProxy", payloadIdentifier, payloadDisplayName);
    }
    
    public void setProxyType(final String type) throws JSONException {
        this.getPayloadJSON().put("ProxyType", (Object)type);
    }
    
    public void setServerURL(final String url) throws JSONException {
        this.getPayloadJSON().put("Host", (Object)url);
    }
    
    public void setServerPort(final int port) throws JSONException {
        this.getPayloadJSON().put("Port", port);
    }
    
    public void setPACUrl(final String pacUrl) throws JSONException {
        this.getPayloadJSON().put("PACUrl", (Object)pacUrl);
    }
    
    public void setByPassUrl(final JSONArray byPassUrl) throws JSONException {
        this.getPayloadJSON().put("ExclusionList", (Object)byPassUrl);
    }
}
