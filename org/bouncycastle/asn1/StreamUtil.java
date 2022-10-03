package org.bouncycastle.asn1;

import java.nio.channels.FileChannel;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

class StreamUtil
{
    private static final long MAX_MEMORY;
    
    static int findLimit(final InputStream inputStream) {
        if (inputStream instanceof LimitedInputStream) {
            return ((LimitedInputStream)inputStream).getRemaining();
        }
        if (inputStream instanceof ASN1InputStream) {
            return ((ASN1InputStream)inputStream).getLimit();
        }
        if (inputStream instanceof ByteArrayInputStream) {
            return ((ByteArrayInputStream)inputStream).available();
        }
        if (inputStream instanceof FileInputStream) {
            try {
                final FileChannel channel = ((FileInputStream)inputStream).getChannel();
                final long n = (channel != null) ? channel.size() : 2147483647L;
                if (n < 2147483647L) {
                    return (int)n;
                }
            }
            catch (final IOException ex) {}
        }
        if (StreamUtil.MAX_MEMORY > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)StreamUtil.MAX_MEMORY;
    }
    
    static int calculateBodyLength(final int n) {
        int n2 = 1;
        if (n > 127) {
            int n3 = 1;
            int n4 = n;
            while ((n4 >>>= 8) != 0) {
                ++n3;
            }
            for (int i = (n3 - 1) * 8; i >= 0; i -= 8) {
                ++n2;
            }
        }
        return n2;
    }
    
    static int calculateTagLength(int i) throws IOException {
        int n = 1;
        if (i >= 31) {
            if (i < 128) {
                ++n;
            }
            else {
                final byte[] array = new byte[5];
                int length = array.length;
                array[--length] = (byte)(i & 0x7F);
                do {
                    i >>= 7;
                    array[--length] = (byte)((i & 0x7F) | 0x80);
                } while (i > 127);
                n += array.length - length;
            }
        }
        return n;
    }
    
    static {
        MAX_MEMORY = Runtime.getRuntime().maxMemory();
    }
}
