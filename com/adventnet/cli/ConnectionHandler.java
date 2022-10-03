package com.adventnet.cli;

import com.adventnet.cli.transport.CLITransportProvider;

public interface ConnectionHandler
{
    void preConnect(final CLISession p0);
    
    void postConnect(final CLITransportProvider p0);
    
    void postLogin(final CLISession p0);
    
    void preDisconnect(final CLISession p0);
    
    void postDisconnect(final CLISession p0);
}
