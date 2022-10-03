package com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.List;
import java.util.Collection;
import java.util.Set;
import java.util.Properties;
import java.util.Map;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssociationQueueHandler
{
    private static final Logger LOGGER;
    private static final Logger MDMLOGGER;
    public static Logger profileLogger;
    private final String separator = "\t";
    private final String smallSeparator = "  ";
    protected static AssociationQueueHandler queueHandler;
    
    public static AssociationQueueHandler getInstance() {
        if (AssociationQueueHandler.queueHandler == null) {
            AssociationQueueHandler.queueHandler = new AssociationQueueHandler();
        }
        return AssociationQueueHandler.queueHandler;
    }
    
    public void addCommandToQueue(final CommandQueueObject commandObject) throws Exception {
        final long postTime = System.currentTimeMillis();
        AssociationQueueHandler.MDMLOGGER.log(Level.INFO, "The customer ID is {0} The command name is {1}", new Object[] { commandObject.getCustomerId(), commandObject.getCommandName() });
        final String qFileName = commandObject.getCustomerId() + "-" + commandObject.getCommandName() + "-" + postTime + ".txt";
        final DCQueue queue = DCQueueHandler.getQueue("mdm-profile-collection");
        final DCQueueData queueData = new DCQueueData();
        queueData.fileName = qFileName;
        queueData.postTime = postTime;
        final Long timeLong = System.currentTimeMillis();
        queueData.queueData = MDMApiFactoryProvider.getAssociationQueueSerializer().serializeObject(commandObject);
        AssociationQueueHandler.MDMLOGGER.log(Level.INFO, "End time of serialization {0}", TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - timeLong));
        JsonConverterUtil.getInstance().convertObjectToJSON(commandObject);
        queueData.customerID = new Long(commandObject.getCustomerId());
        final Map queueExtnTableData = new HashMap();
        queueExtnTableData.put("CUSTOMER_ID", queueData.customerID);
        queueData.queueExtnTableData = queueExtnTableData;
        queueData.queueDataType = 1;
        AssociationQueueHandler.LOGGER.log(Level.INFO, "{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}{11}{12}{13}{14}{15}{16}{17}", new Object[] { commandObject.getCommandName(), "\t", commandObject.getCustomerId(), "\t", qFileName, "\t", "AddedToQueue", "\t", queueData.postTime, "\t", queue.getQueueDataCount(1), " In Memory", "\t", queue.getQueueDataCount(2), " In DB", "\t", "isQueueSuspended:", queue.isQueueSuspended() });
        final Properties hashProps = commandObject.getPropsFile();
        final HashMap<Integer, HashSet> profileList = ((Hashtable<K, HashMap<Integer, HashSet>>)hashProps).get("profileToPlatformMap");
        if (commandObject.getCommandType() == 1) {
            final HashMap<Integer, HashSet> deviceList = ((Hashtable<K, HashMap<Integer, HashSet>>)hashProps).get("deviceMap");
            if (profileList != null) {
                AssociationQueueHandler.profileLogger.log(Level.INFO, "{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}", new Object[] { qFileName, "  ", "DEVICES", "  ", profileList.get(1), "  ", profileList.get(2), "  ", profileList.get(3), "  ", profileList.get(4) });
                AssociationQueueHandler.profileLogger.log(Level.INFO, "{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}", new Object[] { qFileName, "  ", "PROFILES", "  ", deviceList.get(1), "  ", deviceList.get(2), "  ", deviceList.get(3), "  ", deviceList.get(4) });
                this.updateCollectionStatusForDevice(commandObject);
            }
        }
        else if (hashProps.containsKey("usersList") || commandObject.getCommandType() == 2) {
            final ArrayList<?> userList = ((Hashtable<K, ArrayList<?>>)hashProps).get("usersList");
            AssociationQueueHandler.profileLogger.log(Level.INFO, "{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}", new Object[] { qFileName, "  ", "DEVICES", "  ", profileList.get(1), "  ", profileList.get(2), "  ", profileList.get(3), "  ", profileList.get(4) });
            AssociationQueueHandler.profileLogger.log(Level.INFO, "{0}{1}{2}{3}{4}", new Object[] { qFileName, "  ", "PROFILES", "  ", userList.toString() });
            this.updateCollectionStatusForUser(commandObject);
        }
        queue.addToQueue(queueData);
    }
    
    private void updateCollectionStatusForDevice(final CommandQueueObject commandObject) {
        final int collResConstant = 12;
        final String remark = "mdm.profile.distribution.addedtoqueue";
        final Properties propsFile = commandObject.getPropsFile();
        final HashMap deviceMap = ((Hashtable<K, HashMap>)propsFile).get("deviceMap");
        final HashMap collectionToPlatformMap = ((Hashtable<K, HashMap>)propsFile).get("collectionToPlatformMap");
        if (!deviceMap.isEmpty()) {
            for (final int platformtype : deviceMap.keySet()) {
                final List resourceList = new ArrayList(deviceMap.get(platformtype));
                final List<Long> collectionList = collectionToPlatformMap.get(platformtype);
                try {
                    for (final Long collectionId : collectionList) {
                        final HashMap<Long, List> collectionToApplicableRes = ((Hashtable<K, HashMap<Long, List>>)propsFile).get("collectionToApplicableResource");
                        if (collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
                            final List collectionApplicableResource = collectionToApplicableRes.get(collectionId);
                            MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(collectionApplicableResource, collectionId, collResConstant, remark);
                        }
                        else {
                            MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceList, collectionId, collResConstant, remark);
                        }
                    }
                }
                catch (final Exception e) {
                    AssociationQueueHandler.MDMLOGGER.log(Level.SEVERE, "Cannot update the collection status", e);
                }
            }
        }
    }
    
    private void updateCollectionStatusForUser(final CommandQueueObject commandObject) {
        final int collResConstant = 12;
        final String remark = "mdm.profile.distribution.addedtoqueue";
        final Properties propsFile = commandObject.getPropsFile();
        final ArrayList<Long> usersList = ((Hashtable<K, ArrayList<Long>>)propsFile).get("usersList");
        if (!usersList.isEmpty()) {
            final HashMap collectionToPlatformMap = ((Hashtable<K, HashMap>)propsFile).get("collectionToPlatformMap");
            if (!collectionToPlatformMap.isEmpty()) {
                for (final int platformtype : collectionToPlatformMap.keySet()) {
                    final List<Long> collectionList = collectionToPlatformMap.get(platformtype);
                    try {
                        for (final Long collectionId : collectionList) {
                            MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(usersList, collectionId, collResConstant, remark);
                        }
                    }
                    catch (final Exception e) {
                        AssociationQueueHandler.MDMLOGGER.log(Level.SEVERE, "Cannot update the collection status", e);
                    }
                }
            }
        }
        else {
            AssociationQueueHandler.MDMLOGGER.log(Level.INFO, "No need to do status update as user list is not available");
        }
    }
    
    private String getAppAssignmentQueueDataFileName(final Long customerId) {
        return customerId + "_" + MDMUtil.getCurrentTimeInMillis() + "_app_assignment_qdata.txt";
    }
    
    public void executeTask(final HashMap taskInfoMap, final Properties taskProps) throws Exception {
        final String commandName = ((Hashtable<K, String>)taskProps).get("commandName");
        final Integer commandType = ((Hashtable<K, Integer>)taskProps).get("commandType");
        final Boolean isProfileQueueOn = MDMFeatureParamsHandler.getInstance().isFeatureAvailableGlobally("ProfileAssociationQueue", false);
        if ((commandName.equalsIgnoreCase("InstallProfile") || commandName.equalsIgnoreCase("RemoveProfile") || commandName.equalsIgnoreCase("SyncAnnouncement")) && isProfileQueueOn) {
            final CommandQueueObject commandObject = new CommandQueueObject();
            commandObject.setCommandName(taskProps.getProperty("commandName"));
            commandObject.setCustomerId(((Hashtable<K, Object>)taskProps).get("customerId") + "");
            commandObject.setCommandType(commandType);
            commandObject.setPropsFile(taskProps);
            this.addCommandToQueue(commandObject);
        }
        else if (commandName.equalsIgnoreCase("InstallApplication") || commandName.equalsIgnoreCase("RemoveApplication")) {
            final DCQueueData queueData = new DCQueueData();
            queueData.fileName = this.getAppAssignmentQueueDataFileName(((Hashtable<K, Long>)taskProps).get("customerId"));
            queueData.queueData = MDMApiFactoryProvider.getAssociationQueueSerializer().serializeProperty(taskProps);
            queueData.postTime = MDMUtil.getCurrentTimeInMillis();
            queueData.queueDataType = ((commandType != null && commandType == 2) ? 2 : 1);
            final DCQueue dcQueue = DCQueueHandler.getQueue("app-assignment-queue");
            dcQueue.addToQueue(queueData);
            AssociationQueueHandler.profileLogger.log(Level.INFO, "Data added successfully in app assignment queue");
        }
        else if (commandType == 2) {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.adventnet.sym.server.mdm.config.task.AssignUserCommandTask", taskInfoMap, taskProps);
        }
        else {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.adventnet.sym.server.mdm.config.task.AssignDeviceCommandTask", taskInfoMap, taskProps);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMAsyncQueueAccessLogger");
        MDMLOGGER = Logger.getLogger("MDMLogger");
        AssociationQueueHandler.profileLogger = Logger.getLogger("MDMProfileConfigLogger");
        AssociationQueueHandler.queueHandler = null;
    }
}
