package sun.security.x509;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import java.text.Normalizer;
import java.util.Locale;
import java.io.OutputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerInputStream;
import sun.security.pkcs.PKCS9Attribute;
import java.util.List;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Map;
import java.io.IOException;
import java.io.Reader;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.util.Debug;
import sun.security.util.DerEncoder;

public class AVA implements DerEncoder
{
    private static final Debug debug;
    private static final boolean PRESERVE_OLD_DC_ENCODING;
    static final int DEFAULT = 1;
    static final int RFC1779 = 2;
    static final int RFC2253 = 3;
    final ObjectIdentifier oid;
    final DerValue value;
    private static final String specialChars1779 = ",=\n+<>#;\\\"";
    private static final String specialChars2253 = ",=+<>#;\\\"";
    private static final String specialCharsDefault = ",=\n+<>#;\\\" ";
    private static final String escapedDefault = ",+<>;\"";
    private static final String hexDigits = "0123456789ABCDEF";
    
    public AVA(final ObjectIdentifier oid, final DerValue value) {
        if (oid == null || value == null) {
            throw new NullPointerException();
        }
        this.oid = oid;
        this.value = value;
    }
    
    AVA(final Reader reader) throws IOException {
        this(reader, 1);
    }
    
    AVA(final Reader reader, final Map<String, String> map) throws IOException {
        this(reader, 1, map);
    }
    
    AVA(final Reader reader, final int n) throws IOException {
        this(reader, n, Collections.emptyMap());
    }
    
    AVA(final Reader reader, final int n, final Map<String, String> map) throws IOException {
        final StringBuilder sb = new StringBuilder();
        while (true) {
            final int char1 = readChar(reader, "Incorrect AVA format");
            if (char1 == 61) {
                break;
            }
            sb.append((char)char1);
        }
        this.oid = AVAKeyword.getOID(sb.toString(), n, map);
        sb.setLength(0);
        int n2;
        if (n == 3) {
            n2 = reader.read();
            if (n2 == 32) {
                throw new IOException("Incorrect AVA RFC2253 format - leading space must be escaped");
            }
        }
        else {
            do {
                n2 = reader.read();
            } while (n2 == 32 || n2 == 10);
        }
        if (n2 == -1) {
            this.value = new DerValue("");
            return;
        }
        if (n2 == 35) {
            this.value = parseHexString(reader, n);
        }
        else if (n2 == 34 && n != 3) {
            this.value = this.parseQuotedString(reader, sb);
        }
        else {
            this.value = this.parseString(reader, n2, n, sb);
        }
    }
    
    public ObjectIdentifier getObjectIdentifier() {
        return this.oid;
    }
    
    public DerValue getDerValue() {
        return this.value;
    }
    
    public String getValueString() {
        try {
            final String asString = this.value.getAsString();
            if (asString == null) {
                throw new RuntimeException("AVA string is null");
            }
            return asString;
        }
        catch (final IOException ex) {
            throw new RuntimeException("AVA error: " + ex, ex);
        }
    }
    
    private static DerValue parseHexString(final Reader reader, final int n) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int n2 = 0;
        int n3 = 0;
        while (true) {
            final int read = reader.read();
            if (isTerminator(read, n)) {
                if (n3 == 0) {
                    throw new IOException("AVA parse, zero hex digits");
                }
                if (n3 % 2 == 1) {
                    throw new IOException("AVA parse, odd number of hex digits");
                }
                return new DerValue(byteArrayOutputStream.toByteArray());
            }
            else {
                final int index = "0123456789ABCDEF".indexOf(Character.toUpperCase((char)read));
                if (index == -1) {
                    throw new IOException("AVA parse, invalid hex digit: " + (char)read);
                }
                if (n3 % 2 == 1) {
                    n2 = (byte)(n2 * 16 + (byte)index);
                    byteArrayOutputStream.write(n2);
                }
                else {
                    n2 = (byte)index;
                }
                ++n3;
            }
        }
    }
    
    private DerValue parseQuotedString(final Reader reader, final StringBuilder sb) throws IOException {
        int i = readChar(reader, "Quoted string did not end in quote");
        final ArrayList list = new ArrayList();
        boolean b = true;
        while (i != 34) {
            if (i == 92) {
                i = readChar(reader, "Quoted string did not end in quote");
                final Byte embeddedHexPair;
                if ((embeddedHexPair = getEmbeddedHexPair(i, reader)) != null) {
                    b = false;
                    list.add(embeddedHexPair);
                    i = reader.read();
                    continue;
                }
                if (",=\n+<>#;\\\"".indexOf((char)i) < 0) {
                    throw new IOException("Invalid escaped character in AVA: " + (char)i);
                }
            }
            if (list.size() > 0) {
                sb.append(getEmbeddedHexString(list));
                list.clear();
            }
            b &= DerValue.isPrintableStringChar((char)i);
            sb.append((char)i);
            i = readChar(reader, "Quoted string did not end in quote");
        }
        if (list.size() > 0) {
            sb.append(getEmbeddedHexString(list));
            list.clear();
        }
        int read;
        do {
            read = reader.read();
        } while (read == 10 || read == 32);
        if (read != -1) {
            throw new IOException("AVA had characters other than whitespace after terminating quote");
        }
        if (this.oid.equals((Object)PKCS9Attribute.EMAIL_ADDRESS_OID) || (this.oid.equals((Object)X500Name.DOMAIN_COMPONENT_OID) && !AVA.PRESERVE_OLD_DC_ENCODING)) {
            return new DerValue((byte)22, sb.toString().trim());
        }
        if (b) {
            return new DerValue(sb.toString().trim());
        }
        return new DerValue((byte)12, sb.toString().trim());
    }
    
    private DerValue parseString(final Reader reader, int n, final int n2, final StringBuilder sb) throws IOException {
        final ArrayList list = new ArrayList();
        boolean b = true;
        int n3 = 1;
        int n4 = 0;
        do {
            boolean b2 = false;
            if (n == 92) {
                b2 = true;
                n = readChar(reader, "Invalid trailing backslash");
                final Byte embeddedHexPair;
                if ((embeddedHexPair = getEmbeddedHexPair(n, reader)) != null) {
                    b = false;
                    list.add(embeddedHexPair);
                    n = reader.read();
                    n3 = 0;
                    continue;
                }
                if (n2 == 1 && ",=\n+<>#;\\\" ".indexOf((char)n) == -1) {
                    throw new IOException("Invalid escaped character in AVA: '" + (char)n + "'");
                }
                if (n2 == 3) {
                    if (n == 32) {
                        if (n3 == 0 && !trailingSpace(reader)) {
                            throw new IOException("Invalid escaped space character in AVA.  Only a leading or trailing space character can be escaped.");
                        }
                    }
                    else if (n == 35) {
                        if (n3 == 0) {
                            throw new IOException("Invalid escaped '#' character in AVA.  Only a leading '#' can be escaped.");
                        }
                    }
                    else if (",=+<>#;\\\"".indexOf((char)n) == -1) {
                        throw new IOException("Invalid escaped character in AVA: '" + (char)n + "'");
                    }
                }
            }
            else if (n2 == 3) {
                if (",=+<>#;\\\"".indexOf((char)n) != -1) {
                    throw new IOException("Character '" + (char)n + "' in AVA appears without escape");
                }
            }
            else if (",+<>;\"".indexOf((char)n) != -1) {
                throw new IOException("Character '" + (char)n + "' in AVA appears without escape");
            }
            if (list.size() > 0) {
                for (int i = 0; i < n4; ++i) {
                    sb.append(" ");
                }
                n4 = 0;
                sb.append(getEmbeddedHexString(list));
                list.clear();
            }
            b &= DerValue.isPrintableStringChar((char)n);
            if (n == 32 && !b2) {
                ++n4;
            }
            else {
                for (int j = 0; j < n4; ++j) {
                    sb.append(" ");
                }
                n4 = 0;
                sb.append((char)n);
            }
            n = reader.read();
            n3 = 0;
        } while (!isTerminator(n, n2));
        if (n2 == 3 && n4 > 0) {
            throw new IOException("Incorrect AVA RFC2253 format - trailing space must be escaped");
        }
        if (list.size() > 0) {
            sb.append(getEmbeddedHexString(list));
            list.clear();
        }
        if (this.oid.equals((Object)PKCS9Attribute.EMAIL_ADDRESS_OID) || (this.oid.equals((Object)X500Name.DOMAIN_COMPONENT_OID) && !AVA.PRESERVE_OLD_DC_ENCODING)) {
            return new DerValue((byte)22, sb.toString());
        }
        if (b) {
            return new DerValue(sb.toString());
        }
        return new DerValue((byte)12, sb.toString());
    }
    
    private static Byte getEmbeddedHexPair(final int n, final Reader reader) throws IOException {
        if ("0123456789ABCDEF".indexOf(Character.toUpperCase((char)n)) < 0) {
            return null;
        }
        final int char1 = readChar(reader, "unexpected EOF - escaped hex value must include two valid digits");
        if ("0123456789ABCDEF".indexOf(Character.toUpperCase((char)char1)) >= 0) {
            return new Byte((byte)((Character.digit((char)n, 16) << 4) + Character.digit((char)char1, 16)));
        }
        throw new IOException("escaped hex value must include two valid digits");
    }
    
    private static String getEmbeddedHexString(final List<Byte> list) throws IOException {
        final int size = list.size();
        final byte[] array = new byte[size];
        for (int i = 0; i < size; ++i) {
            array[i] = (byte)list.get(i);
        }
        return new String(array, "UTF8");
    }
    
    private static boolean isTerminator(final int n, final int n2) {
        switch (n) {
            case -1:
            case 43:
            case 44: {
                return true;
            }
            case 59: {
                return n2 != 3;
            }
            default: {
                return false;
            }
        }
    }
    
    private static int readChar(final Reader reader, final String s) throws IOException {
        final int read = reader.read();
        if (read == -1) {
            throw new IOException(s);
        }
        return read;
    }
    
    private static boolean trailingSpace(final Reader reader) throws IOException {
        if (!reader.markSupported()) {
            return true;
        }
        reader.mark(9999);
        boolean b;
        while (true) {
            final int read = reader.read();
            if (read == -1) {
                b = true;
                break;
            }
            if (read == 32) {
                continue;
            }
            if (read != 92) {
                b = false;
                break;
            }
            if (reader.read() != 32) {
                b = false;
                break;
            }
        }
        reader.reset();
        return b;
    }
    
    AVA(final DerValue derValue) throws IOException {
        if (derValue.tag != 48) {
            throw new IOException("AVA not a sequence");
        }
        this.oid = derValue.data.getOID();
        this.value = derValue.data.getDerValue();
        if (derValue.data.available() != 0) {
            throw new IOException("AVA, extra bytes = " + derValue.data.available());
        }
    }
    
    AVA(final DerInputStream derInputStream) throws IOException {
        this(derInputStream.getDerValue());
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof AVA && this.toRFC2253CanonicalString().equals(((AVA)o).toRFC2253CanonicalString()));
    }
    
    @Override
    public int hashCode() {
        return this.toRFC2253CanonicalString().hashCode();
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        this.derEncode(derOutputStream);
    }
    
    @Override
    public void derEncode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream.putOID(this.oid);
        this.value.encode(derOutputStream);
        derOutputStream2.write((byte)48, derOutputStream);
        outputStream.write(derOutputStream2.toByteArray());
    }
    
    private String toKeyword(final int n, final Map<String, String> map) {
        return AVAKeyword.getKeyword(this.oid, n, map);
    }
    
    @Override
    public String toString() {
        return this.toKeywordValueString(this.toKeyword(1, Collections.emptyMap()));
    }
    
    public String toRFC1779String() {
        return this.toRFC1779String(Collections.emptyMap());
    }
    
    public String toRFC1779String(final Map<String, String> map) {
        return this.toKeywordValueString(this.toKeyword(2, map));
    }
    
    public String toRFC2253String() {
        return this.toRFC2253String(Collections.emptyMap());
    }
    
    public String toRFC2253String(final Map<String, String> map) {
        final StringBuilder sb = new StringBuilder(100);
        sb.append(this.toKeyword(3, map));
        sb.append('=');
        if ((sb.charAt(0) >= '0' && sb.charAt(0) <= '9') || !isDerString(this.value, false)) {
            byte[] byteArray;
            try {
                byteArray = this.value.toByteArray();
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("DER Value conversion");
            }
            sb.append('#');
            for (int i = 0; i < byteArray.length; ++i) {
                final byte b = byteArray[i];
                sb.append(Character.forDigit(0xF & b >>> 4, 16));
                sb.append(Character.forDigit(0xF & b, 16));
            }
        }
        else {
            String s;
            try {
                s = new String(this.value.getDataBytes(), "UTF8");
            }
            catch (final IOException ex2) {
                throw new IllegalArgumentException("DER Value conversion");
            }
            final StringBuilder sb2 = new StringBuilder();
            for (int j = 0; j < s.length(); ++j) {
                final char char1 = s.charAt(j);
                if (DerValue.isPrintableStringChar(char1) || ",=+<>#;\"\\".indexOf(char1) >= 0) {
                    if (",=+<>#;\"\\".indexOf(char1) >= 0) {
                        sb2.append('\\');
                    }
                    sb2.append(char1);
                }
                else if (char1 == '\0') {
                    sb2.append("\\00");
                }
                else if (AVA.debug != null && Debug.isOn("ava")) {
                    byte[] bytes;
                    try {
                        bytes = Character.toString(char1).getBytes("UTF8");
                    }
                    catch (final IOException ex3) {
                        throw new IllegalArgumentException("DER Value conversion");
                    }
                    for (int k = 0; k < bytes.length; ++k) {
                        sb2.append('\\');
                        sb2.append(Character.toUpperCase(Character.forDigit(0xF & bytes[k] >>> 4, 16)));
                        sb2.append(Character.toUpperCase(Character.forDigit(0xF & bytes[k], 16)));
                    }
                }
                else {
                    sb2.append(char1);
                }
            }
            final char[] charArray = sb2.toString().toCharArray();
            final StringBuilder sb3 = new StringBuilder();
            int n;
            for (n = 0; n < charArray.length && (charArray[n] == ' ' || charArray[n] == '\r'); ++n) {}
            int n2;
            for (n2 = charArray.length - 1; n2 >= 0 && (charArray[n2] == ' ' || charArray[n2] == '\r'); --n2) {}
            for (int l = 0; l < charArray.length; ++l) {
                final char c = charArray[l];
                if (l < n || l > n2) {
                    sb3.append('\\');
                }
                sb3.append(c);
            }
            sb.append(sb3.toString());
        }
        return sb.toString();
    }
    
    public String toRFC2253CanonicalString() {
        final StringBuilder sb = new StringBuilder(40);
        sb.append(this.toKeyword(3, Collections.emptyMap()));
        sb.append('=');
        if ((sb.charAt(0) >= '0' && sb.charAt(0) <= '9') || !isDerString(this.value, true)) {
            byte[] byteArray;
            try {
                byteArray = this.value.toByteArray();
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("DER Value conversion");
            }
            sb.append('#');
            for (int i = 0; i < byteArray.length; ++i) {
                final byte b = byteArray[i];
                sb.append(Character.forDigit(0xF & b >>> 4, 16));
                sb.append(Character.forDigit(0xF & b, 16));
            }
        }
        else {
            String s;
            try {
                s = new String(this.value.getDataBytes(), "UTF8");
            }
            catch (final IOException ex2) {
                throw new IllegalArgumentException("DER Value conversion");
            }
            final StringBuilder sb2 = new StringBuilder();
            int n = 0;
            for (int j = 0; j < s.length(); ++j) {
                final char char1 = s.charAt(j);
                if (DerValue.isPrintableStringChar(char1) || ",+<>;\"\\".indexOf(char1) >= 0 || (j == 0 && char1 == '#')) {
                    if ((j == 0 && char1 == '#') || ",+<>;\"\\".indexOf(char1) >= 0) {
                        sb2.append('\\');
                    }
                    if (!Character.isWhitespace(char1)) {
                        n = 0;
                        sb2.append(char1);
                    }
                    else if (n == 0) {
                        n = 1;
                        sb2.append(char1);
                    }
                }
                else if (AVA.debug != null && Debug.isOn("ava")) {
                    n = 0;
                    byte[] bytes;
                    try {
                        bytes = Character.toString(char1).getBytes("UTF8");
                    }
                    catch (final IOException ex3) {
                        throw new IllegalArgumentException("DER Value conversion");
                    }
                    for (int k = 0; k < bytes.length; ++k) {
                        sb2.append('\\');
                        sb2.append(Character.forDigit(0xF & bytes[k] >>> 4, 16));
                        sb2.append(Character.forDigit(0xF & bytes[k], 16));
                    }
                }
                else {
                    n = 0;
                    sb2.append(char1);
                }
            }
            sb.append(sb2.toString().trim());
        }
        return Normalizer.normalize(sb.toString().toUpperCase(Locale.US).toLowerCase(Locale.US), Normalizer.Form.NFKD);
    }
    
    private static boolean isDerString(final DerValue derValue, final boolean b) {
        if (b) {
            switch (derValue.tag) {
                case 12:
                case 19: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        else {
            switch (derValue.tag) {
                case 12:
                case 19:
                case 20:
                case 22:
                case 27:
                case 30: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
    }
    
    boolean hasRFC2253Keyword() {
        return AVAKeyword.hasKeyword(this.oid, 3);
    }
    
    private String toKeywordValueString(final String s) {
        final StringBuilder sb = new StringBuilder(40);
        sb.append(s);
        sb.append("=");
        try {
            final String asString = this.value.getAsString();
            if (asString == null) {
                final byte[] byteArray = this.value.toByteArray();
                sb.append('#');
                for (int i = 0; i < byteArray.length; ++i) {
                    sb.append("0123456789ABCDEF".charAt(byteArray[i] >> 4 & 0xF));
                    sb.append("0123456789ABCDEF".charAt(byteArray[i] & 0xF));
                }
            }
            else {
                int n = 0;
                final StringBuilder sb2 = new StringBuilder();
                int n2 = 0;
                final int length = asString.length();
                final boolean b = length > 1 && asString.charAt(0) == '\"' && asString.charAt(length - 1) == '\"';
                for (int j = 0; j < length; ++j) {
                    final char char1 = asString.charAt(j);
                    if (b && (j == 0 || j == length - 1)) {
                        sb2.append(char1);
                    }
                    else if (DerValue.isPrintableStringChar(char1) || ",+=\n<>#;\\\"".indexOf(char1) >= 0) {
                        if (n == 0 && ((j == 0 && (char1 == ' ' || char1 == '\n')) || ",+=\n<>#;\\\"".indexOf(char1) >= 0)) {
                            n = 1;
                        }
                        if (char1 != ' ' && char1 != '\n') {
                            if (char1 == '\"' || char1 == '\\') {
                                sb2.append('\\');
                            }
                            n2 = 0;
                        }
                        else {
                            if (n == 0 && n2 != 0) {
                                n = 1;
                            }
                            n2 = 1;
                        }
                        sb2.append(char1);
                    }
                    else if (AVA.debug != null && Debug.isOn("ava")) {
                        n2 = 0;
                        final byte[] bytes = Character.toString(char1).getBytes("UTF8");
                        for (int k = 0; k < bytes.length; ++k) {
                            sb2.append('\\');
                            sb2.append(Character.toUpperCase(Character.forDigit(0xF & bytes[k] >>> 4, 16)));
                            sb2.append(Character.toUpperCase(Character.forDigit(0xF & bytes[k], 16)));
                        }
                    }
                    else {
                        n2 = 0;
                        sb2.append(char1);
                    }
                }
                if (sb2.length() > 0) {
                    final char char2 = sb2.charAt(sb2.length() - 1);
                    if (char2 == ' ' || char2 == '\n') {
                        n = 1;
                    }
                }
                if (!b && n != 0) {
                    sb.append("\"" + sb2.toString() + "\"");
                }
                else {
                    sb.append(sb2.toString());
                }
            }
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("DER Value conversion");
        }
        return sb.toString();
    }
    
    static {
        debug = Debug.getInstance("x509", "\t[AVA]");
        PRESERVE_OLD_DC_ENCODING = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("com.sun.security.preserveOldDCEncoding"));
    }
}
