package com.me.mdm.server.seqcommands;

import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdStatusUpdateHandler;
import com.me.mdm.server.seqcommands.windows.WindowsSeqCmdStatusUpdateHandler;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import java.util.HashMap;
import java.util.Map;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.ds.query.SortColumn;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.Join;
import java.util.Set;
import com.adventnet.persistence.Persistence;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SeqCmdUtils
{
    public static SeqCmdUtils sequentialCommandUtils;
    public Logger logger;
    
    public SeqCmdUtils() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
    
    public static SeqCmdUtils getInstance() {
        if (SeqCmdUtils.sequentialCommandUtils == null) {
            SeqCmdUtils.sequentialCommandUtils = new SeqCmdUtils();
        }
        return SeqCmdUtils.sequentialCommandUtils;
    }
    
    public List getSequentialIDforBaseID(final List<Long> commandList) {
        final List sequentialCommandIDList = new ArrayList();
        final Table commandmap = new Table("MdCommandToSequentialCommand");
        final SelectQuery commandSelect = (SelectQuery)new SelectQueryImpl(commandmap);
        final Column all = new Column("MdCommandToSequentialCommand", "*");
        commandSelect.addSelectColumn(all);
        final Criteria commandIdlistCriteria = new Criteria(new Column("MdCommandToSequentialCommand", "COMMAND_ID"), (Object)commandList.toArray(new Long[commandList.size()]), 8);
        commandSelect.setCriteria(commandIdlistCriteria);
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(commandSelect);
            final Iterator itr = dataObject.getRows("MdCommandToSequentialCommand");
            while (itr.hasNext()) {
                final Row row = itr.next();
                final Long SeqID = (Long)row.get(1);
                final Long cmdID = (Long)row.get(2);
                commandList.remove(cmdID);
                sequentialCommandIDList.add(SeqID);
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Data Access Exception in get SequentialIDforBaseID() : ", (Throwable)e);
        }
        return sequentialCommandIDList;
    }
    
    public Long getSequentialIDforBaseID(final Long commandID) {
        final List temp = new ArrayList();
        temp.add(commandID);
        final List sequentialCommandList = this.getSequentialIDforBaseID(temp);
        if (sequentialCommandList.size() != 0) {
            return sequentialCommandList.get(0);
        }
        return new Long(-1L);
    }
    
    public String getUUIDforcommandID(final Long commandID) {
        final Table table = new Table("MdCommands");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
        final Column commandUUIDColumn = new Column("MdCommands", "COMMAND_UUID");
        final Column commandIDColumn = new Column("MdCommands", "COMMAND_ID");
        selectQuery.addSelectColumn(commandIDColumn);
        selectQuery.addSelectColumn(commandUUIDColumn);
        final Criteria commandIDCriteria = new Criteria(commandIDColumn, (Object)commandID, 0);
        selectQuery.setCriteria(commandIDCriteria);
        try {
            final DataObject d = MDMUtil.getPersistence().get(selectQuery);
            if (!d.isEmpty()) {
                final Row r = d.getFirstRow("MdCommands");
                final String temp = r.get("COMMAND_UUID").toString();
                return temp;
            }
            return null;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in getUUIDforCommandID", e);
            return null;
        }
    }
    
    public List getResourcesRunningSeqCmd(final List<Long> resourceList) {
        final List sequentialExecutingResources = new ArrayList();
        final Table table = new Table("SequentialCmdExecutionStatus");
        final SelectQuery SequentialExecutingResourcesQuery = (SelectQuery)new SelectQueryImpl(table);
        final Column all = new Column("SequentialCmdExecutionStatus", "*");
        SequentialExecutingResourcesQuery.addSelectColumn(all);
        final Criteria resourceIDCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceList.toArray(new Long[resourceList.size()]), 8);
        final Criteria statusCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "COMMAND_STATUS"), (Object)5, 1);
        SequentialExecutingResourcesQuery.setCriteria(resourceIDCriteria.and(statusCriteria));
        try {
            final Persistence persistence = MDMUtil.getPersistence();
            final DataObject dataObject = persistence.get(SequentialExecutingResourcesQuery);
            final Iterator iterator = dataObject.getRows("SequentialCmdExecutionStatus");
            final Set<Long> temp = new HashSet<Long>();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                temp.add((Long)row.get("RESOURCE_ID"));
            }
            sequentialExecutingResources.addAll(temp);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Data Access Exception in getSequentialExecutingforresources(): ", (Throwable)e);
        }
        return sequentialExecutingResources;
    }
    
    public Criteria getSeqCmdInprogressCriteria() {
        final Criteria commandStatusCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "COMMAND_STATUS"), (Object)new int[] { 3, 120, 180, 16, 190 }, 8);
        return commandStatusCriteria;
    }
    
    public DataObject getSuspentionDataForResource(final Long resourceID) {
        final Table seqStatus = new Table("SequentialCmdExecutionStatus");
        final Column column = new Column("SequentialCmdExecutionStatus", "*");
        final SelectQuery currentSequentialCommandQuery = (SelectQuery)new SelectQueryImpl(seqStatus);
        final Column all = new Column("MdSequentialCommands", "*");
        final Column seqcmdColumns = new Column("MdCommandToSequentialCommand", "*");
        final Column resourceColumn = new Column("MdCommandsToDevice", "RESOURCE_ID");
        final Column commandIDColumn = new Column("MdCommandsToDevice", "COMMAND_ID");
        final Column commandDeviceIDColumn = new Column("MdCommandsToDevice", "COMMAND_DEVICE_ID");
        final Column updatedTimeColumn = new Column("MdCommandsToDevice", "UPDATED_AT");
        final Column statusColumn = new Column("MdCommandsToDevice", "RESOURCE_COMMAND_STATUS");
        currentSequentialCommandQuery.addSelectColumn(resourceColumn);
        currentSequentialCommandQuery.addSelectColumn(seqcmdColumns);
        currentSequentialCommandQuery.addSelectColumn(commandIDColumn);
        currentSequentialCommandQuery.addSelectColumn(commandDeviceIDColumn);
        currentSequentialCommandQuery.addSelectColumn(updatedTimeColumn);
        currentSequentialCommandQuery.addSelectColumn(statusColumn);
        currentSequentialCommandQuery.addSelectColumn(column);
        currentSequentialCommandQuery.addSelectColumn(all);
        currentSequentialCommandQuery.addJoin(new Join("SequentialCmdExecutionStatus", "MdSequentialCommands", new String[] { "SEQUENTIAL_COMMAND_ID", "EXECUTION_STATUS" }, new String[] { "SEQUENTIAL_COMMAND_ID", "ORDER" }, 2));
        final Criteria cmdCriteria = new Criteria(Column.getColumn("MdSequentialCommands", "COMMAND_ID"), (Object)Column.getColumn("MdCommandsToDevice", "COMMAND_ID"), 0);
        final Criteria resCriteria = new Criteria(Column.getColumn("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), 0);
        currentSequentialCommandQuery.addJoin(new Join("MdSequentialCommands", "MdCommandsToDevice", cmdCriteria.and(resCriteria), 1));
        currentSequentialCommandQuery.addJoin(new Join("MdSequentialCommands", "MdCommandToSequentialCommand", new String[] { "SEQUENTIAL_COMMAND_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID" }, 1));
        Criteria resourceIDCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria commandStatusCriteria = this.getSeqCmdInprogressCriteria();
        resourceIDCriteria = resourceIDCriteria.and(commandStatusCriteria);
        currentSequentialCommandQuery.setCriteria(resourceIDCriteria);
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(currentSequentialCommandQuery);
            return dataObject;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in getting sequential command status", e);
            return null;
        }
    }
    
    public SequentialSubCommand getCurrentSeqCmdOfResource(final Long resourceID) {
        final Table seqStatus = new Table("SequentialCmdExecutionStatus");
        final Column column = new Column("SequentialCmdExecutionStatus", "*");
        final SelectQuery currentSequentialCommandQuery = (SelectQuery)new SelectQueryImpl(seqStatus);
        final Column all = new Column("MdSequentialCommands", "*");
        currentSequentialCommandQuery.addSelectColumn(column);
        currentSequentialCommandQuery.addSelectColumn(all);
        currentSequentialCommandQuery.addJoin(new Join("SequentialCmdExecutionStatus", "MdSequentialCommands", new String[] { "SEQUENTIAL_COMMAND_ID", "EXECUTION_STATUS" }, new String[] { "SEQUENTIAL_COMMAND_ID", "ORDER" }, 2));
        Criteria resourceIDCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria commandStatusCriteria = this.getSeqCmdInprogressCriteria();
        resourceIDCriteria = resourceIDCriteria.and(commandStatusCriteria);
        currentSequentialCommandQuery.setCriteria(resourceIDCriteria);
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(currentSequentialCommandQuery);
            if (dataObject.isEmpty()) {
                return null;
            }
            final Row row = dataObject.getFirstRow("MdSequentialCommands");
            final Row statusRow = dataObject.getFirstRow("SequentialCmdExecutionStatus");
            final SequentialSubCommand sequentialSubCommand = new SequentialSubCommand();
            sequentialSubCommand.CommandID = (Long)row.get("COMMAND_ID");
            sequentialSubCommand.SequentialCommandID = (Long)row.get("SEQUENTIAL_COMMAND_ID");
            sequentialSubCommand.order = (int)row.get("ORDER");
            sequentialSubCommand.Handler = (String)row.get("HANDLER");
            sequentialSubCommand.AddedAt = (Long)statusRow.get("SEQUENTIAL_COMMAND_ADDED_AT");
            sequentialSubCommand.UpdatedAt = (Long)statusRow.get("UPDATED_AT");
            sequentialSubCommand.status = (int)statusRow.get("COMMAND_STATUS");
            sequentialSubCommand.resourceID = resourceID;
            return sequentialSubCommand;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in getting sequential command status", e);
            return null;
        }
    }
    
    public SequentialSubCommand getCurrentSeqCmdOfResourceWithMeta(final Long resourceID) {
        final Table seqStatus = new Table("SequentialCmdExecutionStatus");
        final Column column = new Column("SequentialCmdExecutionStatus", "*");
        final SelectQuery currentSequentialCommandQuery = (SelectQuery)new SelectQueryImpl(seqStatus);
        final Column all = new Column("MdSequentialCommands", "*");
        final Column paramsall = new Column("SequentialCommandParams", "*");
        currentSequentialCommandQuery.addSelectColumn(paramsall);
        currentSequentialCommandQuery.addSelectColumn(column);
        currentSequentialCommandQuery.addSelectColumn(all);
        currentSequentialCommandQuery.addSelectColumn(Column.getColumn("MdCommandToSequentialCommand", "*"));
        currentSequentialCommandQuery.addJoin(new Join("SequentialCmdExecutionStatus", "MdSequentialCommands", new String[] { "SEQUENTIAL_COMMAND_ID", "EXECUTION_STATUS" }, new String[] { "SEQUENTIAL_COMMAND_ID", "ORDER" }, 2));
        currentSequentialCommandQuery.addJoin(new Join("SequentialCmdExecutionStatus", "SequentialCommandParams", new String[] { "SEQUENTIAL_COMMAND_ID", "RESOURCE_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID", "RESOURCE_ID" }, 2));
        currentSequentialCommandQuery.addJoin(new Join("SequentialCmdExecutionStatus", "MdCommandToSequentialCommand", new String[] { "SEQUENTIAL_COMMAND_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID" }, 2));
        Criteria resourceIDCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria commandStatusCriteria = this.getSeqCmdInprogressCriteria();
        resourceIDCriteria = resourceIDCriteria.and(commandStatusCriteria);
        currentSequentialCommandQuery.setCriteria(resourceIDCriteria);
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(currentSequentialCommandQuery);
            if (dataObject.isEmpty()) {
                return null;
            }
            final Row row = dataObject.getFirstRow("MdSequentialCommands");
            final Row statusRow = dataObject.getFirstRow("SequentialCmdExecutionStatus");
            final SequentialSubCommand sequentialSubCommand = new SequentialSubCommand();
            sequentialSubCommand.CommandID = (Long)row.get("COMMAND_ID");
            sequentialSubCommand.SequentialCommandID = (Long)row.get("SEQUENTIAL_COMMAND_ID");
            sequentialSubCommand.order = (int)row.get("ORDER");
            sequentialSubCommand.Handler = (String)row.get("HANDLER");
            sequentialSubCommand.AddedAt = (Long)statusRow.get("SEQUENTIAL_COMMAND_ADDED_AT");
            sequentialSubCommand.UpdatedAt = (Long)statusRow.get("UPDATED_AT");
            sequentialSubCommand.status = (int)statusRow.get("COMMAND_STATUS");
            sequentialSubCommand.resourceID = resourceID;
            final Row initParamsRow = dataObject.getFirstRow("MdCommandToSequentialCommand");
            final String strInitParams = (String)initParamsRow.get("PARAMS");
            JSONObject initParams = null;
            if (!MDMStringUtils.isEmpty(strInitParams)) {
                initParams = new JSONObject(strInitParams);
            }
            final Row paramsRow = dataObject.getFirstRow("SequentialCommandParams");
            final String strParam = (String)paramsRow.get("PARAMS");
            if (!MDMStringUtils.isEmpty(strParam)) {
                sequentialSubCommand.params = new JSONObject(strParam);
            }
            else {
                sequentialSubCommand.params = new JSONObject();
            }
            if (initParams != null) {
                sequentialSubCommand.params.put("CommandLevelParams", (Object)initParams);
            }
            return sequentialSubCommand;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in getting sequential command status", e);
            return null;
        }
    }
    
    public boolean isSequentialCommandResponse(final Long resourceID, final String UUID) {
        Boolean result = Boolean.FALSE;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCmdExecutionStatus"));
        selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "MdSequentialCommands", new String[] { "SEQUENTIAL_COMMAND_ID", "EXECUTION_STATUS" }, new String[] { "SEQUENTIAL_COMMAND_ID", "ORDER" }, 2));
        selectQuery.addJoin(new Join("MdSequentialCommands", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria statusCriteria = getInstance().getSeqCmdInprogressCriteria();
        final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)UUID, 0);
        selectQuery.addSelectColumn(Column.getColumn("MdCommands", "*"));
        selectQuery.setCriteria(resourceCriteria.and(commandCriteria).and(statusCriteria));
        try {
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (dataObject.isEmpty()) {
                result = false;
            }
            else {
                result = true;
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "uable to check if sequential commadn response. The command will suspend after time out.", (Throwable)e);
        }
        return result;
    }
    
    public SequentialSubCommand getIfSequentialCommandResponse(final Long resourceID, final String uuid) {
        SequentialSubCommand sequentialSubCommand = null;
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCmdExecutionStatus"));
        selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "MdSequentialCommands", new String[] { "SEQUENTIAL_COMMAND_ID", "EXECUTION_STATUS" }, new String[] { "SEQUENTIAL_COMMAND_ID", "ORDER" }, 2));
        selectQuery.addJoin(new Join("MdSequentialCommands", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        selectQuery.addJoin(new Join("MdSequentialCommands", "MdCommandToSequentialCommand", new String[] { "SEQUENTIAL_COMMAND_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID" }, 2));
        selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "SequentialCommandParams", new String[] { "RESOURCE_ID", "SEQUENTIAL_COMMAND_ID" }, new String[] { "RESOURCE_ID", "SEQUENTIAL_COMMAND_ID" }, 2));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria statusCriteria = getInstance().getSeqCmdInprogressCriteria();
        final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)uuid, 0);
        selectQuery.addSelectColumn(Column.getColumn("MdCommandToSequentialCommand", "*"));
        selectQuery.addSelectColumn(Column.getColumn("MdSequentialCommands", "*"));
        selectQuery.addSelectColumn(Column.getColumn("SequentialCmdExecutionStatus", "*"));
        selectQuery.addSelectColumn(Column.getColumn("SequentialCommandParams", "*"));
        selectQuery.setCriteria(resourceCriteria.and(commandCriteria).and(statusCriteria));
        try {
            DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MdSequentialCommands");
                final Row statusRow = dataObject.getFirstRow("SequentialCmdExecutionStatus");
                final Row commandRow = dataObject.getFirstRow("MdCommandToSequentialCommand");
                final Row paramRow = dataObject.getFirstRow("SequentialCommandParams");
                sequentialSubCommand = new SequentialSubCommand();
                sequentialSubCommand.CommandID = (Long)row.get("COMMAND_ID");
                sequentialSubCommand.SequentialCommandID = (Long)row.get("SEQUENTIAL_COMMAND_ID");
                sequentialSubCommand.order = (int)row.get("ORDER");
                sequentialSubCommand.Handler = (String)row.get("HANDLER");
                sequentialSubCommand.AddedAt = (Long)statusRow.get("SEQUENTIAL_COMMAND_ADDED_AT");
                sequentialSubCommand.UpdatedAt = (Long)statusRow.get("UPDATED_AT");
                sequentialSubCommand.status = (int)statusRow.get("COMMAND_STATUS");
                sequentialSubCommand.resourceID = resourceID;
                sequentialSubCommand.isImmidiate = (Boolean)commandRow.get("ALLOW_IMMEDIATE_PROCESSING");
                sequentialSubCommand.params = new JSONObject((String)paramRow.get("PARAMS"));
                selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCmdExecutionStatus"));
                selectQuery.addSelectColumn(Column.getColumn("SequentialCmdExecutionStatus", "*"));
                selectQuery.setCriteria(resourceCriteria.and(statusCriteria));
                dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                final Iterator iterator = dataObject.getRows("SequentialCmdExecutionStatus");
                int cnt = 0;
                while (iterator.hasNext()) {
                    final Row statusRowForSuspend = iterator.next();
                    statusRowForSuspend.set("COMMAND_STATUS", (Object)5);
                    dataObject.updateRow(statusRowForSuspend);
                    ++cnt;
                }
                if (cnt > 1) {
                    MDMUtil.getPersistenceLite().update(dataObject);
                    this.logger.log(Level.SEVERE, "Multiple seq cmds Exsisted for the device and were suspended : {0} SeqSubCmd ; {1}", new Object[] { cnt, sequentialSubCommand });
                    sequentialSubCommand = null;
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "uable to check if sequential commadn response. The command will suspend after time out.", (Throwable)e);
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Unable to check the sequential command response. The command will suspend", (Throwable)e2);
        }
        return sequentialSubCommand;
    }
    
    protected void sequentialCommandCleanUpforResource(final Long resourceID, final boolean isNotify) {
        this.logger.log(Level.INFO, "calling Seq Cmd clean up for resource : {0}", new Object[] { resourceID });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCmdExecutionStatus"));
        selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "SequentialCommandParams", new String[] { "SEQUENTIAL_COMMAND_ID", "RESOURCE_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID", "RESOURCE_ID" }, 1));
        final Criteria resourceDeleteCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        selectQuery.setCriteria(resourceDeleteCriteria);
        final Criteria commandStatusDeleteCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "COMMAND_STATUS"), (Object)new int[] { 3, 180, 120, 16, 190 }, 8);
        selectQuery.addSelectColumn(Column.getColumn("SequentialCmdExecutionStatus", "*"));
        selectQuery.addSelectColumn(Column.getColumn("SequentialCommandParams", "*"));
        try {
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            final Row curSeq = dataObject.getRow("SequentialCmdExecutionStatus", commandStatusDeleteCriteria);
            final Long seqCmdID = (Long)curSeq.get("SEQUENTIAL_COMMAND_ID");
            final Criteria paramDeleteCriteria = new Criteria(new Column("SequentialCommandParams", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria paramSeqIDDeleteCriteria = new Criteria(new Column("SequentialCommandParams", "SEQUENTIAL_COMMAND_ID"), (Object)seqCmdID, 0);
            dataObject.deleteRows("SequentialCmdExecutionStatus", commandStatusDeleteCriteria);
            dataObject.deleteRows("SequentialCommandParams", paramDeleteCriteria.and(paramSeqIDDeleteCriteria));
            MDMUtil.getPersistence().update(dataObject);
            final boolean hasQueueSeqCmd = this.hasQueuedSequentialCommand(resourceID, dataObject);
            if (isNotify && (hasQueueSeqCmd || this.hasQueuedCollectionCommand(resourceID))) {
                this.sendNotification(resourceID);
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Data Access Exception in getCurrentSeqCmdOfResource(): ", (Throwable)e);
        }
    }
    
    private boolean hasQueuedSequentialCommand(final Long resourceID, final DataObject dataObject) {
        Criteria resourceIDCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria commandStatusCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "COMMAND_STATUS"), (Object)12, 0);
        resourceIDCriteria = resourceIDCriteria.and(commandStatusCriteria);
        try {
            final Row row = dataObject.getRow("SequentialCmdExecutionStatus", resourceIDCriteria);
            if (row == null) {
                return false;
            }
            final Long seqID = (Long)row.get("SEQUENTIAL_COMMAND_ID");
            final List resList = new ArrayList();
            resList.add(resourceID);
            SeqCmdRepository.getInstance().initialiseAndLoadFirstCommand(resList, seqID, null);
            return true;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in hasQueuedSequentialCommand", e);
            return false;
        }
    }
    
    private boolean hasQueuedCollectionCommand(final Long resourceID) {
        final Table table = new Table("MdCommandsToDevice");
        final SelectQuery queuedCollectionCommandCriteria = (SelectQuery)new SelectQueryImpl(table);
        final Column deviceIDColumn = new Column("MdCommandsToDevice", "COMMAND_DEVICE_ID");
        final Criteria resourceIDCriteria = new Criteria(new Column("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria yetToApplyStatus = new Criteria(new Column("MdCommandsToDevice", "RESOURCE_COMMAND_STATUS"), (Object)12, 0);
        queuedCollectionCommandCriteria.setCriteria(resourceIDCriteria.and(yetToApplyStatus));
        queuedCollectionCommandCriteria.addSelectColumn(deviceIDColumn);
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(queuedCollectionCommandCriteria);
            return !dataObject.isEmpty();
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in hasQueuedCollectionCommand", e);
            return false;
        }
    }
    
    protected JSONObject getNextSubCommandsInQueueforResource(final Long resourceID) {
        final Table table = new Table("MdSequentialCommands");
        final SelectQuery getSequentialCommandQuery = (SelectQuery)new SelectQueryImpl(table);
        final Column operationsAll = new Column("MdSequentialCommands", "*");
        getSequentialCommandQuery.addSelectColumn(operationsAll);
        final SequentialSubCommand sequentialSubCommand = this.getCurrentSeqCmdOfResource(resourceID);
        final Criteria SeqCriteria = new Criteria(new Column("MdSequentialCommands", "SEQUENTIAL_COMMAND_ID"), (Object)sequentialSubCommand.SequentialCommandID, 0);
        final Criteria StatusCriteria = new Criteria(new Column("MdSequentialCommands", "ORDER"), (Object)sequentialSubCommand.order, 4);
        getSequentialCommandQuery.setCriteria(SeqCriteria.and(StatusCriteria));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(getSequentialCommandQuery);
            if (!dataObject.isEmpty()) {
                final Iterator it = dataObject.getRows("MdSequentialCommands");
                final JSONArray nextCommands = new JSONArray();
                final int i = 0;
                while (it.hasNext()) {
                    final Row row = it.next();
                    nextCommands.put((Object)row.get("COMMAND_ID"));
                }
                final JSONObject j = new JSONObject();
                j.put("QueuedCommands", (Object)nextCommands);
                return j;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in getSeqCommands", e);
        }
        return new JSONObject();
    }
    
    public JSONArray getNextSubCommandsDetailsInQueueForResource(final Long resourceId) {
        final JSONArray commandArray = new JSONArray();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCmdExecutionStatus"));
            selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "MdSequentialCommands", new String[] { "SEQUENTIAL_COMMAND_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID" }, 2));
            selectQuery.addJoin(new Join("MdSequentialCommands", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            final Criteria resourceIdCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria inProgressCriteria = this.getSeqCmdInprogressCriteria();
            selectQuery.addSelectColumn(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("SequentialCmdExecutionStatus", "SEQUENTIAL_COMMAND_ID"));
            selectQuery.addSelectColumn(new Column("SequentialCmdExecutionStatus", "EXECUTION_STATUS"));
            selectQuery.addSelectColumn(new Column("MdSequentialCommands", "COMMAND_ID"));
            selectQuery.addSelectColumn(new Column("MdSequentialCommands", "ORDER"));
            selectQuery.addSelectColumn(new Column("MdSequentialCommands", "SEQUENTIAL_COMMAND_ID"));
            selectQuery.addSelectColumn(new Column("MdCommands", "COMMAND_ID"));
            selectQuery.addSelectColumn(new Column("MdCommands", "COMMAND_TYPE"));
            selectQuery.addSelectColumn(new Column("MdCommands", "COMMAND_UUID"));
            selectQuery.setCriteria(resourceIdCriteria.and(inProgressCriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final SortColumn orderSortColumn = new SortColumn(new Column("MdSequentialCommands", "ORDER"), true);
                dataObject.sortRows("MdSequentialCommands", new SortColumn[] { orderSortColumn });
                final Row sequentialCommandRow = dataObject.getFirstRow("SequentialCmdExecutionStatus");
                if (sequentialCommandRow != null) {
                    final int executedOrder = (int)sequentialCommandRow.get("EXECUTION_STATUS");
                    final Criteria orderCriteria = new Criteria(new Column("MdSequentialCommands", "ORDER"), (Object)executedOrder, 4);
                    final Iterator iterator = dataObject.getRows("MdSequentialCommands", orderCriteria);
                    while (iterator.hasNext()) {
                        final JSONObject commandObject = new JSONObject();
                        final Row sequentialRow = iterator.next();
                        final Long commandId = (Long)sequentialRow.get("COMMAND_ID");
                        final int order = (int)sequentialRow.get("ORDER");
                        final Criteria commandCriteria = new Criteria(new Column("MdCommands", "COMMAND_ID"), (Object)commandId, 0);
                        final Row commandRow = dataObject.getRow("MdCommands", commandCriteria);
                        commandObject.put("COMMAND_ID", (Object)commandId);
                        commandObject.put("COMMAND_UUID", commandRow.get("COMMAND_UUID"));
                        commandObject.put("COMMAND_TYPE", commandRow.get("COMMAND_TYPE"));
                        commandObject.put("order", order);
                        commandArray.put((Object)commandObject);
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in getting next sub command details", (Throwable)e);
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Exception in getting next sub command details", (Throwable)e2);
        }
        return commandArray;
    }
    
    protected void loadNextSubCommand(final Long seqID, final Long cmdID, final Long resID) throws Exception {
        this.logger.log(Level.INFO, "Load next subcommand called seqID : {0} cmdid : {1} resID : {2}", new Object[] { seqID, cmdID, resID });
        final Table table = new Table("MdSequentialCommands");
        final SelectQuery nextCommandQuery = (SelectQuery)new SelectQueryImpl(table);
        Criteria seqCmdCriteria = new Criteria(new Column("MdSequentialCommands", "SEQUENTIAL_COMMAND_ID"), (Object)seqID, 0);
        if (!cmdID.equals(SeqCmdConstants.INITIALIZE)) {
            final Criteria commandIDCriteria = new Criteria(new Column("MdSequentialCommands", "COMMAND_ID"), (Object)cmdID, 0);
            seqCmdCriteria = seqCmdCriteria.and(commandIDCriteria);
        }
        nextCommandQuery.setCriteria(seqCmdCriteria);
        final Column all = new Column("MdSequentialCommands", "*");
        nextCommandQuery.addSelectColumn(all);
        final SortColumn sortColumn = new SortColumn("MdSequentialCommands", "ORDER", true);
        nextCommandQuery.addSortColumn(sortColumn);
        final DataObject dataObject = MDMUtil.getPersistence().get(nextCommandQuery);
        final Row row = dataObject.getFirstRow("MdSequentialCommands");
        final Long nextCmdID = (Long)row.get("COMMAND_ID");
        SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resID, (int)row.get("ORDER"), seqID, 3);
        if (!cmdID.equals(SeqCmdConstants.INITIALIZE)) {
            final SeqCmdStatusUpdateHandler statusUpdateHandler = this.getStatusUpdateHandler(resID);
            statusUpdateHandler.makeStatusUpdateforSubCommand(cmdID, resID, seqID);
        }
        DeviceCommandRepository.getInstance().assignSeqSubCmdToDevices(nextCmdID, resID, 1);
        this.logger.log(Level.INFO, "next cmd loaded {0}", nextCmdID);
    }
    
    void sendNotification(final Long resourceID) {
        final List resourceList = new ArrayList();
        resourceList.add(resourceID);
        this.logger.log(Level.INFO, "Resources sent for notification : {0}", new Object[] { resourceList });
        try {
            final int platformType = ManagedDeviceHandler.getInstance().getPlatformType(resourceID);
            if (platformType == 1) {
                NotificationHandler.getInstance().SendNotification(resourceList, 1);
            }
            else if (platformType == 2) {
                NotificationHandler.getInstance().SendNotification(resourceList, 2);
                NotificationHandler.getInstance().SendNotification(resourceList, 201);
            }
            else if (platformType == 3) {
                NotificationHandler.getInstance().SendNotification(resourceList, 3);
                NotificationHandler.getInstance().SendNotification(resourceList, 303);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Error in Sending Notification", e);
        }
    }
    
    public Long getBaseCommandIDforSequentialID(final Long sequentialID) {
        Long commandID = null;
        final Table BasecmdtoSeqcmd = new Table("MdCommandToSequentialCommand");
        final SelectQuery sequentialcmdforbase = (SelectQuery)new SelectQueryImpl(BasecmdtoSeqcmd);
        sequentialcmdforbase.addSelectColumn(new Column("MdCommandToSequentialCommand", "*"));
        final Criteria seqeuntailCriteria = new Criteria(new Column("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID"), (Object)sequentialID, 0);
        sequentialcmdforbase.setCriteria(seqeuntailCriteria);
        try {
            commandID = (Long)MDMUtil.getPersistence().get(sequentialcmdforbase).getFirstRow("MdCommandToSequentialCommand").get("COMMAND_ID");
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Could not get base cmd ID :{0}", commandID);
        }
        return commandID;
    }
    
    public boolean isSequentialCommandProcessImmediately(final Long sequentialCommandID) {
        try {
            return (boolean)DBUtil.getValueFromDB("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID", (Object)sequentialCommandID, "ALLOW_IMMEDIATE_PROCESSING");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in isSequentialCommandProcessImmediately() : ", e);
            return false;
        }
    }
    
    public void loadSequentialCommandsForDevice(final Long resourceID) {
        final Table table = new Table("SequentialCmdExecutionStatus");
        final SelectQuery queuedSequentialCommandsQuery = (SelectQuery)new SelectQueryImpl(table);
        final Column all = new Column("SequentialCmdExecutionStatus", "*");
        final Criteria resourceIDCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria commandStatusCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "COMMAND_STATUS"), (Object)12, 0);
        queuedSequentialCommandsQuery.addSelectColumn(all);
        queuedSequentialCommandsQuery.setCriteria(resourceIDCriteria);
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(queuedSequentialCommandsQuery);
            final Row inProgressRow = dataObject.getRow("SequentialCmdExecutionStatus", getInstance().getSeqCmdInprogressCriteria());
            if (dataObject.isEmpty() || inProgressRow != null) {
                return;
            }
            final Row row = dataObject.getRow("SequentialCmdExecutionStatus", commandStatusCriteria);
            if (row != null) {
                final Long seqID = (Long)row.get("SEQUENTIAL_COMMAND_ID");
                this.loadNextSubCommand(seqID, SeqCmdConstants.INITIALIZE, resourceID);
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "cannot get Next Sequential Command : ", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Exception in Getting next Sequential command : ", e2);
        }
    }
    
    public boolean isApp(final Long collectionID) throws DataAccessException {
        final Table table = new Table("MdAppToCollection");
        final SelectQuery isAppQuery = (SelectQuery)new SelectQueryImpl(table);
        final Column all = new Column("MdAppToCollection", "*");
        isAppQuery.addSelectColumn(all);
        final Criteria collectionIDCriteria = new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
        isAppQuery.setCriteria(collectionIDCriteria);
        final Persistence persistence = MDMUtil.getPersistence();
        final DataObject dataObject = persistence.get(isAppQuery);
        return !dataObject.isEmpty();
    }
    
    public void removeSeqInstallProfileCmd(final List<Long> resourceList, final List<Long> collnList) {
        this.removeSeqInstallCmd(resourceList, collnList, "InstallProfile");
    }
    
    public void removeSeqInstallAppCmd(final List<Long> resourceList, final List<Long> collnList) {
        this.removeSeqInstallCmd(resourceList, collnList, "InstallApplication", Boolean.FALSE);
    }
    
    public void removeSeqInstallAppCmd(final List<Long> resourceList, final List<Long> collnList, final Boolean appUpdate) {
        this.removeSeqInstallCmd(resourceList, collnList, "InstallApplication", appUpdate);
    }
    
    private Map<Long, Long> getFirstCmdList(final List<Long> seqCmdList) throws DataAccessException {
        final Map<Long, Long> cmdToSeqCmdMap = new HashMap<Long, Long>();
        final SelectQuery firstCmdSelect = (SelectQuery)new SelectQueryImpl(new Table("MdSequentialCommands"));
        final Column all = new Column("MdSequentialCommands", "*");
        firstCmdSelect.addSelectColumn(all);
        final Criteria seqCmdCriteria = new Criteria(new Column("MdSequentialCommands", "SEQUENTIAL_COMMAND_ID"), (Object)seqCmdList.toArray(), 8);
        final Criteria firstCmdCriteria = new Criteria(new Column("MdSequentialCommands", "ORDER"), (Object)1, 0);
        firstCmdSelect.setCriteria(seqCmdCriteria.and(firstCmdCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(firstCmdSelect);
        for (int i = 0; i < seqCmdList.size(); ++i) {
            final Iterator iterator = dataObject.getRows("MdSequentialCommands", new Criteria(new Column("MdSequentialCommands", "SEQUENTIAL_COMMAND_ID"), (Object)seqCmdList.get(i), 0));
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                cmdToSeqCmdMap.put(seqCmdList.get(i), (Long)row.get("COMMAND_ID"));
            }
        }
        return cmdToSeqCmdMap;
    }
    
    private void deleteSeqCmdForResource(final List resourceList, final Long seqCmd) throws DataAccessException {
        final Criteria seqCmdCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "SEQUENTIAL_COMMAND_ID"), (Object)seqCmd, 0);
        final Criteria resCmdCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        final Criteria statusCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "COMMAND_STATUS"), (Object)new int[] { 12, 3, 16, 190 }, 8);
        final Criteria execStatusCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "EXECUTION_STATUS"), (Object)1, 0);
        final Criteria deleteCriteria = seqCmdCriteria.and(resCmdCriteria).and(statusCriteria).and(execStatusCriteria);
        MDMUtil.getPersistence().delete(deleteCriteria);
    }
    
    public void removeEarlierVersionSeqProfileCommand(final List resourceList, final Map<Long, Long> associatedProfileCollnMap) {
        final Set<Long> profileIDSet = associatedProfileCollnMap.keySet();
        final Map<Long, List<Long>> profileCollnMap = ProfileHandler.getProfileCollections(profileIDSet);
        final List<List<Long>> collectionList = new ArrayList<List<Long>>(profileCollnMap.values());
        final List<Long> collnMergeList = new ArrayList<Long>();
        for (final List<Long> collections : collectionList) {
            collnMergeList.addAll(collections);
        }
        collnMergeList.removeAll(new ArrayList<Object>(associatedProfileCollnMap.values()));
        if (!collnMergeList.isEmpty()) {
            this.removeSeqInstallProfileCmd(resourceList, collnMergeList);
        }
    }
    
    public ArrayList<Long> getResExecutingSeqCmd(final List<Long> resourceList, final Long seqCmd) throws DataAccessException {
        final ArrayList<Long> seqResList = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCmdExecutionStatus"));
        final Column all = new Column("SequentialCmdExecutionStatus", "*");
        final Criteria resCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        final Criteria seqCmdCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "SEQUENTIAL_COMMAND_ID"), (Object)seqCmd, 0);
        final Criteria statusCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "COMMAND_STATUS"), (Object)new int[] { 12, 3, 16, 190 }, 8);
        final Criteria execStatusCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "EXECUTION_STATUS"), (Object)1, 0);
        selectQuery.addSelectColumn(all);
        selectQuery.setCriteria(resCriteria.and(seqCmdCriteria).and(statusCriteria).and(execStatusCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("SequentialCmdExecutionStatus");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            seqResList.add((Long)row.get("RESOURCE_ID"));
        }
        return seqResList;
    }
    
    public SeqCmdStatusUpdateHandler getStatusUpdateHandler(final Long resID) {
        SeqCmdStatusUpdateHandler handler = new BaseSeqCmdStatusUpdateHandler();
        final int platformType = ManagedDeviceHandler.getInstance().getPlatformType(resID);
        if (platformType == 3) {
            handler = new WindowsSeqCmdStatusUpdateHandler();
        }
        else if (platformType == 1) {
            handler = new IOSSeqCmdStatusUpdateHandler();
        }
        return handler;
    }
    
    public JSONObject getBaseCmdDetailsforResource(final Long resourceID) throws JSONException {
        Long baseCmd = null;
        String UUID = null;
        JSONObject baseCmdJson = null;
        final Table table = new Table("MdCommandToSequentialCommand");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
        final Join join = new Join("MdCommandToSequentialCommand", "SequentialCmdExecutionStatus", new String[] { "SEQUENTIAL_COMMAND_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID" }, 2);
        final Join mdCommands = new Join("MdCommandToSequentialCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2);
        final Criteria resCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria statusCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "COMMAND_STATUS"), (Object)new int[] { 3, 180, 120, 190 }, 8);
        selectQuery.addJoin(join);
        selectQuery.addJoin(mdCommands);
        selectQuery.setCriteria(resCriteria.and(statusCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_UUID"));
        DataObject dataObject = null;
        try {
            dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MdCommands");
                baseCmd = (Long)row.get("COMMAND_ID");
                UUID = (String)row.get("COMMAND_UUID");
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "error in fetching base cmd id", (Throwable)e);
        }
        if (baseCmd != null) {
            baseCmdJson = new JSONObject();
            baseCmdJson.put("COMMAND_ID", (Object)baseCmd);
            baseCmdJson.put("COMMAND_UUID", (Object)UUID);
        }
        return baseCmdJson;
    }
    
    public JSONObject getSeqCmdParams(final Long resourceID, final Long seqCmdID) throws DataAccessException, JSONException {
        JSONObject params = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCommandParams"));
        selectQuery.addSelectColumn(new Column("SequentialCommandParams", "*"));
        final Criteria resCriteria = new Criteria(new Column("SequentialCommandParams", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria seqIDCriteria = new Criteria(new Column("SequentialCommandParams", "SEQUENTIAL_COMMAND_ID"), (Object)seqCmdID, 0);
        selectQuery.setCriteria(resCriteria.and(seqIDCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("SequentialCommandParams");
            params = new JSONObject((String)row.get("PARAMS"));
        }
        return params.optJSONObject("initialParams");
    }
    
    public void setSeqCmdParams(final Long resourceID, final Long seqCmdID, final JSONObject params) throws DataAccessException, JSONException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCommandParams"));
        selectQuery.addSelectColumn(new Column("SequentialCommandParams", "*"));
        final Criteria resCriteria = new Criteria(new Column("SequentialCommandParams", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria seqIDCriteria = new Criteria(new Column("SequentialCommandParams", "SEQUENTIAL_COMMAND_ID"), (Object)seqCmdID, 0);
        selectQuery.setCriteria(resCriteria.and(seqIDCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final JSONObject initialParams = new JSONObject();
        initialParams.put("initialParams", (Object)params);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("SequentialCommandParams");
            row.set("PARAMS", (Object)initialParams.toString());
            dataObject.updateRow(row);
        }
        else {
            final Row row = new Row("SequentialCommandParams");
            row.set("RESOURCE_ID", (Object)resourceID);
            row.set("SEQUENTIAL_COMMAND_ID", (Object)seqCmdID);
            row.set("PARAMS", (Object)initialParams.toString());
            dataObject.addRow(row);
        }
        MDMUtil.getPersistence().update(dataObject);
    }
    
    public void setSeqCmdParams(final List<Long> resourceList, final List<Long> seqCmdList, final JSONObject params) throws Exception {
        if (params != null) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCommandParams"));
            selectQuery.addSelectColumn(new Column("SequentialCommandParams", "*"));
            final Criteria resCriteria = new Criteria(new Column("SequentialCommandParams", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria seqIDCriteria = new Criteria(new Column("SequentialCommandParams", "SEQUENTIAL_COMMAND_ID"), (Object)seqCmdList.toArray(), 8);
            selectQuery.setCriteria(resCriteria.and(seqIDCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final JSONObject initialParams = new JSONObject();
            initialParams.put("initialParams", (Object)params);
            final HashMap hashMap = this.getCommandLevelParams(seqCmdList);
            for (final Long resID : resourceList) {
                for (final Long seqID : seqCmdList) {
                    if (this.isSeqCmdInResQueue(resID, seqID)) {
                        final Criteria curResCriteria = new Criteria(new Column("SequentialCommandParams", "RESOURCE_ID"), (Object)resID, 0);
                        final Criteria curSeqIDCriteria = new Criteria(new Column("SequentialCommandParams", "SEQUENTIAL_COMMAND_ID"), (Object)seqID, 0);
                        Row curCmdRow = dataObject.getRow("SequentialCommandParams", curResCriteria.and(curSeqIDCriteria));
                        if (curCmdRow != null) {
                            final String paramsStr = (String)curCmdRow.get("PARAMS");
                            if (paramsStr == null || paramsStr.equals("")) {
                                continue;
                            }
                            final JSONObject paramsJson = new JSONObject(paramsStr);
                            paramsJson.put("initialParams", (Object)params);
                            final JSONObject jsonObject = hashMap.get(seqID);
                            if (jsonObject != null) {
                                paramsJson.put("CommandLevelParams", (Object)jsonObject);
                            }
                            curCmdRow.set("PARAMS", (Object)paramsJson.toString());
                            dataObject.updateRow(curCmdRow);
                        }
                        else {
                            curCmdRow = new Row("SequentialCommandParams");
                            curCmdRow.set("RESOURCE_ID", (Object)resID);
                            curCmdRow.set("SEQUENTIAL_COMMAND_ID", (Object)seqID);
                            final JSONObject jsonObject2 = hashMap.get(seqID);
                            if (jsonObject2 != null) {
                                initialParams.put("CommandLevelParams", (Object)jsonObject2);
                            }
                            curCmdRow.set("PARAMS", (Object)initialParams.toString());
                            dataObject.addRow(curCmdRow);
                        }
                    }
                }
            }
            MDMUtil.getPersistence().update(dataObject);
        }
    }
    
    public void removeEarlierVersionSeqCommand(final List resourceList, final Map<Long, Long> associatedProfileCollnMap, final String commandName) {
        final Set<Long> profileIDSet = associatedProfileCollnMap.keySet();
        final Map<Long, List<Long>> profileCollnMap = ProfileHandler.getProfileCollections(profileIDSet);
        final List<List<Long>> collectionList = new ArrayList<List<Long>>(profileCollnMap.values());
        final List<Long> collnMergeList = new ArrayList<Long>();
        for (final List<Long> collections : collectionList) {
            collnMergeList.addAll(collections);
        }
        collnMergeList.removeAll(new ArrayList<Object>(associatedProfileCollnMap.values()));
        if (!collnMergeList.isEmpty()) {
            this.removeSeqInstallCmd(resourceList, collnMergeList, commandName);
        }
    }
    
    public void removeSeqInstallCmd(final List resourceList, final List collnList, final String commandName) {
        this.removeSeqInstallCmd(resourceList, collnList, commandName, Boolean.FALSE);
    }
    
    public void removeSeqInstallCmd(final List resourceList, final List collnList, final String commandName, final Boolean update) {
        final List<Long> seqResList = getInstance().getResourcesRunningSeqCmd(resourceList);
        List<Long> installCmdList = new ArrayList<Long>();
        try {
            installCmdList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collnList, commandName);
            final List seqCmdList = getInstance().getSequentialIDforBaseID(installCmdList);
            if (seqResList.isEmpty() || seqCmdList.isEmpty()) {
                return;
            }
            final Map<Long, Long> firstCmdMap = this.getFirstCmdList(seqCmdList);
            for (int i = 0; i < seqCmdList.size(); ++i) {
                final List curResList = this.getResExecutingSeqCmd(seqResList, seqCmdList.get(i));
                if (curResList.size() != 0) {
                    this.logger.log(Level.INFO, "removing sequential command list for resource list {0} : {1}", new Object[] { String.valueOf(seqCmdList.get(i)), curResList });
                    this.deleteSeqCmdForResource(curResList, seqCmdList.get(i));
                    final List<Long> delcmdList = new ArrayList<Long>();
                    delcmdList.add(firstCmdMap.get(seqCmdList.get(i)));
                    DeviceCommandRepository.getInstance().clearCommandsFromCacheForResources(delcmdList, curResList, 1);
                    DeviceCommandRepository.getInstance().deleteResourcesCommands(delcmdList, curResList, 1);
                    final Long collnID = DeviceCommandRepository.getInstance().getCollectionId(firstCmdMap.get(seqCmdList.get(i)));
                    if (collnID != null && !update) {
                        final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collnID);
                        if (appGroupId != null) {
                            AppsUtil.getInstance().deleteAppResourceRel(curResList, appGroupId);
                            ProfileAssociateHandler.getInstance().deleteRecentProfileForResourceListCollection(curResList, collnID);
                            ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
                            final String remarks = "dc.db.mdm.collection.Successfully_removed_the_app";
                            MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(curResList, collnID, 6, remarks);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Error when trying to remove sequential Cmd", e);
        }
    }
    
    public JSONObject getSeqParamsFromBaseCmd(final Long baseCommand) throws DataAccessException, JSONException {
        JSONObject params = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommandToSequentialCommand"));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final Criteria criteria = new Criteria(new Column("MdCommandToSequentialCommand", "COMMAND_ID"), (Object)baseCommand, 0);
        selectQuery.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdCommandToSequentialCommand");
            params = new JSONObject((String)row.get("PARAMS"));
        }
        return params;
    }
    
    public Long getBaseCollectionIDForResource(final Long resourceId) throws DataAccessException {
        Long collectionId = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommandToSequentialCommand"));
        final Criteria resourceCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria statusCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "COMMAND_STATUS"), (Object)new int[] { 3, 120, 180, 16, 190 }, 8);
        selectQuery.addJoin(new Join("MdCommandToSequentialCommand", "SequentialCmdExecutionStatus", new String[] { "SEQUENTIAL_COMMAND_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID" }, 2));
        selectQuery.addJoin(new Join("MdCommandToSequentialCommand", "MdCollectionCommand", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        selectQuery.setCriteria(resourceCriteria.and(statusCriteria));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdCollectionCommand");
            collectionId = (Long)row.get("COLLECTION_ID");
        }
        return collectionId;
    }
    
    private boolean isSeqCmdInResQueue(final Long resourceID, final Long seqCmdID) {
        boolean isCmdinQueue = false;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCmdExecutionStatus"));
        selectQuery.addSelectColumn(new Column("SequentialCmdExecutionStatus", "*"));
        final Criteria resCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria seqCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "SEQUENTIAL_COMMAND_ID"), (Object)seqCmdID, 0);
        selectQuery.setCriteria(resCriteria.and(seqCriteria));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                isCmdinQueue = true;
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "error in checking command exists", (Throwable)e);
        }
        return isCmdinQueue;
    }
    
    public JSONObject getBaseParamsForResource(final Long resourceId) throws Exception {
        JSONObject params = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommandToSequentialCommand"));
        final Criteria resourceCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria statusCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "COMMAND_STATUS"), (Object)new int[] { 3, 120, 180, 16, 190 }, 8);
        selectQuery.addJoin(new Join("MdCommandToSequentialCommand", "SequentialCmdExecutionStatus", new String[] { "SEQUENTIAL_COMMAND_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID" }, 2));
        selectQuery.setCriteria(resourceCriteria.and(statusCriteria));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdCommandToSequentialCommand");
            final String param = (String)row.get("PARAMS");
            this.logger.log(Level.INFO, "Sequential Command Base Param for resource{0}: params{1}", new Object[] { resourceId, param });
            if (param != null) {
                params = new JSONObject(param);
            }
        }
        return params;
    }
    
    public Long getCollectionIdFromSeqCmdID(final Long seqCmdId) throws DataAccessException {
        Long collectionId = null;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("MdCommandToSequentialCommand"));
        query.addJoin(new Join("MdCommandToSequentialCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        query.addJoin(new Join("MdCommands", "MdCollectionCommand", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        query.setCriteria(new Criteria(new Column("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID"), (Object)seqCmdId, 0));
        query.addSelectColumn(new Column("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID"));
        query.addSelectColumn(new Column("MdCommands", "COMMAND_ID"));
        query.addSelectColumn(new Column("MdCollectionCommand", "COLLECTION_ID"));
        query.addSelectColumn(new Column("MdCollectionCommand", "COMMAND_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdCollectionCommand");
            collectionId = (Long)row.get("COLLECTION_ID");
        }
        return collectionId;
    }
    
    public JSONObject getCommandLevelParamsForSeqCmd(final Long seqcmdID) throws Exception {
        JSONObject jsonObject = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommandToSequentialCommand"));
        selectQuery.addSelectColumn(Column.getColumn("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdCommandToSequentialCommand", "PARAMS"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID"), (Object)seqcmdID, 0));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdCommandToSequentialCommand");
            final String params = (String)row.get("PARAMS");
            if (params != null) {
                jsonObject = new JSONObject(params);
            }
        }
        return jsonObject;
    }
    
    public HashMap getCommandLevelParams(final List seqcmdID) throws Exception {
        final HashMap retVal = new HashMap();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommandToSequentialCommand"));
        selectQuery.addSelectColumn(Column.getColumn("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdCommandToSequentialCommand", "PARAMS"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID"), (Object)seqcmdID.toArray(), 8));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MdCommandToSequentialCommand");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String params = (String)row.get("PARAMS");
            if (params != null) {
                final JSONObject jsonObject = new JSONObject(params);
                retVal.put(row.get("SEQUENTIAL_COMMAND_ID"), jsonObject);
            }
        }
        return retVal;
    }
    
    public Long getBaseProfileIDForSeqID(final Long seqID) throws DataAccessException {
        Long profileID = null;
        final Table BasecmdtoSeqcmd = new Table("MdCommandToSequentialCommand");
        final SelectQuery sequentialcmdforbase = (SelectQuery)new SelectQueryImpl(BasecmdtoSeqcmd);
        sequentialcmdforbase.addJoin(new Join("MdCommandToSequentialCommand", "MdCollectionCommand", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        sequentialcmdforbase.addJoin(new Join("MdCollectionCommand", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sequentialcmdforbase.addSelectColumn(new Column("ProfileToCollection", "*"));
        final Criteria seqeuntailCriteria = new Criteria(new Column("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID"), (Object)seqID, 0);
        sequentialcmdforbase.setCriteria(seqeuntailCriteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(sequentialcmdforbase);
        final Row row = dataObject.getFirstRow("ProfileToCollection");
        profileID = (Long)row.get("PROFILE_ID");
        return profileID;
    }
    
    public Long getAssociatedUserFromParams(final JSONObject initialParams, final Long seqCmdID) throws Exception {
        Long associatedUser = null;
        if (initialParams != null) {
            final Long profileID = getInstance().getBaseProfileIDForSeqID(seqCmdID);
            final String loggedinUser = initialParams.opt("UserId").toString();
            if (loggedinUser != null) {
                try {
                    final JSONObject associatedUserJSON = new JSONObject(loggedinUser);
                    final Iterator iterator = associatedUserJSON.keys();
                    while (iterator.hasNext()) {
                        final String key = iterator.next();
                        if (new Long(Long.parseLong(key)).equals(profileID)) {
                            associatedUser = associatedUserJSON.optLong(key);
                        }
                    }
                }
                catch (final Exception e) {
                    this.logger.log(Level.WARNING, "couldnt type cast to JSON Object: trying to cast associated user as long");
                    associatedUser = Long.parseLong(loggedinUser);
                }
            }
        }
        return associatedUser;
    }
    
    public Long getAssociatedUserFromParamsFrombaseID(final JSONObject initialParams, final Long baseProfileID) throws Exception {
        Long associatedUser = null;
        if (initialParams != null) {
            final String loggedinUser = initialParams.opt("UserId").toString();
            if (loggedinUser != null) {
                try {
                    final JSONObject associatedUserJSON = new JSONObject(loggedinUser);
                    final Iterator iterator = associatedUserJSON.keys();
                    while (iterator.hasNext()) {
                        final String key = iterator.next();
                        if (new Long(Long.parseLong(key)).equals(baseProfileID)) {
                            associatedUser = associatedUserJSON.optLong(key);
                        }
                    }
                }
                catch (final Exception e) {
                    this.logger.log(Level.WARNING, "couldnt type cast to JSON Object: trying to cast associated user as long");
                    associatedUser = Long.parseLong(loggedinUser);
                }
            }
        }
        return associatedUser;
    }
    
    public List getCommandList(final List<SequentialSubCommand> seqList) {
        final List commandList = new ArrayList();
        for (final SequentialSubCommand sequentialSubCommand : seqList) {
            commandList.add(sequentialSubCommand.CommandID);
        }
        return commandList;
    }
    
    public List getSeqList(final List<SequentialSubCommand> seqList) {
        final List commandList = new ArrayList();
        for (final SequentialSubCommand sequentialSubCommand : seqList) {
            commandList.add(sequentialSubCommand.SequentialCommandID);
        }
        return commandList;
    }
    
    public List getResList(final List<SequentialSubCommand> seqList) {
        final List resList = new ArrayList();
        for (final SequentialSubCommand sequentialSubCommand : seqList) {
            resList.add(sequentialSubCommand.resourceID);
        }
        return resList;
    }
    
    static {
        SeqCmdUtils.sequentialCommandUtils = null;
    }
}
