package com.adventnet.sym.server.mdm.chrome.payload;

import org.json.JSONObject;
import org.json.JSONException;

public class ChromeVPNPayload extends ChromeONCPayload
{
    public ChromeVPNPayload() {
    }
    
    public ChromeVPNPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "VPN", payloadIdentifier, payloadDisplayName);
    }
    
    public void setConnType(final int connectionType) throws JSONException {
        String type = "L2TP-IPsec";
        if (connectionType == 13) {
            type = "OpenVPN";
        }
        this.getONCPayloadObject().put("Type", (Object)type);
    }
    
    public void setConnName(final String connectionName) throws JSONException {
        this.setONCName(connectionName);
    }
    
    public void setServerName(final String serverName) throws JSONException {
        this.getONCPayloadObject().put("Host", (Object)serverName);
    }
    
    public void setL2TPConfig(final JSONObject l2tp) throws JSONException {
        this.getONCPayloadObject().put("L2TP", (Object)l2tp);
    }
    
    public void setOpenVPNConfig(final JSONObject openVPN) throws JSONException {
        this.getONCPayloadObject().put("OpenVPN", (Object)openVPN);
    }
}
