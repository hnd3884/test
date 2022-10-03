package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.android.payload.transform.DO2AndroidPayloadHandler;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import java.util.logging.Level;
import java.util.Collection;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.android.payload.AndroidConfigurationPayload;
import java.util.List;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeCertificatePayload;
import java.util.HashMap;

public class DO2ChromePayloadHandler
{
    HashMap<Long, ChromeCertificatePayload> certificateMapper;
    private static final Logger LOGGER;
    
    public DO2ChromePayloadHandler() {
        this.certificateMapper = new HashMap<Long, ChromeCertificatePayload>();
    }
    
    public AndroidConfigurationPayload createPayload(final DataObject dataObject, final List configDOList) {
        AndroidConfigurationPayload configurationPayload = null;
        final List payloadList = new ArrayList();
        try {
            final int configSize = configDOList.size();
            final Long collectionID = (Long)dataObject.getFirstValue("Collection", "COLLECTION_ID");
            final String collectionName = ProfileHandler.getProfileIdentifierFromCollectionID(collectionID);
            final int scope = DeviceCommandRepository.getInstance().getProfileScopeForCollection(collectionID);
            for (int k = 0; k < configSize; ++k) {
                final DataObject configDO = configDOList.get(k);
                final Integer configId = (Integer)configDO.getFirstValue("ConfigData", "CONFIG_ID");
                final JSONObject identifierJson = new JSONObject();
                identifierJson.put("collection_name", (Object)collectionName);
                if (configDO.getRows("MdConfigDataItemExtn").hasNext()) {
                    final String payloadIdentifier = (String)configDO.getFirstValue("MdConfigDataItemExtn", "CONFIG_PAYLOAD_IDENTIFIER");
                    identifierJson.put("payload_identifier", (Object)payloadIdentifier);
                }
                final AndroidPayload settingsPayload = this.createPayload(configId, configDO, identifierJson, scope);
                if (settingsPayload != null) {
                    payloadList.add(settingsPayload);
                }
            }
            if (!this.certificateMapper.isEmpty()) {
                payloadList.addAll(this.certificateMapper.values());
            }
            configurationPayload = this.createChromeConfigurationPayload(dataObject, payloadList);
        }
        catch (final Exception ex) {
            DO2ChromePayloadHandler.LOGGER.log(Level.SEVERE, "Exception in createPayload :: ", ex);
        }
        return configurationPayload;
    }
    
    private AndroidConfigurationPayload createChromeConfigurationPayload(final DataObject dataObject, final List payloadJSONList) {
        AndroidConfigurationPayload cfgPayload = null;
        try {
            final String collectionName = (String)dataObject.getFirstValue("Collection", "COLLECTION_NAME");
            final Long collectionId = (Long)dataObject.getFirstValue("Collection", "COLLECTION_ID");
            final String payloadIdentifier = ProfileHandler.getProfileIdentifierFromCollectionID(collectionId);
            cfgPayload = new AndroidConfigurationPayload(1, payloadIdentifier, collectionName);
            if (payloadJSONList.size() > 0) {
                final JSONArray array = new JSONArray();
                for (int i = 0; i < payloadJSONList.size(); ++i) {
                    array.put(i, (Object)payloadJSONList.get(i).getPayloadJSON());
                }
                cfgPayload.setPayloadContent(array);
                cfgPayload.setPayloadUUID(collectionId);
            }
        }
        catch (final Exception exp) {
            DO2ChromePayloadHandler.LOGGER.log(Level.SEVERE, "Exception in createAndroidConfigurationPayload", exp);
        }
        return cfgPayload;
    }
    
    public AndroidPayload createPayload(final Integer configID, final DataObject dataObject, final JSONObject identifierJson, final int scope) {
        ChromePayload settingsPayload = null;
        try {
            final String collectionName = String.valueOf(identifierJson.get("collection_name"));
            switch (configID) {
                case 701: {
                    final DO2ChromeWifPolicyPayload chromeWifiPayload = new DO2ChromeWifPolicyPayload();
                    settingsPayload = chromeWifiPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".WIFI";
                    settingsPayload.setPayloadIdentifier(identifier);
                    final String certId = settingsPayload.getCertificateID();
                    if (!"-1".equals(certId)) {
                        final String signingCertUUID = this.getCertificatePayloadUUID(Long.parseLong(certId));
                        if (signingCertUUID != null) {
                            settingsPayload.setCertificate(signingCertUUID, true);
                        }
                        break;
                    }
                    break;
                }
                case 702: {
                    final DO2ChromeEthernetPolicyPayload chromeEthernetPayload = new DO2ChromeEthernetPolicyPayload();
                    settingsPayload = chromeEthernetPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".ETHERNET";
                    settingsPayload.setPayloadIdentifier(identifier);
                    final String certificateID = settingsPayload.getCertificateID();
                    if (!"-1".equals(certificateID)) {
                        final String signingCertUUID2 = this.getCertificatePayloadUUID(Long.parseLong(certificateID));
                        if (signingCertUUID2 != null) {
                            settingsPayload.setCertificate(signingCertUUID2, true);
                        }
                        break;
                    }
                    break;
                }
                case 703: {
                    final DO2ChromeCertificatePolicy chromecertificatePayload = new DO2ChromeCertificatePolicy();
                    settingsPayload = chromecertificatePayload.createPayload(dataObject);
                    final String identifier = collectionName + ".CERTIFICATE";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 704: {
                    final DO2ChromeVPNPolicyPayload chromeVPNPayload = new DO2ChromeVPNPolicyPayload();
                    settingsPayload = chromeVPNPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".VPN";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 705: {
                    final Do2ChromeKioskPolicyPayload chromeKioskPayload = new Do2ChromeKioskPolicyPayload();
                    settingsPayload = chromeKioskPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".KIOSK";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 706: {
                    final DO2ChromeRestrictionsPolicyPayload chromeRestrictionsPolicyPayload = new DO2ChromeRestrictionsPolicyPayload();
                    settingsPayload = chromeRestrictionsPolicyPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".restrictions";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 707: {
                    final DO2ChromeWebContentFilterPayload chromeWebContent = new DO2ChromeWebContentFilterPayload();
                    settingsPayload = chromeWebContent.createPayload(dataObject);
                    final String identifier = collectionName + ".URLWhitelistBlacklist";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 708: {
                    final DO2ChromePowerMgmtPayload powerPayload = new DO2ChromePowerMgmtPayload();
                    settingsPayload = powerPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".PowerIdleManagement";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 709: {
                    final DO2ChromeBookmarksPayload bookmarksPayload = new DO2ChromeBookmarksPayload();
                    settingsPayload = bookmarksPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".ManagedBookMarks";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 710: {
                    final DO2ChromeUserRestrictionPayload userRestrictionPayload = new DO2ChromeUserRestrictionPayload();
                    settingsPayload = userRestrictionPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".userRestriction";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 711: {
                    final DO2ChromeVerifyAccessAPIPayload verifyAccessAPIPayload = new DO2ChromeVerifyAccessAPIPayload();
                    settingsPayload = verifyAccessAPIPayload.createPayload(dataObject, scope);
                    final String identifier = collectionName + ".VerifyAccessAPI";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 712: {
                    final DO2ChromeBrowserSettingsPayload browserPayload = new DO2ChromeBrowserSettingsPayload();
                    settingsPayload = browserPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".VerifiedAccess";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 713: {
                    final DO2ChromeApplicationPolicyPayload appPayload = new DO2ChromeApplicationPolicyPayload();
                    settingsPayload = appPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".ExtensionInstallSources";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 714: {
                    final DO2ChromeManagedGuestSessionPayload managedGuestSessionPayload = new DO2ChromeManagedGuestSessionPayload();
                    settingsPayload = managedGuestSessionPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".ManagedGuestSession";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
            }
            if (identifierJson.has("payload_identifier")) {
                settingsPayload.setPayloadIdentifier(String.valueOf(identifierJson.get("payload_identifier")));
            }
        }
        catch (final Exception exp) {
            DO2ChromePayloadHandler.LOGGER.log(Level.SEVERE, "Exception in createPayload ", exp);
        }
        return settingsPayload;
    }
    
    private String getCertificatePayloadUUID(final Long certificateID) {
        String payloadUUID = "-1";
        final Criteria identitycertCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certificateID, 0);
        final DataObject certDO = ProfileCertificateUtil.getCertificateDO(identitycertCriteria);
        try {
            if (certDO != null && !certDO.isEmpty()) {
                final ChromeCertificatePayload certPayload = this.getcertificatePayload(certDO, certificateID);
                if (certPayload != null) {
                    payloadUUID = String.valueOf(certPayload.getPayloadJSON().get("PayloadUUID"));
                }
            }
        }
        catch (final Exception ex) {
            DO2ChromePayloadHandler.LOGGER.log(Level.SEVERE, "Exception in getCertificatePayloadUUID ", ex);
        }
        return payloadUUID;
    }
    
    private ChromeCertificatePayload getcertificatePayload(final DataObject certDO, final Long certificateID) {
        if (!this.certificateMapper.containsKey(certificateID)) {
            try {
                final DO2ChromeCertificatePolicy certificate = new DO2ChromeCertificatePolicy();
                final ChromeCertificatePayload payload = certificate.createPayload(certDO);
                if (payload != null) {
                    this.certificateMapper.put(certificateID, payload);
                }
            }
            catch (final Exception ex) {
                DO2ChromePayloadHandler.LOGGER.log(Level.SEVERE, "Exception in getcertificatePayload ", ex);
            }
        }
        return this.certificateMapper.get(certificateID);
    }
    
    static {
        LOGGER = Logger.getLogger(DO2AndroidPayloadHandler.class.getName());
    }
}
