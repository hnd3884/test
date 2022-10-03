package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;

public class IOUtils
{
    public static long skip(final InputStream input, final long toSkip, final byte[] buffer) throws IOException {
        if (toSkip < 0L) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        long remain;
        long n;
        for (remain = toSkip; remain > 0L; remain -= n) {
            n = input.read(buffer, 0, (int)Math.min(remain, buffer.length));
            if (n < 0L) {
                break;
            }
        }
        return toSkip - remain;
    }
}
