package com.me.mdm.server.apps.appupdatepolicy;

import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import java.util.logging.Logger;

public abstract class BaseAppUpdateToResourceHandler implements AppUpdatesToResourceInterface
{
    protected Logger configLogger;
    protected Logger profileDistLogger;
    
    public BaseAppUpdateToResourceHandler() {
        this.configLogger = Logger.getLogger("MDMConfigLogger");
        this.profileDistLogger = Logger.getLogger("MDMProfileDistributionLog");
    }
    
    @Override
    public abstract List<Long> getResourceListForWhichTheAppToBeScheduled(final List p0, final Long p1, final Long p2) throws Exception;
    
    @Override
    public abstract void updateApprovedAppIdAndItsStatus(final Criteria p0, final Long p1, final Integer p2) throws Exception;
    
    @Override
    public abstract DerivedColumn getResourcesForWhichAppUpdatePolicyIsConfiguredForGivenApp(final Long p0);
    
    public abstract void setIsUpdateAvailable(final Long p0, final List p1) throws Exception;
    
    @Override
    public HashMap scheduleAppUpdatesForResourceBasedOnPolicy(final List resourceList, final Long profileId, final Long collectionId, final Properties properties) throws Exception {
        final List<Long> clonedResourceList = new ArrayList<Long>(resourceList);
        List<Long> devicesToBeScheduled = new ArrayList<Long>();
        final Boolean isScheduledAppUpdateApplicable = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableScheduleAppUpdates") && (properties.get("isSchedule") == null || ((Hashtable<K, Boolean>)properties).get("isSchedule") == Boolean.FALSE) && (properties.get("isAppDowngrade") == null || ((Hashtable<K, Boolean>)properties).get("isAppDowngrade") == Boolean.FALSE) && (properties.get("forceUpdate") == null || ((Hashtable<K, Boolean>)properties).get("forceUpdate") == Boolean.FALSE);
        if (isScheduledAppUpdateApplicable) {
            this.profileDistLogger.log(Level.INFO, "scheduleAppUpdatesForResourceBasedOnPolicy called with props {0} for resourceList {1} profile Id {2} collection Id {3}", new Object[] { properties, resourceList, profileId, collectionId });
            devicesToBeScheduled = this.getResourceListForWhichTheAppToBeScheduled(resourceList, profileId, collectionId);
            if (devicesToBeScheduled != null && !devicesToBeScheduled.isEmpty()) {
                clonedResourceList.removeAll(devicesToBeScheduled);
                final Long appId = MDMUtil.getInstance().getAppIDFromCollection(collectionId);
                final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionId);
                final Criteria scheduleAppToResourceCriteria = this.getCriteria(appGroupId, devicesToBeScheduled);
                this.updateApprovedAppIdAndItsStatus(scheduleAppToResourceCriteria, appId, 2);
                this.setIsUpdateAvailable(appGroupId, devicesToBeScheduled);
            }
        }
        final HashMap<String, List<Long>> map = new HashMap<String, List<Long>>();
        map.put("notScheduledList", clonedResourceList);
        map.put("scheduledList", devicesToBeScheduled);
        this.profileDistLogger.log(Level.INFO, "scheduleAppUpdatesForResourceBasedOnPolicy post processing data {0}", map);
        return map;
    }
    
    @Override
    public abstract Criteria getCriteria(final DerivedColumn p0, final Long p1, final List p2, final Boolean p3);
    
    @Override
    public abstract Criteria getCriteria(final Long p0, final List p1);
    
    @Override
    public abstract Criteria getCriteria(final List p0, final List p1, final Boolean p2) throws Exception;
    
    @Override
    public abstract Criteria getCriteria(final Long p0, final List p1, final DerivedColumn p2, final Boolean p3) throws Exception;
    
    @Override
    public void associateAppUpdateForResource(final List releaseLabelIds, final Long appGroupId, final Long associateAppId) throws Exception {
        final DerivedColumn resourceIdsWithPolicyAssociated = this.getResourcesForWhichAppUpdatePolicyIsConfiguredForGivenApp(appGroupId);
        final Criteria scheduleAppToResourceCriteria = this.getCriteria(resourceIdsWithPolicyAssociated, appGroupId, releaseLabelIds, Boolean.TRUE);
        final Criteria yetToUpdateAppResourceCriteria = this.getCriteria(resourceIdsWithPolicyAssociated, appGroupId, releaseLabelIds, Boolean.FALSE);
        this.updateApprovedAppIdAndItsStatus(scheduleAppToResourceCriteria, associateAppId, 2);
        this.updateApprovedAppIdAndItsStatus(yetToUpdateAppResourceCriteria, associateAppId, 1);
    }
    
    @Override
    public void invokePostPolicyAssociationListeners(final List resourceIds, final Long collectionId) throws Exception {
        this.profileDistLogger.log(Level.INFO, "Post app update policy association listener called for resource {0} collection {1}", new Object[] { resourceIds, collectionId });
        final List<Long> appsListInGivenPolicy = AppUpdatePolicyDBHandler.getInstance().getListOfAppsInGivenPolicy(collectionId);
        this.profileDistLogger.log(Level.INFO, "Apps configured in the given policy collection is {0}", appsListInGivenPolicy);
        final Criteria scheduleAppCriteria = this.getCriteria(appsListInGivenPolicy, resourceIds, Boolean.TRUE);
        this.updateApprovedAppIdAndItsStatus(scheduleAppCriteria, null, 2);
    }
    
    @Override
    public void invokePostPolicyDisassociationListeners(final List resourceIds, final Long collectionId) throws Exception {
        this.profileDistLogger.log(Level.INFO, "Post app update policy disassociation listener called for resource {0} collection {1}", new Object[] { resourceIds, collectionId });
        final List<Long> appListInGivenPolicy = AppUpdatePolicyDBHandler.getInstance().getListOfAppsInGivenPolicy(collectionId);
        this.profileDistLogger.log(Level.INFO, "Apps configured in the given policy collection is {0}", appListInGivenPolicy);
        for (final Long appGroupId : appListInGivenPolicy) {
            final DerivedColumn resourceIdsWithPolicyAssociated = this.getResourcesForWhichAppUpdatePolicyIsConfiguredForGivenApp(appGroupId);
            this.updateApprovedAppIdAndItsStatus(this.getCriteria(appGroupId, resourceIds, resourceIdsWithPolicyAssociated, Boolean.TRUE), null, 1);
        }
    }
    
    @Override
    public void invokePostPolicyModificationListener(final List resourceIds, final List appGroupIds) throws Exception {
        this.configLogger.log(Level.INFO, "invokePostPolicyModificationListener called for resource {0} for appGroup {1}", new Object[] { resourceIds, appGroupIds });
        for (final Long appGroupId : appGroupIds) {
            final DerivedColumn resourceIdsWithPolicyAssociated = this.getResourcesForWhichAppUpdatePolicyIsConfiguredForGivenApp(appGroupId);
            this.updateApprovedAppIdAndItsStatus(this.getCriteria(appGroupId, resourceIds, resourceIdsWithPolicyAssociated, Boolean.TRUE), null, 1);
        }
    }
}
