package java.text;

import java.math.BigInteger;
import sun.misc.FloatingDecimal;
import java.math.BigDecimal;
import java.math.RoundingMode;

final class DigitList implements Cloneable
{
    public static final int MAX_COUNT = 19;
    public int decimalAt;
    public int count;
    public char[] digits;
    private char[] data;
    private RoundingMode roundingMode;
    private boolean isNegative;
    private static final char[] LONG_MIN_REP;
    private StringBuffer tempBuffer;
    
    DigitList() {
        this.decimalAt = 0;
        this.count = 0;
        this.digits = new char[19];
        this.roundingMode = RoundingMode.HALF_EVEN;
        this.isNegative = false;
    }
    
    boolean isZero() {
        for (int i = 0; i < this.count; ++i) {
            if (this.digits[i] != '0') {
                return false;
            }
        }
        return true;
    }
    
    void setRoundingMode(final RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }
    
    public void clear() {
        this.decimalAt = 0;
        this.count = 0;
    }
    
    public void append(final char c) {
        if (this.count == this.digits.length) {
            final char[] digits = new char[this.count + 100];
            System.arraycopy(this.digits, 0, digits, 0, this.count);
            this.digits = digits;
        }
        this.digits[this.count++] = c;
    }
    
    public final double getDouble() {
        if (this.count == 0) {
            return 0.0;
        }
        final StringBuffer stringBuffer = this.getStringBuffer();
        stringBuffer.append('.');
        stringBuffer.append(this.digits, 0, this.count);
        stringBuffer.append('E');
        stringBuffer.append(this.decimalAt);
        return Double.parseDouble(stringBuffer.toString());
    }
    
    public final long getLong() {
        if (this.count == 0) {
            return 0L;
        }
        if (this.isLongMIN_VALUE()) {
            return Long.MIN_VALUE;
        }
        final StringBuffer stringBuffer = this.getStringBuffer();
        stringBuffer.append(this.digits, 0, this.count);
        for (int i = this.count; i < this.decimalAt; ++i) {
            stringBuffer.append('0');
        }
        return Long.parseLong(stringBuffer.toString());
    }
    
    public final BigDecimal getBigDecimal() {
        if (this.count == 0) {
            if (this.decimalAt == 0) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal("0E" + this.decimalAt);
        }
        else {
            if (this.decimalAt == this.count) {
                return new BigDecimal(this.digits, 0, this.count);
            }
            return new BigDecimal(this.digits, 0, this.count).scaleByPowerOfTen(this.decimalAt - this.count);
        }
    }
    
    boolean fitsIntoLong(final boolean b, final boolean b2) {
        while (this.count > 0 && this.digits[this.count - 1] == '0') {
            --this.count;
        }
        if (this.count == 0) {
            return b || b2;
        }
        if (this.decimalAt < this.count || this.decimalAt > 19) {
            return false;
        }
        if (this.decimalAt < 19) {
            return true;
        }
        for (int i = 0; i < this.count; ++i) {
            final char c = this.digits[i];
            final char c2 = DigitList.LONG_MIN_REP[i];
            if (c > c2) {
                return false;
            }
            if (c < c2) {
                return true;
            }
        }
        return this.count < this.decimalAt || !b;
    }
    
    final void set(final boolean b, final double n, final int n2) {
        this.set(b, n, n2, true);
    }
    
    final void set(final boolean b, final double n, final int n2, final boolean b2) {
        final FloatingDecimal.BinaryToASCIIConverter binaryToASCIIConverter = FloatingDecimal.getBinaryToASCIIConverter(n);
        final boolean digitsRoundedUp = binaryToASCIIConverter.digitsRoundedUp();
        final boolean decimalDigitsExact = binaryToASCIIConverter.decimalDigitsExact();
        assert !binaryToASCIIConverter.isExceptional();
        this.set(b, binaryToASCIIConverter.toJavaFormatString(), digitsRoundedUp, decimalDigitsExact, n2, b2);
    }
    
    private void set(final boolean isNegative, final String s, final boolean b, final boolean b2, final int n, final boolean b3) {
        this.isNegative = isNegative;
        final int length = s.length();
        final char[] dataChars = this.getDataChars(length);
        s.getChars(0, length, dataChars, 0);
        this.decimalAt = -1;
        this.count = 0;
        int int1 = 0;
        int n2 = 0;
        boolean b4 = false;
        int i = 0;
        while (i < length) {
            final char c = dataChars[i++];
            if (c == '.') {
                this.decimalAt = this.count;
            }
            else {
                if (c == 'e' || c == 'E') {
                    int1 = parseInt(dataChars, i, length);
                    break;
                }
                if (!b4) {
                    b4 = (c != '0');
                    if (!b4 && this.decimalAt != -1) {
                        ++n2;
                    }
                }
                if (!b4) {
                    continue;
                }
                this.digits[this.count++] = c;
            }
        }
        if (this.decimalAt == -1) {
            this.decimalAt = this.count;
        }
        if (b4) {
            this.decimalAt += int1 - n2;
        }
        if (b3) {
            if (-this.decimalAt > n) {
                this.count = 0;
                return;
            }
            if (-this.decimalAt == n) {
                if (this.shouldRoundUp(0, b, b2)) {
                    this.count = 1;
                    ++this.decimalAt;
                    this.digits[0] = '1';
                }
                else {
                    this.count = 0;
                }
                return;
            }
        }
        while (this.count > 1 && this.digits[this.count - 1] == '0') {
            --this.count;
        }
        this.round(b3 ? (n + this.decimalAt) : n, b, b2);
    }
    
    private final void round(int count, final boolean b, final boolean b2) {
        if (count >= 0 && count < this.count) {
            Label_0080: {
                if (this.shouldRoundUp(count, b, b2)) {
                    while (true) {
                        while (--count >= 0) {
                            final char[] digits = this.digits;
                            final int n = count;
                            ++digits[n];
                            if (this.digits[count] <= '9') {
                                ++count;
                                break Label_0080;
                            }
                        }
                        this.digits[0] = '1';
                        ++this.decimalAt;
                        count = 0;
                        continue;
                    }
                }
            }
            this.count = count;
            while (this.count > 1 && this.digits[this.count - 1] == '0') {
                --this.count;
            }
        }
    }
    
    private boolean shouldRoundUp(final int n, final boolean b, final boolean b2) {
        if (n < this.count) {
            switch (this.roundingMode) {
                case UP: {
                    for (int i = n; i < this.count; ++i) {
                        if (this.digits[i] != '0') {
                            return true;
                        }
                    }
                    break;
                }
                case DOWN: {
                    break;
                }
                case CEILING: {
                    for (int j = n; j < this.count; ++j) {
                        if (this.digits[j] != '0') {
                            return !this.isNegative;
                        }
                    }
                    break;
                }
                case FLOOR: {
                    for (int k = n; k < this.count; ++k) {
                        if (this.digits[k] != '0') {
                            return this.isNegative;
                        }
                    }
                    break;
                }
                case HALF_UP:
                case HALF_DOWN: {
                    if (this.digits[n] > '5') {
                        return true;
                    }
                    if (this.digits[n] != '5') {
                        break;
                    }
                    if (n != this.count - 1) {
                        return true;
                    }
                    if (b2) {
                        return this.roundingMode == RoundingMode.HALF_UP;
                    }
                    return !b;
                }
                case HALF_EVEN: {
                    if (this.digits[n] > '5') {
                        return true;
                    }
                    if (this.digits[n] != '5') {
                        break;
                    }
                    if (n == this.count - 1) {
                        return !b && (!b2 || (n > 0 && this.digits[n - 1] % '\u0002' != 0));
                    }
                    for (int l = n + 1; l < this.count; ++l) {
                        if (this.digits[l] != '0') {
                            return true;
                        }
                    }
                    break;
                }
                case UNNECESSARY: {
                    for (int n2 = n; n2 < this.count; ++n2) {
                        if (this.digits[n2] != '0') {
                            throw new ArithmeticException("Rounding needed with the rounding mode being set to RoundingMode.UNNECESSARY");
                        }
                    }
                    break;
                }
                default: {
                    assert false;
                    break;
                }
            }
        }
        return false;
    }
    
    final void set(final boolean b, final long n) {
        this.set(b, n, 0);
    }
    
    final void set(final boolean isNegative, long n, final int n2) {
        this.isNegative = isNegative;
        if (n <= 0L) {
            if (n == Long.MIN_VALUE) {
                final int n3 = 19;
                this.count = n3;
                this.decimalAt = n3;
                System.arraycopy(DigitList.LONG_MIN_REP, 0, this.digits, 0, this.count);
            }
            else {
                final int n4 = 0;
                this.count = n4;
                this.decimalAt = n4;
            }
        }
        else {
            int n5 = 19;
            while (n > 0L) {
                this.digits[--n5] = (char)(48L + n % 10L);
                n /= 10L;
            }
            this.decimalAt = 19 - n5;
            int n6;
            for (n6 = 18; this.digits[n6] == '0'; --n6) {}
            this.count = n6 - n5 + 1;
            System.arraycopy(this.digits, n5, this.digits, 0, this.count);
        }
        if (n2 > 0) {
            this.round(n2, false, true);
        }
    }
    
    final void set(final boolean b, final BigDecimal bigDecimal, final int n, final boolean b2) {
        final String string = bigDecimal.toString();
        this.extendDigits(string.length());
        this.set(b, string, false, true, n, b2);
    }
    
    final void set(final boolean isNegative, final BigInteger bigInteger, final int n) {
        this.isNegative = isNegative;
        final String string = bigInteger.toString();
        final int length = string.length();
        this.extendDigits(length);
        string.getChars(0, length, this.digits, 0);
        this.decimalAt = length;
        int n2;
        for (n2 = length - 1; n2 >= 0 && this.digits[n2] == '0'; --n2) {}
        this.count = n2 + 1;
        if (n > 0) {
            this.round(n, false, true);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DigitList)) {
            return false;
        }
        final DigitList list = (DigitList)o;
        if (this.count != list.count || this.decimalAt != list.decimalAt) {
            return false;
        }
        for (int i = 0; i < this.count; ++i) {
            if (this.digits[i] != list.digits[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int decimalAt = this.decimalAt;
        for (int i = 0; i < this.count; ++i) {
            decimalAt = decimalAt * 37 + this.digits[i];
        }
        return decimalAt;
    }
    
    public Object clone() {
        try {
            final DigitList list = (DigitList)super.clone();
            final char[] digits = new char[this.digits.length];
            System.arraycopy(this.digits, 0, digits, 0, this.digits.length);
            list.digits = digits;
            list.tempBuffer = null;
            return list;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    private boolean isLongMIN_VALUE() {
        if (this.decimalAt != this.count || this.count != 19) {
            return false;
        }
        for (int i = 0; i < this.count; ++i) {
            if (this.digits[i] != DigitList.LONG_MIN_REP[i]) {
                return false;
            }
        }
        return true;
    }
    
    private static final int parseInt(final char[] array, int i, final int n) {
        boolean b = true;
        final char c;
        if ((c = array[i]) == '-') {
            b = false;
            ++i;
        }
        else if (c == '+') {
            ++i;
        }
        int n2 = 0;
        while (i < n) {
            final char c2 = array[i++];
            if (c2 < '0' || c2 > '9') {
                break;
            }
            n2 = n2 * 10 + (c2 - '0');
        }
        return b ? n2 : (-n2);
    }
    
    @Override
    public String toString() {
        if (this.isZero()) {
            return "0";
        }
        final StringBuffer stringBuffer = this.getStringBuffer();
        stringBuffer.append("0.");
        stringBuffer.append(this.digits, 0, this.count);
        stringBuffer.append("x10^");
        stringBuffer.append(this.decimalAt);
        return stringBuffer.toString();
    }
    
    private StringBuffer getStringBuffer() {
        if (this.tempBuffer == null) {
            this.tempBuffer = new StringBuffer(19);
        }
        else {
            this.tempBuffer.setLength(0);
        }
        return this.tempBuffer;
    }
    
    private void extendDigits(final int n) {
        if (n > this.digits.length) {
            this.digits = new char[n];
        }
    }
    
    private final char[] getDataChars(final int n) {
        if (this.data == null || this.data.length < n) {
            this.data = new char[n];
        }
        return this.data;
    }
    
    static {
        LONG_MIN_REP = "9223372036854775808".toCharArray();
    }
}
