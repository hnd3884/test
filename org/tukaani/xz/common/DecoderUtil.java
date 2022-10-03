package org.tukaani.xz.common;

import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;
import org.tukaani.xz.UnsupportedOptionsException;
import org.tukaani.xz.CorruptedInputException;
import org.tukaani.xz.XZFormatException;
import org.tukaani.xz.XZ;
import java.util.zip.CRC32;

public class DecoderUtil extends Util
{
    public static boolean isCRC32Valid(final byte[] array, final int n, final int n2, final int n3) {
        final CRC32 crc32 = new CRC32();
        crc32.update(array, n, n2);
        final long value = crc32.getValue();
        for (int i = 0; i < 4; ++i) {
            if ((byte)(value >>> i * 8) != array[n3 + i]) {
                return false;
            }
        }
        return true;
    }
    
    public static StreamFlags decodeStreamHeader(final byte[] array) throws IOException {
        for (int i = 0; i < XZ.HEADER_MAGIC.length; ++i) {
            if (array[i] != XZ.HEADER_MAGIC[i]) {
                throw new XZFormatException();
            }
        }
        if (!isCRC32Valid(array, XZ.HEADER_MAGIC.length, 2, XZ.HEADER_MAGIC.length + 2)) {
            throw new CorruptedInputException("XZ Stream Header is corrupt");
        }
        try {
            return decodeStreamFlags(array, XZ.HEADER_MAGIC.length);
        }
        catch (final UnsupportedOptionsException ex) {
            throw new UnsupportedOptionsException("Unsupported options in XZ Stream Header");
        }
    }
    
    public static StreamFlags decodeStreamFooter(final byte[] array) throws IOException {
        if (array[10] != XZ.FOOTER_MAGIC[0] || array[11] != XZ.FOOTER_MAGIC[1]) {
            throw new CorruptedInputException("XZ Stream Footer is corrupt");
        }
        if (!isCRC32Valid(array, 4, 6, 0)) {
            throw new CorruptedInputException("XZ Stream Footer is corrupt");
        }
        StreamFlags decodeStreamFlags;
        try {
            decodeStreamFlags = decodeStreamFlags(array, 8);
        }
        catch (final UnsupportedOptionsException ex) {
            throw new UnsupportedOptionsException("Unsupported options in XZ Stream Footer");
        }
        decodeStreamFlags.backwardSize = 0L;
        for (int i = 0; i < 4; ++i) {
            final StreamFlags streamFlags = decodeStreamFlags;
            streamFlags.backwardSize |= (array[i + 4] & 0xFF) << i * 8;
        }
        decodeStreamFlags.backwardSize = (decodeStreamFlags.backwardSize + 1L) * 4L;
        return decodeStreamFlags;
    }
    
    private static StreamFlags decodeStreamFlags(final byte[] array, final int n) throws UnsupportedOptionsException {
        if (array[n] != 0 || (array[n + 1] & 0xFF) >= 16) {
            throw new UnsupportedOptionsException();
        }
        final StreamFlags streamFlags = new StreamFlags();
        streamFlags.checkType = array[n + 1];
        return streamFlags;
    }
    
    public static boolean areStreamFlagsEqual(final StreamFlags streamFlags, final StreamFlags streamFlags2) {
        return streamFlags.checkType == streamFlags2.checkType;
    }
    
    public static long decodeVLI(final InputStream inputStream) throws IOException {
        int n = inputStream.read();
        if (n == -1) {
            throw new EOFException();
        }
        long n2 = n & 0x7F;
        int n3 = 0;
        while ((n & 0x80) != 0x0) {
            if (++n3 >= 9) {
                throw new CorruptedInputException();
            }
            n = inputStream.read();
            if (n == -1) {
                throw new EOFException();
            }
            if (n == 0) {
                throw new CorruptedInputException();
            }
            n2 |= (long)(n & 0x7F) << n3 * 7;
        }
        return n2;
    }
}
