package com.me.mdm.server.windows.profile.payload.transform;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.me.mdm.server.config.MDMConfigUtil;
import java.security.cert.X509Certificate;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import com.adventnet.sym.webclient.mdm.config.CredentialsMgmtAction;
import com.me.devicemanagement.framework.webclient.common.FileUploadUtil;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.me.mdm.server.windows.profile.payload.content.wifi.msm.eap.WlanEapConfigElement;
import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanEapTypes;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.me.mdm.server.windows.profile.payload.WindowsNativeVPNPayload;
import com.me.mdm.server.windows.profile.payload.content.vpn.CheckPointPluginProfileGenerator;
import com.me.mdm.server.windows.profile.payload.content.vpn.SoniceWallPluginProfileGenerator;
import com.me.mdm.server.windows.profile.payload.content.vpn.PulseSecureProfileGenerator;
import com.me.mdm.server.windows.profile.payload.content.vpn.F5PluginProfileGenerator;
import com.me.mdm.server.windows.profile.payload.content.vpn.BasePluginProfileGenerator;
import com.me.mdm.server.windows.profile.payload.content.vpn.PluginProfileGenerator;
import com.me.mdm.server.windows.profile.payload.WindowsPluginVPNPayload;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.windows.profile.payload.WindowsVPNpayload;
import java.net.URLEncoder;
import com.adventnet.persistence.Row;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;

public class DO2WindowsVPNPayload extends DO2WindowsPayload
{
    public static final int L2TP = 1;
    public static final int PPTP = 2;
    public static final int CUSTOM_SSL = 7;
    public static final int IKEV2 = 9;
    public static final String L2TP_STR = "L2TP";
    public static final String PPTP_STR = "PPTP";
    public static final String IKEV2_STR = "IKEV2";
    public static final String EAP_STR = "EAP";
    public static final String MSCHAPV2_STR = "MSCHAPV2";
    public static final String F5_IDENTIFIER = "F5Networks.vpn.client_btcnfmkykcjs2";
    public static final String CHECK_POINT_IDENTIFIER = "B4D42709.CheckPointVPN_wz4qkf3wxpc74";
    public static final String PULSE_IDENTIFIER = "951D7986.PulseSecureVPN_qzpvqh70t9a4p";
    public static final String SONIC_IDENTIFER = "SonicWALL.MobileConnect_e5kpm93dbe93j";
    public static final int VPN_PAYLOAD = 1;
    public static final int PER_APP_VPN_PAYLOAD = 2;
    public static final int USER_AUTH = 0;
    public static final int SHARED_SECRET = 1;
    public static final int EAP = 2;
    public static final int PROXY_USER_AUTH = 1;
    public static final int PROXY_PAC_URL = 2;
    
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        WindowsPayload windowsPayload = null;
        if (dataObject != null) {
            Iterator iterator = null;
            try {
                iterator = dataObject.getRows("VpnPolicy");
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final Long configDataItemID = (Long)payloadRow.get("CONFIG_DATA_ITEM_ID");
                    String connectionName = (String)payloadRow.get("CONNECTION_NAME");
                    connectionName = URLEncoder.encode(connectionName, "UTF-8").replace("+", "%20");
                    Integer connectionType = (Integer)payloadRow.get("CONNECTION_TYPE");
                    Integer vpnType = (Integer)payloadRow.get("VPN_TYPE");
                    ++connectionType;
                    vpnType = ((dataObject.containsTable("WindowsKioskPolicyApps") || dataObject.containsTable("WindowsKioskPolicySystemApps")) ? 2 : 1);
                    windowsPayload = WindowsVPNpayload.getVPNClassFromType(connectionType, connectionName, vpnType);
                    ((WindowsVPNpayload)windowsPayload).setVPNDeletePayload(Boolean.FALSE);
                    this.createVPNPayload(dataObject, windowsPayload, connectionType, configDataItemID);
                    this.createPerAppVPNPayload(dataObject, windowsPayload, configDataItemID);
                    this.createProxyPayload(windowsPayload, payloadRow);
                    ((WindowsVPNpayload)windowsPayload).setRememberCredentials(Boolean.TRUE);
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Failed to create windows VPN payload", e);
            }
        }
        return windowsPayload;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        WindowsPayload windowsPayload = null;
        if (dataObject != null) {
            Iterator iterator = null;
            try {
                iterator = dataObject.getRows("VpnPolicy");
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    String connectionName = (String)payloadRow.get("CONNECTION_NAME");
                    connectionName = URLEncoder.encode(connectionName, "UTF-8").replace("+", "%20");
                    windowsPayload = new WindowsVPNpayload(connectionName, -1);
                    ((WindowsVPNpayload)windowsPayload).setVPNDeletePayload(Boolean.TRUE);
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Failed to create windows VPN payload", e);
            }
        }
        return windowsPayload;
    }
    
    private void createPerAppVPNPayload(final DataObject dataObject, final WindowsPayload windowsPayload, final Long configDataItemID) throws Exception {
        final WindowsVPNpayload windowsPerAppVPNPayload = (WindowsVPNpayload)windowsPayload;
        Iterator iterator = dataObject.getRows("WindowsKioskPolicyApps", new Criteria(Column.getColumn("WindowsKioskPolicyApps", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0));
        final List appList = new ArrayList();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long appgroupID = (Long)row.get("APP_GROUP_ID");
            final Row appRow = dataObject.getRow("MdAppGroupDetails", new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appgroupID, 0));
            final String pfn = (String)appRow.get("IDENTIFIER");
            appList.add(pfn);
        }
        iterator = dataObject.getRows("WindowsKioskPolicySystemApps", new Criteria(Column.getColumn("WindowsKioskPolicySystemApps", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0));
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long appgroupID = (Long)row.get("APP_GROUP_ID");
            final Row appRow = dataObject.getRow("WindowsSystemApps", new Criteria(Column.getColumn("WindowsSystemApps", "APP_ID"), (Object)appgroupID, 0));
            final String pfn = (String)appRow.get("PACKAGE_FAMILY_NAME");
            appList.add(pfn);
        }
        if (!appList.isEmpty()) {
            final HashMap hashMap = new HashMap();
            hashMap.put("perAppTriggerList", appList);
            windowsPerAppVPNPayload.setTriggers(hashMap);
        }
    }
    
    private WindowsPayload createVPNPayload(final DataObject dataObject, final WindowsPayload windowsPayload, final int connectionType, final Long configDataitemID) throws Exception {
        switch (connectionType) {
            case 1: {
                return this.createL2TPPayload(windowsPayload, dataObject, configDataitemID);
            }
            case 2: {
                return this.createPPTPPayload(windowsPayload, dataObject, configDataitemID);
            }
            case 9: {
                return this.createIKEV2Payload(windowsPayload, dataObject, configDataitemID);
            }
            case 7: {
                return this.createPluginSSLPayload(windowsPayload, dataObject, configDataitemID);
            }
            default: {
                return windowsPayload;
            }
        }
    }
    
    private WindowsPayload createPluginSSLPayload(final WindowsPayload windowsPayload, final DataObject dataObject, final Long configDataitemID) throws Exception {
        final WindowsPluginVPNPayload windowsPluginVPNPayload = (WindowsPluginVPNPayload)windowsPayload;
        if (dataObject != null) {
            final Criteria criteria = new Criteria(Column.getColumn("VpnCustomSSL", "CONFIG_DATA_ITEM_ID"), (Object)configDataitemID, 0);
            final Iterator iterator = dataObject.getRows("VpnCustomSSL", criteria);
            while (iterator.hasNext()) {
                final Row payloadRow = iterator.next();
                final String serverName = (String)payloadRow.get("SERVER_NAME");
                final Integer userAuthenticationType = (Integer)payloadRow.get("USER_AUTHENTICATION");
                final Long caCertID = (Long)payloadRow.get("CERTIFICATE_ID");
                final String identifier = (String)payloadRow.get("IDENTIFIER");
                windowsPluginVPNPayload.addVPNServers(serverName);
                windowsPluginVPNPayload.setPFN(identifier);
                final PluginProfileGenerator pluginProfileGenerator = this.getPluginProfileGenerator(identifier);
                pluginProfileGenerator.createRootElement();
                pluginProfileGenerator.generatePluginXML(pluginProfileGenerator.createPluginProfileData(dataObject, configDataitemID));
                final String xml = pluginProfileGenerator.toString();
                if (xml != null) {
                    windowsPluginVPNPayload.addCustomXML(xml);
                }
            }
        }
        return windowsPluginVPNPayload;
    }
    
    private PluginProfileGenerator getPluginProfileGenerator(String identifier) {
        identifier = identifier.toLowerCase();
        PluginProfileGenerator pluginProfileGenerator = new BasePluginProfileGenerator(identifier);
        if (identifier.toLowerCase().equals("F5Networks.vpn.client_btcnfmkykcjs2".toLowerCase())) {
            pluginProfileGenerator = new F5PluginProfileGenerator(identifier);
        }
        else if (identifier.toLowerCase().equals("951D7986.PulseSecureVPN_qzpvqh70t9a4p".toLowerCase())) {
            pluginProfileGenerator = new PulseSecureProfileGenerator(identifier);
        }
        else if (identifier.toLowerCase().equals("SonicWALL.MobileConnect_e5kpm93dbe93j".toLowerCase())) {
            pluginProfileGenerator = new SoniceWallPluginProfileGenerator(identifier);
        }
        else if (identifier.toLowerCase().equals("B4D42709.CheckPointVPN_wz4qkf3wxpc74".toLowerCase())) {
            pluginProfileGenerator = new CheckPointPluginProfileGenerator(identifier);
        }
        return pluginProfileGenerator;
    }
    
    private WindowsPayload createL2TPPayload(final WindowsPayload windowsPayload, final DataObject dataObject, final Long configDataItemID) throws Exception {
        final WindowsNativeVPNPayload windowsNativeVPNPayload = (WindowsNativeVPNPayload)windowsPayload;
        if (dataObject != null) {
            final Criteria criteria = new Criteria(Column.getColumn("VpnL2TP", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
            final Iterator iterator = dataObject.getRows("VpnL2TP", criteria);
            while (iterator.hasNext()) {
                final Row payloadRow = iterator.next();
                final String serverName = (String)payloadRow.get("SERVER_NAME");
                final Integer userAuthenticationType = (Integer)payloadRow.get("USER_AUTHENTICATION");
                final Long caCertID = (Long)payloadRow.get("CA_CERTIFICATE_ID");
                windowsNativeVPNPayload.addProtocolType("L2TP");
                windowsNativeVPNPayload.addVPNServers(serverName);
                if (userAuthenticationType == 1) {
                    windowsNativeVPNPayload.setAuthType("EAP");
                    String sharedSecret = "";
                    if (payloadRow.get("SHARED_SECRET_ID") != null) {
                        final Long sharedSecretID = (Long)payloadRow.get("SHARED_SECRET_ID");
                        sharedSecret = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(sharedSecretID.toString());
                    }
                    windowsNativeVPNPayload.setL2TPSharedSecret(sharedSecret);
                }
                else if (userAuthenticationType == 0) {
                    windowsNativeVPNPayload.setAuthType("MSCHAPV2");
                }
                else {
                    if (userAuthenticationType != 2) {
                        continue;
                    }
                    final WiFiProperties props = new WiFiProperties();
                    props.setEapType(WlanEapTypes.EAP_TLS);
                    if (caCertID != null && caCertID != -1L) {
                        final Long customerID = (Long)dataObject.getRow("CollnToCustomerRel").get("CUSTOMER_ID");
                        this.setCertificateDetails(props, customerID, caCertID);
                        if (!this.isDuplicateCertificate(props.getTrustedRootCAThumbrpint(), dataObject)) {
                            windowsNativeVPNPayload.setCertificateDeletePayload(props.getTrustedRootCAThumbrpint());
                            windowsNativeVPNPayload.setCertificatePayload(props.getTrusterRootCACertificate(), props.getTrustedRootCAThumbrpint());
                        }
                    }
                    windowsNativeVPNPayload.setAuthType("EAP");
                    windowsNativeVPNPayload.setEAPConfiguration(new WlanEapConfigElement(props).toString());
                }
            }
        }
        return windowsNativeVPNPayload;
    }
    
    private WindowsPayload createPPTPPayload(final WindowsPayload windowsPayload, final DataObject dataObject, final Long configDataItemID) throws Exception {
        final WindowsNativeVPNPayload windowsNativeVPNPayload = (WindowsNativeVPNPayload)windowsPayload;
        if (dataObject != null) {
            final Criteria criteria = new Criteria(Column.getColumn("VpnPPTP", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
            final Iterator iterator = dataObject.getRows("VpnPPTP", criteria);
            while (iterator.hasNext()) {
                final Row payloadRow = iterator.next();
                final String serverName = (String)payloadRow.get("SERVER_NAME");
                final Integer userAuthenticationType = (Integer)payloadRow.get("USER_AUTHENTICATION");
                final Long caCertID = (Long)payloadRow.get("CA_CERTIFICATE_ID");
                windowsNativeVPNPayload.addProtocolType("PPTP");
                windowsNativeVPNPayload.addVPNServers(serverName);
                if (userAuthenticationType == 0) {
                    windowsNativeVPNPayload.setAuthType("MSCHAPV2");
                }
                else {
                    if (userAuthenticationType != 2) {
                        continue;
                    }
                    final WiFiProperties props = new WiFiProperties();
                    props.setEapType(WlanEapTypes.EAP_TLS);
                    if (caCertID != null && caCertID != -1L) {
                        final Long customerID = (Long)dataObject.getRow("CollnToCustomerRel").get("CUSTOMER_ID");
                        this.setCertificateDetails(props, customerID, caCertID);
                        if (!this.isDuplicateCertificate(props.getTrustedRootCAThumbrpint(), dataObject)) {
                            windowsNativeVPNPayload.setCertificateDeletePayload(props.getTrustedRootCAThumbrpint());
                            windowsNativeVPNPayload.setCertificatePayload(props.getTrusterRootCACertificate(), props.getTrustedRootCAThumbrpint());
                        }
                    }
                    windowsNativeVPNPayload.setAuthType("EAP");
                    windowsNativeVPNPayload.setEAPConfiguration(new WlanEapConfigElement(props).toString());
                }
            }
        }
        return windowsNativeVPNPayload;
    }
    
    private WindowsPayload createIKEV2Payload(final WindowsPayload windowsPayload, final DataObject dataObject, final Long configDataItemID) throws Exception {
        final WindowsNativeVPNPayload windowsNativeVPNPayload = (WindowsNativeVPNPayload)windowsPayload;
        if (dataObject != null) {
            final Criteria criteria = new Criteria(Column.getColumn("VpnToPolicyRel", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
            final Iterator iterator = dataObject.getRows("VpnToPolicyRel", criteria);
            while (iterator.hasNext()) {
                final Row policyRow = iterator.next();
                final Long policyID = (Long)policyRow.get("VPN_POLICY_ID");
                final Row payloadRow = dataObject.getRow("VpnIKEv2", new Criteria(Column.getColumn("VpnToPolicyRel", "VPN_POLICY_ID"), (Object)policyID, 0));
                final Long caCertID = (Long)payloadRow.get("CA_CERTIFICATE_ID");
                final String serverName = (String)payloadRow.get("SERVER_NAME");
                final Integer userAuthenticationType = (Integer)payloadRow.get("AUTHENTICATION_METHOD");
                windowsNativeVPNPayload.addProtocolType("IKEV2");
                windowsNativeVPNPayload.addVPNServers(serverName);
                if (userAuthenticationType == 2) {
                    final WiFiProperties props = new WiFiProperties();
                    props.setEapType(WlanEapTypes.EAP_TLS);
                    if (caCertID != null && caCertID != -1L) {
                        final Long customerID = (Long)dataObject.getRow("CollnToCustomerRel").get("CUSTOMER_ID");
                        this.setCertificateDetails(props, customerID, caCertID);
                        if (!this.isDuplicateCertificate(props.getTrustedRootCAThumbrpint(), dataObject)) {
                            windowsNativeVPNPayload.setCertificateDeletePayload(props.getTrustedRootCAThumbrpint());
                            windowsNativeVPNPayload.setCertificatePayload(props.getTrusterRootCACertificate(), props.getTrustedRootCAThumbrpint());
                        }
                    }
                    windowsNativeVPNPayload.setAuthType("EAP");
                    windowsNativeVPNPayload.setEAPConfiguration(new WlanEapConfigElement(props).toString());
                }
            }
        }
        return windowsNativeVPNPayload;
    }
    
    private String getCACertFilePath(final Long caCertID, final DataObject dataObject) throws Exception {
        final Long customerID = (Long)dataObject.getRow("CollnToCustomerRel").get("CUSTOMER_ID");
        String filePath = null;
        final DataObject certDO = ProfileCertificateUtil.getInstance().getCertificateInfo(customerID, caCertID);
        if (certDO != null) {
            final String destFolder = MDMUtil.getCredentialCertificateFolder(customerID);
            final Row certRow = certDO.getFirstRow("CredentialCertificateInfo");
            final String certFileName = (String)certRow.get("CERTIFICATE_FILE_NAME");
            filePath = destFolder + File.separator + certFileName;
        }
        return filePath;
    }
    
    private void setCertificateDetails(final WiFiProperties props, final Long customerID, final Long certID) throws Exception {
        final String cerFolder = MDMUtil.getCredentialCertificateFolder(customerID);
        ProfileCertificateUtil.getInstance();
        final JSONObject certificateJSON = ProfileCertificateUtil.getCertificateDetail(customerID, certID);
        final String certFileName = certificateJSON.getString("CERTIFICATE_FILE_NAME");
        if (!FileUploadUtil.hasVulnerabilityInFileName(certFileName)) {
            new CredentialsMgmtAction();
            final X509Certificate certificate = CredentialsMgmtAction.readX509Certificate(cerFolder + File.separator + certFileName);
            if (certificate != null) {
                final String certificateContent = PayloadSecretFieldsHandler.getInstance().constructSSLCertificate(certID.toString());
                props.setTrustedRootCACertificate(certificateContent);
                final String thumbprint = CertificateUtils.getCertificateFingerPrint(certificate);
                props.setTrustedRootCAThumbrpint(thumbprint);
            }
        }
    }
    
    private boolean isDuplicateCertificate(final String thumbprint, final DataObject dataObject) throws Exception {
        boolean isDuplicateCertificate = false;
        final Row row = dataObject.getFirstRow("CfgDataToCollection");
        final Long collectionID = (Long)row.get("COLLECTION_ID");
        final List configIDList = MDMConfigUtil.getConfigIds(collectionID);
        if (configIDList.contains(607)) {
            final List configDataIDs = ProfileConfigHandler.getConfigDataIds(collectionID, 607);
            if (configDataIDs.size() > 0) {
                final List configDataItemList = ProfileConfigHandler.getConfigDataItemIds(configDataIDs.get(0));
                final Long certID = (Long)DBUtil.getValueFromDB("CertificatePolicy", "CONFIG_DATA_ITEM_ID", configDataItemList.get(0), "CERTIFICATE_ID");
                final String certthumbprint = (String)DBUtil.getValueFromDB("CredentialCertificateInfo", "CERTIFICATE_ID", (Object)certID, "CERTIFICATE_THUMBPRINT");
                if (certthumbprint.equals(thumbprint)) {
                    isDuplicateCertificate = true;
                }
            }
        }
        return isDuplicateCertificate;
    }
    
    private void createProxyPayload(final WindowsPayload vpnPayload, final Row payloadRow) {
        final WindowsVPNpayload windowsVPNpayload = (WindowsVPNpayload)vpnPayload;
        try {
            final Integer proxyType = (Integer)payloadRow.get("PROXY_TYPE");
            if (proxyType != null && proxyType == 1) {
                final String proxyServer = (String)payloadRow.get("PROXY_SERVER");
                final Integer proxyServerPort = (Integer)payloadRow.get("PROXY_SERVER_PORT");
                ((WindowsVPNpayload)vpnPayload).setManualProxy(proxyServer + ":" + proxyServerPort);
            }
            else if (proxyType != null && proxyType == 2) {
                final String pacURL = (String)payloadRow.get("PROXY_PAC_URL");
                ((WindowsVPNpayload)vpnPayload).setAutoConfigProxy(pacURL);
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
}
