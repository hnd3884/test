package com.adventnet.sym.server.mdm.android.payload.transform;

import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONArray;
import java.util.logging.Level;
import java.util.Collection;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.android.payload.AndroidConfigurationPayload;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import java.util.HashMap;
import java.util.logging.Logger;

public class DO2AndroidPayloadHandler
{
    private static final Logger LOGGER;
    HashMap<Long, AndroidPayload> scepConfigMapper;
    
    public DO2AndroidPayloadHandler() {
        this.scepConfigMapper = new HashMap<Long, AndroidPayload>();
    }
    
    public AndroidConfigurationPayload createPayload(final DataObject dataObject, final List configDOList) {
        AndroidConfigurationPayload configurationPayload = null;
        final List payloadList = new ArrayList();
        try {
            final int configSize = configDOList.size();
            final Long collectionID = (Long)dataObject.getFirstValue("Collection", "COLLECTION_ID");
            final String collectionName = ProfileHandler.getProfileIdentifierFromCollectionID(collectionID);
            for (int k = 0; k < configSize; ++k) {
                final DataObject configDO = configDOList.get(k);
                final Integer configId = (Integer)configDO.getFirstValue("ConfigData", "CONFIG_ID");
                final JSONObject identifierJson = new JSONObject();
                identifierJson.put("collection_name", (Object)collectionName);
                if (configDO.getRows("MdConfigDataItemExtn").hasNext()) {
                    final String payloadIdentifier = (String)configDO.getFirstValue("MdConfigDataItemExtn", "CONFIG_PAYLOAD_IDENTIFIER");
                    identifierJson.put("payload_identifier", (Object)payloadIdentifier);
                }
                final List<AndroidPayload> settingsPayload = this.createPayload(configId, configDO, identifierJson);
                if (settingsPayload != null) {
                    payloadList.addAll(settingsPayload);
                }
            }
            if (!this.scepConfigMapper.isEmpty()) {
                payloadList.addAll(this.scepConfigMapper.values());
            }
            configurationPayload = this.createAndroidConfigurationPayload(dataObject, payloadList);
        }
        catch (final Exception ex) {
            DO2AndroidPayloadHandler.LOGGER.log(Level.SEVERE, "Exception in MDMCloudIAMHandler handleUser() :: ", ex);
        }
        return configurationPayload;
    }
    
    public AndroidPayload createAppPayload(final DataObject dataObject, final List configDOList) {
        AndroidPayload appPayload = null;
        try {
            final int configSize = configDOList.size();
            final String collectionName = (String)dataObject.getFirstValue("Collection", "COLLECTION_NAME");
            final JSONObject identifierJson = new JSONObject();
            identifierJson.put("collection_name", (Object)collectionName);
            for (int k = 0; k < configSize; ++k) {
                final DataObject configDO = configDOList.get(k);
                final Integer configId = (Integer)configDO.getFirstValue("ConfigData", "CONFIG_ID");
                appPayload = this.createPayload(configId, configDO, identifierJson).get(0);
            }
        }
        catch (final Exception ex) {
            DO2AndroidPayloadHandler.LOGGER.log(Level.SEVERE, "Exception in MDMCloudIAMHandler handleUser() :: ", ex);
        }
        return appPayload;
    }
    
    private AndroidConfigurationPayload createAndroidConfigurationPayload(final DataObject dataObject, final List payloadJSONList) {
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
            DO2AndroidPayloadHandler.LOGGER.log(Level.SEVERE, "Exception in createAndroidConfigurationPayload", exp);
        }
        return cfgPayload;
    }
    
    public List<AndroidPayload> createPayload(final Integer configID, final DataObject dataObject, final JSONObject identifierJson) {
        final List<AndroidPayload> payload = new ArrayList<AndroidPayload>();
        AndroidPayload settingsPayload = null;
        try {
            final String collectionName = String.valueOf(identifierJson.get("collection_name"));
            switch (configID) {
                case 185: {
                    final DO2AndroidPasscodePolicyPayload passcode = new DO2AndroidPasscodePolicyPayload();
                    settingsPayload = passcode.createPayload(dataObject);
                    final String identifier = collectionName + ".passcode";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 186: {
                    final DO2AndroidRestrictionsPolicyPayload restrictions = new DO2AndroidRestrictionsPolicyPayload();
                    settingsPayload = restrictions.createPayload(dataObject);
                    final String identifier = collectionName + ".restrictions";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 556: {
                    final DO2AndroidWifPolicyiPayload wifiPayload = new DO2AndroidWifPolicyiPayload();
                    settingsPayload = wifiPayload.createPayload(dataObject);
                    final Row wifiPayloadRow = dataObject.getFirstRow("WifiPolicy");
                    final Integer securityType = (Integer)wifiPayloadRow.get("SECURITY_TYPE");
                    if (securityType == 3) {
                        Row certificateRow = null;
                        if ((certificateRow = dataObject.getRow("WifiEnterprise")) != null) {
                            final Long identityID = (Long)certificateRow.get("IDENTITY_CERTIFICATE_ID");
                            if (identityID != null && identityID != -1L) {
                                final String identityCert = this.getCertificatePayloadUUID(identityID);
                                if (identityCert != null) {
                                    settingsPayload.setPayloadID(identityCert);
                                }
                            }
                        }
                    }
                    final String identifier = collectionName + ".wifi";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 553: {
                    final DO2AndroidEmailPolicyPayload emailPayload = new DO2AndroidEmailPolicyPayload();
                    settingsPayload = emailPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".email";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 554: {
                    final DO2AndroidActiveSyncPayload activeSyncPayload = new DO2AndroidActiveSyncPayload();
                    settingsPayload = activeSyncPayload.createPayload(dataObject);
                    final Row exchangeRow = dataObject.getRow("AndroidActiveSyncPolicy");
                    if (exchangeRow != null) {
                        final Long identityCertificateID = (Long)exchangeRow.get("IDENTITY_CERT_ID");
                        if (identityCertificateID != null && identityCertificateID != -1L) {
                            final String identityCertUUID = this.getCertificatePayloadUUID(identityCertificateID);
                            if (identityCertUUID != null) {
                                settingsPayload.setPayloadID(identityCertUUID);
                            }
                        }
                    }
                    final String identifier = collectionName + ".activesync";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 557: {
                    this.addKioskPayload(payload, identifierJson, dataObject);
                    break;
                }
                case 558: {
                    final DO2AndroidWallpaperPayload wallpaperPayload = new DO2AndroidWallpaperPayload();
                    settingsPayload = wallpaperPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".wallpaper";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 559: {
                    final DO2AndroidHttpProxyPayload proxyPayload = new DO2AndroidHttpProxyPayload();
                    settingsPayload = proxyPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".GlobalProxy";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 560: {
                    final DO2AndroidWebClipsPayload webclipsPayload = new DO2AndroidWebClipsPayload();
                    settingsPayload = webclipsPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".webclips";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 555: {
                    final DO2AndroidCertificatePayload certificatePayload = new DO2AndroidCertificatePayload();
                    settingsPayload = certificatePayload.createPayload(dataObject);
                    final String identifier = collectionName + ".certificate";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 561: {
                    final DO2AndroidWebContentPayload webContentPayload = new DO2AndroidWebContentPayload();
                    settingsPayload = webContentPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".WebContentFilter";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 562: {
                    final DO2AndroidAPNPayload apnPayload = new DO2AndroidAPNPayload();
                    settingsPayload = apnPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".APN";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 563: {
                    if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AgentMigration")) {
                        final DO2AndroidAgentMigrationPayload agentMigrationPayload = new DO2AndroidAgentMigrationPayload();
                        settingsPayload = agentMigrationPayload.createPayload(dataObject);
                        final String identifier = collectionName + ".AGD";
                        settingsPayload.setPayloadIdentifier(identifier);
                        break;
                    }
                    break;
                }
                case 564: {
                    final DO2AndroidVpnPayload androidVpnPayload = new DO2AndroidVpnPayload();
                    settingsPayload = androidVpnPayload.createPayload(dataObject);
                    String sCertificateID = null;
                    boolean certificateAuthType = false;
                    if (dataObject.containsTable("VpnPolicy")) {
                        final Row vpnPayloadRow = dataObject.getFirstRow("VpnPolicy");
                        final Integer connectionType = (Integer)vpnPayloadRow.get("CONNECTION_TYPE");
                        if (connectionType == 9 && dataObject.containsTable("VpnCisco")) {
                            final Row vpnCiscoRow = dataObject.getFirstRow("VpnCisco");
                            final Long certId = (Long)vpnCiscoRow.get("CERTIFICATE_ID");
                            if (certId != null && certId.intValue() != -1) {
                                sCertificateID = String.valueOf(vpnCiscoRow.get("CERTIFICATE_ID"));
                                if (sCertificateID != null && Long.valueOf(sCertificateID.trim()) != -1L) {
                                    certificateAuthType = true;
                                }
                            }
                        }
                        else if (connectionType == 5 && dataObject.containsTable("VpnF5SSL")) {
                            final Row vpnF5SSLRow = dataObject.getFirstRow("VpnF5SSL");
                            final Long certId = (Long)vpnF5SSLRow.get("CERTIFICATE_ID");
                            if (certId != null && certId.intValue() != -1) {
                                sCertificateID = String.valueOf(vpnF5SSLRow.get("CERTIFICATE_ID"));
                                if (sCertificateID != null && Long.valueOf(sCertificateID.trim()) != -1L) {
                                    certificateAuthType = true;
                                }
                            }
                        }
                        else if (connectionType == 19 && dataObject.containsTable("VpnL2TP")) {
                            final Row vpnL2tpRow = dataObject.getFirstRow("VpnL2TP");
                            final Long certId = (Long)vpnL2tpRow.get("USER_CERTIFICATE_ID");
                            if (certId != null && certId.intValue() != -1) {
                                sCertificateID = String.valueOf(vpnL2tpRow.get("USER_CERTIFICATE_ID"));
                                if (sCertificateID != null && Long.valueOf(sCertificateID.trim()) != -1L) {
                                    certificateAuthType = true;
                                }
                            }
                        }
                        else if (connectionType == 7 && dataObject.containsTable("VpnJuniperSSL")) {
                            final Row vpnPulseRow = dataObject.getFirstRow("VpnJuniperSSL");
                            final Long certId = (Long)vpnPulseRow.get("CERTIFICATE_ID");
                            if (certId != null && certId.intValue() != -1) {
                                sCertificateID = String.valueOf(vpnPulseRow.get("CERTIFICATE_ID"));
                                if (sCertificateID != null && Long.valueOf(sCertificateID.trim()) != -1L) {
                                    certificateAuthType = true;
                                }
                            }
                        }
                        else if (connectionType == 13 && dataObject.containsTable("VpnPaloAlto")) {
                            final Row vpnPulseRow = dataObject.getFirstRow("VpnPaloAlto");
                            final Long certId = (Long)vpnPulseRow.get("CERTIFICATE_ID");
                            if (certId != null && certId.intValue() != -1) {
                                sCertificateID = String.valueOf(vpnPulseRow.get("CERTIFICATE_ID"));
                                if (sCertificateID != null && Long.valueOf(sCertificateID.trim()) != -1L) {
                                    certificateAuthType = true;
                                }
                            }
                        }
                    }
                    if (certificateAuthType) {
                        final String certUUID = this.getCertificatePayloadUUID(Long.valueOf(sCertificateID));
                        if (certUUID != null) {
                            settingsPayload.setPayloadID(certUUID);
                        }
                    }
                    final String identifier = collectionName + ".AVP";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 565: {
                    final DO2AndroidEFRPPayload androidEFRPPayload = new DO2AndroidEFRPPayload();
                    settingsPayload = androidEFRPPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".EnterpriseFactoryResetSettings";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 566: {
                    final Row scepRow = dataObject.getRow("SCEPConfigurations");
                    final Long scepID = (Long)scepRow.get("SCEP_CONFIG_ID");
                    if (!this.scepConfigMapper.containsKey(scepID)) {
                        final DO2AndroidSCEPPayload androidSCEPPayload = new DO2AndroidSCEPPayload();
                        final AndroidPayload androidScepPayload = androidSCEPPayload.createPayload(dataObject);
                        final String identifier = collectionName + ".Scep";
                        androidScepPayload.setPayloadIdentifier(identifier);
                        if (identifierJson.has("payload_identifier")) {
                            androidScepPayload.setPayloadIdentifier(String.valueOf(identifierJson.get("payload_identifier")));
                        }
                        this.scepConfigMapper.put(scepID, androidScepPayload);
                        break;
                    }
                    break;
                }
                case 567: {
                    final DO2AndroidLockScreenPayload androidLockScreenPayload = new DO2AndroidLockScreenPayload();
                    settingsPayload = androidLockScreenPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".LockScreen";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 901: {
                    final DO2AndroidTrackingPolicy androidTrackingPolicy = new DO2AndroidTrackingPolicy();
                    settingsPayload = androidTrackingPolicy.createPayload(dataObject);
                    final String identifier = collectionName + ".DataUsePolicy";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 902: {
                    final DO2AndroidDataTrackingAction androidDataTrackingAction = new DO2AndroidDataTrackingAction();
                    settingsPayload = androidDataTrackingAction.createPayload(dataObject);
                    final String identifier = collectionName + ".DataUseAction";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 903: {
                    final DO2AndroidDataLevelPayload androidDataLevelPayload = new DO2AndroidDataLevelPayload();
                    settingsPayload = androidDataLevelPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".DataUseLevel";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
                case 568: {
                    final DO2AndroidWorkDataSecurityPayload androidWorkDataSecurityPayload = new DO2AndroidWorkDataSecurityPayload();
                    settingsPayload = androidWorkDataSecurityPayload.createPayload(dataObject);
                    final String identifier = collectionName + ".WorkDataSecurityPolicy";
                    settingsPayload.setPayloadIdentifier(identifier);
                    break;
                }
            }
            if (payload.size() == 0) {
                this.setAndroidPayloadIdentifier(identifierJson, settingsPayload);
                if (settingsPayload != null) {
                    payload.add(settingsPayload);
                }
            }
        }
        catch (final Exception exp) {
            DO2AndroidPayloadHandler.LOGGER.log(Level.SEVERE, "Exception in createPayload ", exp);
        }
        return payload;
    }
    
    private void addKioskPayload(final List payload, final JSONObject identifierJson, final DataObject dataObject) throws Exception {
        final DO2AndroidKioskPolicyPayload kioskPayload = new DO2AndroidKioskPolicyPayload();
        final AndroidPayload settingsPayload = kioskPayload.createPayload(dataObject);
        final String collectionName = String.valueOf(identifierJson.get("collection_name"));
        final String identifier = collectionName + ".kiosk";
        settingsPayload.setPayloadIdentifier(identifier);
        this.setAndroidPayloadIdentifier(identifierJson, settingsPayload);
        payload.add(settingsPayload);
        final String kioskIdentifier = settingsPayload.getPayloadJSON().getString("PayloadIdentifier");
        final Row row = dataObject.getRow("AndroidKioskPolicy");
        if (row != null) {
            final boolean distributeWebApp = (boolean)row.get("AUTO_DISTRIBUTE_WEBAPP");
            final int kioskType = (int)row.get("KIOSK_MODE");
            if ((distributeWebApp && kioskType == 1) || kioskType == 3) {
                final Iterator webClipIterator = dataObject.getRows("WebClipPolicies");
                final DO2AndroidWebClipsPayload webClipsPayload = new DO2AndroidWebClipsPayload();
                while (webClipIterator.hasNext()) {
                    final Row webClipsRow = webClipIterator.next();
                    final Long webclipPolicyId = (Long)webClipsRow.get("WEBCLIP_POLICY_ID");
                    final List tables = new ArrayList();
                    tables.add("WebClipPolicies");
                    final DataObject webClipDO = dataObject.getDataObject(tables, webClipsRow);
                    final AndroidPayload webAppPayload = webClipsPayload.createPayload(webClipDO);
                    final String kioskWebClipIdentifier = kioskIdentifier + ".webclips" + webclipPolicyId;
                    webAppPayload.setPayloadIdentifier(kioskWebClipIdentifier);
                    payload.add(webAppPayload);
                }
            }
        }
        if (dataObject.containsTable("ScreenLayoutSettings")) {
            final DO2AndroidScreenLayoutPayload homescreenPayload = new DO2AndroidScreenLayoutPayload();
            final AndroidPayload layoutPayload = homescreenPayload.createPayload(dataObject);
            final String homeScreenPayloadIdentifier = kioskIdentifier + ".screenlayout";
            layoutPayload.setPayloadIdentifier(homeScreenPayloadIdentifier);
            payload.add(layoutPayload);
        }
    }
    
    private void setAndroidPayloadIdentifier(final JSONObject identifierJson, final AndroidPayload settingsPayload) {
        if (identifierJson.has("payload_identifier") && settingsPayload != null) {
            settingsPayload.setPayloadIdentifier(String.valueOf(identifierJson.get("payload_identifier")));
        }
    }
    
    private String getCertificatePayloadUUID(final Long certificateID) {
        String payloadUUID = null;
        final Criteria identitycertCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certificateID, 0);
        final DataObject certificatesDO = ProfileCertificateUtil.getCertificateDO(identitycertCriteria);
        try {
            if (certificatesDO != null && !certificatesDO.isEmpty()) {
                final Row identityRow = certificatesDO.getFirstRow("Certificates");
                final int type = (int)identityRow.get("CERTIFICATE_TYPE");
                if (type == 1) {
                    final AndroidPayload scepPayload = this.getSCEPConfigPayload(certificatesDO, certificateID);
                    if (scepPayload != null) {
                        payloadUUID = String.valueOf(scepPayload.getPayloadJSON().get("PayloadUUID"));
                    }
                }
            }
        }
        catch (final Exception ex) {
            DO2AndroidPayloadHandler.LOGGER.log(Level.SEVERE, "DO2AndroidPayloadHandler: Exception while retrieving the payload UUID: ", ex);
        }
        return payloadUUID;
    }
    
    private AndroidPayload getSCEPConfigPayload(final DataObject scepDO, final Long scepID) {
        if (!this.scepConfigMapper.containsKey(scepID)) {
            try {
                final AndroidPayload payload = this.createSCEPPayload(scepDO);
                if (payload != null) {
                    this.scepConfigMapper.put(scepID, payload);
                }
            }
            catch (final Exception ex) {
                DO2AndroidPayloadHandler.LOGGER.log(Level.SEVERE, "DO2AndroidPayloadHandler: Exception while creating SCEP payload : ", ex);
            }
        }
        return this.scepConfigMapper.get(scepID);
    }
    
    private AndroidPayload createSCEPPayload(final DataObject scepDO) {
        final DO2AndroidSCEPPayload scep = new DO2AndroidSCEPPayload();
        return scep.createPayload(scepDO);
    }
    
    static {
        LOGGER = Logger.getLogger(DO2AndroidPayloadHandler.class.getName());
    }
}
