package com.me.mdm.server.apps.appupdatepolicy;

import com.me.mdm.server.device.api.model.schedule.SchedulerModel;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;

public class DeploymentWindowTemplateHandler
{
    private static DeploymentWindowTemplateHandler deploymentWindowTemplateHandler;
    
    public static DeploymentWindowTemplateHandler getInstance() {
        if (DeploymentWindowTemplateHandler.deploymentWindowTemplateHandler == null) {
            DeploymentWindowTemplateHandler.deploymentWindowTemplateHandler = new DeploymentWindowTemplateHandler();
        }
        return DeploymentWindowTemplateHandler.deploymentWindowTemplateHandler;
    }
    
    public void addDeploymentWindowTemplateHandler(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject dataObject) throws DataAccessException {
        if (appUpdatePolicyModel.getSchedulerModel() != null) {
            final Row row = new Row("DeploymentWindowTemplate");
            row.set("DEPLOYMENT_TEMPLATE_ID", appUpdatePolicyModel.getDeploymentTemplateId());
            row.set("WINDOW_WEEK_OF_MONTH", (Object)appUpdatePolicyModel.getSchedulerModel().getWeekOfMonth());
            row.set("WINDOW_DAY_OF_WEEK", (Object)appUpdatePolicyModel.getSchedulerModel().getDayOfWeek());
            row.set("WINDOW_START_TIME", (Object)appUpdatePolicyModel.getWindowStartTime());
            row.set("WINDOW_END_TIME", (Object)appUpdatePolicyModel.getWindowEndTime());
            row.set("TIME_ZONE", (Object)appUpdatePolicyModel.getTimeZone());
            dataObject.addRow(row);
        }
    }
    
    public void getDeploymentWindow(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject appUpdatePolicyDO) throws DataAccessException {
        final Row appDeploymentWindowRow = appUpdatePolicyDO.getRow("DeploymentWindowTemplate");
        if (appDeploymentWindowRow != null) {
            appUpdatePolicyModel.setWindowStartTime(String.valueOf(appDeploymentWindowRow.get("WINDOW_START_TIME")));
            appUpdatePolicyModel.setWindowEndTime(String.valueOf(appDeploymentWindowRow.get("WINDOW_END_TIME")));
            appUpdatePolicyModel.setTimeZone((String)appDeploymentWindowRow.get("TIME_ZONE"));
            SchedulerModel schedulerModel = appUpdatePolicyModel.getSchedulerModel();
            if (schedulerModel == null) {
                schedulerModel = new SchedulerModel();
            }
            schedulerModel.setDayOfWeek((String)appDeploymentWindowRow.get("WINDOW_DAY_OF_WEEK"));
            appUpdatePolicyModel.setSchedulerModel(schedulerModel);
        }
    }
    
    static {
        DeploymentWindowTemplateHandler.deploymentWindowTemplateHandler = null;
    }
}
