package com.adventnet.db.adapter;

import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.List;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Column;
import java.util.Map;

public interface DCSQLGenerator
{
    void getSQLForInsert(final String p0, final Map<Column, Object> p1, final StringBuilder p2, final StringBuilder p3) throws QueryConstructionException;
    
    String getDCSpecificColumnName(final String p0, final String p1);
    
    String getDCSpecificColumnName(final String p0);
    
    String getSQLForCast(final String p0, final Column p1);
    
    String getDCDataType(final String p0);
    
    int getDCSQLType(final String p0);
    
    boolean isUniqueKeySupported();
    
    void initSQLGenerator(final SQLGenerator p0);
    
    Map getModifiedValueForUpdate(final String p0, final Map<Column, Object> p1) throws MetaDataException;
    
    String getSQLForArchiveTableCheckConstraint(final String p0, final List<ColumnDefinition> p1);
}
