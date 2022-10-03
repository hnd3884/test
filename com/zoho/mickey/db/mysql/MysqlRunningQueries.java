package com.zoho.mickey.db.mysql;

import java.sql.SQLException;
import java.sql.Connection;
import org.json.JSONObject;
import com.zoho.mickey.db.RunningQueries;

public class MysqlRunningQueries extends RunningQueries
{
    @Override
    public void dumpQueryInformation() throws SQLException {
        final String query = "show full processlist";
        try (final Connection conn = this.getConnection()) {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("Running_Queries", (Object)this.getAsJson(query, conn));
            this.dumpInformation(jsonObject);
        }
    }
}
