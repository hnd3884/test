package com.google.zxing.qrcode.decoder;

import com.google.zxing.common.StringUtils;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Collection;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.FormatException;
import java.util.ArrayList;
import com.google.zxing.common.BitSource;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.DecodeHintType;
import java.util.Map;

final class DecodedBitStreamParser
{
    private static final char[] ALPHANUMERIC_CHARS;
    private static final int GB2312_SUBSET = 1;
    
    private DecodedBitStreamParser() {
    }
    
    static DecoderResult decode(final byte[] bytes, final Version version, final ErrorCorrectionLevel ecLevel, final Map<DecodeHintType, ?> hints) throws FormatException {
        final BitSource bits = new BitSource(bytes);
        final StringBuilder result = new StringBuilder(50);
        CharacterSetECI currentCharacterSetECI = null;
        boolean fc1InEffect = false;
        final List<byte[]> byteSegments = new ArrayList<byte[]>(1);
        Mode mode;
        do {
            if (bits.available() < 4) {
                mode = Mode.TERMINATOR;
            }
            else {
                try {
                    mode = Mode.forBits(bits.readBits(4));
                }
                catch (final IllegalArgumentException iae) {
                    throw FormatException.getFormatInstance();
                }
            }
            if (mode != Mode.TERMINATOR) {
                if (mode == Mode.FNC1_FIRST_POSITION || mode == Mode.FNC1_SECOND_POSITION) {
                    fc1InEffect = true;
                }
                else if (mode == Mode.STRUCTURED_APPEND) {
                    bits.readBits(16);
                }
                else if (mode == Mode.ECI) {
                    final int value = parseECIValue(bits);
                    currentCharacterSetECI = CharacterSetECI.getCharacterSetECIByValue(value);
                    if (currentCharacterSetECI == null) {
                        throw FormatException.getFormatInstance();
                    }
                    continue;
                }
                else if (mode == Mode.HANZI) {
                    final int subset = bits.readBits(4);
                    final int countHanzi = bits.readBits(mode.getCharacterCountBits(version));
                    if (subset != 1) {
                        continue;
                    }
                    decodeHanziSegment(bits, result, countHanzi);
                }
                else {
                    final int count = bits.readBits(mode.getCharacterCountBits(version));
                    if (mode == Mode.NUMERIC) {
                        decodeNumericSegment(bits, result, count);
                    }
                    else if (mode == Mode.ALPHANUMERIC) {
                        decodeAlphanumericSegment(bits, result, count, fc1InEffect);
                    }
                    else if (mode == Mode.BYTE) {
                        decodeByteSegment(bits, result, count, currentCharacterSetECI, byteSegments, hints);
                    }
                    else {
                        if (mode != Mode.KANJI) {
                            throw FormatException.getFormatInstance();
                        }
                        decodeKanjiSegment(bits, result, count);
                    }
                }
            }
        } while (mode != Mode.TERMINATOR);
        return new DecoderResult(bytes, result.toString(), byteSegments.isEmpty() ? null : byteSegments, (ecLevel == null) ? null : ecLevel.toString());
    }
    
    private static void decodeHanziSegment(final BitSource bits, final StringBuilder result, int count) throws FormatException {
        if (count * 13 > bits.available()) {
            throw FormatException.getFormatInstance();
        }
        final byte[] buffer = new byte[2 * count];
        int offset = 0;
        while (count > 0) {
            final int twoBytes = bits.readBits(13);
            int assembledTwoBytes = twoBytes / 96 << 8 | twoBytes % 96;
            if (assembledTwoBytes < 959) {
                assembledTwoBytes += 41377;
            }
            else {
                assembledTwoBytes += 42657;
            }
            buffer[offset] = (byte)(assembledTwoBytes >> 8 & 0xFF);
            buffer[offset + 1] = (byte)(assembledTwoBytes & 0xFF);
            offset += 2;
            --count;
        }
        try {
            result.append(new String(buffer, "GB2312"));
        }
        catch (final UnsupportedEncodingException uee) {
            throw FormatException.getFormatInstance();
        }
    }
    
    private static void decodeKanjiSegment(final BitSource bits, final StringBuilder result, int count) throws FormatException {
        if (count * 13 > bits.available()) {
            throw FormatException.getFormatInstance();
        }
        final byte[] buffer = new byte[2 * count];
        int offset = 0;
        while (count > 0) {
            final int twoBytes = bits.readBits(13);
            int assembledTwoBytes = twoBytes / 192 << 8 | twoBytes % 192;
            if (assembledTwoBytes < 7936) {
                assembledTwoBytes += 33088;
            }
            else {
                assembledTwoBytes += 49472;
            }
            buffer[offset] = (byte)(assembledTwoBytes >> 8);
            buffer[offset + 1] = (byte)assembledTwoBytes;
            offset += 2;
            --count;
        }
        try {
            result.append(new String(buffer, "SJIS"));
        }
        catch (final UnsupportedEncodingException uee) {
            throw FormatException.getFormatInstance();
        }
    }
    
    private static void decodeByteSegment(final BitSource bits, final StringBuilder result, final int count, final CharacterSetECI currentCharacterSetECI, final Collection<byte[]> byteSegments, final Map<DecodeHintType, ?> hints) throws FormatException {
        if (count << 3 > bits.available()) {
            throw FormatException.getFormatInstance();
        }
        final byte[] readBytes = new byte[count];
        for (int i = 0; i < count; ++i) {
            readBytes[i] = (byte)bits.readBits(8);
        }
        String encoding;
        if (currentCharacterSetECI == null) {
            encoding = StringUtils.guessEncoding(readBytes, hints);
        }
        else {
            encoding = currentCharacterSetECI.name();
        }
        try {
            result.append(new String(readBytes, encoding));
        }
        catch (final UnsupportedEncodingException uce) {
            throw FormatException.getFormatInstance();
        }
        byteSegments.add(readBytes);
    }
    
    private static char toAlphaNumericChar(final int value) throws FormatException {
        if (value >= DecodedBitStreamParser.ALPHANUMERIC_CHARS.length) {
            throw FormatException.getFormatInstance();
        }
        return DecodedBitStreamParser.ALPHANUMERIC_CHARS[value];
    }
    
    private static void decodeAlphanumericSegment(final BitSource bits, final StringBuilder result, int count, final boolean fc1InEffect) throws FormatException {
        final int start = result.length();
        while (count > 1) {
            final int nextTwoCharsBits = bits.readBits(11);
            result.append(toAlphaNumericChar(nextTwoCharsBits / 45));
            result.append(toAlphaNumericChar(nextTwoCharsBits % 45));
            count -= 2;
        }
        if (count == 1) {
            result.append(toAlphaNumericChar(bits.readBits(6)));
        }
        if (fc1InEffect) {
            for (int i = start; i < result.length(); ++i) {
                if (result.charAt(i) == '%') {
                    if (i < result.length() - 1 && result.charAt(i + 1) == '%') {
                        result.deleteCharAt(i + 1);
                    }
                    else {
                        result.setCharAt(i, '\u001d');
                    }
                }
            }
        }
    }
    
    private static void decodeNumericSegment(final BitSource bits, final StringBuilder result, int count) throws FormatException {
        while (count >= 3) {
            if (bits.available() < 10) {
                throw FormatException.getFormatInstance();
            }
            final int threeDigitsBits = bits.readBits(10);
            if (threeDigitsBits >= 1000) {
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(threeDigitsBits / 100));
            result.append(toAlphaNumericChar(threeDigitsBits / 10 % 10));
            result.append(toAlphaNumericChar(threeDigitsBits % 10));
            count -= 3;
        }
        if (count == 2) {
            if (bits.available() < 7) {
                throw FormatException.getFormatInstance();
            }
            final int twoDigitsBits = bits.readBits(7);
            if (twoDigitsBits >= 100) {
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(twoDigitsBits / 10));
            result.append(toAlphaNumericChar(twoDigitsBits % 10));
        }
        else if (count == 1) {
            if (bits.available() < 4) {
                throw FormatException.getFormatInstance();
            }
            final int digitBits = bits.readBits(4);
            if (digitBits >= 10) {
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(digitBits));
        }
    }
    
    private static int parseECIValue(final BitSource bits) {
        final int firstByte = bits.readBits(8);
        if ((firstByte & 0x80) == 0x0) {
            return firstByte & 0x7F;
        }
        if ((firstByte & 0xC0) == 0x80) {
            final int secondByte = bits.readBits(8);
            return (firstByte & 0x3F) << 8 | secondByte;
        }
        if ((firstByte & 0xE0) == 0xC0) {
            final int secondThirdBytes = bits.readBits(16);
            return (firstByte & 0x1F) << 16 | secondThirdBytes;
        }
        throw new IllegalArgumentException("Bad ECI bits starting with byte " + firstByte);
    }
    
    static {
        ALPHANUMERIC_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' ', '$', '%', '*', '+', '-', '.', '/', ':' };
    }
}
