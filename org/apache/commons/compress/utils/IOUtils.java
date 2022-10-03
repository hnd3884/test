package org.apache.commons.compress.utils;

import java.io.Closeable;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.file.LinkOption;

public final class IOUtils
{
    private static final int COPY_BUF_SIZE = 8024;
    private static final int SKIP_BUF_SIZE = 4096;
    public static final LinkOption[] EMPTY_LINK_OPTIONS;
    private static final byte[] SKIP_BUF;
    
    private IOUtils() {
    }
    
    public static long copy(final InputStream input, final OutputStream output) throws IOException {
        return copy(input, output, 8024);
    }
    
    public static long copy(final InputStream input, final OutputStream output, final int buffersize) throws IOException {
        if (buffersize < 1) {
            throw new IllegalArgumentException("buffersize must be bigger than 0");
        }
        final byte[] buffer = new byte[buffersize];
        int n = 0;
        long count = 0L;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
    public static long skip(final InputStream input, long numToSkip) throws IOException {
        final long available = numToSkip;
        while (numToSkip > 0L) {
            final long skipped = input.skip(numToSkip);
            if (skipped == 0L) {
                break;
            }
            numToSkip -= skipped;
        }
        while (numToSkip > 0L) {
            final int read = readFully(input, IOUtils.SKIP_BUF, 0, (int)Math.min(numToSkip, 4096L));
            if (read < 1) {
                break;
            }
            numToSkip -= read;
        }
        return available - numToSkip;
    }
    
    public static int read(final File file, final byte[] array) throws IOException {
        try (final InputStream inputStream = Files.newInputStream(file.toPath(), new OpenOption[0])) {
            return readFully(inputStream, array, 0, array.length);
        }
    }
    
    public static int readFully(final InputStream input, final byte[] array) throws IOException {
        return readFully(input, array, 0, array.length);
    }
    
    public static int readFully(final InputStream input, final byte[] array, final int offset, final int len) throws IOException {
        if (len < 0 || offset < 0 || len + offset > array.length || len + offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        int count = 0;
        for (int x = 0; count != len; count += x) {
            x = input.read(array, offset + count, len - count);
            if (x == -1) {
                break;
            }
        }
        return count;
    }
    
    public static void readFully(final ReadableByteChannel channel, final ByteBuffer b) throws IOException {
        int expectedLength;
        int read;
        int readNow;
        for (expectedLength = b.remaining(), read = 0; read < expectedLength; read += readNow) {
            readNow = channel.read(b);
            if (readNow <= 0) {
                break;
            }
        }
        if (read < expectedLength) {
            throw new EOFException();
        }
    }
    
    public static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }
    
    public static void closeQuietly(final Closeable c) {
        if (c != null) {
            try {
                c.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public static void copy(final File sourceFile, final OutputStream outputStream) throws IOException {
        Files.copy(sourceFile.toPath(), outputStream);
    }
    
    public static long copyRange(final InputStream input, final long len, final OutputStream output) throws IOException {
        return copyRange(input, len, output, 8024);
    }
    
    public static long copyRange(final InputStream input, final long len, final OutputStream output, final int buffersize) throws IOException {
        if (buffersize < 1) {
            throw new IllegalArgumentException("buffersize must be bigger than 0");
        }
        byte[] buffer;
        int n;
        long count;
        for (buffer = new byte[(int)Math.min(buffersize, len)], n = 0, count = 0L; count < len && -1 != (n = input.read(buffer, 0, (int)Math.min(len - count, buffer.length))); count += n) {
            output.write(buffer, 0, n);
        }
        return count;
    }
    
    public static byte[] readRange(final InputStream input, final int len) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copyRange(input, len, output);
        return output.toByteArray();
    }
    
    public static byte[] readRange(final ReadableByteChannel input, final int len) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final ByteBuffer b = ByteBuffer.allocate(Math.min(len, 8024));
        int readNow;
        for (int read = 0; read < len; read += readNow) {
            readNow = input.read(b);
            if (readNow <= 0) {
                break;
            }
            output.write(b.array(), 0, readNow);
            b.rewind();
        }
        return output.toByteArray();
    }
    
    static {
        EMPTY_LINK_OPTIONS = new LinkOption[0];
        SKIP_BUF = new byte[4096];
    }
}
