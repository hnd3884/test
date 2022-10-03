package com.dd.plist;

import java.io.FilterOutputStream;
import java.io.FilterInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;

public class Base64
{
    public static final int NO_OPTIONS = 0;
    public static final int ENCODE = 1;
    public static final int DECODE = 0;
    public static final int GZIP = 2;
    public static final int DONT_GUNZIP = 4;
    public static final int DO_BREAK_LINES = 8;
    public static final int URL_SAFE = 16;
    public static final int ORDERED = 32;
    private static final int MAX_LINE_LENGTH = 76;
    private static final byte EQUALS_SIGN = 61;
    private static final byte NEW_LINE = 10;
    private static final String PREFERRED_ENCODING = "US-ASCII";
    private static final byte WHITE_SPACE_ENC = -5;
    private static final byte EQUALS_SIGN_ENC = -1;
    private static final byte[] _STANDARD_ALPHABET;
    private static final byte[] _STANDARD_DECODABET;
    private static final byte[] _URL_SAFE_ALPHABET;
    private static final byte[] _URL_SAFE_DECODABET;
    private static final byte[] _ORDERED_ALPHABET;
    private static final byte[] _ORDERED_DECODABET;
    
    private Base64() {
    }
    
    private static byte[] getAlphabet(final int options) {
        if ((options & 0x10) == 0x10) {
            return Base64._URL_SAFE_ALPHABET;
        }
        if ((options & 0x20) == 0x20) {
            return Base64._ORDERED_ALPHABET;
        }
        return Base64._STANDARD_ALPHABET;
    }
    
    private static byte[] getDecodabet(final int options) {
        if ((options & 0x10) == 0x10) {
            return Base64._URL_SAFE_DECODABET;
        }
        if ((options & 0x20) == 0x20) {
            return Base64._ORDERED_DECODABET;
        }
        return Base64._STANDARD_DECODABET;
    }
    
    private static byte[] encode3to4(final byte[] b4, final byte[] threeBytes, final int numSigBytes, final int options) {
        encode3to4(threeBytes, 0, numSigBytes, b4, 0, options);
        return b4;
    }
    
    private static byte[] encode3to4(final byte[] source, final int srcOffset, final int numSigBytes, final byte[] destination, final int destOffset, final int options) {
        final byte[] ALPHABET = getAlphabet(options);
        final int inBuff = ((numSigBytes > 0) ? (source[srcOffset] << 24 >>> 8) : 0) | ((numSigBytes > 1) ? (source[srcOffset + 1] << 24 >>> 16) : 0) | ((numSigBytes > 2) ? (source[srcOffset + 2] << 24 >>> 24) : 0);
        switch (numSigBytes) {
            case 3: {
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 0x3F];
                destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 0x3F];
                destination[destOffset + 3] = ALPHABET[inBuff & 0x3F];
                return destination;
            }
            case 2: {
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 0x3F];
                destination[destOffset + 2] = ALPHABET[inBuff >>> 6 & 0x3F];
                destination[destOffset + 3] = 61;
                return destination;
            }
            case 1: {
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + 1] = ALPHABET[inBuff >>> 12 & 0x3F];
                destination[destOffset + 3] = (destination[destOffset + 2] = 61);
                return destination;
            }
            default: {
                return destination;
            }
        }
    }
    
    public static void encode(final ByteBuffer raw, final ByteBuffer encoded) {
        final byte[] raw2 = new byte[3];
        final byte[] enc4 = new byte[4];
        while (raw.hasRemaining()) {
            final int rem = Math.min(3, raw.remaining());
            raw.get(raw2, 0, rem);
            encode3to4(enc4, raw2, rem, 0);
            encoded.put(enc4);
        }
    }
    
    public static void encode(final ByteBuffer raw, final CharBuffer encoded) {
        final byte[] raw2 = new byte[3];
        final byte[] enc4 = new byte[4];
        while (raw.hasRemaining()) {
            final int rem = Math.min(3, raw.remaining());
            raw.get(raw2, 0, rem);
            encode3to4(enc4, raw2, rem, 0);
            for (int i = 0; i < 4; ++i) {
                encoded.put((char)(enc4[i] & 0xFF));
            }
        }
    }
    
    public static String encodeObject(final Serializable serializableObject) throws IOException {
        return encodeObject(serializableObject, 0);
    }
    
    public static String encodeObject(final Serializable serializableObject, final int options) throws IOException {
        if (serializableObject == null) {
            throw new NullPointerException("Cannot serialize a null object.");
        }
        ByteArrayOutputStream baos = null;
        OutputStream b64os = null;
        GZIPOutputStream gzos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            b64os = new B64OutputStream(baos, 0x1 | options);
            if ((options & 0x2) != 0x0) {
                gzos = new GZIPOutputStream(b64os);
                oos = new ObjectOutputStream(gzos);
            }
            else {
                oos = new ObjectOutputStream(b64os);
            }
            oos.writeObject(serializableObject);
        }
        catch (final IOException e) {
            throw e;
        }
        finally {
            try {
                oos.close();
            }
            catch (final Exception ex) {}
            try {
                gzos.close();
            }
            catch (final Exception ex2) {}
            try {
                b64os.close();
            }
            catch (final Exception ex3) {}
            try {
                baos.close();
            }
            catch (final Exception ex4) {}
        }
        try {
            return new String(baos.toByteArray(), "US-ASCII");
        }
        catch (final UnsupportedEncodingException uue) {
            return new String(baos.toByteArray());
        }
    }
    
    public static String encodeBytes(final byte[] source) {
        String encoded = null;
        try {
            encoded = encodeBytes(source, 0, source.length, 0);
        }
        catch (final IOException ex) {
            assert false : ex.getMessage();
        }
        assert encoded != null;
        return encoded;
    }
    
    public static String encodeBytes(final byte[] source, final int options) throws IOException {
        return encodeBytes(source, 0, source.length, options);
    }
    
    public static String encodeBytes(final byte[] source, final int off, final int len) {
        String encoded = null;
        try {
            encoded = encodeBytes(source, off, len, 0);
        }
        catch (final IOException ex) {
            assert false : ex.getMessage();
        }
        assert encoded != null;
        return encoded;
    }
    
    public static String encodeBytes(final byte[] source, final int off, final int len, final int options) throws IOException {
        final byte[] encoded = encodeBytesToBytes(source, off, len, options);
        try {
            return new String(encoded, "US-ASCII");
        }
        catch (final UnsupportedEncodingException uue) {
            return new String(encoded);
        }
    }
    
    public static byte[] encodeBytesToBytes(final byte[] source) {
        byte[] encoded = null;
        try {
            encoded = encodeBytesToBytes(source, 0, source.length, 0);
        }
        catch (final IOException ex) {
            assert false : "IOExceptions only come from GZipping, which is turned off: " + ex.getMessage();
        }
        return encoded;
    }
    
    public static byte[] encodeBytesToBytes(final byte[] source, final int off, final int len, final int options) throws IOException {
        if (source == null) {
            throw new NullPointerException("Cannot serialize a null array.");
        }
        if (off < 0) {
            throw new IllegalArgumentException("Cannot have negative offset: " + off);
        }
        if (len < 0) {
            throw new IllegalArgumentException("Cannot have length offset: " + len);
        }
        if (off + len > source.length) {
            throw new IllegalArgumentException(String.format("Cannot have offset of %d and length of %d with array of length %d", off, len, source.length));
        }
        if ((options & 0x2) != 0x0) {
            ByteArrayOutputStream baos = null;
            GZIPOutputStream gzos = null;
            B64OutputStream b64os = null;
            try {
                baos = new ByteArrayOutputStream();
                b64os = new B64OutputStream(baos, 0x1 | options);
                gzos = new GZIPOutputStream(b64os);
                gzos.write(source, off, len);
                gzos.close();
            }
            catch (final IOException e) {
                throw e;
            }
            finally {
                try {
                    gzos.close();
                }
                catch (final Exception ex) {}
                try {
                    b64os.close();
                }
                catch (final Exception ex2) {}
                try {
                    baos.close();
                }
                catch (final Exception ex3) {}
            }
            return baos.toByteArray();
        }
        final boolean breakLines = (options & 0x8) != 0x0;
        int encLen = len / 3 * 4 + ((len % 3 > 0) ? 4 : 0);
        if (breakLines) {
            encLen += encLen / 76;
        }
        final byte[] outBuff = new byte[encLen];
        int d = 0;
        int e2 = 0;
        final int len2 = len - 2;
        int lineLength = 0;
        while (d < len2) {
            encode3to4(source, d + off, 3, outBuff, e2, options);
            lineLength += 4;
            if (breakLines && lineLength >= 76) {
                outBuff[e2 + 4] = 10;
                ++e2;
                lineLength = 0;
            }
            d += 3;
            e2 += 4;
        }
        if (d < len) {
            encode3to4(source, d + off, len - d, outBuff, e2, options);
            e2 += 4;
        }
        if (e2 <= outBuff.length - 1) {
            final byte[] finalOut = new byte[e2];
            System.arraycopy(outBuff, 0, finalOut, 0, e2);
            return finalOut;
        }
        return outBuff;
    }
    
    private static int decode4to3(final byte[] source, final int srcOffset, final byte[] destination, final int destOffset, final int options) {
        if (source == null) {
            throw new NullPointerException("Source array was null.");
        }
        if (destination == null) {
            throw new NullPointerException("Destination array was null.");
        }
        if (srcOffset < 0 || srcOffset + 3 >= source.length) {
            throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and still process four bytes.", source.length, srcOffset));
        }
        if (destOffset < 0 || destOffset + 2 >= destination.length) {
            throw new IllegalArgumentException(String.format("Destination array with length %d cannot have offset of %d and still store three bytes.", destination.length, destOffset));
        }
        final byte[] DECODABET = getDecodabet(options);
        if (source[srcOffset + 2] == 61) {
            final int outBuff = (DECODABET[source[srcOffset]] & 0xFF) << 18 | (DECODABET[source[srcOffset + 1]] & 0xFF) << 12;
            destination[destOffset] = (byte)(outBuff >>> 16);
            return 1;
        }
        if (source[srcOffset + 3] == 61) {
            final int outBuff = (DECODABET[source[srcOffset]] & 0xFF) << 18 | (DECODABET[source[srcOffset + 1]] & 0xFF) << 12 | (DECODABET[source[srcOffset + 2]] & 0xFF) << 6;
            destination[destOffset] = (byte)(outBuff >>> 16);
            destination[destOffset + 1] = (byte)(outBuff >>> 8);
            return 2;
        }
        final int outBuff = (DECODABET[source[srcOffset]] & 0xFF) << 18 | (DECODABET[source[srcOffset + 1]] & 0xFF) << 12 | (DECODABET[source[srcOffset + 2]] & 0xFF) << 6 | (DECODABET[source[srcOffset + 3]] & 0xFF);
        destination[destOffset] = (byte)(outBuff >> 16);
        destination[destOffset + 1] = (byte)(outBuff >> 8);
        destination[destOffset + 2] = (byte)outBuff;
        return 3;
    }
    
    public static byte[] decode(final byte[] source) throws IOException {
        final byte[] decoded = decode(source, 0, source.length, 0);
        return decoded;
    }
    
    public static byte[] decode(final byte[] source, final int off, final int len, final int options) throws IOException {
        if (source == null) {
            throw new NullPointerException("Cannot decode null source array.");
        }
        if (off < 0 || off + len > source.length) {
            throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and process %d bytes.", source.length, off, len));
        }
        if (len == 0) {
            return new byte[0];
        }
        if (len < 4) {
            throw new IllegalArgumentException("Base64-encoded string must have at least four characters, but length specified was " + len);
        }
        final byte[] DECODABET = getDecodabet(options);
        final int len2 = len * 3 / 4;
        final byte[] outBuff = new byte[len2];
        int outBuffPosn = 0;
        final byte[] b4 = new byte[4];
        int b4Posn = 0;
        for (int i = off; i < off + len; ++i) {
            final byte sbiDecode = DECODABET[source[i] & 0xFF];
            if (sbiDecode < -5) {
                throw new IOException(String.format("Bad Base64 input character decimal %d in array position %d", source[i] & 0xFF, i));
            }
            if (sbiDecode >= -1) {
                b4[b4Posn++] = source[i];
                if (b4Posn > 3) {
                    outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, options);
                    b4Posn = 0;
                    if (source[i] == 61) {
                        break;
                    }
                }
            }
        }
        final byte[] out = new byte[outBuffPosn];
        System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
        return out;
    }
    
    public static byte[] decode(final String s) throws IOException {
        return decode(s, 0);
    }
    
    public static byte[] decode(final String s, final int options) throws IOException {
        if (s == null) {
            throw new NullPointerException("Input string was null.");
        }
        byte[] bytes;
        try {
            bytes = s.getBytes("US-ASCII");
        }
        catch (final UnsupportedEncodingException uee) {
            bytes = s.getBytes();
        }
        bytes = decode(bytes, 0, bytes.length, options);
        final boolean dontGunzip = (options & 0x4) != 0x0;
        if (bytes != null && bytes.length >= 4 && !dontGunzip) {
            final int head = (bytes[0] & 0xFF) | (bytes[1] << 8 & 0xFF00);
            if (35615 == head) {
                ByteArrayInputStream bais = null;
                GZIPInputStream gzis = null;
                ByteArrayOutputStream baos = null;
                final byte[] buffer = new byte[2048];
                int length = 0;
                try {
                    baos = new ByteArrayOutputStream();
                    bais = new ByteArrayInputStream(bytes);
                    gzis = new GZIPInputStream(bais);
                    while ((length = gzis.read(buffer)) >= 0) {
                        baos.write(buffer, 0, length);
                    }
                    bytes = baos.toByteArray();
                }
                catch (final IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        baos.close();
                    }
                    catch (final Exception ex) {}
                    try {
                        gzis.close();
                    }
                    catch (final Exception ex2) {}
                    try {
                        bais.close();
                    }
                    catch (final Exception ex3) {}
                }
            }
        }
        return bytes;
    }
    
    public static Object decodeToObject(final String encodedObject) throws IOException, ClassNotFoundException {
        return decodeToObject(encodedObject, 0, null);
    }
    
    public static Object decodeToObject(final String encodedObject, final int options, final ClassLoader loader) throws IOException, ClassNotFoundException {
        final byte[] objBytes = decode(encodedObject, options);
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        Object obj = null;
        try {
            bais = new ByteArrayInputStream(objBytes);
            if (loader == null) {
                ois = new ObjectInputStream(bais);
            }
            else {
                ois = new ObjectInputStream(bais) {
                    public Class<?> resolveClass(final ObjectStreamClass streamClass) throws IOException, ClassNotFoundException {
                        final Class c = Class.forName(streamClass.getName(), false, loader);
                        if (c == null) {
                            return super.resolveClass(streamClass);
                        }
                        return c;
                    }
                };
            }
            obj = ois.readObject();
        }
        catch (final IOException e) {
            throw e;
        }
        catch (final ClassNotFoundException e2) {
            throw e2;
        }
        finally {
            try {
                bais.close();
            }
            catch (final Exception ex) {}
            try {
                ois.close();
            }
            catch (final Exception ex2) {}
        }
        return obj;
    }
    
    public static void encodeToFile(final byte[] dataToEncode, final String filename) throws IOException {
        if (dataToEncode == null) {
            throw new NullPointerException("Data to encode was null.");
        }
        B64OutputStream bos = null;
        try {
            bos = new B64OutputStream(new FileOutputStream(filename), 1);
            bos.write(dataToEncode);
        }
        catch (final IOException e) {
            throw e;
        }
        finally {
            try {
                bos.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    public static void decodeToFile(final String dataToDecode, final String filename) throws IOException {
        B64OutputStream bos = null;
        try {
            bos = new B64OutputStream(new FileOutputStream(filename), 0);
            bos.write(dataToDecode.getBytes("US-ASCII"));
        }
        catch (final IOException e) {
            throw e;
        }
        finally {
            try {
                bos.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    public static byte[] decodeFromFile(final String filename) throws IOException {
        byte[] decodedData = null;
        B64InputStream bis = null;
        try {
            final File file = new File(filename);
            byte[] buffer = null;
            int length = 0;
            int numBytes = 0;
            if (file.length() > 2147483647L) {
                throw new IOException("File is too big for this convenience method (" + file.length() + " bytes).");
            }
            for (buffer = new byte[(int)file.length()], bis = new B64InputStream(new BufferedInputStream(new FileInputStream(file)), 0); (numBytes = bis.read(buffer, length, 4096)) >= 0; length += numBytes) {}
            decodedData = new byte[length];
            System.arraycopy(buffer, 0, decodedData, 0, length);
        }
        catch (final IOException e) {
            throw e;
        }
        finally {
            try {
                bis.close();
            }
            catch (final Exception ex) {}
        }
        return decodedData;
    }
    
    public static String encodeFromFile(final String filename) throws IOException {
        String encodedData = null;
        B64InputStream bis = null;
        try {
            final File file = new File(filename);
            byte[] buffer;
            int length;
            int numBytes;
            for (buffer = new byte[Math.max((int)(file.length() * 1.4 + 1.0), 40)], length = 0, numBytes = 0, bis = new B64InputStream(new BufferedInputStream(new FileInputStream(file)), 1); (numBytes = bis.read(buffer, length, 4096)) >= 0; length += numBytes) {}
            encodedData = new String(buffer, 0, length, "US-ASCII");
        }
        catch (final IOException e) {
            throw e;
        }
        finally {
            try {
                bis.close();
            }
            catch (final Exception ex) {}
        }
        return encodedData;
    }
    
    public static void encodeFileToFile(final String infile, final String outfile) throws IOException {
        final String encoded = encodeFromFile(infile);
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(outfile));
            out.write(encoded.getBytes("US-ASCII"));
        }
        catch (final IOException e) {
            throw e;
        }
        finally {
            try {
                out.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    public static void decodeFileToFile(final String infile, final String outfile) throws IOException {
        final byte[] decoded = decodeFromFile(infile);
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(outfile));
            out.write(decoded);
        }
        catch (final IOException e) {
            throw e;
        }
        finally {
            try {
                out.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    static {
        _STANDARD_ALPHABET = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
        _STANDARD_DECODABET = new byte[] { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9 };
        _URL_SAFE_ALPHABET = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
        _URL_SAFE_DECODABET = new byte[] { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, 63, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9 };
        _ORDERED_ALPHABET = new byte[] { 45, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 95, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122 };
        _ORDERED_DECODABET = new byte[] { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 0, -9, -9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, -9, -9, -9, -1, -9, -9, -9, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, -9, -9, -9, -9, 37, -9, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9 };
    }
    
    public static class B64InputStream extends FilterInputStream
    {
        private boolean encode;
        private int position;
        private byte[] buffer;
        private int bufferLength;
        private int numSigBytes;
        private int lineLength;
        private boolean breakLines;
        private int options;
        private byte[] decodabet;
        
        public B64InputStream(final InputStream in) {
            this(in, 0);
        }
        
        public B64InputStream(final InputStream in, final int options) {
            super(in);
            this.options = options;
            this.breakLines = ((options & 0x8) > 0);
            this.encode = ((options & 0x1) > 0);
            this.bufferLength = (this.encode ? 4 : 3);
            this.buffer = new byte[this.bufferLength];
            this.position = -1;
            this.lineLength = 0;
            this.decodabet = getDecodabet(options);
        }
        
        @Override
        public int read() throws IOException {
            if (this.position < 0) {
                if (this.encode) {
                    final byte[] b3 = new byte[3];
                    int numBinaryBytes = 0;
                    for (int i = 0; i < 3; ++i) {
                        final int b4 = this.in.read();
                        if (b4 < 0) {
                            break;
                        }
                        b3[i] = (byte)b4;
                        ++numBinaryBytes;
                    }
                    if (numBinaryBytes <= 0) {
                        return -1;
                    }
                    encode3to4(b3, 0, numBinaryBytes, this.buffer, 0, this.options);
                    this.position = 0;
                    this.numSigBytes = 4;
                }
                else {
                    final byte[] b5 = new byte[4];
                    int j;
                    for (j = 0; j < 4; ++j) {
                        int b6;
                        do {
                            b6 = this.in.read();
                        } while (b6 >= 0 && this.decodabet[b6 & 0x7F] <= -5);
                        if (b6 < 0) {
                            break;
                        }
                        b5[j] = (byte)b6;
                    }
                    if (j == 4) {
                        this.numSigBytes = decode4to3(b5, 0, this.buffer, 0, this.options);
                        this.position = 0;
                    }
                    else {
                        if (j == 0) {
                            return -1;
                        }
                        throw new IOException("Improperly padded Base64 input.");
                    }
                }
            }
            if (this.position < 0) {
                throw new IOException("Error in Base64 code reading stream.");
            }
            if (this.position >= this.numSigBytes) {
                return -1;
            }
            if (this.encode && this.breakLines && this.lineLength >= 76) {
                this.lineLength = 0;
                return 10;
            }
            ++this.lineLength;
            final int b7 = this.buffer[this.position++];
            if (this.position >= this.bufferLength) {
                this.position = -1;
            }
            return b7 & 0xFF;
        }
        
        @Override
        public int read(final byte[] dest, final int off, final int len) throws IOException {
            int i = 0;
            while (i < len) {
                final int b = this.read();
                if (b >= 0) {
                    dest[off + i] = (byte)b;
                    ++i;
                }
                else {
                    if (i == 0) {
                        return -1;
                    }
                    break;
                }
            }
            return i;
        }
    }
    
    public static class B64OutputStream extends FilterOutputStream
    {
        private boolean encode;
        private int position;
        private byte[] buffer;
        private int bufferLength;
        private int lineLength;
        private boolean breakLines;
        private byte[] b4;
        private boolean suspendEncoding;
        private int options;
        private byte[] decodabet;
        
        public B64OutputStream(final OutputStream out) {
            this(out, 1);
        }
        
        public B64OutputStream(final OutputStream out, final int options) {
            super(out);
            this.breakLines = ((options & 0x8) != 0x0);
            this.encode = ((options & 0x1) != 0x0);
            this.bufferLength = (this.encode ? 3 : 4);
            this.buffer = new byte[this.bufferLength];
            this.position = 0;
            this.lineLength = 0;
            this.suspendEncoding = false;
            this.b4 = new byte[4];
            this.options = options;
            this.decodabet = getDecodabet(options);
        }
        
        @Override
        public void write(final int theByte) throws IOException {
            if (this.suspendEncoding) {
                this.out.write(theByte);
                return;
            }
            if (this.encode) {
                this.buffer[this.position++] = (byte)theByte;
                if (this.position >= this.bufferLength) {
                    this.out.write(encode3to4(this.b4, this.buffer, this.bufferLength, this.options));
                    this.lineLength += 4;
                    if (this.breakLines && this.lineLength >= 76) {
                        this.out.write(10);
                        this.lineLength = 0;
                    }
                    this.position = 0;
                }
            }
            else if (this.decodabet[theByte & 0x7F] > -5) {
                this.buffer[this.position++] = (byte)theByte;
                if (this.position >= this.bufferLength) {
                    final int len = decode4to3(this.buffer, 0, this.b4, 0, this.options);
                    this.out.write(this.b4, 0, len);
                    this.position = 0;
                }
            }
            else if (this.decodabet[theByte & 0x7F] != -5) {
                throw new IOException("Invalid character in Base64 data.");
            }
        }
        
        @Override
        public void write(final byte[] theBytes, final int off, final int len) throws IOException {
            if (this.suspendEncoding) {
                this.out.write(theBytes, off, len);
                return;
            }
            for (int i = 0; i < len; ++i) {
                this.write(theBytes[off + i]);
            }
        }
        
        public void flushBase64() throws IOException {
            if (this.position > 0) {
                if (!this.encode) {
                    throw new IOException("Base64 input not properly padded.");
                }
                this.out.write(encode3to4(this.b4, this.buffer, this.position, this.options));
                this.position = 0;
            }
        }
        
        @Override
        public void close() throws IOException {
            this.flushBase64();
            super.close();
            this.buffer = null;
            this.out = null;
        }
        
        public void suspendEncoding() throws IOException {
            this.flushBase64();
            this.suspendEncoding = true;
        }
        
        public void resumeEncoding() {
            this.suspendEncoding = false;
        }
    }
}
