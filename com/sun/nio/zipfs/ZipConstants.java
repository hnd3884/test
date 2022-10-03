package com.sun.nio.zipfs;

class ZipConstants
{
    static final int METHOD_STORED = 0;
    static final int METHOD_DEFLATED = 8;
    static final int METHOD_DEFLATED64 = 9;
    static final int METHOD_BZIP2 = 12;
    static final int METHOD_LZMA = 14;
    static final int METHOD_LZ77 = 19;
    static final int METHOD_AES = 99;
    static final int FLAG_ENCRYPTED = 1;
    static final int FLAG_DATADESCR = 8;
    static final int FLAG_EFS = 2048;
    static long LOCSIG;
    static long EXTSIG;
    static long CENSIG;
    static long ENDSIG;
    static final int LOCHDR = 30;
    static final int EXTHDR = 16;
    static final int CENHDR = 46;
    static final int ENDHDR = 22;
    static final int LOCVER = 4;
    static final int LOCFLG = 6;
    static final int LOCHOW = 8;
    static final int LOCTIM = 10;
    static final int LOCCRC = 14;
    static final int LOCSIZ = 18;
    static final int LOCLEN = 22;
    static final int LOCNAM = 26;
    static final int LOCEXT = 28;
    static final int EXTCRC = 4;
    static final int EXTSIZ = 8;
    static final int EXTLEN = 12;
    static final int CENVEM = 4;
    static final int CENVER = 6;
    static final int CENFLG = 8;
    static final int CENHOW = 10;
    static final int CENTIM = 12;
    static final int CENCRC = 16;
    static final int CENSIZ = 20;
    static final int CENLEN = 24;
    static final int CENNAM = 28;
    static final int CENEXT = 30;
    static final int CENCOM = 32;
    static final int CENDSK = 34;
    static final int CENATT = 36;
    static final int CENATX = 38;
    static final int CENOFF = 42;
    static final int ENDSUB = 8;
    static final int ENDTOT = 10;
    static final int ENDSIZ = 12;
    static final int ENDOFF = 16;
    static final int ENDCOM = 20;
    static final long ZIP64_ENDSIG = 101075792L;
    static final long ZIP64_LOCSIG = 117853008L;
    static final int ZIP64_ENDHDR = 56;
    static final int ZIP64_LOCHDR = 20;
    static final int ZIP64_EXTHDR = 24;
    static final int ZIP64_EXTID = 1;
    static final int ZIP64_MINVAL32 = 65535;
    static final long ZIP64_MINVAL = 4294967295L;
    static final int ZIP64_ENDLEN = 4;
    static final int ZIP64_ENDVEM = 12;
    static final int ZIP64_ENDVER = 14;
    static final int ZIP64_ENDNMD = 16;
    static final int ZIP64_ENDDSK = 20;
    static final int ZIP64_ENDTOD = 24;
    static final int ZIP64_ENDTOT = 32;
    static final int ZIP64_ENDSIZ = 40;
    static final int ZIP64_ENDOFF = 48;
    static final int ZIP64_ENDEXT = 56;
    static final int ZIP64_LOCDSK = 4;
    static final int ZIP64_LOCOFF = 8;
    static final int ZIP64_LOCTOT = 16;
    static final int ZIP64_EXTCRC = 4;
    static final int ZIP64_EXTSIZ = 8;
    static final int ZIP64_EXTLEN = 16;
    static final int EXTID_ZIP64 = 1;
    static final int EXTID_NTFS = 10;
    static final int EXTID_UNIX = 13;
    static final int EXTID_EFS = 23;
    static final int EXTID_EXTT = 21589;
    static final long END_MAXLEN = 65557L;
    static final int READBLOCKSZ = 128;
    
    static final int CH(final byte[] array, final int n) {
        return Byte.toUnsignedInt(array[n]);
    }
    
    static final int SH(final byte[] array, final int n) {
        return Byte.toUnsignedInt(array[n]) | Byte.toUnsignedInt(array[n + 1]) << 8;
    }
    
    static final long LG(final byte[] array, final int n) {
        return (long)(SH(array, n) | SH(array, n + 2) << 16) & 0xFFFFFFFFL;
    }
    
    static final long LL(final byte[] array, final int n) {
        return LG(array, n) | LG(array, n + 4) << 32;
    }
    
    static long getSig(final byte[] array, final int n) {
        return LG(array, n);
    }
    
    private static boolean pkSigAt(final byte[] array, final int n, final int n2, final int n3) {
        return array[n] == 80 & array[n + 1] == 75 & array[n + 2] == n2 & array[n + 3] == n3;
    }
    
    static boolean cenSigAt(final byte[] array, final int n) {
        return pkSigAt(array, n, 1, 2);
    }
    
    static boolean locSigAt(final byte[] array, final int n) {
        return pkSigAt(array, n, 3, 4);
    }
    
    static boolean endSigAt(final byte[] array, final int n) {
        return pkSigAt(array, n, 5, 6);
    }
    
    static boolean extSigAt(final byte[] array, final int n) {
        return pkSigAt(array, n, 7, 8);
    }
    
    static boolean end64SigAt(final byte[] array, final int n) {
        return pkSigAt(array, n, 6, 6);
    }
    
    static boolean locator64SigAt(final byte[] array, final int n) {
        return pkSigAt(array, n, 6, 7);
    }
    
    static final long LOCSIG(final byte[] array) {
        return LG(array, 0);
    }
    
    static final int LOCVER(final byte[] array) {
        return SH(array, 4);
    }
    
    static final int LOCFLG(final byte[] array) {
        return SH(array, 6);
    }
    
    static final int LOCHOW(final byte[] array) {
        return SH(array, 8);
    }
    
    static final long LOCTIM(final byte[] array) {
        return LG(array, 10);
    }
    
    static final long LOCCRC(final byte[] array) {
        return LG(array, 14);
    }
    
    static final long LOCSIZ(final byte[] array) {
        return LG(array, 18);
    }
    
    static final long LOCLEN(final byte[] array) {
        return LG(array, 22);
    }
    
    static final int LOCNAM(final byte[] array) {
        return SH(array, 26);
    }
    
    static final int LOCEXT(final byte[] array) {
        return SH(array, 28);
    }
    
    static final long EXTCRC(final byte[] array) {
        return LG(array, 4);
    }
    
    static final long EXTSIZ(final byte[] array) {
        return LG(array, 8);
    }
    
    static final long EXTLEN(final byte[] array) {
        return LG(array, 12);
    }
    
    static final int ENDSUB(final byte[] array) {
        return SH(array, 8);
    }
    
    static final int ENDTOT(final byte[] array) {
        return SH(array, 10);
    }
    
    static final long ENDSIZ(final byte[] array) {
        return LG(array, 12);
    }
    
    static final long ENDOFF(final byte[] array) {
        return LG(array, 16);
    }
    
    static final int ENDCOM(final byte[] array) {
        return SH(array, 20);
    }
    
    static final int ENDCOM(final byte[] array, final int n) {
        return SH(array, n + 20);
    }
    
    static final long ZIP64_ENDTOD(final byte[] array) {
        return LL(array, 24);
    }
    
    static final long ZIP64_ENDTOT(final byte[] array) {
        return LL(array, 32);
    }
    
    static final long ZIP64_ENDSIZ(final byte[] array) {
        return LL(array, 40);
    }
    
    static final long ZIP64_ENDOFF(final byte[] array) {
        return LL(array, 48);
    }
    
    static final long ZIP64_LOCOFF(final byte[] array) {
        return LL(array, 8);
    }
    
    static final long CENSIG(final byte[] array, final int n) {
        return LG(array, n + 0);
    }
    
    static final int CENVEM(final byte[] array, final int n) {
        return SH(array, n + 4);
    }
    
    static final int CENVER(final byte[] array, final int n) {
        return SH(array, n + 6);
    }
    
    static final int CENFLG(final byte[] array, final int n) {
        return SH(array, n + 8);
    }
    
    static final int CENHOW(final byte[] array, final int n) {
        return SH(array, n + 10);
    }
    
    static final long CENTIM(final byte[] array, final int n) {
        return LG(array, n + 12);
    }
    
    static final long CENCRC(final byte[] array, final int n) {
        return LG(array, n + 16);
    }
    
    static final long CENSIZ(final byte[] array, final int n) {
        return LG(array, n + 20);
    }
    
    static final long CENLEN(final byte[] array, final int n) {
        return LG(array, n + 24);
    }
    
    static final int CENNAM(final byte[] array, final int n) {
        return SH(array, n + 28);
    }
    
    static final int CENEXT(final byte[] array, final int n) {
        return SH(array, n + 30);
    }
    
    static final int CENCOM(final byte[] array, final int n) {
        return SH(array, n + 32);
    }
    
    static final int CENDSK(final byte[] array, final int n) {
        return SH(array, n + 34);
    }
    
    static final int CENATT(final byte[] array, final int n) {
        return SH(array, n + 36);
    }
    
    static final long CENATX(final byte[] array, final int n) {
        return LG(array, n + 38);
    }
    
    static final long CENOFF(final byte[] array, final int n) {
        return LG(array, n + 42);
    }
    
    static {
        ZipConstants.LOCSIG = 67324752L;
        ZipConstants.EXTSIG = 134695760L;
        ZipConstants.CENSIG = 33639248L;
        ZipConstants.ENDSIG = 101010256L;
    }
}
