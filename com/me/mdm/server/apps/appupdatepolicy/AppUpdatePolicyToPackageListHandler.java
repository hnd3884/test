package com.me.mdm.server.apps.appupdatepolicy;

import java.util.ArrayList;
import com.me.mdm.server.device.api.model.AppDetailsModel;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;

public class AppUpdatePolicyToPackageListHandler
{
    private static AppUpdatePolicyToPackageListHandler appUpdatePolicyToPackageListHandler;
    
    public static AppUpdatePolicyToPackageListHandler getInstance() {
        if (AppUpdatePolicyToPackageListHandler.appUpdatePolicyToPackageListHandler == null) {
            AppUpdatePolicyToPackageListHandler.appUpdatePolicyToPackageListHandler = new AppUpdatePolicyToPackageListHandler();
        }
        return AppUpdatePolicyToPackageListHandler.appUpdatePolicyToPackageListHandler;
    }
    
    public void addAppUpdatePolicyToPackageListRelation(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject dataObject) throws Exception {
        final List<Long> packageList = appUpdatePolicyModel.getPackageList();
        if (packageList != null) {
            final Iterator<Long> iterator = packageList.iterator();
            while (iterator.hasNext()) {
                final Row row = new Row("AutoAppUpdatePackageList");
                row.set("APP_UPDATE_CONF_ID", appUpdatePolicyModel.getAppUpdateConfigId());
                row.set("PACKAGE_ID", (Object)iterator.next());
                dataObject.addRow(row);
            }
        }
    }
    
    public void setAppUpdatePolicyPackageList(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject appUpdatePolicyDO) throws Exception {
        final Iterator<Row> iterator = appUpdatePolicyDO.getRows("MdPackageToAppGroup");
        List<AppDetailsModel> appDetailsModelList = null;
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long packageId = (Long)row.get("PACKAGE_ID");
            final Long appGroupId = (Long)row.get("APP_GROUP_ID");
            final String appName = (String)appUpdatePolicyDO.getValue("MdAppGroupDetails", "GROUP_DISPLAY_NAME", new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0));
            final int platform = (int)appUpdatePolicyDO.getValue("MdAppGroupDetails", "PLATFORM_TYPE", new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0));
            final AppDetailsModel appDetailsModel = new AppDetailsModel();
            appDetailsModel.setAppId(packageId);
            appDetailsModel.setAppName(appName);
            appDetailsModel.setPlatformType(platform);
            if (appDetailsModelList == null) {
                appDetailsModelList = new ArrayList<AppDetailsModel>();
            }
            appDetailsModelList.add(appDetailsModel);
        }
        appUpdatePolicyModel.setAppDetailsModels(appDetailsModelList);
    }
    
    static {
        AppUpdatePolicyToPackageListHandler.appUpdatePolicyToPackageListHandler = null;
    }
}
