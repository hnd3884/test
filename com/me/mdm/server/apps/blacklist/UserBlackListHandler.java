package com.me.mdm.server.apps.blacklist;

import java.util.Hashtable;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONObject;
import com.me.mdm.server.config.ProfileAssociateHandler;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class UserBlackListHandler extends BaseBlacklistAppHandler
{
    private Logger logger;
    
    public UserBlackListHandler() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    @Override
    public void blacklistAppInResource(final HashMap params) throws Exception {
        super.blacklistAppInResource(params);
        ((Hashtable<String, Boolean>)this.associationParams).put("profileOrigin", true);
        List collectionList = new ArrayList(this.profileCollectionMap.values());
        Integer scope = params.get("scope");
        if (scope == null) {
            scope = 0;
        }
        this.updateResourcetoBlacklistAppStatus(this.resourceList, collectionList, 1, scope);
        for (final int i : BlacklistAppHandler.platforms) {
            this.resourceList = this.platformMap.get("ResourceList" + i);
            this.profileCollectionMap = this.platformMap.get("ProfileCollection" + i);
            if (this.resourceList != null && this.profileCollectionMap != null) {
                collectionList = new ArrayList(this.profileCollectionMap.values());
                this.resourceList = this.updateResourcetoBlacklistAppStatus(this.resourceList, collectionList, 1, scope);
                this.sendBlacklistMailToUserIfApplicable();
            }
        }
        final JSONObject jsonObject = new BlacklistAppHandler().getBlackListAppSettings(this.customerID);
        final Long notificationType = (Long)jsonObject.get("BLACKLIST_ACTION_TYPE");
        if (((long)notificationType & 0x2L) != 0x0L) {
            ((Hashtable<String, Integer>)this.associationParams).put("resourceType", 2);
            ((Hashtable<String, List>)this.associationParams).put("configSourceList", this.resourceList);
            ProfileAssociateHandler.getInstance().associateCollectionToMDMResource(this.associationParams);
        }
    }
    
    @Override
    public void removeBlacklistAppInResource(final HashMap params) throws Exception {
        super.removeBlacklistAppInResource(params);
        ((Hashtable<String, Boolean>)this.associationParams).put("profileOrigin", false);
        List collectionList = new ArrayList(this.profileCollectionMap.values());
        Integer scope = params.get("scope");
        final Integer profileOriginInt = params.get("profileOriginInt");
        if (profileOriginInt != null) {
            ((Hashtable<String, Integer>)this.associationParams).put("profileOriginInt", profileOriginInt);
        }
        if (scope == null) {
            scope = 0;
        }
        this.updateResourcetoBlacklistAppStatus(this.resourceList, collectionList, 6, scope);
        for (final int i : BlacklistAppHandler.platforms) {
            this.resourceList = this.platformMap.get("ResourceList" + i);
            this.profileCollectionMap = this.platformMap.get("ProfileCollection" + i);
            if (this.resourceList != null && this.profileCollectionMap != null) {
                collectionList = new ArrayList(this.profileCollectionMap.values());
                this.updateResourcetoBlacklistAppStatus(this.resourceList, collectionList, 6, scope);
            }
        }
        ((Hashtable<String, Integer>)this.associationParams).put("resourceType", 2);
        ((Hashtable<String, List>)this.associationParams).put("configSourceList", this.resourceList);
        ProfileAssociateHandler.getInstance().disassociateCollectionFromMDMResource(this.associationParams);
    }
    
    @Override
    public SelectQuery getDeviceList(final List resourceList) {
        final SelectQuery selectQuery = super.getDeviceList(resourceList);
        final List resList = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(resourceList);
        final Criteria criteria = selectQuery.getCriteria();
        final Criteria resCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resList.toArray(), 8);
        selectQuery.setCriteria(criteria.and(resCriteria));
        return selectQuery;
    }
}
