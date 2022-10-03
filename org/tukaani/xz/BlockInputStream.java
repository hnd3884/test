package org.tukaani.xz;

import java.util.Arrays;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import org.tukaani.xz.common.DecoderUtil;
import org.tukaani.xz.check.Check;
import java.io.DataInputStream;
import java.io.InputStream;

class BlockInputStream extends InputStream
{
    private final DataInputStream inData;
    private final CountingInputStream inCounted;
    private InputStream filterChain;
    private final Check check;
    private final boolean verifyCheck;
    private long uncompressedSizeInHeader;
    private long compressedSizeInHeader;
    private long compressedSizeLimit;
    private final int headerSize;
    private long uncompressedSize;
    private boolean endReached;
    private final byte[] tempBuf;
    
    public BlockInputStream(final InputStream inputStream, final Check check, final boolean verifyCheck, final int n, final long n2, final long uncompressedSizeInHeader, final ArrayCache arrayCache) throws IOException, IndexIndicatorException {
        this.uncompressedSizeInHeader = -1L;
        this.compressedSizeInHeader = -1L;
        this.uncompressedSize = 0L;
        this.endReached = false;
        this.tempBuf = new byte[1];
        this.check = check;
        this.verifyCheck = verifyCheck;
        this.inData = new DataInputStream(inputStream);
        final int unsignedByte = this.inData.readUnsignedByte();
        if (unsignedByte == 0) {
            throw new IndexIndicatorException();
        }
        this.headerSize = 4 * (unsignedByte + 1);
        final byte[] array = new byte[this.headerSize];
        array[0] = (byte)unsignedByte;
        this.inData.readFully(array, 1, this.headerSize - 1);
        if (!DecoderUtil.isCRC32Valid(array, 0, this.headerSize - 4, this.headerSize - 4)) {
            throw new CorruptedInputException("XZ Block Header is corrupt");
        }
        if ((array[1] & 0x3C) != 0x0) {
            throw new UnsupportedOptionsException("Unsupported options in XZ Block Header");
        }
        final int n3 = (array[1] & 0x3) + 1;
        final long[] array2 = new long[n3];
        final byte[][] array3 = new byte[n3][];
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array, 2, this.headerSize - 6);
        try {
            this.compressedSizeLimit = 9223372036854775804L - this.headerSize - check.getSize();
            if ((array[1] & 0x40) != 0x0) {
                this.compressedSizeInHeader = DecoderUtil.decodeVLI(byteArrayInputStream);
                if (this.compressedSizeInHeader == 0L || this.compressedSizeInHeader > this.compressedSizeLimit) {
                    throw new CorruptedInputException();
                }
                this.compressedSizeLimit = this.compressedSizeInHeader;
            }
            if ((array[1] & 0x80) != 0x0) {
                this.uncompressedSizeInHeader = DecoderUtil.decodeVLI(byteArrayInputStream);
            }
            for (int i = 0; i < n3; ++i) {
                array2[i] = DecoderUtil.decodeVLI(byteArrayInputStream);
                final long decodeVLI = DecoderUtil.decodeVLI(byteArrayInputStream);
                if (decodeVLI > byteArrayInputStream.available()) {
                    throw new CorruptedInputException();
                }
                byteArrayInputStream.read(array3[i] = new byte[(int)decodeVLI]);
            }
        }
        catch (final IOException ex) {
            throw new CorruptedInputException("XZ Block Header is corrupt");
        }
        for (int j = byteArrayInputStream.available(); j > 0; --j) {
            if (byteArrayInputStream.read() != 0) {
                throw new UnsupportedOptionsException("Unsupported options in XZ Block Header");
            }
        }
        if (n2 != -1L) {
            final int n4 = this.headerSize + check.getSize();
            if (n4 >= n2) {
                throw new CorruptedInputException("XZ Index does not match a Block Header");
            }
            final long n5 = n2 - n4;
            if (n5 > this.compressedSizeLimit || (this.compressedSizeInHeader != -1L && this.compressedSizeInHeader != n5)) {
                throw new CorruptedInputException("XZ Index does not match a Block Header");
            }
            if (this.uncompressedSizeInHeader != -1L && this.uncompressedSizeInHeader != uncompressedSizeInHeader) {
                throw new CorruptedInputException("XZ Index does not match a Block Header");
            }
            this.compressedSizeLimit = n5;
            this.compressedSizeInHeader = n5;
            this.uncompressedSizeInHeader = uncompressedSizeInHeader;
        }
        final FilterDecoder[] array4 = new FilterDecoder[array2.length];
        for (int k = 0; k < array4.length; ++k) {
            if (array2[k] == 33L) {
                array4[k] = new LZMA2Decoder(array3[k]);
            }
            else if (array2[k] == 3L) {
                array4[k] = new DeltaDecoder(array3[k]);
            }
            else {
                if (!BCJCoder.isBCJFilterID(array2[k])) {
                    throw new UnsupportedOptionsException("Unknown Filter ID " + array2[k]);
                }
                array4[k] = new BCJDecoder(array2[k], array3[k]);
            }
        }
        RawCoder.validate(array4);
        if (n >= 0) {
            int n6 = 0;
            for (int l = 0; l < array4.length; ++l) {
                n6 += array4[l].getMemoryUsage();
            }
            if (n6 > n) {
                throw new MemoryLimitException(n6, n);
            }
        }
        this.inCounted = new CountingInputStream(inputStream);
        this.filterChain = this.inCounted;
        for (int n7 = array4.length - 1; n7 >= 0; --n7) {
            this.filterChain = array4[n7].getInputStream(this.filterChain, arrayCache);
        }
    }
    
    @Override
    public int read() throws IOException {
        return (this.read(this.tempBuf, 0, 1) == -1) ? -1 : (this.tempBuf[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        if (this.endReached) {
            return -1;
        }
        final int read = this.filterChain.read(array, n, n2);
        if (read > 0) {
            if (this.verifyCheck) {
                this.check.update(array, n, read);
            }
            this.uncompressedSize += read;
            final long size = this.inCounted.getSize();
            if (size < 0L || size > this.compressedSizeLimit || this.uncompressedSize < 0L || (this.uncompressedSizeInHeader != -1L && this.uncompressedSize > this.uncompressedSizeInHeader)) {
                throw new CorruptedInputException();
            }
            if (read < n2 || this.uncompressedSize == this.uncompressedSizeInHeader) {
                if (this.filterChain.read() != -1) {
                    throw new CorruptedInputException();
                }
                this.validate();
                this.endReached = true;
            }
        }
        else if (read == -1) {
            this.validate();
            this.endReached = true;
        }
        return read;
    }
    
    private void validate() throws IOException {
        long size = this.inCounted.getSize();
        if ((this.compressedSizeInHeader != -1L && this.compressedSizeInHeader != size) || (this.uncompressedSizeInHeader != -1L && this.uncompressedSizeInHeader != this.uncompressedSize)) {
            throw new CorruptedInputException();
        }
        while ((size++ & 0x3L) != 0x0L) {
            if (this.inData.readUnsignedByte() != 0) {
                throw new CorruptedInputException();
            }
        }
        final byte[] array = new byte[this.check.getSize()];
        this.inData.readFully(array);
        if (this.verifyCheck && !Arrays.equals(this.check.finish(), array)) {
            throw new CorruptedInputException("Integrity check (" + this.check.getName() + ") does not match");
        }
    }
    
    @Override
    public int available() throws IOException {
        return this.filterChain.available();
    }
    
    @Override
    public void close() {
        try {
            this.filterChain.close();
        }
        catch (final IOException ex) {
            assert false;
        }
        this.filterChain = null;
    }
    
    public long getUnpaddedSize() {
        return this.headerSize + this.inCounted.getSize() + this.check.getSize();
    }
    
    public long getUncompressedSize() {
        return this.uncompressedSize;
    }
}
