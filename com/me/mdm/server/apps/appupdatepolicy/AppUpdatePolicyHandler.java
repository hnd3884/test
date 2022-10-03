package com.me.mdm.server.apps.appupdatepolicy;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;

public class AppUpdatePolicyHandler
{
    private static AppUpdatePolicyHandler appUpdatePolicyHandler;
    
    public static AppUpdatePolicyHandler getInstance() {
        if (AppUpdatePolicyHandler.appUpdatePolicyHandler == null) {
            AppUpdatePolicyHandler.appUpdatePolicyHandler = new AppUpdatePolicyHandler();
        }
        return AppUpdatePolicyHandler.appUpdatePolicyHandler;
    }
    
    public void addAppUpdatePolicyConfiguration(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject dataObject) throws Exception {
        final Row row = new Row("AutoAppUpdateConfigDetails");
        row.set("CREATION_TIME", (Object)System.currentTimeMillis());
        row.set("CUSTOMER_ID", (Object)appUpdatePolicyModel.getCustomerId());
        row.set("DESCRIPTION", (Object)appUpdatePolicyModel.getDescription());
        row.set("DISTRIBUTION_TYPE", (Object)appUpdatePolicyModel.getDistributionType());
        row.set("LAST_MODIFIED_BY", (Object)appUpdatePolicyModel.getUserId());
        row.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
        row.set("POLICY_TYPE", (Object)appUpdatePolicyModel.getPolicyType());
        dataObject.addRow(row);
        appUpdatePolicyModel.setAppUpdateConfigId(dataObject.getFirstValue("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"));
    }
    
    public void getAppUpdatePolicyConfiguration(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject dataObject) throws Exception {
        final Row row = dataObject.getFirstRow("AutoAppUpdateConfigDetails");
        appUpdatePolicyModel.setDistributionType((int)row.get("DISTRIBUTION_TYPE"));
        appUpdatePolicyModel.setPolicyType((int)row.get("POLICY_TYPE"));
    }
    
    static {
        AppUpdatePolicyHandler.appUpdatePolicyHandler = null;
    }
}
