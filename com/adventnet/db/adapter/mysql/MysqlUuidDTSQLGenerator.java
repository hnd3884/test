package com.adventnet.db.adapter.mysql;

import java.util.Properties;
import com.adventnet.db.persistence.metadata.IndexColumnDefinition;
import com.adventnet.db.adapter.Ansi92SQLGenerator;
import com.adventnet.ds.query.Column;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.adapter.DTSQLGenerator;

public class MysqlUuidDTSQLGenerator implements DTSQLGenerator
{
    @Override
    public String getDefaultValue(final Object defVal) {
        throw new IllegalArgumentException("Default value for UUID column is not supported in Mysql.");
    }
    
    @Override
    public String getDBDataType(final ColumnDefinition colDef) {
        return "BINARY(16)";
    }
    
    @Override
    public String getDTSpecificEncryptionString(final Column column, final String value) {
        if (value.equals("?") || value.startsWith("@")) {
            return "UNHEX(REPLACE(" + value + ",'-' ,''))";
        }
        return "UNHEX(REPLACE('" + value + "','-' ,''))";
    }
    
    @Override
    public String getDTSpecificDecryptionString(final Column column, final String value) {
        return column.isEncrypted() ? ("HEX(" + value + ")") : value;
    }
    
    @Override
    public String getValue(final Object value, final int comparator, final boolean isCaseSensitive) {
        String valueStr = value.toString();
        if (comparator == 14 || comparator == 15 || comparator == 8 || comparator == 9) {
            valueStr = "'" + valueStr + "'";
        }
        return valueStr;
    }
    
    @Override
    public String handleCaseSensitive(final String retVal, final boolean isCaseSensitive, final boolean isEncrypted, final Ansi92SQLGenerator.Clause columnBelongsToClause) {
        return retVal;
    }
    
    @Override
    public String getSQLForIndexColumn(final IndexColumnDefinition icd) {
        return null;
    }
    
    @Override
    public void initialize(final Properties properties) throws Exception {
    }
}
