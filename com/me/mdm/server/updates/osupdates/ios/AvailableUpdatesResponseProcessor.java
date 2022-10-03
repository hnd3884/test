package com.me.mdm.server.updates.osupdates.ios;

import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.updates.osupdates.OSUpdateCriteriaEvaluator;
import com.me.mdm.server.updates.osupdates.ResourcesMissingUpdatesListener;
import com.me.mdm.server.updates.osupdates.ResourceUpdateEventListener;
import com.me.mdm.server.updates.osupdates.ExtendedOSDetailsDataHandler;
import com.me.mdm.server.updates.osupdates.OSUpdatesProcessor;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.Iterator;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.config.MDMCollectionUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import java.util.List;
import java.util.ArrayList;
import com.me.mdm.server.updates.osupdates.ResourceOSUpdateDataHandler;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.PlistWrapper;
import java.util.logging.Logger;

public class AvailableUpdatesResponseProcessor
{
    private static final Logger LOGGER;
    private boolean isAvailableUpdatesCleared;
    
    public AvailableUpdatesResponseProcessor() {
        this.isAvailableUpdatesCleared = false;
    }
    
    public void processResponse(final Long resourceID, final String responseStr, final String cmdUUID) {
        try {
            final String cmdStatus = PlistWrapper.getInstance().getValueForKeyString("Status", responseStr);
            if (cmdStatus.contains("Error")) {
                AvailableUpdatesResponseProcessor.LOGGER.log(Level.FINE, "Available OS Update Error status received from device: {0} | Response:{1}", new Object[] { resourceID, responseStr });
            }
            else if (cmdStatus.equalsIgnoreCase("Acknowledged")) {
                final NSArray availableUpdatesArray = PlistWrapper.getInstance().getArrayForKey("AvailableOSUpdates", responseStr);
                if (availableUpdatesArray != null && availableUpdatesArray.count() > 0) {
                    for (int i = 0; i < availableUpdatesArray.count(); ++i) {
                        final NSDictionary updateDict = (NSDictionary)availableUpdatesArray.objectAtIndex(i);
                        this.processDiscoveredUpdate(updateDict, resourceID);
                    }
                    this.updateNewUpdatesAvailableRemarks(resourceID);
                }
                else {
                    AvailableUpdatesResponseProcessor.LOGGER.log(Level.FINE, "Device responded with No available updates: {0}", new Object[] { resourceID });
                    this.processOtherAvailableOSUpdateResponse(resourceID);
                }
            }
        }
        catch (final Exception ex) {
            AvailableUpdatesResponseProcessor.LOGGER.log(Level.SEVERE, ex, () -> "Exception occurred while processing OS update response for resource " + n);
        }
    }
    
    private void processOtherAvailableOSUpdateResponse(final Long resourceID) {
        try {
            AvailableUpdatesResponseProcessor.LOGGER.log(Level.SEVERE, "No update available on this device : {0}. Clearing device available entries", new Object[] { resourceID });
            final ResourceOSUpdateDataHandler handler = new ResourceOSUpdateDataHandler();
            handler.deleteAvailableUpdatesForResource(resourceID, new ArrayList<String>());
            this.isAvailableUpdatesCleared = true;
            final List<Long> collnIds = OSUpdatePolicyHandler.getInstance().getCollectionsAssociatedToResource(resourceID);
            AvailableUpdatesResponseProcessor.LOGGER.log(Level.INFO, "OS update policy collections configured for resource: {0} are: {1}", new Object[] { resourceID, collnIds });
            if (collnIds != null && !collnIds.isEmpty()) {
                final List<Long> resourceList = new ArrayList<Long>();
                resourceList.add(resourceID);
                boolean sendDeviceCommand = false;
                final Criteria statusCriteria = new Criteria(new Column("CollnToResources", "REMARKS"), (Object)ScheduleOSUpdateResponseProcessor.updateremark, 2).or(new Criteria(new Column("CollnToResources", "STATUS"), (Object)new int[] { 16, 3 }, 8));
                final JSONObject resourcesStatus = MDMCollectionUtil.getCollectionsStatusForResources(collnIds, resourceList, statusCriteria);
                if (resourcesStatus.length() > 0) {
                    final JSONObject resourceStatus = resourcesStatus.getJSONObject(resourceID.toString());
                    if (resourceStatus.length() > 0) {
                        sendDeviceCommand = true;
                    }
                }
                if (sendDeviceCommand || handler.isAnyUpdateStartedForResource(resourceID)) {
                    this.sendDeviceInfoCommand(resourceID);
                }
                for (final Object co : collnIds) {
                    MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, co.toString(), 6, "mdm.osupdate.remarks.nomissingupdates");
                }
                new IOSOSUpdateHandler().checkAndAddRestrictOSUpdate(resourceID, collnIds.get(0));
            }
        }
        catch (final Exception ex) {
            AvailableUpdatesResponseProcessor.LOGGER.log(Level.SEVERE, ex, () -> "Exception occurred while processing Other Available OS Update Response: " + n);
        }
    }
    
    public void processResponseForSeqCmd(final Long resourceID, final String responseStr, final String cmdUUID, final JSONObject cmdParams) {
        try {
            final String collectionID = String.valueOf(cmdParams.getLong("COLLECTION_ID"));
            this.processResponse(resourceID, responseStr, cmdUUID);
            final JSONObject json = new JSONObject();
            final JSONObject seqParams = new JSONObject();
            boolean isDeviceHasNoUpdateForPolicy = false;
            final DataObject policyObject = OSUpdatePolicyHandler.getInstance().getOSUpdatePolicy(cmdParams.getLong("COLLECTION_ID"));
            if (policyObject != null && !policyObject.isEmpty()) {
                final Row policyRow = policyObject.getFirstRow("OSUpdatePolicy");
                final Integer policyType = (Integer)policyRow.get("POLICY_TYPE");
                Long deferDays = 0L;
                if (policyType.equals(3)) {
                    deferDays = Long.parseLong(policyRow.get("DEFER_DAYS").toString());
                }
                final long currentTimeinMillisec = System.currentTimeMillis();
                final Long deferTimeInMS = deferDays * 24L * 60L * 60L * 1000L;
                final Long detectedTimeInMS = currentTimeinMillisec - deferTimeInMS;
                final JSONObject resourceUpdateObject = new ResourceOSUpdateDataHandler().getApplicableOSVersionForResource(detectedTimeInMS, resourceID);
                if (resourceUpdateObject.length() == 0) {
                    isDeviceHasNoUpdateForPolicy = true;
                }
            }
            AvailableUpdatesResponseProcessor.LOGGER.log(Level.INFO, "Device: {0} | isAvailableUpdatesCleared: {1} | isDeviceHasNoUpdateForPolicy: {2}", new Object[] { resourceID, this.isAvailableUpdatesCleared, isDeviceHasNoUpdateForPolicy });
            if (this.isAvailableUpdatesCleared) {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionID, 6, "mdm.osupdate.remarks.nomissingupdates");
                json.put("action", 2);
            }
            else if (isDeviceHasNoUpdateForPolicy) {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionID, 6, "mdm.osupdate.remarks.nomissingupdatesfrompolicy");
                json.put("action", 2);
            }
            else {
                json.put("action", 1);
            }
            json.put("resourceID", (Object)resourceID);
            json.put("commandUUID", (Object)cmdUUID);
            json.put("params", (Object)seqParams);
            SeqCmdRepository.getInstance().processSeqCommand(json);
        }
        catch (final Exception e) {
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, cmdUUID);
            AvailableUpdatesResponseProcessor.LOGGER.log(Level.SEVERE, e, () -> "Exception occurred while processing OS update sequential command response for resource: " + n);
        }
    }
    
    private void processDiscoveredUpdate(final NSDictionary dict, final Long resID) {
        try {
            final OSUpdatesProcessor processor = new OSUpdatesProcessor();
            processor.setExtnOSDetailsDataHandler(new IOSUpdatesDetailsHandler());
            final ArrayList<Long> list = new ArrayList<Long>();
            list.add(resID);
            processor.setResourcesToCheckMissingUpdate(list, new ResourceUpdateEventListener());
            final JSONObject json = this.prepareOSUpdateDetailsJson(dict);
            processor.processOSUpdateDetails(json, new IOSUniqueOSEvaluator());
        }
        catch (final Exception e) {
            AvailableUpdatesResponseProcessor.LOGGER.log(Level.SEVERE, e, () -> "Exception occurred while processing discovered updates: " + n);
        }
    }
    
    private JSONObject prepareOSUpdateDetailsJson(final NSDictionary dict) throws Exception {
        final JSONObject json = new JSONObject();
        final String humanReadableName = (dict.get((Object)"HumanReadableName") == null) ? null : dict.get((Object)"HumanReadableName").toString();
        final int type = 1;
        final String version = dict.get((Object)"Version").toString();
        json.put("UPDATE_NAME", (Object)((humanReadableName == null) ? "iOS Update ".concat(version) : humanReadableName));
        json.put("UPDATE_DESCRIPTION", (Object)"iOS System Update");
        json.put("UPDATE_TYPE", type);
        json.put("ADDED_AT", System.currentTimeMillis());
        json.put("UPDATE_PLATFORM", 1);
        final Long downloadSize = (dict.get((Object)"DownloadSize") == null) ? -1L : Long.parseLong(dict.get((Object)"DownloadSize").toString());
        final Long installSize = (dict.get((Object)"InstallSize") == null) ? -1L : Long.parseLong(dict.get((Object)"InstallSize").toString());
        final Boolean restartReqd = dict.get((Object)"RestartRequired") == null || Boolean.parseBoolean(dict.get((Object)"RestartRequired").toString());
        json.put("DOWNLOAD_SIZE", (Object)downloadSize);
        json.put("INSTALL_SIZE", (Object)installSize);
        json.put("RESTART_REQUIRED", (Object)restartReqd);
        json.put("VERSION", (Object)version);
        final JSONObject iosjson = new JSONObject();
        final String productKey = dict.get((Object)"ProductKey").toString();
        final String build = dict.get((Object)"Build").toString();
        final Boolean isCritical = (dict.get((Object)"IsCritical") == null) ? Boolean.FALSE : Boolean.parseBoolean(dict.get((Object)"IsCritical").toString());
        iosjson.put("PRODUCT_KEY", (Object)productKey);
        iosjson.put("BUILD", (Object)build);
        if (humanReadableName != null) {
            iosjson.put("HUMAN_READABLE_NAME", (Object)humanReadableName);
        }
        json.put("IOSUpdates", (Object)iosjson);
        return json;
    }
    
    private void updateNewUpdatesAvailableRemarks(final Long resourceID) {
        AvailableUpdatesResponseProcessor.LOGGER.log(Level.INFO, "entered updateNewUpdatesAvailableRemarks: {0}", new Object[] { resourceID });
        try {
            final List collnIds = OSUpdatePolicyHandler.getInstance().getCollectionsAssociatedToResource(resourceID);
            AvailableUpdatesResponseProcessor.LOGGER.log(Level.INFO, "OS update policy collections configured for resource: {0} are: {1}", new Object[] { resourceID, collnIds });
            if (collnIds != null) {
                final SelectQuery sQuery = OSUpdatePolicyHandler.getInstance().osUpdatePolicyDetailsQuery();
                sQuery.addSelectColumn(new Column("OSUpdatePolicy", "COLLECTION_ID"));
                sQuery.addSelectColumn(new Column("OSUpdatePolicy", "DEFER_DAYS"));
                sQuery.addSelectColumn(new Column("OSUpdatePolicy", "POLICY_TYPE"));
                sQuery.setCriteria(new Criteria(new Column("OSUpdatePolicy", "COLLECTION_ID"), (Object)collnIds.toArray(), 8));
                final DataObject dataObject = MDMUtil.getPersistenceLite().get(sQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator immediateUpdate = dataObject.getRows("OSUpdatePolicy", new Criteria(new Column("OSUpdatePolicy", "POLICY_TYPE"), (Object)2, 0));
                    boolean updateStatus = false;
                    if (immediateUpdate.hasNext()) {
                        updateStatus = true;
                    }
                    else {
                        final SortColumn sortColumn = new SortColumn(new Column("OSUpdatePolicy", "DEFER_DAYS"), true);
                        dataObject.sortRows("OSUpdatePolicy", new SortColumn[] { sortColumn });
                        final Iterator deferIterator = dataObject.getRows("OSUpdatePolicy", new Criteria(new Column("OSUpdatePolicy", "POLICY_TYPE"), (Object)3, 0));
                        if (deferIterator.hasNext()) {
                            final Row deferDays = deferIterator.next();
                            final Long minimumDeferDays = (long)(int)deferDays.get("DEFER_DAYS");
                            final Long deferTimeInMS = minimumDeferDays * 24L * 60L * 60L * 1000L;
                            final long currentTimeinMillisec = System.currentTimeMillis();
                            final Long detectedTimeInMS = currentTimeinMillisec - deferTimeInMS;
                            final ArrayList<Long> resourceList = new ArrayList<Long>();
                            resourceList.add(resourceID);
                            final ArrayList<Long> pendedResourceList = new ResourceOSUpdateDataHandler().getAffectedResourcesFromTarget(resourceList, detectedTimeInMS);
                            if (!pendedResourceList.isEmpty()) {
                                updateStatus = true;
                            }
                        }
                    }
                    if (updateStatus) {
                        AvailableUpdatesResponseProcessor.LOGGER.log(Level.INFO, "updateNewUpdatesAvailableRemarks(): Resource:{0} collectionList:{1}", new Object[] { resourceID, collnIds });
                        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("CollnToResources");
                        final Criteria resourceCriteria = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceID, 0);
                        final Criteria collectionCriteria = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collnIds.toArray(), 8);
                        final Criteria remarkCriteria = new Criteria(new Column("CollnToResources", "REMARKS"), (Object)"mdm.osupdate.remarks.nomissingupdates", 0).or(new Criteria(new Column("CollnToResources", "REMARKS"), (Object)"mdm.osupdate.remarks.nomissingupdatesfrompolicy", 0));
                        updateQuery.setCriteria(resourceCriteria.and(collectionCriteria).and(remarkCriteria));
                        updateQuery.setUpdateColumn("REMARKS", (Object)"mdm.db.osupdate.updating_next_version");
                        MDMUtil.getPersistenceLite().update(updateQuery);
                    }
                }
            }
        }
        catch (final Exception e) {
            AvailableUpdatesResponseProcessor.LOGGER.log(Level.SEVERE, "updateNewUpdatesAvailableRemarks", e);
        }
    }
    
    private void sendDeviceInfoCommand(final Long resourceId) throws Exception {
        DeviceCommandRepository.getInstance().assignCommandToDevice("DeviceInformation", resourceId);
        final List resourceList = new ArrayList();
        resourceList.add(resourceId);
        AvailableUpdatesResponseProcessor.LOGGER.log(Level.INFO, "Device information command is sent to the resource:{0} due to osupdate policy", new Object[] { resourceId });
        NotificationHandler.getInstance().SendNotification(resourceList, 1);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
