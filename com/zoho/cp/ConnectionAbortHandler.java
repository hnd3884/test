package com.zoho.cp;

import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.sql.Connection;

public interface ConnectionAbortHandler
{
    void abort(final Connection p0, final Executor p1) throws SQLException;
}
