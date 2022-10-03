package com.zoho.cp;

import java.net.SocketException;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.channels.SocketChannel;
import java.net.InetAddress;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.Socket;

public class MeteredSocket extends Socket
{
    private Socket socket;
    private MeteredInputStream in;
    private MeteredOutputStream out;
    
    public MeteredSocket(final Socket socket) {
        this.socket = socket;
    }
    
    public long getBytesRead() {
        if (this.in == null) {
            return 0L;
        }
        return this.in.getBytesRead();
    }
    
    public int getBytesWritten() {
        if (this.out == null) {
            return 0;
        }
        return this.out.getBytesWritten();
    }
    
    @Override
    public int hashCode() {
        return this.socket.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.socket.equals(obj);
    }
    
    @Override
    public void connect(final SocketAddress endpoint) throws IOException {
        this.socket.connect(endpoint);
    }
    
    @Override
    public void connect(final SocketAddress endpoint, final int timeout) throws IOException {
        this.socket.connect(endpoint, timeout);
    }
    
    @Override
    public void bind(final SocketAddress bindpoint) throws IOException {
        this.socket.bind(bindpoint);
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
    public SocketAddress getRemoteSocketAddress() {
        return this.socket.getRemoteSocketAddress();
    }
    
    @Override
    public SocketAddress getLocalSocketAddress() {
        return this.socket.getLocalSocketAddress();
    }
    
    @Override
    public SocketChannel getChannel() {
        return this.socket.getChannel();
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (this.in == null) {
            this.in = new MeteredInputStream(this.socket.getInputStream());
        }
        return this.in;
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (this.out == null) {
            this.out = new MeteredOutputStream(this.socket.getOutputStream());
        }
        return this.out;
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
        this.socket.close();
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
    
    @Override
    public void setPerformancePreferences(final int connectionTime, final int latency, final int bandwidth) {
        this.socket.setPerformancePreferences(connectionTime, latency, bandwidth);
    }
}
