package org.apache.commons.httpclient;

import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import org.apache.commons.logging.Log;

public class ConnectMethod extends HttpMethodBase
{
    public static final String NAME = "CONNECT";
    private final HostConfiguration targethost;
    private static final Log LOG;
    
    public ConnectMethod() {
        this.targethost = null;
    }
    
    public ConnectMethod(final HttpMethod method) {
        this.targethost = null;
    }
    
    public ConnectMethod(final HostConfiguration targethost) {
        if (targethost == null) {
            throw new IllegalArgumentException("Target host may not be null");
        }
        this.targethost = targethost;
    }
    
    public String getName() {
        return "CONNECT";
    }
    
    public String getPath() {
        if (this.targethost != null) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(this.targethost.getHost());
            int port = this.targethost.getPort();
            if (port == -1) {
                port = this.targethost.getProtocol().getDefaultPort();
            }
            buffer.append(':');
            buffer.append(port);
            return buffer.toString();
        }
        return "/";
    }
    
    public URI getURI() throws URIException {
        final String charset = this.getParams().getUriCharset();
        return new URI(this.getPath(), true, charset);
    }
    
    protected void addCookieRequestHeader(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
    }
    
    protected void addRequestHeaders(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        ConnectMethod.LOG.trace((Object)"enter ConnectMethod.addRequestHeaders(HttpState, HttpConnection)");
        this.addUserAgentRequestHeader(state, conn);
        this.addHostRequestHeader(state, conn);
        this.addProxyConnectionHeader(state, conn);
    }
    
    public int execute(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        ConnectMethod.LOG.trace((Object)"enter ConnectMethod.execute(HttpState, HttpConnection)");
        final int code = super.execute(state, conn);
        if (ConnectMethod.LOG.isDebugEnabled()) {
            ConnectMethod.LOG.debug((Object)("CONNECT status code " + code));
        }
        return code;
    }
    
    protected void writeRequestLine(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(this.getName());
        buffer.append(' ');
        if (this.targethost != null) {
            buffer.append(this.getPath());
        }
        else {
            int port = conn.getPort();
            if (port == -1) {
                port = conn.getProtocol().getDefaultPort();
            }
            buffer.append(conn.getHost());
            buffer.append(':');
            buffer.append(port);
        }
        buffer.append(" ");
        buffer.append(this.getEffectiveVersion());
        final String line = buffer.toString();
        conn.printLine(line, this.getParams().getHttpElementCharset());
        if (Wire.HEADER_WIRE.enabled()) {
            Wire.HEADER_WIRE.output(line);
        }
    }
    
    protected boolean shouldCloseConnection(final HttpConnection conn) {
        if (this.getStatusCode() == 200) {
            Header connectionHeader = null;
            if (!conn.isTransparent()) {
                connectionHeader = this.getResponseHeader("proxy-connection");
            }
            if (connectionHeader == null) {
                connectionHeader = this.getResponseHeader("connection");
            }
            if (connectionHeader != null && connectionHeader.getValue().equalsIgnoreCase("close") && ConnectMethod.LOG.isWarnEnabled()) {
                ConnectMethod.LOG.warn((Object)("Invalid header encountered '" + connectionHeader.toExternalForm() + "' in response " + this.getStatusLine().toString()));
            }
            return false;
        }
        return super.shouldCloseConnection(conn);
    }
    
    static {
        LOG = LogFactory.getLog(ConnectMethod.class);
    }
}
