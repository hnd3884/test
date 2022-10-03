package sun.nio.cs;

import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.io.UnsupportedEncodingException;
import java.nio.charset.IllegalCharsetNameException;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.io.Writer;

public class StreamEncoder extends Writer
{
    private static final int DEFAULT_BYTE_BUFFER_SIZE = 8192;
    private volatile boolean isOpen;
    private Charset cs;
    private CharsetEncoder encoder;
    private ByteBuffer bb;
    private final OutputStream out;
    private WritableByteChannel ch;
    private boolean haveLeftoverChar;
    private char leftoverChar;
    private CharBuffer lcb;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    private void ensureOpen() throws IOException {
        if (!this.isOpen) {
            throw new IOException("Stream closed");
        }
    }
    
    public static StreamEncoder forOutputStreamWriter(final OutputStream outputStream, final Object o, final String s) throws UnsupportedEncodingException {
        String name = s;
        if (name == null) {
            name = Charset.defaultCharset().name();
        }
        try {
            if (Charset.isSupported(name)) {
                return new StreamEncoder(outputStream, o, Charset.forName(name));
            }
        }
        catch (final IllegalCharsetNameException ex) {}
        throw new UnsupportedEncodingException(name);
    }
    
    public static StreamEncoder forOutputStreamWriter(final OutputStream outputStream, final Object o, final Charset charset) {
        return new StreamEncoder(outputStream, o, charset);
    }
    
    public static StreamEncoder forOutputStreamWriter(final OutputStream outputStream, final Object o, final CharsetEncoder charsetEncoder) {
        return new StreamEncoder(outputStream, o, charsetEncoder);
    }
    
    public static StreamEncoder forEncoder(final WritableByteChannel writableByteChannel, final CharsetEncoder charsetEncoder, final int n) {
        return new StreamEncoder(writableByteChannel, charsetEncoder, n);
    }
    
    public String getEncoding() {
        if (this.isOpen()) {
            return this.encodingName();
        }
        return null;
    }
    
    public void flushBuffer() throws IOException {
        synchronized (this.lock) {
            if (!this.isOpen()) {
                throw new IOException("Stream closed");
            }
            this.implFlushBuffer();
        }
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.write(new char[] { (char)n }, 0, 1);
    }
    
    @Override
    public void write(final char[] array, final int n, final int n2) throws IOException {
        synchronized (this.lock) {
            this.ensureOpen();
            if (n < 0 || n > array.length || n2 < 0 || n + n2 > array.length || n + n2 < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (n2 == 0) {
                return;
            }
            this.implWrite(array, n, n2);
        }
    }
    
    @Override
    public void write(final String s, final int n, final int n2) throws IOException {
        if (n2 < 0) {
            throw new IndexOutOfBoundsException();
        }
        final char[] array = new char[n2];
        s.getChars(n, n + n2, array, 0);
        this.write(array, 0, n2);
    }
    
    @Override
    public void flush() throws IOException {
        synchronized (this.lock) {
            this.ensureOpen();
            this.implFlush();
        }
    }
    
    @Override
    public void close() throws IOException {
        synchronized (this.lock) {
            if (!this.isOpen) {
                return;
            }
            this.implClose();
            this.isOpen = false;
        }
    }
    
    private boolean isOpen() {
        return this.isOpen;
    }
    
    private StreamEncoder(final OutputStream outputStream, final Object o, final Charset charset) {
        this(outputStream, o, charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE));
    }
    
    private StreamEncoder(final OutputStream out, final Object o, final CharsetEncoder encoder) {
        super(o);
        this.isOpen = true;
        this.haveLeftoverChar = false;
        this.lcb = null;
        this.out = out;
        this.ch = null;
        this.cs = encoder.charset();
        this.encoder = encoder;
        if (this.ch == null) {
            this.bb = ByteBuffer.allocate(8192);
        }
    }
    
    private StreamEncoder(final WritableByteChannel ch, final CharsetEncoder encoder, final int n) {
        this.isOpen = true;
        this.haveLeftoverChar = false;
        this.lcb = null;
        this.out = null;
        this.ch = ch;
        this.cs = encoder.charset();
        this.encoder = encoder;
        this.bb = ByteBuffer.allocate((n < 0) ? 8192 : n);
    }
    
    private void writeBytes() throws IOException {
        this.bb.flip();
        final int limit = this.bb.limit();
        final int position = this.bb.position();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        if (n > 0) {
            if (this.ch != null) {
                if (this.ch.write(this.bb) != n && !StreamEncoder.$assertionsDisabled) {
                    throw new AssertionError(n);
                }
            }
            else {
                this.out.write(this.bb.array(), this.bb.arrayOffset() + position, n);
            }
        }
        this.bb.clear();
    }
    
    private void flushLeftoverChar(final CharBuffer charBuffer, final boolean b) throws IOException {
        if (!this.haveLeftoverChar && !b) {
            return;
        }
        if (this.lcb == null) {
            this.lcb = CharBuffer.allocate(2);
        }
        else {
            this.lcb.clear();
        }
        if (this.haveLeftoverChar) {
            this.lcb.put(this.leftoverChar);
        }
        if (charBuffer != null && charBuffer.hasRemaining()) {
            this.lcb.put(charBuffer.get());
        }
        this.lcb.flip();
        while (this.lcb.hasRemaining() || b) {
            final CoderResult encode = this.encoder.encode(this.lcb, this.bb, b);
            if (encode.isUnderflow()) {
                if (this.lcb.hasRemaining()) {
                    this.leftoverChar = this.lcb.get();
                    if (charBuffer != null && charBuffer.hasRemaining()) {
                        this.flushLeftoverChar(charBuffer, b);
                    }
                    return;
                }
                break;
            }
            else if (encode.isOverflow()) {
                assert this.bb.position() > 0;
                this.writeBytes();
            }
            else {
                encode.throwException();
            }
        }
        this.haveLeftoverChar = false;
    }
    
    void implWrite(final char[] array, final int n, final int n2) throws IOException {
        final CharBuffer wrap = CharBuffer.wrap(array, n, n2);
        if (this.haveLeftoverChar) {
            this.flushLeftoverChar(wrap, false);
        }
        while (wrap.hasRemaining()) {
            final CoderResult encode = this.encoder.encode(wrap, this.bb, false);
            if (encode.isUnderflow()) {
                assert wrap.remaining() <= 1 : wrap.remaining();
                if (wrap.remaining() == 1) {
                    this.haveLeftoverChar = true;
                    this.leftoverChar = wrap.get();
                    break;
                }
                break;
            }
            else if (encode.isOverflow()) {
                assert this.bb.position() > 0;
                this.writeBytes();
            }
            else {
                encode.throwException();
            }
        }
    }
    
    void implFlushBuffer() throws IOException {
        if (this.bb.position() > 0) {
            this.writeBytes();
        }
    }
    
    void implFlush() throws IOException {
        this.implFlushBuffer();
        if (this.out != null) {
            this.out.flush();
        }
    }
    
    void implClose() throws IOException {
        this.flushLeftoverChar(null, true);
        try {
            while (true) {
                final CoderResult flush = this.encoder.flush(this.bb);
                if (flush.isUnderflow()) {
                    if (this.bb.position() > 0) {
                        this.writeBytes();
                    }
                    if (this.ch != null) {
                        this.ch.close();
                    }
                    else {
                        this.out.close();
                    }
                    break;
                }
                if (flush.isOverflow()) {
                    assert this.bb.position() > 0;
                    this.writeBytes();
                }
                else {
                    flush.throwException();
                }
            }
        }
        catch (final IOException ex) {
            this.encoder.reset();
            throw ex;
        }
    }
    
    String encodingName() {
        return (this.cs instanceof HistoricallyNamedCharset) ? ((HistoricallyNamedCharset)this.cs).historicalName() : this.cs.name();
    }
}
