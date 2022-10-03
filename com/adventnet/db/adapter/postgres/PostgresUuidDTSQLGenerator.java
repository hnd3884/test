package com.adventnet.db.adapter.postgres;

import java.util.Properties;
import com.adventnet.db.persistence.metadata.IndexColumnDefinition;
import com.adventnet.db.adapter.Ansi92SQLGenerator;
import com.adventnet.ds.query.Column;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.adapter.DTSQLGenerator;

public class PostgresUuidDTSQLGenerator implements DTSQLGenerator
{
    @Override
    public String getDefaultValue(final Object defVal) {
        throw new IllegalArgumentException("Default value for UUID column is not supported in Postgres.");
    }
    
    @Override
    public String getDBDataType(final ColumnDefinition colDef) {
        return "UUID";
    }
    
    @Override
    public String getDTSpecificEncryptionString(final Column column, final String value) {
        return value;
    }
    
    @Override
    public String getDTSpecificDecryptionString(final Column column, final String value) {
        return value;
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
