package com.zoho.mickey.db.mysql;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Range;
import com.zoho.mickey.db.AbstractSQLModifier;

public class MysqlSQLModifier extends AbstractSQLModifier
{
    @Override
    public String getSQLForSelectWithRange(final String sql, final Range range) throws QueryConstructionException {
        if (sql == null) {
            throw new QueryConstructionException("Incoming SQL String is null");
        }
        if (range == null) {
            return sql;
        }
        int startIndex = range.getStartIndex();
        final int numOfRows = range.getNumberOfObjects();
        final StringBuilder buff = new StringBuilder(sql);
        if (startIndex != 1 || numOfRows > 0) {
            startIndex = ((startIndex <= 0) ? 0 : (startIndex - 1));
            buff.append(" LIMIT ");
            buff.append(startIndex);
            buff.append(",");
            if (numOfRows > 0) {
                buff.append(numOfRows);
            }
            else {
                buff.append(Long.MAX_VALUE);
            }
        }
        return buff.toString();
    }
}
