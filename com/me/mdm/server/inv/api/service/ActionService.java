package com.me.mdm.server.inv.api.service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import com.me.mdm.server.inv.actions.InvActionUtilProvider;
import java.util.HashSet;
import org.json.simple.JSONArray;
import com.me.mdm.api.command.CommandFacade;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.customgroup.GroupFacade;
import java.util.Collection;
import com.me.mdm.server.device.DeviceFacade;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.me.mdm.api.command.schedule.GroupActionToCollectionHandler;
import com.me.mdm.api.command.schedule.GroupActionScheduleUtils;
import com.me.mdm.api.command.schedule.DeviceActionToCollectionHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.mdm.server.inv.actions.ClearAppDataHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.api.error.APIHTTPException;
import java.util.HashMap;
import java.util.Map;
import com.me.mdm.api.command.container.ContainerCommandWrapper;
import java.util.logging.Logger;

public class ActionService
{
    protected static Logger logger;
    ContainerCommandWrapper containerCommandWrapper;
    
    public ActionService() {
        this.containerCommandWrapper = new ContainerCommandWrapper();
    }
    
    public Map getGroupActionDetails(final Long group_action_id, final Long customerId) throws APIHTTPException {
        final Map resp = new HashMap();
        try {
            if (!this.isGroupValidGroupAction(group_action_id)) {
                final String remarkMsg = "Group Action Id : " + group_action_id;
                throw new APIHTTPException("COM0008", new Object[] { remarkMsg });
            }
            if (customerId == null) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            final SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("GroupActionHistory"));
            groupQuery.addJoin(new Join("GroupActionHistory", "CustomGroup", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
            groupQuery.addJoin(new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            groupQuery.addJoin(new Join("GroupActionHistory", "AaaUser", new String[] { "INITIATED_BY" }, new String[] { "USER_ID" }, 2));
            groupQuery.addSelectColumn(Column.getColumn("GroupActionHistory", "*"));
            groupQuery.addSelectColumn(Column.getColumn("AaaUser", "*"));
            final Criteria groupActionCri = new Criteria(Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID"), (Object)group_action_id, 0);
            final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            groupQuery.setCriteria(groupActionCri.and(customerCri));
            final DataObject dataObj = MDMUtil.getPersistence().get(groupQuery);
            if (dataObj == null || dataObj.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            Row grpActionRow = dataObj.getFirstRow("GroupActionHistory");
            resp.put("action_type", grpActionRow.get("ACTION_ID"));
            resp.put("action_purpose", grpActionRow.get("REASON_MESSAGE"));
            resp.put("initiated_by", grpActionRow.get("INITIATED_BY"));
            resp.put("initiated_time", grpActionRow.get("INITIATED_TIME"));
            resp.put("action_status", grpActionRow.get("ACTION_STATUS"));
            resp.put("updated_by", grpActionRow.get("LAST_MODIFIED_BY"));
            resp.put("updated_time", grpActionRow.get("LAST_MODIFIED_TIME"));
            final Map summary_count = new HashMap();
            summary_count.put("inprogress_count", grpActionRow.get("INPROGRESS_COUNT"));
            summary_count.put("success_count", grpActionRow.get("SUCCESS_COUNT"));
            summary_count.put("failure_count", grpActionRow.get("FAILURE_COUNT"));
            summary_count.put("suspend_count", grpActionRow.get("SUSPEND_COUNT"));
            summary_count.put("total_count", grpActionRow.get("INITIATED_COUNT"));
            grpActionRow = dataObj.getRow("AaaUser");
            resp.put("initiated_by", grpActionRow.get("FIRST_NAME"));
            resp.put("summary_count", summary_count);
            resp.put("app_list", ClearAppDataHandler.getInstance().getGroupActionClearedApps(group_action_id));
        }
        catch (final Exception ex) {
            ActionService.logger.log(Level.SEVERE, "Exception in ActionService::GroupActionDetails :{0}", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return resp;
    }
    
    public void suspendActions(final Map reqParams, final String invActionType, final Long customerId) {
        Long deviceActionId = -1L;
        Long groupActionId = -1L;
        Long collectionID = -1L;
        final Map info = new HashMap();
        try {
            final String commandName = this.containerCommandWrapper.getEquivalentCommandName(invActionType);
            if (reqParams.containsKey("device_action_id")) {
                deviceActionId = Long.valueOf(reqParams.get("device_action_id").toString());
                collectionID = DeviceActionToCollectionHandler.getInstance().getCollectionForDeviceAction(deviceActionId);
            }
            if (reqParams.containsKey("group_action_id")) {
                groupActionId = Long.valueOf(reqParams.get("group_action_id").toString());
                if (GroupActionScheduleUtils.isGroupActionScheduled(groupActionId)) {
                    collectionID = GroupActionToCollectionHandler.getInstance().getCollectionForGroupAction(groupActionId);
                }
                DeviceInvCommandHandler.getInstance().updateGroupActionManualSuspendRemarks(groupActionId);
            }
            if (collectionID != -1L) {
                info.put("collection_id", collectionID);
                info.put("customer_id", Long.parseLong(reqParams.get("customer_id").toString()));
                info.put("user_id", Long.parseLong(reqParams.get("user_id").toString()));
                info.put("user_name", reqParams.get("user_name"));
            }
            DeviceInvCommandHandler.getInstance().suspendBulkCommandExecution(deviceActionId, groupActionId, commandName, customerId, info);
        }
        catch (final Exception ex) {
            ActionService.logger.log(Level.SEVERE, "Exception in ActionService::suspendActions :{0}", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Map validateActionsForDevices(final Map reqParams, final Long customerId) {
        final Map<String, Map<String, Integer>> resp = new HashMap<String, Map<String, Integer>>();
        try {
            List<Long> devices = reqParams.get("devices");
            final List<Long> groups = reqParams.get("groups");
            final List<String> actionList = reqParams.get("actions");
            final Set resourceSet = new LinkedHashSet();
            if (devices != null && devices.size() > 0) {
                final List<Long> deviceList = new ArrayList<Long>();
                for (final Object obj : devices) {
                    deviceList.add(Long.valueOf(String.valueOf(obj)));
                }
                new DeviceFacade().validateIfDevicesExists(deviceList, customerId);
                resourceSet.addAll(deviceList);
            }
            if (groups != null && groups.size() > 0) {
                final List<Long> groupList = new ArrayList<Long>();
                for (final Object obj : groups) {
                    groupList.add(Long.valueOf(String.valueOf(obj)));
                }
                new GroupFacade().validateGroupsIfExists(groupList, customerId);
                final List<Integer> resourceTypeList = new ArrayList<Integer>();
                resourceTypeList.add(120);
                resourceTypeList.add(121);
                devices = MDMGroupHandler.getMemberIdListForGroups(groupList, resourceTypeList);
                final List userList = MDMGroupHandler.getMemberIdListForGroups(groupList, 2);
                devices.addAll(ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(userList, 2));
                resourceSet.addAll(devices);
            }
            final HashMap deviceMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(new ArrayList(resourceSet));
            Map currentActionMap = null;
            new HashMap();
            for (final String action : actionList) {
                currentActionMap = new HashMap();
                final HashMap commandMap = new CommandFacade().getPlatformBasedCommandName(action);
                final Iterator keySet = commandMap.keySet().iterator();
                final JSONArray validDevicesJSONArray = new JSONArray();
                while (keySet.hasNext()) {
                    final int platform = keySet.next();
                    final HashSet deviceSet = deviceMap.get(platform);
                    validDevicesJSONArray.addAll((Collection)InvActionUtilProvider.getInvActionUtil(platform).getApplicableBulkActionDevices(deviceSet, action, customerId));
                }
                currentActionMap.put("total_devices", resourceSet.size());
                currentActionMap.put("applicable_devices", validDevicesJSONArray.size());
                resp.put(action, currentActionMap);
            }
        }
        catch (final Exception ex) {
            ActionService.logger.log(Level.SEVERE, "Exception in ActionService::validateActionsForDevices :{0}", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return resp;
    }
    
    public Map validateActionsForDevices(final Map reqParams, final Long customerId, final String invActionType) {
        final Map resp = new HashMap();
        try {
            List<Long> devices = reqParams.get("devices");
            final List<Long> groups = reqParams.get("groups");
            final Set resourceSet = new LinkedHashSet();
            if (devices != null && devices.size() > 0) {
                final List<Long> deviceList = new ArrayList<Long>();
                for (final Object obj : devices) {
                    deviceList.add(Long.valueOf(String.valueOf(obj)));
                }
                new DeviceFacade().validateIfDevicesExists(deviceList, customerId);
                resourceSet.addAll(deviceList);
            }
            if (groups != null && groups.size() > 0) {
                final List<Long> groupList = new ArrayList<Long>();
                for (final Object obj : groups) {
                    groupList.add(Long.valueOf(String.valueOf(obj)));
                }
                new GroupFacade().validateGroupsIfExists(groupList, customerId);
                final List<Integer> resourceTypeList = new ArrayList<Integer>();
                resourceTypeList.add(120);
                resourceTypeList.add(121);
                devices = MDMGroupHandler.getMemberIdListForGroups(groupList, resourceTypeList);
                resourceSet.addAll(devices);
            }
            final HashMap deviceMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(new ArrayList(resourceSet));
            final HashMap commandMap = new CommandFacade().getPlatformBasedCommandName(invActionType);
            final Iterator keySet = commandMap.keySet().iterator();
            final org.json.JSONArray validDevicesJSONArray = new org.json.JSONArray();
            while (keySet.hasNext()) {
                final int platform = keySet.next();
                final HashSet deviceSet = deviceMap.get(platform);
                final JSONArray currentJSONArray = InvActionUtilProvider.getInvActionUtil(platform).getApplicableBulkActionDevices(deviceSet, invActionType, customerId);
                for (int i = 0; i < currentJSONArray.size(); ++i) {
                    validDevicesJSONArray.put(currentJSONArray.get(i));
                }
            }
            resp.put("total_devices", resourceSet.size());
            resp.put("applicable_devices", validDevicesJSONArray.length());
        }
        catch (final Exception ex) {
            ActionService.logger.log(Level.SEVERE, "Exception in ActionService::validateActionsForDevices :{0}", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return resp;
    }
    
    public Map fetchClearAppDataSuggestions(final String device_id_str, final String group_id_str, final Long customerID) {
        final Map response = new HashMap();
        try {
            Long device_id = -1L;
            Long group_id = -1L;
            List latestAppsList = new ArrayList();
            if (device_id_str != null) {
                device_id = Long.valueOf(device_id_str);
                final List<Long> resourceIDs = new ArrayList<Long>(Arrays.asList(device_id));
                new DeviceFacade().validateIfDevicesExists(resourceIDs, customerID);
                latestAppsList = ClearAppDataHandler.getInstance().fetchDeviceClearAppDataSuggestions(device_id);
            }
            if (group_id_str != null) {
                group_id = Long.valueOf(group_id_str);
                final List<Long> goupList = new ArrayList<Long>(Arrays.asList(group_id));
                new GroupFacade().validateGroupsIfExists(goupList, customerID);
                latestAppsList = ClearAppDataHandler.getInstance().fetchGroupClearAppDataSuggestions(group_id);
            }
            response.put("app_suggestions", latestAppsList);
        }
        catch (final Exception ex) {
            ActionService.logger.log(Level.SEVERE, "Exception in ActionService::fetchClearAppDataSuggestions :{0}", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    private boolean isGroupValidGroupAction(final Long group_action_id) throws Exception {
        final DataObject dataObject = MDMUtil.getPersistence().get("GroupActionHistory", new Criteria(Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID"), (Object)group_action_id, 0));
        if (!dataObject.isEmpty()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    static {
        ActionService.logger = Logger.getLogger("MDMApiLogger");
    }
}
