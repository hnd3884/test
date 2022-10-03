package org.apache.axiom.blob;

import java.io.IOException;
import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.ext.io.ReadFromSupport;
import java.io.OutputStream;
import java.io.InputStream;

final class IOUtil
{
    private IOUtil() {
    }
    
    static long copy(final InputStream in, final OutputStream out, final long length) throws StreamCopyException {
        if (out instanceof ReadFromSupport) {
            return ((ReadFromSupport)out).readFrom(in, length);
        }
        final byte[] buffer = new byte[4096];
        long read = 0L;
        int c;
        for (long toRead = (length == -1L) ? Long.MAX_VALUE : length; toRead > 0L; toRead -= c) {
            try {
                c = in.read(buffer, 0, (int)Math.min(toRead, buffer.length));
            }
            catch (final IOException ex) {
                throw new StreamCopyException(1, ex);
            }
            if (c == -1) {
                break;
            }
            try {
                out.write(buffer, 0, c);
            }
            catch (final IOException ex) {
                throw new StreamCopyException(2, ex);
            }
            read += c;
        }
        return read;
    }
}
