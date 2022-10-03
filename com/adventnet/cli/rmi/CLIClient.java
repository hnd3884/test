package com.adventnet.cli.rmi;

import java.rmi.RemoteException;
import com.adventnet.cli.CLIMessage;
import java.rmi.Remote;

public interface CLIClient extends Remote
{
    boolean callback(final CLISession p0, final CLIMessage p1, final int p2) throws RemoteException;
}
