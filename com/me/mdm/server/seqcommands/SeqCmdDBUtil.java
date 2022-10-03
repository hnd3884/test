package com.me.mdm.server.seqcommands;

import org.json.JSONException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class SeqCmdDBUtil
{
    public static SeqCmdDBUtil sequentialCommandDBUtil;
    public Logger logger;
    private String seperator;
    
    public SeqCmdDBUtil() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
        this.seperator = "\t";
    }
    
    public static SeqCmdDBUtil getInstance() {
        if (SeqCmdDBUtil.sequentialCommandDBUtil == null) {
            SeqCmdDBUtil.sequentialCommandDBUtil = new SeqCmdDBUtil();
        }
        return SeqCmdDBUtil.sequentialCommandDBUtil;
    }
    
    public void addSequentialCommands(final JSONObject sequentialCommands) throws Exception {
        this.logger.log(Level.INFO, "addSequentialCommandsCommand(): sequentialCommands : {0}", sequentialCommands);
        final JSONArray commands = sequentialCommands.getJSONArray("SequentialCommands");
        for (int i = 0; i < commands.length(); ++i) {
            final JSONObject sequentialCommand = commands.getJSONObject(i);
            final String sequentialCommandId = String.valueOf(sequentialCommand.get("SequentialCommandId"));
            final Long timeout = sequentialCommand.getLong("timeout");
            final JSONObject params = sequentialCommand.optJSONObject("params");
            final Boolean allowImmediateProcessing = sequentialCommand.getBoolean("allowImmediateProcessing");
            final JSONArray subCommands = sequentialCommand.getJSONArray("subCommands");
            final Row row = new Row("MdCommandToSequentialCommand");
            row.set(2, (Object)Long.valueOf(String.valueOf(sequentialCommand.get("basecmdID"))));
            row.set(1, (Object)Long.valueOf(sequentialCommandId));
            row.set("ALLOW_IMMEDIATE_PROCESSING", (Object)allowImmediateProcessing);
            row.set("TIMEOUT", (Object)timeout);
            final String paramString = (params == null) ? null : params.toString();
            row.set("PARAMS", (Object)paramString);
            final WritableDataObject d = new WritableDataObject();
            d.addRow(row);
            for (int j = 0; j < subCommands.length(); ++j) {
                final JSONObject temp = subCommands.getJSONObject(j);
                final Row r = new Row("MdSequentialCommands");
                r.set(1, (Object)Long.valueOf(sequentialCommandId));
                r.set(2, (Object)Long.valueOf(String.valueOf(temp.get("cmd_id"))));
                r.set(3, (Object)Integer.valueOf(String.valueOf(temp.get("order"))));
                r.set(4, (Object)String.valueOf(temp.get("handler")));
                d.addRow(r);
            }
            MDMUtil.getPersistence().add((DataObject)d);
            this.logger.log(Level.INFO, "Sequential Command {0} Added Successfully", sequentialCommandId);
        }
    }
    
    protected void addorUpdateSeqcmdStatusAndQueue(final List<Long> resourceList, final int order, final List<Long> seqentialcmdList, final int cmdStatus) {
        if (resourceList.isEmpty() || seqentialcmdList.isEmpty()) {
            return;
        }
        final Table table = new Table("SequentialCmdExecutionStatus");
        final SelectQuery updateStatusQuery = (SelectQuery)new SelectQueryImpl(table);
        final Column all = new Column("SequentialCmdExecutionStatus", "*");
        Criteria resourceCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceList.toArray(new Long[resourceList.size()]), 8);
        final Criteria commandCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "SEQUENTIAL_COMMAND_ID"), (Object)seqentialcmdList.toArray(new Long[seqentialcmdList.size()]), 8);
        resourceCriteria = resourceCriteria.and(commandCriteria);
        updateStatusQuery.addSelectColumn(all);
        updateStatusQuery.setCriteria(resourceCriteria);
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(updateStatusQuery);
            for (final Long resourceID : resourceList) {
                for (final Long sequentialcmdID : seqentialcmdList) {
                    Row row = null;
                    final Criteria resourceIDCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
                    final Criteria commandIDCriteria = new Criteria(new Column("SequentialCmdExecutionStatus", "SEQUENTIAL_COMMAND_ID"), (Object)sequentialcmdID, 0);
                    row = dataObject.getRow("SequentialCmdExecutionStatus", resourceIDCriteria.and(commandIDCriteria));
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
                        dataObject.addRow(row);
                    }
                    else {
                        final int commandStatus = (int)row.get("COMMAND_STATUS");
                        if ((commandStatus == 3 || commandStatus == 120 || commandStatus == 180 || commandStatus == 190) && cmdStatus == 12) {
                            continue;
                        }
                        row.set("EXECUTION_STATUS", (Object)order);
                        row.set("COMMAND_STATUS", (Object)cmdStatus);
                        row.set("UPDATED_AT", (Object)System.currentTimeMillis());
                        dataObject.updateRow(row);
                    }
                }
            }
            MDMUtil.getPersistence().update(dataObject);
            this.logger.log(Level.INFO, "Updated Sequential Command Status {0} {1} {2} {3}", new Object[] { resourceList, order, seqentialcmdList, cmdStatus });
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "addorupdateSequentialcommandstatus(): Status of sequential command was not updated for {0} commands : {1} {2}", new Object[] { resourceList, seqentialcmdList, e });
        }
    }
    
    public void addorUpdateSeqcmdStatusAndQueue(final Long resourceID, final int order, final Long seqentialcmdID, final int cmdStatus) {
        final List resourceList = new ArrayList();
        final List commandList = new ArrayList();
        resourceList.add(resourceID);
        commandList.add(seqentialcmdID);
        this.addorUpdateSeqcmdStatusAndQueue(resourceList, order, commandList, cmdStatus);
    }
    
    protected SequentialSubCommand getNextSubCommand(final Long resourceID) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCmdExecutionStatus"));
        selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "MdSequentialCommands", new String[] { "SEQUENTIAL_COMMAND_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID" }, 2));
        final Criteria statusCriteria = SeqCmdUtils.getInstance().getSeqCmdInprogressCriteria();
        final Criteria resourceCriteria = new Criteria(Column.getColumn("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria nextCmdCriteria = new Criteria(Column.getColumn("SequentialCmdExecutionStatus", "EXECUTION_STATUS"), (Object)Column.getColumn("MdSequentialCommands", "ORDER"), 7);
        selectQuery.setCriteria(statusCriteria.and(resourceCriteria).and(nextCmdCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdSequentialCommands", "*"));
        final SortColumn sortColumn = new SortColumn(new Column("MdSequentialCommands", "ORDER"), true);
        selectQuery.addSortColumn(sortColumn);
        try {
            final DataObject d = MDMUtil.getPersistence().get(selectQuery);
            if (d.isEmpty()) {
                return SeqCmdConstants.NO_NEXT_SUB_COMMAND;
            }
            final Row row = d.getFirstRow("MdSequentialCommands");
            final SequentialSubCommand sequentialSubCommand = new SequentialSubCommand();
            sequentialSubCommand.CommandID = (Long)row.get("COMMAND_ID");
            sequentialSubCommand.SequentialCommandID = (Long)row.get("SEQUENTIAL_COMMAND_ID");
            sequentialSubCommand.order = (int)row.get("ORDER");
            sequentialSubCommand.Handler = (String)row.get("HANDLER");
            return sequentialSubCommand;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in getNextSubCommand : Next Command Could not be fetched :", e);
            return SeqCmdConstants.NO_NEXT_SUB_COMMAND;
        }
    }
    
    protected SequentialSubCommand getSequentialSubCommand(final Long resourceId, final Long commandId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCmdExecutionStatus"));
            selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "MdSequentialCommands", new String[] { "SEQUENTIAL_COMMAND_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID" }, 2));
            final Criteria statusCriteria = SeqCmdUtils.getInstance().getSeqCmdInprogressCriteria();
            final Criteria resourceCriteria = new Criteria(Column.getColumn("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria commandCriteria = new Criteria(new Column("MdSequentialCommands", "COMMAND_ID"), (Object)commandId, 0);
            selectQuery.setCriteria(statusCriteria.and(resourceCriteria).and(commandCriteria));
            selectQuery.addSelectColumn(new Column("MdSequentialCommands", "*"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (dataObject.isEmpty()) {
                return SeqCmdConstants.NO_NEXT_SUB_COMMAND;
            }
            final Row row = dataObject.getFirstRow("MdSequentialCommands");
            final SequentialSubCommand sequentialSubCommand = new SequentialSubCommand();
            sequentialSubCommand.CommandID = (Long)row.get("COMMAND_ID");
            sequentialSubCommand.SequentialCommandID = (Long)row.get("SEQUENTIAL_COMMAND_ID");
            sequentialSubCommand.order = (int)row.get("ORDER");
            sequentialSubCommand.Handler = (String)row.get("HANDLER");
            return sequentialSubCommand;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in getting sequential subcommand", (Throwable)e);
            return SeqCmdConstants.NO_NEXT_SUB_COMMAND;
        }
    }
    
    private Long getSequentialCommandTimeout(final Long sequentialCommandID) {
        try {
            return (Long)DBUtil.getValueFromDB("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID", (Object)sequentialCommandID, "TIMEOUT");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Error in retrieving timeout for :{0} :{1}", new Object[] { sequentialCommandID, e });
            return null;
        }
    }
    
    public void suspendAnyStallingSequentialCommandsForResource(final Long resourceID) throws DataAccessException {
        final DataObject dataObject = SeqCmdUtils.getInstance().getSuspentionDataForResource(resourceID);
        final Row execStatusRow = dataObject.getRow("SequentialCmdExecutionStatus");
        if (execStatusRow == null) {
            return;
        }
        final int status = (int)execStatusRow.get("COMMAND_STATUS");
        final Long SequentialCommandID = (Long)execStatusRow.get("SEQUENTIAL_COMMAND_ID");
        final Integer order = (Integer)execStatusRow.get("EXECUTION_STATUS");
        final Long seqUpdatedAt = (Long)execStatusRow.get("UPDATED_AT");
        if (status == 120) {
            return;
        }
        try {
            final Row row = dataObject.getRow("MdCommandsToDevice");
            final Row timeoutRow = dataObject.getRow("MdCommandToSequentialCommand");
            final Long timeOut = (Long)timeoutRow.get("TIMEOUT");
            if (row != null) {
                final Long updatedAt = (Long)row.get("UPDATED_AT");
                final int subCommandStatus = (int)row.get("RESOURCE_COMMAND_STATUS");
                final Long commmandID = (Long)row.get("COMMAND_ID");
                if (subCommandStatus == 3) {
                    SyMUtil.getInstance();
                    if (SyMUtil.getCurrentTimeInMillis() - updatedAt > timeOut) {
                        this.logger.log(Level.INFO, "Suspending Sequential Command {0} for Resource {1} in subcommand ID {2} Reason : command Sent No response and Seq Cmd timed out", new Object[] { SequentialCommandID, resourceID, commmandID });
                        this.addorUpdateSeqcmdStatusAndQueue(resourceID, order, SequentialCommandID, 5);
                    }
                }
            }
            else if (status != 180 && status != 190) {
                this.logger.log(Level.INFO, "Suspending Sequential Command {0} for Resource {1} in subcommand ID {2} Reason : Command not found in MDCommandToDevice", new Object[] { SequentialCommandID, resourceID });
                this.addorUpdateSeqcmdStatusAndQueue(resourceID, order, SequentialCommandID, 5);
            }
            else {
                SyMUtil.getInstance();
                if (SyMUtil.getCurrentTimeInMillis() - seqUpdatedAt > timeOut && status != 190) {
                    this.logger.log(Level.INFO, "Suspending Sequential Command {0} for Resource {1} in subcommand ID {2} Reason : Too Long in Queue(global timeout)", new Object[] { SequentialCommandID, resourceID });
                    this.addorUpdateSeqcmdStatusAndQueue(resourceID, order, SequentialCommandID, 5);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Error in fetching timeout : ", (Throwable)e);
        }
    }
    
    public Object getCmdScopeParamforResource(final Long resID, final String key) {
        Object value = null;
        try {
            final JSONObject params = this.getParams(resID);
            final JSONObject cmdScopeParams = params.optJSONObject("cmdScopeParams");
            value = cmdScopeParams.opt(key);
            this.logger.log(Level.INFO, "params queried: params\n{0}ResID, key : {1}  {2}", new Object[] { params, resID, key });
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "exception in getting cmd Scope param ", e);
        }
        return value;
    }
    
    public JSONObject getParams(final Long resource_id) {
        final Table table = new Table("SequentialCmdExecutionStatus");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
        selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "SequentialCommandParams", new String[] { "SEQUENTIAL_COMMAND_ID", "RESOURCE_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID", "RESOURCE_ID" }, 2));
        final Column column = new Column("SequentialCommandParams", "*");
        final Criteria criteria = new Criteria(new Column("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resource_id, 0);
        final Criteria cmdCriteria = SeqCmdUtils.getInstance().getSeqCmdInprogressCriteria();
        selectQuery.setCriteria(criteria.and(cmdCriteria));
        selectQuery.addSelectColumn(column);
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row r = dataObject.getFirstRow("SequentialCommandParams");
                final String paramString = (String)r.get("PARAMS");
                if (paramString != null) {
                    final JSONObject params = new JSONObject(paramString.toString());
                    return params;
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Data Access Exception in getParams() : The params couldn't be fetched this may have lead to failure of seqcommand ", (Throwable)e);
        }
        catch (final JSONException e2) {
            this.logger.log(Level.WARNING, "Error in Json structure", (Throwable)e2);
        }
        return new JSONObject();
    }
    
    static {
        SeqCmdDBUtil.sequentialCommandDBUtil = null;
    }
}
