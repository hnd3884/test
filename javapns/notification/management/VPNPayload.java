package javapns.notification.management;

import org.json.JSONException;
import org.json.JSONObject;

class VPNPayload extends MobileConfigPayload
{
    public static final String VPNTYPE_L2TP = "L2TP";
    public static final String VPNTYPE_PPTP = "PPTP";
    public static final String VPNTYPE_IPSec = "IPSec";
    
    public VPNPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName, final String userDefinedName, final boolean overridePrimary, final String vpnType) throws JSONException {
        super(payloadVersion, "com.apple.vpn.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
        final JSONObject payload = this.getPayload();
        payload.put("UserDefinedName", (Object)userDefinedName);
        payload.put("OverridePrimary", overridePrimary);
        payload.put("VPNType", (Object)vpnType);
    }
    
    public JSONObject addPPP() throws JSONException {
        final JSONObject object = new JSONObject();
        this.getPayload().put("PPP", (Object)object);
        return object;
    }
    
    public JSONObject addIPSec() throws JSONException {
        final JSONObject object = new JSONObject();
        this.getPayload().put("IPSec", (Object)object);
        return object;
    }
}
