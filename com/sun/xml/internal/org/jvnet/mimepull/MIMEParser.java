package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.util.logging.Level;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.io.InputStream;
import java.util.logging.Logger;

class MIMEParser implements Iterable<MIMEEvent>
{
    private static final Logger LOGGER;
    private static final String HEADER_ENCODING = "ISO8859-1";
    private static final int NO_LWSP = 1000;
    private STATE state;
    private final InputStream in;
    private final byte[] bndbytes;
    private final int bl;
    private final MIMEConfig config;
    private final int[] bcs;
    private final int[] gss;
    private boolean parsed;
    private boolean done;
    private boolean eof;
    private final int capacity;
    private byte[] buf;
    private int len;
    private boolean bol;
    
    MIMEParser(final InputStream in, final String boundary, final MIMEConfig config) {
        this.state = STATE.START_MESSAGE;
        this.bcs = new int[128];
        this.done = false;
        this.in = in;
        this.bndbytes = getBytes("--" + boundary);
        this.bl = this.bndbytes.length;
        this.config = config;
        this.gss = new int[this.bl];
        this.compileBoundaryPattern();
        this.createBuf(this.capacity = config.chunkSize + 2 + this.bl + 4 + 1000);
    }
    
    @Override
    public Iterator<MIMEEvent> iterator() {
        return new MIMEEventIterator();
    }
    
    private InternetHeaders readHeaders() {
        if (!this.eof) {
            this.fillBuf();
        }
        return new InternetHeaders(new LineInputStream());
    }
    
    private ByteBuffer readBody() {
        if (!this.eof) {
            this.fillBuf();
        }
        final int start = this.match(this.buf, 0, this.len);
        if (start == -1) {
            assert this.len >= this.config.chunkSize;
            final int chunkSize = this.eof ? this.len : this.config.chunkSize;
            if (this.eof) {
                this.done = true;
                throw new MIMEParsingException("Reached EOF, but there is no closing MIME boundary.");
            }
            return this.adjustBuf(chunkSize, this.len - chunkSize);
        }
        else {
            int chunkLen = start;
            if (!this.bol || start != 0) {
                if (start <= 0 || (this.buf[start - 1] != 10 && this.buf[start - 1] != 13)) {
                    return this.adjustBuf(start + 1, this.len - start - 1);
                }
                --chunkLen;
                if (this.buf[start - 1] == 10 && start > 1 && this.buf[start - 2] == 13) {
                    --chunkLen;
                }
            }
            if (start + this.bl + 1 < this.len && this.buf[start + this.bl] == 45 && this.buf[start + this.bl + 1] == 45) {
                this.state = STATE.END_PART;
                this.done = true;
                return this.adjustBuf(chunkLen, 0);
            }
            int lwsp = 0;
            for (int i = start + this.bl; i < this.len && (this.buf[i] == 32 || this.buf[i] == 9); ++i) {
                ++lwsp;
            }
            if (start + this.bl + lwsp < this.len && this.buf[start + this.bl + lwsp] == 10) {
                this.state = STATE.END_PART;
                return this.adjustBuf(chunkLen, this.len - start - this.bl - lwsp - 1);
            }
            if (start + this.bl + lwsp + 1 < this.len && this.buf[start + this.bl + lwsp] == 13 && this.buf[start + this.bl + lwsp + 1] == 10) {
                this.state = STATE.END_PART;
                return this.adjustBuf(chunkLen, this.len - start - this.bl - lwsp - 2);
            }
            if (start + this.bl + lwsp + 1 < this.len) {
                return this.adjustBuf(chunkLen + 1, this.len - chunkLen - 1);
            }
            if (this.eof) {
                this.done = true;
                throw new MIMEParsingException("Reached EOF, but there is no closing MIME boundary.");
            }
            return this.adjustBuf(chunkLen, this.len - chunkLen);
        }
    }
    
    private ByteBuffer adjustBuf(final int chunkSize, final int remaining) {
        assert this.buf != null;
        assert chunkSize >= 0;
        assert remaining >= 0;
        final byte[] temp = this.buf;
        this.createBuf(remaining);
        System.arraycopy(temp, this.len - remaining, this.buf, 0, remaining);
        this.len = remaining;
        return ByteBuffer.wrap(temp, 0, chunkSize);
    }
    
    private void createBuf(final int min) {
        this.buf = new byte[(min < this.capacity) ? this.capacity : min];
    }
    
    private void skipPreamble() {
        while (true) {
            if (!this.eof) {
                this.fillBuf();
            }
            final int start = this.match(this.buf, 0, this.len);
            if (start == -1) {
                if (this.eof) {
                    throw new MIMEParsingException("Missing start boundary");
                }
                this.adjustBuf(this.len - this.bl + 1, this.bl - 1);
            }
            else if (start > this.config.chunkSize) {
                this.adjustBuf(start, this.len - start);
            }
            else {
                int lwsp = 0;
                for (int i = start + this.bl; i < this.len && (this.buf[i] == 32 || this.buf[i] == 9); ++i) {
                    ++lwsp;
                }
                if (start + this.bl + lwsp < this.len && (this.buf[start + this.bl + lwsp] == 10 || this.buf[start + this.bl + lwsp] == 13)) {
                    if (this.buf[start + this.bl + lwsp] == 10) {
                        this.adjustBuf(start + this.bl + lwsp + 1, this.len - start - this.bl - lwsp - 1);
                        break;
                    }
                    if (start + this.bl + lwsp + 1 < this.len && this.buf[start + this.bl + lwsp + 1] == 10) {
                        this.adjustBuf(start + this.bl + lwsp + 2, this.len - start - this.bl - lwsp - 2);
                        break;
                    }
                }
                this.adjustBuf(start + 1, this.len - start - 1);
            }
        }
        if (MIMEParser.LOGGER.isLoggable(Level.FINE)) {
            MIMEParser.LOGGER.log(Level.FINE, "Skipped the preamble. buffer len={0}", this.len);
        }
    }
    
    private static byte[] getBytes(final String s) {
        final char[] chars = s.toCharArray();
        final int size = chars.length;
        final byte[] bytes = new byte[size];
        for (int i = 0; i < size; bytes[i] = (byte)chars[i++]) {}
        return bytes;
    }
    
    private void compileBoundaryPattern() {
        for (int i = 0; i < this.bndbytes.length; ++i) {
            this.bcs[this.bndbytes[i] & 0x7F] = i + 1;
        }
        int i = this.bndbytes.length;
    Label_0040:
        while (i > 0) {
            while (true) {
                int j;
                for (j = this.bndbytes.length - 1; j >= i; --j) {
                    if (this.bndbytes[j] != this.bndbytes[j - i]) {
                        --i;
                        continue Label_0040;
                    }
                    this.gss[j - 1] = i;
                }
                while (j > 0) {
                    this.gss[--j] = i;
                }
                continue;
            }
        }
        this.gss[this.bndbytes.length - 1] = 1;
    }
    
    private int match(final byte[] mybuf, int off, final int len) {
        final int last = len - this.bndbytes.length;
    Label_0009:
        while (off <= last) {
            for (int j = this.bndbytes.length - 1; j >= 0; --j) {
                final byte ch = mybuf[off + j];
                if (ch != this.bndbytes[j]) {
                    off += Math.max(j + 1 - this.bcs[ch & 0x7F], this.gss[j]);
                    continue Label_0009;
                }
            }
            return off;
        }
        return -1;
    }
    
    private void fillBuf() {
        if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
            MIMEParser.LOGGER.log(Level.FINER, "Before fillBuf() buffer len={0}", this.len);
        }
        assert !this.eof;
        while (this.len < this.buf.length) {
            int read;
            try {
                read = this.in.read(this.buf, this.len, this.buf.length - this.len);
            }
            catch (final IOException ioe) {
                throw new MIMEParsingException(ioe);
            }
            if (read == -1) {
                this.eof = true;
                try {
                    if (MIMEParser.LOGGER.isLoggable(Level.FINE)) {
                        MIMEParser.LOGGER.fine("Closing the input stream.");
                    }
                    this.in.close();
                    break;
                }
                catch (final IOException ioe) {
                    throw new MIMEParsingException(ioe);
                }
            }
            this.len += read;
        }
        if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
            MIMEParser.LOGGER.log(Level.FINER, "After fillBuf() buffer len={0}", this.len);
        }
    }
    
    private void doubleBuf() {
        final byte[] temp = new byte[2 * this.len];
        System.arraycopy(this.buf, 0, temp, 0, this.len);
        this.buf = temp;
        if (!this.eof) {
            this.fillBuf();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(MIMEParser.class.getName());
    }
    
    private enum STATE
    {
        START_MESSAGE, 
        SKIP_PREAMBLE, 
        START_PART, 
        HEADERS, 
        BODY, 
        END_PART, 
        END_MESSAGE;
    }
    
    class MIMEEventIterator implements Iterator<MIMEEvent>
    {
        @Override
        public boolean hasNext() {
            return !MIMEParser.this.parsed;
        }
        
        @Override
        public MIMEEvent next() {
            switch (MIMEParser.this.state) {
                case START_MESSAGE: {
                    if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
                        MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", STATE.START_MESSAGE);
                    }
                    MIMEParser.this.state = STATE.SKIP_PREAMBLE;
                    return MIMEEvent.START_MESSAGE;
                }
                case SKIP_PREAMBLE: {
                    if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
                        MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", STATE.SKIP_PREAMBLE);
                    }
                    MIMEParser.this.skipPreamble();
                }
                case START_PART: {
                    if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
                        MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", STATE.START_PART);
                    }
                    MIMEParser.this.state = STATE.HEADERS;
                    return MIMEEvent.START_PART;
                }
                case HEADERS: {
                    if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
                        MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", STATE.HEADERS);
                    }
                    final InternetHeaders ih = MIMEParser.this.readHeaders();
                    MIMEParser.this.state = STATE.BODY;
                    MIMEParser.this.bol = true;
                    return new MIMEEvent.Headers(ih);
                }
                case BODY: {
                    if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
                        MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", STATE.BODY);
                    }
                    final ByteBuffer buf = MIMEParser.this.readBody();
                    MIMEParser.this.bol = false;
                    return new MIMEEvent.Content(buf);
                }
                case END_PART: {
                    if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
                        MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", STATE.END_PART);
                    }
                    if (MIMEParser.this.done) {
                        MIMEParser.this.state = STATE.END_MESSAGE;
                    }
                    else {
                        MIMEParser.this.state = STATE.START_PART;
                    }
                    return MIMEEvent.END_PART;
                }
                case END_MESSAGE: {
                    if (MIMEParser.LOGGER.isLoggable(Level.FINER)) {
                        MIMEParser.LOGGER.log(Level.FINER, "MIMEParser state={0}", STATE.END_MESSAGE);
                    }
                    MIMEParser.this.parsed = true;
                    return MIMEEvent.END_MESSAGE;
                }
                default: {
                    throw new MIMEParsingException("Unknown Parser state = " + MIMEParser.this.state);
                }
            }
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    class LineInputStream
    {
        private int offset;
        
        public String readLine() throws IOException {
            int hdrLen = 0;
            int lwsp = 0;
            while (this.offset + hdrLen < MIMEParser.this.len) {
                if (MIMEParser.this.buf[this.offset + hdrLen] == 10) {
                    lwsp = 1;
                    break;
                }
                if (this.offset + hdrLen + 1 == MIMEParser.this.len) {
                    MIMEParser.this.doubleBuf();
                }
                if (this.offset + hdrLen + 1 >= MIMEParser.this.len) {
                    assert MIMEParser.this.eof;
                    return null;
                }
                else {
                    if (MIMEParser.this.buf[this.offset + hdrLen] == 13 && MIMEParser.this.buf[this.offset + hdrLen + 1] == 10) {
                        lwsp = 2;
                        break;
                    }
                    ++hdrLen;
                }
            }
            if (hdrLen == 0) {
                MIMEParser.this.adjustBuf(this.offset + lwsp, MIMEParser.this.len - this.offset - lwsp);
                return null;
            }
            final String hdr = new String(MIMEParser.this.buf, this.offset, hdrLen, "ISO8859-1");
            this.offset += hdrLen + lwsp;
            return hdr;
        }
    }
}
