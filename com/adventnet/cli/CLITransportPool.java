package com.adventnet.cli;

import com.adventnet.cli.util.CLILogMgr;
import java.util.Enumeration;
import com.adventnet.cli.transport.CLIProtocolOptions;
import com.adventnet.cli.transport.CLITransportProvider;
import java.util.Vector;
import java.util.Hashtable;

class CLITransportPool extends Thread
{
    Hashtable transportPool;
    boolean closeFlag;
    int connectionTimeout;
    private Vector connectionListeners;
    
    CLITransportPool() {
        this.closeFlag = false;
        this.connectionListeners = null;
        this.transportPool = new Hashtable();
        this.start();
    }
    
    synchronized void addProviderToPool(final CLITransportProvider cliTransportProvider, final CLIProtocolOptions cliProtocolOptions, final boolean b) throws Exception {
        final CLITransportElement cliTransportElement = new CLITransportElement(cliTransportProvider);
        final Object id = cliProtocolOptions.getID();
        final CLITransportGroup cliTransportGroup = this.transportPool.get(id);
        if (cliTransportGroup == null) {
            final CLITransportGroup cliTransportGroup2 = new CLITransportGroup();
            cliTransportGroup2.setCLIProtocolOptions(cliProtocolOptions);
            if (!b) {
                cliTransportElement.timeStamp = System.currentTimeMillis();
            }
            else {
                cliTransportElement.setUseFlag(true);
                cliTransportElement.dedicated = true;
            }
            cliTransportGroup2.addElement(cliTransportElement);
            this.transportPool.put(id, cliTransportGroup2);
        }
        else {
            if (!b) {
                cliTransportElement.timeStamp = System.currentTimeMillis();
            }
            else {
                cliTransportElement.setUseFlag(true);
                cliTransportElement.dedicated = true;
            }
            cliTransportGroup.addElement(cliTransportElement);
        }
    }
    
    synchronized void releaseProvider(final CLITransportProvider cliTransportProvider, final CLIProtocolOptions cliProtocolOptions) {
        final Enumeration transportElements = this.transportPool.get(cliProtocolOptions.getID()).getTransportElements();
        while (transportElements.hasMoreElements()) {
            final CLITransportElement cliTransportElement = transportElements.nextElement();
            if (cliTransportElement.getProvider() == cliTransportProvider) {
                cliTransportElement.setUseFlag(false);
                cliTransportElement.timeStamp = System.currentTimeMillis();
                break;
            }
        }
    }
    
    synchronized void removeProvider(final CLITransportProvider cliTransportProvider, final CLIProtocolOptions cliProtocolOptions) {
        final Object id = cliProtocolOptions.getID();
        final CLITransportGroup cliTransportGroup = this.transportPool.get(id);
        if (cliTransportGroup == null) {
            return;
        }
        final Enumeration transportElements = cliTransportGroup.getTransportElements();
        while (transportElements.hasMoreElements()) {
            final CLITransportElement cliTransportElement = transportElements.nextElement();
            if (cliTransportProvider == cliTransportElement.cliTransportProvider) {
                cliTransportGroup.removeElement(cliTransportElement);
                try {
                    cliTransportElement.cliTransportProvider.close();
                }
                catch (final Exception ex) {
                    System.out.println(ex);
                }
                if (cliTransportGroup.groupSize() != 0) {
                    continue;
                }
                this.transportPool.remove(id);
            }
        }
    }
    
    synchronized CLITransportProvider getProviderFromPool(final CLIProtocolOptions cliProtocolOptions) {
        final CLITransportGroup cliTransportGroup = this.transportPool.get(cliProtocolOptions.getID());
        if (cliTransportGroup == null) {
            return null;
        }
        final Enumeration transportElements = cliTransportGroup.getTransportElements();
        while (transportElements.hasMoreElements()) {
            final CLITransportElement cliTransportElement = transportElements.nextElement();
            if (!cliTransportElement.getUseFlag()) {
                cliTransportElement.setUseFlag(true);
                return cliTransportElement.getProvider();
            }
        }
        return null;
    }
    
    void setKeepAliveTimeout(final int keepAliveTimeout, final Object o) {
        final CLITransportGroup cliTransportGroup = this.transportPool.get(o);
        if (cliTransportGroup != null) {
            cliTransportGroup.keepAliveTimeout = keepAliveTimeout;
        }
    }
    
    int getKeepAliveTimeout(final Object o) {
        final CLITransportGroup cliTransportGroup = this.transportPool.get(o);
        if (cliTransportGroup != null) {
            return cliTransportGroup.keepAliveTimeout;
        }
        return -1;
    }
    
    synchronized void setMaxConnectionsForGroup(final int maxConnections, final CLIProtocolOptions cliProtocolOptions) {
        final Object id = cliProtocolOptions.getID();
        CLITransportGroup cliTransportGroup = this.transportPool.get(id);
        if (cliTransportGroup == null) {
            cliTransportGroup = new CLITransportGroup();
            this.transportPool.put(id, cliTransportGroup);
        }
        cliTransportGroup.maxConnections = maxConnections;
    }
    
    CLITransportGroup getCLIGroup(final CLIProtocolOptions cliProtocolOptions) {
        return this.transportPool.get(cliProtocolOptions.getID());
    }
    
    public void run() {
        while (true) {
            synchronized (this.transportPool) {
                if (this.closeFlag) {
                    return;
                }
                final Enumeration keys = this.transportPool.keys();
                while (keys.hasMoreElements()) {
                    final Object nextElement = keys.nextElement();
                    final CLITransportGroup cliTransportGroup = this.transportPool.get(nextElement);
                    this.connectionTimeout = cliTransportGroup.keepAliveTimeout;
                    final Enumeration transportElements = cliTransportGroup.getTransportElements();
                    while (transportElements.hasMoreElements()) {
                        final CLITransportElement cliTransportElement = transportElements.nextElement();
                        if (!cliTransportElement.getUseFlag()) {
                            if (cliTransportElement.dedicated) {
                                continue;
                            }
                            if ((System.currentTimeMillis() - cliTransportElement.timeStamp) / 1000L <= this.connectionTimeout) {
                                continue;
                            }
                            cliTransportGroup.removeElement(cliTransportElement);
                            try {
                                cliTransportElement.cliTransportProvider.close();
                                if (this.connectionListeners != null) {
                                    final Enumeration elements = this.connectionListeners.elements();
                                    while (elements.hasMoreElements()) {
                                        ((ConnectionListener)elements.nextElement()).connectionTimedOut(cliTransportGroup.getCLIProtocolOptions());
                                    }
                                }
                                cliTransportGroup.decrementConnectionCount();
                                CLIResourceManager.decrementConnectionCount();
                            }
                            catch (final Exception ex) {
                                System.out.println(ex);
                            }
                            if (cliTransportGroup.groupSize() != 0) {
                                continue;
                            }
                            this.transportPool.remove(nextElement);
                        }
                    }
                }
            }
            try {
                Thread.sleep(50L);
            }
            catch (final Exception ex2) {}
        }
    }
    
    public synchronized void addConnectionListener(final ConnectionListener connectionListener) {
        if (this.connectionListeners != null) {
            if (!this.connectionListeners.contains(connectionListener)) {
                this.connectionListeners.addElement(connectionListener);
            }
            else {
                CLILogMgr.CLIERR.fail("CLITransportPool: listener already exist, cannot add " + connectionListener, (Throwable)null);
            }
        }
        else {
            (this.connectionListeners = new Vector()).addElement(connectionListener);
        }
    }
    
    public synchronized void removeConnectionListener(final ConnectionListener connectionListener) {
        if (this.connectionListeners != null && this.connectionListeners.size() > 0) {
            this.connectionListeners.removeElement(connectionListener);
        }
    }
    
    public Vector getConnectionListeners() {
        return this.connectionListeners;
    }
    
    public void closeConnections() {
        synchronized (this.transportPool) {
            this.closeFlag = true;
            final Enumeration keys = this.transportPool.keys();
            while (keys.hasMoreElements()) {
                final Object nextElement = keys.nextElement();
                final CLITransportGroup cliTransportGroup = this.transportPool.get(nextElement);
                final Enumeration transportElements = cliTransportGroup.getTransportElements();
                while (transportElements.hasMoreElements()) {
                    final CLITransportElement cliTransportElement = transportElements.nextElement();
                    cliTransportGroup.removeElement(cliTransportElement);
                    if (cliTransportElement.dedicated) {
                        continue;
                    }
                    try {
                        cliTransportElement.cliTransportProvider.close();
                    }
                    catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                }
                this.transportPool.remove(nextElement);
            }
        }
    }
}
