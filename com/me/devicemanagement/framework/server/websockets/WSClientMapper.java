package com.me.devicemanagement.framework.server.websockets;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class WSClientMapper
{
    private static Logger wsFrameworkLogger;
    private Map<Long, ClientManager> clientManagerMap;
    
    private WSClientMapper() {
        this.clientManagerMap = new ConcurrentHashMap<Long, ClientManager>();
    }
    
    static WSClientMapper getInstance() {
        return Loader.wsClientMapper;
    }
    
    void addClientManager(final ClientManager clientMgr) {
        this.clientManagerMap.put(clientMgr.getClientId(), clientMgr);
    }
    
    ClientManager getClientManager(final Long clientId) {
        return this.clientManagerMap.get(clientId);
    }
    
    void removeClientManager(final Long clientId) {
        this.clientManagerMap.remove(clientId);
    }
    
    static {
        WSClientMapper.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
    
    private static class Loader
    {
        static WSClientMapper wsClientMapper;
        
        static {
            Loader.wsClientMapper = new WSClientMapper(null);
        }
    }
}
