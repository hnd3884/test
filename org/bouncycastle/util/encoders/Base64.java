package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.util.Strings;

public class Base64
{
    private static final Encoder encoder;
    
    public static String toBase64String(final byte[] array) {
        return toBase64String(array, 0, array.length);
    }
    
    public static String toBase64String(final byte[] array, final int n, final int n2) {
        return Strings.fromByteArray(encode(array, n, n2));
    }
    
    public static byte[] encode(final byte[] array) {
        return encode(array, 0, array.length);
    }
    
    public static byte[] encode(final byte[] array, final int n, final int n2) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((n2 + 2) / 3 * 4);
        try {
            Base64.encoder.encode(array, n, n2, byteArrayOutputStream);
        }
        catch (final Exception ex) {
            throw new EncoderException("exception encoding base64 string: " + ex.getMessage(), ex);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public static int encode(final byte[] array, final OutputStream outputStream) throws IOException {
        return Base64.encoder.encode(array, 0, array.length, outputStream);
    }
    
    public static int encode(final byte[] array, final int n, final int n2, final OutputStream outputStream) throws IOException {
        return Base64.encoder.encode(array, n, n2, outputStream);
    }
    
    public static byte[] decode(final byte[] array) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(array.length / 4 * 3);
        try {
            Base64.encoder.decode(array, 0, array.length, byteArrayOutputStream);
        }
        catch (final Exception ex) {
            throw new DecoderException("unable to decode base64 data: " + ex.getMessage(), ex);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public static byte[] decode(final String s) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(s.length() / 4 * 3);
        try {
            Base64.encoder.decode(s, byteArrayOutputStream);
        }
        catch (final Exception ex) {
            throw new DecoderException("unable to decode base64 string: " + ex.getMessage(), ex);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public static int decode(final String s, final OutputStream outputStream) throws IOException {
        return Base64.encoder.decode(s, outputStream);
    }
    
    public static int decode(final byte[] array, final int n, final int n2, final OutputStream outputStream) {
        try {
            return Base64.encoder.decode(array, n, n2, outputStream);
        }
        catch (final Exception ex) {
            throw new DecoderException("unable to decode base64 data: " + ex.getMessage(), ex);
        }
    }
    
    static {
        encoder = new Base64Encoder();
    }
}
