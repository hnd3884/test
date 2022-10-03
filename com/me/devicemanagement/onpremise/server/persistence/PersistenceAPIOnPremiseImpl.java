package com.me.devicemanagement.onpremise.server.persistence;

import java.sql.SQLException;
import com.adventnet.db.api.RelationalAPI;
import java.sql.Connection;
import com.adventnet.persistence.ReadOnlyPersistence;
import java.util.logging.Level;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.persistence.DMPersistenceAPI;

public class PersistenceAPIOnPremiseImpl implements DMPersistenceAPI
{
    private static final Logger LOGGER;
    
    public Persistence getPersistence() {
        Persistence persistence = null;
        try {
            persistence = (Persistence)BeanUtil.lookup("Persistence");
            return persistence;
        }
        catch (final Exception ex) {
            PersistenceAPIOnPremiseImpl.LOGGER.log(Level.SEVERE, "Exception while looking up bean for on-premise Persistence..", ex);
            return null;
        }
    }
    
    public Persistence getPersistenceLite() {
        Persistence persistenceLite = null;
        try {
            persistenceLite = (Persistence)BeanUtil.lookup("PersistenceLite");
            return persistenceLite;
        }
        catch (final Exception ex) {
            PersistenceAPIOnPremiseImpl.LOGGER.log(Level.SEVERE, "Exception while looking up bean for on-premise PersistenceLite.. ", ex);
            return null;
        }
    }
    
    public ReadOnlyPersistence getCachedPersistence() {
        ReadOnlyPersistence cachedPersistence = null;
        try {
            cachedPersistence = (ReadOnlyPersistence)BeanUtil.lookup("CachedPersistence");
            return cachedPersistence;
        }
        catch (final Exception ex) {
            PersistenceAPIOnPremiseImpl.LOGGER.log(Level.SEVERE, "Exception while looking up bean for on-premise CachedPersistence..", ex);
            return null;
        }
    }
    
    public ReadOnlyPersistence getReadOnlyPersistence() {
        ReadOnlyPersistence readOnlyPersistence = null;
        try {
            readOnlyPersistence = (ReadOnlyPersistence)BeanUtil.lookup("ReadOnlyPersistence");
            return readOnlyPersistence;
        }
        catch (final Exception ex) {
            PersistenceAPIOnPremiseImpl.LOGGER.log(Level.SEVERE, "Exception while looking up bean for on-premise ReadOnlyPersistence.. ", ex);
            return null;
        }
    }
    
    public Connection getConnection() throws SQLException {
        return RelationalAPI.getInstance().getConnection();
    }
    
    public Connection getReadOnlyConnection() throws Exception {
        try {
            final Connection connection = this.getConnection();
            return connection;
        }
        catch (final Exception e) {
            PersistenceAPIOnPremiseImpl.LOGGER.log(Level.SEVERE, "Exception while looking up bean for on-premise ReadOnlyConnection..", e);
            throw e;
        }
    }
    
    static {
        LOGGER = Logger.getLogger(PersistenceAPIOnPremiseImpl.class.getName());
    }
}
