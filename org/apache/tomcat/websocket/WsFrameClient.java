package org.apache.tomcat.websocket;

import java.io.EOFException;
import javax.websocket.CloseReason;
import java.io.IOException;
import org.apache.juli.logging.LogFactory;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public class WsFrameClient extends WsFrameBase
{
    private final Log log;
    private static final StringManager sm;
    private final AsyncChannelWrapper channel;
    private final CompletionHandler<Integer, Void> handler;
    private volatile ByteBuffer response;
    
    public WsFrameClient(final ByteBuffer response, final AsyncChannelWrapper channel, final WsSession wsSession, final Transformation transformation) {
        super(wsSession, transformation);
        this.log = LogFactory.getLog((Class)WsFrameClient.class);
        this.response = response;
        this.channel = channel;
        this.handler = new WsFrameClientCompletionHandler();
    }
    
    void startInputProcessing() {
        try {
            this.processSocketRead();
        }
        catch (final IOException e) {
            this.close(e);
        }
    }
    
    private void processSocketRead() throws IOException {
    Label_0052:
        while (true) {
            switch (this.getReadState()) {
                case WAITING: {
                    if (!this.changeReadState(ReadState.WAITING, ReadState.PROCESSING)) {
                        continue;
                    }
                    break Label_0052;
                }
                case SUSPENDING_WAIT: {
                    if (!this.changeReadState(ReadState.SUSPENDING_WAIT, ReadState.SUSPENDED)) {
                        continue;
                    }
                    return;
                }
                default: {
                    throw new IllegalStateException(WsFrameClient.sm.getString("wsFrameServer.illegalReadState", new Object[] { this.getReadState() }));
                }
            }
        }
        while (this.response.hasRemaining()) {
            if (this.isSuspended()) {
                if (!this.changeReadState(ReadState.SUSPENDING_PROCESS, ReadState.SUSPENDED)) {
                    continue;
                }
                return;
            }
            else {
                this.inputBuffer.mark();
                this.inputBuffer.position(this.inputBuffer.limit()).limit(this.inputBuffer.capacity());
                final int toCopy = Math.min(this.response.remaining(), this.inputBuffer.remaining());
                final int orgLimit = this.response.limit();
                this.response.limit(this.response.position() + toCopy);
                this.inputBuffer.put(this.response);
                this.response.limit(orgLimit);
                this.inputBuffer.limit(this.inputBuffer.position()).reset();
                this.processInputBuffer();
            }
        }
        this.response.clear();
        if (this.isOpen()) {
            this.channel.read(this.response, (Object)null, this.handler);
        }
        else {
            this.changeReadState(ReadState.CLOSING);
        }
    }
    
    private final void close(final Throwable t) {
        this.changeReadState(ReadState.CLOSING);
        CloseReason cr;
        if (t instanceof WsIOException) {
            cr = ((WsIOException)t).getCloseReason();
        }
        else {
            cr = new CloseReason((CloseReason.CloseCode)CloseReason.CloseCodes.CLOSED_ABNORMALLY, t.getMessage());
        }
        this.wsSession.doClose(cr, cr, true);
    }
    
    @Override
    protected boolean isMasked() {
        return false;
    }
    
    @Override
    protected Log getLog() {
        return this.log;
    }
    
    @Override
    protected void resumeProcessing() {
        this.resumeProcessing(true);
    }
    
    private void resumeProcessing(final boolean checkOpenOnError) {
        try {
            this.processSocketRead();
        }
        catch (final IOException e) {
            if (checkOpenOnError) {
                if (this.isOpen()) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)WsFrameClient.sm.getString("wsFrameClient.ioe"), (Throwable)e);
                    }
                    this.close(e);
                }
            }
            else {
                this.close(e);
            }
        }
    }
    
    static {
        sm = StringManager.getManager((Class)WsFrameClient.class);
    }
    
    private class WsFrameClientCompletionHandler implements CompletionHandler<Integer, Void>
    {
        @Override
        public void completed(final Integer result, final Void attachment) {
            if (result == -1) {
                if (WsFrameClient.this.isOpen()) {
                    WsFrameClient.this.close(new EOFException());
                }
                return;
            }
            WsFrameClient.this.response.flip();
            this.doResumeProcessing(true);
        }
        
        @Override
        public void failed(final Throwable exc, final Void attachment) {
            if (WsFrameClient.this.log.isDebugEnabled()) {
                WsFrameClient.this.log.debug((Object)WsFrameClient.sm.getString("wsFrame.readFailed"), exc);
            }
            if (exc instanceof ReadBufferOverflowException) {
                WsFrameClient.this.response = ByteBuffer.allocate(((ReadBufferOverflowException)exc).getMinBufferSize());
                WsFrameClient.this.response.flip();
                this.doResumeProcessing(false);
            }
            else {
                WsFrameClient.this.close(exc);
            }
        }
        
        private void doResumeProcessing(final boolean checkOpenOnError) {
            while (true) {
                switch (WsFrameClient.this.getReadState()) {
                    case PROCESSING: {
                        if (!WsFrameClient.this.changeReadState(ReadState.PROCESSING, ReadState.WAITING)) {
                            continue;
                        }
                        WsFrameClient.this.resumeProcessing(checkOpenOnError);
                        return;
                    }
                    case SUSPENDING_PROCESS: {
                        if (!WsFrameClient.this.changeReadState(ReadState.SUSPENDING_PROCESS, ReadState.SUSPENDED)) {
                            continue;
                        }
                        return;
                    }
                    default: {
                        throw new IllegalStateException(WsFrameClient.sm.getString("wsFrame.illegalReadState", new Object[] { WsFrameClient.this.getReadState() }));
                    }
                }
            }
        }
    }
}
