package com.me.mdm.server.seqcommands.ios;

import com.adventnet.sym.server.mdm.config.MDMConfigQueryUtil;
import com.adventnet.sym.server.mdm.config.MDMConfigQuery;
import java.util.HashSet;
import com.adventnet.ds.query.SortColumn;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.util.Properties;
import java.util.UUID;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.adventnet.sym.server.mdm.apps.ios.IOSModifiedEnterpriseAppsUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import com.me.mdm.server.seqcommands.SeqCmdDBUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Iterator;
import com.me.mdm.server.profiles.RestrictionProfileHandler;
import java.util.Arrays;
import java.util.TimeZone;
import com.me.mdm.server.profiles.ios.IOSSingletonRestrictionHandler;
import com.me.mdm.server.profiles.kiosk.IOSKioskProfileDataHandler;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Collection;
import org.json.JSONArray;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

public class IOSSeqCmdUtil
{
    static IOSSeqCmdUtil iosSequentialCommandUtil;
    private static final Logger LOGGER;
    private static final Logger SEQLOGGER;
    private static final String MANAGED_APP_LIST = "managed_app_list.xml";
    private static final String KIOSK_RESTRICTION_PROFILE = "kiosk_restriction_profile.xml";
    public static final String HANDLER = "com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler";
    private static final String OS_UPDATE_HANDLER = "com.me.mdm.server.updates.osupdates.ios.IOSUpdateSeqCmdResponseHandler";
    public static String appCollection;
    public static String profileCollection;
    public static String appGroupId;
    public static String appIdentifier;
    public static final String KIOSK_PAYLOAD_IDENTIFER = "com.mdm.kiosk_install_profile";
    public static final String DEFAULT_MDM_APP_PAYLOAD_IDENTIFIER = "com.mdm.kiosk_default_mdm_app";
    private static final HashMap<String, String> IOS_POLICY_COMMAND_AFTER_PROFILE;
    private static final HashMap<String, String> IOS_POLICY_COMMAND_BEFORE_PROFILE;
    private static final HashMap<String, String> IOS_REMOVE_PROFILE;
    private static final String[] IOS_PRIORITIZED_POLICY;
    private static final String[] IOS_REMOVE_PRIORITIZEDPOLICY;
    private static final List<Integer> PREVIOUS_CONFIGURED_CONFIG_LIST;
    
    public static IOSSeqCmdUtil getInstance() {
        if (IOSSeqCmdUtil.iosSequentialCommandUtil == null) {
            IOSSeqCmdUtil.iosSequentialCommandUtil = new IOSSeqCmdUtil();
        }
        return IOSSeqCmdUtil.iosSequentialCommandUtil;
    }
    
    private JSONObject prepareManagedSettingJSONForSeqCmd(final Long collectionID, final int installCommandOrder) {
        final JSONObject installSettingObject = new JSONObject();
        try {
            final List collectionIDList = new ArrayList();
            collectionIDList.add(collectionID);
            final Long installManagedSettingCommandID = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIDList, "InstallManagedSettings").get(0);
            installSettingObject.put("cmd_id", (Object)installManagedSettingCommandID);
            installSettingObject.put("order", installCommandOrder);
            installSettingObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler");
        }
        catch (final Exception ex) {
            IOSSeqCmdUtil.LOGGER.log(Level.SEVERE, null, ex);
        }
        return installSettingObject;
    }
    
    public Long addSeqCmd(final List configIDList, final Long collectionID, final Long customerId, final Long profileId, final JSONObject configParams) throws Exception {
        final List collectionIDList = new ArrayList();
        collectionIDList.add(collectionID);
        JSONObject params = new JSONObject();
        Long baseCommandId = null;
        Long installProfileCommandID = null;
        long timeOut = 10000L;
        try {
            int installCommandOrder = 1;
            final JSONArray installSubCommandArray = new JSONArray();
            final List<String> previousConfiguration = this.getPreviousConfiguredSeqRemovalCommand(collectionID, profileId);
            final List<String> configurationList = new ArrayList<String>();
            final JSONArray configArray = new JSONArray((Collection)configIDList);
            configurationList.addAll(JSONUtil.getInstance().convertStringJSONArrayTOList(configArray));
            configurationList.addAll(previousConfiguration);
            configurationList.add("RemoveProfile");
            final String payloadIdentifier = ProfileHandler.getProfileIdentifierFromProfileID(profileId);
            final JSONObject policyParams = new JSONObject();
            policyParams.put("PROFILE_ID", (Object)profileId);
            policyParams.put("PROFILE_PAYLOAD_IDENTIFIER", (Object)payloadIdentifier);
            final PolicySpecificSeqHandler handler = new PolicySpecificSeqHandler();
            final JSONObject policySpecificParams = new JSONObject();
            policySpecificParams.put("COLLECTION_ID", (Object)collectionID);
            policySpecificParams.put("CUSTOMER_ID", (Object)customerId);
            policySpecificParams.put("order", installCommandOrder);
            policySpecificParams.put("params", (Object)policyParams);
            final JSONObject policyJSON = handler.getPolicyCommand(configurationList, policySpecificParams, IOSSeqCmdUtil.IOS_POLICY_COMMAND_BEFORE_PROFILE, IOSSeqCmdUtil.IOS_PRIORITIZED_POLICY);
            final JSONArray subCommandArray = policyJSON.getJSONArray("commandArray");
            final JSONObject policySeqParams = policyJSON.getJSONObject("params");
            for (int i = 0; i < subCommandArray.length(); ++i) {
                final JSONObject subCommand = (JSONObject)subCommandArray.get(i);
                installSubCommandArray.put((Object)subCommand);
                ++installCommandOrder;
            }
            params = JSONUtil.mergeJSONObjects(params, policySeqParams);
            if (configIDList.contains(183)) {
                final List kioskAppCommandList = this.prepareKioskProfileForSeqCmd(collectionID, installCommandOrder, customerId, profileId, configParams);
                final Iterator kioskListIterator = kioskAppCommandList.iterator();
                while (kioskListIterator.hasNext()) {
                    installSubCommandArray.put(kioskListIterator.next());
                    ++installCommandOrder;
                }
                if (kioskAppCommandList.size() > 1) {
                    baseCommandId = this.createBaseCommandForKiosk(collectionID, "KioskInstallProfile");
                    final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
                    final JSONObject collectionDetails = kioskHandler.isProfileApplicableForIOSKioskAutomation(collectionID, customerId);
                    final Long appGroupId = collectionDetails.optLong("APP_GROUP_ID");
                    final Long appCollectionId = this.getValidAppCollectionIDForKiosk(appGroupId, configParams);
                    params.put(IOSSeqCmdUtil.appCollection, (Object)appCollectionId);
                    params.put(IOSSeqCmdUtil.profileCollection, (Object)collectionID);
                    timeOut = 540000L;
                }
            }
            final List installProfileList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIDList, "InstallProfile");
            if (!installProfileList.isEmpty()) {
                installProfileCommandID = installProfileList.get(0);
            }
            final JSONObject installProfileObject = this.prepareBaseProfileJSONForSeqCmd(installCommandOrder++, installProfileCommandID);
            installSubCommandArray.put((Object)installProfileObject);
            if (baseCommandId == null) {
                baseCommandId = installProfileCommandID;
            }
            if (configIDList.contains(173) || configurationList.contains("SingletonRestriction") || configurationList.contains(951)) {
                final JSONObject restrictionObject = new IOSSingletonRestrictionHandler().prepareRestrictionSeqCmd(installCommandOrder++, collectionID);
                installSubCommandArray.put((Object)restrictionObject);
                configurationList.add("RestrictionProfileStatus");
            }
            final HashMap restrictionCheckHash = new HashMap();
            restrictionCheckHash.put("BLUETOOTH_SETTING", new ArrayList<String>() {
                {
                    this.add("0");
                    this.add("1");
                }
            });
            restrictionCheckHash.put("HOTSPOT_SETTING", new ArrayList<String>() {
                {
                    this.add("0");
                    this.add("1");
                }
            });
            final List<String> timeZoneList = Arrays.asList(TimeZone.getAvailableIDs());
            restrictionCheckHash.put("TIME_ZONE", timeZoneList);
            final boolean isBluetoothNotConfigured = new RestrictionProfileHandler().isRestrictionConfigured(collectionID, restrictionCheckHash, "RestrictionsPolicy");
            if (configIDList.contains(518) || configIDList.contains(521) || ((configIDList.contains(173) || configIDList.contains(951)) && isBluetoothNotConfigured) || configIDList.contains(529)) {
                final JSONObject installSettingObject = this.prepareManagedSettingJSONForSeqCmd(collectionID, installCommandOrder++);
                installSubCommandArray.put((Object)installSettingObject);
            }
            policySpecificParams.put("order", installCommandOrder);
            final JSONObject policyAfterJSON = handler.getPolicyCommand(configurationList, policySpecificParams, IOSSeqCmdUtil.IOS_POLICY_COMMAND_AFTER_PROFILE, IOSSeqCmdUtil.IOS_PRIORITIZED_POLICY);
            final JSONArray subCommandAfterArray = policyAfterJSON.getJSONArray("commandArray");
            final JSONObject policyAfterSeqParams = policyAfterJSON.getJSONObject("params");
            for (int j = 0; j < subCommandAfterArray.length(); ++j) {
                final JSONObject subCommand2 = (JSONObject)subCommandAfterArray.get(j);
                installSubCommandArray.put((Object)subCommand2);
            }
            params = JSONUtil.mergeJSONObjects(params, policyAfterSeqParams);
            if (installSubCommandArray.length() > 1) {
                this.addSubCommandsArrayToSeqCmd(installSubCommandArray, baseCommandId, timeOut, params);
            }
            if (this.checkSeqCmdAvailableForCollection(collectionID, "RemoveProfile")) {
                final JSONObject removeParams = new JSONObject();
                removeParams.put("CUSTOMER_ID", (Object)customerId);
                removeParams.put("PROFILE_ID", (Object)profileId);
                removeParams.put("PROFILE_PAYLOAD_IDENTIFIER", (Object)payloadIdentifier);
                this.createRemoveSeqCommandForCollection(collectionID, removeParams, configurationList);
            }
        }
        catch (final Exception e) {
            IOSSeqCmdUtil.LOGGER.log(Level.SEVERE, "Exception in creating the sequential command", e);
            throw new Exception(e.getMessage());
        }
        return baseCommandId;
    }
    
    private Long getValidAppCollectionIDForKiosk(final Long appGroupId, final JSONObject configParams) {
        Long appCollectionId = MDMUtil.getInstance().getProdCollectionIdFromAppGroupId(appGroupId);
        if (appCollectionId != null && configParams != null && configParams.has("appCollectionId")) {
            final Long tempCollectionId = configParams.getLong("appCollectionId");
            if (tempCollectionId != -1L) {
                final Long tempAppGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(tempCollectionId);
                if (appGroupId.equals(tempAppGroupId)) {
                    IOSSeqCmdUtil.LOGGER.log(Level.INFO, "AppGroupID for appCollectionId in config Params matches with the AppGroupID for the single app kiosk profile");
                    appCollectionId = tempCollectionId;
                }
                else {
                    IOSSeqCmdUtil.LOGGER.log(Level.WARNING, "AppGroupID mismatch: AppGroupID of appCollectionId in configParams and AppGroupID of single app kiosk profile doesn't match");
                }
            }
        }
        return appCollectionId;
    }
    
    public void addSubCommandsArrayToSeqCmd(final JSONArray subCommandArrsy, final Long baseCommandID, final Long timeout, final JSONObject params) {
        try {
            final Long seqCmdId = SeqCmdUtils.getInstance().getSequentialIDforBaseID(baseCommandID);
            if (seqCmdId != -1L) {
                return;
            }
            final JSONObject commandArrayObject = new JSONObject();
            commandArrayObject.put("subCommands", (Object)subCommandArrsy);
            commandArrayObject.put("basecmdID", (Object)baseCommandID);
            commandArrayObject.put("SequentialCommandId", (Object)DeviceCommandRepository.getInstance().addSequentialCommand("Sequential;" + baseCommandID.toString()));
            commandArrayObject.put("allowImmediateProcessing", true);
            commandArrayObject.put("timeout", (Object)timeout);
            if (params.length() > 0) {
                commandArrayObject.put("params", (Object)params);
            }
            final JSONArray commandArray = new JSONArray();
            commandArray.put((Object)commandArrayObject);
            final JSONObject commandObject = new JSONObject();
            commandObject.put("SequentialCommands", (Object)commandArray);
            SeqCmdDBUtil.getInstance().addSequentialCommands(commandObject);
        }
        catch (final Exception ex) {
            IOSSeqCmdUtil.LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    private JSONObject prepareBaseProfileJSONForSeqCmd(final int installCommandOrder, final Long installProfileCommandID) {
        final JSONObject addBaseCommandObject = new JSONObject();
        try {
            addBaseCommandObject.put("cmd_id", (Object)installProfileCommandID);
            addBaseCommandObject.put("order", installCommandOrder);
            addBaseCommandObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler");
        }
        catch (final JSONException ex) {
            IOSSeqCmdUtil.LOGGER.log(Level.SEVERE, null, (Throwable)ex);
        }
        return addBaseCommandObject;
    }
    
    public void addAutomateOSUpdateSequentiaCommand(final Long collectionID) {
        try {
            final JSONObject seqCmds = new JSONObject();
            final JSONArray cmdArray = new JSONArray();
            final JSONObject cmdArrayObj = new JSONObject();
            final JSONArray subCmdArray = new JSONArray();
            final ArrayList collectionIdsList = new ArrayList();
            collectionIdsList.add(collectionID);
            final JSONObject removeOSUpdateRestrictionCmd = new JSONObject();
            removeOSUpdateRestrictionCmd.put("cmd_id", DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIdsList, "RemoveRestrictOSUpdates").get(0));
            removeOSUpdateRestrictionCmd.put("order", 1);
            removeOSUpdateRestrictionCmd.put("handler", (Object)"com.me.mdm.server.updates.osupdates.ios.IOSUpdateSeqCmdResponseHandler");
            subCmdArray.put((Object)removeOSUpdateRestrictionCmd);
            final JSONObject availableUpdatesCmd = new JSONObject();
            availableUpdatesCmd.put("cmd_id", (Object)DeviceCommandRepository.getInstance().addCommand("AvailableOSUpdates"));
            availableUpdatesCmd.put("order", 2);
            availableUpdatesCmd.put("handler", (Object)"com.me.mdm.server.updates.osupdates.ios.IOSUpdateSeqCmdResponseHandler");
            subCmdArray.put((Object)availableUpdatesCmd);
            final JSONObject attemptFirstUpdateCmd = new JSONObject();
            attemptFirstUpdateCmd.put("cmd_id", (Object)DeviceCommandRepository.getInstance().addCommand("AttemptOSUpdate"));
            attemptFirstUpdateCmd.put("order", 3);
            attemptFirstUpdateCmd.put("handler", (Object)"com.me.mdm.server.updates.osupdates.ios.IOSUpdateSeqCmdResponseHandler");
            subCmdArray.put((Object)attemptFirstUpdateCmd);
            final JSONObject osUpdateScanStatus = new JSONObject();
            osUpdateScanStatus.put("cmd_id", (Object)DeviceCommandRepository.getInstance().addCommand("OSUpdateStatus"));
            osUpdateScanStatus.put("order", 4);
            osUpdateScanStatus.put("handler", (Object)"com.me.mdm.server.updates.osupdates.ios.IOSUpdateSeqCmdResponseHandler");
            subCmdArray.put((Object)osUpdateScanStatus);
            final JSONObject clearPasscodeCmd = new JSONObject();
            clearPasscodeCmd.put("cmd_id", (Object)DeviceCommandRepository.getInstance().addCommand("ClearPasscode"));
            clearPasscodeCmd.put("order", 5);
            clearPasscodeCmd.put("handler", (Object)"com.me.mdm.server.updates.osupdates.ios.IOSUpdateSeqCmdResponseHandler");
            subCmdArray.put((Object)clearPasscodeCmd);
            final JSONObject installUpdateCmd = new JSONObject();
            installUpdateCmd.put("cmd_id", DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIdsList, "ScheduleOSUpdate").get(0));
            installUpdateCmd.put("order", 6);
            installUpdateCmd.put("handler", (Object)"com.me.mdm.server.updates.osupdates.ios.IOSUpdateSeqCmdResponseHandler");
            subCmdArray.put((Object)installUpdateCmd);
            final JSONObject OSUpdateRestrict = new JSONObject();
            OSUpdateRestrict.put("cmd_id", DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIdsList, "RestrictOSUpdates").get(0));
            OSUpdateRestrict.put("order", 7);
            OSUpdateRestrict.put("handler", (Object)"com.me.mdm.server.updates.osupdates.ios.IOSUpdateSeqCmdResponseHandler");
            subCmdArray.put((Object)OSUpdateRestrict);
            cmdArrayObj.put("subCommands", (Object)subCmdArray);
            cmdArrayObj.put("basecmdID", DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIdsList, "ScheduleOSUpdate").get(0));
            cmdArrayObj.put("SequentialCommandId", (Object)DeviceCommandRepository.getInstance().addSequentialCommand("Sequential_".concat("ScheduleOSUpdate").concat(collectionID.toString())));
            cmdArrayObj.put("allowImmediateProcessing", false);
            cmdArrayObj.put("timeout", 60000);
            cmdArray.put((Object)cmdArrayObj);
            seqCmds.put("SequentialCommands", (Object)cmdArray);
            IOSSeqCmdUtil.LOGGER.log(Level.INFO, "addAutomateOSUpdateSequentiaCommand final json to add {0}", seqCmds.toString());
            SeqCmdDBUtil.getInstance().addSequentialCommands(seqCmds);
        }
        catch (final Exception ex) {
            IOSSeqCmdUtil.LOGGER.log(Level.SEVERE, "addAutomateOSUpdateSequentiaCommand error ", ex);
        }
    }
    
    private List prepareKioskProfileForSeqCmd(final Long collectionID, int installCommandOrder, final Long customerID, final Long profileId, final JSONObject configParams) {
        final List kioskProfile = new ArrayList();
        final List metaList = new ArrayList();
        final List collectionList = new ArrayList();
        final Long installApplicationCommandID = null;
        try {
            collectionList.add(collectionID);
            final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
            final JSONObject kioskAPP = kioskHandler.isProfileApplicableForIOSKioskAutomation(collectionID, customerID);
            final Integer kioskType = kioskHandler.getKioskType(collectionID);
            if (kioskType == 1 || kioskType == 3) {
                final Long appGroupID = kioskAPP.optLong("APP_GROUP_ID");
                final Long appCollectionID = this.getValidAppCollectionIDForKiosk(appGroupID, configParams);
                if (appCollectionID != null) {
                    final JSONObject installApplicationCommandJSON = this.getCollectionCommandJSON(appCollectionID, installCommandOrder++, "InstallApplication");
                    final JSONObject managedApplicationJSON = this.getManagedApplicationJSON(appCollectionID, collectionID, customerID, installCommandOrder++);
                    kioskProfile.add(installApplicationCommandJSON);
                    kioskProfile.add(managedApplicationJSON);
                }
                this.createCommandForKiosk(collectionID, customerID);
                final JSONObject kioskCommandJSON = this.getCollectionCommandJSON(collectionID, installCommandOrder++, "KioskDefaultRestriction");
                kioskProfile.add(kioskCommandJSON);
            }
        }
        catch (final Exception e) {
            IOSSeqCmdUtil.SEQLOGGER.log(Level.SEVERE, "While Creating kiosk app automation", e);
        }
        return kioskProfile;
    }
    
    public boolean isCollectionInstallSuccessForResource(final Long collectionID, final Long resourceID) {
        if (collectionID != null) {
            try {
                final Table table = new Table("RecentProfileForResource");
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
                selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                final Column column = new Column((String)null, "*");
                selectQuery.addSelectColumn(column);
                Criteria criteria = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collectionID, 0);
                final Criteria criteria2 = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceID, 0);
                criteria = criteria.and(criteria2).and(new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0));
                selectQuery.setCriteria(criteria);
                final Persistence persistence = MDMUtil.getPersistence();
                DataObject dataObject = null;
                dataObject = persistence.get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Row row = dataObject.getFirstRow("CollnToResources");
                    final int status = (int)row.get("STATUS");
                    if (status == 6) {
                        return true;
                    }
                }
            }
            catch (final DataAccessException e) {
                IOSSeqCmdUtil.LOGGER.log(Level.SEVERE, "Exception in getting installed app", (Throwable)e);
            }
        }
        return false;
    }
    
    private String createCommandUUIDForAppGroupID(final Long appGroupID, final Long collectionID, final String profilePath) {
        String commandUUID = null;
        try {
            String appIdentifier = AppsUtil.getInstance().getIdentifierFromAppGroupID(appGroupID);
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS")) {
                appIdentifier = IOSModifiedEnterpriseAppsUtil.getOriginalBundleIDOfEnterpriseApp(appIdentifier);
            }
            final List identifierList = new ArrayList();
            identifierList.add(appIdentifier);
            commandUUID = PayloadHandler.getInstance().createManagedAppListFromIdentifier(collectionID, identifierList, profilePath);
        }
        catch (final Exception e) {
            IOSSeqCmdUtil.LOGGER.log(Level.SEVERE, "Exception in creating commanduuid for appuuid", e);
        }
        return commandUUID;
    }
    
    public JSONObject getAppDetails(final Long appGroupID) throws Exception {
        final JSONObject appDetails = new JSONObject();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        sQuery.addJoin(new Join("MdPackageToAppGroup", "MdLicenseToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        sQuery.addJoin(new Join("MdLicenseToAppGroupRel", "MdLicense", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 1));
        sQuery.addJoin(new Join("MdPackageToAppGroup", "MDAppAssignableDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        final Criteria appGrpCriteria = new Criteria(new Column("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupID, 0);
        final Criteria appEnterpriseAppTypeCriteria = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 0);
        final Criteria appVPPAppType = new Criteria(new Column("MDAppAssignableDetails", "APP_ASSIGNABLE_TYPE"), (Object)2, 0);
        final Criteria appVppCriteria = new Criteria(new Column("MdLicense", "LICENSED_TYPE"), (Object)2, 0);
        final Criteria vppCriteria = appVppCriteria.and(appVPPAppType);
        sQuery.setCriteria(appGrpCriteria.and(appEnterpriseAppTypeCriteria.or(vppCriteria)));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
        final String s = RelationalAPI.getInstance().getSelectSQL((Query)sQuery);
        if (!dataObject.isEmpty()) {
            final Row appsTypeRow = dataObject.getFirstRow("MdPackageToAppGroup");
            appDetails.put("PACKAGE_TYPE", appsTypeRow.get("PACKAGE_TYPE"));
            if (dataObject.containsTable("MdLicense")) {
                final Row appAddedType = dataObject.getFirstRow("MdLicense");
                appDetails.put("LICENSED_TYPE", appAddedType.get("LICENSED_TYPE"));
            }
        }
        return appDetails;
    }
    
    private Long createBaseCommandForKiosk(final Long collectionID, final String request) {
        Long commandID = null;
        final List collectionList = new ArrayList();
        collectionList.add(collectionID);
        final String kioskCommandUUID = request + UUID.randomUUID().toString();
        final List metaList = new ArrayList();
        final Properties managedAppListProps = new Properties();
        managedAppListProps.setProperty("commandUUID", kioskCommandUUID);
        managedAppListProps.setProperty("commandType", request);
        managedAppListProps.setProperty("commandFilePath", "--");
        managedAppListProps.setProperty("dynamicVariable", "false");
        metaList.add(managedAppListProps);
        DeviceCommandRepository.getInstance().addCollectionCommand(collectionID, metaList);
        final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandListFromCommandUUID(collectionList, kioskCommandUUID);
        if (!commandList.isEmpty()) {
            commandID = commandList.get(0);
        }
        return commandID;
    }
    
    public Long getCommandIDForKioskSeqCommand(final Long profileCollectionID, final Long appCollectionID, final String request, final Long baseCollectionId) {
        try {
            final List profileCollectionList = new ArrayList();
            profileCollectionList.add(baseCollectionId);
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(profileCollectionList, request);
            for (final Object commandId : commandList) {
                final JSONObject params = SeqCmdUtils.getInstance().getSeqParamsFromBaseCmd((Long)commandId);
                if (params != null) {
                    final Long appCollnId = params.optLong(IOSSeqCmdUtil.appCollection);
                    final Long profileCollnID = params.optLong(IOSSeqCmdUtil.profileCollection);
                    if (appCollnId.equals(appCollectionID) && profileCollectionID.equals(profileCollnID)) {
                        return (Long)commandId;
                    }
                    continue;
                }
            }
        }
        catch (final Exception e) {
            IOSSeqCmdUtil.SEQLOGGER.log(Level.SEVERE, "Error while getting base commandID for params", e);
        }
        return null;
    }
    
    private void createRemoveSeqCommandForCollection(final Long collectionID, final JSONObject removeParams, final List configurationList) throws Exception {
        final List collectionList = new ArrayList();
        collectionList.add(collectionID);
        final JSONArray removeCommandArray = new JSONArray();
        int removeCommandOrder = removeCommandArray.length() + 1;
        Long baseCommand = null;
        JSONObject params = new JSONObject();
        final Long customerId = removeParams.getLong("CUSTOMER_ID");
        final PolicySpecificSeqHandler policySpecificSeqHandler = new PolicySpecificSeqHandler();
        final JSONObject policySpecificParams = new JSONObject();
        policySpecificParams.put("COLLECTION_ID", (Object)collectionID);
        policySpecificParams.put("CUSTOMER_ID", (Object)customerId);
        policySpecificParams.put("order", removeCommandOrder);
        policySpecificParams.put("params", (Object)removeParams);
        final JSONObject policyJSON = policySpecificSeqHandler.getPolicyCommand(configurationList, policySpecificParams, IOSSeqCmdUtil.IOS_REMOVE_PROFILE, IOSSeqCmdUtil.IOS_REMOVE_PRIORITIZEDPOLICY);
        final JSONArray policyArray = policyJSON.getJSONArray("commandArray");
        final JSONObject policySeqParams = policyJSON.getJSONObject("params");
        for (int i = 0; i < policyArray.length(); ++i) {
            final JSONObject subCommand = (JSONObject)policyArray.get(i);
            removeCommandArray.put((Object)subCommand);
            ++removeCommandOrder;
        }
        params = JSONUtil.mergeJSONObjects(params, policySeqParams);
        if (removeCommandArray.length() > 1) {
            if (baseCommand == null) {
                final List baseCommandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "RemoveProfile");
                baseCommand = (baseCommandList.isEmpty() ? null : baseCommandList.get(0));
            }
            final long timeOut = 10000L;
            this.addSubCommandsArrayToSeqCmd(removeCommandArray, baseCommand, timeOut, params);
        }
    }
    
    private void createCommandForKiosk(final Long collectionId, final Long customerID) {
        final List metaDataList = new ArrayList();
        final String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionId);
        final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, collectionId);
        final String kioskProfileName = mdmProfileDir + File.separator + "kiosk_restriction_profile.xml";
        final String kioskProfileRelativePath = mdmProfileRelativeDirPath + File.separator + "kiosk_restriction_profile.xml";
        final String installCommandUUID = PayloadHandler.getInstance().generateCustomKioskInstallProfile(collectionId, kioskProfileName, customerID);
        final Properties installProfile = new Properties();
        installProfile.setProperty("commandUUID", installCommandUUID);
        installProfile.setProperty("commandType", "KioskDefaultRestriction");
        installProfile.setProperty("commandFilePath", kioskProfileRelativePath);
        installProfile.setProperty("dynamicVariable", "false");
        metaDataList.add(installProfile);
        DeviceCommandRepository.getInstance().addCollectionCommand(collectionId, metaDataList);
    }
    
    public JSONObject getCollectionCommandJSON(final Long collectionId, int installCommandOrder, final String commandName) throws JSONException {
        final JSONObject collectionObject = new JSONObject();
        final List collectionList = new ArrayList();
        collectionList.add(collectionId);
        final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, commandName);
        final Long commandId = commandList.isEmpty() ? null : commandList.get(0);
        if (commandId != null) {
            collectionObject.put("cmd_id", (Object)commandId);
            collectionObject.put("order", installCommandOrder++);
            collectionObject.put("handler", (Object)"com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler");
        }
        return collectionObject;
    }
    
    public void removeSeqCommandForResource(final Long resourceId, final String commandUUID) {
        try {
            final JSONObject params = new JSONObject();
            final JSONObject removeObject = new JSONObject();
            removeObject.put("commandUUID", (Object)commandUUID);
            removeObject.put("resourceID", (Object)resourceId);
            removeObject.put("params", (Object)params);
            removeObject.put("action", 2);
            SeqCmdRepository.getInstance().processSeqCommand(removeObject);
        }
        catch (final JSONException e) {
            IOSSeqCmdUtil.SEQLOGGER.log(Level.SEVERE, "Exception", (Throwable)e);
        }
    }
    
    private JSONObject isSingleAppAssociated(final Long collectionId, final Long profileId) {
        JSONObject singleApp = null;
        try {
            if (profileId != null) {
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
                query.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
                query.addJoin(new Join("ProfileToCollection", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                query.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                query.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
                query.addJoin(new Join("ConfigDataItem", "AppLockPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
                final Criteria criteria = new Criteria(new Column("AppLockPolicy", "KIOSK_MODE"), (Object)new int[] { 1, 3 }, 8);
                final Criteria notCriteria = new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionId, 1);
                final Criteria profileCriteria = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0);
                final Criteria finalCriteria = notCriteria.and(profileCriteria);
                query.setCriteria(criteria.and(finalCriteria));
                final SortColumn sortColmn = new SortColumn(new Column("Collection", "COLLECTION_ID"), false);
                query.addSortColumn(sortColmn);
                query.addSelectColumn(new Column((String)null, "*"));
                final DataObject dataObject = MDMUtil.getPersistence().get(query);
                if (!dataObject.isEmpty()) {
                    singleApp = new JSONObject();
                    final Iterator iterator = dataObject.getRows("Collection");
                    while (iterator.hasNext()) {
                        final Row row = iterator.next();
                        final Long removeCollectionId = (Long)row.get("COLLECTION_ID");
                        final List collectionList = new ArrayList();
                        collectionList.add(removeCollectionId);
                        final List removeCollectionList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "RemoveKioskDefaultRestriction");
                        if (!removeCollectionList.isEmpty()) {
                            singleApp.put("collectionId", (Object)removeCollectionId);
                            break;
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            IOSSeqCmdUtil.LOGGER.log(Level.SEVERE, "Error in single app mode association", e);
        }
        return singleApp;
    }
    
    private JSONObject getManagedApplicationJSON(final Long appCollectionId, final Long profileCollectionId, final Long customerID, final int installCommandOrder) throws JSONException {
        final List metaList = new ArrayList();
        final Long appGroupID = MDMUtil.getInstance().getAppGroupIDFromCollection(appCollectionId);
        final String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", profileCollectionId);
        final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, profileCollectionId);
        final String profileFileName = mdmProfileDir + File.separator + "managed_app_list.xml";
        final String managedAppRelativePath = mdmProfileRelativeDirPath + File.separator + "managed_app_list.xml";
        final String commandUUIDForApp = this.createCommandUUIDForAppGroupID(appGroupID, profileCollectionId, profileFileName);
        final Properties managedAppListProps = new Properties();
        managedAppListProps.setProperty("commandUUID", commandUUIDForApp);
        managedAppListProps.setProperty("commandType", "ManagedApplicationList");
        managedAppListProps.setProperty("commandFilePath", managedAppRelativePath);
        managedAppListProps.setProperty("dynamicVariable", "false");
        metaList.add(managedAppListProps);
        DeviceCommandRepository.getInstance().addCollectionCommand(profileCollectionId, metaList);
        return this.getCollectionCommandJSON(profileCollectionId, installCommandOrder, "ManagedApplicationList");
    }
    
    public Long createKioskAppUpdateSeqCmd(final Long appCollectionId, final Long profileCollectionId, final Long resourceId, final Long customerId) throws Exception {
        Long baseCommand = null;
        int installCommandOrder = 1;
        final JSONArray installSubCommandArray = new JSONArray();
        if (appCollectionId != null && profileCollectionId != null) {
            final JSONObject removeProfile = this.getCollectionCommandJSON(profileCollectionId, installCommandOrder++, "RemoveProfile");
            final JSONObject remoteLock = this.getSecurityCommandJSON(installCommandOrder++, "DeviceLock", "com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler");
            final JSONObject dummyProfile = this.getSecurityCommandJSON(installCommandOrder++, "DefaultMDMKioskProfile", "com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler");
            final JSONObject dummyRemoveProfile = this.getSecurityCommandJSON(installCommandOrder++, "DefaultMDMRemoveKioskProfile", "com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler");
            final JSONObject installApplicationProfile = this.getCollectionCommandJSON(appCollectionId, installCommandOrder++, "InstallApplication");
            final JSONObject installApplicationList = this.getSecurityCommandJSON(installCommandOrder++, "InstalledApplicationList", "com.me.mdm.server.seqcommands.ios.IOSSeqCmdResponseHandler");
            final JSONObject managedApplication = this.getManagedApplicationJSON(appCollectionId, profileCollectionId, customerId, installCommandOrder++);
            final JSONObject installProfile = this.getCollectionCommandJSON(profileCollectionId, installCommandOrder++, "InstallProfile");
            installSubCommandArray.put((Object)removeProfile);
            installSubCommandArray.put((Object)remoteLock);
            installSubCommandArray.put((Object)dummyProfile);
            installSubCommandArray.put((Object)dummyRemoveProfile);
            installSubCommandArray.put((Object)installApplicationProfile);
            installSubCommandArray.put((Object)installApplicationList);
            installSubCommandArray.put((Object)managedApplication);
            installSubCommandArray.put((Object)installProfile);
            baseCommand = this.createBaseCommandForKiosk(appCollectionId, "KioskUpdateProfile");
            final JSONObject params = new JSONObject();
            params.put(IOSSeqCmdUtil.appCollection, (Object)appCollectionId);
            params.put(IOSSeqCmdUtil.profileCollection, (Object)profileCollectionId);
            params.put("case", (Object)"kioskAppUpdate");
            final long timeOut = 540000L;
            final JSONObject commandArrayObject = new JSONObject();
            commandArrayObject.put("subCommands", (Object)installSubCommandArray);
            commandArrayObject.put("basecmdID", (Object)baseCommand);
            commandArrayObject.put("SequentialCommandId", (Object)DeviceCommandRepository.getInstance().addSequentialCommand("Sequential;" + baseCommand.toString()));
            commandArrayObject.put("allowImmediateProcessing", false);
            commandArrayObject.put("timeout", timeOut);
            commandArrayObject.put("params", (Object)params);
            final JSONArray commandArray = new JSONArray();
            commandArray.put((Object)commandArrayObject);
            final JSONObject commandObject = new JSONObject();
            commandObject.put("SequentialCommands", (Object)commandArray);
            SeqCmdDBUtil.getInstance().addSequentialCommands(commandObject);
        }
        return baseCommand;
    }
    
    public JSONObject getSecurityCommandJSON(int installCommandOrder, final String request, final String handler) throws JSONException {
        final JSONObject securityObject = new JSONObject();
        final Long commandId = DeviceCommandRepository.getInstance().addCommand(request);
        securityObject.put("cmd_id", (Object)commandId);
        securityObject.put("order", installCommandOrder++);
        securityObject.put("handler", (Object)handler);
        return securityObject;
    }
    
    private boolean checkSeqCmdAvailableForCollection(final Long collectionId, final String commandType) {
        final List collectionList = new ArrayList();
        collectionList.add(collectionId);
        final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, commandType);
        final Long commandId = commandList.isEmpty() ? null : commandList.get(0);
        if (commandId != null) {
            final Long seqCmdId = SeqCmdUtils.getInstance().getSequentialIDforBaseID(commandId);
            if (seqCmdId != -1L) {
                return false;
            }
        }
        return true;
    }
    
    private List<String> getPreviousConfiguredSeqRemovalCommand(final Long collectionId, final Long profileId) {
        final HashSet<String> configuredCollection = new HashSet<String>();
        try {
            final Criteria passcodeDisableCriteria = new Criteria(new Column("PasscodePolicy", "RESTRICT_PASSCODE"), (Object)true, 0).and(new Criteria(new Column("PasscodePolicy", "FORCE_PASSCODE"), (Object)false, 0));
            final Criteria passcodeRestrictCriteria = new Criteria(new Column("PasscodePolicy", "FORCE_PASSCODE"), (Object)true, 0).and(new Column("PasscodePolicy", "RESTRICT_PASSCODE"), (Object)true, 0);
            final Criteria restrictionCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)173, 0);
            final Criteria sharedDeviceCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)527, 0);
            final Criteria kioskProfileCriteria = new Criteria(new Column("AppLockPolicy", "KIOSK_MODE"), (Object)new int[] { 1, 3 }, 8);
            final Criteria profileCriteria = new Criteria(new Column("ProfileToCollection", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria collectionCriteria = new Criteria(new Column("ProfileToCollection", "COLLECTION_ID"), (Object)collectionId, 1);
            Criteria finalCriteria = passcodeDisableCriteria.or(passcodeRestrictCriteria).or(restrictionCriteria).or(kioskProfileCriteria).or(sharedDeviceCriteria);
            finalCriteria = finalCriteria.and(profileCriteria).and(collectionCriteria);
            final List<Join> configJoins = new ArrayList<Join>();
            configJoins.add(new Join("CfgDataToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            final MDMConfigQuery configQuery = new MDMConfigQuery(IOSSeqCmdUtil.PREVIOUS_CONFIGURED_CONFIG_LIST, finalCriteria);
            IOSSeqCmdUtil.LOGGER.log(Level.INFO, "Previously configured config list:{0}", new Object[] { IOSSeqCmdUtil.PREVIOUS_CONFIGURED_CONFIG_LIST });
            configQuery.setConfigJoins(configJoins);
            final DataObject dataObject = MDMConfigQueryUtil.getConfigDataObject(configQuery);
            if (!dataObject.isEmpty()) {
                final Row passcodeDisableRow = dataObject.getRow("PasscodePolicy", passcodeDisableCriteria);
                if (passcodeDisableRow != null) {
                    configuredCollection.add("DisablePasscode");
                }
                final Row passcodeRestrictRow = dataObject.getRow("PasscodePolicy", passcodeRestrictCriteria);
                if (passcodeRestrictRow != null) {
                    configuredCollection.add("RestrictPasscode");
                }
                final Row restrictionRow = dataObject.getRow("ConfigData", restrictionCriteria);
                if (restrictionRow != null) {
                    configuredCollection.add("SingletonRestriction");
                }
                final Criteria singleAppCriteria = new Criteria(new Column("AppLockPolicy", "KIOSK_MODE"), (Object)1, 0);
                final Row singleAppKioskRow = dataObject.getRow("AppLockPolicy", singleAppCriteria);
                if (singleAppKioskRow != null) {
                    configuredCollection.add("RemoveKioskDefaultRestriction");
                }
                final Criteria singleWebAppCriteria = new Criteria(new Column("AppLockPolicy", "KIOSK_MODE"), (Object)3, 0);
                final Row singleWebAppKioskRow = dataObject.getRow("AppLockPolicy", singleWebAppCriteria);
                if (singleWebAppKioskRow != null) {
                    configuredCollection.add("RemoveKioskDefaultRestriction");
                    configuredCollection.add("RemoveSingleWebAppKioskAppConfiguration");
                }
                final Row sharedDeviceRow = dataObject.getRow("ConfigData", sharedDeviceCriteria);
                if (sharedDeviceRow != null) {
                    configuredCollection.add("SharedDeviceRestrictions");
                }
            }
        }
        catch (final DataAccessException e) {
            IOSSeqCmdUtil.LOGGER.log(Level.SEVERE, "Exception in previous configured command", (Throwable)e);
        }
        return new ArrayList<String>(configuredCollection);
    }
    
    static {
        IOSSeqCmdUtil.iosSequentialCommandUtil = null;
        LOGGER = Logger.getLogger("MDMConfigLogger");
        SEQLOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
        IOSSeqCmdUtil.appCollection = "AppCollection";
        IOSSeqCmdUtil.profileCollection = "ProfileCollection";
        IOSSeqCmdUtil.appGroupId = "appGroupId";
        IOSSeqCmdUtil.appIdentifier = "appIdentifier";
        IOS_POLICY_COMMAND_AFTER_PROFILE = new HashMap<String, String>() {
            {
                this.put(String.valueOf(522), "com.me.mdm.server.seqcommands.ios.policy.IOSLockScreenSeqCmd");
                this.put(String.valueOf(172), "com.me.mdm.server.seqcommands.ios.policy.IOSPasscodeSeqCmd");
                this.put(String.valueOf(183), "com.me.mdm.server.seqcommands.ios.policy.IOSAppLockSeqCmd");
                this.put("RestrictionProfileStatus", "com.me.mdm.server.seqcommands.ios.policy.IOSRestrictionProfileStatusSeqCmd");
                this.put(String.valueOf(527), "com.me.mdm.server.seqcommands.ios.policy.IOSSharedRestrictionSeqCmd");
            }
        };
        IOS_POLICY_COMMAND_BEFORE_PROFILE = new HashMap<String, String>() {
            {
                this.put("DisablePasscode", "com.me.mdm.server.seqcommands.ios.policy.IOSDisablePasscodeSeqCmd");
                this.put("RestrictPasscode", "com.me.mdm.server.seqcommands.ios.policy.IOSRestrictPasscodeSeqCmd");
                this.put("RemoveKioskDefaultRestriction", "com.me.mdm.server.seqcommands.ios.policy.IOSRemoveCustomKioskSeqCmd");
                this.put("RemoveSingleWebAppKioskAppConfiguration", "com.me.mdm.server.seqcommands.ios.policy.IOSRemoveSingleWebKioskSeqCmd");
                this.put("SharedDeviceRestrictions", "com.me.mdm.server.seqcommands.ios.policy.IOSRemoveSharedRestrictionSeqCmd");
            }
        };
        IOS_REMOVE_PROFILE = new HashMap<String, String>() {
            {
                this.put("DisablePasscode", "com.me.mdm.server.seqcommands.ios.policy.IOSDisablePasscodeSeqCmd");
                this.put("RestrictPasscode", "com.me.mdm.server.seqcommands.ios.policy.IOSRestrictPasscodeSeqCmd");
                this.put(String.valueOf(172), "com.me.mdm.server.seqcommands.ios.policy.IOSRemovePasscodeSeqCmd");
                this.put(String.valueOf(183), "com.me.mdm.server.seqcommands.ios.policy.IOSRemoveAppLockSeqCmd");
                this.put("RemoveProfile", "com.me.mdm.server.seqcommands.ios.policy.IOSRemoveSeqCmd");
                this.put("SingletonRestriction", "com.me.mdm.server.seqcommands.ios.policy.IOSRemoveSingletonRestrictionSeqCmd");
                this.put(String.valueOf(173), "com.me.mdm.server.seqcommands.ios.policy.IOSRemoveRestrictionSeqCmd");
                this.put("RemoveKioskDefaultRestriction", "com.me.mdm.server.seqcommands.ios.policy.IOSRemoveCustomKioskSeqCmd");
                this.put("RemoveSingleWebAppKioskAppConfiguration", "com.me.mdm.server.seqcommands.ios.policy.IOSRemoveSingleWebKioskSeqCmd");
                this.put("RestrictionProfileStatus", "com.me.mdm.server.seqcommands.ios.policy.IOSRestrictionProfileStatusSeqCmd");
                this.put(String.valueOf(527), "com.me.mdm.server.seqcommands.ios.policy.IOSRemoveSharedRestrictionSeqCmd");
                this.put("SharedDeviceRestrictions", "com.me.mdm.server.seqcommands.ios.policy.IOSRemovePreSharedRestrictionSeqCmd");
            }
        };
        IOS_PRIORITIZED_POLICY = new String[] { "SharedDeviceRestrictions", "DisablePasscode", "RestrictPasscode", String.valueOf(183), String.valueOf(172), String.valueOf(173), String.valueOf(518), String.valueOf(521), String.valueOf(522), String.valueOf(172), String.valueOf(527), "RestrictionProfileStatus" };
        IOS_REMOVE_PRIORITIZEDPOLICY = new String[] { "DisablePasscode", "RestrictPasscode", String.valueOf(172), "SingletonRestriction", String.valueOf(173), String.valueOf(183), String.valueOf(527), "SharedDeviceRestrictions", "RemoveKioskDefaultRestriction", "RemoveSingleWebAppKioskAppConfiguration", "RemoveProfile", "RestrictionProfileStatus" };
        PREVIOUS_CONFIGURED_CONFIG_LIST = new ArrayList<Integer>() {
            {
                this.add(172);
                this.add(173);
                this.add(183);
                this.add(527);
            }
        };
    }
}
