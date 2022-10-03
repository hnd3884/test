package com.sun.xml.internal.fastinfoset.alphabet;

public final class BuiltInRestrictedAlphabets
{
    public static final char[][] table;
    
    static {
        (table = new char[2][])[0] = "0123456789-+.E ".toCharArray();
        BuiltInRestrictedAlphabets.table[1] = "0123456789-:TZ ".toCharArray();
    }
}
