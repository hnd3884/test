package com.adventnet.db.migration.handler;

public interface DBMigrationPrePostHandler
{
    void preHandle() throws Exception;
    
    void postHandle() throws Exception;
}
