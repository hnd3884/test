package com.adventnet.cli.rmi;

import java.util.Properties;
import java.util.Enumeration;
import com.adventnet.cli.CLIMessage;
import com.adventnet.cli.transport.CLIProtocolOptions;
import java.rmi.RemoteException;
import java.util.Vector;
import com.adventnet.cli.ConnectionListener;
import com.adventnet.cli.CLIClient;
import java.rmi.server.UnicastRemoteObject;

class CLISessionImpl extends UnicastRemoteObject implements CLIClient, ConnectionListener, CLISession
{
    protected com.adventnet.cli.CLISession cliSession;
    CLIResourceManager cliRmgr;
    Vector clients;
    Vector conListeners;
    
    public void open() throws RemoteException {
        try {
            this.cliSession.open();
        }
        catch (final Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }
    
    public void setTransportProviderClassName(final String transportProviderClassName) throws RemoteException {
        this.cliSession.setTransportProviderClassName(transportProviderClassName);
    }
    
    public String getTransportProviderClassName() throws RemoteException {
        return this.cliSession.getTransportProviderClassName();
    }
    
    public CLISessionImpl(final CLIProtocolOptions cliProtocolOptions, final boolean b) throws RemoteException {
        this.cliSession = null;
        this.cliRmgr = null;
        this.clients = null;
        this.conListeners = null;
        try {
            this.cliSession = new com.adventnet.cli.CLISession(cliProtocolOptions, b);
            this.cliRmgr = new CLIResourceManagerImpl();
        }
        catch (final Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
        this.init();
    }
    
    public CLISessionImpl(final CLIProtocolOptions cliProtocolOptions) throws RemoteException {
        this.cliSession = null;
        this.cliRmgr = null;
        this.clients = null;
        this.conListeners = null;
        try {
            this.cliSession = new com.adventnet.cli.CLISession(cliProtocolOptions);
        }
        catch (final Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
        this.init();
    }
    
    private void init() throws RemoteException {
        this.cliSession.addCLIClient(this);
        this.cliSession.addConnectionListener(this);
        this.clients = new Vector();
    }
    
    public void setCLIProtocolOptions(final CLIProtocolOptions cliProtocolOptions) throws RemoteException {
        this.cliSession.setCLIProtocolOptions(cliProtocolOptions);
    }
    
    public CLIProtocolOptions getCLIProtocolOptions() throws RemoteException {
        return this.cliSession.getCLIProtocolOptions();
    }
    
    public void setCLIPrompt(final String cliPrompt) throws RemoteException {
        this.cliSession.setCLIPrompt(cliPrompt);
    }
    
    public String getCLIPrompt() throws RemoteException {
        return this.cliSession.getCLIPrompt();
    }
    
    public void setPooling(final boolean pooling) throws RemoteException {
        this.cliSession.setPooling(pooling);
    }
    
    public boolean isSetPooling() throws RemoteException {
        return this.cliSession.isSetPooling();
    }
    
    public void setMaxConnections(final int maxConnections) throws RemoteException {
        this.cliSession.setMaxConnections(maxConnections);
    }
    
    public int getMaxConnections() throws RemoteException {
        return this.cliSession.getMaxConnections();
    }
    
    public CLIMessage syncSend(final CLIMessage cliMessage) throws RemoteException {
        try {
            return this.cliSession.syncSend(cliMessage);
        }
        catch (final Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }
    
    public void addCLIClient(final com.adventnet.cli.rmi.CLIClient cliClient) throws RemoteException {
        this.clients.addElement(cliClient);
    }
    
    public int send(final CLIMessage cliMessage) throws RemoteException {
        try {
            return this.cliSession.send(cliMessage);
        }
        catch (final Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }
    
    public void close() throws RemoteException {
        try {
            this.cliSession.close();
        }
        catch (final Exception ex) {
            throw new RemoteException(ex.getMessage());
        }
    }
    
    public void setRequestTimeout(final int requestTimeout) throws RemoteException {
        this.cliSession.setRequestTimeout(requestTimeout);
    }
    
    public int getRequestTimeout() throws RemoteException {
        return this.cliSession.getRequestTimeout();
    }
    
    public void setKeepAliveTimeout(final int keepAliveTimeout) throws RemoteException {
        this.cliSession.setKeepAliveTimeout(keepAliveTimeout);
    }
    
    public int getKeepAliveTimeout() throws RemoteException {
        return this.cliSession.getKeepAliveTimeout();
    }
    
    public void setDebug(final boolean debug) throws RemoteException {
        com.adventnet.cli.CLISession.setDebug(debug);
    }
    
    public boolean isSetDebug() throws RemoteException {
        return com.adventnet.cli.CLISession.isSetDebug();
    }
    
    public void setDebugLevel(final int debugLevel) throws RemoteException {
        this.cliSession.setDebugLevel(debugLevel);
    }
    
    public int getDebugLevel() throws RemoteException {
        return this.cliSession.getDebugLevel();
    }
    
    public boolean callback(final com.adventnet.cli.CLISession cliSession, final CLIMessage cliMessage, final int n) {
        final Enumeration elements = this.clients.elements();
        while (elements.hasMoreElements()) {
            final com.adventnet.cli.rmi.CLIClient cliClient = (com.adventnet.cli.rmi.CLIClient)elements.nextElement();
            try {
                if (cliClient.callback(this, cliMessage, n)) {
                    return true;
                }
                continue;
            }
            catch (final RemoteException ex) {
                return true;
            }
        }
        return true;
    }
    
    public void setIgnoreSpecialCharacters(final boolean ignoreSpecialCharacters) throws RemoteException {
        this.cliSession.setIgnoreSpecialCharacters(ignoreSpecialCharacters);
    }
    
    public boolean isSetIgnoreSpecialCharacters() throws RemoteException {
        return this.cliSession.isSetIgnoreSpecialCharacters();
    }
    
    public String getInitialMessage() throws RemoteException {
        return this.cliSession.getInitialMessage();
    }
    
    public void removeCLIClient(final com.adventnet.cli.rmi.CLIClient cliClient) throws RemoteException {
        synchronized (this.clients) {
            this.clients.removeElement(cliClient);
        }
    }
    
    public int getCLIClientsSize() throws RemoteException {
        synchronized (this.clients) {
            return this.clients.size();
        }
    }
    
    public void addConnectionListener(final com.adventnet.cli.rmi.ConnectionListener connectionListener) throws RemoteException {
        if (this.conListeners == null) {
            this.conListeners = new Vector();
        }
        this.conListeners.addElement(connectionListener);
    }
    
    public void removeConnectionListener(final com.adventnet.cli.rmi.ConnectionListener connectionListener) throws RemoteException {
        if (this.conListeners.size() > 0) {
            this.conListeners.removeElement(connectionListener);
        }
    }
    
    public Properties getCLIPromptAction() throws RemoteException {
        return this.cliSession.getCLIPromptAction();
    }
    
    public void setCLIPromptAction(final Properties cliPromptAction) throws RemoteException {
        this.cliSession.setCLIPromptAction(cliPromptAction);
    }
    
    public String getInterruptCmd() throws RemoteException {
        return this.cliSession.getInterruptCmd();
    }
    
    public void setInterruptCmd(final String interruptCmd) throws RemoteException {
        this.cliSession.setInterruptCmd(interruptCmd);
    }
    
    public CLIResourceManager getResourceManager() throws RemoteException {
        return this.cliRmgr;
    }
    
    public void connectionTimedOut(final CLIProtocolOptions cliProtocolOptions) {
        if (this.conListeners != null) {
            final Enumeration elements = this.conListeners.elements();
            while (elements.hasMoreElements()) {
                final com.adventnet.cli.rmi.ConnectionListener connectionListener = (com.adventnet.cli.rmi.ConnectionListener)elements.nextElement();
                try {
                    connectionListener.connectionTimedOut(cliProtocolOptions);
                }
                catch (final RemoteException ex) {}
            }
        }
    }
    
    public void connectionDown(final CLIProtocolOptions cliProtocolOptions) {
        if (this.conListeners != null) {
            final Enumeration elements = this.conListeners.elements();
            while (elements.hasMoreElements()) {
                final com.adventnet.cli.rmi.ConnectionListener connectionListener = (com.adventnet.cli.rmi.ConnectionListener)elements.nextElement();
                try {
                    connectionListener.connectionDown(cliProtocolOptions);
                }
                catch (final RemoteException ex) {}
            }
        }
    }
}
