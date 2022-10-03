package sun.net.httpserver;

import java.text.SimpleDateFormat;
import java.util.Locale;
import com.sun.net.httpserver.HttpExchange;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.io.BufferedOutputStream;
import java.io.IOException;
import com.sun.net.httpserver.HttpPrincipal;
import java.util.Map;
import java.text.DateFormat;
import java.util.TimeZone;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.URI;
import com.sun.net.httpserver.Headers;

class ExchangeImpl
{
    Headers reqHdrs;
    Headers rspHdrs;
    Request req;
    String method;
    boolean writefinished;
    URI uri;
    HttpConnection connection;
    long reqContentLen;
    long rspContentLen;
    InputStream ris;
    OutputStream ros;
    Thread thread;
    boolean close;
    boolean closed;
    boolean http10;
    private static final String pattern = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final TimeZone gmtTZ;
    private static final ThreadLocal<DateFormat> dateFormat;
    private static final String HEAD = "HEAD";
    InputStream uis;
    OutputStream uos;
    LeftOverInputStream uis_orig;
    PlaceholderOutputStream uos_orig;
    boolean sentHeaders;
    Map<String, Object> attributes;
    int rcode;
    HttpPrincipal principal;
    ServerImpl server;
    private byte[] rspbuf;
    
    ExchangeImpl(final String method, final URI uri, final Request req, final long reqContentLen, final HttpConnection connection) throws IOException {
        this.http10 = false;
        this.rcode = -1;
        this.rspbuf = new byte[128];
        this.req = req;
        this.reqHdrs = req.headers();
        this.rspHdrs = new Headers();
        this.method = method;
        this.uri = uri;
        this.connection = connection;
        this.reqContentLen = reqContentLen;
        this.ros = req.outputStream();
        this.ris = req.inputStream();
        (this.server = this.getServerImpl()).startExchange();
    }
    
    public Headers getRequestHeaders() {
        return new UnmodifiableHeaders(this.reqHdrs);
    }
    
    public Headers getResponseHeaders() {
        return this.rspHdrs;
    }
    
    public URI getRequestURI() {
        return this.uri;
    }
    
    public String getRequestMethod() {
        return this.method;
    }
    
    public HttpContextImpl getHttpContext() {
        return this.connection.getHttpContext();
    }
    
    private boolean isHeadRequest() {
        return "HEAD".equals(this.getRequestMethod());
    }
    
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        try {
            if (this.uis_orig == null || this.uos == null) {
                this.connection.close();
                return;
            }
            if (!this.uos_orig.isWrapped()) {
                this.connection.close();
                return;
            }
            if (!this.uis_orig.isClosed()) {
                this.uis_orig.close();
            }
            this.uos.close();
        }
        catch (final IOException ex) {
            this.connection.close();
        }
    }
    
    public InputStream getRequestBody() {
        if (this.uis != null) {
            return this.uis;
        }
        if (this.reqContentLen == -1L) {
            this.uis_orig = new ChunkedInputStream(this, this.ris);
            this.uis = this.uis_orig;
        }
        else {
            this.uis_orig = new FixedLengthInputStream(this, this.ris, this.reqContentLen);
            this.uis = this.uis_orig;
        }
        return this.uis;
    }
    
    LeftOverInputStream getOriginalInputStream() {
        return this.uis_orig;
    }
    
    public int getResponseCode() {
        return this.rcode;
    }
    
    public OutputStream getResponseBody() {
        if (this.uos == null) {
            this.uos_orig = new PlaceholderOutputStream(null);
            this.uos = this.uos_orig;
        }
        return this.uos;
    }
    
    PlaceholderOutputStream getPlaceholderResponseBody() {
        this.getResponseBody();
        return this.uos_orig;
    }
    
    public void sendResponseHeaders(final int rcode, long rspContentLen) throws IOException {
        if (this.sentHeaders) {
            throw new IOException("headers already sent");
        }
        this.rcode = rcode;
        final String string = "HTTP/1.1 " + rcode + Code.msg(rcode) + "\r\n";
        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(this.ros);
        final PlaceholderOutputStream placeholderResponseBody = this.getPlaceholderResponseBody();
        bufferedOutputStream.write(this.bytes(string, 0), 0, string.length());
        boolean b = false;
        this.rspHdrs.set("Date", ExchangeImpl.dateFormat.get().format(new Date()));
        if ((rcode >= 100 && rcode < 200) || rcode == 204 || rcode == 304) {
            if (rspContentLen != -1L) {
                this.server.getLogger().warning("sendResponseHeaders: rCode = " + rcode + ": forcing contentLen = -1");
            }
            rspContentLen = -1L;
        }
        if (this.isHeadRequest()) {
            if (rspContentLen >= 0L) {
                this.server.getLogger().warning("sendResponseHeaders: being invoked with a content length for a HEAD request");
            }
            b = true;
            rspContentLen = 0L;
        }
        else if (rspContentLen == 0L) {
            if (this.http10) {
                placeholderResponseBody.setWrappedStream(new UndefLengthOutputStream(this, this.ros));
                this.close = true;
            }
            else {
                this.rspHdrs.set("Transfer-encoding", "chunked");
                placeholderResponseBody.setWrappedStream(new ChunkedOutputStream(this, this.ros));
            }
        }
        else {
            if (rspContentLen == -1L) {
                b = true;
                rspContentLen = 0L;
            }
            this.rspHdrs.set("Content-length", Long.toString(rspContentLen));
            placeholderResponseBody.setWrappedStream(new FixedLengthOutputStream(this, this.ros, rspContentLen));
        }
        this.write(this.rspHdrs, bufferedOutputStream);
        this.rspContentLen = rspContentLen;
        bufferedOutputStream.flush();
        this.sentHeaders = true;
        if (b) {
            this.server.addEvent(new WriteFinishedEvent(this));
            this.closed = true;
        }
        this.server.logReply(rcode, this.req.requestLine(), null);
    }
    
    void write(final Headers headers, final OutputStream outputStream) throws IOException {
        for (final Map.Entry entry : headers.entrySet()) {
            final String s = (String)entry.getKey();
            for (final String s2 : (List)entry.getValue()) {
                int length = s.length();
                final byte[] bytes = this.bytes(s, 2);
                bytes[length++] = 58;
                bytes[length++] = 32;
                outputStream.write(bytes, 0, length);
                final byte[] bytes2 = this.bytes(s2, 2);
                int length2 = s2.length();
                bytes2[length2++] = 13;
                bytes2[length2++] = 10;
                outputStream.write(bytes2, 0, length2);
            }
        }
        outputStream.write(13);
        outputStream.write(10);
    }
    
    private byte[] bytes(final String s, final int n) {
        final int length = s.length();
        if (length + n > this.rspbuf.length) {
            this.rspbuf = new byte[2 * (this.rspbuf.length + (length + n - this.rspbuf.length))];
        }
        final char[] charArray = s.toCharArray();
        for (int i = 0; i < charArray.length; ++i) {
            this.rspbuf[i] = (byte)charArray[i];
        }
        return this.rspbuf;
    }
    
    public InetSocketAddress getRemoteAddress() {
        final Socket socket = this.connection.getChannel().socket();
        return new InetSocketAddress(socket.getInetAddress(), socket.getPort());
    }
    
    public InetSocketAddress getLocalAddress() {
        final Socket socket = this.connection.getChannel().socket();
        return new InetSocketAddress(socket.getLocalAddress(), socket.getLocalPort());
    }
    
    public String getProtocol() {
        final String requestLine = this.req.requestLine();
        return requestLine.substring(requestLine.lastIndexOf(32) + 1);
    }
    
    public SSLSession getSSLSession() {
        final SSLEngine sslEngine = this.connection.getSSLEngine();
        if (sslEngine == null) {
            return null;
        }
        return sslEngine.getSession();
    }
    
    public Object getAttribute(final String s) {
        if (s == null) {
            throw new NullPointerException("null name parameter");
        }
        if (this.attributes == null) {
            this.attributes = this.getHttpContext().getAttributes();
        }
        return this.attributes.get(s);
    }
    
    public void setAttribute(final String s, final Object o) {
        if (s == null) {
            throw new NullPointerException("null name parameter");
        }
        if (this.attributes == null) {
            this.attributes = this.getHttpContext().getAttributes();
        }
        this.attributes.put(s, o);
    }
    
    public void setStreams(final InputStream uis, final OutputStream uos) {
        assert this.uis != null;
        if (uis != null) {
            this.uis = uis;
        }
        if (uos != null) {
            this.uos = uos;
        }
    }
    
    HttpConnection getConnection() {
        return this.connection;
    }
    
    ServerImpl getServerImpl() {
        return this.getHttpContext().getServerImpl();
    }
    
    public HttpPrincipal getPrincipal() {
        return this.principal;
    }
    
    void setPrincipal(final HttpPrincipal principal) {
        this.principal = principal;
    }
    
    static ExchangeImpl get(final HttpExchange httpExchange) {
        if (httpExchange instanceof HttpExchangeImpl) {
            return ((HttpExchangeImpl)httpExchange).getExchangeImpl();
        }
        assert httpExchange instanceof HttpsExchangeImpl;
        return ((HttpsExchangeImpl)httpExchange).getExchangeImpl();
    }
    
    static {
        gmtTZ = TimeZone.getTimeZone("GMT");
        dateFormat = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
                simpleDateFormat.setTimeZone(ExchangeImpl.gmtTZ);
                return simpleDateFormat;
            }
        };
    }
}
