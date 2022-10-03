package com.adventnet.sym.server.mdm.config.task;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.core.MDMUserHandler;
import java.util.Map;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

public class AssignCommandTaskProcessor
{
    private static AssignCommandTaskProcessor taskProcessor;
    private Logger configLogger;
    
    public AssignCommandTaskProcessor() {
        this.configLogger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static AssignCommandTaskProcessor getTaskProcessor() {
        return AssignCommandTaskProcessor.taskProcessor;
    }
    
    public void assignDeviceCommandTask(final Properties taskProps) {
        final HashMap deviceMap = ((Hashtable<K, HashMap>)taskProps).get("deviceMap");
        final HashMap collectionToPlatformMap = ((Hashtable<K, HashMap>)taskProps).get("collectionToPlatformMap");
        final HashMap profileToPlatformMap = ((Hashtable<K, HashMap>)taskProps).get("profileToPlatformMap");
        final HashMap<Long, Long> allprofileCollnMap = ((Hashtable<K, HashMap<Long, Long>>)taskProps).get("profileCollnMap");
        final HashMap<Long, List> collectionToApplicableRes = ((Hashtable<K, HashMap<Long, List>>)taskProps).get("collectionToApplicableResource");
        final String commandName = ((Hashtable<K, String>)taskProps).get("commandName");
        final Long customerId = ((Hashtable<K, Long>)taskProps).get("customerId");
        final Boolean isSilentInstall = ((Hashtable<K, Boolean>)taskProps).get("isSilentInstall");
        final String loggedOnUser = ((Hashtable<K, String>)taskProps).get("UserId");
        final Iterator platformItem = deviceMap.keySet().iterator();
        while (platformItem.hasNext()) {
            try {
                final int platformtype = platformItem.next();
                ((Hashtable<String, Integer>)taskProps).put("platformtype", platformtype);
                final List resourceList = new ArrayList(deviceMap.get(platformtype));
                ((Hashtable<String, List>)taskProps).put("resourceList", resourceList);
                final List collectionList = collectionToPlatformMap.get(platformtype);
                ((Hashtable<String, List>)taskProps).put("collectionList", collectionList);
                final Map<Long, Long> profileCollnMap = this.getPlatformSpecificProfileCollectionMap(profileToPlatformMap.get(platformtype), allprofileCollnMap);
                ((Hashtable<String, Map<Long, Long>>)taskProps).put("profileCollnMap", profileCollnMap);
                if (!resourceList.isEmpty() && !collectionList.isEmpty() && (commandName != null & !commandName.isEmpty())) {
                    this.configLogger.log(Level.INFO, "Init task for AssignDeviceCommandTask for resource:{0}; CollectionList:{1}; command name:{2}", new Object[] { resourceList, collectionList, commandName });
                    final CollectionCommandTaskData collectionCommandTaskData = new CollectionCommandTaskData();
                    collectionCommandTaskData.setCommandName(commandName);
                    collectionCommandTaskData.setPlatform(platformtype);
                    collectionCommandTaskData.setResourceList(resourceList);
                    collectionCommandTaskData.setCustomerId(customerId);
                    collectionCommandTaskData.setCollectionList(collectionList);
                    collectionCommandTaskData.setProfileCollectionMap(profileCollnMap);
                    collectionCommandTaskData.setAppSilentInstall(isSilentInstall);
                    collectionCommandTaskData.setLoggedInUserId(loggedOnUser);
                    collectionCommandTaskData.setTaskProperties(taskProps);
                    collectionCommandTaskData.setCollectionToApplicableResource(collectionToApplicableRes);
                    new CollectionCommandHandler().initCollectionCommandTask(collectionCommandTaskData);
                }
                else {
                    this.configLogger.log(Level.INFO, "Inputs empty for platform {3}, resource:{0}; CollectionList:{1}; command name:{2}", new Object[] { resourceList, collectionList, commandName, platformtype });
                }
            }
            catch (final Exception e) {
                this.configLogger.log(Level.SEVERE, "Exception in Assigndevice command ", e);
            }
        }
    }
    
    public void assignUserCommandTask(final Properties taskProps) {
        final List userList = ((Hashtable<K, List>)taskProps).get("usersList");
        final HashMap collectionToPlatformMap = ((Hashtable<K, HashMap>)taskProps).get("collectionToPlatformMap");
        final HashMap profileToPlatformMap = ((Hashtable<K, HashMap>)taskProps).get("profileToPlatformMap");
        final HashMap<Long, Long> allprofileCollnMap = ((Hashtable<K, HashMap<Long, Long>>)taskProps).get("profileCollnMap");
        final HashMap<Long, List> collectionToApplicableRes = ((Hashtable<K, HashMap<Long, List>>)taskProps).get("collectionToApplicableResource");
        final String commandName = ((Hashtable<K, String>)taskProps).get("commandName");
        final Long customerId = ((Hashtable<K, Long>)taskProps).get("customerId");
        final Boolean isSilentInstall = ((Hashtable<K, Boolean>)taskProps).get("isSilentInstall");
        final String loggedOnUser = ((Hashtable<K, String>)taskProps).get("UserId");
        final HashMap userMap = new MDMUserHandler().getDirectoryUserIdsBasedOnType(userList);
        if (userMap != null && !userMap.isEmpty()) {
            final List gsuiteUsers = new ArrayList(userMap.get(1));
            final Iterator collnPlatformIter = collectionToPlatformMap.keySet().iterator();
            while (collnPlatformIter.hasNext()) {
                try {
                    final int platformtype = collnPlatformIter.next();
                    ((Hashtable<String, Integer>)taskProps).put("platformtype", platformtype);
                    final List resourceList = new ArrayList(gsuiteUsers);
                    ((Hashtable<String, List>)taskProps).put("resourceList", resourceList);
                    final List collectionList = collectionToPlatformMap.get(platformtype);
                    ((Hashtable<String, List>)taskProps).put("collectionList", collectionList);
                    final Map<Long, Long> profileCollnMap = this.getPlatformSpecificProfileCollectionMap(profileToPlatformMap.get(platformtype), allprofileCollnMap);
                    ((Hashtable<String, Map<Long, Long>>)taskProps).put("profileCollnMap", profileCollnMap);
                    if (!resourceList.isEmpty() && !collectionList.isEmpty() && (commandName != null & !commandName.isEmpty())) {
                        this.configLogger.log(Level.INFO, "Init task for AssignUserCommandTask for resource:{0}; CollectionList:{1}; command name:{2}, platformType:{3}", new Object[] { resourceList, collectionList, commandName, platformtype });
                        final CollectionCommandTaskData collectionCommandTaskData = new CollectionCommandTaskData();
                        collectionCommandTaskData.setCommandName(commandName);
                        collectionCommandTaskData.setPlatform(platformtype);
                        collectionCommandTaskData.setResourceList(resourceList);
                        collectionCommandTaskData.setCustomerId(customerId);
                        collectionCommandTaskData.setCollectionList(collectionList);
                        collectionCommandTaskData.setProfileCollectionMap(profileCollnMap);
                        collectionCommandTaskData.setAppSilentInstall(isSilentInstall);
                        collectionCommandTaskData.setLoggedInUserId(loggedOnUser);
                        collectionCommandTaskData.setTaskProperties(taskProps);
                        collectionCommandTaskData.setCollectionToApplicableResource(collectionToApplicableRes);
                        new CollectionUserCommandHandler().initCollectionCommandTask(collectionCommandTaskData);
                    }
                    else {
                        this.configLogger.log(Level.INFO, "Inputs empty for platform {3}, resource:{0}; CollectionList:{1}; command name:{2}", new Object[] { resourceList, collectionList, commandName, platformtype });
                    }
                }
                catch (final Exception e) {
                    this.configLogger.log(Level.SEVERE, "Exception in Assigndevice command ", e);
                }
            }
        }
    }
    
    private Map<Long, Long> getPlatformSpecificProfileCollectionMap(final List profileList, final HashMap<Long, Long> allprofileCollnMap) {
        final HashMap<Long, Long> profileCollnMap = new HashMap<Long, Long>();
        final Iterator profileItem = profileList.iterator();
        while (profileItem.hasNext() && allprofileCollnMap != null) {
            final Long profileId = profileItem.next();
            profileCollnMap.put(profileId, allprofileCollnMap.get(profileId));
        }
        return profileCollnMap;
    }
    
    static {
        AssignCommandTaskProcessor.taskProcessor = new AssignCommandTaskProcessor();
    }
}
