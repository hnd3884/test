package sun.rmi.transport.proxy;

import java.net.SocketException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.InetAddress;
import java.io.IOException;
import java.net.SocketImpl;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;

class WrappedSocket extends Socket
{
    protected Socket socket;
    protected InputStream in;
    protected OutputStream out;
    
    public WrappedSocket(final Socket socket, final InputStream in, final OutputStream out) throws IOException {
        super((SocketImpl)null);
        this.in = null;
        this.out = null;
        this.socket = socket;
        this.in = in;
        this.out = out;
    }
    
    @Override
    public InetAddress getInetAddress() {
        return this.socket.getInetAddress();
    }
    
    @Override
    public InetAddress getLocalAddress() {
        return AccessController.doPrivileged((PrivilegedAction<InetAddress>)new PrivilegedAction<InetAddress>() {
            @Override
            public InetAddress run() {
                return WrappedSocket.this.socket.getLocalAddress();
            }
        });
    }
    
    @Override
    public int getPort() {
        return this.socket.getPort();
    }
    
    @Override
    public int getLocalPort() {
        return this.socket.getLocalPort();
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (this.in == null) {
            this.in = this.socket.getInputStream();
        }
        return this.in;
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (this.out == null) {
            this.out = this.socket.getOutputStream();
        }
        return this.out;
    }
    
    @Override
    public void setTcpNoDelay(final boolean tcpNoDelay) throws SocketException {
        this.socket.setTcpNoDelay(tcpNoDelay);
    }
    
    @Override
    public boolean getTcpNoDelay() throws SocketException {
        return this.socket.getTcpNoDelay();
    }
    
    @Override
    public void setSoLinger(final boolean b, final int n) throws SocketException {
        this.socket.setSoLinger(b, n);
    }
    
    @Override
    public int getSoLinger() throws SocketException {
        return this.socket.getSoLinger();
    }
    
    @Override
    public synchronized void setSoTimeout(final int soTimeout) throws SocketException {
        this.socket.setSoTimeout(soTimeout);
    }
    
    @Override
    public synchronized int getSoTimeout() throws SocketException {
        return this.socket.getSoTimeout();
    }
    
    @Override
    public synchronized void close() throws IOException {
        this.socket.close();
    }
    
    @Override
    public String toString() {
        return "Wrapped" + this.socket.toString();
    }
}
