package com.google.zxing.maxicode.decoder;

import java.util.List;
import java.text.DecimalFormat;
import com.google.zxing.common.DecoderResult;
import java.text.NumberFormat;

final class DecodedBitStreamParser
{
    private static final char SHIFTA = '\ufff0';
    private static final char SHIFTB = '\ufff1';
    private static final char SHIFTC = '\ufff2';
    private static final char SHIFTD = '\ufff3';
    private static final char SHIFTE = '\ufff4';
    private static final char TWOSHIFTA = '\ufff5';
    private static final char THREESHIFTA = '\ufff6';
    private static final char LATCHA = '\ufff7';
    private static final char LATCHB = '\ufff8';
    private static final char LOCK = '\ufff9';
    private static final char ECI = '\ufffa';
    private static final char NS = '\ufffb';
    private static final char PAD = '\ufffc';
    private static final char FS = '\u001c';
    private static final char GS = '\u001d';
    private static final char RS = '\u001e';
    private static final NumberFormat NINE_DIGITS;
    private static final NumberFormat THREE_DIGITS;
    private static final String[] SETS;
    
    private DecodedBitStreamParser() {
    }
    
    static DecoderResult decode(final byte[] bytes, final int mode) {
        final StringBuilder result = new StringBuilder(144);
        switch (mode) {
            case 2:
            case 3: {
                String postcode;
                if (mode == 2) {
                    final int pc = getPostCode2(bytes);
                    final NumberFormat df = new DecimalFormat("0000000000".substring(0, getPostCode2Length(bytes)));
                    postcode = df.format(pc);
                }
                else {
                    postcode = getPostCode3(bytes);
                }
                final String country = DecodedBitStreamParser.THREE_DIGITS.format(getCountry(bytes));
                final String service = DecodedBitStreamParser.THREE_DIGITS.format(getServiceClass(bytes));
                result.append(getMessage(bytes, 10, 84));
                if (result.toString().startsWith("[)>\u001e01\u001d")) {
                    result.insert(9, postcode + '\u001d' + country + '\u001d' + service + '\u001d');
                    break;
                }
                result.insert(0, postcode + '\u001d' + country + '\u001d' + service + '\u001d');
                break;
            }
            case 4: {
                result.append(getMessage(bytes, 1, 93));
                break;
            }
            case 5: {
                result.append(getMessage(bytes, 1, 77));
                break;
            }
        }
        return new DecoderResult(bytes, result.toString(), null, String.valueOf(mode));
    }
    
    private static int getBit(int bit, final byte[] bytes) {
        --bit;
        return ((bytes[bit / 6] & 1 << 5 - bit % 6) != 0x0) ? 1 : 0;
    }
    
    private static int getInt(final byte[] bytes, final byte[] x) {
        int val = 0;
        for (int i = 0; i < x.length; ++i) {
            val += getBit(x[i], bytes) << x.length - i - 1;
        }
        return val;
    }
    
    private static int getCountry(final byte[] bytes) {
        return getInt(bytes, new byte[] { 53, 54, 43, 44, 45, 46, 47, 48, 37, 38 });
    }
    
    private static int getServiceClass(final byte[] bytes) {
        return getInt(bytes, new byte[] { 55, 56, 57, 58, 59, 60, 49, 50, 51, 52 });
    }
    
    private static int getPostCode2Length(final byte[] bytes) {
        return getInt(bytes, new byte[] { 39, 40, 41, 42, 31, 32 });
    }
    
    private static int getPostCode2(final byte[] bytes) {
        return getInt(bytes, new byte[] { 33, 34, 35, 36, 25, 26, 27, 28, 29, 30, 19, 20, 21, 22, 23, 24, 13, 14, 15, 16, 17, 18, 7, 8, 9, 10, 11, 12, 1, 2 });
    }
    
    private static String getPostCode3(final byte[] bytes) {
        return String.valueOf(new char[] { DecodedBitStreamParser.SETS[0].charAt(getInt(bytes, new byte[] { 39, 40, 41, 42, 31, 32 })), DecodedBitStreamParser.SETS[0].charAt(getInt(bytes, new byte[] { 33, 34, 35, 36, 25, 26 })), DecodedBitStreamParser.SETS[0].charAt(getInt(bytes, new byte[] { 27, 28, 29, 30, 19, 20 })), DecodedBitStreamParser.SETS[0].charAt(getInt(bytes, new byte[] { 21, 22, 23, 24, 13, 14 })), DecodedBitStreamParser.SETS[0].charAt(getInt(bytes, new byte[] { 15, 16, 17, 18, 7, 8 })), DecodedBitStreamParser.SETS[0].charAt(getInt(bytes, new byte[] { 9, 10, 11, 12, 1, 2 })) });
    }
    
    private static String getMessage(final byte[] bytes, final int start, final int len) {
        final StringBuilder sb = new StringBuilder();
        int shift = -1;
        int set = 0;
        int lastset = 0;
        for (int i = start; i < start + len; ++i) {
            final char c = DecodedBitStreamParser.SETS[set].charAt(bytes[i]);
            switch (c) {
                case '\ufff7': {
                    set = 0;
                    shift = -1;
                    break;
                }
                case '\ufff8': {
                    set = 1;
                    shift = -1;
                    break;
                }
                case '\ufff0':
                case '\ufff1':
                case '\ufff2':
                case '\ufff3':
                case '\ufff4': {
                    lastset = set;
                    set = c - '\ufff0';
                    shift = 1;
                    break;
                }
                case '\ufff5': {
                    lastset = set;
                    set = 0;
                    shift = 2;
                    break;
                }
                case '\ufff6': {
                    lastset = set;
                    set = 0;
                    shift = 3;
                    break;
                }
                case '\ufffb': {
                    final int nsval = (bytes[++i] << 24) + (bytes[++i] << 18) + (bytes[++i] << 12) + (bytes[++i] << 6) + bytes[++i];
                    sb.append(DecodedBitStreamParser.NINE_DIGITS.format(nsval));
                    break;
                }
                case '\ufff9': {
                    shift = -1;
                    break;
                }
                default: {
                    sb.append(c);
                    break;
                }
            }
            if (shift-- == 0) {
                set = lastset;
            }
        }
        while (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\ufffc') {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    static {
        NINE_DIGITS = new DecimalFormat("000000000");
        THREE_DIGITS = new DecimalFormat("000");
        SETS = new String[] { "\nABCDEFGHIJKLMNOPQRSTUVWXYZ\ufffa\u001c\u001d\u001e\ufffb \ufffc\"#$%&'()*+,-./0123456789:\ufff1\ufff2\ufff3\ufff4\ufff8", "`abcdefghijklmnopqrstuvwxyz\ufffa\u001c\u001d\u001e\ufffb{\ufffc}~\u007f;<=>?[\\]^_ ,./:@!|\ufffc\ufff5\ufff6\ufffc\ufff0\ufff2\ufff3\ufff4\ufff7", "\u00c0\u00c1\u00c2\u00c3\u00c4\u00c5\u00c6\u00c7\u00c8\u00c9\u00ca\u00cb\u00cc\u00cd\u00ce\u00cf\u00d0\u00d1\u00d2\u00d3\u00d4\u00d5\u00d6\u00d7\u00d8\u00d9\u00da\ufffa\u001c\u001d\u001e\u00db\u00dc\u00dd\u00de\u00dfª¬±²³µ¹º¼½¾\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\ufff7 \ufff9\ufff3\ufff4\ufff8", "\u00e0\u00e1\u00e2\u00e3\u00e4\u00e5\u00e6\u00e7\u00e8\u00e9\u00ea\u00eb\u00ec\u00ed\u00ee\u00ef\u00f0\u00f1\u00f2\u00f3\u00f4\u00f5\u00f6\u00f7\u00f8\u00f9\u00fa\ufffa\u001c\u001d\u001e\ufffb\u00fb\u00fc\u00fd\u00fe\u00ff¡¨«¯°´·¸»¿\u008a\u008b\u008c\u008d\u008e\u008f\u0090\u0091\u0092\u0093\u0094\ufff7 \ufff2\ufff9\ufff4\ufff8", "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\ufffa\ufffc\ufffc\u001b\ufffb\u001c\u001d\u001e\u001f\u009f ¢£¤¥¦§©\u00ad®¶\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009d\u009e\ufff7 \ufff2\ufff3\ufff9\ufff8", "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?" };
    }
}
