package com.sun.org.apache.xml.internal.security.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import org.w3c.dom.Element;
import java.math.BigInteger;

@Deprecated
public class Base64
{
    public static final int BASE64DEFAULTLENGTH = 76;
    private static final int BASELENGTH = 255;
    private static final int LOOKUPLENGTH = 64;
    private static final int TWENTYFOURBITGROUP = 24;
    private static final int EIGHTBIT = 8;
    private static final int SIXTEENBIT = 16;
    private static final int FOURBYTE = 4;
    private static final int SIGN = -128;
    private static final char PAD = '=';
    private static final byte[] base64Alphabet;
    private static final char[] lookUpBase64Alphabet;
    
    private Base64() {
    }
    
    static final byte[] getBytes(final BigInteger bigInteger, int n) {
        n = n + 7 >> 3 << 3;
        if (n < bigInteger.bitLength()) {
            throw new IllegalArgumentException(I18n.translate("utils.Base64.IllegalBitlength"));
        }
        final byte[] byteArray = bigInteger.toByteArray();
        if (bigInteger.bitLength() % 8 != 0 && bigInteger.bitLength() / 8 + 1 == n / 8) {
            return byteArray;
        }
        int n2 = 0;
        int length = byteArray.length;
        if (bigInteger.bitLength() % 8 == 0) {
            n2 = 1;
            --length;
        }
        final int n3 = n / 8 - length;
        final byte[] array = new byte[n / 8];
        System.arraycopy(byteArray, n2, array, n3, length);
        return array;
    }
    
    public static final String encode(final BigInteger bigInteger) {
        return XMLUtils.encodeToString(XMLUtils.getBytes(bigInteger, bigInteger.bitLength()));
    }
    
    public static final byte[] encode(final BigInteger bigInteger, int n) {
        n = n + 7 >> 3 << 3;
        if (n < bigInteger.bitLength()) {
            throw new IllegalArgumentException(I18n.translate("utils.Base64.IllegalBitlength"));
        }
        final byte[] byteArray = bigInteger.toByteArray();
        if (bigInteger.bitLength() % 8 != 0 && bigInteger.bitLength() / 8 + 1 == n / 8) {
            return byteArray;
        }
        int n2 = 0;
        int length = byteArray.length;
        if (bigInteger.bitLength() % 8 == 0) {
            n2 = 1;
            --length;
        }
        final int n3 = n / 8 - length;
        final byte[] array = new byte[n / 8];
        System.arraycopy(byteArray, n2, array, n3, length);
        return array;
    }
    
    public static final BigInteger decodeBigIntegerFromElement(final Element element) throws Base64DecodingException {
        return new BigInteger(1, decode(element));
    }
    
    public static final BigInteger decodeBigIntegerFromText(final Text text) throws Base64DecodingException {
        return new BigInteger(1, decode(text.getData()));
    }
    
    public static final void fillElementWithBigInteger(final Element element, final BigInteger bigInteger) {
        String s = encode(bigInteger);
        if (!XMLUtils.ignoreLineBreaks() && s.length() > 76) {
            s = "\n" + s + "\n";
        }
        element.appendChild(element.getOwnerDocument().createTextNode(s));
    }
    
    public static final byte[] decode(final Element element) throws Base64DecodingException {
        Node node = element.getFirstChild();
        final StringBuffer sb = new StringBuffer();
        while (node != null) {
            if (node.getNodeType() == 3) {
                sb.append(((Text)node).getData());
            }
            node = node.getNextSibling();
        }
        return decode(sb.toString());
    }
    
    public static final Element encodeToElement(final Document document, final String s, final byte[] array) {
        final Element elementInSignatureSpace = XMLUtils.createElementInSignatureSpace(document, s);
        elementInSignatureSpace.appendChild(document.createTextNode(encode(array)));
        return elementInSignatureSpace;
    }
    
    public static final byte[] decode(final byte[] array) throws Base64DecodingException {
        return decodeInternal(array, -1);
    }
    
    public static final String encode(final byte[] array) {
        return XMLUtils.ignoreLineBreaks() ? encode(array, Integer.MAX_VALUE) : encode(array, 76);
    }
    
    public static final byte[] decode(final BufferedReader bufferedReader) throws IOException, Base64DecodingException {
        byte[] byteArray = null;
        final UnsyncByteArrayOutputStream unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
        try {
            String line;
            while (null != (line = bufferedReader.readLine())) {
                unsyncByteArrayOutputStream.write(decode(line));
            }
            byteArray = unsyncByteArrayOutputStream.toByteArray();
        }
        finally {
            unsyncByteArrayOutputStream.close();
        }
        return byteArray;
    }
    
    protected static final boolean isWhiteSpace(final byte b) {
        return b == 32 || b == 13 || b == 10 || b == 9;
    }
    
    protected static final boolean isPad(final byte b) {
        return b == 61;
    }
    
    public static final String encode(final byte[] array, int n) {
        if (n < 4) {
            n = Integer.MAX_VALUE;
        }
        if (array == null) {
            return null;
        }
        final long n2 = array.length * 8L;
        if (n2 == 0L) {
            return "";
        }
        final long n3 = n2 % 24L;
        final int n4 = (int)(n2 / 24L);
        final int n5 = (n3 != 0L) ? (n4 + 1) : n4;
        final int n6 = (n5 - 1) / (n / 4);
        final char[] array2 = new char[n5 * 4 + n6 * 2];
        int n7 = 0;
        int n8 = 0;
        int i = 0;
        for (int j = 0; j < n6; ++j) {
            for (int k = 0; k < 19; ++k) {
                final byte b = array[n8++];
                final byte b2 = array[n8++];
                final byte b3 = array[n8++];
                final byte b4 = (byte)(b2 & 0xF);
                final byte b5 = (byte)(b & 0x3);
                final byte b6 = ((b & 0xFFFFFF80) == 0x0) ? ((byte)(b >> 2)) : ((byte)(b >> 2 ^ 0xC0));
                final byte b7 = ((b2 & 0xFFFFFF80) == 0x0) ? ((byte)(b2 >> 4)) : ((byte)(b2 >> 4 ^ 0xF0));
                final byte b8 = ((b3 & 0xFFFFFF80) == 0x0) ? ((byte)(b3 >> 6)) : ((byte)(b3 >> 6 ^ 0xFC));
                array2[n7++] = Base64.lookUpBase64Alphabet[b6];
                array2[n7++] = Base64.lookUpBase64Alphabet[b7 | b5 << 4];
                array2[n7++] = Base64.lookUpBase64Alphabet[b4 << 2 | b8];
                array2[n7++] = Base64.lookUpBase64Alphabet[b3 & 0x3F];
                ++i;
            }
            array2[n7++] = '\r';
            array2[n7++] = '\n';
        }
        while (i < n4) {
            final byte b9 = array[n8++];
            final byte b10 = array[n8++];
            final byte b11 = array[n8++];
            final byte b12 = (byte)(b10 & 0xF);
            final byte b13 = (byte)(b9 & 0x3);
            final byte b14 = ((b9 & 0xFFFFFF80) == 0x0) ? ((byte)(b9 >> 2)) : ((byte)(b9 >> 2 ^ 0xC0));
            final byte b15 = ((b10 & 0xFFFFFF80) == 0x0) ? ((byte)(b10 >> 4)) : ((byte)(b10 >> 4 ^ 0xF0));
            final byte b16 = ((b11 & 0xFFFFFF80) == 0x0) ? ((byte)(b11 >> 6)) : ((byte)(b11 >> 6 ^ 0xFC));
            array2[n7++] = Base64.lookUpBase64Alphabet[b14];
            array2[n7++] = Base64.lookUpBase64Alphabet[b15 | b13 << 4];
            array2[n7++] = Base64.lookUpBase64Alphabet[b12 << 2 | b16];
            array2[n7++] = Base64.lookUpBase64Alphabet[b11 & 0x3F];
            ++i;
        }
        if (n3 == 8L) {
            final byte b17 = array[n8];
            final byte b18 = (byte)(b17 & 0x3);
            array2[n7++] = Base64.lookUpBase64Alphabet[((b17 & 0xFFFFFF80) == 0x0) ? ((byte)(b17 >> 2)) : ((byte)(b17 >> 2 ^ 0xC0))];
            array2[n7++] = Base64.lookUpBase64Alphabet[b18 << 4];
            array2[n7++] = '=';
            array2[n7++] = '=';
        }
        else if (n3 == 16L) {
            final byte b19 = array[n8];
            final byte b20 = array[n8 + 1];
            final byte b21 = (byte)(b20 & 0xF);
            final byte b22 = (byte)(b19 & 0x3);
            final byte b23 = ((b19 & 0xFFFFFF80) == 0x0) ? ((byte)(b19 >> 2)) : ((byte)(b19 >> 2 ^ 0xC0));
            final byte b24 = ((b20 & 0xFFFFFF80) == 0x0) ? ((byte)(b20 >> 4)) : ((byte)(b20 >> 4 ^ 0xF0));
            array2[n7++] = Base64.lookUpBase64Alphabet[b23];
            array2[n7++] = Base64.lookUpBase64Alphabet[b24 | b22 << 4];
            array2[n7++] = Base64.lookUpBase64Alphabet[b21 << 2];
            array2[n7++] = '=';
        }
        return new String(array2);
    }
    
    public static final byte[] decode(final String s) throws Base64DecodingException {
        if (s == null) {
            return null;
        }
        final byte[] array = new byte[s.length()];
        return decodeInternal(array, getBytesInternal(s, array));
    }
    
    protected static final int getBytesInternal(final String s, final byte[] array) {
        final int length = s.length();
        int n = 0;
        for (int i = 0; i < length; ++i) {
            final byte b = (byte)s.charAt(i);
            if (!isWhiteSpace(b)) {
                array[n++] = b;
            }
        }
        return n;
    }
    
    protected static final byte[] decodeInternal(final byte[] array, int removeWhiteSpace) throws Base64DecodingException {
        if (removeWhiteSpace == -1) {
            removeWhiteSpace = removeWhiteSpace(array);
        }
        if (removeWhiteSpace % 4 != 0) {
            throw new Base64DecodingException("decoding.divisible.four");
        }
        final int n = removeWhiteSpace / 4;
        if (n == 0) {
            return new byte[0];
        }
        int n2 = (n - 1) * 4;
        int n3 = (n - 1) * 3;
        final byte b = Base64.base64Alphabet[array[n2++]];
        final byte b2 = Base64.base64Alphabet[array[n2++]];
        if (b == -1 || b2 == -1) {
            throw new Base64DecodingException("decoding.general");
        }
        final byte b4;
        final byte b3 = Base64.base64Alphabet[b4 = array[n2++]];
        final byte b6;
        final byte b5 = Base64.base64Alphabet[b6 = array[n2++]];
        byte[] array2;
        if (b3 == -1 || b5 == -1) {
            if (isPad(b4) && isPad(b6)) {
                if ((b2 & 0xF) != 0x0) {
                    throw new Base64DecodingException("decoding.general");
                }
                array2 = new byte[n3 + 1];
                array2[n3] = (byte)(b << 2 | b2 >> 4);
            }
            else {
                if (isPad(b4) || !isPad(b6)) {
                    throw new Base64DecodingException("decoding.general");
                }
                if ((b3 & 0x3) != 0x0) {
                    throw new Base64DecodingException("decoding.general");
                }
                array2 = new byte[n3 + 2];
                array2[n3++] = (byte)(b << 2 | b2 >> 4);
                array2[n3] = (byte)((b2 & 0xF) << 4 | (b3 >> 2 & 0xF));
            }
        }
        else {
            array2 = new byte[n3 + 3];
            array2[n3++] = (byte)(b << 2 | b2 >> 4);
            array2[n3++] = (byte)((b2 & 0xF) << 4 | (b3 >> 2 & 0xF));
            array2[n3++] = (byte)(b3 << 6 | b5);
        }
        int n4 = 0;
        int n5 = 0;
        for (int i = n - 1; i > 0; --i) {
            final byte b7 = Base64.base64Alphabet[array[n5++]];
            final byte b8 = Base64.base64Alphabet[array[n5++]];
            final byte b9 = Base64.base64Alphabet[array[n5++]];
            final byte b10 = Base64.base64Alphabet[array[n5++]];
            if (b7 == -1 || b8 == -1 || b9 == -1 || b10 == -1) {
                throw new Base64DecodingException("decoding.general");
            }
            array2[n4++] = (byte)(b7 << 2 | b8 >> 4);
            array2[n4++] = (byte)((b8 & 0xF) << 4 | (b9 >> 2 & 0xF));
            array2[n4++] = (byte)(b9 << 6 | b10);
        }
        return array2;
    }
    
    public static final void decode(final String s, final OutputStream outputStream) throws Base64DecodingException, IOException {
        final byte[] array = new byte[s.length()];
        decode(array, outputStream, getBytesInternal(s, array));
    }
    
    public static final void decode(final byte[] array, final OutputStream outputStream) throws Base64DecodingException, IOException {
        decode(array, outputStream, -1);
    }
    
    protected static final void decode(final byte[] array, final OutputStream outputStream, int removeWhiteSpace) throws Base64DecodingException, IOException {
        if (removeWhiteSpace == -1) {
            removeWhiteSpace = removeWhiteSpace(array);
        }
        if (removeWhiteSpace % 4 != 0) {
            throw new Base64DecodingException("decoding.divisible.four");
        }
        final int n = removeWhiteSpace / 4;
        if (n == 0) {
            return;
        }
        int n2 = 0;
        for (int i = n - 1; i > 0; --i) {
            final byte b = Base64.base64Alphabet[array[n2++]];
            final byte b2 = Base64.base64Alphabet[array[n2++]];
            final byte b3 = Base64.base64Alphabet[array[n2++]];
            final byte b4 = Base64.base64Alphabet[array[n2++]];
            if (b == -1 || b2 == -1 || b3 == -1 || b4 == -1) {
                throw new Base64DecodingException("decoding.general");
            }
            outputStream.write((byte)(b << 2 | b2 >> 4));
            outputStream.write((byte)((b2 & 0xF) << 4 | (b3 >> 2 & 0xF)));
            outputStream.write((byte)(b3 << 6 | b4));
        }
        final byte b5 = Base64.base64Alphabet[array[n2++]];
        final byte b6 = Base64.base64Alphabet[array[n2++]];
        if (b5 == -1 || b6 == -1) {
            throw new Base64DecodingException("decoding.general");
        }
        final byte b8;
        final byte b7 = Base64.base64Alphabet[b8 = array[n2++]];
        final byte b10;
        final byte b9 = Base64.base64Alphabet[b10 = array[n2++]];
        if (b7 == -1 || b9 == -1) {
            if (isPad(b8) && isPad(b10)) {
                if ((b6 & 0xF) != 0x0) {
                    throw new Base64DecodingException("decoding.general");
                }
                outputStream.write((byte)(b5 << 2 | b6 >> 4));
            }
            else {
                if (isPad(b8) || !isPad(b10)) {
                    throw new Base64DecodingException("decoding.general");
                }
                if ((b7 & 0x3) != 0x0) {
                    throw new Base64DecodingException("decoding.general");
                }
                outputStream.write((byte)(b5 << 2 | b6 >> 4));
                outputStream.write((byte)((b6 & 0xF) << 4 | (b7 >> 2 & 0xF)));
            }
        }
        else {
            outputStream.write((byte)(b5 << 2 | b6 >> 4));
            outputStream.write((byte)((b6 & 0xF) << 4 | (b7 >> 2 & 0xF)));
            outputStream.write((byte)(b7 << 6 | b9));
        }
    }
    
    public static final void decode(final InputStream inputStream, final OutputStream outputStream) throws Base64DecodingException, IOException {
        int n = 0;
        final byte[] array = new byte[4];
        int read;
        while ((read = inputStream.read()) > 0) {
            final byte b = (byte)read;
            if (isWhiteSpace(b)) {
                continue;
            }
            if (isPad(b)) {
                array[n++] = b;
                if (n == 3) {
                    array[n++] = (byte)inputStream.read();
                    break;
                }
                break;
            }
            else {
                if ((array[n++] = b) == -1) {
                    throw new Base64DecodingException("decoding.general");
                }
                if (n != 4) {
                    continue;
                }
                n = 0;
                final byte b2 = Base64.base64Alphabet[array[0]];
                final byte b3 = Base64.base64Alphabet[array[1]];
                final byte b4 = Base64.base64Alphabet[array[2]];
                final byte b5 = Base64.base64Alphabet[array[3]];
                outputStream.write((byte)(b2 << 2 | b3 >> 4));
                outputStream.write((byte)((b3 & 0xF) << 4 | (b4 >> 2 & 0xF)));
                outputStream.write((byte)(b4 << 6 | b5));
            }
        }
        final byte b6 = array[0];
        final byte b7 = array[1];
        final byte b8 = array[2];
        final byte b9 = array[3];
        final byte b10 = Base64.base64Alphabet[b6];
        final byte b11 = Base64.base64Alphabet[b7];
        final byte b12 = Base64.base64Alphabet[b8];
        final byte b13 = Base64.base64Alphabet[b9];
        if (b12 == -1 || b13 == -1) {
            if (isPad(b8) && isPad(b9)) {
                if ((b11 & 0xF) != 0x0) {
                    throw new Base64DecodingException("decoding.general");
                }
                outputStream.write((byte)(b10 << 2 | b11 >> 4));
            }
            else {
                if (isPad(b8) || !isPad(b9)) {
                    throw new Base64DecodingException("decoding.general");
                }
                final byte b14 = Base64.base64Alphabet[b8];
                if ((b14 & 0x3) != 0x0) {
                    throw new Base64DecodingException("decoding.general");
                }
                outputStream.write((byte)(b10 << 2 | b11 >> 4));
                outputStream.write((byte)((b11 & 0xF) << 4 | (b14 >> 2 & 0xF)));
            }
        }
        else {
            outputStream.write((byte)(b10 << 2 | b11 >> 4));
            outputStream.write((byte)((b11 & 0xF) << 4 | (b12 >> 2 & 0xF)));
            outputStream.write((byte)(b12 << 6 | b13));
        }
    }
    
    protected static final int removeWhiteSpace(final byte[] array) {
        if (array == null) {
            return 0;
        }
        int n = 0;
        for (final byte b : array) {
            if (!isWhiteSpace(b)) {
                array[n++] = b;
            }
        }
        return n;
    }
    
    static {
        base64Alphabet = new byte[255];
        lookUpBase64Alphabet = new char[64];
        for (int i = 0; i < 255; ++i) {
            Base64.base64Alphabet[i] = -1;
        }
        for (int j = 90; j >= 65; --j) {
            Base64.base64Alphabet[j] = (byte)(j - 65);
        }
        for (int k = 122; k >= 97; --k) {
            Base64.base64Alphabet[k] = (byte)(k - 97 + 26);
        }
        for (int l = 57; l >= 48; --l) {
            Base64.base64Alphabet[l] = (byte)(l - 48 + 52);
        }
        Base64.base64Alphabet[43] = 62;
        Base64.base64Alphabet[47] = 63;
        for (int n = 0; n <= 25; ++n) {
            Base64.lookUpBase64Alphabet[n] = (char)(65 + n);
        }
        for (int n2 = 26, n3 = 0; n2 <= 51; ++n2, ++n3) {
            Base64.lookUpBase64Alphabet[n2] = (char)(97 + n3);
        }
        for (int n4 = 52, n5 = 0; n4 <= 61; ++n4, ++n5) {
            Base64.lookUpBase64Alphabet[n4] = (char)(48 + n5);
        }
        Base64.lookUpBase64Alphabet[62] = '+';
        Base64.lookUpBase64Alphabet[63] = '/';
    }
}
