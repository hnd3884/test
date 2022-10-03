package com.me.devicemanagement.framework.server.websockets;

import java.util.logging.Level;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class GatewaySessionManager
{
    private static Logger wsFrameworkLogger;
    private Map<Integer, Map<String, GatewaySession>> sessionMap;
    
    private GatewaySessionManager() {
        this.sessionMap = new ConcurrentHashMap<Integer, Map<String, GatewaySession>>();
    }
    
    public static GatewaySessionManager getInstance() {
        return Loader.sessionManagerInstance;
    }
    
    public GatewaySession createSession(final int toolId, final String sessionId, final Logger sessionLogger) throws Exception {
        GatewaySession session = null;
        if (!this.isSessionAlreadyExist(toolId, sessionId)) {
            session = new GatewaySession(sessionId, sessionLogger);
            this.addToSessionMap(toolId, session);
        }
        return session;
    }
    
    public void terminateSession(final int toolId, final String sessionId) throws Exception {
        GatewaySession session = null;
        if (this.isSessionAlreadyExist(toolId, sessionId)) {
            session = (GatewaySession)this.sessionMap.get(toolId).get(sessionId);
            session.endSession();
            this.removeFromSessionMap(toolId, sessionId);
            GatewaySessionManager.wsFrameworkLogger.log(Level.FINE, "GatewaySessionManager::deleteSession -> Deleted the session");
            GatewaySessionManager.wsFrameworkLogger.log(Level.FINE, "GatewaySessionManager::deleteSession -> Tool ID : {0}", toolId);
            GatewaySessionManager.wsFrameworkLogger.log(Level.FINE, "GatewaySessionManager::deleteSession -> Session ID : {0}", sessionId);
        }
    }
    
    public void addToSessionMap(final int toolId, final GatewaySession sessionObj) {
        Map<String, GatewaySession> toolSession;
        if (this.isToolAlreadyExistInSession(toolId)) {
            toolSession = this.sessionMap.get(toolId);
        }
        else {
            toolSession = new ConcurrentHashMap<String, GatewaySession>();
            this.sessionMap.put(toolId, toolSession);
        }
        toolSession.put(sessionObj.getSessionId(), sessionObj);
        GatewaySessionManager.wsFrameworkLogger.log(Level.FINE, "GatewaySessionManager::addToSessionMap -> Added the session to the sessionMap");
    }
    
    public boolean removeFromSessionMap(final int toolId, final String sessionId) {
        boolean isSessionRemoved = false;
        if (this.isSessionAlreadyExist(toolId, sessionId)) {
            this.sessionMap.get(toolId).remove(sessionId);
            isSessionRemoved = true;
            GatewaySessionManager.wsFrameworkLogger.log(Level.FINE, "GatewaySessionManager::removeFromSessionMap -> Session under the given tool is removed from the sessionMap");
        }
        return isSessionRemoved;
    }
    
    public GatewaySession getSessionObject(final int toolId, final String sessionId) {
        GatewaySession session = null;
        if (this.isSessionAlreadyExist(toolId, sessionId)) {
            session = (GatewaySession)this.sessionMap.get(toolId).get(sessionId);
            GatewaySessionManager.wsFrameworkLogger.log(Level.FINE, "GatewaySessionManager::getSessionObject -> Retrieved Session object for Tool ID : " + toolId + ", Session ID : " + sessionId);
        }
        return session;
    }
    
    public int getLiveSessionCount(final int toolId) {
        int activeSessionCount = 0;
        if (this.isToolAlreadyExistInSession(toolId)) {
            activeSessionCount = this.sessionMap.get(toolId).size();
            GatewaySessionManager.wsFrameworkLogger.log(Level.FINE, "GatewaySessionManager::getLiveSessionCount -> Live Session Count : " + activeSessionCount);
        }
        return activeSessionCount;
    }
    
    public boolean isSessionAlreadyExist(final int toolId, final String sessionId) {
        boolean isSessionExist = false;
        GatewaySession session = null;
        if (this.isToolAlreadyExistInSession(toolId)) {
            session = (GatewaySession)this.sessionMap.get(toolId).get(sessionId);
            if (session != null) {
                isSessionExist = true;
                GatewaySessionManager.wsFrameworkLogger.log(Level.FINE, "GatewaySessionManager::isSessionAlreadyExist -> Tool ID : " + toolId + ", Session ID : " + sessionId + ". Already Exists");
            }
            else {
                GatewaySessionManager.wsFrameworkLogger.log(Level.FINE, "GatewaySessionManager::isSessionAlreadyExist -> Session is null for session ID : " + sessionId);
            }
        }
        return isSessionExist;
    }
    
    public boolean isToolAlreadyExistInSession(final int toolId) {
        boolean isToolExist = false;
        Map<String, GatewaySession> toolSession = null;
        if (!this.sessionMap.isEmpty()) {
            toolSession = this.sessionMap.get(toolId);
            if (toolSession != null) {
                isToolExist = true;
            }
            else {
                GatewaySessionManager.wsFrameworkLogger.log(Level.WARNING, "GatewaySessionManager::isToolAlreadyExistInSession -> Specified tool is not present in the sessionMap");
            }
        }
        else {
            GatewaySessionManager.wsFrameworkLogger.log(Level.WARNING, "GatewaySessionManager::isToolAlreadyExistInSession -> SessionMap is empty");
        }
        return isToolExist;
    }
    
    public void addClientToSession(final int toolId, final String sessionId, final ClientManager clientMgr) {
        final GatewaySession session = this.getSessionObject(toolId, sessionId);
        if (session != null) {
            session.addClient(clientMgr.getClientId(), clientMgr);
            GatewaySessionManager.wsFrameworkLogger.log(Level.FINE, "GatewaySessionManager::addClientToSession -> Client - " + clientMgr.getClientId() + " added to session");
        }
    }
    
    public void removeClientFromSession(final int toolId, final String sessionId, final long clientId) {
        final GatewaySession session = this.getSessionObject(toolId, sessionId);
        if (session != null) {
            session.removeClient(clientId);
            GatewaySessionManager.wsFrameworkLogger.log(Level.FINE, "GatewaySessionManager::removeClientFromSession -> Client - " + clientId + " removed from session");
        }
    }
    
    public Map<Long, ClientManager> getParticipantsMap(final int toolId, final String sessionId) {
        Map<Long, ClientManager> participantsMap = null;
        final GatewaySession session = this.getSessionObject(toolId, sessionId);
        if (session != null) {
            participantsMap = session.getParticipantsMap();
        }
        return participantsMap;
    }
    
    static {
        GatewaySessionManager.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
    
    private static class Loader
    {
        static GatewaySessionManager sessionManagerInstance;
        
        static {
            Loader.sessionManagerInstance = new GatewaySessionManager(null);
        }
    }
}
