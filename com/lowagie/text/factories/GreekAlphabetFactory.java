package com.lowagie.text.factories;

import com.lowagie.text.SpecialSymbol;

public class GreekAlphabetFactory
{
    public static final String getString(final int index) {
        return getString(index, true);
    }
    
    public static final String getLowerCaseString(final int index) {
        return getString(index);
    }
    
    public static final String getUpperCaseString(final int index) {
        return getString(index).toUpperCase();
    }
    
    public static final String getString(int index, final boolean lowercase) {
        if (index < 1) {
            return "";
        }
        --index;
        int bytes = 1;
        int start = 0;
        for (int symbols = 24; index >= symbols + start; start += symbols, symbols *= 24) {
            ++bytes;
        }
        int c = index - start;
        final char[] value = new char[bytes];
        while (bytes > 0) {
            --bytes;
            value[bytes] = (char)(c % 24);
            if (value[bytes] > '\u0010') {
                final char[] array = value;
                final int n = bytes;
                ++array[n];
            }
            final char[] array2 = value;
            final int n2 = bytes;
            array2[n2] += (lowercase ? '\u03b1' : '\u0391');
            value[bytes] = SpecialSymbol.getCorrespondingSymbol(value[bytes]);
            c /= 24;
        }
        return String.valueOf(value);
    }
    
    public static void main(final String[] args) {
        for (int i = 1; i < 1000; ++i) {
            System.out.println(getString(i));
        }
    }
}
