package org.tukaani.xz.common;

import java.io.IOException;
import java.util.zip.CRC32;
import java.io.OutputStream;

public class EncoderUtil extends Util
{
    public static void writeCRC32(final OutputStream outputStream, final byte[] array) throws IOException {
        final CRC32 crc32 = new CRC32();
        crc32.update(array);
        final long value = crc32.getValue();
        for (int i = 0; i < 4; ++i) {
            outputStream.write((byte)(value >>> i * 8));
        }
    }
    
    public static void encodeVLI(final OutputStream outputStream, long n) throws IOException {
        while (n >= 128L) {
            outputStream.write((byte)(n | 0x80L));
            n >>>= 7;
        }
        outputStream.write((byte)n);
    }
}
