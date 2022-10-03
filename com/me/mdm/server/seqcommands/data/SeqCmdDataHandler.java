package com.me.mdm.server.seqcommands.data;

import org.json.JSONObject;
import com.me.mdm.server.seqcommands.SeqCmdStatusUpdateHandler;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdStatusUpdateHandler;
import com.me.mdm.server.seqcommands.windows.WindowsSeqCmdStatusUpdateHandler;
import com.me.mdm.server.seqcommands.BaseSeqCmdStatusUpdateHandler;
import java.util.HashMap;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class SeqCmdDataHandler
{
    public Logger logger;
    private DataObject statusDataObject;
    private DataObject commandDataObject;
    List cleanUpQueue;
    ArrayList commandsToLoad;
    List resList;
    List seqList;
    
    public SeqCmdDataHandler() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
        this.cleanUpQueue = new ArrayList();
        this.commandsToLoad = new ArrayList();
    }
    
    public void initalise(final List resList, final List seqCmdList) throws DataAccessException {
        this.resList = resList;
        this.seqList = seqCmdList;
        final long stime = System.currentTimeMillis();
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCmdExecutionStatus"));
        selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "SequentialCommandParams", new String[] { "RESOURCE_ID", "SEQUENTIAL_COMMAND_ID" }, new String[] { "RESOURCE_ID", "SEQUENTIAL_COMMAND_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("SequentialCmdExecutionStatus", "*"));
        selectQuery.addSelectColumn(Column.getColumn("SequentialCommandParams", "*"));
        final Criteria seqCriteria = new Criteria(Column.getColumn("SequentialCmdExecutionStatus", "SEQUENTIAL_COMMAND_ID"), (Object)seqCmdList.toArray(), 8);
        final Criteria resCriteria = new Criteria(Column.getColumn("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resList.toArray(), 8);
        selectQuery.setCriteria(seqCriteria.and(resCriteria));
        this.statusDataObject = MDMUtil.getPersistence().get(selectQuery);
        selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdSequentialCommands"));
        selectQuery.addSelectColumn(Column.getColumn("MdSequentialCommands", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdSequentialCommands", "SEQUENTIAL_COMMAND_ID"), (Object)seqCmdList.toArray(), 8));
        final SortColumn commandSortColumn = new SortColumn(new Column("MdSequentialCommands", "SEQUENTIAL_COMMAND_ID"), true);
        final SortColumn orderSortColumn = new SortColumn(new Column("MdSequentialCommands", "ORDER"), true);
        (this.commandDataObject = MDMUtil.getPersistence().get(selectQuery)).sortRows("MdSequentialCommands", new SortColumn[] { commandSortColumn, orderSortColumn });
        this.logger.log(Level.INFO, "[Seq] [Init] initalise():Time to intialise the DO for bulk - {0} hashcode  {1}", new Object[] { System.currentTimeMillis() - stime, this.hashCode() });
    }
    
    public SequentialSubCommand getCurrentSeqCmdForResource(final Long resID) throws Exception {
        SequentialSubCommand sequentialSubCommand = null;
        final Criteria resourceIDCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resID, 0);
        final Criteria commandStatusCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "COMMAND_STATUS"), (Object)new int[] { 3, 120, 180, 16, 190 }, 8);
        final Row row = this.statusDataObject.getRow("SequentialCmdExecutionStatus", resourceIDCriteria.and(commandStatusCriteria));
        if (row != null) {
            final Long seqID = (Long)row.get("SEQUENTIAL_COMMAND_ID");
            final int order = (int)row.get("EXECUTION_STATUS");
            Criteria sequentialIDCriteria = new Criteria(new Column("MdSequentialCommands", "SEQUENTIAL_COMMAND_ID"), (Object)seqID, 0);
            final Criteria orderCriteria = new Criteria(new Column("MdSequentialCommands", "ORDER"), (Object)order, 0);
            sequentialIDCriteria = sequentialIDCriteria.and(orderCriteria);
            final Row commandRow = this.commandDataObject.getRow("MdSequentialCommands", sequentialIDCriteria);
            sequentialSubCommand = new SequentialSubCommand();
            sequentialSubCommand.CommandID = (Long)commandRow.get("COMMAND_ID");
            sequentialSubCommand.SequentialCommandID = (Long)commandRow.get("SEQUENTIAL_COMMAND_ID");
            sequentialSubCommand.order = (int)commandRow.get("ORDER");
            sequentialSubCommand.Handler = (String)commandRow.get("HANDLER");
            sequentialSubCommand.AddedAt = (Long)row.get("SEQUENTIAL_COMMAND_ADDED_AT");
            sequentialSubCommand.UpdatedAt = (Long)row.get("UPDATED_AT");
            sequentialSubCommand.params = this.getParams(resID, sequentialSubCommand.SequentialCommandID);
            sequentialSubCommand.resourceID = resID;
        }
        return sequentialSubCommand;
    }
    
    public SequentialSubCommand getNextCommand(final SequentialSubCommand currentSubCommand) throws DataAccessException {
        SequentialSubCommand sequentialSubCommand = null;
        final Criteria sequentialIDCriteria = new Criteria(new Column("MdSequentialCommands", "SEQUENTIAL_COMMAND_ID"), (Object)currentSubCommand.SequentialCommandID, 0);
        final Criteria orderCriteria = new Criteria(new Column("MdSequentialCommands", "ORDER"), (Object)new Long(currentSubCommand.order), 5);
        final Row row = this.commandDataObject.getRow("MdSequentialCommands", sequentialIDCriteria.and(orderCriteria));
        if (row != null) {
            sequentialSubCommand = new SequentialSubCommand();
            sequentialSubCommand.CommandID = (Long)row.get("COMMAND_ID");
            sequentialSubCommand.SequentialCommandID = (Long)row.get("SEQUENTIAL_COMMAND_ID");
            sequentialSubCommand.order = (int)row.get("ORDER");
            sequentialSubCommand.Handler = (String)row.get("HANDLER");
            sequentialSubCommand.resourceID = currentSubCommand.resourceID;
            sequentialSubCommand.params = currentSubCommand.params;
        }
        return sequentialSubCommand;
    }
    
    public void addorUpdateSeqStatusAndQueue(final Long resourceID, final int order, final Long sequentialcmdID, final int cmdStatus) throws DataAccessException {
        Row row = null;
        final Criteria resourceIDCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria commandIDCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "SEQUENTIAL_COMMAND_ID"), (Object)sequentialcmdID, 0);
        row = this.statusDataObject.getRow("SequentialCmdExecutionStatus", resourceIDCriteria.and(commandIDCriteria));
        if (row == null) {
            row = new Row("SequentialCmdExecutionStatus");
            row.set("RESOURCE_ID", (Object)resourceID);
            row.set("EXECUTION_STATUS", (Object)order);
            row.set("SEQUENTIAL_COMMAND_ID", (Object)sequentialcmdID);
            row.set("COMMAND_STATUS", (Object)cmdStatus);
            final Row row2 = row;
            final String s = "UPDATED_AT";
            SyMUtil.getInstance();
            row2.set(s, (Object)SyMUtil.getCurrentTimeInMillis());
            final Row row3 = row;
            final String s2 = "SEQUENTIAL_COMMAND_ADDED_AT";
            SyMUtil.getInstance();
            row3.set(s2, (Object)SyMUtil.getCurrentTimeInMillis());
            this.statusDataObject.addRow(row);
        }
        else {
            final int commandStatus = (int)row.get("COMMAND_STATUS");
            if ((commandStatus != 3 && commandStatus != 120 && commandStatus != 180 && commandStatus != 190) || cmdStatus != 12) {
                row.set("EXECUTION_STATUS", (Object)order);
                row.set("COMMAND_STATUS", (Object)cmdStatus);
                row.set("UPDATED_AT", (Object)System.currentTimeMillis());
                this.statusDataObject.updateRow(row);
            }
        }
    }
    
    public void cleanUpSeqCmdForResource(final Long resourceID, final SequentialSubCommand sequentialSubCommand) throws DataAccessException {
        Criteria resourceDeleteCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria commandStatusDeleteCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "COMMAND_STATUS"), (Object)new int[] { 3, 180, 120, 16, 190 }, 8);
        resourceDeleteCriteria = resourceDeleteCriteria.and(commandStatusDeleteCriteria);
        this.statusDataObject.deleteRows("SequentialCmdExecutionStatus", resourceDeleteCriteria);
        this.cleanUpQueue.add(resourceID);
    }
    
    public void addToLoadQueue(final SequentialSubCommand sequentialSubCommand) {
        this.commandsToLoad.add(sequentialSubCommand);
    }
    
    public void loadAndCommitCommand() throws Exception {
        final Iterator iterator = this.commandsToLoad.iterator();
        MDMUtil.getPersistence().update(this.statusDataObject);
        this.callStatusUpdateHandlerInBulk(this.commandsToLoad);
        this.logger.log(Level.INFO, "[Seq] [Init] Seq DB level changes commited, going to add command for device. {0}", this.hashCode());
        while (iterator.hasNext()) {
            final SequentialSubCommand sequentialSubCommand = iterator.next();
            final long stime = System.currentTimeMillis();
            DeviceCommandRepository.getInstance().assignSeqSubCmdToDevices(sequentialSubCommand.CommandID, sequentialSubCommand.resourceID, 1);
            this.logger.log(Level.INFO, "[Seq] [Init] loadAndCommitCommand(): Assign command time taken - {0} got seq ID {1}", new Object[] { System.currentTimeMillis() - stime, sequentialSubCommand.CommandID });
        }
        this.logger.log(Level.INFO, "[Seq] [Init] Commands have been added for device. {0}", this.hashCode());
    }
    
    private void callStatusUpdateHandlerInBulk(final List<SequentialSubCommand> seqList) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)SeqCmdUtils.getInstance().getResList(seqList).toArray(), 8));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        Iterator iterator = seqList.iterator();
        final HashMap platformMap = new HashMap();
        while (iterator.hasNext()) {
            final SequentialSubCommand sequentialSubCommand = iterator.next();
            final Row devRow = dataObject.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)sequentialSubCommand.resourceID, 0));
            final Integer platform = (Integer)devRow.get("PLATFORM_TYPE");
            List platformList = platformMap.get(platform);
            if (platformList == null) {
                platformList = new ArrayList();
            }
            platformList.add(sequentialSubCommand);
            platformMap.put(platform, platformList);
        }
        iterator = platformMap.keySet().iterator();
        while (iterator.hasNext()) {
            SeqCmdStatusUpdateHandler handler = new BaseSeqCmdStatusUpdateHandler();
            final int platformType = iterator.next();
            if (platformType == 3) {
                handler = new WindowsSeqCmdStatusUpdateHandler();
            }
            else if (platformType == 1) {
                handler = new IOSSeqCmdStatusUpdateHandler();
            }
            final long stime = System.currentTimeMillis();
            handler.makeStatusUpdateforSubCommand(platformMap.get(platformType));
            this.logger.log(Level.INFO, "[Seq] [Init] callStatusUpdateHandlerInBulk():Time to callStatusUpdateHandlerInBulk - {0} hashcode  {1}", new Object[] { System.currentTimeMillis() - stime, this.hashCode() });
        }
    }
    
    private JSONObject getParams(final Long resID, final Long seqID) throws Exception {
        JSONObject jsonObject = null;
        final Criteria seqCriteria = new Criteria(Column.getColumn("SequentialCommandParams", "SEQUENTIAL_COMMAND_ID"), (Object)seqID, 0);
        final Criteria resCriteria = new Criteria(Column.getColumn("SequentialCommandParams", "RESOURCE_ID"), (Object)resID, 0);
        final Row row = this.statusDataObject.getRow("SequentialCommandParams", seqCriteria.and(resCriteria));
        if (row != null) {
            final String paramString = (String)row.get("PARAMS");
            if (paramString != null) {
                jsonObject = new JSONObject(paramString.toString());
            }
        }
        else {
            jsonObject = new JSONObject();
        }
        return jsonObject;
    }
    
    public void updateParams(final SequentialSubCommand sequentialSubCommand) throws DataAccessException {
        final JSONObject jsonObject = null;
        if (sequentialSubCommand != null) {
            final Criteria seqCriteria = new Criteria(Column.getColumn("SequentialCommandParams", "SEQUENTIAL_COMMAND_ID"), (Object)sequentialSubCommand.SequentialCommandID, 0);
            final Criteria resCriteria = new Criteria(Column.getColumn("SequentialCommandParams", "RESOURCE_ID"), (Object)sequentialSubCommand.resourceID, 0);
            Row row = this.statusDataObject.getRow("SequentialCommandParams", seqCriteria.and(resCriteria));
            if (row != null) {
                row.set("PARAMS", (Object)sequentialSubCommand.params);
                this.statusDataObject.updateRow(row);
            }
            else if (sequentialSubCommand.params.length() != 0) {
                row = new Row("SequentialCommandParams");
                row.set("RESOURCE_ID", (Object)sequentialSubCommand.resourceID);
                row.set("SEQUENTIAL_COMMAND_ID", (Object)sequentialSubCommand.SequentialCommandID);
                row.set("PARAMS", (Object)sequentialSubCommand.params);
                this.statusDataObject.addRow(row);
            }
        }
    }
    
    @Override
    public String toString() {
        final String toStr = "CleanUpList : " + this.cleanUpQueue.toString() + "  LoadList : " + this.commandsToLoad.toString();
        return toStr;
    }
}
