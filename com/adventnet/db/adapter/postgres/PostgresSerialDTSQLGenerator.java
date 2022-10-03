package com.adventnet.db.adapter.postgres;

import java.util.Properties;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.adapter.Ansi92SQLGenerator;
import com.adventnet.ds.query.Column;
import com.adventnet.db.adapter.DTSQLGenerator;

public class PostgresSerialDTSQLGenerator implements DTSQLGenerator
{
    @Override
    public String getDefaultValue(final Object defVal) {
        throw new IllegalArgumentException("Default value for SERIAL column is not supported.");
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
        if (value == null) {
            return null;
        }
        return value.toString();
    }
    
    @Override
    public String handleCaseSensitive(final String retVal, final boolean isCaseSensitive, final boolean isEncrypted, final Ansi92SQLGenerator.Clause columnBelongsToClause) {
        return retVal;
    }
    
    @Override
    public String getDBDataType(final ColumnDefinition colDef) {
        return "BIGSERIAL";
    }
    
    @Override
    public void initialize(final Properties properties) throws Exception {
    }
}
