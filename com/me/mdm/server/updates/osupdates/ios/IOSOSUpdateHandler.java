package com.me.mdm.server.updates.osupdates.ios;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.updates.osupdates.OSUpdatesDataHandler;
import java.util.List;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Properties;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.io.File;
import org.json.JSONObject;
import java.util.logging.Logger;

public class IOSOSUpdateHandler
{
    public static final String IOS_SCHEDULE_OS_UPDATE_XML = "schedule_os_update.xml";
    public static final String IOS_OS_UPDATE_RESTRICTION_XML = "restrict_os_update.xml";
    private static final Logger LOGGER;
    
    public JSONObject addIOSOSUpdatePolicyXML(final Long collectionId, final String mdmProfileDir, final String mdmProfileRelativeDirPath) {
        final JSONObject addedFilePath = new JSONObject();
        try {
            final String iOScmdFileName = mdmProfileDir + File.separator + "schedule_os_update.xml";
            final String iOSUpdateCmdUUID = PayloadHandler.getInstance().createScheduleOSUpdateCommandXML(collectionId, iOScmdFileName);
            final String iOScmdRelPath = mdmProfileRelativeDirPath + File.separator + "schedule_os_update.xml";
            final JSONObject scheduleObject = new JSONObject();
            scheduleObject.put("commandUUID", (Object)iOSUpdateCmdUUID);
            scheduleObject.put("commandFilePath", (Object)iOScmdRelPath);
            final String iosRestrictCmdFileName = mdmProfileDir + File.separator + "restrict_os_update.xml";
            final String iosRestrictionCmdUUID = PayloadHandler.getInstance().createOSUpdateRestrictionXML(collectionId, iosRestrictCmdFileName);
            final String iosRestrictionRelPath = mdmProfileRelativeDirPath + File.separator + "restrict_os_update.xml";
            final JSONObject restrictObject = new JSONObject();
            restrictObject.put("commandUUID", (Object)iosRestrictionCmdUUID);
            restrictObject.put("commandFilePath", (Object)iosRestrictionRelPath);
            addedFilePath.put("ScheduleOSUpdate", (Object)scheduleObject);
            addedFilePath.put("RestrictOSUpdates", (Object)restrictObject);
        }
        catch (final Exception ex) {
            IOSOSUpdateHandler.LOGGER.log(Level.SEVERE, "Exception while generating the XML for os update", ex);
        }
        return addedFilePath;
    }
    
    public boolean addOSUpdateCommand(final Long collectionId, final JSONObject iosCmdObject) {
        boolean status = false;
        try {
            final JSONObject osScheduleObject = iosCmdObject.optJSONObject("ScheduleOSUpdate");
            final String iOSUpdateCmdUUID = osScheduleObject.optString("commandUUID");
            final String iOScmdRelPath = osScheduleObject.optString("commandFilePath");
            final JSONObject iosRestrictObject = iosCmdObject.optJSONObject("RestrictOSUpdates");
            final String iosRestrictionCmdUUID = iosRestrictObject.optString("commandUUID");
            final String iosRestrictionRelPath = iosRestrictObject.optString("commandFilePath");
            final List metaDataList = new ArrayList();
            final Properties iOSUpdateProps = new Properties();
            iOSUpdateProps.setProperty("commandUUID", iOSUpdateCmdUUID);
            iOSUpdateProps.setProperty("commandType", "ScheduleOSUpdate");
            iOSUpdateProps.setProperty("commandFilePath", iOScmdRelPath);
            iOSUpdateProps.setProperty("dynamicVariable", String.valueOf(Boolean.TRUE));
            metaDataList.add(iOSUpdateProps);
            IOSOSUpdateHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy iOS collection command : {0}", "ScheduleOSUpdate");
            final Properties iosUpdateRestProp = new Properties();
            iosUpdateRestProp.setProperty("commandUUID", iosRestrictionCmdUUID);
            iosUpdateRestProp.setProperty("commandType", "RestrictOSUpdates");
            iosUpdateRestProp.setProperty("commandFilePath", iosRestrictionRelPath);
            iosUpdateRestProp.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
            metaDataList.add(iosUpdateRestProp);
            IOSOSUpdateHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy iOS collection command : {0}", "RestrictOSUpdates");
            final Properties iosUpdateRemoveRestProp = new Properties();
            iosUpdateRemoveRestProp.setProperty("commandUUID", "RemoveRestrictOSUpdates;Collection=" + collectionId.toString());
            iosUpdateRemoveRestProp.setProperty("commandType", "RemoveRestrictOSUpdates");
            iosUpdateRemoveRestProp.setProperty("commandFilePath", "--");
            iosUpdateRemoveRestProp.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
            metaDataList.add(iosUpdateRemoveRestProp);
            IOSOSUpdateHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy iOS collection command : {0}", "RemoveRestrictOSUpdates");
            IOSOSUpdateHandler.LOGGER.log(Level.INFO, "publishOSUpdatePolicy iOS collection metadata : {0} - {1} - {2}", new Object[] { iOSUpdateProps, iosUpdateRestProp, iosUpdateRemoveRestProp });
            DeviceCommandRepository.getInstance().addCollectionCommand(collectionId, metaDataList);
            IOSSeqCmdUtil.getInstance().addAutomateOSUpdateSequentiaCommand(collectionId);
            status = true;
        }
        catch (final Exception ex) {
            status = false;
            IOSOSUpdateHandler.LOGGER.log(Level.SEVERE, "Exception while adding osUpdate Command to devices.", ex);
        }
        return status;
    }
    
    public void checkAndAddRestrictOSUpdate(final Long resourceId, final Long collectionId) {
        final List<Long> collectionList = new ArrayList<Long>();
        collectionList.add(collectionId);
        if (new OSUpdatesDataHandler().isOSUpdatePolicyApplicableForResource(resourceId)) {
            final Long commandId = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "RestrictOSUpdates").get(0);
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            final List<Long> commandList = new ArrayList<Long>();
            commandList.add(commandId);
            IOSOSUpdateHandler.LOGGER.log(Level.INFO, "Going to add the Restrict OS Update command for resource:{0} and collection:{1}", new Object[] { resourceList, collectionList });
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, resourceList);
        }
    }
    
    public void checkAndRemoveRestrictOSUpdateCommand(final List resourceList) {
        if (resourceList != null && !resourceList.isEmpty()) {
            final Criteria restrictUpdate = new Criteria(new Column("MdCommands", "COMMAND_TYPE"), (Object)"RestrictOSUpdates", 0);
            final List<Long> commandIdList = new OSUpdatesDataHandler().getOSUpdateCommandForResourceOnCriteria(resourceList, restrictUpdate);
            IOSOSUpdateHandler.LOGGER.log(Level.INFO, "Going to delete the Restrict OS update command - {0} for resource -{1}", new Object[] { commandIdList, resourceList });
            DeviceCommandRepository.getInstance().deleteResourceCommand(resourceList, commandIdList);
            DeviceCommandRepository.getInstance().refreshResourceCommandsToCache(resourceList, 1);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
