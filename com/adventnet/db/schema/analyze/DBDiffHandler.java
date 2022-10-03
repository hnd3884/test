package com.adventnet.db.schema.analyze;

public interface DBDiffHandler
{
    void compareSchema(final String p0, final String p1) throws Exception;
    
    void compareSchemaVsTableDef() throws Exception;
}
