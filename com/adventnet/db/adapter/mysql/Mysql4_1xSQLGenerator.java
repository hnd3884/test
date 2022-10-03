package com.adventnet.db.adapter.mysql;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.persistence.metadata.ColumnDefinition;

public class Mysql4_1xSQLGenerator extends MysqlSQLGenerator
{
    @Override
    protected String processColumnDefn(final ColumnDefinition colDefn) throws QueryConstructionException {
        final StringBuilder colBuffer = new StringBuilder(60);
        final String columnName = this.getDBSpecificColumnName(colDefn.getColumnName());
        final String dataType = colDefn.getDataType();
        final Object defaultVal = colDefn.getDefaultValue();
        final boolean nullable = colDefn.isNullable();
        colBuffer.append(columnName);
        colBuffer.append(" ");
        colBuffer.append(this.getDBDataType(colDefn));
        if (colDefn.getDataType().equals("SCHAR")) {
            colBuffer.append(" CHARACTER SET LATIN1");
        }
        if (defaultVal != null) {
            colBuffer.append(" DEFAULT " + this.getDefaultValue(dataType, defaultVal));
        }
        if (!nullable) {
            colBuffer.append(" NOT NULL");
        }
        return colBuffer.toString();
    }
}
