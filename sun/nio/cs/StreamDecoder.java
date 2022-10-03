package sun.nio.cs;

import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.IllegalCharsetNameException;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;
import java.io.Reader;

public class StreamDecoder extends Reader
{
    private static final int MIN_BYTE_BUFFER_SIZE = 32;
    private static final int DEFAULT_BYTE_BUFFER_SIZE = 8192;
    private volatile boolean isOpen;
    private boolean haveLeftoverChar;
    private char leftoverChar;
    private static volatile boolean channelsAvailable;
    private Charset cs;
    private CharsetDecoder decoder;
    private ByteBuffer bb;
    private InputStream in;
    private ReadableByteChannel ch;
    
    private void ensureOpen() throws IOException {
        if (!this.isOpen) {
            throw new IOException("Stream closed");
        }
    }
    
    public static StreamDecoder forInputStreamReader(final InputStream inputStream, final Object o, final String s) throws UnsupportedEncodingException {
        String name = s;
        if (name == null) {
            name = Charset.defaultCharset().name();
        }
        try {
            if (Charset.isSupported(name)) {
                return new StreamDecoder(inputStream, o, Charset.forName(name));
            }
        }
        catch (final IllegalCharsetNameException ex) {}
        throw new UnsupportedEncodingException(name);
    }
    
    public static StreamDecoder forInputStreamReader(final InputStream inputStream, final Object o, final Charset charset) {
        return new StreamDecoder(inputStream, o, charset);
    }
    
    public static StreamDecoder forInputStreamReader(final InputStream inputStream, final Object o, final CharsetDecoder charsetDecoder) {
        return new StreamDecoder(inputStream, o, charsetDecoder);
    }
    
    public static StreamDecoder forDecoder(final ReadableByteChannel readableByteChannel, final CharsetDecoder charsetDecoder, final int n) {
        return new StreamDecoder(readableByteChannel, charsetDecoder, n);
    }
    
    public String getEncoding() {
        if (this.isOpen()) {
            return this.encodingName();
        }
        return null;
    }
    
    @Override
    public int read() throws IOException {
        return this.read0();
    }
    
    private int read0() throws IOException {
        synchronized (this.lock) {
            if (this.haveLeftoverChar) {
                this.haveLeftoverChar = false;
                return this.leftoverChar;
            }
            final char[] array = new char[2];
            final int read = this.read(array, 0, 2);
            switch (read) {
                case -1: {
                    return -1;
                }
                case 2: {
                    this.leftoverChar = array[1];
                    this.haveLeftoverChar = true;
                }
                case 1: {
                    return array[0];
                }
                default: {
                    assert false : read;
                    return -1;
                }
            }
        }
    }
    
    @Override
    public int read(final char[] array, final int n, final int n2) throws IOException {
        int n3 = n;
        int n4 = n2;
        synchronized (this.lock) {
            this.ensureOpen();
            if (n3 < 0 || n3 > array.length || n4 < 0 || n3 + n4 > array.length || n3 + n4 < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (n4 == 0) {
                return 0;
            }
            int n5 = 0;
            if (this.haveLeftoverChar) {
                array[n3] = this.leftoverChar;
                ++n3;
                --n4;
                this.haveLeftoverChar = false;
                n5 = 1;
                if (n4 == 0 || !this.implReady()) {
                    return n5;
                }
            }
            if (n4 != 1) {
                return n5 + this.implRead(array, n3, n3 + n4);
            }
            final int read0 = this.read0();
            if (read0 == -1) {
                return (n5 == 0) ? -1 : n5;
            }
            array[n3] = (char)read0;
            return n5 + 1;
        }
    }
    
    @Override
    public boolean ready() throws IOException {
        synchronized (this.lock) {
            this.ensureOpen();
            return this.haveLeftoverChar || this.implReady();
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
    
    private static FileChannel getChannel(final FileInputStream fileInputStream) {
        if (!StreamDecoder.channelsAvailable) {
            return null;
        }
        try {
            return fileInputStream.getChannel();
        }
        catch (final UnsatisfiedLinkError unsatisfiedLinkError) {
            StreamDecoder.channelsAvailable = false;
            return null;
        }
    }
    
    StreamDecoder(final InputStream inputStream, final Object o, final Charset charset) {
        this(inputStream, o, charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE));
    }
    
    StreamDecoder(final InputStream in, final Object o, final CharsetDecoder decoder) {
        super(o);
        this.isOpen = true;
        this.haveLeftoverChar = false;
        this.cs = decoder.charset();
        this.decoder = decoder;
        if (this.ch == null) {
            this.in = in;
            this.ch = null;
            this.bb = ByteBuffer.allocate(8192);
        }
        this.bb.flip();
    }
    
    StreamDecoder(final ReadableByteChannel ch, final CharsetDecoder decoder, final int n) {
        this.isOpen = true;
        this.haveLeftoverChar = false;
        this.in = null;
        this.ch = ch;
        this.decoder = decoder;
        this.cs = decoder.charset();
        (this.bb = ByteBuffer.allocate((n < 0) ? 8192 : ((n < 32) ? 32 : n))).flip();
    }
    
    private int readBytes() throws IOException {
        this.bb.compact();
        try {
            if (this.ch != null) {
                final int read = this.ch.read(this.bb);
                if (read < 0) {
                    return read;
                }
            }
            else {
                final int limit = this.bb.limit();
                final int position = this.bb.position();
                assert position <= limit;
                final int n = (position <= limit) ? (limit - position) : 0;
                assert n > 0;
                final int read2 = this.in.read(this.bb.array(), this.bb.arrayOffset() + position, n);
                if (read2 < 0) {
                    return read2;
                }
                if (read2 == 0) {
                    throw new IOException("Underlying input stream returned zero bytes");
                }
                assert read2 <= n : "n = " + read2 + ", rem = " + n;
                this.bb.position(position + read2);
            }
        }
        finally {
            this.bb.flip();
        }
        final int remaining = this.bb.remaining();
        assert remaining != 0 : remaining;
        return remaining;
    }
    
    int implRead(final char[] array, final int n, final int n2) throws IOException {
        assert n2 - n > 1;
        CharBuffer charBuffer = CharBuffer.wrap(array, n, n2 - n);
        if (charBuffer.position() != 0) {
            charBuffer = charBuffer.slice();
        }
        boolean b = false;
        while (true) {
            final CoderResult decode = this.decoder.decode(this.bb, charBuffer, b);
            if (decode.isUnderflow()) {
                if (b) {
                    break;
                }
                if (!charBuffer.hasRemaining()) {
                    break;
                }
                if (charBuffer.position() > 0 && !this.inReady()) {
                    break;
                }
                if (this.readBytes() >= 0) {
                    continue;
                }
                b = true;
                if (charBuffer.position() == 0 && !this.bb.hasRemaining()) {
                    break;
                }
                this.decoder.reset();
            }
            else if (decode.isOverflow()) {
                assert charBuffer.position() > 0;
                break;
            }
            else {
                decode.throwException();
            }
        }
        if (b) {
            this.decoder.reset();
        }
        if (charBuffer.position() == 0) {
            if (b) {
                return -1;
            }
            assert false;
        }
        return charBuffer.position();
    }
    
    String encodingName() {
        return (this.cs instanceof HistoricallyNamedCharset) ? ((HistoricallyNamedCharset)this.cs).historicalName() : this.cs.name();
    }
    
    private boolean inReady() {
        try {
            return (this.in != null && this.in.available() > 0) || this.ch instanceof FileChannel;
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    boolean implReady() {
        return this.bb.hasRemaining() || this.inReady();
    }
    
    void implClose() throws IOException {
        if (this.ch != null) {
            this.ch.close();
        }
        else {
            this.in.close();
        }
    }
    
    static {
        StreamDecoder.channelsAvailable = true;
    }
}
