package com.adventnet.telnet.telnetwindow;

import java.rmi.Remote;
import java.rmi.Naming;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.rmi.server.UnicastRemoteObject;

public class TelnetProxyClientAPIImpl extends UnicastRemoteObject implements TelnetProxyClientAPI
{
    Hashtable reqHandlers;
    int unique_id;
    
    public TelnetProxyClientAPIImpl() throws RemoteException {
        this.reqHandlers = new Hashtable();
        this.unique_id = 0;
    }
    
    public int connect(final String s, final int n) throws IOException, RemoteException {
        return this.connect(s, n, null);
    }
    
    public int connect(final String s, final int n, final String s2) throws IOException, RemoteException {
        final TelnetRequestHandler telnetRequestHandler = new TelnetRequestHandler();
        telnetRequestHandler.connect(s, n, s2);
        final int n2 = this.unique_id++;
        if (this.unique_id >= Integer.MAX_VALUE) {
            this.unique_id = 0;
        }
        this.reqHandlers.put(new Integer(n2), telnetRequestHandler);
        return n2;
    }
    
    public byte[] readAsByteArray(final int n) throws IOException, RemoteException {
        final TelnetRequestHandler telnetRequestHandler = this.reqHandlers.get(new Integer(n));
        if (telnetRequestHandler != null) {
            return telnetRequestHandler.readAsByteArray();
        }
        return null;
    }
    
    public int read(final int n, final byte[] array) throws IOException, RemoteException {
        final TelnetRequestHandler telnetRequestHandler = this.reqHandlers.get(new Integer(n));
        int read = 0;
        if (telnetRequestHandler != null) {
            read = telnetRequestHandler.read(array);
        }
        return read;
    }
    
    public void disconnect(final int n) throws IOException, RemoteException {
        final TelnetRequestHandler telnetRequestHandler = this.reqHandlers.remove(new Integer(n));
        if (telnetRequestHandler != null) {
            telnetRequestHandler.disconnect();
        }
    }
    
    public void write(final int n, final byte b) throws IOException, RemoteException {
        final TelnetRequestHandler telnetRequestHandler = this.reqHandlers.get(new Integer(n));
        if (telnetRequestHandler != null) {
            telnetRequestHandler.write(b);
        }
    }
    
    public void write(final int n, final byte[] array) throws IOException, RemoteException {
        final TelnetRequestHandler telnetRequestHandler = this.reqHandlers.get(new Integer(n));
        if (telnetRequestHandler != null) {
            telnetRequestHandler.write(array);
        }
    }
    
    public void setSocketTimeout(final int n, final int n2) throws IOException, RemoteException {
        this.reqHandlers.get(new Integer(n)).telnetSession.setSocketTimeout(n2 * 1000);
    }
    
    public String login(final int n, final String s, final String s2) throws IOException, RemoteException {
        final TelnetRequestHandler telnetRequestHandler = this.reqHandlers.get(new Integer(n));
        if (telnetRequestHandler != null) {
            return telnetRequestHandler.login(s, s2);
        }
        return null;
    }
    
    public static void main(final String[] array) {
        int int1 = 1099;
        if (array.length == 1) {
            try {
                int1 = Integer.parseInt(array[0]);
            }
            catch (final Exception ex) {
                System.out.println("Exception while setting the RMI Port : " + ex.getMessage());
            }
        }
        try {
            Naming.rebind("rmi://localhost:" + int1 + "/TELNET", new TelnetProxyClientAPIImpl());
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
        }
    }
}
