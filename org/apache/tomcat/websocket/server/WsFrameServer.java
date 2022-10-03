package org.apache.tomcat.websocket.server;

import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketEvent;
import java.nio.ByteBuffer;
import org.apache.tomcat.websocket.WsIOException;
import java.io.IOException;
import java.io.EOFException;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.WsSession;
import org.apache.coyote.http11.upgrade.UpgradeInfo;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.tomcat.websocket.WsFrameBase;

public class WsFrameServer extends WsFrameBase
{
    private final Log log;
    private static final StringManager sm;
    private final SocketWrapperBase<?> socketWrapper;
    private final UpgradeInfo upgradeInfo;
    private final ClassLoader applicationClassLoader;
    
    public WsFrameServer(final SocketWrapperBase<?> socketWrapper, final UpgradeInfo upgradeInfo, final WsSession wsSession, final Transformation transformation, final ClassLoader applicationClassLoader) {
        super(wsSession, transformation);
        this.log = LogFactory.getLog((Class)WsFrameServer.class);
        this.socketWrapper = socketWrapper;
        this.upgradeInfo = upgradeInfo;
        this.applicationClassLoader = applicationClassLoader;
    }
    
    private void onDataAvailable() throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"wsFrameServer.onDataAvailable");
        }
        if (this.isOpen() && this.inputBuffer.hasRemaining() && !this.isSuspended()) {
            this.processInputBuffer();
        }
        while (this.isOpen() && !this.isSuspended()) {
            this.inputBuffer.mark();
            this.inputBuffer.position(this.inputBuffer.limit()).limit(this.inputBuffer.capacity());
            final int read = this.socketWrapper.read(false, this.inputBuffer);
            this.inputBuffer.limit(this.inputBuffer.position()).reset();
            if (read < 0) {
                throw new EOFException();
            }
            if (read == 0) {
                return;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)WsFrameServer.sm.getString("wsFrameServer.bytesRead", new Object[] { Integer.toString(read) }));
            }
            this.processInputBuffer();
        }
    }
    
    @Override
    protected void updateStats(final long payloadLength) {
        this.upgradeInfo.addMsgsReceived(1L);
        this.upgradeInfo.addBytesReceived(payloadLength);
    }
    
    @Override
    protected boolean isMasked() {
        return true;
    }
    
    @Override
    protected Transformation getTransformation() {
        return super.getTransformation();
    }
    
    @Override
    protected boolean isOpen() {
        return super.isOpen();
    }
    
    @Override
    protected Log getLog() {
        return this.log;
    }
    
    @Override
    protected void sendMessageText(final boolean last) throws WsIOException {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.applicationClassLoader);
            super.sendMessageText(last);
        }
        finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }
    
    @Override
    protected void sendMessageBinary(final ByteBuffer msg, final boolean last) throws WsIOException {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.applicationClassLoader);
            super.sendMessageBinary(msg, last);
        }
        finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }
    
    @Override
    protected void resumeProcessing() {
        this.socketWrapper.processSocket(SocketEvent.OPEN_READ, true);
    }
    
    AbstractEndpoint.Handler.SocketState notifyDataAvailable() throws IOException {
        while (this.isOpen()) {
            switch (this.getReadState()) {
                case WAITING: {
                    if (!this.changeReadState(ReadState.WAITING, ReadState.PROCESSING)) {
                        continue;
                    }
                    try {
                        return this.doOnDataAvailable();
                    }
                    catch (final IOException e) {
                        this.changeReadState(ReadState.CLOSING);
                        throw e;
                    }
                }
                case SUSPENDING_WAIT: {
                    if (!this.changeReadState(ReadState.SUSPENDING_WAIT, ReadState.SUSPENDED)) {
                        continue;
                    }
                    return AbstractEndpoint.Handler.SocketState.SUSPENDED;
                }
                default: {
                    throw new IllegalStateException(WsFrameServer.sm.getString("wsFrameServer.illegalReadState", new Object[] { this.getReadState() }));
                }
            }
        }
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }
    
    private AbstractEndpoint.Handler.SocketState doOnDataAvailable() throws IOException {
        this.onDataAvailable();
        while (this.isOpen()) {
            switch (this.getReadState()) {
                case PROCESSING: {
                    if (!this.changeReadState(ReadState.PROCESSING, ReadState.WAITING)) {
                        continue;
                    }
                    return AbstractEndpoint.Handler.SocketState.UPGRADED;
                }
                case SUSPENDING_PROCESS: {
                    if (!this.changeReadState(ReadState.SUSPENDING_PROCESS, ReadState.SUSPENDED)) {
                        continue;
                    }
                    return AbstractEndpoint.Handler.SocketState.SUSPENDED;
                }
                default: {
                    throw new IllegalStateException(WsFrameServer.sm.getString("wsFrameServer.illegalReadState", new Object[] { this.getReadState() }));
                }
            }
        }
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }
    
    static {
        sm = StringManager.getManager((Class)WsFrameServer.class);
    }
}
