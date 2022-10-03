package com.adventnet.telnet.telnetwindow;

import java.rmi.RemoteException;
import java.io.IOException;
import java.rmi.Remote;

public interface TelnetProxyClientAPI extends Remote
{
    int connect(final String p0, final int p1) throws IOException, RemoteException;
    
    int connect(final String p0, final int p1, final String p2) throws IOException, RemoteException;
    
    void setSocketTimeout(final int p0, final int p1) throws IOException, RemoteException;
    
    byte[] readAsByteArray(final int p0) throws IOException, RemoteException;
    
    void disconnect(final int p0) throws IOException, RemoteException;
    
    void write(final int p0, final byte p1) throws IOException, RemoteException;
    
    void write(final int p0, final byte[] p1) throws IOException, RemoteException;
    
    int read(final int p0, final byte[] p1) throws IOException, RemoteException;
    
    String login(final int p0, final String p1, final String p2) throws IOException, RemoteException;
}
