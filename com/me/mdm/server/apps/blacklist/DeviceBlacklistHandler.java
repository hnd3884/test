package com.me.mdm.server.apps.blacklist;

import java.util.Hashtable;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;

public class DeviceBlacklistHandler extends BaseBlacklistAppHandler
{
    private Logger logger;
    
    public DeviceBlacklistHandler() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    @Override
    public void blacklistAppInResource(final HashMap params) throws Exception {
        super.blacklistAppInResource(params);
        this.logger.log(Level.INFO, "Blacklisting for device implementation called for associaition");
        ((Hashtable<String, Boolean>)this.associationParams).put("profileOrigin", false);
        List collectionList = new ArrayList(this.profileCollectionMap.values());
        Integer scope = params.get("scope");
        if (scope == null) {
            scope = 0;
        }
        for (final int i : BlacklistAppHandler.platforms) {
            this.resourceList = this.platformMap.get("ResourceList" + i);
            this.profileCollectionMap = this.platformMap.get("ProfileCollection" + i);
            if (this.resourceList != null && this.profileCollectionMap != null) {
                ((Hashtable<String, HashMap>)this.associationParams).put("profileCollectionMap", this.profileCollectionMap);
                collectionList = new ArrayList(this.profileCollectionMap.values());
                this.resourceList = this.updateResourcetoBlacklistAppStatus(this.resourceList, collectionList, 1, scope);
                if (this.sendBlacklistMailToUserIfApplicable()) {
                    new ProfileAssociateHandler().associateCollectionForResource(this.associationParams);
                }
            }
        }
    }
    
    @Override
    public void removeBlacklistAppInResource(final HashMap params) throws Exception {
        super.removeBlacklistAppInResource(params);
        this.logger.log(Level.INFO, "Blacklisting for device implementation called for dis associaition");
        ((Hashtable<String, Boolean>)this.associationParams).put("profileOrigin", false);
        List collectionList = new ArrayList(this.profileCollectionMap.values());
        Integer scope = params.get("scope");
        if (scope == null) {
            scope = 0;
        }
        final Boolean isGroupListner = params.get("isGroupListener");
        if (isGroupListner != null) {
            ((Hashtable<String, Boolean>)this.associationParams).put("isGroupListener", isGroupListner);
        }
        for (final int i : BlacklistAppHandler.platforms) {
            this.resourceList = this.platformMap.get("ResourceList" + i);
            this.profileCollectionMap = this.platformMap.get("ProfileCollection" + i);
            if (this.resourceList != null && this.profileCollectionMap != null) {
                ((Hashtable<String, HashMap>)this.associationParams).put("profileCollectionMap", this.profileCollectionMap);
                collectionList = new ArrayList(this.profileCollectionMap.values());
                this.updateResourcetoBlacklistAppStatus(this.resourceList, collectionList, 6, scope);
                new ProfileAssociateHandler().disAssociateCollectionForResource(this.associationParams);
            }
        }
    }
    
    @Override
    public SelectQuery getDeviceList(final List resourceList) {
        final SelectQuery selectQuery = super.getDeviceList(resourceList);
        final Criteria criteria = selectQuery.getCriteria();
        final Criteria resCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        selectQuery.setCriteria(criteria.and(resCriteria));
        return selectQuery;
    }
}
