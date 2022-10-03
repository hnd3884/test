package com.adventnet.cli.rmi;

import java.rmi.Remote;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import com.adventnet.cli.transport.CLIProtocolOptions;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CLIFactoryImpl extends UnicastRemoteObject implements CLIFactory
{
    CLIResourceManagerImpl cliResourceManager;
    
    public CLIFactoryImpl() throws RemoteException {
        this.cliResourceManager = null;
    }
    
    public CLISession createCLISession(final CLIProtocolOptions cliProtocolOptions, final boolean b) throws RemoteException {
        return new CLISessionImpl(cliProtocolOptions, b);
    }
    
    public CLISession createCLISession(final CLIProtocolOptions cliProtocolOptions) throws RemoteException {
        return new CLISessionImpl(cliProtocolOptions);
    }
    
    public CLIResourceManager createCLIResourceManager() throws RemoteException {
        if (this.cliResourceManager == null) {
            this.cliResourceManager = new CLIResourceManagerImpl();
        }
        return this.cliResourceManager;
    }
    
    public static void main(final String[] array) {
        System.setSecurityManager(new RMISecurityManager());
        try {
            final String s = "";
            final String[] array2 = { "-h", "-p" };
            final String[] array3 = { null, null };
            parseOptions(array, array2, array3);
            String s2;
            if (array3[0] != null) {
                s2 = s + array3[0];
            }
            else {
                s2 = s + "localhost";
            }
            final String string = s2 + ":";
            String s3;
            if (array3[1] != null) {
                s3 = string + array3[1];
            }
            else {
                s3 = string + "1099";
            }
            Naming.rebind("rmi://" + s3 + "/AdventnetCLIFactory", new CLIFactoryImpl());
            System.out.println("Factory is ready");
        }
        catch (final Exception ex) {
            System.out.println("CLIFactoryImpl: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    static void parseOptions(final String[] array, final String[] array2, final String[] array3) {
        boolean b = false;
        for (int i = 0; i < array2.length; ++i) {
            int j = 0;
            while (j < array.length) {
                if (array[j].equals(array2[i])) {
                    if (j++ < array.length) {
                        array3[i] = array[j];
                        break;
                    }
                    b = true;
                    break;
                }
                else {
                    ++j;
                }
            }
            if (b) {
                break;
            }
        }
    }
}
