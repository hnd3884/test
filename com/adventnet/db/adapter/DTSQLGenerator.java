package com.adventnet.db.adapter;

import java.util.Properties;
import com.adventnet.db.persistence.metadata.IndexColumnDefinition;
import com.adventnet.ds.query.Column;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.zoho.mickey.Initializable;

public interface DTSQLGenerator extends Initializable
{
    String getDefaultValue(final Object p0);
    
    String getDBDataType(final ColumnDefinition p0);
    
    String getDTSpecificEncryptionString(final Column p0, final String p1);
    
    String getDTSpecificDecryptionString(final Column p0, final String p1);
    
    String getValue(final Object p0, final int p1, final boolean p2);
    
    String handleCaseSensitive(final String p0, final boolean p1, final boolean p2, final Ansi92SQLGenerator.Clause p3);
    
    default String getSQLForIndexColumn(final IndexColumnDefinition icd) {
        return null;
    }
    
    default void initialize(final Properties properties) throws Exception {
    }
}
