package com.me.devicemanagement.framework.server.persistence;

import java.sql.SQLException;
import java.sql.Connection;
import com.adventnet.persistence.ReadOnlyPersistence;
import com.adventnet.persistence.Persistence;

public interface DMPersistenceAPI
{
    Persistence getPersistence();
    
    Persistence getPersistenceLite();
    
    ReadOnlyPersistence getCachedPersistence();
    
    ReadOnlyPersistence getReadOnlyPersistence();
    
    Connection getConnection() throws SQLException;
    
    Connection getReadOnlyConnection() throws Exception;
}
