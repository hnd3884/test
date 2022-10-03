package org.apache.poi.util;

import java.util.zip.Checksum;
import java.util.zip.CRC32;
import java.io.FileOutputStream;
import java.io.File;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.Closeable;
import org.apache.poi.POIDocument;
import java.nio.channels.ReadableByteChannel;
import java.nio.ByteBuffer;
import java.io.EOFException;
import java.io.PushbackInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.poi.EmptyFileException;
import java.io.IOException;
import java.io.InputStream;

public final class IOUtils
{
    private static final POILogger logger;
    private static final int SKIP_BUFFER_SIZE = 2048;
    private static byte[] SKIP_BYTE_BUFFER;
    private static int BYTE_ARRAY_MAX_OVERRIDE;
    
    private IOUtils() {
    }
    
    public static void setByteArrayMaxOverride(final int maxOverride) {
        IOUtils.BYTE_ARRAY_MAX_OVERRIDE = maxOverride;
    }
    
    public static byte[] peekFirst8Bytes(final InputStream stream) throws IOException, EmptyFileException {
        return peekFirstNBytes(stream, 8);
    }
    
    private static void checkByteSizeLimit(final int length) {
        if (IOUtils.BYTE_ARRAY_MAX_OVERRIDE != -1 && length > IOUtils.BYTE_ARRAY_MAX_OVERRIDE) {
            throwRFE(length, IOUtils.BYTE_ARRAY_MAX_OVERRIDE);
        }
    }
    
    public static byte[] peekFirstNBytes(final InputStream stream, final int limit) throws IOException, EmptyFileException {
        checkByteSizeLimit(limit);
        stream.mark(limit);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(limit);
        copy(new BoundedInputStream(stream, limit), bos);
        final int readBytes = bos.size();
        if (readBytes == 0) {
            throw new EmptyFileException();
        }
        if (readBytes < limit) {
            bos.write(new byte[limit - readBytes]);
        }
        final byte[] peekedBytes = bos.toByteArray();
        if (stream instanceof PushbackInputStream) {
            final PushbackInputStream pin = (PushbackInputStream)stream;
            pin.unread(peekedBytes, 0, readBytes);
        }
        else {
            stream.reset();
        }
        return peekedBytes;
    }
    
    public static byte[] toByteArray(final InputStream stream) throws IOException {
        return toByteArray(stream, Integer.MAX_VALUE);
    }
    
    public static byte[] toByteArray(final InputStream stream, final int length) throws IOException {
        return toByteArray(stream, length, Integer.MAX_VALUE);
    }
    
    public static byte[] toByteArray(final InputStream stream, final long length, final int maxLength) throws IOException {
        if (length < 0L || maxLength < 0L) {
            throw new RecordFormatException("Can't allocate an array of length < 0");
        }
        if (length > 2147483647L) {
            throw new RecordFormatException("Can't allocate an array > 2147483647");
        }
        if (length != 2147483647L || maxLength != Integer.MAX_VALUE) {
            checkLength(length, maxLength);
        }
        final int len = Math.min((int)length, maxLength);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream((len == Integer.MAX_VALUE) ? 4096 : len);
        final byte[] buffer = new byte[4096];
        int totalBytes = 0;
        int readBytes;
        do {
            readBytes = stream.read(buffer, 0, Math.min(buffer.length, len - totalBytes));
            totalBytes += Math.max(readBytes, 0);
            if (readBytes > 0) {
                baos.write(buffer, 0, readBytes);
            }
            checkByteSizeLimit(readBytes);
        } while (totalBytes < len && readBytes > -1);
        if (maxLength != Integer.MAX_VALUE && totalBytes == maxLength) {
            throw new IOException("MaxLength (" + maxLength + ") reached - stream seems to be invalid.");
        }
        if (len != Integer.MAX_VALUE && totalBytes < len) {
            throw new EOFException("unexpected EOF - expected len: " + len + " - actual len: " + totalBytes);
        }
        return baos.toByteArray();
    }
    
    private static void checkLength(final long length, final int maxLength) {
        if (IOUtils.BYTE_ARRAY_MAX_OVERRIDE > 0) {
            if (length > IOUtils.BYTE_ARRAY_MAX_OVERRIDE) {
                throwRFE(length, IOUtils.BYTE_ARRAY_MAX_OVERRIDE);
            }
        }
        else if (length > maxLength) {
            throwRFE(length, maxLength);
        }
    }
    
    public static byte[] toByteArray(final ByteBuffer buffer, final int length) {
        if (buffer.hasArray() && buffer.arrayOffset() == 0) {
            return buffer.array();
        }
        checkByteSizeLimit(length);
        final byte[] data = new byte[length];
        buffer.get(data);
        return data;
    }
    
    public static int readFully(final InputStream in, final byte[] b) throws IOException {
        return readFully(in, b, 0, b.length);
    }
    
    public static int readFully(final InputStream in, final byte[] b, final int off, final int len) throws IOException {
        int total = 0;
        while (true) {
            final int got = in.read(b, off + total, len - total);
            if (got < 0) {
                return (total == 0) ? -1 : total;
            }
            total += got;
            if (total == len) {
                return total;
            }
        }
    }
    
    public static int readFully(final ReadableByteChannel channel, final ByteBuffer b) throws IOException {
        int total = 0;
        while (true) {
            final int got = channel.read(b);
            if (got < 0) {
                return (total == 0) ? -1 : total;
            }
            total += got;
            if (total == b.capacity() || b.position() == b.capacity()) {
                return total;
            }
        }
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public static void write(final POIDocument doc, final OutputStream out) throws IOException {
        try {
            doc.write(out);
        }
        finally {
            closeQuietly(out);
        }
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public static void write(final Workbook doc, final OutputStream out) throws IOException {
        try {
            doc.write(out);
        }
        finally {
            closeQuietly(out);
        }
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public static void writeAndClose(final POIDocument doc, final OutputStream out) throws IOException {
        try {
            write(doc, out);
        }
        finally {
            closeQuietly(doc);
        }
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public static void writeAndClose(final POIDocument doc, final File out) throws IOException {
        try {
            doc.write(out);
        }
        finally {
            closeQuietly(doc);
        }
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public static void writeAndClose(final POIDocument doc) throws IOException {
        try {
            doc.write();
        }
        finally {
            closeQuietly(doc);
        }
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public static void writeAndClose(final Workbook doc, final OutputStream out) throws IOException {
        try {
            doc.write(out);
        }
        finally {
            closeQuietly(doc);
        }
    }
    
    public static long copy(final InputStream inp, final OutputStream out) throws IOException {
        return copy(inp, out, -1L);
    }
    
    public static long copy(final InputStream inp, final OutputStream out, final long limit) throws IOException {
        final byte[] buff = new byte[4096];
        long totalCount = 0L;
        int readBytes = -1;
        do {
            final int todoBytes = (int)((limit < 0L) ? buff.length : Math.min(limit - totalCount, buff.length));
            if (todoBytes > 0) {
                readBytes = inp.read(buff, 0, todoBytes);
                if (readBytes <= 0) {
                    continue;
                }
                out.write(buff, 0, readBytes);
                totalCount += readBytes;
            }
        } while (readBytes >= 0 && (limit == -1L || totalCount < limit));
        return totalCount;
    }
    
    public static long copy(final InputStream srcStream, final File destFile) throws IOException {
        final File destDirectory = destFile.getParentFile();
        if (!destDirectory.exists() && !destDirectory.mkdirs()) {
            throw new RuntimeException("Can't create destination directory: " + destDirectory);
        }
        try (final OutputStream destStream = new FileOutputStream(destFile)) {
            return copy(srcStream, destStream);
        }
    }
    
    public static long calculateChecksum(final byte[] data) {
        final Checksum sum = new CRC32();
        sum.update(data, 0, data.length);
        return sum.getValue();
    }
    
    public static long calculateChecksum(final InputStream stream) throws IOException {
        final Checksum sum = new CRC32();
        final byte[] buf = new byte[4096];
        int count;
        while ((count = stream.read(buf)) != -1) {
            if (count > 0) {
                sum.update(buf, 0, count);
            }
        }
        return sum.getValue();
    }
    
    public static void closeQuietly(final Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        }
        catch (final Exception exc) {
            IOUtils.logger.log(7, "Unable to close resource: " + exc, exc);
        }
    }
    
    public static long skipFully(final InputStream input, final long toSkip) throws IOException {
        if (toSkip < 0L) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        if (toSkip == 0L) {
            return 0L;
        }
        if (IOUtils.SKIP_BYTE_BUFFER == null) {
            IOUtils.SKIP_BYTE_BUFFER = new byte[2048];
        }
        long remain;
        long n;
        for (remain = toSkip; remain > 0L; remain -= n) {
            n = input.read(IOUtils.SKIP_BYTE_BUFFER, 0, (int)Math.min(remain, 2048L));
            if (n < 0L) {
                break;
            }
        }
        if (toSkip == remain) {
            return -1L;
        }
        return toSkip - remain;
    }
    
    public static byte[] safelyAllocate(final long length, final int maxLength) {
        safelyAllocateCheck(length, maxLength);
        checkByteSizeLimit((int)length);
        return new byte[(int)length];
    }
    
    public static void safelyAllocateCheck(final long length, final int maxLength) {
        if (length < 0L) {
            throw new RecordFormatException("Can't allocate an array of length < 0, but had " + length + " and " + maxLength);
        }
        if (length > 2147483647L) {
            throw new RecordFormatException("Can't allocate an array > 2147483647");
        }
        checkLength(length, maxLength);
    }
    
    public static int readByte(final InputStream is) throws IOException {
        final int b = is.read();
        if (b == -1) {
            throw new EOFException();
        }
        return b;
    }
    
    private static void throwRFE(final long length, final int maxLength) {
        throw new RecordFormatException("Tried to allocate an array of length " + length + ", but " + maxLength + " is the maximum for this record type.\nIf the file is not corrupt, please open an issue on bugzilla to request \nincreasing the maximum allowable size for this record type.\nAs a temporary workaround, consider setting a higher override value with IOUtils.setByteArrayMaxOverride()");
    }
    
    static {
        logger = POILogFactory.getLogger(IOUtils.class);
        IOUtils.BYTE_ARRAY_MAX_OVERRIDE = -1;
    }
}
