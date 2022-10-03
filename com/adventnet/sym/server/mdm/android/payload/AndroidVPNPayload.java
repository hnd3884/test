package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONObject;
import org.json.JSONException;

public class AndroidVPNPayload extends AndroidPayload
{
    public static final int L2TP_PSK = 19;
    public static final int PPTP = 1;
    public static final int PULSE_SECURE = 7;
    public static final int F5_SSL = 5;
    public static final int PALO_ALTO = 13;
    public static final int CISCO_ANYCONNECT = 9;
    
    public AndroidVPNPayload() {
    }
    
    public AndroidVPNPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "VPN", payloadIdentifier, payloadDisplayName);
    }
    
    public void setVpnName(final String value) throws JSONException {
        this.getPayloadJSON().put("VPNName", (Object)value);
    }
    
    public void setVpnType(final String value) throws JSONException {
        this.getPayloadJSON().put("VPNType", (Object)value);
    }
    
    public void setHostServerName(final String value) throws JSONException {
        this.getPayloadJSON().put("HostServerName", (Object)value);
    }
    
    public void setVpnAddPermission(final Boolean value) throws JSONException {
        this.getPayloadJSON().put("AllowAddVpn", (Object)value);
    }
    
    public void setVpnModifyPermission(final Boolean value) throws JSONException {
        this.getPayloadJSON().put("AllowModifyVpn", (Object)value);
    }
    
    public void setAuthName(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("AuthName", (Object)value);
    }
    
    public void setAuthPass(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("AuthPassword", (Object)value);
    }
    
    public void setAllowedApps(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("AllowedApps", (Object)value);
    }
    
    public void setAuthType(final JSONObject jsonObject, final Integer value) throws JSONException {
        jsonObject.put("AuthType", (Object)value);
    }
    
    public void setAuthType(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("AuthType", (Object)value);
    }
    
    public void setActionOnProfile(final JSONObject jsonObject, final Integer value) throws JSONException {
        jsonObject.put("ProfileAction", (Object)value);
    }
    
    public void setUserName(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("UserName", (Object)value);
    }
    
    public void setUserPass(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("Password", (Object)value);
    }
    
    public void setUserName2(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("UserName2", (Object)value);
    }
    
    public void setUserPass2(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("Password2", (Object)value);
    }
    
    public void setCertName(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("ClientCertAlias", (Object)value);
    }
    
    public void setRealmName(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("Realm", (Object)value);
    }
    
    public void setF5Json(final JSONObject jsonObject, final JSONObject f5json) throws JSONException {
        jsonObject.put("F5", (Object)f5json);
    }
    
    public void setIsDefault(final JSONObject jsonObject, final Boolean value) throws JSONException {
        jsonObject.put("Default", (Object)value);
    }
    
    public void setRole(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("Role", (Object)value);
    }
    
    public void setRouteType(final JSONObject jsonObject, final Integer value) throws JSONException {
        jsonObject.put("RouteType", (Object)value);
    }
    
    public void setPerAppAction(final JSONObject jsonObject, final Integer value) throws JSONException {
        jsonObject.put("PerAppAction", (Object)value);
    }
    
    public void setAlwaysOn(final JSONObject jsonObject, final Boolean value) throws JSONException {
        jsonObject.put("AlwaysOn", (Object)value);
    }
    
    public void setAlwaysOn(final Boolean value) throws JSONException {
        this.getPayloadJSON().put("AlwaysOn", (Object)value);
    }
    
    public void setLockDownEnabled(final Boolean value) throws JSONException {
        this.getPayloadJSON().put("LockdownEnabled", (Object)value);
    }
    
    public void setUilessAuth(final JSONObject jsonObject, final Boolean value) throws JSONException {
        jsonObject.put("UiLessAuth", (Object)value);
    }
    
    public void setWebLogOnMode(final JSONObject jsonObject, final Boolean value) throws JSONException {
        jsonObject.put("WebLogOn", (Object)value);
    }
    
    public void setConnectionProtocol(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("ConnectionProtocol", (Object)value);
    }
    
    public void setDisAllowedApps(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("DisallowedApps", (Object)value);
    }
    
    public void setIkeIdentidy(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("IkeIdentity", (Object)value);
    }
    
    public void setFipsMode(final JSONObject jsonObject, final Boolean value) throws JSONException {
        jsonObject.put("FipsMode", (Object)value);
    }
    
    public void setStrictMode(final JSONObject jsonObject, final Boolean value) throws JSONException {
        jsonObject.put("StrictMode", (Object)value);
    }
    
    public void setCertRevocation(final JSONObject jsonObject, final Boolean value) throws JSONException {
        jsonObject.put("CertificateRevocation", (Object)value);
    }
    
    public void setConnectionName(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("ConnectionName", (Object)value);
    }
    
    public void setIsL2tpSecretEnabled(final JSONObject jsonObject, final Boolean value) throws JSONException {
        jsonObject.put("IsL2TPSecretEnabled", (Object)value);
    }
    
    public void setSharedSceret(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("SharedSecret", (Object)value);
    }
    
    public void setIpsecIdentifier(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("IPSecIdentifier", (Object)value);
    }
    
    public void setL2TPSecret(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("L2TPSecret", (Object)value);
    }
    
    public void setUserMessage(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("ENMessage", (Object)value);
    }
    
    public void setModifyVpn(final JSONObject jsonObject, final Boolean value) throws JSONException {
        jsonObject.put("ModifyVPN", (Object)value);
    }
    
    public void setRemoveViaRestriction(final JSONObject jsonObject, final Boolean value) throws JSONException {
        jsonObject.put("RemoveVpnWithRestriction", (Object)value);
    }
    
    public void setAppId(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("AppID", (Object)value);
    }
    
    public void setCertPassword(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("CertificatePassword", (Object)value);
    }
    
    public void setConnectMethoed(final JSONObject jsonObject, final String value) throws JSONException {
        jsonObject.put("ConnectMethod", (Object)value);
    }
    
    public void setVpnAuthDetails(final JSONObject vpnAuthDetails, final Integer connectionType) throws JSONException {
        String vpnType = null;
        switch (connectionType) {
            case 1: {
                vpnType = "PPTP";
                break;
            }
            case 5:
            case 7:
            case 9:
            case 13: {
                vpnType = "ThirdParty";
                break;
            }
            case 14: {
                vpnType = "IPSEC_XAUTH_PSK";
                break;
            }
            case 15: {
                vpnType = "IPSEC_XAUTH_RSA";
                break;
            }
            case 16: {
                vpnType = "IPSEC_IKEV2_PSK";
                break;
            }
            case 17: {
                vpnType = "IPSEC_IKEV2_RSA";
                break;
            }
            case 18: {
                vpnType = "IPSEC_HYBRID_RSA";
                break;
            }
            case 19: {
                vpnType = "L2TP_IPSEC_PSK";
                break;
            }
            case 20: {
                vpnType = "L2TP_IPSEC";
                break;
            }
        }
        this.getPayloadJSON().put(vpnType, (Object)vpnAuthDetails);
    }
    
    public void setCertificate(final JSONObject jsonObject, final String certificate) throws JSONException {
        jsonObject.put("Certificate", (Object)certificate);
    }
    
    public void setCertificatePassword(final JSONObject jsonObject, final String password) throws JSONException {
        jsonObject.put("CertificatePassword", (Object)password);
    }
    
    public void setEnrollType(final String type) throws JSONException {
        this.getPayloadJSON().put("ClientCertEnrollType", (Object)type);
    }
    
    public void setDnsServers(final String serverName) throws JSONException {
        this.getPayloadJSON().put("DnsServers", (Object)serverName);
    }
    
    public void setForwardingRoutes(final String forwardingRoutes) throws JSONException {
        this.getPayloadJSON().put("ForwardingRoutes", (Object)forwardingRoutes);
    }
}
