package com.zoho.mickey.db.postgres;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Range;
import com.zoho.mickey.db.AbstractSQLModifier;

public class PostgresSQLModifier extends AbstractSQLModifier
{
    @Override
    public String getSQLForSelectWithRange(final String sql, final Range range) throws QueryConstructionException {
        if (sql == null) {
            throw new QueryConstructionException("Incoming SQL String is null");
        }
        if (range == null) {
            return sql;
        }
        final StringBuilder buffer = new StringBuilder(sql);
        final int startIndex = range.getStartIndex();
        final int numOfRows = range.getNumberOfObjects();
        if (startIndex != 1 || numOfRows > 0) {
            buffer.append(" LIMIT ");
            buffer.append((numOfRows <= 0) ? "ALL" : String.valueOf(numOfRows));
            if (startIndex > 1) {
                buffer.append(" OFFSET ");
                buffer.append(startIndex - 1);
            }
        }
        return buffer.toString();
    }
}
