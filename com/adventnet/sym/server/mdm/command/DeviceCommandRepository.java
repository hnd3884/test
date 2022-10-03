package com.adventnet.sym.server.mdm.command;

import java.util.Hashtable;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.profiles.mac.MDMFilevaultPersonalRecoveryKeyImport;
import com.me.mdm.server.apple.useraccount.AppleMultiUserUtils;
import com.adventnet.sym.server.mdm.security.MacDeviceUserUnlockHandler;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Map;
import java.util.PriorityQueue;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.me.mdm.server.seqcommands.SeqCmdDBUtil;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.command.CommandQueueItem;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.me.mdm.api.command.schedule.ScheduledActionsUtils;
import com.me.mdm.api.command.schedule.ScheduledCommandToCollectionHandler;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.me.mdm.server.command.CommandStatusHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONArray;
import java.util.HashSet;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.List;
import java.util.Properties;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import com.adventnet.persistence.WritableDataObject;
import java.util.Collection;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.lang.reflect.Method;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;

public class DeviceCommandRepository
{
    protected static DeviceCommandRepository mdmCommandUtil;
    public Logger logger;
    Logger accesslogger;
    public static final int RUNTIME_COMMAND_DATA_TYPE = 0;
    public static final int FS_COMMAND_DATA_TYPE = 1;
    public static final int DB_COMMAND_DATA_TYPE = 2;
    protected static final Integer SYNCHRONIZE_ADD_COMMAND;
    protected static final Integer SYNCHRONIZE_COMMAND_CACHE;
    public static final String RES_SIZE_KEY = "command_res_size";
    public static final String CMND_SIZE_KEY = "command_len_size";
    public Integer resSize;
    public Integer cmndSize;
    public Integer defaultChunkSize;
    
    public DeviceCommandRepository() {
        this.logger = Logger.getLogger("MDMLogger");
        this.accesslogger = Logger.getLogger("MDMCommandsLogger");
        this.defaultChunkSize = 500;
        String chunkSizeStr = null;
        try {
            chunkSizeStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("command_res_size");
            this.resSize = ((chunkSizeStr == null) ? this.defaultChunkSize : Integer.parseInt(chunkSizeStr));
            final String profileChunkSizeStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("command_len_size");
            this.cmndSize = ((profileChunkSizeStr == null) ? 5 : Integer.parseInt(profileChunkSizeStr));
        }
        catch (final Exception e) {
            this.resSize = 500;
            this.cmndSize = 5;
        }
    }
    
    public static DeviceCommandRepository getInstance() {
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            try {
                final Method getInstance = Class.forName("com.me.mdmcloud.server.command.MDMCloudDeviceCommandRepository").getDeclaredMethod("getInstance", (Class<?>[])new Class[0]);
                final DeviceCommandRepository mdmCloudCommandUtil = (DeviceCommandRepository)getInstance.invoke(null, (Object[])null);
                if (mdmCloudCommandUtil != null) {
                    return mdmCloudCommandUtil;
                }
            }
            catch (final Exception e) {
                Logger.getLogger("MDMLogger").log(Level.INFO, "Exception while getting MDMCloudDeviceCommandRepository Instance..");
            }
        }
        if (DeviceCommandRepository.mdmCommandUtil == null) {
            DeviceCommandRepository.mdmCommandUtil = new DeviceCommandRepository();
        }
        return DeviceCommandRepository.mdmCommandUtil;
    }
    
    public Long addSequentialCommand(final String command) {
        return this.addCommand(command);
    }
    
    public Long addCommandWithPriority(final String cmdUUID, final int priority) throws Exception {
        final DeviceCommand command = new DeviceCommand();
        command.priority = priority;
        command.commandUUID = cmdUUID;
        command.commandStr = "--";
        command.commandType = cmdUUID;
        return this.addCommand(command);
    }
    
    protected Long addCommand(final DeviceCommand command) throws Exception {
        this.logger.log(Level.INFO, "addCommand(): command.commandUUID", command.commandUUID);
        Long commandID = null;
        try {
            if (command.commandStr == null) {
                command.commandStr = "--";
            }
            if (command.commandUUID == null) {
                return -1L;
            }
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommands"));
            final Criteria criteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)command.commandUUID, 0, false);
            sQuery.setCriteria(criteria);
            sQuery.addSelectColumn(new Column("MdCommands", "COMMAND_ID"));
            DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (dataObject.isEmpty()) {
                try {
                    final Row commandRow = new Row("MdCommands");
                    commandRow.set("COMMAND_UUID", (Object)command.commandUUID);
                    if (command.commandFilePath != null) {
                        commandRow.set("COMMAND_DATA_FILE_PATH", (Object)command.commandFilePath);
                    }
                    if (command.commandStr != null) {
                        commandRow.set("COMMAND_DATA_VALUE", (Object)command.commandStr);
                    }
                    commandRow.set("COMMAND_DATA_TYPE", (Object)command.commandDataType);
                    commandRow.set("COMMAND_TYPE", (Object)command.commandType);
                    commandRow.set("COMMAND_DYNAMIC_VARIABLE", (Object)command.dynamicVariable);
                    commandRow.set("PRIORITY", (Object)command.priority);
                    dataObject.addRow(commandRow);
                    dataObject = MDMUtil.getPersistence().add(dataObject);
                }
                catch (final DataAccessException exp) {
                    final int errorCode = exp.getErrorCode();
                    if (errorCode != 1001) {
                        throw exp;
                    }
                    this.accesslogger.log(Level.WARNING, "DeviceCommand Seems to be already added by another Transaction, So gracefully handle the Exception", (Throwable)exp);
                    dataObject = MDMUtil.getPersistence().get(sQuery);
                }
            }
            else if (!command.commandStr.equalsIgnoreCase("--")) {
                final Row commandRow = dataObject.getRow("MdCommands");
                commandRow.set("COMMAND_DATA_VALUE", (Object)command.commandStr);
                dataObject.updateRow(commandRow);
                MDMUtil.getPersistence().update(dataObject);
            }
            commandID = (Long)dataObject.getFirstValue("MdCommands", "COMMAND_ID");
        }
        catch (final DataAccessException exp2) {
            this.accesslogger.log(Level.WARNING, "Exception While adding Command {0}", (Throwable)exp2);
            throw exp2;
        }
        return commandID;
    }
    
    protected JSONObject addCommand(final HashMap commandMap) throws Exception {
        this.logger.log(Level.INFO, "addCommand(): commandList:", commandMap.toString());
        final JSONObject responseJSON = new JSONObject();
        try {
            final ArrayList<String> commandUUIDList = new ArrayList<String>(commandMap.keySet());
            if (!commandUUIDList.isEmpty()) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdCommands"));
                final Criteria criteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)commandUUIDList.toArray(), 8, false);
                selectQuery.setCriteria(criteria);
                selectQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_ID"));
                selectQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_UUID"));
                DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("MdCommands");
                    while (iterator.hasNext()) {
                        final Row mdCommandsRow = iterator.next();
                        final Long commandId = (Long)mdCommandsRow.get("COMMAND_ID");
                        final String commandUUID = (String)mdCommandsRow.get("COMMAND_UUID");
                        commandUUIDList.remove(commandUUID);
                        responseJSON.put(commandUUID, (Object)commandId);
                    }
                }
                if (!commandUUIDList.isEmpty()) {
                    dataObject = (DataObject)new WritableDataObject();
                    for (final String commandUUID2 : commandUUIDList) {
                        final DeviceCommand deviceCommand = commandMap.get(commandUUID2);
                        if (deviceCommand.commandStr == null) {
                            deviceCommand.commandStr = "--";
                        }
                        final Row mdCommandsRow2 = new Row("MdCommands");
                        mdCommandsRow2.set("COMMAND_UUID", (Object)deviceCommand.commandUUID);
                        if (deviceCommand.commandFilePath != null) {
                            mdCommandsRow2.set("COMMAND_DATA_FILE_PATH", (Object)deviceCommand.commandFilePath);
                        }
                        if (deviceCommand.commandStr != null) {
                            mdCommandsRow2.set("COMMAND_DATA_VALUE", (Object)deviceCommand.commandStr);
                        }
                        mdCommandsRow2.set("COMMAND_DATA_TYPE", (Object)deviceCommand.commandDataType);
                        mdCommandsRow2.set("COMMAND_TYPE", (Object)deviceCommand.commandType);
                        mdCommandsRow2.set("COMMAND_DYNAMIC_VARIABLE", (Object)deviceCommand.dynamicVariable);
                        mdCommandsRow2.set("PRIORITY", (Object)deviceCommand.priority);
                        dataObject.addRow(mdCommandsRow2);
                    }
                    final DataObject finalDo = MDMUtil.getPersistenceLite().add(dataObject);
                    if (!finalDo.isEmpty()) {
                        final Iterator iterator2 = finalDo.getRows("MdCommands");
                        while (iterator2.hasNext()) {
                            final Row mdCommandsRow3 = iterator2.next();
                            final Long commandId2 = (Long)mdCommandsRow3.get("COMMAND_ID");
                            final String commandUUID3 = (String)mdCommandsRow3.get("COMMAND_UUID");
                            commandUUIDList.remove(commandUUID3);
                            responseJSON.put(commandUUID3, (Object)commandId2);
                        }
                    }
                }
            }
            return responseJSON;
        }
        catch (final DataAccessException exp) {
            this.accesslogger.log(Level.WARNING, "Exception While adding Commands", (Throwable)exp);
            throw exp;
        }
    }
    
    public void addManageAppCommand(final Long collectionID) throws DataAccessException {
        final String commandUUID = "ManageApplication;Collection=" + Long.toString(collectionID);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCollectionCommand"));
        final Join mdCommandsJoin = new Join("MdCollectionCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2);
        sQuery.addJoin(mdCommandsJoin);
        sQuery.addSelectColumn(new Column("MdCommands", "*"));
        final Criteria mdCollectionCmdCri = new Criteria(new Column("MdCollectionCommand", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria mdCmdCri = new Criteria(new Column("MdCommands", "COMMAND_TYPE"), (Object)"InstallApplication", 0);
        sQuery.setCriteria(mdCollectionCmdCri.and(mdCmdCri));
        final Row row = MDMUtil.getPersistence().get(sQuery).getRow("MdCommands");
        final Properties manageAppCommandProp = new Properties();
        manageAppCommandProp.setProperty("commandUUID", commandUUID);
        manageAppCommandProp.setProperty("commandType", "ManageApplication");
        manageAppCommandProp.setProperty("commandFilePath", row.get("COMMAND_DATA_FILE_PATH").toString());
        manageAppCommandProp.setProperty("dynamicVariable", String.valueOf(Boolean.TRUE));
        final List collectionMetaDataList = new ArrayList();
        collectionMetaDataList.add(manageAppCommandProp);
        this.addCollectionCommand(collectionID, collectionMetaDataList);
    }
    
    public void addCollectionCommand(final Long collectionID, final List collectionMetaDataList) {
        this.logger.log(Level.INFO, "addCollectionCommand(): collectionID:", collectionID);
        this.logger.log(Level.INFO, "addCollectionCommand(): collectionMetaDataList:", collectionMetaDataList);
        try {
            final Criteria cCollection = new Criteria(new Column("MdCollectionCommand", "COLLECTION_ID"), (Object)collectionID, 0);
            final DataObject commandDO = MDMUtil.getPersistence().get("MdCollectionCommand", cCollection);
            final DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
            for (int i = 0; i < collectionMetaDataList.size(); ++i) {
                final Properties collectionMetaData = collectionMetaDataList.get(i);
                final String commandUUID = ((Hashtable<K, String>)collectionMetaData).get("commandUUID");
                final String commandFilePath = ((Hashtable<K, String>)collectionMetaData).get("commandFilePath");
                final String commandType = ((Hashtable<K, String>)collectionMetaData).get("commandType");
                final Boolean dynamicVariable = Boolean.valueOf(((Hashtable<K, String>)collectionMetaData).get("dynamicVariable"));
                final String priority = ((Hashtable<K, String>)collectionMetaData).get("priority");
                final DeviceCommand command = new DeviceCommand();
                command.commandUUID = commandUUID;
                command.commandFilePath = commandFilePath;
                command.commandType = commandType;
                command.commandDataType = 1;
                command.dynamicVariable = dynamicVariable;
                command.priority = 40;
                if (!MDMStringUtils.isEmpty(priority)) {
                    command.priority = Integer.parseInt(priority);
                }
                final Long commandID = this.addCommand(command);
                Row collectionCommandRow = commandDO.getRow("MdCollectionCommand", new Criteria(new Column("MdCollectionCommand", "COMMAND_ID"), (Object)commandID, 0));
                if (collectionCommandRow == null) {
                    collectionCommandRow = new Row("MdCollectionCommand");
                    collectionCommandRow.set("COLLECTION_ID", (Object)collectionID);
                    collectionCommandRow.set("COMMAND_ID", (Object)commandID);
                    dataObject.addRow(collectionCommandRow);
                }
            }
            if (!dataObject.isEmpty()) {
                MDMUtil.getPersistence().add(dataObject);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addCollectionCommand", exp);
        }
    }
    
    public Integer getProfileScopeForCollection(final Long collectionId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        final Join profileToCollectionJoin = new Join("Collection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join profileJoin = new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        sQuery.addJoin(profileToCollectionJoin);
        sQuery.addJoin(profileJoin);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionId, 0));
        final Row row = MDMUtil.getPersistence().get(sQuery).getRow("Profile");
        Integer scope = 0;
        if (row != null) {
            scope = (Integer)row.get("SCOPE");
        }
        return scope;
    }
    
    public List getCollectionIdsCommandList(final List collectionList, final String commandName) {
        this.logger.log(Level.INFO, "getCollectionIdsCommandList(): collectionList: {0}", collectionList);
        this.logger.log(Level.INFO, "getCollectionIdsCommandList(): commandName: {0}", commandName);
        final List collectionCommandIdList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCollectionCommand"));
            final Join commandJoin = new Join("MdCollectionCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2);
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_TYPE"), (Object)commandName, 0, false);
            selectQuery.addJoin(commandJoin);
            DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
            final Criteria criteria = new Criteria(Column.getColumn("MdCollectionCommand", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
            selectQuery.setCriteria(criteria.and(commandCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdCollectionCommand", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MdCommands", "*"));
            dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator rowIterator = dataObject.getRows("MdCollectionCommand");
                while (rowIterator.hasNext()) {
                    final Row collectionCommandRow = rowIterator.next();
                    final Long collectionID = (Long)collectionCommandRow.get("COLLECTION_ID");
                    final Long commandID = (Long)collectionCommandRow.get("COMMAND_ID");
                    collectionCommandIdList.add(commandID);
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getCollectionIdsCommandList", exp);
        }
        return collectionCommandIdList;
    }
    
    public List getCollectionIDListForCmdID(final List cmdID) {
        final List collectionList = new ArrayList();
        final Table mdcollectioncmd = new Table("MdCollectionCommand");
        final SelectQuery collectionIdQuery = (SelectQuery)new SelectQueryImpl(mdcollectioncmd);
        final Column all = new Column("MdCollectionCommand", "*");
        final Criteria criteria = new Criteria(new Column("MdCollectionCommand", "COMMAND_ID"), (Object)cmdID.toArray(), 8);
        collectionIdQuery.addSelectColumn(all);
        collectionIdQuery.setCriteria(criteria);
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(collectionIdQuery);
            final Iterator iterator = dataObject.getRows("MdCollectionCommand");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                collectionList.add(row.get("COLLECTION_ID"));
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in getCollectionIDListForCmdID", (Throwable)e);
        }
        return collectionList;
    }
    
    public void addSecurityCommand(final Long resourceID, final String commandName) {
        this.addSecurityCommand(resourceID, commandName, 1);
    }
    
    public void addSecurityCommand(final Long resourceID, final String commandName, final int commandRepType) {
        this.logger.log(Level.INFO, "addSecurityCommand(): resourceID:{0}", resourceID);
        this.logger.log(Level.INFO, "addSecurityCommand(): commandName:{0}", commandName);
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandName;
        command.commandType = commandName;
        command.commandStr = "--";
        command.priority = 100;
        Long commandID = null;
        try {
            commandID = this.addCommand(command);
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            this.assignCommandToDevices(commandID, resourceList, commandRepType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addSecurityCommand", exp);
        }
    }
    
    public void addSecurityCommand(final List<Long> resourceList, final String commandName, final int commandRepType) {
        this.logger.log(Level.INFO, "addSecurityCommand(): resourceIDs:{0}", resourceList);
        this.logger.log(Level.INFO, "addSecurityCommand(): commandName:{0}", commandName);
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandName;
        command.commandType = commandName;
        command.commandStr = "--";
        command.priority = 100;
        Long commandID = null;
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList, commandRepType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addSecurityCommand", exp);
        }
    }
    
    public void addAndAssignCommand(final Long resourceID, final String commandName, final int commandRepType) {
        this.logger.log(Level.INFO, "addAndAssignCommand(): resourceID:{0}", resourceID);
        this.logger.log(Level.INFO, "addAndAssignCommand(): commandName:{0}", commandName);
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandName;
        command.commandType = commandName;
        command.commandStr = "--";
        Long commandID = null;
        try {
            commandID = this.addCommand(command);
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            this.assignCommandToDevices(commandID, resourceList, commandRepType);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public Long addCommand(final String commandName) {
        Long commandID = null;
        this.logger.log(Level.INFO, "addCommand()");
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandName;
        command.commandType = commandName;
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addCommand", exp);
        }
        return commandID;
    }
    
    public Long addEnrollmentCommand(final String resourceUDID) {
        this.logger.log(Level.INFO, "addEnrollmentCommand(): resourceUDID:", resourceUDID);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "Enrollment";
        command.commandType = "Enrollment";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevice(commandID, resourceUDID);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addEnrollmentCommand", exp);
        }
        return commandID;
    }
    
    public Long addEnrollmentCommand(final Long resourceID) {
        this.logger.log(Level.INFO, "addEnrollmentCommand(): resourceID:", resourceID);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "Enrollment";
        command.commandType = "Enrollment";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            final List<Long> resourceIdList = new ArrayList<Long>();
            resourceIdList.add(resourceID);
            this.assignCommandToDevices(commandID, resourceIdList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addEnrollmentCommand", exp);
        }
        return commandID;
    }
    
    public void addDefaultAppCatalogCommand(final Long resourceID, final String commandUUID) {
        this.logger.log(Level.INFO, "addDefaultAppCatalogCommand(): resourceID:", resourceID);
        try {
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            this.addDefaultAppCatalogCommand(resourceList, commandUUID);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addDefaultAppCatalogCommand", exp);
        }
    }
    
    public void addDefaultAppCatalogCommand(final List resourceList, final String commandUUID) {
        this.logger.log(Level.INFO, "addDefaultAppCatalogCommand(): resourceID List:", resourceList);
        try {
            Long commandID = null;
            final DeviceCommand command = new DeviceCommand();
            command.commandUUID = commandUUID;
            command.commandType = commandUUID;
            command.commandStr = "--";
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addDefaultAppCatalogCommand", exp);
        }
    }
    
    public void addUserAssignmentCommand(final Long resourceID, final String commandUUID) {
        this.logger.log(Level.INFO, "addUserAssignmentCommand(): resourceID:", resourceID);
        try {
            Long commandID = null;
            final DeviceCommand command = new DeviceCommand();
            command.commandUUID = commandUUID;
            command.commandType = commandUUID;
            command.commandStr = "--";
            commandID = this.addCommand(command);
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public Long addWindowsCommand(final List resourceList, final String commandName) {
        this.logger.log(Level.INFO, "addWindowsCommand(): resourceID: {0} commandName {1}", new Object[] { resourceList, commandName });
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandName;
        command.commandType = commandName;
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList, 1);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addWindowsCommand", exp);
        }
        return commandID;
    }
    
    public boolean addNativeAppScanCommands(final DeviceDetails deviceDetails) {
        boolean isNativeAppCommandSent = Boolean.FALSE;
        if (deviceDetails.nativeAgentInstalled && deviceDetails.locationTrackingEnabled && deviceDetails.platform == 3 && !ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(deviceDetails.osVersion, 10.0f) && ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(deviceDetails.osVersion, 8.1f) && deviceDetails.osVersion != null && deviceDetails.osVersion.startsWith("9.2.")) {
            this.addLocationCommand(Arrays.asList(deviceDetails.resourceId), 2);
            isNativeAppCommandSent = Boolean.TRUE;
        }
        return isNativeAppCommandSent;
    }
    
    public void addiOSDeviceSyncCommandsAndNotify(final Long resourceID) {
        try {
            final DeviceDetails deviceDetails = new DeviceDetails(resourceID);
            final ArrayList deviceList = new ArrayList();
            deviceList.add(resourceID);
            final HashMap commandMap = new HashMap();
            final List<String> iosScanCommandList = this.getIosSyncCommandList(deviceDetails);
            final List profileTypeCommandList = new ArrayList();
            for (int i = 0; i < iosScanCommandList.size(); ++i) {
                final DeviceCommand command = new DeviceCommand();
                final String commandUUID = iosScanCommandList.get(i);
                command.commandUUID = commandUUID;
                if (!commandUUID.contains("USER_INVOKED")) {
                    command.commandType = commandUUID;
                }
                else {
                    command.commandType = commandUUID.split(";")[0];
                }
                command.commandStr = "--";
                commandMap.put(command.commandUUID, command);
            }
            final JSONObject commandUUIDcommandIDJSON = getInstance().addCommand(commandMap);
            final HashMap iosCommandMap = new HashMap();
            for (final Object object : iosScanCommandList) {
                profileTypeCommandList.add(JSONUtil.optLongForUVH(commandUUIDcommandIDJSON, object.toString(), (Long)null));
            }
            if (!profileTypeCommandList.isEmpty()) {
                iosCommandMap.put(deviceDetails.resourceId, profileTypeCommandList);
            }
            this.assignCommandToDevices(iosCommandMap, 1);
            NotificationHandler.getInstance().SendNotification(deviceList, 1);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in addDeviceSyncCommands", e);
        }
    }
    
    public void addDeviceScanCommand(final ArrayList<DeviceDetails> deviceDetailsList, final JSONObject params) {
        this.logger.log(Level.INFO, "addDeviceScanCommand(): deviceDetailsList:{0}", deviceDetailsList.toArray());
        try {
            final Long userId = JSONUtil.optLongForUVH(params, "USER_ID".toLowerCase(), Long.valueOf(-1L));
            final String scanPersonalApps = params.optString("DoNotScanPersonalApps", (String)null);
            Boolean isAetUploaded = false;
            if (!deviceDetailsList.isEmpty()) {
                final DeviceDetails tempDeviceDetails = deviceDetailsList.get(0);
                isAetUploaded = WpAppSettingsHandler.getInstance().isAETUploaded(tempDeviceDetails.customerId);
            }
            final JSONObject commandUUIDcommandIDJSON = params.getJSONObject("commandUUIDcommandIDMap");
            final HashMap profileTypeCommandMap = new HashMap();
            final HashMap nativeTypeCommandMap = new HashMap();
            final HashMap criteriaList = new HashMap();
            final HashSet commandIdCriteriaList = new HashSet();
            final JSONObject commandStatusJSON = new JSONObject();
            for (final DeviceDetails deviceDetails : deviceDetailsList) {
                final JSONArray commandsJSONArray = new JSONArray();
                final ArrayList<Long> profileTypeCommandList = new ArrayList<Long>();
                final ArrayList<Long> nativeTypeCommandList = new ArrayList<Long>();
                final Boolean isMac = deviceDetails.platform == 1 && (deviceDetails.modelType == 3 || deviceDetails.modelType == 4);
                final JSONObject privacyJson = deviceDetails.privacySettingsJSON;
                final int fetchApp = privacyJson.optInt("fetch_installed_app");
                final int fetchLocation = privacyJson.optInt("fetch_location");
                final Boolean isLocationFetchDisabled = !deviceDetails.locationTrackingEnabled || fetchLocation == 2;
                if (deviceDetails.platform == 1) {
                    final List iosScanCommand = this.getIosInvScanCommandList(deviceDetails);
                    if (userId != -1L && iosScanCommand.contains("DeviceInformation")) {
                        iosScanCommand.remove("DeviceInformation");
                        iosScanCommand.add("DeviceInformation;USER_INVOKED");
                    }
                    final long odlocationAgentVersion = 1402L;
                    if (deviceDetails.agentVersionCode < odlocationAgentVersion || isLocationFetchDisabled || isMac) {
                        iosScanCommand.remove("GetLocation");
                    }
                    if (!deviceDetails.isSupervised) {
                        iosScanCommand.remove("AvailableOSUpdates");
                    }
                    for (final Object object : iosScanCommand) {
                        profileTypeCommandList.add(JSONUtil.optLongForUVH(commandUUIDcommandIDJSON, object.toString(), (Long)null));
                    }
                }
                else if (deviceDetails.platform == 3) {
                    final Boolean isWin10OrAbove = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(deviceDetails.osVersion, 10.0f);
                    if (isAetUploaded && !isWin10OrAbove) {
                        profileTypeCommandList.add(JSONUtil.optLongForUVH(commandUUIDcommandIDJSON, "InstalledApplicationList", (Long)null));
                        profileTypeCommandList.add(JSONUtil.optLongForUVH(commandUUIDcommandIDJSON, "DeviceInformation", (Long)null));
                    }
                    else {
                        profileTypeCommandList.add(JSONUtil.optLongForUVH(commandUUIDcommandIDJSON, "DeviceInformation", (Long)null));
                    }
                    if (isWin10OrAbove) {
                        profileTypeCommandList.add(JSONUtil.optLongForUVH(commandUUIDcommandIDJSON, "InstalledApplicationList", (Long)null));
                        if (fetchLocation != 2 && deviceDetails.locationTrackingEnabled) {
                            profileTypeCommandList.add(JSONUtil.optLongForUVH(commandUUIDcommandIDJSON, "GetLocation", (Long)null));
                        }
                        if (deviceDetails.modelType != 1) {
                            profileTypeCommandList.add(JSONUtil.optLongForUVH(commandUUIDcommandIDJSON, "WmiQuery;ComputerSystem;NetworkAdapterConfig;ComputerSystemProduct", (Long)null));
                        }
                    }
                    if (deviceDetails.nativeAgentInstalled && deviceDetails.locationTrackingEnabled && !ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(deviceDetails.osVersion, 10.0f) && ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(deviceDetails.osVersion, 8.1f) && deviceDetails.osVersion != null && deviceDetails.osVersion.startsWith("9.2.")) {
                        nativeTypeCommandList.add(JSONUtil.optLongForUVH(commandUUIDcommandIDJSON, "GetLocation", (Long)null));
                    }
                }
                else if (deviceDetails.platform == 4) {
                    profileTypeCommandList.add(JSONUtil.optLongForUVH(commandUUIDcommandIDJSON, "AssetScan", (Long)null));
                }
                else {
                    String commandUUID = "AssetScan";
                    if (userId != -1L) {
                        commandUUID = commandUUID + ";" + "USER_INVOKED";
                    }
                    profileTypeCommandList.add(JSONUtil.optLongForUVH(commandUUIDcommandIDJSON, commandUUID, (Long)null));
                }
                if (deviceDetails.knoxContainerActive) {
                    String commandUUID = "AssetScanContainer";
                    if (userId != -1L) {
                        commandUUID = commandUUID + ";" + "USER_INVOKED";
                    }
                    profileTypeCommandList.add(JSONUtil.optLongForUVH(commandUUIDcommandIDJSON, commandUUID, (Long)null));
                }
                if ((scanPersonalApps == null || !scanPersonalApps.equalsIgnoreCase("true")) && deviceDetails.profileOwner && deviceDetails.nativeAgentInstalled) {
                    nativeTypeCommandList.add(JSONUtil.optLongForUVH(commandUUIDcommandIDJSON, "PersonalAppsInfo", (Long)null));
                }
                if (!profileTypeCommandList.isEmpty()) {
                    profileTypeCommandMap.put(deviceDetails.resourceId, profileTypeCommandList);
                    final JSONObject commandIdCommandUUIDJSON = APIUtil.invertJSONObject(commandUUIDcommandIDJSON);
                    for (final Long commandID : profileTypeCommandList) {
                        final String commandUUID2 = commandIdCommandUUIDJSON.optString(commandID.toString(), (String)null);
                        if (!MDMUtil.isStringEmpty(commandUUID2) && MDMUtil.getInstance().getScanCommandList().contains(String.valueOf(commandIdCommandUUIDJSON.get(commandID.toString())).split(";")[0]) && userId != -1L) {
                            final JSONObject commandJSON = new JSONObject();
                            final String commandUUIDString = String.valueOf(commandIdCommandUUIDJSON.get(commandID.toString()));
                            commandJSON.put("RESOURCE_ID", deviceDetails.resourceId);
                            commandJSON.put("ADDED_BY", (Object)userId);
                            commandJSON.put("COMMAND_ID", (Object)commandID);
                            commandJSON.put("COMMAND_UUID", (Object)commandUUIDString);
                            commandJSON.put("REMARKS", (Object)DeviceInvCommandHandler.getInstance().getRemarksString(commandUUIDString));
                            commandsJSONArray.put((Object)commandJSON);
                            commandIdCriteriaList.add(commandID);
                        }
                    }
                }
                if (!nativeTypeCommandList.isEmpty()) {
                    nativeTypeCommandMap.put(deviceDetails.resourceId, nativeTypeCommandList);
                }
                commandStatusJSON.put(String.valueOf(deviceDetails.resourceId), (Object)commandsJSONArray);
            }
            criteriaList.put("COMMAND_ID", new ArrayList(commandIdCriteriaList));
            this.assignCommandToDevices(profileTypeCommandMap, 1);
            this.assignCommandToDevices(nativeTypeCommandMap, 2);
            final JSONObject responseJSON = new CommandStatusHandler().populateCommandStatusForDevices(commandStatusJSON, criteriaList);
            this.logger.log(Level.INFO, "updatedCommandStatusJSON : {0}", responseJSON.toString());
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addDeviceScanCommand", exp);
        }
    }
    
    public void addDeviceScanCommand(final DeviceDetails deviceDetails, final Long userId) {
        this.logger.log(Level.INFO, "addDeviceScanCommand(): resourceID:{0}", deviceDetails.resourceId);
        try {
            Long commandID = null;
            final Boolean isMac = deviceDetails.platform == 1 && (deviceDetails.modelType == 3 || deviceDetails.modelType == 4);
            final JSONObject privacyJson = deviceDetails.privacySettingsJSON;
            final int fetchApp = privacyJson.optInt("fetch_installed_app");
            final int fetchLocation = privacyJson.optInt("fetch_location");
            final Boolean isLocationFetchDisabled = !deviceDetails.locationTrackingEnabled || fetchLocation == 2;
            String[] deviceScanCommands;
            if (deviceDetails.platform == 1) {
                final List iosScanCommand = this.getIosInvScanCommandList(deviceDetails);
                final long odlocationAgentVersion = 1402L;
                if (deviceDetails.agentVersionCode < odlocationAgentVersion || isLocationFetchDisabled || isMac) {
                    iosScanCommand.remove("GetLocation");
                }
                if (!LocationSettingsDataHandler.getInstance().isLocationHistoryEnabled(deviceDetails.customerId)) {
                    iosScanCommand.remove("GetLocation");
                }
                deviceScanCommands = iosScanCommand.toArray(new String[1]);
            }
            else if (deviceDetails.platform == 3) {
                final List<String> cmdList = new ArrayList<String>();
                final Boolean isWin10OrAbove = ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(deviceDetails.osVersion, 10.0f);
                if (WpAppSettingsHandler.getInstance().isAETUploaded(deviceDetails.customerId) && !isWin10OrAbove) {
                    cmdList.add("InstalledApplicationList");
                    cmdList.add("DeviceInformation");
                }
                else {
                    cmdList.add("DeviceInformation");
                }
                if (isWin10OrAbove) {
                    cmdList.add("InstalledApplicationList");
                    if (fetchLocation != 2 && deviceDetails.locationTrackingEnabled) {
                        cmdList.add("GetLocation");
                    }
                    if (deviceDetails.modelType != 1) {
                        cmdList.add("WmiQuery;ComputerSystem;NetworkAdapterConfig;ComputerSystemProduct");
                    }
                }
                deviceScanCommands = cmdList.toArray(new String[1]);
            }
            else if (deviceDetails.platform == 4) {
                deviceScanCommands = new String[] { "AssetScan" };
            }
            else {
                final long odlocationAgentVersion2 = 77L;
                if (deviceDetails.agentVersionCode >= odlocationAgentVersion2) {
                    deviceScanCommands = new String[] { "AssetScan" };
                }
                else {
                    deviceScanCommands = new String[] { "AndroidInvScan" };
                }
            }
            for (int i = 0; i < deviceScanCommands.length; ++i) {
                final DeviceCommand command = new DeviceCommand();
                command.commandType = deviceScanCommands[i];
                if (MDMUtil.getInstance().getScanCommandList().contains(command.commandType) && userId != null && (deviceDetails.platform == 2 || deviceDetails.platform == 1)) {
                    command.commandUUID = deviceScanCommands[i] + ";" + "USER_INVOKED";
                }
                else {
                    command.commandUUID = deviceScanCommands[i];
                }
                command.commandStr = "--";
                commandID = this.addCommand(command);
                final List resourceList = new ArrayList();
                resourceList.add(deviceDetails.resourceId);
                this.assignCommandToDevices(commandID, resourceList);
                if (MDMUtil.getInstance().getScanCommandList().contains(command.commandType) && userId != null) {
                    DeviceInvCommandHandler.getInstance().addOrUpdateCommandInitiatedCommandHistory(deviceDetails.resourceId, commandID, userId, command.commandUUID);
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addDeviceScanCommand", exp);
        }
    }
    
    protected List getIosInvScanCommandList(final DeviceDetails device) throws Exception {
        final int modelType = device.modelType;
        final boolean isMultiUser = device.isMultiUser;
        final List commandList = new ArrayList();
        final boolean managedOnly = Integer.parseInt(device.privacySettingsJSON.get("fetch_installed_app").toString()) == 2;
        commandList.add("SecurityInfo");
        commandList.add("CertificateList");
        commandList.add("InstalledApplicationList");
        commandList.add("ManagedApplicationList");
        if (managedOnly && (modelType == 4 || modelType == 3)) {
            commandList.add("FetchAppleAgentDetails");
        }
        switch (modelType) {
            case 0:
            case 1:
            case 2: {
                commandList.add("Restrictions");
                commandList.add("GetLocation");
                break;
            }
            case 3:
            case 4: {
                commandList.add("GetLocation");
                if (!ManagedDeviceHandler.getInstance().isEqualOrAboveOSVersion(device.resourceId, "11.0")) {
                    commandList.remove("ManagedApplicationList");
                    break;
                }
                break;
            }
        }
        commandList.add("DeviceInformation");
        if (isMultiUser && device.privacySettingsJSON.optInt("recent_users_report", -1) != 2) {
            switch (modelType) {
                case 2:
                case 3:
                case 4: {
                    commandList.add("UserList");
                    break;
                }
            }
        }
        commandList.add("ProfileList");
        commandList.add("ProvisioningProfileList");
        commandList.add("AvailableOSUpdates");
        return commandList;
    }
    
    protected List getIosSyncCommandList(final DeviceDetails deviceDetails) throws Exception {
        final List commandList = new ArrayList();
        commandList.add("InstalledApplicationList");
        commandList.add("ManagedApplicationList");
        switch (deviceDetails.modelType) {
            case 0:
            case 1:
            case 2: {
                commandList.add("GetLocation");
                break;
            }
            case 3:
            case 4: {
                commandList.add("GetLocation");
                final String osVersion = deviceDetails.osVersion;
                if (!osVersion.equals("11.0") && !new VersionChecker().isGreater(osVersion, "11.0")) {
                    commandList.remove("ManagedApplicationList");
                    break;
                }
                break;
            }
        }
        commandList.add("DeviceInformation");
        return commandList;
    }
    
    public void addContainerScanCommand(final DeviceDetails deviceDetails) {
        final DeviceCommand command = new DeviceCommand();
        final long odlocationAgentVersion = 77L;
        if (deviceDetails.agentVersionCode >= odlocationAgentVersion) {
            command.commandUUID = "AssetScanContainer";
            command.commandType = "AssetScanContainer";
            command.commandStr = "--";
        }
        else {
            command.commandUUID = "AndroidInvScanContainer";
            command.commandType = "AndroidInvScanContainer";
            command.commandStr = "--";
        }
        try {
            final Long commandID = this.addCommand(command);
            final List resourceList = new ArrayList();
            resourceList.add(deviceDetails.resourceId);
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in addContainerScanCommand", e);
        }
    }
    
    public Long addAdminAgentUpgradeCommand(final String resourceUDID, final int repType) {
        this.logger.log(Level.INFO, "addAdminAgentUpgradeCommand(): resourceUDID:{0}", resourceUDID);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "AgentUpgrade";
        command.commandType = "AgentUpgrade";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevice(commandID, resourceUDID, repType);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return commandID;
    }
    
    public Long addRemoveDeviceCommand(final String resourceUDID) {
        this.logger.log(Level.INFO, "addRemoveDeviceCommand(): resourceUDID:{0}", resourceUDID);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "RemoveDevice";
        command.commandType = "RemoveDevice";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevice(commandID, resourceUDID);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addRemoveDeviceCommand", exp);
        }
        finally {
            if (ManagedDeviceHandler.getInstance().isPersonalProfileManaged(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(resourceUDID))) {
                getInstance().addNativeAppRemoveDeviceCommand(resourceUDID);
            }
        }
        return commandID;
    }
    
    public void assignCommandToDevices(final List commandList, final List resourceList) {
        this.assignCommandToDevices(commandList, resourceList, 1);
    }
    
    public void assignCommandToDevices(final List commandList, final List resourceList, final int repoType) {
        this.logger.log(Level.INFO, "assignCommandToDevice(): commandList:{0}", commandList);
        this.logger.log(Level.INFO, "assignCommandToDevice(): resourceList:{0}", resourceList);
        try {
            for (int j = 0; j < commandList.size(); ++j) {
                this.assignCommandToDevices(commandList.get(j), resourceList, repoType);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in assignCommandToDevices", exp);
        }
    }
    
    public void removeMdCommandsForScheduledCommands(final Long collectionID, final List resourceIDs) {
        try {
            this.logger.log(Level.INFO, "Removing schedulec commands from mdcommandstodevice for collectionID{0} and resourceIDs", new Object[] { collectionID, resourceIDs });
            final DeleteQuery dq = (DeleteQuery)new DeleteQueryImpl("MdCommandsToDevice");
            final Long commandID = ScheduledCommandToCollectionHandler.getInstance().getCommandForCollection(collectionID);
            final Long tempCommandID = ScheduledActionsUtils.getTempCommandIDForCommandID(commandID, collectionID);
            final Criteria commandCriteria = new Criteria(new Column("MdCommandsToDevice", "COMMAND_ID"), (Object)tempCommandID, 0);
            final Criteria resourceCriteria = new Criteria(new Column("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
            dq.setCriteria(commandCriteria.and(resourceCriteria));
            MDMUtil.getPersistence().delete(dq);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in removeMdCommandsForScheduledCommands", e);
        }
    }
    
    public void assignCommandToDevicesWithSlot(final List commandList, final List resourceList, final int repoType, final long slotStartTime, final long slotEndTime) throws Exception {
        this.logger.log(Level.INFO, "assignCommandToDeviceWithSlot(): commandList:{0}", commandList);
        this.logger.log(Level.INFO, "assignCommandToDeviceWithSlot(): resourceList:{0}", resourceList);
        this.logger.log(Level.INFO, "assignCommandToDeviceWithSlot(): slotStartTime:{0}", slotStartTime);
        this.logger.log(Level.INFO, "assignCommandToDeviceWithSlot(): slotEndTime:{0}", slotEndTime);
        try {
            for (int j = 0; j < commandList.size(); ++j) {
                this.assignCommandToDevicesWithSlot(commandList.get(j), resourceList, repoType, slotStartTime, slotEndTime);
            }
        }
        catch (final DataAccessException exp) {
            this.logger.log(Level.SEVERE, "Exception in assignCommandToDevicesWithSlot commandList:{0} resourceList:{1} slotStartTime:{2} slotEndTime:{3}", new Object[] { commandList, resourceList, slotStartTime, slotEndTime });
        }
    }
    
    public void assignCommandToDevicesWithSlot(final Long commandID, final List resourceList, final int commandRepositoryType, final long slotStartTime, final long slotEndTime) throws Exception {
        this.logger.log(Level.INFO, "assignCommandToDevicesWithSlot(): commandID:{0} resourceList {1} slotId{2},commandRespositoryType{3}", new Object[] { commandID, resourceList, slotEndTime, commandRepositoryType });
        this.logger.log(Level.INFO, "assignCommandToDeviceWithSlot(): slotStartTime:{0}", slotStartTime);
        this.logger.log(Level.INFO, "assignCommandToDeviceWithSlot(): slotEndTime:{0}", slotEndTime);
        try {
            if (resourceList != null && resourceList.size() > 0) {
                MDMUtil.addCommandToThreadLocal(commandID);
            }
            DataObject deviceCommandObject = MDMUtil.getPersistence().constructDataObject();
            final Criteria commandIDCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
            final Criteria commandRepCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
            final Criteria slotStartTimeCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "SLOT_BEGIN_TIME"), (Object)slotStartTime, 0);
            final Criteria slotEndTimeCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "SLOT_END_TIME"), (Object)slotEndTime, 0);
            final Column resourceColumn = Column.getColumn("MdCommandsToDevice", "RESOURCE_ID");
            for (int i = 0; i < resourceList.size(); ++i) {
                final long stime = System.currentTimeMillis();
                final long stime2 = System.currentTimeMillis();
                final Criteria resourceCriteria = new Criteria(resourceColumn, resourceList.get(i), 0);
                final Criteria criteria = resourceCriteria.and(commandIDCriteria).and(commandRepCriteria).and(slotEndTimeCriteria).and(slotStartTimeCriteria);
                deviceCommandObject = MDMUtil.getPersistence().get("MdCommandsToDevice", criteria);
                if (this.canApplyThisCommand(resourceList.get(i), commandID)) {
                    Row deviceCommandRow = deviceCommandObject.getRow("MdCommandsToDevice", criteria);
                    if (deviceCommandRow == null) {
                        deviceCommandRow = new Row("MdCommandsToDevice");
                        deviceCommandRow.set("RESOURCE_ID", resourceList.get(i));
                        deviceCommandRow.set("COMMAND_ID", (Object)commandID);
                        deviceCommandRow.set("COMMAND_REPOSITORY_TYPE", (Object)commandRepositoryType);
                        deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                        final Row row = deviceCommandRow;
                        final String s = "ADDED_AT";
                        SyMUtil.getInstance();
                        row.set(s, (Object)SyMUtil.getCurrentTimeInMillis());
                        final Row row2 = deviceCommandRow;
                        final String s2 = "UPDATED_AT";
                        SyMUtil.getInstance();
                        row2.set(s2, (Object)SyMUtil.getCurrentTimeInMillis());
                        deviceCommandRow.set("SLOT_BEGIN_TIME", (Object)slotStartTime);
                        deviceCommandRow.set("SLOT_END_TIME", (Object)slotEndTime);
                        deviceCommandObject.addRow(deviceCommandRow);
                    }
                    else {
                        deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                        deviceCommandObject.updateRow(deviceCommandRow);
                    }
                }
                this.logger.log(Level.INFO, "assignCommandToDevicesWithSlot(): Wait Time For Cache - {0} Process Time - {1}", new Object[] { stime2 - stime, System.currentTimeMillis() - stime2 });
            }
            MDMUtil.getPersistence().update(deviceCommandObject);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in assignCommandToDevicesWithSlot", exp);
            throw exp;
        }
    }
    
    public void assignCommandToDevices(final HashMap<Long, ArrayList<Long>> devicesToCommandsMap, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "inside bulk assignCommandToDevices(): devicesToCommandsMap: {0}", devicesToCommandsMap);
        try {
            if (!devicesToCommandsMap.isEmpty()) {
                final ArrayList<Long> resourceList = new ArrayList<Long>(devicesToCommandsMap.keySet());
                final ArrayList tempList = new ArrayList((Collection<? extends E>)devicesToCommandsMap.values());
                final HashSet tempSet = new HashSet();
                for (final Object object : tempList) {
                    final ArrayList tempCommandList = (ArrayList)object;
                    tempSet.addAll(tempCommandList);
                }
                final ArrayList<Long> consolidatedCommandList = new ArrayList<Long>(tempSet);
                Criteria commandIdCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)consolidatedCommandList.toArray(), 8);
                final Criteria commandRepCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
                Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                final DataObject dataObject = MDMUtil.getPersistenceLite().get("MdCommandsToDevice", commandRepCriteria.and(commandIdCriteria.and(resourceCriteria)));
                final DataObject managedDeviceDO = MDMUtil.getPersistenceLite().get("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8));
                final HashMap deviceCommandQueueItemMap = this.getCommandQueueItem(resourceList, consolidatedCommandList, devicesToCommandsMap, commandRepositoryType);
                final long stime = System.currentTimeMillis();
                for (final Long resourceId : resourceList) {
                    final HashMap commandQueueItemMap = deviceCommandQueueItemMap.get(resourceId);
                    final ArrayList<Long> commandsList = devicesToCommandsMap.get(resourceId);
                    resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceId, 0);
                    for (final Long commandId : commandsList) {
                        commandIdCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandId, 0);
                        Row deviceCommandRow = dataObject.getRow("MdCommandsToDevice", resourceCriteria.and(commandIdCriteria));
                        if (deviceCommandRow == null) {
                            deviceCommandRow = new Row("MdCommandsToDevice");
                            deviceCommandRow.set("RESOURCE_ID", (Object)resourceId);
                            deviceCommandRow.set("COMMAND_ID", (Object)commandId);
                            deviceCommandRow.set("COMMAND_REPOSITORY_TYPE", (Object)commandRepositoryType);
                            deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                            final Long currentTime = System.currentTimeMillis();
                            deviceCommandRow.set("ADDED_AT", (Object)currentTime);
                            deviceCommandRow.set("UPDATED_AT", (Object)currentTime);
                            dataObject.addRow(deviceCommandRow);
                        }
                        else {
                            deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                            dataObject.updateRow(deviceCommandRow);
                        }
                        final Row managedDeviceRow = managedDeviceDO.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0));
                        if (managedDeviceRow != null) {
                            final String deviceUDID = (String)managedDeviceRow.get("UDID");
                            final CommandQueueItem commandQueueItem = commandQueueItemMap.get(commandId);
                            if (commandQueueItem.addedTime == null) {
                                commandQueueItem.addedTime = System.currentTimeMillis();
                            }
                            this.addDeviceCommandsToCache(deviceUDID, commandQueueItem, commandRepositoryType);
                        }
                    }
                }
                final long stime2 = System.currentTimeMillis();
                MDMUtil.getPersistenceLite().update(dataObject);
                this.logger.log(Level.INFO, "assignCommandToDevices(): Wait Time For Cache - {0} Process Time - {1}", new Object[] { stime2 - stime, System.currentTimeMillis() - stime2 });
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in bulk assignCommandToDevices() -- ", e);
        }
    }
    
    public void assignCommandToDevices(final Long commandID, final List resourceList) {
        this.assignCommandToDevices(commandID, resourceList, 1);
    }
    
    public void assignSyncCommandToDevices(final Long commandID, final List resourceList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "assignSyncCommandToDevices(): commandID:{0} resourceList {1} ", new Object[] { commandID, resourceList });
        try {
            if (resourceList != null && resourceList.size() > 0) {
                MDMUtil.addCommandToThreadLocal(commandID);
            }
            DataObject deviceCommandObject = MDMUtil.getPersistence().constructDataObject();
            final Criteria commandIDCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
            final Criteria commandRepCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
            Criteria resourceCriteria = null;
            final Column resourceColumn = Column.getColumn("MdCommandsToDevice", "RESOURCE_ID");
            for (int i = 0; i < resourceList.size(); ++i) {
                final long stime = System.currentTimeMillis();
                final long stime2;
                synchronized (DeviceCommandRepository.SYNCHRONIZE_ADD_COMMAND) {
                    stime2 = System.currentTimeMillis();
                    resourceCriteria = new Criteria(resourceColumn, resourceList.get(i), 0);
                    final Criteria criteria = resourceCriteria.and(commandIDCriteria).and(commandRepCriteria);
                    deviceCommandObject = MDMUtil.getPersistence().get("MdCommandsToDevice", criteria);
                    if (this.canApplyThisCommand(resourceList.get(i), commandID)) {
                        Row deviceCommandRow = deviceCommandObject.getRow("MdCommandsToDevice", criteria);
                        if (deviceCommandRow == null) {
                            deviceCommandRow = new Row("MdCommandsToDevice");
                            deviceCommandRow.set("RESOURCE_ID", resourceList.get(i));
                            deviceCommandRow.set("COMMAND_ID", (Object)commandID);
                            deviceCommandRow.set("COMMAND_REPOSITORY_TYPE", (Object)commandRepositoryType);
                            deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                            final Row row = deviceCommandRow;
                            final String s = "ADDED_AT";
                            SyMUtil.getInstance();
                            row.set(s, (Object)SyMUtil.getCurrentTimeInMillis());
                            final Row row2 = deviceCommandRow;
                            final String s2 = "UPDATED_AT";
                            SyMUtil.getInstance();
                            row2.set(s2, (Object)SyMUtil.getCurrentTimeInMillis());
                            deviceCommandObject.addRow(deviceCommandRow);
                            MDMUtil.getPersistence().update(deviceCommandObject);
                        }
                        else {
                            deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                            deviceCommandObject.updateRow(deviceCommandRow);
                            MDMUtil.getPersistence().update(deviceCommandObject);
                        }
                        final Criteria resourceUDIDCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), resourceList.get(i), 0);
                        final DataObject dataObject = MDMUtil.getPersistence().get("ManagedDevice", resourceUDIDCriteria);
                        if (dataObject.containsTable("ManagedDevice")) {
                            final String deviceUDID = (String)dataObject.getFirstValue("ManagedDevice", "UDID");
                            final CommandQueueItem commandQueueItem = this.getCommandQueueItem(commandID, resourceList.get(i), commandRepositoryType, (Long)deviceCommandRow.get("ADDED_AT"));
                            this.addDeviceCommandsToCache(deviceUDID, commandQueueItem, commandRepositoryType);
                        }
                    }
                    else {
                        if (((String)DBUtil.getValueFromDB("MdCommands", "COMMAND_ID", (Object)commandID, "COMMAND_TYPE")).equals("RemoveProfile")) {
                            ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceList.get(i), (Long)DBUtil.getValueFromDB("MdCollectionCommand", "COMMAND_ID", (Object)commandID, "COLLECTION_ID"));
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceList.get(i), this.getCollectionId(commandID).toString(), 6, "dc.db.mdm.collection.Successfully_removed_the_policy");
                        }
                        else {
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceList.get(i), this.getCollectionId(commandID).toString(), 8, this.getNotApplicableRemarks(resourceList.get(i)));
                        }
                        MDMCollectionStatusUpdate.getInstance().updateCollnToResErrorCode(resourceList.get(i), this.getCollectionId(commandID), null);
                    }
                }
                this.logger.log(Level.INFO, "assignSyncCommandToDevices(): Wait Time For Cache - {0} Process Time - {1}", new Object[] { stime2 - stime, System.currentTimeMillis() - stime2 });
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in assignSyncCommandToDevices", exp);
        }
    }
    
    protected boolean canApplyThisCommand(final Long resourceId, final Long commandId) throws Exception {
        return !this.isKnoxProfile(commandId) || KnoxUtil.getInstance().canApplyKnoxProfile(resourceId);
    }
    
    protected String getNotApplicableRemarks(final Long resourceId) throws Exception {
        if (!KnoxUtil.getInstance().isRegisteredAsKnox(resourceId)) {
            return "dc.mdm.android.knox.profile.notApplicable.noKnox";
        }
        if (!KnoxUtil.getInstance().canApplyKnoxProfile(resourceId)) {
            return "mdm.android.knox.profile.noContainer";
        }
        return "";
    }
    
    protected boolean isKnoxProfile(final Long commandId) throws DataAccessException, Exception {
        final Long collectionId = this.getCollectionId(commandId);
        final Integer scope = this.getProfileScopeForCollection(collectionId);
        return scope != null && scope == 1;
    }
    
    public Long getCollectionId(final Long commandId) throws Exception {
        return (Long)DBUtil.getValueFromDB("MdCollectionCommand", "COMMAND_ID", (Object)commandId, "COLLECTION_ID");
    }
    
    public void assignCommandToDevice(final Long commandID, final String deviceUDID) {
        this.assignCommandToDevice(commandID, deviceUDID, 1);
    }
    
    public void assignCommandToDevice(final Long commandID, final String deviceUDID, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "assignCommandToDevice(): commandID:{0}", commandID);
        this.logger.log(Level.INFO, "assignCommandToDevice(): deviceUDID:{0}", deviceUDID);
        try {
            DataObject deviceCommandObject = MDMUtil.getPersistence().constructDataObject();
            final Criteria commandIDCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
            final Criteria resourceIDCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "UDID"), (Object)deviceUDID, 0, false);
            final Criteria cmdRepTypeCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
            final Criteria criteria = resourceIDCriteria.and(commandIDCriteria).and(cmdRepTypeCriteria);
            deviceCommandObject = MDMUtil.getPersistence().get("MdCommandsToDevice", criteria);
            Row deviceCommandRow = null;
            if (deviceCommandObject.isEmpty()) {
                deviceCommandRow = new Row("MdCommandsToDevice");
                deviceCommandRow.set("UDID", (Object)deviceUDID);
                deviceCommandRow.set("COMMAND_ID", (Object)commandID);
                deviceCommandRow.set("COMMAND_REPOSITORY_TYPE", (Object)commandRepositoryType);
                deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                final Row row = deviceCommandRow;
                final String s = "ADDED_AT";
                SyMUtil.getInstance();
                row.set(s, (Object)SyMUtil.getCurrentTimeInMillis());
                final Row row2 = deviceCommandRow;
                final String s2 = "UPDATED_AT";
                SyMUtil.getInstance();
                row2.set(s2, (Object)SyMUtil.getCurrentTimeInMillis());
                deviceCommandObject.addRow(deviceCommandRow);
                MDMUtil.getPersistence().add(deviceCommandObject);
            }
            else {
                deviceCommandRow = deviceCommandObject.getFirstRow("MdCommandsToDevice");
                deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                deviceCommandObject.updateRow(deviceCommandRow);
                MDMUtil.getPersistence().update(deviceCommandObject);
            }
            final CommandQueueItem commandQueueItem = this.getCommandQueueItem(commandID, ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID), commandRepositoryType, (Long)deviceCommandRow.get("ADDED_AT"));
            this.addDeviceCommandsToCache(deviceUDID, commandQueueItem, commandRepositoryType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in assignCommandToDevice", exp);
        }
    }
    
    public void assignCommandToDevice(final String commandUUID, final Long resourceID) {
        this.logger.log(Level.INFO, "assignCommandToDevice(): commandUUID:{0}", commandUUID);
        try {
            final Criteria criteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)commandUUID, 0, false);
            final DataObject dataObject = MDMUtil.getPersistence().get("MdCommands", criteria);
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            if (!dataObject.isEmpty()) {
                final Long commandID = (Long)dataObject.getFirstValue("MdCommands", "COMMAND_ID");
                this.assignCommandToDevices(commandID, resourceList);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in assignCommandToDevice", exp);
        }
    }
    
    public void loadCommandsForDevice(final String resourceUDID, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "updating the commands to cache for resource {0}", resourceUDID);
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommandsToDevice"));
            final Criteria resourceCommandStatusCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_COMMAND_STATUS"), (Object)12, 0);
            final Criteria agentTypeCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
            Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "UDID"), (Object)resourceUDID, 0, false);
            final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(resourceUDID);
            if (resourceId != null) {
                resourceCriteria = resourceCriteria.or(new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceId, 0));
            }
            final Criteria criteria = resourceCriteria.and(resourceCommandStatusCriteria).and(agentTypeCriteria).and(this.getSlotCriteria());
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("MdCommandsToDevice", "*"));
            SeqCmdDBUtil.getInstance().suspendAnyStallingSequentialCommandsForResource(resourceId);
            SeqCmdUtils.getInstance().loadSequentialCommandsForDevice(resourceId);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator rowIterator = dataObject.getRows("MdCommandsToDevice");
                final HashMap<Long, Long> commandsVsAddedTime = new HashMap<Long, Long>();
                while (rowIterator.hasNext()) {
                    final Row deviceToCommandRow = rowIterator.next();
                    final Long commandID = (Long)deviceToCommandRow.get("COMMAND_ID");
                    final Long addedTime = (Long)deviceToCommandRow.get("ADDED_AT");
                    commandsVsAddedTime.put(commandID, addedTime);
                }
                final List<CommandQueueItem> commandQueueItems = this.getCommandQueueItem(commandsVsAddedTime, resourceId, commandRepositoryType);
                for (final CommandQueueItem queueItem : commandQueueItems) {
                    this.addDeviceCommandsToCache(resourceUDID, queueItem, commandRepositoryType);
                }
                MDMInvDataPopulator.getInstance().checkAndUpdateDeviceScanStatus(resourceId, 1, 4, "dc.common.SCANNING_IN_PROGRESS");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in loadCommandsForDevice", exp);
        }
    }
    
    public void refreshResourceCommandsToCache(final List<Long> resourceIDList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "updating the commands to cache for resources {0}", resourceIDList);
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommandsToDevice"));
            final Criteria resourceCommandStatusCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_COMMAND_STATUS"), (Object)12, 0);
            final Criteria agentTypeCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceIDList.toArray(), 8, false);
            final Criteria criteria = resourceCriteria.and(resourceCommandStatusCriteria).and(agentTypeCriteria).and(this.getSlotCriteria());
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("MdCommandsToDevice", "COMMAND_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("MdCommandsToDevice", "RESOURCE_COMMAND_STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("MdCommandsToDevice", "ADDED_AT"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Map<Long, String> devResUDIDMap = ManagedDeviceHandler.getInstance().getResourceID_UDID(resourceIDList);
            for (final Long resourceID : devResUDIDMap.keySet()) {
                final PriorityQueue<CommandQueueItem> commandIDList = new PriorityQueue<CommandQueueItem>();
                final Criteria resource = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceID, 0);
                final Iterator rowIterator = dataObject.getRows("MdCommandsToDevice", resource);
                final HashMap<Long, Long> commandsVsAddedTime = new HashMap<Long, Long>();
                while (rowIterator.hasNext()) {
                    final Row deviceToCommandRow = rowIterator.next();
                    final Long commandID = (Long)deviceToCommandRow.get("COMMAND_ID");
                    final Long addedTime = (Long)deviceToCommandRow.get("ADDED_AT");
                    commandsVsAddedTime.put(commandID, addedTime);
                }
                commandIDList.addAll((Collection<?>)this.getCommandQueueItem(commandsVsAddedTime, resourceID, commandRepositoryType));
                this.updateResourceCommandsToCache(devResUDIDMap.get(resourceID), commandIDList, commandRepositoryType);
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    protected void updateResourceCommandsToCache(final String deviceUDID, final PriorityQueue<CommandQueueItem> commandIDList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "updateresourceCommandsToCache initiated. deviceUDID : {0}; CommandIDList : {1}; reposositoryType : {2}", new Object[] { deviceUDID, commandIDList, commandRepositoryType });
        final HashMap deviceCommandCache = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("MDM_DEVICE_COMMANDS", 2);
        if (deviceCommandCache != null) {
            final HashMap cmdRepoCache = deviceCommandCache.get(commandRepositoryType);
            if (cmdRepoCache != null && cmdRepoCache.containsKey(deviceUDID)) {
                cmdRepoCache.put(deviceUDID, commandIDList);
                deviceCommandCache.put(commandRepositoryType, cmdRepoCache);
                ApiFactoryProvider.getCacheAccessAPI().putCache("MDM_DEVICE_COMMANDS", (Object)deviceCommandCache, 2);
            }
        }
    }
    
    protected DeviceCommand addCommandToCache(final Long commandID, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addCommandToCache(): commandID:{0}", commandID);
        DeviceCommand command = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommands"));
            final Criteria criteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_ID"), (Object)commandID, 0);
            sQuery.setCriteria(criteria);
            sQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (!dataObject.isEmpty()) {
                final Row commandRow = dataObject.getRow("MdCommands");
                command = new DeviceCommand();
                command.commandUUID = (String)commandRow.get("COMMAND_UUID");
                command.commandType = (String)commandRow.get("COMMAND_TYPE");
                command.commandFilePath = (String)commandRow.get("COMMAND_DATA_FILE_PATH");
                command.commandStr = (String)commandRow.get("COMMAND_DATA_VALUE");
                command.commandDataType = (int)commandRow.get("COMMAND_DATA_TYPE");
                command.dynamicVariable = (Boolean)commandRow.get("COMMAND_DYNAMIC_VARIABLE");
                command.priority = (int)commandRow.get("PRIORITY");
            }
            if (command != null) {
                HashMap cacheParentHash = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("MDM_COMMAND_REPOSITORY", 2);
                if (cacheParentHash == null) {
                    cacheParentHash = new HashMap();
                }
                HashMap cacheHash = cacheParentHash.get(commandRepositoryType);
                if (cacheHash == null) {
                    cacheHash = new HashMap();
                }
                cacheHash.put(commandID, command);
                cacheParentHash.put(commandRepositoryType, cacheHash);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addCommandToCache", exp);
        }
        return command;
    }
    
    protected DeviceCommand getCommandFromCache(final Long commandID, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "getCommandFromCache(): commandID:{0}", commandID);
        DeviceCommand command = null;
        try {
            final HashMap cacheParentHash = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("MDM_COMMAND_REPOSITORY", 2);
            if (cacheParentHash != null) {
                final HashMap cacheHash = cacheParentHash.get(commandRepositoryType);
                command = cacheHash.get(commandID);
            }
            if (command == null) {
                command = this.addCommandToCache(commandID, commandRepositoryType);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getCommandFromCache", exp);
        }
        return command;
    }
    
    protected void addDeviceCommandsToCache(final String resourceUDID, final CommandQueueItem commandID, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addDeviceCommandsToCache(): resourceUDID:{0}", resourceUDID);
        this.logger.log(Level.INFO, "addDeviceCommandsToCache(): commandID:{0}", commandID);
        final long stime = System.currentTimeMillis();
        final long stime2;
        synchronized (DeviceCommandRepository.SYNCHRONIZE_COMMAND_CACHE) {
            stime2 = System.currentTimeMillis();
            try {
                final String cacheName = "MDM_DEVICE_COMMANDS";
                HashMap cacheParentHash = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName, 2);
                if (cacheParentHash == null) {
                    cacheParentHash = new HashMap();
                }
                if (cacheParentHash != null) {
                    HashMap cacheHash = cacheParentHash.get(commandRepositoryType);
                    if (cacheHash == null) {
                        cacheHash = new HashMap();
                    }
                    final PriorityQueue<CommandQueueItem> cacheCommandList = (cacheHash.get(resourceUDID) instanceof ArrayList) ? this.getPriorityQueueForArrayList(cacheHash, resourceUDID) : cacheHash.get(resourceUDID);
                    if (cacheCommandList != null) {
                        if (!cacheCommandList.contains(commandID)) {
                            cacheCommandList.add(commandID);
                            cacheHash.put(resourceUDID, cacheCommandList);
                        }
                    }
                    else {
                        final PriorityQueue<CommandQueueItem> commandList = new PriorityQueue<CommandQueueItem>();
                        commandList.add(commandID);
                        cacheHash.put(resourceUDID, commandList);
                    }
                    cacheParentHash.put(commandRepositoryType, cacheHash);
                    ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, (Object)cacheParentHash, 2);
                }
            }
            catch (final Exception exp) {
                this.logger.log(Level.SEVERE, "Exception in addDeviceCommandsToCache", exp);
            }
        }
        this.logger.log(Level.INFO, "addDeviceCommandsToCache(): Wait Time For Cache - {0} Process Time - {1}", new Object[] { stime2 - stime, System.currentTimeMillis() - stime2 });
    }
    
    public void updateResourceCommandStatus(final Long commandID, final String resourceUDID, final int commandRepositoryType, final int status) {
        this.logger.log(Level.INFO, "updateResourceCommandStatus(): commandID:{0}", commandID);
        this.logger.log(Level.INFO, "updateResourceCommandStatus(): resourceUDID:{0}", resourceUDID);
        this.logger.log(Level.INFO, "updateResourceCommandStatus(): status:{0}", status);
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(resourceUDID);
        if (resourceID != null) {
            this.updateResourceCommandStatus(commandID, resourceID, commandRepositoryType, status);
        }
        else {
            try {
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdCommandsToDevice");
                final Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "UDID"), (Object)resourceUDID, 0);
                final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
                final Criteria agentTypeCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
                final Criteria criteria = resourceCriteria.and(commandCriteria).and(agentTypeCriteria);
                updateQuery.setCriteria(criteria);
                updateQuery.setUpdateColumn("RESOURCE_COMMAND_STATUS", (Object)status);
                final UpdateQuery updateQuery2 = updateQuery;
                final String s = "UPDATED_AT";
                SyMUtil.getInstance();
                updateQuery2.setUpdateColumn(s, (Object)SyMUtil.getCurrentTimeInMillis());
                MDMUtil.getPersistence().update(updateQuery);
            }
            catch (final Exception exp) {
                this.logger.log(Level.SEVERE, "Exception in updateResourceCommandStatus", exp);
            }
        }
    }
    
    public void updateResourceCommandStatus(final Long commandID, final Long resourceID, final int commandRepositoryType, final int status) {
        this.logger.log(Level.INFO, "updateResourceCommandStatus(): commandID:{0}", commandID);
        this.logger.log(Level.INFO, "updateResourceCommandStatus(): resourceID:{0}", resourceID);
        this.logger.log(Level.INFO, "updateResourceCommandStatus(): status:{0}", status);
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdCommandsToDevice");
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
            final Criteria agentTypeCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
            final Criteria criteria = resourceCriteria.and(commandCriteria).and(agentTypeCriteria);
            updateQuery.setCriteria(criteria);
            updateQuery.setUpdateColumn("RESOURCE_COMMAND_STATUS", (Object)status);
            final UpdateQuery updateQuery2 = updateQuery;
            final String s = "UPDATED_AT";
            SyMUtil.getInstance();
            updateQuery2.setUpdateColumn(s, (Object)SyMUtil.getCurrentTimeInMillis());
            MDMUtil.getPersistence().update(updateQuery);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in updateResourceCommandStatus", exp);
        }
    }
    
    public void deleteResourceCommand(final Long commandID, final Long resourceID) {
        this.logger.log(Level.INFO, "deleteResourceCommand(): commandID:{0}", commandID);
        this.logger.log(Level.INFO, "deleteResourceCommand(): resourceID:{0}", resourceID);
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
            final Criteria criteria = resourceCriteria.and(commandCriteria);
            DataAccess.delete("MdCommandsToDevice", criteria);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in deleteResourceCommand", exp);
        }
    }
    
    public void deleteResourcesCommand(final Long commandID, final List resourceList) {
        this.logger.log(Level.INFO, "deleteResourceCommand(): commandID:{0}", commandID);
        this.logger.log(Level.INFO, "deleteResourceCommand(): resourceList:{0}", resourceList);
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
            final Criteria criteria = resourceCriteria.and(commandCriteria);
            DataAccess.delete("MdCommandsToDevice", criteria);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in deleteResourcesCommand", exp);
        }
    }
    
    public void deleteResourcesCommands(final List commandList, final List resourceList, final Integer commandRepositoryType) {
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandList.toArray(), 8);
            Criteria baseCriteria = resourceCriteria.and(commandCriteria);
            if (commandRepositoryType != null) {
                final Criteria commandRepositoryCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
                baseCriteria = baseCriteria.and(commandRepositoryCriteria);
            }
            DataAccess.delete(baseCriteria);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in deleteResourcesCommands ", exp);
        }
    }
    
    public Long getCommandID(final String commandUUID) {
        Long commandID = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommands"));
            final Criteria criteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)commandUUID, 0, false);
            sQuery.setCriteria(criteria);
            sQuery.addSelectColumn(new Column("MdCommands", "COMMAND_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (!dataObject.isEmpty()) {
                commandID = (Long)dataObject.getFirstValue("MdCommands", "COMMAND_ID");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getCommandID", exp);
        }
        this.logger.log(Level.INFO, "The command ID  {0} for CommandUUID {1}", new Object[] { commandID, commandUUID });
        return commandID;
    }
    
    public boolean checkCommandAvailableForDevice(final String commandUUID, final String deviceUDID) {
        boolean isCommandAvailable = false;
        try {
            final Long commandID = this.getCommandID(commandUUID);
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0, false);
            final Criteria udidCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "UDID"), (Object)deviceUDID, 0, false);
            final DataObject dataObject = MDMUtil.getPersistence().get("MdCommandsToDevice", commandCriteria.and(udidCriteria));
            if (!dataObject.isEmpty()) {
                isCommandAvailable = true;
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred in checkCommandAvailableForDevice() {0}", exp);
        }
        return isCommandAvailable;
    }
    
    public List getCommandsAvailableDeviceList(final List commandUUIDList, final List resourceList) {
        List resListToReturn = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommandsToDevice"));
            final Join mdCommandsToDevice = new Join("MdCommandsToDevice", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2);
            selectQuery.addJoin(mdCommandsToDevice);
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)commandUUIDList.toArray(), 8);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            selectQuery.setCriteria(commandCriteria.and(resourceCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdCommandsToDevice", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iter = dataObject.getRows("MdCommandsToDevice");
                resListToReturn = DBUtil.getColumnValuesAsList(iter, "RESOURCE_ID");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred in checkCommandAvailableForDevice() {0}", exp);
        }
        return resListToReturn;
    }
    
    public boolean checkCommandAvailableForDevice(final String commandUUID, final Long resourceID) {
        boolean isCommandAvailable = false;
        try {
            final Long commandID = this.getCommandID(commandUUID);
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0, false);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("MdCommandsToDevice", commandCriteria.and(resourceCriteria));
            if (!dataObject.isEmpty()) {
                isCommandAvailable = true;
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred in checkCommandAvailableForDevice() {0}", exp);
        }
        return isCommandAvailable;
    }
    
    public void deleteResourceCommand(final String commandUUID, final String deviceUDID) {
        this.logger.log(Level.INFO, "deleteResourceCommand(): commandUUID:{0}", commandUUID);
        this.logger.log(Level.INFO, "deleteResourceCommand(): deviceUDID:{0}", deviceUDID);
        try {
            final Long commandID = this.getCommandID(commandUUID);
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0, false);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "UDID"), (Object)deviceUDID, 0, false);
            DataAccess.delete("MdCommandsToDevice", commandCriteria.and(resourceCriteria));
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in deleteResourceCommand", exp);
        }
    }
    
    public void deleteResourceCommand(final String commandUUID, final String deviceUDID, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "deleteResourceCommand(): commandUUID:{0}", commandUUID);
        this.logger.log(Level.INFO, "deleteResourceCommand(): deviceUDID:{0}", deviceUDID);
        try {
            final Long commandID = this.getCommandID(commandUUID);
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0, false);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "UDID"), (Object)deviceUDID, 0, false);
            final Criteria cmdRepCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
            DataAccess.delete("MdCommandsToDevice", commandCriteria.and(resourceCriteria).and(cmdRepCriteria));
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public void deleteResourceCommand(final String commandUUID, final Long resourceID) {
        this.logger.log(Level.INFO, "deleteResourceCommand(): commandUUID:{0}", commandUUID);
        this.logger.log(Level.INFO, "deleteResourceCommand(): resourceID:{0}", resourceID);
        try {
            final Long commandID = this.getCommandID(commandUUID);
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0, false);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            DataAccess.delete("MdCommandsToDevice", commandCriteria.and(resourceCriteria));
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred in deleteResourceCommand() {0}", exp);
        }
    }
    
    public void deleteResourceCommand(final List<Long> resourceIDList, final List<Long> commandIDList) {
        this.logger.log(Level.INFO, "deleteResourceCommand(): commandUUID:{0}", commandIDList);
        this.logger.log(Level.INFO, "deleteResourceCommand(): resourceID:{0}", resourceIDList);
        try {
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandIDList.toArray(), 8, false);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceIDList.toArray(), 8);
            DataAccess.delete("MdCommandsToDevice", commandCriteria.and(resourceCriteria));
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred in deleteResourceCommand() {0}", exp);
        }
    }
    
    public void deleteMDCommandToDevice(final Long resourceID) {
        this.logger.log(Level.INFO, "deleteMDCommandToDevice(): resourceID:{0}", resourceID);
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            DataAccess.delete("MdCommandsToDevice", resourceCriteria);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred in deleteMDCommandToDevice() {0}", exp);
        }
    }
    
    protected void deleteAllCommandsFromDB(final Long resourceID, String udid) {
        this.logger.log(Level.INFO, "deleteAllCommandsFromDB(): resourceID:{0} udid:{1}", new Object[] { resourceID, udid });
        try {
            Criteria criteria = null;
            if (resourceID == null && udid == null) {
                return;
            }
            if (resourceID != null) {
                if (udid == null) {
                    udid = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID);
                }
                final Criteria resourceCriteria = criteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceID, 0);
                if (udid != null) {
                    final Criteria udidCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "UDID"), (Object)udid, 0);
                    criteria = criteria.or(udidCriteria);
                }
                DataAccess.delete("CommandHistory", new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)resourceID, 0));
            }
            else if (udid != null) {
                criteria = new Criteria(Column.getColumn("MdCommandsToDevice", "UDID"), (Object)udid, 0);
            }
            DataAccess.delete("MdCommandsToDevice", criteria);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred in deleteAllCommandsFromDB() {0}", exp);
        }
    }
    
    public void deleteResourcesCommand(final Long commandID) {
        this.logger.log(Level.INFO, "deleteResourcesCommand(): commandID:{0}", commandID);
        try {
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
            DataAccess.delete("MdCommandsToDevice", commandCriteria);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred in deleteResourcesCommand() {0}", exp);
        }
    }
    
    public void deleteCommand(final Long commandID) {
        this.logger.log(Level.INFO, "deleteCommand(): commandID: {0}", commandID);
        try {
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_ID"), (Object)commandID, 0);
            DataAccess.delete("MdCommands", commandCriteria);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred in deleteCommand() {0}", exp);
        }
    }
    
    protected HashMap getDeviceCommandCache() {
        HashMap metaDataMap = null;
        try {
            final String cacheName = "MDM_COMMANDS_STATUS";
            metaDataMap = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName, 2);
            if (metaDataMap == null) {
                metaDataMap = new HashMap();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in getDeviceCommandCache() {0}", ex);
        }
        return metaDataMap;
    }
    
    public boolean hasDeviceCommandInCacheOrRepo(final String strUDID) {
        final int platformType = ManagedDeviceHandler.getInstance().getPlatformType(strUDID);
        switch (platformType) {
            case 1: {
                return this.hasDeviceCommandInCacheOrRepo(strUDID, 1);
            }
            case 2: {
                return this.hasDeviceCommandInCacheOrRepo(strUDID, 1) || this.hasDeviceCommandInCacheOrRepo(strUDID, 2);
            }
            case 3: {
                return this.hasDeviceCommandInCacheOrRepo(strUDID, 1) || this.hasDeviceCommandInCacheOrRepo(strUDID, 2);
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean hasDeviceCommandInCacheOrRepo(final String strUDID, final int commandRepositoryType) {
        HashMap parentHash = null;
        final DeviceCommand command = null;
        final Long commandID = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommandsToDevice"));
            final Criteria resourceCommandStatusCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_COMMAND_STATUS"), (Object)12, 0);
            final Criteria agentTypeCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
            Criteria resourceCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "UDID"), (Object)strUDID, 0, false);
            final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
            if (resourceId != null) {
                resourceCriteria = resourceCriteria.or(new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceId, 0));
            }
            final Criteria criteria = resourceCriteria.and(resourceCommandStatusCriteria).and(agentTypeCriteria);
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("MdCommandsToDevice", "*"));
            final DataObject deviceCommandObject = MDMUtil.getPersistence().get(selectQuery);
            if (!deviceCommandObject.isEmpty()) {
                return true;
            }
            final long stime = System.currentTimeMillis();
            final long stime2;
            synchronized (DeviceCommandRepository.SYNCHRONIZE_COMMAND_CACHE) {
                stime2 = System.currentTimeMillis();
                parentHash = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("MDM_DEVICE_COMMANDS", 2);
                if (parentHash != null) {
                    final HashMap cacheHash = parentHash.get(commandRepositoryType);
                    if (cacheHash != null) {
                        final PriorityQueue<CommandQueueItem> commandList = cacheHash.get(strUDID);
                        if (commandList != null && !commandList.isEmpty()) {
                            this.logger.log(Level.INFO, "hasDeviceCommandInCacheOrRepo(): Wait Time For Cache - {0} Process Time - {1}", new Object[] { stime2 - stime, System.currentTimeMillis() - stime2 });
                            return true;
                        }
                    }
                }
            }
            this.logger.log(Level.INFO, "hasDeviceCommandInCacheOrRepo(): Wait Time For Cache - {0} Process Time - {1}", new Object[] { stime2 - stime, System.currentTimeMillis() - stime2 });
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in hasDeviceCommandInCache() {0}", ex);
        }
        return false;
    }
    
    private Boolean isCommandInCommandQueue(final PriorityQueue<CommandQueueItem> queue, final CommandQueueItem queueItem) {
        for (final CommandQueueItem nextQueueItem : queue) {
            if (nextQueueItem.equals(queueItem)) {
                return true;
            }
        }
        return false;
    }
    
    private PriorityQueue<CommandQueueItem> removeRendundantCommands(final PriorityQueue<CommandQueueItem> queueItems) {
        try {
            final PriorityQueue<CommandQueueItem> newQueueItems = new PriorityQueue<CommandQueueItem>();
            for (final CommandQueueItem queueItem : queueItems) {
                if (this.isCommandInCommandQueue(newQueueItems, queueItem)) {
                    continue;
                }
                newQueueItems.add(queueItem);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while removing redundant queueItems", e);
        }
        return queueItems;
    }
    
    public DeviceCommand getDeviceCommandFromCache(final String strUDID, final int commandRepositoryType) {
        HashMap parentHash = null;
        DeviceCommand command = null;
        Long commandID = null;
        final long stime = System.currentTimeMillis();
        long stime2 = 1L;
        try {
            synchronized (DeviceCommandRepository.SYNCHRONIZE_COMMAND_CACHE) {
                stime2 = System.currentTimeMillis();
                parentHash = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("MDM_DEVICE_COMMANDS", 2);
                if (parentHash != null) {
                    final HashMap cacheHash = parentHash.get(commandRepositoryType);
                    if (cacheHash != null) {
                        PriorityQueue<CommandQueueItem> commandList = (cacheHash.get(strUDID) instanceof ArrayList) ? this.getPriorityQueueForArrayList(cacheHash, strUDID) : cacheHash.get(strUDID);
                        commandList = this.removeRendundantCommands(commandList);
                        if (commandList != null && !commandList.isEmpty()) {
                            final CommandQueueItem commandQueueItem = commandList.peek();
                            final SequentialSubCommand sequentialSubCommand = SeqCmdUtils.getInstance().getCurrentSeqCmdOfResource(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID));
                            if (commandQueueItem.priority <= 40 && sequentialSubCommand != null && sequentialSubCommand.AddedAt.compareTo(commandQueueItem.addedTime) < 0) {
                                this.logger.log(Level.INFO, "Next command is dropped due to sequential command id:{0}", new Object[] { sequentialSubCommand.SequentialCommandID });
                                this.logger.log(Level.INFO, "getDeviceCommandFromCache(): Wait Time For Cache - {0} Process Time - {1}", new Object[] { stime2 - stime, System.currentTimeMillis() - stime2 });
                                return null;
                            }
                            commandID = commandQueueItem.commandID;
                            commandList.poll();
                            command = this.getCommandFromCache(commandID, commandRepositoryType);
                            CustomerInfoUtil.getInstance();
                            if (CustomerInfoUtil.isSAS()) {
                                ApiFactoryProvider.getCacheAccessAPI().putCache("MDM_DEVICE_COMMANDS", (Object)parentHash, 2);
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in getDeviceCommandFromCache() {0}", ex);
        }
        if (commandID != null) {
            this.updateResourceCommandStatus(commandID, strUDID, commandRepositoryType, 3);
        }
        this.logger.log(Level.INFO, "getDeviceCommandFromCache():Wait Time For Cache - {0} Process Time - {1}", new Object[] { stime2 - stime, System.currentTimeMillis() - stime2 });
        return command;
    }
    
    public boolean removeDeviceScanCommand(final String strUDID, final int commandRepositoryType) {
        HashMap parentHash = null;
        Map childHash = null;
        boolean isRemoved = false;
        try {
            parentHash = this.getDeviceCommandCache();
            if (parentHash != null) {
                final HashMap childcacheHash = parentHash.get(commandRepositoryType);
                if (childcacheHash != null) {
                    childHash = childcacheHash.get(strUDID);
                    if (childHash != null) {
                        for (final Map.Entry me : childHash.entrySet()) {
                            if (me.getValue().equals("YET_TO_SEND")) {
                                final String command = me.getKey();
                                if (command.contains("DeviceInformation") || command.contains("SecurityInfo") || command.contains("Restrictions") || command.contains("CertificateList") || command.contains("InstalledApplicationList") || command.contains("ManagedApplicationList") || command.contains("ProfileList") || command.contains("ProvisioningProfileList") || command.contains("AvailableOSUpdates") || command.contains("OSUpdateStatus")) {
                                    childHash.remove(command);
                                }
                                this.logger.log(Level.INFO, "{0} for device uuid, the {1} command to be removed, without processing.", new Object[] { strUDID, command });
                                break;
                            }
                        }
                    }
                    else {
                        this.logger.log(Level.INFO, "{0} for device uuid, no commands are available to process.", strUDID);
                    }
                }
            }
            isRemoved = true;
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in removeDeviceScanCommand() {0}", ex);
        }
        return isRemoved;
    }
    
    public boolean removeDeviceCommand(final String strUDID, final String commandName, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "removeDeviceCommand(): strUDID: {0}", strUDID);
        this.logger.log(Level.INFO, "removeDeviceCommand(): Command Name: {0}", commandName);
        HashMap parentHash = null;
        Map childHash = null;
        boolean isRemoved = false;
        try {
            parentHash = this.getDeviceCommandCache();
            if (parentHash != null) {
                final HashMap childcacheHash = parentHash.get(commandRepositoryType);
                if (childcacheHash != null) {
                    childHash = childcacheHash.get(strUDID);
                    if (childHash != null) {
                        for (final Map.Entry me : childHash.entrySet()) {
                            if (me.getValue().equals("YET_TO_SEND")) {
                                final String command = me.getKey();
                                if (command.contains(commandName)) {
                                    childHash.remove(command);
                                }
                                this.logger.log(Level.INFO, "{0} for device uuid, the {1} command to be removed, without processing.", new Object[] { strUDID, command });
                                break;
                            }
                        }
                    }
                    else {
                        this.logger.log(Level.INFO, "{0} for device uuid, no commands are available to process.", strUDID);
                    }
                }
            }
            isRemoved = true;
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in removeDeviceCommand() {0}", ex);
        }
        return isRemoved;
    }
    
    public void clearCommandFromDevice(final String strUDID, final Long resourceID, final String commandName) {
        this.clearCommandFromDevice(strUDID, resourceID, commandName, 1);
    }
    
    public void clearCommandFromDevice(final String strUDID, final Long resourceID, final String commandName, final int commandRepositoryType) {
        final boolean isCommandCleared = this.clearDeviceCommand(strUDID, commandName, commandRepositoryType);
        final boolean isCommandAvailableForDevice = this.checkCommandAvailableForDevice(commandName, resourceID);
        if (isCommandAvailableForDevice) {
            this.deleteResourceCommand(commandName, resourceID);
        }
        if (isCommandCleared || isCommandAvailableForDevice) {
            final String accessMessage = "DATA-REMOVED: " + commandName + "\t" + strUDID + "\t" + "Command-Removed";
            this.accesslogger.log(Level.INFO, accessMessage);
        }
    }
    
    public void clearAppClientRemoveDeviceCommand(final String strUDID) {
        final String commandName = "RemoveDevice";
        final int commandRepositoryType = 2;
        final boolean isCommandCleared = this.clearDeviceCommand(strUDID, commandName, commandRepositoryType);
        final boolean isCommandAvailableForDevice = this.checkCommandAvailableForDevice(commandName, strUDID);
        if (isCommandAvailableForDevice) {
            this.deleteResourceCommand(commandName, strUDID);
        }
        if (isCommandCleared || isCommandAvailableForDevice) {
            final String accessMessage = "DATA-REMOVED: " + commandName + "\t" + strUDID + "\t" + "Command-Removed";
            this.accesslogger.log(Level.INFO, accessMessage);
        }
    }
    
    public void clearAppClientCorporateWipeCommand(final String strUDID) {
        final String commandName = "CorporateWipe";
        final int commandRepositoryType = 2;
        final boolean isCommandCleared = this.clearDeviceCommand(strUDID, commandName, commandRepositoryType);
        final boolean isCommandAvailableForDevice = this.checkCommandAvailableForDevice(commandName, strUDID);
        if (isCommandAvailableForDevice) {
            this.deleteResourceCommand(commandName, strUDID);
        }
        if (isCommandCleared || isCommandAvailableForDevice) {
            final String accessMessage = "DATA-REMOVED: " + commandName + "\t" + strUDID + "\t" + "Command-Removed";
            this.accesslogger.log(Level.INFO, accessMessage);
        }
    }
    
    protected boolean clearDeviceCommand(final String strUDID, final String commandName, final int commandRepositoryType) {
        HashMap parentHash = null;
        boolean isCommandCleared = false;
        try {
            final Long commandId = (Long)DBUtil.getValueFromDB("MdCommands", "COMMAND_UUID", (Object)commandName, "COMMAND_ID");
            if (commandId != null) {
                parentHash = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("MDM_DEVICE_COMMANDS", 2);
                if (parentHash != null) {
                    final HashMap childCacheHash = parentHash.get(commandRepositoryType);
                    if (childCacheHash != null && !childCacheHash.isEmpty()) {
                        final PriorityQueue<CommandQueueItem> cacheCommandList = childCacheHash.get(strUDID);
                        final CommandQueueItem item = this.getCommandQueueItem(commandId, MDMUtil.getInstance().getResourceIDFromUDID(strUDID), commandRepositoryType, MDMUtil.getCurrentTimeInMillis());
                        if (cacheCommandList != null && cacheCommandList.remove(item)) {
                            parentHash.put(strUDID, cacheCommandList);
                            ApiFactoryProvider.getCacheAccessAPI().putCache("MDM_DEVICE_COMMANDS", (Object)parentHash, 2);
                            isCommandCleared = true;
                            this.logger.log(Level.INFO, "{0} for device uuid, the {1} command to be removed, without processing.", new Object[] { strUDID, commandId });
                        }
                        else {
                            this.logger.log(Level.INFO, "{0} for device uuid, no commands are available to process.", strUDID);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in clearRemoveDeviceCommand() {0}", ex);
        }
        return isCommandCleared;
    }
    
    public void clearDeviceCommand(final List<Long> resourceList, final List<Long> commandIDList) {
        this.logger.log(Level.INFO, "Clear Device Command. ResourceList : {0}, CommandIDList : {1}", new Object[] { resourceList, commandIDList });
        if (commandIDList != null && resourceList != null) {
            this.deleteResourceCommand(resourceList, commandIDList);
            this.refreshResourceCommandsToCache(resourceList, 1);
            final Map<Long, String> resID_UDIDMap = ManagedDeviceHandler.getInstance().getResourceID_UDID(resourceList);
            final Map<Long, String> commandInfo = this.getCommandInfoMap(commandIDList);
            final List<String> resUDIDList = new ArrayList<String>(resID_UDIDMap.values());
            final List<String> commandNameList = new ArrayList<String>(commandInfo.values());
            for (final String udid : resUDIDList) {
                for (final String commandName : commandNameList) {
                    final String accessMessage = "DATA-REMOVED: " + commandName + "\t" + udid + "\t" + "Command-Removed";
                    this.accesslogger.log(Level.INFO, accessMessage);
                }
            }
        }
    }
    
    public void addSyncAgentSettingsCommandForAndroid(List resourceList) {
        this.addSyncAgentSettingsCommand(resourceList, 1);
        resourceList = ManagedDeviceHandler.getInstance().filterProfileOwnerResource(resourceList);
        if (resourceList != null) {
            this.addSyncAgentSettingsCommand(resourceList, 2);
        }
    }
    
    public void addSyncAgentSettingsCommand(final List resourceList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addSyncAgentSettingsCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addSyncAgentSettingsCommand(): commandName: {0}", "SyncAgentSettings");
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "SyncAgentSettings";
        command.commandType = "SyncAgentSettings";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList, commandRepositoryType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addSyncAgentSettingsCommand  {0}", exp);
        }
    }
    
    public void addSyncPrivacySettingsCommand(final List resourceList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addSyncPrivacySettingsCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addSyncPrivacySettingsCommand(): commandName: {0}", "SyncPrivacySettings");
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "SyncPrivacySettings";
        command.commandType = "SyncPrivacySettings";
        command.commandStr = "--";
        try {
            final Long commandID = this.addCommand(command);
            final ArrayList<Long> commandList = new ArrayList<Long>();
            commandList.add(commandID);
            this.assignCommandToDevicesInChunck(resourceList, commandList, commandRepositoryType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addSyncPrivacySettingsCommand  ", exp);
        }
    }
    
    public void addLocationConfigurationCommand(final List resourceList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addLocationHistorySettingsCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addLocationHistorySettingsCommand(): commandName: {0}", "LocationConfiguration");
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "LocationConfiguration";
        command.commandType = "LocationConfiguration";
        command.commandStr = "--";
        try {
            final Long commandID = this.addCommand(command);
            final ArrayList<Long> commandList = new ArrayList<Long>();
            commandList.add(commandID);
            this.assignCommandToDevicesInChunck(resourceList, commandList, commandRepositoryType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addSyncAgentSettingsCommand  {0}", exp);
        }
    }
    
    public void addAndroidPasscodeRecoveryCommand(final List resourceList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addAndroidPasscodeRecoveryCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addAndroidPasscodeRecoveryCommand(): commandName: {0}", "AndroidPasscodeRecoveryCommand");
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "AndroidPasscodeRecoveryCommand";
        command.commandType = "AndroidPasscodeRecoveryCommand";
        command.commandStr = "--";
        try {
            final Long commandID = this.addCommand(command);
            final ArrayList<Long> commandList = new ArrayList<Long>();
            commandList.add(commandID);
            this.assignCommandToDevicesInChunck(resourceList, commandList, commandRepositoryType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addAndroidPasscodeRecoveryCommand  {0}", exp);
        }
    }
    
    public void addBatteryConfigurationCommand(final List<Long> resourceList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addBatteryTrackingCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addBatteryTrackingCommand(): commandName: {0}", "BATTERY_CONFIGURATION");
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "BATTERY_CONFIGURATION";
        command.commandType = "BATTERY_CONFIGURATION";
        command.commandStr = "--";
        try {
            final Long commandID = this.addCommand(command);
            final ArrayList<Long> commandList = new ArrayList<Long>();
            commandList.add(commandID);
            final HashMap<Long, ArrayList<Long>> commandMap = new HashMap<Long, ArrayList<Long>>();
            for (final Long resourceID : resourceList) {
                commandMap.put(resourceID, commandList);
            }
            this.assignCommandToDevices(commandMap, commandRepositoryType);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in addBatteryTrackingCommand  {0}", e);
        }
    }
    
    public void addLocationSettingsCommand(final List resourceList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addLocationSettingsCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addLocationSettingsCommand(): commandName: {0}", "LocationSettings");
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "LocationSettings";
        command.commandType = "LocationSettings";
        command.commandStr = "--";
        try {
            final Long commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList, commandRepositoryType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addSyncAgentSettingsCommand  {0}", exp);
        }
    }
    
    public void addKNOXAvailabilityCommand(final List resourceList, final String commandType) {
        this.logger.log(Level.INFO, "addKNOXAvailabilityCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addKNOXAvailabilityCommand(): commandName: {0}", commandType);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandType;
        command.commandType = commandType;
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addKNOXAvailabilityCommand  {0}", exp);
        }
    }
    
    public void addSAFEApkMigrateCommand(final List resourceList) {
        final String commandType = "AgentMigrate";
        this.logger.log(Level.INFO, "addSAFEApkMigrateCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addSAFEApkMigrateCommand(): commandName: {0}", commandType);
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandType;
        command.commandType = commandType;
        command.commandStr = "--";
        try {
            final Long commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addSAFEApkMigrateCommand  {0}", exp);
        }
    }
    
    public void addUnmanageOldAgentCommand(final List resourceList) {
        final String commandType = "RemoveOldAgent";
        this.logger.log(Level.INFO, "addUnmanageOldAgentCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addUnmanageOldAgentCommand(): commandName: {0}", commandType);
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandType;
        command.commandType = commandType;
        command.commandStr = "--";
        try {
            final Long commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addUnmanageOldAgentCommand  {0}", exp);
        }
    }
    
    public void addAgentUpgradeCommand(final List resourceList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addAgentUpgradeCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addAgentUpgradeCommand(): commandName: {0}", "AgentUpgrade");
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "AgentUpgrade";
        command.commandType = "AgentUpgrade";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList, commandRepositoryType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addAgentUpgradeCommand {0}", exp);
        }
    }
    
    public void addApplicationConfigurationCommand(final List resourceList) {
        this.logger.log(Level.INFO, "addApplicationConfigurationCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addApplicationConfigurationCommand(): commandName: {0}", "ManagedApplicationConfiguration");
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "ManagedApplicationConfiguration";
        command.commandType = "ManagedApplicationConfiguration";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList, 1);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addApplicationConfigurationCommand {0}", exp);
        }
    }
    
    public void addAppPermissionPolicyCommand(final List resourceList) {
        this.logger.log(Level.INFO, "addAppPermissionPolicyCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addAppPermissionPolicyCommand(): commandName: {0}", "ManagedAppPermissionPolicy");
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "ManagedAppPermissionPolicy";
        command.commandType = "ManagedAppPermissionPolicy";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList, 1);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addAppPermissionPolicyCommand {0}", exp);
        }
    }
    
    public void addUpgradeMobileConfigCommand(final String commandName, final List resourceList) {
        this.logger.log(Level.INFO, "addUpgradeMobileConfigCommand(): resourceList: {0}", resourceList);
        this.logger.log(Level.INFO, "addUpgradeMobileConfigCommand(): commandName: {0}", commandName);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandName;
        command.commandType = "InstallProfile";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addUpgradeMobileConfigCommand {0}", exp);
        }
    }
    
    public void addGCMReRegisterCommand(final List resourceList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addGCMReRegisterCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addGCMReRegisterCommand(): commandName: {0}", "ReregisterNotificationToken");
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "ReregisterNotificationToken";
        command.commandType = "ReregisterNotificationToken";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList, commandRepositoryType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addGCMReRegisterCommand  {0}", exp);
        }
    }
    
    public void addBlacklistAppCommand(final List resourceList, final int scope) {
        this.logger.log(Level.INFO, "addBlacklistAppCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addBlacklistAppCommand(): commandName: {0}", "BlacklistWhitelistApp");
        String commandName = "BlacklistWhitelistApp";
        if (scope == 1) {
            commandName = "BlacklistWhitelistAppContainer";
        }
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandName;
        command.commandType = commandName;
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addBlacklistAppCommand  {0}", exp);
        }
    }
    
    public void removeAllMdCommandToDeviceForResource(final Long resourceID) {
        this.logger.log(Level.INFO, "removeAllMdCommandToDeviceForResource(): resourceID: {0}", resourceID);
        try {
            this.deleteMDCommandToDevice(resourceID);
            this.clearAllCommandsForResource(resourceID);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in removeAllMdCommandToDeviceForResource {0}", exp);
        }
    }
    
    public void removeAllCommandsForResource(final Long resourceID, final String udid) {
        this.logger.log(Level.INFO, "removeAllCommandsForResource(): resourceID: {0}", resourceID);
        try {
            this.deleteAllCommandsFromDB(resourceID, udid);
            this.clearAllCommandsFromCache(resourceID, udid);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in removeAllCommandsForResource {0}", exp);
        }
    }
    
    protected void clearAllCommandsFromCache(final Long resourceID, String udid) {
        try {
            if (udid == null && resourceID != null) {
                udid = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID);
            }
            if (udid != null) {
                final long stime = System.currentTimeMillis();
                synchronized (DeviceCommandRepository.SYNCHRONIZE_COMMAND_CACHE) {
                    final long stime2 = System.currentTimeMillis();
                    final HashMap parentHash = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("MDM_DEVICE_COMMANDS", 2);
                    if (parentHash != null && !parentHash.isEmpty()) {
                        final PriorityQueue<CommandQueueItem> cacheCommandList = new PriorityQueue<CommandQueueItem>();
                        cacheCommandList.addAll((Collection<?>)this.clearAllCommandsForResourceInCmdRepository(parentHash, udid, 1));
                        cacheCommandList.addAll((Collection<?>)this.clearAllCommandsForResourceInCmdRepository(parentHash, udid, 2));
                        cacheCommandList.addAll((Collection<?>)this.clearAllCommandsForResourceInCmdRepository(parentHash, udid, 3));
                        if (cacheCommandList != null) {
                            ApiFactoryProvider.getCacheAccessAPI().putCache("MDM_DEVICE_COMMANDS", (Object)parentHash, 2);
                            this.logger.log(Level.INFO, "For UUID {0}, Commands {1} removed without processing.", new Object[] { udid, cacheCommandList });
                        }
                    }
                    this.logger.log(Level.INFO, "clearAllCommandsFromCache(): Wait Time For Cache - {0} Process Time - {1}", new Object[] { stime2 - stime, System.currentTimeMillis() - stime2 });
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in clearAllCommandsFromCache() {0}", ex);
        }
    }
    
    protected PriorityQueue<CommandQueueItem> clearAllCommandsForResourceInCmdRepository(final HashMap parentHash, final String udid, final int cmdRepositoryType) {
        final HashMap childCacheHash = parentHash.get(cmdRepositoryType);
        if (childCacheHash != null && !childCacheHash.isEmpty()) {
            final PriorityQueue<CommandQueueItem> cacheCommandList = childCacheHash.remove(udid);
            if (cacheCommandList != null) {
                return cacheCommandList;
            }
        }
        return new PriorityQueue<CommandQueueItem>();
    }
    
    protected void clearAllCommandsForResource(final Long resourceID) {
        this.clearAllCommandsFromCache(resourceID, null);
    }
    
    public Long addDeviceClientSettingsCommand(final Long resourceID) {
        this.logger.log(Level.INFO, "addDeviceClientSettingsCommand(): resourceID:", resourceID);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "DeviceClientSettings";
        command.commandType = "DeviceClientSettings";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevice("DeviceClientSettings", resourceID);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addDeviceClientSettingsCommand", exp);
        }
        return commandID;
    }
    
    public Long addDeviceCommunicationCommand(final List resourceList) {
        this.logger.log(Level.INFO, "addDeviceCommunicationCommand(): resourceID:{0}", resourceList);
        Long commandID = null;
        try {
            commandID = this.addCommand("DeviceCommunicationPush");
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addDeviceCommunicationCommand", exp);
        }
        return commandID;
    }
    
    public Long addChannelUriCommand(final List resourceList) {
        this.logger.log(Level.INFO, "addDeviceCommunicationCommand(): resourceID:{0}", resourceList);
        Long commandID = null;
        try {
            commandID = this.addCommand("GetChannelUri");
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addChannelUriCommand", exp);
        }
        return commandID;
    }
    
    public Long addEnableLostModeCommand(final List resourceList) throws Exception {
        this.logger.log(Level.INFO, "addEnableLostModeCommand(): resource list:{0}", resourceList);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "EnableLostMode";
        command.commandType = "EnableLostMode";
        commandID = this.addCommand(command);
        this.assignCommandToDevices(commandID, resourceList);
        return commandID;
    }
    
    public Long addDisableLostModeCommand(final List resourceList) throws Exception {
        this.logger.log(Level.INFO, "addDisableLostModeCommand(): resource list:{0}", resourceList);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "DisableLostMode";
        command.commandType = "DisableLostMode";
        commandID = this.addCommand(command);
        this.assignCommandToDevices(commandID, resourceList);
        return commandID;
    }
    
    public Long addLostModeDeviceLocationCommand(final List resourceList) throws Exception {
        this.logger.log(Level.INFO, "addLostModeDeviceLocationCommand(): resource list:{0}", resourceList);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "LostModeDeviceLocation";
        command.commandType = "LostModeDeviceLocation";
        commandID = this.addCommand(command);
        this.assignCommandToDevices(commandID, resourceList);
        return commandID;
    }
    
    public Long addDeviceInformationCommand(final List resourceList) throws Exception {
        this.logger.log(Level.INFO, "addDeviceInformationCommand(): resource list:{0}", resourceList);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "DeviceInformation";
        command.commandType = "DeviceInformation";
        commandID = this.addCommand(command);
        this.assignCommandToDevices(commandID, resourceList);
        return commandID;
    }
    
    public Long addClearAppDataCommand(final List resourceList) throws Exception {
        this.logger.log(Level.INFO, "addClearAppDataCommand(): resource list:{0}", resourceList);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "ClearAppData";
        command.commandType = "ClearAppData";
        commandID = this.addCommand(command);
        this.assignCommandToDevices(commandID, resourceList);
        return commandID;
    }
    
    public Long addAndroidDeviceInformationCommand(final List resourceList) throws Exception {
        this.logger.log(Level.INFO, "addDeviceInformationCommand(): resource list:{0}", resourceList);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "DeviceInfo";
        command.commandType = "DeviceInfo";
        commandID = this.addCommand(command);
        this.assignCommandToDevices(commandID, resourceList);
        return commandID;
    }
    
    public Long addRefreshTokenUpdateCommand(final List resourceList) throws Exception {
        this.logger.log(Level.INFO, "addDeviceInformationCommand(): resource list:{0}", resourceList);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "RefreshTokenUpdate";
        command.commandType = "RefreshTokenUpdate";
        commandID = this.addCommand(command);
        this.assignCommandToDevices(commandID, resourceList);
        return commandID;
    }
    
    public void addDeviceCommand(final Long resourceID, final String strCommandName, final String strCommandUUID) {
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("ManagedDevice", resourceCriteria);
            if (dataObject.containsTable("ManagedDevice")) {
                final String deviceUDID = (String)dataObject.getFirstValue("ManagedDevice", "UDID");
                this.addDeviceCommand(deviceUDID, strCommandName, strCommandUUID);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred in addDeviceCommand() {0}", exp);
        }
    }
    
    public synchronized void addDeviceCommand(final String strUDID, final String strCommandName, final String strCommandUUID) {
        try {
            this.logger.log(Level.FINE, "********** addDeviceCommand() started **************** ");
            this.logger.log(Level.INFO, "UDID: {0}", strUDID);
            this.logger.log(Level.INFO, "Command Name: {0}", strCommandName);
            this.logger.log(Level.INFO, "CommandUUID: {0}", strCommandUUID);
            final String cacheName = "MDM_COMMANDS_STATUS";
            final HashMap commandHash = new HashMap();
            final String Status = "YET_TO_SEND";
            if (strCommandName.equals("DeviceScan")) {
                final String deviceCommand = "DeviceInformation;" + strCommandUUID;
                final String securityCommand = "SecurityInfo;" + strCommandUUID;
                final String restrictionsCommand = "Restrictions;" + strCommandUUID;
                final String certificateCommand = "CertificateList;" + strCommandUUID;
                final String installedAppCommand = "InstalledApplicationList;" + strCommandUUID;
                final String managedAppListCommand = "ManagedApplicationList;" + strCommandUUID;
                final String profileListCmd = "ProfileList;" + strCommandUUID;
                final String provProfileListCmd = "ProvisioningProfileList;" + strCommandUUID;
                commandHash.put(deviceCommand, Status);
                commandHash.put(securityCommand, Status);
                commandHash.put(restrictionsCommand, Status);
                commandHash.put(certificateCommand, Status);
                commandHash.put(installedAppCommand, Status);
                commandHash.put(managedAppListCommand, Status);
                commandHash.put(profileListCmd, Status);
                commandHash.put(provProfileListCmd, Status);
            }
            else if (strCommandName.equals("InstallProfile")) {
                final String deviceCommand = strCommandUUID;
                commandHash.put(deviceCommand, Status);
            }
            else if (strCommandName.equals("RemoveProfile")) {
                final String deviceCommand = strCommandUUID;
                commandHash.put(deviceCommand, Status);
            }
            else if (strCommandName.equals("InstallApplication")) {
                final String deviceCommand = strCommandUUID;
                commandHash.put(deviceCommand, Status);
            }
            else if (strCommandName.equals("RemoveApplication")) {
                final String deviceCommand = strCommandUUID;
                commandHash.put(deviceCommand, Status);
            }
            else {
                final String deviceCommand = strCommandName + ";" + strCommandUUID;
                commandHash.put(deviceCommand, Status);
            }
            final HashMap cacheHash = this.getDeviceCommandCache();
            if (cacheHash != null) {
                final HashMap cacheCommandHash = cacheHash.get(strUDID);
                if (cacheCommandHash != null) {
                    cacheCommandHash.putAll(commandHash);
                }
                else {
                    cacheHash.put(strUDID, commandHash);
                }
            }
            this.logger.log(Level.INFO, "addDeviceCommand: Command Hash going to update in cache: {0}", commandHash);
            ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, (Object)cacheHash, 2);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in addDeviceCommand() {0}", ex);
        }
    }
    
    public String getDeviceCommand(final String strUDID) {
        HashMap parentHash = null;
        Map childHash = null;
        String command = null;
        try {
            parentHash = this.getDeviceCommandCache();
            if (parentHash != null) {
                childHash = parentHash.get(strUDID);
                if (childHash != null) {
                    for (final Map.Entry me : childHash.entrySet()) {
                        if (me.getValue().equals("YET_TO_SEND")) {
                            command = me.getKey();
                            childHash.remove(command);
                            this.logger.log(Level.INFO, "{0} for device uuid, the {1} command to process.", new Object[] { strUDID, command });
                            break;
                        }
                    }
                }
                else {
                    this.logger.log(Level.INFO, "{0} for device uuid, no commands are available to process.", strUDID);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in getDeviceCommand() {0}", ex);
        }
        return command;
    }
    
    protected Long addNativeAppCommand(final List resourceList, final String commandName) {
        final Long commandID = this.addCommand(commandName);
        this.assignCommandToDevices(commandID, resourceList, 2);
        return commandID;
    }
    
    protected Long addNativeAppSecurityCommand(final String udid, final String commandName) {
        final Long commandID = this.addCommand(commandName);
        this.assignCommandToDevice(commandID, udid, 2);
        return commandID;
    }
    
    public Long addNativeAppChannelUriCommand(final List resourceList) {
        this.logger.log(Level.INFO, "addNativeAppChannelUriCommand(): resourceID:{0}", resourceList);
        return this.addNativeAppCommand(resourceList, "AppNotificationCredential");
    }
    
    public Long addSyncAppCatalogCommand(final List resourceList) {
        this.logger.log(Level.INFO, "addSyncAppCatalogCommand(): resourceID:{0}", resourceList);
        return this.addNativeAppCommand(resourceList, "SyncAppCatalog");
    }
    
    public Long addAppCatalogStatusSummaryCommand(final List resourceList) {
        this.logger.log(Level.INFO, "addAppCatalogStatusSummaryCommand(): resourceID:{0}", resourceList);
        return this.addNativeAppCommand(resourceList, "AppCatalogSummary");
    }
    
    public Long addLocationCommand(final List resourceList, final int cmdRepositoryType) {
        this.logger.log(Level.INFO, "addLocationCommand(): resourceID:{0}", resourceList);
        Long commandID = null;
        if (cmdRepositoryType == 2) {
            commandID = this.addNativeAppCommand(resourceList, "GetLocation");
        }
        else if (cmdRepositoryType == 1) {
            commandID = this.addWindowsCommand(resourceList, "GetLocation");
        }
        return commandID;
    }
    
    public Long addCorporateWipeCommand(final String udid) {
        this.logger.log(Level.INFO, "addCorporateWipeCommand(): UDID:{0}", udid);
        return this.addNativeAppSecurityCommand(udid, "CorporateWipe");
    }
    
    public Long addNativeAppRemoveDeviceCommand(final String udid) {
        this.logger.log(Level.INFO, "addNativeAppRemoveDeviceCommand(): UDID:{0}", udid);
        return this.addNativeAppSecurityCommand(udid, "RemoveDevice");
    }
    
    public void addLanguageLicenseCommand(final List resourceList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addLanguageLicenseCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addLanguageLicenseCommand(): commandName: {0}", "LanguagePackUpdate");
        Long commandID = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_TYPE"), (Object)"LanguagePackUpdate", 0, false);
            DataObject dataObject = MDMUtil.getPersistence().get("MdCommands", criteria);
            if (!dataObject.isEmpty()) {
                commandID = (Long)dataObject.getFirstValue("MdCommands", "COMMAND_ID");
            }
            else {
                final Row commandRow = new Row("MdCommands");
                commandRow.set("COMMAND_TYPE", (Object)"LanguagePackUpdate");
                commandRow.set("COMMAND_UUID", (Object)"LanguagePackUpdate");
                commandRow.set("COMMAND_DATA_VALUE", (Object)"--");
                dataObject.addRow(commandRow);
                dataObject = MDMUtil.getPersistence().add(dataObject);
                commandID = (Long)dataObject.getFirstValue("MdCommands", "COMMAND_ID");
            }
            this.assignCommandToDevices(commandID, resourceList, commandRepositoryType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addLanguageLicenseCommand  {0}", exp);
        }
    }
    
    public Boolean clearCommandsFromCacheForResources(final List commandIDList, final List resourceList, final int commandRepositoryType) {
        Boolean isCommandCleared = null;
        try {
            HashMap parentHash = null;
            isCommandCleared = false;
            final List<String> udidList = ManagedDeviceHandler.getInstance().getUDIDListForResourceIDList(resourceList);
            final List<CommandQueueItem> commandList = this.getQueueItemListforCommandList(commandIDList, commandRepositoryType);
            if (commandIDList != null && udidList != null) {
                for (final String strUDID : udidList) {
                    parentHash = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("MDM_DEVICE_COMMANDS", 2);
                    if (parentHash != null) {
                        final HashMap childCacheHash = parentHash.get(commandRepositoryType);
                        if (childCacheHash == null || childCacheHash.isEmpty()) {
                            continue;
                        }
                        final PriorityQueue<CommandQueueItem> cacheCommandList = childCacheHash.get(strUDID);
                        if (cacheCommandList != null) {
                            cacheCommandList.removeAll(commandList);
                            parentHash.put(strUDID, cacheCommandList);
                            ApiFactoryProvider.getCacheAccessAPI().putCache("MDM_DEVICE_COMMANDS", (Object)parentHash, 2);
                            isCommandCleared = true;
                            this.logger.log(Level.INFO, "{0} for device uuid, the {1} command to be removed, without processing.", new Object[] { strUDID, commandIDList });
                            final String accessMessage = "DATA-REMOVED: " + commandIDList.toString() + "\t" + strUDID + "\t" + "Command-Removed";
                            this.accesslogger.log(Level.INFO, accessMessage);
                        }
                        else {
                            this.logger.log(Level.INFO, "{0} for device uuid, no commands are available to process.", strUDID);
                        }
                    }
                }
            }
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception occured while removing commands from cache", (Throwable)ex);
        }
        return isCommandCleared;
    }
    
    public void adduploadAgentLogCommand(final Long resourceId, final int commandRepType) {
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "UploadAgentLogs";
        command.commandType = "UploadAgentLogs";
        command.commandStr = "--";
        try {
            final Long commandID = this.addCommand(command);
            final List resourceList = new ArrayList();
            resourceList.add(resourceId);
            this.assignCommandToDevices(commandID, resourceList, commandRepType);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void addOrUpdateCommandOnPPM(final Map cmdData) {
        this.logger.log(Level.INFO, "addCommandOnPPM data : {0}", cmdData);
        final String commandUUID = cmdData.get("CommandUUID");
        if (commandUUID != null) {
            Long commandID = null;
            final DeviceCommand command = new DeviceCommand();
            command.commandUUID = commandUUID;
            command.commandType = commandUUID;
            command.commandStr = "--";
            if (cmdData.containsKey("PRIORITY")) {
                command.priority = cmdData.get("PRIORITY");
            }
            try {
                commandID = this.addCommand(command);
                cmdData.put("CommandID", commandID);
                this.assignCommandToDevices(cmdData);
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception occurred while adding commnad. command name: ", ex);
            }
        }
    }
    
    protected void assignCommandToDevices(final Map cmdData) {
        final Long commandID = cmdData.get("CommandID");
        final List resourceList = cmdData.get("RresourceList");
        final int commandRepositoryType = cmdData.get("CMDRepType");
        this.logger.log(Level.INFO, "assignCommandToDevice(): commandID:{0}", commandID);
        this.logger.log(Level.INFO, "assignCommandToDevice(): resourceList:{0}", resourceList);
        try {
            DataObject deviceCommandObject = MDMUtil.getPersistence().constructDataObject();
            final Criteria commandIDCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
            final Criteria resourceIDCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            Criteria criteria = resourceIDCriteria.and(commandIDCriteria);
            deviceCommandObject = MDMUtil.getPersistence().get("MdCommandsToDevice", criteria);
            Criteria resourceCriteria = null;
            final Column resourceColumn = Column.getColumn("MdCommandsToDevice", "RESOURCE_ID");
            for (int i = 0; i < resourceList.size(); ++i) {
                resourceCriteria = new Criteria(resourceColumn, resourceList.get(i), 0);
                criteria = resourceCriteria.and(commandIDCriteria);
                Row deviceCommandRow = deviceCommandObject.getRow("MdCommandsToDevice", criteria);
                if (deviceCommandRow == null) {
                    deviceCommandRow = new Row("MdCommandsToDevice");
                    deviceCommandRow.set("RESOURCE_ID", resourceList.get(i));
                    deviceCommandRow.set("COMMAND_ID", (Object)commandID);
                    deviceCommandRow.set("COMMAND_REPOSITORY_TYPE", (Object)commandRepositoryType);
                    deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                    final Row row = deviceCommandRow;
                    final String s = "ADDED_AT";
                    SyMUtil.getInstance();
                    row.set(s, (Object)SyMUtil.getCurrentTimeInMillis());
                    final Row row2 = deviceCommandRow;
                    final String s2 = "UPDATED_AT";
                    SyMUtil.getInstance();
                    row2.set(s2, (Object)SyMUtil.getCurrentTimeInMillis());
                    deviceCommandObject.addRow(deviceCommandRow);
                }
                else {
                    deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                    deviceCommandObject.updateRow(deviceCommandRow);
                }
            }
            MDMUtil.getPersistence().update(deviceCommandObject);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public Map<Long, String> getCommandInfoMap(final List<Long> commandID) {
        final Map<Long, String> commandInfoMap = new HashMap<Long, String>();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_ID"), (Object)commandID.toArray(), 8);
            final DataObject dataObj = MDMUtil.getPersistence().get("MdCommands", criteria);
            if (!dataObj.isEmpty()) {
                final Iterator deviceIterator = dataObj.getRows("MdCommands");
                while (deviceIterator.hasNext()) {
                    final Row row = deviceIterator.next();
                    commandInfoMap.put((Long)row.get("COMMAND_ID"), (String)row.get("COMMAND_UUID"));
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return commandInfoMap;
    }
    
    public void addSystemAppCommand(final Long resourceID) {
        this.logger.log(Level.INFO, "addSystemAppCommand(): resourceID: {0}", resourceID);
        this.logger.log(Level.INFO, "addSystemAppCommand(): commandName: {0}", "PreloadedAppsInfo");
        final String commandName = "PreloadedAppsInfo";
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandName;
        command.commandType = commandName;
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addSystemAppCommand  {0}", exp);
        }
    }
    
    public void addSmsPublicKeyDistributorCommand(final Long resourceID) {
        this.logger.log(Level.INFO, "addSmsPublicKeyDistributorCommand(): resourceID: {0}", resourceID);
        this.logger.log(Level.INFO, "addSmsPublicKeyDistributorCommand(): commandName: {0}", "SavePublicKey");
        final String publishKeyCommandName = "SavePublicKey";
        Long publishCommandID = null;
        final DeviceCommand publishKeyCommand = new DeviceCommand();
        publishKeyCommand.commandUUID = publishKeyCommandName;
        publishKeyCommand.commandType = publishKeyCommandName;
        try {
            publishCommandID = this.addCommand(publishKeyCommand);
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            this.assignCommandToDevices(publishCommandID, resourceList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addSmsPublicKeyDistributorCommand{0}", exp);
        }
    }
    
    public void addCertificateDistributeCommand(final Long resourceId) {
        this.logger.log(Level.INFO, "addCertificateDistributeCommand() : resourceID : {0} ", resourceId);
        final String publishCertificateCommand = "CertificateRequest";
        Long publishCommandId = null;
        final DeviceCommand publishCertificate = new DeviceCommand();
        publishCertificate.commandUUID = publishCertificateCommand;
        publishCertificate.commandType = publishCertificateCommand;
        try {
            publishCommandId = this.addCommand(publishCertificate);
            final List resourceList = new ArrayList();
            resourceList.add(resourceId);
            this.assignCommandToDevices(publishCommandId, resourceList);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in pushing the public certifcate {0}", e.toString());
        }
    }
    
    public void addSystemAppContainerCommand(final Long resourceID) {
        this.logger.log(Level.INFO, "addSystemAppContainerCommand(): resourceID: {0}", resourceID);
        this.logger.log(Level.INFO, "addSystemAppContainerCommand(): commandName: {0}", "PreloadedContainerAppsInfo");
        final String commandName = "PreloadedContainerAppsInfo";
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandName;
        command.commandType = commandName;
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addSystemAppContainerCommand  {0}", exp);
        }
    }
    
    public void addDeviceNameCommand(final Long resourceID) {
        this.accesslogger.log(Level.INFO, "addDeviceName(): resourceID: {0}", resourceID);
        this.accesslogger.log(Level.INFO, "addDeviceName(): commandName: {0}", "DeviceName");
        final String commandName = "DeviceName";
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandName;
        command.commandType = commandName;
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            this.accesslogger.log(Level.SEVERE, "Exception in addDeviceName  {0}", exp);
        }
        this.accesslogger.log(Level.INFO, "Exiting addDeviceName()");
    }
    
    public void addMDMDefaultAppConfiguration(final List resourceIDs) {
        this.logger.log(Level.INFO, "addMDMDefaultAppConfiguration(): resourceIDs:", resourceIDs);
        try {
            Long commandID = null;
            final DeviceCommand command = new DeviceCommand();
            command.commandUUID = "MDMDefaultApplicationConfiguration";
            command.commandType = "MDMDefaultApplicationConfiguration";
            command.commandStr = "--";
            command.dynamicVariable = true;
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceIDs);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addMDMDefaultAppConfiguration", exp);
        }
    }
    
    public void addTermsSyncCommand(final List<Long> resourceIDs, final int commandRepositoryType) {
        final String commandName = "TermsOfUse";
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandName;
        command.commandType = commandName;
        command.commandStr = "--";
        try {
            final Long commandID = this.addCommand(command);
            final ArrayList<Long> commandList = new ArrayList<Long>();
            commandList.add(commandID);
            this.assignCommandToDevicesInChunck(resourceIDs, commandList, commandRepositoryType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addTermsSyncCommand  {0}", exp);
        }
        this.logger.log(Level.INFO, "Exiting addTermsSyncCommand()");
    }
    
    public void addContentSyncCommand(final List<Long> resourceIDs, final int commandRepType) {
        final String commandName = "SyncDocuments";
        this.accesslogger.log(Level.INFO, "addContentSyncCommand(): resourceID: {0}", resourceIDs);
        this.accesslogger.log(Level.INFO, "addContentSyncCommand(): commandName: {0}", "SyncDocuments");
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandName;
        command.commandType = commandName;
        command.commandStr = "--";
        try {
            final Long commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceIDs, commandRepType);
        }
        catch (final Exception exp) {
            this.accesslogger.log(Level.SEVERE, "Exception in addContentSyncCommand  {0}", exp);
        }
        this.accesslogger.log(Level.INFO, "Exiting addContentSyncCommand()");
    }
    
    public void addAnnouncementSyncCommand(final List<Long> resourceIds, final int commandRepType) {
        final String commandName = "SyncAnnouncement";
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandName;
        command.commandType = commandName;
        command.commandStr = "--";
        try {
            final Long commandID = this.addCommand(command);
            final ArrayList<Long> commandList = new ArrayList<Long>();
            commandList.add(commandID);
            final HashMap<Long, ArrayList<Long>> deviceToCommandMap = new HashMap<Long, ArrayList<Long>>();
            for (final Long resourceId : resourceIds) {
                deviceToCommandMap.put(resourceId, commandList);
            }
            this.assignCommandToDevices(deviceToCommandMap, commandRepType);
        }
        catch (final Exception exp) {
            this.accesslogger.log(Level.SEVERE, "Exception in addContentSyncCommand  {0}", exp);
        }
        this.accesslogger.log(Level.INFO, "Exiting addContentSyncCommand()");
    }
    
    public boolean isCommandInprogress(final Long resourceID, final String cmdUUID) {
        boolean isInprogress = false;
        try {
            final Long commandID = this.getCommandID(cmdUUID);
            final Criteria resCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria commandUUID = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
            final DataObject commandDO = MDMUtil.getPersistence().get("MdCommandsToDevice", resCriteria.and(commandUUID));
            if (!commandDO.isEmpty()) {
                isInprogress = true;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while isCommandInprogress", ex);
        }
        return isInprogress;
    }
    
    public int getDeviceCommandStatus(final Long resourceId, final String cmdUUID) {
        int status = -1;
        try {
            final Long commandID = this.getCommandID(cmdUUID);
            final Criteria resCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria commandUUID = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
            final DataObject commandDO = MDMUtil.getPersistence().get("MdCommandsToDevice", resCriteria.and(commandUUID));
            if (!commandDO.isEmpty()) {
                final Row commandRow = commandDO.getFirstRow("MdCommandsToDevice");
                status = (int)commandRow.get("RESOURCE_COMMAND_STATUS");
            }
            else {
                status = 3;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getDeviceCommandStatus", ex);
        }
        return status;
    }
    
    protected Long getCommandAddedTime(final Long commandID, final Long resourceID) throws DataAccessException {
        final SequentialSubCommand sequentialSubCommand = SeqCmdUtils.getInstance().getCurrentSeqCmdOfResource(resourceID);
        if (sequentialSubCommand != null && sequentialSubCommand.CommandID.equals(commandID)) {
            return sequentialSubCommand.AddedAt;
        }
        final Table mdCommandsToDevice = new Table("MdCommandsToDevice");
        final SelectQuery AddedTimeQuery = (SelectQuery)new SelectQueryImpl(mdCommandsToDevice);
        final Column resourceColumn = new Column("MdCommandsToDevice", "RESOURCE_ID");
        final Column commandIDColumn = new Column("MdCommandsToDevice", "COMMAND_ID");
        final Column commandDeviceIDColumn = new Column("MdCommandsToDevice", "COMMAND_DEVICE_ID");
        final Column addedTimeColumn = new Column("MdCommandsToDevice", "ADDED_AT");
        AddedTimeQuery.addSelectColumn(resourceColumn);
        AddedTimeQuery.addSelectColumn(commandIDColumn);
        AddedTimeQuery.addSelectColumn(commandDeviceIDColumn);
        AddedTimeQuery.addSelectColumn(addedTimeColumn);
        final Criteria resourceCriteria = new Criteria(resourceColumn, (Object)resourceID, 0);
        final Criteria commandIDCriteria = new Criteria(commandIDColumn, (Object)commandID, 0);
        AddedTimeQuery.setCriteria(resourceCriteria.and(commandIDCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(AddedTimeQuery);
        if (dataObject.isEmpty()) {
            SyMUtil.getInstance();
            return SyMUtil.getCurrentTimeInMillis();
        }
        final Row row = dataObject.getFirstRow("MdCommandsToDevice");
        final Long time = (Long)row.get("ADDED_AT");
        if (time.equals(-1L)) {
            SyMUtil.getInstance();
            return SyMUtil.getCurrentTimeInMillis();
        }
        return time;
    }
    
    protected List<CommandQueueItem> getQueueItemListforCommandList(final List commandList, final int commandRepositoryType) throws DataAccessException {
        final List<CommandQueueItem> queueItemCommandList = new ArrayList<CommandQueueItem>();
        final Iterator iterator = commandList.iterator();
        while (iterator.hasNext()) {
            final CommandQueueItem temp = new CommandQueueItem();
            final Long cmdID = iterator.next();
            temp.commandID = cmdID;
            temp.priority = this.getCommandFromCache(cmdID, commandRepositoryType).priority;
            queueItemCommandList.add(temp);
        }
        return queueItemCommandList;
    }
    
    protected CommandQueueItem getCommandQueueItem(final Long commandID, final Long resourceID, final int commandRepositoryType) throws DataAccessException {
        return this.getCommandQueueItem(commandID, resourceID, commandRepositoryType, null);
    }
    
    private CommandQueueItem getCommandQueueItem(final Long commandID, final Long resourceID, final int commandRepositoryType, final Long addedAt) throws DataAccessException {
        final CommandQueueItem queueItem = new CommandQueueItem();
        queueItem.commandID = commandID;
        queueItem.priority = this.getCommandFromCache(commandID, commandRepositoryType).priority;
        if (addedAt == null) {
            queueItem.addedTime = this.getCommandAddedTime(commandID, resourceID);
        }
        else {
            queueItem.addedTime = addedAt;
        }
        return queueItem;
    }
    
    private HashMap getCommandQueueItem(final ArrayList<Long> resourceList, final ArrayList<Long> commandList, final HashMap<Long, ArrayList<Long>> devicesToCommandsMap, final int commandRepositoryType) throws DataAccessException {
        final SelectQuery mdCommandsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdCommands"));
        final SelectQuery mdCommandsToDevicesQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdCommandsToDevice"));
        mdCommandsQuery.setCriteria(new Criteria(Column.getColumn("MdCommands", "COMMAND_ID"), (Object)commandList.toArray(), 8));
        mdCommandsToDevicesQuery.setCriteria(new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceList.toArray(), 8).and(new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandList.toArray(), 8)).and(new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0)));
        mdCommandsQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_ID"));
        mdCommandsQuery.addSelectColumn(Column.getColumn("MdCommands", "PRIORITY"));
        mdCommandsToDevicesQuery.addSelectColumn(Column.getColumn("MdCommandsToDevice", "COMMAND_DEVICE_ID"));
        mdCommandsToDevicesQuery.addSelectColumn(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"));
        mdCommandsToDevicesQuery.addSelectColumn(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"));
        mdCommandsToDevicesQuery.addSelectColumn(Column.getColumn("MdCommandsToDevice", "ADDED_AT"));
        final DataObject mdCommandsDO = MDMUtil.getPersistenceLite().get(mdCommandsQuery);
        final DataObject mdCommandsToDeviceDO = MDMUtil.getPersistenceLite().get(mdCommandsToDevicesQuery);
        final HashMap resourceMap = new HashMap();
        if (!mdCommandsDO.isEmpty()) {
            for (final Long resourceId : resourceList) {
                final Iterator iterator = mdCommandsToDeviceDO.getRows("MdCommandsToDevice", new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceId, 0));
                final ArrayList<Long> resourceCommandList = devicesToCommandsMap.get(resourceId);
                final HashMap commandMap = new HashMap();
                for (final Long commandId : resourceCommandList) {
                    final CommandQueueItem commandQueueItem = new CommandQueueItem();
                    commandQueueItem.commandID = commandId;
                    final Row mdCommandsRow = mdCommandsDO.getRow("MdCommands", new Criteria(Column.getColumn("MdCommands", "COMMAND_ID"), (Object)commandId, 0));
                    final int priority = (int)mdCommandsRow.get("PRIORITY");
                    commandQueueItem.priority = priority;
                    final Row mdCommandsToDeviceRow = mdCommandsToDeviceDO.getRow("MdCommandsToDevice", new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandId, 0).and(new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceId, 0)));
                    if (mdCommandsToDeviceRow != null) {
                        final Long addedTime = (Long)mdCommandsToDeviceRow.get("ADDED_AT");
                        commandQueueItem.addedTime = addedTime;
                    }
                    else {
                        commandQueueItem.addedTime = MDMUtil.getCurrentTimeInMillis();
                    }
                    commandMap.put(commandId, commandQueueItem);
                }
                resourceMap.put(resourceId, commandMap);
            }
        }
        return resourceMap;
    }
    
    private List<CommandQueueItem> getCommandQueueItem(final HashMap<Long, Long> commandsToAddedTime, final Long resourceID, final int commandRepositoryType) throws DataAccessException {
        final List<CommandQueueItem> commandQueueItems = new ArrayList<CommandQueueItem>();
        final SequentialSubCommand sequentialSubCommand = SeqCmdUtils.getInstance().getCurrentSeqCmdOfResource(resourceID);
        for (final Long commandID : commandsToAddedTime.keySet()) {
            final CommandQueueItem queueItem = new CommandQueueItem();
            queueItem.commandID = commandID;
            queueItem.priority = this.getCommandFromCache(commandID, commandRepositoryType).priority;
            queueItem.addedTime = commandsToAddedTime.get(commandID);
            if (sequentialSubCommand != null && sequentialSubCommand.CommandID.compareTo(commandID) == 0) {
                queueItem.addedTime = sequentialSubCommand.AddedAt;
            }
            commandQueueItems.add(queueItem);
        }
        return commandQueueItems;
    }
    
    protected PriorityQueue<CommandQueueItem> getPriorityQueueForArrayList(final HashMap cacheHash, final String resourceUDID) {
        final PriorityQueue<CommandQueueItem> queue = new PriorityQueue<CommandQueueItem>();
        final ArrayList commandList = cacheHash.get(resourceUDID);
        if (commandList == null) {
            return null;
        }
        final Iterator iterator = commandList.iterator();
        while (iterator.hasNext()) {
            final CommandQueueItem temp = new CommandQueueItem();
            temp.commandID = iterator.next();
            final CommandQueueItem commandQueueItem = temp;
            SyMUtil.getInstance();
            commandQueueItem.addedTime = SyMUtil.getCurrentTimeInMillis();
            temp.priority = 100;
            queue.add(temp);
        }
        cacheHash.put(resourceUDID, queue);
        return queue;
    }
    
    public void assignSyncSeqSubCmdToDevices(final Long commandID, final Long resourceID, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "assignSyncSeqSubCmdToDevices(): commandID:{0} resourceID {1} ", new Object[] { commandID, resourceID });
        try {
            DataObject deviceCommandObject = MDMUtil.getPersistence().constructDataObject();
            final Criteria commandIDCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
            final Criteria commandRepCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
            Criteria resourceCriteria = null;
            final Column resourceColumn = Column.getColumn("MdCommandsToDevice", "RESOURCE_ID");
            final long stime = System.currentTimeMillis();
            final long stime2;
            synchronized (DeviceCommandRepository.SYNCHRONIZE_ADD_COMMAND) {
                stime2 = System.currentTimeMillis();
                resourceCriteria = new Criteria(resourceColumn, (Object)resourceID, 0);
                final Criteria criteria = resourceCriteria.and(commandIDCriteria).and(commandRepCriteria);
                deviceCommandObject = MDMUtil.getPersistence().get("MdCommandsToDevice", criteria);
                Row deviceCommandRow = deviceCommandObject.getRow("MdCommandsToDevice", criteria);
                if (deviceCommandRow == null) {
                    deviceCommandRow = new Row("MdCommandsToDevice");
                    deviceCommandRow.set("RESOURCE_ID", (Object)resourceID);
                    deviceCommandRow.set("COMMAND_ID", (Object)commandID);
                    deviceCommandRow.set("COMMAND_REPOSITORY_TYPE", (Object)commandRepositoryType);
                    deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                    final Row row = deviceCommandRow;
                    final String s = "ADDED_AT";
                    SyMUtil.getInstance();
                    row.set(s, (Object)SyMUtil.getCurrentTimeInMillis());
                    final Row row2 = deviceCommandRow;
                    final String s2 = "UPDATED_AT";
                    SyMUtil.getInstance();
                    row2.set(s2, (Object)SyMUtil.getCurrentTimeInMillis());
                    deviceCommandObject.addRow(deviceCommandRow);
                    MDMUtil.getPersistence().update(deviceCommandObject);
                }
                else {
                    deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                    deviceCommandObject.updateRow(deviceCommandRow);
                    MDMUtil.getPersistence().update(deviceCommandObject);
                }
                final Criteria resourceUDIDCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
                final DataObject dataObject = MDMUtil.getPersistence().get("ManagedDevice", resourceUDIDCriteria);
                if (dataObject.containsTable("ManagedDevice")) {
                    final String deviceUDID = (String)dataObject.getFirstValue("ManagedDevice", "UDID");
                    final CommandQueueItem commandQueueItem = this.getCommandQueueItem(commandID, resourceID, commandRepositoryType);
                    this.addSeqCmdToDeviceCache(deviceUDID, commandQueueItem, commandRepositoryType);
                }
            }
            this.logger.log(Level.INFO, "assignSyncSeqSubCmdToDevices(): Wait Time For Cache - {0} Process Time - {1}", new Object[] { stime2 - stime, System.currentTimeMillis() - stime2 });
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in assignSyncSeqSubCmdToDevices", exp);
        }
    }
    
    protected void addSeqCmdToDeviceCache(final String resourceUDID, final CommandQueueItem commandID, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addSeqCmdToDeviceCache(): resourceUDID:{0}", resourceUDID);
        this.logger.log(Level.INFO, "addSeqCmdToDeviceCache(): commandID:{0}", commandID);
        final long stime = System.currentTimeMillis();
        final long stime2;
        synchronized (DeviceCommandRepository.SYNCHRONIZE_COMMAND_CACHE) {
            stime2 = System.currentTimeMillis();
            try {
                final String cacheName = "MDM_DEVICE_COMMANDS";
                HashMap cacheParentHash = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName, 2);
                if (cacheParentHash == null) {
                    cacheParentHash = new HashMap();
                }
                if (cacheParentHash != null) {
                    HashMap cacheHash = cacheParentHash.get(commandRepositoryType);
                    if (cacheHash == null) {
                        cacheHash = new HashMap();
                    }
                    PriorityQueue<CommandQueueItem> cacheCommandList = (cacheHash.get(resourceUDID) instanceof ArrayList) ? this.getPriorityQueueForArrayList(cacheHash, resourceUDID) : cacheHash.get(resourceUDID);
                    if (cacheCommandList == null) {
                        cacheCommandList = new PriorityQueue<CommandQueueItem>();
                    }
                    cacheCommandList.remove(commandID);
                    cacheCommandList.add(commandID);
                    cacheHash.put(resourceUDID, cacheCommandList);
                    cacheParentHash.put(commandRepositoryType, cacheHash);
                    ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, (Object)cacheParentHash, 2);
                }
            }
            catch (final Exception exp) {
                this.logger.log(Level.SEVERE, "Exception in addSeqCmdToDeviceCache", exp);
            }
        }
        this.logger.log(Level.INFO, "addSeqCmdToDeviceCache(): Wait Time For Cache - {0} Process Time - {1}", new Object[] { stime2 - stime, System.currentTimeMillis() - stime2 });
    }
    
    public List getCollectionIdsCommandListFromCommandUUID(final List collectionList, final String commandUUID) {
        this.logger.log(Level.INFO, "getCollectionIdsCommandList(): collectionList: {0}", collectionList);
        this.logger.log(Level.INFO, "getCollectionIdsCommandList(): commandName: {0}", commandUUID);
        final List collectionCommandIdList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCollectionCommand"));
            final Join commandJoin = new Join("MdCollectionCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2);
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)commandUUID, 0, false);
            selectQuery.addJoin(commandJoin);
            DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
            final Criteria criteria = new Criteria(Column.getColumn("MdCollectionCommand", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
            selectQuery.setCriteria(criteria.and(commandCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdCollectionCommand", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MdCommands", "*"));
            dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator rowIterator = dataObject.getRows("MdCollectionCommand");
                while (rowIterator.hasNext()) {
                    final Row collectionCommandRow = rowIterator.next();
                    final Long collectionID = (Long)collectionCommandRow.get("COLLECTION_ID");
                    final Long commandID = (Long)collectionCommandRow.get("COMMAND_ID");
                    collectionCommandIdList.add(commandID);
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getCollectionIdsCommandList", exp);
        }
        return collectionCommandIdList;
    }
    
    public HashMap getCommandIdsForCollection(final Long collectionID) throws DataAccessException {
        final HashMap hashMap = new HashMap();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCollectionCommand"));
        selectQuery.addJoin(new Join("MdCollectionCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_UUID"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdCollectionCommand", "COLLECTION_ID"), (Object)collectionID, 0));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MdCommands");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String UUID = (String)row.get("COMMAND_UUID");
            final Long commandID = (Long)row.get("COMMAND_ID");
            if (UUID.contains("InstallApplication")) {
                hashMap.put("InstallApplication", commandID);
            }
            else if (UUID.contains("RemoveApplication")) {
                hashMap.put("RemoveApplication", commandID);
            }
            else if (UUID.contains("UpdateApplication")) {
                hashMap.put("UpdateApplication", commandID);
            }
            else if (UUID.contains("ApplicationConfiguration")) {
                hashMap.put("ApplicationConfiguration", commandID);
            }
            else {
                hashMap.put(UUID, commandID);
            }
        }
        return hashMap;
    }
    
    public void addRestrictionCommandToDevice(final DeviceDetails deviceDetails) {
        final Long resourceId = deviceDetails.resourceId;
        try {
            final List<String> commandList = new ArrayList<String>();
            Long commandID = null;
            final int platformType = deviceDetails.platform;
            switch (platformType) {
                case 1: {
                    commandList.add("Restrictions");
                    break;
                }
                case 3: {
                    commandList.add("DeviceInformation");
                    break;
                }
                case 2: {
                    final long odlocationAgentVersion = 77L;
                    if (deviceDetails.agentVersionCode >= odlocationAgentVersion) {
                        commandList.add("AssetScan");
                        break;
                    }
                    commandList.add("AndroidInvScan");
                    break;
                }
            }
            for (int i = 0; i < commandList.size(); ++i) {
                final DeviceCommand command = new DeviceCommand();
                command.commandUUID = commandList.get(i);
                command.commandType = commandList.get(i);
                command.commandStr = "--";
                commandID = this.addCommand(command);
                final List resourceList = new ArrayList();
                resourceList.add(resourceId);
                this.assignCommandToDevices(commandID, resourceList);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception while adding the restriction command to resource:" + n.toString());
        }
    }
    
    public void addCertificateCommandToDevice(final DeviceDetails deviceDetails) {
        final Long resourceId = deviceDetails.resourceId;
        try {
            final List<String> commandList = new ArrayList<String>();
            Long commandID = null;
            final int platformType = deviceDetails.platform;
            switch (platformType) {
                case 1: {
                    commandList.add("CertificateList");
                    break;
                }
                case 3: {
                    commandList.add("DeviceInformation");
                    break;
                }
                case 2: {
                    final long odlocationAgentVersion = 77L;
                    if (deviceDetails.agentVersionCode >= odlocationAgentVersion) {
                        commandList.add("AssetScan");
                        break;
                    }
                    commandList.add("AndroidInvScan");
                    break;
                }
            }
            for (int i = 0; i < commandList.size(); ++i) {
                final DeviceCommand command = new DeviceCommand();
                command.commandUUID = commandList.get(i);
                command.commandType = commandList.get(i);
                command.commandStr = "--";
                commandID = this.addCommand(command);
                final List resourceList = new ArrayList();
                resourceList.add(resourceId);
                this.assignCommandToDevices(commandID, resourceList);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception while adding certificate command to resource:" + n.toString());
        }
    }
    
    public Long addCommand(final String commandUUID, final String commandType) {
        Long commandID = null;
        this.logger.log(Level.INFO, "addCommand(). CommandUUID:{0} & CommandTye:{1}", new Object[] { commandUUID, commandType });
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandUUID;
        command.commandType = commandType;
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addCommand", exp);
        }
        return commandID;
    }
    
    public Long addComplianceActionCommand(final String commandType, final String commandUUID, final String encodedCommandData) {
        Long commandId = -1L;
        this.logger.log(Level.INFO, "addComplianceActionCommand  CommandType:{0},  CommandUUID:{1} ", new Object[] { commandType, commandUUID });
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = commandUUID;
        command.commandType = commandType;
        if (encodedCommandData != null) {
            command.commandStr = encodedCommandData;
        }
        try {
            commandId = this.addCommand(command);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in addCommand", e);
        }
        return commandId;
    }
    
    public void addSecurityInfoCommand(final List resourceList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addSecurityInfoCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addSecurityInfoCommand(): commandName: {0}", "SecurityInfo");
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "SecurityInfo";
        command.commandType = "SecurityInfo";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList, commandRepositoryType);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addSecurityInfoCommand  {0}", exp);
        }
    }
    
    public String getI18NCommandName(final String commandName) throws Exception {
        try {
            String name = "";
            switch (commandName) {
                case "EnableLostMode": {
                    name = I18N.getMsg("dc.mdm.geoLoc.find_my_phone.when_lost.enable_lost_mode", new Object[0]);
                    break;
                }
                case "DisableLostMode": {
                    name = I18N.getMsg("dc.mdm.geoLoc.find_my_phone.when_lost.stop_lost_mode", new Object[0]);
                    break;
                }
                case "EmailAlertCommand": {
                    name = I18N.getMsg("mdm.compliance.email_alert_command", new Object[0]);
                    break;
                }
                case "EraseDevice": {
                    name = I18N.getMsg("dc.mdm.inv.remote_wipe", new Object[0]);
                    break;
                }
                case "CorporateWipe": {
                    name = I18N.getMsg("dc.mdm.inv.corporate_wipe", new Object[0]);
                    break;
                }
                case "DeviceRing":
                case "PlayLostModeSound": {
                    name = I18N.getMsg("dc.mdm.inv.ring_device", new Object[0]);
                    break;
                }
                case "MarkAsNonCompliant": {
                    name = I18N.getMsg("mdm.compliance.mark_as_non_compliant", new Object[0]);
                    break;
                }
                case "DeviceInformation":
                case "AssetScan":
                case "AssetScanContainer":
                case "AndroidInvScan":
                case "AndroidInvScanContainer": {
                    name = I18N.getMsg("dc.common.SCAN_NOW", new Object[0]);
                    break;
                }
                case "LostModeDeviceLocation":
                case "GetLocation": {
                    name = I18N.getMsg("dc.mdm.inv.get_Location", new Object[0]);
                    break;
                }
                case "CreateContainer": {
                    name = I18N.getMsg("dc.mdm.knox.container.create", new Object[0]);
                    break;
                }
                case "RemoveContainer": {
                    name = I18N.getMsg("dc.mdm.android.knox.deactivate_knox", new Object[0]);
                    break;
                }
                case "ContainerLock": {
                    name = I18N.getMsg("dc.mdm.knox.container.lock", new Object[0]);
                    break;
                }
                case "ContainerUnlock": {
                    name = I18N.getMsg("dc.mdm.knox.container.unlock", new Object[0]);
                    break;
                }
                case "ClearContainerPasscode":
                case "ClearPasscode": {
                    name = I18N.getMsg("dc.mdm.inv.clear_passcode", new Object[0]);
                    break;
                }
                case "RemoteSession": {
                    name = I18N.getMsg("dc.mdm.inv.remote_troubleshoot", new Object[0]);
                    break;
                }
                case "RemoteDebug": {
                    name = I18N.getMsg("dc.mdm.inv.remote_debug", new Object[0]);
                    break;
                }
                case "DeviceLock": {
                    name = I18N.getMsg("dc.mdm.inv.remote_lock", new Object[0]);
                    break;
                }
                case "ResetPasscode": {
                    name = I18N.getMsg("dc.mdm.inv.reset_passcode", new Object[0]);
                    break;
                }
                case "ShutDownDevice": {
                    name = I18N.getMsg("mdm.common.remote_shutdown", new Object[0]);
                    break;
                }
                case "RestartDevice": {
                    name = I18N.getMsg("mdm.common.remote_restart", new Object[0]);
                    break;
                }
                case "PauseKioskCommand": {
                    name = I18N.getMsg("mdm.inv.pause_kiosk", new Object[0]);
                    break;
                }
                case "ResumeKioskCommand": {
                    name = I18N.getMsg("mdm.inv.resume_kiosk", new Object[0]);
                    break;
                }
                case "UnlockUserAccount": {
                    name = I18N.getMsg("dc.mdm.inv.unlock_user_account", new Object[0]);
                    break;
                }
                case "ClearAppData": {
                    name = I18N.getMsg("dc.mdm.inv.clear_app_data", new Object[0]);
                    break;
                }
                default: {
                    name = commandName;
                    break;
                }
            }
            return name;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- getI18NCommandName()    >   Error   ", e);
            throw e;
        }
    }
    
    public String getCommandRemarks(final JSONObject requestJSON) throws JSONException {
        final String commandType = String.valueOf(requestJSON.get("COMMAND_TYPE"));
        final int status = requestJSON.getInt("COMMAND_STATUS");
        final String deviceName = String.valueOf(requestJSON.get("NAME"));
        final String auditMessage = requestJSON.optString("AUDIT_MESSAGE", (String)null);
        final String dbRemarks = requestJSON.optString("REMARKS", (String)null);
        final String udid = requestJSON.optString("UDID", (String)null);
        final Long resourceID = requestJSON.getLong("RESOURCE_ID");
        String remarks = null;
        final int platform = requestJSON.optInt("PLATFORM_TYPE", -1);
        final String errorCode = requestJSON.optString("ERROR_CODE", (String)null);
        try {
            final String commandName = this.getI18NCommandName(commandType);
            switch (status) {
                case 2: {
                    final String s = commandType;
                    switch (s) {
                        case "DeviceLock": {
                            if (platform == 2) {
                                remarks = I18N.getMsg("dc.mdm.actionlog.securitycommands.success", new Object[] { commandName, deviceName });
                                break;
                            }
                            remarks = I18N.getMsg("dc.mdm.actionlog.securitycommands.success_with_msg", new Object[] { commandName, deviceName });
                            break;
                        }
                        case "DisableLostMode": {
                            remarks = I18N.getMsg("dc.mdm.geoLoc.eventlog.disable_lost_mode", new Object[] { deviceName, auditMessage });
                            break;
                        }
                        case "EnableLostMode": {
                            remarks = I18N.getMsg("dc.mdm.geoLoc.eventlog.enable_lost_mode", new Object[] { deviceName, auditMessage });
                            break;
                        }
                        case "RemoteDebug": {
                            remarks = I18N.getMsg("dc.mdm.inv.success", new Object[0]);
                            break;
                        }
                        case "ActivateKnox":
                        case "DeactivateKnox":
                        case "CreateContainer":
                        case "RemoveContainer":
                        case "ContainerLock":
                        case "ContainerUnlock":
                        case "ClearContainerPasscode":
                        case "EraseDevice":
                        case "CorporateWipe":
                        case "ClearPasscode":
                        case "LostModeDeviceLocation":
                        case "GetLocation":
                        case "ResetPasscode":
                        case "DeviceRing":
                        case "RemoteAlarm":
                        case "PlayLostModeSound":
                        case "ShutDownDevice":
                        case "RestartDevice":
                        case "FetchLocation":
                        case "AssetScan":
                        case "RemoteSession":
                        case "DeviceInformation":
                        case "AndroidInvScan":
                        case "AssetScanContainer":
                        case "AndroidInvScanContainer":
                        case "ResumeKioskCommand":
                        case "PauseKioskCommand":
                        case "ClearAppData": {
                            remarks = I18N.getMsg("dc.mdm.actionlog.securitycommands.success", new Object[] { commandName, deviceName });
                            break;
                        }
                        case "UnlockUserAccount": {
                            remarks = I18N.getMsg("dc.mdm.actionlog.macunlockuser.success", new Object[] { new MacDeviceUserUnlockHandler().getUserNameForResourceID(resourceID), deviceName });
                            break;
                        }
                        case "LogOutUser": {
                            String loggedInUser = AppleMultiUserUtils.getLoggedInUserName(resourceID);
                            if (MDMStringUtils.isEmpty(loggedInUser)) {
                                loggedInUser = "users";
                            }
                            remarks = I18N.getMsg("dc.mdm.actionlog.shareddevicelogout.success", new Object[] { loggedInUser, deviceName });
                            break;
                        }
                        case "MacFileVaultPersonalKeyRotate": {
                            final String serialNumber = MDMFilevaultPersonalRecoveryKeyImport.getSerialNumberForResourceID(resourceID);
                            remarks = I18N.getMsg("mdm.profile.filevault_rotate_success", new Object[] { deviceName, serialNumber });
                            break;
                        }
                    }
                    break;
                }
                case 1: {
                    final String s2 = commandType;
                    switch (s2) {
                        case "ActivateKnox": {
                            remarks = I18N.getMsg("dc.mdm.android.knox.ui.activate_knox", new Object[] { deviceName });
                            break;
                        }
                        case "DeactivateKnox": {
                            remarks = I18N.getMsg("dc.mdm.android.knox.ui.deactivate_knox", new Object[] { deviceName });
                            break;
                        }
                        case "ContainerLock": {
                            remarks = I18N.getMsg("dc.mdm.android.knox.ui.lock_container", new Object[] { deviceName });
                            break;
                        }
                        case "ClearContainerPasscode": {
                            remarks = I18N.getMsg("dc.mdm.android.knox.ui.passcode_clear", new Object[] { deviceName });
                            break;
                        }
                        case "ContainerUnlock": {
                            remarks = I18N.getMsg("dc.mdm.android.knox.ui.unlock_container", new Object[] { deviceName });
                            break;
                        }
                        case "RemoteDebug": {
                            remarks = I18N.getMsg("dc.mdm.inv.remote_debug_initiated", new Object[0]);
                            break;
                        }
                        case "CreateContainer":
                        case "RemoveContainer":
                        case "DeviceLock":
                        case "DisableLostMode":
                        case "EnableLostMode":
                        case "EraseDevice":
                        case "CorporateWipe":
                        case "ClearPasscode":
                        case "GetLocation":
                        case "LostModeDeviceLocation":
                        case "ResetPasscode":
                        case "DeviceRing":
                        case "RemoteAlarm":
                        case "PlayLostModeSound":
                        case "ShutDownDevice":
                        case "RestartDevice":
                        case "FetchLocation":
                        case "AssetScan":
                        case "ResumeKioskCommand":
                        case "RemoteSession":
                        case "DeviceInformation":
                        case "AndroidInvScan":
                        case "AssetScanContainer":
                        case "AndroidInvScanContainer":
                        case "PauseKioskCommand":
                        case "ClearAppData": {
                            remarks = I18N.getMsg("dc.mdm.ui.securitycommands.initiate", new Object[] { commandName, deviceName });
                            break;
                        }
                        case "UnlockUserAccount": {
                            remarks = I18N.getMsg("dc.mdm.actionlog.macunlockuser.initiate", new Object[] { deviceName, new MacDeviceUserUnlockHandler().getUserNameForResourceID(resourceID) });
                            break;
                        }
                        case "MacFileVaultPersonalKeyRotate": {
                            final String serialNumber2 = MDMFilevaultPersonalRecoveryKeyImport.getSerialNumberForResourceID(resourceID);
                            remarks = I18N.getMsg("mdm.profile.filevault_rotate_initiated", new Object[] { deviceName, serialNumber2 });
                            break;
                        }
                        case "LogOutUser": {
                            String loggedInUser2 = AppleMultiUserUtils.getLoggedInUserName(resourceID);
                            if (MDMStringUtils.isEmpty(loggedInUser2)) {
                                loggedInUser2 = "users";
                            }
                            remarks = I18N.getMsg("dc.mdm.actionlog.shareddevicelogout.initiate", new Object[] { loggedInUser2, deviceName });
                            break;
                        }
                    }
                    break;
                }
                case 0: {
                    if ("EnableLostMode".equals(commandType)) {
                        if (!MDMUtil.isStringEmpty(dbRemarks)) {
                            remarks = I18N.getMsg(dbRemarks, new Object[] { deviceName, DMUserHandler.getUserNameFromUserID(MDMUtil.getInstance().getCurrentlyLoggedOnUserID()) });
                            break;
                        }
                        remarks = I18N.getMsg("dc.mdm.actionlog.securitycommands.failure", new Object[] { commandName, deviceName });
                        break;
                    }
                    else if ("RemoteDebug".equals(commandType)) {
                        if (!MDMUtil.isStringEmpty(dbRemarks)) {
                            remarks = I18N.getMsg(dbRemarks, new Object[0]);
                            break;
                        }
                        remarks = I18N.getMsg("dc.mdm.actionlog.securitycommands.failure", new Object[] { commandName, deviceName });
                        break;
                    }
                    else {
                        if ("DisableLostMode".equals(commandType) && platform == 1 && String.valueOf(12143).equals(errorCode)) {
                            remarks = requestJSON.getString("REMARKS");
                            break;
                        }
                        if ("UnlockUserAccount".equals(commandType)) {
                            remarks = I18N.getMsg("dc.mdm.actionlog.macunlockuser.failure", new Object[] { new MacDeviceUserUnlockHandler().getUserNameForResourceID(resourceID), deviceName });
                            break;
                        }
                        if ("LogOutUser".equalsIgnoreCase(commandType)) {
                            String loggedInUser3 = AppleMultiUserUtils.getLoggedInUserName(resourceID);
                            if (MDMStringUtils.isEmpty(loggedInUser3)) {
                                loggedInUser3 = "users";
                            }
                            remarks = I18N.getMsg("dc.mdm.actionlog.shareddevicelogout.failure", new Object[] { loggedInUser3, deviceName });
                            break;
                        }
                        if (!"RestartDevice".equals(commandType) || platform != 4) {
                            remarks = I18N.getMsg("dc.mdm.actionlog.securitycommands.failure", new Object[] { commandName, deviceName });
                            break;
                        }
                        if (errorCode.equals(String.valueOf(70000))) {
                            remarks = I18N.getMsg("dc.mdm.actionlog.securitycommands.not_in_kiosk", new Object[] { commandName });
                            break;
                        }
                        remarks = I18N.getMsg("dc.mdm.actionlog.securitycommands.failure", new Object[] { commandName, deviceName });
                        break;
                    }
                    break;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- getCommandRemarks()    >   Error   ", e);
        }
        return remarks;
    }
    
    public void addCapabilitiesInfoCommand(final Long resourceId) {
        this.addCapabilitiesInfoCommand(Arrays.asList(resourceId));
    }
    
    public void addCapabilitiesInfoCommand(final List resourceList) {
        this.logger.log(Level.INFO, "addCapabilitiesInfoCommand(): resourceID: {0} for CapabilitiesInfo", resourceList);
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "CapabilitiesInfo";
        command.commandType = "CapabilitiesInfo";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addCapabilitiesInfoCommand  {0}", exp);
        }
    }
    
    public List<String> getAppleAppInstalledScanCommand(final Long resourceId) throws Exception {
        final List<String> commandList = new ArrayList<String>();
        final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
        commandList.add("InstalledApplicationList");
        commandList.add("ManagedApplicationList");
        switch (deviceDetails.modelType) {
            case 3:
            case 4: {
                if (!ManagedDeviceHandler.getInstance().isEqualOrAboveOSVersion(resourceId, "11.0")) {
                    commandList.remove("ManagedApplicationList");
                    break;
                }
                break;
            }
        }
        return commandList;
    }
    
    public List<Long> getCommandIdsFromCommandUUIDs(final List<String> commandUUIDs) {
        final List<Long> commandIdList = new ArrayList<Long>();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommands"));
            final Criteria criteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)commandUUIDs.toArray(), 8, false);
            sQuery.setCriteria(criteria);
            sQuery.addSelectColumn(new Column("MdCommands", "COMMAND_ID"));
            sQuery.addSelectColumn(new Column("MdCommands", "COMMAND_UUID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (!dataObject.isEmpty()) {
                for (final String commandUUID : commandUUIDs) {
                    final Criteria commandUUIDCriteria = new Criteria(new Column("MdCommands", "COMMAND_UUID"), (Object)commandUUID, 0);
                    final Row commandRow = dataObject.getRow("MdCommands", commandUUIDCriteria);
                    if (commandRow != null) {
                        final Long commandId = (Long)commandRow.get("COMMAND_ID");
                        commandIdList.add(commandId);
                    }
                }
            }
            this.logger.log(Level.INFO, "Getting command Id from uuid:{0} & commandId:{1}", new Object[] { commandUUIDs, commandIdList });
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in getting commandList", (Throwable)e);
        }
        return commandIdList;
    }
    
    public void addAndroidSyncAppCatalogCmd(final List resourceList) {
        AppsUtil.getInstance().addOrUpdateAppCatalogSync(resourceList);
        final Long commandID = this.addCommand("SyncAppCatalog");
        this.assignCommandToDevices(commandID, resourceList);
    }
    
    public void assignCommandToUserChannel(final Long commandID, final String deviceUDID) {
        this.assignCommandToDevice(commandID, deviceUDID, 4);
    }
    
    public void assignCommandToUserChannel(final List commandList, final List resourceList) {
        this.assignCommandToDevices(commandList, resourceList, 4);
    }
    
    public void assignDeviceCommandToOnUserChannel(final Long commandID, final String deviceUDID) {
        this.assignCommandToDevice(commandID, deviceUDID, 5);
    }
    
    public void assignDeviceCommandToOnUserChannel(final List commandList, final List resourceList) {
        this.assignCommandToDevices(commandList, resourceList, 5);
    }
    
    public List<Long> getDeviceCommandsOnUserChannel(final Long resourceID) {
        List<Long> commandIDList = new ArrayList<Long>();
        DataObject dob = null;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdCommandsToDevice"));
        query.addSelectColumn(new Column("MdCommandsToDevice", "*"));
        final Criteria resIDCri = new Criteria(new Column("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria cmdRepType = new Criteria(new Column("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)5, 0);
        final Criteria statusCri = new Criteria(new Column("MdCommandsToDevice", "RESOURCE_COMMAND_STATUS"), (Object)12, 0);
        query.setCriteria(resIDCri.and(cmdRepType).and(statusCri));
        try {
            dob = MDMUtil.getPersistence().get(query);
            if (dob != null && !dob.isEmpty()) {
                commandIDList = DBUtil.getColumnValuesAsList(dob.getRows("MdCommandsToDevice"), "COMMAND_ID");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceCommandsOnUserChannel  {0}", ex);
        }
        return commandIDList;
    }
    
    public void clearDeviceCommandsOnUserChannel(final Long resourceID, final List commandIDs) {
        final List<Long> resourceIDList = new ArrayList<Long>();
        resourceIDList.add(resourceID);
        this.deleteResourceCommand(resourceIDList, commandIDs);
        this.refreshResourceCommandsToCache(resourceIDList, 5);
    }
    
    public void addSyncDownloadSettingsCommand(final List resourceList, final int commandRepositoryType) {
        this.logger.log(Level.INFO, "addSyncDownloadAgentSettingsCommand(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addSyncDownloadAgentSettingsCommand(): commandName: {0}", "SyncDownloadSettings");
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "SyncDownloadSettings";
        command.commandType = "SyncDownloadSettings";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList, commandRepositoryType);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in addSyncDownloadAgentSettingsCommand", ex);
        }
    }
    
    public void assignCommandToDevicesInChunck(final List<Long> resourceIDs, final List<Long> commandIDs, final int commandRepositoryType) {
        final long stime = System.currentTimeMillis();
        final List<List> resourceSubList = MDMUtil.getInstance().splitListIntoSubLists(resourceIDs, this.resSize);
        final List<List> commandListSubList = MDMUtil.getInstance().splitListIntoSubLists(commandIDs, this.cmndSize);
        final Iterator iterateCommandList = commandListSubList.iterator();
        while (iterateCommandList.hasNext()) {
            final ArrayList<Long> commandList = new ArrayList<Long>(iterateCommandList.next());
            for (final List<Long> resourceList : resourceSubList) {
                final HashMap<Long, ArrayList<Long>> devicesToCommandsMap = new HashMap<Long, ArrayList<Long>>();
                for (final Long resourceId : resourceList) {
                    devicesToCommandsMap.put(resourceId, commandList);
                }
                this.assignCommandToDevices(devicesToCommandsMap, commandRepositoryType);
            }
        }
        this.logger.log(Level.INFO, "Total time taken for process command in the chunck process is {0}", System.currentTimeMillis() - stime);
    }
    
    public void assignCommandToDevicesInChunck(final List<Long> resourceIDs, final Long commandID) {
        final List<Long> commandIDs = new ArrayList<Long>();
        commandIDs.add(commandID);
        if (resourceIDs != null) {
            this.assignCommandToDevicesInChunck(new ArrayList<Long>(resourceIDs), commandIDs, 1);
        }
    }
    
    public void assignCommandToDevices(final Long commandID, final List resourceList, final int commandRepositoryType) {
        final Boolean enableSyncCommandAdd = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableSyncCommandAdd");
        if (enableSyncCommandAdd != null && enableSyncCommandAdd) {
            this.assignSyncCommandToDevices(commandID, resourceList, commandRepositoryType);
        }
        else {
            this.logger.log(Level.INFO, "assignCommandToDevices(): commandID:{0} resourceList {1} ", new Object[] { commandID, resourceList });
            try {
                if (resourceList != null && resourceList.size() > 0) {
                    MDMUtil.addCommandToThreadLocal(commandID);
                }
                DataObject deviceCommandObject = MDMUtil.getPersistence().constructDataObject();
                final Criteria commandIDCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
                final Criteria commandRepCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
                Criteria resourceCriteria = null;
                final Column resourceColumn = Column.getColumn("MdCommandsToDevice", "RESOURCE_ID");
                for (int i = 0; i < resourceList.size(); ++i) {
                    final long stime = System.currentTimeMillis();
                    final long stime2 = System.currentTimeMillis();
                    resourceCriteria = new Criteria(resourceColumn, resourceList.get(i), 0);
                    final Criteria criteria = resourceCriteria.and(commandIDCriteria).and(commandRepCriteria);
                    deviceCommandObject = MDMUtil.getPersistence().get("MdCommandsToDevice", criteria);
                    if (this.canApplyThisCommand(resourceList.get(i), commandID)) {
                        Row deviceCommandRow = deviceCommandObject.getRow("MdCommandsToDevice", criteria);
                        if (deviceCommandRow == null) {
                            deviceCommandRow = new Row("MdCommandsToDevice");
                            deviceCommandRow.set("RESOURCE_ID", resourceList.get(i));
                            deviceCommandRow.set("COMMAND_ID", (Object)commandID);
                            deviceCommandRow.set("COMMAND_REPOSITORY_TYPE", (Object)commandRepositoryType);
                            deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                            final Row row = deviceCommandRow;
                            final String s = "ADDED_AT";
                            SyMUtil.getInstance();
                            row.set(s, (Object)SyMUtil.getCurrentTimeInMillis());
                            final Row row2 = deviceCommandRow;
                            final String s2 = "UPDATED_AT";
                            SyMUtil.getInstance();
                            row2.set(s2, (Object)SyMUtil.getCurrentTimeInMillis());
                            deviceCommandObject.addRow(deviceCommandRow);
                            MDMUtil.getPersistence().update(deviceCommandObject);
                        }
                        else {
                            deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                            deviceCommandObject.updateRow(deviceCommandRow);
                            MDMUtil.getPersistence().update(deviceCommandObject);
                        }
                        final Criteria resourceUDIDCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), resourceList.get(i), 0);
                        final DataObject dataObject = MDMUtil.getPersistence().get("ManagedDevice", resourceUDIDCriteria);
                        if (dataObject.containsTable("ManagedDevice")) {
                            final String deviceUDID = (String)dataObject.getFirstValue("ManagedDevice", "UDID");
                            final CommandQueueItem commandQueueItem = this.getCommandQueueItem(commandID, resourceList.get(i), commandRepositoryType, (Long)deviceCommandRow.get("ADDED_AT"));
                            this.addDeviceCommandsToCache(deviceUDID, commandQueueItem, commandRepositoryType);
                        }
                    }
                    else {
                        if (((String)DBUtil.getValueFromDB("MdCommands", "COMMAND_ID", (Object)commandID, "COMMAND_TYPE")).equals("RemoveProfile")) {
                            ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceList.get(i), (Long)DBUtil.getValueFromDB("MdCollectionCommand", "COMMAND_ID", (Object)commandID, "COLLECTION_ID"));
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceList.get(i), this.getCollectionId(commandID).toString(), 6, "dc.db.mdm.collection.Successfully_removed_the_policy");
                        }
                        else {
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceList.get(i), this.getCollectionId(commandID).toString(), 8, this.getNotApplicableRemarks(resourceList.get(i)));
                        }
                        MDMCollectionStatusUpdate.getInstance().updateCollnToResErrorCode(resourceList.get(i), this.getCollectionId(commandID), null);
                    }
                    this.logger.log(Level.INFO, "assignCommandToDevices(): Wait Time For Cache - {0} Process Time - {1}", new Object[] { stime2 - stime, System.currentTimeMillis() - stime2 });
                }
            }
            catch (final Exception exp) {
                this.logger.log(Level.SEVERE, "Exception in assignCommandToDevices", exp);
            }
        }
    }
    
    public void assignSeqSubCmdToDevices(final Long commandID, final Long resourceID, final int commandRepositoryType) {
        final Boolean enableSyncCommandAdd = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableSyncCommandAdd");
        if (enableSyncCommandAdd != null && enableSyncCommandAdd) {
            this.assignSyncSeqSubCmdToDevices(commandID, resourceID, commandRepositoryType);
        }
        else {
            this.logger.log(Level.INFO, "assignSeqSubCmdToDevices(): commandID:{0} resourceID {1} ", new Object[] { commandID, resourceID });
            try {
                DataObject deviceCommandObject = MDMUtil.getPersistence().constructDataObject();
                final Criteria commandIDCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), (Object)commandID, 0);
                final Criteria commandRepCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
                Criteria resourceCriteria = null;
                final Column resourceColumn = Column.getColumn("MdCommandsToDevice", "RESOURCE_ID");
                final long stime = System.currentTimeMillis();
                final long stime2 = System.currentTimeMillis();
                resourceCriteria = new Criteria(resourceColumn, (Object)resourceID, 0);
                final Criteria criteria = resourceCriteria.and(commandIDCriteria).and(commandRepCriteria);
                deviceCommandObject = MDMUtil.getPersistence().get("MdCommandsToDevice", criteria);
                Row deviceCommandRow = deviceCommandObject.getRow("MdCommandsToDevice", criteria);
                if (deviceCommandRow == null) {
                    deviceCommandRow = new Row("MdCommandsToDevice");
                    deviceCommandRow.set("RESOURCE_ID", (Object)resourceID);
                    deviceCommandRow.set("COMMAND_ID", (Object)commandID);
                    deviceCommandRow.set("COMMAND_REPOSITORY_TYPE", (Object)commandRepositoryType);
                    deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                    final Row row = deviceCommandRow;
                    final String s = "ADDED_AT";
                    SyMUtil.getInstance();
                    row.set(s, (Object)SyMUtil.getCurrentTimeInMillis());
                    final Row row2 = deviceCommandRow;
                    final String s2 = "UPDATED_AT";
                    SyMUtil.getInstance();
                    row2.set(s2, (Object)SyMUtil.getCurrentTimeInMillis());
                    deviceCommandObject.addRow(deviceCommandRow);
                    MDMUtil.getPersistence().update(deviceCommandObject);
                }
                else {
                    deviceCommandRow.set("RESOURCE_COMMAND_STATUS", (Object)12);
                    deviceCommandObject.updateRow(deviceCommandRow);
                    MDMUtil.getPersistence().update(deviceCommandObject);
                }
                final Criteria resourceUDIDCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
                final DataObject dataObject = MDMUtil.getPersistence().get("ManagedDevice", resourceUDIDCriteria);
                if (dataObject.containsTable("ManagedDevice")) {
                    final String deviceUDID = (String)dataObject.getFirstValue("ManagedDevice", "UDID");
                    final CommandQueueItem commandQueueItem = this.getCommandQueueItem(commandID, resourceID, commandRepositoryType);
                    this.addSeqCmdToDeviceCache(deviceUDID, commandQueueItem, commandRepositoryType);
                }
                this.logger.log(Level.INFO, "assignSeqSubCmdToDevices(): Wait Time For Cache - {0} Process Time - {1}", new Object[] { stime2 - stime, System.currentTimeMillis() - stime2 });
            }
            catch (final Exception exp) {
                this.logger.log(Level.SEVERE, "Exception in assignSeqSubCmdToDevices", exp);
            }
        }
    }
    
    public void addSharedIPadConfiguration(final List<Long> resourceList) {
        this.logger.log(Level.INFO, "addSharedIPadConfiguration(): resourceID: {0}", resourceList);
        this.logger.log(Level.INFO, "addSharedIPadConfiguration(): commandName: {0}", "SharedDeviceConfiguration");
        Long commandID = null;
        final DeviceCommand command = new DeviceCommand();
        command.commandUUID = "SharedDeviceConfiguration";
        command.commandType = "SharedDeviceConfiguration";
        command.commandStr = "--";
        try {
            commandID = this.addCommand(command);
            this.assignCommandToDevices(commandID, resourceList);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in addSharedIPadConfiguration  {0}", exp);
        }
    }
    
    private Criteria getSlotCriteria() {
        final Long currentMillis = System.currentTimeMillis();
        final Criteria slotCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "SLOT_BEGIN_TIME"), (Object)null, 0, false);
        final Criteria slotStartCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "SLOT_BEGIN_TIME"), (Object)currentMillis, 6, false);
        final Criteria slotEndCriteria = new Criteria(Column.getColumn("MdCommandsToDevice", "SLOT_END_TIME"), (Object)currentMillis, 4, false);
        return slotCriteria.or(slotStartCriteria.and(slotEndCriteria));
    }
    
    static {
        DeviceCommandRepository.mdmCommandUtil = null;
        SYNCHRONIZE_ADD_COMMAND = new Integer(1);
        SYNCHRONIZE_COMMAND_CACHE = new Integer(2);
    }
}
