package com.lowagie.text.factories;

public class RomanNumberFactory
{
    private static final RomanDigit[] roman;
    
    public static final String getString(int index) {
        final StringBuffer buf = new StringBuffer();
        if (index < 0) {
            buf.append('-');
            index = -index;
        }
        if (index > 3000) {
            buf.append('|');
            buf.append(getString(index / 1000));
            buf.append('|');
            index -= index / 1000 * 1000;
        }
        int pos = 0;
        while (true) {
            RomanDigit dig;
            for (dig = RomanNumberFactory.roman[pos]; index >= dig.value; index -= dig.value) {
                buf.append(dig.digit);
            }
            if (index <= 0) {
                break;
            }
            int j = pos;
            while (!RomanNumberFactory.roman[++j].pre) {}
            if (index + RomanNumberFactory.roman[j].value >= dig.value) {
                buf.append(RomanNumberFactory.roman[j].digit).append(dig.digit);
                index -= dig.value - RomanNumberFactory.roman[j].value;
            }
            ++pos;
        }
        return buf.toString();
    }
    
    public static final String getLowerCaseString(final int index) {
        return getString(index);
    }
    
    public static final String getUpperCaseString(final int index) {
        return getString(index).toUpperCase();
    }
    
    public static final String getString(final int index, final boolean lowercase) {
        if (lowercase) {
            return getLowerCaseString(index);
        }
        return getUpperCaseString(index);
    }
    
    public static void main(final String[] args) {
        for (int i = 1; i < 2000; ++i) {
            System.out.println(getString(i));
        }
    }
    
    static {
        roman = new RomanDigit[] { new RomanDigit('m', 1000, false), new RomanDigit('d', 500, false), new RomanDigit('c', 100, true), new RomanDigit('l', 50, false), new RomanDigit('x', 10, true), new RomanDigit('v', 5, false), new RomanDigit('i', 1, true) };
    }
    
    private static class RomanDigit
    {
        public char digit;
        public int value;
        public boolean pre;
        
        RomanDigit(final char digit, final int value, final boolean pre) {
            this.digit = digit;
            this.value = value;
            this.pre = pre;
        }
    }
}
