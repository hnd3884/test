package com.adventnet.telnet.telnetwindow;

import java.io.IOException;

public class TelnetInterfaceDirectImpl implements TelnetInterface
{
    TelnetRequestHandler handler;
    
    TelnetInterfaceDirectImpl() {
        this.handler = new TelnetRequestHandler();
    }
    
    public void write(final byte[] array) throws IOException {
        try {
            this.handler.write(array);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public void connect(final String s, final int n) throws IOException {
        try {
            this.handler.connect(s, n);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public void connect(final String s, final int n, final String s2) throws IOException {
        try {
            this.handler.connect(s, n, s2);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public void setSocketTimeout(final int n) throws IOException {
        try {
            this.handler.telnetSession.setSocketTimeout(n * 1000);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public void disconnect() throws IOException {
        try {
            this.handler.disconnect();
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public int read(final byte[] array) throws IOException {
        byte[] asByteArray;
        try {
            asByteArray = this.handler.readAsByteArray();
            System.arraycopy(asByteArray, 0, array, 0, asByteArray.length);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
        return asByteArray.length;
    }
    
    public String login(final String s, final String s2) throws IOException {
        return this.handler.login(s, s2);
    }
}
