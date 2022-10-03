package org.apache.catalina.connector;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.juli.logging.LogFactory;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import org.apache.catalina.security.SecurityUtil;
import org.apache.coyote.Constants;
import org.apache.coyote.ContainerThreadMarker;
import javax.servlet.ReadListener;
import org.apache.coyote.ActionCode;
import java.io.IOException;
import java.nio.Buffer;
import org.apache.coyote.Request;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.collections.SynchronizedStack;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentMap;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.buf.ByteChunk;
import java.io.Reader;

public class InputBuffer extends Reader implements ByteChunk.ByteInputChannel, ApplicationBufferHandler
{
    protected static final StringManager sm;
    private static final Log log;
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public final int INITIAL_STATE = 0;
    public final int CHAR_STATE = 1;
    public final int BYTE_STATE = 2;
    private static final ConcurrentMap<Charset, SynchronizedStack<B2CConverter>> encoders;
    private ByteBuffer bb;
    private CharBuffer cb;
    private int state;
    private boolean closed;
    private String enc;
    protected B2CConverter conv;
    private Request coyoteRequest;
    private int markPos;
    private int readLimit;
    private final int size;
    
    public InputBuffer() {
        this(8192);
    }
    
    public InputBuffer(final int size) {
        this.state = 0;
        this.closed = false;
        this.markPos = -1;
        this.size = size;
        this.clear(this.bb = ByteBuffer.allocate(size));
        this.clear(this.cb = CharBuffer.allocate(size));
        this.readLimit = size;
    }
    
    public void setRequest(final Request coyoteRequest) {
        this.coyoteRequest = coyoteRequest;
    }
    
    public void recycle() {
        this.state = 0;
        if (this.cb.capacity() > this.size) {
            this.clear(this.cb = CharBuffer.allocate(this.size));
        }
        else {
            this.clear(this.cb);
        }
        this.readLimit = this.size;
        this.markPos = -1;
        this.clear(this.bb);
        this.closed = false;
        if (this.conv != null) {
            this.conv.recycle();
            InputBuffer.encoders.get(this.conv.getCharset()).push((Object)this.conv);
            this.conv = null;
        }
        this.enc = null;
    }
    
    @Override
    public void close() throws IOException {
        this.closed = true;
    }
    
    public int available() {
        int available = this.availableInThisBuffer();
        if (available == 0) {
            this.coyoteRequest.action(ActionCode.AVAILABLE, (Object)(this.coyoteRequest.getReadListener() != null));
            available = ((this.coyoteRequest.getAvailable() > 0) ? 1 : 0);
        }
        return available;
    }
    
    private int availableInThisBuffer() {
        int available = 0;
        if (this.state == 2) {
            available = this.bb.remaining();
        }
        else if (this.state == 1) {
            available = this.cb.remaining();
        }
        return available;
    }
    
    public void setReadListener(final ReadListener listener) {
        this.coyoteRequest.setReadListener(listener);
    }
    
    public boolean isFinished() {
        int available = 0;
        if (this.state == 2) {
            available = this.bb.remaining();
        }
        else if (this.state == 1) {
            available = this.cb.remaining();
        }
        return available <= 0 && this.coyoteRequest.isFinished();
    }
    
    public boolean isReady() {
        if (this.coyoteRequest.getReadListener() == null) {
            if (InputBuffer.log.isDebugEnabled()) {
                InputBuffer.log.debug((Object)InputBuffer.sm.getString("inputBuffer.requiresNonBlocking"));
            }
            return false;
        }
        if (this.isFinished()) {
            if (!ContainerThreadMarker.isContainerThread()) {
                this.coyoteRequest.action(ActionCode.DISPATCH_READ, (Object)null);
                this.coyoteRequest.action(ActionCode.DISPATCH_EXECUTE, (Object)null);
            }
            return false;
        }
        return this.availableInThisBuffer() > 0 || this.coyoteRequest.isReady();
    }
    
    boolean isBlocking() {
        return this.coyoteRequest.getReadListener() == null;
    }
    
    public int realReadBytes() throws IOException {
        if (this.closed) {
            return -1;
        }
        if (this.coyoteRequest == null) {
            return -1;
        }
        if (this.state == 0) {
            this.state = 2;
        }
        try {
            return this.coyoteRequest.doRead((ApplicationBufferHandler)this);
        }
        catch (final IOException ioe) {
            this.coyoteRequest.setErrorException((Exception)ioe);
            throw new ClientAbortException(ioe);
        }
    }
    
    public int readByte() throws IOException {
        this.throwIfClosed();
        if (this.checkByteBufferEof()) {
            return -1;
        }
        return this.bb.get() & 0xFF;
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        this.throwIfClosed();
        if (this.checkByteBufferEof()) {
            return -1;
        }
        final int n = Math.min(len, this.bb.remaining());
        this.bb.get(b, off, n);
        return n;
    }
    
    public int read(final ByteBuffer to) throws IOException {
        this.throwIfClosed();
        if (this.checkByteBufferEof()) {
            return -1;
        }
        final int n = Math.min(to.remaining(), this.bb.remaining());
        final int orgLimit = this.bb.limit();
        this.bb.limit(this.bb.position() + n);
        to.put(this.bb);
        this.bb.limit(orgLimit);
        to.limit(to.position()).position(to.position() - n);
        return n;
    }
    
    @Deprecated
    public void setEncoding(final String s) {
        this.enc = s;
    }
    
    public int realReadChars() throws IOException {
        this.checkConverter();
        boolean eof = false;
        if (this.bb.remaining() <= 0) {
            final int nRead = this.realReadBytes();
            if (nRead < 0) {
                eof = true;
            }
        }
        if (this.markPos == -1) {
            this.clear(this.cb);
        }
        else {
            this.makeSpace(this.bb.remaining());
            if (this.cb.capacity() - this.cb.limit() == 0 && this.bb.remaining() != 0) {
                this.clear(this.cb);
                this.markPos = -1;
            }
        }
        this.state = 1;
        this.conv.convert(this.bb, this.cb, (ByteChunk.ByteInputChannel)this, eof);
        if (this.cb.remaining() == 0 && eof) {
            return -1;
        }
        return this.cb.remaining();
    }
    
    @Override
    public int read() throws IOException {
        this.throwIfClosed();
        if (this.checkCharBufferEof()) {
            return -1;
        }
        return this.cb.get();
    }
    
    @Override
    public int read(final char[] cbuf) throws IOException {
        this.throwIfClosed();
        return this.read(cbuf, 0, cbuf.length);
    }
    
    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        this.throwIfClosed();
        if (this.checkCharBufferEof()) {
            return -1;
        }
        final int n = Math.min(len, this.cb.remaining());
        this.cb.get(cbuf, off, n);
        return n;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        this.throwIfClosed();
        if (n < 0L) {
            throw new IllegalArgumentException();
        }
        long nRead = 0L;
        while (nRead < n) {
            if (this.cb.remaining() >= n) {
                this.cb.position(this.cb.position() + (int)n);
                nRead = n;
            }
            else {
                nRead += this.cb.remaining();
                this.cb.position(this.cb.limit());
                final int nb = this.realReadChars();
                if (nb < 0) {
                    break;
                }
                continue;
            }
        }
        return nRead;
    }
    
    @Override
    public boolean ready() throws IOException {
        this.throwIfClosed();
        if (this.state == 0) {
            this.state = 1;
        }
        return this.available() > 0;
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public void mark(final int readAheadLimit) throws IOException {
        this.throwIfClosed();
        if (this.cb.remaining() <= 0) {
            this.clear(this.cb);
        }
        else if (this.cb.capacity() > 2 * this.size && this.cb.remaining() < this.cb.position()) {
            this.cb.compact();
            this.cb.flip();
        }
        this.readLimit = this.cb.position() + readAheadLimit + this.size;
        this.markPos = this.cb.position();
    }
    
    @Override
    public void reset() throws IOException {
        this.throwIfClosed();
        if (this.state == 1) {
            if (this.markPos < 0) {
                this.clear(this.cb);
                this.markPos = -1;
                final IOException ioe = new IOException();
                this.coyoteRequest.setErrorException((Exception)ioe);
                throw ioe;
            }
            this.cb.position(this.markPos);
        }
        else {
            this.clear(this.bb);
        }
    }
    
    private void throwIfClosed() throws IOException {
        if (this.closed) {
            final IOException ioe = new IOException(InputBuffer.sm.getString("inputBuffer.streamClosed"));
            this.coyoteRequest.setErrorException((Exception)ioe);
            throw ioe;
        }
    }
    
    public void checkConverter() throws IOException {
        if (this.conv != null) {
            return;
        }
        Charset charset = null;
        if (this.coyoteRequest != null) {
            charset = this.coyoteRequest.getCharset();
        }
        if (charset == null) {
            if (this.enc == null) {
                charset = Constants.DEFAULT_BODY_CHARSET;
            }
            else {
                charset = B2CConverter.getCharset(this.enc);
            }
        }
        SynchronizedStack<B2CConverter> stack = (SynchronizedStack<B2CConverter>)InputBuffer.encoders.get(charset);
        if (stack == null) {
            stack = (SynchronizedStack<B2CConverter>)new SynchronizedStack();
            InputBuffer.encoders.putIfAbsent(charset, stack);
            stack = (SynchronizedStack<B2CConverter>)InputBuffer.encoders.get(charset);
        }
        this.conv = (B2CConverter)stack.pop();
        if (this.conv == null) {
            this.conv = createConverter(charset);
        }
    }
    
    private static B2CConverter createConverter(final Charset charset) throws IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                return AccessController.doPrivileged((PrivilegedExceptionAction<B2CConverter>)new PrivilegedExceptionAction<B2CConverter>() {
                    @Override
                    public B2CConverter run() throws IOException {
                        return new B2CConverter(charset);
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                final Exception e = ex.getException();
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                throw new IOException(e);
            }
        }
        return new B2CConverter(charset);
    }
    
    public void setByteBuffer(final ByteBuffer buffer) {
        this.bb = buffer;
    }
    
    public ByteBuffer getByteBuffer() {
        return this.bb;
    }
    
    public void expand(final int size) {
    }
    
    private boolean checkByteBufferEof() throws IOException {
        if (this.bb.remaining() == 0) {
            final int n = this.realReadBytes();
            if (n < 0) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkCharBufferEof() throws IOException {
        if (this.cb.remaining() == 0) {
            final int n = this.realReadChars();
            if (n < 0) {
                return true;
            }
        }
        return false;
    }
    
    private void clear(final Buffer buffer) {
        buffer.rewind().limit(0);
    }
    
    private void makeSpace(final int count) {
        int desiredSize = this.cb.limit() + count;
        if (desiredSize > this.readLimit) {
            desiredSize = this.readLimit;
        }
        if (desiredSize <= this.cb.capacity()) {
            return;
        }
        int newSize = 2 * this.cb.capacity();
        if (desiredSize >= newSize) {
            newSize = 2 * this.cb.capacity() + count;
        }
        if (newSize > this.readLimit) {
            newSize = this.readLimit;
        }
        CharBuffer tmp = CharBuffer.allocate(newSize);
        final int oldPosition = this.cb.position();
        this.cb.position(0);
        tmp.put(this.cb);
        tmp.flip();
        tmp.position(oldPosition);
        this.cb = tmp;
        tmp = null;
    }
    
    static {
        sm = StringManager.getManager((Class)InputBuffer.class);
        log = LogFactory.getLog((Class)InputBuffer.class);
        encoders = new ConcurrentHashMap<Charset, SynchronizedStack<B2CConverter>>();
    }
}
