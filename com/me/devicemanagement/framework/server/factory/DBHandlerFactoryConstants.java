package com.me.devicemanagement.framework.server.factory;

public class DBHandlerFactoryConstants
{
    public static String persistenceDBHandler;
    public static String dataAccessDBHandler;
    
    static {
        DBHandlerFactoryConstants.persistenceDBHandler = "com.me.devicemanagement.framework.server.util.PersistenceDBHandler";
        DBHandlerFactoryConstants.dataAccessDBHandler = "com.me.devicemanagement.framework.server.util.DataAccessDBHandler";
    }
}
