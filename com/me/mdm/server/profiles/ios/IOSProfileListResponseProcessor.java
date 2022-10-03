package com.me.mdm.server.profiles.ios;

import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.dd.plist.NSObject;
import java.util.List;
import java.util.Collections;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.sym.server.mdm.ios.payload.transform.PayloadIdentifierConstants;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.me.mdm.agent.util.ResponseTester;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSProfileListResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        try {
            final Long resourceId = (Long)params.get("resourceId");
            final Long customerId = params.optLong("customerId");
            this.processResponse(resourceId, (String)params.get("strData"), customerId);
            this.removeManagedProfiles(customerId, resourceId);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while IOSProfileListResponseProcessor processQueuedCommand ", e);
        }
        return new JSONObject();
    }
    
    private void processResponse(final Long resourceID, String responseStr, final long customerId) {
        try {
            if (ResponseTester.isTestMode("ProfileListResponseTestMode")) {
                responseStr = ResponseTester.getTestResponseString("profilelisttestresponse.xml");
            }
            final IOSConfigPayloadDataHandler configHandler = new IOSConfigPayloadDataHandler();
            final DeviceConfigPayloadsDataHandler deviceConfigHandler = new DeviceConfigPayloadsDataHandler();
            final String status = PlistWrapper.getInstance().getValueForKeyString("Status", responseStr);
            if (status != null && status.equalsIgnoreCase("Acknowledged")) {
                final NSArray array = PlistWrapper.getInstance().getArrayForKey("ProfileList", responseStr);
                if (array == null || array.count() < 1) {
                    new DeviceConfigPayloadsDataHandler().clearInstalledProfiles(resourceID);
                }
                else {
                    final JSONArray deviceProfiles = new JSONArray();
                    for (int i = 0; i < array.count(); ++i) {
                        final NSDictionary configDict = (NSDictionary)array.objectAtIndex(i);
                        final JSONObject configJson = this.parseConfigProfile(configDict);
                        final Long profileID = configHandler.addOrUpdateProfilePayloads(configJson);
                        final JSONObject deviceInstalledJson = this.getDeviceInstalledConfigJson(profileID, resourceID, configDict);
                        deviceProfiles.put((Object)deviceInstalledJson);
                        this.checkAndAddUpgradeMobileConfig(resourceID, configDict, customerId);
                    }
                    deviceConfigHandler.clearAndUpdateInstalledProfiles(resourceID, deviceProfiles);
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while IOSProfileListResponseProcessor ", e);
        }
    }
    
    private JSONObject parseConfigProfile(final NSDictionary configDict) {
        final JSONObject json = new JSONObject();
        try {
            final String uuid = (configDict.get((Object)"PayloadUUID") == null) ? null : configDict.get((Object)"PayloadUUID").toString();
            if (uuid != null) {
                final String name = (configDict.get((Object)"PayloadDisplayName") == null) ? "iOS Configuration Payload" : configDict.get((Object)"PayloadDisplayName").toString();
                final String desc = (configDict.get((Object)"PayloadDescription") == null) ? "No description available" : configDict.get((Object)"PayloadDescription").toString();
                final String identifier = (configDict.get((Object)"PayloadIdentifier") == null) ? "Unknown" : configDict.get((Object)"PayloadIdentifier").toString();
                final String org = (configDict.get((Object)"PayloadOrganization") == null) ? "Unknown" : configDict.get((Object)"PayloadOrganization").toString();
                final String type = "Configuration";
                final Integer version = (configDict.get((Object)"PayloadVersion") == null) ? IOSConfigPayloadConstants.DEFAULT_PAYLOAD_VERSION : Integer.parseInt(configDict.get((Object)"PayloadVersion").toString());
                final Boolean encrypted = (configDict.get((Object)"IsEncrypted") == null) ? Boolean.FALSE : Boolean.parseBoolean(configDict.get((Object)"IsEncrypted").toString());
                final Boolean hasPasscode = (configDict.get((Object)"HasRemovalPasscode") == null) ? Boolean.FALSE : Boolean.parseBoolean(configDict.get((Object)"HasRemovalPasscode").toString());
                final Boolean unremovable = (configDict.get((Object)"PayloadRemovalDisallowed") == null) ? Boolean.FALSE : Boolean.parseBoolean(configDict.get((Object)"PayloadRemovalDisallowed").toString());
                json.put("PAYLOAD_DISPLAY_NAME", (Object)name);
                json.put("PAYLOAD_DESCRIPTION", (Object)desc);
                json.put("PAYLOAD_IDENTIFIER", (Object)identifier);
                json.put("PAYLOAD_ORGANIZATION", (Object)org);
                json.put("PAYLOAD_TYPE", (Object)type);
                json.put("PAYLOAD_UUID", (Object)uuid);
                json.put("PAYLOAD_VERSION", (Object)version);
                json.put("PAYLOAD_IS_ENCRYPTED", (Object)encrypted);
                json.put("PAYLOAD_UNREMOVABLE", (Object)unremovable);
                json.put("PAYLOAD_HAS_REM_PASSWORD", (Object)hasPasscode);
                final NSArray payloadsArray = (NSArray)configDict.get((Object)"PayloadContent");
                if (payloadsArray != null) {
                    final JSONArray payloadsJArray = new JSONArray();
                    for (int i = 0; i < payloadsArray.count(); ++i) {
                        final NSDictionary payloadDict = (NSDictionary)payloadsArray.objectAtIndex(i);
                        final JSONObject payloadJson = this.parseInnerConfigPayload(payloadDict);
                        payloadsJArray.put((Object)payloadJson);
                    }
                    json.put("PayloadContent", (Object)payloadsJArray);
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while parseConfigProfile ", e);
        }
        return json;
    }
    
    private JSONObject parseInnerConfigPayload(final NSDictionary configDict) {
        final JSONObject json = new JSONObject();
        try {
            String uuid = (configDict.get((Object)"PayloadUUID") == null) ? null : configDict.get((Object)"PayloadUUID").toString();
            if (uuid == null) {
                uuid = "unknown-uuid";
            }
            final String name = (configDict.get((Object)"PayloadDisplayName") == null) ? "iOS Configuration Payload" : configDict.get((Object)"PayloadDisplayName").toString();
            final String desc = (configDict.get((Object)"PayloadDescription") == null) ? "No description available" : configDict.get((Object)"PayloadDescription").toString();
            final String identifier = (configDict.get((Object)"PayloadIdentifier") == null) ? "Unknown" : configDict.get((Object)"PayloadIdentifier").toString();
            final String org = (configDict.get((Object)"PayloadOrganization") == null) ? "Unknown" : configDict.get((Object)"PayloadOrganization").toString();
            final String type = (configDict.get((Object)"PayloadType") == null) ? "Unknown" : configDict.get((Object)"PayloadType").toString();
            final Integer version = (configDict.get((Object)"PayloadVersion") == null) ? IOSConfigPayloadConstants.DEFAULT_PAYLOAD_VERSION : Integer.parseInt(configDict.get((Object)"PayloadVersion").toString());
            json.put("PAYLOAD_DISPLAY_NAME", (Object)name);
            json.put("PAYLOAD_DESCRIPTION", (Object)desc);
            json.put("PAYLOAD_IDENTIFIER", (Object)identifier);
            json.put("PAYLOAD_ORGANIZATION", (Object)org);
            json.put("PAYLOAD_TYPE", (Object)type);
            json.put("PAYLOAD_UUID", (Object)uuid);
            json.put("PAYLOAD_VERSION", (Object)version);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while parseInnerConfigPayload ", e);
        }
        return json;
    }
    
    private JSONObject getDeviceInstalledConfigJson(final Long profileID, final Long resourceID, final NSDictionary configDict) {
        final JSONObject json = new JSONObject();
        try {
            final Boolean ismanaged = (configDict.get((Object)"IsManaged") == null) ? Boolean.FALSE : Boolean.parseBoolean(configDict.get((Object)"IsManaged").toString());
            final String identifier = (configDict.get((Object)"PayloadIdentifier") == null) ? "Unknown" : configDict.get((Object)"PayloadIdentifier").toString();
            int source = IOSConfigPayloadConstants.INSTALLED_SOURCE_UNKNOWN;
            if (ismanaged || identifier.contains(PayloadIdentifierConstants.MDM_INSTALATION_PROFILE_IDENTIFIER)) {
                source = IOSConfigPayloadConstants.INSTALLED_SOURCE_MDM;
            }
            json.put("RESOURCE_ID", (Object)resourceID);
            json.put("PROFILE_PAYLOAD_ID", (Object)profileID);
            json.put("INSTALLED_SOURCE", source);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while getDeviceInstalledConfigJson ", e);
        }
        return json;
    }
    
    private void removeManagedProfiles(final Long customerId, final Long resourceId) {
        try {
            final String paramValue = CustomerParamsHandler.getInstance().getParameterValue("DELETE_NOTMANAGED_PROFILELIST", (long)customerId);
            if (!MDMStringUtils.isEmpty(paramValue) && Boolean.valueOf(paramValue)) {
                final JSONObject paramObject = new JSONObject();
                paramObject.put("customerId", (Object)customerId);
                paramObject.put("resourceId", (Object)resourceId);
                new IOSRemoveMangedProfileHandler().checkAndRemoveProfile(paramObject);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Error while parseInnerConfigPayload ", ex);
        }
    }
    
    private void checkAndAddUpgradeMobileConfig(final Long resourceID, final NSDictionary configDict, final long customerId) {
        try {
            final String identifier = (configDict.get((Object)"PayloadIdentifier") == null) ? "Unknown" : configDict.get((Object)"PayloadIdentifier").toString();
            if (identifier.contains(PayloadIdentifierConstants.MDM_INSTALATION_PROFILE_IDENTIFIER)) {
                final NSObject signerCertificates = configDict.get((Object)"SignerCertificates");
                if (signerCertificates == null || ((NSArray)signerCertificates).count() == 0) {
                    final String iOSVersion = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
                    if (iOSVersion != null) {
                        final String commandName = this.getUpgradeMobileConfigCommandName(iOSVersion);
                        if (this.isNeedToResendUpgradeMobileConfig(resourceID, customerId, commandName)) {
                            DeviceCommandRepository.getInstance().addUpgradeMobileConfigCommand(commandName, Collections.singletonList(resourceID));
                            this.updateTimeInCommandHistory(resourceID, commandName);
                            Logger.getLogger("MDMLogger").log(Level.INFO, "Upgrade Mobile Config command successfully added for resource: {0} | commandName: {1} | OsVersion: {2}", new Object[] { resourceID, commandName, iOSVersion });
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, e, () -> "Error while checking if Upgrade Mobile config is needed for resource: " + n);
        }
    }
    
    private void updateTimeInCommandHistory(final Long resourceID, final String commandName) throws DataAccessException {
        final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandName);
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("UPDATED_TIME", System.currentTimeMillis());
        final Criteria resourceIdCriteria = new Criteria(new Column("CommandHistory", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria commandIdCriteria = new Criteria(new Column("CommandHistory", "COMMAND_ID"), (Object)commandId, 0);
        new CommandStatusHandler().updateCommandStatus(jsonObject, resourceIdCriteria.and(commandIdCriteria));
    }
    
    private boolean isNeedToResendUpgradeMobileConfig(final Long resourceID, final long customerId, final String commandName) throws Exception {
        final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandName);
        if (commandId != null) {
            final JSONObject recentCommandInfo = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
            final long mobileConfigUpgradedTime = recentCommandInfo.optLong("UPDATED_TIME", -1L);
            if (mobileConfigUpgradedTime > 0L) {
                final String ppmAppliedTime = CustomerParamsHandler.getInstance().getParameterValue("IOSMDM_PROFILE_SIGINING", customerId);
                if (ppmAppliedTime != null && mobileConfigUpgradedTime < Long.parseLong(ppmAppliedTime)) {
                    Logger.getLogger("MDMLogger").log(Level.INFO, "Signer Cert is unavailable, thus need to add Upgrade Mobile config for resource: {0} | commandName: {1}", new Object[] { resourceID, commandName });
                    return true;
                }
            }
        }
        return false;
    }
    
    private String getUpgradeMobileConfigCommandName(final String iOSVersion) {
        final boolean isGreaterOrEqual5 = new VersionChecker().isGreaterOrEqual(iOSVersion, "5");
        return isGreaterOrEqual5 ? "InstallProfile;Collection=UpgradeMobileConfig5" : "InstallProfile;Collection=UpgradeMobileConfig4";
    }
}
