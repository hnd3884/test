package com.me.devicemanagement.framework.server.websockets;

import java.util.HashMap;
import java.util.logging.Level;
import javax.websocket.Session;
import java.util.Map;
import java.util.logging.Logger;

public class ClientDetails
{
    private static Logger wsFrameworkLogger;
    long clientId;
    String clientName;
    Map<String, String> requestParams;
    String clientType;
    int socketType;
    Object socketSessionObject;
    
    ClientDetails(final Map requestParamsMap, final Session sockSessionObj) {
        this.clientId = Long.parseLong(requestParamsMap.get("clientId").toString());
        final Object clientNameObj = requestParamsMap.get("clientName");
        if (clientNameObj == null) {
            this.clientName = null;
        }
        else {
            this.clientName = clientNameObj.toString();
        }
        this.clientType = requestParamsMap.get("clientType").toString();
        this.requestParams = requestParamsMap;
        this.socketSessionObject = sockSessionObj;
        this.socketType = Constants.ClientSocketType.WEBSOCKET.ordinal();
        ClientDetails.wsFrameworkLogger.log(Level.INFO, this.clientType + " -> ClientDetails object created");
    }
    
    ClientDetails(final long clientId, final String clientName, final String clientType, final String sessionId, final TCPSession tcpSession) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientType = clientType;
        this.socketSessionObject = tcpSession;
        this.socketType = Constants.ClientSocketType.TCP.ordinal();
        (this.requestParams = new HashMap<String, String>()).put("clientId", String.valueOf(clientId));
        this.requestParams.put("clientName", clientName);
        this.requestParams.put("clientType", clientType);
        this.requestParams.put("sessionId", sessionId);
        ClientDetails.wsFrameworkLogger.log(Level.INFO, clientType + " -> TCP ClientDetails object created");
    }
    
    static {
        ClientDetails.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}
