package org.apache.tomcat.websocket.server;

import java.util.concurrent.Executor;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.websocket.Transformation;
import java.io.EOFException;
import java.io.IOException;
import javax.websocket.SendResult;
import java.net.SocketTimeoutException;
import org.apache.juli.logging.LogFactory;
import java.nio.ByteBuffer;
import javax.websocket.SendHandler;
import org.apache.coyote.http11.upgrade.UpgradeInfo;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.WsRemoteEndpointImplBase;

public class WsRemoteEndpointImplServer extends WsRemoteEndpointImplBase
{
    private static final StringManager sm;
    private final Log log;
    private final SocketWrapperBase<?> socketWrapper;
    private final UpgradeInfo upgradeInfo;
    private final WsWriteTimeout wsWriteTimeout;
    private volatile SendHandler handler;
    private volatile ByteBuffer[] buffers;
    private volatile long timeoutExpiry;
    
    public WsRemoteEndpointImplServer(final SocketWrapperBase<?> socketWrapper, final UpgradeInfo upgradeInfo, final WsServerContainer serverContainer) {
        this.log = LogFactory.getLog((Class)WsRemoteEndpointImplServer.class);
        this.handler = null;
        this.buffers = null;
        this.timeoutExpiry = -1L;
        this.socketWrapper = socketWrapper;
        this.upgradeInfo = upgradeInfo;
        this.wsWriteTimeout = serverContainer.getTimeout();
    }
    
    @Override
    protected final boolean isMasked() {
        return false;
    }
    
    @Override
    protected void doWrite(final SendHandler handler, final long blockingWriteTimeoutExpiry, final ByteBuffer... buffers) {
        if (blockingWriteTimeoutExpiry == -1L) {
            this.handler = handler;
            this.buffers = buffers;
            this.onWritePossible(true);
        }
        else {
            try {
                for (final ByteBuffer buffer : buffers) {
                    final long timeout = blockingWriteTimeoutExpiry - System.currentTimeMillis();
                    if (timeout <= 0L) {
                        final SendResult sr = new SendResult((Throwable)new SocketTimeoutException());
                        handler.onResult(sr);
                        return;
                    }
                    this.socketWrapper.setWriteTimeout(timeout);
                    this.socketWrapper.write(true, buffer);
                }
                final long timeout2 = blockingWriteTimeoutExpiry - System.currentTimeMillis();
                if (timeout2 <= 0L) {
                    final SendResult sr2 = new SendResult((Throwable)new SocketTimeoutException());
                    handler.onResult(sr2);
                    return;
                }
                this.socketWrapper.setWriteTimeout(timeout2);
                this.socketWrapper.flush(true);
                handler.onResult(WsRemoteEndpointImplServer.SENDRESULT_OK);
            }
            catch (final IOException e) {
                final SendResult sr3 = new SendResult((Throwable)e);
                handler.onResult(sr3);
            }
        }
    }
    
    @Override
    protected void updateStats(final long payloadLength) {
        this.upgradeInfo.addMsgsSent(1L);
        this.upgradeInfo.addBytesSent(payloadLength);
    }
    
    public void onWritePossible(final boolean useDispatch) {
        final ByteBuffer[] buffers = this.buffers;
        if (buffers == null) {
            return;
        }
        boolean complete = false;
        try {
            this.socketWrapper.flush(false);
            while (this.socketWrapper.isReadyForWrite()) {
                complete = true;
                for (final ByteBuffer buffer : buffers) {
                    if (buffer.hasRemaining()) {
                        complete = false;
                        this.socketWrapper.write(false, buffer);
                        break;
                    }
                }
                if (complete) {
                    this.socketWrapper.flush(false);
                    complete = this.socketWrapper.isReadyForWrite();
                    if (complete) {
                        this.wsWriteTimeout.unregister(this);
                        this.clearHandler(null, useDispatch);
                        break;
                    }
                    break;
                }
            }
        }
        catch (final IOException | IllegalStateException e) {
            this.wsWriteTimeout.unregister(this);
            this.clearHandler(e, useDispatch);
            this.close();
        }
        if (!complete) {
            final long timeout = this.getSendTimeout();
            if (timeout > 0L) {
                this.timeoutExpiry = timeout + System.currentTimeMillis();
                this.wsWriteTimeout.register(this);
            }
        }
    }
    
    @Override
    protected void doClose() {
        if (this.handler != null) {
            this.clearHandler(new EOFException(), true);
        }
        try {
            this.socketWrapper.close();
        }
        catch (final Exception e) {
            if (this.log.isInfoEnabled()) {
                this.log.info((Object)WsRemoteEndpointImplServer.sm.getString("wsRemoteEndpointServer.closeFailed"), (Throwable)e);
            }
        }
        this.wsWriteTimeout.unregister(this);
    }
    
    protected long getTimeoutExpiry() {
        return this.timeoutExpiry;
    }
    
    protected void onTimeout(final boolean useDispatch) {
        if (this.handler != null) {
            this.clearHandler(new SocketTimeoutException(), useDispatch);
        }
        this.close();
    }
    
    @Override
    protected void setTransformation(final Transformation transformation) {
        super.setTransformation(transformation);
    }
    
    private void clearHandler(final Throwable t, final boolean useDispatch) {
        final SendHandler sh = this.handler;
        this.handler = null;
        this.buffers = null;
        if (sh != null) {
            if (useDispatch) {
                final OnResultRunnable r = new OnResultRunnable(sh, t);
                final AbstractEndpoint<?> endpoint = (AbstractEndpoint<?>)this.socketWrapper.getEndpoint();
                final Executor containerExecutor = endpoint.getExecutor();
                if (endpoint.isRunning() && containerExecutor != null) {
                    containerExecutor.execute(r);
                }
                else {
                    r.run();
                }
            }
            else if (t == null) {
                sh.onResult(new SendResult());
            }
            else {
                sh.onResult(new SendResult(t));
            }
        }
    }
    
    static {
        sm = StringManager.getManager((Class)WsRemoteEndpointImplServer.class);
    }
    
    private static class OnResultRunnable implements Runnable
    {
        private final SendHandler sh;
        private final Throwable t;
        
        private OnResultRunnable(final SendHandler sh, final Throwable t) {
            this.sh = sh;
            this.t = t;
        }
        
        @Override
        public void run() {
            if (this.t == null) {
                this.sh.onResult(new SendResult());
            }
            else {
                this.sh.onResult(new SendResult(this.t));
            }
        }
    }
}
