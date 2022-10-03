package com.adventnet.db.schema.analyze;

import org.json.JSONObject;
import org.json.JSONArray;

public interface SchemaComparatorHandler
{
    boolean compareTableSchema(final String p0);
    
    boolean ignoreTableRowCount(final String p0) throws Exception;
    
    void preInvoke(final String p0) throws Exception;
    
    boolean compareFKConstrains(final String p0);
    
    boolean compareUniqueConstraints(final String p0);
    
    boolean compareIndexes(final String p0);
    
    boolean comparePKColumns(final String p0);
    
    boolean compareColumns(final String p0);
    
    void postInvoke(final String p0, final JSONArray p1) throws Exception;
    
    void setComparatorType(final SchemaComparator.ComparatorType p0);
    
    boolean isDiffIgnorable(final String p0, final JSONObject p1);
}
