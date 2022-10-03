package org.tukaani.xz;

import org.tukaani.xz.rangecoder.RangeEncoder;
import java.io.IOException;
import org.tukaani.xz.lzma.LZMAEncoder;
import org.tukaani.xz.rangecoder.RangeEncoderToStream;
import org.tukaani.xz.lz.LZEncoder;
import java.io.OutputStream;

public class LZMAOutputStream extends FinishableOutputStream
{
    private OutputStream out;
    private final ArrayCache arrayCache;
    private LZEncoder lz;
    private final RangeEncoderToStream rc;
    private LZMAEncoder lzma;
    private final int props;
    private final boolean useEndMarker;
    private final long expectedUncompressedSize;
    private long currentUncompressedSize;
    private boolean finished;
    private IOException exception;
    private final byte[] tempBuf;
    
    private LZMAOutputStream(final OutputStream out, final LZMA2Options lzma2Options, final boolean b, final boolean useEndMarker, final long expectedUncompressedSize, final ArrayCache arrayCache) throws IOException {
        this.currentUncompressedSize = 0L;
        this.finished = false;
        this.exception = null;
        this.tempBuf = new byte[1];
        if (out == null) {
            throw new NullPointerException();
        }
        if (expectedUncompressedSize < -1L) {
            throw new IllegalArgumentException("Invalid expected input size (less than -1)");
        }
        this.useEndMarker = useEndMarker;
        this.expectedUncompressedSize = expectedUncompressedSize;
        this.arrayCache = arrayCache;
        this.out = out;
        this.rc = new RangeEncoderToStream(out);
        int dictSize = lzma2Options.getDictSize();
        this.lzma = LZMAEncoder.getInstance(this.rc, lzma2Options.getLc(), lzma2Options.getLp(), lzma2Options.getPb(), lzma2Options.getMode(), dictSize, 0, lzma2Options.getNiceLen(), lzma2Options.getMatchFinder(), lzma2Options.getDepthLimit(), arrayCache);
        this.lz = this.lzma.getLZEncoder();
        final byte[] presetDict = lzma2Options.getPresetDict();
        if (presetDict != null && presetDict.length > 0) {
            if (b) {
                throw new UnsupportedOptionsException("Preset dictionary cannot be used in .lzma files (try a raw LZMA stream instead)");
            }
            this.lz.setPresetDict(dictSize, presetDict);
        }
        this.props = (lzma2Options.getPb() * 5 + lzma2Options.getLp()) * 9 + lzma2Options.getLc();
        if (b) {
            out.write(this.props);
            for (int i = 0; i < 4; ++i) {
                out.write(dictSize & 0xFF);
                dictSize >>>= 8;
            }
            for (int j = 0; j < 8; ++j) {
                out.write((int)(expectedUncompressedSize >>> 8 * j) & 0xFF);
            }
        }
    }
    
    public LZMAOutputStream(final OutputStream outputStream, final LZMA2Options lzma2Options, final long n) throws IOException {
        this(outputStream, lzma2Options, n, ArrayCache.getDefaultCache());
    }
    
    public LZMAOutputStream(final OutputStream outputStream, final LZMA2Options lzma2Options, final long n, final ArrayCache arrayCache) throws IOException {
        this(outputStream, lzma2Options, true, n == -1L, n, arrayCache);
    }
    
    public LZMAOutputStream(final OutputStream outputStream, final LZMA2Options lzma2Options, final boolean b) throws IOException {
        this(outputStream, lzma2Options, b, ArrayCache.getDefaultCache());
    }
    
    public LZMAOutputStream(final OutputStream outputStream, final LZMA2Options lzma2Options, final boolean b, final ArrayCache arrayCache) throws IOException {
        this(outputStream, lzma2Options, false, b, -1L, arrayCache);
    }
    
    public int getProps() {
        return this.props;
    }
    
    public long getUncompressedSize() {
        return this.currentUncompressedSize;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.tempBuf[0] = (byte)n;
        this.write(this.tempBuf, 0, 1);
    }
    
    @Override
    public void write(final byte[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i < 0 || n + i > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        if (this.expectedUncompressedSize != -1L && this.expectedUncompressedSize - this.currentUncompressedSize < i) {
            throw new XZIOException("Expected uncompressed input size (" + this.expectedUncompressedSize + " bytes) was exceeded");
        }
        this.currentUncompressedSize += i;
        try {
            while (i > 0) {
                final int fillWindow = this.lz.fillWindow(array, n, i);
                n += fillWindow;
                i -= fillWindow;
                this.lzma.encodeForLZMA1();
            }
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
    }
    
    @Override
    public void flush() throws IOException {
        throw new XZIOException("LZMAOutputStream does not support flushing");
    }
    
    @Override
    public void finish() throws IOException {
        if (!this.finished) {
            if (this.exception != null) {
                throw this.exception;
            }
            try {
                if (this.expectedUncompressedSize != -1L && this.expectedUncompressedSize != this.currentUncompressedSize) {
                    throw new XZIOException("Expected uncompressed size (" + this.expectedUncompressedSize + ") doesn't equal the number of bytes written to the stream (" + this.currentUncompressedSize + ")");
                }
                this.lz.setFinishing();
                this.lzma.encodeForLZMA1();
                if (this.useEndMarker) {
                    this.lzma.encodeLZMA1EndMarker();
                }
                this.rc.finish();
            }
            catch (final IOException exception) {
                throw this.exception = exception;
            }
            this.finished = true;
            this.lzma.putArraysToCache(this.arrayCache);
            this.lzma = null;
            this.lz = null;
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.out != null) {
            try {
                this.finish();
            }
            catch (final IOException ex) {}
            try {
                this.out.close();
            }
            catch (final IOException exception) {
                if (this.exception == null) {
                    this.exception = exception;
                }
            }
            this.out = null;
        }
        if (this.exception != null) {
            throw this.exception;
        }
    }
}
