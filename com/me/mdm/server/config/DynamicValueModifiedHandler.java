package com.me.mdm.server.config;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.Properties;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.Collection;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.core.UserEvent;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

public class DynamicValueModifiedHandler
{
    private static Logger logger;
    private static Logger profilelogger;
    private static final HashMap<String, List> CONFIGDETAILS;
    
    private List getConfigIdToDistribute(final UserEvent userEvent) {
        final HashSet configId = new HashSet();
        try {
            final JSONObject modifiedObject = userEvent.additionalDetails;
            final JSONArray modifiedField = modifiedObject.optJSONArray("MODIFIED_FIELDS");
            for (int i = 0; i < modifiedField.length(); ++i) {
                final String modified = String.valueOf(modifiedField.get(i));
                final List configList = DynamicValueModifiedHandler.CONFIGDETAILS.get(modified);
                if (configList != null && !configList.isEmpty()) {
                    configId.addAll(configList);
                }
            }
        }
        catch (final Exception e) {
            DynamicValueModifiedHandler.logger.log(Level.SEVERE, "Exception while getting configId from userevent", e);
        }
        return new ArrayList(configId);
    }
    
    private List getConfigIdTODistribute(final DeviceEvent deviceEvent) {
        final HashSet configId = new HashSet();
        try {
            final JSONObject modifiedObject = deviceEvent.resourceJSON;
            final Iterator modifiedKeys = modifiedObject.keys();
            while (modifiedKeys.hasNext()) {
                final String modifiedKey = modifiedKeys.next();
                final List configList = DynamicValueModifiedHandler.CONFIGDETAILS.get(modifiedKey);
                if (configList != null && !configList.isEmpty()) {
                    configId.addAll(configList);
                }
            }
        }
        catch (final Exception e) {
            DynamicValueModifiedHandler.logger.log(Level.SEVERE, "Exception while getting configid from device event", e);
        }
        return new ArrayList(configId);
    }
    
    public void redistributeProfileToUser(final UserEvent userEvent) {
        try {
            DynamicValueModifiedHandler.logger.log(Level.INFO, "UserEvent changes is listened going to redistribute the profile. UserEvent:{0}", new Object[] { userEvent.toString() });
            final List configIdList = this.getConfigIdToDistribute(userEvent);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
            final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
            sQuery.addJoin(new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            sQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria userCriteria = new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)userEvent.resourceID, 0);
            final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            sQuery.setCriteria(userCriteria.and(managedDeviceCriteria).and(userNotInTrashCriteria));
            final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            if (!dobj.isEmpty()) {
                final Iterator<Row> iter = dobj.getRows("ManagedDevice");
                while (iter.hasNext()) {
                    final Row row = iter.next();
                    final JSONObject deviceDetails = new JSONObject();
                    deviceDetails.put("RESOURCE_ID", (Object)row.get("RESOURCE_ID"));
                    deviceDetails.put("CUSTOMER_ID", (Object)userEvent.customerID);
                    deviceDetails.put("PLATFORM_TYPE", (Object)row.get("PLATFORM_TYPE"));
                    this.redistributeProfilesToDevice(deviceDetails, configIdList);
                }
            }
        }
        catch (final Exception ex) {
            DynamicValueModifiedHandler.logger.log(Level.SEVERE, "Exception while redistributing profile to user", ex);
        }
    }
    
    public void redistributeProfilesToDevice(final DeviceEvent deviceEvent) {
        try {
            DynamicValueModifiedHandler.logger.log(Level.INFO, "DeviceEvent Changes is listened going to redistribute the profile. Device Event:{0}", new Object[] { deviceEvent.toString() });
            final List configIdList = this.getConfigIdTODistribute(deviceEvent);
            final Row row = DBUtil.getRowFromDB("ManagedDevice", "RESOURCE_ID", (Object)deviceEvent.resourceID);
            if (row != null) {
                final JSONObject deviceDetails = new JSONObject();
                deviceDetails.put("RESOURCE_ID", (Object)row.get("RESOURCE_ID"));
                deviceDetails.put("CUSTOMER_ID", (Object)deviceEvent.customerID);
                deviceDetails.put("PLATFORM_TYPE", (Object)row.get("PLATFORM_TYPE"));
                this.redistributeProfilesToDevice(deviceDetails, configIdList);
            }
        }
        catch (final Exception ex) {
            DynamicValueModifiedHandler.logger.log(Level.SEVERE, ex, () -> "Exception while redistributing profile to devices." + deviceEvent2.toString());
        }
    }
    
    public void redistributeProfileToDevice(final MDMGroupMemberEvent groupEvent) {
        try {
            DynamicValueModifiedHandler.logger.log(Level.INFO, "GroupEvent changes is listened going to redistribute the profile:{0}", new Object[] { groupEvent.toString() });
            final List configIdList = DynamicValueModifiedHandler.CONFIGDETAILS.get("GROUPNAME");
            final List resourceList = Arrays.asList(groupEvent.memberIds);
            for (int i = 0; i < resourceList.size(); ++i) {
                final Long resourceId = resourceList.get(i);
                final JSONObject deviceDetails = new JSONObject();
                deviceDetails.put("RESOURCE_ID", (Object)resourceId);
                deviceDetails.put("CUSTOMER_ID", (Object)groupEvent.customerId);
                this.redistributeProfilesToDevice(deviceDetails, configIdList);
            }
        }
        catch (final Exception ex) {
            DynamicValueModifiedHandler.logger.log(Level.SEVERE, "Exception while adding resource for group event", ex);
        }
    }
    
    private void redistributeProfilesToDevice(final JSONObject deviceDetails, final List configList) {
        try {
            DynamicValueModifiedHandler.logger.log(Level.INFO, "Listener redistributing the device. Device Details:{0} & ConfigId:{1}", new Object[] { deviceDetails.toString(), configList });
            final ArrayList resourceList = new ArrayList();
            resourceList.add(deviceDetails.getLong("RESOURCE_ID"));
            final SelectQuery profileToResourceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
            profileToResourceQuery.addJoin(new Join("RecentProfileForResource", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            profileToResourceQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            profileToResourceQuery.addJoin(new Join("RecentProfileForResource", "MdCollectionCommand", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            profileToResourceQuery.addJoin(new Join("MdCollectionCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            profileToResourceQuery.addJoin(new Join("RecentProfileForResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            profileToResourceQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            profileToResourceQuery.setCriteria(new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceDetails.getLong("RESOURCE_ID"), 0).and(new Criteria(Column.getColumn("MdCommands", "COMMAND_DYNAMIC_VARIABLE"), (Object)true, 0)).and(new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)1, 0)).and(new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0)).and(new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)configList.toArray(), 8)));
            profileToResourceQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
            profileToResourceQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
            profileToResourceQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"));
            final DataObject profileToResourceDobj = MDMUtil.getPersistence().get(profileToResourceQuery);
            final HashMap profileCollectionMap = new HashMap();
            if (!profileToResourceDobj.isEmpty()) {
                final Iterator<Row> profileIterator = profileToResourceDobj.getRows("RecentProfileForResource");
                while (profileIterator.hasNext()) {
                    final Row profileRow = profileIterator.next();
                    profileCollectionMap.put(profileRow.get("PROFILE_ID"), profileRow.get("COLLECTION_ID"));
                }
                final Properties properties = new Properties();
                ((Hashtable<String, ArrayList>)properties).put("resourceList", resourceList);
                ((Hashtable<String, String>)properties).put("commandName", "InstallProfile");
                ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
                ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
                ((Hashtable<String, Long>)properties).put("customerId", deviceDetails.getLong("CUSTOMER_ID"));
                ((Hashtable<String, HashMap>)properties).put("profileProperties", ProfileUtil.getInstance().getProfileAssociatedUserForResource(deviceDetails.getLong("RESOURCE_ID"), profileCollectionMap));
                DynamicValueModifiedHandler.logger.log(Level.INFO, "Associating profile from listener for resource:{0} & properties:{1}", new Object[] { resourceList, properties.toString() });
                DynamicValueModifiedHandler.profilelogger.log(Level.INFO, "Associating profile from listener for resource:{0} & properties:{1}", new Object[] { resourceList, properties.toString() });
                ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
            }
        }
        catch (final Exception ex) {
            DynamicValueModifiedHandler.logger.log(Level.SEVERE, "Exception while reassociating profile to device.", ex);
        }
    }
    
    static {
        DynamicValueModifiedHandler.logger = Logger.getLogger("MDMConfigLogger");
        DynamicValueModifiedHandler.profilelogger = Logger.getLogger("MDMProfileConfigLogger");
        CONFIGDETAILS = new HashMap<String, List>() {
            {
                ((HashMap<String, ArrayList>)this).put("EMAIL_ADDRESS", new ArrayList(Arrays.asList(522, 567, 174, 553, 602, 564, 525, 767, 612)));
                ((HashMap<String, ArrayList>)this).put("FIRST_NAME", new ArrayList(Arrays.asList(522, 567, 177, 774, 605, 176, 766, 521, 187, 178, 562, 520, 179, 516, 773, 181, 180, 556, 566, 604, 606, 564, 525, 767, 612)));
                ((HashMap<String, ArrayList>)this).put("LAST_NAME", new ArrayList(Arrays.asList(522, 567, 177, 774, 605, 176, 766, 521, 187, 178, 562, 520, 179, 516, 773, 181, 180, 556, 566, 604, 606, 564, 525, 767, 612)));
                ((HashMap<String, ArrayList>)this).put("MIDDLE_NAME", new ArrayList(Arrays.asList(522, 567, 525, 767, 612)));
                ((HashMap<String, ArrayList>)this).put("NAME", new ArrayList(Arrays.asList(522, 567, 177, 774, 605, 176, 766, 521, 187, 562, 520, 179, 516, 773, 181, 180, 556, 604, 606, 175, 554, 566, 603, 525, 767, 612)));
                ((HashMap<String, ArrayList>)this).put("DISPLAY_NAME", new ArrayList(Arrays.asList(522, 567, 525, 767, 612)));
                ((HashMap<String, ArrayList>)this).put("DEVICE_NAME", new ArrayList(Arrays.asList(522, 567, 525, 767, 612)));
                ((HashMap<String, ArrayList>)this).put("ASSET_OWNER", new ArrayList(Arrays.asList(522, 567, 525, 767, 612)));
                ((HashMap<String, ArrayList>)this).put("ASSET_TAG", new ArrayList(Arrays.asList(522, 567, 525, 767, 612)));
                ((HashMap<String, ArrayList>)this).put("OFFICE", new ArrayList(Arrays.asList(522, 567, 525, 767, 612)));
                ((HashMap<String, ArrayList>)this).put("GROUPNAME", new ArrayList(Arrays.asList(522, 567, 525, 767, 612)));
            }
        };
    }
}
