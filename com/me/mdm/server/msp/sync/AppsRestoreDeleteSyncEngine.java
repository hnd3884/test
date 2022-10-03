package com.me.mdm.server.msp.sync;

import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.List;

public abstract class AppsRestoreDeleteSyncEngine extends BaseConfigurationsSyncEngine
{
    List appIdentifiers;
    
    AppsRestoreDeleteSyncEngine(final DCQueueData dcQueueData) {
        super(dcQueueData);
        this.appIdentifiers = this.qData.getJSONArray("appIds").toList();
    }
    
    @Override
    public JSONObject getChildSpecificUVH(final Long customerId) throws Exception {
        final List<Long> profileIds = new ArrayList<Long>();
        final List<Long> packageIds = new ArrayList<Long>();
        final JSONObject response = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria identifierCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)this.appIdentifiers.toArray(), 8);
        selectQuery.setCriteria(customerCriteria.and(identifierCriteria.and(AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria())));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (dmDataSetWrapper.next()) {
            final Long profileId = (Long)dmDataSetWrapper.getValue("PROFILE_ID");
            final Long packageId = (Long)dmDataSetWrapper.getValue("PACKAGE_ID");
            if (!profileIds.contains(profileId)) {
                profileIds.add(profileId);
            }
            if (!packageIds.contains(packageId)) {
                packageIds.add(packageId);
            }
        }
        response.put("profile_ids", (Collection)profileIds);
        response.put("app_ids", (Collection)packageIds);
        return response;
    }
    
    @Override
    public abstract void setParentDO() throws Exception;
    
    @Override
    public abstract void sync();
}
