package org.apache.commons.fileupload.util.mime;

import java.io.IOException;
import java.io.OutputStream;

final class Base64Decoder
{
    private static final int INVALID_BYTE = -1;
    private static final int PAD_BYTE = -2;
    private static final int MASK_BYTE_UNSIGNED = 255;
    private static final int INPUT_BYTES_PER_CHUNK = 4;
    private static final byte[] ENCODING_TABLE;
    private static final byte PADDING = 61;
    private static final byte[] DECODING_TABLE;
    
    private Base64Decoder() {
    }
    
    public static int decode(final byte[] data, final OutputStream out) throws IOException {
        int outLen = 0;
        final byte[] cache = new byte[4];
        int cachedBytes = 0;
        for (final byte b : data) {
            final byte d = Base64Decoder.DECODING_TABLE[0xFF & b];
            if (d != -1) {
                cache[cachedBytes++] = d;
                if (cachedBytes == 4) {
                    final byte b2 = cache[0];
                    final byte b3 = cache[1];
                    final byte b4 = cache[2];
                    final byte b5 = cache[3];
                    if (b2 == -2 || b3 == -2) {
                        throw new IOException("Invalid Base64 input: incorrect padding, first two bytes cannot be padding");
                    }
                    out.write(b2 << 2 | b3 >> 4);
                    ++outLen;
                    if (b4 != -2) {
                        out.write(b3 << 4 | b4 >> 2);
                        ++outLen;
                        if (b5 != -2) {
                            out.write(b4 << 6 | b5);
                            ++outLen;
                        }
                    }
                    else if (b5 != -2) {
                        throw new IOException("Invalid Base64 input: incorrect padding, 4th byte must be padding if 3rd byte is");
                    }
                    cachedBytes = 0;
                }
            }
        }
        if (cachedBytes != 0) {
            throw new IOException("Invalid Base64 input: truncated");
        }
        return outLen;
    }
    
    static {
        ENCODING_TABLE = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
        DECODING_TABLE = new byte[256];
        for (int i = 0; i < Base64Decoder.DECODING_TABLE.length; ++i) {
            Base64Decoder.DECODING_TABLE[i] = -1;
        }
        for (int i = 0; i < Base64Decoder.ENCODING_TABLE.length; ++i) {
            Base64Decoder.DECODING_TABLE[Base64Decoder.ENCODING_TABLE[i]] = (byte)i;
        }
        Base64Decoder.DECODING_TABLE[61] = -2;
    }
}
