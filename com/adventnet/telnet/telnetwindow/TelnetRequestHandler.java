package com.adventnet.telnet.telnetwindow;

import java.net.SocketException;
import java.io.IOException;
import com.adventnet.telnet.TelnetSession;

public class TelnetRequestHandler
{
    protected TelnetSession telnetSession;
    protected String host;
    protected int port;
    private String error;
    private static byte[] one;
    
    TelnetRequestHandler() {
        this.port = 23;
        this.error = null;
    }
    
    public void connect(final String s, final int n) throws IOException {
        (this.telnetSession = new TelnetSession()).connect(s, n);
    }
    
    public void connect(final String s, final int n, final String terminalType) throws IOException {
        this.telnetSession = new TelnetSession();
        if (terminalType != null) {
            this.telnetSession.setTerminalType(terminalType);
        }
        this.telnetSession.connect(s, n);
    }
    
    public byte[] readAsByteArray() throws IOException {
        final byte[] array = new byte[256];
        final int read = this.telnetSession.read(array);
        if (read > 0) {
            final byte[] array2 = new byte[read];
            System.arraycopy(array, 0, array2, 0, read);
            return array2;
        }
        return null;
    }
    
    public int read(final byte[] array) throws IOException {
        final int read = this.telnetSession.read(array);
        if (read > 0) {
            return read;
        }
        return 0;
    }
    
    public void disconnect() throws IOException {
        this.telnetSession.disconnect();
    }
    
    public void write(final byte b) throws IOException {
        TelnetRequestHandler.one[0] = b;
        this.telnetSession.write(TelnetRequestHandler.one);
    }
    
    public void write(final byte[] array) throws IOException {
        final String s = "\n";
        final String s2 = new String(array);
        final String s3 = "\r\n";
        if (s2.equals(s)) {
            this.telnetSession.write(s3.getBytes());
        }
        else {
            this.telnetSession.write(array);
        }
    }
    
    public void setSocketTimeout(final int socketTimeout) throws IOException {
        try {
            this.telnetSession.setSocketTimeout(socketTimeout);
        }
        catch (final SocketException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public String login(final String s, final String s2) throws IOException {
        this.telnetSession.setLoginPrompt("login:");
        this.telnetSession.setPasswdPrompt("Password:");
        this.telnetSession.login(s, s2);
        return this.telnetSession.getLoginMessage();
    }
    
    static {
        TelnetRequestHandler.one = new byte[1];
    }
}
