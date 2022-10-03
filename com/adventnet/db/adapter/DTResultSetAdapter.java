package com.adventnet.db.adapter;

import java.sql.SQLException;

public interface DTResultSetAdapter
{
    Object getValue(final ResultSetAdapter p0, final int p1, final String p2) throws SQLException;
    
    Object getValue(final ResultSetAdapter p0, final String p1, final String p2) throws SQLException;
}
