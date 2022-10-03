package com.adventnet.cli;

import java.util.Enumeration;
import com.adventnet.cli.transport.CLIProtocolOptions;
import java.util.Vector;

class CLITransportGroup
{
    int keepAliveTimeout;
    int maxConnections;
    Vector transportElementList;
    int connectionCount;
    CLIProtocolOptions cliProtocolOpts;
    
    CLITransportGroup() {
        this.keepAliveTimeout = 60;
        this.maxConnections = 1;
        this.connectionCount = 0;
        this.cliProtocolOpts = null;
        this.transportElementList = new Vector();
        ++this.connectionCount;
    }
    
    synchronized Enumeration getTransportElements() {
        return this.transportElementList.elements();
    }
    
    synchronized void incrementConnectionCount() {
        ++this.connectionCount;
    }
    
    synchronized void decrementConnectionCount() {
        if (this.connectionCount > 0) {
            --this.connectionCount;
        }
    }
    
    synchronized boolean isMaxConnectionsReached(final boolean b) {
        if (this.maxConnections == 0) {
            ++this.connectionCount;
            return false;
        }
        if (b) {
            if (this.groupSize() == this.maxConnections) {
                return true;
            }
            ++this.connectionCount;
            return false;
        }
        else {
            if (++this.connectionCount > this.maxConnections) {
                --this.connectionCount;
                return true;
            }
            return false;
        }
    }
    
    synchronized int groupSize() {
        return this.transportElementList.size();
    }
    
    synchronized void addElement(final CLITransportElement cliTransportElement) {
        this.transportElementList.addElement(cliTransportElement);
    }
    
    synchronized void removeElement(final CLITransportElement cliTransportElement) {
        this.transportElementList.removeElement(cliTransportElement);
    }
    
    public CLIProtocolOptions getCLIProtocolOptions() {
        return this.cliProtocolOpts;
    }
    
    public void setCLIProtocolOptions(final CLIProtocolOptions cliProtocolOpts) {
        this.cliProtocolOpts = cliProtocolOpts;
    }
}
