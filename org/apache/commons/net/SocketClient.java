package org.apache.commons.net;

import java.io.Closeable;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.InetAddress;
import java.io.IOException;
import java.nio.charset.Charset;
import java.net.Proxy;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;

public abstract class SocketClient
{
    public static final String NETASCII_EOL = "\r\n";
    private static final SocketFactory __DEFAULT_SOCKET_FACTORY;
    private static final ServerSocketFactory __DEFAULT_SERVER_SOCKET_FACTORY;
    private ProtocolCommandSupport __commandSupport;
    protected int _timeout_;
    protected Socket _socket_;
    protected String _hostname_;
    protected int _defaultPort_;
    protected InputStream _input_;
    protected OutputStream _output_;
    protected SocketFactory _socketFactory_;
    protected ServerSocketFactory _serverSocketFactory_;
    private static final int DEFAULT_CONNECT_TIMEOUT = 0;
    protected int connectTimeout;
    private int receiveBufferSize;
    private int sendBufferSize;
    private Proxy connProxy;
    private Charset charset;
    
    public SocketClient() {
        this.connectTimeout = 0;
        this.receiveBufferSize = -1;
        this.sendBufferSize = -1;
        this.charset = Charset.defaultCharset();
        this._socket_ = null;
        this._hostname_ = null;
        this._input_ = null;
        this._output_ = null;
        this._timeout_ = 0;
        this._defaultPort_ = 0;
        this._socketFactory_ = SocketClient.__DEFAULT_SOCKET_FACTORY;
        this._serverSocketFactory_ = SocketClient.__DEFAULT_SERVER_SOCKET_FACTORY;
    }
    
    protected void _connectAction_() throws IOException {
        this._socket_.setSoTimeout(this._timeout_);
        this._input_ = this._socket_.getInputStream();
        this._output_ = this._socket_.getOutputStream();
    }
    
    public void connect(final InetAddress host, final int port) throws SocketException, IOException {
        this._hostname_ = null;
        this._connect(host, port, null, -1);
    }
    
    public void connect(final String hostname, final int port) throws SocketException, IOException {
        this._hostname_ = hostname;
        this._connect(InetAddress.getByName(hostname), port, null, -1);
    }
    
    public void connect(final InetAddress host, final int port, final InetAddress localAddr, final int localPort) throws SocketException, IOException {
        this._hostname_ = null;
        this._connect(host, port, localAddr, localPort);
    }
    
    private void _connect(final InetAddress host, final int port, final InetAddress localAddr, final int localPort) throws SocketException, IOException {
        this._socket_ = this._socketFactory_.createSocket();
        if (this.receiveBufferSize != -1) {
            this._socket_.setReceiveBufferSize(this.receiveBufferSize);
        }
        if (this.sendBufferSize != -1) {
            this._socket_.setSendBufferSize(this.sendBufferSize);
        }
        if (localAddr != null) {
            this._socket_.bind(new InetSocketAddress(localAddr, localPort));
        }
        this._socket_.connect(new InetSocketAddress(host, port), this.connectTimeout);
        this._connectAction_();
    }
    
    public void connect(final String hostname, final int port, final InetAddress localAddr, final int localPort) throws SocketException, IOException {
        this._hostname_ = hostname;
        this._connect(InetAddress.getByName(hostname), port, localAddr, localPort);
    }
    
    public void connect(final InetAddress host) throws SocketException, IOException {
        this._hostname_ = null;
        this.connect(host, this._defaultPort_);
    }
    
    public void connect(final String hostname) throws SocketException, IOException {
        this.connect(hostname, this._defaultPort_);
    }
    
    public void disconnect() throws IOException {
        this.closeQuietly(this._socket_);
        this.closeQuietly(this._input_);
        this.closeQuietly(this._output_);
        this._socket_ = null;
        this._hostname_ = null;
        this._input_ = null;
        this._output_ = null;
    }
    
    private void closeQuietly(final Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    private void closeQuietly(final Closeable close) {
        if (close != null) {
            try {
                close.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public boolean isConnected() {
        return this._socket_ != null && this._socket_.isConnected();
    }
    
    public boolean isAvailable() {
        if (this.isConnected()) {
            try {
                if (this._socket_.getInetAddress() == null) {
                    return false;
                }
                if (this._socket_.getPort() == 0) {
                    return false;
                }
                if (this._socket_.getRemoteSocketAddress() == null) {
                    return false;
                }
                if (this._socket_.isClosed()) {
                    return false;
                }
                if (this._socket_.isInputShutdown()) {
                    return false;
                }
                if (this._socket_.isOutputShutdown()) {
                    return false;
                }
                this._socket_.getInputStream();
                this._socket_.getOutputStream();
            }
            catch (final IOException ioex) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    public void setDefaultPort(final int port) {
        this._defaultPort_ = port;
    }
    
    public int getDefaultPort() {
        return this._defaultPort_;
    }
    
    public void setDefaultTimeout(final int timeout) {
        this._timeout_ = timeout;
    }
    
    public int getDefaultTimeout() {
        return this._timeout_;
    }
    
    public void setSoTimeout(final int timeout) throws SocketException {
        this._socket_.setSoTimeout(timeout);
    }
    
    public void setSendBufferSize(final int size) throws SocketException {
        this.sendBufferSize = size;
    }
    
    protected int getSendBufferSize() {
        return this.sendBufferSize;
    }
    
    public void setReceiveBufferSize(final int size) throws SocketException {
        this.receiveBufferSize = size;
    }
    
    protected int getReceiveBufferSize() {
        return this.receiveBufferSize;
    }
    
    public int getSoTimeout() throws SocketException {
        return this._socket_.getSoTimeout();
    }
    
    public void setTcpNoDelay(final boolean on) throws SocketException {
        this._socket_.setTcpNoDelay(on);
    }
    
    public boolean getTcpNoDelay() throws SocketException {
        return this._socket_.getTcpNoDelay();
    }
    
    public void setKeepAlive(final boolean keepAlive) throws SocketException {
        this._socket_.setKeepAlive(keepAlive);
    }
    
    public boolean getKeepAlive() throws SocketException {
        return this._socket_.getKeepAlive();
    }
    
    public void setSoLinger(final boolean on, final int val) throws SocketException {
        this._socket_.setSoLinger(on, val);
    }
    
    public int getSoLinger() throws SocketException {
        return this._socket_.getSoLinger();
    }
    
    public int getLocalPort() {
        return this._socket_.getLocalPort();
    }
    
    public InetAddress getLocalAddress() {
        return this._socket_.getLocalAddress();
    }
    
    public int getRemotePort() {
        return this._socket_.getPort();
    }
    
    public InetAddress getRemoteAddress() {
        return this._socket_.getInetAddress();
    }
    
    public boolean verifyRemote(final Socket socket) {
        final InetAddress host1 = socket.getInetAddress();
        final InetAddress host2 = this.getRemoteAddress();
        return host1.equals(host2);
    }
    
    public void setSocketFactory(final SocketFactory factory) {
        if (factory == null) {
            this._socketFactory_ = SocketClient.__DEFAULT_SOCKET_FACTORY;
        }
        else {
            this._socketFactory_ = factory;
        }
        this.connProxy = null;
    }
    
    public void setServerSocketFactory(final ServerSocketFactory factory) {
        if (factory == null) {
            this._serverSocketFactory_ = SocketClient.__DEFAULT_SERVER_SOCKET_FACTORY;
        }
        else {
            this._serverSocketFactory_ = factory;
        }
    }
    
    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public int getConnectTimeout() {
        return this.connectTimeout;
    }
    
    public ServerSocketFactory getServerSocketFactory() {
        return this._serverSocketFactory_;
    }
    
    public void addProtocolCommandListener(final ProtocolCommandListener listener) {
        this.getCommandSupport().addProtocolCommandListener(listener);
    }
    
    public void removeProtocolCommandListener(final ProtocolCommandListener listener) {
        this.getCommandSupport().removeProtocolCommandListener(listener);
    }
    
    protected void fireReplyReceived(final int replyCode, final String reply) {
        if (this.getCommandSupport().getListenerCount() > 0) {
            this.getCommandSupport().fireReplyReceived(replyCode, reply);
        }
    }
    
    protected void fireCommandSent(final String command, final String message) {
        if (this.getCommandSupport().getListenerCount() > 0) {
            this.getCommandSupport().fireCommandSent(command, message);
        }
    }
    
    protected void createCommandSupport() {
        this.__commandSupport = new ProtocolCommandSupport(this);
    }
    
    protected ProtocolCommandSupport getCommandSupport() {
        return this.__commandSupport;
    }
    
    public void setProxy(final Proxy proxy) {
        this.setSocketFactory(new DefaultSocketFactory(proxy));
        this.connProxy = proxy;
    }
    
    public Proxy getProxy() {
        return this.connProxy;
    }
    
    @Deprecated
    public String getCharsetName() {
        return this.charset.name();
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }
    
    static {
        __DEFAULT_SOCKET_FACTORY = SocketFactory.getDefault();
        __DEFAULT_SERVER_SOCKET_FACTORY = ServerSocketFactory.getDefault();
    }
}
