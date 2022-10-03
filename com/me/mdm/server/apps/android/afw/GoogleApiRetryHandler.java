package com.me.mdm.server.apps.android.afw;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue.AssociationQueueHandler;
import com.adventnet.sym.server.mdm.config.task.AssignCommandTaskProcessor;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import java.util.Properties;
import java.util.HashMap;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Map;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.Collection;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Logger;

public class GoogleApiRetryHandler
{
    public static Logger logger;
    public static final int REDISTRIBUTE_IN_THREAD = 1;
    public static final int REDISTRIBUTE_VIA_SCHEDULER = 2;
    public static final String REDISTRIBUTION_TYPE = "REDISTRIBUTION_TYPE";
    public static final String PROFILE_LIST = "PROFILE_LIST";
    public static final String COLLECTION_LIST = "COLLECTION_LIST";
    public static final String PROFILE_COLLECTION_MAP = "PROFILE_COLLECTION_MAP";
    public static final String DEVICE_LIST = "DEVICE_LIST";
    public static final String IS_SILENT_INSTALL = "IS_SILENT_INSTALL";
    public static final String AFW_ACCOUNT_READY_HANDLING = "AFW_ACCOUNT_READY_HANDLING";
    public static final String REDISTRIBUTION_PARAMS = "REDISTRIBUTION_PARAMS";
    
    public int getInitialDelay() throws Exception {
        final String initialDelayStr = MDMApiFactoryProvider.getGoogleApiProductBasedHandler().getValueFromPropertiesFile("APP_REDISTRIBUTION_INITIAL_DELAY");
        return Integer.parseInt(initialDelayStr);
    }
    
    public int getStepValueForExponentialDelay() throws Exception {
        final String stepValueForExponentialDelayStr = MDMApiFactoryProvider.getGoogleApiProductBasedHandler().getValueFromPropertiesFile("EXPONENTIAL_DELAY_IN_STEPS");
        return Integer.parseInt(stepValueForExponentialDelayStr);
    }
    
    public int getDelayMaxLimit() throws Exception {
        final String delayMaxLimitStr = MDMApiFactoryProvider.getGoogleApiProductBasedHandler().getValueFromPropertiesFile("DELAY_MAX_LIMIT");
        return Integer.parseInt(delayMaxLimitStr);
    }
    
    public Boolean isCommonDelayRedistributionAllowed() throws Exception {
        final String commonDelayRedisAllowedStr = MDMApiFactoryProvider.getGoogleApiProductBasedHandler().getValueFromPropertiesFile("commonAfwDelayRedistributionNeeded");
        return Boolean.parseBoolean(commonDelayRedisAllowedStr);
    }
    
    public Boolean validateIfDelayRedistributionNeeded(final Boolean afwAccountReadyHandling, final Long customerId) throws Exception {
        Boolean delayRedistributionNeeded = false;
        final Boolean commonAfwDelayRedistributionNeeded = new GoogleApiRetryHandler().isCommonDelayRedistributionAllowed();
        final Boolean isAppDistributionDelayNeeded = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AfwAppRedistributionDelayNeeded");
        final String appRedistributionDelayStr = CustomerParamsHandler.getInstance().getParameterValue("AFWAccAppRedistributionDelayTime", (long)customerId);
        if (commonAfwDelayRedistributionNeeded && isAppDistributionDelayNeeded && afwAccountReadyHandling) {
            if (appRedistributionDelayStr == null) {
                delayRedistributionNeeded = true;
            }
            else {
                final int appRedistributionDelay = Integer.parseInt(appRedistributionDelayStr);
                if (appRedistributionDelay <= new GoogleApiRetryHandler().getDelayMaxLimit()) {
                    delayRedistributionNeeded = true;
                }
                else {
                    GoogleApiRetryHandler.logger.log(Level.INFO, "[MANAGED_ACCOUNT_DELAY_REDISTRIBUTION]DelayRedistributionNeededValidation failed as the delay exceeds max value {0}", appRedistributionDelay);
                }
            }
        }
        GoogleApiRetryHandler.logger.log(Level.INFO, "Validating if Delay Redistribution needed for customer {0} : {1} and is afwAccountReadyHandling : {2}", new Object[] { customerId, delayRedistributionNeeded, afwAccountReadyHandling });
        return delayRedistributionNeeded;
    }
    
    public Boolean delayRedistributionForNotRecogDevices(final Collection<Long> managedAccountYetToRecogDevices, final Long customerId, final Boolean afwAccountReadyHandling) {
        Boolean doNotProceedWithFurtherCollection = false;
        try {
            if (!managedAccountYetToRecogDevices.isEmpty()) {
                int appRedistributionDelay = new GoogleApiRetryHandler().getInitialDelay();
                final String delayValueInDBStr = CustomerParamsHandler.getInstance().getParameterValue("AFWAccAppRedistributionDelayTime", (long)customerId);
                if (delayValueInDBStr != null) {
                    final int delayValueInDB = Integer.parseInt(delayValueInDBStr);
                    if (delayValueInDB != 0) {
                        final int stepValueForExponentialDelay = new GoogleApiRetryHandler().getStepValueForExponentialDelay();
                        appRedistributionDelay = delayValueInDB * stepValueForExponentialDelay;
                    }
                }
                if (appRedistributionDelay <= new GoogleApiRetryHandler().getDelayMaxLimit()) {
                    doNotProceedWithFurtherCollection = true;
                    GoogleApiRetryHandler.logger.log(Level.SEVERE, "[MANAGED_ACCOUNT_DELAY_REDISTRIBUTION] Going to redistribute AFW apps to devices {0} with delay {1} seconds", new Object[] { managedAccountYetToRecogDevices.toString(), appRedistributionDelay });
                    CustomerParamsHandler.getInstance().addOrUpdateParameter("AFWAccAppRedistributionDelayTime", String.valueOf(appRedistributionDelay), (long)customerId);
                    for (final Long resourceID : managedAccountYetToRecogDevices) {
                        new GoogleAccountChangeHandler().redistributeAppAssociation(resourceID, customerId, appRedistributionDelay, afwAccountReadyHandling);
                    }
                }
                else {
                    GoogleApiRetryHandler.logger.log(Level.INFO, "[MANAGED_ACCOUNT_DELAY_REDISTRIBUTION] As the delay exceeds max value by {0} seconds,drop re-distribution of AFW Apps", appRedistributionDelay);
                }
            }
        }
        catch (final Exception ex) {
            GoogleApiRetryHandler.logger.log(Level.SEVERE, "[MANAGED_ACCOUNT_DELAY_REDISTRIBUTION] Exception occurred while redistributing apps with delay ", ex);
        }
        return doNotProceedWithFurtherCollection;
    }
    
    public void associateAfwAppsToDevices(final JSONObject params, final Map<Long, Long> profileCollnMap) throws Exception {
        final List<Long> profileList = JSONUtil.getInstance().convertLongJSONArrayTOList(params.optJSONArray("PROFILE_LIST"));
        final List<Long> collectionList = JSONUtil.getInstance().convertJSONArrayTOList(params.optJSONArray("COLLECTION_LIST"));
        final List<Long> deviceList = JSONUtil.getInstance().convertJSONArrayTOList(params.optJSONArray("DEVICE_LIST"));
        if (!profileList.isEmpty() && !collectionList.isEmpty() && !deviceList.isEmpty()) {
            final Set resSet = new HashSet();
            resSet.addAll(deviceList);
            final HashMap deviceMap = new HashMap();
            deviceMap.put(2, resSet);
            final HashMap collectionToPlatformMap = new HashMap();
            collectionToPlatformMap.put(2, collectionList);
            final HashMap profileToPlatformMap = new HashMap();
            profileToPlatformMap.put(2, profileList);
            final Boolean isSilentInstall = params.optBoolean("IS_SILENT_INSTALL");
            final Boolean aFWAccountReadyHandling = params.optBoolean("AFW_ACCOUNT_READY_HANDLING");
            final Long customerId = params.optLong("CUSTOMER_ID");
            final JSONObject redistributionParams = params.optJSONObject("REDISTRIBUTION_PARAMS");
            final Properties properties = new Properties();
            ((Hashtable<String, HashMap>)properties).put("deviceMap", deviceMap);
            ((Hashtable<String, HashMap>)properties).put("collectionToPlatformMap", collectionToPlatformMap);
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", isSilentInstall);
            ((Hashtable<String, HashMap>)properties).put("profileToPlatformMap", profileToPlatformMap);
            ((Hashtable<String, String>)properties).put("commandName", "InstallApplication");
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, Long>)properties).put("customerId", customerId);
            ((Hashtable<String, Boolean>)properties).put("isNotify", false);
            ((Hashtable<String, Boolean>)properties).put("isAppUpgrade", false);
            ((Hashtable<String, Integer>)properties).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_USER);
            ((Hashtable<String, Map<Long, Long>>)properties).put("profileCollnMap", profileCollnMap);
            ((Hashtable<String, Integer>)properties).put("commandType", 1);
            ((Hashtable<String, Boolean>)properties).put("AFWAccountReadyHandling", aFWAccountReadyHandling);
            final int redistributionType = redistributionParams.optInt("REDISTRIBUTION_TYPE", 1);
            if (redistributionType == 1) {
                AssignCommandTaskProcessor.getTaskProcessor().assignDeviceCommandTask(properties);
            }
            else if (redistributionType == 2) {
                final String taskName = redistributionParams.optString("taskName");
                final HashMap taskInfoMap = new HashMap();
                taskInfoMap.put("poolName", "mdmPool");
                taskInfoMap.put("taskName", taskName);
                taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                AssociationQueueHandler.getInstance().executeTask(taskInfoMap, properties);
            }
        }
    }
    
    public Boolean isAFWNewlyApprovedAppRedistributionAllowed(final Long customerId) throws Exception {
        final String isAFWNewlyApprovedAppRedisAllowed = MDMApiFactoryProvider.getGoogleApiProductBasedHandler().getValueFromPropertiesFile("isAFWNewlyApprovedAppRedisAllowed");
        return Boolean.parseBoolean(isAFWNewlyApprovedAppRedisAllowed);
    }
    
    public void initiateNewlyApprovedAppRedistribution(final HashMap params) {
        try {
            final Long customerId = params.get("CustomerID");
            if (new GoogleApiRetryHandler().isAFWNewlyApprovedAppRedistributionAllowed(customerId)) {
                final JSONObject specificParams = params.get("specificParams");
                final List nonPortalAppsBeforeSync = params.get("nonPortalAppsBeforeSync");
                final List newlyApprovedApps = params.get("newlyApprovedApps");
                final List newlyApprovedAppsForRedistribution = new ArrayList(newlyApprovedApps);
                newlyApprovedAppsForRedistribution.retainAll(nonPortalAppsBeforeSync);
                if (!newlyApprovedAppsForRedistribution.isEmpty()) {
                    GoogleApiRetryHandler.logger.log(Level.INFO, "Newly Approved AFW Apps for Redistribution : {0}", newlyApprovedAppsForRedistribution.toString());
                    final JSONObject propsJSON = new JSONObject();
                    propsJSON.put("customerId", (Object)customerId);
                    propsJSON.put("newlyApprovedAppDetails", params.get("newlyApprovedAppDetails"));
                    propsJSON.put("newlyApprovedAppGroupIds", (Collection)newlyApprovedAppsForRedistribution);
                    final Properties taskProps = new Properties();
                    ((Hashtable<String, JSONObject>)taskProps).put("REQUEST_DATA", propsJSON);
                    int syncSource = 1;
                    if (specificParams != null) {
                        syncSource = specificParams.optInt("source", 1);
                    }
                    if (syncSource == 1) {
                        final HashMap taskInfoMap = new HashMap();
                        taskInfoMap.put("taskName", "AFWNewlyApprovedAppsRedistributionTask");
                        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                        taskInfoMap.put("poolName", "mdmPool");
                        ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.apps.android.afw.AFWNewlyApprovedAppsRedistributionTask", taskInfoMap, taskProps);
                    }
                    else if (syncSource == 2) {
                        new AFWNewlyApprovedAppsRedistributionTask().executeTask(taskProps);
                    }
                }
                else {
                    GoogleApiRetryHandler.logger.log(Level.SEVERE, "No newly approved app for redistribution");
                }
            }
            else {
                GoogleApiRetryHandler.logger.log(Level.INFO, "AFW Newly approved app redistribution is not enabled");
            }
        }
        catch (final Exception ex) {
            GoogleApiRetryHandler.logger.log(Level.SEVERE, "Exception occurred in validateAndInitiateNewlyApprovedAFWAppRedis {0}", ex);
        }
    }
    
    static {
        GoogleApiRetryHandler.logger = Logger.getLogger("MDMLogger");
    }
}
