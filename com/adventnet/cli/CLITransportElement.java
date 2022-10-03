package com.adventnet.cli;

import com.adventnet.cli.transport.CLITransportProvider;

class CLITransportElement
{
    CLITransportProvider cliTransportProvider;
    long timeStamp;
    long priority;
    boolean inUse;
    boolean dedicated;
    
    CLITransportElement(final CLITransportProvider cliTransportProvider) {
        this.timeStamp = 0L;
        this.priority = 0L;
        this.inUse = false;
        this.dedicated = false;
        this.cliTransportProvider = cliTransportProvider;
    }
    
    CLITransportProvider getProvider() {
        return this.cliTransportProvider;
    }
    
    void setUseFlag(final boolean inUse) {
        this.inUse = inUse;
    }
    
    boolean getUseFlag() {
        return this.inUse;
    }
}
