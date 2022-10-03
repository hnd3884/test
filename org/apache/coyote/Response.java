package org.apache.coyote;

import org.apache.juli.logging.LogFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.buf.ByteChunk;
import java.io.IOException;
import org.apache.tomcat.util.http.parser.MediaType;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.MessageBytes;
import javax.servlet.WriteListener;
import java.util.concurrent.atomic.AtomicInteger;
import java.nio.charset.Charset;
import org.apache.tomcat.util.http.MimeHeaders;
import java.util.Locale;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;

public final class Response
{
    private static final StringManager sm;
    private static final Log log;
    private static final Locale DEFAULT_LOCALE;
    int status;
    String message;
    final MimeHeaders headers;
    OutputBuffer outputBuffer;
    final Object[] notes;
    volatile boolean committed;
    volatile ActionHook hook;
    String contentType;
    String contentLanguage;
    Charset charset;
    String characterEncoding;
    long contentLength;
    private Locale locale;
    private long contentWritten;
    private long commitTime;
    private Exception errorException;
    private final AtomicInteger errorState;
    Request req;
    volatile WriteListener listener;
    private boolean fireListener;
    private boolean registeredForWrite;
    private final Object nonBlockingStateLock;
    
    public Response() {
        this.status = 200;
        this.message = null;
        this.headers = new MimeHeaders();
        this.notes = new Object[32];
        this.committed = false;
        this.contentType = null;
        this.contentLanguage = null;
        this.charset = null;
        this.characterEncoding = null;
        this.contentLength = -1L;
        this.locale = Response.DEFAULT_LOCALE;
        this.contentWritten = 0L;
        this.commitTime = -1L;
        this.errorException = null;
        this.errorState = new AtomicInteger(0);
        this.fireListener = false;
        this.registeredForWrite = false;
        this.nonBlockingStateLock = new Object();
    }
    
    public Request getRequest() {
        return this.req;
    }
    
    public void setRequest(final Request req) {
        this.req = req;
    }
    
    public void setOutputBuffer(final OutputBuffer outputBuffer) {
        this.outputBuffer = outputBuffer;
    }
    
    public MimeHeaders getMimeHeaders() {
        return this.headers;
    }
    
    protected void setHook(final ActionHook hook) {
        this.hook = hook;
    }
    
    public final void setNote(final int pos, final Object value) {
        this.notes[pos] = value;
    }
    
    public final Object getNote(final int pos) {
        return this.notes[pos];
    }
    
    public void action(final ActionCode actionCode, final Object param) {
        if (this.hook != null) {
            if (param == null) {
                this.hook.action(actionCode, this);
            }
            else {
                this.hook.action(actionCode, param);
            }
        }
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public void setStatus(final int status) {
        this.status = status;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
    public boolean isCommitted() {
        return this.committed;
    }
    
    public void setCommitted(final boolean v) {
        if (v && !this.committed) {
            this.commitTime = System.currentTimeMillis();
        }
        this.committed = v;
    }
    
    public long getCommitTime() {
        return this.commitTime;
    }
    
    public void setErrorException(final Exception ex) {
        this.errorException = ex;
    }
    
    public Exception getErrorException() {
        return this.errorException;
    }
    
    public boolean isExceptionPresent() {
        return this.errorException != null;
    }
    
    public boolean setError() {
        return this.errorState.compareAndSet(0, 1);
    }
    
    public boolean isError() {
        return this.errorState.get() > 0;
    }
    
    public boolean isErrorReportRequired() {
        return this.errorState.get() == 1;
    }
    
    public boolean setErrorReported() {
        return this.errorState.compareAndSet(1, 2);
    }
    
    public void reset() throws IllegalStateException {
        if (this.committed) {
            throw new IllegalStateException();
        }
        this.recycle();
    }
    
    public boolean containsHeader(final String name) {
        return this.headers.getHeader(name) != null;
    }
    
    public void setHeader(final String name, final String value) {
        final char cc = name.charAt(0);
        if ((cc == 'C' || cc == 'c') && this.checkSpecialHeader(name, value)) {
            return;
        }
        this.headers.setValue(name).setString(value);
    }
    
    public void addHeader(final String name, final String value) {
        this.addHeader(name, value, null);
    }
    
    public void addHeader(final String name, final String value, final Charset charset) {
        final char cc = name.charAt(0);
        if ((cc == 'C' || cc == 'c') && this.checkSpecialHeader(name, value)) {
            return;
        }
        final MessageBytes mb = this.headers.addValue(name);
        if (charset != null) {
            mb.setCharset(charset);
        }
        mb.setString(value);
    }
    
    private boolean checkSpecialHeader(final String name, final String value) {
        if (name.equalsIgnoreCase("Content-Type")) {
            this.setContentType(value);
            return true;
        }
        if (name.equalsIgnoreCase("Content-Length")) {
            try {
                final long cL = Long.parseLong(value);
                this.setContentLength(cL);
                return true;
            }
            catch (final NumberFormatException ex) {
                return false;
            }
        }
        return false;
    }
    
    public void sendHeaders() {
        this.action(ActionCode.COMMIT, this);
        this.setCommitted(true);
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    public void setLocale(final Locale locale) {
        if (locale == null) {
            this.locale = null;
            this.contentLanguage = null;
            return;
        }
        this.locale = locale;
        this.contentLanguage = locale.toLanguageTag();
    }
    
    public String getContentLanguage() {
        return this.contentLanguage;
    }
    
    public void setCharacterEncoding(final String characterEncoding) {
        if (this.isCommitted()) {
            return;
        }
        if (characterEncoding == null) {
            this.charset = null;
            this.characterEncoding = null;
            return;
        }
        this.characterEncoding = characterEncoding;
        try {
            this.charset = B2CConverter.getCharset(characterEncoding);
        }
        catch (final UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }
    
    public void setContentType(final String type) {
        if (type == null) {
            this.contentType = null;
            return;
        }
        MediaType m = null;
        try {
            m = MediaType.parseMediaType(new StringReader(type));
        }
        catch (final IOException ex) {}
        if (m == null) {
            this.contentType = type;
            return;
        }
        this.contentType = m.toStringNoCharset();
        String charsetValue = m.getCharset();
        if (charsetValue == null) {
            this.contentType = type;
        }
        else {
            this.contentType = m.toStringNoCharset();
            charsetValue = charsetValue.trim();
            if (charsetValue.length() > 0) {
                try {
                    this.charset = B2CConverter.getCharset(charsetValue);
                }
                catch (final UnsupportedEncodingException e) {
                    Response.log.warn((Object)Response.sm.getString("response.encoding.invalid", new Object[] { charsetValue }), (Throwable)e);
                }
            }
        }
    }
    
    public void setContentTypeNoCharset(final String type) {
        this.contentType = type;
    }
    
    public String getContentType() {
        String ret = this.contentType;
        if (ret != null && this.charset != null) {
            ret = ret + ";charset=" + this.characterEncoding;
        }
        return ret;
    }
    
    public void setContentLength(final long contentLength) {
        this.contentLength = contentLength;
    }
    
    public int getContentLength() {
        final long length = this.getContentLengthLong();
        if (length < 2147483647L) {
            return (int)length;
        }
        return -1;
    }
    
    public long getContentLengthLong() {
        return this.contentLength;
    }
    
    @Deprecated
    public void doWrite(final ByteChunk chunk) throws IOException {
        this.outputBuffer.doWrite(chunk);
        this.contentWritten += chunk.getLength();
    }
    
    public void doWrite(final ByteBuffer chunk) throws IOException {
        final int len = chunk.remaining();
        this.outputBuffer.doWrite(chunk);
        this.contentWritten += len - chunk.remaining();
    }
    
    public void recycle() {
        this.contentType = null;
        this.contentLanguage = null;
        this.locale = Response.DEFAULT_LOCALE;
        this.charset = null;
        this.characterEncoding = null;
        this.contentLength = -1L;
        this.status = 200;
        this.message = null;
        this.committed = false;
        this.commitTime = -1L;
        this.errorException = null;
        this.errorState.set(0);
        this.headers.clear();
        this.listener = null;
        synchronized (this.nonBlockingStateLock) {
            this.fireListener = false;
            this.registeredForWrite = false;
        }
        this.contentWritten = 0L;
    }
    
    public long getContentWritten() {
        return this.contentWritten;
    }
    
    public long getBytesWritten(final boolean flush) {
        if (flush) {
            this.action(ActionCode.CLIENT_FLUSH, this);
        }
        return this.outputBuffer.getBytesWritten();
    }
    
    public WriteListener getWriteListener() {
        return this.listener;
    }
    
    public void setWriteListener(final WriteListener listener) {
        if (listener == null) {
            throw new NullPointerException(Response.sm.getString("response.nullWriteListener"));
        }
        if (this.getWriteListener() != null) {
            throw new IllegalStateException(Response.sm.getString("response.writeListenerSet"));
        }
        final AtomicBoolean result = new AtomicBoolean(false);
        this.action(ActionCode.ASYNC_IS_ASYNC, result);
        if (!result.get()) {
            throw new IllegalStateException(Response.sm.getString("response.notAsync"));
        }
        this.listener = listener;
        if (this.isReady()) {
            synchronized (this.nonBlockingStateLock) {
                this.registeredForWrite = true;
                this.fireListener = true;
            }
            this.action(ActionCode.DISPATCH_WRITE, null);
            if (!ContainerThreadMarker.isContainerThread()) {
                this.action(ActionCode.DISPATCH_EXECUTE, null);
            }
        }
    }
    
    public boolean isReady() {
        if (this.listener == null) {
            if (Response.log.isDebugEnabled()) {
                Response.log.debug((Object)Response.sm.getString("response.notNonBlocking"));
            }
            return false;
        }
        boolean ready = false;
        synchronized (this.nonBlockingStateLock) {
            if (this.registeredForWrite) {
                this.fireListener = true;
                return false;
            }
            ready = this.checkRegisterForWrite();
            this.fireListener = !ready;
        }
        return ready;
    }
    
    public boolean checkRegisterForWrite() {
        final AtomicBoolean ready = new AtomicBoolean(false);
        synchronized (this.nonBlockingStateLock) {
            if (!this.registeredForWrite) {
                this.action(ActionCode.NB_WRITE_INTEREST, ready);
                this.registeredForWrite = !ready.get();
            }
        }
        return ready.get();
    }
    
    public void onWritePossible() throws IOException {
        boolean fire = false;
        synchronized (this.nonBlockingStateLock) {
            this.registeredForWrite = false;
            if (this.fireListener) {
                this.fireListener = false;
                fire = true;
            }
        }
        if (fire) {
            this.listener.onWritePossible();
        }
    }
    
    static {
        sm = StringManager.getManager((Class)Response.class);
        log = LogFactory.getLog((Class)Response.class);
        DEFAULT_LOCALE = Locale.getDefault();
    }
}
