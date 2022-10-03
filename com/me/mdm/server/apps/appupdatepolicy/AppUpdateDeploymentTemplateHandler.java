package com.me.mdm.server.apps.appupdatepolicy;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;

public class AppUpdateDeploymentTemplateHandler
{
    private static AppUpdateDeploymentTemplateHandler appUpdateDeploymentTemplateHandler;
    
    public static AppUpdateDeploymentTemplateHandler getInstance() {
        if (AppUpdateDeploymentTemplateHandler.appUpdateDeploymentTemplateHandler == null) {
            AppUpdateDeploymentTemplateHandler.appUpdateDeploymentTemplateHandler = new AppUpdateDeploymentTemplateHandler();
        }
        return AppUpdateDeploymentTemplateHandler.appUpdateDeploymentTemplateHandler;
    }
    
    public void addAppUpdateDeploymentTemplate(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject dataObject) throws Exception {
        final Row row = new Row("AppUpdateDeploymentPolicy");
        row.set("IS_SILENT_INSTALL", (Object)appUpdatePolicyModel.getIsSilentInstall());
        row.set("IS_NOTIFY_USER", (Object)appUpdatePolicyModel.getIsNotifyUser());
        row.set("DEPLOYMENT_TEMPLATE_ID", appUpdatePolicyModel.getDeploymentTemplateId());
        dataObject.addRow(row);
    }
    
    public void getAppUpdateDeploymentTemplate(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject appUpdatePolicyDO) throws DataAccessException {
        final Row appUpdateDepPolicy = appUpdatePolicyDO.getFirstRow("AppUpdateDeploymentPolicy");
        appUpdatePolicyModel.setIsSilentInstall((Boolean)appUpdateDepPolicy.get("IS_SILENT_INSTALL"));
        appUpdatePolicyModel.setIsNotifyUser((Boolean)appUpdateDepPolicy.get("IS_NOTIFY_USER"));
    }
    
    static {
        AppUpdateDeploymentTemplateHandler.appUpdateDeploymentTemplateHandler = null;
    }
}
