package com.me.mdm.server.command;

import com.adventnet.persistence.internal.UniqueValueHolder;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SortColumn;
import java.util.Collection;
import java.util.HashMap;
import org.json.JSONArray;
import java.util.List;
import com.me.mdm.server.audit.AuditDataHandler;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import org.json.JSONObject;
import java.util.logging.Logger;

public class CommandStatusHandler
{
    private Logger logger;
    
    public CommandStatusHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    private Long addOrUpdateCommandStatus(final JSONObject commandData) {
        DMSecurityLogger.info(this.logger, "CommandStatusHandler", "addOrUpdateCommandStatus", "Command Data : {0}", (Object)commandData);
        final Long resourceID = commandData.optLong("RESOURCE_ID");
        final Long commandID = commandData.optLong("COMMAND_ID");
        Long commandHistoryID = commandData.optLong("COMMAND_HISTORY_ID", -1L);
        final Long time = System.currentTimeMillis();
        int commandStatus = -1;
        try {
            if (commandHistoryID != null && resourceID != null && commandID != null) {
                DataObject commandStatusDO = null;
                if (commandHistoryID == null || commandHistoryID == -1L) {
                    commandStatusDO = MDMUtil.getPersistence().constructDataObject();
                    Row commandStatusRow = new Row("CommandHistory");
                    commandStatusRow.set("RESOURCE_ID", (Object)resourceID);
                    commandStatusRow.set("COMMAND_ID", (Object)commandID);
                    commandStatus = 1;
                    commandStatusRow.set("COMMAND_STATUS", (Object)commandStatus);
                    commandStatusRow.set("ADDED_TIME", (Object)time);
                    commandStatusRow.set("ADDED_BY", (Object)(commandData.has("ADDED_BY") ? Long.valueOf(commandData.getLong("ADDED_BY")) : null));
                    commandStatusRow.set("UPDATED_TIME", (Object)time);
                    commandStatusRow.set("REMARKS", (Object)commandData.optString("REMARKS", (String)null));
                    commandStatusRow.set("REMARKS_ARGS", (Object)commandData.optString("REMARKS_ARGS", (String)null));
                    commandStatusDO.addRow(commandStatusRow);
                    commandStatusDO = MDMUtil.getPersistence().add(commandStatusDO);
                    commandStatusRow = commandStatusDO.getFirstRow("CommandHistory");
                    commandHistoryID = (Long)commandStatusRow.get("COMMAND_HISTORY_ID");
                    commandData.put("COMMAND_HISTORY_ID", (Object)commandHistoryID);
                }
                else {
                    commandStatusDO = MDMUtil.getPersistence().get("CommandHistory", new Criteria(Column.getColumn("CommandHistory", "COMMAND_HISTORY_ID"), (Object)commandHistoryID, 0));
                    if (!commandStatusDO.isEmpty()) {
                        final Row commandStatusRow = commandStatusDO.getFirstRow("CommandHistory");
                        if (commandData.has("COMMAND_STATUS")) {
                            commandStatus = commandData.optInt("COMMAND_STATUS", 0);
                            commandStatusRow.set("COMMAND_STATUS", (Object)commandStatus);
                        }
                        commandStatusRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                        commandStatusRow.set("REMARKS", (Object)commandData.optString("REMARKS"));
                        commandStatusRow.set("REMARKS_ARGS", (Object)commandData.optString("REMARKS_ARGS"));
                        commandStatusDO.updateRow(commandStatusRow);
                        MDMUtil.getPersistence().update(commandStatusDO);
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while addOrUpdateCommandStatus", ex);
        }
        this.logger.log(Level.INFO, "CommandHistoryId:{0}", commandHistoryID);
        try {
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("UPDATED_TIME", (Object)time);
            requestJSON.put("COMMAND_STATUS", commandStatus);
            requestJSON.put("COMMAND_HISTORY_ID", (Object)commandHistoryID);
            this.populateCommandAudit(requestJSON);
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.WARNING, "Exception occurred while populateCommandAudit", e);
        }
        return commandHistoryID;
    }
    
    private void populateCommandAudit(final JSONObject requestJSON) throws DataAccessException {
        final Long commmandHistoryId = JSONUtil.optLongForUVH(requestJSON, "COMMAND_HISTORY_ID", Long.valueOf(-1L));
        final int commandStatus = requestJSON.optInt("COMMAND_STATUS", 0);
        final Long updatedTime = JSONUtil.optLongForUVH(requestJSON, "UPDATED_TIME", Long.valueOf(-1L));
        final Row row = new Row("CommandAuditLog");
        row.set("COMMAND_STATUS", (Object)commandStatus);
        row.set("COMMAND_HISTORY_ID", (Object)commmandHistoryId);
        row.set("UPDATED_TIME", (Object)updatedTime);
        final DataObject dataObject = (DataObject)new WritableDataObject();
        dataObject.addRow(row);
        MDMUtil.getPersistence().add(dataObject);
    }
    
    private void populateCommandAudit(final JSONObject requestJSON, final ArrayList commandHistoryList) throws DataAccessException {
        final int commandStatus = requestJSON.optInt("COMMAND_STATUS", 0);
        final Long updatedTime = JSONUtil.optLongForUVH(requestJSON, "UPDATED_TIME", Long.valueOf(-1L));
        final DataObject dataObject = (DataObject)new WritableDataObject();
        for (final Object commmandHistoryId : commandHistoryList) {
            final Row row = new Row("CommandAuditLog");
            row.set("COMMAND_STATUS", (Object)commandStatus);
            row.set("COMMAND_HISTORY_ID", (Object)commmandHistoryId);
            row.set("UPDATED_TIME", (Object)updatedTime);
            dataObject.addRow(row);
        }
        MDMUtil.getPersistence().add(dataObject);
    }
    
    public SelectQuery getCommandStatusTableQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CommandHistory"));
        final Join commandErrorJoin = new Join("CommandHistory", "CommandError", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 1);
        final Join cmdAuditRelJoin = new Join("CommandHistory", "CommandAuditRel", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 1);
        final Join auditInfoJoin = new Join("CommandAuditRel", "AuditInfo", new String[] { "AUDIT_ID" }, new String[] { "AUDIT_ID" }, 1);
        selectQuery.addJoin(commandErrorJoin);
        selectQuery.addJoin(cmdAuditRelJoin);
        selectQuery.addJoin(auditInfoJoin);
        selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "*"));
        selectQuery.addSelectColumn(Column.getColumn("CommandError", "*"));
        selectQuery.addSelectColumn(Column.getColumn("AuditInfo", "*"));
        selectQuery.addSelectColumn(Column.getColumn("CommandAuditRel", "*"));
        return selectQuery;
    }
    
    public void updateCommandStatus(final JSONObject commandData) {
        final Long resourceID = commandData.optLong("RESOURCE_ID");
        final Long commandID = commandData.optLong("COMMAND_ID");
        final int[] lastStatus = (int[])commandData.opt("PREVIOUS_STATUS");
        final int newStatus = commandData.optInt("COMMAND_STATUS");
        final String remarks = commandData.optString("REMARKS", (String)null);
        final String remarkArgs = commandData.optString("REMARKS_ARGS", (String)null);
        this.logger.log(Level.INFO, "Command status updated for {0}. Current Status:{1}", new Object[] { resourceID, newStatus });
        try {
            if (resourceID != null) {
                final Criteria resCrit = new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)resourceID, 0);
                final Criteria prevStatesCrit = new Criteria(Column.getColumn("CommandHistory", "COMMAND_STATUS"), (Object)lastStatus, 8);
                Criteria criteria = resCrit.and(prevStatesCrit);
                if (commandID != null) {
                    final Criteria cmdCrit = new Criteria(Column.getColumn("CommandHistory", "COMMAND_ID"), (Object)commandID, 0);
                    criteria = criteria.and(cmdCrit);
                }
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("CommandHistory");
                updateQuery.setUpdateColumn("COMMAND_STATUS", (Object)newStatus);
                updateQuery.setUpdateColumn("UPDATED_TIME", (Object)System.currentTimeMillis());
                updateQuery.setUpdateColumn("REMARKS", (Object)remarks);
                updateQuery.setUpdateColumn("REMARKS_ARGS", (Object)remarkArgs);
                updateQuery.setCriteria(criteria);
                MDMUtil.getPersistence().update(updateQuery);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while updateCommandStatus", ex);
        }
    }
    
    private void addOrUpdateCommandError(final JSONObject commandErrorData) {
        final Long cmdHistoryID = commandErrorData.optLong("COMMAND_HISTORY_ID");
        final Integer errorCode = commandErrorData.optInt("ERROR_CODE");
        this.logger.log(Level.INFO, "Error code :{0}; Command History Id:{1}", new Object[] { errorCode, cmdHistoryID });
        try {
            final DataObject cmdErrorDO = MDMUtil.getPersistence().get("CommandError", new Criteria(Column.getColumn("CommandError", "COMMAND_HISTORY_ID"), (Object)cmdHistoryID, 0));
            if (cmdErrorDO.isEmpty()) {
                final Row cmdErrorRow = new Row("CommandError");
                cmdErrorRow.set("COMMAND_HISTORY_ID", (Object)cmdHistoryID);
                cmdErrorRow.set("ERROR_CODE", (Object)errorCode);
                cmdErrorDO.addRow(cmdErrorRow);
                MDMUtil.getPersistence().add(cmdErrorDO);
            }
            else {
                final Row cmdErrorRow = cmdErrorDO.getFirstRow("CommandError");
                cmdErrorRow.set("ERROR_CODE", (Object)errorCode);
                cmdErrorDO.updateRow(cmdErrorRow);
                MDMUtil.getPersistence().update(cmdErrorDO);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while addOrUpdateCommandError", ex);
        }
    }
    
    public void addOrUpdateAuditForCommand(final JSONObject commandAuditData) {
        final Long commandHistoryId = commandAuditData.optLong("COMMAND_HISTORY_ID");
        try {
            final Criteria commandHistoryCriteria = new Criteria(Column.getColumn("CommandAuditRel", "COMMAND_HISTORY_ID"), (Object)commandHistoryId, 0);
            final DataObject auditDO = MDMUtil.getPersistence().get("CommandAuditRel", commandHistoryCriteria);
            if (auditDO.isEmpty()) {
                final Long auditID = new AuditDataHandler().addOrUpdateAuditInfo(commandAuditData);
                final Row auditCmdRelRow = new Row("CommandAuditRel");
                auditCmdRelRow.set("AUDIT_ID", (Object)auditID);
                auditCmdRelRow.set("COMMAND_HISTORY_ID", (Object)commandHistoryId);
                auditDO.addRow(auditCmdRelRow);
                MDMUtil.getPersistence().add(auditDO);
            }
            else {
                final Row auditCmdRelRow2 = auditDO.getFirstRow("CommandAuditRel");
                final Long auditID2 = (Long)auditCmdRelRow2.get("AUDIT_ID");
                commandAuditData.put("AUDIT_ID", (Object)auditID2);
                new AuditDataHandler().addOrUpdateAuditInfo(commandAuditData);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while addOrUpdateAuditForCommand", ex);
        }
    }
    
    public JSONObject populateBulkCommandStatusForDevices(final List<Long> resourceList, final Long commandID, final Long userId, final String remarks) {
        final JSONObject commandStatusJSON = new JSONObject();
        for (final Long resourceId : resourceList) {
            final JSONObject commandJSON = new JSONObject();
            final JSONArray commandsJSONArray = new JSONArray();
            commandJSON.put("RESOURCE_ID", (Object)resourceId);
            commandJSON.put("ADDED_BY", (Object)userId);
            commandJSON.put("COMMAND_ID", (Object)commandID);
            commandJSON.put("REMARKS", (Object)remarks);
            commandsJSONArray.put((Object)commandJSON);
            commandStatusJSON.put(String.valueOf(resourceId), (Object)commandsJSONArray);
        }
        final HashMap criteriaMap = new HashMap();
        final ArrayList<Long> cmdList = new ArrayList<Long>();
        cmdList.add(commandID);
        criteriaMap.put("COMMAND_ID", cmdList);
        return new CommandStatusHandler().populateCommandStatusForDevices(commandStatusJSON, criteriaMap);
    }
    
    public JSONObject populateCommandStatusForDevices(JSONObject commandStatusData, final HashMap criteriaList) {
        try {
            JSONObject statusJSONObject = new JSONObject();
            final Iterator<String> resourceIterator = commandStatusData.keys();
            final ArrayList<Long> resourceList = new ArrayList<Long>();
            while (resourceIterator.hasNext()) {
                resourceList.add(Long.valueOf(resourceIterator.next()));
            }
            Criteria criteria = new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final ArrayList<String> criteriaColumnList = new ArrayList<String>(criteriaList.keySet());
            for (final String s : criteriaColumnList) {
                final String criteriaColumn = s;
                switch (s) {
                    case "COMMAND_ID": {
                        final ArrayList<Long> tempList = criteriaList.get(criteriaColumn);
                        criteria = criteria.and(new Criteria(Column.getColumn("CommandHistory", "COMMAND_ID"), (Object)tempList.toArray(), 8));
                        continue;
                    }
                    case "COMMAND_HISTORY_ID": {
                        final ArrayList<Long> tempList = criteriaList.get(criteriaColumn);
                        criteria = criteria.and(new Criteria(Column.getColumn("CommandHistory", "COMMAND_HISTORY_ID"), (Object)tempList.toArray(), 8));
                        continue;
                    }
                }
            }
            final SelectQuery selectQuery = this.getCommandStatusTableQuery();
            selectQuery.setCriteria(criteria);
            DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            commandStatusData = this.addOrUpdateCommandStatusForDevices(commandStatusData, dataObject);
            this.addOrUpdateCommandErrorForDevices(commandStatusData, dataObject);
            this.addOrUpdateAuditForCommandForDevices(commandStatusData, dataObject);
            dataObject = MDMUtil.getPersistenceLite().update(dataObject);
            statusJSONObject = this.generateResponseJSONForCommandHistoryStatus(commandStatusData, dataObject);
            return statusJSONObject;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in populateCommandStatusForDevices()", e);
            return new JSONObject();
        }
    }
    
    public Long populateCommandStatus(final JSONObject commandStatusData) {
        Long commandHistoryID = -1L;
        try {
            final Long resourceID = commandStatusData.optLong("RESOURCE_ID", -1L);
            final Long commandID = commandStatusData.optLong("COMMAND_ID", -1L);
            final Integer errorCode = commandStatusData.optInt("ERROR_CODE", -1);
            final String message = commandStatusData.optString("AUDIT_MESSAGE", (String)null);
            if (resourceID != -1L && commandID != -1L) {
                commandHistoryID = this.addOrUpdateCommandStatus(commandStatusData);
                if (errorCode != null && errorCode != -1) {
                    this.addOrUpdateCommandError(commandStatusData);
                }
                if (message != null) {
                    this.addOrUpdateAuditForCommand(commandStatusData);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while populateCommandStatus", ex);
        }
        return commandHistoryID;
    }
    
    public JSONObject getRecentCommandInfo(final Long resourceID, final Long commandID) {
        final JSONObject commandInfo = new JSONObject();
        try {
            final Criteria resCrit = new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria commandCrit = new Criteria(Column.getColumn("CommandHistory", "COMMAND_ID"), (Object)commandID, 0);
            final SelectQuery selectQuery = this.getCommandStatusTableQuery();
            final Column updateTimeCol = Column.getColumn("CommandHistory", "UPDATED_TIME");
            final SortColumn updateSortCol = new SortColumn(updateTimeCol, false);
            selectQuery.addSortColumn(updateSortCol);
            selectQuery.setCriteria(resCrit.and(commandCrit));
            final DataObject commandDO = MDMUtil.getPersistence().get(selectQuery);
            if (!commandDO.isEmpty()) {
                final List tableList = commandDO.getTableNames();
                if (tableList.contains("CommandHistory")) {
                    final Row commandRow = commandDO.getFirstRow("CommandHistory");
                    if (commandRow != null) {
                        commandInfo.put("COMMAND_HISTORY_ID", commandRow.get("COMMAND_HISTORY_ID"));
                        commandInfo.put("COMMAND_ID", commandRow.get("COMMAND_ID"));
                        commandInfo.put("COMMAND_STATUS", commandRow.get("COMMAND_STATUS"));
                        commandInfo.put("RESOURCE_ID", commandRow.get("RESOURCE_ID"));
                        commandInfo.put("ADDED_TIME", commandRow.get("ADDED_TIME"));
                        commandInfo.put("ADDED_BY", commandRow.get("ADDED_BY"));
                        commandInfo.put("UPDATED_TIME", commandRow.get("UPDATED_TIME"));
                        commandInfo.put("REMARKS", commandRow.get("REMARKS"));
                        commandInfo.put("REMARKS_ARGS", commandRow.get("REMARKS_ARGS"));
                    }
                }
                if (tableList.contains("CommandError")) {
                    final Row commandErrRow = commandDO.getFirstRow("CommandError");
                    if (commandErrRow != null) {
                        commandInfo.put("ERROR_CODE", commandErrRow.get("ERROR_CODE"));
                    }
                }
                if (tableList.contains("AuditInfo")) {
                    final Row auditRow = commandDO.getFirstRow("AuditInfo");
                    if (auditRow != null) {
                        commandInfo.put("TICKET_ID", auditRow.get("TICKET_ID"));
                        commandInfo.put("AUDIT_MESSAGE", auditRow.get("AUDIT_MESSAGE"));
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getRecentCommandInfo", ex);
        }
        return commandInfo;
    }
    
    public JSONObject getRecentBulkCommandInfo(final List<Long> resourceList, final Long commandID) {
        JSONObject commandInfo = new JSONObject();
        final JSONObject commandDetailsJSON = new JSONObject();
        try {
            final Criteria resCrit = new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria commandCrit = new Criteria(Column.getColumn("CommandHistory", "COMMAND_ID"), (Object)commandID, 0);
            final SelectQuery selectQuery = this.getCommandStatusTableQuery();
            final Column updateTimeCol = Column.getColumn("CommandHistory", "UPDATED_TIME");
            final SortColumn updateSortCol = new SortColumn(updateTimeCol, false);
            selectQuery.addSortColumn(updateSortCol);
            selectQuery.setCriteria(resCrit.and(commandCrit));
            final DataObject commandDO = MDMUtil.getPersistence().get(selectQuery);
            if (!commandDO.isEmpty()) {
                commandInfo = new JSONObject();
                final JSONArray commandDetailsArray = new JSONArray();
                for (final Long resourceID : resourceList) {
                    final Row commandRow = commandDO.getRow("CommandHistory", new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)resourceID, 0));
                    Long cmdHisId = -1L;
                    if (commandRow != null) {
                        cmdHisId = Long.valueOf(commandRow.get("COMMAND_HISTORY_ID").toString());
                        commandInfo.put("COMMAND_HISTORY_ID", commandRow.get("COMMAND_HISTORY_ID"));
                        commandInfo.put("COMMAND_ID", commandRow.get("COMMAND_ID"));
                        commandInfo.put("COMMAND_STATUS", commandRow.get("COMMAND_STATUS"));
                        commandInfo.put("REMARKS", commandRow.get("REMARKS"));
                        commandInfo.put("RESOURCE_ID", commandRow.get("RESOURCE_ID"));
                        commandInfo.put("ADDED_TIME", commandRow.get("ADDED_TIME"));
                        commandInfo.put("ADDED_BY", commandRow.get("ADDED_BY"));
                        commandInfo.put("UPDATED_TIME", commandRow.get("UPDATED_TIME"));
                        commandInfo.put("REMARKS_ARGS", commandRow.get("REMARKS_ARGS"));
                    }
                    final Row commandErrRow = commandDO.getRow("CommandError", new Criteria(Column.getColumn("CommandError", "COMMAND_HISTORY_ID"), (Object)cmdHisId, 0));
                    if (commandErrRow != null) {
                        commandInfo.put("ERROR_CODE", commandErrRow.get("ERROR_CODE"));
                    }
                    final Row auditRow = commandDO.getRow("AuditInfo", new Criteria(Column.getColumn("CommandAuditRel", "COMMAND_HISTORY_ID"), (Object)cmdHisId, 0), new Join("AuditInfo", "CommandAuditRel", new String[] { "AUDIT_ID" }, new String[] { "AUDIT_ID" }, 2));
                    if (auditRow != null) {
                        commandInfo.put("TICKET_ID", auditRow.get("TICKET_ID"));
                        commandInfo.put("AUDIT_MESSAGE", auditRow.get("AUDIT_MESSAGE"));
                    }
                    commandDetailsArray.put((Object)commandInfo);
                    commandDetailsJSON.put(String.valueOf(resourceID), (Object)commandDetailsArray);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getRecentBulkCommandInfo", ex);
        }
        return commandDetailsJSON;
    }
    
    public JSONObject getRecentCommandBulkInfo(final DataObject commandDO) {
        final JSONObject commandDetailsJSON = new JSONObject();
        try {
            if (!commandDO.isEmpty()) {
                final Iterator<Row> iterator = commandDO.getRows("CommandHistory");
                while (iterator.hasNext()) {
                    final Row commandRow = iterator.next();
                    final JSONArray commandDetailsArray = new JSONArray();
                    final JSONObject commandInfo = new JSONObject();
                    Long resID = -1L;
                    Long cmdHisId = -1L;
                    if (commandRow != null) {
                        cmdHisId = Long.valueOf(commandRow.get("COMMAND_HISTORY_ID").toString());
                        commandInfo.put("COMMAND_HISTORY_ID", commandRow.get("COMMAND_HISTORY_ID"));
                        commandInfo.put("COMMAND_STATUS", commandRow.get("COMMAND_STATUS"));
                        commandInfo.put("COMMAND_ID", commandRow.get("COMMAND_ID"));
                        resID = Long.valueOf(commandRow.get("RESOURCE_ID").toString());
                        commandInfo.put("RESOURCE_ID", (Object)resID);
                        commandInfo.put("ADDED_TIME", commandRow.get("ADDED_TIME"));
                        commandInfo.put("ADDED_BY", commandRow.get("ADDED_BY"));
                        commandInfo.put("UPDATED_TIME", commandRow.get("UPDATED_TIME"));
                        commandInfo.put("REMARKS_ARGS", commandRow.get("REMARKS_ARGS"));
                    }
                    final Row commandErrRow = commandDO.getRow("CommandError", new Criteria(Column.getColumn("CommandError", "COMMAND_HISTORY_ID"), (Object)cmdHisId, 0));
                    if (commandErrRow != null) {
                        commandInfo.put("ERROR_CODE", commandErrRow.get("ERROR_CODE"));
                    }
                    final Row auditRow = commandDO.getRow("AuditInfo", new Criteria(Column.getColumn("CommandAuditRel", "COMMAND_HISTORY_ID"), (Object)cmdHisId, 0), new Join("AuditInfo", "CommandAuditRel", new String[] { "AUDIT_ID" }, new String[] { "AUDIT_ID" }, 2));
                    if (auditRow != null) {
                        commandInfo.put("TICKET_ID", auditRow.get("TICKET_ID"));
                        commandInfo.put("AUDIT_MESSAGE", auditRow.get("AUDIT_MESSAGE"));
                    }
                    commandDetailsArray.put((Object)commandInfo);
                    commandDetailsJSON.put(String.valueOf(resID), (Object)commandDetailsArray);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getBulkRecentCommandBulkInfo", ex);
        }
        return commandDetailsJSON;
    }
    
    public void updateCommandStatus(final JSONObject statusJSON, final ArrayList resourceList, final int currentStatus) throws DataAccessException, JSONException {
        final Long commandId = JSONUtil.optLongForUVH(statusJSON, "COMMAND_ID", (Long)null);
        statusJSON.put("UPDATED_TIME", MDMUtil.getCurrentTimeInMillis());
        ArrayList updatedCommandHistoryList = new ArrayList();
        if (resourceList != null && !resourceList.isEmpty()) {
            Criteria commandCriteria;
            if (commandId == null) {
                commandCriteria = new Criteria(Column.getColumn("CommandHistory", "COMMAND_ID"), (Object)null, 1);
            }
            else {
                commandCriteria = new Criteria(Column.getColumn("CommandHistory", "COMMAND_ID"), (Object)commandId, 0);
            }
            final Criteria resourceCriteria = new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria currentStatusCriteria = new Criteria(Column.getColumn("CommandHistory", "COMMAND_STATUS"), (Object)currentStatus, 0);
            final Criteria criteria = resourceCriteria.and(commandCriteria).and(currentStatusCriteria);
            updatedCommandHistoryList = this.updateCommandStatus(statusJSON, criteria);
        }
        this.populateCommandAudit(statusJSON, updatedCommandHistoryList);
    }
    
    public ArrayList updateCommandStatus(final JSONObject statusJSON, final Criteria criteria) throws DataAccessException, JSONException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("CommandHistory");
        updateQuery.setCriteria(criteria);
        if (statusJSON.has("COMMAND_STATUS")) {
            final int cmdStatus = statusJSON.getInt("COMMAND_STATUS");
            updateQuery.setUpdateColumn("COMMAND_STATUS", (Object)cmdStatus);
        }
        final String remarks = statusJSON.optString("REMARKS", (String)null);
        if (remarks != null) {
            updateQuery.setUpdateColumn("REMARKS", (Object)remarks);
        }
        final String remarkArgs = statusJSON.optString("REMARKS_ARGS", (String)null);
        if (remarkArgs != null) {
            updateQuery.setUpdateColumn("REMARKS_ARGS", (Object)remarkArgs);
        }
        final Long time = JSONUtil.optLongForUVH(statusJSON, "UPDATED_TIME", Long.valueOf(-1L));
        updateQuery.setUpdateColumn("UPDATED_TIME", (Object)time);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CommandHistory"));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "COMMAND_HISTORY_ID"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        ArrayList commandHistoryList = new ArrayList();
        if (dataObject != null && !dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("CommandHistory");
            commandHistoryList = (ArrayList)DBUtil.getColumnValuesAsList(iterator, "COMMAND_HISTORY_ID");
        }
        MDMUtil.getPersistence().update(updateQuery);
        return commandHistoryList;
    }
    
    private JSONObject addOrUpdateCommandStatusForDevices(final JSONObject commandDataJSON, final DataObject dataObject) {
        final Long addedTime = MDMUtil.getCurrentTimeInMillis();
        DMSecurityLogger.info(this.logger, "CommandStatusHandler", "addOrUpdateCommandStatusForDevices", "Command Data : {0}", (Object)commandDataJSON);
        try {
            final JSONObject responseJSON = new JSONObject();
            final Iterator<String> resourceIterator = commandDataJSON.keys();
            final ArrayList<Long> resourceList = new ArrayList<Long>();
            while (resourceIterator.hasNext()) {
                resourceList.add(Long.valueOf(resourceIterator.next()));
            }
            for (final Long resourceId : resourceList) {
                final JSONArray responseCommandsJSONArray = new JSONArray();
                final JSONArray commandsJSONArray = commandDataJSON.getJSONArray(resourceId.toString());
                for (int i = 0; i < commandsJSONArray.length(); ++i) {
                    final JSONObject commandJSON = commandsJSONArray.getJSONObject(i);
                    final Long commandHistoryId = JSONUtil.optLongForUVH(commandJSON, "COMMAND_HISTORY_ID", Long.valueOf(-1L));
                    final Long commandId = JSONUtil.optLongForUVH(commandJSON, "COMMAND_ID", Long.valueOf(-1L));
                    final int commandStatus = commandJSON.optInt("COMMAND_STATUS", 1);
                    final Long userId = JSONUtil.optLongForUVH(commandJSON, "ADDED_BY", Long.valueOf(-1L));
                    UniqueValueHolder commandHistoryUVH = null;
                    if (commandId != -1L) {
                        if (commandHistoryId == -1L) {
                            final Row commandStatusRow = new Row("CommandHistory");
                            commandStatusRow.set("RESOURCE_ID", (Object)resourceId);
                            commandStatusRow.set("COMMAND_ID", (Object)commandId);
                            commandStatusRow.set("COMMAND_STATUS", (Object)commandStatus);
                            commandStatusRow.set("ADDED_TIME", (Object)addedTime);
                            commandStatusRow.set("ADDED_BY", (Object)userId);
                            commandStatusRow.set("UPDATED_TIME", (Object)addedTime);
                            commandStatusRow.set("REMARKS", (Object)commandJSON.optString("REMARKS", (String)null));
                            commandStatusRow.set("REMARKS_ARGS", (Object)commandJSON.optString("REMARKS_ARGS", (String)null));
                            dataObject.addRow(commandStatusRow);
                            commandHistoryUVH = (UniqueValueHolder)commandStatusRow.get("COMMAND_HISTORY_ID");
                            commandJSON.put("COMMAND_HISTORY_ID", (Object)commandHistoryUVH);
                        }
                        else {
                            final Row commandStatusRow = dataObject.getRow("CommandHistory", new Criteria(Column.getColumn("CommandHistory", "COMMAND_HISTORY_ID"), (Object)commandHistoryId, 0));
                            commandStatusRow.set("UPDATED_TIME", (Object)addedTime);
                            commandStatusRow.set("REMARKS", (Object)commandJSON.optString("REMARKS"));
                            commandStatusRow.set("REMARKS_ARGS", (Object)commandJSON.optString("REMARKS_ARGS"));
                            if (commandJSON.optString("COMMAND_STATUS").equals(String.valueOf(6))) {
                                commandStatusRow.set("COMMAND_STATUS", (Object)commandJSON.optString("COMMAND_STATUS"));
                            }
                            dataObject.updateRow(commandStatusRow);
                        }
                    }
                    final Row commandAuditLogRow = new Row("CommandAuditLog");
                    commandAuditLogRow.set("COMMAND_STATUS", (Object)commandStatus);
                    commandAuditLogRow.set("UPDATED_TIME", (Object)addedTime);
                    if (commandHistoryUVH != null) {
                        commandAuditLogRow.set("COMMAND_HISTORY_ID", (Object)commandHistoryUVH);
                    }
                    else {
                        commandAuditLogRow.set("COMMAND_HISTORY_ID", (Object)commandHistoryId);
                    }
                    dataObject.addRow(commandAuditLogRow);
                    responseCommandsJSONArray.put((Object)commandJSON);
                }
                responseJSON.put(resourceId.toString(), (Object)responseCommandsJSONArray);
            }
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred during addition/updation of commandhistory -- ", e);
            return new JSONObject();
        }
    }
    
    private void addOrUpdateCommandErrorForDevices(final JSONObject commandStatusJSON, final DataObject dataObject) {
        this.logger.log(Level.INFO, "Error addOrUpdateCommandErrorForDevices commandStatusJSON :{0}", new Object[] { commandStatusJSON.toString() });
        try {
            final Iterator<String> resourceIterator = commandStatusJSON.keys();
            final ArrayList<Long> resourceList = new ArrayList<Long>();
            while (resourceIterator.hasNext()) {
                resourceList.add(Long.valueOf(resourceIterator.next()));
            }
            for (final Long resourceId : resourceList) {
                final JSONArray commandsJSONArray = commandStatusJSON.getJSONArray(resourceId.toString());
                for (int i = 0; i < commandsJSONArray.length(); ++i) {
                    final JSONObject commandJSON = commandsJSONArray.getJSONObject(i);
                    final Object commandHistoryId = commandJSON.get("COMMAND_HISTORY_ID");
                    final int errorCode = commandJSON.optInt("ERROR_CODE", 0);
                    if (errorCode != 0) {
                        final Row commandErrorRow = dataObject.getRow("CommandError", new Criteria(Column.getColumn("CommandError", "COMMAND_HISTORY_ID"), commandHistoryId, 0));
                        if (commandErrorRow != null) {
                            commandErrorRow.set("COMMAND_HISTORY_ID", commandHistoryId);
                            commandErrorRow.set("ERROR_CODE", (Object)errorCode);
                            dataObject.addRow(commandErrorRow);
                        }
                        else {
                            commandErrorRow.set("ERROR_CODE", (Object)errorCode);
                            dataObject.updateRow(commandErrorRow);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occurred while addOrUpdateCommandErrorForDevices", e);
        }
    }
    
    public void addOrUpdateAuditForCommandForDevices(final JSONObject commandAuditDataJSON, final DataObject dataObject) {
        this.logger.log(Level.INFO, "addOrUpdateAuditForCommandForDevices commandAuditDataJSON :{0}", new Object[] { commandAuditDataJSON.toString() });
        try {
            final Iterator<String> resourceIterator = commandAuditDataJSON.keys();
            final ArrayList<Long> resourceList = new ArrayList<Long>();
            while (resourceIterator.hasNext()) {
                resourceList.add(Long.valueOf(resourceIterator.next()));
            }
            for (final Long resourceId : resourceList) {
                final JSONArray commandsJSONArray = commandAuditDataJSON.getJSONArray(resourceId.toString());
                for (int i = 0; i < commandsJSONArray.length(); ++i) {
                    final JSONObject commandJSON = commandsJSONArray.getJSONObject(i);
                    final Object commandHistoryId = commandJSON.get("COMMAND_HISTORY_ID");
                    final String auditMessage = commandJSON.optString("AUDIT_MESSAGE", (String)null);
                    if (!MDMUtil.isStringEmpty(auditMessage)) {
                        Row commandAuditRelRow = dataObject.getRow("CommandAuditRel", new Criteria(Column.getColumn("CommandAuditRel", "COMMAND_HISTORY_ID"), commandHistoryId, 0));
                        if (commandAuditRelRow == null) {
                            final Row auditInfoRow = new Row("AuditInfo");
                            auditInfoRow.set("TICKET_ID", (Object)commandJSON.optString("TICKET_ID", (String)null));
                            auditInfoRow.set("AUDIT_MESSAGE", (Object)auditMessage);
                            dataObject.addRow(auditInfoRow);
                            final UniqueValueHolder auditIdUVH = (UniqueValueHolder)auditInfoRow.get("AUDIT_ID");
                            commandAuditRelRow = new Row("CommandAuditRel");
                            commandAuditRelRow.set("COMMAND_HISTORY_ID", commandHistoryId);
                            commandAuditRelRow.set("AUDIT_ID", (Object)auditIdUVH);
                            dataObject.addRow(commandAuditRelRow);
                        }
                        else {
                            final Long auditId = (Long)commandAuditRelRow.get("AUDIT_ID");
                            final Row auditInfoRow2 = dataObject.getRow("AuditInfo", new Criteria(Column.getColumn("AuditInfo", "AUDIT_ID"), (Object)auditId, 0));
                            auditInfoRow2.set("TICKET_ID", (Object)commandJSON.optString("TICKET_ID", (String)null));
                            auditInfoRow2.set("AUDIT_MESSAGE", (Object)auditMessage);
                            dataObject.updateRow(auditInfoRow2);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occurred while addOrUpdateAuditForCommandForDevices", e);
        }
    }
    
    public JSONObject generateResponseJSONForCommandHistoryStatus(final JSONObject commandStatusJSON, final DataObject dataObject) {
        try {
            final JSONObject responseJSON = new JSONObject();
            final Iterator<String> resourceIterator = commandStatusJSON.keys();
            final ArrayList<Long> resourceList = new ArrayList<Long>();
            while (resourceIterator.hasNext()) {
                resourceList.add(Long.valueOf(resourceIterator.next()));
            }
            for (final Long resourceId : resourceList) {
                final JSONArray responseCommandsJSONArray = new JSONArray();
                final JSONArray commandsJSONArray = commandStatusJSON.getJSONArray(resourceId.toString());
                for (int i = 0; i < commandsJSONArray.length(); ++i) {
                    final JSONObject commandJSON = commandsJSONArray.getJSONObject(i);
                    final String cmdHistoryIdUVH = commandJSON.optString("COMMAND_HISTORY_ID");
                    Long commandHistoryId = -1L;
                    if (cmdHistoryIdUVH.indexOf("[") == -1) {
                        commandHistoryId = Long.valueOf(cmdHistoryIdUVH);
                    }
                    else {
                        commandHistoryId = Long.valueOf(cmdHistoryIdUVH.substring(cmdHistoryIdUVH.indexOf("[") + 1, cmdHistoryIdUVH.indexOf("]")));
                    }
                    commandJSON.put("COMMAND_HISTORY_ID", (Object)commandHistoryId);
                    responseCommandsJSONArray.put((Object)commandJSON);
                }
                responseJSON.put(resourceId.toString(), (Object)responseCommandsJSONArray);
            }
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in generateResponseJSONForCommandHistoryStatus() ", e);
            return new JSONObject();
        }
    }
}
