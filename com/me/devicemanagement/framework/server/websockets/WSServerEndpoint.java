package com.me.devicemanagement.framework.server.websockets;

import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import javax.websocket.OnClose;
import javax.websocket.CloseReason;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import java.io.IOException;
import java.util.logging.Level;
import javax.websocket.server.PathParam;
import javax.websocket.Session;
import java.util.logging.Logger;
import java.util.Map;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/dcWebSocket/{clientType}")
public class WSServerEndpoint
{
    private ClientManager clientMgr;
    private Map<String, String> paramMap;
    private static Logger wsFrameworkLogger;
    
    @OnOpen
    public void onOpen(final Session sockSession, @PathParam("clientType") final String clientType) throws IOException {
        WSServerEndpoint.wsFrameworkLogger.log(Level.INFO, "-------------------------------------------------------------------------");
        WSServerEndpoint.wsFrameworkLogger.log(Level.INFO, "************** New client : " + clientType + " ****************");
        WSServerEndpoint.wsFrameworkLogger.log(Level.INFO, "-------------------------------------------------------------------------");
        this.handleSocketOpen(sockSession, clientType);
    }
    
    @OnMessage
    public void onStringMessage(final String text) {
        if (this.clientMgr != null) {
            WSServerEndpoint.wsFrameworkLogger.log(Level.FINE, "Received -> " + this.clientMgr.getClientType() + " - String - " + text);
            this.clientMgr.setLastContactTime(System.currentTimeMillis());
            this.clientMgr.handleTextMessage(text);
        }
        else {
            WSServerEndpoint.wsFrameworkLogger.log(Level.WARNING, "onStringMessage: ClientManager object is null");
        }
    }
    
    @OnMessage
    public void onBinaryMessage(final byte[] binaryData) {
        if (this.clientMgr != null) {
            WSServerEndpoint.wsFrameworkLogger.log(Level.FINE, "Received -> " + this.clientMgr.getClientType() + " - Byte - " + binaryData);
            this.clientMgr.setLastContactTime(System.currentTimeMillis());
            this.clientMgr.handleBinaryMessage(binaryData);
        }
        else {
            WSServerEndpoint.wsFrameworkLogger.log(Level.WARNING, "onBinaryMessage: ClientManager object is null");
        }
    }
    
    @OnError
    public void onError(final Throwable throwable, @PathParam("clientType") final String clientType) {
        WSServerEndpoint.wsFrameworkLogger.log(Level.INFO, "onError - Connection Error : " + throwable.getMessage() + " - ClientType : " + clientType);
        if (this.clientMgr != null) {
            ClientAccessLogger.logClientAccess(String.valueOf(this.clientMgr.getClientId()), this.paramMap.get("clientName"), clientType, "ERROR", this.clientMgr.getConnectionMode(), Constants.ClientSocketType.WEBSOCKET.ordinal(), throwable.getMessage());
            ((WSSocketImpl)this.clientMgr.getClientSocket()).performInternalSocketCleanup();
            this.clientMgr.handleSocketError(throwable);
        }
        else {
            WSServerEndpoint.wsFrameworkLogger.log(Level.WARNING, clientType + " -> ClientManager object is null");
        }
    }
    
    @OnClose
    public void onClose(final CloseReason closeReason, @PathParam("clientType") final String clientType) {
        WSServerEndpoint.wsFrameworkLogger.log(Level.INFO, "onClose - Connection Closed : " + closeReason.getReasonPhrase() + " - ClientType : " + clientType);
        if (this.clientMgr != null) {
            final String remarks = String.format("CloseReason : %s, CloseCode : %s", closeReason.getReasonPhrase(), closeReason.getCloseCode());
            ClientAccessLogger.logClientAccess(String.valueOf(this.clientMgr.getClientId()), this.paramMap.get("clientName"), clientType, "DISCONNECTED", this.clientMgr.getConnectionMode(), Constants.ClientSocketType.WEBSOCKET.ordinal(), remarks);
            this.clientMgr.setLastContactTime(System.currentTimeMillis());
            ((WSSocketImpl)this.clientMgr.getClientSocket()).performInternalSocketCleanup();
            this.clientMgr.handleSocketClose(closeReason.getReasonPhrase());
        }
        else {
            WSServerEndpoint.wsFrameworkLogger.log(Level.WARNING, "onClose: Socket Closed but, ClientManager object is null");
        }
    }
    
    private static Map<String, String> convertToParameterMap(final Map<String, List<String>> paramMapList) {
        WSServerEndpoint.wsFrameworkLogger.log(Level.FINEST, "Entered into convertToParameterMap method");
        final Map<String, String> paramMap = new HashMap<String, String>();
        WSServerEndpoint.wsFrameworkLogger.log(Level.INFO, "---------------------------");
        WSServerEndpoint.wsFrameworkLogger.log(Level.INFO, "RECEIVED PARAMETERS - START");
        final Set<String> keySet = paramMapList.keySet();
        for (final String key : keySet) {
            paramMap.put(key, paramMapList.get(key).get(0));
            WSServerEndpoint.wsFrameworkLogger.log(Level.INFO, key + " -> " + paramMap.get(key));
        }
        WSServerEndpoint.wsFrameworkLogger.log(Level.INFO, "RECEIVED PARAMETERS - END");
        WSServerEndpoint.wsFrameworkLogger.log(Level.INFO, "---------------------------");
        return paramMap;
    }
    
    private void handleSocketOpen(final Session wsSockSession, final String clientType) throws IOException {
        try {
            wsSockSession.setMaxBinaryMessageBufferSize(SocketAdapterConfManager.getInstance().getMaxBinaryMessageBufferSize(clientType));
            wsSockSession.setMaxTextMessageBufferSize(SocketAdapterConfManager.getInstance().getMaxTextMessageBufferSize(clientType));
            final Map<String, List<String>> paramMapList = wsSockSession.getRequestParameterMap();
            if (!paramMapList.isEmpty()) {
                WSServerEndpoint.wsFrameworkLogger.log(Level.FINEST, clientType + " -> Entered into onOpen method");
                this.paramMap = convertToParameterMap(paramMapList);
                final ClientDetails clientDetails = new ClientDetails(this.paramMap, wsSockSession);
                this.clientMgr = ClientRequestMapper.getInstance().createClientManager(clientDetails);
                if (this.clientMgr != null) {
                    ClientAccessLogger.logClientAccess(String.valueOf(this.clientMgr.getClientId()), clientDetails.clientName, clientType, "CONNECTED", this.clientMgr.getConnectionMode(), Constants.ClientSocketType.WEBSOCKET.ordinal());
                    this.clientMgr.setClientStatus(Constants.ClientStatus.VERIFYING.ordinal());
                    this.clientMgr.setLastContactTime(System.currentTimeMillis());
                    if (!ConnectionPoolHandler.getInstance().addClientRequestToPool(this.clientMgr)) {
                        WSServerEndpoint.wsFrameworkLogger.log(Level.WARNING, clientType + " -> Adding Client Request to thread pool failed - " + clientType + ":" + this.clientMgr.getClientId());
                        wsSockSession.close();
                    }
                }
                else {
                    WSServerEndpoint.wsFrameworkLogger.log(Level.WARNING, clientType + " -> ClientManager object is null");
                    wsSockSession.close();
                }
            }
            else {
                WSServerEndpoint.wsFrameworkLogger.log(Level.WARNING, clientType + " -> No request parameters from the client. Dropping Client Connection");
                wsSockSession.close();
            }
        }
        catch (final Exception ex) {
            WSServerEndpoint.wsFrameworkLogger.log(Level.SEVERE, clientType + " -> Request rejected! Dropping client connection", ex);
            wsSockSession.close();
        }
    }
    
    static {
        WSServerEndpoint.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}
