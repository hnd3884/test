package org.apache.lucene.document;

import java.util.zip.Inflater;
import java.util.zip.DataFormatException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.UnicodeUtil;
import java.util.zip.Deflater;
import java.io.ByteArrayOutputStream;

public class CompressionTools
{
    private CompressionTools() {
    }
    
    public static byte[] compress(final byte[] value, final int offset, final int length, final int compressionLevel) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        final Deflater compressor = new Deflater();
        try {
            compressor.setLevel(compressionLevel);
            compressor.setInput(value, offset, length);
            compressor.finish();
            final byte[] buf = new byte[1024];
            while (!compressor.finished()) {
                final int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }
        }
        finally {
            compressor.end();
        }
        return bos.toByteArray();
    }
    
    public static byte[] compress(final byte[] value, final int offset, final int length) {
        return compress(value, offset, length, 9);
    }
    
    public static byte[] compress(final byte[] value) {
        return compress(value, 0, value.length, 9);
    }
    
    public static byte[] compressString(final String value) {
        return compressString(value, 9);
    }
    
    public static byte[] compressString(final String value, final int compressionLevel) {
        final byte[] b = new byte[3 * value.length()];
        final int len = UnicodeUtil.UTF16toUTF8(value, 0, value.length(), b);
        return compress(b, 0, len, compressionLevel);
    }
    
    public static byte[] decompress(final BytesRef bytes) throws DataFormatException {
        return decompress(bytes.bytes, bytes.offset, bytes.length);
    }
    
    public static byte[] decompress(final byte[] value) throws DataFormatException {
        return decompress(value, 0, value.length);
    }
    
    public static byte[] decompress(final byte[] value, final int offset, final int length) throws DataFormatException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        final Inflater decompressor = new Inflater();
        try {
            decompressor.setInput(value, offset, length);
            final byte[] buf = new byte[1024];
            while (!decompressor.finished()) {
                final int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }
        }
        finally {
            decompressor.end();
        }
        return bos.toByteArray();
    }
    
    public static String decompressString(final byte[] value) throws DataFormatException {
        return decompressString(value, 0, value.length);
    }
    
    public static String decompressString(final byte[] value, final int offset, final int length) throws DataFormatException {
        final byte[] bytes = decompress(value, offset, length);
        final char[] result = new char[bytes.length];
        final int len = UnicodeUtil.UTF8toUTF16(bytes, 0, bytes.length, result);
        return new String(result, 0, len);
    }
    
    public static String decompressString(final BytesRef bytes) throws DataFormatException {
        return decompressString(bytes.bytes, bytes.offset, bytes.length);
    }
}
