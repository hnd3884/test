package javax.mail.internet;

import com.sun.mail.util.PropUtil;
import java.util.HashMap;
import java.util.NoSuchElementException;
import com.sun.mail.util.LineInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.StringReader;
import com.sun.mail.util.QDecoderStream;
import java.io.ByteArrayInputStream;
import com.sun.mail.util.ASCIIUtility;
import java.io.ByteArrayOutputStream;
import com.sun.mail.util.QEncoderStream;
import com.sun.mail.util.BEncoderStream;
import java.util.StringTokenizer;
import com.sun.mail.util.UUEncoderStream;
import com.sun.mail.util.QPEncoderStream;
import com.sun.mail.util.BASE64EncoderStream;
import javax.mail.MessagingException;
import com.sun.mail.util.UUDecoderStream;
import com.sun.mail.util.QPDecoderStream;
import com.sun.mail.util.BASE64DecoderStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.io.InputStream;
import java.io.IOException;
import javax.mail.EncodingAware;
import javax.activation.DataSource;
import java.util.Map;

public class MimeUtility
{
    public static final int ALL = -1;
    private static final Map<String, Boolean> nonAsciiCharsetMap;
    private static final boolean decodeStrict;
    private static final boolean encodeEolStrict;
    private static final boolean ignoreUnknownEncoding;
    private static final boolean allowUtf8;
    private static final boolean foldEncodedWords;
    private static final boolean foldText;
    private static String defaultJavaCharset;
    private static String defaultMIMECharset;
    private static Map<String, String> mime2java;
    private static Map<String, String> java2mime;
    static final int ALL_ASCII = 1;
    static final int MOSTLY_ASCII = 2;
    static final int MOSTLY_NONASCII = 3;
    
    private MimeUtility() {
    }
    
    public static String getEncoding(final DataSource ds) {
        ContentType cType = null;
        InputStream is = null;
        String encoding = null;
        if (ds instanceof EncodingAware) {
            encoding = ((EncodingAware)ds).getEncoding();
            if (encoding != null) {
                return encoding;
            }
        }
        try {
            cType = new ContentType(ds.getContentType());
            is = ds.getInputStream();
            final boolean isText = cType.match("text/*");
            final int i = checkAscii(is, -1, !isText);
            switch (i) {
                case 1: {
                    encoding = "7bit";
                    break;
                }
                case 2: {
                    if (isText && nonAsciiCharset(cType)) {
                        encoding = "base64";
                        break;
                    }
                    encoding = "quoted-printable";
                    break;
                }
                default: {
                    encoding = "base64";
                    break;
                }
            }
        }
        catch (final Exception ex) {
            return "base64";
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (final IOException ex2) {}
        }
        return encoding;
    }
    
    private static boolean nonAsciiCharset(final ContentType ct) {
        String charset = ct.getParameter("charset");
        if (charset == null) {
            return false;
        }
        charset = charset.toLowerCase(Locale.ENGLISH);
        Boolean bool;
        synchronized (MimeUtility.nonAsciiCharsetMap) {
            bool = MimeUtility.nonAsciiCharsetMap.get(charset);
        }
        if (bool == null) {
            try {
                final byte[] b = "\r\n".getBytes(charset);
                bool = (b.length != 2 || b[0] != 13 || b[1] != 10);
            }
            catch (final UnsupportedEncodingException uex) {
                bool = Boolean.FALSE;
            }
            catch (final RuntimeException ex) {
                bool = Boolean.TRUE;
            }
            synchronized (MimeUtility.nonAsciiCharsetMap) {
                MimeUtility.nonAsciiCharsetMap.put(charset, bool);
            }
        }
        return bool;
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
        if (!MimeUtility.ignoreUnknownEncoding) {
            throw new MessagingException("Unknown encoding: " + encoding);
        }
        return is;
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
        final StringBuilder sb = new StringBuilder();
        final StringBuilder wsb = new StringBuilder();
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
                        sb.append((CharSequence)wsb);
                    }
                    prevWasEncoded = true;
                }
                catch (final ParseException pex) {
                    word = s;
                    if (!MimeUtility.decodeStrict) {
                        final String dword = decodeInnerWords(word);
                        if (dword != word) {
                            if (!prevWasEncoded || !word.startsWith("=?")) {
                                if (wsb.length() > 0) {
                                    sb.append((CharSequence)wsb);
                                }
                            }
                            prevWasEncoded = word.endsWith("?=");
                            word = dword;
                        }
                        else {
                            if (wsb.length() > 0) {
                                sb.append((CharSequence)wsb);
                            }
                            prevWasEncoded = false;
                        }
                    }
                    else {
                        if (wsb.length() > 0) {
                            sb.append((CharSequence)wsb);
                        }
                        prevWasEncoded = false;
                    }
                }
                sb.append(word);
                wsb.setLength(0);
            }
        }
        sb.append((CharSequence)wsb);
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
        final StringBuilder outb = new StringBuilder();
        doEncode(string, b64, jcharset, 68 - charset.length(), "=?" + charset + "?" + encoding + "?", true, encodingWord, outb);
        return outb.toString();
    }
    
    private static void doEncode(final String string, final boolean b64, final String jcharset, final int avail, final String prefix, final boolean first, final boolean encodingWord, final StringBuilder buf) throws UnsupportedEncodingException {
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
            int split = size / 2;
            if (Character.isHighSurrogate(string.charAt(split - 1))) {
                --split;
            }
            if (split > 0) {
                doEncode(string.substring(0, split), b64, jcharset, avail, prefix, first, encodingWord, buf);
            }
            doEncode(string.substring(split, size), b64, jcharset, avail, prefix, false, encodingWord, buf);
        }
        else {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
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
            throw new ParseException("encoded word does not start with \"=?\": " + eword);
        }
        int start = 2;
        int pos;
        if ((pos = eword.indexOf(63, start)) == -1) {
            throw new ParseException("encoded word does not include charset: " + eword);
        }
        String charset = eword.substring(start, pos);
        final int lpos = charset.indexOf(42);
        if (lpos >= 0) {
            charset = charset.substring(0, lpos);
        }
        charset = javaCharset(charset);
        start = pos + 1;
        if ((pos = eword.indexOf(63, start)) == -1) {
            throw new ParseException("encoded word does not include encoding: " + eword);
        }
        final String encoding = eword.substring(start, pos);
        start = pos + 1;
        if ((pos = eword.indexOf("?=", start)) == -1) {
            throw new ParseException("encoded word does not end with \"?=\": " + eword);
        }
        final String word = eword.substring(start, pos);
        try {
            String decodedWord;
            if (word.length() > 0) {
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
                decodedWord = ((count <= 0) ? "" : new String(bytes, 0, count, charset));
            }
            else {
                decodedWord = "";
            }
            if (pos + 2 < eword.length()) {
                String rest = eword.substring(pos + 2);
                if (!MimeUtility.decodeStrict) {
                    rest = decodeInnerWords(rest);
                }
                decodedWord += rest;
            }
            return decodedWord;
        }
        catch (final UnsupportedEncodingException uex) {
            throw uex;
        }
        catch (final IOException ioex) {
            throw new ParseException(ioex.toString());
        }
        catch (final IllegalArgumentException iex) {
            throw new UnsupportedEncodingException(charset);
        }
    }
    
    private static String decodeInnerWords(final String word) throws UnsupportedEncodingException {
        int start = 0;
        final StringBuilder buf = new StringBuilder();
        int i;
        while ((i = word.indexOf("=?", start)) >= 0) {
            buf.append(word.substring(start, i));
            int end = word.indexOf(63, i + 2);
            if (end < 0) {
                break;
            }
            end = word.indexOf(63, end + 1);
            if (end < 0) {
                break;
            }
            end = word.indexOf("?=", end + 1);
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
        final int len = (word == null) ? 0 : word.length();
        if (len == 0) {
            return "\"\"";
        }
        boolean needQuoting = false;
        for (int i = 0; i < len; ++i) {
            final char c = word.charAt(i);
            if (c == '\"' || c == '\\' || c == '\r' || c == '\n') {
                final StringBuilder sb = new StringBuilder(len + 3);
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
            if (c < ' ' || (c >= '\u007f' && !MimeUtility.allowUtf8) || specials.indexOf(c) >= 0) {
                needQuoting = true;
            }
        }
        if (needQuoting) {
            final StringBuilder sb2 = new StringBuilder(len + 2);
            sb2.append('\"').append(word).append('\"');
            return sb2.toString();
        }
        return word;
    }
    
    public static String fold(int used, String s) {
        if (!MimeUtility.foldText) {
            return s;
        }
        int end;
        for (end = s.length() - 1; end >= 0; --end) {
            final char c = s.charAt(end);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                break;
            }
        }
        if (end != s.length() - 1) {
            s = s.substring(0, end + 1);
        }
        if (used + s.length() <= 76) {
            return makesafe(s);
        }
        final StringBuilder sb = new StringBuilder(s.length() + 4);
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
        return makesafe(sb);
    }
    
    private static String makesafe(final CharSequence s) {
        int i;
        for (i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (c == '\r') {
                break;
            }
            if (c == '\n') {
                break;
            }
        }
        if (i == s.length()) {
            return s.toString();
        }
        final StringBuilder sb = new StringBuilder(s.length() + 1);
        final BufferedReader r = new BufferedReader(new StringReader(s.toString()));
        try {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append("\r\n");
                    assert line.length() > 0;
                    final char c2 = line.charAt(0);
                    if (c2 != ' ' && c2 != '\t') {
                        sb.append(' ');
                    }
                }
                sb.append(line);
            }
        }
        catch (final IOException ex) {
            return s.toString();
        }
        return sb.toString();
    }
    
    public static String unfold(String s) {
        if (!MimeUtility.foldText) {
            return s;
        }
        StringBuilder sb = null;
        int i;
        while ((i = indexOfAny(s, "\r\n")) >= 0) {
            final int start = i;
            final int slen = s.length();
            if (++i < slen && s.charAt(i - 1) == '\r' && s.charAt(i) == '\n') {
                ++i;
            }
            if (start > 0 && s.charAt(start - 1) == '\\') {
                if (sb == null) {
                    sb = new StringBuilder(s.length());
                }
                sb.append(s.substring(0, start - 1));
                sb.append(s.substring(start, i));
                s = s.substring(i);
            }
            else {
                final char c;
                if (i >= slen || (c = s.charAt(i)) == ' ' || c == '\t') {
                    if (sb == null) {
                        sb = new StringBuilder(s.length());
                    }
                    sb.append(s.substring(0, start));
                    s = s.substring(i);
                }
                else {
                    if (sb == null) {
                        sb = new StringBuilder(s.length());
                    }
                    sb.append(s.substring(0, i));
                    s = s.substring(i);
                }
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
        String alias = MimeUtility.mime2java.get(charset.toLowerCase(Locale.ENGLISH));
        if (alias != null) {
            try {
                Charset.forName(alias);
            }
            catch (final Exception ex) {
                alias = null;
            }
        }
        return (alias == null) ? charset : alias;
    }
    
    public static String mimeCharset(final String charset) {
        if (MimeUtility.java2mime == null || charset == null) {
            return charset;
        }
        final String alias = MimeUtility.java2mime.get(charset.toLowerCase(Locale.ENGLISH));
        return (alias == null) ? charset : alias;
    }
    
    public static String getDefaultJavaCharset() {
        if (MimeUtility.defaultJavaCharset == null) {
            String mimecs = null;
            try {
                mimecs = System.getProperty("mail.mime.charset");
            }
            catch (final SecurityException ex) {}
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
            try {
                MimeUtility.defaultMIMECharset = System.getProperty("mail.mime.charset");
            }
            catch (final SecurityException ex) {}
        }
        if (MimeUtility.defaultMIMECharset == null) {
            MimeUtility.defaultMIMECharset = mimeCharset(getDefaultJavaCharset());
        }
        return MimeUtility.defaultMIMECharset;
    }
    
    private static void loadMappings(final LineInputStream is, final Map<String, String> table) {
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
                table.put(key.toLowerCase(Locale.ENGLISH), value);
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
        nonAsciiCharsetMap = new HashMap<String, Boolean>();
        decodeStrict = PropUtil.getBooleanSystemProperty("mail.mime.decodetext.strict", true);
        encodeEolStrict = PropUtil.getBooleanSystemProperty("mail.mime.encodeeol.strict", false);
        ignoreUnknownEncoding = PropUtil.getBooleanSystemProperty("mail.mime.ignoreunknownencoding", false);
        allowUtf8 = PropUtil.getBooleanSystemProperty("mail.mime.allowutf8", false);
        foldEncodedWords = PropUtil.getBooleanSystemProperty("mail.mime.foldencodedwords", false);
        foldText = PropUtil.getBooleanSystemProperty("mail.mime.foldtext", true);
        MimeUtility.java2mime = new HashMap<String, String>(40);
        MimeUtility.mime2java = new HashMap<String, String>(14);
        try {
            InputStream is = MimeUtility.class.getResourceAsStream("/META-INF/javamail.charset.map");
            if (is != null) {
                try {
                    is = new LineInputStream(is);
                    loadMappings((LineInputStream)is, MimeUtility.java2mime);
                    loadMappings((LineInputStream)is, MimeUtility.mime2java);
                }
                finally {
                    try {
                        is.close();
                    }
                    catch (final Exception ex) {}
                }
            }
        }
        catch (final Exception ex2) {}
        if (MimeUtility.java2mime.isEmpty()) {
            MimeUtility.java2mime.put("8859_1", "ISO-8859-1");
            MimeUtility.java2mime.put("iso8859_1", "ISO-8859-1");
            MimeUtility.java2mime.put("iso8859-1", "ISO-8859-1");
            MimeUtility.java2mime.put("8859_2", "ISO-8859-2");
            MimeUtility.java2mime.put("iso8859_2", "ISO-8859-2");
            MimeUtility.java2mime.put("iso8859-2", "ISO-8859-2");
            MimeUtility.java2mime.put("8859_3", "ISO-8859-3");
            MimeUtility.java2mime.put("iso8859_3", "ISO-8859-3");
            MimeUtility.java2mime.put("iso8859-3", "ISO-8859-3");
            MimeUtility.java2mime.put("8859_4", "ISO-8859-4");
            MimeUtility.java2mime.put("iso8859_4", "ISO-8859-4");
            MimeUtility.java2mime.put("iso8859-4", "ISO-8859-4");
            MimeUtility.java2mime.put("8859_5", "ISO-8859-5");
            MimeUtility.java2mime.put("iso8859_5", "ISO-8859-5");
            MimeUtility.java2mime.put("iso8859-5", "ISO-8859-5");
            MimeUtility.java2mime.put("8859_6", "ISO-8859-6");
            MimeUtility.java2mime.put("iso8859_6", "ISO-8859-6");
            MimeUtility.java2mime.put("iso8859-6", "ISO-8859-6");
            MimeUtility.java2mime.put("8859_7", "ISO-8859-7");
            MimeUtility.java2mime.put("iso8859_7", "ISO-8859-7");
            MimeUtility.java2mime.put("iso8859-7", "ISO-8859-7");
            MimeUtility.java2mime.put("8859_8", "ISO-8859-8");
            MimeUtility.java2mime.put("iso8859_8", "ISO-8859-8");
            MimeUtility.java2mime.put("iso8859-8", "ISO-8859-8");
            MimeUtility.java2mime.put("8859_9", "ISO-8859-9");
            MimeUtility.java2mime.put("iso8859_9", "ISO-8859-9");
            MimeUtility.java2mime.put("iso8859-9", "ISO-8859-9");
            MimeUtility.java2mime.put("sjis", "Shift_JIS");
            MimeUtility.java2mime.put("jis", "ISO-2022-JP");
            MimeUtility.java2mime.put("iso2022jp", "ISO-2022-JP");
            MimeUtility.java2mime.put("euc_jp", "euc-jp");
            MimeUtility.java2mime.put("koi8_r", "koi8-r");
            MimeUtility.java2mime.put("euc_cn", "euc-cn");
            MimeUtility.java2mime.put("euc_tw", "euc-tw");
            MimeUtility.java2mime.put("euc_kr", "euc-kr");
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
            MimeUtility.mime2java.put("gb2312", "GB18030");
            MimeUtility.mime2java.put("cp936", "GB18030");
            MimeUtility.mime2java.put("ms936", "GB18030");
            MimeUtility.mime2java.put("gbk", "GB18030");
        }
    }
}
