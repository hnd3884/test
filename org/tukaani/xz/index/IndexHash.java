package org.tukaani.xz.index;

import java.io.IOException;
import java.io.DataInputStream;
import java.util.Arrays;
import org.tukaani.xz.common.DecoderUtil;
import java.util.zip.Checksum;
import java.util.zip.CheckedInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import org.tukaani.xz.check.CRC32;
import org.tukaani.xz.check.SHA256;
import org.tukaani.xz.XZIOException;
import org.tukaani.xz.CorruptedInputException;
import org.tukaani.xz.check.Check;

public class IndexHash extends IndexBase
{
    private Check hash;
    
    public IndexHash() {
        super(new CorruptedInputException());
        try {
            this.hash = new SHA256();
        }
        catch (final NoSuchAlgorithmException ex) {
            this.hash = new CRC32();
        }
    }
    
    public void add(final long n, final long n2) throws XZIOException {
        super.add(n, n2);
        final ByteBuffer allocate = ByteBuffer.allocate(16);
        allocate.putLong(n);
        allocate.putLong(n2);
        this.hash.update(allocate.array());
    }
    
    public void validate(final InputStream inputStream) throws IOException {
        final java.util.zip.CRC32 crc32 = new java.util.zip.CRC32();
        crc32.update(0);
        final CheckedInputStream checkedInputStream = new CheckedInputStream(inputStream, crc32);
        if (DecoderUtil.decodeVLI(checkedInputStream) != this.recordCount) {
            throw new CorruptedInputException("XZ Block Header or the start of XZ Index is corrupt");
        }
        final IndexHash indexHash = new IndexHash();
        for (long n = 0L; n < this.recordCount; ++n) {
            final long decodeVLI = DecoderUtil.decodeVLI(checkedInputStream);
            final long decodeVLI2 = DecoderUtil.decodeVLI(checkedInputStream);
            try {
                indexHash.add(decodeVLI, decodeVLI2);
            }
            catch (final XZIOException ex) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
            if (indexHash.blocksSum > this.blocksSum || indexHash.uncompressedSum > this.uncompressedSum || indexHash.indexListSize > this.indexListSize) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
        }
        if (indexHash.blocksSum != this.blocksSum || indexHash.uncompressedSum != this.uncompressedSum || indexHash.indexListSize != this.indexListSize || !Arrays.equals(indexHash.hash.finish(), this.hash.finish())) {
            throw new CorruptedInputException("XZ Index is corrupt");
        }
        final DataInputStream dataInputStream = new DataInputStream(checkedInputStream);
        for (int i = this.getIndexPaddingSize(); i > 0; --i) {
            if (dataInputStream.readUnsignedByte() != 0) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
        }
        final long value = crc32.getValue();
        for (int j = 0; j < 4; ++j) {
            if ((value >>> j * 8 & 0xFFL) != dataInputStream.readUnsignedByte()) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
        }
    }
}
