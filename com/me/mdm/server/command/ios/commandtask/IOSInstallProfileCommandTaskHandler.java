package com.me.mdm.server.command.ios.commandtask;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import java.util.LinkedHashMap;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.me.mdm.server.profiles.IOSInstallProfileResponseProcessor;
import com.adventnet.sym.server.mdm.apps.AppLicenseMgmtHandler;
import com.adventnet.sym.server.mdm.apps.ios.AppleAppLicenseMgmtHandler;
import java.util.Properties;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.HashSet;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.Map;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import org.json.JSONObject;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.profiles.MDMCollectionNotApplicableHandler;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicableHandler;
import java.util.Collection;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.mdm.server.profiles.kiosk.IOSKioskProfileDataHandler;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.task.CollectionCommandTaskData;
import java.util.logging.Logger;

public class IOSInstallProfileCommandTaskHandler
{
    public static Logger logger;
    private static Logger mdmLogger;
    
    public void executeCommandForDevice(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        try {
            IOSInstallProfileCommandTaskHandler.logger.log(Level.INFO, "****** Install Profile sub task initiated : {0}", collectionCommandTaskData);
            final List resourceList = collectionCommandTaskData.getResourceList();
            final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
            final List collectionList = collectionCommandTaskData.getCollectionList();
            final List appKioskList = new ArrayList();
            final int collResConstant = 18;
            final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
            final Long customerId = collectionCommandTaskData.getCustomerId();
            if (profileCollectionMap != null && !profileCollectionMap.isEmpty()) {
                ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resourceList, profileCollectionMap, true, "InstallProfile");
                SeqCmdUtils.getInstance().removeEarlierVersionSeqProfileCommand(resourceList, profileCollectionMap);
                SeqCmdUtils.getInstance().removeEarlierVersionSeqCommand(resourceList, profileCollectionMap, "KioskInstallProfile");
            }
            final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
            for (int j = 0; j < collectionList.size(); ++j) {
                final Long collectionID = collectionList.get(j);
                final Long profileId = collectionCommandTaskData.getProfileIdForCollection(collectionID);
                final List configIds = MDMConfigUtil.getConfigIds(collectionID);
                final List collectionResourceList = new ArrayList(resourceList);
                if (configIds != null) {
                    final List naList = MDMConfigNotApplicableHandler.getInstance(profileId, collectionID).invokeConfigNotApplicationListeners(configIds, new ArrayList<Long>(resourceList), collectionCommandTaskData.getCustomerId());
                    collectionResourceList.removeAll(naList);
                    IOSInstallProfileCommandTaskHandler.logger.log(Level.INFO, "Marked following devices {0} for collection {1} as not applicable", new Object[] { naList, collectionList.get(j) });
                    final List collectionNAList = new MDMCollectionNotApplicableHandler(collectionID).invokeCollectionNotApplicableListener(configIds, new ArrayList<Long>(resourceList), collectionCommandTaskData.getCustomerId());
                    collectionResourceList.removeAll(collectionNAList);
                    IOSInstallProfileCommandTaskHandler.logger.log(Level.INFO, "Marked following devices {0} for collection List{1} as not applicable", new Object[] { collectionNAList, collectionList.get(j) });
                }
                if (configIds.contains(183) && kioskHandler.isIOSKioskAppAutomation(collectionID, customerId)) {
                    this.associateAppForKiosk(collectionCommandTaskData, collectionResourceList, collectionID);
                }
                else if (configIds.contains(753)) {
                    IOSInstallProfileCommandTaskHandler.logger.log(Level.INFO, "MacFirmware: Collection contains MacFirmware Policy , so marking following devices {0} for collection List\"{1} as not applicable ; As We have already added FirmwarePassword as sequential commands", new Object[] { collectionResourceList, collectionList.get(j) });
                }
                else {
                    IOSInstallProfileCommandTaskHandler.logger.log(Level.FINE, "Going to send commands for resource:{0} for collection:{1}", new Object[] { collectionResourceList, collectionID });
                    MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(collectionResourceList, collectionID, collResConstant, remark);
                    final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionID), collectionCommandTaskData.getCommandName());
                    final JSONObject seqCmdParams = new JSONObject();
                    seqCmdParams.put("UserId", (Object)collectionCommandTaskData.getLoggedInUserId());
                    SeqCmdRepository.getInstance().executeSequentially(collectionResourceList, commandList, seqCmdParams);
                    DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, collectionResourceList);
                }
            }
            NotificationHandler.getInstance().SendNotification(resourceList, collectionCommandTaskData.getPlatform());
            IOSInstallProfileCommandTaskHandler.logger.log(Level.INFO, "****** Install Profile sub task completed ******");
        }
        catch (final DataAccessException e) {
            IOSInstallProfileCommandTaskHandler.logger.log(Level.SEVERE, "Exception in iOS install profile command");
        }
        catch (final JSONException e2) {
            IOSInstallProfileCommandTaskHandler.logger.log(Level.SEVERE, "Exception in iOS install profile command");
        }
        catch (final SyMException e3) {
            IOSInstallProfileCommandTaskHandler.logger.log(Level.SEVERE, "Exception in iOS install profile command");
        }
        catch (final Exception e4) {
            IOSInstallProfileCommandTaskHandler.logger.log(Level.SEVERE, "Exception in iOS Install profile command", e4);
        }
    }
    
    private void associateAppForKiosk(final CollectionCommandTaskData collectionCommandTaskData, final List applicableResList, final Long profileCollectionID) {
        try {
            final List resourceList = new ArrayList(applicableResList);
            final Properties taskProps = collectionCommandTaskData.getTaskProperties();
            final boolean isGroup = ((Hashtable<K, Boolean>)taskProps).get("isGroup");
            final List groupList = ((Hashtable<K, List>)taskProps).get("groupList");
            final Long customerID = collectionCommandTaskData.getCustomerId();
            final String commandName = collectionCommandTaskData.getCommandName();
            final String loggedOnUser = collectionCommandTaskData.getLoggedInUserId();
            final Integer platformType = collectionCommandTaskData.getPlatform();
            final List collectionResourceList = new ArrayList();
            collectionResourceList.addAll(resourceList);
            final List baseProfileCollectionList = new ArrayList();
            baseProfileCollectionList.add(profileCollectionID);
            final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
            final JSONObject kioskAppJSON = kioskHandler.isProfileApplicableForIOSKioskAutomation(profileCollectionID, customerID);
            final Long appGroupId = kioskAppJSON.optLong("APP_GROUP_ID");
            final String bundleIdentifier = AppsUtil.getInstance().getIdentifierFromAppGroupID(appGroupId);
            final Long collectionID = MDMUtil.getInstance().getProdCollectionIdFromAppGroupIdNotInTrash(appGroupId);
            final int collResConstant = 18;
            final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
            if (collectionID != null) {
                final List associatedResourceList = new ArrayList();
                final AppsUtil appHandler = new AppsUtil();
                final Properties applicableForResource = appHandler.isiOSDeviceApplicableForSilentDistribution(collectionID, resourceList, customerID);
                final List appCollectionList = ((Hashtable<K, List>)applicableForResource).get("APPSCOLLECTION");
                final List applicableResourceList = ((Hashtable<K, List>)applicableForResource).get("RESOURCELIST");
                final List<Long> groupWithAppAssociated = new ArrayList<Long>();
                final List<Long> resWithSomeVersionOfApp = new ArrayList<Long>();
                LinkedHashMap groupWithAppAssociatedMap = null;
                Map resourceWithAppAssociatedMap = null;
                final Map<Object, HashSet> appCollectionToResource = new HashMap<Object, HashSet>();
                Long appProfileId = null;
                if (applicableResourceList != null) {
                    collectionResourceList.removeAll(applicableResourceList);
                }
                if (!appCollectionList.isEmpty() && applicableResourceList != null && !applicableResourceList.isEmpty()) {
                    appProfileId = MDMUtil.getInstance().getProfileDetailsForCollectionId(collectionID).get("PROFILE_ID");
                    resourceWithAppAssociatedMap = AppsUtil.getInstance().getManagedDevicesWithVersionOfApp(appProfileId, applicableResourceList);
                    resWithSomeVersionOfApp.addAll(resourceWithAppAssociatedMap.keySet());
                    final List cloneOfApplicableResourceList = new ArrayList(applicableResourceList);
                    cloneOfApplicableResourceList.removeAll(resWithSomeVersionOfApp);
                    if (isGroup) {
                        groupWithAppAssociatedMap = ProfileUtil.getInstance().getManagedGroupAssignedForProfile(groupList, appProfileId);
                        groupWithAppAssociated.addAll(groupWithAppAssociatedMap.keySet());
                    }
                    final Properties props = new Properties();
                    ((Hashtable<String, List>)props).put("resourceList", cloneOfApplicableResourceList);
                    ((Hashtable<String, List>)props).put("collectionList", appCollectionList);
                    ((Hashtable<String, Integer>)props).put("platformtype", platformType);
                    ((Hashtable<String, Long>)props).put("customerId", customerID);
                    ((Hashtable<String, Boolean>)props).put("isSilentInstall", false);
                    ((Hashtable<String, Boolean>)props).put("isNotify", false);
                    Properties profileToBusinessStore = new Properties();
                    final List appProfileList = new ArrayList();
                    appProfileList.add(appProfileId);
                    List tempResList = new ArrayList();
                    if (isGroup) {
                        tempResList = new ArrayList(groupList);
                    }
                    else {
                        tempResList = new ArrayList(cloneOfApplicableResourceList);
                    }
                    profileToBusinessStore = new ProfileAssociateHandler().getPreferredProfileToBusinessStoreMap(profileToBusinessStore, platformType, appProfileList, tempResList);
                    if (!profileToBusinessStore.isEmpty()) {
                        final Properties collectionToBusinessStore = new Properties();
                        if (profileToBusinessStore != null && !profileToBusinessStore.isEmpty()) {
                            ((Hashtable<Long, Object>)collectionToBusinessStore).put(collectionID, ((Hashtable<K, Object>)profileToBusinessStore).get(appProfileId));
                        }
                        ((Hashtable<String, Properties>)props).put("collectionToBusinessStore", collectionToBusinessStore);
                    }
                    final Properties appLicenseResourceList = new AppleAppLicenseMgmtHandler().assignAppForDevices(props);
                    final List notSupportedDeviceList = ((Hashtable<K, List>)appLicenseResourceList).get(AppLicenseMgmtHandler.notSupportedDeviceList);
                    final List failedList = ((Hashtable<K, List>)appLicenseResourceList).get(AppLicenseMgmtHandler.failedResourceList);
                    MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(notSupportedDeviceList, profileCollectionID, 7, "mdm.windows.app.no_compatible_package");
                    final IOSInstallProfileResponseProcessor processor = new IOSInstallProfileResponseProcessor();
                    MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(failedList, profileCollectionID, 7, "mdm.profile.ios.kiosk.appFailed" + processor.getAppViewURL(collectionID, platformType));
                    applicableResourceList.removeAll(notSupportedDeviceList);
                    applicableResourceList.removeAll(failedList);
                    associatedResourceList.addAll(applicableResourceList);
                    IOSInstallProfileCommandTaskHandler.logger.log(Level.INFO, "Associated resources For Kiosk Automation:{0}", applicableResourceList.toString());
                }
                if (!collectionResourceList.isEmpty()) {
                    this.updateStatusForKiosk(collectionResourceList, bundleIdentifier, profileCollectionID, customerID);
                    associatedResourceList.addAll(collectionResourceList);
                }
                if (!groupWithAppAssociated.isEmpty()) {
                    final HashMap groupMemberHash = MDMCustomGroupUtil.getInstance().getMemberIdsForGroup(groupWithAppAssociated, 1);
                    for (final Long groupId : groupWithAppAssociatedMap.keySet()) {
                        final Long appCollectionId = groupWithAppAssociatedMap.get(groupId);
                        final Map platformMap = groupMemberHash.get(groupId);
                        final HashSet iOSPlatformList = platformMap.get(1);
                        iOSPlatformList.retainAll(associatedResourceList);
                        if (!iOSPlatformList.isEmpty()) {
                            this.addResourceToAppCollection(appCollectionToResource, new ArrayList(iOSPlatformList), appCollectionId);
                            IOSInstallProfileCommandTaskHandler.logger.log(Level.INFO, "Group with app associated collection:{0} groupId:{1} resourceList:{2}", new Object[] { appCollectionId, groupId, iOSPlatformList });
                            associatedResourceList.removeAll(iOSPlatformList);
                        }
                    }
                }
                else if (!resWithSomeVersionOfApp.isEmpty()) {
                    for (final Long resourceId : resourceWithAppAssociatedMap.keySet()) {
                        final Long appCollectionId2 = resourceWithAppAssociatedMap.get(resourceId);
                        final List iOSPlatformList2 = new ArrayList();
                        iOSPlatformList2.add(resourceId);
                        IOSInstallProfileCommandTaskHandler.logger.log(Level.INFO, "Device with app associated collection:{0} resourceId:{1} resourceList:{2}", new Object[] { appCollectionId2, resourceId, iOSPlatformList2 });
                        this.addResourceToAppCollection(appCollectionToResource, iOSPlatformList2, appCollectionId2);
                    }
                    associatedResourceList.removeAll(resWithSomeVersionOfApp);
                }
                if (!associatedResourceList.isEmpty()) {
                    this.addResourceToAppCollection(appCollectionToResource, associatedResourceList, collectionID);
                }
                final JSONObject jsonObject;
                final JSONObject params = jsonObject = new JSONObject();
                IOSSeqCmdUtil.getInstance();
                jsonObject.put(IOSSeqCmdUtil.profileCollection, (Object)profileCollectionID);
                params.put(IOSSeqCmdUtil.appGroupId, (Object)appGroupId);
                params.put(IOSSeqCmdUtil.appIdentifier, (Object)bundleIdentifier);
                params.put("UserId", (Object)loggedOnUser);
                this.addSequentialCommandForKioskAutomation(params, appCollectionToResource, collResConstant, remark, collectionCommandTaskData);
            }
            else {
                final DataObject systemAppDo = AppsUtil.getInstance().getIOSSystemApps(new Criteria(new Column("IOSSystemApps", "IDENTIFIER"), (Object)bundleIdentifier, 0));
                if (systemAppDo.isEmpty()) {
                    this.updateStatusForKiosk(collectionResourceList, bundleIdentifier, profileCollectionID, customerID);
                }
                final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(baseProfileCollectionList, commandName);
                final JSONObject params2 = new JSONObject();
                params2.put("UserId", (Object)loggedOnUser);
                SeqCmdRepository.getInstance().executeSequentially(collectionResourceList, commandList, params2);
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, collectionResourceList);
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(collectionResourceList, profileCollectionID, collResConstant, remark);
                IOSInstallProfileCommandTaskHandler.logger.log(Level.INFO, "Resource having app installed{0}", collectionResourceList.toString());
            }
        }
        catch (final Exception e) {
            IOSInstallProfileCommandTaskHandler.logger.log(Level.SEVERE, "Error While adding Kiosk Install Application Task", e);
        }
    }
    
    private void addResourceToAppCollection(final Map<Object, HashSet> resourceAppMap, final List resourceList, final Long appCollectionId) {
        if (resourceAppMap.containsKey(appCollectionId)) {
            final HashSet existingResourceList = resourceAppMap.get(appCollectionId);
            existingResourceList.addAll(resourceList);
        }
        else {
            resourceAppMap.put(appCollectionId, new HashSet(resourceList));
        }
    }
    
    private void addSequentialCommandForKioskAutomation(final JSONObject params, final Map<Object, HashSet> resourceAppMap, final int collResConstant, final String remark, final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        final Iterator iterator = resourceAppMap.keySet().iterator();
        IOSSeqCmdUtil.getInstance();
        final Long profileCollectionID = params.getLong(IOSSeqCmdUtil.profileCollection);
        while (iterator.hasNext()) {
            final Long appCollectionId = iterator.next();
            Long commandID = IOSSeqCmdUtil.getInstance().getCommandIDForKioskSeqCommand(profileCollectionID, appCollectionId, "KioskInstallProfile", profileCollectionID);
            if (commandID == null) {
                final JSONObject configParams = new JSONObject();
                if (appCollectionId != null) {
                    configParams.put("appCollectionId", (Object)appCollectionId);
                }
                commandID = IOSSeqCmdUtil.getInstance().addSeqCmd(MDMConfigUtil.getConfigIds(profileCollectionID), profileCollectionID, collectionCommandTaskData.getCustomerId(), collectionCommandTaskData.getProfileIdForCollection(profileCollectionID), configParams);
            }
            final List commandList = new ArrayList();
            commandList.add(commandID);
            final List associatedResourceList = new ArrayList();
            associatedResourceList.addAll(resourceAppMap.get(appCollectionId));
            IOSSeqCmdUtil.getInstance();
            params.put(IOSSeqCmdUtil.appCollection, (Object)appCollectionId);
            SeqCmdRepository.getInstance().executeSequentially(associatedResourceList, commandList, params);
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(associatedResourceList, profileCollectionID, collResConstant, remark);
            IOSInstallProfileCommandTaskHandler.logger.log(Level.INFO, "Profile Associated For Automation:{0} for appCollection:{1}", new Object[] { associatedResourceList, appCollectionId });
        }
    }
    
    private void updateStatusForKiosk(final List collectionResourceList, final String bundleIdentifier, final Long profileCollectionID, final Long customerId) throws Exception {
        final List notapplicable = AppsUtil.getInstance().installedAppResourceListFromIdentifier(collectionResourceList, bundleIdentifier);
        if (notapplicable.size() != 0) {
            IOSInstallProfileCommandTaskHandler.logger.log(Level.INFO, "Resource not having app{0}", notapplicable.toString());
            final List supervisedDevice = ManagedDeviceHandler.getInstance().getSupervisedIosDevicelist(notapplicable);
            notapplicable.removeAll(supervisedDevice);
            final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
            kioskHandler.updateFailedKioskAppForResource(supervisedDevice, profileCollectionID, customerId);
        }
        IOSInstallProfileCommandTaskHandler.mdmLogger.log(Level.INFO, "Resources marked as failed due to unsupervised device", notapplicable.toString());
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(notapplicable, profileCollectionID, 7, "dc.mdm.device_mgmt.profile_supervised_eror_msg");
        MDMCollectionStatusUpdate.getInstance().updateCollnToResListErrorCode(notapplicable, profileCollectionID, 29000);
    }
    
    static {
        IOSInstallProfileCommandTaskHandler.logger = Logger.getLogger("MDMConfigLogger");
        IOSInstallProfileCommandTaskHandler.mdmLogger = Logger.getLogger("MDMLogger");
    }
}
