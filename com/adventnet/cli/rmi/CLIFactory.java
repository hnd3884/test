package com.adventnet.cli.rmi;

import java.rmi.RemoteException;
import com.adventnet.cli.transport.CLIProtocolOptions;
import java.rmi.Remote;

public interface CLIFactory extends Remote
{
    CLISession createCLISession(final CLIProtocolOptions p0, final boolean p1) throws RemoteException;
    
    CLISession createCLISession(final CLIProtocolOptions p0) throws RemoteException;
    
    CLIResourceManager createCLIResourceManager() throws RemoteException;
}
