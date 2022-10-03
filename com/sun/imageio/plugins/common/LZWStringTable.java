package com.sun.imageio.plugins.common;

import java.io.PrintStream;

public class LZWStringTable
{
    private static final int RES_CODES = 2;
    private static final short HASH_FREE = -1;
    private static final short NEXT_FIRST = -1;
    private static final int MAXBITS = 12;
    private static final int MAXSTR = 4096;
    private static final short HASHSIZE = 9973;
    private static final short HASHSTEP = 2039;
    byte[] strChr;
    short[] strNxt;
    short[] strHsh;
    short numStrings;
    int[] strLen;
    
    public LZWStringTable() {
        this.strChr = new byte[4096];
        this.strNxt = new short[4096];
        this.strLen = new int[4096];
        this.strHsh = new short[9973];
    }
    
    public int addCharString(final short n, final byte b) {
        if (this.numStrings >= 4096) {
            return 65535;
        }
        int hash;
        for (hash = hash(n, b); this.strHsh[hash] != -1; hash = (hash + 2039) % 9973) {}
        this.strHsh[hash] = this.numStrings;
        this.strChr[this.numStrings] = b;
        if (n == -1) {
            this.strNxt[this.numStrings] = -1;
            this.strLen[this.numStrings] = 1;
        }
        else {
            this.strNxt[this.numStrings] = n;
            this.strLen[this.numStrings] = this.strLen[n] + 1;
        }
        final short numStrings = this.numStrings;
        this.numStrings = (short)(numStrings + 1);
        return numStrings;
    }
    
    public short findCharString(final short n, final byte b) {
        if (n == -1) {
            return (short)(b & 0xFF);
        }
        short n2;
        for (int hash = hash(n, b); (n2 = this.strHsh[hash]) != -1; hash = (hash + 2039) % 9973) {
            if (this.strNxt[n2] == n && this.strChr[n2] == b) {
                return n2;
            }
        }
        return -1;
    }
    
    public void clearTable(final int n) {
        this.numStrings = 0;
        for (int i = 0; i < 9973; ++i) {
            this.strHsh[i] = -1;
        }
        for (int n2 = (1 << n) + 2, j = 0; j < n2; ++j) {
            this.addCharString((short)(-1), (byte)j);
        }
    }
    
    public static int hash(final short n, final byte b) {
        return (((short)(b << 8) ^ n) & 0xFFFF) % 9973;
    }
    
    public int expandCode(final byte[] array, final int n, short n2, int n3) {
        if (n == -2 && n3 == 1) {
            n3 = 0;
        }
        if (n2 == -1 || n3 == this.strLen[n2]) {
            return 0;
        }
        final int n4 = this.strLen[n2] - n3;
        final int n5 = array.length - n;
        int n6;
        if (n5 > n4) {
            n6 = n4;
        }
        else {
            n6 = n5;
        }
        int n7 = n4 - n6;
        for (int n8 = n + n6; n8 > n && n2 != -1; n2 = this.strNxt[n2]) {
            if (--n7 < 0) {
                array[--n8] = this.strChr[n2];
            }
        }
        if (n4 > n6) {
            return -n6;
        }
        return n6;
    }
    
    public void dump(final PrintStream printStream) {
        for (short n = 258; n < this.numStrings; ++n) {
            printStream.println(" strNxt[" + n + "] = " + this.strNxt[n] + " strChr " + Integer.toHexString(this.strChr[n] & 0xFF) + " strLen " + Integer.toHexString(this.strLen[n]));
        }
    }
}
