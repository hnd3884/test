package org.tukaani.xz;

import org.tukaani.xz.common.EncoderUtil;
import java.io.IOException;
import org.tukaani.xz.index.IndexEncoder;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.common.StreamFlags;
import java.io.OutputStream;

public class XZOutputStream extends FinishableOutputStream
{
    private final ArrayCache arrayCache;
    private OutputStream out;
    private final StreamFlags streamFlags;
    private final Check check;
    private final IndexEncoder index;
    private BlockOutputStream blockEncoder;
    private FilterEncoder[] filters;
    private boolean filtersSupportFlushing;
    private IOException exception;
    private boolean finished;
    private final byte[] tempBuf;
    
    public XZOutputStream(final OutputStream outputStream, final FilterOptions filterOptions) throws IOException {
        this(outputStream, filterOptions, 4);
    }
    
    public XZOutputStream(final OutputStream outputStream, final FilterOptions filterOptions, final ArrayCache arrayCache) throws IOException {
        this(outputStream, filterOptions, 4, arrayCache);
    }
    
    public XZOutputStream(final OutputStream outputStream, final FilterOptions filterOptions, final int n) throws IOException {
        this(outputStream, new FilterOptions[] { filterOptions }, n);
    }
    
    public XZOutputStream(final OutputStream outputStream, final FilterOptions filterOptions, final int n, final ArrayCache arrayCache) throws IOException {
        this(outputStream, new FilterOptions[] { filterOptions }, n, arrayCache);
    }
    
    public XZOutputStream(final OutputStream outputStream, final FilterOptions[] array) throws IOException {
        this(outputStream, array, 4);
    }
    
    public XZOutputStream(final OutputStream outputStream, final FilterOptions[] array, final ArrayCache arrayCache) throws IOException {
        this(outputStream, array, 4, arrayCache);
    }
    
    public XZOutputStream(final OutputStream outputStream, final FilterOptions[] array, final int n) throws IOException {
        this(outputStream, array, n, ArrayCache.getDefaultCache());
    }
    
    public XZOutputStream(final OutputStream out, final FilterOptions[] array, final int checkType, final ArrayCache arrayCache) throws IOException {
        this.streamFlags = new StreamFlags();
        this.index = new IndexEncoder();
        this.blockEncoder = null;
        this.exception = null;
        this.finished = false;
        this.tempBuf = new byte[1];
        this.arrayCache = arrayCache;
        this.out = out;
        this.updateFilters(array);
        this.streamFlags.checkType = checkType;
        this.check = Check.getInstance(checkType);
        this.encodeStreamHeader();
    }
    
    public void updateFilters(final FilterOptions filterOptions) throws XZIOException {
        this.updateFilters(new FilterOptions[] { filterOptions });
    }
    
    public void updateFilters(final FilterOptions[] array) throws XZIOException {
        if (this.blockEncoder != null) {
            throw new UnsupportedOptionsException("Changing filter options in the middle of a XZ Block not implemented");
        }
        if (array.length < 1 || array.length > 4) {
            throw new UnsupportedOptionsException("XZ filter chain must be 1-4 filters");
        }
        this.filtersSupportFlushing = true;
        final FilterEncoder[] filters = new FilterEncoder[array.length];
        for (int i = 0; i < array.length; ++i) {
            filters[i] = array[i].getFilterEncoder();
            this.filtersSupportFlushing &= filters[i].supportsFlushing();
        }
        RawCoder.validate(filters);
        this.filters = filters;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.tempBuf[0] = (byte)n;
        this.write(this.tempBuf, 0, 1);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        if (n < 0 || n2 < 0 || n + n2 < 0 || n + n2 > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        try {
            if (this.blockEncoder == null) {
                this.blockEncoder = new BlockOutputStream(this.out, this.filters, this.check, this.arrayCache);
            }
            this.blockEncoder.write(array, n, n2);
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
    }
    
    public void endBlock() throws IOException {
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        if (this.blockEncoder != null) {
            try {
                this.blockEncoder.finish();
                this.index.add(this.blockEncoder.getUnpaddedSize(), this.blockEncoder.getUncompressedSize());
                this.blockEncoder = null;
            }
            catch (final IOException exception) {
                throw this.exception = exception;
            }
        }
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
            if (this.blockEncoder != null) {
                if (this.filtersSupportFlushing) {
                    this.blockEncoder.flush();
                }
                else {
                    this.endBlock();
                    this.out.flush();
                }
            }
            else {
                this.out.flush();
            }
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
    }
    
    @Override
    public void finish() throws IOException {
        if (!this.finished) {
            this.endBlock();
            try {
                this.index.encode(this.out);
                this.encodeStreamFooter();
            }
            catch (final IOException exception) {
                throw this.exception = exception;
            }
            this.finished = true;
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
    
    private void encodeStreamFlags(final byte[] array, final int n) {
        array[n] = 0;
        array[n + 1] = (byte)this.streamFlags.checkType;
    }
    
    private void encodeStreamHeader() throws IOException {
        this.out.write(XZ.HEADER_MAGIC);
        final byte[] array = new byte[2];
        this.encodeStreamFlags(array, 0);
        this.out.write(array);
        EncoderUtil.writeCRC32(this.out, array);
    }
    
    private void encodeStreamFooter() throws IOException {
        final byte[] array = new byte[6];
        final long n = this.index.getIndexSize() / 4L - 1L;
        for (int i = 0; i < 4; ++i) {
            array[i] = (byte)(n >>> i * 8);
        }
        this.encodeStreamFlags(array, 4);
        EncoderUtil.writeCRC32(this.out, array);
        this.out.write(array);
        this.out.write(XZ.FOOTER_MAGIC);
    }
}
