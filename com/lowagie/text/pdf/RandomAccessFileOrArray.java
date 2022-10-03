package com.lowagie.text.pdf;

import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import com.lowagie.text.error_messages.MessageLocalization;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import com.lowagie.text.Document;
import java.io.RandomAccessFile;
import java.io.DataInput;

public class RandomAccessFileOrArray implements DataInput
{
    MappedRandomAccessFile rf;
    RandomAccessFile trf;
    boolean plainRandomAccess;
    String filename;
    byte[] arrayIn;
    int arrayInPtr;
    byte back;
    boolean isBack;
    private int startOffset;
    
    public RandomAccessFileOrArray(final String filename) throws IOException {
        this(filename, false, Document.plainRandomAccess);
    }
    
    public RandomAccessFileOrArray(final String filename, final boolean forceRead, final boolean plainRandomAccess) throws IOException {
        this.isBack = false;
        this.startOffset = 0;
        this.plainRandomAccess = plainRandomAccess;
        final File file = new File(filename);
        if (!file.canRead()) {
            if (filename.startsWith("file:/") || filename.startsWith("http://") || filename.startsWith("https://") || filename.startsWith("jar:") || filename.startsWith("wsjar:")) {
                final InputStream is = new URL(filename).openStream();
                try {
                    this.arrayIn = InputStreamToArray(is);
                    return;
                }
                finally {
                    try {
                        is.close();
                    }
                    catch (final IOException ex) {}
                }
            }
            InputStream is = null;
            if ("-".equals(filename)) {
                is = System.in;
            }
            else {
                is = BaseFont.getResourceStream(filename);
            }
            if (is == null) {
                throw new IOException(MessageLocalization.getComposedMessage("1.not.found.as.file.or.resource", filename));
            }
            try {
                this.arrayIn = InputStreamToArray(is);
                return;
            }
            finally {
                try {
                    is.close();
                }
                catch (final IOException ex2) {}
            }
        }
        if (forceRead) {
            InputStream s = null;
            try {
                s = new FileInputStream(file);
                this.arrayIn = InputStreamToArray(s);
            }
            finally {
                try {
                    if (s != null) {
                        s.close();
                    }
                }
                catch (final Exception ex3) {}
            }
            return;
        }
        this.filename = filename;
        if (plainRandomAccess) {
            this.trf = new RandomAccessFile(filename, "r");
        }
        else {
            this.rf = new MappedRandomAccessFile(filename, "r");
        }
    }
    
    public RandomAccessFileOrArray(final URL url) throws IOException {
        this.isBack = false;
        this.startOffset = 0;
        final InputStream is = url.openStream();
        try {
            this.arrayIn = InputStreamToArray(is);
        }
        finally {
            try {
                is.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public RandomAccessFileOrArray(final InputStream is) throws IOException {
        this.isBack = false;
        this.startOffset = 0;
        this.arrayIn = InputStreamToArray(is);
    }
    
    public static byte[] InputStreamToArray(final InputStream is) throws IOException {
        final byte[] b = new byte[8192];
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            final int read = is.read(b);
            if (read < 1) {
                break;
            }
            out.write(b, 0, read);
        }
        out.close();
        return out.toByteArray();
    }
    
    public RandomAccessFileOrArray(final byte[] arrayIn) {
        this.isBack = false;
        this.startOffset = 0;
        this.arrayIn = arrayIn;
    }
    
    public RandomAccessFileOrArray(final RandomAccessFileOrArray file) {
        this.isBack = false;
        this.startOffset = 0;
        this.filename = file.filename;
        this.arrayIn = file.arrayIn;
        this.startOffset = file.startOffset;
        this.plainRandomAccess = file.plainRandomAccess;
    }
    
    public void pushBack(final byte b) {
        this.back = b;
        this.isBack = true;
    }
    
    public int read() throws IOException {
        if (this.isBack) {
            this.isBack = false;
            return this.back & 0xFF;
        }
        if (this.arrayIn == null) {
            return this.plainRandomAccess ? this.trf.read() : this.rf.read();
        }
        if (this.arrayInPtr >= this.arrayIn.length) {
            return -1;
        }
        return this.arrayIn[this.arrayInPtr++] & 0xFF;
    }
    
    public int read(final byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        int n = 0;
        if (this.isBack) {
            this.isBack = false;
            if (len == 1) {
                b[off] = this.back;
                return 1;
            }
            n = 1;
            b[off++] = this.back;
            --len;
        }
        if (this.arrayIn == null) {
            return (this.plainRandomAccess ? this.trf.read(b, off, len) : this.rf.read(b, off, len)) + n;
        }
        if (this.arrayInPtr >= this.arrayIn.length) {
            return -1;
        }
        if (this.arrayInPtr + len > this.arrayIn.length) {
            len = this.arrayIn.length - this.arrayInPtr;
        }
        System.arraycopy(this.arrayIn, this.arrayInPtr, b, off, len);
        this.arrayInPtr += len;
        return len + n;
    }
    
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public void readFully(final byte[] b) throws IOException {
        this.readFully(b, 0, b.length);
    }
    
    @Override
    public void readFully(final byte[] b, final int off, final int len) throws IOException {
        int n = 0;
        do {
            final int count = this.read(b, off + n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        } while (n < len);
    }
    
    public long skip(final long n) throws IOException {
        return this.skipBytes((int)n);
    }
    
    @Override
    public int skipBytes(int n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        int adj = 0;
        if (this.isBack) {
            this.isBack = false;
            if (n == 1) {
                return 1;
            }
            --n;
            adj = 1;
        }
        final int pos = this.getFilePointer();
        final int len = this.length();
        int newpos = pos + n;
        if (newpos > len) {
            newpos = len;
        }
        this.seek(newpos);
        return newpos - pos + adj;
    }
    
    public void reOpen() throws IOException {
        if (this.filename != null && this.rf == null && this.trf == null) {
            if (this.plainRandomAccess) {
                this.trf = new RandomAccessFile(this.filename, "r");
            }
            else {
                this.rf = new MappedRandomAccessFile(this.filename, "r");
            }
        }
        this.seek(0);
    }
    
    protected void insureOpen() throws IOException {
        if (this.filename != null && this.rf == null && this.trf == null) {
            this.reOpen();
        }
    }
    
    public boolean isOpen() {
        return this.filename == null || this.rf != null || this.trf != null;
    }
    
    public void close() throws IOException {
        this.isBack = false;
        if (this.rf != null) {
            this.rf.close();
            this.rf = null;
            this.plainRandomAccess = true;
        }
        else if (this.trf != null) {
            this.trf.close();
            this.trf = null;
        }
    }
    
    public int length() throws IOException {
        if (this.arrayIn == null) {
            this.insureOpen();
            return (int)(this.plainRandomAccess ? this.trf.length() : this.rf.length()) - this.startOffset;
        }
        return this.arrayIn.length - this.startOffset;
    }
    
    public void seek(int pos) throws IOException {
        pos += this.startOffset;
        this.isBack = false;
        if (this.arrayIn == null) {
            this.insureOpen();
            if (this.plainRandomAccess) {
                this.trf.seek(pos);
            }
            else {
                this.rf.seek(pos);
            }
        }
        else {
            this.arrayInPtr = pos;
        }
    }
    
    public void seek(final long pos) throws IOException {
        this.seek((int)pos);
    }
    
    public int getFilePointer() throws IOException {
        this.insureOpen();
        final int n = this.isBack ? 1 : 0;
        if (this.arrayIn == null) {
            return (int)(this.plainRandomAccess ? this.trf.getFilePointer() : this.rf.getFilePointer()) - n - this.startOffset;
        }
        return this.arrayInPtr - n - this.startOffset;
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        final int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch != 0;
    }
    
    @Override
    public byte readByte() throws IOException {
        final int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte)ch;
    }
    
    @Override
    public int readUnsignedByte() throws IOException {
        final int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch;
    }
    
    @Override
    public short readShort() throws IOException {
        final int ch1 = this.read();
        final int ch2 = this.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (short)((ch1 << 8) + ch2);
    }
    
    public final short readShortLE() throws IOException {
        final int ch1 = this.read();
        final int ch2 = this.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (short)((ch2 << 8) + (ch1 << 0));
    }
    
    @Override
    public int readUnsignedShort() throws IOException {
        final int ch1 = this.read();
        final int ch2 = this.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (ch1 << 8) + ch2;
    }
    
    public final int readUnsignedShortLE() throws IOException {
        final int ch1 = this.read();
        final int ch2 = this.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (ch2 << 8) + (ch1 << 0);
    }
    
    @Override
    public char readChar() throws IOException {
        final int ch1 = this.read();
        final int ch2 = this.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (char)((ch1 << 8) + ch2);
    }
    
    public final char readCharLE() throws IOException {
        final int ch1 = this.read();
        final int ch2 = this.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (char)((ch2 << 8) + (ch1 << 0));
    }
    
    @Override
    public int readInt() throws IOException {
        final int ch1 = this.read();
        final int ch2 = this.read();
        final int ch3 = this.read();
        final int ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
    }
    
    public final int readIntLE() throws IOException {
        final int ch1 = this.read();
        final int ch2 = this.read();
        final int ch3 = this.read();
        final int ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }
    
    public final long readUnsignedInt() throws IOException {
        final long ch1 = this.read();
        final long ch2 = this.read();
        final long ch3 = this.read();
        final long ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0L) {
            throw new EOFException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
    }
    
    public final long readUnsignedIntLE() throws IOException {
        final long ch1 = this.read();
        final long ch2 = this.read();
        final long ch3 = this.read();
        final long ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0L) {
            throw new EOFException();
        }
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }
    
    @Override
    public long readLong() throws IOException {
        return ((long)this.readInt() << 32) + ((long)this.readInt() & 0xFFFFFFFFL);
    }
    
    public final long readLongLE() throws IOException {
        final int i1 = this.readIntLE();
        final int i2 = this.readIntLE();
        return ((long)i2 << 32) + ((long)i1 & 0xFFFFFFFFL);
    }
    
    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }
    
    public final float readFloatLE() throws IOException {
        return Float.intBitsToFloat(this.readIntLE());
    }
    
    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }
    
    public final double readDoubleLE() throws IOException {
        return Double.longBitsToDouble(this.readLongLE());
    }
    
    @Override
    public String readLine() throws IOException {
        final StringBuffer input = new StringBuffer();
        int c = -1;
        boolean eol = false;
        while (!eol) {
            switch (c = this.read()) {
                case -1:
                case 10: {
                    eol = true;
                    continue;
                }
                case 13: {
                    eol = true;
                    final int cur = this.getFilePointer();
                    if (this.read() != 10) {
                        this.seek(cur);
                        continue;
                    }
                    continue;
                }
                default: {
                    input.append((char)c);
                    continue;
                }
            }
        }
        if (c == -1 && input.length() == 0) {
            return null;
        }
        return input.toString();
    }
    
    @Override
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }
    
    public int getStartOffset() {
        return this.startOffset;
    }
    
    public void setStartOffset(final int startOffset) {
        this.startOffset = startOffset;
    }
    
    public ByteBuffer getNioByteBuffer() throws IOException {
        if (this.filename != null) {
            FileChannel channel;
            if (this.plainRandomAccess) {
                channel = this.trf.getChannel();
            }
            else {
                channel = this.rf.getChannel();
            }
            return channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size());
        }
        return ByteBuffer.wrap(this.arrayIn);
    }
}
