package com.adventnet.db.adapter.mysql;

import java.util.Iterator;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.logging.Logger;

public class Mysql5v6xSQLGenerator extends MysqlSQLGenerator
{
    private static final Logger LOGGER;
    
    @Override
    protected void handleTimeStamp(final ColumnDefinition colDefn, final StringBuilder colBuffer) {
        String colDataType = "";
        if (DataTypeUtil.isEDT(colDefn.getDataType())) {
            colDataType = DataTypeManager.getDataTypeDefinition(colDefn.getDataType()).getBaseType();
        }
        else {
            colDataType = colDefn.getDataType();
        }
        if (colDataType.equalsIgnoreCase("TIMESTAMP") && colDefn.isNullable()) {
            colBuffer.append(" NULL ");
        }
        if (colDataType.equalsIgnoreCase("TIMESTAMP") && !colDefn.isNullable() && null == colDefn.defaultValue() && this.isTimeStampColumnPresentInTable(colDefn.getTableName())) {
            throw new IllegalArgumentException("Default value mandatory for TIMESTAMP column from MySQL 5.6 and above! (format: [1989-06-17 09:28:00]) For tables already containing one or more Timestamp column(s)");
        }
    }
    
    private boolean isTimeStampColumnPresentInTable(final String tableName) {
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException e) {
            Mysql5v6xSQLGenerator.LOGGER.warning("Table Definition not found for the table : [ " + tableName + " ]");
            e.printStackTrace();
        }
        if (null != td) {
            for (final String columnName : td.getColumnNames()) {
                if (td.getColumnType(columnName).equalsIgnoreCase("TIMESTAMP")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(Mysql5v6xSQLGenerator.class.getName());
    }
}
