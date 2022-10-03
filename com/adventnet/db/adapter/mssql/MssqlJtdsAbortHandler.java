package com.adventnet.db.adapter.mssql;

import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.sql.Connection;
import com.zoho.cp.ConnectionAbortHandler;

public class MssqlJtdsAbortHandler implements ConnectionAbortHandler
{
    public void abort(final Connection connection, final Executor executor) throws SQLException {
        connection.close();
    }
}
