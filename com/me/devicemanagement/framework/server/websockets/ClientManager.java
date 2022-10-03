package com.me.devicemanagement.framework.server.websockets;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class ClientManager
{
    private static Logger wsFrameworkLogger;
    private Map<Long, ClientManager> listenerClients;
    private long connectedTime;
    private long lastContactTime;
    private int clientStatus;
    protected int connectionMode;
    private ClientDetails clientDetails;
    
    public ClientManager(final ClientDetails clientObj) {
        this.connectedTime = System.currentTimeMillis();
        this.lastContactTime = System.currentTimeMillis();
        this.clientDetails = clientObj;
        this.listenerClients = new ConcurrentHashMap<Long, ClientManager>();
        this.clientStatus = Constants.ClientStatus.CONNECTED.ordinal();
        WSClientMapper.getInstance().addClientManager(this);
    }
    
    void handleSocketReady() {
        this.clientStatus = Constants.ClientStatus.READY.ordinal();
        this.onSocketReady();
    }
    
    void handleSocketClose(final String closeReason) {
        this.clientStatus = Constants.ClientStatus.DISCONNECTED.ordinal();
        this.onSocketClose(closeReason);
        WSClientMapper.getInstance().removeClientManager(this.clientDetails.clientId);
    }
    
    void handleSocketError(final Throwable error) {
        this.clientStatus = Constants.ClientStatus.DISCONNECTED.ordinal();
        this.onSocketError(error);
        WSClientMapper.getInstance().removeClientManager(this.clientDetails.clientId);
    }
    
    public synchronized void addListenerClient(final ClientManager client) {
        synchronized (this.listenerClients) {
            this.listenerClients.put(client.getClientId(), client);
        }
    }
    
    public synchronized void removeListenerClient(final long clientId) {
        synchronized (this.listenerClients) {
            this.listenerClients.remove(clientId);
        }
    }
    
    void setClientStatus(final int status) {
        this.clientStatus = status;
    }
    
    public int getClientStatus() {
        return this.clientStatus;
    }
    
    public long getClientId() {
        return this.clientDetails.clientId;
    }
    
    public String getClientName() {
        return this.clientDetails.clientName;
    }
    
    public int getConnectionMode() {
        return this.connectionMode;
    }
    
    public String getClientType() {
        return this.clientDetails.clientType;
    }
    
    public ClientDetails getClientDetails() {
        return this.clientDetails;
    }
    
    public String getSessionId() {
        return this.clientDetails.requestParams.get("sessionId");
    }
    
    public String getRequestParameter(final String key) {
        return this.clientDetails.requestParams.get(key);
    }
    
    public long getLastContactTime() {
        return this.lastContactTime;
    }
    
    public void setLastContactTime(final long lastContactTime) {
        this.lastContactTime = lastContactTime;
    }
    
    public long getConnectedTime() {
        return this.connectedTime;
    }
    
    protected Map<String, String> getRequestParamsMap() {
        return this.clientDetails.requestParams;
    }
    
    public int getClientSocketType() {
        return this.clientDetails.socketType;
    }
    
    public long getIdleSessionTimeOut() throws Exception {
        return this.getClientSocket().getMaxIdleTime();
    }
    
    public synchronized int getNumberOfListeners() {
        return this.listenerClients.size();
    }
    
    public synchronized Set<Long> getListenerClientIdSet() {
        return this.listenerClients.keySet();
    }
    
    public ClientManager getListener(final Long clientId) {
        return this.listenerClients.get(clientId);
    }
    
    public void broadcastToListeners(final String textData, final boolean isBlocking) throws Exception {
        try {
            for (final Long clientId : this.listenerClients.keySet()) {
                this.sendToListener(clientId, textData, isBlocking);
            }
        }
        catch (final Exception ex) {
            ClientManager.wsFrameworkLogger.log(Level.SEVERE, "Exception while broadcasting string data to listeners", ex);
            throw ex;
        }
    }
    
    public void broadcastToListeners(final byte[] binaryData, final boolean isBlocking) throws Exception {
        try {
            for (final Long clientId : this.listenerClients.keySet()) {
                this.sendToListener(clientId, binaryData, isBlocking);
            }
        }
        catch (final Exception ex) {
            ClientManager.wsFrameworkLogger.log(Level.SEVERE, "Exception while broadcasting binary data to listeners", ex);
            throw ex;
        }
    }
    
    public void sendToListener(final long clientId, final String textData, final boolean isBlocking) throws Exception {
        final ClientManager clientMgr = this.listenerClients.get(clientId);
        if (clientMgr != null) {
            try {
                clientMgr.getClientSocket().sendString(textData, isBlocking);
            }
            catch (final Exception ex) {
                ClientManager.wsFrameworkLogger.log(Level.SEVERE, "Exception while sending string data to the listener", ex);
                throw ex;
            }
        }
    }
    
    public void sendToListener(final long clientId, final byte[] binaryData, final boolean isBlocking) throws Exception {
        final ClientManager clientMgr = this.listenerClients.get(clientId);
        if (clientMgr != null) {
            try {
                clientMgr.getClientSocket().sendBytes(binaryData, isBlocking);
            }
            catch (final Exception ex) {
                ClientManager.wsFrameworkLogger.log(Level.SEVERE, "Exception while sending binary data to the listener", ex);
                throw ex;
            }
        }
    }
    
    public void closeSocket() {
        try {
            this.getClientSocket().closeSocket();
        }
        catch (final Exception ex) {
            ClientManager.wsFrameworkLogger.log(Level.SEVERE, "ClientManager -> Exception while closing the socket", ex);
        }
    }
    
    public abstract void handleTextMessage(final String p0);
    
    public abstract void handleBinaryMessage(final byte[] p0);
    
    public abstract Socket getClientSocket();
    
    public abstract void onSocketReady();
    
    public abstract void onSocketClose(final String p0);
    
    public abstract void onSocketError(final Throwable p0);
    
    static {
        ClientManager.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}
