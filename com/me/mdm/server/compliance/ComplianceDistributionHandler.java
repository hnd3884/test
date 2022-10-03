package com.me.mdm.server.compliance;

import java.util.Collection;
import java.util.Collections;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import java.util.HashMap;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComplianceDistributionHandler
{
    private static ComplianceDistributionHandler complianceDistributionHandler;
    private Logger logger;
    
    private ComplianceDistributionHandler() {
        (this.logger = Logger.getLogger("MDMDeviceComplianceLogger")).log(Level.FINEST, " -- ComplianceDistributionHandler()   >   new object Creation  ");
    }
    
    public static ComplianceDistributionHandler getInstance() {
        if (ComplianceDistributionHandler.complianceDistributionHandler == null) {
            ComplianceDistributionHandler.complianceDistributionHandler = new ComplianceDistributionHandler();
        }
        return ComplianceDistributionHandler.complianceDistributionHandler;
    }
    
    public void associateComplianceToDevices(final JSONObject complianceJSON) throws Exception {
        try {
            ComplianceHandler.getInstance().addToQueue(complianceJSON, 160);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- associateOrDisassociateComplianceToDevices() >   Error   ", e);
            throw e;
        }
    }
    
    public void disassociateComplianceToDevices(final JSONObject complianceJSON) throws Exception {
        try {
            ComplianceHandler.getInstance().addToQueue(complianceJSON, 161);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- disassociateComplianceToDevices() >   Error   ", e);
            throw e;
        }
    }
    
    public void associateComplianceToGroups(final JSONObject complianceJSON) throws Exception {
        try {
            ComplianceHandler.getInstance().addToQueue(complianceJSON, 164);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- associateOrDisassociateComplianceToDeviceGroups() >   Error   ", e);
            throw e;
        }
    }
    
    public void disassociateComplianceToGroups(final JSONObject complianceJSON) throws Exception {
        try {
            ComplianceHandler.getInstance().addToQueue(complianceJSON, 165);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- disassociateComplianceToGroups() >   Error   ", e);
            throw e;
        }
    }
    
    public void associateComplianceToMDMResource(final JSONObject complianceJSON) throws Exception {
        try {
            ComplianceHandler.getInstance().addToQueue(complianceJSON, 162);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- disassociateComplianceToGroups() >   Error   ", e);
            throw e;
        }
    }
    
    public void disassociateComplianceToMDMResource(final JSONObject complianceJSON) throws Exception {
        try {
            ComplianceHandler.getInstance().addToQueue(complianceJSON, 163);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- disassociateComplianceToGroups() >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getManagedGroupsForComplianceProfile(final JSONObject profileJSON) throws DataAccessException, JSONException {
        try {
            final Long complianceId = JSONUtil.optLongForUVH(profileJSON, "compliance_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(profileJSON, "customer_id", Long.valueOf(-1L));
            final Criteria profileCriteria = new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)complianceId, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("RecentProfileForGroup", profileCriteria);
            final JSONArray groupList = new JSONArray();
            final Iterator iterator = dataObject.getRows("RecentProfileForGroup");
            while (iterator.hasNext()) {
                final Row recentProfileRow = iterator.next();
                final Long groupId = (Long)recentProfileRow.get("GROUP_ID");
                groupList.put((Object)groupId);
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("group_list", (Object)groupList);
            return responseJSON;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- getManagedGroupsForComplianceProfile() >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getManagedDevicesForComplianceProfile(final JSONObject profileJSON) throws DataAccessException, JSONException {
        try {
            final Long complianceId = JSONUtil.optLongForUVH(profileJSON, "compliance_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(profileJSON, "customer_id", Long.valueOf(-1L));
            final Criteria profileCriteria = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)complianceId, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("RecentProfileForResource", profileCriteria);
            final JSONArray resourceList = new JSONArray();
            final Iterator iterator = dataObject.getRows("RecentProfileForResource");
            while (iterator.hasNext()) {
                final Row recentProfileRow = iterator.next();
                final Long resourceId = (Long)recentProfileRow.get("RESOURCE_ID");
                resourceList.put((Object)resourceId);
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("resource_list", (Object)resourceList);
            return responseJSON;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- getManagedDevicesForComplianceProfile() >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getManagedUsersForComplianceProfile(final JSONObject profileJSON) throws DataAccessException, JSONException {
        try {
            final Long complianceId = JSONUtil.optLongForUVH(profileJSON, "compliance_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(profileJSON, "customer_id", Long.valueOf(-1L));
            final Criteria profileCriteria = new Criteria(new Column("RecentProfileForMDMResource", "PROFILE_ID"), (Object)complianceId, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("RecentProfileForMDMResource", profileCriteria);
            final JSONArray userList = new JSONArray();
            final Iterator iterator = dataObject.getRows("RecentProfileForMDMResource");
            while (iterator.hasNext()) {
                final Row recentProfileRow = iterator.next();
                final Long resourceId = (Long)recentProfileRow.get("RESOURCE_ID");
                userList.put((Object)resourceId);
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("user_list", (Object)userList);
            return responseJSON;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- getManagedUsersForC   omplianceProfile() >   Error   ", e);
            throw e;
        }
    }
    
    public void distributeComplianceProfile(final JSONObject requestJSON) throws Exception {
        try {
            final Long complianceId = JSONUtil.optLongForUVH(requestJSON, "compliance_id", Long.valueOf(-1L));
            final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
            final JSONArray groupList = requestJSON.optJSONArray("group_list");
            final JSONArray resourceList = requestJSON.optJSONArray("resource_list");
            final JSONArray userList = requestJSON.optJSONArray("user_list");
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final JSONObject distributionJSON = new JSONObject();
            final String complianceState = String.valueOf(requestJSON.get("compliance_state"));
            distributionJSON.put("compliance_id", (Object)complianceId);
            distributionJSON.put("customer_id", (Object)customerId);
            distributionJSON.put("user_id", (Object)userId);
            distributionJSON.put("user_name", (Object)DMUserHandler.getUserNameFromUserID(userId));
            distributionJSON.put("profile_id", (Object)complianceId);
            distributionJSON.put("collection_id", (Object)collectionId);
            distributionJSON.put("compliance_state", (Object)complianceState);
            if (complianceState.equalsIgnoreCase("compliance_updated")) {
                distributionJSON.put("compliance_state", 905);
                if (resourceList != null && resourceList.length() != 0) {
                    distributionJSON.put("resource_list", (Object)resourceList);
                    this.associateComplianceToDevices(distributionJSON);
                }
                if (groupList != null && groupList.length() != 0) {
                    distributionJSON.put("resource_list", (Object)groupList);
                    this.associateComplianceToGroups(distributionJSON);
                }
                if (userList != null && userList.length() != 0) {
                    distributionJSON.put("resource_list", (Object)userList);
                    this.associateComplianceToMDMResource(distributionJSON);
                }
            }
            else if (complianceState.equalsIgnoreCase("compliance_deleted")) {
                if (resourceList != null && resourceList.length() != 0) {
                    distributionJSON.put("resource_list", (Object)resourceList);
                    this.disassociateComplianceToDevices(distributionJSON);
                }
                if (groupList != null && groupList.length() != 0) {
                    distributionJSON.put("resource_list", (Object)groupList);
                    this.disassociateComplianceToGroups(distributionJSON);
                }
                if (userList != null && userList.length() != 0) {
                    distributionJSON.put("resource_list", (Object)userList);
                    this.disassociateComplianceToMDMResource(distributionJSON);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- distributeComplianceProfile() >   Error   ", e);
            throw e;
        }
    }
    
    public JSONObject getComplianceProfilesForGroup(final JSONObject requestJSON) throws DataAccessException, JSONException {
        try {
            final Long groupResourceId = JSONUtil.optLongForUVH(requestJSON, "group_id", Long.valueOf(-1L));
            final Long customerID = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
            final Criteria cGroup = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID"), (Object)groupResourceId, 0);
            final Join profileJoin = new Join("RecentProfileForGroup", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join historyJoin = new Join("RecentProfileForGroup", "GroupToProfileHistory", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Criteria complianceCriteria = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)5, 0);
            selectQuery.addSelectColumn(new Column("RecentProfileForGroup", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("RecentProfileForGroup", "GROUP_ID"));
            selectQuery.addSelectColumn(new Column("RecentProfileForGroup", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("GroupToProfileHistory", "ASSOCIATED_BY"));
            selectQuery.addSelectColumn(new Column("GroupToProfileHistory", "GROUP_HISTORY_ID"));
            selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("Profile", "PROFILE_TYPE"));
            selectQuery.addJoin(profileJoin);
            selectQuery.addJoin(historyJoin);
            selectQuery.setCriteria(cGroup.and(complianceCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final JSONArray complianceJSONArray = new JSONArray();
            if (!dataObject.isEmpty()) {
                final Iterator profileIterator = dataObject.getRows("RecentProfileForGroup");
                final Iterator historyIterator = dataObject.getRows("GroupToProfileHistory");
                while (profileIterator.hasNext() && historyIterator.hasNext()) {
                    final Row profileRow = profileIterator.next();
                    final Row historyRow = historyIterator.next();
                    final Long complianceId = (Long)profileRow.get("PROFILE_ID");
                    final Long collectionId = (Long)profileRow.get("COLLECTION_ID");
                    final Long associatedBy = (Long)historyRow.get("ASSOCIATED_BY");
                    final JSONObject profileJSON = new JSONObject();
                    profileJSON.put("compliance_id", (Object)complianceId);
                    profileJSON.put("collection_id", (Object)collectionId);
                    profileJSON.put("profile_id", (Object)complianceId);
                    profileJSON.put("user_id", (Object)associatedBy);
                    complianceJSONArray.put((Object)profileJSON);
                }
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("compliance_list", (Object)complianceJSONArray);
            return responseJSON;
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- getComplianceProfilesForGroup() >   Error   ", e);
            throw e;
        }
    }
    
    public void removePendingCommands(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final JSONArray deviceJSONArray = requestJSON.getJSONArray("resource_list");
            final List resourceList = new ArrayList();
            for (int i = 0; i < deviceJSONArray.length(); ++i) {
                resourceList.add(JSONUtil.optLongForUVH(deviceJSONArray, i, -1L));
            }
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CollectionToRules"));
            final Join ruleJoin = new Join("CollectionToRules", "RuleToAction", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 2);
            final Join actionJoin = new Join("RuleToAction", "ActionToActionAttributes", new String[] { "ACTION_ID" }, new String[] { "ACTION_ID" }, 2);
            final Join commandDataJoin = new Join("ActionToActionAttributes", "CommandData", new String[] { "ACTION_ATTRIBUTE_ID" }, new String[] { "COMMAND_DATA_ID" }, 2);
            selectQuery.addJoin(ruleJoin);
            selectQuery.addJoin(actionJoin);
            selectQuery.addJoin(commandDataJoin);
            selectQuery.addSelectColumn(Column.getColumn("CollectionToRules", "*"));
            selectQuery.addSelectColumn(Column.getColumn("RuleToAction", "*"));
            selectQuery.addSelectColumn(Column.getColumn("ActionToActionAttributes", "*"));
            selectQuery.addSelectColumn(Column.getColumn("CommandData", "*"));
            final Criteria collectionCriteria = new Criteria(Column.getColumn("CollectionToRules", "COLLECTION_ID"), (Object)collectionId, 0);
            selectQuery.setCriteria(collectionCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final List commandList = new ArrayList();
            if (dataObject != null) {
                final Iterator iterator = dataObject.getRows("CommandData");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final int commandType = (int)row.get("COMMAND_TYPE");
                    switch (commandType) {
                        case 2: {
                            final Long commandId = DeviceCommandRepository.getInstance().getCommandID("LostModeCommand;Collection=" + collectionId);
                            commandList.add(commandId);
                            continue;
                        }
                        case 3: {
                            Long commandId = DeviceCommandRepository.getInstance().getCommandID("EraseDevice;Collection=" + collectionId);
                            commandList.add(commandId);
                            commandId = DeviceCommandRepository.getInstance().getCommandID("CorporateWipe;Collection=" + collectionId);
                            commandList.add(commandId);
                            continue;
                        }
                    }
                }
            }
            DeviceCommandRepository.getInstance().clearCommandsFromCacheForResources(commandList, resourceList, 1);
            DeviceCommandRepository.getInstance().deleteResourcesCommands(commandList, resourceList, 1);
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- removePendingCommands()   >   Error   ", e);
            throw e;
        }
    }
    
    public Boolean checkComplianceRemoveSafeOnUserChange(final Long resourceID, final Long complianceId, final Long collectionId) {
        Boolean removeSafe = true;
        final HashMap profileCollnMap = new HashMap();
        profileCollnMap.put(complianceId, collectionId);
        final HashMap excludeMap = ProfileDistributionListHandler.getDistributionProfileListHandler(0).getGroupDeviceExcludeProfileMap(new ArrayList(Collections.singleton(resourceID)), profileCollnMap, null);
        final List excludeList = excludeMap.get(complianceId);
        if (excludeList != null && excludeList.contains(resourceID)) {
            removeSafe = Boolean.FALSE;
        }
        return removeSafe;
    }
    
    static {
        ComplianceDistributionHandler.complianceDistributionHandler = null;
    }
}
