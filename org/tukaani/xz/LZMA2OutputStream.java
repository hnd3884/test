package org.tukaani.xz;

import java.io.OutputStream;
import org.tukaani.xz.rangecoder.RangeEncoder;
import java.io.IOException;
import org.tukaani.xz.lzma.LZMAEncoder;
import org.tukaani.xz.rangecoder.RangeEncoderToBuffer;
import org.tukaani.xz.lz.LZEncoder;

class LZMA2OutputStream extends FinishableOutputStream
{
    static final int COMPRESSED_SIZE_MAX = 65536;
    private final ArrayCache arrayCache;
    private FinishableOutputStream out;
    private LZEncoder lz;
    private RangeEncoderToBuffer rc;
    private LZMAEncoder lzma;
    private final int props;
    private boolean dictResetNeeded;
    private boolean stateResetNeeded;
    private boolean propsNeeded;
    private int pendingSize;
    private boolean finished;
    private IOException exception;
    private final byte[] chunkHeader;
    private final byte[] tempBuf;
    
    private static int getExtraSizeBefore(final int n) {
        return (65536 > n) ? (65536 - n) : 0;
    }
    
    static int getMemoryUsage(final LZMA2Options lzma2Options) {
        final int dictSize = lzma2Options.getDictSize();
        return 70 + LZMAEncoder.getMemoryUsage(lzma2Options.getMode(), dictSize, getExtraSizeBefore(dictSize), lzma2Options.getMatchFinder());
    }
    
    LZMA2OutputStream(final FinishableOutputStream out, final LZMA2Options lzma2Options, final ArrayCache arrayCache) {
        this.dictResetNeeded = true;
        this.stateResetNeeded = true;
        this.propsNeeded = true;
        this.pendingSize = 0;
        this.finished = false;
        this.exception = null;
        this.chunkHeader = new byte[6];
        this.tempBuf = new byte[1];
        if (out == null) {
            throw new NullPointerException();
        }
        this.arrayCache = arrayCache;
        this.out = out;
        this.rc = new RangeEncoderToBuffer(65536, arrayCache);
        final int dictSize = lzma2Options.getDictSize();
        this.lzma = LZMAEncoder.getInstance(this.rc, lzma2Options.getLc(), lzma2Options.getLp(), lzma2Options.getPb(), lzma2Options.getMode(), dictSize, getExtraSizeBefore(dictSize), lzma2Options.getNiceLen(), lzma2Options.getMatchFinder(), lzma2Options.getDepthLimit(), this.arrayCache);
        this.lz = this.lzma.getLZEncoder();
        final byte[] presetDict = lzma2Options.getPresetDict();
        if (presetDict != null && presetDict.length > 0) {
            this.lz.setPresetDict(dictSize, presetDict);
            this.dictResetNeeded = false;
        }
        this.props = (lzma2Options.getPb() * 5 + lzma2Options.getLp()) * 9 + lzma2Options.getLc();
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
        try {
            while (i > 0) {
                final int fillWindow = this.lz.fillWindow(array, n, i);
                n += fillWindow;
                i -= fillWindow;
                this.pendingSize += fillWindow;
                if (this.lzma.encodeForLZMA2()) {
                    this.writeChunk();
                }
            }
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
    }
    
    private void writeChunk() throws IOException {
        final int finish = this.rc.finish();
        int n = this.lzma.getUncompressedSize();
        assert finish > 0 : finish;
        assert n > 0 : n;
        if (finish + 2 < n) {
            this.writeLZMA(n, finish);
        }
        else {
            this.lzma.reset();
            n = this.lzma.getUncompressedSize();
            assert n > 0 : n;
            this.writeUncompressed(n);
        }
        this.pendingSize -= n;
        this.lzma.resetUncompressedSize();
        this.rc.reset();
    }
    
    private void writeLZMA(final int n, final int n2) throws IOException {
        int n3;
        if (this.propsNeeded) {
            if (this.dictResetNeeded) {
                n3 = 224;
            }
            else {
                n3 = 192;
            }
        }
        else if (this.stateResetNeeded) {
            n3 = 160;
        }
        else {
            n3 = 128;
        }
        this.chunkHeader[0] = (byte)(n3 | n - 1 >>> 16);
        this.chunkHeader[1] = (byte)(n - 1 >>> 8);
        this.chunkHeader[2] = (byte)(n - 1);
        this.chunkHeader[3] = (byte)(n2 - 1 >>> 8);
        this.chunkHeader[4] = (byte)(n2 - 1);
        if (this.propsNeeded) {
            this.chunkHeader[5] = (byte)this.props;
            this.out.write(this.chunkHeader, 0, 6);
        }
        else {
            this.out.write(this.chunkHeader, 0, 5);
        }
        this.rc.write(this.out);
        this.propsNeeded = false;
        this.stateResetNeeded = false;
        this.dictResetNeeded = false;
    }
    
    private void writeUncompressed(int i) throws IOException {
        while (i > 0) {
            final int min = Math.min(i, 65536);
            this.chunkHeader[0] = (byte)(this.dictResetNeeded ? 1 : 2);
            this.chunkHeader[1] = (byte)(min - 1 >>> 8);
            this.chunkHeader[2] = (byte)(min - 1);
            this.out.write(this.chunkHeader, 0, 3);
            this.lz.copyUncompressed(this.out, i, min);
            i -= min;
            this.dictResetNeeded = false;
        }
        this.stateResetNeeded = true;
    }
    
    private void writeEndMarker() throws IOException {
        assert !this.finished;
        if (this.exception != null) {
            throw this.exception;
        }
        this.lz.setFinishing();
        try {
            while (this.pendingSize > 0) {
                this.lzma.encodeForLZMA2();
                this.writeChunk();
            }
            this.out.write(0);
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
        this.finished = true;
        this.lzma.putArraysToCache(this.arrayCache);
        this.lzma = null;
        this.lz = null;
        this.rc.putArraysToCache(this.arrayCache);
        this.rc = null;
    }
    
    @Override
    public void flush() throws IOException {
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        try {
            this.lz.setFlushing();
            while (this.pendingSize > 0) {
                this.lzma.encodeForLZMA2();
                this.writeChunk();
            }
            this.out.flush();
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
    }
    
    @Override
    public void finish() throws IOException {
        if (!this.finished) {
            this.writeEndMarker();
            try {
                this.out.finish();
            }
            catch (final IOException exception) {
                throw this.exception = exception;
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.out != null) {
            if (!this.finished) {
                try {
                    this.writeEndMarker();
                }
                catch (final IOException ex) {}
            }
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
