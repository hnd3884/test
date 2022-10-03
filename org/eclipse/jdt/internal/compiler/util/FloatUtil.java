package org.eclipse.jdt.internal.compiler.util;

public class FloatUtil
{
    private static final int DOUBLE_FRACTION_WIDTH = 52;
    private static final int DOUBLE_PRECISION = 53;
    private static final int MAX_DOUBLE_EXPONENT = 1023;
    private static final int MIN_NORMALIZED_DOUBLE_EXPONENT = -1022;
    private static final int MIN_UNNORMALIZED_DOUBLE_EXPONENT = -1075;
    private static final int DOUBLE_EXPONENT_BIAS = 1023;
    private static final int DOUBLE_EXPONENT_SHIFT = 52;
    private static final int SINGLE_FRACTION_WIDTH = 23;
    private static final int SINGLE_PRECISION = 24;
    private static final int MAX_SINGLE_EXPONENT = 127;
    private static final int MIN_NORMALIZED_SINGLE_EXPONENT = -126;
    private static final int MIN_UNNORMALIZED_SINGLE_EXPONENT = -150;
    private static final int SINGLE_EXPONENT_BIAS = 127;
    private static final int SINGLE_EXPONENT_SHIFT = 23;
    
    public static float valueOfHexFloatLiteral(final char[] source) {
        final long bits = convertHexFloatingPointLiteralToBits(source);
        return Float.intBitsToFloat((int)bits);
    }
    
    public static double valueOfHexDoubleLiteral(final char[] source) {
        final long bits = convertHexFloatingPointLiteralToBits(source);
        return Double.longBitsToDouble(bits);
    }
    
    private static long convertHexFloatingPointLiteralToBits(final char[] source) {
        final int length = source.length;
        long mantissa = 0L;
        int next = 0;
        char nextChar = source[next];
        nextChar = source[next];
        if (nextChar != '0') {
            throw new NumberFormatException();
        }
        ++next;
        nextChar = source[next];
        if (nextChar != 'X' && nextChar != 'x') {
            throw new NumberFormatException();
        }
        ++next;
        int binaryPointPosition = -1;
        while (true) {
            nextChar = source[next];
            switch (nextChar) {
                case '0': {
                    ++next;
                    continue;
                }
                case '.': {
                    binaryPointPosition = next;
                    ++next;
                    continue;
                }
                default: {
                    int mantissaBits = 0;
                    int leadingDigitPosition = -1;
                    while (true) {
                        nextChar = source[next];
                        int hexdigit = 0;
                        switch (nextChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9': {
                                hexdigit = nextChar - '0';
                                break;
                            }
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f': {
                                hexdigit = nextChar - 'a' + 10;
                                break;
                            }
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F': {
                                hexdigit = nextChar - 'A' + 10;
                                break;
                            }
                            case '.': {
                                binaryPointPosition = next;
                                ++next;
                                continue;
                            }
                            default: {
                                if (binaryPointPosition < 0) {
                                    binaryPointPosition = next;
                                }
                                nextChar = source[next];
                                if (nextChar != 'P' && nextChar != 'p') {
                                    throw new NumberFormatException();
                                }
                                ++next;
                                int exponent = 0;
                                int exponentSign = 1;
                            Label_0662:
                                while (next < length) {
                                    nextChar = source[next];
                                    switch (nextChar) {
                                        case '+': {
                                            exponentSign = 1;
                                            ++next;
                                            continue;
                                        }
                                        case '-': {
                                            exponentSign = -1;
                                            ++next;
                                            continue;
                                        }
                                        case '0':
                                        case '1':
                                        case '2':
                                        case '3':
                                        case '4':
                                        case '5':
                                        case '6':
                                        case '7':
                                        case '8':
                                        case '9': {
                                            final int digit = nextChar - '0';
                                            exponent = exponent * 10 + digit;
                                            ++next;
                                            continue;
                                        }
                                        default: {
                                            break Label_0662;
                                        }
                                    }
                                }
                                boolean doublePrecision = true;
                                if (next < length) {
                                    nextChar = source[next];
                                    switch (nextChar) {
                                        case 'F':
                                        case 'f': {
                                            doublePrecision = false;
                                            ++next;
                                            break;
                                        }
                                        case 'D':
                                        case 'd': {
                                            doublePrecision = true;
                                            ++next;
                                            break;
                                        }
                                        default: {
                                            throw new NumberFormatException();
                                        }
                                    }
                                }
                                if (mantissa == 0L) {
                                    return 0L;
                                }
                                int scaleFactorCompensation = 0;
                                final long top = mantissa >>> mantissaBits - 4;
                                if ((top & 0x8L) == 0x0L) {
                                    --mantissaBits;
                                    ++scaleFactorCompensation;
                                    if ((top & 0x4L) == 0x0L) {
                                        --mantissaBits;
                                        ++scaleFactorCompensation;
                                        if ((top & 0x2L) == 0x0L) {
                                            --mantissaBits;
                                            ++scaleFactorCompensation;
                                        }
                                    }
                                }
                                long result = 0L;
                                if (doublePrecision) {
                                    long fraction;
                                    if (mantissaBits > 53) {
                                        final int extraBits = mantissaBits - 53;
                                        fraction = mantissa >>> extraBits - 1;
                                        final long lowBit = fraction & 0x1L;
                                        fraction += lowBit;
                                        fraction >>>= 1;
                                        if ((fraction & 0x20000000000000L) != 0x0L) {
                                            fraction >>>= 1;
                                            --scaleFactorCompensation;
                                        }
                                    }
                                    else {
                                        fraction = mantissa << 53 - mantissaBits;
                                    }
                                    int scaleFactor = 0;
                                    if (mantissaBits > 0) {
                                        if (leadingDigitPosition < binaryPointPosition) {
                                            scaleFactor = 4 * (binaryPointPosition - leadingDigitPosition);
                                            scaleFactor -= scaleFactorCompensation;
                                        }
                                        else {
                                            scaleFactor = -4 * (leadingDigitPosition - binaryPointPosition - 1);
                                            scaleFactor -= scaleFactorCompensation;
                                        }
                                    }
                                    final int e = exponentSign * exponent + scaleFactor;
                                    if (e - 1 > 1023) {
                                        result = Double.doubleToLongBits(Double.POSITIVE_INFINITY);
                                    }
                                    else if (e - 1 >= -1022) {
                                        final long biasedExponent = e - 1 + 1023;
                                        result = (fraction & 0xFFEFFFFFFFFFFFFFL);
                                        result |= biasedExponent << 52;
                                    }
                                    else if (e - 1 > -1075) {
                                        final long biasedExponent = 0L;
                                        result = fraction >>> -1022 - e + 1;
                                        result |= biasedExponent << 52;
                                    }
                                    else {
                                        result = Double.doubleToLongBits(Double.NaN);
                                    }
                                    return result;
                                }
                                long fraction;
                                if (mantissaBits > 24) {
                                    final int extraBits = mantissaBits - 24;
                                    fraction = mantissa >>> extraBits - 1;
                                    final long lowBit = fraction & 0x1L;
                                    fraction += lowBit;
                                    fraction >>>= 1;
                                    if ((fraction & 0x1000000L) != 0x0L) {
                                        fraction >>>= 1;
                                        --scaleFactorCompensation;
                                    }
                                }
                                else {
                                    fraction = mantissa << 24 - mantissaBits;
                                }
                                int scaleFactor = 0;
                                if (mantissaBits > 0) {
                                    if (leadingDigitPosition < binaryPointPosition) {
                                        scaleFactor = 4 * (binaryPointPosition - leadingDigitPosition);
                                        scaleFactor -= scaleFactorCompensation;
                                    }
                                    else {
                                        scaleFactor = -4 * (leadingDigitPosition - binaryPointPosition - 1);
                                        scaleFactor -= scaleFactorCompensation;
                                    }
                                }
                                final int e = exponentSign * exponent + scaleFactor;
                                if (e - 1 > 127) {
                                    result = Float.floatToIntBits(Float.POSITIVE_INFINITY);
                                }
                                else if (e - 1 >= -126) {
                                    final long biasedExponent = e - 1 + 127;
                                    result = (fraction & 0xFFFFFFFFFF7FFFFFL);
                                    result |= biasedExponent << 23;
                                }
                                else if (e - 1 > -150) {
                                    final long biasedExponent = 0L;
                                    result = fraction >>> -126 - e + 1;
                                    result |= biasedExponent << 23;
                                }
                                else {
                                    result = Float.floatToIntBits(Float.NaN);
                                }
                                return result;
                            }
                        }
                        if (mantissaBits == 0) {
                            leadingDigitPosition = next;
                            mantissa = hexdigit;
                            mantissaBits = 4;
                        }
                        else if (mantissaBits < 60) {
                            mantissa <<= 4;
                            mantissa |= hexdigit;
                            mantissaBits += 4;
                        }
                        ++next;
                    }
                    break;
                }
            }
        }
    }
}
