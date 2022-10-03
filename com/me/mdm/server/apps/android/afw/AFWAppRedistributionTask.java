package com.me.mdm.server.apps.android.afw;

import java.util.Hashtable;
import java.util.Map;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import java.util.Collection;
import com.me.mdm.server.deployment.policy.AppDeploymentPolicyImpl;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class AFWAppRedistributionTask implements SchedulerExecutionInterface
{
    public Logger logger;
    
    public AFWAppRedistributionTask() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void executeTask(final Properties properties) {
        try {
            this.logger.log(Level.INFO, "[MANAGED_ACCOUNT_DELAY_REDISTRIBUTION] Inside AFWAccReadyAppRedistributionTask..");
            final Object requestDataObj = ((Hashtable<K, Object>)properties).get("REQUEST_DATA");
            JSONObject requestData = null;
            if (requestDataObj instanceof JSONObject) {
                requestData = (JSONObject)requestDataObj;
            }
            else {
                requestData = new JSONObject((String)requestDataObj);
            }
            final Long customerId = requestData.getLong("customerId");
            final Long resourceId = requestData.getLong("resourceId");
            final int presentDelayInProps = requestData.getInt("PRESENT_DELAY");
            final String taskName = requestData.getString("NEXT_TASK_NAME");
            int presentDelayInDB = -1;
            final String presentDelayInDBStr = CustomerParamsHandler.getInstance().getParameterValue("AFWAccAppRedistributionDelayTime", (long)customerId);
            if (presentDelayInDBStr != null) {
                presentDelayInDB = Integer.parseInt(presentDelayInDBStr);
            }
            if (presentDelayInDB == presentDelayInProps) {
                final List resourceList = new ArrayList();
                resourceList.add(resourceId);
                final Properties defaultAppSettings = AppsUtil.getInstance().getAppSettings(customerId);
                final boolean defaultSilentInstall = ((Hashtable<K, Boolean>)defaultAppSettings).get("isSilentInstall");
                final SelectQuery profileToResourceQuery = new GoogleAccountChangeHandler().getPortalAppsAssociatedWithResource(resourceId, 2);
                final DataObject profileToResourceDobj = MDMUtil.getPersistence().get(profileToResourceQuery);
                if (!profileToResourceDobj.isEmpty()) {
                    final Iterator<Row> profileIterator = profileToResourceDobj.getRows("RecentProfileForResource");
                    final Map<Long, Long> silentInstallProfileCollnMap = new HashMap<Long, Long>();
                    final List silentInstallCollectionList = new ArrayList();
                    final List silentInstallProfileList = new ArrayList();
                    while (profileIterator.hasNext()) {
                        final Row profileRow = profileIterator.next();
                        final Long profileID = (Long)profileRow.get("PROFILE_ID");
                        final Long collectionID = (Long)profileRow.get("COLLECTION_ID");
                        final JSONObject appDeploymentPolicy = new AppDeploymentPolicyImpl().getEffectiveDeploymentDataPolicy(resourceId, profileID);
                        boolean silentInstall = defaultSilentInstall;
                        if (appDeploymentPolicy != null && appDeploymentPolicy.has("PolicyDetails")) {
                            final JSONObject depConfigData = appDeploymentPolicy.getJSONObject("PolicyDetails");
                            silentInstall = depConfigData.getBoolean("FORCE_APP_INSTALL");
                        }
                        if (silentInstall) {
                            silentInstallProfileCollnMap.put(profileID, collectionID);
                            silentInstallCollectionList.add(collectionID);
                            silentInstallProfileList.add(profileID);
                        }
                    }
                    this.logger.log(Level.INFO, "[MANAGED_ACCOUNT_DELAY_REDISTRIBUTION]Waited {2} seconds. Initiating distribution {0} now with props {1}", new Object[] { taskName, properties, presentDelayInProps });
                    final JSONObject redistributionParams = new JSONObject();
                    redistributionParams.put("REDISTRIBUTION_TYPE", 1);
                    final JSONObject associateAppsToDevicesParams = new JSONObject();
                    associateAppsToDevicesParams.put("PROFILE_LIST", (Collection)silentInstallProfileList);
                    associateAppsToDevicesParams.put("COLLECTION_LIST", (Collection)silentInstallCollectionList);
                    associateAppsToDevicesParams.put("DEVICE_LIST", (Collection)resourceList);
                    associateAppsToDevicesParams.put("IS_SILENT_INSTALL", true);
                    associateAppsToDevicesParams.put("REDISTRIBUTION_PARAMS", (Object)redistributionParams);
                    associateAppsToDevicesParams.put("CUSTOMER_ID", (Object)customerId);
                    associateAppsToDevicesParams.put("AFW_ACCOUNT_READY_HANDLING", true);
                    new GoogleApiRetryHandler().associateAfwAppsToDevices(associateAppsToDevicesParams, silentInstallProfileCollnMap);
                }
                else {
                    this.logger.log(Level.INFO, "[MANAGED_ACCOUNT_DELAY_REDISTRIBUTION] No apps to silently distribute");
                }
            }
            else {
                this.logger.log(Level.SEVERE, "[MANAGED_ACCOUNT_DELAY_REDISTRIBUTION] As present delay in db and taskProps is not same, there might be some issue. So aborting the process");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "[MANAGED_ACCOUNT_DELAY_REDISTRIBUTION] Exception occurred in AFWAppRedistributionTask executeTask()", ex);
        }
    }
}
