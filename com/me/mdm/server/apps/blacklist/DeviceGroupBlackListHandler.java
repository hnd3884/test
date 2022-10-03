package com.me.mdm.server.apps.blacklist;

import java.util.Hashtable;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;

public class DeviceGroupBlackListHandler extends BaseBlacklistAppHandler
{
    private Logger logger;
    
    public DeviceGroupBlackListHandler() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    @Override
    public void blacklistAppInResource(final HashMap params) throws Exception {
        super.blacklistAppInResource(params);
        this.logger.log(Level.INFO, "Blacklisting for deviceGroup implementation called for associaition");
        ((Hashtable<String, Boolean>)this.associationParams).put("profileOrigin", true);
        List collectionList = new ArrayList(this.profileCollectionMap.values());
        Integer scope = params.get("scope");
        if (scope == null) {
            scope = 0;
        }
        this.updateResourcetoBlacklistAppStatus(this.resourceList, collectionList, 1, scope);
        final Boolean performBlacklist = Boolean.FALSE;
        final Boolean isNotify = params.get("isNotify") == null || params.get("isNotify");
        for (final int i : BlacklistAppHandler.platforms) {
            this.resourceList = this.platformMap.get("ResourceList" + i);
            this.profileCollectionMap = this.platformMap.get("ProfileCollection" + i);
            if (this.resourceList != null && this.profileCollectionMap != null) {
                collectionList = new ArrayList(this.profileCollectionMap.values());
                this.resourceList = this.updateResourcetoBlacklistAppStatus(this.resourceList, collectionList, 1, scope);
                if (isNotify) {
                    this.sendBlacklistMailToUserIfApplicable();
                }
            }
        }
        final JSONObject jsonObject = new BlacklistAppHandler().getBlackListAppSettings(this.customerID);
        final Long notificationType = (Long)jsonObject.get("BLACKLIST_ACTION_TYPE");
        if (((long)notificationType & 0x2L) != 0x0L) {
            new ProfileAssociateHandler().associateCollectionForGroup(this.associationParams);
        }
    }
    
    @Override
    public void removeBlacklistAppInResource(final HashMap params) throws Exception {
        super.removeBlacklistAppInResource(params);
        this.logger.log(Level.INFO, "Blacklisting for deviceGroup implementation called for dis associaition");
        ((Hashtable<String, Boolean>)this.associationParams).put("profileOrigin", true);
        List collectionList = new ArrayList(this.profileCollectionMap.values());
        Integer scope = params.get("scope");
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
        new ProfileAssociateHandler().disAssociateCollectionForGroup(this.associationParams);
    }
    
    @Override
    public SelectQuery getDeviceList(final List resourceList) {
        final SelectQuery selectQuery = super.getDeviceList(resourceList);
        final List resourcetypeList = new ArrayList();
        resourcetypeList.add(120);
        resourcetypeList.add(121);
        final List resList = MDMGroupHandler.getMemberIdListForGroups(resourceList, resourcetypeList);
        final Criteria criteria = selectQuery.getCriteria();
        final Criteria resCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resList.toArray(), 8);
        selectQuery.setCriteria(criteria.and(resCriteria));
        return selectQuery;
    }
}
