package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import java.util.NoSuchElementException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;
import java.io.InputStreamReader;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QDecoderStream;
import java.io.ByteArrayInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import java.io.ByteArrayOutputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QEncoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BEncoderStream;
import java.util.StringTokenizer;
import java.io.UnsupportedEncodingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.UUEncoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QPEncoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.UUDecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QPDecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64DecoderStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import java.io.InputStream;
import java.io.IOException;
import javax.activation.DataSource;
import java.util.Hashtable;

public class MimeUtility
{
    public static final int ALL = -1;
    private static final int BUFFER_SIZE = 1024;
    private static boolean decodeStrict;
    private static boolean encodeEolStrict;
    private static boolean foldEncodedWords;
    private static boolean foldText;
    private static String defaultJavaCharset;
    private static String defaultMIMECharset;
    private static Hashtable mime2java;
    private static Hashtable java2mime;
    static final int ALL_ASCII = 1;
    static final int MOSTLY_ASCII = 2;
    static final int MOSTLY_NONASCII = 3;
    
    private MimeUtility() {
    }
    
    public static String getEncoding(final DataSource ds) {
        ContentType cType = null;
        InputStream is = null;
        String encoding = null;
        try {
            cType = new ContentType(ds.getContentType());
            is = ds.getInputStream();
        }
        catch (final Exception ex) {
            return "base64";
        }
        final boolean isText = cType.match("text/*");
        final int i = checkAscii(is, -1, !isText);
        switch (i) {
            case 1: {
                encoding = "7bit";
                break;
            }
            case 2: {
                encoding = "quoted-printable";
                break;
            }
            default: {
                encoding = "base64";
                break;
            }
        }
        try {
            is.close();
        }
        catch (final IOException ex2) {}
        return encoding;
    }
    
    public static String getEncoding(final DataHandler dh) {
        ContentType cType = null;
        String encoding = null;
        if (dh.getName() != null) {
            return getEncoding(dh.getDataSource());
        }
        try {
            cType = new ContentType(dh.getContentType());
        }
        catch (final Exception ex) {
            return "base64";
        }
        if (cType.match("text/*")) {
            final AsciiOutputStream aos = new AsciiOutputStream(false, false);
            try {
                dh.writeTo(aos);
            }
            catch (final IOException ex2) {}
            switch (aos.getAscii()) {
                case 1: {
                    encoding = "7bit";
                    break;
                }
                case 2: {
                    encoding = "quoted-printable";
                    break;
                }
                default: {
                    encoding = "base64";
                    break;
                }
            }
        }
        else {
            final AsciiOutputStream aos = new AsciiOutputStream(true, MimeUtility.encodeEolStrict);
            try {
                dh.writeTo(aos);
            }
            catch (final IOException ex3) {}
            if (aos.getAscii() == 1) {
                encoding = "7bit";
            }
            else {
                encoding = "base64";
            }
        }
        return encoding;
    }
    
    public static InputStream decode(final InputStream is, final String encoding) throws MessagingException {
        if (encoding.equalsIgnoreCase("base64")) {
            return new BASE64DecoderStream(is);
        }
        if (encoding.equalsIgnoreCase("quoted-printable")) {
            return new QPDecoderStream(is);
        }
        if (encoding.equalsIgnoreCase("uuencode") || encoding.equalsIgnoreCase("x-uuencode") || encoding.equalsIgnoreCase("x-uue")) {
            return new UUDecoderStream(is);
        }
        if (encoding.equalsIgnoreCase("binary") || encoding.equalsIgnoreCase("7bit") || encoding.equalsIgnoreCase("8bit")) {
            return is;
        }
        throw new MessagingException("Unknown encoding: " + encoding);
    }
    
    public static OutputStream encode(final OutputStream os, final String encoding) throws MessagingException {
        if (encoding == null) {
            return os;
        }
        if (encoding.equalsIgnoreCase("base64")) {
            return new BASE64EncoderStream(os);
        }
        if (encoding.equalsIgnoreCase("quoted-printable")) {
            return new QPEncoderStream(os);
        }
        if (encoding.equalsIgnoreCase("uuencode") || encoding.equalsIgnoreCase("x-uuencode") || encoding.equalsIgnoreCase("x-uue")) {
            return new UUEncoderStream(os);
        }
        if (encoding.equalsIgnoreCase("binary") || encoding.equalsIgnoreCase("7bit") || encoding.equalsIgnoreCase("8bit")) {
            return os;
        }
        throw new MessagingException("Unknown encoding: " + encoding);
    }
    
    public static OutputStream encode(final OutputStream os, final String encoding, final String filename) throws MessagingException {
        if (encoding == null) {
            return os;
        }
        if (encoding.equalsIgnoreCase("base64")) {
            return new BASE64EncoderStream(os);
        }
        if (encoding.equalsIgnoreCase("quoted-printable")) {
            return new QPEncoderStream(os);
        }
        if (encoding.equalsIgnoreCase("uuencode") || encoding.equalsIgnoreCase("x-uuencode") || encoding.equalsIgnoreCase("x-uue")) {
            return new UUEncoderStream(os, filename);
        }
        if (encoding.equalsIgnoreCase("binary") || encoding.equalsIgnoreCase("7bit") || encoding.equalsIgnoreCase("8bit")) {
            return os;
        }
        throw new MessagingException("Unknown encoding: " + encoding);
    }
    
    public static String encodeText(final String text) throws UnsupportedEncodingException {
        return encodeText(text, null, null);
    }
    
    public static String encodeText(final String text, final String charset, final String encoding) throws UnsupportedEncodingException {
        return encodeWord(text, charset, encoding, false);
    }
    
    public static String decodeText(final String etext) throws UnsupportedEncodingException {
        final String lwsp = " \t\n\r";
        if (etext.indexOf("=?") == -1) {
            return etext;
        }
        final StringTokenizer st = new StringTokenizer(etext, lwsp, true);
        final StringBuffer sb = new StringBuffer();
        final StringBuffer wsb = new StringBuffer();
        boolean prevWasEncoded = false;
        while (st.hasMoreTokens()) {
            final String s = st.nextToken();
            final char c;
            if ((c = s.charAt(0)) == ' ' || c == '\t' || c == '\r' || c == '\n') {
                wsb.append(c);
            }
            else {
                String word;
                try {
                    word = decodeWord(s);
                    if (!prevWasEncoded && wsb.length() > 0) {
                        sb.append(wsb);
                    }
                    prevWasEncoded = true;
                }
                catch (final ParseException pex) {
                    word = s;
                    if (!MimeUtility.decodeStrict) {
                        word = decodeInnerWords(word);
                    }
                    if (wsb.length() > 0) {
                        sb.append(wsb);
                    }
                    prevWasEncoded = false;
                }
                sb.append(word);
                wsb.setLength(0);
            }
        }
        return sb.toString();
    }
    
    public static String encodeWord(final String word) throws UnsupportedEncodingException {
        return encodeWord(word, null, null);
    }
    
    public static String encodeWord(final String word, final String charset, final String encoding) throws UnsupportedEncodingException {
        return encodeWord(word, charset, encoding, true);
    }
    
    private static String encodeWord(final String string, String charset, String encoding, final boolean encodingWord) throws UnsupportedEncodingException {
        final int ascii = checkAscii(string);
        if (ascii == 1) {
            return string;
        }
        String jcharset;
        if (charset == null) {
            jcharset = getDefaultJavaCharset();
            charset = getDefaultMIMECharset();
        }
        else {
            jcharset = javaCharset(charset);
        }
        if (encoding == null) {
            if (ascii != 3) {
                encoding = "Q";
            }
            else {
                encoding = "B";
            }
        }
        boolean b64;
        if (encoding.equalsIgnoreCase("B")) {
            b64 = true;
        }
        else {
            if (!encoding.equalsIgnoreCase("Q")) {
                throw new UnsupportedEncodingException("Unknown transfer encoding: " + encoding);
            }
            b64 = false;
        }
        final StringBuffer outb = new StringBuffer();
        doEncode(string, b64, jcharset, 68 - charset.length(), "=?" + charset + "?" + encoding + "?", true, encodingWord, outb);
        return outb.toString();
    }
    
    private static void doEncode(final String string, final boolean b64, final String jcharset, final int avail, final String prefix, final boolean first, final boolean encodingWord, final StringBuffer buf) throws UnsupportedEncodingException {
        final byte[] bytes = string.getBytes(jcharset);
        int len;
        if (b64) {
            len = BEncoderStream.encodedLength(bytes);
        }
        else {
            len = QEncoderStream.encodedLength(bytes, encodingWord);
        }
        final int size;
        if (len > avail && (size = string.length()) > 1) {
            doEncode(string.substring(0, size / 2), b64, jcharset, avail, prefix, first, encodingWord, buf);
            doEncode(string.substring(size / 2, size), b64, jcharset, avail, prefix, false, encodingWord, buf);
        }
        else {
            final ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
            OutputStream eos;
            if (b64) {
                eos = new BEncoderStream(os);
            }
            else {
                eos = new QEncoderStream(os, encodingWord);
            }
            try {
                eos.write(bytes);
                eos.close();
            }
            catch (final IOException ex) {}
            final byte[] encodedBytes = os.toByteArray();
            if (!first) {
                if (MimeUtility.foldEncodedWords) {
                    buf.append("\r\n ");
                }
                else {
                    buf.append(" ");
                }
            }
            buf.append(prefix);
            for (int i = 0; i < encodedBytes.length; ++i) {
                buf.append((char)encodedBytes[i]);
            }
            buf.append("?=");
        }
    }
    
    public static String decodeWord(final String eword) throws ParseException, UnsupportedEncodingException {
        if (!eword.startsWith("=?")) {
            throw new ParseException();
        }
        int start = 2;
        int pos;
        if ((pos = eword.indexOf(63, start)) == -1) {
            throw new ParseException();
        }
        final String charset = javaCharset(eword.substring(start, pos));
        start = pos + 1;
        if ((pos = eword.indexOf(63, start)) == -1) {
            throw new ParseException();
        }
        final String encoding = eword.substring(start, pos);
        start = pos + 1;
        if ((pos = eword.indexOf("?=", start)) == -1) {
            throw new ParseException();
        }
        final String word = eword.substring(start, pos);
        try {
            final ByteArrayInputStream bis = new ByteArrayInputStream(ASCIIUtility.getBytes(word));
            InputStream is;
            if (encoding.equalsIgnoreCase("B")) {
                is = new BASE64DecoderStream(bis);
            }
            else {
                if (!encoding.equalsIgnoreCase("Q")) {
                    throw new UnsupportedEncodingException("unknown encoding: " + encoding);
                }
                is = new QDecoderStream(bis);
            }
            int count = bis.available();
            final byte[] bytes = new byte[count];
            count = is.read(bytes, 0, count);
            String s = new String(bytes, 0, count, charset);
            if (pos + 2 < eword.length()) {
                String rest = eword.substring(pos + 2);
                if (!MimeUtility.decodeStrict) {
                    rest = decodeInnerWords(rest);
                }
                s += rest;
            }
            return s;
        }
        catch (final UnsupportedEncodingException uex) {
            throw uex;
        }
        catch (final IOException ioex) {
            throw new ParseException();
        }
        catch (final IllegalArgumentException iex) {
            throw new UnsupportedEncodingException();
        }
    }
    
    private static String decodeInnerWords(final String word) throws UnsupportedEncodingException {
        int start = 0;
        final StringBuffer buf = new StringBuffer();
        int i;
        while ((i = word.indexOf("=?", start)) >= 0) {
            buf.append(word.substring(start, i));
            final int end = word.indexOf("?=", i);
            if (end < 0) {
                break;
            }
            String s = word.substring(i, end + 2);
            try {
                s = decodeWord(s);
            }
            catch (final ParseException ex) {}
            buf.append(s);
            start = end + 2;
        }
        if (start == 0) {
            return word;
        }
        if (start < word.length()) {
            buf.append(word.substring(start));
        }
        return buf.toString();
    }
    
    public static String quote(final String word, final String specials) {
        final int len = word.length();
        boolean needQuoting = false;
        for (int i = 0; i < len; ++i) {
            final char c = word.charAt(i);
            if (c == '\"' || c == '\\' || c == '\r' || c == '\n') {
                final StringBuffer sb = new StringBuffer(len + 3);
                sb.append('\"');
                sb.append(word.substring(0, i));
                int lastc = 0;
                for (int j = i; j < len; ++j) {
                    final char cc = word.charAt(j);
                    if (cc == '\"' || cc == '\\' || cc == '\r' || cc == '\n') {
                        if (cc != '\n' || lastc != 13) {
                            sb.append('\\');
                        }
                    }
                    sb.append(cc);
                    lastc = cc;
                }
                sb.append('\"');
                return sb.toString();
            }
            if (c < ' ' || c >= '\u007f' || specials.indexOf(c) >= 0) {
                needQuoting = true;
            }
        }
        if (needQuoting) {
            final StringBuffer sb2 = new StringBuffer(len + 2);
            sb2.append('\"').append(word).append('\"');
            return sb2.toString();
        }
        return word;
    }
    
    static String fold(int used, String s) {
        if (!MimeUtility.foldText) {
            return s;
        }
        int end;
        for (end = s.length() - 1; end >= 0; --end) {
            final char c = s.charAt(end);
            if (c != ' ' && c != '\t') {
                break;
            }
        }
        if (end != s.length() - 1) {
            s = s.substring(0, end + 1);
        }
        if (used + s.length() <= 76) {
            return s;
        }
        final StringBuffer sb = new StringBuffer(s.length() + 4);
        char lastc = '\0';
        while (used + s.length() > 76) {
            int lastspace = -1;
            for (int i = 0; i < s.length() && (lastspace == -1 || used + i <= 76); ++i) {
                final char c = s.charAt(i);
                if ((c == ' ' || c == '\t') && lastc != ' ' && lastc != '\t') {
                    lastspace = i;
                }
                lastc = c;
            }
            if (lastspace == -1) {
                sb.append(s);
                s = "";
                used = 0;
                break;
            }
            sb.append(s.substring(0, lastspace));
            sb.append("\r\n");
            lastc = s.charAt(lastspace);
            sb.append(lastc);
            s = s.substring(lastspace + 1);
            used = 1;
        }
        sb.append(s);
        return sb.toString();
    }
    
    static String unfold(String s) {
        if (!MimeUtility.foldText) {
            return s;
        }
        StringBuffer sb = null;
        int i;
        while ((i = indexOfAny(s, "\r\n")) >= 0) {
            final int start = i;
            final int l = s.length();
            if (++i < l && s.charAt(i - 1) == '\r' && s.charAt(i) == '\n') {
                ++i;
            }
            if (start == 0 || s.charAt(start - 1) != '\\') {
                char c;
                if (i < l && ((c = s.charAt(i)) == ' ' || c == '\t')) {
                    ++i;
                    while (i < l && ((c = s.charAt(i)) == ' ' || c == '\t')) {
                        ++i;
                    }
                    if (sb == null) {
                        sb = new StringBuffer(s.length());
                    }
                    if (start != 0) {
                        sb.append(s.substring(0, start));
                        sb.append(' ');
                    }
                    s = s.substring(i);
                }
                else {
                    if (sb == null) {
                        sb = new StringBuffer(s.length());
                    }
                    sb.append(s.substring(0, i));
                    s = s.substring(i);
                }
            }
            else {
                if (sb == null) {
                    sb = new StringBuffer(s.length());
                }
                sb.append(s.substring(0, start - 1));
                sb.append(s.substring(start, i));
                s = s.substring(i);
            }
        }
        if (sb != null) {
            sb.append(s);
            return sb.toString();
        }
        return s;
    }
    
    private static int indexOfAny(final String s, final String any) {
        return indexOfAny(s, any, 0);
    }
    
    private static int indexOfAny(final String s, final String any, final int start) {
        try {
            for (int len = s.length(), i = start; i < len; ++i) {
                if (any.indexOf(s.charAt(i)) >= 0) {
                    return i;
                }
            }
            return -1;
        }
        catch (final StringIndexOutOfBoundsException e) {
            return -1;
        }
    }
    
    public static String javaCharset(final String charset) {
        if (MimeUtility.mime2java == null || charset == null) {
            return charset;
        }
        final String alias = MimeUtility.mime2java.get(charset.toLowerCase());
        return (alias == null) ? charset : alias;
    }
    
    public static String mimeCharset(final String charset) {
        if (MimeUtility.java2mime == null || charset == null) {
            return charset;
        }
        final String alias = MimeUtility.java2mime.get(charset.toLowerCase());
        return (alias == null) ? charset : alias;
    }
    
    public static String getDefaultJavaCharset() {
        if (MimeUtility.defaultJavaCharset == null) {
            String mimecs = null;
            mimecs = SAAJUtil.getSystemProperty("mail.mime.charset");
            if (mimecs != null && mimecs.length() > 0) {
                return MimeUtility.defaultJavaCharset = javaCharset(mimecs);
            }
            try {
                MimeUtility.defaultJavaCharset = System.getProperty("file.encoding", "8859_1");
            }
            catch (final SecurityException sex) {
                class NullInputStream extends InputStream
                {
                    @Override
                    public int read() {
                        return 0;
                    }
                }
                final InputStreamReader reader = new InputStreamReader(new NullInputStream());
                MimeUtility.defaultJavaCharset = reader.getEncoding();
                if (MimeUtility.defaultJavaCharset == null) {
                    MimeUtility.defaultJavaCharset = "8859_1";
                }
            }
        }
        return MimeUtility.defaultJavaCharset;
    }
    
    static String getDefaultMIMECharset() {
        if (MimeUtility.defaultMIMECharset == null) {
            MimeUtility.defaultMIMECharset = SAAJUtil.getSystemProperty("mail.mime.charset");
        }
        if (MimeUtility.defaultMIMECharset == null) {
            MimeUtility.defaultMIMECharset = mimeCharset(getDefaultJavaCharset());
        }
        return MimeUtility.defaultMIMECharset;
    }
    
    private static void loadMappings(final LineInputStream is, final Hashtable table) {
        while (true) {
            String currLine;
            try {
                currLine = is.readLine();
            }
            catch (final IOException ioex) {
                break;
            }
            if (currLine == null) {
                break;
            }
            if (currLine.startsWith("--") && currLine.endsWith("--")) {
                break;
            }
            if (currLine.trim().length() == 0) {
                continue;
            }
            if (currLine.startsWith("#")) {
                continue;
            }
            final StringTokenizer tk = new StringTokenizer(currLine, " \t");
            try {
                final String key = tk.nextToken();
                final String value = tk.nextToken();
                table.put(key.toLowerCase(), value);
            }
            catch (final NoSuchElementException ex) {}
        }
    }
    
    static int checkAscii(final String s) {
        int ascii = 0;
        int non_ascii = 0;
        for (int l = s.length(), i = 0; i < l; ++i) {
            if (nonascii(s.charAt(i))) {
                ++non_ascii;
            }
            else {
                ++ascii;
            }
        }
        if (non_ascii == 0) {
            return 1;
        }
        if (ascii > non_ascii) {
            return 2;
        }
        return 3;
    }
    
    static int checkAscii(final byte[] b) {
        int ascii = 0;
        int non_ascii = 0;
        for (int i = 0; i < b.length; ++i) {
            if (nonascii(b[i] & 0xFF)) {
                ++non_ascii;
            }
            else {
                ++ascii;
            }
        }
        if (non_ascii == 0) {
            return 1;
        }
        if (ascii > non_ascii) {
            return 2;
        }
        return 3;
    }
    
    static int checkAscii(final InputStream is, int max, final boolean breakOnNonAscii) {
        int ascii = 0;
        int non_ascii = 0;
        int block = 4096;
        int linelen = 0;
        boolean longLine = false;
        boolean badEOL = false;
        final boolean checkEOL = MimeUtility.encodeEolStrict && breakOnNonAscii;
        byte[] buf = null;
        if (max != 0) {
            block = ((max == -1) ? 4096 : Math.min(max, 4096));
            buf = new byte[block];
        }
        while (max != 0) {
            int len;
            try {
                if ((len = is.read(buf, 0, block)) == -1) {
                    break;
                }
                int lastb = 0;
                for (int i = 0; i < len; ++i) {
                    final int b = buf[i] & 0xFF;
                    if (checkEOL && ((lastb == 13 && b != 10) || (lastb != 13 && b == 10))) {
                        badEOL = true;
                    }
                    if (b == 13 || b == 10) {
                        linelen = 0;
                    }
                    else if (++linelen > 998) {
                        longLine = true;
                    }
                    if (nonascii(b)) {
                        if (breakOnNonAscii) {
                            return 3;
                        }
                        ++non_ascii;
                    }
                    else {
                        ++ascii;
                    }
                    lastb = b;
                }
            }
            catch (final IOException ioex) {
                break;
            }
            if (max != -1) {
                max -= len;
            }
        }
        if (max == 0 && breakOnNonAscii) {
            return 3;
        }
        if (non_ascii == 0) {
            if (badEOL) {
                return 3;
            }
            if (longLine) {
                return 2;
            }
            return 1;
        }
        else {
            if (ascii > non_ascii) {
                return 2;
            }
            return 3;
        }
    }
    
    static final boolean nonascii(final int b) {
        return b >= 127 || (b < 32 && b != 13 && b != 10 && b != 9);
    }
    
    static {
        MimeUtility.decodeStrict = true;
        MimeUtility.encodeEolStrict = false;
        MimeUtility.foldEncodedWords = false;
        MimeUtility.foldText = true;
        try {
            String s = SAAJUtil.getSystemProperty("mail.mime.decodetext.strict");
            MimeUtility.decodeStrict = (s == null || !s.equalsIgnoreCase("false"));
            s = SAAJUtil.getSystemProperty("mail.mime.encodeeol.strict");
            MimeUtility.encodeEolStrict = (s != null && s.equalsIgnoreCase("true"));
            s = SAAJUtil.getSystemProperty("mail.mime.foldencodedwords");
            MimeUtility.foldEncodedWords = (s != null && s.equalsIgnoreCase("true"));
            s = SAAJUtil.getSystemProperty("mail.mime.foldtext");
            MimeUtility.foldText = (s == null || !s.equalsIgnoreCase("false"));
        }
        catch (final SecurityException ex) {}
        MimeUtility.java2mime = new Hashtable(40);
        MimeUtility.mime2java = new Hashtable(10);
        try {
            InputStream is = MimeUtility.class.getResourceAsStream("/META-INF/javamail.charset.map");
            if (is != null) {
                is = new LineInputStream(is);
                loadMappings((LineInputStream)is, MimeUtility.java2mime);
                loadMappings((LineInputStream)is, MimeUtility.mime2java);
            }
        }
        catch (final Exception ex2) {}
        if (MimeUtility.java2mime.isEmpty()) {
            MimeUtility.java2mime.put("8859_1", "ISO-8859-1");
            MimeUtility.java2mime.put("iso8859_1", "ISO-8859-1");
            MimeUtility.java2mime.put("ISO8859-1", "ISO-8859-1");
            MimeUtility.java2mime.put("8859_2", "ISO-8859-2");
            MimeUtility.java2mime.put("iso8859_2", "ISO-8859-2");
            MimeUtility.java2mime.put("ISO8859-2", "ISO-8859-2");
            MimeUtility.java2mime.put("8859_3", "ISO-8859-3");
            MimeUtility.java2mime.put("iso8859_3", "ISO-8859-3");
            MimeUtility.java2mime.put("ISO8859-3", "ISO-8859-3");
            MimeUtility.java2mime.put("8859_4", "ISO-8859-4");
            MimeUtility.java2mime.put("iso8859_4", "ISO-8859-4");
            MimeUtility.java2mime.put("ISO8859-4", "ISO-8859-4");
            MimeUtility.java2mime.put("8859_5", "ISO-8859-5");
            MimeUtility.java2mime.put("iso8859_5", "ISO-8859-5");
            MimeUtility.java2mime.put("ISO8859-5", "ISO-8859-5");
            MimeUtility.java2mime.put("8859_6", "ISO-8859-6");
            MimeUtility.java2mime.put("iso8859_6", "ISO-8859-6");
            MimeUtility.java2mime.put("ISO8859-6", "ISO-8859-6");
            MimeUtility.java2mime.put("8859_7", "ISO-8859-7");
            MimeUtility.java2mime.put("iso8859_7", "ISO-8859-7");
            MimeUtility.java2mime.put("ISO8859-7", "ISO-8859-7");
            MimeUtility.java2mime.put("8859_8", "ISO-8859-8");
            MimeUtility.java2mime.put("iso8859_8", "ISO-8859-8");
            MimeUtility.java2mime.put("ISO8859-8", "ISO-8859-8");
            MimeUtility.java2mime.put("8859_9", "ISO-8859-9");
            MimeUtility.java2mime.put("iso8859_9", "ISO-8859-9");
            MimeUtility.java2mime.put("ISO8859-9", "ISO-8859-9");
            MimeUtility.java2mime.put("SJIS", "Shift_JIS");
            MimeUtility.java2mime.put("MS932", "Shift_JIS");
            MimeUtility.java2mime.put("JIS", "ISO-2022-JP");
            MimeUtility.java2mime.put("ISO2022JP", "ISO-2022-JP");
            MimeUtility.java2mime.put("EUC_JP", "euc-jp");
            MimeUtility.java2mime.put("KOI8_R", "koi8-r");
            MimeUtility.java2mime.put("EUC_CN", "euc-cn");
            MimeUtility.java2mime.put("EUC_TW", "euc-tw");
            MimeUtility.java2mime.put("EUC_KR", "euc-kr");
        }
        if (MimeUtility.mime2java.isEmpty()) {
            MimeUtility.mime2java.put("iso-2022-cn", "ISO2022CN");
            MimeUtility.mime2java.put("iso-2022-kr", "ISO2022KR");
            MimeUtility.mime2java.put("utf-8", "UTF8");
            MimeUtility.mime2java.put("utf8", "UTF8");
            MimeUtility.mime2java.put("ja_jp.iso2022-7", "ISO2022JP");
            MimeUtility.mime2java.put("ja_jp.eucjp", "EUCJIS");
            MimeUtility.mime2java.put("euc-kr", "KSC5601");
            MimeUtility.mime2java.put("euckr", "KSC5601");
            MimeUtility.mime2java.put("us-ascii", "ISO-8859-1");
            MimeUtility.mime2java.put("x-us-ascii", "ISO-8859-1");
        }
    }
}
