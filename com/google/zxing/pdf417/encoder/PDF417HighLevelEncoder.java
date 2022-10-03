package com.google.zxing.pdf417.encoder;

import java.util.Arrays;
import java.math.BigInteger;
import com.google.zxing.WriterException;

final class PDF417HighLevelEncoder
{
    private static final int TEXT_COMPACTION = 0;
    private static final int BYTE_COMPACTION = 1;
    private static final int NUMERIC_COMPACTION = 2;
    private static final int SUBMODE_ALPHA = 0;
    private static final int SUBMODE_LOWER = 1;
    private static final int SUBMODE_MIXED = 2;
    private static final int SUBMODE_PUNCTUATION = 3;
    private static final int LATCH_TO_TEXT = 900;
    private static final int LATCH_TO_BYTE_PADDED = 901;
    private static final int LATCH_TO_NUMERIC = 902;
    private static final int SHIFT_TO_BYTE = 913;
    private static final int LATCH_TO_BYTE = 924;
    private static final byte[] TEXT_MIXED_RAW;
    private static final byte[] TEXT_PUNCTUATION_RAW;
    private static final byte[] MIXED;
    private static final byte[] PUNCTUATION;
    
    private PDF417HighLevelEncoder() {
    }
    
    private static byte[] getBytesForMessage(final String msg) {
        return msg.getBytes();
    }
    
    static String encodeHighLevel(final String msg, final Compaction compaction) throws WriterException {
        byte[] bytes = null;
        final StringBuilder sb = new StringBuilder(msg.length());
        final int len = msg.length();
        int p = 0;
        int encodingMode = 0;
        int textSubMode = 0;
        if (compaction == Compaction.TEXT) {
            encodeText(msg, p, len, sb, textSubMode);
        }
        else if (compaction == Compaction.BYTE) {
            encodingMode = 1;
            bytes = getBytesForMessage(msg);
            encodeBinary(bytes, p, bytes.length, encodingMode, sb);
        }
        else if (compaction == Compaction.NUMERIC) {
            encodingMode = 2;
            sb.append('\u0386');
            encodeNumeric(msg, p, len, sb);
        }
        else {
            while (p < len) {
                final int n = determineConsecutiveDigitCount(msg, p);
                if (n >= 13) {
                    sb.append('\u0386');
                    encodingMode = 2;
                    textSubMode = 0;
                    encodeNumeric(msg, p, n, sb);
                    p += n;
                }
                else {
                    final int t = determineConsecutiveTextCount(msg, p);
                    if (t >= 5 || n == len) {
                        if (encodingMode != 0) {
                            sb.append('\u0384');
                            encodingMode = 0;
                            textSubMode = 0;
                        }
                        textSubMode = encodeText(msg, p, t, sb, textSubMode);
                        p += t;
                    }
                    else {
                        if (bytes == null) {
                            bytes = getBytesForMessage(msg);
                        }
                        int b = determineConsecutiveBinaryCount(msg, bytes, p);
                        if (b == 0) {
                            b = 1;
                        }
                        if (b == 1 && encodingMode == 0) {
                            encodeBinary(bytes, p, 1, 0, sb);
                        }
                        else {
                            encodeBinary(bytes, p, b, encodingMode, sb);
                            encodingMode = 1;
                            textSubMode = 0;
                        }
                        p += b;
                    }
                }
            }
        }
        return sb.toString();
    }
    
    private static int encodeText(final CharSequence msg, final int startpos, final int count, final StringBuilder sb, final int initialSubmode) {
        final StringBuilder tmp = new StringBuilder(count);
        int submode = initialSubmode;
        int idx = 0;
        while (true) {
            final char ch = msg.charAt(startpos + idx);
            switch (submode) {
                case 0: {
                    if (isAlphaUpper(ch)) {
                        if (ch == ' ') {
                            tmp.append('\u001a');
                            break;
                        }
                        tmp.append((char)(ch - 'A'));
                        break;
                    }
                    else {
                        if (isAlphaLower(ch)) {
                            submode = 1;
                            tmp.append('\u001b');
                            continue;
                        }
                        if (isMixed(ch)) {
                            submode = 2;
                            tmp.append('\u001c');
                            continue;
                        }
                        tmp.append('\u001d');
                        tmp.append((char)PDF417HighLevelEncoder.PUNCTUATION[ch]);
                        break;
                    }
                    break;
                }
                case 1: {
                    if (isAlphaLower(ch)) {
                        if (ch == ' ') {
                            tmp.append('\u001a');
                            break;
                        }
                        tmp.append((char)(ch - 'a'));
                        break;
                    }
                    else {
                        if (isAlphaUpper(ch)) {
                            tmp.append('\u001b');
                            tmp.append((char)(ch - 'A'));
                            break;
                        }
                        if (isMixed(ch)) {
                            submode = 2;
                            tmp.append('\u001c');
                            continue;
                        }
                        tmp.append('\u001d');
                        tmp.append((char)PDF417HighLevelEncoder.PUNCTUATION[ch]);
                        break;
                    }
                    break;
                }
                case 2: {
                    if (isMixed(ch)) {
                        tmp.append((char)PDF417HighLevelEncoder.MIXED[ch]);
                        break;
                    }
                    if (isAlphaUpper(ch)) {
                        submode = 0;
                        tmp.append('\u001c');
                        continue;
                    }
                    if (isAlphaLower(ch)) {
                        submode = 1;
                        tmp.append('\u001b');
                        continue;
                    }
                    if (startpos + idx + 1 < count) {
                        final char next = msg.charAt(startpos + idx + 1);
                        if (isPunctuation(next)) {
                            submode = 3;
                            tmp.append('\u0019');
                            continue;
                        }
                    }
                    tmp.append('\u001d');
                    tmp.append((char)PDF417HighLevelEncoder.PUNCTUATION[ch]);
                    break;
                }
                default: {
                    if (isPunctuation(ch)) {
                        tmp.append((char)PDF417HighLevelEncoder.PUNCTUATION[ch]);
                        break;
                    }
                    submode = 0;
                    tmp.append('\u001d');
                    continue;
                }
            }
            if (++idx >= count) {
                break;
            }
        }
        char h = '\0';
        final int len = tmp.length();
        for (int i = 0; i < len; ++i) {
            final boolean odd = i % 2 != 0;
            if (odd) {
                h = (char)(h * '\u001e' + tmp.charAt(i));
                sb.append(h);
            }
            else {
                h = tmp.charAt(i);
            }
        }
        if (len % 2 != 0) {
            sb.append((char)(h * '\u001e' + 29));
        }
        return submode;
    }
    
    private static void encodeBinary(final byte[] bytes, final int startpos, final int count, final int startmode, final StringBuilder sb) {
        if (count == 1 && startmode == 0) {
            sb.append('\u0391');
        }
        int idx = startpos;
        if (count >= 6) {
            sb.append('\u039c');
            final char[] chars = new char[5];
            while (startpos + count - idx >= 6) {
                long t = 0L;
                for (int i = 0; i < 6; ++i) {
                    t <<= 8;
                    t += (bytes[idx + i] & 0xFF);
                }
                for (int i = 0; i < 5; ++i) {
                    chars[i] = (char)(t % 900L);
                    t /= 900L;
                }
                for (int i = chars.length - 1; i >= 0; --i) {
                    sb.append(chars[i]);
                }
                idx += 6;
            }
        }
        if (idx < startpos + count) {
            sb.append('\u0385');
        }
        for (int j = idx; j < startpos + count; ++j) {
            final int ch = bytes[j] & 0xFF;
            sb.append((char)ch);
        }
    }
    
    private static void encodeNumeric(final String msg, final int startpos, final int count, final StringBuilder sb) {
        int idx = 0;
        final StringBuilder tmp = new StringBuilder(count / 3 + 1);
        final BigInteger num900 = BigInteger.valueOf(900L);
        final BigInteger num901 = BigInteger.valueOf(0L);
        while (idx < count - 1) {
            tmp.setLength(0);
            final int len = Math.min(44, count - idx);
            final String part = '1' + msg.substring(startpos + idx, startpos + idx + len);
            BigInteger bigint = new BigInteger(part);
            do {
                final BigInteger c = bigint.mod(num900);
                tmp.append((char)c.intValue());
                bigint = bigint.divide(num900);
            } while (!bigint.equals(num901));
            for (int i = tmp.length() - 1; i >= 0; --i) {
                sb.append(tmp.charAt(i));
            }
            idx += len;
        }
    }
    
    private static boolean isDigit(final char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    private static boolean isAlphaUpper(final char ch) {
        return ch == ' ' || (ch >= 'A' && ch <= 'Z');
    }
    
    private static boolean isAlphaLower(final char ch) {
        return ch == ' ' || (ch >= 'a' && ch <= 'z');
    }
    
    private static boolean isMixed(final char ch) {
        return PDF417HighLevelEncoder.MIXED[ch] != -1;
    }
    
    private static boolean isPunctuation(final char ch) {
        return PDF417HighLevelEncoder.PUNCTUATION[ch] != -1;
    }
    
    private static boolean isText(final char ch) {
        return ch == '\t' || ch == '\n' || ch == '\r' || (ch >= ' ' && ch <= '~');
    }
    
    private static int determineConsecutiveDigitCount(final CharSequence msg, final int startpos) {
        int count = 0;
        final int len = msg.length();
        int idx = startpos;
        if (idx < len) {
            for (char ch = msg.charAt(idx); isDigit(ch) && idx < len; ch = msg.charAt(idx)) {
                ++count;
                if (++idx < len) {}
            }
        }
        return count;
    }
    
    private static int determineConsecutiveTextCount(final CharSequence msg, final int startpos) {
        final int len = msg.length();
        int idx = startpos;
        while (idx < len) {
            char ch;
            int numericCount;
            for (ch = msg.charAt(idx), numericCount = 0; numericCount < 13 && isDigit(ch) && idx < len; ch = msg.charAt(idx)) {
                ++numericCount;
                if (++idx < len) {}
            }
            if (numericCount >= 13) {
                return idx - startpos - numericCount;
            }
            if (numericCount > 0) {
                continue;
            }
            ch = msg.charAt(idx);
            if (!isText(ch)) {
                break;
            }
            ++idx;
        }
        return idx - startpos;
    }
    
    private static int determineConsecutiveBinaryCount(final CharSequence msg, final byte[] bytes, final int startpos) throws WriterException {
        int len;
        int idx;
        for (len = msg.length(), idx = startpos; idx < len; ++idx) {
            char ch;
            int numericCount;
            int i;
            for (ch = msg.charAt(idx), numericCount = 0; numericCount < 13 && isDigit(ch); ch = msg.charAt(i)) {
                ++numericCount;
                i = idx + numericCount;
                if (i >= len) {
                    break;
                }
            }
            if (numericCount >= 13) {
                return idx - startpos;
            }
            int textCount;
            int j;
            for (textCount = 0; textCount < 5 && isText(ch); ch = msg.charAt(j)) {
                ++textCount;
                j = idx + textCount;
                if (j >= len) {
                    break;
                }
            }
            if (textCount >= 5) {
                return idx - startpos;
            }
            ch = msg.charAt(idx);
            if (bytes[idx] == 63 && ch != '?') {
                throw new WriterException("Non-encodable character detected: " + ch + " (Unicode: " + (int)ch + ')');
            }
        }
        return idx - startpos;
    }
    
    static {
        TEXT_MIXED_RAW = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 38, 13, 9, 44, 58, 35, 45, 46, 36, 47, 43, 37, 42, 61, 94, 0, 32, 0, 0, 0 };
        TEXT_PUNCTUATION_RAW = new byte[] { 59, 60, 62, 64, 91, 92, 93, 95, 96, 126, 33, 13, 9, 44, 58, 10, 45, 46, 36, 47, 34, 124, 42, 40, 41, 63, 123, 125, 39, 0 };
        MIXED = new byte[128];
        PUNCTUATION = new byte[128];
        Arrays.fill(PDF417HighLevelEncoder.MIXED, (byte)(-1));
        for (byte i = 0; i < PDF417HighLevelEncoder.TEXT_MIXED_RAW.length; ++i) {
            final byte b = PDF417HighLevelEncoder.TEXT_MIXED_RAW[i];
            if (b > 0) {
                PDF417HighLevelEncoder.MIXED[b] = i;
            }
        }
        Arrays.fill(PDF417HighLevelEncoder.PUNCTUATION, (byte)(-1));
        for (byte i = 0; i < PDF417HighLevelEncoder.TEXT_PUNCTUATION_RAW.length; ++i) {
            final byte b = PDF417HighLevelEncoder.TEXT_PUNCTUATION_RAW[i];
            if (b > 0) {
                PDF417HighLevelEncoder.PUNCTUATION[b] = i;
            }
        }
    }
}
