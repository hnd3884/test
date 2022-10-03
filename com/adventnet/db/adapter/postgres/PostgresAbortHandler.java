package com.adventnet.db.adapter.postgres;

import java.sql.SQLException;
import org.postgresql.core.BaseConnection;
import java.util.concurrent.Executor;
import java.sql.Connection;
import com.zoho.cp.ConnectionAbortHandler;

public class PostgresAbortHandler implements ConnectionAbortHandler
{
    public void abort(final Connection connection, final Executor executor) throws SQLException {
        ((BaseConnection)connection).cancelQuery();
    }
}
