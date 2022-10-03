package com.lowagie.text.pdf;

import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.ArrayList;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.lowagie.text.ExceptionConverter;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class PdfEncodings
{
    protected static final int CIDNONE = 0;
    protected static final int CIDRANGE = 1;
    protected static final int CIDCHAR = 2;
    static final char[] winansiByteToChar;
    static final char[] pdfEncodingByteToChar;
    static final IntHashtable winansi;
    static final IntHashtable pdfEncoding;
    static ConcurrentHashMap<String, ExtraEncoding> extraEncodings;
    static final ConcurrentHashMap<String, char[][]> cmaps;
    public static final byte[][] CRLF_CID_NEWLINE;
    
    public static final byte[] convertToBytes(final String text, final String encoding) {
        if (text == null) {
            return new byte[0];
        }
        if (encoding == null || encoding.length() == 0) {
            final int len = text.length();
            final byte[] b = new byte[len];
            for (int k = 0; k < len; ++k) {
                b[k] = (byte)text.charAt(k);
            }
            return b;
        }
        final ExtraEncoding extra = PdfEncodings.extraEncodings.get(encoding.toLowerCase(Locale.ROOT));
        if (extra != null) {
            final byte[] b = extra.charToByte(text, encoding);
            if (b != null) {
                return b;
            }
        }
        IntHashtable hash = null;
        if (encoding.equals("Cp1252")) {
            hash = PdfEncodings.winansi;
        }
        else if (encoding.equals("PDF")) {
            hash = PdfEncodings.pdfEncoding;
        }
        if (hash != null) {
            final char[] cc = text.toCharArray();
            final int len2 = cc.length;
            int ptr = 0;
            final byte[] b2 = new byte[len2];
            int c = 0;
            for (final char char1 : cc) {
                if (char1 < '\u0080' || (char1 > ' ' && char1 <= '\u00ff')) {
                    c = char1;
                }
                else {
                    c = hash.get(char1);
                }
                if (c != 0) {
                    b2[ptr++] = (byte)c;
                }
            }
            if (ptr == len2) {
                return b2;
            }
            final byte[] b3 = new byte[ptr];
            System.arraycopy(b2, 0, b3, 0, ptr);
            return b3;
        }
        else {
            if (encoding.equals("UnicodeBig")) {
                final char[] cc = text.toCharArray();
                final int len2 = cc.length;
                final byte[] b4 = new byte[cc.length * 2 + 2];
                b4[0] = -2;
                b4[1] = -1;
                int bptr = 2;
                for (final char c2 : cc) {
                    b4[bptr++] = (byte)(c2 >> 8);
                    b4[bptr++] = (byte)(c2 & '\u00ff');
                }
                return b4;
            }
            try {
                return text.getBytes(encoding);
            }
            catch (final UnsupportedEncodingException e) {
                throw new ExceptionConverter(e);
            }
        }
    }
    
    public static final byte[] convertToBytes(final char char1, final String encoding) {
        if (encoding == null || encoding.length() == 0) {
            return new byte[] { (byte)char1 };
        }
        final ExtraEncoding extra = PdfEncodings.extraEncodings.get(encoding.toLowerCase(Locale.ROOT));
        if (extra != null) {
            final byte[] b = extra.charToByte(char1, encoding);
            if (b != null) {
                return b;
            }
        }
        IntHashtable hash = null;
        if (encoding.equals("Cp1252")) {
            hash = PdfEncodings.winansi;
        }
        else if (encoding.equals("PDF")) {
            hash = PdfEncodings.pdfEncoding;
        }
        if (hash != null) {
            int c = 0;
            if (char1 < '\u0080' || (char1 > ' ' && char1 <= '\u00ff')) {
                c = char1;
            }
            else {
                c = hash.get(char1);
            }
            if (c != 0) {
                return new byte[] { (byte)c };
            }
            return new byte[0];
        }
        else {
            if (encoding.equals("UnicodeBig")) {
                final byte[] b2 = { -2, -1, (byte)(char1 >> 8), (byte)(char1 & '\u00ff') };
                return b2;
            }
            try {
                return String.valueOf(char1).getBytes(encoding);
            }
            catch (final UnsupportedEncodingException e) {
                throw new ExceptionConverter(e);
            }
        }
    }
    
    public static final String convertToString(final byte[] bytes, final String encoding) {
        if (bytes == null) {
            return "";
        }
        if (encoding == null || encoding.length() == 0) {
            final char[] c = new char[bytes.length];
            for (int k = 0; k < bytes.length; ++k) {
                c[k] = (char)(bytes[k] & 0xFF);
            }
            return new String(c);
        }
        final ExtraEncoding extra = PdfEncodings.extraEncodings.get(encoding.toLowerCase(Locale.ROOT));
        if (extra != null) {
            final String text = extra.byteToChar(bytes, encoding);
            if (text != null) {
                return text;
            }
        }
        char[] ch = null;
        if (encoding.equals("Cp1252")) {
            ch = PdfEncodings.winansiByteToChar;
        }
        else if (encoding.equals("PDF")) {
            ch = PdfEncodings.pdfEncodingByteToChar;
        }
        if (ch != null) {
            final int len = bytes.length;
            final char[] c2 = new char[len];
            for (int i = 0; i < len; ++i) {
                c2[i] = ch[bytes[i] & 0xFF];
            }
            return new String(c2);
        }
        try {
            return new String(bytes, encoding);
        }
        catch (final UnsupportedEncodingException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static boolean isPdfDocEncoding(final String text) {
        if (text == null) {
            return true;
        }
        for (int len = text.length(), k = 0; k < len; ++k) {
            final char char1 = text.charAt(k);
            if (char1 >= '\u0080') {
                if (char1 <= ' ' || char1 > '\u00ff') {
                    if (!PdfEncodings.pdfEncoding.containsKey(char1)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public static void clearCmap(final String name) {
        if (name.length() == 0) {
            PdfEncodings.cmaps.clear();
        }
        else {
            PdfEncodings.cmaps.remove(name);
        }
    }
    
    public static void loadCmap(final String name, final byte[][] newline) {
        try {
            char[][] planes = null;
            planes = PdfEncodings.cmaps.get(name);
            if (planes == null) {
                planes = readCmap(name, newline);
                PdfEncodings.cmaps.putIfAbsent(name, planes);
            }
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static String convertCmap(final String name, final byte[] seq) {
        return convertCmap(name, seq, 0, seq.length);
    }
    
    public static String convertCmap(final String name, final byte[] seq, final int start, final int length) {
        try {
            char[][] planes = null;
            planes = PdfEncodings.cmaps.get(name);
            if (planes == null) {
                planes = readCmap(name, (byte[][])null);
                PdfEncodings.cmaps.putIfAbsent(name, planes);
            }
            return decodeSequence(seq, start, length, planes);
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    static String decodeSequence(final byte[] seq, final int start, final int length, final char[][] planes) {
        final StringBuffer buf = new StringBuffer();
        final int end = start + length;
        int currentPlane = 0;
        for (int k = start; k < end; ++k) {
            final int one = seq[k] & 0xFF;
            final char[] plane = planes[currentPlane];
            final int cid = plane[one];
            if ((cid & 0x8000) == 0x0) {
                buf.append((char)cid);
                currentPlane = 0;
            }
            else {
                currentPlane = (cid & 0x7FFF);
            }
        }
        return buf.toString();
    }
    
    static char[][] readCmap(final String name, final byte[][] newline) throws IOException {
        final ArrayList<char[]> planes = new ArrayList<char[]>();
        planes.add(new char[256]);
        readCmap(name, planes);
        if (newline != null) {
            for (final byte[] element : newline) {
                encodeSequence(element.length, element, '\u7fff', planes);
            }
        }
        final char[][] ret = new char[planes.size()][];
        return planes.toArray(ret);
    }
    
    static void readCmap(final String name, final ArrayList<char[]> planes) throws IOException {
        final String fullName = "com/lowagie/text/pdf/fonts/cmaps/" + name;
        final InputStream in = BaseFont.getResourceStream(fullName);
        if (in == null) {
            throw new IOException(MessageLocalization.getComposedMessage("the.cmap.1.was.not.found", name));
        }
        encodeStream(in, planes);
        in.close();
    }
    
    static void encodeStream(final InputStream in, final ArrayList<char[]> planes) throws IOException {
        final BufferedReader rd = new BufferedReader(new InputStreamReader(in, StandardCharsets.ISO_8859_1));
        String line = null;
        int state = 0;
        final byte[] seqs = new byte[7];
        while ((line = rd.readLine()) != null) {
            if (line.length() < 6) {
                continue;
            }
            switch (state) {
                case 0: {
                    if (line.indexOf("begincidrange") >= 0) {
                        state = 1;
                        continue;
                    }
                    if (line.indexOf("begincidchar") >= 0) {
                        state = 2;
                        continue;
                    }
                    if (line.indexOf("usecmap") >= 0) {
                        final StringTokenizer tk = new StringTokenizer(line);
                        final String t = tk.nextToken();
                        readCmap(t.substring(1), planes);
                        continue;
                    }
                    continue;
                }
                case 1: {
                    if (line.indexOf("endcidrange") >= 0) {
                        state = 0;
                        continue;
                    }
                    final StringTokenizer tk = new StringTokenizer(line);
                    String t = tk.nextToken();
                    final int size = t.length() / 2 - 1;
                    final long start = Long.parseLong(t.substring(1, t.length() - 1), 16);
                    t = tk.nextToken();
                    final long end = Long.parseLong(t.substring(1, t.length() - 1), 16);
                    t = tk.nextToken();
                    int cid = Integer.parseInt(t);
                    for (long k = start; k <= end; ++k) {
                        breakLong(k, size, seqs);
                        encodeSequence(size, seqs, (char)cid, planes);
                        ++cid;
                    }
                    continue;
                }
                case 2: {
                    if (line.indexOf("endcidchar") >= 0) {
                        state = 0;
                        continue;
                    }
                    final StringTokenizer tk = new StringTokenizer(line);
                    String t = tk.nextToken();
                    final int size = t.length() / 2 - 1;
                    final long start = Long.parseLong(t.substring(1, t.length() - 1), 16);
                    t = tk.nextToken();
                    final int cid2 = Integer.parseInt(t);
                    breakLong(start, size, seqs);
                    encodeSequence(size, seqs, (char)cid2, planes);
                    continue;
                }
            }
        }
    }
    
    static void breakLong(final long n, final int size, final byte[] seqs) {
        for (int k = 0; k < size; ++k) {
            seqs[k] = (byte)(n >> (size - 1 - k) * 8);
        }
    }
    
    static void encodeSequence(int size, final byte[] seqs, final char cid, final ArrayList<char[]> planes) {
        --size;
        int nextPlane = 0;
        for (int idx = 0; idx < size; ++idx) {
            final char[] plane = planes.get(nextPlane);
            final int one = seqs[idx] & 0xFF;
            char c = plane[one];
            if (c != '\0' && (c & '\u8000') == 0x0) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("inconsistent.mapping"));
            }
            if (c == '\0') {
                planes.add(new char[256]);
                c = (char)(planes.size() - 1 | 0x8000);
                plane[one] = c;
            }
            nextPlane = (c & '\u7fff');
        }
        final char[] plane2 = planes.get(nextPlane);
        final int one2 = seqs[size] & 0xFF;
        final char c2 = plane2[one2];
        if ((c2 & '\u8000') != 0x0) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("inconsistent.mapping"));
        }
        plane2[one2] = cid;
    }
    
    public static void addExtraEncoding(final String name, final ExtraEncoding enc) {
        PdfEncodings.extraEncodings.putIfAbsent(name.toLowerCase(Locale.ROOT), enc);
    }
    
    static {
        winansiByteToChar = new char[] { '\0', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\u007f', '\u20ac', '\ufffd', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021', '\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\ufffd', '\u017d', '\ufffd', '\ufffd', '\u2018', '\u2019', '\u201c', '\u201d', '\u2022', '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\ufffd', '\u017e', '\u0178', ' ', '¡', '¢', '£', '¤', '¥', '¦', '§', '¨', '©', 'ª', '«', '¬', '\u00ad', '®', '¯', '°', '±', '²', '³', '´', 'µ', '¶', '·', '¸', '¹', 'º', '»', '¼', '½', '¾', '¿', '\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u00c7', '\u00c8', '\u00c9', '\u00ca', '\u00cb', '\u00cc', '\u00cd', '\u00ce', '\u00cf', '\u00d0', '\u00d1', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u00d7', '\u00d8', '\u00d9', '\u00da', '\u00db', '\u00dc', '\u00dd', '\u00de', '\u00df', '\u00e0', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u00e7', '\u00e8', '\u00e9', '\u00ea', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef', '\u00f0', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u00f7', '\u00f8', '\u00f9', '\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u00fe', '\u00ff' };
        pdfEncodingByteToChar = new char[] { '\0', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\u007f', '\u2022', '\u2020', '\u2021', '\u2026', '\u2014', '\u2013', '\u0192', '\u2044', '\u2039', '\u203a', '\u2212', '\u2030', '\u201e', '\u201c', '\u201d', '\u2018', '\u2019', '\u201a', '\u2122', '\ufb01', '\ufb02', '\u0141', '\u0152', '\u0160', '\u0178', '\u017d', '\u0131', '\u0142', '\u0153', '\u0161', '\u017e', '\ufffd', '\u20ac', '¡', '¢', '£', '¤', '¥', '¦', '§', '¨', '©', 'ª', '«', '¬', '\u00ad', '®', '¯', '°', '±', '²', '³', '´', 'µ', '¶', '·', '¸', '¹', 'º', '»', '¼', '½', '¾', '¿', '\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u00c7', '\u00c8', '\u00c9', '\u00ca', '\u00cb', '\u00cc', '\u00cd', '\u00ce', '\u00cf', '\u00d0', '\u00d1', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u00d7', '\u00d8', '\u00d9', '\u00da', '\u00db', '\u00dc', '\u00dd', '\u00de', '\u00df', '\u00e0', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u00e7', '\u00e8', '\u00e9', '\u00ea', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef', '\u00f0', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u00f7', '\u00f8', '\u00f9', '\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u00fe', '\u00ff' };
        winansi = new IntHashtable();
        pdfEncoding = new IntHashtable();
        PdfEncodings.extraEncodings = new ConcurrentHashMap<String, ExtraEncoding>(200, 0.85f, 64);
        for (int k = 128; k < 161; ++k) {
            final char c = PdfEncodings.winansiByteToChar[k];
            if (c != '\ufffd') {
                PdfEncodings.winansi.put(c, k);
            }
        }
        for (int k = 128; k < 161; ++k) {
            final char c = PdfEncodings.pdfEncodingByteToChar[k];
            if (c != '\ufffd') {
                PdfEncodings.pdfEncoding.put(c, k);
            }
        }
        addExtraEncoding("Wingdings", new WingdingsConversion());
        addExtraEncoding("Symbol", new SymbolConversion(true));
        addExtraEncoding("ZapfDingbats", new SymbolConversion(false));
        addExtraEncoding("SymbolTT", new SymbolTTConversion());
        addExtraEncoding("Cp437", new Cp437Conversion());
        cmaps = new ConcurrentHashMap<String, char[][]>(100, 0.85f, 64);
        CRLF_CID_NEWLINE = new byte[][] { { 10 }, { 13, 10 } };
    }
    
    private static class WingdingsConversion implements ExtraEncoding
    {
        private static final byte[] table;
        
        @Override
        public byte[] charToByte(final char char1, final String encoding) {
            if (char1 == ' ') {
                return new byte[] { (byte)char1 };
            }
            if (char1 >= '\u2701' && char1 <= '\u27be') {
                final byte v = WingdingsConversion.table[char1 - '\u2700'];
                if (v != 0) {
                    return new byte[] { v };
                }
            }
            return new byte[0];
        }
        
        @Override
        public byte[] charToByte(final String text, final String encoding) {
            final char[] cc = text.toCharArray();
            final byte[] b = new byte[cc.length];
            int ptr = 0;
            final int len = cc.length;
            for (final char c : cc) {
                if (c == ' ') {
                    b[ptr++] = (byte)c;
                }
                else if (c >= '\u2701' && c <= '\u27be') {
                    final byte v = WingdingsConversion.table[c - '\u2700'];
                    if (v != 0) {
                        b[ptr++] = v;
                    }
                }
            }
            if (ptr == len) {
                return b;
            }
            final byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }
        
        @Override
        public String byteToChar(final byte[] b, final String encoding) {
            return null;
        }
        
        static {
            table = new byte[] { 0, 35, 34, 0, 0, 0, 41, 62, 81, 42, 0, 0, 65, 63, 0, 0, 0, 0, 0, -4, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 86, 0, 88, 89, 0, 0, 0, 0, 0, 0, 0, 0, -75, 0, 0, 0, 0, 0, -74, 0, 0, 0, -83, -81, -84, 0, 0, 0, 0, 0, 0, 0, 0, 124, 123, 0, 0, 0, 84, 0, 0, 0, 0, 0, 0, 0, 0, -90, 0, 0, 0, 113, 114, 0, 0, 0, 117, 0, 0, 0, 0, 0, 0, 125, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -24, -40, 0, 0, -60, -58, 0, 0, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, -36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        }
    }
    
    private static class Cp437Conversion implements ExtraEncoding
    {
        private static IntHashtable c2b;
        private static final char[] table;
        
        @Override
        public byte[] charToByte(final String text, final String encoding) {
            final char[] cc = text.toCharArray();
            final byte[] b = new byte[cc.length];
            int ptr = 0;
            final int len = cc.length;
            for (final char c : cc) {
                if (c < '\u0080') {
                    b[ptr++] = (byte)c;
                }
                else {
                    final byte v = (byte)Cp437Conversion.c2b.get(c);
                    if (v != 0) {
                        b[ptr++] = v;
                    }
                }
            }
            if (ptr == len) {
                return b;
            }
            final byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }
        
        @Override
        public byte[] charToByte(final char char1, final String encoding) {
            if (char1 < '\u0080') {
                return new byte[] { (byte)char1 };
            }
            final byte v = (byte)Cp437Conversion.c2b.get(char1);
            if (v != 0) {
                return new byte[] { v };
            }
            return new byte[0];
        }
        
        @Override
        public String byteToChar(final byte[] b, final String encoding) {
            final int len = b.length;
            final char[] cc = new char[len];
            int ptr = 0;
            for (int k = 0; k < len; ++k) {
                final int c = b[k] & 0xFF;
                if (c >= 32) {
                    if (c < 128) {
                        cc[ptr++] = (char)c;
                    }
                    else {
                        final char v = Cp437Conversion.table[c - 128];
                        cc[ptr++] = v;
                    }
                }
            }
            return new String(cc, 0, ptr);
        }
        
        static {
            Cp437Conversion.c2b = new IntHashtable();
            table = new char[] { '\u00c7', '\u00fc', '\u00e9', '\u00e2', '\u00e4', '\u00e0', '\u00e5', '\u00e7', '\u00ea', '\u00eb', '\u00e8', '\u00ef', '\u00ee', '\u00ec', '\u00c4', '\u00c5', '\u00c9', '\u00e6', '\u00c6', '\u00f4', '\u00f6', '\u00f2', '\u00fb', '\u00f9', '\u00ff', '\u00d6', '\u00dc', '¢', '£', '¥', '\u20a7', '\u0192', '\u00e1', '\u00ed', '\u00f3', '\u00fa', '\u00f1', '\u00d1', 'ª', 'º', '¿', '\u2310', '¬', '½', '¼', '¡', '«', '»', '\u2591', '\u2592', '\u2593', '\u2502', '\u2524', '\u2561', '\u2562', '\u2556', '\u2555', '\u2563', '\u2551', '\u2557', '\u255d', '\u255c', '\u255b', '\u2510', '\u2514', '\u2534', '\u252c', '\u251c', '\u2500', '\u253c', '\u255e', '\u255f', '\u255a', '\u2554', '\u2569', '\u2566', '\u2560', '\u2550', '\u256c', '\u2567', '\u2568', '\u2564', '\u2565', '\u2559', '\u2558', '\u2552', '\u2553', '\u256b', '\u256a', '\u2518', '\u250c', '\u2588', '\u2584', '\u258c', '\u2590', '\u2580', '\u03b1', '\u00df', '\u0393', '\u03c0', '\u03a3', '\u03c3', 'µ', '\u03c4', '\u03a6', '\u0398', '\u03a9', '\u03b4', '\u221e', '\u03c6', '\u03b5', '\u2229', '\u2261', '±', '\u2265', '\u2264', '\u2320', '\u2321', '\u00f7', '\u2248', '°', '\u2219', '·', '\u221a', '\u207f', '²', '\u25a0', ' ' };
            for (int k = 0; k < Cp437Conversion.table.length; ++k) {
                Cp437Conversion.c2b.put(Cp437Conversion.table[k], k + 128);
            }
        }
    }
    
    private static class SymbolConversion implements ExtraEncoding
    {
        private static final IntHashtable t1;
        private static final IntHashtable t2;
        private IntHashtable translation;
        private static final char[] table1;
        private static final char[] table2;
        
        SymbolConversion(final boolean symbol) {
            if (symbol) {
                this.translation = SymbolConversion.t1;
            }
            else {
                this.translation = SymbolConversion.t2;
            }
        }
        
        @Override
        public byte[] charToByte(final String text, final String encoding) {
            final char[] cc = text.toCharArray();
            final byte[] b = new byte[cc.length];
            int ptr = 0;
            final int len = cc.length;
            for (final char c : cc) {
                final byte v = (byte)this.translation.get(c);
                if (v != 0) {
                    b[ptr++] = v;
                }
            }
            if (ptr == len) {
                return b;
            }
            final byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }
        
        @Override
        public byte[] charToByte(final char char1, final String encoding) {
            final byte v = (byte)this.translation.get(char1);
            if (v != 0) {
                return new byte[] { v };
            }
            return new byte[0];
        }
        
        @Override
        public String byteToChar(final byte[] b, final String encoding) {
            return null;
        }
        
        static {
            t1 = new IntHashtable();
            t2 = new IntHashtable();
            table1 = new char[] { ' ', '!', '\u2200', '#', '\u2203', '%', '&', '\u220b', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '\u2245', '\u0391', '\u0392', '\u03a7', '\u0394', '\u0395', '\u03a6', '\u0393', '\u0397', '\u0399', '\u03d1', '\u039a', '\u039b', '\u039c', '\u039d', '\u039f', '\u03a0', '\u0398', '\u03a1', '\u03a3', '\u03a4', '\u03a5', '\u03c2', '\u03a9', '\u039e', '\u03a8', '\u0396', '[', '\u2234', ']', '\u22a5', '_', '\u0305', '\u03b1', '\u03b2', '\u03c7', '\u03b4', '\u03b5', '\u03d5', '\u03b3', '\u03b7', '\u03b9', '\u03c6', '\u03ba', '\u03bb', '\u03bc', '\u03bd', '\u03bf', '\u03c0', '\u03b8', '\u03c1', '\u03c3', '\u03c4', '\u03c5', '\u03d6', '\u03c9', '\u03be', '\u03c8', '\u03b6', '{', '|', '}', '~', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u20ac', '\u03d2', '\u2032', '\u2264', '\u2044', '\u221e', '\u0192', '\u2663', '\u2666', '\u2665', '\u2660', '\u2194', '\u2190', '\u2191', '\u2192', '\u2193', '°', '±', '\u2033', '\u2265', '\u00d7', '\u221d', '\u2202', '\u2022', '\u00f7', '\u2260', '\u2261', '\u2248', '\u2026', '\u2502', '\u2500', '\u21b5', '\u2135', '\u2111', '\u211c', '\u2118', '\u2297', '\u2295', '\u2205', '\u2229', '\u222a', '\u2283', '\u2287', '\u2284', '\u2282', '\u2286', '\u2208', '\u2209', '\u2220', '\u2207', '®', '©', '\u2122', '\u220f', '\u221a', '\u2022', '¬', '\u2227', '\u2228', '\u21d4', '\u21d0', '\u21d1', '\u21d2', '\u21d3', '\u25ca', '\u2329', '\0', '\0', '\0', '\u2211', '\u239b', '\u239c', '\u239d', '\u23a1', '\u23a2', '\u23a3', '\u23a7', '\u23a8', '\u23a9', '\u23aa', '\0', '\u232a', '\u222b', '\u2320', '\u23ae', '\u2321', '\u239e', '\u239f', '\u23a0', '\u23a4', '\u23a5', '\u23a6', '\u23ab', '\u23ac', '\u23ad', '\0' };
            table2 = new char[] { ' ', '\u2701', '\u2702', '\u2703', '\u2704', '\u260e', '\u2706', '\u2707', '\u2708', '\u2709', '\u261b', '\u261e', '\u270c', '\u270d', '\u270e', '\u270f', '\u2710', '\u2711', '\u2712', '\u2713', '\u2714', '\u2715', '\u2716', '\u2717', '\u2718', '\u2719', '\u271a', '\u271b', '\u271c', '\u271d', '\u271e', '\u271f', '\u2720', '\u2721', '\u2722', '\u2723', '\u2724', '\u2725', '\u2726', '\u2727', '\u2605', '\u2729', '\u272a', '\u272b', '\u272c', '\u272d', '\u272e', '\u272f', '\u2730', '\u2731', '\u2732', '\u2733', '\u2734', '\u2735', '\u2736', '\u2737', '\u2738', '\u2739', '\u273a', '\u273b', '\u273c', '\u273d', '\u273e', '\u273f', '\u2740', '\u2741', '\u2742', '\u2743', '\u2744', '\u2745', '\u2746', '\u2747', '\u2748', '\u2749', '\u274a', '\u274b', '\u25cf', '\u274d', '\u25a0', '\u274f', '\u2750', '\u2751', '\u2752', '\u25b2', '\u25bc', '\u25c6', '\u2756', '\u25d7', '\u2758', '\u2759', '\u275a', '\u275b', '\u275c', '\u275d', '\u275e', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\u2761', '\u2762', '\u2763', '\u2764', '\u2765', '\u2766', '\u2767', '\u2663', '\u2666', '\u2665', '\u2660', '\u2460', '\u2461', '\u2462', '\u2463', '\u2464', '\u2465', '\u2466', '\u2467', '\u2468', '\u2469', '\u2776', '\u2777', '\u2778', '\u2779', '\u277a', '\u277b', '\u277c', '\u277d', '\u277e', '\u277f', '\u2780', '\u2781', '\u2782', '\u2783', '\u2784', '\u2785', '\u2786', '\u2787', '\u2788', '\u2789', '\u278a', '\u278b', '\u278c', '\u278d', '\u278e', '\u278f', '\u2790', '\u2791', '\u2792', '\u2793', '\u2794', '\u2192', '\u2194', '\u2195', '\u2798', '\u2799', '\u279a', '\u279b', '\u279c', '\u279d', '\u279e', '\u279f', '\u27a0', '\u27a1', '\u27a2', '\u27a3', '\u27a4', '\u27a5', '\u27a6', '\u27a7', '\u27a8', '\u27a9', '\u27aa', '\u27ab', '\u27ac', '\u27ad', '\u27ae', '\u27af', '\0', '\u27b1', '\u27b2', '\u27b3', '\u27b4', '\u27b5', '\u27b6', '\u27b7', '\u27b8', '\u27b9', '\u27ba', '\u27bb', '\u27bc', '\u27bd', '\u27be', '\0' };
            for (int k = 0; k < SymbolConversion.table1.length; ++k) {
                final int v = SymbolConversion.table1[k];
                if (v != 0) {
                    SymbolConversion.t1.put(v, k + 32);
                }
            }
            for (int k = 0; k < SymbolConversion.table2.length; ++k) {
                final int v = SymbolConversion.table2[k];
                if (v != 0) {
                    SymbolConversion.t2.put(v, k + 32);
                }
            }
        }
    }
    
    private static class SymbolTTConversion implements ExtraEncoding
    {
        @Override
        public byte[] charToByte(final char char1, final String encoding) {
            if ((char1 & '\uff00') == 0x0 || (char1 & '\uff00') == 0xF000) {
                return new byte[] { (byte)char1 };
            }
            return new byte[0];
        }
        
        @Override
        public byte[] charToByte(final String text, final String encoding) {
            final char[] ch = text.toCharArray();
            final byte[] b = new byte[ch.length];
            int ptr = 0;
            final int len = ch.length;
            for (final char c : ch) {
                if ((c & '\uff00') == 0x0 || (c & '\uff00') == 0xF000) {
                    b[ptr++] = (byte)c;
                }
            }
            if (ptr == len) {
                return b;
            }
            final byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }
        
        @Override
        public String byteToChar(final byte[] b, final String encoding) {
            return null;
        }
    }
}
