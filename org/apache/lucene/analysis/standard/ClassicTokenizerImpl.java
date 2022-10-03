package org.apache.lucene.analysis.standard;

import java.io.IOException;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.io.Reader;

class ClassicTokenizerImpl
{
    public static final int YYEOF = -1;
    private static final int ZZ_BUFFERSIZE = 4096;
    public static final int YYINITIAL = 0;
    private static final int[] ZZ_LEXSTATE;
    private static final String ZZ_CMAP_PACKED = "&\u0000\u0001\u0005\u0001\u0003\u0004\u0000\u0001\t\u0001\u0007\u0001\u0004\u0001\t\n\u0002\u0006\u0000\u0001\u0006\u001a\n\u0004\u0000\u0001\b\u0001\u0000\u001a\n/\u0000\u0001\n\n\u0000\u0001\n\u0004\u0000\u0001\n\u0005\u0000\u0017\n\u0001\u0000\u001f\n\u0001\u0000\u0128\n\u0002\u0000\u0012\n\u001c\u0000^\n\u0002\u0000\t\n\u0002\u0000\u0007\n\u000e\u0000\u0002\n\u000e\u0000\u0005\n\t\u0000\u0001\n\u008b\u0000\u0001\n\u000b\u0000\u0001\n\u0001\u0000\u0003\n\u0001\u0000\u0001\n\u0001\u0000\u0014\n\u0001\u0000,\n\u0001\u0000\b\n\u0002\u0000\u001a\n\f\u0000\u0082\n\n\u00009\n\u0002\u0000\u0002\n\u0002\u0000\u0002\n\u0003\u0000&\n\u0002\u0000\u0002\n7\u0000&\n\u0002\u0000\u0001\n\u0007\u0000'\nH\u0000\u001b\n\u0005\u0000\u0003\n.\u0000\u001a\n\u0005\u0000\u000b\n\u0015\u0000\n\u0002\u0007\u0000c\n\u0001\u0000\u0001\n\u000f\u0000\u0002\n\t\u0000\n\u0002\u0003\n\u0013\u0000\u0001\n\u0001\u0000\u001b\nS\u0000&\n\u015f\u00005\n\u0003\u0000\u0001\n\u0012\u0000\u0001\n\u0007\u0000\n\n\u0004\u0000\n\u0002\u0015\u0000\b\n\u0002\u0000\u0002\n\u0002\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0001\n\u0003\u0000\u0004\n\"\u0000\u0002\n\u0001\u0000\u0003\n\u0004\u0000\n\u0002\u0002\n\u0013\u0000\u0006\n\u0004\u0000\u0002\n\u0002\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0002\n\u0001\u0000\u0002\n\u0001\u0000\u0002\n\u001f\u0000\u0004\n\u0001\u0000\u0001\n\u0007\u0000\n\u0002\u0002\u0000\u0003\n\u0010\u0000\u0007\n\u0001\u0000\u0001\n\u0001\u0000\u0003\n\u0001\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0002\n\u0001\u0000\u0005\n\u0003\u0000\u0001\n\u0012\u0000\u0001\n\u000f\u0000\u0001\n\u0005\u0000\n\u0002\u0015\u0000\b\n\u0002\u0000\u0002\n\u0002\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0002\n\u0002\u0000\u0004\n\u0003\u0000\u0001\n\u001e\u0000\u0002\n\u0001\u0000\u0003\n\u0004\u0000\n\u0002\u0015\u0000\u0006\n\u0003\u0000\u0003\n\u0001\u0000\u0004\n\u0003\u0000\u0002\n\u0001\u0000\u0001\n\u0001\u0000\u0002\n\u0003\u0000\u0002\n\u0003\u0000\u0003\n\u0003\u0000\b\n\u0001\u0000\u0003\n-\u0000\t\u0002\u0015\u0000\b\n\u0001\u0000\u0003\n\u0001\u0000\u0017\n\u0001\u0000\n\n\u0001\u0000\u0005\n&\u0000\u0002\n\u0004\u0000\n\u0002\u0015\u0000\b\n\u0001\u0000\u0003\n\u0001\u0000\u0017\n\u0001\u0000\n\n\u0001\u0000\u0005\n$\u0000\u0001\n\u0001\u0000\u0002\n\u0004\u0000\n\u0002\u0015\u0000\b\n\u0001\u0000\u0003\n\u0001\u0000\u0017\n\u0001\u0000\u0010\n&\u0000\u0002\n\u0004\u0000\n\u0002\u0015\u0000\u0012\n\u0003\u0000\u0018\n\u0001\u0000\t\n\u0001\u0000\u0001\n\u0002\u0000\u0007\n9\u0000\u0001\u00010\n\u0001\u0001\u0002\n\f\u0001\u0007\n\t\u0001\n\u0002'\u0000\u0002\n\u0001\u0000\u0001\n\u0002\u0000\u0002\n\u0001\u0000\u0001\n\u0002\u0000\u0001\n\u0006\u0000\u0004\n\u0001\u0000\u0007\n\u0001\u0000\u0003\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0002\u0000\u0002\n\u0001\u0000\u0004\n\u0001\u0000\u0002\n\t\u0000\u0001\n\u0002\u0000\u0005\n\u0001\u0000\u0001\n\t\u0000\n\u0002\u0002\u0000\u0002\n\"\u0000\u0001\n\u001f\u0000\n\u0002\u0016\u0000\b\n\u0001\u0000\"\n\u001d\u0000\u0004\nt\u0000\"\n\u0001\u0000\u0005\n\u0001\u0000\u0002\n\u0015\u0000\n\u0002\u0006\u0000\u0006\nJ\u0000&\n\n\u0000'\n\t\u0000Z\n\u0005\u0000D\n\u0005\u0000R\n\u0006\u0000\u0007\n\u0001\u0000?\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000'\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u001f\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000\u0007\n\u0001\u0000\u0017\n\u0001\u0000\u001f\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000'\n\u0001\u0000\u0013\n\u000e\u0000\t\u0002.\u0000U\n\f\u0000\u026c\n\u0002\u0000\b\n\n\u0000\u001a\n\u0005\u0000K\n\u0095\u00004\n,\u0000\n\u0002&\u0000\n\u0002\u0006\u0000X\n\b\u0000)\n\u0557\u0000\u009c\n\u0004\u0000Z\n\u0006\u0000\u0016\n\u0002\u0000\u0006\n\u0002\u0000&\n\u0002\u0000\u0006\n\u0002\u0000\b\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u001f\n\u0002\u00005\n\u0001\u0000\u0007\n\u0001\u0000\u0001\n\u0003\u0000\u0003\n\u0001\u0000\u0007\n\u0003\u0000\u0004\n\u0002\u0000\u0006\n\u0004\u0000\r\n\u0005\u0000\u0003\n\u0001\u0000\u0007\n\u0082\u0000\u0001\n\u0082\u0000\u0001\n\u0004\u0000\u0001\n\u0002\u0000\n\n\u0001\u0000\u0001\n\u0003\u0000\u0005\n\u0006\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0001\u0000\u0003\n\u0001\u0000\u0007\n\u0ecb\u0000\u0002\n*\u0000\u0005\n\n\u0000\u0001\u000bT\u000b\b\u000b\u0002\u000b\u0002\u000bZ\u000b\u0001\u000b\u0003\u000b\u0006\u000b(\u000b\u0003\u000b\u0001\u0000^\n\u0011\u0000\u0018\n8\u0000\u0010\u000b\u0100\u0000\u0080\u000b\u0080\u0000\u19b6\u000b\n\u000b@\u0000\u51a6\u000bZ\u000b\u048d\n\u0773\u0000\u2ba4\n\u215c\u0000\u012e\u000b\u00d2\u000b\u0007\n\f\u0000\u0005\n\u0005\u0000\u0001\n\u0001\u0000\n\n\u0001\u0000\r\n\u0001\u0000\u0005\n\u0001\u0000\u0001\n\u0001\u0000\u0002\n\u0001\u0000\u0002\n\u0001\u0000l\n!\u0000\u016b\n\u0012\u0000@\n\u0002\u00006\n(\u0000\f\nt\u0000\u0003\n\u0001\u0000\u0001\n\u0001\u0000\u0087\n\u0013\u0000\n\u0002\u0007\u0000\u001a\n\u0006\u0000\u001a\n\n\u0000\u0001\u000b:\u000b\u001f\n\u0003\u0000\u0006\n\u0002\u0000\u0006\n\u0002\u0000\u0006\n\u0002\u0000\u0003\n#\u0000";
    private static final char[] ZZ_CMAP;
    private static final int[] ZZ_ACTION;
    private static final String ZZ_ACTION_PACKED_0 = "\u0001\u0000\u0001\u0001\u0003\u0002\u0001\u0003\u000b\u0000\u0001\u0002\u0003\u0004\u0002\u0000\u0001\u0005\u0001\u0000\u0001\u0005\u0003\u0004\u0006\u0005\u0001\u0006\u0001\u0004\u0002\u0007\u0001\b\u0001\u0000\u0001\b\u0003\u0000\u0002\b\u0001\t\u0001\n\u0001\u0004";
    private static final int[] ZZ_ROWMAP;
    private static final String ZZ_ROWMAP_PACKED_0 = "\u0000\u0000\u0000\f\u0000\u0018\u0000$\u00000\u0000\f\u0000<\u0000H\u0000T\u0000`\u0000l\u0000x\u0000\u0084\u0000\u0090\u0000\u009c\u0000¨\u0000´\u0000\u00c0\u0000\u00cc\u0000\u00d8\u0000\u00e4\u0000\u00f0\u0000\u00fc\u0000\u0108\u0000\u0114\u0000\u0120\u0000\u012c\u0000\u0138\u0000\u0144\u0000\u0150\u0000\u015c\u0000\u0168\u0000\u0174\u0000\u0180\u0000\u018c\u0000\u0198\u0000\u01a4\u0000¨\u0000\u01b0\u0000\u01bc\u0000\u01c8\u0000\u01d4\u0000\u01e0\u0000\u01ec\u0000\u01f8\u0000<\u0000l\u0000\u0204\u0000\u0210\u0000\u021c";
    private static final int[] ZZ_TRANS;
    private static final String ZZ_TRANS_PACKED_0 = "\u0001\u0002\u0001\u0003\u0001\u0004\u0007\u0002\u0001\u0005\u0001\u0006\r\u0000\u0002\u0003\u0001\u0000\u0001\u0007\u0001\u0000\u0001\b\u0002\t\u0001\n\u0001\u0003\u0002\u0000\u0001\u0003\u0001\u0004\u0001\u0000\u0001\u000b\u0001\u0000\u0001\b\u0002\f\u0001\r\u0001\u0004\u0002\u0000\u0001\u0003\u0001\u0004\u0001\u000e\u0001\u000f\u0001\u0010\u0001\u0011\u0002\t\u0001\n\u0001\u0012\u0002\u0000\u0001\u0013\u0001\u0014\u0007\u0000\u0001\u0015\u0002\u0000\u0002\u0016\u0007\u0000\u0001\u0016\u0002\u0000\u0001\u0017\u0001\u0018\u0007\u0000\u0001\u0019\u0003\u0000\u0001\u001a\u0007\u0000\u0001\n\u0002\u0000\u0001\u001b\u0001\u001c\u0007\u0000\u0001\u001d\u0002\u0000\u0001\u001e\u0001\u001f\u0007\u0000\u0001 \u0002\u0000\u0001!\u0001\"\u0007\u0000\u0001#\u000b\u0000\u0001$\u0002\u0000\u0001\u0013\u0001\u0014\u0007\u0000\u0001%\u000b\u0000\u0001&\u0002\u0000\u0002\u0016\u0007\u0000\u0001'\u0002\u0000\u0001\u0003\u0001\u0004\u0001\u000e\u0001\u0007\u0001\u0010\u0001\u0011\u0002\t\u0001\n\u0001\u0012\u0002\u0000\u0002\u0013\u0001\u0000\u0001(\u0001\u0000\u0001\b\u0002)\u0001\u0000\u0001\u0013\u0002\u0000\u0001\u0013\u0001\u0014\u0001\u0000\u0001*\u0001\u0000\u0001\b\u0002+\u0001,\u0001\u0014\u0002\u0000\u0001\u0013\u0001\u0014\u0001\u0000\u0001(\u0001\u0000\u0001\b\u0002)\u0001\u0000\u0001\u0015\u0002\u0000\u0002\u0016\u0001\u0000\u0001-\u0002\u0000\u0001-\u0002\u0000\u0001\u0016\u0002\u0000\u0002\u0017\u0001\u0000\u0001)\u0001\u0000\u0001\b\u0002)\u0001\u0000\u0001\u0017\u0002\u0000\u0001\u0017\u0001\u0018\u0001\u0000\u0001+\u0001\u0000\u0001\b\u0002+\u0001,\u0001\u0018\u0002\u0000\u0001\u0017\u0001\u0018\u0001\u0000\u0001)\u0001\u0000\u0001\b\u0002)\u0001\u0000\u0001\u0019\u0003\u0000\u0001\u001a\u0001\u0000\u0001,\u0002\u0000\u0003,\u0001\u001a\u0002\u0000\u0002\u001b\u0001\u0000\u0001.\u0001\u0000\u0001\b\u0002\t\u0001\n\u0001\u001b\u0002\u0000\u0001\u001b\u0001\u001c\u0001\u0000\u0001/\u0001\u0000\u0001\b\u0002\f\u0001\r\u0001\u001c\u0002\u0000\u0001\u001b\u0001\u001c\u0001\u0000\u0001.\u0001\u0000\u0001\b\u0002\t\u0001\n\u0001\u001d\u0002\u0000\u0002\u001e\u0001\u0000\u0001\t\u0001\u0000\u0001\b\u0002\t\u0001\n\u0001\u001e\u0002\u0000\u0001\u001e\u0001\u001f\u0001\u0000\u0001\f\u0001\u0000\u0001\b\u0002\f\u0001\r\u0001\u001f\u0002\u0000\u0001\u001e\u0001\u001f\u0001\u0000\u0001\t\u0001\u0000\u0001\b\u0002\t\u0001\n\u0001 \u0002\u0000\u0002!\u0001\u0000\u0001\n\u0002\u0000\u0003\n\u0001!\u0002\u0000\u0001!\u0001\"\u0001\u0000\u0001\r\u0002\u0000\u0003\r\u0001\"\u0002\u0000\u0001!\u0001\"\u0001\u0000\u0001\n\u0002\u0000\u0003\n\u0001#\u0004\u0000\u0001\u000e\u0006\u0000\u0001$\u0002\u0000\u0001\u0013\u0001\u0014\u0001\u0000\u00010\u0001\u0000\u0001\b\u0002)\u0001\u0000\u0001\u0015\u0002\u0000\u0002\u0016\u0001\u0000\u0001-\u0002\u0000\u0001-\u0002\u0000\u0001'\u0002\u0000\u0002\u0013\u0007\u0000\u0001\u0013\u0002\u0000\u0002\u0017\u0007\u0000\u0001\u0017\u0002\u0000\u0002\u001b\u0007\u0000\u0001\u001b\u0002\u0000\u0002\u001e\u0007\u0000\u0001\u001e\u0002\u0000\u0002!\u0007\u0000\u0001!\u0002\u0000\u00021\u0007\u0000\u00011\u0002\u0000\u0002\u0013\u0007\u0000\u00012\u0002\u0000\u00021\u0001\u0000\u0001-\u0002\u0000\u0001-\u0002\u0000\u00011\u0002\u0000\u0002\u0013\u0001\u0000\u00010\u0001\u0000\u0001\b\u0002)\u0001\u0000\u0001\u0013\u0001\u0000";
    private static final int ZZ_UNKNOWN_ERROR = 0;
    private static final int ZZ_NO_MATCH = 1;
    private static final int ZZ_PUSHBACK_2BIG = 2;
    private static final String[] ZZ_ERROR_MSG;
    private static final int[] ZZ_ATTRIBUTE;
    private static final String ZZ_ATTRIBUTE_PACKED_0 = "\u0001\u0000\u0001\t\u0003\u0001\u0001\t\u000b\u0000\u0004\u0001\u0002\u0000\u0001\u0001\u0001\u0000\u000f\u0001\u0001\u0000\u0001\u0001\u0003\u0000\u0005\u0001";
    private Reader zzReader;
    private int zzState;
    private int zzLexicalState;
    private char[] zzBuffer;
    private int zzMarkedPos;
    private int zzCurrentPos;
    private int zzStartRead;
    private int zzEndRead;
    private int yyline;
    private int yychar;
    private int yycolumn;
    private boolean zzAtBOL;
    private boolean zzAtEOF;
    private boolean zzEOFDone;
    public static final int ALPHANUM = 0;
    public static final int APOSTROPHE = 1;
    public static final int ACRONYM = 2;
    public static final int COMPANY = 3;
    public static final int EMAIL = 4;
    public static final int HOST = 5;
    public static final int NUM = 6;
    public static final int CJ = 7;
    public static final int ACRONYM_DEP = 8;
    public static final String[] TOKEN_TYPES;
    
    private static int[] zzUnpackAction() {
        final int[] result = new int[50];
        int offset = 0;
        offset = zzUnpackAction("\u0001\u0000\u0001\u0001\u0003\u0002\u0001\u0003\u000b\u0000\u0001\u0002\u0003\u0004\u0002\u0000\u0001\u0005\u0001\u0000\u0001\u0005\u0003\u0004\u0006\u0005\u0001\u0006\u0001\u0004\u0002\u0007\u0001\b\u0001\u0000\u0001\b\u0003\u0000\u0002\b\u0001\t\u0001\n\u0001\u0004", offset, result);
        return result;
    }
    
    private static int zzUnpackAction(final String packed, final int offset, final int[] result) {
        int i = 0;
        int j = offset;
        final int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            final int value = packed.charAt(i++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }
    
    private static int[] zzUnpackRowMap() {
        final int[] result = new int[50];
        int offset = 0;
        offset = zzUnpackRowMap("\u0000\u0000\u0000\f\u0000\u0018\u0000$\u00000\u0000\f\u0000<\u0000H\u0000T\u0000`\u0000l\u0000x\u0000\u0084\u0000\u0090\u0000\u009c\u0000¨\u0000´\u0000\u00c0\u0000\u00cc\u0000\u00d8\u0000\u00e4\u0000\u00f0\u0000\u00fc\u0000\u0108\u0000\u0114\u0000\u0120\u0000\u012c\u0000\u0138\u0000\u0144\u0000\u0150\u0000\u015c\u0000\u0168\u0000\u0174\u0000\u0180\u0000\u018c\u0000\u0198\u0000\u01a4\u0000¨\u0000\u01b0\u0000\u01bc\u0000\u01c8\u0000\u01d4\u0000\u01e0\u0000\u01ec\u0000\u01f8\u0000<\u0000l\u0000\u0204\u0000\u0210\u0000\u021c", offset, result);
        return result;
    }
    
    private static int zzUnpackRowMap(final String packed, final int offset, final int[] result) {
        int i = 0;
        int j = offset;
        int high;
        for (int l = packed.length(); i < l; high = packed.charAt(i++) << 16, result[j++] = (high | packed.charAt(i++))) {}
        return j;
    }
    
    private static int[] zzUnpackTrans() {
        final int[] result = new int[552];
        int offset = 0;
        offset = zzUnpackTrans("\u0001\u0002\u0001\u0003\u0001\u0004\u0007\u0002\u0001\u0005\u0001\u0006\r\u0000\u0002\u0003\u0001\u0000\u0001\u0007\u0001\u0000\u0001\b\u0002\t\u0001\n\u0001\u0003\u0002\u0000\u0001\u0003\u0001\u0004\u0001\u0000\u0001\u000b\u0001\u0000\u0001\b\u0002\f\u0001\r\u0001\u0004\u0002\u0000\u0001\u0003\u0001\u0004\u0001\u000e\u0001\u000f\u0001\u0010\u0001\u0011\u0002\t\u0001\n\u0001\u0012\u0002\u0000\u0001\u0013\u0001\u0014\u0007\u0000\u0001\u0015\u0002\u0000\u0002\u0016\u0007\u0000\u0001\u0016\u0002\u0000\u0001\u0017\u0001\u0018\u0007\u0000\u0001\u0019\u0003\u0000\u0001\u001a\u0007\u0000\u0001\n\u0002\u0000\u0001\u001b\u0001\u001c\u0007\u0000\u0001\u001d\u0002\u0000\u0001\u001e\u0001\u001f\u0007\u0000\u0001 \u0002\u0000\u0001!\u0001\"\u0007\u0000\u0001#\u000b\u0000\u0001$\u0002\u0000\u0001\u0013\u0001\u0014\u0007\u0000\u0001%\u000b\u0000\u0001&\u0002\u0000\u0002\u0016\u0007\u0000\u0001'\u0002\u0000\u0001\u0003\u0001\u0004\u0001\u000e\u0001\u0007\u0001\u0010\u0001\u0011\u0002\t\u0001\n\u0001\u0012\u0002\u0000\u0002\u0013\u0001\u0000\u0001(\u0001\u0000\u0001\b\u0002)\u0001\u0000\u0001\u0013\u0002\u0000\u0001\u0013\u0001\u0014\u0001\u0000\u0001*\u0001\u0000\u0001\b\u0002+\u0001,\u0001\u0014\u0002\u0000\u0001\u0013\u0001\u0014\u0001\u0000\u0001(\u0001\u0000\u0001\b\u0002)\u0001\u0000\u0001\u0015\u0002\u0000\u0002\u0016\u0001\u0000\u0001-\u0002\u0000\u0001-\u0002\u0000\u0001\u0016\u0002\u0000\u0002\u0017\u0001\u0000\u0001)\u0001\u0000\u0001\b\u0002)\u0001\u0000\u0001\u0017\u0002\u0000\u0001\u0017\u0001\u0018\u0001\u0000\u0001+\u0001\u0000\u0001\b\u0002+\u0001,\u0001\u0018\u0002\u0000\u0001\u0017\u0001\u0018\u0001\u0000\u0001)\u0001\u0000\u0001\b\u0002)\u0001\u0000\u0001\u0019\u0003\u0000\u0001\u001a\u0001\u0000\u0001,\u0002\u0000\u0003,\u0001\u001a\u0002\u0000\u0002\u001b\u0001\u0000\u0001.\u0001\u0000\u0001\b\u0002\t\u0001\n\u0001\u001b\u0002\u0000\u0001\u001b\u0001\u001c\u0001\u0000\u0001/\u0001\u0000\u0001\b\u0002\f\u0001\r\u0001\u001c\u0002\u0000\u0001\u001b\u0001\u001c\u0001\u0000\u0001.\u0001\u0000\u0001\b\u0002\t\u0001\n\u0001\u001d\u0002\u0000\u0002\u001e\u0001\u0000\u0001\t\u0001\u0000\u0001\b\u0002\t\u0001\n\u0001\u001e\u0002\u0000\u0001\u001e\u0001\u001f\u0001\u0000\u0001\f\u0001\u0000\u0001\b\u0002\f\u0001\r\u0001\u001f\u0002\u0000\u0001\u001e\u0001\u001f\u0001\u0000\u0001\t\u0001\u0000\u0001\b\u0002\t\u0001\n\u0001 \u0002\u0000\u0002!\u0001\u0000\u0001\n\u0002\u0000\u0003\n\u0001!\u0002\u0000\u0001!\u0001\"\u0001\u0000\u0001\r\u0002\u0000\u0003\r\u0001\"\u0002\u0000\u0001!\u0001\"\u0001\u0000\u0001\n\u0002\u0000\u0003\n\u0001#\u0004\u0000\u0001\u000e\u0006\u0000\u0001$\u0002\u0000\u0001\u0013\u0001\u0014\u0001\u0000\u00010\u0001\u0000\u0001\b\u0002)\u0001\u0000\u0001\u0015\u0002\u0000\u0002\u0016\u0001\u0000\u0001-\u0002\u0000\u0001-\u0002\u0000\u0001'\u0002\u0000\u0002\u0013\u0007\u0000\u0001\u0013\u0002\u0000\u0002\u0017\u0007\u0000\u0001\u0017\u0002\u0000\u0002\u001b\u0007\u0000\u0001\u001b\u0002\u0000\u0002\u001e\u0007\u0000\u0001\u001e\u0002\u0000\u0002!\u0007\u0000\u0001!\u0002\u0000\u00021\u0007\u0000\u00011\u0002\u0000\u0002\u0013\u0007\u0000\u00012\u0002\u0000\u00021\u0001\u0000\u0001-\u0002\u0000\u0001-\u0002\u0000\u00011\u0002\u0000\u0002\u0013\u0001\u0000\u00010\u0001\u0000\u0001\b\u0002)\u0001\u0000\u0001\u0013\u0001\u0000", offset, result);
        return result;
    }
    
    private static int zzUnpackTrans(final String packed, final int offset, final int[] result) {
        int i = 0;
        int j = offset;
        final int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            --value;
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }
    
    private static int[] zzUnpackAttribute() {
        final int[] result = new int[50];
        int offset = 0;
        offset = zzUnpackAttribute("\u0001\u0000\u0001\t\u0003\u0001\u0001\t\u000b\u0000\u0004\u0001\u0002\u0000\u0001\u0001\u0001\u0000\u000f\u0001\u0001\u0000\u0001\u0001\u0003\u0000\u0005\u0001", offset, result);
        return result;
    }
    
    private static int zzUnpackAttribute(final String packed, final int offset, final int[] result) {
        int i = 0;
        int j = offset;
        final int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            final int value = packed.charAt(i++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }
    
    public final int yychar() {
        return this.yychar;
    }
    
    public final void getText(final CharTermAttribute t) {
        t.copyBuffer(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
    }
    
    public final void setBufferSize(final int numChars) {
        throw new UnsupportedOperationException();
    }
    
    ClassicTokenizerImpl(final Reader in) {
        this.zzLexicalState = 0;
        this.zzBuffer = new char[4096];
        this.zzAtBOL = true;
        this.zzReader = in;
    }
    
    private static char[] zzUnpackCMap(final String packed) {
        final char[] map = new char[65536];
        int i = 0;
        int j = 0;
        while (i < 1138) {
            int count = packed.charAt(i++);
            final char value = packed.charAt(i++);
            do {
                map[j++] = value;
            } while (--count > 0);
        }
        return map;
    }
    
    private boolean zzRefill() throws IOException {
        if (this.zzStartRead > 0) {
            System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
            this.zzEndRead -= this.zzStartRead;
            this.zzCurrentPos -= this.zzStartRead;
            this.zzMarkedPos -= this.zzStartRead;
            this.zzStartRead = 0;
        }
        if (this.zzCurrentPos >= this.zzBuffer.length) {
            final char[] newBuffer = new char[this.zzCurrentPos * 2];
            System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
            this.zzBuffer = newBuffer;
        }
        final int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
        if (numRead > 0) {
            this.zzEndRead += numRead;
            return false;
        }
        if (numRead != 0) {
            return true;
        }
        final int c = this.zzReader.read();
        if (c == -1) {
            return true;
        }
        this.zzBuffer[this.zzEndRead++] = (char)c;
        return false;
    }
    
    public final void yyclose() throws IOException {
        this.zzAtEOF = true;
        this.zzEndRead = this.zzStartRead;
        if (this.zzReader != null) {
            this.zzReader.close();
        }
    }
    
    public final void yyreset(final Reader reader) {
        this.zzReader = reader;
        this.zzAtBOL = true;
        this.zzAtEOF = false;
        this.zzEOFDone = false;
        final int n = 0;
        this.zzStartRead = n;
        this.zzEndRead = n;
        final int n2 = 0;
        this.zzMarkedPos = n2;
        this.zzCurrentPos = n2;
        final int yyline = 0;
        this.yycolumn = yyline;
        this.yychar = yyline;
        this.yyline = yyline;
        this.zzLexicalState = 0;
        if (this.zzBuffer.length > 4096) {
            this.zzBuffer = new char[4096];
        }
    }
    
    public final int yystate() {
        return this.zzLexicalState;
    }
    
    public final void yybegin(final int newState) {
        this.zzLexicalState = newState;
    }
    
    public final String yytext() {
        return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
    }
    
    public final char yycharat(final int pos) {
        return this.zzBuffer[this.zzStartRead + pos];
    }
    
    public final int yylength() {
        return this.zzMarkedPos - this.zzStartRead;
    }
    
    private void zzScanError(final int errorCode) {
        String message;
        try {
            message = ClassicTokenizerImpl.ZZ_ERROR_MSG[errorCode];
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            message = ClassicTokenizerImpl.ZZ_ERROR_MSG[0];
        }
        throw new Error(message);
    }
    
    public void yypushback(final int number) {
        if (number > this.yylength()) {
            this.zzScanError(2);
        }
        this.zzMarkedPos -= number;
    }
    
    public int getNextToken() throws IOException {
        int zzEndReadL = this.zzEndRead;
        char[] zzBufferL = this.zzBuffer;
        final char[] zzCMapL = ClassicTokenizerImpl.ZZ_CMAP;
        final int[] zzTransL = ClassicTokenizerImpl.ZZ_TRANS;
        final int[] zzRowMapL = ClassicTokenizerImpl.ZZ_ROWMAP;
        final int[] zzAttrL = ClassicTokenizerImpl.ZZ_ATTRIBUTE;
        while (true) {
            int zzMarkedPosL = this.zzMarkedPos;
            this.yychar += zzMarkedPosL - this.zzStartRead;
            int zzAction = -1;
            final int n = zzMarkedPosL;
            this.zzStartRead = n;
            this.zzCurrentPos = n;
            int zzCurrentPosL = n;
            this.zzState = ClassicTokenizerImpl.ZZ_LEXSTATE[this.zzLexicalState];
            int zzAttributes = zzAttrL[this.zzState];
            if ((zzAttributes & 0x1) == 0x1) {
                zzAction = this.zzState;
            }
            int zzInput;
            while (true) {
                if (zzCurrentPosL < zzEndReadL) {
                    zzInput = zzBufferL[zzCurrentPosL++];
                }
                else {
                    if (this.zzAtEOF) {
                        zzInput = -1;
                        break;
                    }
                    this.zzCurrentPos = zzCurrentPosL;
                    this.zzMarkedPos = zzMarkedPosL;
                    final boolean eof = this.zzRefill();
                    zzCurrentPosL = this.zzCurrentPos;
                    zzMarkedPosL = this.zzMarkedPos;
                    zzBufferL = this.zzBuffer;
                    zzEndReadL = this.zzEndRead;
                    if (eof) {
                        zzInput = -1;
                        break;
                    }
                    zzInput = zzBufferL[zzCurrentPosL++];
                }
                final int zzNext = zzTransL[zzRowMapL[this.zzState] + zzCMapL[zzInput]];
                if (zzNext == -1) {
                    break;
                }
                this.zzState = zzNext;
                zzAttributes = zzAttrL[this.zzState];
                if ((zzAttributes & 0x1) != 0x1) {
                    continue;
                }
                zzAction = this.zzState;
                zzMarkedPosL = zzCurrentPosL;
                if ((zzAttributes & 0x8) == 0x8) {
                    break;
                }
            }
            this.zzMarkedPos = zzMarkedPosL;
            switch ((zzAction < 0) ? zzAction : ClassicTokenizerImpl.ZZ_ACTION[zzAction]) {
                case 1: {
                    continue;
                }
                case 11: {
                    continue;
                }
                case 2: {
                    return 0;
                }
                case 12: {
                    continue;
                }
                case 3: {
                    return 7;
                }
                case 13: {
                    continue;
                }
                case 4: {
                    return 5;
                }
                case 14: {
                    continue;
                }
                case 5: {
                    return 6;
                }
                case 15: {
                    continue;
                }
                case 6: {
                    return 1;
                }
                case 16: {
                    continue;
                }
                case 7: {
                    return 3;
                }
                case 17: {
                    continue;
                }
                case 8: {
                    return 8;
                }
                case 18: {
                    continue;
                }
                case 9: {
                    return 2;
                }
                case 19: {
                    continue;
                }
                case 10: {
                    return 4;
                }
                case 20: {
                    continue;
                }
                default: {
                    if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
                        this.zzAtEOF = true;
                        return -1;
                    }
                    this.zzScanError(1);
                    continue;
                }
            }
        }
    }
    
    static {
        ZZ_LEXSTATE = new int[] { 0, 0 };
        ZZ_CMAP = zzUnpackCMap("&\u0000\u0001\u0005\u0001\u0003\u0004\u0000\u0001\t\u0001\u0007\u0001\u0004\u0001\t\n\u0002\u0006\u0000\u0001\u0006\u001a\n\u0004\u0000\u0001\b\u0001\u0000\u001a\n/\u0000\u0001\n\n\u0000\u0001\n\u0004\u0000\u0001\n\u0005\u0000\u0017\n\u0001\u0000\u001f\n\u0001\u0000\u0128\n\u0002\u0000\u0012\n\u001c\u0000^\n\u0002\u0000\t\n\u0002\u0000\u0007\n\u000e\u0000\u0002\n\u000e\u0000\u0005\n\t\u0000\u0001\n\u008b\u0000\u0001\n\u000b\u0000\u0001\n\u0001\u0000\u0003\n\u0001\u0000\u0001\n\u0001\u0000\u0014\n\u0001\u0000,\n\u0001\u0000\b\n\u0002\u0000\u001a\n\f\u0000\u0082\n\n\u00009\n\u0002\u0000\u0002\n\u0002\u0000\u0002\n\u0003\u0000&\n\u0002\u0000\u0002\n7\u0000&\n\u0002\u0000\u0001\n\u0007\u0000'\nH\u0000\u001b\n\u0005\u0000\u0003\n.\u0000\u001a\n\u0005\u0000\u000b\n\u0015\u0000\n\u0002\u0007\u0000c\n\u0001\u0000\u0001\n\u000f\u0000\u0002\n\t\u0000\n\u0002\u0003\n\u0013\u0000\u0001\n\u0001\u0000\u001b\nS\u0000&\n\u015f\u00005\n\u0003\u0000\u0001\n\u0012\u0000\u0001\n\u0007\u0000\n\n\u0004\u0000\n\u0002\u0015\u0000\b\n\u0002\u0000\u0002\n\u0002\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0001\n\u0003\u0000\u0004\n\"\u0000\u0002\n\u0001\u0000\u0003\n\u0004\u0000\n\u0002\u0002\n\u0013\u0000\u0006\n\u0004\u0000\u0002\n\u0002\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0002\n\u0001\u0000\u0002\n\u0001\u0000\u0002\n\u001f\u0000\u0004\n\u0001\u0000\u0001\n\u0007\u0000\n\u0002\u0002\u0000\u0003\n\u0010\u0000\u0007\n\u0001\u0000\u0001\n\u0001\u0000\u0003\n\u0001\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0002\n\u0001\u0000\u0005\n\u0003\u0000\u0001\n\u0012\u0000\u0001\n\u000f\u0000\u0001\n\u0005\u0000\n\u0002\u0015\u0000\b\n\u0002\u0000\u0002\n\u0002\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0002\n\u0002\u0000\u0004\n\u0003\u0000\u0001\n\u001e\u0000\u0002\n\u0001\u0000\u0003\n\u0004\u0000\n\u0002\u0015\u0000\u0006\n\u0003\u0000\u0003\n\u0001\u0000\u0004\n\u0003\u0000\u0002\n\u0001\u0000\u0001\n\u0001\u0000\u0002\n\u0003\u0000\u0002\n\u0003\u0000\u0003\n\u0003\u0000\b\n\u0001\u0000\u0003\n-\u0000\t\u0002\u0015\u0000\b\n\u0001\u0000\u0003\n\u0001\u0000\u0017\n\u0001\u0000\n\n\u0001\u0000\u0005\n&\u0000\u0002\n\u0004\u0000\n\u0002\u0015\u0000\b\n\u0001\u0000\u0003\n\u0001\u0000\u0017\n\u0001\u0000\n\n\u0001\u0000\u0005\n$\u0000\u0001\n\u0001\u0000\u0002\n\u0004\u0000\n\u0002\u0015\u0000\b\n\u0001\u0000\u0003\n\u0001\u0000\u0017\n\u0001\u0000\u0010\n&\u0000\u0002\n\u0004\u0000\n\u0002\u0015\u0000\u0012\n\u0003\u0000\u0018\n\u0001\u0000\t\n\u0001\u0000\u0001\n\u0002\u0000\u0007\n9\u0000\u0001\u00010\n\u0001\u0001\u0002\n\f\u0001\u0007\n\t\u0001\n\u0002'\u0000\u0002\n\u0001\u0000\u0001\n\u0002\u0000\u0002\n\u0001\u0000\u0001\n\u0002\u0000\u0001\n\u0006\u0000\u0004\n\u0001\u0000\u0007\n\u0001\u0000\u0003\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0002\u0000\u0002\n\u0001\u0000\u0004\n\u0001\u0000\u0002\n\t\u0000\u0001\n\u0002\u0000\u0005\n\u0001\u0000\u0001\n\t\u0000\n\u0002\u0002\u0000\u0002\n\"\u0000\u0001\n\u001f\u0000\n\u0002\u0016\u0000\b\n\u0001\u0000\"\n\u001d\u0000\u0004\nt\u0000\"\n\u0001\u0000\u0005\n\u0001\u0000\u0002\n\u0015\u0000\n\u0002\u0006\u0000\u0006\nJ\u0000&\n\n\u0000'\n\t\u0000Z\n\u0005\u0000D\n\u0005\u0000R\n\u0006\u0000\u0007\n\u0001\u0000?\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000'\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u001f\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000\u0007\n\u0001\u0000\u0017\n\u0001\u0000\u001f\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000'\n\u0001\u0000\u0013\n\u000e\u0000\t\u0002.\u0000U\n\f\u0000\u026c\n\u0002\u0000\b\n\n\u0000\u001a\n\u0005\u0000K\n\u0095\u00004\n,\u0000\n\u0002&\u0000\n\u0002\u0006\u0000X\n\b\u0000)\n\u0557\u0000\u009c\n\u0004\u0000Z\n\u0006\u0000\u0016\n\u0002\u0000\u0006\n\u0002\u0000&\n\u0002\u0000\u0006\n\u0002\u0000\b\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u001f\n\u0002\u00005\n\u0001\u0000\u0007\n\u0001\u0000\u0001\n\u0003\u0000\u0003\n\u0001\u0000\u0007\n\u0003\u0000\u0004\n\u0002\u0000\u0006\n\u0004\u0000\r\n\u0005\u0000\u0003\n\u0001\u0000\u0007\n\u0082\u0000\u0001\n\u0082\u0000\u0001\n\u0004\u0000\u0001\n\u0002\u0000\n\n\u0001\u0000\u0001\n\u0003\u0000\u0005\n\u0006\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0001\u0000\u0003\n\u0001\u0000\u0007\n\u0ecb\u0000\u0002\n*\u0000\u0005\n\n\u0000\u0001\u000bT\u000b\b\u000b\u0002\u000b\u0002\u000bZ\u000b\u0001\u000b\u0003\u000b\u0006\u000b(\u000b\u0003\u000b\u0001\u0000^\n\u0011\u0000\u0018\n8\u0000\u0010\u000b\u0100\u0000\u0080\u000b\u0080\u0000\u19b6\u000b\n\u000b@\u0000\u51a6\u000bZ\u000b\u048d\n\u0773\u0000\u2ba4\n\u215c\u0000\u012e\u000b\u00d2\u000b\u0007\n\f\u0000\u0005\n\u0005\u0000\u0001\n\u0001\u0000\n\n\u0001\u0000\r\n\u0001\u0000\u0005\n\u0001\u0000\u0001\n\u0001\u0000\u0002\n\u0001\u0000\u0002\n\u0001\u0000l\n!\u0000\u016b\n\u0012\u0000@\n\u0002\u00006\n(\u0000\f\nt\u0000\u0003\n\u0001\u0000\u0001\n\u0001\u0000\u0087\n\u0013\u0000\n\u0002\u0007\u0000\u001a\n\u0006\u0000\u001a\n\n\u0000\u0001\u000b:\u000b\u001f\n\u0003\u0000\u0006\n\u0002\u0000\u0006\n\u0002\u0000\u0006\n\u0002\u0000\u0003\n#\u0000");
        ZZ_ACTION = zzUnpackAction();
        ZZ_ROWMAP = zzUnpackRowMap();
        ZZ_TRANS = zzUnpackTrans();
        ZZ_ERROR_MSG = new String[] { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
        ZZ_ATTRIBUTE = zzUnpackAttribute();
        TOKEN_TYPES = StandardTokenizer.TOKEN_TYPES;
    }
}
