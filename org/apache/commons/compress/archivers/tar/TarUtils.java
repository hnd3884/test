package org.apache.commons.compress.archivers.tar;

import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import java.nio.charset.StandardCharsets;
import org.apache.commons.compress.utils.IOUtils;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.compress.archivers.zip.ZipEncoding;

public class TarUtils
{
    private static final int BYTE_MASK = 255;
    static final ZipEncoding DEFAULT_ENCODING;
    static final ZipEncoding FALLBACK_ENCODING;
    
    private TarUtils() {
    }
    
    public static long parseOctal(final byte[] buffer, final int offset, final int length) {
        long result = 0L;
        int end = offset + length;
        int start = offset;
        if (length < 2) {
            throw new IllegalArgumentException("Length " + length + " must be at least 2");
        }
        if (buffer[start] == 0) {
            return 0L;
        }
        while (start < end && buffer[start] == 32) {
            ++start;
        }
        for (byte trailer = buffer[end - 1]; start < end && (trailer == 0 || trailer == 32); --end, trailer = buffer[end - 1]) {}
        while (start < end) {
            final byte currentByte = buffer[start];
            if (currentByte < 48 || currentByte > 55) {
                throw new IllegalArgumentException(exceptionMessage(buffer, offset, length, start, currentByte));
            }
            result = (result << 3) + (currentByte - 48);
            ++start;
        }
        return result;
    }
    
    public static long parseOctalOrBinary(final byte[] buffer, final int offset, final int length) {
        if ((buffer[offset] & 0x80) == 0x0) {
            return parseOctal(buffer, offset, length);
        }
        final boolean negative = buffer[offset] == -1;
        if (length < 9) {
            return parseBinaryLong(buffer, offset, length, negative);
        }
        return parseBinaryBigInteger(buffer, offset, length, negative);
    }
    
    private static long parseBinaryLong(final byte[] buffer, final int offset, final int length, final boolean negative) {
        if (length >= 9) {
            throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number exceeds maximum signed long value");
        }
        long val = 0L;
        for (int i = 1; i < length; ++i) {
            val = (val << 8) + (buffer[offset + i] & 0xFF);
        }
        if (negative) {
            --val;
            val ^= (long)Math.pow(2.0, (length - 1) * 8.0) - 1L;
        }
        return negative ? (-val) : val;
    }
    
    private static long parseBinaryBigInteger(final byte[] buffer, final int offset, final int length, final boolean negative) {
        final byte[] remainder = new byte[length - 1];
        System.arraycopy(buffer, offset + 1, remainder, 0, length - 1);
        BigInteger val = new BigInteger(remainder);
        if (negative) {
            val = val.add(BigInteger.valueOf(-1L)).not();
        }
        if (val.bitLength() > 63) {
            throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number exceeds maximum signed long value");
        }
        return negative ? (-val.longValue()) : val.longValue();
    }
    
    public static boolean parseBoolean(final byte[] buffer, final int offset) {
        return buffer[offset] == 1;
    }
    
    private static String exceptionMessage(final byte[] buffer, final int offset, final int length, final int current, final byte currentByte) {
        String string = new String(buffer, offset, length);
        string = string.replace("\u0000", "{NUL}");
        return "Invalid byte " + currentByte + " at offset " + (current - offset) + " in '" + string + "' len=" + length;
    }
    
    public static String parseName(final byte[] buffer, final int offset, final int length) {
        try {
            return parseName(buffer, offset, length, TarUtils.DEFAULT_ENCODING);
        }
        catch (final IOException ex) {
            try {
                return parseName(buffer, offset, length, TarUtils.FALLBACK_ENCODING);
            }
            catch (final IOException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }
    
    public static String parseName(final byte[] buffer, final int offset, final int length, final ZipEncoding encoding) throws IOException {
        int len = 0;
        for (int i = offset; len < length && buffer[i] != 0; ++len, ++i) {}
        if (len > 0) {
            final byte[] b = new byte[len];
            System.arraycopy(buffer, offset, b, 0, len);
            return encoding.decode(b);
        }
        return "";
    }
    
    public static TarArchiveStructSparse parseSparse(final byte[] buffer, final int offset) {
        final long sparseOffset = parseOctalOrBinary(buffer, offset, 12);
        final long sparseNumbytes = parseOctalOrBinary(buffer, offset + 12, 12);
        return new TarArchiveStructSparse(sparseOffset, sparseNumbytes);
    }
    
    static List<TarArchiveStructSparse> readSparseStructs(final byte[] buffer, final int offset, final int entries) throws IOException {
        final List<TarArchiveStructSparse> sparseHeaders = new ArrayList<TarArchiveStructSparse>();
        for (int i = 0; i < entries; ++i) {
            try {
                final TarArchiveStructSparse sparseHeader = parseSparse(buffer, offset + i * 24);
                if (sparseHeader.getOffset() < 0L) {
                    throw new IOException("Corrupted TAR archive, sparse entry with negative offset");
                }
                if (sparseHeader.getNumbytes() < 0L) {
                    throw new IOException("Corrupted TAR archive, sparse entry with negative numbytes");
                }
                sparseHeaders.add(sparseHeader);
            }
            catch (final IllegalArgumentException ex) {
                throw new IOException("Corrupted TAR archive, sparse entry is invalid", ex);
            }
        }
        return Collections.unmodifiableList((List<? extends TarArchiveStructSparse>)sparseHeaders);
    }
    
    public static int formatNameBytes(final String name, final byte[] buf, final int offset, final int length) {
        try {
            return formatNameBytes(name, buf, offset, length, TarUtils.DEFAULT_ENCODING);
        }
        catch (final IOException ex) {
            try {
                return formatNameBytes(name, buf, offset, length, TarUtils.FALLBACK_ENCODING);
            }
            catch (final IOException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }
    
    public static int formatNameBytes(final String name, final byte[] buf, final int offset, final int length, final ZipEncoding encoding) throws IOException {
        int len;
        ByteBuffer b;
        for (len = name.length(), b = encoding.encode(name); b.limit() > length && len > 0; b = encoding.encode(name.substring(0, --len))) {}
        final int limit = b.limit() - b.position();
        System.arraycopy(b.array(), b.arrayOffset(), buf, offset, limit);
        for (int i = limit; i < length; ++i) {
            buf[offset + i] = 0;
        }
        return offset + length;
    }
    
    public static void formatUnsignedOctalString(final long value, final byte[] buffer, final int offset, final int length) {
        int remaining = length;
        --remaining;
        if (value == 0L) {
            buffer[offset + remaining--] = 48;
        }
        else {
            long val;
            for (val = value; remaining >= 0 && val != 0L; val >>>= 3, --remaining) {
                buffer[offset + remaining] = (byte)(48 + (byte)(val & 0x7L));
            }
            if (val != 0L) {
                throw new IllegalArgumentException(value + "=" + Long.toOctalString(value) + " will not fit in octal number buffer of length " + length);
            }
        }
        while (remaining >= 0) {
            buffer[offset + remaining] = 48;
            --remaining;
        }
    }
    
    public static int formatOctalBytes(final long value, final byte[] buf, final int offset, final int length) {
        int idx = length - 2;
        formatUnsignedOctalString(value, buf, offset, idx);
        buf[offset + idx++] = 32;
        buf[offset + idx] = 0;
        return offset + length;
    }
    
    public static int formatLongOctalBytes(final long value, final byte[] buf, final int offset, final int length) {
        final int idx = length - 1;
        formatUnsignedOctalString(value, buf, offset, idx);
        buf[offset + idx] = 32;
        return offset + length;
    }
    
    public static int formatLongOctalOrBinaryBytes(final long value, final byte[] buf, final int offset, final int length) {
        final long maxAsOctalChar = (length == 8) ? 2097151L : 8589934591L;
        final boolean negative = value < 0L;
        if (!negative && value <= maxAsOctalChar) {
            return formatLongOctalBytes(value, buf, offset, length);
        }
        if (length < 9) {
            formatLongBinary(value, buf, offset, length, negative);
        }
        else {
            formatBigIntegerBinary(value, buf, offset, length, negative);
        }
        buf[offset] = (byte)(negative ? 255 : 128);
        return offset + length;
    }
    
    private static void formatLongBinary(final long value, final byte[] buf, final int offset, final int length, final boolean negative) {
        final int bits = (length - 1) * 8;
        final long max = 1L << bits;
        long val = Math.abs(value);
        if (val < 0L || val >= max) {
            throw new IllegalArgumentException("Value " + value + " is too large for " + length + " byte field.");
        }
        if (negative) {
            val ^= max - 1L;
            ++val;
            val |= 255L << bits;
        }
        for (int i = offset + length - 1; i >= offset; --i) {
            buf[i] = (byte)val;
            val >>= 8;
        }
    }
    
    private static void formatBigIntegerBinary(final long value, final byte[] buf, final int offset, final int length, final boolean negative) {
        final BigInteger val = BigInteger.valueOf(value);
        final byte[] b = val.toByteArray();
        final int len = b.length;
        if (len > length - 1) {
            throw new IllegalArgumentException("Value " + value + " is too large for " + length + " byte field.");
        }
        final int off = offset + length - len;
        System.arraycopy(b, 0, buf, off, len);
        final byte fill = (byte)(negative ? 255 : 0);
        for (int i = offset + 1; i < off; ++i) {
            buf[i] = fill;
        }
    }
    
    public static int formatCheckSumOctalBytes(final long value, final byte[] buf, final int offset, final int length) {
        int idx = length - 2;
        formatUnsignedOctalString(value, buf, offset, idx);
        buf[offset + idx++] = 0;
        buf[offset + idx] = 32;
        return offset + length;
    }
    
    public static long computeCheckSum(final byte[] buf) {
        long sum = 0L;
        for (final byte element : buf) {
            sum += (0xFF & element);
        }
        return sum;
    }
    
    public static boolean verifyCheckSum(final byte[] header) {
        final long storedSum = parseOctal(header, 148, 8);
        long unsignedSum = 0L;
        long signedSum = 0L;
        for (int i = 0; i < header.length; ++i) {
            byte b = header[i];
            if (148 <= i && i < 156) {
                b = 32;
            }
            unsignedSum += (0xFF & b);
            signedSum += b;
        }
        return storedSum == unsignedSum || storedSum == signedSum;
    }
    
    @Deprecated
    protected static Map<String, String> parsePaxHeaders(final InputStream inputStream, final List<TarArchiveStructSparse> sparseHeaders, final Map<String, String> globalPaxHeaders) throws IOException {
        return parsePaxHeaders(inputStream, sparseHeaders, globalPaxHeaders, -1L);
    }
    
    protected static Map<String, String> parsePaxHeaders(final InputStream inputStream, final List<TarArchiveStructSparse> sparseHeaders, final Map<String, String> globalPaxHeaders, final long headerSize) throws IOException {
        final Map<String, String> headers = new HashMap<String, String>(globalPaxHeaders);
        Long offset = null;
        int totalRead = 0;
        while (true) {
            int len = 0;
            int read = 0;
            int ch;
            while ((ch = inputStream.read()) != -1) {
                ++read;
                ++totalRead;
                if (ch == 10) {
                    break;
                }
                if (ch == 32) {
                    final ByteArrayOutputStream coll = new ByteArrayOutputStream();
                    while ((ch = inputStream.read()) != -1) {
                        ++read;
                        if (++totalRead < 0) {
                            break;
                        }
                        if (headerSize >= 0L && totalRead >= headerSize) {
                            break;
                        }
                        if (ch == 61) {
                            final String keyword = coll.toString("UTF-8");
                            final int restLen = len - read;
                            if (restLen <= 1) {
                                headers.remove(keyword);
                                break;
                            }
                            if (headerSize >= 0L && restLen > headerSize - totalRead) {
                                throw new IOException("Paxheader value size " + restLen + " exceeds size of header record");
                            }
                            final byte[] rest = IOUtils.readRange(inputStream, restLen);
                            final int got = rest.length;
                            if (got != restLen) {
                                throw new IOException("Failed to read Paxheader. Expected " + restLen + " bytes, read " + got);
                            }
                            totalRead += restLen;
                            if (rest[restLen - 1] != 10) {
                                throw new IOException("Failed to read Paxheader.Value should end with a newline");
                            }
                            final String value = new String(rest, 0, restLen - 1, StandardCharsets.UTF_8);
                            headers.put(keyword, value);
                            if (keyword.equals("GNU.sparse.offset")) {
                                if (offset != null) {
                                    sparseHeaders.add(new TarArchiveStructSparse(offset, 0L));
                                }
                                try {
                                    offset = Long.valueOf(value);
                                }
                                catch (final NumberFormatException ex) {
                                    throw new IOException("Failed to read Paxheader.GNU.sparse.offset contains a non-numeric value");
                                }
                                if (offset < 0L) {
                                    throw new IOException("Failed to read Paxheader.GNU.sparse.offset contains negative value");
                                }
                            }
                            if (keyword.equals("GNU.sparse.numbytes")) {
                                if (offset == null) {
                                    throw new IOException("Failed to read Paxheader.GNU.sparse.offset is expected before GNU.sparse.numbytes shows up.");
                                }
                                long numbytes;
                                try {
                                    numbytes = Long.parseLong(value);
                                }
                                catch (final NumberFormatException ex2) {
                                    throw new IOException("Failed to read Paxheader.GNU.sparse.numbytes contains a non-numeric value.");
                                }
                                if (numbytes < 0L) {
                                    throw new IOException("Failed to read Paxheader.GNU.sparse.numbytes contains negative value");
                                }
                                sparseHeaders.add(new TarArchiveStructSparse(offset, numbytes));
                                offset = null;
                            }
                            break;
                        }
                        else {
                            coll.write((byte)ch);
                        }
                    }
                    break;
                }
                if (ch < 48 || ch > 57) {
                    throw new IOException("Failed to read Paxheader. Encountered a non-number while reading length");
                }
                len *= 10;
                len += ch - 48;
            }
            if (ch == -1) {
                if (offset != null) {
                    sparseHeaders.add(new TarArchiveStructSparse(offset, 0L));
                }
                return headers;
            }
        }
    }
    
    @Deprecated
    protected static List<TarArchiveStructSparse> parsePAX01SparseHeaders(final String sparseMap) {
        try {
            return parseFromPAX01SparseHeaders(sparseMap);
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    protected static List<TarArchiveStructSparse> parseFromPAX01SparseHeaders(final String sparseMap) throws IOException {
        final List<TarArchiveStructSparse> sparseHeaders = new ArrayList<TarArchiveStructSparse>();
        final String[] sparseHeaderStrings = sparseMap.split(",");
        if (sparseHeaderStrings.length % 2 == 1) {
            throw new IOException("Corrupted TAR archive. Bad format in GNU.sparse.map PAX Header");
        }
        for (int i = 0; i < sparseHeaderStrings.length; i += 2) {
            long sparseOffset;
            try {
                sparseOffset = Long.parseLong(sparseHeaderStrings[i]);
            }
            catch (final NumberFormatException ex) {
                throw new IOException("Corrupted TAR archive. Sparse struct offset contains a non-numeric value");
            }
            if (sparseOffset < 0L) {
                throw new IOException("Corrupted TAR archive. Sparse struct offset contains negative value");
            }
            long sparseNumbytes;
            try {
                sparseNumbytes = Long.parseLong(sparseHeaderStrings[i + 1]);
            }
            catch (final NumberFormatException ex2) {
                throw new IOException("Corrupted TAR archive. Sparse struct numbytes contains a non-numeric value");
            }
            if (sparseNumbytes < 0L) {
                throw new IOException("Corrupted TAR archive. Sparse struct numbytes contains negative value");
            }
            sparseHeaders.add(new TarArchiveStructSparse(sparseOffset, sparseNumbytes));
        }
        return Collections.unmodifiableList((List<? extends TarArchiveStructSparse>)sparseHeaders);
    }
    
    protected static List<TarArchiveStructSparse> parsePAX1XSparseHeaders(final InputStream inputStream, final int recordSize) throws IOException {
        final List<TarArchiveStructSparse> sparseHeaders = new ArrayList<TarArchiveStructSparse>();
        long bytesRead = 0L;
        long[] readResult = readLineOfNumberForPax1X(inputStream);
        long sparseHeadersCount = readResult[0];
        if (sparseHeadersCount < 0L) {
            throw new IOException("Corrupted TAR archive. Negative value in sparse headers block");
        }
        bytesRead += readResult[1];
        while (sparseHeadersCount-- > 0L) {
            readResult = readLineOfNumberForPax1X(inputStream);
            final long sparseOffset = readResult[0];
            if (sparseOffset < 0L) {
                throw new IOException("Corrupted TAR archive. Sparse header block offset contains negative value");
            }
            bytesRead += readResult[1];
            readResult = readLineOfNumberForPax1X(inputStream);
            final long sparseNumbytes = readResult[0];
            if (sparseNumbytes < 0L) {
                throw new IOException("Corrupted TAR archive. Sparse header block numbytes contains negative value");
            }
            bytesRead += readResult[1];
            sparseHeaders.add(new TarArchiveStructSparse(sparseOffset, sparseNumbytes));
        }
        final long bytesToSkip = recordSize - bytesRead % recordSize;
        IOUtils.skip(inputStream, bytesToSkip);
        return sparseHeaders;
    }
    
    private static long[] readLineOfNumberForPax1X(final InputStream inputStream) throws IOException {
        long result = 0L;
        long bytesRead = 0L;
        int number;
        while ((number = inputStream.read()) != 10) {
            ++bytesRead;
            if (number == -1) {
                throw new IOException("Unexpected EOF when reading parse information of 1.X PAX format");
            }
            if (number < 48 || number > 57) {
                throw new IOException("Corrupted TAR archive. Non-numeric value in sparse headers block");
            }
            result = result * 10L + (number - 48);
        }
        ++bytesRead;
        return new long[] { result, bytesRead };
    }
    
    static {
        DEFAULT_ENCODING = ZipEncodingHelper.getZipEncoding(null);
        FALLBACK_ENCODING = new ZipEncoding() {
            @Override
            public boolean canEncode(final String name) {
                return true;
            }
            
            @Override
            public ByteBuffer encode(final String name) {
                final int length = name.length();
                final byte[] buf = new byte[length];
                for (int i = 0; i < length; ++i) {
                    buf[i] = (byte)name.charAt(i);
                }
                return ByteBuffer.wrap(buf);
            }
            
            @Override
            public String decode(final byte[] buffer) {
                final int length = buffer.length;
                final StringBuilder result = new StringBuilder(length);
                for (final byte b : buffer) {
                    if (b == 0) {
                        break;
                    }
                    result.append((char)(b & 0xFF));
                }
                return result.toString();
            }
        };
    }
}
