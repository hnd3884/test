package com.adventnet.cli.rmi;

import java.rmi.RemoteException;
import com.adventnet.cli.transport.CLIProtocolOptions;
import java.rmi.Remote;

public interface ConnectionListener extends Remote
{
    void connectionTimedOut(final CLIProtocolOptions p0) throws RemoteException;
    
    void connectionDown(final CLIProtocolOptions p0) throws RemoteException;
}
