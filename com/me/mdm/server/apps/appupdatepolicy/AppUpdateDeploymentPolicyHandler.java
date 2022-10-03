package com.me.mdm.server.apps.appupdatepolicy;

import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.device.api.model.schedule.SchedulerModel;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;

public class AppUpdateDeploymentPolicyHandler
{
    private static AppUpdateDeploymentPolicyHandler appUpdateDeploymentPolicyHandler;
    
    public static AppUpdateDeploymentPolicyHandler getInstance() {
        if (AppUpdateDeploymentPolicyHandler.appUpdateDeploymentPolicyHandler == null) {
            AppUpdateDeploymentPolicyHandler.appUpdateDeploymentPolicyHandler = new AppUpdateDeploymentPolicyHandler();
        }
        return AppUpdateDeploymentPolicyHandler.appUpdateDeploymentPolicyHandler;
    }
    
    public void addOrUpdateAppUpdateDeploymentPolicy(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject dataObject) throws Exception {
        final Boolean isSilentInstall = appUpdatePolicyModel.getIsSilentInstall();
        final Boolean isNotifyUser = appUpdatePolicyModel.getIsNotifyUser();
        final SchedulerModel schedulerModel = appUpdatePolicyModel.getSchedulerModel();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdmDeploymentTemplate"));
        selectQuery.addJoin(new Join("MdmDeploymentTemplate", "DeploymentWindowTemplate", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1));
        selectQuery.addJoin(new Join("MdmDeploymentTemplate", "AppUpdateDeploymentPolicy", new String[] { "DEPLOYMENT_TEMPLATE_ID" }, new String[] { "DEPLOYMENT_TEMPLATE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("MdmDeploymentTemplate", "DEPLOYMENT_TEMPLATE_ID"));
        final Criteria appDeploymentTemplateCriteria = new Criteria(Column.getColumn("AppUpdateDeploymentPolicy", "IS_NOTIFY_USER"), (Object)isNotifyUser, 0).and(new Criteria(Column.getColumn("AppUpdateDeploymentPolicy", "IS_SILENT_INSTALL"), (Object)isSilentInstall, 0));
        Criteria deploymentWindowCriteria;
        if (schedulerModel != null) {
            final String windowStartTime = appUpdatePolicyModel.getWindowStartTime();
            final String windowEndTIme = appUpdatePolicyModel.getWindowEndTime();
            final String dayOfWeek = schedulerModel.getDayOfWeek();
            final String weekOfMonth = schedulerModel.getWeekOfMonth();
            final String timeZone = appUpdatePolicyModel.getTimeZone();
            final Criteria windowStartTimeCriteria = new Criteria(Column.getColumn("DeploymentWindowTemplate", "WINDOW_START_TIME"), (Object)windowStartTime, 0);
            final Criteria windowEndTimeCriteria = new Criteria(Column.getColumn("DeploymentWindowTemplate", "WINDOW_END_TIME"), (Object)windowEndTIme, 0);
            final Criteria dayOfWeekCriteria = new Criteria(Column.getColumn("DeploymentWindowTemplate", "WINDOW_DAY_OF_WEEK"), (Object)dayOfWeek, 0);
            final Criteria weekOfMonthCriteria = new Criteria(Column.getColumn("DeploymentWindowTemplate", "WINDOW_WEEK_OF_MONTH"), (Object)weekOfMonth, 0);
            final Criteria timeZoneCriteria = new Criteria(Column.getColumn("DeploymentWindowTemplate", "TIME_ZONE"), (Object)timeZone, 0);
            deploymentWindowCriteria = windowStartTimeCriteria.and(windowEndTimeCriteria).and(dayOfWeekCriteria).and(weekOfMonthCriteria).and(timeZoneCriteria);
        }
        else {
            deploymentWindowCriteria = new Criteria(Column.getColumn("DeploymentWindowTemplate", "DEPLOYMENT_TEMPLATE_ID"), (Object)null, 0);
        }
        selectQuery.setCriteria(appDeploymentTemplateCriteria.and(deploymentWindowCriteria));
        final DataObject existingDO = DataAccess.get(selectQuery);
        if (existingDO.isEmpty()) {
            MDMAppDeploymentTemplateHandler.getInstance().addMDMAppDeploymentTemplate(appUpdatePolicyModel, dataObject);
            AppUpdateDeploymentTemplateHandler.getInstance().addAppUpdateDeploymentTemplate(appUpdatePolicyModel, dataObject);
            DeploymentWindowTemplateHandler.getInstance().addDeploymentWindowTemplateHandler(appUpdatePolicyModel, dataObject);
        }
        else {
            appUpdatePolicyModel.setDeploymentTemplateId(existingDO.getFirstValue("MdmDeploymentTemplate", "DEPLOYMENT_TEMPLATE_ID"));
        }
    }
    
    public void setAppUpdateDeploymentPolicy(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject appUpdatePolicyDO) throws Exception {
        AppUpdateDeploymentTemplateHandler.getInstance().getAppUpdateDeploymentTemplate(appUpdatePolicyModel, appUpdatePolicyDO);
        DeploymentWindowTemplateHandler.getInstance().getDeploymentWindow(appUpdatePolicyModel, appUpdatePolicyDO);
    }
    
    static {
        AppUpdateDeploymentPolicyHandler.appUpdateDeploymentPolicyHandler = null;
    }
}
