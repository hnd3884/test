package com.me.mdm.server.windows.profile.payload.transform;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.me.mdm.server.config.MDMConfigUtil;
import java.security.cert.X509Certificate;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.io.File;
import com.adventnet.sym.webclient.mdm.config.CredentialsMgmtAction;
import com.me.devicemanagement.framework.webclient.common.FileUploadUtil;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanEapTypes;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.windows.profile.payload.content.wifi.WlanProfile;
import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanAuthentication;
import com.me.mdm.server.windows.profile.payload.content.wifi.enums.WlanConnectionMode;
import java.net.URLEncoder;
import com.me.mdm.server.windows.profile.payload.content.wifi.WiFiProperties;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.me.mdm.server.windows.profile.payload.WindowsWiFiPayload;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;

public class DO2WindowsWiFiPayload extends DO2WindowsPayload
{
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        final WindowsWiFiPayload wifiPayload = new WindowsWiFiPayload();
        Iterator iterator = null;
        try {
            iterator = dataObject.getRows("WifiPolicy");
            final List alreadyAddedCACertList = new ArrayList();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                String ssidName = row.get("SERVICE_SET_IDENTIFIER").toString();
                WiFiProperties props = new WiFiProperties();
                props.setSsidName(ssidName);
                ssidName = URLEncoder.encode(ssidName, "UTF-8").replace("+", "%20");
                WlanConnectionMode connectionMode = WlanConnectionMode.MANUAL;
                if (row.get("AUTO_JOIN")) {
                    connectionMode = WlanConnectionMode.AUTO;
                }
                props.setConnectionMode(connectionMode);
                final int securityType = (int)row.get("SECURITY_TYPE");
                WlanAuthentication authentication = WlanAuthentication.OPEN;
                switch (securityType) {
                    case 0: {
                        authentication = WlanAuthentication.OPEN;
                        break;
                    }
                    case 1: {
                        authentication = WlanAuthentication.WPA2_PERSONAL;
                        props = this.updateWPA2PSKProps(props, dataObject);
                        break;
                    }
                    case 2: {
                        authentication = WlanAuthentication.WPA2_ENTERPRISE;
                        props = this.updateEAPProps(props, dataObject);
                        break;
                    }
                }
                props.setAuthenticationType(authentication);
                String proxyUrl = "";
                String proxyPort = "";
                if ((int)row.get("PROXY_TYPE") == 2) {
                    proxyUrl = (String)row.get("PROXY_SERVER");
                    proxyPort = ((Integer)row.get("PROXY_SERVER_PORT")).toString();
                }
                final WlanProfile wlan = new WlanProfile(props);
                final String wlanData = wlan.toString();
                wifiPayload.setWlanXml(ssidName, wlanData);
                wifiPayload.setDeleteOnInstallItem(ssidName);
                if (proxyUrl != null && !proxyUrl.trim().isEmpty()) {
                    wifiPayload.setProxy(ssidName, proxyUrl, proxyPort);
                }
                if (props.getAuthenticationType() == WlanAuthentication.WPA2_ENTERPRISE && props.getTrusterRootCACertificate() != null && !this.isDuplicateCertificate(props.getTrustedRootCAThumbrpint(), dataObject) && !alreadyAddedCACertList.contains(props.getTrustedRootCAThumbrpint().trim())) {
                    wifiPayload.setCertificateDeletePayload(props.getTrustedRootCAThumbrpint());
                    wifiPayload.setCertificatePayload(props.getTrusterRootCACertificate(), props.getTrustedRootCAThumbrpint());
                    alreadyAddedCACertList.add(props.getTrustedRootCAThumbrpint().trim());
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while creating windows wifi payload : ", ex);
        }
        return wifiPayload;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        WindowsWiFiPayload wifiPayload = null;
        try {
            final Iterator iterator = dataObject.getRows("WifiPolicy");
            while (iterator.hasNext()) {
                wifiPayload = new WindowsWiFiPayload();
                final Row row = iterator.next();
                final String ssidName = (String)row.get("SERVICE_SET_IDENTIFIER");
                if (ssidName != null) {
                    wifiPayload.setRemoveProfile(ssidName);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while creating windows wifi remove payload : ", ex);
        }
        return wifiPayload;
    }
    
    private WiFiProperties updateWPA2PSKProps(final WiFiProperties props, final DataObject dataObject) throws DataAccessException {
        final Iterator iterator = dataObject.getRows("WifiNonEnterprise");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            String preSharedKey = "";
            if (row.get("PASSWORD_ID") != null) {
                final Long incomingServerPasswordId = (Long)row.get("PASSWORD_ID");
                preSharedKey = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(incomingServerPasswordId.toString());
            }
            props.setPassPhrase(preSharedKey);
        }
        return props;
    }
    
    private WiFiProperties updateEAPProps(final WiFiProperties props, final DataObject dataObject) throws DataAccessException, Exception {
        final Iterator iterator = dataObject.getRows("WifiEnterprise");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            props.setEapType(this.getEapType(row));
            props.setInnerEapType(this.getInnerEapType(row));
            final Long caCertID = (Long)row.get("CERTIFICATE_ID");
            if (caCertID != null && caCertID != -1L) {
                final Long customerID = (Long)dataObject.getRow("CollnToCustomerRel").get("CUSTOMER_ID");
                this.setCertificateDetails(props, customerID, caCertID);
            }
        }
        return props;
    }
    
    private WlanEapTypes getEapType(final Row row) {
        WlanEapTypes wlanEapType = WlanEapTypes.EAP_PEAP;
        if (row.get("PEAP")) {
            wlanEapType = WlanEapTypes.EAP_PEAP;
        }
        if (row.get("TLS")) {
            wlanEapType = WlanEapTypes.EAP_TLS;
        }
        return wlanEapType;
    }
    
    private WlanEapTypes getInnerEapType(final Row row) {
        final int innerEapType = (int)row.get("INNER_IDENTITY");
        WlanEapTypes innerEapWlanType = WlanEapTypes.EAP_MSCHAPv2;
        switch (innerEapType) {
            case 3: {
                innerEapWlanType = WlanEapTypes.EAP_MSCHAPv2;
                break;
            }
        }
        return innerEapWlanType;
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
    
    private String getCACertFilePath(final Long caCertID, final DataObject dataObject) throws DataAccessException, Exception {
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
}
