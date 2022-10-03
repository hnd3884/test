package com.sshtools.net;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import com.maverick.ssh.SocketTimeoutSupport;
import com.maverick.ssh.SshTransport;

public class SocketWrapper implements SshTransport, SocketTimeoutSupport
{
    protected Socket socket;
    
    public SocketWrapper(final Socket socket) {
        this.socket = socket;
    }
    
    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }
    
    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }
    
    public String getHost() {
        return (this.socket.getInetAddress() == null) ? "proxied" : this.socket.getInetAddress().getHostAddress();
    }
    
    public int getPort() {
        return this.socket.getPort();
    }
    
    public void close() throws IOException {
        this.socket.close();
    }
    
    public SshTransport duplicate() throws IOException {
        return new SocketWrapper(new Socket(this.getHost(), this.socket.getPort()));
    }
    
    public void setSoTimeout(final int soTimeout) throws IOException {
        this.socket.setSoTimeout(soTimeout);
    }
    
    public int getSoTimeout() throws IOException {
        return this.socket.getSoTimeout();
    }
}
