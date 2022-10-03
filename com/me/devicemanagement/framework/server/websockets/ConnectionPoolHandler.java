package com.me.devicemanagement.framework.server.websockets;

import java.util.Hashtable;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

final class ConnectionPoolHandler
{
    private static Logger wsFrameworkLogger;
    private static ConnectionPoolHandler connPoolObj;
    private String taskExecutorClass;
    
    static ConnectionPoolHandler getInstance() {
        if (ConnectionPoolHandler.connPoolObj == null) {
            ConnectionPoolHandler.connPoolObj = new ConnectionPoolHandler();
        }
        return ConnectionPoolHandler.connPoolObj;
    }
    
    boolean submitClientRequestToProcessor(final Properties clientProps) {
        boolean addStatus = false;
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "ClientRequestProcessor");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "wsConnAcceptPool");
        try {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously(this.taskExecutorClass, taskInfoMap, clientProps);
            addStatus = true;
        }
        catch (final Exception ex) {
            ConnectionPoolHandler.wsFrameworkLogger.log(Level.WARNING, "Exception occured while submitting client request to processor", ex);
        }
        return addStatus;
    }
    
    boolean addClientRequestToPool(final ClientManager clientMgr) {
        this.taskExecutorClass = ClientRequestProcessor.class.getName();
        final Properties props = new Properties();
        ((Hashtable<String, Long>)props).put("clientId", clientMgr.getClientId());
        final boolean addStatus = this.submitClientRequestToProcessor(props);
        ConnectionPoolHandler.wsFrameworkLogger.log(Level.INFO, "Client request submitted to the thread pool");
        return addStatus;
    }
    
    static {
        ConnectionPoolHandler.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
        ConnectionPoolHandler.connPoolObj = null;
    }
}
