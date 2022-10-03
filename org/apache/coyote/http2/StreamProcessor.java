package org.apache.coyote.http2;

import org.apache.juli.logging.LogFactory;
import org.apache.coyote.ActionCode;
import org.apache.coyote.RequestGroupInfo;
import java.util.Iterator;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.coyote.ErrorState;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.coyote.http11.OutputFilter;
import org.apache.coyote.http11.filters.GzipOutputFilter;
import org.apache.coyote.Response;
import org.apache.coyote.Request;
import java.io.IOException;
import org.apache.coyote.Processor;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.coyote.ContainerThreadMarker;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.coyote.Adapter;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.coyote.AbstractProcessor;

class StreamProcessor extends AbstractProcessor
{
    private static final Log log;
    private static final StringManager sm;
    private final Http2UpgradeHandler handler;
    private final Stream stream;
    
    StreamProcessor(final Http2UpgradeHandler handler, final Stream stream, final Adapter adapter, final SocketWrapperBase<?> socketWrapper) {
        super(socketWrapper.getEndpoint(), stream.getCoyoteRequest(), stream.getCoyoteResponse());
        this.handler = handler;
        this.stream = stream;
        this.setAdapter(adapter);
        this.setSocketWrapper(socketWrapper);
    }
    
    final void process(final SocketEvent event) {
        try {
            synchronized (this) {
                ContainerThreadMarker.set();
                AbstractEndpoint.Handler.SocketState state = AbstractEndpoint.Handler.SocketState.CLOSED;
                try {
                    state = this.process(this.socketWrapper, event);
                    if (state == AbstractEndpoint.Handler.SocketState.LONG) {
                        this.handler.getProtocol().getHttp11Protocol().addWaitingProcessor(this);
                    }
                    else if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                        this.handler.getProtocol().getHttp11Protocol().removeWaitingProcessor(this);
                        if (!this.stream.isInputFinished() && this.getErrorState().isIoAllowed()) {
                            final StreamException se = new StreamException(StreamProcessor.sm.getString("streamProcessor.cancel", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString() }), Http2Error.CANCEL, this.stream.getIdAsInt());
                            this.stream.close(se);
                        }
                        else if (!this.getErrorState().isConnectionIoAllowed()) {
                            final ConnectionException ce = new ConnectionException(StreamProcessor.sm.getString("streamProcessor.error.connection", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString() }), Http2Error.INTERNAL_ERROR);
                            this.stream.close(ce);
                        }
                        else if (!this.getErrorState().isIoAllowed()) {
                            StreamException se = this.stream.getResetException();
                            if (se == null) {
                                se = new StreamException(StreamProcessor.sm.getString("streamProcessor.error.stream", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString() }), Http2Error.INTERNAL_ERROR, this.stream.getIdAsInt());
                            }
                            this.stream.close(se);
                        }
                        else if (!this.stream.isActive()) {
                            this.stream.recycle();
                        }
                    }
                }
                catch (final Exception e) {
                    final String msg = StreamProcessor.sm.getString("streamProcessor.error.connection", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString() });
                    if (StreamProcessor.log.isDebugEnabled()) {
                        StreamProcessor.log.debug((Object)msg, (Throwable)e);
                    }
                    final ConnectionException ce2 = new ConnectionException(msg, Http2Error.INTERNAL_ERROR, e);
                    this.stream.close(ce2);
                    state = AbstractEndpoint.Handler.SocketState.CLOSED;
                }
                finally {
                    if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                        this.recycle();
                    }
                    ContainerThreadMarker.clear();
                }
            }
        }
        finally {
            this.handler.executeQueuedStream();
        }
    }
    
    @Override
    protected final void prepareResponse() throws IOException {
        this.response.setCommitted(true);
        prepareHeaders(this.request, this.response, this.handler.getProtocol(), this.stream);
        this.stream.writeHeaders();
    }
    
    static void prepareHeaders(final Request coyoteRequest, final Response coyoteResponse, final Http2Protocol protocol, final Stream stream) {
        final MimeHeaders headers = coyoteResponse.getMimeHeaders();
        final int statusCode = coyoteResponse.getStatus();
        headers.addValue(":status").setString(Integer.toString(statusCode));
        if (protocol != null && protocol.useCompression(coyoteRequest, coyoteResponse)) {
            stream.addOutputFilter(new GzipOutputFilter());
        }
        if (statusCode >= 200 && statusCode != 204 && statusCode != 205 && statusCode != 304) {
            final String contentType = coyoteResponse.getContentType();
            if (contentType != null) {
                headers.setValue("content-type").setString(contentType);
            }
            final String contentLanguage = coyoteResponse.getContentLanguage();
            if (contentLanguage != null) {
                headers.setValue("content-language").setString(contentLanguage);
            }
            final long contentLength = coyoteResponse.getContentLengthLong();
            if (contentLength != -1L && headers.getValue("content-length") == null) {
                headers.addValue("content-length").setLong(contentLength);
            }
        }
        else if (statusCode == 205) {
            coyoteResponse.setContentLength(0L);
        }
        else {
            coyoteResponse.setContentLength(-1L);
        }
        if (statusCode >= 200 && headers.getValue("date") == null) {
            headers.addValue("date").setString(FastHttpDateFormat.getCurrentDate());
        }
    }
    
    @Override
    protected final void finishResponse() throws IOException {
        this.stream.getOutputBuffer().end();
    }
    
    @Override
    protected final void ack(final ContinueResponseTiming continueResponseTiming) {
        if ((continueResponseTiming == ContinueResponseTiming.ALWAYS || continueResponseTiming == this.handler.getProtocol().getContinueResponseTimingInternal()) && !this.response.isCommitted() && this.request.hasExpectation()) {
            try {
                this.stream.writeAck();
            }
            catch (final IOException ioe) {
                this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
            }
        }
    }
    
    @Override
    protected final void flush() throws IOException {
        this.stream.getOutputBuffer().flush();
    }
    
    @Override
    protected final int available(final boolean doRead) {
        return this.stream.getInputBuffer().available();
    }
    
    @Override
    protected final void setRequestBody(final ByteChunk body) {
        this.stream.getInputBuffer().insertReplayedBody(body);
        try {
            this.stream.receivedEndOfStream();
        }
        catch (final ConnectionException ex) {}
    }
    
    @Override
    protected final void setSwallowResponse() {
    }
    
    @Override
    protected final void disableSwallowRequest() {
    }
    
    @Override
    protected void processSocketEvent(final SocketEvent event, final boolean dispatch) {
        if (dispatch) {
            this.handler.processStreamOnContainerThread(this, event);
        }
        else {
            this.process(event);
        }
    }
    
    @Override
    protected final boolean isReadyForRead() {
        return this.stream.getInputBuffer().isReadyForRead();
    }
    
    @Override
    protected final boolean isRequestBodyFullyRead() {
        return this.stream.getInputBuffer().isRequestBodyFullyRead();
    }
    
    @Override
    protected final void registerReadInterest() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected final boolean isReadyForWrite() {
        return this.stream.isReadyForWrite();
    }
    
    @Override
    protected final void executeDispatches() {
        final Iterator<DispatchType> dispatches = this.getIteratorAndClearDispatches();
        while (dispatches != null && dispatches.hasNext()) {
            final DispatchType dispatchType = dispatches.next();
            this.processSocketEvent(dispatchType.getSocketStatus(), true);
        }
    }
    
    @Override
    protected final boolean isPushSupported() {
        return this.stream.isPushSupported();
    }
    
    @Override
    protected final void doPush(final Request pushTarget) {
        try {
            this.stream.push(pushTarget);
        }
        catch (final IOException ioe) {
            this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
            this.response.setErrorException(ioe);
        }
    }
    
    @Override
    protected Object getConnectionID() {
        return this.stream.getConnectionId();
    }
    
    @Override
    protected Object getStreamID() {
        return this.stream.getIdAsString().toString();
    }
    
    @Override
    public final void recycle() {
        final RequestGroupInfo global = this.handler.getProtocol().getGlobal();
        if (global != null) {
            global.removeRequestProcessor(this.request.getRequestProcessor());
        }
        this.setSocketWrapper(null);
        this.setAdapter(null);
    }
    
    @Override
    protected final Log getLog() {
        return StreamProcessor.log;
    }
    
    @Override
    public final void pause() {
    }
    
    public final AbstractEndpoint.Handler.SocketState service(final SocketWrapperBase<?> socket) throws IOException {
        try {
            this.adapter.service(this.request, this.response);
        }
        catch (final Exception e) {
            if (StreamProcessor.log.isDebugEnabled()) {
                StreamProcessor.log.debug((Object)StreamProcessor.sm.getString("streamProcessor.service.error"), (Throwable)e);
            }
            this.response.setStatus(500);
            this.setErrorState(ErrorState.CLOSE_NOW, e);
        }
        if (this.getErrorState().isError()) {
            this.action(ActionCode.CLOSE, null);
            this.request.updateCounters();
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        if (this.isAsync()) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        this.action(ActionCode.CLOSE, null);
        this.request.updateCounters();
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }
    
    @Override
    protected final boolean flushBufferedWrite() throws IOException {
        if (StreamProcessor.log.isDebugEnabled()) {
            StreamProcessor.log.debug((Object)StreamProcessor.sm.getString("streamProcessor.flushBufferedWrite.entry", new Object[] { this.stream.getConnectionId(), this.stream.getIdAsString() }));
        }
        if (!this.stream.flush(false)) {
            return false;
        }
        if (this.stream.isReadyForWrite()) {
            throw new IllegalStateException();
        }
        return true;
    }
    
    @Override
    protected final AbstractEndpoint.Handler.SocketState dispatchEndRequest() throws IOException {
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }
    
    static {
        log = LogFactory.getLog((Class)StreamProcessor.class);
        sm = StringManager.getManager((Class)StreamProcessor.class);
    }
}
