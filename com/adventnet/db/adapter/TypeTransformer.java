package com.adventnet.db.adapter;

import java.sql.SQLException;
import java.sql.ResultSetMetaData;

@FunctionalInterface
public interface TypeTransformer
{
    public static final TypeTransformer DEFAULT = (orginalType, alteredType, column, rsmd) -> alteredType;
    
    int getColumnType(final int p0, final int p1, final int p2, final ResultSetMetaData p3) throws SQLException;
}
