package com.sun.mail.util;

import java.lang.reflect.Method;
import java.io.FileDescriptor;
import java.util.Collections;
import java.util.Set;
import java.net.SocketOption;
import java.net.SocketException;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.channels.SocketChannel;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.net.Socket;

public class WriteTimeoutSocket extends Socket
{
    private final Socket socket;
    private final ScheduledExecutorService ses;
    private final int timeout;
    
    public WriteTimeoutSocket(final Socket socket, final int timeout) throws IOException {
        this.socket = socket;
        this.ses = Executors.newScheduledThreadPool(1);
        this.timeout = timeout;
    }
    
    public WriteTimeoutSocket(final int timeout) throws IOException {
        this(new Socket(), timeout);
    }
    
    public WriteTimeoutSocket(final InetAddress address, final int port, final int timeout) throws IOException {
        this(timeout);
        this.socket.connect(new InetSocketAddress(address, port));
    }
    
    public WriteTimeoutSocket(final InetAddress address, final int port, final InetAddress localAddress, final int localPort, final int timeout) throws IOException {
        this(timeout);
        this.socket.bind(new InetSocketAddress(localAddress, localPort));
        this.socket.connect(new InetSocketAddress(address, port));
    }
    
    public WriteTimeoutSocket(final String host, final int port, final int timeout) throws IOException {
        this(timeout);
        this.socket.connect(new InetSocketAddress(host, port));
    }
    
    public WriteTimeoutSocket(final String host, final int port, final InetAddress localAddress, final int localPort, final int timeout) throws IOException {
        this(timeout);
        this.socket.bind(new InetSocketAddress(localAddress, localPort));
        this.socket.connect(new InetSocketAddress(host, port));
    }
    
    @Override
    public void connect(final SocketAddress remote) throws IOException {
        this.socket.connect(remote, 0);
    }
    
    @Override
    public void connect(final SocketAddress remote, final int timeout) throws IOException {
        this.socket.connect(remote, timeout);
    }
    
    @Override
    public void bind(final SocketAddress local) throws IOException {
        this.socket.bind(local);
    }
    
    @Override
    public SocketAddress getRemoteSocketAddress() {
        return this.socket.getRemoteSocketAddress();
    }
    
    @Override
    public SocketAddress getLocalSocketAddress() {
        return this.socket.getLocalSocketAddress();
    }
    
    @Override
    public void setPerformancePreferences(final int connectionTime, final int latency, final int bandwidth) {
        this.socket.setPerformancePreferences(connectionTime, latency, bandwidth);
    }
    
    @Override
    public SocketChannel getChannel() {
        return this.socket.getChannel();
    }
    
    @Override
    public InetAddress getInetAddress() {
        return this.socket.getInetAddress();
    }
    
    @Override
    public InetAddress getLocalAddress() {
        return this.socket.getLocalAddress();
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
        return this.socket.getInputStream();
    }
    
    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        return new TimeoutOutputStream(this.socket.getOutputStream(), this.ses, this.timeout);
    }
    
    @Override
    public void setTcpNoDelay(final boolean on) throws SocketException {
        this.socket.setTcpNoDelay(on);
    }
    
    @Override
    public boolean getTcpNoDelay() throws SocketException {
        return this.socket.getTcpNoDelay();
    }
    
    @Override
    public void setSoLinger(final boolean on, final int linger) throws SocketException {
        this.socket.setSoLinger(on, linger);
    }
    
    @Override
    public int getSoLinger() throws SocketException {
        return this.socket.getSoLinger();
    }
    
    @Override
    public void sendUrgentData(final int data) throws IOException {
        this.socket.sendUrgentData(data);
    }
    
    @Override
    public void setOOBInline(final boolean on) throws SocketException {
        this.socket.setOOBInline(on);
    }
    
    @Override
    public boolean getOOBInline() throws SocketException {
        return this.socket.getOOBInline();
    }
    
    @Override
    public void setSoTimeout(final int timeout) throws SocketException {
        this.socket.setSoTimeout(timeout);
    }
    
    @Override
    public int getSoTimeout() throws SocketException {
        return this.socket.getSoTimeout();
    }
    
    @Override
    public void setSendBufferSize(final int size) throws SocketException {
        this.socket.setSendBufferSize(size);
    }
    
    @Override
    public int getSendBufferSize() throws SocketException {
        return this.socket.getSendBufferSize();
    }
    
    @Override
    public void setReceiveBufferSize(final int size) throws SocketException {
        this.socket.setReceiveBufferSize(size);
    }
    
    @Override
    public int getReceiveBufferSize() throws SocketException {
        return this.socket.getReceiveBufferSize();
    }
    
    @Override
    public void setKeepAlive(final boolean on) throws SocketException {
        this.socket.setKeepAlive(on);
    }
    
    @Override
    public boolean getKeepAlive() throws SocketException {
        return this.socket.getKeepAlive();
    }
    
    @Override
    public void setTrafficClass(final int tc) throws SocketException {
        this.socket.setTrafficClass(tc);
    }
    
    @Override
    public int getTrafficClass() throws SocketException {
        return this.socket.getTrafficClass();
    }
    
    @Override
    public void setReuseAddress(final boolean on) throws SocketException {
        this.socket.setReuseAddress(on);
    }
    
    @Override
    public boolean getReuseAddress() throws SocketException {
        return this.socket.getReuseAddress();
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.socket.close();
        }
        finally {
            this.ses.shutdownNow();
        }
    }
    
    @Override
    public void shutdownInput() throws IOException {
        this.socket.shutdownInput();
    }
    
    @Override
    public void shutdownOutput() throws IOException {
        this.socket.shutdownOutput();
    }
    
    @Override
    public String toString() {
        return this.socket.toString();
    }
    
    @Override
    public boolean isConnected() {
        return this.socket.isConnected();
    }
    
    @Override
    public boolean isBound() {
        return this.socket.isBound();
    }
    
    @Override
    public boolean isClosed() {
        return this.socket.isClosed();
    }
    
    @Override
    public boolean isInputShutdown() {
        return this.socket.isInputShutdown();
    }
    
    @Override
    public boolean isOutputShutdown() {
        return this.socket.isOutputShutdown();
    }
    
    public <T> Socket setOption(final SocketOption<T> so, final T val) throws IOException {
        throw new UnsupportedOperationException("WriteTimeoutSocket.setOption");
    }
    
    public <T> T getOption(final SocketOption<T> so) throws IOException {
        throw new UnsupportedOperationException("WriteTimeoutSocket.getOption");
    }
    
    public Set<SocketOption<?>> supportedOptions() {
        return Collections.emptySet();
    }
    
    public FileDescriptor getFileDescriptor$() {
        try {
            final Method m = Socket.class.getDeclaredMethod("getFileDescriptor$", (Class<?>[])new Class[0]);
            return (FileDescriptor)m.invoke(this.socket, new Object[0]);
        }
        catch (final Exception ex) {
            return null;
        }
    }
}
