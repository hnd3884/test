package com.me.mdm.server.seqcommands;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import org.json.JSONObject;
import java.util.logging.Logger;

public class BaseSeqCmdResponseHandler implements SeqCmdResponseHandler
{
    public Logger logger;
    
    public BaseSeqCmdResponseHandler() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
    
    @Override
    public Long onSuccess(final JSONObject params) throws Exception {
        final SequentialSubCommand sequentialSubCommand = SeqCmdDBUtil.getInstance().getNextSubCommand(params.getLong("resourceID"));
        if (sequentialSubCommand != SeqCmdConstants.NO_NEXT_SUB_COMMAND) {
            return sequentialSubCommand.CommandID;
        }
        return SeqCmdConstants.NO_NEXT_COMMAND;
    }
    
    @Override
    public Long onFailure(final JSONObject params) throws Exception {
        return SeqCmdConstants.ABORT_COMMAND;
    }
    
    @Override
    public Long retry(final JSONObject params) throws Exception {
        return DeviceCommandRepository.getInstance().getCommandID(String.valueOf(params.get("commandUUID")));
    }
    
    @Override
    public JSONObject processLater(final JSONObject params) throws Exception {
        throw new Exception("Operation Not Supported");
    }
    
    @Override
    public boolean setParams(final Long resource_id, final JSONObject param) {
        final SequentialSubCommand subCommand = SeqCmdUtils.getInstance().getCurrentSeqCmdOfResource(resource_id);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCommandParams"));
        selectQuery.addSelectColumn(new Column("SequentialCommandParams", "*"));
        final Criteria resCriteria = new Criteria(new Column("SequentialCommandParams", "RESOURCE_ID"), (Object)resource_id, 0);
        final Criteria cmdCriteria = new Criteria(new Column("SequentialCommandParams", "SEQUENTIAL_COMMAND_ID"), (Object)subCommand.SequentialCommandID, 0);
        selectQuery.setCriteria(resCriteria.and(cmdCriteria));
        try {
            DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject.isEmpty()) {
                dataObject = (DataObject)new WritableDataObject();
                final Row r = new Row("SequentialCommandParams");
                r.set("RESOURCE_ID", (Object)resource_id);
                r.set("SEQUENTIAL_COMMAND_ID", (Object)subCommand.SequentialCommandID);
                r.set("PARAMS", (Object)param.toString());
                dataObject.addRow(r);
            }
            else {
                final Row row = dataObject.getFirstRow("SequentialCommandParams");
                row.set("PARAMS", (Object)param.toString());
                dataObject.updateRow(row);
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Data Access exception in Set Params: ", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Data Access exception in Set Params: ", e2);
        }
        return false;
    }
    
    @Override
    public boolean subCommandPreProcessor(final Long resourceID, final Long commandID, final SequentialSubCommand sequentialSubCommand) {
        return true;
    }
    
    @Override
    public Long notNow(final JSONObject params) throws Exception {
        return DeviceCommandRepository.getInstance().getCommandID(String.valueOf(params.get("commandUUID")));
    }
}
