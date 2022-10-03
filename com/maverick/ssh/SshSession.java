package com.maverick.ssh;

import java.io.OutputStream;
import java.io.InputStream;

public interface SshSession extends SshChannel
{
    public static final int EXITCODE_NOT_RECEIVED = Integer.MIN_VALUE;
    
    boolean startShell() throws SshException;
    
    SshClient getClient();
    
    boolean executeCommand(final String p0) throws SshException;
    
    boolean executeCommand(final String p0, final String p1) throws SshException;
    
    boolean requestPseudoTerminal(final String p0, final int p1, final int p2, final int p3, final int p4, final byte[] p5) throws SshException;
    
    boolean requestPseudoTerminal(final String p0, final int p1, final int p2, final int p3, final int p4, final PseudoTerminalModes p5) throws SshException;
    
    boolean requestPseudoTerminal(final String p0, final int p1, final int p2, final int p3, final int p4) throws SshException;
    
    InputStream getInputStream() throws SshIOException;
    
    OutputStream getOutputStream() throws SshIOException;
    
    InputStream getStderrInputStream() throws SshIOException;
    
    void close();
    
    int exitCode();
    
    void changeTerminalDimensions(final int p0, final int p1, final int p2, final int p3) throws SshException;
    
    boolean isClosed();
}
