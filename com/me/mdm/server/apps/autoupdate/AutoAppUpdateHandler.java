package com.me.mdm.server.apps.autoupdate;

import java.util.Iterator;
import java.util.Collection;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.inv.AppDataHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.logging.Logger;

public class AutoAppUpdateHandler
{
    public static final String APP_UPDATE_CONFIGURATIONS = "app_update_configurations";
    public static final String GLOBAL_RESOURCES = "global_resources";
    public static final String DEPLOY_FOR_ALL_RESOURCES = "deploy_for_all_resources";
    public static final String EXCLUDED_RESOURCES = "excluded_resources";
    public static final String SPECIFIC_INCLUDED_RESOURCE = "specific_included_resources";
    public static final String DEPLOYMENT_CONFIG = "deployment_config";
    public static final String INCLUDED_RESOURCES = "include_resources";
    public static final String NEVER_UPDATE_RESOURCES = "never_update_resources";
    public static final String APP_IDS_ARRAY = "app_ids";
    public static final String ALL_RESOURCES_FLAG = "all_resources";
    public static final String SILENT_INSTALL = "silent_install";
    public static final String NOTIFY_USER_VIA_EMAIL = "notify_user_via_email";
    public static final String ALL_APPS_FLAG = "all_apps";
    public static final String RESOURCE_IDS_ARRAY = "resource_ids";
    public static final String EXCLUDE_RESOURCE_FLAG = "exclude_resource";
    public static final String EXCLUDE_APP_FLAG = "exclude_app";
    public static final String DESCRIPTION = "description";
    private Logger logger;
    private CommonQueueData commonQueueData;
    private static AutoAppUpdateHandler autoAppUpdateHandler;
    
    public AutoAppUpdateHandler() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    public static AutoAppUpdateHandler getInstance() {
        if (AutoAppUpdateHandler.autoAppUpdateHandler == null) {
            AutoAppUpdateHandler.autoAppUpdateHandler = new AutoAppUpdateHandler();
        }
        return AutoAppUpdateHandler.autoAppUpdateHandler;
    }
    
    private void addUpdateToQueue(final JSONObject appDataJson) {
        try {
            (this.commonQueueData = new CommonQueueData()).setEmptyJsonQueueData();
            this.commonQueueData.setClassName("com.me.mdm.server.apps.autoupdate.task.AutoAppUpdateTask");
            this.commonQueueData.setTaskName("AutoAppUpdateTask");
            this.commonQueueData.setCustomerId(appDataJson.getLong("customer_id"));
            this.commonQueueData.setJsonQueueData(appDataJson);
            CommonQueueUtil.getInstance().addToQueue(this.commonQueueData, CommonQueues.MDM_APP_MGMT);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Pushing app updates to queue failed", e);
        }
    }
    
    public void handleInitialAutoAppUpdate(final Long customerId) {
        this.logger.log(Level.INFO, "Pending set of auto app update initiated");
        final List<Long> appGroupList = new AppDataHandler().getStoreAppsWithUpdate(customerId);
        this.logger.log(Level.INFO, "Total no. of that have app update {0}", appGroupList.size());
        try {
            if (appGroupList != null && !appGroupList.isEmpty()) {
                final List<List> appGroupSubLists = MDMUtil.getInstance().splitListIntoSubLists(appGroupList, 5);
                final Iterator<List> iterator = (Iterator<List>)appGroupSubLists.iterator();
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("UpdateAllExistingApps")) {
                    while (iterator.hasNext()) {
                        final List<Long> appGroupSubList = iterator.next();
                        final JSONObject appObject = new JSONObject();
                        appObject.put("customer_id", (Object)customerId);
                        appObject.put("appGroupList", (Collection)appGroupSubList);
                        this.addUpdateToQueue(appObject);
                    }
                }
                else if (iterator.hasNext()) {
                    final List<Long> appGroupSubList = iterator.next();
                    this.logger.log(Level.INFO, "Picking top 5 for scaling as of now {0}", appGroupSubList);
                    final JSONObject appObject = new JSONObject();
                    appObject.put("customer_id", (Object)customerId);
                    appObject.put("appGroupList", (Collection)appGroupSubList);
                    this.addUpdateToQueue(appObject);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Could not initiate initial auto update push", e);
        }
    }
    
    public void handleAutoAppUpdate(final Long customerId, final List<Long> appGroupList) {
        try {
            if (appGroupList != null && !appGroupList.isEmpty()) {
                final JSONObject appObject = new JSONObject();
                appObject.put("customer_id", (Object)customerId);
                appObject.put("appGroupList", (Collection)appGroupList);
                this.addUpdateToQueue(appObject);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Could not initiate auto update push", e);
        }
    }
}
