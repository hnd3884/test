package com.me.mdm.server.settings.location;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashSet;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberListener;

public class LocationSettingsCGMemberListener implements MDMGroupMemberListener
{
    @Override
    public void groupMemberAdded(final MDMGroupMemberEvent groupEvent) {
        try {
            final Long groupId = groupEvent.groupID;
            final JSONObject resourceJSON = new JSONObject();
            List deviceIds;
            if (groupEvent.groupType != 7) {
                deviceIds = Arrays.asList(groupEvent.memberIds);
            }
            else {
                final List memberUserList = new ArrayList();
                Collections.addAll(memberUserList, groupEvent.memberIds);
                final ArrayList<Long> memberGroupList = (ArrayList<Long>)this.getSubGroupMembers(groupEvent);
                memberUserList.removeAll(memberGroupList);
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution") && !memberGroupList.isEmpty() && !MDMGroupHandler.getInstance().isInCycle(memberGroupList)) {
                    memberGroupList.addAll(MDMGroupHandler.getInstance().getSubGroupList(memberGroupList));
                    final List<Integer> resourceTypeList = new ArrayList<Integer>();
                    resourceTypeList.add(2);
                    memberUserList.addAll(MDMGroupHandler.getMemberIdListForGroups(memberGroupList, resourceTypeList));
                    for (final Long groupID : memberGroupList) {
                        resourceJSON.put(String.valueOf(groupID), (Object)new JSONObject().put("NODE_ID", (Object)groupID));
                    }
                }
                deviceIds = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(memberUserList, 2);
            }
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(groupId);
            final HashSet locationDeviceList = new HashSet();
            locationDeviceList.addAll(deviceIds);
            final Boolean isLocationEnable = LocationSettingsDataHandler.getInstance().isLocationTrackingEnabled(customerId);
            boolean status = isLocationEnable;
            final List<Long> list = new ArrayList<Long>();
            list.add(groupId);
            final int groupInclusionStatus = LocationSettingsDataHandler.getInstance().getGroupInclusionStatus(customerId, list);
            final JSONObject locationSettingsJSON = LocationSettingsDataHandler.getInstance().getLocationSettingsJSON(customerId);
            locationSettingsJSON.put("UPDATED_TIME", System.currentTimeMillis());
            if (groupInclusionStatus != -1) {
                final Boolean isInclusion = groupInclusionStatus != 0;
                final DataObject locationDeviceStatusDO = LocationSettingsDataHandler.getInstance().getLocationDeviceStatusDO(locationSettingsJSON);
                if (isLocationEnable) {
                    status = isInclusion;
                }
                resourceJSON.put(String.valueOf(groupId), (Object)new JSONObject().put("NODE_ID", (Object)groupId));
                locationSettingsJSON.put("resourceJSON", (Object)resourceJSON);
                LocationSettingsDataHandler.getInstance().addOrUpdateLocationResourceCriteria(locationSettingsJSON);
                LocationSettingsDataHandler.getInstance().updateSelectedDeviceStatus(locationDeviceStatusDO, locationDeviceList, status);
                MDMUtil.getPersistence().update(locationDeviceStatusDO);
            }
            LocationSettingsRequestHandler.getInstance().locationSettingsCommandHandling(locationSettingsJSON);
        }
        catch (final Exception e) {
            Logger.getLogger(LocationSettingsCGMemberListener.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    private List getSubGroupMembers(final MDMGroupMemberEvent groupEvent) {
        final ArrayList<Long> memberGroupList = new ArrayList<Long>();
        try {
            final SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
            groupQuery.setCriteria(new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)groupEvent.memberIds, 8));
            groupQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
            final DataObject dataObject = DataAccess.get(groupQuery);
            final Iterator rows = dataObject.getRows("CustomGroup");
            while (rows.hasNext()) {
                final Row r = rows.next();
                memberGroupList.add((Long)r.get("RESOURCE_ID"));
            }
        }
        catch (final DataAccessException e) {
            Logger.getLogger(LocationSettingsCGMemberListener.class.getName()).log(Level.SEVERE, "error in gettting subgroup members", (Throwable)e);
        }
        return memberGroupList;
    }
    
    @Override
    public void groupMemberRemoved(final MDMGroupMemberEvent groupEvent) {
        final Long groupId = groupEvent.groupID;
        final Long customerId = groupEvent.customerId;
        try {
            final Boolean isLocationEnable = LocationSettingsDataHandler.getInstance().isLocationTrackingEnabled(customerId);
            final Boolean isResourceIncluded = LocationSettingsDataHandler.getInstance().isResourceIncluded(customerId);
            boolean status = isLocationEnable;
            final JSONObject resourceToRemove = new JSONObject();
            if (groupEvent.groupType == 7) {
                final List userIds = new ArrayList();
                Collections.addAll(userIds, groupEvent.memberIds);
                final ArrayList<Long> memberGroupList = (ArrayList<Long>)this.getSubGroupMembers(groupEvent);
                userIds.removeAll(memberGroupList);
                if (!memberGroupList.isEmpty() && !MDMGroupHandler.getInstance().isInCycle(memberGroupList)) {
                    memberGroupList.addAll(MDMGroupHandler.getInstance().getSubGroupList(memberGroupList));
                    final List<Integer> resourceTypeList = new ArrayList<Integer>();
                    resourceTypeList.add(2);
                    userIds.addAll(MDMGroupHandler.getMemberIdListForGroups(memberGroupList, resourceTypeList));
                }
                for (final Long groupID : memberGroupList) {
                    resourceToRemove.put(String.valueOf(groupID), (Object)new JSONObject().put("NODE_ID", (Object)groupID));
                }
                groupEvent.memberIds = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(userIds, 2).toArray(new Long[0]);
            }
            final HashSet locationDeviceList = LocationSettingsDataHandler.getInstance().getNonAssociatedDevices(groupEvent);
            final JSONObject locationSettingsJSON = LocationSettingsDataHandler.getInstance().getLocationSettingsJSON(customerId);
            locationSettingsJSON.put("resourceToRemove", (Object)resourceToRemove);
            if (locationDeviceList != null && !locationDeviceList.isEmpty()) {
                locationSettingsJSON.put("UPDATED_TIME", System.currentTimeMillis());
                final DataObject locationDeviceStatusDO = LocationSettingsDataHandler.getInstance().getLocationDeviceStatusDO(locationSettingsJSON);
                if (isLocationEnable) {
                    status = !isResourceIncluded;
                }
                LocationSettingsDataHandler.getInstance().updateSelectedDeviceStatus(locationDeviceStatusDO, locationDeviceList, status);
                MDMUtil.getPersistence().update(locationDeviceStatusDO);
                LocationSettingsRequestHandler.getInstance().locationSettingsCommandHandling(locationSettingsJSON);
            }
            LocationSettingsDataHandler.getInstance().addOrUpdateLocationResourceCriteria(locationSettingsJSON);
        }
        catch (final Exception e) {
            Logger.getLogger(LocationSettingsCGMemberListener.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
