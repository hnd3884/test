package org.bouncycastle.crypto.generators;

import java.util.HashSet;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.crypto.DataLengthException;
import java.util.Set;

public class OpenBSDBCrypt
{
    private static final byte[] encodingTable;
    private static final byte[] decodingTable;
    private static final String defaultVersion = "2y";
    private static final Set<String> allowedVersions;
    
    private static String createBcryptString(final String s, final byte[] array, final byte[] array2, final int n) {
        if (!OpenBSDBCrypt.allowedVersions.contains(s)) {
            throw new IllegalArgumentException("Version " + s + " is not accepted by this implementation.");
        }
        final StringBuffer sb = new StringBuffer(60);
        sb.append('$');
        sb.append(s);
        sb.append('$');
        sb.append((n < 10) ? ("0" + n) : Integer.toString(n));
        sb.append('$');
        sb.append(encodeData(array2));
        sb.append(encodeData(BCrypt.generate(array, array2, n)));
        return sb.toString();
    }
    
    public static String generate(final char[] array, final byte[] array2, final int n) {
        return generate("2y", array, array2, n);
    }
    
    public static String generate(final String s, final char[] array, final byte[] array2, final int n) {
        if (!OpenBSDBCrypt.allowedVersions.contains(s)) {
            throw new IllegalArgumentException("Version " + s + " is not accepted by this implementation.");
        }
        if (array == null) {
            throw new IllegalArgumentException("Password required.");
        }
        if (array2 == null) {
            throw new IllegalArgumentException("Salt required.");
        }
        if (array2.length != 16) {
            throw new DataLengthException("16 byte salt required: " + array2.length);
        }
        if (n < 4 || n > 31) {
            throw new IllegalArgumentException("Invalid cost factor.");
        }
        final byte[] utf8ByteArray = Strings.toUTF8ByteArray(array);
        final byte[] array3 = new byte[(utf8ByteArray.length >= 72) ? 72 : (utf8ByteArray.length + 1)];
        if (array3.length > utf8ByteArray.length) {
            System.arraycopy(utf8ByteArray, 0, array3, 0, utf8ByteArray.length);
        }
        else {
            System.arraycopy(utf8ByteArray, 0, array3, 0, array3.length);
        }
        Arrays.fill(utf8ByteArray, (byte)0);
        final String bcryptString = createBcryptString(s, array3, array2, n);
        Arrays.fill(array3, (byte)0);
        return bcryptString;
    }
    
    public static boolean checkPassword(final String s, final char[] array) {
        if (s.length() != 60) {
            throw new DataLengthException("Bcrypt String length: " + s.length() + ", 60 required.");
        }
        if (s.charAt(0) != '$' || s.charAt(3) != '$' || s.charAt(6) != '$') {
            throw new IllegalArgumentException("Invalid Bcrypt String format.");
        }
        final String substring = s.substring(1, 3);
        if (!OpenBSDBCrypt.allowedVersions.contains(substring)) {
            throw new IllegalArgumentException("Bcrypt version '" + s.substring(1, 3) + "' is not supported by this implementation");
        }
        int int1;
        try {
            int1 = Integer.parseInt(s.substring(4, 6));
        }
        catch (final NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid cost factor: " + s.substring(4, 6));
        }
        if (int1 < 4 || int1 > 31) {
            throw new IllegalArgumentException("Invalid cost factor: " + int1 + ", 4 < cost < 31 expected.");
        }
        if (array == null) {
            throw new IllegalArgumentException("Missing password.");
        }
        return s.equals(generate(substring, array, decodeSaltString(s.substring(s.lastIndexOf(36) + 1, s.length() - 31)), int1));
    }
    
    private static String encodeData(byte[] array) {
        if (array.length != 24 && array.length != 16) {
            throw new DataLengthException("Invalid length: " + array.length + ", 24 for key or 16 for salt expected");
        }
        int n = 0;
        if (array.length == 16) {
            n = 1;
            final byte[] array2 = new byte[18];
            System.arraycopy(array, 0, array2, 0, array.length);
            array = array2;
        }
        else {
            array[array.length - 1] = 0;
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int length = array.length, i = 0; i < length; i += 3) {
            final int n2 = array[i] & 0xFF;
            final int n3 = array[i + 1] & 0xFF;
            final int n4 = array[i + 2] & 0xFF;
            byteArrayOutputStream.write(OpenBSDBCrypt.encodingTable[n2 >>> 2 & 0x3F]);
            byteArrayOutputStream.write(OpenBSDBCrypt.encodingTable[(n2 << 4 | n3 >>> 4) & 0x3F]);
            byteArrayOutputStream.write(OpenBSDBCrypt.encodingTable[(n3 << 2 | n4 >>> 6) & 0x3F]);
            byteArrayOutputStream.write(OpenBSDBCrypt.encodingTable[n4 & 0x3F]);
        }
        final String fromByteArray = Strings.fromByteArray(byteArrayOutputStream.toByteArray());
        if (n == 1) {
            return fromByteArray.substring(0, 22);
        }
        return fromByteArray.substring(0, fromByteArray.length() - 1);
    }
    
    private static byte[] decodeSaltString(final String s) {
        final char[] charArray = s.toCharArray();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(16);
        if (charArray.length != 22) {
            throw new DataLengthException("Invalid base64 salt length: " + charArray.length + " , 22 required.");
        }
        for (int i = 0; i < charArray.length; ++i) {
            final char c = charArray[i];
            if (c > 'z' || c < '.' || (c > '9' && c < 'A')) {
                throw new IllegalArgumentException("Salt string contains invalid character: " + (int)c);
            }
        }
        final char[] array = new char[24];
        System.arraycopy(charArray, 0, array, 0, charArray.length);
        final char[] array2 = array;
        for (int length = array2.length, j = 0; j < length; j += 4) {
            final byte b = OpenBSDBCrypt.decodingTable[array2[j]];
            final byte b2 = OpenBSDBCrypt.decodingTable[array2[j + 1]];
            final byte b3 = OpenBSDBCrypt.decodingTable[array2[j + 2]];
            final byte b4 = OpenBSDBCrypt.decodingTable[array2[j + 3]];
            byteArrayOutputStream.write(b << 2 | b2 >> 4);
            byteArrayOutputStream.write(b2 << 4 | b3 >> 2);
            byteArrayOutputStream.write(b3 << 6 | b4);
        }
        final byte[] byteArray = byteArrayOutputStream.toByteArray();
        final byte[] array3 = new byte[16];
        System.arraycopy(byteArray, 0, array3, 0, array3.length);
        return array3;
    }
    
    static {
        encodingTable = new byte[] { 46, 47, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57 };
        decodingTable = new byte[128];
        (allowedVersions = new HashSet<String>()).add("2a");
        OpenBSDBCrypt.allowedVersions.add("2y");
        OpenBSDBCrypt.allowedVersions.add("2b");
        for (int i = 0; i < OpenBSDBCrypt.decodingTable.length; ++i) {
            OpenBSDBCrypt.decodingTable[i] = -1;
        }
        for (int j = 0; j < OpenBSDBCrypt.encodingTable.length; ++j) {
            OpenBSDBCrypt.decodingTable[OpenBSDBCrypt.encodingTable[j]] = (byte)j;
        }
    }
}
