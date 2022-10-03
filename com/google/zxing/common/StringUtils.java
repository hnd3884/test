package com.google.zxing.common;

import com.google.zxing.DecodeHintType;
import java.util.Map;

public final class StringUtils
{
    private static final String PLATFORM_DEFAULT_ENCODING;
    public static final String SHIFT_JIS = "SJIS";
    public static final String GB2312 = "GB2312";
    private static final String EUC_JP = "EUC_JP";
    private static final String UTF8 = "UTF8";
    private static final String ISO88591 = "ISO8859_1";
    private static final boolean ASSUME_SHIFT_JIS;
    
    private StringUtils() {
    }
    
    public static String guessEncoding(final byte[] bytes, final Map<DecodeHintType, ?> hints) {
        if (hints != null) {
            final String characterSet = (String)hints.get(DecodeHintType.CHARACTER_SET);
            if (characterSet != null) {
                return characterSet;
            }
        }
        if (bytes.length > 3 && bytes[0] == -17 && bytes[1] == -69 && bytes[2] == -65) {
            return "UTF8";
        }
        final int length = bytes.length;
        boolean canBeISO88591 = true;
        boolean canBeShiftJIS = true;
        boolean canBeUTF8 = true;
        int utf8BytesLeft = 0;
        int maybeDoubleByteCount = 0;
        int maybeSingleByteKatakanaCount = 0;
        boolean sawLatin1Supplement = false;
        boolean sawUTF8Start = false;
        boolean lastWasPossibleDoubleByteStart = false;
        for (int i = 0; i < length && (canBeISO88591 || canBeShiftJIS || canBeUTF8); ++i) {
            final int value = bytes[i] & 0xFF;
            if (value >= 128 && value <= 191) {
                if (utf8BytesLeft > 0) {
                    --utf8BytesLeft;
                }
            }
            else {
                if (utf8BytesLeft > 0) {
                    canBeUTF8 = false;
                }
                if (value >= 192 && value <= 253) {
                    sawUTF8Start = true;
                    for (int valueCopy = value; (valueCopy & 0x40) != 0x0; valueCopy <<= 1) {
                        ++utf8BytesLeft;
                    }
                }
            }
            if ((value == 194 || value == 195) && i < length - 1) {
                final int nextValue = bytes[i + 1] & 0xFF;
                if (nextValue <= 191 && ((value == 194 && nextValue >= 160) || (value == 195 && nextValue >= 128))) {
                    sawLatin1Supplement = true;
                }
            }
            if (value >= 127 && value <= 159) {
                canBeISO88591 = false;
            }
            if (value >= 161 && value <= 223 && !lastWasPossibleDoubleByteStart) {
                ++maybeSingleByteKatakanaCount;
            }
            if (!lastWasPossibleDoubleByteStart && ((value >= 240 && value <= 255) || value == 128 || value == 160)) {
                canBeShiftJIS = false;
            }
            if ((value >= 129 && value <= 159) || (value >= 224 && value <= 239)) {
                if (lastWasPossibleDoubleByteStart) {
                    lastWasPossibleDoubleByteStart = false;
                }
                else {
                    lastWasPossibleDoubleByteStart = true;
                    if (i >= bytes.length - 1) {
                        canBeShiftJIS = false;
                    }
                    else {
                        final int nextValue = bytes[i + 1] & 0xFF;
                        if (nextValue < 64 || nextValue > 252) {
                            canBeShiftJIS = false;
                        }
                        else {
                            ++maybeDoubleByteCount;
                        }
                    }
                }
            }
            else {
                lastWasPossibleDoubleByteStart = false;
            }
        }
        if (utf8BytesLeft > 0) {
            canBeUTF8 = false;
        }
        if (canBeShiftJIS && StringUtils.ASSUME_SHIFT_JIS) {
            return "SJIS";
        }
        if (canBeUTF8 && sawUTF8Start) {
            return "UTF8";
        }
        if (canBeShiftJIS && (maybeDoubleByteCount >= 3 || 20 * maybeSingleByteKatakanaCount > length)) {
            return "SJIS";
        }
        if (!sawLatin1Supplement && canBeISO88591) {
            return "ISO8859_1";
        }
        return StringUtils.PLATFORM_DEFAULT_ENCODING;
    }
    
    static {
        PLATFORM_DEFAULT_ENCODING = System.getProperty("file.encoding");
        ASSUME_SHIFT_JIS = ("SJIS".equalsIgnoreCase(StringUtils.PLATFORM_DEFAULT_ENCODING) || "EUC_JP".equalsIgnoreCase(StringUtils.PLATFORM_DEFAULT_ENCODING));
    }
}
