package com.me.mdm.server.apps.android.afw;

import java.util.Hashtable;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.Map;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.util.HashMap;
import java.util.Collection;
import com.me.mdm.server.deployment.policy.AppDeploymentPolicyImpl;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.deployment.DeploymentBean;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import java.util.List;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class AFWNewlyApprovedAppsRedistributionTask implements SchedulerExecutionInterface, CommonQueueProcessorInterface
{
    public Logger logger;
    
    public AFWNewlyApprovedAppsRedistributionTask() {
        this.logger = Logger.getLogger("MDMBStoreLogger");
    }
    
    public void executeTask(final Properties properties) {
        try {
            this.logger.log(Level.INFO, "Inside AFWNewlyApprovedAppsRedistributionTask..");
            final Object requestDataObj = ((Hashtable<K, Object>)properties).get("REQUEST_DATA");
            JSONObject requestData = null;
            if (requestDataObj instanceof JSONObject) {
                requestData = (JSONObject)requestDataObj;
            }
            else {
                requestData = new JSONObject((String)requestDataObj);
            }
            final Long customerId = requestData.optLong("customerId");
            final JSONArray newlyApprovedAppsDetails = requestData.optJSONArray("newlyApprovedAppDetails");
            final List<Long> newlyApprovedAppGroupIds = JSONUtil.getInstance().convertLongJSONArrayTOList(requestData.optJSONArray("newlyApprovedAppGroupIds"));
            this.distributeNewlyApprovedApps(customerId, newlyApprovedAppsDetails, newlyApprovedAppGroupIds);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred in AFWAppRedistributionTask executeTask()", ex);
        }
    }
    
    private void distributeNewlyApprovedApps(final Long customerId, final JSONArray newlyApprovedAppsDetails, final List<Long> newlyApprovedAppGroupIds) throws Exception {
        final Properties newProps = new Properties();
        ((Hashtable<String, Boolean>)newProps).put("isAppConfig", true);
        ((Hashtable<String, Boolean>)newProps).put("isAppUpgrade", false);
        ((Hashtable<String, Boolean>)newProps).put("AFWAccountReadyHandling", false);
        ((Hashtable<String, String>)newProps).put("commandName", "InstallApplication");
        ((Hashtable<String, Integer>)newProps).put("commandType", 1);
        ((Hashtable<String, Integer>)newProps).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_USER);
        final List<DeploymentBean> silentResourceDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final List<DeploymentBean> nonSilentResourceDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final HashMap appsToDevices = new AppsUtil().getDevicesAssociatedWithApps(newlyApprovedAppGroupIds, customerId, 2);
        for (int i = 0; i < newlyApprovedAppsDetails.length(); ++i) {
            final JSONObject newlyApprovedApp = newlyApprovedAppsDetails.getJSONObject(i);
            final Long appGroupId = newlyApprovedApp.optLong("APP_GROUP_ID");
            final Long profileId = newlyApprovedApp.optLong("PROFILE_ID");
            final Long collectionId = newlyApprovedApp.optLong("COLLECTION_ID");
            if (!appsToDevices.containsKey(appGroupId)) {
                this.logger.log(Level.SEVERE, "Newly approved app {0} is not distributed to any device", appGroupId);
            }
            else {
                final List totalDevicesList = appsToDevices.get(appGroupId);
                final List<Long> silentInstallDevicesList = new AppDeploymentPolicyImpl().getSilentInstallDeployedResources(totalDevicesList, profileId, null);
                final List<Long> nonSilentInstallDevicesList = new ArrayList<Long>(totalDevicesList);
                nonSilentInstallDevicesList.removeAll(silentInstallDevicesList);
                final Map<Long, Long> profileCollnMap = new HashMap<Long, Long>();
                profileCollnMap.put(profileId, collectionId);
                final DeploymentBean silentResourceDeploymentBean = new DeploymentBean();
                silentResourceDeploymentBean.setIncludedResourceList((ArrayList)silentInstallDevicesList);
                silentResourceDeploymentBean.setCustomerId(customerId);
                silentResourceDeploymentBean.setSilentInstall(true);
                silentResourceDeploymentBean.setNotifyUserViaEmail(false);
                final DeploymentBean nonSilentResourceDeploymentBean = new DeploymentBean();
                nonSilentResourceDeploymentBean.setIncludedResourceList((ArrayList)nonSilentInstallDevicesList);
                nonSilentResourceDeploymentBean.setCustomerId(customerId);
                nonSilentResourceDeploymentBean.setSilentInstall(false);
                nonSilentResourceDeploymentBean.setNotifyUserViaEmail(false);
                final Properties appObject = new Properties();
                ((Hashtable<String, Long>)appObject).put("profileId", profileId);
                ((Hashtable<String, Long>)appObject).put("collectionId", collectionId);
                ((Hashtable<String, Map<Long, Long>>)appObject).put("profileCollectionMap", profileCollnMap);
                MDMAppMgmtHandler.getInstance().putDeploymentBean(silentResourceDeploymentBeanSet, silentResourceDeploymentBean, appGroupId, (HashMap)profileCollnMap, appObject);
                MDMAppMgmtHandler.getInstance().putDeploymentBean(nonSilentResourceDeploymentBeanSet, nonSilentResourceDeploymentBean, appGroupId, (HashMap)profileCollnMap, appObject);
            }
        }
        MDMAppMgmtHandler.getInstance().bulkDistributeAppsToDevices(silentResourceDeploymentBeanSet, newProps, 2);
        MDMAppMgmtHandler.getInstance().bulkDistributeAppsToDevices(nonSilentResourceDeploymentBeanSet, newProps, 2);
    }
    
    public void processData(final CommonQueueData data) {
        final Long customerId = data.getCustomerId();
        final JSONObject queueData = data.getJsonQueueData();
        final JSONArray identifiers = queueData.optJSONArray("newIdentifiers");
        final JSONArray newlyApprovedAppsDetails = new JSONArray();
        final List<Long> newlyApprovedAppGroupIds = new ArrayList<Long>();
        if (identifiers != null) {
            try {
                for (int index = 0; index < identifiers.length(); ++index) {
                    final Criteria portalAppCriteria = new Criteria(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
                    final JSONObject appObject = AppsUtil.getInstance().getAppIDAndLabelForGivenIdentifier((String)identifiers.get(index), 2, customerId, portalAppCriteria);
                    if (appObject.length() > 0) {
                        newlyApprovedAppsDetails.put((Object)appObject);
                        newlyApprovedAppGroupIds.add((Long)appObject.get("APP_GROUP_ID"));
                    }
                }
                if (newlyApprovedAppsDetails.length() > 0) {
                    this.distributeNewlyApprovedApps(customerId, newlyApprovedAppsDetails, newlyApprovedAppGroupIds);
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Cannot redistribute newly approved apps to devices {0}", e);
            }
        }
    }
}
