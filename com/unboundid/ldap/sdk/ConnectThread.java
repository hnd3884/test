package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import javax.net.ssl.SSLSocket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import com.unboundid.util.Debug;
import java.net.InetAddress;
import javax.net.SocketFactory;
import java.util.concurrent.CountDownLatch;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;

final class ConnectThread extends Thread
{
    private final AtomicBoolean connected;
    private final AtomicReference<Socket> socket;
    private final AtomicReference<Thread> thread;
    private final AtomicReference<Throwable> exception;
    private final CountDownLatch startLatch;
    private final int connectTimeoutMillis;
    private final int port;
    private final SocketFactory socketFactory;
    private final InetAddress address;
    
    ConnectThread(final SocketFactory socketFactory, final InetAddress address, final int port, final int connectTimeoutMillis) {
        super("Background connect thread for " + address + ':' + port);
        this.setDaemon(true);
        this.socketFactory = socketFactory;
        this.address = address;
        this.port = port;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.connected = new AtomicBoolean(false);
        this.socket = new AtomicReference<Socket>();
        this.thread = new AtomicReference<Thread>();
        this.exception = new AtomicReference<Throwable>();
        this.startLatch = new CountDownLatch(1);
    }
    
    @Override
    public void run() {
        this.thread.set(Thread.currentThread());
        this.startLatch.countDown();
        try {
            Socket s;
            boolean connectNeeded;
            try {
                s = this.socketFactory.createSocket();
                connectNeeded = true;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                s = this.socketFactory.createSocket(this.address, this.port);
                connectNeeded = false;
            }
            this.socket.set(s);
            if (connectNeeded) {
                s.connect(new InetSocketAddress(this.address, this.port), this.connectTimeoutMillis);
            }
            this.connected.set(true);
            if (s instanceof SSLSocket) {
                try {
                    ((SSLSocket)s).startHandshake();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    s.close();
                    throw e;
                }
            }
        }
        catch (final Throwable t) {
            Debug.debugException(t);
            this.socket.set(null);
            this.connected.set(false);
            this.exception.set(t);
        }
        finally {
            this.thread.set(null);
        }
    }
    
    Socket getConnectedSocket() throws LDAPException {
        if (this.startLatch.getCount() > 0L) {
            try {
                this.startLatch.await();
            }
            catch (final InterruptedException ie) {
                Debug.debugException(ie);
                Thread.currentThread().interrupt();
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_CONNECT_THREAD_INTERRUPTED.get(this.address.getHostAddress(), this.port, StaticUtils.getExceptionMessage(ie)), ie);
            }
        }
        final Thread t = this.thread.get();
        if (t != null) {
            try {
                t.join(this.connectTimeoutMillis);
            }
            catch (final InterruptedException ie2) {
                Debug.debugException(ie2);
                Thread.currentThread().interrupt();
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_CONNECT_THREAD_INTERRUPTED.get(this.address.getHostAddress(), this.port, StaticUtils.getExceptionMessage(ie2)), ie2);
            }
        }
        if (this.connected.get()) {
            return this.socket.get();
        }
        try {
            if (t != null) {
                t.interrupt();
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        try (final Socket s = this.socket.get()) {}
        catch (final Exception e) {
            Debug.debugException(e);
        }
        final Throwable cause = this.exception.get();
        if (cause == null) {
            throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_CONNECT_THREAD_TIMEOUT.get(this.address, this.port, this.connectTimeoutMillis));
        }
        StaticUtils.rethrowIfError(cause);
        throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_CONNECT_THREAD_EXCEPTION.get(this.address, this.port, StaticUtils.getExceptionMessage(cause)), cause);
    }
}
