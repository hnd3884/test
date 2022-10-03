package com.adventnet.cli.rmi;

import java.util.Properties;
import com.adventnet.cli.CLIMessage;
import com.adventnet.cli.transport.CLIProtocolOptions;
import java.rmi.RemoteException;
import java.rmi.Remote;

public interface CLISession extends Remote
{
    void open() throws RemoteException;
    
    void setTransportProviderClassName(final String p0) throws RemoteException;
    
    String getTransportProviderClassName() throws RemoteException;
    
    void setCLIProtocolOptions(final CLIProtocolOptions p0) throws RemoteException;
    
    CLIProtocolOptions getCLIProtocolOptions() throws RemoteException;
    
    void setCLIPrompt(final String p0) throws RemoteException;
    
    String getCLIPrompt() throws RemoteException;
    
    void setPooling(final boolean p0) throws RemoteException;
    
    boolean isSetPooling() throws RemoteException;
    
    void setMaxConnections(final int p0) throws RemoteException;
    
    int getMaxConnections() throws RemoteException;
    
    CLIMessage syncSend(final CLIMessage p0) throws RemoteException;
    
    void addCLIClient(final CLIClient p0) throws RemoteException;
    
    int send(final CLIMessage p0) throws RemoteException;
    
    void close() throws RemoteException;
    
    void setRequestTimeout(final int p0) throws RemoteException;
    
    int getRequestTimeout() throws RemoteException;
    
    void setKeepAliveTimeout(final int p0) throws RemoteException;
    
    int getKeepAliveTimeout() throws RemoteException;
    
    void setDebug(final boolean p0) throws RemoteException;
    
    boolean isSetDebug() throws RemoteException;
    
    void setDebugLevel(final int p0) throws RemoteException;
    
    int getDebugLevel() throws RemoteException;
    
    void setIgnoreSpecialCharacters(final boolean p0) throws RemoteException;
    
    boolean isSetIgnoreSpecialCharacters() throws RemoteException;
    
    String getInitialMessage() throws RemoteException;
    
    void removeCLIClient(final CLIClient p0) throws RemoteException;
    
    int getCLIClientsSize() throws RemoteException;
    
    void addConnectionListener(final ConnectionListener p0) throws RemoteException;
    
    void removeConnectionListener(final ConnectionListener p0) throws RemoteException;
    
    Properties getCLIPromptAction() throws RemoteException;
    
    void setCLIPromptAction(final Properties p0) throws RemoteException;
    
    String getInterruptCmd() throws RemoteException;
    
    void setInterruptCmd(final String p0) throws RemoteException;
    
    CLIResourceManager getResourceManager() throws RemoteException;
}
