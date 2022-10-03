package com.sun.mail.iap;

import java.net.UnknownHostException;
import java.lang.reflect.Field;
import java.nio.channels.SocketChannel;
import java.net.InetAddress;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.Inflater;
import javax.net.ssl.SSLSocket;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.IOException;
import com.sun.mail.util.PropUtil;
import com.sun.mail.util.SocketFetcher;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.io.DataOutputStream;
import com.sun.mail.util.TraceOutputStream;
import com.sun.mail.util.TraceInputStream;
import java.util.Properties;
import com.sun.mail.util.MailLogger;
import java.net.Socket;

public class Protocol
{
    protected String host;
    private Socket socket;
    protected boolean quote;
    protected MailLogger logger;
    protected MailLogger traceLogger;
    protected Properties props;
    protected String prefix;
    private TraceInputStream traceInput;
    private volatile ResponseInputStream input;
    private TraceOutputStream traceOutput;
    private volatile DataOutputStream output;
    private int tagCounter;
    private final String tagPrefix;
    private String localHostName;
    private final List<ResponseHandler> handlers;
    private volatile long timestamp;
    static final AtomicInteger tagNum;
    private static final byte[] CRLF;
    
    public Protocol(final String host, final int port, final Properties props, final String prefix, final boolean isSSL, final MailLogger logger) throws IOException, ProtocolException {
        this.tagCounter = 0;
        this.handlers = new CopyOnWriteArrayList<ResponseHandler>();
        boolean connected = false;
        this.tagPrefix = this.computePrefix(props, prefix);
        try {
            this.host = host;
            this.props = props;
            this.prefix = prefix;
            this.logger = logger;
            this.traceLogger = logger.getSubLogger("protocol", null);
            this.socket = SocketFetcher.getSocket(host, port, props, prefix, isSSL);
            this.quote = PropUtil.getBooleanProperty(props, "mail.debug.quote", false);
            this.initStreams();
            this.processGreeting(this.readResponse());
            this.timestamp = System.currentTimeMillis();
            connected = true;
        }
        finally {
            if (!connected) {
                this.disconnect();
            }
        }
    }
    
    private void initStreams() throws IOException {
        (this.traceInput = new TraceInputStream(this.socket.getInputStream(), this.traceLogger)).setQuote(this.quote);
        this.input = new ResponseInputStream(this.traceInput);
        (this.traceOutput = new TraceOutputStream(this.socket.getOutputStream(), this.traceLogger)).setQuote(this.quote);
        this.output = new DataOutputStream(new BufferedOutputStream(this.traceOutput));
    }
    
    private String computePrefix(final Properties props, final String prefix) {
        if (PropUtil.getBooleanProperty(props, prefix + ".reusetagprefix", false)) {
            return "A";
        }
        int n = Protocol.tagNum.getAndIncrement() % 18278;
        String tagPrefix;
        if (n < 26) {
            tagPrefix = new String(new char[] { (char)(65 + n) });
        }
        else if (n < 702) {
            n -= 26;
            tagPrefix = new String(new char[] { (char)(65 + n / 26), (char)(65 + n % 26) });
        }
        else {
            n -= 702;
            tagPrefix = new String(new char[] { (char)(65 + n / 676), (char)(65 + n % 676 / 26), (char)(65 + n % 26) });
        }
        return tagPrefix;
    }
    
    public Protocol(final InputStream in, final PrintStream out, final Properties props, final boolean debug) throws IOException {
        this.tagCounter = 0;
        this.handlers = new CopyOnWriteArrayList<ResponseHandler>();
        this.host = "localhost";
        this.props = props;
        this.quote = false;
        this.tagPrefix = this.computePrefix(props, "mail.imap");
        this.logger = new MailLogger(this.getClass(), "DEBUG", debug, System.out);
        this.traceLogger = this.logger.getSubLogger("protocol", null);
        (this.traceInput = new TraceInputStream(in, this.traceLogger)).setQuote(this.quote);
        this.input = new ResponseInputStream(this.traceInput);
        (this.traceOutput = new TraceOutputStream(out, this.traceLogger)).setQuote(this.quote);
        this.output = new DataOutputStream(new BufferedOutputStream(this.traceOutput));
        this.timestamp = System.currentTimeMillis();
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public void addResponseHandler(final ResponseHandler h) {
        this.handlers.add(h);
    }
    
    public void removeResponseHandler(final ResponseHandler h) {
        this.handlers.remove(h);
    }
    
    public void notifyResponseHandlers(final Response[] responses) {
        if (this.handlers.isEmpty()) {
            return;
        }
        for (final Response r : responses) {
            if (r != null) {
                for (final ResponseHandler rh : this.handlers) {
                    if (rh != null) {
                        rh.handleResponse(r);
                    }
                }
            }
        }
    }
    
    protected void processGreeting(final Response r) throws ProtocolException {
        if (r.isBYE()) {
            throw new ConnectionException(this, r);
        }
    }
    
    protected ResponseInputStream getInputStream() {
        return this.input;
    }
    
    protected OutputStream getOutputStream() {
        return this.output;
    }
    
    protected synchronized boolean supportsNonSyncLiterals() {
        return false;
    }
    
    public Response readResponse() throws IOException, ProtocolException {
        return new Response(this);
    }
    
    public boolean hasResponse() {
        try {
            return this.input.available() > 0;
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    protected ByteArray getResponseBuffer() {
        return null;
    }
    
    public String writeCommand(final String command, final Argument args) throws IOException, ProtocolException {
        final String tag = this.tagPrefix + Integer.toString(this.tagCounter++);
        this.output.writeBytes(tag + " " + command);
        if (args != null) {
            this.output.write(32);
            args.write(this);
        }
        this.output.write(Protocol.CRLF);
        this.output.flush();
        return tag;
    }
    
    public synchronized Response[] command(final String command, final Argument args) {
        this.commandStart(command);
        final List<Response> v = new ArrayList<Response>();
        boolean done = false;
        String tag = null;
        try {
            tag = this.writeCommand(command, args);
        }
        catch (final LiteralException lex) {
            v.add(lex.getResponse());
            done = true;
        }
        catch (final Exception ex) {
            v.add(Response.byeResponse(ex));
            done = true;
        }
        Response byeResp = null;
        while (!done) {
            Response r = null;
            try {
                r = this.readResponse();
            }
            catch (final IOException ioex) {
                if (byeResp == null) {
                    byeResp = Response.byeResponse(ioex);
                }
                break;
            }
            catch (final ProtocolException pex) {
                this.logger.log(Level.FINE, "ignoring bad response", pex);
                continue;
            }
            if (r.isBYE()) {
                byeResp = r;
            }
            else {
                v.add(r);
                if (!r.isTagged() || !r.getTag().equals(tag)) {
                    continue;
                }
                done = true;
            }
        }
        if (byeResp != null) {
            v.add(byeResp);
        }
        final Response[] responses = new Response[v.size()];
        v.toArray(responses);
        this.timestamp = System.currentTimeMillis();
        this.commandEnd();
        return responses;
    }
    
    public void handleResult(final Response response) throws ProtocolException {
        if (response.isOK()) {
            return;
        }
        if (response.isNO()) {
            throw new CommandFailedException(response);
        }
        if (response.isBAD()) {
            throw new BadCommandException(response);
        }
        if (response.isBYE()) {
            this.disconnect();
            throw new ConnectionException(this, response);
        }
    }
    
    public void simpleCommand(final String cmd, final Argument args) throws ProtocolException {
        final Response[] r = this.command(cmd, args);
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
    }
    
    public synchronized void startTLS(final String cmd) throws IOException, ProtocolException {
        if (this.socket instanceof SSLSocket) {
            return;
        }
        this.simpleCommand(cmd, null);
        this.socket = SocketFetcher.startTLS(this.socket, this.host, this.props, this.prefix);
        this.initStreams();
    }
    
    public synchronized void startCompression(final String cmd) throws IOException, ProtocolException {
        this.simpleCommand(cmd, null);
        final Inflater inf = new Inflater(true);
        (this.traceInput = new TraceInputStream(new InflaterInputStream(this.socket.getInputStream(), inf), this.traceLogger)).setQuote(this.quote);
        this.input = new ResponseInputStream(this.traceInput);
        final int level = PropUtil.getIntProperty(this.props, this.prefix + ".compress.level", -1);
        final int strategy = PropUtil.getIntProperty(this.props, this.prefix + ".compress.strategy", 0);
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.log(Level.FINE, "Creating Deflater with compression level {0} and strategy {1}", level, strategy);
        }
        final Deflater def = new Deflater(-1, true);
        try {
            def.setLevel(level);
        }
        catch (final IllegalArgumentException ex) {
            this.logger.log(Level.FINE, "Ignoring bad compression level", ex);
        }
        try {
            def.setStrategy(strategy);
        }
        catch (final IllegalArgumentException ex) {
            this.logger.log(Level.FINE, "Ignoring bad compression strategy", ex);
        }
        (this.traceOutput = new TraceOutputStream(new DeflaterOutputStream(this.socket.getOutputStream(), def, true), this.traceLogger)).setQuote(this.quote);
        this.output = new DataOutputStream(new BufferedOutputStream(this.traceOutput));
    }
    
    public boolean isSSL() {
        return this.socket instanceof SSLSocket;
    }
    
    public InetAddress getInetAddress() {
        return this.socket.getInetAddress();
    }
    
    public SocketChannel getChannel() {
        SocketChannel ret = this.socket.getChannel();
        if (ret != null) {
            return ret;
        }
        if (this.socket instanceof SSLSocket) {
            try {
                final Field f = this.socket.getClass().getDeclaredField("socket");
                f.setAccessible(true);
                final Socket s = (Socket)f.get(this.socket);
                ret = s.getChannel();
            }
            catch (final Exception ex) {}
        }
        return ret;
    }
    
    public boolean supportsUtf8() {
        return false;
    }
    
    protected synchronized void disconnect() {
        if (this.socket != null) {
            try {
                this.socket.close();
            }
            catch (final IOException ex) {}
            this.socket = null;
        }
    }
    
    protected synchronized String getLocalHost() {
        if (this.localHostName == null || this.localHostName.length() <= 0) {
            this.localHostName = this.props.getProperty(this.prefix + ".localhost");
        }
        if (this.localHostName == null || this.localHostName.length() <= 0) {
            this.localHostName = this.props.getProperty(this.prefix + ".localaddress");
        }
        try {
            if (this.localHostName == null || this.localHostName.length() <= 0) {
                final InetAddress localHost = InetAddress.getLocalHost();
                this.localHostName = localHost.getCanonicalHostName();
                if (this.localHostName == null) {
                    this.localHostName = "[" + localHost.getHostAddress() + "]";
                }
            }
        }
        catch (final UnknownHostException ex) {}
        if ((this.localHostName == null || this.localHostName.length() <= 0) && this.socket != null && this.socket.isBound()) {
            final InetAddress localHost = this.socket.getLocalAddress();
            this.localHostName = localHost.getCanonicalHostName();
            if (this.localHostName == null) {
                this.localHostName = "[" + localHost.getHostAddress() + "]";
            }
        }
        return this.localHostName;
    }
    
    protected boolean isTracing() {
        return this.traceLogger.isLoggable(Level.FINEST);
    }
    
    protected void suspendTracing() {
        if (this.traceLogger.isLoggable(Level.FINEST)) {
            this.traceInput.setTrace(false);
            this.traceOutput.setTrace(false);
        }
    }
    
    protected void resumeTracing() {
        if (this.traceLogger.isLoggable(Level.FINEST)) {
            this.traceInput.setTrace(true);
            this.traceOutput.setTrace(true);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            this.disconnect();
        }
        finally {
            super.finalize();
        }
    }
    
    private void commandStart(final String command) {
    }
    
    private void commandEnd() {
    }
    
    static {
        tagNum = new AtomicInteger();
        CRLF = new byte[] { 13, 10 };
    }
}
