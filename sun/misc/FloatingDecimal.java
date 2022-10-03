package sun.misc;

import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.regex.Matcher;

public class FloatingDecimal
{
    static final int EXP_SHIFT = 52;
    static final long FRACT_HOB = 4503599627370496L;
    static final long EXP_ONE = 4607182418800017408L;
    static final int MAX_SMALL_BIN_EXP = 62;
    static final int MIN_SMALL_BIN_EXP = -21;
    static final int MAX_DECIMAL_DIGITS = 15;
    static final int MAX_DECIMAL_EXPONENT = 308;
    static final int MIN_DECIMAL_EXPONENT = -324;
    static final int BIG_DECIMAL_EXPONENT = 324;
    static final int MAX_NDIGITS = 1100;
    static final int SINGLE_EXP_SHIFT = 23;
    static final int SINGLE_FRACT_HOB = 8388608;
    static final int SINGLE_MAX_DECIMAL_DIGITS = 7;
    static final int SINGLE_MAX_DECIMAL_EXPONENT = 38;
    static final int SINGLE_MIN_DECIMAL_EXPONENT = -45;
    static final int SINGLE_MAX_NDIGITS = 200;
    static final int INT_DECIMAL_DIGITS = 9;
    private static final String INFINITY_REP = "Infinity";
    private static final int INFINITY_LENGTH;
    private static final String NAN_REP = "NaN";
    private static final int NAN_LENGTH;
    private static final BinaryToASCIIConverter B2AC_POSITIVE_INFINITY;
    private static final BinaryToASCIIConverter B2AC_NEGATIVE_INFINITY;
    private static final BinaryToASCIIConverter B2AC_NOT_A_NUMBER;
    private static final BinaryToASCIIConverter B2AC_POSITIVE_ZERO;
    private static final BinaryToASCIIConverter B2AC_NEGATIVE_ZERO;
    private static final ThreadLocal<BinaryToASCIIBuffer> threadLocalBinaryToASCIIBuffer;
    static final ASCIIToBinaryConverter A2BC_POSITIVE_INFINITY;
    static final ASCIIToBinaryConverter A2BC_NEGATIVE_INFINITY;
    static final ASCIIToBinaryConverter A2BC_NOT_A_NUMBER;
    static final ASCIIToBinaryConverter A2BC_POSITIVE_ZERO;
    static final ASCIIToBinaryConverter A2BC_NEGATIVE_ZERO;
    
    public static String toJavaFormatString(final double n) {
        return getBinaryToASCIIConverter(n).toJavaFormatString();
    }
    
    public static String toJavaFormatString(final float n) {
        return getBinaryToASCIIConverter(n).toJavaFormatString();
    }
    
    public static void appendTo(final double n, final Appendable appendable) {
        getBinaryToASCIIConverter(n).appendTo(appendable);
    }
    
    public static void appendTo(final float n, final Appendable appendable) {
        getBinaryToASCIIConverter(n).appendTo(appendable);
    }
    
    public static double parseDouble(final String s) throws NumberFormatException {
        return readJavaFormatString(s).doubleValue();
    }
    
    public static float parseFloat(final String s) throws NumberFormatException {
        return readJavaFormatString(s).floatValue();
    }
    
    private static BinaryToASCIIBuffer getBinaryToASCIIBuffer() {
        return FloatingDecimal.threadLocalBinaryToASCIIBuffer.get();
    }
    
    public static BinaryToASCIIConverter getBinaryToASCIIConverter(final double n) {
        return getBinaryToASCIIConverter(n, true);
    }
    
    static BinaryToASCIIConverter getBinaryToASCIIConverter(final double n, final boolean b) {
        final long doubleToRawLongBits = Double.doubleToRawLongBits(n);
        final boolean b2 = (doubleToRawLongBits & Long.MIN_VALUE) != 0x0L;
        final long n2 = doubleToRawLongBits & 0xFFFFFFFFFFFFFL;
        int n3 = (int)((doubleToRawLongBits & 0x7FF0000000000000L) >> 52);
        if (n3 != 2047) {
            long n5;
            int n6;
            if (n3 == 0) {
                if (n2 == 0L) {
                    return b2 ? FloatingDecimal.B2AC_NEGATIVE_ZERO : FloatingDecimal.B2AC_POSITIVE_ZERO;
                }
                final int numberOfLeadingZeros = Long.numberOfLeadingZeros(n2);
                final int n4 = numberOfLeadingZeros - 11;
                n5 = n2 << n4;
                n3 = 1 - n4;
                n6 = 64 - numberOfLeadingZeros;
            }
            else {
                n5 = (n2 | 0x10000000000000L);
                n6 = 53;
            }
            n3 -= 1023;
            final BinaryToASCIIBuffer binaryToASCIIBuffer = getBinaryToASCIIBuffer();
            binaryToASCIIBuffer.setSign(b2);
            binaryToASCIIBuffer.dtoa(n3, n5, n6, b);
            return binaryToASCIIBuffer;
        }
        if (n2 == 0L) {
            return b2 ? FloatingDecimal.B2AC_NEGATIVE_INFINITY : FloatingDecimal.B2AC_POSITIVE_INFINITY;
        }
        return FloatingDecimal.B2AC_NOT_A_NUMBER;
    }
    
    private static BinaryToASCIIConverter getBinaryToASCIIConverter(final float n) {
        final int floatToRawIntBits = Float.floatToRawIntBits(n);
        final boolean b = (floatToRawIntBits & Integer.MIN_VALUE) != 0x0;
        final int n2 = floatToRawIntBits & 0x7FFFFF;
        int n3 = (floatToRawIntBits & 0x7F800000) >> 23;
        if (n3 != 255) {
            int n5;
            int n6;
            if (n3 == 0) {
                if (n2 == 0) {
                    return b ? FloatingDecimal.B2AC_NEGATIVE_ZERO : FloatingDecimal.B2AC_POSITIVE_ZERO;
                }
                final int numberOfLeadingZeros = Integer.numberOfLeadingZeros(n2);
                final int n4 = numberOfLeadingZeros - 8;
                n5 = n2 << n4;
                n3 = 1 - n4;
                n6 = 32 - numberOfLeadingZeros;
            }
            else {
                n5 = (n2 | 0x800000);
                n6 = 24;
            }
            n3 -= 127;
            final BinaryToASCIIBuffer binaryToASCIIBuffer = getBinaryToASCIIBuffer();
            binaryToASCIIBuffer.setSign(b);
            binaryToASCIIBuffer.dtoa(n3, (long)n5 << 29, n6, true);
            return binaryToASCIIBuffer;
        }
        if (n2 == 0L) {
            return b ? FloatingDecimal.B2AC_NEGATIVE_INFINITY : FloatingDecimal.B2AC_POSITIVE_INFINITY;
        }
        return FloatingDecimal.B2AC_NOT_A_NUMBER;
    }
    
    static ASCIIToBinaryConverter readJavaFormatString(String trim) throws NumberFormatException {
        boolean b = false;
        boolean b2 = false;
        try {
            trim = trim.trim();
            final int length = trim.length();
            if (length == 0) {
                throw new NumberFormatException("empty String");
            }
            int i = 0;
            switch (trim.charAt(i)) {
                case '-': {
                    b = true;
                }
                case '+': {
                    ++i;
                    b2 = true;
                    break;
                }
            }
            final char char1 = trim.charAt(i);
            if (char1 == 'N') {
                if (length - i == FloatingDecimal.NAN_LENGTH && trim.indexOf("NaN", i) == i) {
                    return FloatingDecimal.A2BC_NOT_A_NUMBER;
                }
            }
            else if (char1 == 'I') {
                if (length - i == FloatingDecimal.INFINITY_LENGTH && trim.indexOf("Infinity", i) == i) {
                    return b ? FloatingDecimal.A2BC_NEGATIVE_INFINITY : FloatingDecimal.A2BC_POSITIVE_INFINITY;
                }
            }
            else {
                if (char1 == '0' && length > i + 1) {
                    final char char2 = trim.charAt(i + 1);
                    if (char2 == 'x' || char2 == 'X') {
                        return parseHexString(trim);
                    }
                }
                final char[] array = new char[length];
                int n = 0;
                int n2 = 0;
                int n3 = 0;
                int n4 = 0;
                int n5 = 0;
                while (i < length) {
                    final char char3 = trim.charAt(i);
                    if (char3 == '0') {
                        ++n4;
                    }
                    else {
                        if (char3 != '.') {
                            break;
                        }
                        if (n2 != 0) {
                            throw new NumberFormatException("multiple points");
                        }
                        n3 = i;
                        if (b2) {
                            --n3;
                        }
                        n2 = 1;
                    }
                    ++i;
                }
                while (i < length) {
                    final char char4 = trim.charAt(i);
                    if (char4 >= '1' && char4 <= '9') {
                        array[n++] = char4;
                        n5 = 0;
                    }
                    else if (char4 == '0') {
                        array[n++] = char4;
                        ++n5;
                    }
                    else {
                        if (char4 != '.') {
                            break;
                        }
                        if (n2 != 0) {
                            throw new NumberFormatException("multiple points");
                        }
                        n3 = i;
                        if (b2) {
                            --n3;
                        }
                        n2 = 1;
                    }
                    ++i;
                }
                final int n6 = n - n5;
                final boolean b3 = n6 == 0;
                if (!b3 || n4 != 0) {
                    int n7;
                    if (n2 != 0) {
                        n7 = n3 - n4;
                    }
                    else {
                        n7 = n6 + n5;
                    }
                    final char char5;
                    if (i < length && ((char5 = trim.charAt(i)) == 'e' || char5 == 'E')) {
                        int n8 = 1;
                        int n9 = 0;
                        final int n10 = 214748364;
                        boolean b4 = false;
                        switch (trim.charAt(++i)) {
                            case '-': {
                                n8 = -1;
                            }
                            case '+': {
                                ++i;
                                break;
                            }
                        }
                        final int n11 = i;
                        while (i < length) {
                            if (n9 >= n10) {
                                b4 = true;
                            }
                            final char char6 = trim.charAt(i++);
                            if (char6 < '0' || char6 > '9') {
                                --i;
                                break;
                            }
                            n9 = n9 * 10 + (char6 - '0');
                        }
                        final int n12 = 324 + n6 + n5;
                        if (b4 || n9 > n12) {
                            n7 = n8 * n12;
                        }
                        else {
                            n7 += n8 * n9;
                        }
                        if (i == n11) {
                            throw new NumberFormatException("For input string: \"" + trim + "\"");
                        }
                    }
                    if (i >= length || (i == length - 1 && (trim.charAt(i) == 'f' || trim.charAt(i) == 'F' || trim.charAt(i) == 'd' || trim.charAt(i) == 'D'))) {
                        if (b3) {
                            return b ? FloatingDecimal.A2BC_NEGATIVE_ZERO : FloatingDecimal.A2BC_POSITIVE_ZERO;
                        }
                        return new ASCIIToBinaryBuffer(b, n7, array, n6);
                    }
                }
            }
        }
        catch (final StringIndexOutOfBoundsException ex) {}
        throw new NumberFormatException("For input string: \"" + trim + "\"");
    }
    
    static ASCIIToBinaryConverter parseHexString(final String s) {
        final Matcher matcher = HexFloatPattern.VALUE.matcher(s);
        if (!matcher.matches()) {
            throw new NumberFormatException("For input string: \"" + s + "\"");
        }
        final String group = matcher.group(1);
        final boolean b = group != null && group.equals("-");
        int length = 0;
        final String group2;
        String s2;
        int n;
        if ((group2 = matcher.group(4)) != null) {
            s2 = stripLeadingZeros(group2);
            n = s2.length();
        }
        else {
            final String stripLeadingZeros = stripLeadingZeros(matcher.group(6));
            n = stripLeadingZeros.length();
            final String group3 = matcher.group(7);
            length = group3.length();
            s2 = ((stripLeadingZeros == null) ? "" : stripLeadingZeros) + group3;
        }
        final String stripLeadingZeros2 = stripLeadingZeros(s2);
        final int length2 = stripLeadingZeros2.length();
        int n2;
        if (n >= 1) {
            n2 = 4 * (n - 1);
        }
        else {
            n2 = -4 * (length - length2 + 1);
        }
        if (length2 == 0) {
            return b ? FloatingDecimal.A2BC_NEGATIVE_ZERO : FloatingDecimal.A2BC_POSITIVE_ZERO;
        }
        final String group4 = matcher.group(8);
        final boolean b2 = group4 == null || group4.equals("+");
        long n3;
        try {
            n3 = Integer.parseInt(matcher.group(9));
        }
        catch (final NumberFormatException ex) {
            return b ? (b2 ? FloatingDecimal.A2BC_NEGATIVE_INFINITY : FloatingDecimal.A2BC_NEGATIVE_ZERO) : (b2 ? FloatingDecimal.A2BC_POSITIVE_INFINITY : FloatingDecimal.A2BC_POSITIVE_ZERO);
        }
        long n4 = (b2 ? 1L : -1L) * n3 + n2;
        boolean b3 = false;
        boolean b4 = false;
        final long n5 = 0L;
        final long n6 = getHexDigit(stripLeadingZeros2, 0);
        long n7;
        int n8;
        if (n6 == 1L) {
            n7 = (n5 | n6 << 52);
            n8 = 48;
        }
        else if (n6 <= 3L) {
            n7 = (n5 | n6 << 51);
            n8 = 47;
            ++n4;
        }
        else if (n6 <= 7L) {
            n7 = (n5 | n6 << 50);
            n8 = 46;
            n4 += 2L;
        }
        else {
            if (n6 > 15L) {
                throw new AssertionError((Object)"Result from digit conversion too large!");
            }
            n7 = (n5 | n6 << 49);
            n8 = 45;
            n4 += 3L;
        }
        int n9;
        for (n9 = 1; n9 < length2 && n8 >= 0; n8 -= 4, ++n9) {
            n7 |= (long)getHexDigit(stripLeadingZeros2, n9) << n8;
        }
        if (n9 < length2) {
            final long n10 = getHexDigit(stripLeadingZeros2, n9);
            switch (n8) {
                case -1: {
                    n7 |= (n10 & 0xEL) >> 1;
                    b3 = ((n10 & 0x1L) != 0x0L);
                    break;
                }
                case -2: {
                    n7 |= (n10 & 0xCL) >> 2;
                    b3 = ((n10 & 0x2L) != 0x0L);
                    b4 = ((n10 & 0x1L) != 0x0L);
                    break;
                }
                case -3: {
                    n7 |= (n10 & 0x8L) >> 3;
                    b3 = ((n10 & 0x4L) != 0x0L);
                    b4 = ((n10 & 0x3L) != 0x0L);
                    break;
                }
                case -4: {
                    b3 = ((n10 & 0x8L) != 0x0L);
                    b4 = ((n10 & 0x7L) != 0x0L);
                    break;
                }
                default: {
                    throw new AssertionError((Object)"Unexpected shift distance remainder.");
                }
            }
            ++n9;
            while (n9 < length2 && !b4) {
                final long n11 = getHexDigit(stripLeadingZeros2, n9);
                b4 = (b4 || n11 != 0L);
                ++n9;
            }
        }
        int n12 = b ? Integer.MIN_VALUE : 0;
        if (n4 >= -126L) {
            if (n4 > 127L) {
                n12 |= 0x7F800000;
            }
            else {
                final int n13 = 28;
                final boolean b5 = (n7 & (1L << n13) - 1L) != 0x0L || b3 || b4;
                int n14 = (int)(n7 >>> n13);
                if ((n14 & 0x3) != 0x1 || b5) {
                    ++n14;
                }
                n12 |= ((int)n4 + 126 << 23) + (n14 >> 1);
            }
        }
        else if (n4 >= -150L) {
            final int n15 = (int)(-98L - n4);
            assert n15 >= 29;
            assert n15 < 53;
            final boolean b6 = (n7 & (1L << n15) - 1L) != 0x0L || b3 || b4;
            int n16 = (int)(n7 >>> n15);
            if ((n16 & 0x3) != 0x1 || b6) {
                ++n16;
            }
            n12 |= n16 >> 1;
        }
        final float intBitsToFloat = Float.intBitsToFloat(n12);
        if (n4 > 1023L) {
            return b ? FloatingDecimal.A2BC_NEGATIVE_INFINITY : FloatingDecimal.A2BC_POSITIVE_INFINITY;
        }
        long n17;
        if (n4 <= 1023L && n4 >= -1022L) {
            n17 = ((n4 + 1023L << 52 & 0x7FF0000000000000L) | (0xFFFFFFFFFFFFFL & n7));
        }
        else {
            if (n4 < -1075L) {
                return b ? FloatingDecimal.A2BC_NEGATIVE_ZERO : FloatingDecimal.A2BC_POSITIVE_ZERO;
            }
            b4 = (b4 || b3);
            final int n18 = 53 - ((int)n4 + 1074 + 1);
            assert n18 >= 1 && n18 <= 53;
            b3 = ((n7 & 1L << n18 - 1) != 0x0L);
            if (n18 > 1) {
                final long n19 = ~(-1L << n18 - 1);
                b4 = (b4 || (n7 & n19) != 0x0L);
            }
            n17 = (0x0L | (0xFFFFFFFFFFFFFL & n7 >> n18));
        }
        final boolean b7 = (n17 & 0x1L) == 0x0L;
        if ((b7 && b3 && b4) || (!b7 && b3)) {
            ++n17;
        }
        return new PreparedASCIIToBinaryBuffer(b ? Double.longBitsToDouble(n17 | Long.MIN_VALUE) : Double.longBitsToDouble(n17), intBitsToFloat);
    }
    
    static String stripLeadingZeros(final String s) {
        if (!s.isEmpty() && s.charAt(0) == '0') {
            for (int i = 1; i < s.length(); ++i) {
                if (s.charAt(i) != '0') {
                    return s.substring(i);
                }
            }
            return "";
        }
        return s;
    }
    
    static int getHexDigit(final String s, final int n) {
        final int digit = Character.digit(s.charAt(n), 16);
        if (digit <= -1 || digit >= 16) {
            throw new AssertionError((Object)("Unexpected failure of digit conversion of " + s.charAt(n)));
        }
        return digit;
    }
    
    static {
        INFINITY_LENGTH = "Infinity".length();
        NAN_LENGTH = "NaN".length();
        B2AC_POSITIVE_INFINITY = new ExceptionalBinaryToASCIIBuffer("Infinity", false);
        B2AC_NEGATIVE_INFINITY = new ExceptionalBinaryToASCIIBuffer("-Infinity", true);
        B2AC_NOT_A_NUMBER = new ExceptionalBinaryToASCIIBuffer("NaN", false);
        B2AC_POSITIVE_ZERO = new BinaryToASCIIBuffer(false, new char[] { '0' });
        B2AC_NEGATIVE_ZERO = new BinaryToASCIIBuffer(true, new char[] { '0' });
        threadLocalBinaryToASCIIBuffer = new ThreadLocal<BinaryToASCIIBuffer>() {
            @Override
            protected BinaryToASCIIBuffer initialValue() {
                return new BinaryToASCIIBuffer();
            }
        };
        A2BC_POSITIVE_INFINITY = new PreparedASCIIToBinaryBuffer(Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        A2BC_NEGATIVE_INFINITY = new PreparedASCIIToBinaryBuffer(Double.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        A2BC_NOT_A_NUMBER = new PreparedASCIIToBinaryBuffer(Double.NaN, Float.NaN);
        A2BC_POSITIVE_ZERO = new PreparedASCIIToBinaryBuffer(0.0, 0.0f);
        A2BC_NEGATIVE_ZERO = new PreparedASCIIToBinaryBuffer(-0.0, -0.0f);
    }
    
    private static class ExceptionalBinaryToASCIIBuffer implements BinaryToASCIIConverter
    {
        private final String image;
        private boolean isNegative;
        
        public ExceptionalBinaryToASCIIBuffer(final String image, final boolean isNegative) {
            this.image = image;
            this.isNegative = isNegative;
        }
        
        @Override
        public String toJavaFormatString() {
            return this.image;
        }
        
        @Override
        public void appendTo(final Appendable appendable) {
            if (appendable instanceof StringBuilder) {
                ((StringBuilder)appendable).append(this.image);
            }
            else if (appendable instanceof StringBuffer) {
                ((StringBuffer)appendable).append(this.image);
            }
            else {
                assert false;
            }
        }
        
        @Override
        public int getDecimalExponent() {
            throw new IllegalArgumentException("Exceptional value does not have an exponent");
        }
        
        @Override
        public int getDigits(final char[] array) {
            throw new IllegalArgumentException("Exceptional value does not have digits");
        }
        
        @Override
        public boolean isNegative() {
            return this.isNegative;
        }
        
        @Override
        public boolean isExceptional() {
            return true;
        }
        
        @Override
        public boolean digitsRoundedUp() {
            throw new IllegalArgumentException("Exceptional value is not rounded");
        }
        
        @Override
        public boolean decimalDigitsExact() {
            throw new IllegalArgumentException("Exceptional value is not exact");
        }
    }
    
    static class BinaryToASCIIBuffer implements BinaryToASCIIConverter
    {
        private boolean isNegative;
        private int decExponent;
        private int firstDigitIndex;
        private int nDigits;
        private final char[] digits;
        private final char[] buffer;
        private boolean exactDecimalConversion;
        private boolean decimalDigitsRoundedUp;
        private static int[] insignificantDigitsNumber;
        private static final int[] N_5_BITS;
        
        BinaryToASCIIBuffer() {
            this.buffer = new char[26];
            this.exactDecimalConversion = false;
            this.decimalDigitsRoundedUp = false;
            this.digits = new char[20];
        }
        
        BinaryToASCIIBuffer(final boolean isNegative, final char[] digits) {
            this.buffer = new char[26];
            this.exactDecimalConversion = false;
            this.decimalDigitsRoundedUp = false;
            this.isNegative = isNegative;
            this.decExponent = 0;
            this.digits = digits;
            this.firstDigitIndex = 0;
            this.nDigits = digits.length;
        }
        
        @Override
        public String toJavaFormatString() {
            return new String(this.buffer, 0, this.getChars(this.buffer));
        }
        
        @Override
        public void appendTo(final Appendable appendable) {
            final int chars = this.getChars(this.buffer);
            if (appendable instanceof StringBuilder) {
                ((StringBuilder)appendable).append(this.buffer, 0, chars);
            }
            else if (appendable instanceof StringBuffer) {
                ((StringBuffer)appendable).append(this.buffer, 0, chars);
            }
            else {
                assert false;
            }
        }
        
        @Override
        public int getDecimalExponent() {
            return this.decExponent;
        }
        
        @Override
        public int getDigits(final char[] array) {
            System.arraycopy(this.digits, this.firstDigitIndex, array, 0, this.nDigits);
            return this.nDigits;
        }
        
        @Override
        public boolean isNegative() {
            return this.isNegative;
        }
        
        @Override
        public boolean isExceptional() {
            return false;
        }
        
        @Override
        public boolean digitsRoundedUp() {
            return this.decimalDigitsRoundedUp;
        }
        
        @Override
        public boolean decimalDigitsExact() {
            return this.exactDecimalConversion;
        }
        
        private void setSign(final boolean isNegative) {
            this.isNegative = isNegative;
        }
        
        private void developLongDigits(int n, long n2, final int n3) {
            if (n3 != 0) {
                final long n4 = FDBigInteger.LONG_5_POW[n3] << n3;
                final long n5 = n2 % n4;
                n2 /= n4;
                n += n3;
                if (n5 >= n4 >> 1) {
                    ++n2;
                }
            }
            int firstDigitIndex = this.digits.length - 1;
            if (n2 <= 2147483647L) {
                assert n2 > 0L : n2;
                final int n6 = (int)n2;
                int i;
                int j;
                for (i = n6 % 10, j = n6 / 10; i == 0; i = j % 10, j /= 10) {
                    ++n;
                }
                while (j != 0) {
                    this.digits[firstDigitIndex--] = (char)(i + 48);
                    ++n;
                    i = j % 10;
                    j /= 10;
                }
                this.digits[firstDigitIndex] = (char)(i + 48);
            }
            else {
                int k;
                for (k = (int)(n2 % 10L), n2 /= 10L; k == 0; k = (int)(n2 % 10L), n2 /= 10L) {
                    ++n;
                }
                while (n2 != 0L) {
                    this.digits[firstDigitIndex--] = (char)(k + 48);
                    ++n;
                    k = (int)(n2 % 10L);
                    n2 /= 10L;
                }
                this.digits[firstDigitIndex] = (char)(k + 48);
            }
            this.decExponent = n + 1;
            this.firstDigitIndex = firstDigitIndex;
            this.nDigits = this.digits.length - firstDigitIndex;
        }
        
        private void dtoa(final int n, long n2, final int n3, final boolean b) {
            assert n2 > 0L;
            assert (n2 & 0x10000000000000L) != 0x0L;
            final int numberOfTrailingZeros = Long.numberOfTrailingZeros(n2);
            final int n4 = 53 - numberOfTrailingZeros;
            this.decimalDigitsRoundedUp = false;
            this.exactDecimalConversion = false;
            final int max = Math.max(0, n4 - n - 1);
            if (n <= 62 && n >= -21 && max < FDBigInteger.LONG_5_POW.length && n4 + BinaryToASCIIBuffer.N_5_BITS[max] < 64 && max == 0) {
                int insignificantDigitsForPow2;
                if (n > n3) {
                    insignificantDigitsForPow2 = insignificantDigitsForPow2(n - n3 - 1);
                }
                else {
                    insignificantDigitsForPow2 = 0;
                }
                if (n >= 52) {
                    n2 <<= n - 52;
                }
                else {
                    n2 >>>= 52 - n;
                }
                this.developLongDigits(0, n2, insignificantDigitsForPow2);
                return;
            }
            int estimateDecExp = estimateDecExp(n2, n);
            final int max2 = Math.max(0, -estimateDecExp);
            final int n5 = max2 + max + n;
            final int max3 = Math.max(0, estimateDecExp);
            final int n6 = max3 + max;
            final int n7 = max2;
            final int n8 = n5 - n3;
            n2 >>>= numberOfTrailingZeros;
            final int n9 = n5 - (n4 - 1);
            final int min = Math.min(n9, n6);
            int n10 = n9 - min;
            int n11 = n6 - min;
            int n12 = n8 - min;
            if (n4 == 1) {
                --n12;
            }
            if (n12 < 0) {
                n10 -= n12;
                n11 -= n12;
                n12 = 0;
            }
            final int n13 = n4 + n10 + ((max2 < BinaryToASCIIBuffer.N_5_BITS.length) ? BinaryToASCIIBuffer.N_5_BITS[max2] : (max2 * 3));
            final int n14 = n11 + 1 + ((max3 + 1 < BinaryToASCIIBuffer.N_5_BITS.length) ? BinaryToASCIIBuffer.N_5_BITS[max3 + 1] : ((max3 + 1) * 3));
            int nDigits;
            int n22;
            int n23;
            long n25;
            if (n13 < 64 && n14 < 64) {
                if (n13 < 32 && n14 < 32) {
                    final int n15 = (int)n2 * FDBigInteger.SMALL_5_POW[max2] << n10;
                    final int n16 = FDBigInteger.SMALL_5_POW[max3] << n11;
                    final int n17 = FDBigInteger.SMALL_5_POW[n7] << n12;
                    final int n18 = n16 * 10;
                    nDigits = 0;
                    final int n19 = n15 / n16;
                    int n20 = 10 * (n15 % n16);
                    int n21 = n17 * 10;
                    n22 = ((n20 < n21) ? 1 : 0);
                    n23 = ((n20 + n21 > n18) ? 1 : 0);
                    assert n19 < 10 : n19;
                    if (n19 == 0 && n23 == 0) {
                        --estimateDecExp;
                    }
                    else {
                        this.digits[nDigits++] = (char)(48 + n19);
                    }
                    if (!b || estimateDecExp < -3 || estimateDecExp >= 8) {
                        n22 = (n23 = 0);
                    }
                    while (n22 == 0 && n23 == 0) {
                        final int n24 = n20 / n16;
                        n20 = 10 * (n20 % n16);
                        n21 *= 10;
                        assert n24 < 10 : n24;
                        if (n21 > 0L) {
                            n22 = ((n20 < n21) ? 1 : 0);
                            n23 = ((n20 + n21 > n18) ? 1 : 0);
                        }
                        else {
                            n22 = 1;
                            n23 = 1;
                        }
                        this.digits[nDigits++] = (char)(48 + n24);
                    }
                    n25 = (n20 << 1) - n18;
                    this.exactDecimalConversion = (n20 == 0);
                }
                else {
                    final long n26 = n2 * FDBigInteger.LONG_5_POW[max2] << n10;
                    final long n27 = FDBigInteger.LONG_5_POW[max3] << n11;
                    final long n28 = FDBigInteger.LONG_5_POW[n7] << n12;
                    final long n29 = n27 * 10L;
                    nDigits = 0;
                    final int n30 = (int)(n26 / n27);
                    long n31 = 10L * (n26 % n27);
                    long n32 = n28 * 10L;
                    n22 = ((n31 < n32) ? 1 : 0);
                    n23 = ((n31 + n32 > n29) ? 1 : 0);
                    assert n30 < 10 : n30;
                    if (n30 == 0 && n23 == 0) {
                        --estimateDecExp;
                    }
                    else {
                        this.digits[nDigits++] = (char)(48 + n30);
                    }
                    if (!b || estimateDecExp < -3 || estimateDecExp >= 8) {
                        n22 = (n23 = 0);
                    }
                    while (n22 == 0 && n23 == 0) {
                        final int n33 = (int)(n31 / n27);
                        n31 = 10L * (n31 % n27);
                        n32 *= 10L;
                        assert n33 < 10 : n33;
                        if (n32 > 0L) {
                            n22 = ((n31 < n32) ? 1 : 0);
                            n23 = ((n31 + n32 > n29) ? 1 : 0);
                        }
                        else {
                            n22 = 1;
                            n23 = 1;
                        }
                        this.digits[nDigits++] = (char)(48 + n33);
                    }
                    n25 = (n31 << 1) - n29;
                    this.exactDecimalConversion = (n31 == 0L);
                }
            }
            else {
                final FDBigInteger valueOfPow52 = FDBigInteger.valueOfPow52(max3, n11);
                final int normalizationBias = valueOfPow52.getNormalizationBias();
                final FDBigInteger leftShift = valueOfPow52.leftShift(normalizationBias);
                FDBigInteger fdBigInteger = FDBigInteger.valueOfMulPow52(n2, max2, n10 + normalizationBias);
                FDBigInteger fdBigInteger2 = FDBigInteger.valueOfPow52(n7 + 1, n12 + normalizationBias + 1);
                final FDBigInteger valueOfPow53 = FDBigInteger.valueOfPow52(max3 + 1, n11 + normalizationBias + 1);
                nDigits = 0;
                final int quoRemIteration = fdBigInteger.quoRemIteration(leftShift);
                n22 = ((fdBigInteger.cmp(fdBigInteger2) < 0) ? 1 : 0);
                n23 = ((valueOfPow53.addAndCmp(fdBigInteger, fdBigInteger2) <= 0) ? 1 : 0);
                assert quoRemIteration < 10 : quoRemIteration;
                if (quoRemIteration == 0 && n23 == 0) {
                    --estimateDecExp;
                }
                else {
                    this.digits[nDigits++] = (char)(48 + quoRemIteration);
                }
                if (!b || estimateDecExp < -3 || estimateDecExp >= 8) {
                    n22 = (n23 = 0);
                }
                while (n22 == 0 && n23 == 0) {
                    final int quoRemIteration2 = fdBigInteger.quoRemIteration(leftShift);
                    assert quoRemIteration2 < 10 : quoRemIteration2;
                    fdBigInteger2 = fdBigInteger2.multBy10();
                    n22 = ((fdBigInteger.cmp(fdBigInteger2) < 0) ? 1 : 0);
                    n23 = ((valueOfPow53.addAndCmp(fdBigInteger, fdBigInteger2) <= 0) ? 1 : 0);
                    this.digits[nDigits++] = (char)(48 + quoRemIteration2);
                }
                if (n23 != 0 && n22 != 0) {
                    fdBigInteger = fdBigInteger.leftShift(1);
                    n25 = fdBigInteger.cmp(valueOfPow53);
                }
                else {
                    n25 = 0L;
                }
                this.exactDecimalConversion = (fdBigInteger.cmp(FDBigInteger.ZERO) == 0);
            }
            this.decExponent = estimateDecExp + 1;
            this.firstDigitIndex = 0;
            this.nDigits = nDigits;
            if (n23 != 0) {
                if (n22 != 0) {
                    if (n25 == 0L) {
                        if ((this.digits[this.firstDigitIndex + this.nDigits - 1] & '\u0001') != 0x0) {
                            this.roundup();
                        }
                    }
                    else if (n25 > 0L) {
                        this.roundup();
                    }
                }
                else {
                    this.roundup();
                }
            }
        }
        
        private void roundup() {
            int n = this.firstDigitIndex + this.nDigits - 1;
            char c = this.digits[n];
            if (c == '9') {
                while (c == '9' && n > this.firstDigitIndex) {
                    this.digits[n] = '0';
                    c = this.digits[--n];
                }
                if (c == '9') {
                    ++this.decExponent;
                    this.digits[this.firstDigitIndex] = '1';
                    return;
                }
            }
            this.digits[n] = (char)(c + '\u0001');
            this.decimalDigitsRoundedUp = true;
        }
        
        static int estimateDecExp(final long n, final int n2) {
            final double n3 = (Double.longBitsToDouble(0x3FF0000000000000L | (n & 0xFFFFFFFFFFFFFL)) - 1.5) * 0.289529654 + 0.176091259 + n2 * 0.301029995663981;
            final long doubleToRawLongBits = Double.doubleToRawLongBits(n3);
            final int n4 = (int)((doubleToRawLongBits & 0x7FF0000000000000L) >> 52) - 1023;
            final boolean b = (doubleToRawLongBits & Long.MIN_VALUE) != 0x0L;
            if (n4 >= 0 && n4 < 52) {
                final long n5 = 4503599627370495L >> n4;
                final int n6 = (int)(((doubleToRawLongBits & 0xFFFFFFFFFFFFFL) | 0x10000000000000L) >> 52 - n4);
                return b ? (((n5 & doubleToRawLongBits) == 0x0L) ? (-n6) : (-n6 - 1)) : n6;
            }
            if (n4 < 0) {
                return ((doubleToRawLongBits & Long.MAX_VALUE) == 0x0L) ? 0 : (b ? -1 : 0);
            }
            return (int)n3;
        }
        
        private static int insignificantDigits(int n) {
            int n2;
            for (n2 = 0; n >= 10L; n /= (int)10L, ++n2) {}
            return n2;
        }
        
        private static int insignificantDigitsForPow2(final int n) {
            if (n > 1 && n < BinaryToASCIIBuffer.insignificantDigitsNumber.length) {
                return BinaryToASCIIBuffer.insignificantDigitsNumber[n];
            }
            return 0;
        }
        
        private int getChars(final char[] array) {
            assert this.nDigits <= 19 : this.nDigits;
            int n = 0;
            if (this.isNegative) {
                array[0] = '-';
                n = 1;
            }
            if (this.decExponent > 0 && this.decExponent < 8) {
                final int min = Math.min(this.nDigits, this.decExponent);
                System.arraycopy(this.digits, this.firstDigitIndex, array, n, min);
                n += min;
                if (min < this.decExponent) {
                    final int n2 = this.decExponent - min;
                    Arrays.fill(array, n, n + n2, '0');
                    n += n2;
                    array[n++] = '.';
                    array[n++] = '0';
                }
                else {
                    array[n++] = '.';
                    if (min < this.nDigits) {
                        final int n3 = this.nDigits - min;
                        System.arraycopy(this.digits, this.firstDigitIndex + min, array, n, n3);
                        n += n3;
                    }
                    else {
                        array[n++] = '0';
                    }
                }
            }
            else if (this.decExponent <= 0 && this.decExponent > -3) {
                array[n++] = '0';
                array[n++] = '.';
                if (this.decExponent != 0) {
                    Arrays.fill(array, n, n - this.decExponent, '0');
                    n -= this.decExponent;
                }
                System.arraycopy(this.digits, this.firstDigitIndex, array, n, this.nDigits);
                n += this.nDigits;
            }
            else {
                array[n++] = this.digits[this.firstDigitIndex];
                array[n++] = '.';
                if (this.nDigits > 1) {
                    System.arraycopy(this.digits, this.firstDigitIndex + 1, array, n, this.nDigits - 1);
                    n += this.nDigits - 1;
                }
                else {
                    array[n++] = '0';
                }
                array[n++] = 'E';
                int n4;
                if (this.decExponent <= 0) {
                    array[n++] = '-';
                    n4 = -this.decExponent + 1;
                }
                else {
                    n4 = this.decExponent - 1;
                }
                if (n4 <= 9) {
                    array[n++] = (char)(n4 + 48);
                }
                else if (n4 <= 99) {
                    array[n++] = (char)(n4 / 10 + 48);
                    array[n++] = (char)(n4 % 10 + 48);
                }
                else {
                    array[n++] = (char)(n4 / 100 + 48);
                    final int n5 = n4 % 100;
                    array[n++] = (char)(n5 / 10 + 48);
                    array[n++] = (char)(n5 % 10 + 48);
                }
            }
            return n;
        }
        
        static {
            BinaryToASCIIBuffer.insignificantDigitsNumber = new int[] { 0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 12, 12, 13, 13, 13, 14, 14, 14, 15, 15, 15, 15, 16, 16, 16, 17, 17, 17, 18, 18, 18, 19 };
            N_5_BITS = new int[] { 0, 3, 5, 7, 10, 12, 14, 17, 19, 21, 24, 26, 28, 31, 33, 35, 38, 40, 42, 45, 47, 49, 52, 54, 56, 59, 61 };
        }
    }
    
    static class PreparedASCIIToBinaryBuffer implements ASCIIToBinaryConverter
    {
        private final double doubleVal;
        private final float floatVal;
        
        public PreparedASCIIToBinaryBuffer(final double doubleVal, final float floatVal) {
            this.doubleVal = doubleVal;
            this.floatVal = floatVal;
        }
        
        @Override
        public double doubleValue() {
            return this.doubleVal;
        }
        
        @Override
        public float floatValue() {
            return this.floatVal;
        }
    }
    
    static class ASCIIToBinaryBuffer implements ASCIIToBinaryConverter
    {
        boolean isNegative;
        int decExponent;
        char[] digits;
        int nDigits;
        private static final double[] SMALL_10_POW;
        private static final float[] SINGLE_SMALL_10_POW;
        private static final double[] BIG_10_POW;
        private static final double[] TINY_10_POW;
        private static final int MAX_SMALL_TEN;
        private static final int SINGLE_MAX_SMALL_TEN;
        
        ASCIIToBinaryBuffer(final boolean isNegative, final int decExponent, final char[] digits, final int nDigits) {
            this.isNegative = isNegative;
            this.decExponent = decExponent;
            this.digits = digits;
            this.nDigits = nDigits;
        }
        
        @Override
        public double doubleValue() {
            final int min = Math.min(this.nDigits, 16);
            int n = this.digits[0] - '0';
            final int min2 = Math.min(min, 9);
            for (int i = 1; i < min2; ++i) {
                n = n * 10 + this.digits[i] - 48;
            }
            long n2 = n;
            for (int j = min2; j < min; ++j) {
                n2 = n2 * 10L + (this.digits[j] - '0');
            }
            double n3 = (double)n2;
            final int n4 = this.decExponent - min;
            if (this.nDigits <= 15) {
                if (n4 == 0 || n3 == 0.0) {
                    return this.isNegative ? (-n3) : n3;
                }
                if (n4 >= 0) {
                    if (n4 <= ASCIIToBinaryBuffer.MAX_SMALL_TEN) {
                        final double n5 = n3 * ASCIIToBinaryBuffer.SMALL_10_POW[n4];
                        return this.isNegative ? (-n5) : n5;
                    }
                    final int n6 = 15 - min;
                    if (n4 <= ASCIIToBinaryBuffer.MAX_SMALL_TEN + n6) {
                        final double n7 = n3 * ASCIIToBinaryBuffer.SMALL_10_POW[n6] * ASCIIToBinaryBuffer.SMALL_10_POW[n4 - n6];
                        return this.isNegative ? (-n7) : n7;
                    }
                }
                else if (n4 >= -ASCIIToBinaryBuffer.MAX_SMALL_TEN) {
                    final double n8 = n3 / ASCIIToBinaryBuffer.SMALL_10_POW[-n4];
                    return this.isNegative ? (-n8) : n8;
                }
            }
            if (n4 > 0) {
                if (this.decExponent > 309) {
                    return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
                }
                if ((n4 & 0xF) != 0x0) {
                    n3 *= ASCIIToBinaryBuffer.SMALL_10_POW[n4 & 0xF];
                }
                int k;
                if ((k = n4 >> 4) != 0) {
                    int n9 = 0;
                    while (k > 1) {
                        if ((k & 0x1) != 0x0) {
                            n3 *= ASCIIToBinaryBuffer.BIG_10_POW[n9];
                        }
                        ++n9;
                        k >>= 1;
                    }
                    double n10 = n3 * ASCIIToBinaryBuffer.BIG_10_POW[n9];
                    if (Double.isInfinite(n10)) {
                        if (Double.isInfinite(n3 / 2.0 * ASCIIToBinaryBuffer.BIG_10_POW[n9])) {
                            return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
                        }
                        n10 = Double.MAX_VALUE;
                    }
                    n3 = n10;
                }
            }
            else if (n4 < 0) {
                final int n11 = -n4;
                if (this.decExponent < -325) {
                    return this.isNegative ? -0.0 : 0.0;
                }
                if ((n11 & 0xF) != 0x0) {
                    n3 /= ASCIIToBinaryBuffer.SMALL_10_POW[n11 & 0xF];
                }
                int l;
                if ((l = n11 >> 4) != 0) {
                    int n12 = 0;
                    while (l > 1) {
                        if ((l & 0x1) != 0x0) {
                            n3 *= ASCIIToBinaryBuffer.TINY_10_POW[n12];
                        }
                        ++n12;
                        l >>= 1;
                    }
                    double n13 = n3 * ASCIIToBinaryBuffer.TINY_10_POW[n12];
                    if (n13 == 0.0) {
                        if (n3 * 2.0 * ASCIIToBinaryBuffer.TINY_10_POW[n12] == 0.0) {
                            return this.isNegative ? -0.0 : 0.0;
                        }
                        n13 = Double.MIN_VALUE;
                    }
                    n3 = n13;
                }
            }
            if (this.nDigits > 1100) {
                this.nDigits = 1101;
                this.digits[1100] = '1';
            }
            final FDBigInteger fdBigInteger = new FDBigInteger(n2, this.digits, min, this.nDigits);
            final int n14 = this.decExponent - this.nDigits;
            long doubleToRawLongBits = Double.doubleToRawLongBits(n3);
            final int max = Math.max(0, -n14);
            final int max2 = Math.max(0, n14);
            final FDBigInteger multByPow52 = fdBigInteger.multByPow52(max2, 0);
            multByPow52.makeImmutable();
            FDBigInteger leftShift = null;
            int n15 = 0;
            do {
                int n16 = (int)(doubleToRawLongBits >>> 52);
                final long n17 = doubleToRawLongBits & 0xFFFFFFFFFFFFFL;
                long n18;
                if (n16 > 0) {
                    n18 = (n17 | 0x10000000000000L);
                }
                else {
                    assert n17 != 0L : n17;
                    final int n19 = Long.numberOfLeadingZeros(n17) - 11;
                    n18 = n17 << n19;
                    n16 = 1 - n19;
                }
                n16 -= 1023;
                final int numberOfTrailingZeros = Long.numberOfTrailingZeros(n18);
                final long n20 = n18 >>> numberOfTrailingZeros;
                final int n21 = n16 - 52 + numberOfTrailingZeros;
                final int n22 = 53 - numberOfTrailingZeros;
                int n23 = max;
                int n24 = max2;
                if (n21 >= 0) {
                    n23 += n21;
                }
                else {
                    n24 -= n21;
                }
                final int n25 = n23;
                int n26;
                if (n16 <= -1023) {
                    n26 = n16 + numberOfTrailingZeros + 1023;
                }
                else {
                    n26 = 1 + numberOfTrailingZeros;
                }
                final int n27 = n23 + n26;
                final int n28 = n24 + n26;
                final int min3 = Math.min(n27, Math.min(n28, n25));
                final int n29 = n27 - min3;
                final int n30 = n28 - min3;
                int n31 = n25 - min3;
                final FDBigInteger valueOfMulPow52 = FDBigInteger.valueOfMulPow52(n20, max, n29);
                if (leftShift == null || n15 != n30) {
                    leftShift = multByPow52.leftShift(n30);
                    n15 = n30;
                }
                final int cmp;
                boolean b;
                FDBigInteger fdBigInteger2;
                if ((cmp = valueOfMulPow52.cmp(leftShift)) > 0) {
                    b = true;
                    fdBigInteger2 = valueOfMulPow52.leftInplaceSub(leftShift);
                    if (n22 == 1 && n21 > -1022 && --n31 < 0) {
                        n31 = 0;
                        fdBigInteger2 = fdBigInteger2.leftShift(1);
                    }
                }
                else {
                    if (cmp >= 0) {
                        break;
                    }
                    b = false;
                    fdBigInteger2 = leftShift.rightInplaceSub(valueOfMulPow52);
                }
                final int cmpPow52 = fdBigInteger2.cmpPow52(max, n31);
                if (cmpPow52 < 0) {
                    break;
                }
                if (cmpPow52 == 0) {
                    if ((doubleToRawLongBits & 0x1L) != 0x0L) {
                        doubleToRawLongBits += (b ? -1L : 1L);
                        break;
                    }
                    break;
                }
                else {
                    doubleToRawLongBits += (b ? -1L : 1L);
                }
            } while (doubleToRawLongBits != 0L && doubleToRawLongBits != 9218868437227405312L);
            if (this.isNegative) {
                doubleToRawLongBits |= Long.MIN_VALUE;
            }
            return Double.longBitsToDouble(doubleToRawLongBits);
        }
        
        @Override
        public float floatValue() {
            final int min = Math.min(this.nDigits, 8);
            int n = this.digits[0] - '0';
            for (int i = 1; i < min; ++i) {
                n = n * 10 + this.digits[i] - 48;
            }
            final float n2 = (float)n;
            final int n3 = this.decExponent - min;
            if (this.nDigits <= 7) {
                if (n3 == 0 || n2 == 0.0f) {
                    return this.isNegative ? (-n2) : n2;
                }
                if (n3 >= 0) {
                    if (n3 <= ASCIIToBinaryBuffer.SINGLE_MAX_SMALL_TEN) {
                        final float n4 = n2 * ASCIIToBinaryBuffer.SINGLE_SMALL_10_POW[n3];
                        return this.isNegative ? (-n4) : n4;
                    }
                    final int n5 = 7 - min;
                    if (n3 <= ASCIIToBinaryBuffer.SINGLE_MAX_SMALL_TEN + n5) {
                        final float n6 = n2 * ASCIIToBinaryBuffer.SINGLE_SMALL_10_POW[n5] * ASCIIToBinaryBuffer.SINGLE_SMALL_10_POW[n3 - n5];
                        return this.isNegative ? (-n6) : n6;
                    }
                }
                else if (n3 >= -ASCIIToBinaryBuffer.SINGLE_MAX_SMALL_TEN) {
                    final float n7 = n2 / ASCIIToBinaryBuffer.SINGLE_SMALL_10_POW[-n3];
                    return this.isNegative ? (-n7) : n7;
                }
            }
            else if (this.decExponent >= this.nDigits && this.nDigits + this.decExponent <= 15) {
                long n8 = n;
                for (int j = min; j < this.nDigits; ++j) {
                    n8 = n8 * 10L + (this.digits[j] - '0');
                }
                final float n9 = (float)(n8 * ASCIIToBinaryBuffer.SMALL_10_POW[this.decExponent - this.nDigits]);
                return this.isNegative ? (-n9) : n9;
            }
            double n10 = n2;
            if (n3 > 0) {
                if (this.decExponent > 39) {
                    return this.isNegative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
                }
                if ((n3 & 0xF) != 0x0) {
                    n10 *= ASCIIToBinaryBuffer.SMALL_10_POW[n3 & 0xF];
                }
                int k;
                if ((k = n3 >> 4) != 0) {
                    int n11 = 0;
                    while (k > 0) {
                        if ((k & 0x1) != 0x0) {
                            n10 *= ASCIIToBinaryBuffer.BIG_10_POW[n11];
                        }
                        ++n11;
                        k >>= 1;
                    }
                }
            }
            else if (n3 < 0) {
                final int n12 = -n3;
                if (this.decExponent < -46) {
                    return this.isNegative ? -0.0f : 0.0f;
                }
                if ((n12 & 0xF) != 0x0) {
                    n10 /= ASCIIToBinaryBuffer.SMALL_10_POW[n12 & 0xF];
                }
                int l;
                if ((l = n12 >> 4) != 0) {
                    int n13 = 0;
                    while (l > 0) {
                        if ((l & 0x1) != 0x0) {
                            n10 *= ASCIIToBinaryBuffer.TINY_10_POW[n13];
                        }
                        ++n13;
                        l >>= 1;
                    }
                }
            }
            final float max = Math.max(Float.MIN_VALUE, Math.min(Float.MAX_VALUE, (float)n10));
            if (this.nDigits > 200) {
                this.nDigits = 201;
                this.digits[200] = '1';
            }
            final FDBigInteger fdBigInteger = new FDBigInteger(n, this.digits, min, this.nDigits);
            final int n14 = this.decExponent - this.nDigits;
            int floatToRawIntBits = Float.floatToRawIntBits(max);
            final int max2 = Math.max(0, -n14);
            final int max3 = Math.max(0, n14);
            final FDBigInteger multByPow52 = fdBigInteger.multByPow52(max3, 0);
            multByPow52.makeImmutable();
            FDBigInteger leftShift = null;
            int n15 = 0;
            do {
                int n16 = floatToRawIntBits >>> 23;
                final int n17 = floatToRawIntBits & 0x7FFFFF;
                int n18;
                if (n16 > 0) {
                    n18 = (n17 | 0x800000);
                }
                else {
                    assert n17 != 0 : n17;
                    final int n19 = Integer.numberOfLeadingZeros(n17) - 8;
                    n18 = n17 << n19;
                    n16 = 1 - n19;
                }
                n16 -= 127;
                final int numberOfTrailingZeros = Integer.numberOfTrailingZeros(n18);
                final int n20 = n18 >>> numberOfTrailingZeros;
                final int n21 = n16 - 23 + numberOfTrailingZeros;
                final int n22 = 24 - numberOfTrailingZeros;
                int n23 = max2;
                int n24 = max3;
                if (n21 >= 0) {
                    n23 += n21;
                }
                else {
                    n24 -= n21;
                }
                final int n25 = n23;
                int n26;
                if (n16 <= -127) {
                    n26 = n16 + numberOfTrailingZeros + 127;
                }
                else {
                    n26 = 1 + numberOfTrailingZeros;
                }
                final int n27 = n23 + n26;
                final int n28 = n24 + n26;
                final int min2 = Math.min(n27, Math.min(n28, n25));
                final int n29 = n27 - min2;
                final int n30 = n28 - min2;
                int n31 = n25 - min2;
                final FDBigInteger valueOfMulPow52 = FDBigInteger.valueOfMulPow52(n20, max2, n29);
                if (leftShift == null || n15 != n30) {
                    leftShift = multByPow52.leftShift(n30);
                    n15 = n30;
                }
                final int cmp;
                boolean b;
                FDBigInteger fdBigInteger2;
                if ((cmp = valueOfMulPow52.cmp(leftShift)) > 0) {
                    b = true;
                    fdBigInteger2 = valueOfMulPow52.leftInplaceSub(leftShift);
                    if (n22 == 1 && n21 > -126 && --n31 < 0) {
                        n31 = 0;
                        fdBigInteger2 = fdBigInteger2.leftShift(1);
                    }
                }
                else {
                    if (cmp >= 0) {
                        break;
                    }
                    b = false;
                    fdBigInteger2 = leftShift.rightInplaceSub(valueOfMulPow52);
                }
                final int cmpPow52 = fdBigInteger2.cmpPow52(max2, n31);
                if (cmpPow52 < 0) {
                    break;
                }
                if (cmpPow52 == 0) {
                    if ((floatToRawIntBits & 0x1) != 0x0) {
                        floatToRawIntBits += (b ? -1 : 1);
                        break;
                    }
                    break;
                }
                else {
                    floatToRawIntBits += (b ? -1 : 1);
                }
            } while (floatToRawIntBits != 0 && floatToRawIntBits != 2139095040);
            if (this.isNegative) {
                floatToRawIntBits |= Integer.MIN_VALUE;
            }
            return Float.intBitsToFloat(floatToRawIntBits);
        }
        
        static {
            SMALL_10_POW = new double[] { 1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, 1.0E7, 1.0E8, 1.0E9, 1.0E10, 1.0E11, 1.0E12, 1.0E13, 1.0E14, 1.0E15, 1.0E16, 1.0E17, 1.0E18, 1.0E19, 1.0E20, 1.0E21, 1.0E22 };
            SINGLE_SMALL_10_POW = new float[] { 1.0f, 10.0f, 100.0f, 1000.0f, 10000.0f, 100000.0f, 1000000.0f, 1.0E7f, 1.0E8f, 1.0E9f, 1.0E10f };
            BIG_10_POW = new double[] { 1.0E16, 1.0E32, 1.0E64, 1.0E128, 1.0E256 };
            TINY_10_POW = new double[] { 1.0E-16, 1.0E-32, 1.0E-64, 1.0E-128, 1.0E-256 };
            MAX_SMALL_TEN = ASCIIToBinaryBuffer.SMALL_10_POW.length - 1;
            SINGLE_MAX_SMALL_TEN = ASCIIToBinaryBuffer.SINGLE_SMALL_10_POW.length - 1;
        }
    }
    
    private static class HexFloatPattern
    {
        private static final Pattern VALUE;
        
        static {
            VALUE = Pattern.compile("([-+])?0[xX](((\\p{XDigit}+)\\.?)|((\\p{XDigit}*)\\.(\\p{XDigit}+)))[pP]([-+])?(\\p{Digit}+)[fFdD]?");
        }
    }
    
    interface ASCIIToBinaryConverter
    {
        double doubleValue();
        
        float floatValue();
    }
    
    public interface BinaryToASCIIConverter
    {
        String toJavaFormatString();
        
        void appendTo(final Appendable p0);
        
        int getDecimalExponent();
        
        int getDigits(final char[] p0);
        
        boolean isNegative();
        
        boolean isExceptional();
        
        boolean digitsRoundedUp();
        
        boolean decimalDigitsExact();
    }
}
