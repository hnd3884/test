package com.adventnet.telnet.telnetwindow;

import java.io.IOException;
import com.adventnet.cli.ssh.sshwindow.SshInterfaceRMIImpl;

public abstract class TelnetClient
{
    TelnetInterface telnetInterface;
    boolean rmi;
    private static final int debug = 0;
    private byte[] tempbuf;
    private byte neg_state;
    private int connectionID;
    
    public TelnetClient(final String s) {
        this(s, 1099);
    }
    
    public TelnetClient(final String s, final int n) {
        this(s, n, false);
    }
    
    public TelnetClient(final String s, final int n, final boolean b) {
        this.rmi = false;
        this.tempbuf = new byte[0];
        this.neg_state = 0;
        this.connectionID = -1;
        if (s != null) {
            if (b) {
                this.telnetInterface = new SshInterfaceRMIImpl(s, n);
            }
            else {
                this.telnetInterface = new TelnetInterfaceRMIImpl(s, n);
            }
        }
        else {
            this.telnetInterface = new TelnetInterfaceDirectImpl();
        }
    }
    
    public void connect(final String s, final int n) throws IOException {
        this.telnetInterface.connect(s, n);
    }
    
    public void connect(final String s, final int n, final String s2) throws IOException {
        this.telnetInterface.connect(s, n, s2);
    }
    
    void setSocketTimeout(final int socketTimeout) throws Exception {
        this.telnetInterface.setSocketTimeout(socketTimeout);
    }
    
    public void disconnect() throws IOException {
        this.telnetInterface.disconnect();
    }
    
    public void write(final byte[] array) throws IOException {
        this.telnetInterface.write(array);
    }
    
    public int read(final byte[] array) throws IOException {
        return this.telnetInterface.read(array);
    }
    
    public String login(final String s, final String s2) throws IOException {
        return this.telnetInterface.login(s, s2);
    }
}
