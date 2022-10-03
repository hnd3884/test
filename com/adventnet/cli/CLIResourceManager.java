package com.adventnet.cli;

import com.adventnet.cli.transport.CLITransportProvider;
import com.adventnet.cli.transport.CLIProtocolOptions;
import com.adventnet.cli.util.CLILogMgr;

public class CLIResourceManager
{
    static int systemWideConnectionCount;
    int systemWideMaxConnections;
    static CLITransportPool cliTransportPool;
    static CLIResourceManager cliResourceManager;
    int keepAliveTimeout;
    int maxConnections;
    boolean enablePooling;
    
    CLIResourceManager() {
        this.systemWideMaxConnections = 0;
        this.keepAliveTimeout = 60;
        this.maxConnections = 1;
        this.enablePooling = false;
        if (CLIResourceManager.cliTransportPool == null) {
            CLIResourceManager.cliTransportPool = new CLITransportPool();
        }
    }
    
    public static CLIResourceManager getInstance() {
        if (CLIResourceManager.cliResourceManager == null) {
            CLIResourceManager.cliResourceManager = new CLIResourceManager();
        }
        return CLIResourceManager.cliResourceManager;
    }
    
    static synchronized void decrementConnectionCount() {
        if (CLIResourceManager.systemWideConnectionCount > 0) {
            --CLIResourceManager.systemWideConnectionCount;
        }
    }
    
    boolean isSystemWideMaxConnectionsReached() {
        CLILogMgr.setDebugMessage("CLIUSER", "CLIResourceManager: isSystemWideMaxConnectionsReached : systemWideMaxConnections : " + this.systemWideMaxConnections + " maxConnections: " + this.maxConnections + " thread name :" + Thread.currentThread().getName(), 2, null);
        if (this.systemWideMaxConnections == 0 || this.maxConnections == 0) {
            ++CLIResourceManager.systemWideConnectionCount;
            return false;
        }
        if (CLIResourceManager.systemWideConnectionCount >= this.systemWideMaxConnections) {
            return true;
        }
        ++CLIResourceManager.systemWideConnectionCount;
        return false;
    }
    
    public void setSystemWideMaxConnections(final int systemWideMaxConnections) {
        if (systemWideMaxConnections < 0) {
            return;
        }
        this.systemWideMaxConnections = systemWideMaxConnections;
    }
    
    public int getSystemWideMaxConnections() {
        return this.systemWideMaxConnections;
    }
    
    public int getAliveConnectionsCount() {
        return CLIResourceManager.systemWideConnectionCount;
    }
    
    CLITransportProvider updateResourceManager(final CLIProtocolOptions cliProtocolOptions, final String s, final CLISession cliSession, final boolean b) throws Exception, MaxConnectionException {
        if (b) {
            return null;
        }
        final CLITransportGroup cliTransportGroup = CLIResourceManager.cliTransportPool.transportPool.get(cliProtocolOptions.getID());
        if (cliTransportGroup != null && cliTransportGroup.isMaxConnectionsReached(true)) {
            throw new MaxConnectionException("Max connections (per Device) Reached");
        }
        final CLITransportProvider providerInstance = this.getProviderInstance(cliProtocolOptions, s);
        this.addProviderToPool(providerInstance, cliProtocolOptions, cliSession, cliSession.isSessionAlive = true);
        return providerInstance;
    }
    
    CLITransportProvider getProvider(final CLIProtocolOptions cliProtocolOptions, final String s, final CLISession cliSession) throws Exception, MaxConnectionException {
        CLILogMgr.setDebugMessage("CLIUSER", "################# " + cliProtocolOptions.getID() + "getProvider ::: ", 2, null);
        final CLITransportProvider providerFromPool = CLIResourceManager.cliTransportPool.getProviderFromPool(cliProtocolOptions);
        final CLITransportGroup cliTransportGroup = CLIResourceManager.cliTransportPool.transportPool.get(cliProtocolOptions.getID());
        if (cliTransportGroup != null) {
            CLILogMgr.setDebugMessage("CLIUSER", "################# " + cliProtocolOptions.getID() + " cliGroup.connectionCount :: " + cliTransportGroup.connectionCount, 2, null);
            CLILogMgr.setDebugMessage("CLIUSER", "################# " + cliProtocolOptions.getID() + " cliGroup.maxConnections :: " + cliTransportGroup.maxConnections, 2, null);
        }
        if (providerFromPool == null) {
            CLILogMgr.setDebugMessage("CLIUSER", "################# " + cliProtocolOptions.getID() + "cliTransportProvider==null ::: ", 2, null);
            final CLITransportGroup cliTransportGroup2 = CLIResourceManager.cliTransportPool.transportPool.get(cliProtocolOptions.getID());
            if (cliTransportGroup2 != null) {
                CLILogMgr.setDebugMessage("CLIUSER", "################# " + cliProtocolOptions.getID() + " :: cliGroup.connectionCount :: " + cliTransportGroup2.connectionCount, 2, null);
                if (cliTransportGroup2.isMaxConnectionsReached(false)) {
                    CLILogMgr.setDebugMessage("CLIUSER", "################# " + cliProtocolOptions.getID() + " :: isMaxConnectionsReached = true ::: " + " cliGroup.connectionCount :: " + cliTransportGroup2.connectionCount, 2, null);
                    throw new MaxConnectionException("Max connections (per Device) Reached");
                }
            }
            CLITransportProvider providerInstance;
            try {
                CLILogMgr.setDebugMessage("CLIUSER", "################# " + cliProtocolOptions.getID() + " :: getProviderInstance :: ", 2, null);
                providerInstance = this.getProviderInstance(cliProtocolOptions, s);
                CLILogMgr.setDebugMessage("CLIUSER", "################# " + cliProtocolOptions.getID() + " :: gotProviderInstance :: ", 2, null);
            }
            catch (final Exception ex) {
                CLILogMgr.setDebugMessage("CLIUSER", "################# " + cliProtocolOptions.getID() + " :: Exception e :: ", 2, null);
                ex.printStackTrace();
                try {
                    if (cliTransportGroup2 != null) {
                        cliTransportGroup2.decrementConnectionCount();
                    }
                }
                catch (final Exception ex2) {}
                throw ex;
            }
            CLILogMgr.setDebugMessage("CLIUSER", "################# " + cliProtocolOptions.getID() + " :: return cliTransportProvider :: ", 2, null);
            cliSession.gotFromPool = false;
            cliSession.isSessionAlive = true;
            return providerInstance;
        }
        CLILogMgr.setDebugMessage("CLIUSER", "################# " + cliProtocolOptions.getID() + " :: clisess.gotFromPool=true; return cliTransportProvider::: ", 2, null);
        cliSession.gotFromPool = true;
        return providerFromPool;
    }
    
    CLITransportProvider getProviderInstance(final CLIProtocolOptions cliProtocolOptions, final String s) throws Exception, MaxConnectionException {
        if (this.isSystemWideMaxConnectionsReached()) {
            throw new MaxConnectionException("System Wide Max Connections Reached");
        }
        CLITransportProvider cliTransportProvider;
        try {
            cliTransportProvider = (CLITransportProvider)Class.forName(s).newInstance();
        }
        catch (final Exception ex) {
            decrementConnectionCount();
            throw ex;
        }
        try {
            cliTransportProvider.open(cliProtocolOptions);
        }
        catch (final Exception ex2) {
            decrementConnectionCount();
            throw ex2;
        }
        return cliTransportProvider;
    }
    
    void setMaxConnectionsForGroup(final int n, final CLIProtocolOptions cliProtocolOptions) {
        CLIResourceManager.cliTransportPool.setMaxConnectionsForGroup(n, cliProtocolOptions);
    }
    
    void addProviderToPool(final CLITransportProvider cliTransportProvider, final CLIProtocolOptions cliProtocolOptions, final CLISession cliSession, final boolean b) throws Exception {
        final CLITransportGroup cliGroup = CLIResourceManager.cliTransportPool.getCLIGroup(cliProtocolOptions);
        CLIResourceManager.cliTransportPool.addProviderToPool(cliTransportProvider, cliProtocolOptions, b);
        if (cliGroup == null) {
            this.setMaxConnectionsForGroup(cliSession.getMaxConnections(), cliProtocolOptions);
        }
        if (cliSession.isSetPooling()) {
            this.setKeepAliveTimeout(cliSession.getKeepAliveTimeout(), cliProtocolOptions.getID());
        }
    }
    
    void releaseProvider(final CLITransportProvider cliTransportProvider, final CLIProtocolOptions cliProtocolOptions) {
        CLILogMgr.setDebugMessage("CLIUSER", "################# " + cliProtocolOptions.getID() + " :: releaseProvider ::: ", 2, null);
        CLIResourceManager.cliTransportPool.releaseProvider(cliTransportProvider, cliProtocolOptions);
    }
    
    void removeProvider(final CLITransportProvider cliTransportProvider, final CLIProtocolOptions cliProtocolOptions) {
        CLILogMgr.setDebugMessage("CLIUSER", "################# " + cliProtocolOptions.getID() + " :: releaseProvider ::: ", 2, null);
        CLIResourceManager.cliTransportPool.removeProvider(cliTransportProvider, cliProtocolOptions);
        final CLITransportGroup cliGroup = CLIResourceManager.cliTransportPool.getCLIGroup(cliProtocolOptions);
        if (cliGroup != null) {
            cliGroup.decrementConnectionCount();
        }
        decrementConnectionCount();
    }
    
    void setKeepAliveTimeout(final int n, final Object o) {
        CLIResourceManager.cliTransportPool.setKeepAliveTimeout(n, o);
    }
    
    int getKeepAliveTimeout(final Object o) {
        return CLIResourceManager.cliTransportPool.getKeepAliveTimeout(o);
    }
    
    public void setKeepAliveTimeout(final int keepAliveTimeout) {
        if (keepAliveTimeout > 0) {
            this.keepAliveTimeout = keepAliveTimeout;
        }
    }
    
    public int getKeepAliveTimeout() {
        return this.keepAliveTimeout;
    }
    
    public void setMaxConnections(final int maxConnections) {
        if (maxConnections >= 0) {
            this.maxConnections = maxConnections;
        }
    }
    
    public int getMaxConnections() {
        return this.maxConnections;
    }
    
    public void setPooling(final boolean enablePooling) {
        this.enablePooling = enablePooling;
    }
    
    public boolean isSetPooling() {
        return this.enablePooling;
    }
    
    void addConnectionListener(final ConnectionListener connectionListener) {
        CLIResourceManager.cliTransportPool.addConnectionListener(connectionListener);
    }
    
    void removeConnectionListener(final ConnectionListener connectionListener) {
        CLIResourceManager.cliTransportPool.removeConnectionListener(connectionListener);
    }
    
    public synchronized void closeAllConnections() {
        CLIResourceManager.cliTransportPool.closeConnections();
    }
    
    static {
        CLIResourceManager.systemWideConnectionCount = 0;
        CLIResourceManager.cliResourceManager = null;
    }
}
