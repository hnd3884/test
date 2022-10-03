package com.adventnet.cli.ssh.sshwindow;

import java.io.IOException;
import java.rmi.Naming;
import com.adventnet.telnet.telnetwindow.TelnetProxyClientAPI;
import com.adventnet.telnet.telnetwindow.TelnetInterface;

public class SshInterfaceRMIImpl implements TelnetInterface
{
    private TelnetProxyClientAPI telnetp;
    private int connectionID;
    
    public SshInterfaceRMIImpl(final String s) {
        this(s, 1099);
    }
    
    public SshInterfaceRMIImpl(final String s, final int n) {
        try {
            this.telnetp = (TelnetProxyClientAPI)Naming.lookup("rmi://" + s + ":" + n + "/SSH");
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void write(final byte[] array) throws IOException {
        try {
            this.telnetp.write(this.connectionID, array);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public void connect(final String s, final int n) throws IOException {
        try {
            this.connectionID = this.telnetp.connect(s, n);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public void connect(final String s, final int n, final String s2) throws IOException {
        this.connectionID = this.telnetp.connect(s, n, s2);
    }
    
    public void setSocketTimeout(final int n) throws IOException {
        try {
            this.telnetp.setSocketTimeout(this.connectionID, n);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public void disconnect() throws IOException {
        try {
            this.telnetp.disconnect(this.connectionID);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public int read(final byte[] array) throws IOException {
        byte[] asByteArray;
        try {
            asByteArray = this.telnetp.readAsByteArray(this.connectionID);
            if (asByteArray == null) {
                return 0;
            }
            System.arraycopy(asByteArray, 0, array, 0, asByteArray.length);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
        return asByteArray.length;
    }
    
    public String login(final String s, final String s2) throws IOException {
        return this.telnetp.login(this.connectionID, s, s2);
    }
}
