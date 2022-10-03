package com.me.mdm.server.apps.appupdatepolicy;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;

public class AppUpdatePolicyCollnToScheduleRepoHandler
{
    private static AppUpdatePolicyCollnToScheduleRepoHandler appUpdatePolicyCollnToScheduleRepoHandler;
    
    public static AppUpdatePolicyCollnToScheduleRepoHandler getInstance() {
        if (AppUpdatePolicyCollnToScheduleRepoHandler.appUpdatePolicyCollnToScheduleRepoHandler == null) {
            AppUpdatePolicyCollnToScheduleRepoHandler.appUpdatePolicyCollnToScheduleRepoHandler = new AppUpdatePolicyCollnToScheduleRepoHandler();
        }
        return AppUpdatePolicyCollnToScheduleRepoHandler.appUpdatePolicyCollnToScheduleRepoHandler;
    }
    
    public void addAppUpdatePolicyCollnToScheduleRepoRelation(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject dataObject) throws DataAccessException {
        if (appUpdatePolicyModel.getScheduleId() != null) {
            final Row row = new Row("AppUpdatePolicyCollnToScheduleRepo");
            row.set("SCHEDULE_ID", (Object)appUpdatePolicyModel.getScheduleId());
            row.set("COLLECTION_ID", (Object)appUpdatePolicyModel.getCollectionId());
            dataObject.addRow(row);
        }
    }
    
    static {
        AppUpdatePolicyCollnToScheduleRepoHandler.appUpdatePolicyCollnToScheduleRepoHandler = null;
    }
}
