package com.adventnet.cli.ssh.sshwindow;

import java.rmi.Remote;
import java.rmi.Naming;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI;
import java.rmi.server.UnicastRemoteObject;

public class SshProxyClientAPIImpl extends UnicastRemoteObject implements TelnetProxyClientAPI
{
    Hashtable reqHandlers;
    int unique_id;
    
    public SshProxyClientAPIImpl() throws RemoteException {
        this.reqHandlers = new Hashtable();
        this.unique_id = 0;
    }
    
    public int connect(final String s, final int n) throws IOException, RemoteException {
        return this.connect(s, n, null);
    }
    
    public int connect(final String s, final int n, final String s2) throws IOException, RemoteException {
        final SshRequestHandler sshRequestHandler = new SshRequestHandler();
        sshRequestHandler.connect(s, n, s2);
        final int n2 = this.unique_id++;
        if (this.unique_id >= Integer.MAX_VALUE) {
            this.unique_id = 0;
        }
        this.reqHandlers.put(new Integer(n2), sshRequestHandler);
        return n2;
    }
    
    public byte[] readAsByteArray(final int n) throws IOException, RemoteException {
        final SshRequestHandler sshRequestHandler = this.reqHandlers.get(new Integer(n));
        if (sshRequestHandler != null) {
            return sshRequestHandler.readAsByteArray();
        }
        return null;
    }
    
    public int read(final int n, final byte[] array) throws IOException, RemoteException {
        final SshRequestHandler sshRequestHandler = this.reqHandlers.get(new Integer(n));
        int read = 0;
        if (sshRequestHandler != null) {
            read = sshRequestHandler.read(array);
        }
        return read;
    }
    
    public void disconnect(final int n) throws IOException, RemoteException {
        final SshRequestHandler sshRequestHandler = this.reqHandlers.remove(new Integer(n));
        if (sshRequestHandler != null) {
            sshRequestHandler.disconnect();
        }
    }
    
    public void write(final int n, final byte b) throws IOException, RemoteException {
        final SshRequestHandler sshRequestHandler = this.reqHandlers.get(new Integer(n));
        if (sshRequestHandler != null) {
            sshRequestHandler.write(b);
        }
    }
    
    public void write(final int n, final byte[] array) throws IOException, RemoteException {
        final SshRequestHandler sshRequestHandler = this.reqHandlers.get(new Integer(n));
        if (sshRequestHandler != null) {
            sshRequestHandler.write(array);
        }
    }
    
    public void setSocketTimeout(final int n, final int n2) throws IOException, RemoteException {
        this.reqHandlers.get(new Integer(n)).setSocketTimeout(n2 * 1000);
    }
    
    public String login(final int n, final String s, final String s2) throws IOException, RemoteException {
        return this.reqHandlers.get(new Integer(n)).login(s, s2);
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
            Naming.rebind("rmi://localhost:" + int1 + "/SSH", new SshProxyClientAPIImpl());
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
        }
    }
}
