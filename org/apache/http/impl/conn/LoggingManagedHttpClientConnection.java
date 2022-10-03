package org.apache.http.impl.conn;

import org.apache.http.Header;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.HttpRequest;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.config.MessageConstraints;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import org.apache.commons.logging.Log;

class LoggingManagedHttpClientConnection extends DefaultManagedHttpClientConnection
{
    private final Log log;
    private final Log headerLog;
    private final Wire wire;
    
    public LoggingManagedHttpClientConnection(final String id, final Log log, final Log headerLog, final Log wireLog, final int bufferSize, final int fragmentSizeHint, final CharsetDecoder charDecoder, final CharsetEncoder charEncoder, final MessageConstraints constraints, final ContentLengthStrategy incomingContentStrategy, final ContentLengthStrategy outgoingContentStrategy, final HttpMessageWriterFactory<HttpRequest> requestWriterFactory, final HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        super(id, bufferSize, fragmentSizeHint, charDecoder, charEncoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
        this.log = log;
        this.headerLog = headerLog;
        this.wire = new Wire(wireLog, id);
    }
    
    public void close() throws IOException {
        if (super.isOpen()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)(this.getId() + ": Close connection"));
            }
            super.close();
        }
    }
    
    public void setSocketTimeout(final int timeout) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)(this.getId() + ": set socket timeout to " + timeout));
        }
        super.setSocketTimeout(timeout);
    }
    
    @Override
    public void shutdown() throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)(this.getId() + ": Shutdown connection"));
        }
        super.shutdown();
    }
    
    protected InputStream getSocketInputStream(final Socket socket) throws IOException {
        InputStream in = super.getSocketInputStream(socket);
        if (this.wire.enabled()) {
            in = new LoggingInputStream(in, this.wire);
        }
        return in;
    }
    
    protected OutputStream getSocketOutputStream(final Socket socket) throws IOException {
        OutputStream out = super.getSocketOutputStream(socket);
        if (this.wire.enabled()) {
            out = new LoggingOutputStream(out, this.wire);
        }
        return out;
    }
    
    protected void onResponseReceived(final HttpResponse response) {
        if (response != null && this.headerLog.isDebugEnabled()) {
            this.headerLog.debug((Object)(this.getId() + " << " + response.getStatusLine().toString()));
            final Header[] arr$;
            final Header[] headers = arr$ = response.getAllHeaders();
            for (final Header header : arr$) {
                this.headerLog.debug((Object)(this.getId() + " << " + header.toString()));
            }
        }
    }
    
    protected void onRequestSubmitted(final HttpRequest request) {
        if (request != null && this.headerLog.isDebugEnabled()) {
            this.headerLog.debug((Object)(this.getId() + " >> " + request.getRequestLine().toString()));
            final Header[] arr$;
            final Header[] headers = arr$ = request.getAllHeaders();
            for (final Header header : arr$) {
                this.headerLog.debug((Object)(this.getId() + " >> " + header.toString()));
            }
        }
    }
}
