package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONException;

public class AndroidAPNPayload extends AndroidPayload
{
    private static final int APN_PROTOCOL_IPV4 = 0;
    private static final int APN_PROTOCOL_IPV6 = 1;
    private static final int APN_PROTOCOL_IPV4_IPV6 = 2;
    private static final int APN_PROTOCOL_PPP = 3;
    
    public AndroidAPNPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "APN", payloadIdentifier, payloadDisplayName);
    }
    
    public void setAPN(final String apn) throws JSONException {
        this.getPayloadJSON().put("APN", (Object)apn);
    }
    
    public void setUserPassword(final String password) throws JSONException {
        this.getPayloadJSON().put("Password", (Object)password);
    }
    
    public void setUserName(final String userName) throws JSONException {
        this.getPayloadJSON().put("Username", (Object)userName);
    }
    
    public void setServer(final String server) throws JSONException {
        this.getPayloadJSON().put("Server", (Object)server);
    }
    
    public void setPort(final String port) throws JSONException {
        this.getPayloadJSON().put("Port", (Object)port);
    }
    
    public void setAuthType(final Integer authType) throws JSONException {
        this.getPayloadJSON().put("APNAuthType", (Object)authType);
    }
    
    public void setIsPreferred(final Boolean isPreferred) throws JSONException {
        this.getPayloadJSON().put("IsPreferedApn", (Object)isPreferred);
    }
    
    public void setMCC(final String mcc) throws JSONException {
        this.getPayloadJSON().put("MCC", (Object)mcc);
    }
    
    public void setMNC(final String mnc) throws JSONException {
        this.getPayloadJSON().put("MNC", (Object)mnc);
    }
    
    public void setMMSC(final String mmsc) throws JSONException {
        this.getPayloadJSON().put("MMSC", (Object)mmsc);
    }
    
    public void setMmsPort(final String mmsPort) throws JSONException {
        this.getPayloadJSON().put("MMSPort", (Object)mmsPort);
    }
    
    public void setMmsProxy(final String mmsProxy) throws JSONException {
        this.getPayloadJSON().put("MMSProxy", (Object)mmsProxy);
    }
    
    public void setAPNName(final String name) throws JSONException {
        this.getPayloadJSON().put("APNName", (Object)name);
    }
    
    public void setProtocol(final Integer protocol) throws JSONException {
        this.getPayloadJSON().put("Protocol", (Object)this.getProtocol(protocol));
    }
    
    public void setRoamingProtocol(final Integer roamingProtocol) throws JSONException {
        this.getPayloadJSON().put("RoamingProtocol", (Object)this.getProtocol(roamingProtocol));
    }
    
    public void setProxyServer(final String proxyServer) throws JSONException {
        this.getPayloadJSON().put("Proxy", (Object)proxyServer);
    }
    
    public void setType(final String apnType) throws JSONException {
        this.getPayloadJSON().put("APNType", (Object)apnType);
    }
    
    private String getProtocol(final int protocol) {
        String protocolString = "";
        if (protocol == 0) {
            protocolString = "IPV4";
        }
        else if (protocol == 1) {
            protocolString = "IPV6";
        }
        else if (protocol == 2) {
            protocolString = "IPV4V6";
        }
        else if (protocol == 3) {
            protocolString = "PPP";
        }
        return protocolString;
    }
    
    public void setMVNO(final int mvno) throws JSONException {
        this.getPayloadJSON().put("MVNO", (Object)this.getMVNO(mvno));
    }
    
    private String getMVNO(final int mvno) {
        String mvnoString = "";
        if (mvno == -1) {
            mvnoString = "";
        }
        else if (mvno == 0) {
            mvnoString = "cid";
        }
        else if (mvno == 1) {
            mvnoString = "iccid";
        }
        else if (mvno == 2) {
            mvnoString = "imsi";
        }
        else if (mvno == 3) {
            mvnoString = "spn";
        }
        return mvnoString;
    }
}
