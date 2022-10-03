package org.apache.commons.net;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.DatagramSocket;
import java.nio.charset.Charset;

public abstract class DatagramSocketClient
{
    private static final DatagramSocketFactory __DEFAULT_SOCKET_FACTORY;
    private Charset charset;
    protected int _timeout_;
    protected DatagramSocket _socket_;
    protected boolean _isOpen_;
    protected DatagramSocketFactory _socketFactory_;
    
    public DatagramSocketClient() {
        this.charset = Charset.defaultCharset();
        this._socket_ = null;
        this._timeout_ = 0;
        this._isOpen_ = false;
        this._socketFactory_ = DatagramSocketClient.__DEFAULT_SOCKET_FACTORY;
    }
    
    public void open() throws SocketException {
        (this._socket_ = this._socketFactory_.createDatagramSocket()).setSoTimeout(this._timeout_);
        this._isOpen_ = true;
    }
    
    public void open(final int port) throws SocketException {
        (this._socket_ = this._socketFactory_.createDatagramSocket(port)).setSoTimeout(this._timeout_);
        this._isOpen_ = true;
    }
    
    public void open(final int port, final InetAddress laddr) throws SocketException {
        (this._socket_ = this._socketFactory_.createDatagramSocket(port, laddr)).setSoTimeout(this._timeout_);
        this._isOpen_ = true;
    }
    
    public void close() {
        if (this._socket_ != null) {
            this._socket_.close();
        }
        this._socket_ = null;
        this._isOpen_ = false;
    }
    
    public boolean isOpen() {
        return this._isOpen_;
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
    
    public int getSoTimeout() throws SocketException {
        return this._socket_.getSoTimeout();
    }
    
    public int getLocalPort() {
        return this._socket_.getLocalPort();
    }
    
    public InetAddress getLocalAddress() {
        return this._socket_.getLocalAddress();
    }
    
    public void setDatagramSocketFactory(final DatagramSocketFactory factory) {
        if (factory == null) {
            this._socketFactory_ = DatagramSocketClient.__DEFAULT_SOCKET_FACTORY;
        }
        else {
            this._socketFactory_ = factory;
        }
    }
    
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
        __DEFAULT_SOCKET_FACTORY = new DefaultDatagramSocketFactory();
    }
}
