package com.me.mdm.server.apps.blacklist;

import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;

public class NetworkBlacklistHandler extends BaseBlacklistAppHandler
{
    private Logger logger;
    
    public NetworkBlacklistHandler() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    @Override
    public void blacklistAppInResource(final HashMap params) throws Exception {
        super.blacklistAppInResource(params);
        this.logger.log(Level.INFO, "Blacklisting for network implementation called for association");
        ((Hashtable<String, Boolean>)this.associationParams).put("profileOrigin", false);
        Integer scope = params.get("scope");
        if (scope == null) {
            scope = 0;
        }
        for (final int i : BlacklistAppHandler.platforms) {
            this.resourceList = this.platformMap.get("ResourceList" + i);
            this.profileCollectionMap = this.platformMap.get("ProfileCollection" + i);
            if (this.profileCollectionMap != null) {
                final Long userID = ((Hashtable<K, Long>)this.associationParams).get("loggedOnUser");
                final List collectionList = new ArrayList(this.profileCollectionMap.values());
                if (this.resourceList != null) {
                    ((Hashtable<String, List>)this.associationParams).put("resourceList", this.resourceList);
                    this.resourceList = this.updateResourcetoBlacklistAppStatus(this.resourceList, collectionList, 1, scope);
                    if (this.sendBlacklistMailToUserIfApplicable()) {
                        new ProfileAssociateHandler().associateCollectionForResource(this.associationParams);
                    }
                }
                this.updateBlacklistGlobalSetting(collectionList, true, userID);
                this.logger.log(Level.INFO, "Blacklisting for network implementation called for associaition platform {0} association params : {1}", new Object[] { i, this.associationParams });
            }
        }
        params.put("isNotify", Boolean.FALSE);
        params.put("resourceIDs", this.resourceList = this.getAllGroupList(this.customerID, 6, true));
        new DeviceGroupBlackListHandler().blacklistAppInResource(params);
        params.put("resourceIDs", this.resourceList = this.getAllGroupList(this.customerID, 7, true));
        new UserGroupBlackListHandler().blacklistAppInResource(params);
    }
    
    @Override
    public void removeBlacklistAppInResource(final HashMap params) throws Exception {
        super.removeBlacklistAppInResource(params);
        this.logger.log(Level.INFO, "Whitelisting for network implementation called for associaition");
        ((Hashtable<String, Boolean>)this.associationParams).put("profileOrigin", false);
        params.put("resourceIDs", this.resourceList = this.getAllGroupList(this.customerID, 6, false));
        new DeviceGroupBlackListHandler().removeBlacklistAppInResource(params);
        params.put("resourceIDs", this.resourceList = this.getAllGroupList(this.customerID, 7, false));
        new UserGroupBlackListHandler().removeBlacklistAppInResource(params);
        final Long userID = ((Hashtable<K, Long>)this.associationParams).get("loggedOnUser");
        boolean emptyResource = Boolean.TRUE;
        for (final int i : BlacklistAppHandler.platforms) {
            this.resourceList = this.platformMap.get("ResourceList" + i);
            this.profileCollectionMap = this.platformMap.get("ProfileCollection" + i);
            if (this.profileCollectionMap != null) {
                final List collectionList = new ArrayList(this.profileCollectionMap.values());
                if (this.resourceList != null) {
                    ((Hashtable<String, List>)this.associationParams).put("resourceList", this.resourceList);
                    Integer scope = params.get("scope");
                    if (scope == null) {
                        scope = 0;
                    }
                    this.updateResourcetoBlacklistAppStatus(this.resourceList, collectionList, 6, scope);
                    new ProfileAssociateHandler().disAssociateCollectionForResource(this.associationParams);
                }
                this.updateBlacklistGlobalSetting(collectionList, false, userID);
                emptyResource = Boolean.FALSE;
                this.logger.log(Level.INFO, "App Whitelisting for network implementation called platform {0} association params : {1}", new Object[] { i, this.associationParams });
            }
        }
        if (emptyResource) {
            final List appGroupList = params.get("appIDs");
            this.updateWhitelistGlobalSettingwithAppGroup(appGroupList, userID);
        }
    }
    
    @Override
    public SelectQuery getDeviceList(final List resourceList) {
        final SelectQuery selectQuery = super.getDeviceList(resourceList);
        final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
        selectQuery2.addJoin(new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery2.setCriteria(new Criteria(new Column("CustomGroup", "GROUP_TYPE"), (Object)8, 1));
        selectQuery2.addSelectColumn(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
        selectQuery2.addSelectColumn(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
        final DerivedTable derivedTable = new DerivedTable("UserTable", (Query)selectQuery2);
        final Table baseTable = new Table("ManagedDevice");
        selectQuery.addJoin(new Join(baseTable, (Table)derivedTable, new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
        final Criteria criteria = selectQuery.getCriteria();
        final Criteria notGroupCriteria = new Criteria(Column.getColumn(derivedTable.getTableAlias(), "GROUP_RESOURCE_ID"), (Object)null, 0);
        selectQuery.setCriteria(criteria.and(notGroupCriteria));
        return selectQuery;
    }
    
    private void updateBlacklistGlobalSetting(final List collectionList, final Boolean flag, final Long userID) throws DataAccessException {
        UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("BlacklistAppToCollection");
        updateQuery.setCriteria(new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)collectionList.toArray(), 8));
        updateQuery.setUpdateColumn("GLOBAL_BLACKLIST", (Object)flag);
        MDMUtil.getPersistence().update(updateQuery);
        updateQuery = (UpdateQuery)new UpdateQueryImpl("Profile");
        updateQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        updateQuery.setCriteria(new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionList.toArray(), 8));
        updateQuery.setUpdateColumn("LAST_MODIFIED_BY", (Object)userID);
        MDMUtil.getPersistence().update(updateQuery);
    }
    
    private void updateWhitelistGlobalSettingwithAppGroup(final List appGroupList, final Long userID) throws DataAccessException {
        UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("BlacklistAppToCollection");
        updateQuery.setCriteria(new Criteria(Column.getColumn("BlacklistAppToCollection", "APP_GROUP_ID"), (Object)appGroupList.toArray(), 8));
        updateQuery.setUpdateColumn("GLOBAL_BLACKLIST", (Object)Boolean.FALSE);
        updateQuery.setUpdateColumn("APPLIED_STATUS", (Object)Boolean.FALSE);
        MDMUtil.getPersistence().update(updateQuery);
        updateQuery = (UpdateQuery)new UpdateQueryImpl("Profile");
        updateQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        updateQuery.addJoin(new Join("ProfileToCollection", "BlacklistAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        updateQuery.setCriteria(new Criteria(Column.getColumn("BlacklistAppToCollection", "APP_GROUP_ID"), (Object)appGroupList.toArray(), 8));
        updateQuery.setUpdateColumn("LAST_MODIFIED_BY", (Object)userID);
        MDMUtil.getPersistence().update(updateQuery);
    }
    
    private List getAllGroupList(final Long customerID, final int grpType, final boolean isAssociate) throws DataAccessException {
        final List resList = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CustomGroup"));
        selectQuery.addJoin(new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        Criteria criteria = null;
        if (!isAssociate) {
            selectQuery.addJoin(new Join("CustomGroup", "BlacklistAppCollectionStatus", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            criteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"), (Object)new ArrayList(this.profileCollectionMap.values()).toArray(), 8);
        }
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria grpCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)grpType, 0);
        if (isAssociate) {
            selectQuery.setCriteria(customerCriteria.and(grpCriteria));
        }
        else {
            selectQuery.setCriteria(customerCriteria.and(grpCriteria).and(criteria));
        }
        selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final Iterator iterable = dataObject.getRows("CustomGroup");
        while (iterable.hasNext()) {
            final Row row = iterable.next();
            final Long groupID = (Long)row.get("RESOURCE_ID");
            if (!resList.contains(groupID)) {
                resList.add(groupID);
            }
        }
        return resList;
    }
}
