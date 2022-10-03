package com.sshtools.util;

import com.maverick.ssh.ChannelEventListener;
import java.io.OutputStream;
import java.io.InputStream;
import com.maverick.ssh.PseudoTerminalModes;
import com.maverick.ssh.message.SshMessageRouter;
import com.maverick.ssh.SshClient;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshIOException;
import com.maverick.util.IOStreamConnector;
import com.maverick.util.DynamicBuffer;
import com.maverick.ssh.SshSession;

public class BufferedSession implements SshSession
{
    SshSession gc;
    DynamicBuffer fc;
    DynamicBuffer ec;
    IOStreamConnector dc;
    IOStreamConnector cc;
    
    public BufferedSession(final SshSession gc) throws SshException {
        this.fc = new DynamicBuffer();
        this.ec = new DynamicBuffer();
        try {
            this.gc = gc;
            this.dc = new IOStreamConnector(gc.getInputStream(), this.fc.getOutputStream());
            this.cc = new IOStreamConnector(gc.getStderrInputStream(), this.ec.getOutputStream());
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
    }
    
    public SshClient getClient() {
        return this.gc.getClient();
    }
    
    public void setAutoConsumeInput(final boolean autoConsumeInput) {
        this.gc.setAutoConsumeInput(autoConsumeInput);
    }
    
    public boolean startShell() throws SshException {
        return this.gc.startShell();
    }
    
    public SshMessageRouter getMessageRouter() {
        return this.gc.getMessageRouter();
    }
    
    public boolean executeCommand(final String s) throws SshException {
        return this.gc.executeCommand(s);
    }
    
    public boolean executeCommand(final String s, final String s2) throws SshException {
        return this.gc.executeCommand(s, s2);
    }
    
    public boolean requestPseudoTerminal(final String s, final int n, final int n2, final int n3, final int n4, final byte[] array) throws SshException {
        return this.gc.requestPseudoTerminal(s, n, n2, n3, n4, array);
    }
    
    public boolean requestPseudoTerminal(final String s, final int n, final int n2, final int n3, final int n4, final PseudoTerminalModes pseudoTerminalModes) throws SshException {
        return this.gc.requestPseudoTerminal(s, n, n2, n3, n4, pseudoTerminalModes);
    }
    
    public boolean requestPseudoTerminal(final String s, final int n, final int n2, final int n3, final int n4) throws SshException {
        return this.gc.requestPseudoTerminal(s, n, n2, n3, n4);
    }
    
    public InputStream getInputStream() throws SshIOException {
        return this.fc.getInputStream();
    }
    
    public OutputStream getOutputStream() throws SshIOException {
        return this.gc.getOutputStream();
    }
    
    public InputStream getStderrInputStream() throws SshIOException {
        return this.ec.getInputStream();
    }
    
    public void close() {
        this.fc.close();
        this.ec.close();
        this.gc.close();
    }
    
    public int exitCode() {
        return this.gc.exitCode();
    }
    
    public void changeTerminalDimensions(final int n, final int n2, final int n3, final int n4) throws SshException {
        this.gc.changeTerminalDimensions(n, n2, n3, n4);
    }
    
    public boolean isClosed() {
        return this.gc.isClosed();
    }
    
    public int getChannelId() {
        return this.gc.getChannelId();
    }
    
    public void addChannelEventListener(final ChannelEventListener channelEventListener) {
        this.gc.addChannelEventListener(channelEventListener);
    }
}
