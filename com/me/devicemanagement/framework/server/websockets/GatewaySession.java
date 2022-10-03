package com.me.devicemanagement.framework.server.websockets;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GatewaySession
{
    private Logger sessionLogger;
    private String sessionId;
    private long startTime;
    protected Map<Long, ClientManager> participantsMap;
    
    public GatewaySession(final String sessionId, final Logger logger) {
        this.sessionId = sessionId;
        this.startTime = System.currentTimeMillis();
        this.participantsMap = new ConcurrentHashMap<Long, ClientManager>();
        (this.sessionLogger = logger).log(Level.FINE, "Session::Session -> Session created with session ID", sessionId);
    }
    
    public void addClient(final Long clientId, final ClientManager newClient) {
        this.onBeforeAddingClient(newClient);
        if (this.participantsMap.size() > 0) {
            for (final Long cliId : this.participantsMap.keySet()) {
                final ClientManager existingClient = this.participantsMap.get(cliId);
                newClient.addListenerClient(existingClient);
                existingClient.addListenerClient(newClient);
            }
        }
        this.participantsMap.put(clientId, newClient);
        this.onAfterAddingClient(newClient);
        this.sessionLogger.log(Level.FINE, "Session::addClient -> ClientManager object added to the participants list");
    }
    
    public void removeClient(final Long clientId) {
        final ClientManager client = this.participantsMap.get(clientId);
        if (client != null) {
            this.onBeforeRemovingClient(client);
            this.participantsMap.remove(clientId);
            this.onAfterRemovingClient(client);
        }
        if (this.participantsMap.size() > 0) {
            for (final Long cliId : this.participantsMap.keySet()) {
                final ClientManager cliMgr = this.participantsMap.get(cliId);
                cliMgr.removeListenerClient(clientId);
            }
        }
        this.sessionLogger.log(Level.FINE, "Session::removeClient -> Specified client removed. All other clients are notified");
    }
    
    public void onBeforeAddingClient(final ClientManager clientMgr) {
        this.sessionLogger.log(Level.INFO, "Session::onBeforeAddingClient - Client Id - " + clientMgr.getClientId());
    }
    
    public void onAfterAddingClient(final ClientManager clientMgr) {
        this.sessionLogger.log(Level.INFO, "Session::onAfterAddingClient - Client Id - " + clientMgr.getClientId());
    }
    
    public void onBeforeRemovingClient(final ClientManager clientMgr) {
        this.sessionLogger.log(Level.INFO, "Session::onBeforeRemovingClient - Client Id - " + clientMgr.getClientId());
    }
    
    public void onAfterRemovingClient(final ClientManager clientMgr) {
        this.sessionLogger.log(Level.INFO, "Session::onAfterRemovingClient - Client Id - " + clientMgr.getClientId());
    }
    
    public Map<Long, ClientManager> getParticipantsMap() {
        return this.participantsMap;
    }
    
    public int getParticipantsCount() {
        return this.participantsMap.size();
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public void endSession() {
        for (final Long clientId : this.participantsMap.keySet()) {
            final ClientManager cliMgr = this.participantsMap.get(clientId);
            if (cliMgr != null) {
                cliMgr.closeSocket();
            }
        }
        this.sessionLogger.log(Level.FINE, "Session::endSession -> Notified all the clients in the participants list about the session termination");
    }
    
    public ClientManager getClient(final long clientId) {
        return this.participantsMap.get(clientId);
    }
    
    public String getSessionId() {
        return this.sessionId;
    }
    
    public void broadcastToParticipants(final String message, final boolean isBlocking) throws Exception {
        for (final Long clientId : this.participantsMap.keySet()) {
            final ClientManager cliMgr = this.participantsMap.get(clientId);
            if (cliMgr != null) {
                cliMgr.getClientSocket().sendString(message, isBlocking);
            }
        }
    }
}
