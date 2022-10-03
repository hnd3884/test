package com.lowagie.text.factories;

import com.lowagie.text.error_messages.MessageLocalization;

public class RomanAlphabetFactory
{
    public static final String getString(int index) {
        if (index < 1) {
            throw new NumberFormatException(MessageLocalization.getComposedMessage("you.can.t.translate.a.negative.number.into.an.alphabetical.value"));
        }
        --index;
        int bytes = 1;
        int start = 0;
        for (int symbols = 26; index >= symbols + start; start += symbols, symbols *= 26) {
            ++bytes;
        }
        int c;
        char[] value;
        for (c = index - start, value = new char[bytes]; bytes > 0; value[--bytes] = (char)(97 + c % 26), c /= 26) {}
        return new String(value);
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
        for (int i = 1; i < 32000; ++i) {
            System.out.println(getString(i));
        }
    }
}
