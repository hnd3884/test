package com.me.mdm.server.updates.osupdates.task;

import java.util.Hashtable;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.mdm.server.updates.osupdates.ios.AppleServiceOSUpdateSyncHandler;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.me.mdm.server.updates.osupdates.ios.IOSOSUpdateHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.updates.osupdates.ResourceOSUpdateDataHandler;
import java.util.HashSet;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import java.util.Collection;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.List;
import org.json.JSONException;
import com.me.mdm.server.notification.NotificationHandler;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Properties;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class OSUpdateScheduleTask implements SchedulerExecutionInterface, CommonQueueProcessorInterface
{
    private static final Logger LOGGER;
    public static final String RESOURCE_IDS = "RESOURCE_IDS";
    public static final String PROFILE_IDS = "PROFILE_IDS";
    public static final String CUSTOMER_ID = "CUSTOMER_ID";
    public static final String MODE = "MODE";
    public static final String MODE_ASSOCIATE = "MODE_ASSOCIATE";
    
    public void processData(final CommonQueueData data) {
        OSUpdateScheduleTask.LOGGER.log(Level.INFO, "Started processing the data in OSUpdateScheduleTask");
        final Properties taskProps = new Properties();
        try {
            final List<Long> profileList = JSONUtil.getInstance().convertLongJSONArrayTOList((JSONArray)data.getJsonQueueData().get("PROFILE_IDS"));
            final List<Long> iosResourceList = JSONUtil.getInstance().convertLongJSONArrayTOList((JSONArray)data.getJsonQueueData().get("RESOURCE_IDS"));
            ((Hashtable<String, List<Long>>)taskProps).put("PROFILE_IDS", profileList);
            ((Hashtable<String, List<Long>>)taskProps).put("RESOURCE_IDS", iosResourceList);
            final List<Long> resourceList = this.scheduleOSUpdatePolicy(taskProps);
            OSUpdateScheduleTask.LOGGER.log(Level.INFO, "WakingUp resources for OSUpdate Policy:{0}", new Object[] { resourceList });
            NotificationHandler.getInstance().SendNotification(resourceList);
        }
        catch (final JSONException exp) {
            OSUpdateScheduleTask.LOGGER.log(Level.SEVERE, "Cannot parse the necessary data for forming props file", (Throwable)exp);
        }
        catch (final Exception e) {
            OSUpdateScheduleTask.LOGGER.log(Level.SEVERE, "Exception in waking resource for OSUpdate Profiles", e);
        }
    }
    
    public void executeTask(final Properties taskProps) {
        final CommonQueueData tempData = new CommonQueueData();
        try {
            tempData.setJsonQueueData(new JSONObject((String)((Hashtable<K, String>)taskProps).get("jsonParams")));
            tempData.setCustomerId(((Hashtable<K, Long>)taskProps).get("customerId"));
            this.processData(tempData);
        }
        catch (final JSONException exp) {
            OSUpdateScheduleTask.LOGGER.log(Level.SEVERE, "Cannot fetch JSON from Props", (Throwable)exp);
        }
    }
    
    private HashMap<Integer, List<OSUpdatePolicyObjects>> getScheduleObjectFromTaskProps(final Properties taskProps) {
        final List<Long> osPolicyList = new ArrayList<Long>();
        final HashMap<Integer, List<OSUpdatePolicyObjects>> osupdateObjects = new HashMap<Integer, List<OSUpdatePolicyObjects>>();
        try {
            Criteria criteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)3, 0);
            if (taskProps.get("PROFILE_IDS") != null) {
                osPolicyList.addAll(((Hashtable<K, List<? extends Long>>)taskProps).get("PROFILE_IDS"));
                criteria = criteria.and(new Criteria(new Column("Profile", "PROFILE_ID"), (Object)osPolicyList.toArray(), 8));
            }
            else if (taskProps.get("CUSTOMER_ID") != null) {
                final Long customerId = ((Hashtable<K, Long>)taskProps).get("CUSTOMER_ID");
                final Criteria customerIDCriteria = new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
                criteria = criteria.and(customerIDCriteria);
            }
            final Criteria markedForDelete = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            criteria = criteria.and(markedForDelete);
            final SelectQuery sQuery = OSUpdatePolicyHandler.getInstance().osUpdatePolicyDetailsQuery();
            sQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            sQuery.addSelectColumn(new Column("Profile", "CREATED_BY"));
            sQuery.addSelectColumn(new Column("Profile", "PLATFORM_TYPE"));
            sQuery.addSelectColumn(new Column("RecentProfileToColln", "PROFILE_ID"));
            sQuery.addSelectColumn(new Column("RecentProfileToColln", "COLLECTION_ID"));
            sQuery.addSelectColumn(new Column("ProfileToCustomerRel", "*"));
            sQuery.addSelectColumn(new Column("OSUpdatePolicy", "COLLECTION_ID"));
            sQuery.addSelectColumn(new Column("OSUpdatePolicy", "DEFER_DAYS"));
            sQuery.addSelectColumn(new Column("OSUpdatePolicy", "POLICY_TYPE"));
            sQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(sQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("Profile");
                while (iterator.hasNext()) {
                    final Row profileRow = iterator.next();
                    final Integer platformType = (Integer)profileRow.get("PLATFORM_TYPE");
                    final Long profileId = (Long)profileRow.get("PROFILE_ID");
                    final Long userID = (Long)profileRow.get("CREATED_BY");
                    final Row osUpdatePolicyRow = dataObject.getRow("OSUpdatePolicy", new Criteria(new Column("RecentProfileToColln", "PROFILE_ID"), (Object)profileId, 0));
                    final Long collectionId = (Long)osUpdatePolicyRow.get("COLLECTION_ID");
                    final Object deferDays = osUpdatePolicyRow.get("DEFER_DAYS");
                    final Integer policyType = (Integer)osUpdatePolicyRow.get("POLICY_TYPE");
                    final OSUpdatePolicyObjects singleObject = new OSUpdatePolicyObjects();
                    singleObject.platformType = platformType;
                    singleObject.profileId = profileId;
                    singleObject.policyCreatedUser = userID;
                    singleObject.collectionId = collectionId;
                    singleObject.deferDays = Long.parseLong(deferDays.toString());
                    singleObject.policyType = policyType;
                    if (osupdateObjects.containsKey(platformType)) {
                        final List<OSUpdatePolicyObjects> tempScheduleObjects = osupdateObjects.get(platformType);
                        tempScheduleObjects.add(singleObject);
                    }
                    else {
                        final List<OSUpdatePolicyObjects> tempScheduleObjects = new ArrayList<OSUpdatePolicyObjects>();
                        tempScheduleObjects.add(singleObject);
                        osupdateObjects.put(platformType, tempScheduleObjects);
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            OSUpdateScheduleTask.LOGGER.log(Level.SEVERE, "Exception in getting the profile DO in OSUpdate", (Throwable)e);
        }
        catch (final Exception e2) {
            OSUpdateScheduleTask.LOGGER.log(Level.SEVERE, "Exception in getting list for OSUpdate", e2);
        }
        return osupdateObjects;
    }
    
    public List<Long> scheduleOSUpdatePolicy(final Properties taskProps) {
        final HashSet<Long> wakeupResources = new HashSet<Long>();
        try {
            OSUpdateScheduleTask.LOGGER.log(Level.INFO, "OSUpdateScheduleTask Started");
            final ArrayList<Long> resourcesList = ((Hashtable<K, ArrayList<Long>>)taskProps).get("RESOURCE_IDS");
            final HashMap<Integer, List<OSUpdatePolicyObjects>> scheduleObjects = this.getScheduleObjectFromTaskProps(taskProps);
            for (final Integer platformType : scheduleObjects.keySet()) {
                final List<OSUpdatePolicyObjects> scheduleObjectsList = scheduleObjects.get(platformType);
                OSUpdateScheduleTask.LOGGER.log(Level.INFO, "OSUpdateScheduleTask Started for OSUpdatePolicy");
                switch (platformType) {
                    case 1: {
                        wakeupResources.addAll((Collection<?>)this.scheduleOSUpdateForAppleDevice(scheduleObjectsList, resourcesList));
                        continue;
                    }
                    default: {
                        OSUpdateScheduleTask.LOGGER.log(Level.INFO, "Unhandled Schedule OS Object");
                        continue;
                    }
                }
            }
        }
        catch (final Exception exp) {
            OSUpdateScheduleTask.LOGGER.log(Level.SEVERE, "Exception occurred during OSUpdateScheduleTask execution : ", exp);
        }
        OSUpdateScheduleTask.LOGGER.log(Level.INFO, "OSUpdateScheduleTask completed.");
        return new ArrayList<Long>(wakeupResources);
    }
    
    private List<Long> scheduleOSUpdateForAppleDevice(final List<OSUpdatePolicyObjects> scheduleObjectsList, final List resourceList) throws Exception {
        final HashSet<Long> wakeUpResource = new HashSet<Long>();
        final HashSet<Long> queryAvailableUpdateResourceList = new HashSet<Long>();
        OSUpdateScheduleTask.LOGGER.log(Level.INFO, "Started Apple OS Update for resources: {0}", new Object[] { resourceList });
        for (final OSUpdatePolicyObjects scheduleObjects : scheduleObjectsList) {
            OSUpdateScheduleTask.LOGGER.log(Level.INFO, "Processing update policy : Policy Type: {0} | Profile: {1}, Collection: {2}, Defer days: {3}", new Object[] { scheduleObjects.policyType, scheduleObjects.profileId, scheduleObjects.collectionId, scheduleObjects.deferDays });
            final Long profileId = scheduleObjects.profileId;
            final Long userId = scheduleObjects.policyCreatedUser;
            final Long collectionId = scheduleObjects.collectionId;
            final List collectionList = new ArrayList();
            collectionList.add(collectionId);
            final Integer policyType = scheduleObjects.policyType;
            Long noOfDaysDefer = 0L;
            final long currentTimeinMillisec = System.currentTimeMillis();
            if (policyType.equals(3)) {
                noOfDaysDefer = scheduleObjects.deferDays;
            }
            final Long deferTimeInMS = noOfDaysDefer * 24L * 60L * 60L * 1000L;
            final Long detectedTimeInMS = currentTimeinMillisec - deferTimeInMS;
            List targetResourceList = new ArrayList();
            if (resourceList != null && !resourceList.isEmpty()) {
                targetResourceList.addAll(resourceList);
            }
            else {
                targetResourceList = OSUpdatePolicyHandler.getInstance().getPlatformTargetResourceIds(collectionList, 1);
            }
            final ResourceOSUpdateDataHandler detectedUpdates = new ResourceOSUpdateDataHandler();
            final List affectedResourcesFromTarget = detectedUpdates.getAffectedResourcesFromTarget((ArrayList<Long>)targetResourceList, detectedTimeInMS);
            OSUpdateScheduleTask.LOGGER.log(Level.INFO, "Resources pending for the detected OS updates: Collection: {0} | Resources: {1}", new Object[] { collectionId, affectedResourcesFromTarget });
            if (affectedResourcesFromTarget.isEmpty() && policyType.equals(2) && resourceList != null && !resourceList.isEmpty()) {
                OSUpdateScheduleTask.LOGGER.log(Level.INFO, "IOS OSUpdate: Immediate policy affected resource:{0}", new Object[] { resourceList });
                affectedResourcesFromTarget.addAll(resourceList);
            }
            if (!affectedResourcesFromTarget.isEmpty()) {
                wakeUpResource.addAll((Collection<?>)affectedResourcesFromTarget);
                final List collectionIdsCommandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "ScheduleOSUpdate");
                final JSONObject cmdParams = new JSONObject();
                cmdParams.put("COLLECTION_ID", (Object)collectionId);
                final JSONObject params = new JSONObject();
                params.put(profileId.toString(), (Object)userId);
                cmdParams.put("UserId", (Object)params.toString());
                this.updateNextVersionRemarks(affectedResourcesFromTarget, collectionId);
                new IOSOSUpdateHandler().checkAndRemoveRestrictOSUpdateCommand(affectedResourcesFromTarget);
                OSUpdateScheduleTask.LOGGER.log(Level.INFO, "Going to OS Update in the device. ResourceList:{0}", new Object[] { affectedResourcesFromTarget });
                SeqCmdRepository.getInstance().executeSequentially(affectedResourcesFromTarget, collectionIdsCommandList, cmdParams);
            }
            targetResourceList.removeAll(affectedResourcesFromTarget);
            queryAvailableUpdateResourceList.addAll((Collection<?>)targetResourceList);
        }
        if (!queryAvailableUpdateResourceList.isEmpty()) {
            if (resourceList == null || resourceList.isEmpty()) {
                wakeUpResource.addAll((Collection<?>)this.addAvailableOSUpdateToDevices(new ArrayList<Long>(queryAvailableUpdateResourceList), true));
            }
            else {
                wakeUpResource.addAll((Collection<?>)this.addAvailableOSUpdateToDevices(new ArrayList<Long>(queryAvailableUpdateResourceList), false));
            }
        }
        return new ArrayList<Long>(wakeUpResource);
    }
    
    private List<Long> addAvailableOSUpdateToDevices(final List<Long> resourceList, final boolean checkOSUpdateToApple) {
        if (checkOSUpdateToApple) {
            final AppleServiceOSUpdateSyncHandler syncHandler = new AppleServiceOSUpdateSyncHandler();
            final List<Long> applicableList = syncHandler.getResourcesApplicableForCheckingUpdate(resourceList);
            OSUpdateScheduleTask.LOGGER.log(Level.INFO, "Applicable Resources for sending available OSupdate:{0}", new Object[] { applicableList });
            resourceList.retainAll(applicableList);
        }
        if (!resourceList.isEmpty()) {
            final Long commandID = DeviceCommandRepository.getInstance().getCommandID("AvailableOSUpdates");
            if (commandID == null || commandID == -1L) {
                DeviceCommandRepository.getInstance().addCommand("AvailableOSUpdates");
            }
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandID, resourceList);
        }
        return resourceList;
    }
    
    private void updateNextVersionRemarks(final List<Long> resourceList, final Long collectionId) {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("CollnToResources");
            final Criteria resourceCriteria = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria collectionCriteria = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria remarkCriteria = new Criteria(new Column("CollnToResources", "REMARKS"), (Object)"mdm.osupdate.remarks.nomissingupdates", 0).or(new Criteria(new Column("CollnToResources", "REMARKS"), (Object)"mdm.osupdate.remarks.nomissingupdatesfrompolicy", 0));
            updateQuery.setCriteria(resourceCriteria.and(collectionCriteria).and(remarkCriteria));
            updateQuery.setUpdateColumn("REMARKS", (Object)"mdm.db.osupdate.updating_next_version");
            OSUpdateScheduleTask.LOGGER.log(Level.INFO, "Going to update next version remark for resource{0} for collection:{1}", new Object[] { resourceList, collectionId });
            MDMUtil.getPersistenceLite().update(updateQuery);
        }
        catch (final Exception ex) {
            OSUpdateScheduleTask.LOGGER.log(Level.SEVERE, "Exception in updateNextVersionRemarks", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
