package com.me.mdm.server.config;

import java.util.Hashtable;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.deployment.MDMResourceToProfileDeploymentConfigHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.core.MDMUserHandler;
import java.util.List;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class ProfileAssociateHandler
{
    private static ProfileAssociateHandler instance;
    private Logger profileDistributionLog;
    
    public ProfileAssociateHandler() {
        this.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
    }
    
    public static ProfileAssociateHandler getInstance() {
        return new ProfileAssociateHandler();
    }
    
    public void associateCollectionToMDMResource(final Properties properties) {
        this.profileDistributionLog.log(Level.INFO, "associateCollectionToMDMResource initiated with props {0}", new Object[] { properties });
        final int resourceType = ((Hashtable<K, Integer>)properties).get("resourceType");
        final ProfileAssociateDataHandler dataHandler = new ProfileAssociateDataHandler();
        final List configSourceList = ((Hashtable<K, List>)properties).get("configSourceList");
        switch (resourceType) {
            case 2: {
                final List userList = ((Hashtable<K, List>)properties).get("resourceList");
                if (configSourceList != null && configSourceList.isEmpty()) {
                    ((Hashtable<String, List>)properties).put("configSourceList", userList);
                }
                final HashMap<Integer, Set> userMap = new MDMUserHandler().getUserIdsBasedOnType(userList);
                ((Hashtable<String, Set>)properties).put("resourceList", userMap.get(1));
                dataHandler.associateProfileToManagedUser(properties);
                ((Hashtable<String, Set>)properties).put("resourceList", userMap.get(2));
                dataHandler.associateProfileToDirectoryUser(properties);
                break;
            }
        }
    }
    
    public void disassociateCollectionFromMDMResource(final Properties properties) {
        try {
            final int resourceType = ((Hashtable<K, Integer>)properties).get("resourceType");
            final List resourceList = ((Hashtable<K, List>)properties).get("resourceList");
            final ProfileAssociateDataHandler handler = new ProfileAssociateDataHandler();
            final HashMap<Long, Long> profileCollectionMap = ((Hashtable<K, HashMap<Long, Long>>)properties).get("profileCollectionMap");
            final List<Long> profileList = new ArrayList<Long>(profileCollectionMap.keySet());
            switch (resourceType) {
                case 2: {
                    final HashMap<Integer, Set> userMap = new MDMUserHandler().getUserIdsBasedOnType(resourceList);
                    ((Hashtable<String, Set>)properties).put("resourceList", userMap.get(1));
                    handler.disassociateProfileFromManagedUser(properties);
                    ((Hashtable<String, Set>)properties).put("resourceList", userMap.get(2));
                    handler.disassociateProfileFromDirectoryUser(properties);
                    break;
                }
            }
            new MDMResourceToProfileDeploymentConfigHandler().deleteMDMResourceToDeploymentConfig(resourceList, profileList);
        }
        catch (final DataAccessException ex) {
            this.profileDistributionLog.log(Level.SEVERE, "Exception in disassociateCollectionFromMDMResource", (Throwable)ex);
        }
    }
    
    public void updateMDMResourceProfileSummary() {
        com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().updateuserProfileSummary();
    }
    
    public Long getAssociatedByForProfile(final Long profileId, final Long resourceId) {
        final Criteria cr1 = new Criteria(Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria cr2 = new Criteria(Column.getColumn("RecentProfileForMDMResource", "PROFILE_ID"), (Object)profileId, 0);
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get("RecentProfileForMDMResource", cr1.and(cr2));
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("RecentProfileForMDMResource");
                return (Long)row.get("ASSOCIATED_BY");
            }
        }
        catch (final DataAccessException e) {
            this.profileDistributionLog.log(Level.SEVERE, "Exception in getAssociatedByForProfile", (Throwable)e);
        }
        return null;
    }
    
    static {
        ProfileAssociateHandler.instance = null;
    }
}
