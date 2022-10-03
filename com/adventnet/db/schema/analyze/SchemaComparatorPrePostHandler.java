package com.adventnet.db.schema.analyze;

public interface SchemaComparatorPrePostHandler
{
    void preHandle() throws Exception;
    
    void postHandle() throws Exception;
}
