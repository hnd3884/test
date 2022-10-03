package com.google.zxing.datamatrix.decoder;

import java.io.UnsupportedEncodingException;
import java.util.List;
import com.google.zxing.FormatException;
import java.util.Collection;
import java.util.ArrayList;
import com.google.zxing.common.BitSource;
import com.google.zxing.common.DecoderResult;

final class DecodedBitStreamParser
{
    private static final char[] C40_BASIC_SET_CHARS;
    private static final char[] C40_SHIFT2_SET_CHARS;
    private static final char[] TEXT_BASIC_SET_CHARS;
    private static final char[] TEXT_SHIFT3_SET_CHARS;
    
    private DecodedBitStreamParser() {
    }
    
    static DecoderResult decode(final byte[] bytes) throws FormatException {
        final BitSource bits = new BitSource(bytes);
        final StringBuilder result = new StringBuilder(100);
        final StringBuilder resultTrailer = new StringBuilder(0);
        final List<byte[]> byteSegments = new ArrayList<byte[]>(1);
        Mode mode = Mode.ASCII_ENCODE;
        do {
            if (mode == Mode.ASCII_ENCODE) {
                mode = decodeAsciiSegment(bits, result, resultTrailer);
            }
            else {
                switch (mode) {
                    case C40_ENCODE: {
                        decodeC40Segment(bits, result);
                        break;
                    }
                    case TEXT_ENCODE: {
                        decodeTextSegment(bits, result);
                        break;
                    }
                    case ANSIX12_ENCODE: {
                        decodeAnsiX12Segment(bits, result);
                        break;
                    }
                    case EDIFACT_ENCODE: {
                        decodeEdifactSegment(bits, result);
                        break;
                    }
                    case BASE256_ENCODE: {
                        decodeBase256Segment(bits, result, byteSegments);
                        break;
                    }
                    default: {
                        throw FormatException.getFormatInstance();
                    }
                }
                mode = Mode.ASCII_ENCODE;
            }
        } while (mode != Mode.PAD_ENCODE && bits.available() > 0);
        if (resultTrailer.length() > 0) {
            result.append(resultTrailer.toString());
        }
        return new DecoderResult(bytes, result.toString(), byteSegments.isEmpty() ? null : byteSegments, null);
    }
    
    private static Mode decodeAsciiSegment(final BitSource bits, final StringBuilder result, final StringBuilder resultTrailer) throws FormatException {
        boolean upperShift = false;
        do {
            int oneByte = bits.readBits(8);
            if (oneByte == 0) {
                throw FormatException.getFormatInstance();
            }
            if (oneByte <= 128) {
                if (upperShift) {
                    oneByte += 128;
                }
                result.append((char)(oneByte - 1));
                return Mode.ASCII_ENCODE;
            }
            if (oneByte == 129) {
                return Mode.PAD_ENCODE;
            }
            if (oneByte <= 229) {
                final int value = oneByte - 130;
                if (value < 10) {
                    result.append('0');
                }
                result.append(value);
            }
            else {
                if (oneByte == 230) {
                    return Mode.C40_ENCODE;
                }
                if (oneByte == 231) {
                    return Mode.BASE256_ENCODE;
                }
                if (oneByte == 232) {
                    result.append('\u001d');
                }
                else {
                    if (oneByte == 233) {
                        continue;
                    }
                    if (oneByte == 234) {
                        continue;
                    }
                    if (oneByte == 235) {
                        upperShift = true;
                    }
                    else if (oneByte == 236) {
                        result.append("[)>\u001e05\u001d");
                        resultTrailer.insert(0, "\u001e\u0004");
                    }
                    else if (oneByte == 237) {
                        result.append("[)>\u001e06\u001d");
                        resultTrailer.insert(0, "\u001e\u0004");
                    }
                    else {
                        if (oneByte == 238) {
                            return Mode.ANSIX12_ENCODE;
                        }
                        if (oneByte == 239) {
                            return Mode.TEXT_ENCODE;
                        }
                        if (oneByte == 240) {
                            return Mode.EDIFACT_ENCODE;
                        }
                        if (oneByte == 241) {
                            continue;
                        }
                        if (oneByte < 242) {
                            continue;
                        }
                        if (oneByte == 254 && bits.available() == 0) {
                            continue;
                        }
                        throw FormatException.getFormatInstance();
                    }
                }
            }
        } while (bits.available() > 0);
        return Mode.ASCII_ENCODE;
    }
    
    private static void decodeC40Segment(final BitSource bits, final StringBuilder result) throws FormatException {
        boolean upperShift = false;
        final int[] cValues = new int[3];
        int shift = 0;
        while (bits.available() != 8) {
            final int firstByte = bits.readBits(8);
            if (firstByte == 254) {
                return;
            }
            parseTwoBytes(firstByte, bits.readBits(8), cValues);
            for (int i = 0; i < 3; ++i) {
                final int cValue = cValues[i];
                switch (shift) {
                    case 0: {
                        if (cValue < 3) {
                            shift = cValue + 1;
                            break;
                        }
                        if (cValue < DecodedBitStreamParser.C40_BASIC_SET_CHARS.length) {
                            final char c40char = DecodedBitStreamParser.C40_BASIC_SET_CHARS[cValue];
                            if (upperShift) {
                                result.append((char)(c40char + '\u0080'));
                                upperShift = false;
                            }
                            else {
                                result.append(c40char);
                            }
                            break;
                        }
                        throw FormatException.getFormatInstance();
                    }
                    case 1: {
                        if (upperShift) {
                            result.append((char)(cValue + 128));
                            upperShift = false;
                        }
                        else {
                            result.append((char)cValue);
                        }
                        shift = 0;
                        break;
                    }
                    case 2: {
                        if (cValue < DecodedBitStreamParser.C40_SHIFT2_SET_CHARS.length) {
                            final char c40char = DecodedBitStreamParser.C40_SHIFT2_SET_CHARS[cValue];
                            if (upperShift) {
                                result.append((char)(c40char + '\u0080'));
                                upperShift = false;
                            }
                            else {
                                result.append(c40char);
                            }
                        }
                        else if (cValue == 27) {
                            result.append('\u001d');
                        }
                        else {
                            if (cValue != 30) {
                                throw FormatException.getFormatInstance();
                            }
                            upperShift = true;
                        }
                        shift = 0;
                        break;
                    }
                    case 3: {
                        if (upperShift) {
                            result.append((char)(cValue + 224));
                            upperShift = false;
                        }
                        else {
                            result.append((char)(cValue + 96));
                        }
                        shift = 0;
                        break;
                    }
                    default: {
                        throw FormatException.getFormatInstance();
                    }
                }
            }
            if (bits.available() <= 0) {
                return;
            }
        }
    }
    
    private static void decodeTextSegment(final BitSource bits, final StringBuilder result) throws FormatException {
        boolean upperShift = false;
        final int[] cValues = new int[3];
        int shift = 0;
        while (bits.available() != 8) {
            final int firstByte = bits.readBits(8);
            if (firstByte == 254) {
                return;
            }
            parseTwoBytes(firstByte, bits.readBits(8), cValues);
            for (int i = 0; i < 3; ++i) {
                final int cValue = cValues[i];
                switch (shift) {
                    case 0: {
                        if (cValue < 3) {
                            shift = cValue + 1;
                            break;
                        }
                        if (cValue < DecodedBitStreamParser.TEXT_BASIC_SET_CHARS.length) {
                            final char textChar = DecodedBitStreamParser.TEXT_BASIC_SET_CHARS[cValue];
                            if (upperShift) {
                                result.append((char)(textChar + '\u0080'));
                                upperShift = false;
                            }
                            else {
                                result.append(textChar);
                            }
                            break;
                        }
                        throw FormatException.getFormatInstance();
                    }
                    case 1: {
                        if (upperShift) {
                            result.append((char)(cValue + 128));
                            upperShift = false;
                        }
                        else {
                            result.append((char)cValue);
                        }
                        shift = 0;
                        break;
                    }
                    case 2: {
                        if (cValue < DecodedBitStreamParser.C40_SHIFT2_SET_CHARS.length) {
                            final char c40char = DecodedBitStreamParser.C40_SHIFT2_SET_CHARS[cValue];
                            if (upperShift) {
                                result.append((char)(c40char + '\u0080'));
                                upperShift = false;
                            }
                            else {
                                result.append(c40char);
                            }
                        }
                        else if (cValue == 27) {
                            result.append('\u001d');
                        }
                        else {
                            if (cValue != 30) {
                                throw FormatException.getFormatInstance();
                            }
                            upperShift = true;
                        }
                        shift = 0;
                        break;
                    }
                    case 3: {
                        if (cValue < DecodedBitStreamParser.TEXT_SHIFT3_SET_CHARS.length) {
                            final char textChar = DecodedBitStreamParser.TEXT_SHIFT3_SET_CHARS[cValue];
                            if (upperShift) {
                                result.append((char)(textChar + '\u0080'));
                                upperShift = false;
                            }
                            else {
                                result.append(textChar);
                            }
                            shift = 0;
                            break;
                        }
                        throw FormatException.getFormatInstance();
                    }
                    default: {
                        throw FormatException.getFormatInstance();
                    }
                }
            }
            if (bits.available() <= 0) {
                return;
            }
        }
    }
    
    private static void decodeAnsiX12Segment(final BitSource bits, final StringBuilder result) throws FormatException {
        final int[] cValues = new int[3];
        while (bits.available() != 8) {
            final int firstByte = bits.readBits(8);
            if (firstByte == 254) {
                return;
            }
            parseTwoBytes(firstByte, bits.readBits(8), cValues);
            for (int i = 0; i < 3; ++i) {
                final int cValue = cValues[i];
                if (cValue == 0) {
                    result.append('\r');
                }
                else if (cValue == 1) {
                    result.append('*');
                }
                else if (cValue == 2) {
                    result.append('>');
                }
                else if (cValue == 3) {
                    result.append(' ');
                }
                else if (cValue < 14) {
                    result.append((char)(cValue + 44));
                }
                else {
                    if (cValue >= 40) {
                        throw FormatException.getFormatInstance();
                    }
                    result.append((char)(cValue + 51));
                }
            }
            if (bits.available() <= 0) {
                return;
            }
        }
    }
    
    private static void parseTwoBytes(final int firstByte, final int secondByte, final int[] result) {
        int fullBitValue = (firstByte << 8) + secondByte - 1;
        int temp = fullBitValue / 1600;
        result[0] = temp;
        fullBitValue -= temp * 1600;
        temp = fullBitValue / 40;
        result[2] = fullBitValue - (result[1] = temp) * 40;
    }
    
    private static void decodeEdifactSegment(final BitSource bits, final StringBuilder result) {
        boolean unlatch = false;
        while (bits.available() > 16) {
            for (int i = 0; i < 4; ++i) {
                int edifactValue = bits.readBits(6);
                if (edifactValue == 31) {
                    unlatch = true;
                }
                if (!unlatch) {
                    if ((edifactValue & 0x20) == 0x0) {
                        edifactValue |= 0x40;
                    }
                    result.append((char)edifactValue);
                }
            }
            if (unlatch || bits.available() <= 0) {
                return;
            }
        }
    }
    
    private static void decodeBase256Segment(final BitSource bits, final StringBuilder result, final Collection<byte[]> byteSegments) throws FormatException {
        int codewordPosition = 1 + bits.getByteOffset();
        final int d1 = unrandomize255State(bits.readBits(8), codewordPosition++);
        int count;
        if (d1 == 0) {
            count = bits.available() / 8;
        }
        else if (d1 < 250) {
            count = d1;
        }
        else {
            count = 250 * (d1 - 249) + unrandomize255State(bits.readBits(8), codewordPosition++);
        }
        if (count < 0) {
            throw FormatException.getFormatInstance();
        }
        final byte[] bytes = new byte[count];
        for (int i = 0; i < count; ++i) {
            if (bits.available() < 8) {
                throw FormatException.getFormatInstance();
            }
            bytes[i] = (byte)unrandomize255State(bits.readBits(8), codewordPosition++);
        }
        byteSegments.add(bytes);
        try {
            result.append(new String(bytes, "ISO8859_1"));
        }
        catch (final UnsupportedEncodingException uee) {
            throw new IllegalStateException("Platform does not support required encoding: " + uee);
        }
    }
    
    private static int unrandomize255State(final int randomizedBase256Codeword, final int base256CodewordPosition) {
        final int pseudoRandomNumber = 149 * base256CodewordPosition % 255 + 1;
        final int tempVariable = randomizedBase256Codeword - pseudoRandomNumber;
        return (tempVariable >= 0) ? tempVariable : (tempVariable + 256);
    }
    
    static {
        C40_BASIC_SET_CHARS = new char[] { '*', '*', '*', ' ', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        C40_SHIFT2_SET_CHARS = new char[] { '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_' };
        TEXT_BASIC_SET_CHARS = new char[] { '*', '*', '*', ' ', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
        TEXT_SHIFT3_SET_CHARS = new char[] { '\'', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '{', '|', '}', '~', '\u007f' };
    }
    
    private enum Mode
    {
        PAD_ENCODE, 
        ASCII_ENCODE, 
        C40_ENCODE, 
        TEXT_ENCODE, 
        ANSIX12_ENCODE, 
        EDIFACT_ENCODE, 
        BASE256_ENCODE;
    }
}
