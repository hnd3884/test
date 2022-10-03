package org.apache.commons.net.io;

import java.net.Socket;
import java.io.Closeable;
import java.io.Writer;
import java.io.Reader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public final class Util
{
    public static final int DEFAULT_COPY_BUFFER_SIZE = 1024;
    
    private Util() {
    }
    
    public static final long copyStream(final InputStream source, final OutputStream dest, final int bufferSize, final long streamSize, final CopyStreamListener listener, final boolean flush) throws CopyStreamException {
        long total = 0L;
        final byte[] buffer = new byte[(bufferSize > 0) ? bufferSize : 1024];
        try {
            int numBytes;
            while ((numBytes = source.read(buffer)) != -1) {
                if (numBytes == 0) {
                    final int singleByte = source.read();
                    if (singleByte < 0) {
                        break;
                    }
                    dest.write(singleByte);
                    if (flush) {
                        dest.flush();
                    }
                    ++total;
                    if (listener == null) {
                        continue;
                    }
                    listener.bytesTransferred(total, 1, streamSize);
                }
                else {
                    dest.write(buffer, 0, numBytes);
                    if (flush) {
                        dest.flush();
                    }
                    total += numBytes;
                    if (listener == null) {
                        continue;
                    }
                    listener.bytesTransferred(total, numBytes, streamSize);
                }
            }
        }
        catch (final IOException e) {
            throw new CopyStreamException("IOException caught while copying.", total, e);
        }
        return total;
    }
    
    public static final long copyStream(final InputStream source, final OutputStream dest, final int bufferSize, final long streamSize, final CopyStreamListener listener) throws CopyStreamException {
        return copyStream(source, dest, bufferSize, streamSize, listener, true);
    }
    
    public static final long copyStream(final InputStream source, final OutputStream dest, final int bufferSize) throws CopyStreamException {
        return copyStream(source, dest, bufferSize, -1L, null);
    }
    
    public static final long copyStream(final InputStream source, final OutputStream dest) throws CopyStreamException {
        return copyStream(source, dest, 1024);
    }
    
    public static final long copyReader(final Reader source, final Writer dest, final int bufferSize, final long streamSize, final CopyStreamListener listener) throws CopyStreamException {
        long total = 0L;
        final char[] buffer = new char[(bufferSize > 0) ? bufferSize : 1024];
        try {
            int numChars;
            while ((numChars = source.read(buffer)) != -1) {
                if (numChars == 0) {
                    final int singleChar = source.read();
                    if (singleChar < 0) {
                        break;
                    }
                    dest.write(singleChar);
                    dest.flush();
                    ++total;
                    if (listener == null) {
                        continue;
                    }
                    listener.bytesTransferred(total, 1, streamSize);
                }
                else {
                    dest.write(buffer, 0, numChars);
                    dest.flush();
                    total += numChars;
                    if (listener == null) {
                        continue;
                    }
                    listener.bytesTransferred(total, numChars, streamSize);
                }
            }
        }
        catch (final IOException e) {
            throw new CopyStreamException("IOException caught while copying.", total, e);
        }
        return total;
    }
    
    public static final long copyReader(final Reader source, final Writer dest, final int bufferSize) throws CopyStreamException {
        return copyReader(source, dest, bufferSize, -1L, null);
    }
    
    public static final long copyReader(final Reader source, final Writer dest) throws CopyStreamException {
        return copyReader(source, dest, 1024);
    }
    
    public static void closeQuietly(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public static void closeQuietly(final Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            }
            catch (final IOException ex) {}
        }
    }
}
