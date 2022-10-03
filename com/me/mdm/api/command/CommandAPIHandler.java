package com.me.mdm.api.command;

import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.ds.query.SelectQuery;

public class CommandAPIHandler
{
    public SelectQuery getCommandHistoryQuery() {
        final SelectQuery commandHistoryQuery = new CommandStatusHandler().getCommandStatusTableQuery();
        commandHistoryQuery.addJoin(new Join("CommandHistory", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        commandHistoryQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_ID"));
        commandHistoryQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_UUID"));
        commandHistoryQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_TYPE"));
        commandHistoryQuery.addSortColumn(new SortColumn(Column.getColumn("CommandHistory", "ADDED_TIME"), false));
        return commandHistoryQuery;
    }
}
