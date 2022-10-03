package com.me.mdm.server.profiles.config;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class AndroidVPNConfigHandler extends VPNConfigHandler
{
    public static final int ANDROID_PPTP_VPN_TYPE = 1;
    public static final int ANDROID_F5SSL_VPN_TYPE = 5;
    public static final int ANDROID_PULSE_SECURE_SSL_VPN_TYPE = 7;
    public static final int ANDROID_CISCO_ANY_CONNECT_VPN_TYPE = 9;
    public static final int ANDROID_PALOALTO_VPN_TYPE = 13;
    public static final int ANDROID_IPSEC_XAUTH_PSK_VPN_TYPE = 14;
    public static final int ANDROID_IPSEC_XAUTH_RSA_VPN_TYPE = 15;
    public static final int ANDROID_IPSEC_IKEV2_PSK_VPN_TYPE = 16;
    public static final int ANDROID_IPSEC_IKEV2_RSA_VPN_TYPE = 17;
    public static final int ANDROID_IPSEC_HYBRID_RDA_VPN_TYPE = 18;
    public static final int ANDROID_L2TP_PSK_VPN_TYPE = 19;
    public static final int ANDROID_L2TP_RSA_VPN_TYPE = 20;
    public static final String ANDROID_API_VPN_TYPE_L2TP_PSK = "l2tp_psk";
    public static final String ANDROID_API_VPN_TYPE_L2TP_RSA = "l2tp_rsa";
    public static final String ANDROID_API_VPN_TYPE_PPTP = "pptp";
    public static final String ANDROID_API_VPN_TYPE_IPSEC_XAUTH_PSK = "ipsec_xauth_psk";
    public static final String ANDROID_API_VPN_TYPE_IPSEC_XAUTH_RSA = "ipsec_xauth_rsa";
    public static final String ANDROID_API_VPN_TYPE_IPSEC_IKEV2_PSK = "ipsec_ikev2_psk";
    public static final String ANDROID_API_VPN_TYPE_IPSEC_IKEV2_RSA = "ipsec_ikev2_rsa";
    public static final String ANDROID_API_VPN_TYPE_IPSEC_HYBRID_RDA = "ipsec_hybrid_rda";
    public static final String ANDROID_API_VPN_TYPE_PULSESECURE = "pulsesecure";
    public static final String ANDROID_API_VPN_TYPE_F5SSL = "f5ssl";
    public static final String ANDROID_API_VPN_TYPE_CISCOANYCONNECT = "ciscoanyconnect";
    public static final String ANDROID_API_VPN_TYPE_PALOALTO = "paloalto";
    public static final String ANDROID_VPN_TYPE_CISCOANYCONNECT = "CiscoAnyConnect";
    public static final String ANDROID_VPN_TYPE_PULSESECURE = "JuniperSSL";
    public static final String ANDROID_VPN_TYPE_F5SSL = "F5SSL";
    public static final String ANDROID_VPN_TYPE_PALOALTO = "PaloAlto";
    
    @Override
    protected void checkAndAddInnerJSON(final JSONObject configJSON, final DataObject dataObject, final String configName) throws Exception {
        if (configJSON.has("payload_id")) {
            final JSONArray templateConfigProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
            this.constructInnerJsonValue(templateConfigProperties, configJSON, dataObject, configName);
        }
    }
    
    @Override
    public JSONObject apiJSONToServerJSON(final String configName, final JSONObject apiJSON) throws APIHTTPException {
        this.logger.log(Level.INFO, "Started converting API JSON to Server JSON...");
        try {
            final JSONObject result = super.apiJSONToServerJSON(configName, apiJSON);
            final int connectionType = result.getInt("connection_type");
            switch (connectionType) {
                case 1: {
                    result.put("SUB_CONFIG", (Object)"PPTP");
                    break;
                }
                case 5: {
                    result.put("SUB_CONFIG", (Object)"F5SSL");
                    break;
                }
                case 7: {
                    result.put("SUB_CONFIG", (Object)"JuniperSSL");
                    break;
                }
                case 9: {
                    result.put("SUB_CONFIG", (Object)"CiscoAnyConnect");
                    break;
                }
                case 13: {
                    result.put("SUB_CONFIG", (Object)"PaloAlto");
                    break;
                }
                case 14: {
                    result.put("SUB_CONFIG", (Object)"IPSec");
                    break;
                }
                case 15: {
                    result.put("SUB_CONFIG", (Object)"IPSec");
                    break;
                }
                case 16: {
                    result.put("SUB_CONFIG", (Object)"IPSec");
                    break;
                }
                case 17: {
                    result.put("SUB_CONFIG", (Object)"IPSec");
                    break;
                }
                case 18: {
                    result.put("SUB_CONFIG", (Object)"IPSec");
                    break;
                }
                case 19: {
                    result.put("SUB_CONFIG", (Object)"L2TP");
                    break;
                }
                case 20: {
                    result.put("SUB_CONFIG", (Object)"L2TP");
                    break;
                }
            }
            return result;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in apiJSONToServerJSON", (Throwable)e);
            return null;
        }
    }
    
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        final JSONArray jsonArray = super.DOToAPIJSON(dataObject, configName);
        try {
            for (int index = 0; index < jsonArray.length(); ++index) {
                final JSONObject jsonObject = jsonArray.getJSONObject(index);
                final int connectionType = jsonObject.getInt("connection_type");
                final String connectionName = this.getVpnConnectionName(connectionType);
                if (jsonObject.has(connectionName) && jsonObject.getJSONObject(connectionName).has("allowed_apps") && String.valueOf(jsonObject.getJSONObject(connectionName).get("allowed_apps")).length() > 0) {
                    final String[] apps = String.valueOf(jsonObject.getJSONObject(connectionName).get("allowed_apps")).split(",");
                    final JSONArray appDetails = AppsUtil.getInstance().getAppNameFromIdentifierJSON(apps, 2, CustomerInfoUtil.getInstance().getCustomerId());
                    jsonObject.getJSONObject(connectionName).put("allowed_apps", (Object)appDetails);
                }
                else if (jsonObject.has(connectionName) && jsonObject.getJSONObject(connectionName).has("allowed_apps") && String.valueOf(jsonObject.getJSONObject(connectionName).get("allowed_apps")).length() <= 0) {
                    final JSONArray appDetails2 = new JSONArray();
                    jsonObject.getJSONObject(connectionName).put("allowed_apps", (Object)appDetails2);
                }
                final String[] vpnTypes = { "l2tp_psk", "l2tp_rsa", "pptp", "ipsec_xauth_psk", "ipsec_xauth_rsa", "ipsec_ikev2_psk", "ipsec_ikev2_rsa", "ipsec_hybrid_rda", "pulsesecure", "f5ssl", "ciscoanyconnect", "paloalto", "sub_config" };
                final List<String> vpnTypeList = new ArrayList<String>(Arrays.asList(vpnTypes));
                switch (connectionType) {
                    case 1: {
                        vpnTypeList.remove("pptp");
                        break;
                    }
                    case 5: {
                        vpnTypeList.remove("f5ssl");
                        break;
                    }
                    case 7: {
                        vpnTypeList.remove("pulsesecure");
                        break;
                    }
                    case 9: {
                        vpnTypeList.remove("ciscoanyconnect");
                        break;
                    }
                    case 13: {
                        vpnTypeList.remove("paloalto");
                        break;
                    }
                    case 14: {
                        vpnTypeList.remove("ipsec_xauth_psk");
                        break;
                    }
                    case 15: {
                        vpnTypeList.remove("ipsec_xauth_rsa");
                        break;
                    }
                    case 16: {
                        vpnTypeList.remove("ipsec_ikev2_psk");
                        break;
                    }
                    case 17: {
                        vpnTypeList.remove("ipsec_ikev2_rsa");
                        break;
                    }
                    case 18: {
                        vpnTypeList.remove("ipsec_hybrid_rda");
                        break;
                    }
                    case 19: {
                        vpnTypeList.remove("l2tp_psk");
                        break;
                    }
                    case 20: {
                        vpnTypeList.remove("l2tp_rsa");
                        break;
                    }
                }
                this.removeKeyFromJSON(jsonObject, vpnTypeList);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception occurred in DOToAPIJSON");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return jsonArray;
    }
    
    private String getVpnConnectionName(final int connectionType) {
        switch (connectionType) {
            case 1: {
                return "pptp";
            }
            case 5: {
                return "f5ssl";
            }
            case 7: {
                return "pulsesecure";
            }
            case 9: {
                return "ciscoanyconnect";
            }
            case 13: {
                return "paloalto";
            }
            case 14: {
                return "ipsec_xauth_psk";
            }
            case 15: {
                return "ipsec_xauth_rsa";
            }
            case 16: {
                return "ipsec_ikev2_psk";
            }
            case 17: {
                return "ipsec_ikev2_rsa";
            }
            case 18: {
                return "ipsec_hybrid_rda";
            }
            case 19: {
                return "l2tp_psk";
            }
            case 20: {
                return "l2tp_rsa";
            }
            default: {
                return null;
            }
        }
    }
}
