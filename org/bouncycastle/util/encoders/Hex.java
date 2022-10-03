package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.util.Strings;

public class Hex
{
    private static final Encoder encoder;
    
    public static String toHexString(final byte[] array) {
        return toHexString(array, 0, array.length);
    }
    
    public static String toHexString(final byte[] array, final int n, final int n2) {
        return Strings.fromByteArray(encode(array, n, n2));
    }
    
    public static byte[] encode(final byte[] array) {
        return encode(array, 0, array.length);
    }
    
    public static byte[] encode(final byte[] array, final int n, final int n2) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            Hex.encoder.encode(array, n, n2, byteArrayOutputStream);
        }
        catch (final Exception ex) {
            throw new EncoderException("exception encoding Hex string: " + ex.getMessage(), ex);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public static int encode(final byte[] array, final OutputStream outputStream) throws IOException {
        return Hex.encoder.encode(array, 0, array.length, outputStream);
    }
    
    public static int encode(final byte[] array, final int n, final int n2, final OutputStream outputStream) throws IOException {
        return Hex.encoder.encode(array, n, n2, outputStream);
    }
    
    public static byte[] decode(final byte[] array) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            Hex.encoder.decode(array, 0, array.length, byteArrayOutputStream);
        }
        catch (final Exception ex) {
            throw new DecoderException("exception decoding Hex data: " + ex.getMessage(), ex);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public static byte[] decode(final String s) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            Hex.encoder.decode(s, byteArrayOutputStream);
        }
        catch (final Exception ex) {
            throw new DecoderException("exception decoding Hex string: " + ex.getMessage(), ex);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public static int decode(final String s, final OutputStream outputStream) throws IOException {
        return Hex.encoder.decode(s, outputStream);
    }
    
    static {
        encoder = new HexEncoder();
    }
}
