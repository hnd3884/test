package com.me.mdm.server.command.ios.commandtask;

import java.util.Hashtable;
import org.json.JSONArray;
import java.util.Iterator;
import com.me.mdm.server.profiles.ios.DeviceConfigPayloadsDataHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.profiles.kiosk.IOSKioskProfileDataHandler;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.config.task.CollectionCommandHandler;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.profiles.MDMCollectionNotApplicableHandler;
import com.me.mdm.server.profiles.MDMConfigNotApplicableHandler;
import com.me.mdm.server.config.MDMConfigUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.task.CollectionCommandTaskData;
import java.util.logging.Logger;

public class IOSRemoveProfileCommandTaskHandler
{
    public static Logger logger;
    private static Logger mdmLogger;
    
    public void executeCommandForDevice(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        IOSRemoveProfileCommandTaskHandler.logger.log(Level.INFO, "****** Uninstall Profile sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final HashMap<Long, List> collectionToDirectRemovalRes = ((Hashtable<K, HashMap<Long, List>>)collectionCommandTaskData.getTaskProperties()).get("collnToProfileDirectRemovalResources");
        final int collResConstant = 18;
        final HashSet<Long> resourceNotificationset = new HashSet<Long>();
        final HashMap<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, false, "InstallProfile");
        }
        for (int j = 0; j < collectionList.size(); ++j) {
            final Long collectionID = collectionList.get(j);
            final Long profileID = collectionCommandTaskData.getProfileIdForCollection(collectionID);
            final List clonedResourceList = new ArrayList(collectionToApplicableRes.get(collectionID));
            List directRemovalResourceList = new ArrayList();
            if (collectionToDirectRemovalRes.get(collectionID) != null) {
                directRemovalResourceList = new ArrayList(collectionToDirectRemovalRes.get(collectionID));
            }
            final List configIds = MDMConfigUtil.getConfigIds(collectionID);
            if (configIds != null && !configIds.isEmpty()) {
                final List naList = MDMConfigNotApplicableHandler.getInstance(profileID, collectionID).invokeConfigNotApplicableRemoveListeners(configIds, new ArrayList<Long>(clonedResourceList), new ArrayList<Long>(directRemovalResourceList));
                clonedResourceList.removeAll(naList);
                IOSRemoveProfileCommandTaskHandler.logger.log(Level.INFO, "Marked following devices {0} for collection {1} as not applicable", new Object[] { naList, collectionList.get(j) });
                final List collectionNAList = new MDMCollectionNotApplicableHandler(collectionID).invokeCollectionNotApplicableRemoveListener(configIds, new ArrayList<Long>(clonedResourceList), collectionCommandTaskData.getCustomerId());
                clonedResourceList.removeAll(collectionNAList);
                IOSRemoveProfileCommandTaskHandler.logger.log(Level.INFO, "Marked following devices {0} for collection List{1} as not applicable", new Object[] { collectionNAList, collectionList.get(j) });
            }
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedResourceList, collectionList.get(j), collResConstant, "");
            new CollectionCommandHandler().removeInstallProfileCommandFromDevice(clonedResourceList, Arrays.asList(collectionList.get(j)), "InstallProfile");
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(j)), collectionCommandTaskData.getCommandName());
            clonedResourceList.addAll(directRemovalResourceList);
            final JSONObject seqCmdParams = new JSONObject();
            seqCmdParams.put("UserId", (Object)collectionCommandTaskData.getLoggedInUserId());
            SeqCmdRepository.getInstance().executeSequentially(clonedResourceList, commandList, seqCmdParams);
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, clonedResourceList);
            SeqCmdUtils.getInstance().removeSeqInstallProfileCmd(clonedResourceList, Arrays.asList(collectionList.get(j)));
            this.processRemoveProfileCollection(collectionCommandTaskData, clonedResourceList, Arrays.asList(collectionList.get(j)));
            resourceNotificationset.addAll((Collection<?>)clonedResourceList);
        }
        NotificationHandler.getInstance().SendNotification(new ArrayList(resourceNotificationset), collectionCommandTaskData.getPlatform());
        IOSRemoveProfileCommandTaskHandler.logger.log(Level.INFO, "************** Uninstall profile sub task completed *************");
    }
    
    private void processRemoveProfileCollection(final CollectionCommandTaskData collectionCommandTaskData, final List resourceList, final List collectionList) {
        try {
            final List appKioskList = new ArrayList();
            final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
            final Long customerId = collectionCommandTaskData.getCustomerId();
            for (int i = 0; i < collectionList.size(); ++i) {
                if (MDMConfigUtil.getConfigIds(Long.valueOf(collectionList.get(i))).contains(183) && kioskHandler.isIOSKioskAppAutomation(collectionList.get(i), customerId)) {
                    appKioskList.add(collectionList.get(i));
                }
            }
            if (!appKioskList.isEmpty()) {
                this.kioskRemoveProfile(collectionCommandTaskData, resourceList, appKioskList);
            }
        }
        catch (final Exception e) {
            IOSRemoveProfileCommandTaskHandler.logger.log(Level.SEVERE, "Exception in remove profile collection", e);
        }
    }
    
    private void kioskRemoveProfile(final CollectionCommandTaskData collectionCommandTaskData, final List resourceList, final List collectionList) {
        final Long customerID = collectionCommandTaskData.getCustomerId();
        SeqCmdUtils.getInstance().removeSeqInstallCmd(resourceList, collectionList, "KioskInstallProfile");
        this.removeAnyKioskUpdateForResource(collectionList, resourceList, collectionCommandTaskData.getLoggedInUserId(), customerID);
    }
    
    private void removeAnyKioskUpdateForResource(final List collectionList, final List resourceList, final String userId, final Long customerId) {
        try {
            final List appCollectionList = new ArrayList();
            for (int i = 0; i < collectionList.size(); ++i) {
                final Long collectionId = collectionList.get(i);
                final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
                final JSONObject kioskObject = kioskHandler.isProfileApplicableForIOSKioskAutomation(collectionId, customerId);
                final Long appGroupId = kioskObject.optLong("APP_GROUP_ID");
                if (appGroupId != null && appGroupId != 0L) {
                    final Long appCollectionId = MDMUtil.getInstance().getProdCollectionIdFromAppGroupId(appGroupId);
                    appCollectionList.add(appCollectionId);
                }
            }
            this.addAppCollectionCommand(appCollectionList, resourceList, userId);
            SeqCmdUtils.getInstance().removeSeqInstallCmd(resourceList, appCollectionList, "KioskUpdateProfile");
        }
        catch (final Exception e) {
            IOSRemoveProfileCommandTaskHandler.logger.log(Level.SEVERE, "Exception in removing Kiosk Update Command", e);
        }
    }
    
    private void addAppCollectionCommand(final List collectionList, final List resourceList, final String userId) throws Exception {
        final List<Long> seqResList = SeqCmdUtils.getInstance().getResourcesRunningSeqCmd(resourceList);
        final List<Long> installCmdList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "KioskUpdateProfile");
        final List seqCmdList = SeqCmdUtils.getInstance().getSequentialIDforBaseID(installCmdList);
        if (seqCmdList.isEmpty() || seqResList.isEmpty()) {
            return;
        }
        for (int i = 0; i < seqCmdList.size(); ++i) {
            final Long seqCmdId = seqCmdList.get(i);
            final List curResList = SeqCmdUtils.getInstance().getResExecutingSeqCmd(seqResList, seqCmdId);
            final Long collectionId = SeqCmdUtils.getInstance().getCollectionIdFromSeqCmdID(seqCmdId);
            final List collectionSeqList = new ArrayList();
            collectionSeqList.add(collectionId);
            final List cmdList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "InstallApplication");
            final JSONObject params = new JSONObject();
            params.put("UserId", (Object)userId);
            SeqCmdRepository.getInstance().executeSequentially(curResList, cmdList, params);
            IOSRemoveProfileCommandTaskHandler.logger.log(Level.INFO, "Commands added after removing SeqCommands{0}:For Resource{1}", new Object[] { cmdList, curResList });
            DeviceCommandRepository.getInstance().assignCommandToDevices(cmdList, curResList);
        }
    }
    
    public List<Long> getResourceNotHavingProfile(final List<Long> resourceList, final Long profileId) {
        final List<Long> notHavingResourceList = new ArrayList<Long>(resourceList);
        try {
            final HashMap profileMap = MDMUtil.getInstance().getProfileDetails(profileId);
            if (profileMap != null) {
                final String profilePayloadIdentifier = profileMap.get("PROFILE_PAYLOAD_IDENTIFIER");
                final JSONObject filterObject = new JSONObject();
                filterObject.put("PAYLOAD_IDENTIFIER", (Object)profilePayloadIdentifier);
                final JSONObject resourceAppliedObject = new DeviceConfigPayloadsDataHandler().getInstalledProfilesDetails(resourceList, filterObject);
                for (final Long resourceId : notHavingResourceList) {
                    final JSONArray payloadArray = resourceAppliedObject.optJSONArray(resourceId.toString());
                    if (payloadArray != null && payloadArray.length() > 0) {
                        notHavingResourceList.remove(resourceId);
                    }
                }
            }
        }
        catch (final Exception ex) {}
        return notHavingResourceList;
    }
    
    static {
        IOSRemoveProfileCommandTaskHandler.logger = Logger.getLogger("MDMConfigLogger");
        IOSRemoveProfileCommandTaskHandler.mdmLogger = Logger.getLogger("MDMLogger");
    }
}
