package com.me.mdm.server.windows.profile.payload.transform;

import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.me.mdm.server.windows.profile.payload.WindowsConfigurationPayload;
import java.util.List;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2WindowsPayloadHandler
{
    private final Logger logger;
    
    public DO2WindowsPayloadHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public WindowsConfigurationPayload createPayload(final DataObject dataObject, final List configDOList) {
        WindowsConfigurationPayload profilePayload = null;
        try {
            final int configSize = configDOList.size();
            final Long collectionID = (Long)dataObject.getFirstValue("Collection", "COLLECTION_ID");
            final String collectionName = ProfileHandler.getProfileIdentifierFromCollectionID(collectionID);
            profilePayload = new WindowsConfigurationPayload();
            for (int k = 0; k < configSize; ++k) {
                final DataObject configDO = configDOList.get(k);
                final Integer configId = (Integer)configDO.getFirstValue("ConfigData", "CONFIG_ID");
                final JSONObject identifierJson = new JSONObject();
                identifierJson.put("collection_name", (Object)collectionName);
                if (configDO.getRows("MdConfigDataItemExtn").hasNext()) {
                    final String payloadIdentifier = (String)configDO.getFirstValue("MdConfigDataItemExtn", "CONFIG_PAYLOAD_IDENTIFIER");
                    identifierJson.put("payload_identifier", (Object)payloadIdentifier);
                }
                final WindowsPayload payload = this.createPayload(configId, configDO, identifierJson);
                if (payload != null) {
                    if (payload.getNonAtomicDeletePayloadCommand() != null && payload.getNonAtomicDeletePayloadCommand().getRequestItems() != null && !payload.getNonAtomicDeletePayloadCommand().getRequestItems().isEmpty()) {
                        profilePayload.setNonAtomicPayloadContent(payload.getNonAtomicDeletePayloadCommand());
                    }
                    if (payload.getAddPayloadCommand() != null && payload.getAddPayloadCommand().getRequestItems() != null && !payload.getAddPayloadCommand().getRequestItems().isEmpty()) {
                        profilePayload.setAtomicPayloadContent(payload.getAddPayloadCommand());
                    }
                    if (payload.getReplacePayloadCommand() != null && payload.getReplacePayloadCommand().getRequestItems() != null && !payload.getReplacePayloadCommand().getRequestItems().isEmpty()) {
                        profilePayload.setAtomicPayloadContent(payload.getReplacePayloadCommand());
                    }
                    if (payload.getExecPayloadCommand() != null && payload.getExecPayloadCommand().getRequestItems() != null && !payload.getExecPayloadCommand().getRequestItems().isEmpty()) {
                        profilePayload.setAtomicPayloadContent(payload.getExecPayloadCommand());
                    }
                    if (payload.getDeletePayloadCommand() != null && payload.getDeletePayloadCommand().getRequestItems() != null && !payload.getDeletePayloadCommand().getRequestItems().isEmpty()) {
                        profilePayload.setAtomicPayloadContent(payload.getDeletePayloadCommand());
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while creating windows payload : ", ex);
        }
        return profilePayload;
    }
    
    private WindowsPayload createPayload(final Integer configID, final DataObject dataObject, final JSONObject identifierJson) {
        WindowsPayload profilePayload = null;
        DO2WindowsPayload payload = null;
        String identifier = null;
        try {
            final String collectionName = String.valueOf(identifierJson.get("collection_name"));
            switch (configID) {
                case 601: {
                    payload = new DO2WindowsPasscodePayload();
                    identifier = collectionName + ".passwordpolicy";
                    break;
                }
                case 602: {
                    payload = new DO2WindowsEmailPayload();
                    identifier = collectionName + ".emailpolicy";
                    break;
                }
                case 603: {
                    payload = new DO2WindowsActiveSyncPayload();
                    identifier = collectionName + ".exchangepolicy";
                    break;
                }
                case 604: {
                    payload = new DO2WindowsRestrictionsPayload();
                    identifier = collectionName + ".restrictionspolicy";
                    break;
                }
                case 605: {
                    payload = new DO2WindowsWiFiPayload();
                    identifier = collectionName + ".wifipolicy";
                    break;
                }
                case 606: {
                    payload = new DO2WindowsSCEPPayload();
                    identifier = collectionName + ".sceppolicy";
                    break;
                }
                case 607: {
                    payload = new DO2WindowsCertificatePayload();
                    identifier = collectionName + ".certificatepolicy";
                    break;
                }
                case 608: {
                    payload = new DO2WindowsLockDownPayload();
                    identifier = collectionName + ".lockdownpolicy";
                    break;
                }
                case 609: {
                    payload = new DO2WindowsVPNPayload();
                    identifier = collectionName + ".vpnpolicy";
                    break;
                }
                case 610: {
                    payload = new DO2WindowsEDPPolicy();
                    identifier = collectionName + ".edppolicy";
                    break;
                }
                case 611: {
                    payload = new DO2WindowsLockDownModePayload();
                    identifier = collectionName + ".lockdownmodepolicy";
                    break;
                }
                case 612: {
                    payload = new DO2WindowsCustomPayload();
                    identifier = collectionName + ".custompolicy";
                    break;
                }
                case 613: {
                    payload = new DO2WindowsBitlockerPayload();
                    identifier = collectionName + ".bitlockerpolicy";
                    break;
                }
            }
            profilePayload = payload.createPayload(dataObject);
            if (identifierJson.has("payload_identifier")) {
                identifier = String.valueOf(identifierJson.get("payload_identifier"));
            }
            profilePayload.setCommandUUID(identifier);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while creating windows payload : ", exp);
        }
        return profilePayload;
    }
    
    public WindowsConfigurationPayload createRemoveProfilePayload(final DataObject dataObject, final List configDOList) {
        WindowsConfigurationPayload profilePayload = null;
        SyncMLRequestCommand policyPayload = null;
        try {
            final int configSize = configDOList.size();
            final String collectionName = (String)dataObject.getFirstValue("Collection", "COLLECTION_NAME");
            profilePayload = new WindowsConfigurationPayload();
            for (int k = 0; k < configSize; ++k) {
                final DataObject configDO = configDOList.get(k);
                final Integer configId = (Integer)configDO.getFirstValue("ConfigData", "CONFIG_ID");
                final JSONObject identifierJson = new JSONObject();
                identifierJson.put("collection_name", (Object)collectionName);
                if (configDO.getRows("MdConfigDataItemExtn").hasNext()) {
                    final String payloadIdentifier = (String)configDO.getFirstValue("MdConfigDataItemExtn", "CONFIG_PAYLOAD_IDENTIFIER");
                    identifierJson.put("payload_identifier", (Object)payloadIdentifier);
                }
                final WindowsPayload payload = this.createRemoveProfilePayload(configId, configDO, identifierJson);
                if (payload != null) {
                    if (configId == 604 || configId == 610) {
                        policyPayload = payload.getReplacePayloadCommand();
                        profilePayload.setPayloadContent(policyPayload);
                    }
                    if (payload.getNonAtomicDeletePayloadCommand() != null && payload.getNonAtomicDeletePayloadCommand().getRequestItems() != null && !payload.getNonAtomicDeletePayloadCommand().getRequestItems().isEmpty()) {
                        profilePayload.setNonAtomicPayloadContent(payload.getNonAtomicDeletePayloadCommand());
                    }
                    if (payload.getDeletePayloadCommand() != null) {
                        policyPayload = payload.getDeletePayloadCommand();
                        profilePayload.setPayloadContent(policyPayload);
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while creating windows remove payload : ", ex);
        }
        return profilePayload;
    }
    
    private WindowsPayload createRemoveProfilePayload(final Integer configID, final DataObject dataObject, final JSONObject identifierJson) {
        WindowsPayload profilePayload = null;
        DO2WindowsPayload payload = null;
        String identifier = null;
        try {
            final String collectionName = String.valueOf(identifierJson.get("collection_name"));
            switch (configID) {
                case 601: {
                    payload = new DO2WindowsPasscodePayload();
                    identifier = collectionName + ".passwordpolicy";
                    break;
                }
                case 602: {
                    payload = new DO2WindowsEmailPayload();
                    identifier = collectionName + ".emailpolicy";
                    break;
                }
                case 603: {
                    payload = new DO2WindowsActiveSyncPayload();
                    identifier = collectionName + ".exchangepolicy";
                    break;
                }
                case 604: {
                    payload = new DO2WindowsRestrictionsPayload();
                    identifier = collectionName + ".restrictionspolicy";
                    break;
                }
                case 605: {
                    payload = new DO2WindowsWiFiPayload();
                    identifier = collectionName + ".wifipolicy";
                    break;
                }
                case 606: {
                    payload = new DO2WindowsSCEPPayload();
                    identifier = collectionName + ".sceppolicy";
                    break;
                }
                case 607: {
                    payload = new DO2WindowsCertificatePayload();
                    identifier = collectionName + ".certificatepolicy";
                    break;
                }
                case 608: {
                    payload = new DO2WindowsLockDownPayload();
                    identifier = collectionName + ".lockdownpolicy";
                    break;
                }
                case 609: {
                    payload = new DO2WindowsVPNPayload();
                    identifier = collectionName + ".vpnpolicy";
                    break;
                }
                case 610: {
                    payload = new DO2WindowsEDPPolicy();
                    identifier = collectionName + ".edppolicy";
                    break;
                }
                case 611: {
                    payload = new DO2WindowsLockDownModePayload();
                    identifier = collectionName + ".lockdownmodepolicy";
                    break;
                }
                case 612: {
                    payload = new DO2WindowsCustomPayload();
                    identifier = collectionName + ".custompolicy";
                    break;
                }
                case 613: {
                    payload = new DO2WindowsBitlockerPayload();
                    identifier = collectionName + ".bitlockerpolicy";
                    break;
                }
            }
            profilePayload = payload.createRemoveProfilePayload(dataObject);
            if (identifierJson.has("payload_identifier")) {
                identifier = String.valueOf(identifierJson.get("payload_identifier"));
            }
            profilePayload.setCommandUUID(identifier);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while creating windows remove payload : ", exp);
        }
        return profilePayload;
    }
    
    public WindowsConfigurationPayload[] createAppPayloads(final DataObject dataObject, final List configDOList) {
        final WindowsConfigurationPayload[] cfgPayloads = new WindowsConfigurationPayload[4];
        WindowsConfigurationPayload cfgInstallPayload = null;
        WindowsConfigurationPayload cfgRemovalPayload = null;
        WindowsConfigurationPayload cfgUpdatePayload = null;
        WindowsConfigurationPayload cfgConfigPayload = null;
        try {
            final int configSize = configDOList.size();
            cfgInstallPayload = new WindowsConfigurationPayload();
            cfgRemovalPayload = new WindowsConfigurationPayload();
            cfgUpdatePayload = new WindowsConfigurationPayload();
            cfgConfigPayload = new WindowsConfigurationPayload();
            for (int k = 0; k < configSize; ++k) {
                final DataObject configDO = configDOList.get(k);
                final DO2WindowsAppPayload appPayload = new DO2WindowsAppPayload();
                final WindowsPayload[] payloads = appPayload.createAppPayload(configDO);
                final WindowsPayload configPayload = appPayload.createAppConfigPayload(configDO);
                for (int i = 0; i < payloads.length; ++i) {
                    final WindowsPayload payload = payloads[i];
                    if (payload.getPayloadType().equalsIgnoreCase("InstallConfigPayload")) {
                        cfgInstallPayload.setPayloadContent(payload.getAddPayloadCommand());
                        cfgInstallPayload.setPayloadContent(payload.getExecPayloadCommand());
                        cfgInstallPayload.setNonAtomicPayloadContent(payload.getNonAtomicDeletePayloadCommand());
                    }
                    else if (payload.getPayloadType().equalsIgnoreCase("RemoveConfigPayload")) {
                        cfgRemovalPayload.setPayloadContent(payload.getDeletePayloadCommand());
                        cfgRemovalPayload.setPayloadContent(payload.getExecPayloadCommand());
                    }
                    else if (payload.getPayloadType().equalsIgnoreCase("UpdateConfigPayload")) {
                        cfgUpdatePayload.setPayloadContent(payload.getReplacePayloadCommand());
                        cfgUpdatePayload.setPayloadContent(payload.getAddPayloadCommand());
                        cfgUpdatePayload.setPayloadContent(payload.getExecPayloadCommand());
                        cfgUpdatePayload.setNonAtomicPayloadContent(payload.getNonAtomicDeletePayloadCommand());
                    }
                }
                if (configPayload != null) {
                    cfgConfigPayload.setNonAtomicPayloadContent(configPayload.getNonAtomicDeletePayloadCommand());
                    cfgConfigPayload.setPayloadContent(configPayload.getReplacePayloadCommand());
                }
            }
            if (!cfgInstallPayload.getAtomicPayloadContent().getRequestCmds().isEmpty()) {
                cfgInstallPayload.setConfigurationPayloadType("InstallApplication");
                cfgPayloads[0] = cfgInstallPayload;
            }
            if (!cfgRemovalPayload.getAtomicPayloadContent().getRequestCmds().isEmpty()) {
                cfgRemovalPayload.setConfigurationPayloadType("RemoveApplication");
                cfgPayloads[1] = cfgRemovalPayload;
            }
            if (!cfgUpdatePayload.getAtomicPayloadContent().getRequestCmds().isEmpty()) {
                cfgUpdatePayload.setConfigurationPayloadType("UpdateApplication");
                cfgPayloads[2] = cfgUpdatePayload;
            }
            cfgConfigPayload.setConfigurationPayloadType("ApplicationConfiguration");
            cfgPayloads[3] = cfgConfigPayload;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while creating windows app payload : ", ex);
        }
        return cfgPayloads;
    }
}
