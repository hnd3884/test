package org.apache.lucene.codecs;

import org.apache.lucene.store.BufferedChecksumIndexInput;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.ChecksumIndexInput;
import org.apache.lucene.store.IndexOutput;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.lucene.index.IndexFormatTooNewException;
import org.apache.lucene.index.IndexFormatTooOldException;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.StringHelper;
import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.store.DataOutput;

public final class CodecUtil
{
    public static final int CODEC_MAGIC = 1071082519;
    public static final int FOOTER_MAGIC = -1071082520;
    
    private CodecUtil() {
    }
    
    public static void writeHeader(final DataOutput out, final String codec, final int version) throws IOException {
        final BytesRef bytes = new BytesRef(codec);
        if (bytes.length != codec.length() || bytes.length >= 128) {
            throw new IllegalArgumentException("codec must be simple ASCII, less than 128 characters in length [got " + codec + "]");
        }
        out.writeInt(1071082519);
        out.writeString(codec);
        out.writeInt(version);
    }
    
    public static void writeIndexHeader(final DataOutput out, final String codec, final int version, final byte[] id, final String suffix) throws IOException {
        if (id.length != 16) {
            throw new IllegalArgumentException("Invalid id: " + StringHelper.idToString(id));
        }
        writeHeader(out, codec, version);
        out.writeBytes(id, 0, id.length);
        final BytesRef suffixBytes = new BytesRef(suffix);
        if (suffixBytes.length != suffix.length() || suffixBytes.length >= 256) {
            throw new IllegalArgumentException("suffix must be simple ASCII, less than 256 characters in length [got " + suffix + "]");
        }
        out.writeByte((byte)suffixBytes.length);
        out.writeBytes(suffixBytes.bytes, suffixBytes.offset, suffixBytes.length);
    }
    
    public static int headerLength(final String codec) {
        return 9 + codec.length();
    }
    
    public static int indexHeaderLength(final String codec, final String suffix) {
        return headerLength(codec) + 16 + 1 + suffix.length();
    }
    
    public static int checkHeader(final DataInput in, final String codec, final int minVersion, final int maxVersion) throws IOException {
        final int actualHeader = in.readInt();
        if (actualHeader != 1071082519) {
            throw new CorruptIndexException("codec header mismatch: actual header=" + actualHeader + " vs expected header=" + 1071082519, in);
        }
        return checkHeaderNoMagic(in, codec, minVersion, maxVersion);
    }
    
    public static int checkHeaderNoMagic(final DataInput in, final String codec, final int minVersion, final int maxVersion) throws IOException {
        final String actualCodec = in.readString();
        if (!actualCodec.equals(codec)) {
            throw new CorruptIndexException("codec mismatch: actual codec=" + actualCodec + " vs expected codec=" + codec, in);
        }
        final int actualVersion = in.readInt();
        if (actualVersion < minVersion) {
            throw new IndexFormatTooOldException(in, actualVersion, minVersion, maxVersion);
        }
        if (actualVersion > maxVersion) {
            throw new IndexFormatTooNewException(in, actualVersion, minVersion, maxVersion);
        }
        return actualVersion;
    }
    
    public static int checkIndexHeader(final DataInput in, final String codec, final int minVersion, final int maxVersion, final byte[] expectedID, final String expectedSuffix) throws IOException {
        final int version = checkHeader(in, codec, minVersion, maxVersion);
        checkIndexHeaderID(in, expectedID);
        checkIndexHeaderSuffix(in, expectedSuffix);
        return version;
    }
    
    public static byte[] checkIndexHeaderID(final DataInput in, final byte[] expectedID) throws IOException {
        final byte[] id = new byte[16];
        in.readBytes(id, 0, id.length);
        if (!Arrays.equals(id, expectedID)) {
            throw new CorruptIndexException("file mismatch, expected id=" + StringHelper.idToString(expectedID) + ", got=" + StringHelper.idToString(id), in);
        }
        return id;
    }
    
    public static String checkIndexHeaderSuffix(final DataInput in, final String expectedSuffix) throws IOException {
        final int suffixLength = in.readByte() & 0xFF;
        final byte[] suffixBytes = new byte[suffixLength];
        in.readBytes(suffixBytes, 0, suffixBytes.length);
        final String suffix = new String(suffixBytes, 0, suffixBytes.length, StandardCharsets.UTF_8);
        if (!suffix.equals(expectedSuffix)) {
            throw new CorruptIndexException("file mismatch, expected suffix=" + expectedSuffix + ", got=" + suffix, in);
        }
        return suffix;
    }
    
    public static void writeFooter(final IndexOutput out) throws IOException {
        out.writeInt(-1071082520);
        out.writeInt(0);
        writeCRC(out);
    }
    
    public static int footerLength() {
        return 16;
    }
    
    public static long checkFooter(final ChecksumIndexInput in) throws IOException {
        validateFooter(in);
        final long actualChecksum = in.getChecksum();
        final long expectedChecksum = readCRC(in);
        if (expectedChecksum != actualChecksum) {
            throw new CorruptIndexException("checksum failed (hardware problem?) : expected=" + Long.toHexString(expectedChecksum) + " actual=" + Long.toHexString(actualChecksum), in);
        }
        return actualChecksum;
    }
    
    public static void checkFooter(final ChecksumIndexInput in, final Throwable priorException) throws IOException {
        if (priorException == null) {
            checkFooter(in);
        }
        else {
            try {
                final long remaining = in.length() - in.getFilePointer();
                if (remaining < footerLength()) {
                    priorException.addSuppressed(new CorruptIndexException("checksum status indeterminate: remaining=" + remaining + ", please run checkindex for more details", in));
                }
                else {
                    in.skipBytes(remaining - footerLength());
                    try {
                        final long checksum = checkFooter(in);
                        priorException.addSuppressed(new CorruptIndexException("checksum passed (" + Long.toHexString(checksum) + "). possibly transient resource issue, or a Lucene or JVM bug", in));
                    }
                    catch (final CorruptIndexException t) {
                        priorException.addSuppressed(t);
                    }
                }
            }
            catch (final Throwable t2) {
                priorException.addSuppressed(new CorruptIndexException("checksum status indeterminate: unexpected exception", in, t2));
            }
            IOUtils.reThrow(priorException);
        }
    }
    
    public static long retrieveChecksum(final IndexInput in) throws IOException {
        if (in.length() < footerLength()) {
            throw new CorruptIndexException("misplaced codec footer (file truncated?): length=" + in.length() + " but footerLength==" + footerLength(), in);
        }
        in.seek(in.length() - footerLength());
        validateFooter(in);
        return readCRC(in);
    }
    
    private static void validateFooter(final IndexInput in) throws IOException {
        final long remaining = in.length() - in.getFilePointer();
        final long expected = footerLength();
        if (remaining < expected) {
            throw new CorruptIndexException("misplaced codec footer (file truncated?): remaining=" + remaining + ", expected=" + expected, in);
        }
        if (remaining > expected) {
            throw new CorruptIndexException("misplaced codec footer (file extended?): remaining=" + remaining + ", expected=" + expected, in);
        }
        final int magic = in.readInt();
        if (magic != -1071082520) {
            throw new CorruptIndexException("codec footer mismatch (file truncated?): actual footer=" + magic + " vs expected footer=" + -1071082520, in);
        }
        final int algorithmID = in.readInt();
        if (algorithmID != 0) {
            throw new CorruptIndexException("codec footer mismatch: unknown algorithmID: " + algorithmID, in);
        }
    }
    
    @Deprecated
    public static void checkEOF(final IndexInput in) throws IOException {
        if (in.getFilePointer() != in.length()) {
            throw new CorruptIndexException("did not read all bytes from file: read " + in.getFilePointer() + " vs size " + in.length(), in);
        }
    }
    
    public static long checksumEntireFile(final IndexInput input) throws IOException {
        final IndexInput clone = input.clone();
        clone.seek(0L);
        final ChecksumIndexInput in = new BufferedChecksumIndexInput(clone);
        assert in.getFilePointer() == 0L;
        in.seek(in.length() - footerLength());
        return checkFooter(in);
    }
    
    public static long readCRC(final IndexInput input) throws IOException {
        final long value = input.readLong();
        if ((value & 0xFFFFFFFF00000000L) != 0x0L) {
            throw new CorruptIndexException("Illegal CRC-32 checksum: " + value, input);
        }
        return value;
    }
    
    public static void writeCRC(final IndexOutput output) throws IOException {
        final long value = output.getChecksum();
        if ((value & 0xFFFFFFFF00000000L) != 0x0L) {
            throw new IllegalStateException("Illegal CRC-32 checksum: " + value + " (resource=" + output + ")");
        }
        output.writeLong(value);
    }
}
