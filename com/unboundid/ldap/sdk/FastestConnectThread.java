package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import javax.net.SocketFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class FastestConnectThread extends Thread
{
    private final AtomicBoolean connectionSelected;
    private final BindRequest bindRequest;
    private final BlockingQueue<Object> resultQueue;
    private final int port;
    private final LDAPConnection connection;
    private final LDAPConnectionPoolHealthCheck healthCheck;
    private final PostConnectProcessor postConnectProcessor;
    private final String address;
    
    FastestConnectThread(final String address, final int port, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions, final BindRequest bindRequest, final PostConnectProcessor postConnectProcessor, final LDAPConnectionPoolHealthCheck healthCheck, final BlockingQueue<Object> resultQueue, final AtomicBoolean connectionSelected) {
        super("Fastest Connect Thread for " + address + ':' + port);
        this.setDaemon(true);
        this.address = address;
        this.port = port;
        this.bindRequest = bindRequest;
        this.postConnectProcessor = postConnectProcessor;
        this.healthCheck = healthCheck;
        this.resultQueue = resultQueue;
        this.connectionSelected = connectionSelected;
        this.connection = new LDAPConnection(socketFactory, connectionOptions);
    }
    
    @Override
    public void run() {
        boolean returned = false;
        try {
            this.connection.connect(this.address, this.port);
            ServerSet.doBindPostConnectAndHealthCheckProcessing(this.connection, this.bindRequest, this.postConnectProcessor, this.healthCheck);
            returned = (this.connectionSelected.compareAndSet(false, true) && this.resultQueue.offer(this.connection));
        }
        catch (final Exception e) {
            Debug.debugException(e);
            this.resultQueue.offer(e);
        }
        finally {
            if (!returned) {
                this.connection.close();
            }
        }
    }
}
