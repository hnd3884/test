package com.adventnet.sym.server.mdm.android.payload.transform;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import java.io.InputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidVPNPayload;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AndroidVpnPayload implements DO2AndroidPayload
{
    public Logger logger;
    
    public DO2AndroidVpnPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidVPNPayload payload = null;
        Long certID = 0L;
        try {
            payload = new AndroidVPNPayload("1.0", "com.mdm.mobiledevice.vpn", "VPN Policy");
            final Iterator iterator = dataObject.getRows("VpnPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final JSONObject authDetails = new JSONObject();
                final Long configdataItem = (Long)row.get("CONFIG_DATA_ITEM_ID");
                final Integer connectionType = (Integer)row.get("CONNECTION_TYPE");
                Boolean alwaysOn = (Boolean)row.get("ALWAYS_ON");
                final Boolean lockdownEnabled = (Boolean)row.get("LOCKDOWN_ENABLED");
                payload.setLockDownEnabled(lockdownEnabled);
                switch (connectionType) {
                    case 1: {
                        final String vpnType = "PPTP";
                        final String vpnName = (String)row.get("CONNECTION_NAME");
                        final Iterator subConfigIterator = dataObject.getRows("VpnPPTP");
                        final Row configrow = subConfigIterator.next();
                        final String serverName = (String)configrow.get("SERVER_NAME");
                        final String authName = (String)configrow.get("ACCOUNT");
                        String password = "";
                        if (configrow.get("PASSWORD_ID") != null) {
                            final Long passwordID = (Long)configrow.get("PASSWORD_ID");
                            password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID.toString());
                        }
                        final Boolean allowModifyVpn = Boolean.FALSE;
                        payload.setVpnType(vpnType);
                        payload.setVpnName(vpnName);
                        payload.setHostServerName(serverName);
                        payload.setVpnModifyPermission(allowModifyVpn);
                        payload.setAlwaysOn(alwaysOn);
                        payload.setAuthName(authDetails, authName);
                        payload.setAuthPass(authDetails, password);
                        payload.setVpnAuthDetails(authDetails, connectionType);
                        continue;
                    }
                    case 7: {
                        final String vpnType = "ThirdParty";
                        final String vpnName = (String)row.get("CONNECTION_NAME");
                        final Iterator subConfigIterator = dataObject.getRows("VpnJuniperSSL");
                        final Row configPulse = subConfigIterator.next();
                        final String serverName = (String)configPulse.get("SERVER_NAME");
                        final String allowedApps = (String)configPulse.get("ALLOWED_APPS");
                        final Integer authType = (Integer)configPulse.get("AUTH_TYPE");
                        final Integer actionOnProfile = (Integer)configPulse.get("ACTION_ON_PROFILE");
                        final String authName = (String)configPulse.get("ACCOUNT");
                        String password = "";
                        if (configPulse.get("PASSWORD_ID") != null) {
                            final Long passwordID2 = (Long)configPulse.get("PASSWORD_ID");
                            password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID2.toString());
                        }
                        final String altusername = (String)configPulse.get("ALT_USERNAME");
                        String altpassword = (String)configPulse.get("ALT_PASSWORD");
                        if (configPulse.get("ALT_PASSWORD_ID") != null) {
                            final Long altPasswordID = (Long)configPulse.get("ALT_PASSWORD_ID");
                            altpassword = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(altPasswordID.toString());
                        }
                        final String realm = (String)configPulse.get("REALM");
                        final String role = (String)configPulse.get("ROLE");
                        final Boolean defaultVpn = (Boolean)configPulse.get("IS_DEAFAULT_VPN");
                        final Integer route_type = (Integer)configPulse.get("ROUTE_TYPE");
                        final Integer perAppAction = (Integer)configPulse.get("APP_VPN_ACTION");
                        alwaysOn = (Boolean)configPulse.get("ALWAYS_ON");
                        final Boolean uilessAuth = (Boolean)configPulse.get("UILESS_AUTH");
                        certID = (Long)configPulse.get("CERTIFICATE_ID");
                        final String certName = this.getCertName(dataObject, certID);
                        payload.setVpnType(vpnType);
                        payload.setVpnName(vpnName);
                        payload.setHostServerName(serverName);
                        payload.setAppId(authDetails, "PulseSecure");
                        payload.setAllowedApps(authDetails, allowedApps);
                        payload.setAuthType(authDetails, authType);
                        payload.setActionOnProfile(authDetails, actionOnProfile);
                        payload.setUserName(authDetails, authName);
                        payload.setUserPass(authDetails, password);
                        payload.setUserName2(authDetails, altusername);
                        payload.setUserPass2(authDetails, altpassword);
                        payload.setCertName(authDetails, certName);
                        payload.setRealmName(authDetails, realm);
                        payload.setIsDefault(authDetails, defaultVpn);
                        payload.setRole(authDetails, role);
                        payload.setRouteType(authDetails, route_type);
                        payload.setPerAppAction(authDetails, perAppAction);
                        payload.setAlwaysOn(authDetails, alwaysOn);
                        payload.setUilessAuth(authDetails, uilessAuth);
                        this.setCertificateData(dataObject, certID, payload, authDetails);
                        payload.setVpnAuthDetails(authDetails, connectionType);
                        continue;
                    }
                    case 9: {
                        final String vpnType = "ThirdParty";
                        final String vpnName = (String)row.get("CONNECTION_NAME");
                        final Iterator subConfigIterator = dataObject.getRows("VpnCisco");
                        final Row configAnyConnect = subConfigIterator.next();
                        final String serverName = (String)configAnyConnect.get("SERVER_NAME");
                        final String connectionProtocol = (String)configAnyConnect.get("VPN_CONNECTION_PROTOCOL");
                        final String ikeIdentity = (String)configAnyConnect.get("IKE_IDENTITY");
                        final String ipsecAuthType = (String)configAnyConnect.get("IPSEC_AUTH_TYPE");
                        alwaysOn = (Boolean)configAnyConnect.get("ALWAYS_ON");
                        final Boolean fipsMode = (Boolean)configAnyConnect.get("FIPS_MODE");
                        final Boolean strictMode = (Boolean)configAnyConnect.get("STRICT_MODE");
                        final Boolean certRevocation = (Boolean)configAnyConnect.get("CERT_REVOCATION");
                        certID = (Long)configAnyConnect.get("CERTIFICATE_ID");
                        final String certName = this.getCertName(dataObject, certID);
                        payload.setVpnType(vpnType);
                        payload.setVpnName(vpnName);
                        payload.setHostServerName(serverName);
                        payload.setAppId(authDetails, "AnyConnect");
                        payload.setConnectionProtocol(authDetails, connectionProtocol);
                        payload.setAuthType(authDetails, ipsecAuthType);
                        payload.setIkeIdentidy(authDetails, ikeIdentity);
                        payload.setCertName(authDetails, certName);
                        payload.setAlwaysOn(authDetails, alwaysOn);
                        payload.setFipsMode(authDetails, fipsMode);
                        payload.setStrictMode(authDetails, strictMode);
                        payload.setCertRevocation(authDetails, certRevocation);
                        payload.setConnectionName(authDetails, vpnName);
                        this.setCertificateData(dataObject, certID, payload, authDetails);
                        payload.setVpnAuthDetails(authDetails, connectionType);
                        continue;
                    }
                    case 19: {
                        final String vpnType = "L2TP_IPSEC_PSK";
                        final String vpnName = (String)row.get("CONNECTION_NAME");
                        final Iterator subConfigIterator = dataObject.getRows("VpnL2TP");
                        final Row configL2tpPsk = subConfigIterator.next();
                        final String serverName = (String)configL2tpPsk.get("SERVER_NAME");
                        final Boolean allowModifyVpn = Boolean.FALSE;
                        final String authName = (String)configL2tpPsk.get("ACCOUNT");
                        String password = "";
                        if (configL2tpPsk.get("PASSWORD_ID") != null) {
                            final Long passwordID3 = (Long)configL2tpPsk.get("PASSWORD_ID");
                            password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID3.toString());
                        }
                        String sharedSecret = "";
                        if (configL2tpPsk.get("SHARED_SECRET_ID") != null) {
                            final Long sharedSecretID = (Long)configL2tpPsk.get("SHARED_SECRET_ID");
                            sharedSecret = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(sharedSecretID.toString());
                        }
                        final Boolean isL2TPSecretEnabled = (Boolean)configL2tpPsk.get("IS_L2TP_SECRET_ENABLED");
                        String l2TPSecret = "";
                        if (configL2tpPsk.get("L2TP_SECRET_ID") != null) {
                            final Long l2tpSecretID = (Long)configL2tpPsk.get("L2TP_SECRET_ID");
                            l2TPSecret = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(l2tpSecretID.toString());
                        }
                        final String ipsecIdentifier = (String)configL2tpPsk.get("IPSEC_IDENTIFIER");
                        final String dnsServers = (String)configL2tpPsk.get("DNS_SERVERS");
                        final String forwardingRoutes = (String)configL2tpPsk.get("FORWARDING_ROUTES");
                        payload.setVpnType(vpnType);
                        payload.setVpnName(vpnName);
                        payload.setHostServerName(serverName);
                        payload.setVpnModifyPermission(allowModifyVpn);
                        payload.setAlwaysOn(alwaysOn);
                        payload.setAuthName(authDetails, authName);
                        payload.setAuthPass(authDetails, password);
                        payload.setIsL2tpSecretEnabled(authDetails, isL2TPSecretEnabled);
                        payload.setL2TPSecret(authDetails, l2TPSecret);
                        payload.setSharedSceret(authDetails, sharedSecret);
                        payload.setIpsecIdentifier(authDetails, ipsecIdentifier);
                        payload.setVpnAuthDetails(authDetails, connectionType);
                        payload.setDnsServers(dnsServers);
                        payload.setForwardingRoutes(forwardingRoutes);
                        continue;
                    }
                    case 20: {
                        final String vpnType = "L2TP_IPSEC";
                        final String vpnName = (String)row.get("CONNECTION_NAME");
                        final Iterator subConfigIterator = dataObject.getRows("VpnL2TP");
                        final Row configL2tpRsa = subConfigIterator.next();
                        final String serverName = (String)configL2tpRsa.get("SERVER_NAME");
                        final Boolean allowModifyVpn = Boolean.FALSE;
                        final String authName = (String)configL2tpRsa.get("ACCOUNT");
                        String password = "";
                        if (configL2tpRsa.get("PASSWORD_ID") != null) {
                            final Long passwordID4 = (Long)configL2tpRsa.get("PASSWORD_ID");
                            password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID4.toString());
                        }
                        String sharedSecret = "";
                        if (configL2tpRsa.get("SHARED_SECRET_ID") != null) {
                            final Long sharedSecretID2 = (Long)configL2tpRsa.get("SHARED_SECRET_ID");
                            sharedSecret = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(sharedSecretID2.toString());
                        }
                        final Boolean isL2TPSecretEnabled = (Boolean)configL2tpRsa.get("IS_L2TP_SECRET_ENABLED");
                        String l2TPSecret = "";
                        if (configL2tpRsa.get("L2TP_SECRET_ID") != null) {
                            final Long l2tpSecretID2 = (Long)configL2tpRsa.get("L2TP_SECRET_ID");
                            l2TPSecret = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(l2tpSecretID2.toString());
                        }
                        certID = (Long)configL2tpRsa.get("USER_CERTIFICATE_ID");
                        final String ipsecIdentifier = (String)configL2tpRsa.get("IPSEC_IDENTIFIER");
                        final String dnsServers = (String)configL2tpRsa.get("DNS_SERVERS");
                        final String forwardingRoutes = (String)configL2tpRsa.get("FORWARDING_ROUTES");
                        payload.setVpnType(vpnType);
                        payload.setVpnName(vpnName);
                        payload.setHostServerName(serverName);
                        payload.setVpnModifyPermission(allowModifyVpn);
                        payload.setAlwaysOn(alwaysOn);
                        payload.setAuthName(authDetails, authName);
                        payload.setAuthPass(authDetails, password);
                        payload.setIsL2tpSecretEnabled(authDetails, isL2TPSecretEnabled);
                        payload.setL2TPSecret(authDetails, l2TPSecret);
                        payload.setSharedSceret(authDetails, sharedSecret);
                        payload.setIpsecIdentifier(authDetails, ipsecIdentifier);
                        payload.setVpnAuthDetails(authDetails, connectionType);
                        payload.setDnsServers(dnsServers);
                        payload.setForwardingRoutes(forwardingRoutes);
                        continue;
                    }
                    case 14: {
                        final String vpnType = "IPSEC_XAUTH_PSK";
                        final String vpnName = (String)row.get("CONNECTION_NAME");
                        final Iterator subConfigIterator = dataObject.getRows("VpnIPSec");
                        final Row configIpsecPsk = subConfigIterator.next();
                        final String serverName = (String)configIpsecPsk.get("SERVER_NAME");
                        final Boolean allowModifyVpn = Boolean.FALSE;
                        final String authName = (String)configIpsecPsk.get("ACCOUNT");
                        String password = "";
                        if (configIpsecPsk.get("PASSWORD_ID") != null) {
                            final Long passwordID5 = (Long)configIpsecPsk.get("PASSWORD_ID");
                            password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID5.toString());
                        }
                        String sharedSecret = "";
                        if (configIpsecPsk.get("SHARED_SECRET_ID") != null) {
                            final Long sharedSecretID3 = (Long)configIpsecPsk.get("SHARED_SECRET_ID");
                            sharedSecret = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(sharedSecretID3.toString());
                        }
                        final String ipsecIdentifier = (String)configIpsecPsk.get("IPSEC_IDENTIFIER");
                        final String dnsServers = (String)configIpsecPsk.get("DNS_SERVERS");
                        final String forwardingRoutes = (String)configIpsecPsk.get("FORWARDING_ROUTES");
                        payload.setVpnType(vpnType);
                        payload.setVpnName(vpnName);
                        payload.setHostServerName(serverName);
                        payload.setVpnModifyPermission(allowModifyVpn);
                        payload.setAlwaysOn(alwaysOn);
                        payload.setAuthName(authDetails, authName);
                        payload.setAuthPass(authDetails, password);
                        payload.setSharedSceret(authDetails, sharedSecret);
                        payload.setIpsecIdentifier(authDetails, ipsecIdentifier);
                        payload.setVpnAuthDetails(authDetails, connectionType);
                        payload.setDnsServers(dnsServers);
                        payload.setForwardingRoutes(forwardingRoutes);
                        continue;
                    }
                    case 16: {
                        final String vpnType = "IPSEC_IKEV2_PSK";
                        final String vpnName = (String)row.get("CONNECTION_NAME");
                        final Iterator subConfigIterator = dataObject.getRows("VpnIPSec");
                        final Row configIpsecPsk2 = subConfigIterator.next();
                        final String serverName = (String)configIpsecPsk2.get("SERVER_NAME");
                        final Boolean allowModifyVpn = Boolean.FALSE;
                        final String authName = (String)configIpsecPsk2.get("ACCOUNT");
                        String password = "";
                        if (configIpsecPsk2.get("PASSWORD_ID") != null) {
                            final Long passwordID6 = (Long)configIpsecPsk2.get("PASSWORD_ID");
                            password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID6.toString());
                        }
                        String sharedSecret = "";
                        if (configIpsecPsk2.get("SHARED_SECRET_ID") != null) {
                            final Long sharedSecretID4 = (Long)configIpsecPsk2.get("SHARED_SECRET_ID");
                            sharedSecret = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(sharedSecretID4.toString());
                        }
                        final String ipsecIdentifier = (String)configIpsecPsk2.get("IPSEC_IDENTIFIER");
                        final String dnsServers = (String)configIpsecPsk2.get("DNS_SERVERS");
                        final String forwardingRoutes = (String)configIpsecPsk2.get("FORWARDING_ROUTES");
                        payload.setVpnType(vpnType);
                        payload.setVpnName(vpnName);
                        payload.setHostServerName(serverName);
                        payload.setVpnModifyPermission(allowModifyVpn);
                        payload.setAlwaysOn(alwaysOn);
                        payload.setAuthName(authDetails, authName);
                        payload.setAuthPass(authDetails, password);
                        payload.setSharedSceret(authDetails, sharedSecret);
                        payload.setIpsecIdentifier(authDetails, ipsecIdentifier);
                        payload.setVpnAuthDetails(authDetails, connectionType);
                        payload.setDnsServers(dnsServers);
                        payload.setForwardingRoutes(forwardingRoutes);
                        continue;
                    }
                    case 15: {
                        final String vpnType = "IPSEC_XAUTH_RSA";
                        final String vpnName = (String)row.get("CONNECTION_NAME");
                        final Iterator subConfigIterator = dataObject.getRows("VpnIPSec");
                        final Row configIpsecRsa = subConfigIterator.next();
                        final String serverName = (String)configIpsecRsa.get("SERVER_NAME");
                        final Boolean allowModifyVpn = Boolean.FALSE;
                        final String authName = (String)configIpsecRsa.get("ACCOUNT");
                        String password = "";
                        if (configIpsecRsa.get("PASSWORD_ID") != null) {
                            final Long passwordID7 = (Long)configIpsecRsa.get("PASSWORD_ID");
                            password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID7.toString());
                        }
                        String sharedSecret = "";
                        if (configIpsecRsa.get("SHARED_SECRET_ID") != null) {
                            final Long sharedSecretID5 = (Long)configIpsecRsa.get("SHARED_SECRET_ID");
                            sharedSecret = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(sharedSecretID5.toString());
                        }
                        final String ipsecIdentifier = (String)configIpsecRsa.get("IPSEC_IDENTIFIER");
                        final String dnsServers = (String)configIpsecRsa.get("DNS_SERVERS");
                        final String forwardingRoutes = (String)configIpsecRsa.get("FORWARDING_ROUTES");
                        payload.setVpnType(vpnType);
                        payload.setVpnName(vpnName);
                        payload.setHostServerName(serverName);
                        payload.setVpnModifyPermission(allowModifyVpn);
                        payload.setAlwaysOn(alwaysOn);
                        payload.setAuthName(authDetails, authName);
                        payload.setAuthPass(authDetails, password);
                        payload.setSharedSceret(authDetails, sharedSecret);
                        payload.setIpsecIdentifier(authDetails, ipsecIdentifier);
                        payload.setVpnAuthDetails(authDetails, connectionType);
                        payload.setDnsServers(dnsServers);
                        payload.setForwardingRoutes(forwardingRoutes);
                        continue;
                    }
                    case 17: {
                        final String vpnType = "IPSEC_IKEV2_RSA";
                        final String vpnName = (String)row.get("CONNECTION_NAME");
                        final Iterator subConfigIterator = dataObject.getRows("VpnIPSec");
                        final Row configIpsecRsa2 = subConfigIterator.next();
                        final String serverName = (String)configIpsecRsa2.get("SERVER_NAME");
                        final Boolean allowModifyVpn = Boolean.FALSE;
                        final String authName = (String)configIpsecRsa2.get("ACCOUNT");
                        String password = "";
                        if (configIpsecRsa2.get("PASSWORD_ID") != null) {
                            final Long passwordID8 = (Long)configIpsecRsa2.get("PASSWORD_ID");
                            password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID8.toString());
                        }
                        String sharedSecret = "";
                        if (configIpsecRsa2.get("SHARED_SECRET_ID") != null) {
                            final Long sharedSecretID6 = (Long)configIpsecRsa2.get("SHARED_SECRET_ID");
                            sharedSecret = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(sharedSecretID6.toString());
                        }
                        final String ipsecIdentifier = (String)configIpsecRsa2.get("IPSEC_IDENTIFIER");
                        final String dnsServers = (String)configIpsecRsa2.get("DNS_SERVERS");
                        final String forwardingRoutes = (String)configIpsecRsa2.get("FORWARDING_ROUTES");
                        payload.setVpnType(vpnType);
                        payload.setVpnName(vpnName);
                        payload.setHostServerName(serverName);
                        payload.setVpnModifyPermission(allowModifyVpn);
                        payload.setAlwaysOn(alwaysOn);
                        payload.setAuthName(authDetails, authName);
                        payload.setAuthPass(authDetails, password);
                        payload.setSharedSceret(authDetails, sharedSecret);
                        payload.setIpsecIdentifier(authDetails, ipsecIdentifier);
                        payload.setVpnAuthDetails(authDetails, connectionType);
                        payload.setDnsServers(dnsServers);
                        payload.setForwardingRoutes(forwardingRoutes);
                        continue;
                    }
                    case 18: {
                        final String vpnType = "IPSEC_HYBRID_RSA";
                        final String vpnName = (String)row.get("CONNECTION_NAME");
                        final Iterator subConfigIterator = dataObject.getRows("VpnIPSec");
                        final Row configIpsecHybrid = subConfigIterator.next();
                        final String serverName = (String)configIpsecHybrid.get("SERVER_NAME");
                        final Boolean allowModifyVpn = Boolean.FALSE;
                        final String authName = (String)configIpsecHybrid.get("ACCOUNT");
                        String password = "";
                        if (configIpsecHybrid.get("PASSWORD_ID") != null) {
                            final Long passwordID9 = (Long)configIpsecHybrid.get("PASSWORD_ID");
                            password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID9.toString());
                        }
                        String sharedSecret = "";
                        if (configIpsecHybrid.get("SHARED_SECRET_ID") != null) {
                            final Long sharedSecretID7 = (Long)configIpsecHybrid.get("SHARED_SECRET_ID");
                            sharedSecret = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(sharedSecretID7.toString());
                        }
                        final String ipsecIdentifier = (String)configIpsecHybrid.get("IPSEC_IDENTIFIER");
                        final String dnsServers = (String)configIpsecHybrid.get("DNS_SERVERS");
                        final String forwardingRoutes = (String)configIpsecHybrid.get("FORWARDING_ROUTES");
                        payload.setVpnType(vpnType);
                        payload.setVpnName(vpnName);
                        payload.setHostServerName(serverName);
                        payload.setVpnModifyPermission(allowModifyVpn);
                        payload.setAlwaysOn(alwaysOn);
                        payload.setAuthName(authDetails, authName);
                        payload.setAuthPass(authDetails, password);
                        payload.setSharedSceret(authDetails, sharedSecret);
                        payload.setIpsecIdentifier(authDetails, ipsecIdentifier);
                        payload.setVpnAuthDetails(authDetails, connectionType);
                        payload.setDnsServers(dnsServers);
                        payload.setForwardingRoutes(forwardingRoutes);
                        continue;
                    }
                    case 5: {
                        final String vpnType = "ThirdParty";
                        final String vpnName = (String)row.get("CONNECTION_NAME");
                        final Iterator subConfigIterator = dataObject.getRows("VpnF5SSL");
                        final Row configF5Ssl = subConfigIterator.next();
                        final String serverName = (String)configF5Ssl.get("SERVER_NAME");
                        final Boolean modifyVpn = Boolean.FALSE;
                        final String message = (String)configF5Ssl.get("USER_MESSAGE");
                        final String allowedApps = (String)configF5Ssl.get("ALLOWED_APPS");
                        final Boolean fipsMode = (Boolean)configF5Ssl.get("FIPS_MODE");
                        final Boolean webLogon = (Boolean)configF5Ssl.get("WEB_LOGON_MODE");
                        final String authName = (String)configF5Ssl.get("ACCOUNT");
                        String password = "";
                        if (configF5Ssl.get("PASSWORD_ID") != null) {
                            final Long passwordID10 = (Long)configF5Ssl.get("PASSWORD_ID");
                            password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID10.toString());
                        }
                        final String disAllowedApps = (String)configF5Ssl.get("DISALLOWED_APPS");
                        certID = (Long)configF5Ssl.get("CERTIFICATE_ID");
                        final String certName = this.getCertName(dataObject, certID);
                        payload.setVpnType(vpnType);
                        payload.setVpnName(vpnName);
                        payload.setHostServerName(serverName);
                        payload.setAlwaysOn(alwaysOn);
                        payload.setAppId(authDetails, "F5");
                        payload.setModifyVpn(authDetails, modifyVpn);
                        payload.setUserMessage(authDetails, message);
                        payload.setCertName(authDetails, certName);
                        final JSONObject f5JsonObject = new JSONObject();
                        payload.setCertName(f5JsonObject, certName);
                        payload.setAllowedApps(f5JsonObject, allowedApps);
                        payload.setFipsMode(f5JsonObject, fipsMode);
                        payload.setWebLogOnMode(f5JsonObject, webLogon);
                        payload.setUserName(f5JsonObject, authName);
                        payload.setUserPass(f5JsonObject, password);
                        payload.setDisAllowedApps(f5JsonObject, disAllowedApps);
                        payload.setF5Json(authDetails, f5JsonObject);
                        this.setCertificateData(dataObject, certID, payload, authDetails);
                        payload.setVpnAuthDetails(authDetails, connectionType);
                        continue;
                    }
                    case 13: {
                        final String vpnType = "ThirdParty";
                        final String vpnName = (String)row.get("CONNECTION_NAME");
                        final Iterator subConfigIterator = dataObject.getRows("VpnPaloAlto");
                        final Row configPaloAlto = subConfigIterator.next();
                        final String serverName = (String)configPaloAlto.get("SERVER_NAME");
                        final String allowedApps = (String)configPaloAlto.get("ALLOWED_APPS");
                        String certPAssword = "";
                        if (configPaloAlto.get("CLIENT_CERT_PASSWORD_ID") != null) {
                            final Long passwordID11 = (Long)configPaloAlto.get("CLIENT_CERT_PASSWORD_ID");
                            certPAssword = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID11.toString());
                        }
                        final String authName = (String)configPaloAlto.get("ACCOUNT");
                        String password = "";
                        if (configPaloAlto.get("PASSWORD_ID") != null) {
                            final Long passwordID11 = (Long)configPaloAlto.get("PASSWORD_ID");
                            password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID11.toString());
                        }
                        final String connectionMethod = (String)configPaloAlto.get("CONNECT_METHOD");
                        final Boolean removeViaRestriction = (Boolean)configPaloAlto.get("REMOVE_VPN_VIA_RESTRICTION");
                        certID = (Long)configPaloAlto.get("CERTIFICATE_ID");
                        final String certName = this.getCertName(dataObject, certID);
                        payload.setVpnType(vpnType);
                        payload.setVpnName(vpnName);
                        payload.setAlwaysOn(alwaysOn);
                        payload.setAppId(authDetails, "Paloalto");
                        payload.setAllowedApps(authDetails, allowedApps);
                        payload.setCertName(authDetails, certName);
                        payload.setHostServerName(serverName);
                        payload.setCertPassword(authDetails, certPAssword);
                        payload.setUserName(authDetails, authName);
                        payload.setUserPass(authDetails, password);
                        payload.setConnectMethoed(authDetails, connectionMethod);
                        payload.setRemoveViaRestriction(authDetails, removeViaRestriction);
                        this.setCertificateData(dataObject, certID, payload, authDetails);
                        payload.setVpnAuthDetails(authDetails, connectionType);
                        continue;
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "DO2AndroidVPNPayload:Exception while creating VPN payload ", ex);
        }
        return payload;
    }
    
    byte[] certificateBytesArray(final String fileName) throws IOException {
        InputStream inputStream = null;
        byte[] bytes = null;
        try {
            inputStream = ApiFactoryProvider.getFileAccessAPI().getInputStream(fileName);
            if (inputStream != null) {
                bytes = IOUtils.toByteArray(inputStream);
                if (bytes.length > Integer.MAX_VALUE) {
                    throw new IOException("The file is too big");
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger(DO2AndroidVpnPayload.class.getName()).log(Level.SEVERE, "Exception in certificateBytesArray", e);
        }
        finally {
            inputStream.close();
        }
        return bytes;
    }
    
    void setCertificateData(final DataObject data, final Long certId, final AndroidVPNPayload payload, final JSONObject json) {
        if (certId != null && certId != -1L) {
            try {
                final Long customerID = (Long)data.getRow("CollnToCustomerRel").get("CUSTOMER_ID");
                final DataObject certDO = ProfileCertificateUtil.getInstance().getCertificateInfo(customerID, certId);
                final Criteria identitycertCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certId, 0);
                final DataObject certificatesDO = ProfileCertificateUtil.getCertificateDO(identitycertCriteria);
                final Row identityRow = certificatesDO.getFirstRow("Certificates");
                final int type = (int)identityRow.get("CERTIFICATE_TYPE");
                if (type == 0) {
                    payload.setEnrollType("Raw");
                }
                else if (type == 1) {
                    payload.setEnrollType("Scep");
                }
                if (certDO != null && !certDO.isEmpty() && type == 0) {
                    final Row certRow = certDO.getFirstRow("CredentialCertificateInfo");
                    final Long certificateId = (Long)certRow.get("CERTIFICATE_ID");
                    final String password = PayloadSecretFieldsHandler.getInstance().constructPayloadCertificatePassword(certificateId.toString());
                    final String certificate = PayloadSecretFieldsHandler.getInstance().constructPayloadCertificate(certificateId.toString());
                    payload.setCertificate(json, certificate);
                    payload.setCertificatePassword(json, password);
                }
            }
            catch (final DataAccessException e) {
                e.printStackTrace();
            }
            catch (final IOException e2) {
                e2.printStackTrace();
            }
            catch (final Exception e3) {
                e3.printStackTrace();
            }
        }
    }
    
    String getCertName(final DataObject data, final Long certId) {
        String certFileName = null;
        if (certId != null) {
            try {
                final Long customerID = (Long)data.getRow("CollnToCustomerRel").get("CUSTOMER_ID");
                final DataObject certDO = ProfileCertificateUtil.getInstance().getCertificateInfo(customerID, certId);
                if (certDO != null && !certDO.isEmpty()) {
                    final Row certRow = certDO.getFirstRow("CredentialCertificateInfo");
                    certFileName = (String)certRow.get("CERTIFICATE_FILE_NAME");
                    return certFileName;
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception while getting Certificate name in Vpn");
            }
        }
        return certFileName;
    }
}
