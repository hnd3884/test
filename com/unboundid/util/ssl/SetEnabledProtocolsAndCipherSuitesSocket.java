package com.unboundid.util.ssl;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import java.net.SocketException;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.channels.SocketChannel;
import java.net.InetAddress;
import java.io.IOException;
import java.net.SocketAddress;
import com.unboundid.util.Validator;
import java.net.Socket;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.InternalUseOnly;
import javax.net.ssl.SSLSocket;

@InternalUseOnly
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class SetEnabledProtocolsAndCipherSuitesSocket extends SSLSocket
{
    private final Set<String> cipherSuites;
    private final Set<String> protocols;
    private final SSLSocket delegateSocket;
    
    SetEnabledProtocolsAndCipherSuitesSocket(final Socket delegateSocket, final Set<String> protocols, final Set<String> cipherSuites) {
        Validator.ensureTrue(delegateSocket instanceof SSLSocket);
        Validator.ensureFalse(delegateSocket.isConnected());
        this.delegateSocket = (SSLSocket)delegateSocket;
        this.protocols = protocols;
        this.cipherSuites = cipherSuites;
    }
    
    @Override
    public void connect(final SocketAddress endpoint) throws IOException {
        this.connect(endpoint, 0);
    }
    
    @Override
    public void connect(final SocketAddress endpoint, final int timeout) throws IOException {
        this.delegateSocket.connect(endpoint, timeout);
        SSLUtil.applyEnabledSSLProtocols(this.delegateSocket, this.protocols);
        SSLUtil.applyEnabledSSLCipherSuites(this.delegateSocket, this.cipherSuites);
    }
    
    @Override
    public void bind(final SocketAddress bindpoint) throws IOException {
        this.delegateSocket.bind(bindpoint);
    }
    
    @Override
    public InetAddress getInetAddress() {
        return this.delegateSocket.getInetAddress();
    }
    
    @Override
    public InetAddress getLocalAddress() {
        return this.delegateSocket.getLocalAddress();
    }
    
    @Override
    public int getPort() {
        return this.delegateSocket.getPort();
    }
    
    @Override
    public int getLocalPort() {
        return this.delegateSocket.getLocalPort();
    }
    
    @Override
    public SocketAddress getRemoteSocketAddress() {
        return this.delegateSocket.getRemoteSocketAddress();
    }
    
    @Override
    public SocketAddress getLocalSocketAddress() {
        return this.delegateSocket.getLocalSocketAddress();
    }
    
    @Override
    public SocketChannel getChannel() {
        return this.delegateSocket.getChannel();
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return this.delegateSocket.getInputStream();
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.delegateSocket.getOutputStream();
    }
    
    @Override
    public void setTcpNoDelay(final boolean on) throws SocketException {
        this.delegateSocket.setTcpNoDelay(on);
    }
    
    @Override
    public boolean getTcpNoDelay() throws SocketException {
        return this.delegateSocket.getTcpNoDelay();
    }
    
    @Override
    public void setSoLinger(final boolean on, final int linger) throws SocketException {
        this.delegateSocket.setSoLinger(on, linger);
    }
    
    @Override
    public int getSoLinger() throws SocketException {
        return this.delegateSocket.getSoLinger();
    }
    
    @Override
    public void sendUrgentData(final int data) throws IOException {
        throw new SocketException(SSLMessages.ERR_SET_ENABLED_PROTOCOLS_SOCKET_URGENT_DATA_NOT_SUPPORTED.get());
    }
    
    @Override
    public void setOOBInline(final boolean on) throws SocketException {
        throw new SocketException(SSLMessages.ERR_SET_ENABLED_PROTOCOLS_SOCKET_URGENT_DATA_NOT_SUPPORTED.get());
    }
    
    @Override
    public boolean getOOBInline() throws SocketException {
        throw new SocketException(SSLMessages.ERR_SET_ENABLED_PROTOCOLS_SOCKET_URGENT_DATA_NOT_SUPPORTED.get());
    }
    
    @Override
    public void setSoTimeout(final int timeout) throws SocketException {
        this.delegateSocket.setSoTimeout(timeout);
    }
    
    @Override
    public int getSoTimeout() throws SocketException {
        return this.delegateSocket.getSoTimeout();
    }
    
    @Override
    public void setSendBufferSize(final int size) throws SocketException {
        this.delegateSocket.setSendBufferSize(size);
    }
    
    @Override
    public int getSendBufferSize() throws SocketException {
        return this.delegateSocket.getSendBufferSize();
    }
    
    @Override
    public void setReceiveBufferSize(final int size) throws SocketException {
        this.delegateSocket.setReceiveBufferSize(size);
    }
    
    @Override
    public int getReceiveBufferSize() throws SocketException {
        return this.delegateSocket.getReceiveBufferSize();
    }
    
    @Override
    public void setKeepAlive(final boolean on) throws SocketException {
        this.delegateSocket.setKeepAlive(on);
    }
    
    @Override
    public boolean getKeepAlive() throws SocketException {
        return this.delegateSocket.getKeepAlive();
    }
    
    @Override
    public void setTrafficClass(final int tc) throws SocketException {
        this.delegateSocket.setTrafficClass(tc);
    }
    
    @Override
    public int getTrafficClass() throws SocketException {
        return this.delegateSocket.getTrafficClass();
    }
    
    @Override
    public void setReuseAddress(final boolean on) throws SocketException {
        this.delegateSocket.setReuseAddress(on);
    }
    
    @Override
    public boolean getReuseAddress() throws SocketException {
        return this.delegateSocket.getReuseAddress();
    }
    
    @Override
    public void close() throws IOException {
        this.delegateSocket.close();
    }
    
    @Override
    public void shutdownInput() throws IOException {
        throw new UnsupportedOperationException(SSLMessages.ERR_SET_ENABLED_PROTOCOLS_SOCKET_SHUTDOWN_INPUT.get());
    }
    
    @Override
    public void shutdownOutput() throws IOException {
        throw new UnsupportedOperationException(SSLMessages.ERR_SET_ENABLED_PROTOCOLS_SOCKET_SHUTDOWN_OUTPUT.get());
    }
    
    @Override
    public boolean isConnected() {
        return this.delegateSocket.isConnected();
    }
    
    @Override
    public boolean isBound() {
        return this.delegateSocket.isBound();
    }
    
    @Override
    public boolean isClosed() {
        return this.delegateSocket.isClosed();
    }
    
    @Override
    public boolean isInputShutdown() {
        return this.delegateSocket.isInputShutdown();
    }
    
    @Override
    public boolean isOutputShutdown() {
        return this.delegateSocket.isOutputShutdown();
    }
    
    @Override
    public void setPerformancePreferences(final int connectionTime, final int latency, final int bandwidth) {
        this.delegateSocket.setPerformancePreferences(connectionTime, latency, bandwidth);
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return this.delegateSocket.getSupportedCipherSuites();
    }
    
    @Override
    public String[] getEnabledCipherSuites() {
        return this.delegateSocket.getEnabledCipherSuites();
    }
    
    @Override
    public void setEnabledCipherSuites(final String[] suites) {
        this.delegateSocket.setEnabledCipherSuites(suites);
    }
    
    @Override
    public String[] getSupportedProtocols() {
        return this.delegateSocket.getSupportedProtocols();
    }
    
    @Override
    public String[] getEnabledProtocols() {
        return this.delegateSocket.getEnabledProtocols();
    }
    
    @Override
    public void setEnabledProtocols(final String[] protocols) {
        this.delegateSocket.setEnabledProtocols(protocols);
    }
    
    @Override
    public SSLSession getSession() {
        return this.delegateSocket.getSession();
    }
    
    @Override
    public void addHandshakeCompletedListener(final HandshakeCompletedListener listener) {
        this.delegateSocket.addHandshakeCompletedListener(listener);
    }
    
    @Override
    public void removeHandshakeCompletedListener(final HandshakeCompletedListener listener) {
        this.delegateSocket.removeHandshakeCompletedListener(listener);
    }
    
    @Override
    public void startHandshake() throws IOException {
        this.delegateSocket.startHandshake();
    }
    
    @Override
    public void setUseClientMode(final boolean mode) {
        this.delegateSocket.setUseClientMode(mode);
    }
    
    @Override
    public boolean getUseClientMode() {
        return this.delegateSocket.getUseClientMode();
    }
    
    @Override
    public void setNeedClientAuth(final boolean need) {
        this.delegateSocket.setNeedClientAuth(need);
    }
    
    @Override
    public boolean getNeedClientAuth() {
        return this.delegateSocket.getNeedClientAuth();
    }
    
    @Override
    public void setWantClientAuth(final boolean want) {
        this.delegateSocket.setWantClientAuth(want);
    }
    
    @Override
    public boolean getWantClientAuth() {
        return this.delegateSocket.getWantClientAuth();
    }
    
    @Override
    public void setEnableSessionCreation(final boolean flag) {
        this.delegateSocket.setEnableSessionCreation(flag);
    }
    
    @Override
    public boolean getEnableSessionCreation() {
        return this.delegateSocket.getEnableSessionCreation();
    }
    
    @Override
    public String toString() {
        return this.delegateSocket.toString();
    }
}
