package com.zoho.mickey.db;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Range;

public class AbstractSQLModifier implements SQLModifier
{
    @Override
    public String getSQLForSelectWithRange(final String sql, final Range range) throws QueryConstructionException {
        return sql;
    }
    
    @Override
    public String getSQLForUnionWithRange(final String sql, final Range range) throws QueryConstructionException {
        final StringBuilder buffer = new StringBuilder();
        if (range != null) {
            final String rangeSQL = this.getSQLForSelectWithRange(sql, range);
            buffer.append(rangeSQL);
        }
        return buffer.toString();
    }
}
