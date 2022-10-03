package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.persistence.Row;

public class ONCPayloadUtil
{
    public static final int PROXY_TYPE_MANUAL = 1;
    public static final int PROXY_TYPE_PAC = 2;
    
    public String getAuthProtocol(final Row wifiEnterprise) {
        String eapMethod = "PEAP";
        final boolean peap = (boolean)wifiEnterprise.get("PEAP");
        final boolean tls = (boolean)wifiEnterprise.get("TLS");
        final boolean ttls = (boolean)wifiEnterprise.get("TTLS");
        final boolean eapSim = (boolean)wifiEnterprise.get("EAP_SIM");
        final boolean leap = (boolean)wifiEnterprise.get("LEAP");
        if (peap) {
            eapMethod = "PEAP";
        }
        else if (tls) {
            eapMethod = "EAP-TLS";
        }
        else if (ttls) {
            eapMethod = "EAP-TTLS";
        }
        else if (eapSim) {
            eapMethod = "EAP-SIM";
        }
        else if (leap) {
            eapMethod = "LEAP";
        }
        return eapMethod;
    }
    
    public String getInnerProtocol(final int protocol) {
        switch (protocol) {
            case 0: {
                return "PAP";
            }
            case 2: {
                return "MSCHAP";
            }
            case 3: {
                return "MSCHAPv2";
            }
            case 4: {
                return "Automatic";
            }
            case 5: {
                return "MD5";
            }
            case 6: {
                return "GTC";
            }
            default: {
                return "Automatic";
            }
        }
    }
}
