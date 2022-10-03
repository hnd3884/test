package com.unboundid.ldap.listener;

import java.util.Iterator;
import com.unboundid.ldap.sdk.ExtendedResult;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.extensions.NoticeOfDisconnectionExtendedResult;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.InternalUseOnly;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import java.net.Socket;
import java.net.SocketException;
import java.io.IOException;
import java.net.InetAddress;
import javax.net.ServerSocketFactory;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentHashMap;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDAPListener extends Thread
{
    private final AtomicBoolean stopRequested;
    private final AtomicLong nextConnectionID;
    private final AtomicReference<ServerSocket> serverSocket;
    private final AtomicReference<Thread> thread;
    private final ConcurrentHashMap<Long, LDAPListenerClientConnection> establishedConnections;
    private final CountDownLatch startLatch;
    private final LDAPListenerConfig config;
    
    public LDAPListener(final LDAPListenerConfig config) {
        this.config = config.duplicate();
        this.stopRequested = new AtomicBoolean(false);
        this.nextConnectionID = new AtomicLong(0L);
        this.serverSocket = new AtomicReference<ServerSocket>(null);
        this.thread = new AtomicReference<Thread>(null);
        this.startLatch = new CountDownLatch(1);
        this.establishedConnections = new ConcurrentHashMap<Long, LDAPListenerClientConnection>(StaticUtils.computeMapCapacity(20));
        this.setName("LDAP Listener Thread (not listening");
    }
    
    public void startListening() throws IOException {
        final ServerSocketFactory f = this.config.getServerSocketFactory();
        final InetAddress a = this.config.getListenAddress();
        final int p = this.config.getListenPort();
        if (a == null) {
            this.serverSocket.set(f.createServerSocket(this.config.getListenPort(), 128));
        }
        else {
            this.serverSocket.set(f.createServerSocket(this.config.getListenPort(), 128, a));
        }
        final int receiveBufferSize = this.config.getReceiveBufferSize();
        if (receiveBufferSize > 0) {
            this.serverSocket.get().setReceiveBufferSize(receiveBufferSize);
        }
        this.setName("LDAP Listener Thread (listening on port " + this.serverSocket.get().getLocalPort() + ')');
        this.start();
        try {
            this.startLatch.await();
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
    }
    
    @InternalUseOnly
    @Override
    public void run() {
        this.thread.set(Thread.currentThread());
        final LDAPListenerExceptionHandler exceptionHandler = this.config.getExceptionHandler();
        try {
            this.startLatch.countDown();
            while (!this.stopRequested.get()) {
                Socket s;
                try {
                    s = this.serverSocket.get().accept();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    if (e instanceof SocketException && this.serverSocket.get().isClosed()) {
                        return;
                    }
                    if (exceptionHandler == null) {
                        continue;
                    }
                    exceptionHandler.connectionCreationFailure(null, e);
                    continue;
                }
                LDAPListenerClientConnection c;
                try {
                    c = new LDAPListenerClientConnection(this, s, this.config.getRequestHandler(), this.config.getExceptionHandler());
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    if (exceptionHandler == null) {
                        continue;
                    }
                    exceptionHandler.connectionCreationFailure(s, le);
                    continue;
                }
                final int maxConnections = this.config.getMaxConnections();
                if (maxConnections > 0 && this.establishedConnections.size() >= maxConnections) {
                    c.close(new LDAPException(ResultCode.BUSY, ListenerMessages.ERR_LDAP_LISTENER_MAX_CONNECTIONS_ESTABLISHED.get(maxConnections)));
                }
                else {
                    this.establishedConnections.put(c.getConnectionID(), c);
                    c.start();
                }
            }
        }
        finally {
            final ServerSocket s2 = this.serverSocket.getAndSet(null);
            if (s2 != null) {
                try {
                    s2.close();
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                }
            }
            this.serverSocket.set(null);
            this.thread.set(null);
        }
    }
    
    public void closeAllConnections(final boolean sendNoticeOfDisconnection) {
        final NoticeOfDisconnectionExtendedResult noticeOfDisconnection = new NoticeOfDisconnectionExtendedResult(ResultCode.OTHER, null, new Control[0]);
        final ArrayList<LDAPListenerClientConnection> connList = new ArrayList<LDAPListenerClientConnection>(this.establishedConnections.values());
        for (final LDAPListenerClientConnection c : connList) {
            if (sendNoticeOfDisconnection) {
                try {
                    c.sendUnsolicitedNotification(noticeOfDisconnection);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
            }
            try {
                c.close();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
    }
    
    public void shutDown(final boolean closeExisting) {
        this.stopRequested.set(true);
        final ServerSocket s = this.serverSocket.get();
        if (s != null) {
            try {
                s.close();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        final Thread t = this.thread.get();
        if (t != null) {
            while (t.isAlive()) {
                try {
                    t.join(100L);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    if (e2 instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                }
                if (t.isAlive()) {
                    try {
                        t.interrupt();
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                    }
                }
            }
        }
        if (closeExisting) {
            this.closeAllConnections(false);
        }
    }
    
    public InetAddress getListenAddress() {
        final ServerSocket s = this.serverSocket.get();
        if (s == null) {
            return null;
        }
        return s.getInetAddress();
    }
    
    public int getListenPort() {
        final ServerSocket s = this.serverSocket.get();
        if (s == null) {
            return -1;
        }
        return s.getLocalPort();
    }
    
    LDAPListenerConfig getConfig() {
        return this.config;
    }
    
    long nextConnectionID() {
        return this.nextConnectionID.getAndIncrement();
    }
    
    void connectionClosed(final LDAPListenerClientConnection connection) {
        this.establishedConnections.remove(connection.getConnectionID());
    }
}
