package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.common.EncoderUtil;
import java.io.ByteArrayOutputStream;
import org.tukaani.xz.check.Check;
import java.io.OutputStream;

class BlockOutputStream extends FinishableOutputStream
{
    private final OutputStream out;
    private final CountingOutputStream outCounted;
    private FinishableOutputStream filterChain;
    private final Check check;
    private final int headerSize;
    private final long compressedSizeLimit;
    private long uncompressedSize;
    private final byte[] tempBuf;
    
    public BlockOutputStream(final OutputStream out, final FilterEncoder[] array, final Check check, final ArrayCache arrayCache) throws IOException {
        this.uncompressedSize = 0L;
        this.tempBuf = new byte[1];
        this.out = out;
        this.check = check;
        this.outCounted = new CountingOutputStream(out);
        this.filterChain = this.outCounted;
        for (int i = array.length - 1; i >= 0; --i) {
            this.filterChain = array[i].getOutputStream(this.filterChain, arrayCache);
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(0);
        byteArrayOutputStream.write(array.length - 1);
        for (int j = 0; j < array.length; ++j) {
            EncoderUtil.encodeVLI(byteArrayOutputStream, array[j].getFilterID());
            final byte[] filterProps = array[j].getFilterProps();
            EncoderUtil.encodeVLI(byteArrayOutputStream, filterProps.length);
            byteArrayOutputStream.write(filterProps);
        }
        while ((byteArrayOutputStream.size() & 0x3) != 0x0) {
            byteArrayOutputStream.write(0);
        }
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        this.headerSize = byteArray.length + 4;
        if (this.headerSize > 1024) {
            throw new UnsupportedOptionsException();
        }
        byteArray[0] = (byte)(byteArray.length / 4);
        out.write(byteArray);
        EncoderUtil.writeCRC32(out, byteArray);
        this.compressedSizeLimit = 9223372036854775804L - this.headerSize - check.getSize();
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.tempBuf[0] = (byte)n;
        this.write(this.tempBuf, 0, 1);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.filterChain.write(array, n, n2);
        this.check.update(array, n, n2);
        this.uncompressedSize += n2;
        this.validate();
    }
    
    @Override
    public void flush() throws IOException {
        this.filterChain.flush();
        this.validate();
    }
    
    @Override
    public void finish() throws IOException {
        this.filterChain.finish();
        this.validate();
        for (long size = this.outCounted.getSize(); (size & 0x3L) != 0x0L; ++size) {
            this.out.write(0);
        }
        this.out.write(this.check.finish());
    }
    
    private void validate() throws IOException {
        final long size = this.outCounted.getSize();
        if (size < 0L || size > this.compressedSizeLimit || this.uncompressedSize < 0L) {
            throw new XZIOException("XZ Stream has grown too big");
        }
    }
    
    public long getUnpaddedSize() {
        return this.headerSize + this.outCounted.getSize() + this.check.getSize();
    }
    
    public long getUncompressedSize() {
        return this.uncompressedSize;
    }
}
