package com.me.mdm.server.apps.appupdatepolicy;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;

public class AppUpdatePolicyDistributedCountHandler
{
    private static AppUpdatePolicyDistributedCountHandler appUpdatePolicyDistributedCountHandler;
    
    public static AppUpdatePolicyDistributedCountHandler getInstance() {
        if (AppUpdatePolicyDistributedCountHandler.appUpdatePolicyDistributedCountHandler == null) {
            AppUpdatePolicyDistributedCountHandler.appUpdatePolicyDistributedCountHandler = new AppUpdatePolicyDistributedCountHandler();
        }
        return AppUpdatePolicyDistributedCountHandler.appUpdatePolicyDistributedCountHandler;
    }
    
    public void getAppUpdatePolicyDistributedCount(final AppUpdatePolicyModel appUpdatePolicyModel) throws Exception {
        final Long profileId = appUpdatePolicyModel.getProfileId();
        final SelectQuery countQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria markedForDeleteCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
        countQuery.setCriteria(profileCriteria.and(markedForDeleteCriteria));
        appUpdatePolicyModel.setPolicyAssociatedGroupCount(MDMDBUtil.getCount(countQuery, "RecentProfileForGroup", "GROUP_ID"));
    }
    
    static {
        AppUpdatePolicyDistributedCountHandler.appUpdatePolicyDistributedCountHandler = null;
    }
}
