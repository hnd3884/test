package com.adventnet.db.api;

import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.misc.ShowStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.parser.TokenMgrError;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.parser.ParseException;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.ds.query.QueryConstructionException;

public class FilterReadOnlyQueries
{
    public static boolean isReadOnly(final String sql) throws QueryConstructionException {
        SwisSQLStatement sqlStmt = null;
        try {
            if (sql == null || sql.isEmpty()) {
                throw new QueryConstructionException("Query should not be empty or null");
            }
            sqlStmt = new SwisSQLAPI(sql).parse();
        }
        catch (final ParseException | ConvertException | TokenMgrError ex) {
            throw new QueryConstructionException("Provide a valid SQL string", ex);
        }
        if (!(sqlStmt instanceof SelectQueryStatement) && !(sqlStmt instanceof ShowStatement)) {
            throw new QueryConstructionException("Only SELECT and SHOW statements are allowed");
        }
        return Boolean.TRUE;
    }
}
