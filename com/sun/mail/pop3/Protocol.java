package com.sun.mail.pop3;

import com.sun.mail.util.SharedByteArrayOutputStream;
import java.net.SocketException;
import com.sun.mail.util.LineInputStream;
import java.io.EOFException;
import java.io.InterruptedIOException;
import java.util.StringTokenizer;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import javax.net.ssl.SSLSocket;
import java.util.Locale;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import com.sun.mail.util.SocketFetcher;
import java.util.logging.Level;
import com.sun.mail.util.PropUtil;
import java.util.Map;
import com.sun.mail.util.MailLogger;
import com.sun.mail.util.TraceOutputStream;
import com.sun.mail.util.TraceInputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.util.Properties;
import java.net.Socket;

class Protocol
{
    private Socket socket;
    private String host;
    private Properties props;
    private String prefix;
    private BufferedReader input;
    private PrintWriter output;
    private TraceInputStream traceInput;
    private TraceOutputStream traceOutput;
    private MailLogger logger;
    private MailLogger traceLogger;
    private String apopChallenge;
    private Map<String, String> capabilities;
    private boolean pipelining;
    private boolean noauthdebug;
    private boolean traceSuspended;
    private static final int POP3_PORT = 110;
    private static final String CRLF = "\r\n";
    private static final int SLOP = 128;
    private static char[] digits;
    
    Protocol(final String host, int port, final MailLogger logger, final Properties props, final String prefix, final boolean isSSL) throws IOException {
        this.apopChallenge = null;
        this.capabilities = null;
        this.noauthdebug = true;
        this.host = host;
        this.props = props;
        this.prefix = prefix;
        this.logger = logger;
        this.traceLogger = logger.getSubLogger("protocol", null);
        this.noauthdebug = !PropUtil.getBooleanProperty(props, "mail.debug.auth", false);
        final boolean enableAPOP = this.getBoolProp(props, prefix + ".apop.enable");
        final boolean disableCapa = this.getBoolProp(props, prefix + ".disablecapa");
        Response r;
        try {
            if (port == -1) {
                port = 110;
            }
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("connecting to host \"" + host + "\", port " + port + ", isSSL " + isSSL);
            }
            this.socket = SocketFetcher.getSocket(host, port, props, prefix, isSSL);
            this.initStreams();
            r = this.simpleCommand(null);
        }
        catch (final IOException ioe) {
            throw cleanupAndThrow(this.socket, ioe);
        }
        if (!r.ok) {
            throw cleanupAndThrow(this.socket, new IOException("Connect failed"));
        }
        if (enableAPOP && r.data != null) {
            final int challStart = r.data.indexOf(60);
            final int challEnd = r.data.indexOf(62, challStart);
            if (challStart != -1 && challEnd != -1) {
                this.apopChallenge = r.data.substring(challStart, challEnd + 1);
            }
            logger.log(Level.FINE, "APOP challenge: {0}", this.apopChallenge);
        }
        if (!disableCapa) {
            this.setCapabilities(this.capa());
        }
        this.pipelining = (this.hasCapability("PIPELINING") || PropUtil.getBooleanProperty(props, prefix + ".pipelining", false));
        if (this.pipelining) {
            logger.config("PIPELINING enabled");
        }
    }
    
    private static IOException cleanupAndThrow(final Socket socket, final IOException ife) {
        try {
            socket.close();
        }
        catch (final Throwable thr) {
            if (isRecoverable(thr)) {
                ife.addSuppressed(thr);
            }
            else {
                thr.addSuppressed(ife);
                if (thr instanceof Error) {
                    throw (Error)thr;
                }
                if (thr instanceof RuntimeException) {
                    throw (RuntimeException)thr;
                }
                throw new RuntimeException("unexpected exception", thr);
            }
        }
        return ife;
    }
    
    private static boolean isRecoverable(final Throwable t) {
        return t instanceof Exception || t instanceof LinkageError;
    }
    
    private final synchronized boolean getBoolProp(final Properties props, final String prop) {
        final boolean val = PropUtil.getBooleanProperty(props, prop, false);
        if (this.logger.isLoggable(Level.CONFIG)) {
            this.logger.config(prop + ": " + val);
        }
        return val;
    }
    
    private void initStreams() throws IOException {
        final boolean quote = PropUtil.getBooleanProperty(this.props, "mail.debug.quote", false);
        (this.traceInput = new TraceInputStream(this.socket.getInputStream(), this.traceLogger)).setQuote(quote);
        (this.traceOutput = new TraceOutputStream(this.socket.getOutputStream(), this.traceLogger)).setQuote(quote);
        this.input = new BufferedReader(new InputStreamReader(this.traceInput, "iso-8859-1"));
        this.output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.traceOutput, "iso-8859-1")));
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.socket != null) {
                this.quit();
            }
        }
        finally {
            super.finalize();
        }
    }
    
    synchronized void setCapabilities(final InputStream in) {
        if (in == null) {
            this.capabilities = null;
            return;
        }
        this.capabilities = new HashMap<String, String>(10);
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(in, "us-ascii"));
        }
        catch (final UnsupportedEncodingException ex) {
            assert false;
        }
        try {
            String s;
            while ((s = r.readLine()) != null) {
                String cap = s;
                final int i = cap.indexOf(32);
                if (i > 0) {
                    cap = cap.substring(0, i);
                }
                this.capabilities.put(cap.toUpperCase(Locale.ENGLISH), s);
            }
        }
        catch (final IOException ex2) {}
        finally {
            try {
                in.close();
            }
            catch (final IOException ex3) {}
        }
    }
    
    synchronized boolean hasCapability(final String c) {
        return this.capabilities != null && this.capabilities.containsKey(c.toUpperCase(Locale.ENGLISH));
    }
    
    synchronized Map<String, String> getCapabilities() {
        return this.capabilities;
    }
    
    synchronized String login(final String user, final String password) throws IOException {
        final boolean batch = this.pipelining && this.socket instanceof SSLSocket;
        try {
            if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("authentication command trace suppressed");
                this.suspendTracing();
            }
            String dpw = null;
            if (this.apopChallenge != null) {
                dpw = this.getDigest(password);
            }
            Response r;
            if (this.apopChallenge != null && dpw != null) {
                r = this.simpleCommand("APOP " + user + " " + dpw);
            }
            else if (batch) {
                String cmd = "USER " + user;
                this.batchCommandStart(cmd);
                this.issueCommand(cmd);
                cmd = "PASS " + password;
                this.batchCommandContinue(cmd);
                this.issueCommand(cmd);
                r = this.readResponse();
                if (!r.ok) {
                    final String err = (r.data != null) ? r.data : "USER command failed";
                    this.readResponse();
                    this.batchCommandEnd();
                    return err;
                }
                r = this.readResponse();
                this.batchCommandEnd();
            }
            else {
                r = this.simpleCommand("USER " + user);
                if (!r.ok) {
                    return (r.data != null) ? r.data : "USER command failed";
                }
                r = this.simpleCommand("PASS " + password);
            }
            if (this.noauthdebug && this.isTracing()) {
                this.logger.log(Level.FINE, "authentication command {0}", r.ok ? "succeeded" : "failed");
            }
            if (!r.ok) {
                return (r.data != null) ? r.data : "login failed";
            }
            return null;
        }
        finally {
            this.resumeTracing();
        }
    }
    
    private String getDigest(final String password) {
        final String key = this.apopChallenge + password;
        byte[] digest;
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            digest = md.digest(key.getBytes("iso-8859-1"));
        }
        catch (final NoSuchAlgorithmException nsae) {
            return null;
        }
        catch (final UnsupportedEncodingException uee) {
            return null;
        }
        return toHex(digest);
    }
    
    private static String toHex(final byte[] bytes) {
        final char[] result = new char[bytes.length * 2];
        int index = 0;
        int i = 0;
        while (index < bytes.length) {
            final int temp = bytes[index] & 0xFF;
            result[i++] = Protocol.digits[temp >> 4];
            result[i++] = Protocol.digits[temp & 0xF];
            ++index;
        }
        return new String(result);
    }
    
    synchronized boolean quit() throws IOException {
        boolean ok = false;
        try {
            final Response r = this.simpleCommand("QUIT");
            ok = r.ok;
        }
        finally {
            this.close();
        }
        return ok;
    }
    
    void close() {
        try {
            this.socket.close();
        }
        catch (final IOException ex) {}
        finally {
            this.socket = null;
            this.input = null;
            this.output = null;
        }
    }
    
    synchronized Status stat() throws IOException {
        final Response r = this.simpleCommand("STAT");
        final Status s = new Status();
        if (!r.ok) {
            throw new IOException("STAT command failed: " + r.data);
        }
        if (r.data != null) {
            try {
                final StringTokenizer st = new StringTokenizer(r.data);
                s.total = Integer.parseInt(st.nextToken());
                s.size = Integer.parseInt(st.nextToken());
            }
            catch (final RuntimeException ex) {}
        }
        return s;
    }
    
    synchronized int list(final int msg) throws IOException {
        final Response r = this.simpleCommand("LIST " + msg);
        int size = -1;
        if (r.ok && r.data != null) {
            try {
                final StringTokenizer st = new StringTokenizer(r.data);
                st.nextToken();
                size = Integer.parseInt(st.nextToken());
            }
            catch (final RuntimeException ex) {}
        }
        return size;
    }
    
    synchronized InputStream list() throws IOException {
        final Response r = this.multilineCommand("LIST", 128);
        return r.bytes;
    }
    
    synchronized InputStream retr(final int msg, int size) throws IOException {
        final boolean batch = size == 0 && this.pipelining;
        Response r;
        if (batch) {
            String cmd = "LIST " + msg;
            this.batchCommandStart(cmd);
            this.issueCommand(cmd);
            cmd = "RETR " + msg;
            this.batchCommandContinue(cmd);
            this.issueCommand(cmd);
            r = this.readResponse();
            if (r.ok && r.data != null) {
                try {
                    final StringTokenizer st = new StringTokenizer(r.data);
                    st.nextToken();
                    size = Integer.parseInt(st.nextToken());
                    if (size > 1073741824 || size < 0) {
                        size = 0;
                    }
                    else {
                        if (this.logger.isLoggable(Level.FINE)) {
                            this.logger.fine("pipeline message size " + size);
                        }
                        size += 128;
                    }
                }
                catch (final RuntimeException ex) {}
            }
            r = this.readResponse();
            if (r.ok) {
                r.bytes = this.readMultilineResponse(size + 128);
            }
            this.batchCommandEnd();
        }
        else {
            final String cmd = "RETR " + msg;
            this.multilineCommandStart(cmd);
            this.issueCommand(cmd);
            r = this.readResponse();
            if (!r.ok) {
                this.multilineCommandEnd();
                return null;
            }
            if (size <= 0 && r.data != null) {
                try {
                    final StringTokenizer st = new StringTokenizer(r.data);
                    final String s = st.nextToken();
                    final String octets = st.nextToken();
                    if (octets.equals("octets")) {
                        size = Integer.parseInt(s);
                        if (size > 1073741824 || size < 0) {
                            size = 0;
                        }
                        else {
                            if (this.logger.isLoggable(Level.FINE)) {
                                this.logger.fine("guessing message size: " + size);
                            }
                            size += 128;
                        }
                    }
                }
                catch (final RuntimeException ex2) {}
            }
            r.bytes = this.readMultilineResponse(size);
            this.multilineCommandEnd();
        }
        if (r.ok && size > 0 && this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("got message size " + r.bytes.available());
        }
        return r.bytes;
    }
    
    synchronized boolean retr(final int msg, final OutputStream os) throws IOException {
        final String cmd = "RETR " + msg;
        this.multilineCommandStart(cmd);
        this.issueCommand(cmd);
        final Response r = this.readResponse();
        if (!r.ok) {
            this.multilineCommandEnd();
            return false;
        }
        Throwable terr = null;
        int lastb = 10;
        int b;
        try {
            while ((b = this.input.read()) >= 0) {
                if (lastb == 10 && b == 46) {
                    b = this.input.read();
                    if (b == 13) {
                        b = this.input.read();
                        break;
                    }
                }
                if (terr == null) {
                    try {
                        os.write(b);
                    }
                    catch (final IOException ex) {
                        this.logger.log(Level.FINE, "exception while streaming", ex);
                        terr = ex;
                    }
                    catch (final RuntimeException ex2) {
                        this.logger.log(Level.FINE, "exception while streaming", ex2);
                        terr = ex2;
                    }
                }
                lastb = b;
            }
        }
        catch (final InterruptedIOException iioex) {
            try {
                this.socket.close();
            }
            catch (final IOException ex3) {}
            throw iioex;
        }
        if (b < 0) {
            throw new EOFException("EOF on socket");
        }
        if (terr != null) {
            if (terr instanceof IOException) {
                throw (IOException)terr;
            }
            if (terr instanceof RuntimeException) {
                throw (RuntimeException)terr;
            }
            assert false;
        }
        this.multilineCommandEnd();
        return true;
    }
    
    synchronized InputStream top(final int msg, final int n) throws IOException {
        final Response r = this.multilineCommand("TOP " + msg + " " + n, 0);
        return r.bytes;
    }
    
    synchronized boolean dele(final int msg) throws IOException {
        final Response r = this.simpleCommand("DELE " + msg);
        return r.ok;
    }
    
    synchronized String uidl(final int msg) throws IOException {
        final Response r = this.simpleCommand("UIDL " + msg);
        if (!r.ok) {
            return null;
        }
        final int i = r.data.indexOf(32);
        if (i > 0) {
            return r.data.substring(i + 1);
        }
        return null;
    }
    
    synchronized boolean uidl(final String[] uids) throws IOException {
        final Response r = this.multilineCommand("UIDL", 15 * uids.length);
        if (!r.ok) {
            return false;
        }
        final LineInputStream lis = new LineInputStream(r.bytes);
        String line = null;
        while ((line = lis.readLine()) != null) {
            final int i = line.indexOf(32);
            if (i >= 1) {
                if (i >= line.length()) {
                    continue;
                }
                final int n = Integer.parseInt(line.substring(0, i));
                if (n <= 0 || n > uids.length) {
                    continue;
                }
                uids[n - 1] = line.substring(i + 1);
            }
        }
        try {
            r.bytes.close();
        }
        catch (final IOException ex) {}
        return true;
    }
    
    synchronized boolean noop() throws IOException {
        final Response r = this.simpleCommand("NOOP");
        return r.ok;
    }
    
    synchronized boolean rset() throws IOException {
        final Response r = this.simpleCommand("RSET");
        return r.ok;
    }
    
    synchronized boolean stls() throws IOException {
        if (this.socket instanceof SSLSocket) {
            return true;
        }
        final Response r = this.simpleCommand("STLS");
        if (r.ok) {
            try {
                this.socket = SocketFetcher.startTLS(this.socket, this.host, this.props, this.prefix);
                this.initStreams();
            }
            catch (final IOException ioex) {
                try {
                    this.socket.close();
                    this.socket = null;
                    this.input = null;
                    this.output = null;
                }
                finally {
                    this.socket = null;
                    this.input = null;
                    this.output = null;
                }
                final IOException sioex = new IOException("Could not convert socket to TLS");
                sioex.initCause(ioex);
            }
        }
        return r.ok;
    }
    
    synchronized boolean isSSL() {
        return this.socket instanceof SSLSocket;
    }
    
    synchronized InputStream capa() throws IOException {
        final Response r = this.multilineCommand("CAPA", 128);
        if (!r.ok) {
            return null;
        }
        return r.bytes;
    }
    
    private Response simpleCommand(final String cmd) throws IOException {
        this.simpleCommandStart(cmd);
        this.issueCommand(cmd);
        final Response r = this.readResponse();
        this.simpleCommandEnd();
        return r;
    }
    
    private void issueCommand(String cmd) throws IOException {
        if (this.socket == null) {
            throw new IOException("Folder is closed");
        }
        if (cmd != null) {
            cmd += "\r\n";
            this.output.print(cmd);
            this.output.flush();
        }
    }
    
    private Response readResponse() throws IOException {
        String line = null;
        try {
            line = this.input.readLine();
        }
        catch (final InterruptedIOException iioex) {
            try {
                this.socket.close();
            }
            catch (final IOException ex2) {}
            throw new EOFException(iioex.getMessage());
        }
        catch (final SocketException ex) {
            try {
                this.socket.close();
            }
            catch (final IOException ex3) {}
            throw new EOFException(ex.getMessage());
        }
        if (line == null) {
            this.traceLogger.finest("<EOF>");
            throw new EOFException("EOF on socket");
        }
        final Response r = new Response();
        if (line.startsWith("+OK")) {
            r.ok = true;
        }
        else {
            if (!line.startsWith("-ERR")) {
                throw new IOException("Unexpected response: " + line);
            }
            r.ok = false;
        }
        final int i;
        if ((i = line.indexOf(32)) >= 0) {
            r.data = line.substring(i + 1);
        }
        return r;
    }
    
    private Response multilineCommand(final String cmd, final int size) throws IOException {
        this.multilineCommandStart(cmd);
        this.issueCommand(cmd);
        final Response r = this.readResponse();
        if (!r.ok) {
            this.multilineCommandEnd();
            return r;
        }
        r.bytes = this.readMultilineResponse(size);
        this.multilineCommandEnd();
        return r;
    }
    
    private InputStream readMultilineResponse(final int size) throws IOException {
        final SharedByteArrayOutputStream buf = new SharedByteArrayOutputStream(size);
        int lastb = 10;
        int b;
        try {
            while ((b = this.input.read()) >= 0) {
                if (lastb == 10 && b == 46) {
                    b = this.input.read();
                    if (b == 13) {
                        b = this.input.read();
                        break;
                    }
                }
                buf.write(b);
                lastb = b;
            }
        }
        catch (final InterruptedIOException iioex) {
            try {
                this.socket.close();
            }
            catch (final IOException ex) {}
            throw iioex;
        }
        if (b < 0) {
            throw new EOFException("EOF on socket");
        }
        return buf.toStream();
    }
    
    protected boolean isTracing() {
        return this.traceLogger.isLoggable(Level.FINEST);
    }
    
    private void suspendTracing() {
        if (this.traceLogger.isLoggable(Level.FINEST)) {
            this.traceInput.setTrace(false);
            this.traceOutput.setTrace(false);
        }
    }
    
    private void resumeTracing() {
        if (this.traceLogger.isLoggable(Level.FINEST)) {
            this.traceInput.setTrace(true);
            this.traceOutput.setTrace(true);
        }
    }
    
    private void simpleCommandStart(final String command) {
    }
    
    private void simpleCommandEnd() {
    }
    
    private void multilineCommandStart(final String command) {
    }
    
    private void multilineCommandEnd() {
    }
    
    private void batchCommandStart(final String command) {
    }
    
    private void batchCommandContinue(final String command) {
    }
    
    private void batchCommandEnd() {
    }
    
    static {
        Protocol.digits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
