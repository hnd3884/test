package sun.text.normalizer;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;

public final class NormalizerImpl
{
    static final NormalizerImpl IMPL;
    static final int UNSIGNED_BYTE_MASK = 255;
    static final long UNSIGNED_INT_MASK = 4294967295L;
    private static final String DATA_FILE_NAME = "/sun/text/resources/unorm.icu";
    public static final int QC_NFC = 17;
    public static final int QC_NFKC = 34;
    public static final int QC_NFD = 4;
    public static final int QC_NFKD = 8;
    public static final int QC_ANY_NO = 15;
    public static final int QC_MAYBE = 16;
    public static final int QC_ANY_MAYBE = 48;
    public static final int QC_MASK = 63;
    private static final int COMBINES_FWD = 64;
    private static final int COMBINES_BACK = 128;
    public static final int COMBINES_ANY = 192;
    private static final int CC_SHIFT = 8;
    public static final int CC_MASK = 65280;
    private static final int EXTRA_SHIFT = 16;
    private static final long MIN_SPECIAL = 4227858432L;
    private static final long SURROGATES_TOP = 4293918720L;
    private static final long MIN_HANGUL = 4293918720L;
    private static final long JAMO_V_TOP = 4294115328L;
    static final int INDEX_TRIE_SIZE = 0;
    static final int INDEX_CHAR_COUNT = 1;
    static final int INDEX_COMBINE_DATA_COUNT = 2;
    public static final int INDEX_MIN_NFC_NO_MAYBE = 6;
    public static final int INDEX_MIN_NFKC_NO_MAYBE = 7;
    public static final int INDEX_MIN_NFD_NO_MAYBE = 8;
    public static final int INDEX_MIN_NFKD_NO_MAYBE = 9;
    static final int INDEX_FCD_TRIE_SIZE = 10;
    static final int INDEX_AUX_TRIE_SIZE = 11;
    static final int INDEX_TOP = 32;
    private static final int AUX_UNSAFE_SHIFT = 11;
    private static final int AUX_COMP_EX_SHIFT = 10;
    private static final int AUX_NFC_SKIPPABLE_F_SHIFT = 12;
    private static final int AUX_MAX_FNC = 1024;
    private static final int AUX_UNSAFE_MASK = 2048;
    private static final int AUX_FNC_MASK = 1023;
    private static final int AUX_COMP_EX_MASK = 1024;
    private static final long AUX_NFC_SKIP_F_MASK = 4096L;
    private static final int MAX_BUFFER_SIZE = 20;
    private static FCDTrieImpl fcdTrieImpl;
    private static NormTrieImpl normTrieImpl;
    private static AuxTrieImpl auxTrieImpl;
    private static int[] indexes;
    private static char[] combiningTable;
    private static char[] extraData;
    private static boolean isDataLoaded;
    private static boolean isFormatVersion_2_1;
    private static boolean isFormatVersion_2_2;
    private static byte[] unicodeVersion;
    private static final int DATA_BUFFER_SIZE = 25000;
    public static final int MIN_WITH_LEAD_CC = 768;
    private static final int DECOMP_FLAG_LENGTH_HAS_CC = 128;
    private static final int DECOMP_LENGTH_MASK = 127;
    private static final int BMP_INDEX_LENGTH = 2048;
    private static final int SURROGATE_BLOCK_BITS = 5;
    public static final int JAMO_L_BASE = 4352;
    public static final int JAMO_V_BASE = 4449;
    public static final int JAMO_T_BASE = 4519;
    public static final int HANGUL_BASE = 44032;
    public static final int JAMO_L_COUNT = 19;
    public static final int JAMO_V_COUNT = 21;
    public static final int JAMO_T_COUNT = 28;
    public static final int HANGUL_COUNT = 11172;
    private static final int OPTIONS_NX_MASK = 31;
    private static final int OPTIONS_UNICODE_MASK = 224;
    public static final int OPTIONS_SETS_MASK = 255;
    private static final UnicodeSet[] nxCache;
    private static final int NX_HANGUL = 1;
    private static final int NX_CJK_COMPAT = 2;
    public static final int BEFORE_PRI_29 = 256;
    public static final int OPTIONS_COMPAT = 4096;
    public static final int OPTIONS_COMPOSE_CONTIGUOUS = 8192;
    public static final int WITHOUT_CORRIGENDUM4_CORRECTIONS = 262144;
    private static final char[][] corrigendum4MappingTable;
    
    public static int getFromIndexesArr(final int n) {
        return NormalizerImpl.indexes[n];
    }
    
    private NormalizerImpl() throws IOException {
        if (!NormalizerImpl.isDataLoaded) {
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(ICUData.getRequiredStream("/sun/text/resources/unorm.icu"), 25000);
            final NormalizerDataReader normalizerDataReader = new NormalizerDataReader(bufferedInputStream);
            NormalizerImpl.indexes = normalizerDataReader.readIndexes(32);
            final byte[] array = new byte[NormalizerImpl.indexes[0]];
            NormalizerImpl.combiningTable = new char[NormalizerImpl.indexes[2]];
            NormalizerImpl.extraData = new char[NormalizerImpl.indexes[1]];
            final byte[] array2 = new byte[NormalizerImpl.indexes[10]];
            final byte[] array3 = new byte[NormalizerImpl.indexes[11]];
            NormalizerImpl.fcdTrieImpl = new FCDTrieImpl();
            NormalizerImpl.normTrieImpl = new NormTrieImpl();
            NormalizerImpl.auxTrieImpl = new AuxTrieImpl();
            normalizerDataReader.read(array, array2, array3, NormalizerImpl.extraData, NormalizerImpl.combiningTable);
            NormTrieImpl.normTrie = new IntTrie(new ByteArrayInputStream(array), NormalizerImpl.normTrieImpl);
            FCDTrieImpl.fcdTrie = new CharTrie(new ByteArrayInputStream(array2), NormalizerImpl.fcdTrieImpl);
            AuxTrieImpl.auxTrie = new CharTrie(new ByteArrayInputStream(array3), NormalizerImpl.auxTrieImpl);
            NormalizerImpl.isDataLoaded = true;
            final byte[] dataFormatVersion = normalizerDataReader.getDataFormatVersion();
            NormalizerImpl.isFormatVersion_2_1 = (dataFormatVersion[0] > 2 || (dataFormatVersion[0] == 2 && dataFormatVersion[1] >= 1));
            NormalizerImpl.isFormatVersion_2_2 = (dataFormatVersion[0] > 2 || (dataFormatVersion[0] == 2 && dataFormatVersion[1] >= 2));
            NormalizerImpl.unicodeVersion = normalizerDataReader.getUnicodeVersion();
            bufferedInputStream.close();
        }
    }
    
    private static boolean isHangulWithoutJamoT(final char c) {
        final char c2 = (char)(c - '\uac00');
        return c2 < '\u2ba4' && c2 % '\u001c' == 0;
    }
    
    private static boolean isNorm32Regular(final long n) {
        return n < 4227858432L;
    }
    
    private static boolean isNorm32LeadSurrogate(final long n) {
        return 4227858432L <= n && n < 4293918720L;
    }
    
    private static boolean isNorm32HangulOrJamo(final long n) {
        return n >= 4293918720L;
    }
    
    private static boolean isJamoVTNorm32JamoV(final long n) {
        return n < 4294115328L;
    }
    
    public static long getNorm32(final char c) {
        return 0xFFFFFFFFL & (long)NormTrieImpl.normTrie.getLeadValue(c);
    }
    
    public static long getNorm32FromSurrogatePair(final long n, final char c) {
        return 0xFFFFFFFFL & (long)NormTrieImpl.normTrie.getTrailValue((int)n, c);
    }
    
    private static long getNorm32(final int n) {
        return 0xFFFFFFFFL & (long)NormTrieImpl.normTrie.getCodePointValue(n);
    }
    
    private static long getNorm32(final char[] array, final int n, final int n2) {
        long n3 = getNorm32(array[n]);
        if ((n3 & (long)n2) > 0L && isNorm32LeadSurrogate(n3)) {
            n3 = getNorm32FromSurrogatePair(n3, array[n + 1]);
        }
        return n3;
    }
    
    public static VersionInfo getUnicodeVersion() {
        return VersionInfo.getInstance(NormalizerImpl.unicodeVersion[0], NormalizerImpl.unicodeVersion[1], NormalizerImpl.unicodeVersion[2], NormalizerImpl.unicodeVersion[3]);
    }
    
    public static char getFCD16(final char c) {
        return FCDTrieImpl.fcdTrie.getLeadValue(c);
    }
    
    public static char getFCD16FromSurrogatePair(final char c, final char c2) {
        return FCDTrieImpl.fcdTrie.getTrailValue(c, c2);
    }
    
    public static int getFCD16(final int n) {
        return FCDTrieImpl.fcdTrie.getCodePointValue(n);
    }
    
    private static int getExtraDataIndex(final long n) {
        return (int)(n >> 16);
    }
    
    private static int decompose(final long n, final int n2, final DecomposeArgs decomposeArgs) {
        int extraDataIndex = getExtraDataIndex(n);
        decomposeArgs.length = NormalizerImpl.extraData[extraDataIndex++];
        if ((n & (long)n2 & 0x8L) != 0x0L && decomposeArgs.length >= 256) {
            extraDataIndex += (decomposeArgs.length >> 7 & 0x1) + (decomposeArgs.length & 0x7F);
            decomposeArgs.length >>= 8;
        }
        if ((decomposeArgs.length & 0x80) > 0) {
            final char c = NormalizerImpl.extraData[extraDataIndex++];
            decomposeArgs.cc = (0xFF & c >> 8);
            decomposeArgs.trailCC = ('\u00ff' & c);
        }
        else {
            final int n3 = 0;
            decomposeArgs.trailCC = n3;
            decomposeArgs.cc = n3;
        }
        decomposeArgs.length &= 0x7F;
        return extraDataIndex;
    }
    
    private static int decompose(final long n, final DecomposeArgs decomposeArgs) {
        int extraDataIndex = getExtraDataIndex(n);
        decomposeArgs.length = NormalizerImpl.extraData[extraDataIndex++];
        if ((decomposeArgs.length & 0x80) > 0) {
            final char c = NormalizerImpl.extraData[extraDataIndex++];
            decomposeArgs.cc = (0xFF & c >> 8);
            decomposeArgs.trailCC = ('\u00ff' & c);
        }
        else {
            final int n2 = 0;
            decomposeArgs.trailCC = n2;
            decomposeArgs.cc = n2;
        }
        decomposeArgs.length &= 0x7F;
        return extraDataIndex;
    }
    
    private static int getNextCC(final NextCCArgs nextCCArgs) {
        nextCCArgs.c = nextCCArgs.source[nextCCArgs.next++];
        long n = getNorm32(nextCCArgs.c);
        if ((n & 0xFF00L) == 0x0L) {
            nextCCArgs.c2 = '\0';
            return 0;
        }
        if (isNorm32LeadSurrogate(n)) {
            if (nextCCArgs.next != nextCCArgs.limit) {
                final char c2 = nextCCArgs.source[nextCCArgs.next];
                nextCCArgs.c2 = c2;
                if (UTF16.isTrailSurrogate(c2)) {
                    ++nextCCArgs.next;
                    n = getNorm32FromSurrogatePair(n, nextCCArgs.c2);
                    return (int)(0xFFL & n >> 8);
                }
            }
            nextCCArgs.c2 = '\0';
            return 0;
        }
        nextCCArgs.c2 = '\0';
        return (int)(0xFFL & n >> 8);
    }
    
    private static long getPrevNorm32(final PrevArgs prevArgs, final int n, final int n2) {
        final char[] src = prevArgs.src;
        final int current = prevArgs.current - 1;
        prevArgs.current = current;
        prevArgs.c = src[current];
        prevArgs.c2 = '\0';
        if (prevArgs.c < n) {
            return 0L;
        }
        if (!UTF16.isSurrogate(prevArgs.c)) {
            return getNorm32(prevArgs.c);
        }
        if (UTF16.isLeadSurrogate(prevArgs.c)) {
            return 0L;
        }
        if (prevArgs.current != prevArgs.start) {
            final char c2 = prevArgs.src[prevArgs.current - 1];
            prevArgs.c2 = c2;
            if (UTF16.isLeadSurrogate(c2)) {
                --prevArgs.current;
                final long norm32 = getNorm32(prevArgs.c2);
                if ((norm32 & (long)n2) == 0x0L) {
                    return 0L;
                }
                return getNorm32FromSurrogatePair(norm32, prevArgs.c);
            }
        }
        prevArgs.c2 = '\0';
        return 0L;
    }
    
    private static int getPrevCC(final PrevArgs prevArgs) {
        return (int)(0xFFL & getPrevNorm32(prevArgs, 768, 65280) >> 8);
    }
    
    public static boolean isNFDSafe(final long n, final int n2, final int n3) {
        if ((n & (long)n2) == 0x0L) {
            return true;
        }
        if (isNorm32Regular(n) && (n & (long)n3) != 0x0L) {
            final DecomposeArgs decomposeArgs = new DecomposeArgs();
            decompose(n, n3, decomposeArgs);
            return decomposeArgs.cc == 0;
        }
        return (n & 0xFF00L) == 0x0L;
    }
    
    public static boolean isTrueStarter(final long n, final int n2, final int n3) {
        if ((n & (long)n2) == 0x0L) {
            return true;
        }
        if ((n & (long)n3) != 0x0L) {
            final DecomposeArgs decomposeArgs = new DecomposeArgs();
            final int decompose = decompose(n, n3, decomposeArgs);
            if (decomposeArgs.cc == 0) {
                final int n4 = n2 & 0x3F;
                if ((getNorm32(NormalizerImpl.extraData, decompose, n4) & (long)n4) == 0x0L) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static int insertOrdered(final char[] src, final int i, int current, final int n, final char c, final char c2, final int n2) {
        int n3 = n2;
        if (i < current && n2 != 0) {
            final PrevArgs prevArgs = new PrevArgs();
            prevArgs.current = current;
            prevArgs.start = i;
            prevArgs.src = src;
            final int prevCC = getPrevCC(prevArgs);
            int n4 = prevArgs.current;
            if (n2 < prevCC) {
                n3 = prevCC;
                int j = n4;
                while (i < n4) {
                    final int prevCC2 = getPrevCC(prevArgs);
                    n4 = prevArgs.current;
                    if (n2 >= prevCC2) {
                        break;
                    }
                    j = n4;
                }
                int n5 = n;
                do {
                    src[--n5] = src[--current];
                } while (j != current);
            }
        }
        src[current] = c;
        if (c2 != '\0') {
            src[current + 1] = c2;
        }
        return n3;
    }
    
    private static int mergeOrdered(final char[] array, int start, int next, final char[] array2, final int next2, final int limit, final boolean b) {
        int insertOrdered = 0;
        final boolean b2 = next == next2;
        final NextCCArgs nextCCArgs = new NextCCArgs();
        nextCCArgs.source = array2;
        nextCCArgs.next = next2;
        nextCCArgs.limit = limit;
        if (start != next || !b) {
            while (nextCCArgs.next < nextCCArgs.limit) {
                final int nextCC = getNextCC(nextCCArgs);
                if (nextCC == 0) {
                    insertOrdered = 0;
                    if (b2) {
                        next = nextCCArgs.next;
                    }
                    else {
                        array2[next++] = nextCCArgs.c;
                        if (nextCCArgs.c2 != '\0') {
                            array2[next++] = nextCCArgs.c2;
                        }
                    }
                    if (b) {
                        break;
                    }
                    start = next;
                }
                else {
                    final int n = next + ((nextCCArgs.c2 == '\0') ? 1 : 2);
                    insertOrdered = insertOrdered(array, start, next, n, nextCCArgs.c, nextCCArgs.c2, nextCC);
                    next = n;
                }
            }
        }
        if (nextCCArgs.next == nextCCArgs.limit) {
            return insertOrdered;
        }
        if (!b2) {
            do {
                array[next++] = array2[nextCCArgs.next++];
            } while (nextCCArgs.next != nextCCArgs.limit);
            nextCCArgs.limit = next;
        }
        final PrevArgs prevArgs = new PrevArgs();
        prevArgs.src = array2;
        prevArgs.start = start;
        prevArgs.current = nextCCArgs.limit;
        return getPrevCC(prevArgs);
    }
    
    private static int mergeOrdered(final char[] array, final int n, final int n2, final char[] array2, final int n3, final int n4) {
        return mergeOrdered(array, n, n2, array2, n3, n4, true);
    }
    
    public static NormalizerBase.QuickCheckResult quickCheck(final char[] array, int i, final int n, final int n2, final int n3, final int n4, final boolean b, final UnicodeSet set) {
        final ComposePartArgs composePartArgs = new ComposePartArgs();
        final int n5 = i;
        if (!NormalizerImpl.isDataLoaded) {
            return NormalizerBase.MAYBE;
        }
        final int n6 = 0xFF00 | n3;
        NormalizerBase.QuickCheckResult quickCheckResult = NormalizerBase.YES;
        char prevCC = '\0';
        while (i != n) {
            final char c;
            long n7;
            if ((c = array[i++]) >= n2 && ((n7 = getNorm32(c)) & (long)n6) != 0x0L) {
                char c2;
                if (isNorm32LeadSurrogate(n7)) {
                    if (i != n && UTF16.isTrailSurrogate(c2 = array[i])) {
                        ++i;
                        n7 = getNorm32FromSurrogatePair(n7, c2);
                    }
                    else {
                        n7 = 0L;
                        c2 = '\0';
                    }
                }
                else {
                    c2 = '\0';
                }
                if (nx_contains(set, c, c2)) {
                    n7 = 0L;
                }
                final char c3 = (char)(n7 >> 8 & 0xFFL);
                if (c3 != '\0' && c3 < prevCC) {
                    return NormalizerBase.NO;
                }
                prevCC = c3;
                final long n8 = n7 & (long)n3;
                NormalizerBase.QuickCheckResult quickCheckResult2;
                if ((n8 & 0xFL) >= 1L) {
                    quickCheckResult2 = NormalizerBase.NO;
                }
                else {
                    if (n8 == 0L) {
                        continue;
                    }
                    if (b) {
                        quickCheckResult = NormalizerBase.MAYBE;
                        continue;
                    }
                    final int n9 = n3 << 2 & 0xF;
                    int n10 = i - 1;
                    if (UTF16.isTrailSurrogate(array[n10])) {
                        --n10;
                    }
                    final int previousStarter = findPreviousStarter(array, n5, n10, n6, n9, (char)n2);
                    i = findNextStarter(array, i, n, n3, n9, (char)n2);
                    composePartArgs.prevCC = prevCC;
                    if (0 == strCompare(composePart(composePartArgs, previousStarter, array, i, n, n4, set), 0, composePartArgs.length, array, previousStarter, i, false)) {
                        continue;
                    }
                    quickCheckResult2 = NormalizerBase.NO;
                }
                return quickCheckResult2;
            }
            else {
                prevCC = '\0';
            }
        }
        return quickCheckResult;
    }
    
    public static int decompose(final char[] array, final int n, final int n2, final char[] array2, final int n3, final int n4, final boolean b, final int[] array3, final UnicodeSet set) {
        final char[] array4 = new char[3];
        int n5 = n3;
        int n6 = n;
        char c;
        int n7;
        if (!b) {
            c = (char)NormalizerImpl.indexes[8];
            n7 = 4;
        }
        else {
            c = (char)NormalizerImpl.indexes[9];
            n7 = 8;
        }
        final int n8 = 0xFF00 | n7;
        int n9 = 0;
        int n10 = 0;
        long n11 = 0L;
        char c2 = '\0';
        int decompose = 0;
        int cc;
        int n12 = cc = -1;
        while (true) {
            final int n13 = n6;
            while (n6 != n2 && ((c2 = array[n6]) < c || ((n11 = getNorm32(c2)) & (long)n8) == 0x0L)) {
                n10 = 0;
                ++n6;
            }
            if (n6 != n13) {
                final int n14 = n6 - n13;
                if (n5 + n14 <= n4) {
                    System.arraycopy(array, n13, array2, n5, n14);
                }
                n5 = (n9 = n5 + n14);
            }
            if (n6 == n2) {
                break;
            }
            ++n6;
            char c3;
            char[] extraData;
            int length;
            if (isNorm32HangulOrJamo(n11)) {
                if (nx_contains(set, c2)) {
                    c3 = '\0';
                    extraData = null;
                    length = 1;
                }
                else {
                    extraData = array4;
                    decompose = 0;
                    n12 = (cc = 0);
                    final char c4 = (char)(c2 - '\uac00');
                    c3 = (char)(c4 % '\u001c');
                    c2 = (char)(c4 / '\u001c');
                    if (c3 > '\0') {
                        array4[2] = (char)('\u11a7' + c3);
                        length = 3;
                    }
                    else {
                        length = 2;
                    }
                    array4[1] = (char)(4449 + c2 % '\u0015');
                    array4[0] = (char)(4352 + c2 / '\u0015');
                }
            }
            else {
                if (isNorm32Regular(n11)) {
                    c3 = '\0';
                    length = 1;
                }
                else if (n6 != n2 && UTF16.isTrailSurrogate(c3 = array[n6])) {
                    ++n6;
                    length = 2;
                    n11 = getNorm32FromSurrogatePair(n11, c3);
                }
                else {
                    c3 = '\0';
                    length = 1;
                    n11 = 0L;
                }
                if (nx_contains(set, c2, c3)) {
                    n12 = (cc = 0);
                    extraData = null;
                }
                else if ((n11 & (long)n7) == 0x0L) {
                    n12 = (cc = (int)(0xFFL & n11 >> 8));
                    extraData = null;
                    decompose = -1;
                }
                else {
                    final DecomposeArgs decomposeArgs = new DecomposeArgs();
                    decompose = decompose(n11, n7, decomposeArgs);
                    extraData = NormalizerImpl.extraData;
                    length = decomposeArgs.length;
                    cc = decomposeArgs.cc;
                    n12 = decomposeArgs.trailCC;
                    if (length == 1) {
                        c2 = extraData[decompose];
                        c3 = '\0';
                        extraData = null;
                        decompose = -1;
                    }
                }
            }
            if (n5 + length <= n4) {
                final int n15 = n5;
                if (extraData == null) {
                    if (cc != 0 && cc < n10) {
                        n5 += length;
                        n12 = insertOrdered(array2, n9, n15, n5, c2, c3, cc);
                    }
                    else {
                        array2[n5++] = c2;
                        if (c3 != '\0') {
                            array2[n5++] = c3;
                        }
                    }
                }
                else if (cc != 0 && cc < n10) {
                    n5 += length;
                    n12 = mergeOrdered(array2, n9, n15, extraData, decompose, decompose + length);
                }
                else {
                    do {
                        array2[n5++] = extraData[decompose++];
                    } while (--length > 0);
                }
            }
            else {
                n5 += length;
            }
            n10 = n12;
            if (n10 != 0) {
                continue;
            }
            n9 = n5;
        }
        array3[0] = n10;
        return n5 - n3;
    }
    
    private static int getNextCombining(final NextCombiningArgs nextCombiningArgs, final int n, final UnicodeSet set) {
        nextCombiningArgs.c = nextCombiningArgs.source[nextCombiningArgs.start++];
        long n2 = getNorm32(nextCombiningArgs.c);
        nextCombiningArgs.c2 = '\0';
        nextCombiningArgs.combiningIndex = 0;
        nextCombiningArgs.cc = '\0';
        if ((n2 & 0xFFC0L) == 0x0L) {
            return 0;
        }
        Label_0153: {
            if (!isNorm32Regular(n2)) {
                if (isNorm32HangulOrJamo(n2)) {
                    nextCombiningArgs.combiningIndex = (int)(0xFFFFFFFFL & (0xFFF0L | n2 >> 16));
                    return (int)(n2 & 0xC0L);
                }
                if (nextCombiningArgs.start != n) {
                    final char c2 = nextCombiningArgs.source[nextCombiningArgs.start];
                    nextCombiningArgs.c2 = c2;
                    if (UTF16.isTrailSurrogate(c2)) {
                        ++nextCombiningArgs.start;
                        n2 = getNorm32FromSurrogatePair(n2, nextCombiningArgs.c2);
                        break Label_0153;
                    }
                }
                nextCombiningArgs.c2 = '\0';
                return 0;
            }
        }
        if (nx_contains(set, nextCombiningArgs.c, nextCombiningArgs.c2)) {
            return 0;
        }
        nextCombiningArgs.cc = (char)(n2 >> 8 & 0xFFL);
        final int n3 = (int)(n2 & 0xC0L);
        if (n3 != 0) {
            final int extraDataIndex = getExtraDataIndex(n2);
            nextCombiningArgs.combiningIndex = ((extraDataIndex > 0) ? NormalizerImpl.extraData[extraDataIndex - 1] : '\0');
        }
        return n3;
    }
    
    private static int getCombiningIndexFromStarter(final char c, final char c2) {
        long n = getNorm32(c);
        if (c2 != '\0') {
            n = getNorm32FromSurrogatePair(n, c2);
        }
        return NormalizerImpl.extraData[getExtraDataIndex(n) - 1];
    }
    
    private static int combine(final char[] array, int n, final int n2, final int[] array2) {
        if (array2.length < 2) {
            throw new IllegalArgumentException();
        }
        char c;
        while (true) {
            c = array[n++];
            if (c >= n2) {
                break;
            }
            n += (((array[n] & '\u8000') != 0x0) ? 2 : 1);
        }
        if ((c & '\u7fff') == n2) {
            final char c2 = array[n];
            final int n3 = (int)(0xFFFFFFFFL & (long)((c2 & '\u2000') + 1));
            int n4;
            int n5;
            if ((c2 & '\u8000') != 0x0) {
                if ((c2 & '\u4000') != 0x0) {
                    n4 = (int)(0xFFFFFFFFL & (long)((c2 & '\u03ff') | 0xD800));
                    n5 = array[n + 1];
                }
                else {
                    n4 = array[n + 1];
                    n5 = 0;
                }
            }
            else {
                n4 = (c2 & '\u1fff');
                n5 = 0;
            }
            array2[0] = n4;
            array2[1] = n5;
            return n3;
        }
        return 0;
    }
    
    private static char recompose(final RecomposeArgs recomposeArgs, final int n, final UnicodeSet set) {
        int n2 = 0;
        int n3 = 0;
        final int[] array = new int[2];
        int n4 = -1;
        int combiningIndexFromStarter = 0;
        int n5 = 0;
        char cc = '\0';
        final NextCombiningArgs nextCombiningArgs = new NextCombiningArgs();
        nextCombiningArgs.source = recomposeArgs.source;
        nextCombiningArgs.cc = '\0';
        nextCombiningArgs.c2 = '\0';
        while (true) {
            nextCombiningArgs.start = recomposeArgs.start;
            int nextCombining = getNextCombining(nextCombiningArgs, recomposeArgs.limit, set);
            final int combiningIndex = nextCombiningArgs.combiningIndex;
            recomposeArgs.start = nextCombiningArgs.start;
            Label_0813: {
                if ((nextCombining & 0x80) != 0x0 && n4 != -1) {
                    if ((combiningIndex & 0x8000) != 0x0) {
                        if ((n & 0x100) != 0x0 || cc == '\0') {
                            int start = -1;
                            nextCombining = 0;
                            nextCombiningArgs.c2 = recomposeArgs.source[n4];
                            if (combiningIndex == 65522) {
                                nextCombiningArgs.c2 -= '\u1100';
                                if (nextCombiningArgs.c2 < '\u0013') {
                                    start = recomposeArgs.start - 1;
                                    nextCombiningArgs.c = (char)(44032 + (nextCombiningArgs.c2 * '\u0015' + (nextCombiningArgs.c - '\u1161')) * 28);
                                    if (recomposeArgs.start != recomposeArgs.limit && (nextCombiningArgs.c2 = (char)(recomposeArgs.source[recomposeArgs.start] - '\u11a7')) < '\u001c') {
                                        ++recomposeArgs.start;
                                        final NextCombiningArgs nextCombiningArgs2 = nextCombiningArgs;
                                        nextCombiningArgs2.c += nextCombiningArgs.c2;
                                    }
                                    else {
                                        nextCombining = 64;
                                    }
                                    if (!nx_contains(set, nextCombiningArgs.c)) {
                                        recomposeArgs.source[n4] = nextCombiningArgs.c;
                                    }
                                    else {
                                        if (!isHangulWithoutJamoT(nextCombiningArgs.c)) {
                                            --recomposeArgs.start;
                                        }
                                        start = recomposeArgs.start;
                                    }
                                }
                            }
                            else if (isHangulWithoutJamoT(nextCombiningArgs.c2)) {
                                final NextCombiningArgs nextCombiningArgs3 = nextCombiningArgs;
                                nextCombiningArgs3.c2 += (char)(nextCombiningArgs.c - '\u11a7');
                                if (!nx_contains(set, nextCombiningArgs.c2)) {
                                    start = recomposeArgs.start - 1;
                                    recomposeArgs.source[n4] = nextCombiningArgs.c2;
                                }
                            }
                            if (start != -1) {
                                int limit = start;
                                for (int i = recomposeArgs.start; i < recomposeArgs.limit; recomposeArgs.source[limit++] = recomposeArgs.source[i++]) {}
                                recomposeArgs.start = start;
                                recomposeArgs.limit = limit;
                            }
                            nextCombiningArgs.c2 = '\0';
                            if (nextCombining != 0) {
                                if (recomposeArgs.start == recomposeArgs.limit) {
                                    return cc;
                                }
                                combiningIndexFromStarter = 65520;
                                continue;
                            }
                        }
                    }
                    else if ((combiningIndexFromStarter & 0x8000) == 0x0) {
                        if ((n & 0x100) != 0x0) {
                            if (cc == nextCombiningArgs.cc) {
                                if (cc != '\0') {
                                    break Label_0813;
                                }
                            }
                        }
                        else if (cc >= nextCombiningArgs.cc && cc != '\0') {
                            break Label_0813;
                        }
                        final int combine;
                        if (0 != (combine = combine(NormalizerImpl.combiningTable, combiningIndexFromStarter, combiningIndex, array)) && !nx_contains(set, (char)n2, (char)n3)) {
                            n2 = array[0];
                            n3 = array[1];
                            int start2 = (nextCombiningArgs.c2 == '\0') ? (recomposeArgs.start - 1) : (recomposeArgs.start - 2);
                            recomposeArgs.source[n4] = (char)n2;
                            if (n5 != 0) {
                                if (n3 != 0) {
                                    recomposeArgs.source[n4 + 1] = (char)n3;
                                }
                                else {
                                    n5 = 0;
                                    for (int n6 = n4 + 1, j = n6 + 1; j < start2; recomposeArgs.source[n6++] = recomposeArgs.source[j++]) {}
                                    --start2;
                                }
                            }
                            else if (n3 != 0) {
                                n5 = 1;
                                recomposeArgs.source[n4 + 1] = (char)n3;
                            }
                            if (start2 < recomposeArgs.start) {
                                int limit2 = start2;
                                for (int k = recomposeArgs.start; k < recomposeArgs.limit; recomposeArgs.source[limit2++] = recomposeArgs.source[k++]) {}
                                recomposeArgs.start = start2;
                                recomposeArgs.limit = limit2;
                            }
                            if (recomposeArgs.start == recomposeArgs.limit) {
                                return cc;
                            }
                            if (combine > 1) {
                                combiningIndexFromStarter = getCombiningIndexFromStarter((char)n2, (char)n3);
                                continue;
                            }
                            n4 = -1;
                            continue;
                        }
                    }
                }
            }
            cc = nextCombiningArgs.cc;
            if (recomposeArgs.start == recomposeArgs.limit) {
                return cc;
            }
            if (nextCombiningArgs.cc == '\0') {
                if ((nextCombining & 0x40) != 0x0) {
                    if (nextCombiningArgs.c2 == '\0') {
                        n5 = 0;
                        n4 = recomposeArgs.start - 1;
                    }
                    else {
                        n5 = 0;
                        n4 = recomposeArgs.start - 2;
                    }
                    combiningIndexFromStarter = combiningIndex;
                }
                else {
                    n4 = -1;
                }
            }
            else {
                if ((n & 0x2000) == 0x0) {
                    continue;
                }
                n4 = -1;
            }
        }
    }
    
    private static int findPreviousStarter(final char[] src, final int start, final int current, final int n, final int n2, final char c) {
        final PrevArgs prevArgs = new PrevArgs();
        prevArgs.src = src;
        prevArgs.start = start;
        prevArgs.current = current;
        while (prevArgs.start < prevArgs.current && !isTrueStarter(getPrevNorm32(prevArgs, c, n | n2), n, n2)) {}
        return prevArgs.current;
    }
    
    private static int findNextStarter(final char[] array, int i, final int n, final int n2, final int n3, final char c) {
        final int n4 = 0xFF00 | n2;
        final DecomposeArgs decomposeArgs = new DecomposeArgs();
        while (i != n) {
            final char c2 = array[i];
            if (c2 >= c) {
                long n5 = getNorm32(c2);
                if ((n5 & (long)n4) != 0x0L) {
                    char c3;
                    if (isNorm32LeadSurrogate(n5)) {
                        if (i + 1 == n) {
                            break;
                        }
                        if (!UTF16.isTrailSurrogate(c3 = array[i + 1])) {
                            break;
                        }
                        n5 = getNorm32FromSurrogatePair(n5, c3);
                        if ((n5 & (long)n4) == 0x0L) {
                            break;
                        }
                    }
                    else {
                        c3 = '\0';
                    }
                    if ((n5 & (long)n3) != 0x0L) {
                        final int decompose = decompose(n5, n3, decomposeArgs);
                        if (decomposeArgs.cc == 0 && (getNorm32(NormalizerImpl.extraData, decompose, n2) & (long)n2) == 0x0L) {
                            break;
                        }
                    }
                    i += ((c3 == '\0') ? 1 : 2);
                    continue;
                }
            }
            return i;
        }
        return i;
    }
    
    private static char[] composePart(final ComposePartArgs composePartArgs, final int n, final char[] array, final int n2, final int n3, final int n4, final UnicodeSet set) {
        final boolean b = (n4 & 0x1000) != 0x0;
        final int[] array2 = { 0 };
        char[] source = new char[(n3 - n) * 20];
        while (true) {
            composePartArgs.length = decompose(array, n, n2, source, 0, source.length, b, array2, set);
            if (composePartArgs.length <= source.length) {
                break;
            }
            source = new char[composePartArgs.length];
        }
        int n5 = composePartArgs.length;
        if (composePartArgs.length >= 2) {
            final RecomposeArgs recomposeArgs = new RecomposeArgs();
            recomposeArgs.source = source;
            recomposeArgs.start = 0;
            recomposeArgs.limit = n5;
            composePartArgs.prevCC = recompose(recomposeArgs, n4, set);
            n5 = recomposeArgs.limit;
        }
        composePartArgs.length = n5;
        return source;
    }
    
    private static boolean composeHangul(final char c, final char c2, long norm32, final char[] array, final int[] array2, final int n, final boolean b, final char[] array3, final int n2, final UnicodeSet set) {
        int n3 = array2[0];
        if (isJamoVTNorm32JamoV(norm32)) {
            final char c3 = (char)(c - '\u1100');
            if (c3 < '\u0013') {
                char c4 = (char)(44032 + (c3 * '\u0015' + (c2 - '\u1161')) * 28);
                if (n3 != n) {
                    final char c5 = array[n3];
                    final char c6;
                    if ((c6 = (char)(c5 - '\u11a7')) < '\u001c') {
                        ++n3;
                        c4 += c6;
                    }
                    else if (b) {
                        norm32 = getNorm32(c5);
                        if (isNorm32Regular(norm32) && (norm32 & 0x8L) != 0x0L) {
                            final DecomposeArgs decomposeArgs = new DecomposeArgs();
                            final int decompose = decompose(norm32, 8, decomposeArgs);
                            final char c7;
                            if (decomposeArgs.length == 1 && (c7 = (char)(NormalizerImpl.extraData[decompose] - '\u11a7')) < '\u001c') {
                                ++n3;
                                c4 += c7;
                            }
                        }
                    }
                }
                if (nx_contains(set, c4)) {
                    if (!isHangulWithoutJamoT(c4)) {
                        --n3;
                    }
                    return false;
                }
                array3[n2] = c4;
                array2[0] = n3;
                return true;
            }
        }
        else if (isHangulWithoutJamoT(c)) {
            final char c8 = (char)(c + (c2 - '\u11a7'));
            if (nx_contains(set, c8)) {
                return false;
            }
            array3[n2] = c8;
            array2[0] = n3;
            return true;
        }
        return false;
    }
    
    public static int compose(final char[] array, final int n, final int n2, final char[] array2, final int n3, final int n4, final int n5, final UnicodeSet set) {
        final int[] array3 = { 0 };
        int n6 = n3;
        int nextStarter = n;
        char c;
        int n7;
        if ((n5 & 0x1000) != 0x0) {
            c = (char)NormalizerImpl.indexes[7];
            n7 = 34;
        }
        else {
            c = (char)NormalizerImpl.indexes[6];
            n7 = 17;
        }
        int n8 = nextStarter;
        final int n9 = 0xFF00 | n7;
        int n10 = 0;
        int prevCC = 0;
        long n11 = 0L;
        char c2 = '\0';
        while (true) {
            int n12 = nextStarter;
            while (nextStarter != n2 && ((c2 = array[nextStarter]) < c || ((n11 = getNorm32(c2)) & (long)n9) == 0x0L)) {
                prevCC = 0;
                ++nextStarter;
            }
            if (nextStarter != n12) {
                final int n13 = nextStarter - n12;
                if (n6 + n13 <= n4) {
                    System.arraycopy(array, n12, array2, n6, n13);
                }
                n6 = (n10 = n6 + n13);
                n8 = nextStarter - 1;
                if (UTF16.isTrailSurrogate(array[n8]) && n12 < n8 && UTF16.isLeadSurrogate(array[n8 - 1])) {
                    --n8;
                }
                n12 = nextStarter;
            }
            if (nextStarter == n2) {
                break;
            }
            ++nextStarter;
            int n14;
            char c3;
            int length;
            if (isNorm32HangulOrJamo(n11)) {
                n14 = (prevCC = 0);
                n10 = n6;
                array3[0] = nextStarter;
                if (n6 > 0 && composeHangul(array[n12 - 1], c2, n11, array, array3, n2, (n5 & 0x1000) != 0x0, array2, (n6 <= n4) ? (n6 - 1) : 0, set)) {
                    nextStarter = (n8 = array3[0]);
                    continue;
                }
                nextStarter = array3[0];
                c3 = '\0';
                length = 1;
                n8 = n12;
            }
            else {
                if (isNorm32Regular(n11)) {
                    c3 = '\0';
                    length = 1;
                }
                else if (nextStarter != n2 && UTF16.isTrailSurrogate(c3 = array[nextStarter])) {
                    ++nextStarter;
                    length = 2;
                    n11 = getNorm32FromSurrogatePair(n11, c3);
                }
                else {
                    c3 = '\0';
                    length = 1;
                    n11 = 0L;
                }
                final ComposePartArgs composePartArgs = new ComposePartArgs();
                if (nx_contains(set, c2, c3)) {
                    n14 = 0;
                }
                else if ((n11 & (long)n7) == 0x0L) {
                    n14 = (int)(0xFFL & n11 >> 8);
                }
                else {
                    final int n15 = n7 << 2 & 0xF;
                    if (isTrueStarter(n11, 0xFF00 | n7, n15)) {
                        n8 = n12;
                    }
                    else {
                        n6 -= n12 - n8;
                    }
                    nextStarter = findNextStarter(array, nextStarter, n2, n7, n15, c);
                    composePartArgs.prevCC = prevCC;
                    composePartArgs.length = length;
                    final char[] composePart = composePart(composePartArgs, n8, array, nextStarter, n2, n5, set);
                    if (composePart == null) {
                        break;
                    }
                    prevCC = composePartArgs.prevCC;
                    int length2 = composePartArgs.length;
                    if (n6 + composePartArgs.length <= n4) {
                        for (int i = 0; i < composePartArgs.length; array2[n6++] = composePart[i++], --length2) {}
                    }
                    else {
                        n6 += length2;
                    }
                    n8 = nextStarter;
                    continue;
                }
            }
            if (n6 + length <= n4) {
                if (n14 != 0 && n14 < prevCC) {
                    final int n16 = n6;
                    n6 += length;
                    prevCC = insertOrdered(array2, n10, n16, n6, c2, c3, n14);
                }
                else {
                    array2[n6++] = c2;
                    if (c3 != '\0') {
                        array2[n6++] = c3;
                    }
                    prevCC = n14;
                }
            }
            else {
                n6 += length;
                prevCC = n14;
            }
        }
        return n6 - n3;
    }
    
    public static int getCombiningClass(final int n) {
        return (int)(getNorm32(n) >> 8 & 0xFFL);
    }
    
    public static boolean isFullCompositionExclusion(final int n) {
        return NormalizerImpl.isFormatVersion_2_1 && (AuxTrieImpl.auxTrie.getCodePointValue(n) & '\u0400') != 0x0;
    }
    
    public static boolean isCanonSafeStart(final int n) {
        return NormalizerImpl.isFormatVersion_2_1 && (AuxTrieImpl.auxTrie.getCodePointValue(n) & '\u0800') == 0x0;
    }
    
    public static boolean isNFSkippable(final int n, final NormalizerBase.Mode mode, long n2) {
        n2 &= 0xFFFFFFFFL;
        final long norm32 = getNorm32(n);
        if ((norm32 & n2) != 0x0L) {
            return false;
        }
        if (mode == NormalizerBase.NFD || mode == NormalizerBase.NFKD || mode == NormalizerBase.NONE) {
            return true;
        }
        if ((norm32 & 0x4L) == 0x0L) {
            return true;
        }
        if (isNorm32HangulOrJamo(norm32)) {
            return !isHangulWithoutJamoT((char)n);
        }
        return NormalizerImpl.isFormatVersion_2_2 && ((long)AuxTrieImpl.auxTrie.getCodePointValue(n) & 0x1000L) == 0x0L;
    }
    
    public static UnicodeSet addPropertyStarts(final UnicodeSet set) {
        final TrieIterator trieIterator = new TrieIterator(NormTrieImpl.normTrie);
        final RangeValueIterator.Element element = new RangeValueIterator.Element();
        while (trieIterator.next(element)) {
            set.add(element.start);
        }
        final TrieIterator trieIterator2 = new TrieIterator(FCDTrieImpl.fcdTrie);
        final RangeValueIterator.Element element2 = new RangeValueIterator.Element();
        while (trieIterator2.next(element2)) {
            set.add(element2.start);
        }
        if (NormalizerImpl.isFormatVersion_2_1) {
            final TrieIterator trieIterator3 = new TrieIterator(AuxTrieImpl.auxTrie);
            final RangeValueIterator.Element element3 = new RangeValueIterator.Element();
            while (trieIterator3.next(element3)) {
                set.add(element3.start);
            }
        }
        for (int i = 44032; i < 55204; i += 28) {
            set.add(i);
            set.add(i + 1);
        }
        set.add(55204);
        return set;
    }
    
    public static final int quickCheck(final int n, final int n2) {
        final int n3 = (int)getNorm32(n) & (new int[] { 0, 0, 4, 8, 17, 34 })[n2];
        if (n3 == 0) {
            return 1;
        }
        if ((n3 & 0xF) != 0x0) {
            return 0;
        }
        return 2;
    }
    
    private static int strCompare(final char[] array, int i, final int n, final char[] array2, int n2, final int n3, final boolean b) {
        final int n4 = i;
        final int n5 = n2;
        final int n6 = n - i;
        final int n7 = n3 - n2;
        int n8;
        int n9;
        if (n6 < n7) {
            n8 = -1;
            n9 = n4 + n6;
        }
        else if (n6 == n7) {
            n8 = 0;
            n9 = n4 + n6;
        }
        else {
            n8 = 1;
            n9 = n4 + n7;
        }
        if (array == array2) {
            return n8;
        }
        while (i != n9) {
            char c = array[i];
            char c2 = array2[n2];
            if (c != c2) {
                final int n10 = n4 + n6;
                final int n11 = n5 + n7;
                if (c >= '\ud800' && c2 >= '\ud800' && b) {
                    if (c > '\udbff' || i + 1 == n10 || !UTF16.isTrailSurrogate(array[i + 1])) {
                        if (!UTF16.isTrailSurrogate(c) || n4 == i || !UTF16.isLeadSurrogate(array[i - 1])) {
                            c -= '\u2800';
                        }
                    }
                    if (c2 > '\udbff' || n2 + 1 == n11 || !UTF16.isTrailSurrogate(array2[n2 + 1])) {
                        if (!UTF16.isTrailSurrogate(c2) || n5 == n2 || !UTF16.isLeadSurrogate(array2[n2 - 1])) {
                            c2 -= '\u2800';
                        }
                    }
                }
                return c - c2;
            }
            ++i;
            ++n2;
        }
        return n8;
    }
    
    private static final synchronized UnicodeSet internalGetNXHangul() {
        if (NormalizerImpl.nxCache[1] == null) {
            NormalizerImpl.nxCache[1] = new UnicodeSet(44032, 55203);
        }
        return NormalizerImpl.nxCache[1];
    }
    
    private static final synchronized UnicodeSet internalGetNXCJKCompat() {
        if (NormalizerImpl.nxCache[2] == null) {
            final UnicodeSet set = new UnicodeSet("[:Ideographic:]");
            final UnicodeSet set2 = new UnicodeSet();
            final UnicodeSetIterator unicodeSetIterator = new UnicodeSetIterator(set);
            while (unicodeSetIterator.nextRange() && unicodeSetIterator.codepoint != UnicodeSetIterator.IS_STRING) {
                for (int i = unicodeSetIterator.codepoint; i <= unicodeSetIterator.codepointEnd; ++i) {
                    if ((getNorm32(i) & 0x4L) > 0L) {
                        set2.add(i);
                    }
                }
            }
            NormalizerImpl.nxCache[2] = set2;
        }
        return NormalizerImpl.nxCache[2];
    }
    
    private static final synchronized UnicodeSet internalGetNXUnicode(int n) {
        n &= 0xE0;
        if (n == 0) {
            return null;
        }
        if (NormalizerImpl.nxCache[n] == null) {
            final UnicodeSet set = new UnicodeSet();
            switch (n) {
                case 32: {
                    set.applyPattern("[:^Age=3.2:]");
                    NormalizerImpl.nxCache[n] = set;
                    break;
                }
                default: {
                    return null;
                }
            }
        }
        return NormalizerImpl.nxCache[n];
    }
    
    private static final synchronized UnicodeSet internalGetNX(int n) {
        n &= 0xFF;
        if (NormalizerImpl.nxCache[n] == null) {
            if (n == 1) {
                return internalGetNXHangul();
            }
            if (n == 2) {
                return internalGetNXCJKCompat();
            }
            if ((n & 0xE0) != 0x0 && (n & 0x1F) == 0x0) {
                return internalGetNXUnicode(n);
            }
            final UnicodeSet set = new UnicodeSet();
            final UnicodeSet internalGetNXHangul;
            if ((n & 0x1) != 0x0 && null != (internalGetNXHangul = internalGetNXHangul())) {
                set.addAll(internalGetNXHangul);
            }
            final UnicodeSet internalGetNXCJKCompat;
            if ((n & 0x2) != 0x0 && null != (internalGetNXCJKCompat = internalGetNXCJKCompat())) {
                set.addAll(internalGetNXCJKCompat);
            }
            final UnicodeSet internalGetNXUnicode;
            if ((n & 0xE0) != 0x0 && null != (internalGetNXUnicode = internalGetNXUnicode(n))) {
                set.addAll(internalGetNXUnicode);
            }
            NormalizerImpl.nxCache[n] = set;
        }
        return NormalizerImpl.nxCache[n];
    }
    
    public static final UnicodeSet getNX(int n) {
        if ((n &= 0xFF) == 0x0) {
            return null;
        }
        return internalGetNX(n);
    }
    
    private static final boolean nx_contains(final UnicodeSet set, final int n) {
        return set != null && set.contains(n);
    }
    
    private static final boolean nx_contains(final UnicodeSet set, final char c, final char c2) {
        return set != null && set.contains((c2 == '\0') ? c : UCharacterProperty.getRawSupplementary(c, c2));
    }
    
    public static int getDecompose(final int[] array, final String[] array2) {
        final DecomposeArgs decomposeArgs = new DecomposeArgs();
        int n = -1;
        int n2 = 0;
        while (++n < 195102) {
            if (n == 12543) {
                n = 63744;
            }
            else if (n == 65536) {
                n = 119134;
            }
            else if (n == 119233) {
                n = 194560;
            }
            final long norm32 = getNorm32(n);
            if ((norm32 & 0x4L) != 0x0L && n2 < array.length) {
                array[n2] = n;
                array2[n2++] = new String(NormalizerImpl.extraData, decompose(norm32, decomposeArgs), decomposeArgs.length);
            }
        }
        return n2;
    }
    
    private static boolean needSingleQuotation(final char c) {
        return (c >= '\t' && c <= '\r') || (c >= ' ' && c <= '/') || (c >= ':' && c <= '@') || (c >= '[' && c <= '`') || (c >= '{' && c <= '~');
    }
    
    public static String canonicalDecomposeWithSingleQuotation(final String s) {
        final char[] charArray = s.toCharArray();
        int n = 0;
        final int length = charArray.length;
        char[] array = new char[charArray.length * 3];
        int n2 = 0;
        int n3 = array.length;
        final char[] array2 = new char[3];
        final int n4 = 4;
        final char c = (char)NormalizerImpl.indexes[8];
        final int n5 = 0xFF00 | n4;
        int n6 = 0;
        int n7 = 0;
        long n8 = 0L;
        char c2 = '\0';
        while (true) {
            final int n9 = n;
            while (n != length && ((c2 = charArray[n]) < c || ((n8 = getNorm32(c2)) & (long)n5) == 0x0L || (c2 >= '\uac00' && c2 <= '\ud7a3'))) {
                n7 = 0;
                ++n;
            }
            if (n != n9) {
                final int n10 = n - n9;
                if (n2 + n10 <= n3) {
                    System.arraycopy(charArray, n9, array, n2, n10);
                }
                n2 = (n6 = n2 + n10);
            }
            if (n == length) {
                break;
            }
            ++n;
            char c3;
            int length2;
            if (isNorm32Regular(n8)) {
                c3 = '\0';
                length2 = 1;
            }
            else if (n != length && Character.isLowSurrogate(c3 = charArray[n])) {
                ++n;
                length2 = 2;
                n8 = getNorm32FromSurrogatePair(n8, c3);
            }
            else {
                c3 = '\0';
                length2 = 1;
                n8 = 0L;
            }
            int cc;
            char[] extraData;
            int decompose;
            if ((n8 & (long)n4) == 0x0L) {
                final int n11 = cc = (int)(0xFFL & n8 >> 8);
                extraData = null;
                decompose = -1;
            }
            else {
                final DecomposeArgs decomposeArgs = new DecomposeArgs();
                decompose = decompose(n8, n4, decomposeArgs);
                extraData = NormalizerImpl.extraData;
                length2 = decomposeArgs.length;
                cc = decomposeArgs.cc;
                final int n11 = decomposeArgs.trailCC;
                if (length2 == 1) {
                    c2 = extraData[decompose];
                    c3 = '\0';
                    extraData = null;
                    decompose = -1;
                }
            }
            if (n2 + length2 * 3 >= n3) {
                final char[] array3 = new char[n3 * 2];
                System.arraycopy(array, 0, array3, 0, n2);
                array = array3;
                n3 = array.length;
            }
            final int n12 = n2;
            int n11;
            if (extraData == null) {
                if (needSingleQuotation(c2)) {
                    array[n2++] = '\'';
                    array[n2++] = c2;
                    array[n2++] = '\'';
                    n11 = 0;
                }
                else if (cc != 0 && cc < n7) {
                    n2 += length2;
                    n11 = insertOrdered(array, n6, n12, n2, c2, c3, cc);
                }
                else {
                    array[n2++] = c2;
                    if (c3 != '\0') {
                        array[n2++] = c3;
                    }
                }
            }
            else if (needSingleQuotation(extraData[decompose])) {
                array[n2++] = '\'';
                array[n2++] = extraData[decompose++];
                array[n2++] = '\'';
                --length2;
                do {
                    array[n2++] = extraData[decompose++];
                } while (--length2 > 0);
            }
            else if (cc != 0 && cc < n7) {
                n2 += length2;
                n11 = mergeOrdered(array, n6, n12, extraData, decompose, decompose + length2);
            }
            else {
                do {
                    array[n2++] = extraData[decompose++];
                } while (--length2 > 0);
            }
            n7 = n11;
            if (n7 != 0) {
                continue;
            }
            n6 = n2;
        }
        return new String(array, 0, n2);
    }
    
    public static String convert(final String s) {
        if (s == null) {
            return null;
        }
        final StringBuffer sb = new StringBuffer();
        int nextCodePoint;
        while ((nextCodePoint = UCharacterIterator.getInstance(s).nextCodePoint()) != -1) {
            switch (nextCodePoint) {
                case 194664: {
                    sb.append(NormalizerImpl.corrigendum4MappingTable[0]);
                    continue;
                }
                case 194676: {
                    sb.append(NormalizerImpl.corrigendum4MappingTable[1]);
                    continue;
                }
                case 194847: {
                    sb.append(NormalizerImpl.corrigendum4MappingTable[2]);
                    continue;
                }
                case 194911: {
                    sb.append(NormalizerImpl.corrigendum4MappingTable[3]);
                    continue;
                }
                case 195007: {
                    sb.append(NormalizerImpl.corrigendum4MappingTable[4]);
                    continue;
                }
                default: {
                    UTF16.append(sb, nextCodePoint);
                    continue;
                }
            }
        }
        return sb.toString();
    }
    
    static {
        try {
            IMPL = new NormalizerImpl();
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        nxCache = new UnicodeSet[256];
        corrigendum4MappingTable = new char[][] { { '\ud844', '\udf6a' }, { '\u5f33' }, { '\u43ab' }, { '\u7aae' }, { '\u4d57' } };
    }
    
    static final class NormTrieImpl implements Trie.DataManipulate
    {
        static IntTrie normTrie;
        
        @Override
        public int getFoldingOffset(final int n) {
            return 2048 + (n >> 11 & 0x7FE0);
        }
        
        static {
            NormTrieImpl.normTrie = null;
        }
    }
    
    static final class FCDTrieImpl implements Trie.DataManipulate
    {
        static CharTrie fcdTrie;
        
        @Override
        public int getFoldingOffset(final int n) {
            return n;
        }
        
        static {
            FCDTrieImpl.fcdTrie = null;
        }
    }
    
    static final class AuxTrieImpl implements Trie.DataManipulate
    {
        static CharTrie auxTrie;
        
        @Override
        public int getFoldingOffset(final int n) {
            return (n & 0x3FF) << 5;
        }
        
        static {
            AuxTrieImpl.auxTrie = null;
        }
    }
    
    private static final class DecomposeArgs
    {
        int cc;
        int trailCC;
        int length;
    }
    
    private static final class NextCCArgs
    {
        char[] source;
        int next;
        int limit;
        char c;
        char c2;
    }
    
    private static final class PrevArgs
    {
        char[] src;
        int start;
        int current;
        char c;
        char c2;
    }
    
    private static final class NextCombiningArgs
    {
        char[] source;
        int start;
        char c;
        char c2;
        int combiningIndex;
        char cc;
    }
    
    private static final class RecomposeArgs
    {
        char[] source;
        int start;
        int limit;
    }
    
    private static final class ComposePartArgs
    {
        int prevCC;
        int length;
    }
}
