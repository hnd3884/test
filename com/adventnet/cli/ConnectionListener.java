package com.adventnet.cli;

import com.adventnet.cli.transport.CLIProtocolOptions;

public interface ConnectionListener
{
    void connectionTimedOut(final CLIProtocolOptions p0);
    
    void connectionDown(final CLIProtocolOptions p0);
}
