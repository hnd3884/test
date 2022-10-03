package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import java.util.ArrayList;
import com.dd.plist.NSArray;
import com.me.mdm.server.profiles.config.IOSWifiConfigHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.WifiPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2WifiPayload implements DO2Payload
{
    private static final int WIFI_NONE = 0;
    private static final int WIFI_PERSONAL_WEP = 1;
    private static final int WIFI_PERSONAL_WPA = 2;
    private static final int WIFI_PERSONAL_ANY = 3;
    private static final int WIFI_ENTERPRISE_WEP = 4;
    private static final int WIFI_ENTERPRISE_WPA = 5;
    private static final int WIFI_ENTERPRISE_ANY = 6;
    private static final int WIFI_ENTERPRISE_WPA3 = 7;
    private static final Logger LOGGER;
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        WifiPayload wifiPayload = null;
        final WifiPayload[] payloadArray = { null };
        try {
            if (dataObject != null) {
                final Iterator iterator = dataObject.getRows("WifiPolicy");
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final Long configDataItemID = (Long)payloadRow.get("CONFIG_DATA_ITEM_ID");
                    final Integer encryptionType = (Integer)payloadRow.get("SECURITY_TYPE");
                    final String serviceSetIdentifier = (String)payloadRow.get("SERVICE_SET_IDENTIFIER");
                    final Boolean autoJoin = (Boolean)payloadRow.get("AUTO_JOIN");
                    final Boolean hiddenNetwork = (Boolean)payloadRow.get("HIDDEN_NETWORK");
                    wifiPayload = new WifiPayload(1, "MDM", "com.mdm.mobiledevice.wifi", "Wifi Profile Configuration");
                    wifiPayload.setSSID_STR(serviceSetIdentifier);
                    wifiPayload.setAutoJoin(autoJoin);
                    wifiPayload.setHIDDEN_NETWORK(hiddenNetwork);
                    switch (encryptionType) {
                        case 0: {
                            wifiPayload.setEncryptionType("None");
                            break;
                        }
                        case 1: {
                            wifiPayload.setEncryptionType("WEP");
                            wifiPayload = this.createPersonalEncryptions(wifiPayload, dataObject, configDataItemID);
                            break;
                        }
                        case 2: {
                            wifiPayload.setEncryptionType("WPA");
                            wifiPayload = this.createPersonalEncryptions(wifiPayload, dataObject, configDataItemID);
                            break;
                        }
                        case 3: {
                            wifiPayload.setEncryptionType("Any");
                            wifiPayload = this.createPersonalEncryptions(wifiPayload, dataObject, configDataItemID);
                            break;
                        }
                        case 4: {
                            wifiPayload.initializeDicts();
                            wifiPayload.setEncryptionType("WEP");
                            wifiPayload = this.createEnterpriseEncryption(wifiPayload, dataObject, configDataItemID);
                            break;
                        }
                        case 5: {
                            wifiPayload.initializeDicts();
                            wifiPayload.setEncryptionType("WPA2");
                            wifiPayload = this.createEnterpriseEncryption(wifiPayload, dataObject, configDataItemID);
                            break;
                        }
                        case 6: {
                            wifiPayload.initializeDicts();
                            wifiPayload.setEncryptionType("Any");
                            wifiPayload = this.createEnterpriseEncryption(wifiPayload, dataObject, configDataItemID);
                            break;
                        }
                        case 7: {
                            wifiPayload.setEncryptionType("WPA3");
                            wifiPayload = this.createPersonalEncryptions(wifiPayload, dataObject, configDataItemID);
                            break;
                        }
                    }
                    wifiPayload = this.createProxyPayload(wifiPayload, payloadRow);
                    this.addAppleSpecificDetails(wifiPayload, configDataItemID, dataObject);
                }
            }
        }
        catch (final Exception exp) {
            DO2WifiPayload.LOGGER.log(Level.SEVERE, "Exception in creating wifi payload", exp);
        }
        payloadArray[0] = wifiPayload;
        return payloadArray;
    }
    
    private void addAppleSpecificDetails(final WifiPayload payload, final Long configDataItemID, final DataObject dataObject) throws Exception {
        if (!dataObject.containsTable("AppleWifiPolicy")) {
            return;
        }
        final Row row = dataObject.getRow("AppleWifiPolicy", new Criteria(Column.getColumn("AppleWifiPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0));
        if (row == null) {
            return;
        }
        final Integer setupModes = (Integer)row.get("SETUP_MODES");
        final Boolean disableMACRandomization = (Boolean)row.get("DISABLE_MAC_RANDOMIZE");
        if (setupModes != null) {
            final NSArray setupModeArray = new IOSWifiConfigHandler().getSetupModesNSArray(setupModes);
            if (setupModeArray != null) {
                payload.setSetupModes(setupModeArray);
            }
        }
        payload.setDisableAssociationMACRandomization(disableMACRandomization);
    }
    
    private WifiPayload createEnterpriseEncryption(final WifiPayload wifiPayload, final DataObject dataObject, final Long configDataItemID) {
        try {
            if (dataObject != null) {
                Integer[] acceptEAPTypes = null;
                final ArrayList<Integer> arrayList = new ArrayList<Integer>();
                final Criteria criteria = new Criteria(Column.getColumn("WifiEnterprise", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
                final Iterator iterator = dataObject.getRows("WifiEnterprise", criteria);
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final Boolean tls = (Boolean)payloadRow.get("TLS");
                    final Boolean leap = (Boolean)payloadRow.get("LEAP");
                    final Boolean eap_fast = (Boolean)payloadRow.get("EAP_FAST");
                    final Boolean ttls = (Boolean)payloadRow.get("TTLS");
                    final Boolean peap = (Boolean)payloadRow.get("PEAP");
                    final Boolean eap_sim = (Boolean)payloadRow.get("EAP_SIM");
                    final Boolean use_pac = (Boolean)payloadRow.get("USE_PAC");
                    final Boolean provisional_pac = (Boolean)payloadRow.get("PROVISION_PAC");
                    final Boolean provisional_pac_anon = (Boolean)payloadRow.get("PROVISION_PAC_ANONYMOUS");
                    final Integer inner_identity = (Integer)payloadRow.get("INNER_IDENTITY");
                    final String userName = (String)payloadRow.get("USER_NAME");
                    String password = "";
                    if (payloadRow.get("PASSWORD_ID") != null) {
                        final Long passwordId = (Long)payloadRow.get("PASSWORD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordId.toString());
                    }
                    final Boolean per_connection_pwd = (Boolean)payloadRow.get("USE_PER_CONNECTION_PWD");
                    final String outerIdentity = (String)payloadRow.get("OUTER_IDENTITY");
                    if (tls) {
                        arrayList.add(new Integer(13));
                    }
                    if (leap) {
                        arrayList.add(new Integer(17));
                    }
                    if (eap_sim) {
                        arrayList.add(new Integer(18));
                    }
                    if (ttls) {
                        arrayList.add(new Integer(21));
                    }
                    if (peap) {
                        arrayList.add(new Integer(25));
                    }
                    if (eap_fast) {
                        arrayList.add(new Integer(43));
                    }
                    if (arrayList.size() > 0) {
                        acceptEAPTypes = new Integer[arrayList.size()];
                        for (int i = 0; i < arrayList.size(); ++i) {
                            acceptEAPTypes[i] = arrayList.get(i);
                        }
                        wifiPayload.setAcceptEAPTypes(acceptEAPTypes);
                    }
                    wifiPayload.setEAPFASTUsePAC(use_pac);
                    wifiPayload.setEAPFASTProvisionPAC(provisional_pac);
                    wifiPayload.setEAPFASTProvisionPACAnonymously(provisional_pac_anon);
                    wifiPayload.setOuterIdentity(outerIdentity);
                    wifiPayload.setUserName(userName);
                    if (per_connection_pwd) {
                        wifiPayload.setOneTimeUserPassword(per_connection_pwd);
                    }
                    else if (password != null) {
                        wifiPayload.setUserPassword(password);
                    }
                    if (ttls) {
                        if (inner_identity == 0) {
                            wifiPayload.setTTLSInnerAuthentication("PAP");
                        }
                        else if (inner_identity == 1) {
                            wifiPayload.setTTLSInnerAuthentication("CHAP");
                        }
                        else if (inner_identity == 2) {
                            wifiPayload.setTTLSInnerAuthentication("MSCHAP");
                        }
                        else {
                            if (inner_identity != 3) {
                                continue;
                            }
                            wifiPayload.setTTLSInnerAuthentication("MSCHAPv2");
                        }
                    }
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return wifiPayload;
    }
    
    private WifiPayload createProxyPayload(final WifiPayload wifiPayload, final Row payloadRow) {
        try {
            final Integer proxyType = (Integer)payloadRow.get("PROXY_TYPE");
            if (proxyType != null) {
                if (proxyType == 0) {
                    wifiPayload.setWiFiProxy("None");
                }
                else if (proxyType == 1) {
                    wifiPayload.setWiFiProxy("Manual");
                    final String proxyServer = (String)payloadRow.get("PROXY_SERVER");
                    final Integer proxyServerPort = (Integer)payloadRow.get("PROXY_SERVER_PORT");
                    final String userName = (String)payloadRow.get("PROXY_USER_NAME");
                    String password = "";
                    if (payloadRow.get("PROXY_PASSWORD_ID") != null) {
                        final Long passwordId = (Long)payloadRow.get("PROXY_PASSWORD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordId.toString());
                    }
                    wifiPayload.setProxyServer(proxyServer);
                    wifiPayload.setProxyServerPort(proxyServerPort);
                    wifiPayload.setProxyUsername(userName);
                    wifiPayload.setProxyPassword(password);
                }
                else if (proxyType == 2) {
                    wifiPayload.setWiFiProxy("Auto");
                    final String pacURL = (String)payloadRow.get("PROXY_PAC_URL");
                    wifiPayload.setProxyAutoConfigURLString(pacURL);
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return wifiPayload;
    }
    
    private WifiPayload createPersonalEncryptions(final WifiPayload wifiPayload, final DataObject dataObject, final Long configDataItemID) {
        try {
            if (dataObject != null) {
                final Criteria criteria = new Criteria(Column.getColumn("WifiNonEnterprise", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
                final Iterator iterator = dataObject.getRows("WifiNonEnterprise", criteria);
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    String password = "";
                    if (payloadRow.get("PASSWORD_ID") != null) {
                        final Long passwordId = (Long)payloadRow.get("PASSWORD_ID");
                        password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordId.toString());
                    }
                    wifiPayload.setPassword(password);
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return wifiPayload;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
