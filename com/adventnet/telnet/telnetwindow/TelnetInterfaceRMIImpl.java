package com.adventnet.telnet.telnetwindow;

import java.io.IOException;
import java.rmi.Naming;

public class TelnetInterfaceRMIImpl implements TelnetInterface
{
    TelnetProxyClientAPI telnetp;
    int connectionID;
    
    TelnetInterfaceRMIImpl(final String s) {
        this(s, 1099);
    }
    
    TelnetInterfaceRMIImpl(final String s, final int n) {
        try {
            this.telnetp = (TelnetProxyClientAPI)Naming.lookup("rmi://" + s + ":" + n + "/TELNET");
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
            ex.printStackTrace();
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
        try {
            this.connectionID = this.telnetp.connect(s, n, s2);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public void setSocketTimeout(final int n) throws IOException {
        try {
            this.telnetp.setSocketTimeout(this.connectionID, n);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
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
