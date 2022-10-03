package sun.misc;

import java.util.Arrays;

public class FormattedFloatingDecimal
{
    private int decExponentRounded;
    private char[] mantissa;
    private char[] exponent;
    private static final ThreadLocal<Object> threadLocalCharBuffer;
    
    public static FormattedFloatingDecimal valueOf(final double n, final int n2, final Form form) {
        return new FormattedFloatingDecimal(n2, form, FloatingDecimal.getBinaryToASCIIConverter(n, form == Form.COMPATIBLE));
    }
    
    private static char[] getBuffer() {
        return FormattedFloatingDecimal.threadLocalCharBuffer.get();
    }
    
    private FormattedFloatingDecimal(int n, final Form form, final FloatingDecimal.BinaryToASCIIConverter binaryToASCIIConverter) {
        if (binaryToASCIIConverter.isExceptional()) {
            this.mantissa = binaryToASCIIConverter.toJavaFormatString().toCharArray();
            this.exponent = null;
            return;
        }
        final char[] buffer = getBuffer();
        final int digits = binaryToASCIIConverter.getDigits(buffer);
        final int decimalExponent = binaryToASCIIConverter.getDecimalExponent();
        final boolean negative = binaryToASCIIConverter.isNegative();
        switch (form) {
            case COMPATIBLE: {
                this.fillCompatible(n, buffer, digits, this.decExponentRounded = decimalExponent, negative);
                break;
            }
            case DECIMAL_FLOAT: {
                final int applyPrecision = applyPrecision(decimalExponent, buffer, digits, decimalExponent + n);
                this.fillDecimal(n, buffer, digits, applyPrecision, negative);
                this.decExponentRounded = applyPrecision;
                break;
            }
            case SCIENTIFIC: {
                final int applyPrecision2 = applyPrecision(decimalExponent, buffer, digits, n + 1);
                this.fillScientific(n, buffer, digits, applyPrecision2, negative);
                this.decExponentRounded = applyPrecision2;
                break;
            }
            case GENERAL: {
                final int applyPrecision3 = applyPrecision(decimalExponent, buffer, digits, n);
                if (applyPrecision3 - 1 < -4 || applyPrecision3 - 1 >= n) {
                    --n;
                    this.fillScientific(n, buffer, digits, applyPrecision3, negative);
                }
                else {
                    n -= applyPrecision3;
                    this.fillDecimal(n, buffer, digits, applyPrecision3, negative);
                }
                this.decExponentRounded = applyPrecision3;
                break;
            }
            default: {
                assert false;
                break;
            }
        }
    }
    
    public int getExponentRounded() {
        return this.decExponentRounded - 1;
    }
    
    public char[] getMantissa() {
        return this.mantissa;
    }
    
    public char[] getExponent() {
        return this.exponent;
    }
    
    private static int applyPrecision(final int n, final char[] array, final int n2, final int n3) {
        if (n3 >= n2 || n3 < 0) {
            return n;
        }
        if (n3 != 0) {
            if (array[n3] >= '5') {
                int n4 = n3;
                char c = array[--n4];
                if (c == '9') {
                    while (c == '9' && n4 > 0) {
                        c = array[--n4];
                    }
                    if (c == '9') {
                        array[0] = '1';
                        Arrays.fill(array, 1, n2, '0');
                        return n + 1;
                    }
                }
                array[n4] = (char)(c + '\u0001');
                Arrays.fill(array, n4 + 1, n2, '0');
            }
            else {
                Arrays.fill(array, n3, n2, '0');
            }
            return n;
        }
        if (array[0] >= '5') {
            array[0] = '1';
            Arrays.fill(array, 1, n2, '0');
            return n + 1;
        }
        Arrays.fill(array, 0, n2, '0');
        return n;
    }
    
    private void fillCompatible(final int n, final char[] array, final int n2, final int n3, final boolean b) {
        final int n4 = b ? 1 : 0;
        if (n3 > 0 && n3 < 8) {
            if (n2 < n3) {
                final int n5 = n3 - n2;
                System.arraycopy(array, 0, this.mantissa = create(b, n2 + n5 + 2), n4, n2);
                Arrays.fill(this.mantissa, n4 + n2, n4 + n2 + n5, '0');
                this.mantissa[n4 + n2 + n5] = '.';
                this.mantissa[n4 + n2 + n5 + 1] = '0';
            }
            else if (n3 < n2) {
                final int min = Math.min(n2 - n3, n);
                System.arraycopy(array, 0, this.mantissa = create(b, n3 + 1 + min), n4, n3);
                this.mantissa[n4 + n3] = '.';
                System.arraycopy(array, n3, this.mantissa, n4 + n3 + 1, min);
            }
            else {
                System.arraycopy(array, 0, this.mantissa = create(b, n2 + 2), n4, n2);
                this.mantissa[n4 + n2] = '.';
                this.mantissa[n4 + n2 + 1] = '0';
            }
        }
        else if (n3 <= 0 && n3 > -3) {
            final int max = Math.max(0, Math.min(-n3, n));
            final int max2 = Math.max(0, Math.min(n2, n + n3));
            if (max > 0) {
                (this.mantissa = create(b, max + 2 + max2))[n4] = '0';
                this.mantissa[n4 + 1] = '.';
                Arrays.fill(this.mantissa, n4 + 2, n4 + 2 + max, '0');
                if (max2 > 0) {
                    System.arraycopy(array, 0, this.mantissa, n4 + 2 + max, max2);
                }
            }
            else if (max2 > 0) {
                (this.mantissa = create(b, max + 2 + max2))[n4] = '0';
                this.mantissa[n4 + 1] = '.';
                System.arraycopy(array, 0, this.mantissa, n4 + 2, max2);
            }
            else {
                (this.mantissa = create(b, 1))[n4] = '0';
            }
        }
        else {
            if (n2 > 1) {
                (this.mantissa = create(b, n2 + 1))[n4] = array[0];
                this.mantissa[n4 + 1] = '.';
                System.arraycopy(array, 1, this.mantissa, n4 + 2, n2 - 1);
            }
            else {
                (this.mantissa = create(b, 3))[n4] = array[0];
                this.mantissa[n4 + 1] = '.';
                this.mantissa[n4 + 2] = '0';
            }
            final boolean b2 = n3 <= 0;
            int n6;
            int n7;
            if (b2) {
                n6 = -n3 + 1;
                n7 = 1;
            }
            else {
                n6 = n3 - 1;
                n7 = 0;
            }
            if (n6 <= 9) {
                (this.exponent = create(b2, 1))[n7] = (char)(n6 + 48);
            }
            else if (n6 <= 99) {
                (this.exponent = create(b2, 2))[n7] = (char)(n6 / 10 + 48);
                this.exponent[n7 + 1] = (char)(n6 % 10 + 48);
            }
            else {
                (this.exponent = create(b2, 3))[n7] = (char)(n6 / 100 + 48);
                final int n8 = n6 % 100;
                this.exponent[n7 + 1] = (char)(n8 / 10 + 48);
                this.exponent[n7 + 2] = (char)(n8 % 10 + 48);
            }
        }
    }
    
    private static char[] create(final boolean b, final int n) {
        if (b) {
            final char[] array = new char[n + 1];
            array[0] = '-';
            return array;
        }
        return new char[n];
    }
    
    private void fillDecimal(final int n, final char[] array, final int n2, final int n3, final boolean b) {
        final int n4 = b ? 1 : 0;
        if (n3 > 0) {
            if (n2 < n3) {
                System.arraycopy(array, 0, this.mantissa = create(b, n3), n4, n2);
                Arrays.fill(this.mantissa, n4 + n2, n4 + n3, '0');
            }
            else {
                final int min = Math.min(n2 - n3, n);
                System.arraycopy(array, 0, this.mantissa = create(b, n3 + ((min > 0) ? (min + 1) : 0)), n4, n3);
                if (min > 0) {
                    this.mantissa[n4 + n3] = '.';
                    System.arraycopy(array, n3, this.mantissa, n4 + n3 + 1, min);
                }
            }
        }
        else if (n3 <= 0) {
            final int max = Math.max(0, Math.min(-n3, n));
            final int max2 = Math.max(0, Math.min(n2, n + n3));
            if (max > 0) {
                (this.mantissa = create(b, max + 2 + max2))[n4] = '0';
                this.mantissa[n4 + 1] = '.';
                Arrays.fill(this.mantissa, n4 + 2, n4 + 2 + max, '0');
                if (max2 > 0) {
                    System.arraycopy(array, 0, this.mantissa, n4 + 2 + max, max2);
                }
            }
            else if (max2 > 0) {
                (this.mantissa = create(b, max + 2 + max2))[n4] = '0';
                this.mantissa[n4 + 1] = '.';
                System.arraycopy(array, 0, this.mantissa, n4 + 2, max2);
            }
            else {
                (this.mantissa = create(b, 1))[n4] = '0';
            }
        }
    }
    
    private void fillScientific(final int n, final char[] array, final int n2, final int n3, final boolean b) {
        final int n4 = b ? 1 : 0;
        final int max = Math.max(0, Math.min(n2 - 1, n));
        if (max > 0) {
            (this.mantissa = create(b, max + 2))[n4] = array[0];
            this.mantissa[n4 + 1] = '.';
            System.arraycopy(array, 1, this.mantissa, n4 + 2, max);
        }
        else {
            (this.mantissa = create(b, 1))[n4] = array[0];
        }
        char c;
        int n5;
        if (n3 <= 0) {
            c = '-';
            n5 = -n3 + 1;
        }
        else {
            c = '+';
            n5 = n3 - 1;
        }
        if (n5 <= 9) {
            this.exponent = new char[] { c, '0', (char)(n5 + 48) };
        }
        else if (n5 <= 99) {
            this.exponent = new char[] { c, (char)(n5 / 10 + 48), (char)(n5 % 10 + 48) };
        }
        else {
            final char c2 = (char)(n5 / 100 + 48);
            final int n6 = n5 % 100;
            this.exponent = new char[] { c, c2, (char)(n6 / 10 + 48), (char)(n6 % 10 + 48) };
        }
    }
    
    static {
        threadLocalCharBuffer = new ThreadLocal<Object>() {
            @Override
            protected Object initialValue() {
                return new char[20];
            }
        };
    }
    
    public enum Form
    {
        SCIENTIFIC, 
        COMPATIBLE, 
        DECIMAL_FLOAT, 
        GENERAL;
    }
}
