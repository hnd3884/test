package com.me.devicemanagement.framework.server.factory;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class DBHandlerFactoryProvider
{
    private static final Logger LOGGER;
    private static DBHandler dbHandler;
    
    public static DBHandler getDBHandler() {
        try {
            if (DBHandlerFactoryProvider.dbHandler == null) {
                if (SyMUtil.isProbeServer()) {
                    DBHandlerFactoryProvider.dbHandler = (DBHandler)Class.forName(DBHandlerFactoryConstants.persistenceDBHandler).newInstance();
                }
                else {
                    DBHandlerFactoryProvider.dbHandler = (DBHandler)Class.forName(DBHandlerFactoryConstants.dataAccessDBHandler).newInstance();
                }
            }
        }
        catch (final Exception e) {
            DBHandlerFactoryProvider.LOGGER.log(Level.SEVERE, "Exception  during Instantiation for DBHandlerFactoryProvider API", e);
        }
        return DBHandlerFactoryProvider.dbHandler;
    }
    
    static {
        LOGGER = Logger.getLogger(DBHandlerFactoryProvider.class.getName());
        DBHandlerFactoryProvider.dbHandler = null;
    }
}
