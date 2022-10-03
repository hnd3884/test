package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class UrlBase64
{
    private static final Encoder encoder;
    
    public static byte[] encode(final byte[] array) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            UrlBase64.encoder.encode(array, 0, array.length, byteArrayOutputStream);
        }
        catch (final Exception ex) {
            throw new EncoderException("exception encoding URL safe base64 data: " + ex.getMessage(), ex);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public static int encode(final byte[] array, final OutputStream outputStream) throws IOException {
        return UrlBase64.encoder.encode(array, 0, array.length, outputStream);
    }
    
    public static byte[] decode(final byte[] array) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            UrlBase64.encoder.decode(array, 0, array.length, byteArrayOutputStream);
        }
        catch (final Exception ex) {
            throw new DecoderException("exception decoding URL safe base64 string: " + ex.getMessage(), ex);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public static int decode(final byte[] array, final OutputStream outputStream) throws IOException {
        return UrlBase64.encoder.decode(array, 0, array.length, outputStream);
    }
    
    public static byte[] decode(final String s) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            UrlBase64.encoder.decode(s, byteArrayOutputStream);
        }
        catch (final Exception ex) {
            throw new DecoderException("exception decoding URL safe base64 string: " + ex.getMessage(), ex);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public static int decode(final String s, final OutputStream outputStream) throws IOException {
        return UrlBase64.encoder.decode(s, outputStream);
    }
    
    static {
        encoder = new UrlBase64Encoder();
    }
}
