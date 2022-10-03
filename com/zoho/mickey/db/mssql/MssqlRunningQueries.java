package com.zoho.mickey.db.mssql;

import java.sql.SQLException;
import java.sql.Connection;
import org.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import com.zoho.mickey.db.RunningQueries;

public class MssqlRunningQueries extends RunningQueries
{
    @Override
    public void dumpQueryInformation() throws SQLException {
        String currentExecutingQueries = "SELECT SPid, sp.Status, LogiName, HostName, sesn.program_name ProgramName, blocked BlkBy, WaitTime, LastWaitType, cmd Command, cpu CPUTime, physical_io DiskIO, open_tran OpenTransactions, last_batch LastBatch, sesn.login_time LoginTime, DB_NAME(sp.dbid) DatabaseName, sesn.total_scheduled_time/1000 TotalScheduledTime, sesn.total_elapsed_time/1000 TotalElapsedTimeOfSession, sesn.last_request_start_time LastRequestStartTime, sesn.last_request_end_time LastRequestEndTime, sqltxt.text Query \tFROM master.sys.sysprocesses sp \t  cross apply sys.dm_exec_sql_text(sp.sql_handle) as sqltxt \t  cross apply sys.dm_exec_sessions as sesn \twhere sesn.session_id = sp.spid and sp.spid>50 ORDER BY sp.spid";
        currentExecutingQueries = StringUtils.normalizeSpace(currentExecutingQueries);
        try (final Connection conn = this.getConnection()) {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("Running_Queries", (Object)this.getAsJson(currentExecutingQueries, conn));
            this.dumpInformation(jsonObject);
        }
    }
}
