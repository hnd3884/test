package org.apache.commons.fileupload.util.mime;

import java.util.HashMap;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public final class MimeUtility
{
    private static final String US_ASCII_CHARSET = "US-ASCII";
    private static final String BASE64_ENCODING_MARKER = "B";
    private static final String QUOTEDPRINTABLE_ENCODING_MARKER = "Q";
    private static final String ENCODED_TOKEN_MARKER = "=?";
    private static final String ENCODED_TOKEN_FINISHER = "?=";
    private static final String LINEAR_WHITESPACE = " \t\r\n";
    private static final Map<String, String> MIME2JAVA;
    
    private MimeUtility() {
    }
    
    public static String decodeText(final String text) throws UnsupportedEncodingException {
        if (text.indexOf("=?") < 0) {
            return text;
        }
        int offset = 0;
        final int endOffset = text.length();
        int startWhiteSpace = -1;
        int endWhiteSpace = -1;
        final StringBuilder decodedText = new StringBuilder(text.length());
        boolean previousTokenEncoded = false;
        while (offset < endOffset) {
            char ch = text.charAt(offset);
            if (" \t\r\n".indexOf(ch) != -1) {
                startWhiteSpace = offset;
                while (offset < endOffset) {
                    ch = text.charAt(offset);
                    if (" \t\r\n".indexOf(ch) == -1) {
                        endWhiteSpace = offset;
                        break;
                    }
                    ++offset;
                }
            }
            else {
                final int wordStart = offset;
                while (offset < endOffset) {
                    ch = text.charAt(offset);
                    if (" \t\r\n".indexOf(ch) != -1) {
                        break;
                    }
                    ++offset;
                }
                final String word = text.substring(wordStart, offset);
                if (word.startsWith("=?")) {
                    try {
                        final String decodedWord = decodeWord(word);
                        if (!previousTokenEncoded && startWhiteSpace != -1) {
                            decodedText.append(text.substring(startWhiteSpace, endWhiteSpace));
                            startWhiteSpace = -1;
                        }
                        previousTokenEncoded = true;
                        decodedText.append(decodedWord);
                        continue;
                    }
                    catch (final ParseException ex) {}
                }
                if (startWhiteSpace != -1) {
                    decodedText.append(text.substring(startWhiteSpace, endWhiteSpace));
                    startWhiteSpace = -1;
                }
                previousTokenEncoded = false;
                decodedText.append(word);
            }
        }
        return decodedText.toString();
    }
    
    private static String decodeWord(final String word) throws ParseException, UnsupportedEncodingException {
        if (!word.startsWith("=?")) {
            throw new ParseException("Invalid RFC 2047 encoded-word: " + word);
        }
        final int charsetPos = word.indexOf(63, 2);
        if (charsetPos == -1) {
            throw new ParseException("Missing charset in RFC 2047 encoded-word: " + word);
        }
        final String charset = word.substring(2, charsetPos).toLowerCase(Locale.ENGLISH);
        final int encodingPos = word.indexOf(63, charsetPos + 1);
        if (encodingPos == -1) {
            throw new ParseException("Missing encoding in RFC 2047 encoded-word: " + word);
        }
        final String encoding = word.substring(charsetPos + 1, encodingPos);
        final int encodedTextPos = word.indexOf("?=", encodingPos + 1);
        if (encodedTextPos == -1) {
            throw new ParseException("Missing encoded text in RFC 2047 encoded-word: " + word);
        }
        final String encodedText = word.substring(encodingPos + 1, encodedTextPos);
        if (encodedText.length() == 0) {
            return "";
        }
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream(encodedText.length());
            final byte[] encodedData = encodedText.getBytes("US-ASCII");
            if (encoding.equals("B")) {
                Base64Decoder.decode(encodedData, out);
            }
            else {
                if (!encoding.equals("Q")) {
                    throw new UnsupportedEncodingException("Unknown RFC 2047 encoding: " + encoding);
                }
                QuotedPrintableDecoder.decode(encodedData, out);
            }
            final byte[] decodedData = out.toByteArray();
            return new String(decodedData, javaCharset(charset));
        }
        catch (final IOException e) {
            throw new UnsupportedEncodingException("Invalid RFC 2047 encoding");
        }
    }
    
    private static String javaCharset(final String charset) {
        if (charset == null) {
            return null;
        }
        final String mappedCharset = MimeUtility.MIME2JAVA.get(charset.toLowerCase(Locale.ENGLISH));
        if (mappedCharset == null) {
            return charset;
        }
        return mappedCharset;
    }
    
    static {
        (MIME2JAVA = new HashMap<String, String>()).put("iso-2022-cn", "ISO2022CN");
        MimeUtility.MIME2JAVA.put("iso-2022-kr", "ISO2022KR");
        MimeUtility.MIME2JAVA.put("utf-8", "UTF8");
        MimeUtility.MIME2JAVA.put("utf8", "UTF8");
        MimeUtility.MIME2JAVA.put("ja_jp.iso2022-7", "ISO2022JP");
        MimeUtility.MIME2JAVA.put("ja_jp.eucjp", "EUCJIS");
        MimeUtility.MIME2JAVA.put("euc-kr", "KSC5601");
        MimeUtility.MIME2JAVA.put("euckr", "KSC5601");
        MimeUtility.MIME2JAVA.put("us-ascii", "ISO-8859-1");
        MimeUtility.MIME2JAVA.put("x-us-ascii", "ISO-8859-1");
    }
}
