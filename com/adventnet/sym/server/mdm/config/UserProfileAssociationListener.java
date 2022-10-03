package com.adventnet.sym.server.mdm.config;

import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Properties;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.me.mdm.server.config.DynamicValueModifiedHandler;
import com.adventnet.sym.server.mdm.core.UserEvent;
import com.adventnet.sym.server.mdm.core.ManagedUserListener;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class UserProfileAssociationListener extends ManagedDeviceListener implements ManagedUserListener
{
    @Override
    public void userAdded(final UserEvent userEvent) {
    }
    
    @Override
    public void userDeleted(final UserEvent userEvent) {
    }
    
    @Override
    public void userDetailsModified(final UserEvent userEvent) {
        final DynamicValueModifiedHandler handler = new DynamicValueModifiedHandler();
        handler.redistributeProfileToUser(userEvent);
    }
    
    @Override
    public void userTrashed(final UserEvent userEvent) {
    }
    
    @Override
    public void userAssigned(final DeviceEvent deviceEvent) {
        UserProfileAssociationListener.mdmlogger.info("Entering UserProfileAssociationListener:userAssigned");
        this.redistributeProfiles(deviceEvent);
        UserProfileAssociationListener.mdmlogger.info("Exiting UserProfileAssociationListener:userAssigned");
    }
    
    private void redistributeProfiles(final DeviceEvent deviceEvent) {
        try {
            final Row row = DBUtil.getRowFromDB("ManagedDevice", "RESOURCE_ID", (Object)deviceEvent.resourceID);
            if (row != null) {
                final JSONObject deviceDetails = new JSONObject();
                deviceDetails.put("RESOURCE_ID", (Object)row.get("RESOURCE_ID"));
                deviceDetails.put("CUSTOMER_ID", (Object)deviceEvent.customerID);
                deviceDetails.put("PLATFORM_TYPE", (Object)row.get("PLATFORM_TYPE"));
                deviceDetails.put("technicianUserId", deviceEvent.resourceJSON.opt("technicianUserId"));
                this.redistributeProfilesToDevice(deviceDetails);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(UserProfileAssociationListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void redistributeProfilesToDevice(final JSONObject deviceDetails) throws Exception {
        final ArrayList resourceList = new ArrayList();
        resourceList.add(deviceDetails.getLong("RESOURCE_ID"));
        final SelectQuery profileToResourceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        profileToResourceQuery.addJoin(new Join("RecentProfileForResource", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        profileToResourceQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        profileToResourceQuery.addJoin(new Join("RecentProfileForResource", "MdCollectionCommand", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        profileToResourceQuery.addJoin(new Join("MdCollectionCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        profileToResourceQuery.setCriteria(new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceDetails.getLong("RESOURCE_ID"), 0).and(new Criteria(Column.getColumn("MdCommands", "COMMAND_DYNAMIC_VARIABLE"), (Object)true, 0)).and(new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)1, 0)).and(new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0)));
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
            ((Hashtable<String, Integer>)properties).put("platformtype", deviceDetails.getInt("PLATFORM_TYPE"));
            if (deviceDetails.opt("technicianUserId") != null) {
                ((Hashtable<String, Object>)properties).put("loggedOnUser", deviceDetails.opt("technicianUserId"));
            }
            ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
        }
    }
    
    @Override
    public void deviceDetailChanged(final DeviceEvent deviceEvent) {
        final DynamicValueModifiedHandler handler = new DynamicValueModifiedHandler();
        handler.redistributeProfilesToDevice(deviceEvent);
    }
}
