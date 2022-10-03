package org.apache.poi.poifs.filesystem;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.property.DocumentProperty;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.poi.util.LittleEndianInput;
import java.io.InputStream;

public final class DocumentInputStream extends InputStream implements LittleEndianInput
{
    private static int SKIP_BUFFER_SIZE;
    private static byte[] SKIP_BYTE_BUFFER;
    private static final int EOF = -1;
    private int _current_offset;
    private int _current_block_count;
    private int _marked_offset;
    private int _marked_offset_count;
    private final int _document_size;
    private boolean _closed;
    private final POIFSDocument _document;
    private Iterator<ByteBuffer> _data;
    private ByteBuffer _buffer;
    
    public DocumentInputStream(final DocumentEntry document) throws IOException {
        if (!(document instanceof DocumentNode)) {
            throw new IOException("Cannot open internal document storage, " + document + " not a Document Node");
        }
        this._current_offset = 0;
        this._current_block_count = 0;
        this._marked_offset = 0;
        this._marked_offset_count = 0;
        this._document_size = document.getSize();
        this._closed = false;
        final DocumentNode doc = (DocumentNode)document;
        final DocumentProperty property = (DocumentProperty)doc.getProperty();
        this._document = new POIFSDocument(property, ((DirectoryNode)doc.getParent()).getFileSystem());
        this._data = this._document.getBlockIterator();
    }
    
    public DocumentInputStream(final POIFSDocument document) {
        this._current_offset = 0;
        this._current_block_count = 0;
        this._marked_offset = 0;
        this._marked_offset_count = 0;
        this._document_size = document.getSize();
        this._closed = false;
        this._document = document;
        this._data = this._document.getBlockIterator();
    }
    
    @Override
    public int available() {
        return this.remainingBytes();
    }
    
    private int remainingBytes() {
        if (this._closed) {
            throw new IllegalStateException("cannot perform requested operation on a closed stream");
        }
        return this._document_size - this._current_offset;
    }
    
    @Override
    public void close() {
        this._closed = true;
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public synchronized void mark(final int ignoredReadlimit) {
        this._marked_offset = this._current_offset;
        this._marked_offset_count = Math.max(0, this._current_block_count - 1);
    }
    
    @Override
    public int read() throws IOException {
        this.dieIfClosed();
        if (this.atEOD()) {
            return -1;
        }
        final byte[] b = { 0 };
        final int result = this.read(b, 0, 1);
        return (result == -1) ? -1 : (b[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        this.dieIfClosed();
        if (b == null) {
            throw new IllegalArgumentException("buffer must not be null");
        }
        if (off < 0 || len < 0 || b.length < off + len) {
            throw new IndexOutOfBoundsException("can't read past buffer boundaries");
        }
        if (len == 0) {
            return 0;
        }
        if (this.atEOD()) {
            return -1;
        }
        final int limit = Math.min(this.remainingBytes(), len);
        this.readFully(b, off, limit);
        return limit;
    }
    
    @Override
    public synchronized void reset() {
        if (this._marked_offset == 0 && this._marked_offset_count == 0) {
            this._current_block_count = this._marked_offset_count;
            this._current_offset = this._marked_offset;
            this._data = this._document.getBlockIterator();
            this._buffer = null;
            return;
        }
        this._data = this._document.getBlockIterator();
        this._current_offset = 0;
        for (int i = 0; i < this._marked_offset_count; ++i) {
            this._buffer = this._data.next();
            this._current_offset += this._buffer.remaining();
        }
        this._current_block_count = this._marked_offset_count;
        if (this._current_offset != this._marked_offset) {
            this._buffer = this._data.next();
            ++this._current_block_count;
            final int skipBy = this._marked_offset - this._current_offset;
            this._buffer.position(this._buffer.position() + skipBy);
        }
        this._current_offset = this._marked_offset;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        this.dieIfClosed();
        if (n < 0L) {
            return 0L;
        }
        long new_offset = this._current_offset + n;
        if (new_offset < this._current_offset) {
            new_offset = this._document_size;
        }
        else if (new_offset > this._document_size) {
            new_offset = this._document_size;
        }
        final long rval = new_offset - this._current_offset;
        final byte[] skip = IOUtils.safelyAllocate(rval, Integer.MAX_VALUE);
        this.readFully(skip);
        return rval;
    }
    
    private void dieIfClosed() throws IOException {
        if (this._closed) {
            throw new IOException("cannot perform requested operation on a closed stream");
        }
    }
    
    private boolean atEOD() {
        return this._current_offset == this._document_size;
    }
    
    private void checkAvaliable(final int requestedSize) {
        if (this._closed) {
            throw new IllegalStateException("cannot perform requested operation on a closed stream");
        }
        if (requestedSize > this._document_size - this._current_offset) {
            throw new RuntimeException("Buffer underrun - requested " + requestedSize + " bytes but " + (this._document_size - this._current_offset) + " was available");
        }
    }
    
    @Override
    public void readFully(final byte[] buf) {
        this.readFully(buf, 0, buf.length);
    }
    
    @Override
    public void readFully(final byte[] buf, final int off, final int len) {
        if (len < 0) {
            throw new RuntimeException("Can't read negative number of bytes");
        }
        this.checkAvaliable(len);
        int limit;
        for (int read = 0; read < len; read += limit) {
            if (this._buffer == null || this._buffer.remaining() == 0) {
                ++this._current_block_count;
                this._buffer = this._data.next();
            }
            limit = Math.min(len - read, this._buffer.remaining());
            this._buffer.get(buf, off + read, limit);
            this._current_offset += limit;
        }
    }
    
    @Override
    public void readPlain(final byte[] buf, final int off, final int len) {
        this.readFully(buf, off, len);
    }
    
    @Override
    public byte readByte() {
        return (byte)this.readUByte();
    }
    
    @Override
    public double readDouble() {
        return Double.longBitsToDouble(this.readLong());
    }
    
    @Override
    public long readLong() {
        this.checkAvaliable(8);
        final byte[] data = new byte[8];
        this.readFully(data, 0, 8);
        return LittleEndian.getLong(data, 0);
    }
    
    @Override
    public short readShort() {
        this.checkAvaliable(2);
        final byte[] data = new byte[2];
        this.readFully(data, 0, 2);
        return LittleEndian.getShort(data);
    }
    
    @Override
    public int readInt() {
        this.checkAvaliable(4);
        final byte[] data = new byte[4];
        this.readFully(data, 0, 4);
        return LittleEndian.getInt(data);
    }
    
    public long readUInt() {
        final int i = this.readInt();
        return (long)i & 0xFFFFFFFFL;
    }
    
    @Override
    public int readUShort() {
        this.checkAvaliable(2);
        final byte[] data = new byte[2];
        this.readFully(data, 0, 2);
        return LittleEndian.getUShort(data);
    }
    
    @Override
    public int readUByte() {
        this.checkAvaliable(1);
        final byte[] data = { 0 };
        this.readFully(data, 0, 1);
        if (data[0] >= 0) {
            return data[0];
        }
        return data[0] + 256;
    }
    
    static {
        DocumentInputStream.SKIP_BUFFER_SIZE = 2048;
        DocumentInputStream.SKIP_BYTE_BUFFER = new byte[DocumentInputStream.SKIP_BUFFER_SIZE];
    }
}
