package com.zoho.mickey.db.postgres;

import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import org.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import com.zoho.mickey.db.RunningQueries;

public class PostgresRunningQueries extends RunningQueries
{
    @Override
    public void dumpQueryInformation() throws SQLException {
        String blockingQuery = "SELECT blocked_locks.pid      AS blocked_pid,         blocked_activity.usename    AS blocked_user,         blocking_locks.pid          AS blocking_pid,         blocking_activity.usename   AS blocking_user,         blocked_locks.mode          AS lock_mode,         blocked_activity.query      AS blocked_statement,         blocking_activity.query     AS current_statement_in_blocking_process   FROM  pg_catalog.pg_locks         blocked_locks    JOIN pg_catalog.pg_stat_activity blocked_activity  ON blocked_activity.pid = blocked_locks.pid    JOIN pg_catalog.pg_locks         blocking_locks        ON blocking_locks.locktype = blocked_locks.locktype        AND blocking_locks.DATABASE IS NOT DISTINCT FROM blocked_locks.DATABASE        AND blocking_locks.relation IS NOT DISTINCT FROM blocked_locks.relation        AND blocking_locks.page IS NOT DISTINCT FROM blocked_locks.page        AND blocking_locks.tuple IS NOT DISTINCT FROM blocked_locks.tuple        AND blocking_locks.virtualxid IS NOT DISTINCT FROM blocked_locks.virtualxid        AND blocking_locks.transactionid IS NOT DISTINCT FROM blocked_locks.transactionid        AND blocking_locks.classid IS NOT DISTINCT FROM blocked_locks.classid        AND blocking_locks.objid IS NOT DISTINCT FROM blocked_locks.objid        AND blocking_locks.objsubid IS NOT DISTINCT FROM blocked_locks.objsubid        AND blocking_locks.pid != blocked_locks.pid    JOIN pg_catalog.pg_stat_activity blocking_activity ON blocking_activity.pid = blocking_locks.pid   WHERE NOT blocked_locks.GRANTED;";
        blockingQuery = StringUtils.normalizeSpace(blockingQuery);
        try (final Connection conn = this.getConnection()) {
            final DatabaseMetaData dbm = conn.getMetaData();
            String runningQuery;
            if (Float.parseFloat(dbm.getDatabaseMajorVersion() + "." + dbm.getDatabaseMinorVersion()) >= 9.6f) {
                runningQuery = "SELECT datname AS database_name, pid, usename AS username, application_name, backend_start, xact_start, query_start, wait_event_type, wait_event, state, query from pg_stat_activity";
            }
            else {
                runningQuery = "SELECT datname AS database_name, pid, usename AS username, application_name, backend_start, xact_start, query_start, state_change, waiting, state, query from pg_stat_activity";
            }
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("Running_Queries", (Object)this.getAsJson(runningQuery, conn));
            jsonObject.put("Blocked_Query", (Object)this.getAsJson(blockingQuery, conn));
            this.dumpInformation(jsonObject);
        }
    }
}
