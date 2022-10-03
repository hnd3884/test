package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.ios.payload.transform.remove.DO2RemoveMacFileVaultPolicy;
import com.me.mdm.server.profiles.ios.IOSSingletonRestrictionHandler;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.sym.server.mdm.ios.payload.CertificatePayload;
import com.me.mdm.api.core.certificate.CredentialCertificate;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;
import com.adventnet.sym.server.mdm.certificates.scep.DynamicScepServer;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.ios.payload.VPNPayLoadType;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.SSOPayload;
import com.adventnet.sym.server.mdm.ios.payload.MACFileVaultRecoveryKeyEscrowPayload;
import com.adventnet.sym.server.mdm.ios.payload.MACFileVaultPayload;
import com.adventnet.sym.server.mdm.ios.payload.WebConentFilterPayload;
import com.adventnet.sym.server.mdm.ios.payload.WifiPayload;
import com.adventnet.sym.server.mdm.ios.payload.ExchangeActiveSyncPayload;
import com.adventnet.sym.server.mdm.ios.payload.EMailPayload;
import org.json.JSONObject;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSArray;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.ios.payload.ConfigurationPayload;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import java.util.HashMap;
import java.util.logging.Logger;

public class DO2PayloadHandler
{
    private final Logger logger;
    public static final String OS_UPDATE_RESTRICTION_PAYLOAD_IDENTIFIER = "com.mdm.osupdate.restriction";
    HashMap<Long, IOSPayload> certificateMapper;
    HashMap<Long, IOSPayload> scepConfigMapper;
    HashMap<Long, IOSPayload> adCertConfigMapper;
    
    public DO2PayloadHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.certificateMapper = new HashMap<Long, IOSPayload>();
        this.scepConfigMapper = new HashMap<Long, IOSPayload>();
        this.adCertConfigMapper = new HashMap<Long, IOSPayload>();
    }
    
    public ConfigurationPayload createPayload(final DataObject dataObject, final List configDOList) {
        ConfigurationPayload configurationPayload = null;
        final List payloadList = new ArrayList();
        try {
            for (int configSize = configDOList.size(), k = 0; k < configSize; ++k) {
                final DataObject configDO = configDOList.get(k);
                final Integer configId = (Integer)configDO.getFirstValue("ConfigData", "CONFIG_ID");
                final IOSPayload[] settingsPayload = this.createPayload(configId, configDO);
                if (settingsPayload != null) {
                    for (final IOSPayload payload : settingsPayload) {
                        if (payload != null) {
                            payloadList.add(payload);
                        }
                    }
                }
            }
            if (!this.certificateMapper.isEmpty()) {
                payloadList.addAll(this.certificateMapper.values());
            }
            if (!this.scepConfigMapper.isEmpty()) {
                payloadList.addAll(this.scepConfigMapper.values());
            }
            if (!this.adCertConfigMapper.isEmpty()) {
                payloadList.addAll(this.adCertConfigMapper.values());
            }
            configurationPayload = this.createConfigurationPayload(dataObject, payloadList);
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in create payload", (Throwable)ex);
        }
        return configurationPayload;
    }
    
    public IOSPayload createAppPayload(final DataObject dataObject, final List configDOList) {
        IOSPayload appPayload = null;
        try {
            for (int configSize = configDOList.size(), k = 0; k < configSize; ++k) {
                final DataObject configDO = configDOList.get(k);
                final Integer configId = (Integer)configDO.getFirstValue("ConfigData", "CONFIG_ID");
                final IOSPayload[] settingsPayload = this.createPayload(configId, configDO);
                appPayload = settingsPayload[0];
            }
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in createAppPayload", (Throwable)ex);
        }
        return appPayload;
    }
    
    private ConfigurationPayload createConfigurationPayload(final DataObject dataObject, final List payloadDictList) {
        ConfigurationPayload cfgPayload = null;
        try {
            final String collectionName = (String)dataObject.getFirstValue("Collection", "COLLECTION_NAME");
            final Long collectionID = (Long)dataObject.getFirstValue("Collection", "COLLECTION_ID");
            final String profileIdentifier = ProfileHandler.getProfileIdentifierFromCollectionID(collectionID);
            cfgPayload = new ConfigurationPayload(1, "MDM", profileIdentifier, collectionName);
            final Integer securityType = (Integer)dataObject.getFirstValue("IOSCollectionPayload", "SECURITY_TYPE");
            cfgPayload.setPayloadRemovalDisallowed(securityType);
            if (payloadDictList.size() > 0) {
                final NSArray nsarray = new NSArray(payloadDictList.size());
                for (int i = 0; i < payloadDictList.size(); ++i) {
                    nsarray.setValue(i, (Object)payloadDictList.get(i).getPayloadDict());
                }
                cfgPayload.setPayloadContent(nsarray);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in create config payload", exp);
        }
        return cfgPayload;
    }
    
    private ConfigurationPayload createRemoveConfigurationPayload(final DataObject dataObject, final List payloadDictList) {
        final ConfigurationPayload configurationPayload = this.createConfigurationPayload(dataObject, payloadDictList);
        configurationPayload.setDurationForRemoval(86400.0f);
        return configurationPayload;
    }
    
    public NSArray createManagedSettingsArrayItem(final List configIDList, final DataObject dataObject) {
        try {
            final List<NSDictionary> settingList = new ArrayList<NSDictionary>();
            for (int i = 0; i < configIDList.size(); ++i) {
                final DataObject configDO = configIDList.get(i);
                final Integer configID = (Integer)configDO.getFirstValue("ConfigData", "CONFIG_ID");
                final List<NSDictionary> settingPayload = this.createManagedSettingItem(configID, configDO);
                settingList.addAll(settingPayload);
            }
            final NSArray configArray = new NSArray(settingList.size());
            for (int k = 0; k < settingList.size(); ++k) {
                configArray.setValue(k, (Object)settingList.get(k));
            }
            return configArray;
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public NSArray createSharedDeviceRestrictionItem(final List configIDList) {
        try {
            final List<NSDictionary> settingList = new ArrayList<NSDictionary>();
            for (int i = 0; i < configIDList.size(); ++i) {
                final DataObject configDO = configIDList.get(i);
                final Integer configID = (Integer)configDO.getFirstValue("ConfigData", "CONFIG_ID");
                if (configID == 527) {
                    final DO2SharedDeviceSettings sharedDevicePayload = new DO2SharedDeviceSettings();
                    settingList.addAll(sharedDevicePayload.createSettingCommand(configDO, new JSONObject()));
                }
            }
            final NSArray configArray = new NSArray(settingList.size());
            for (int k = 0; k < settingList.size(); ++k) {
                configArray.setValue(k, (Object)settingList.get(k));
            }
            return configArray;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in createSharedDeviceRestrictionItem", e);
            return null;
        }
    }
    
    public NSArray createLockScreenSettingArrayItem(final List configIDList, final JSONObject params) {
        try {
            List<NSDictionary> settingList = new ArrayList<NSDictionary>();
            for (int i = 0; i < configIDList.size(); ++i) {
                final DataObject configDO = configIDList.get(i);
                final Integer configID = (Integer)configDO.getFirstValue("ConfigData", "CONFIG_ID");
                if (configID.equals(522)) {
                    final DO2LockScreenPayload lockScreenPayload = new DO2LockScreenPayload();
                    settingList = lockScreenPayload.createSettingCommand(configDO, params);
                }
            }
            final NSArray configArray = new NSArray(settingList.size());
            for (int k = 0; k < settingList.size(); ++k) {
                configArray.setValue(k, (Object)settingList.get(k));
            }
            return configArray;
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private List<NSDictionary> createManagedSettingItem(final Integer configID, final DataObject dataObject) {
        final List<NSDictionary> profileSetting = new ArrayList<NSDictionary>();
        try {
            final JSONObject params = new JSONObject();
            switch (configID) {
                case 518: {
                    final DO2iOSWallpaperPayload ioswallpaper = new DO2iOSWallpaperPayload();
                    final List wallpaperList = ioswallpaper.createSettingCommand(dataObject, params);
                    profileSetting.addAll(wallpaperList);
                    break;
                }
                case 521: {
                    final Do2ApplicationAttibutes applicationAttibutes = new Do2ApplicationAttibutes();
                    final List applicationAttibList = applicationAttibutes.createSettingCommand(dataObject, params);
                    profileSetting.addAll(applicationAttibList);
                    break;
                }
                case 173:
                case 951: {
                    final DO2RestrictionSettings restrictionSettings = new DO2RestrictionSettings();
                    final List restrictionList = restrictionSettings.createSettingCommand(dataObject, params);
                    profileSetting.addAll(restrictionList);
                    break;
                }
                case 529: {
                    final DO2IOSAccessibilitySettings accessibilitySettings = new DO2IOSAccessibilitySettings();
                    final List accessibilityList = accessibilitySettings.createSettingCommand(dataObject, params);
                    profileSetting.addAll(accessibilityList);
                    break;
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception while creating profile setting", ex);
        }
        return profileSetting;
    }
    
    private IOSPayload[] createPayload(final Integer configID, final DataObject dataObject) {
        IOSPayload[] settingsPayload = null;
        try {
            switch (configID) {
                case 172: {
                    final DO2PasscodePolicyPayload passcode = new DO2PasscodePolicyPayload();
                    settingsPayload = passcode.createPayload(dataObject);
                    break;
                }
                case 765: {
                    final DO2ADCertPayload adCertPayload = new DO2ADCertPayload();
                    settingsPayload = adCertPayload.createPayload(dataObject);
                    break;
                }
                case 757: {
                    final DO2MacPasscodePolicyPayload macpasscode = new DO2MacPasscodePolicyPayload();
                    settingsPayload = macpasscode.createPayload(dataObject);
                    break;
                }
                case 173: {
                    final DO2RestrictionsPolicyPayload restrictions = new DO2RestrictionsPolicyPayload();
                    settingsPayload = restrictions.createPayload(dataObject);
                    break;
                }
                case 751: {
                    final DO2MacRestrictionPolicy restrictionPolicy = new DO2MacRestrictionPolicy();
                    settingsPayload = restrictionPolicy.createPayload(dataObject);
                    break;
                }
                case 759: {
                    final DO2MacSystemPreferencePayload preferencePayload = new DO2MacSystemPreferencePayload();
                    settingsPayload = preferencePayload.createPayload(dataObject);
                    break;
                }
                case 760: {
                    final DO2MacEnergySaverPolicyPayload energySaverPolicyPayload = new DO2MacEnergySaverPolicyPayload();
                    settingsPayload = energySaverPolicyPayload.createPayload(dataObject);
                    break;
                }
                case 179: {
                    final DO2CalDavPolicyPayload calDav = new DO2CalDavPolicyPayload();
                    settingsPayload = calDav.createPayload(dataObject);
                    break;
                }
                case 181: {
                    final DO2CardDavPolicyPayload cardDave = new DO2CardDavPolicyPayload();
                    settingsPayload = cardDave.createPayload(dataObject);
                    break;
                }
                case 174: {
                    final DO2EmailPolicyPayload email = new DO2EmailPolicyPayload();
                    final IOSPayload[] payloadArray = email.createPayload(dataObject);
                    settingsPayload = new IOSPayload[payloadArray.length];
                    for (int i = 0; i < payloadArray.length; ++i) {
                        final EMailPayload emailPayload = (EMailPayload)payloadArray[i];
                        final Row emailRow = dataObject.getRow("EMailPolicy");
                        final Boolean useMimeEncryptInEmail = (Boolean)emailRow.get("USE_MIME_ENCRYPT");
                        if (useMimeEncryptInEmail) {
                            final Long encryptionCertificateID = (Long)emailRow.get("ENCRYPTION_CERT_ID");
                            if (encryptionCertificateID != null && encryptionCertificateID != -1L) {
                                final String encrypCertUUID = this.getCertificatePayloadUUID(encryptionCertificateID);
                                if (encrypCertUUID != null) {
                                    emailPayload.setSMIMEEncryptionCertificateUUID(encrypCertUUID);
                                }
                            }
                            final Long signingCertificateID = (Long)emailRow.get("SIGNING_CERT_ID");
                            if (signingCertificateID != null && signingCertificateID != -1L) {
                                final String signingCertUUID = this.getCertificatePayloadUUID(signingCertificateID);
                                if (signingCertUUID != null) {
                                    emailPayload.setSMIMESigningCertificateUUID(signingCertUUID);
                                }
                            }
                        }
                        settingsPayload[i] = emailPayload;
                    }
                    break;
                }
                case 175: {
                    final DO2ExchangeActiveSyncPayload sync = new DO2ExchangeActiveSyncPayload();
                    final IOSPayload[] exchangePayloadArray = sync.createPayload(dataObject);
                    settingsPayload = new IOSPayload[exchangePayloadArray.length];
                    for (int j = 0; j < exchangePayloadArray.length; ++j) {
                        final ExchangeActiveSyncPayload exchangepayload = (ExchangeActiveSyncPayload)exchangePayloadArray[j];
                        final Row exchangeRow = dataObject.getRow("ExchangeActiveSyncPolicy");
                        if (exchangeRow != null) {
                            final Long identityCertificateID = (Long)exchangeRow.get("IDENTITY_CERT_ID");
                            if (identityCertificateID != null && identityCertificateID != -1L) {
                                final String identityCertUUID = this.getCertificatePayloadUUID(identityCertificateID);
                                if (identityCertUUID != null) {
                                    exchangepayload.setPayloadCertificateUUID(identityCertUUID);
                                }
                            }
                            final Boolean useMimeEncryptInExchange = (Boolean)exchangeRow.get("USE_MIME_ENCRYPT");
                            if (useMimeEncryptInExchange) {
                                final Long encryptionCertificateID2 = (Long)exchangeRow.get("ENCRYPTION_CERT_ID");
                                if (encryptionCertificateID2 != null && encryptionCertificateID2 != -1L) {
                                    final String encrypCertUUID2 = this.getCertificatePayloadUUID(encryptionCertificateID2);
                                    if (encrypCertUUID2 != null) {
                                        exchangepayload.setSMIMEEncryptionCertificateUUID(encrypCertUUID2);
                                    }
                                }
                                final Long signingCertificateID2 = (Long)exchangeRow.get("SIGNING_CERT_ID");
                                if (signingCertificateID2 != null && signingCertificateID2 != -1L) {
                                    final String signingCertUUID2 = this.getCertificatePayloadUUID(signingCertificateID2);
                                    if (signingCertUUID2 != null) {
                                        exchangepayload.setSMIMESigningCertificateUUID(signingCertUUID2);
                                    }
                                }
                            }
                        }
                        settingsPayload[j] = exchangepayload;
                    }
                    break;
                }
                case 182: {
                    final DO2WebClipsPayload webclips = new DO2WebClipsPayload();
                    settingsPayload = webclips.createPayload(dataObject);
                    break;
                }
                case 176:
                case 521:
                case 766: {
                    final DO2VpnPayload do2VpnPayload = new DO2VpnPayload();
                    final IOSPayload[] vpnPayloadArray = do2VpnPayload.createPayload(dataObject);
                    settingsPayload = this.getVPNPayloadWithCertificate(dataObject, vpnPayloadArray);
                    break;
                }
                case 756: {
                    final DO2MacVpnPayload do2MacVpnPayload = new DO2MacVpnPayload();
                    final IOSPayload[] macPayloadArray = this.getVPNPayloadWithCertificate(dataObject, do2MacVpnPayload.createPayload(dataObject));
                    final DO2MacPerAppVPNPayload perAppVPNPayload = new DO2MacPerAppVPNPayload();
                    final IOSPayload[] macPerAppVpnPayloadArray = perAppVPNPayload.createPayload(dataObject);
                    settingsPayload = new IOSPayload[macPayloadArray.length + macPerAppVpnPayloadArray.length];
                    int counter = 0;
                    for (int k = 0; k < macPayloadArray.length; ++k) {
                        settingsPayload[counter] = macPayloadArray[k];
                        ++counter;
                    }
                    for (int k = 0; k < macPerAppVpnPayloadArray.length; ++k) {
                        settingsPayload[counter] = macPerAppVpnPayloadArray[k];
                        ++counter;
                    }
                    break;
                }
                case 301: {
                    final DO2AppsPayload appsPayload = new DO2AppsPayload();
                    settingsPayload = appsPayload.createPayload(dataObject);
                    break;
                }
                case 178: {
                    final DO2LdapPayload ldapPayload = new DO2LdapPayload();
                    settingsPayload = ldapPayload.createPayload(dataObject);
                    break;
                }
                case 180: {
                    final DO2SubscribedCalendarPayload subscriberPayload = new DO2SubscribedCalendarPayload();
                    settingsPayload = subscriberPayload.createPayload(dataObject);
                    break;
                }
                case 177:
                case 774: {
                    final DO2WifiPayload wifiPayload = new DO2WifiPayload();
                    final IOSPayload[] wifiPayloadArray = wifiPayload.createPayload(dataObject);
                    settingsPayload = new IOSPayload[wifiPayloadArray.length];
                    for (int l = 0; l < wifiPayloadArray.length; ++l) {
                        final WifiPayload payload = (WifiPayload)wifiPayloadArray[l];
                        final Row wifiPayloadRow = dataObject.getFirstRow("WifiPolicy");
                        final Integer securityType = (Integer)wifiPayloadRow.get("SECURITY_TYPE");
                        if (securityType == 4 || securityType == 5 || securityType == 6) {
                            Row certificateRow = null;
                            if ((certificateRow = dataObject.getRow("WifiEnterprise")) != null) {
                                final Long certificateID = (Long)certificateRow.get("CERTIFICATE_ID");
                                final Long identityID = (Long)certificateRow.get("IDENTITY_CERTIFICATE_ID");
                                if (certificateID != null && certificateID != -1L) {
                                    final String[] certificateUUIDArray = { this.getCertificatePayloadUUID(certificateID) };
                                    if (certificateUUIDArray[0] != null) {
                                        payload.setPayloadCertificateAnchorUUID(certificateUUIDArray);
                                    }
                                }
                                if (identityID != null && identityID != -1L) {
                                    final String identityCert = this.getCertificatePayloadUUID(identityID);
                                    if (identityCert != null) {
                                        payload.setPayloadCertificateUUID(identityCert);
                                    }
                                }
                            }
                        }
                        settingsPayload[l] = payload;
                    }
                    break;
                }
                case 183: {
                    final DO2AppLockPayload appLockPayload = new DO2AppLockPayload();
                    settingsPayload = appLockPayload.createPayload(dataObject);
                    break;
                }
                case 184:
                case 768: {
                    final DO2GlobalHttpProxyPayload globalHttpProxyPayload = new DO2GlobalHttpProxyPayload();
                    settingsPayload = globalHttpProxyPayload.createPayload(dataObject);
                    break;
                }
                case 187: {
                    final DO2CellularPayload cellularPayload = new DO2CellularPayload();
                    settingsPayload = cellularPayload.createPayload(dataObject);
                    break;
                }
                case 188: {
                    final Do2WebContentPolicyPayload webContent = new Do2WebContentPolicyPayload();
                    final IOSPayload[] iosWCFPayloadArray = webContent.createPayload(dataObject);
                    settingsPayload = new IOSPayload[iosWCFPayloadArray.length];
                    for (int m = 0; m < iosWCFPayloadArray.length; ++m) {
                        final WebConentFilterPayload wcfPayload = (WebConentFilterPayload)iosWCFPayloadArray[m];
                        final Row appleWCFRow = dataObject.getRow("AppleWCFConfig");
                        if (appleWCFRow != null) {
                            final Long payloadCertificateID = (Long)appleWCFRow.get("CERTIFICATE_ID");
                            if (payloadCertificateID != null && payloadCertificateID != -1L) {
                                final String payloadCertUUID = this.getCertificatePayloadUUID(payloadCertificateID);
                                if (payloadCertUUID != null) {
                                    wcfPayload.setPayloadCertificateUUID(payloadCertUUID);
                                }
                            }
                        }
                        settingsPayload[m] = wcfPayload;
                    }
                    break;
                }
                case 758: {
                    final DO2MacWebContentPolicyPayload macWebContent = new DO2MacWebContentPolicyPayload();
                    final IOSPayload[] macWCFPayloadArray = macWebContent.createPayload(dataObject);
                    settingsPayload = new IOSPayload[macWCFPayloadArray.length];
                    for (int i2 = 0; i2 < macWCFPayloadArray.length; ++i2) {
                        final WebConentFilterPayload wcfPayload2 = (WebConentFilterPayload)macWCFPayloadArray[i2];
                        final Row appleWCFRow2 = dataObject.getRow("AppleWCFConfig");
                        if (appleWCFRow2 != null) {
                            final Long payloadCertificateID2 = (Long)appleWCFRow2.get("CERTIFICATE_ID");
                            if (payloadCertificateID2 != null && payloadCertificateID2 != -1L) {
                                final String payloadCertUUID2 = this.getCertificatePayloadUUID(payloadCertificateID2);
                                if (payloadCertUUID2 != null) {
                                    wcfPayload2.setPayloadCertificateUUID(payloadCertUUID2);
                                }
                            }
                        }
                        settingsPayload[i2] = wcfPayload2;
                    }
                    break;
                }
                case 515:
                case 772: {
                    Row certificateRow2 = null;
                    certificateRow2 = dataObject.getRow("CredentialCertificateInfo");
                    final Long cerId = (Long)certificateRow2.get("CERTIFICATE_ID");
                    if (!this.certificateMapper.containsKey(cerId)) {
                        final DO2CertificatePolicyPayload certificate = new DO2CertificatePolicyPayload();
                        final IOSPayload[] certificatePayload = certificate.createPayload(dataObject);
                        this.certificateMapper.put(cerId, certificatePayload[0]);
                        break;
                    }
                    break;
                }
                case 516:
                case 773: {
                    final Row scepRow = dataObject.getRow("SCEPConfigurations");
                    final Long scepID = (Long)scepRow.get("SCEP_CONFIG_ID");
                    if (!this.scepConfigMapper.containsKey(scepID)) {
                        final DO2iOSSCEPPayload scep = new DO2iOSSCEPPayload();
                        final IOSPayload[] scepPayload = scep.createPayload(dataObject);
                        this.scepConfigMapper.put(scepID, scepPayload[0]);
                    }
                    final Row scepServerRow = dataObject.getRow("SCEPServers");
                    final Long caCertificateID = (Long)scepServerRow.get("CA_CERTIFICATE_ID");
                    if (caCertificateID != null && caCertificateID > 0L) {
                        this.getCertificatePayloadUUID(caCertificateID);
                        break;
                    }
                    break;
                }
                case 517: {
                    final DO2ManagedDomainPayload managedContent = new DO2ManagedDomainPayload();
                    settingsPayload = managedContent.createPayload(dataObject);
                    break;
                }
                case 518:
                case 522: {
                    settingsPayload = new IOSPayload[] { null };
                    final DO2RestrictionsPolicyPayload singletonRestrict = new DO2RestrictionsPolicyPayload();
                    settingsPayload[0] = singletonRestrict.autoCreateRestrictionPayload(configID, dataObject);
                    break;
                }
                case 519:
                case 769: {
                    final DO2AirPrintPolicyPayload airPrint = new DO2AirPrintPolicyPayload();
                    settingsPayload = airPrint.createPayload(dataObject);
                    break;
                }
                case 770: {
                    final DO2FileVaultPolicy fileVault = new DO2FileVaultPolicy();
                    final IOSPayload[] filevaultPayloadArray = fileVault.createPayload(dataObject);
                    final IOSPayload[] certificateFilevaultPayloadArray = new IOSPayload[filevaultPayloadArray.length];
                    for (int i3 = 0; i3 < filevaultPayloadArray.length; ++i3) {
                        final MACFileVaultPayload fileVaultPayload = (MACFileVaultPayload)filevaultPayloadArray[i3];
                        if (fileVault.isInstitutionalRecoveryKey) {
                            final Long certificateID2 = fileVault.institutionalConfigurationJSON.getLong("INSTITUTION_ENCRYPTION_CERT");
                            final String certificateUUID = this.getCertificatePayloadUUID(certificateID2);
                            fileVaultPayload.setPayloadCertificateUUID(certificateUUID);
                        }
                        certificateFilevaultPayloadArray[i3] = fileVaultPayload;
                    }
                    IOSPayload[] certificateEscrowPayloadArray = null;
                    if (fileVault.canCreateEscrowPayload) {
                        final Do2MacFileVaultEscrowPolicy fileVaultEscrow = new Do2MacFileVaultEscrowPolicy();
                        final IOSPayload[] escrowPayloadArray = fileVaultEscrow.createPayload(dataObject);
                        certificateEscrowPayloadArray = new IOSPayload[escrowPayloadArray.length];
                        for (int i4 = 0; i4 < escrowPayloadArray.length; ++i4) {
                            final MACFileVaultRecoveryKeyEscrowPayload ecsrowPayload = (MACFileVaultRecoveryKeyEscrowPayload)escrowPayloadArray[i4];
                            final Long certificateID3 = fileVault.personalConfigurationJSON.getLong("RECOVERY_ENCRYPT_CERT_ID");
                            if (certificateID3 != null) {
                                final String certificateUUID2 = this.getCertificatePayloadUUID(certificateID3);
                                ecsrowPayload.setEncryptCertPayloadUUID(certificateUUID2);
                            }
                            certificateEscrowPayloadArray[i4] = ecsrowPayload;
                        }
                    }
                    settingsPayload = mergePayload(certificateFilevaultPayloadArray, certificateEscrowPayloadArray);
                    break;
                }
                case 520: {
                    final Do2SSOPayload ssoPayload = new Do2SSOPayload();
                    final IOSPayload[] ssoPayloadArray = ssoPayload.createPayload(dataObject);
                    settingsPayload = new IOSPayload[ssoPayloadArray.length];
                    for (int i4 = 0; i4 < ssoPayloadArray.length; ++i4) {
                        final SSOPayload ssopayload = (SSOPayload)ssoPayloadArray[i4];
                        if (dataObject.containsTable("SSOToCertificateRel")) {
                            Row ssorow = null;
                            if ((ssorow = dataObject.getRow("SSOToCertificateRel")) != null) {
                                final Long certificateId = (Long)ssorow.get("CLIENT_CERT_ID");
                                if (certificateId != null && certificateId != -1L) {
                                    final String certificateUUID3 = this.getCertificatePayloadUUID(certificateId);
                                    ssopayload.setPayloadCertificateUUID(certificateUUID3);
                                }
                            }
                        }
                        settingsPayload[i4] = ssopayload;
                    }
                    break;
                }
                case 771: {
                    final DO2DirectoryBindPolicyPayload directoryPayload = new DO2DirectoryBindPolicyPayload();
                    settingsPayload = directoryPayload.createPayload(dataObject);
                    break;
                }
                case 525:
                case 767: {
                    final DO2CustomProfilePayload customProfilePayload = new DO2CustomProfilePayload();
                    settingsPayload = customProfilePayload.createPayload(dataObject);
                    break;
                }
                case 752: {
                    final DO2MacAccountConfigPayload accountConfigPayload = new DO2MacAccountConfigPayload();
                    settingsPayload = accountConfigPayload.createPayload(dataObject);
                    break;
                }
                case 754: {
                    final DO2MacPPPCPolicyPayload pppcPolicyPayload = new DO2MacPPPCPolicyPayload();
                    settingsPayload = pppcPolicyPayload.createPayload(dataObject);
                    break;
                }
                case 755: {
                    final DO2MacSystemExtensionPayload systemExtensionPayload = new DO2MacSystemExtensionPayload();
                    settingsPayload = systemExtensionPayload.createPayload(dataObject);
                    break;
                }
                case 761: {
                    final DO2LoginWindowSettingPayload loginWindowSettingPayload = new DO2LoginWindowSettingPayload();
                    settingsPayload = loginWindowSettingPayload.createPayload(dataObject);
                    break;
                }
                case 762: {
                    final DO2MacLoginWindowItemSettingPayload loginItemSettingPayload = new DO2MacLoginWindowItemSettingPayload();
                    settingsPayload = loginItemSettingPayload.createPayload(dataObject);
                    break;
                }
                case 526:
                case 763: {
                    final DO2FontPayload fontPayload = new DO2FontPayload();
                    settingsPayload = fontPayload.createPayload(dataObject);
                    break;
                }
                case 764: {
                    final DO2MacGatekeeperSettingPayload gatekeeperSettingPayload = new DO2MacGatekeeperSettingPayload();
                    settingsPayload = gatekeeperSettingPayload.createPayload(dataObject);
                    break;
                }
                case 951: {
                    final DO2TvOSRestrictionPolicy tvRestrictionPolicy = new DO2TvOSRestrictionPolicy();
                    settingsPayload = tvRestrictionPolicy.createPayload(dataObject);
                    break;
                }
                case 527: {
                    final DO2SharedDeviceConfigurationPayload sharedPolicy = new DO2SharedDeviceConfigurationPayload();
                    settingsPayload = sharedPolicy.createPayload(dataObject);
                    break;
                }
                case 528:
                case 775: {
                    final DO2AppNotificationPolicyPayload notificationPolicy = new DO2AppNotificationPolicyPayload();
                    settingsPayload = notificationPolicy.createPayload(dataObject);
                    break;
                }
                case 529: {
                    final DO2IOSAccessibilitySettings accessibilitySetting = new DO2IOSAccessibilitySettings();
                    final IOSPayload[] dummyPayload = accessibilitySetting.createPayload(dataObject);
                    if (dummyPayload != null) {
                        settingsPayload = dummyPayload;
                        break;
                    }
                    break;
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "DO2PayloadHandler: Error while createPayload() ", exp);
        }
        return settingsPayload;
    }
    
    private IOSPayload[] getVPNPayloadWithCertificate(final DataObject dataObject, final IOSPayload[] vpnPayloadArray) throws Exception {
        final IOSPayload[] settingsPayload = new IOSPayload[vpnPayloadArray.length];
        for (int i = 0; i < vpnPayloadArray.length; ++i) {
            final VPNPayLoadType vpnPayload = (VPNPayLoadType)vpnPayloadArray[i];
            String sCertificateID = null;
            boolean certificateAuthType = false;
            if (dataObject.containsTable("VpnPolicy")) {
                final Row vpnPayloadRow = dataObject.getFirstRow("VpnPolicy");
                Integer connectionType = (Integer)vpnPayloadRow.get("CONNECTION_TYPE");
                ++connectionType;
                if (connectionType == 3 && dataObject.containsTable("VpnIPSec")) {
                    final Row vpnIPSecRow = dataObject.getFirstRow("VpnIPSec");
                    final Integer authType = (Integer)vpnIPSecRow.get("MACHINE_AUTHENTICATION");
                    if (authType == 1) {
                        sCertificateID = String.valueOf(vpnIPSecRow.get("CERTIFICATE_ID"));
                        if (sCertificateID != null && Long.valueOf(sCertificateID.trim()) != -1L) {
                            certificateAuthType = true;
                        }
                    }
                }
                else if ((connectionType == 10 || connectionType == 4 || connectionType == 11) && dataObject.containsTable("VpnCisco")) {
                    final Row vpnCiscoRow = dataObject.getFirstRow("VpnCisco");
                    final Integer authType = (Integer)vpnCiscoRow.get("USER_AUTHENTICATION");
                    if (authType == 1) {
                        sCertificateID = String.valueOf(vpnCiscoRow.get("CERTIFICATE_ID"));
                        if (sCertificateID != null && Long.valueOf(sCertificateID.trim()) != -1L) {
                            certificateAuthType = true;
                        }
                    }
                }
                else if (connectionType == 5 && dataObject.containsTable("VpnJuniperSSL")) {
                    final Row vpnJuniperRow = dataObject.getFirstRow("VpnJuniperSSL");
                    final Integer authType = (Integer)vpnJuniperRow.get("USER_AUTHENTICATION");
                    if (authType == 1) {
                        sCertificateID = String.valueOf(vpnJuniperRow.get("CERTIFICATE_ID"));
                        if (sCertificateID != null && Long.valueOf(sCertificateID.trim()) != -1L) {
                            certificateAuthType = true;
                        }
                    }
                }
                else if ((connectionType == 6 || connectionType == 12 || connectionType == 13) && dataObject.containsTable("VpnF5SSL")) {
                    final Row vpnF5SSLRow = dataObject.getFirstRow("VpnF5SSL");
                    final Integer authType = (Integer)vpnF5SSLRow.get("USER_AUTHENTICATION");
                    if (authType == 1) {
                        sCertificateID = String.valueOf(vpnF5SSLRow.get("CERTIFICATE_ID"));
                        if (sCertificateID != null && Long.valueOf(sCertificateID.trim()) != -1L) {
                            certificateAuthType = true;
                        }
                    }
                }
                else if (connectionType == 9 && dataObject.containsTable("VpnIKEv2")) {
                    final Row vpnIKEv2Row = dataObject.getFirstRow("VpnIKEv2");
                    final Integer authType = (Integer)vpnIKEv2Row.get("AUTHENTICATION_METHOD");
                    final Integer eapEnable = (Integer)vpnIKEv2Row.get("EAP_ENABLING");
                    if (authType == 1 || (authType == 0 && eapEnable == 1)) {
                        vpnPayload.setAuthenticationMethod("Certificate");
                        if (dataObject.containsTable("VpnPolicyToCertificate")) {
                            final Row certificatePayloadRow = dataObject.getFirstRow("VpnPolicyToCertificate");
                            sCertificateID = String.valueOf(certificatePayloadRow.get("CLIENT_CERT_ID"));
                            if (sCertificateID != null && Long.valueOf(sCertificateID.trim()) != -1L) {
                                certificateAuthType = true;
                            }
                        }
                    }
                }
                else if (connectionType == 7) {
                    final Row customSSLRow = dataObject.getFirstRow("VpnCustomSSL");
                    final Integer authType = (Integer)customSSLRow.get("USER_AUTHENTICATION");
                    if (authType == 1) {
                        sCertificateID = String.valueOf(customSSLRow.get("CERTIFICATE_ID"));
                        if (sCertificateID != null && Long.valueOf(sCertificateID.trim()) != -1L) {
                            certificateAuthType = true;
                        }
                    }
                }
            }
            if (certificateAuthType) {
                final String certUUID = this.getCertificatePayloadUUID(Long.valueOf(sCertificateID));
                if (certUUID != null) {
                    vpnPayload.setPayloadCertificateUUID(certUUID);
                }
            }
            settingsPayload[i] = vpnPayload;
        }
        return settingsPayload;
    }
    
    private String getCertificatePayloadUUID(final Long certificateID) {
        String payloadUUID = null;
        final Criteria identitycertCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certificateID, 0);
        final DataObject certificatesDO = ProfileCertificateUtil.getCertificateDO(identitycertCriteria);
        try {
            if (certificatesDO != null && !certificatesDO.isEmpty()) {
                final Row identityRow = certificatesDO.getFirstRow("Certificates");
                final int type = (int)identityRow.get("CERTIFICATE_TYPE");
                if (type == 0) {
                    final IOSPayload certPayload = this.getcertificatePayload(certificatesDO, certificateID);
                    if (certPayload != null) {
                        payloadUUID = certPayload.getPayloadDict().objectForKey("PayloadUUID").toString();
                    }
                }
                else if (type == 2) {
                    final IOSPayload certPayload = this.getPublicCertificate(certificatesDO, certificateID);
                    if (certPayload != null) {
                        payloadUUID = certPayload.getPayloadDict().objectForKey("PayloadUUID").toString();
                    }
                }
                else if (type == 3) {
                    final IOSPayload certPayload = this.getPublicCertificate(certificatesDO, certificateID);
                    if (certPayload != null) {
                        payloadUUID = certPayload.getPayloadDict().objectForKey("PayloadUUID").toString();
                    }
                }
                else if (type == 1) {
                    final IOSPayload scepPayload = this.getSCEPConfigPayload(certificatesDO, certificateID);
                    if (scepPayload != null) {
                        payloadUUID = scepPayload.getPayloadDict().objectForKey("PayloadUUID").toString();
                    }
                }
                else if (type == 5) {
                    final IOSPayload adCertPayload = this.getADCertConfigPayload(certificatesDO, certificateID);
                    if (adCertPayload != null) {
                        payloadUUID = adCertPayload.getPayloadDict().objectForKey("PayloadUUID").toString();
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getcertificate payload UUID", ex);
        }
        return payloadUUID;
    }
    
    private IOSPayload getcertificatePayload(final DataObject certDO, final Long certificateID) {
        if (!this.certificateMapper.containsKey(certificateID)) {
            try {
                final IOSPayload payload = this.createCertificatePayload(certDO);
                if (payload != null) {
                    this.certificateMapper.put(certificateID, payload);
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception in getcertificate payload", ex);
            }
        }
        return this.certificateMapper.get(certificateID);
    }
    
    private IOSPayload getPublicCertificate(final DataObject certDO, final Long certificateID) {
        if (!this.certificateMapper.containsKey(certificateID)) {
            try {
                final IOSPayload payload = this.createPublicCertificateForFileVault(certDO);
                if (payload != null) {
                    this.certificateMapper.put(certificateID, payload);
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception in get public certificate", ex);
                ex.printStackTrace();
            }
        }
        return this.certificateMapper.get(certificateID);
    }
    
    private IOSPayload getSCEPConfigPayload(final DataObject scepDO, final Long scepID) {
        if (!this.scepConfigMapper.containsKey(scepID)) {
            try {
                final IOSPayload payload = this.createSCEPPayload(scepDO);
                if (payload != null) {
                    this.scepConfigMapper.put(scepID, payload);
                    final ScepServer scepServer = DynamicScepServer.getScepServerForScepId(scepID);
                    if (scepServer != null) {
                        final CredentialCertificate certificate = scepServer.getCertificate();
                        if (certificate != null) {
                            final long certificateId = certificate.getCertificateId();
                            this.getCertificatePayloadUUID(certificateId);
                        }
                    }
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception in getscep payload", ex);
            }
        }
        return this.scepConfigMapper.get(scepID);
    }
    
    private IOSPayload getADCertConfigPayload(final DataObject adCertDO, final Long adCertConfigID) {
        if (!this.adCertConfigMapper.containsKey(adCertConfigID)) {
            try {
                final IOSPayload payload = this.createADCertPayload(adCertDO);
                if (payload != null) {
                    this.adCertConfigMapper.put(adCertConfigID, payload);
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception in getADCertConfigPayload:- ", ex);
            }
        }
        return this.adCertConfigMapper.get(adCertConfigID);
    }
    
    private IOSPayload createSCEPPayload(final DataObject scepDO) {
        final DO2iOSSCEPPayload scep = new DO2iOSSCEPPayload();
        final IOSPayload[] scepPayloadArray = scep.createPayload(scepDO);
        return scepPayloadArray[0];
    }
    
    private IOSPayload createADCertPayload(final DataObject adCertDO) {
        final DO2ADCertPayload adCertPayload = new DO2ADCertPayload();
        final IOSPayload[] adCertPayloadArray = adCertPayload.createPayload(adCertDO);
        return adCertPayloadArray[0];
    }
    
    private IOSPayload createCertificatePayload(final DataObject certDO) {
        final DO2CertificatePolicyPayload certificate = new DO2CertificatePolicyPayload();
        final IOSPayload[] certificateArray = certificate.createPayload(certDO);
        return certificateArray[0];
    }
    
    private IOSPayload createPublicCertificateForFileVault(final DataObject certDO) {
        final CertificatePayload payload = new CertificatePayload(1, "MDM", "com.mdm.mobiledevice.certificate", "iOS Certificate");
        try {
            final Row credentialRow = certDO.getFirstRow("CredentialCertificateInfo");
            final Long certificateID = (Long)credentialRow.get("CERTIFICATE_ID");
            final String certificateContent = PayloadSecretFieldsHandler.getInstance().constructFilevaultPayloadCertificate(certificateID.toString());
            payload.setCertificatePayloadContent(certificateContent);
            payload.setPayloadDisplayName("Escrow Certificate");
            payload.setPayloadType("com.apple.security.pkcs1");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while parsing certificate", ex);
        }
        return payload;
    }
    
    public static IOSPayload[] mergePayload(final IOSPayload[] existingPayload, final IOSPayload[] newPayload) {
        int length = 0;
        if (existingPayload != null) {
            length += existingPayload.length;
        }
        if (newPayload != null) {
            length += newPayload.length;
        }
        final IOSPayload[] iosPayloadArray = new IOSPayload[length];
        int i = 0;
        if (existingPayload != null) {
            for (final IOSPayload iosPayload : existingPayload) {
                iosPayloadArray[i++] = iosPayload;
            }
        }
        if (newPayload != null) {
            for (final IOSPayload iosPayload : newPayload) {
                iosPayloadArray[i++] = iosPayload;
            }
        }
        return iosPayloadArray;
    }
    
    public ConfigurationPayload createCustomKioskPayload(final Long collectionId, final String payloadIdentifier, final Long customerId) {
        final NSArray payloadArray = new NSArray(1);
        final DO2RestrictionsPolicyPayload restrictionPayload = new DO2RestrictionsPolicyPayload();
        final IOSPayload payload = restrictionPayload.kioskRestrictionPayload(collectionId, customerId);
        payloadArray.setValue(0, (Object)payload.getPayloadDict());
        final ConfigurationPayload cfgPayload = new ConfigurationPayload(1, "MDM", payloadIdentifier, "KioskInstallProfile");
        cfgPayload.setPayloadContent(payloadArray);
        return cfgPayload;
    }
    
    public ConfigurationPayload createDefaultKioskPayload() {
        final NSArray payloadArray = new NSArray(1);
        final DO2AppLockPayload appLockPayload = new DO2AppLockPayload();
        final IOSPayload payload = appLockPayload.createDefaultMDMKiosk();
        payloadArray.setValue(0, (Object)payload.getPayloadDict());
        final ConfigurationPayload cfgPayload = new ConfigurationPayload(1, "MDM", "com.mdm.kiosk_default_mdm_app", "DefaultMDMKioskProfile");
        cfgPayload.setPayloadContent(payloadArray);
        return cfgPayload;
    }
    
    public ConfigurationPayload createRestrictOSUpdatePayload(final DataObject dataObject) {
        final NSArray payloadArray = new NSArray(1);
        final IOSPayload osRestrictionPayload = new DO2RestrictionsPolicyPayload().createOSUpdateRestrictionPayload(dataObject);
        payloadArray.setValue(0, (Object)osRestrictionPayload.getPayloadDict());
        final ConfigurationPayload cfgPayload = new ConfigurationPayload(1, "MDM", "com.mdm.osupdate.restriction", "RestrictOSUpdates");
        cfgPayload.setPayloadContent(payloadArray);
        return cfgPayload;
    }
    
    public ConfigurationPayload createSingletonRestrictPayload(final JSONObject restrictionObject) {
        final NSArray payloadArray = new NSArray(1);
        final IOSPayload deviceNameRest = new DO2RestrictionsPolicyPayload().createRestrictionFromJSON(restrictionObject, "com.mdm.mobiledevice.singlerestriction", "Singleton Restriction");
        payloadArray.setValue(0, (Object)deviceNameRest.getPayloadDict());
        final int payloadVersion = 1;
        final String payloadOrganization = "MDM";
        new IOSSingletonRestrictionHandler();
        final ConfigurationPayload cfgPayload = new ConfigurationPayload(payloadVersion, payloadOrganization, "com.mdm.singleton.restriction", "SingletonRestriction");
        cfgPayload.setPayloadContent(payloadArray);
        return cfgPayload;
    }
    
    public ConfigurationPayload createRemovePayload(final DataObject dataObject, final List configDOList) {
        ConfigurationPayload configurationPayload = null;
        final List payloadList = new ArrayList();
        try {
            for (int configSize = configDOList.size(), k = 0; k < configSize; ++k) {
                final DataObject configDO = configDOList.get(k);
                final Integer configId = (Integer)configDO.getFirstValue("ConfigData", "CONFIG_ID");
                final IOSPayload[] settingsPayload = this.createRemovePayload(configId, configDO);
                if (settingsPayload != null) {
                    for (final IOSPayload payload : settingsPayload) {
                        if (payload != null) {
                            payloadList.add(payload);
                        }
                    }
                }
            }
            configurationPayload = this.createRemoveConfigurationPayload(dataObject, payloadList);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in create remove payload", (Throwable)e);
        }
        return configurationPayload;
    }
    
    private IOSPayload[] createRemovePayload(final Integer configID, final DataObject dataObject) {
        IOSPayload[] settingsPayload = null;
        switch (configID) {
            case 770: {
                final DO2RemoveMacFileVaultPolicy fileVaultPolicy = new DO2RemoveMacFileVaultPolicy();
                settingsPayload = fileVaultPolicy.createPayload(dataObject);
                break;
            }
        }
        return settingsPayload;
    }
    
    public ConfigurationPayload createPasscodeRestrictionPayload(final String payloadIdentifier, final String requestType, final boolean restriction) {
        final NSArray payloadArray = new NSArray(1);
        final IOSPayload passcodePayload = new DO2RestrictionsPolicyPayload().createPasscodeRestrictionPayload(restriction);
        payloadArray.setValue(0, (Object)passcodePayload.getPayloadDict());
        final ConfigurationPayload cfgPayload = new ConfigurationPayload(1, "MDM", payloadIdentifier, requestType);
        cfgPayload.setPayloadContent(payloadArray);
        return cfgPayload;
    }
}
