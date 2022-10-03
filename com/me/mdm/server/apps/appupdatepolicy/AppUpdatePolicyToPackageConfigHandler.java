package com.me.mdm.server.apps.appupdatepolicy;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;

public class AppUpdatePolicyToPackageConfigHandler
{
    private static AppUpdatePolicyToPackageConfigHandler appUpdatePolicyToPackageConfigHandler;
    
    public static AppUpdatePolicyToPackageConfigHandler getInstance() {
        if (AppUpdatePolicyToPackageConfigHandler.appUpdatePolicyToPackageConfigHandler == null) {
            AppUpdatePolicyToPackageConfigHandler.appUpdatePolicyToPackageConfigHandler = new AppUpdatePolicyToPackageConfigHandler();
        }
        return AppUpdatePolicyToPackageConfigHandler.appUpdatePolicyToPackageConfigHandler;
    }
    
    public void addAppUpdateConfigToPackageConfigRelation(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject dataObject) throws Exception {
        final Row row = new Row("AutoAppUpdatePackageConfig");
        row.set("APP_UPDATE_CONF_ID", appUpdatePolicyModel.getAppUpdateConfigId());
        row.set("ALL_APPS", (Object)appUpdatePolicyModel.getIsAllApps());
        row.set("INCLUSION_FLAG", (Object)appUpdatePolicyModel.getInclusionFlag());
        dataObject.addRow(row);
    }
    
    public void getAppUpdateConfigToPackageRelation(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject appUpdatePolicyDO) throws DataAccessException {
        final Row row = appUpdatePolicyDO.getFirstRow("AutoAppUpdatePackageConfig");
        appUpdatePolicyModel.setIsAllApps((Boolean)row.get("ALL_APPS"));
        appUpdatePolicyModel.setInclusionFlag((Boolean)row.get("INCLUSION_FLAG"));
    }
    
    static {
        AppUpdatePolicyToPackageConfigHandler.appUpdatePolicyToPackageConfigHandler = null;
    }
}
