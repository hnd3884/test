package org.apache.lucene.analysis.in;

import java.util.Iterator;
import java.util.BitSet;
import org.apache.lucene.analysis.util.StemmerUtil;
import java.util.IdentityHashMap;

public class IndicNormalizer
{
    private static final IdentityHashMap<Character.UnicodeBlock, ScriptData> scripts;
    private static final int[][] decompositions;
    
    private static int flag(final Character.UnicodeBlock ub) {
        return IndicNormalizer.scripts.get(ub).flag;
    }
    
    public int normalize(final char[] text, int len) {
        for (int i = 0; i < len; ++i) {
            final Character.UnicodeBlock block = Character.UnicodeBlock.of(text[i]);
            final ScriptData sd = IndicNormalizer.scripts.get(block);
            if (sd != null) {
                final int ch = text[i] - sd.base;
                if (sd.decompMask.get(ch)) {
                    len = this.compose(ch, block, sd, text, i, len);
                }
            }
        }
        return len;
    }
    
    private int compose(final int ch0, final Character.UnicodeBlock block0, final ScriptData sd, final char[] text, final int pos, int len) {
        if (pos + 1 >= len) {
            return len;
        }
        final int ch = text[pos + 1] - sd.base;
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(text[pos + 1]);
        if (block != block0) {
            return len;
        }
        int ch2 = -1;
        if (pos + 2 < len) {
            ch2 = text[pos + 2] - sd.base;
            final Character.UnicodeBlock block2 = Character.UnicodeBlock.of(text[pos + 2]);
            if (text[pos + 2] == '\u200d') {
                ch2 = 255;
            }
            else if (block2 != block) {
                ch2 = -1;
            }
        }
        for (int i = 0; i < IndicNormalizer.decompositions.length; ++i) {
            if (IndicNormalizer.decompositions[i][0] == ch0 && (IndicNormalizer.decompositions[i][4] & sd.flag) != 0x0 && IndicNormalizer.decompositions[i][1] == ch && (IndicNormalizer.decompositions[i][2] < 0 || IndicNormalizer.decompositions[i][2] == ch2)) {
                text[pos] = (char)(sd.base + IndicNormalizer.decompositions[i][3]);
                len = StemmerUtil.delete(text, pos + 1, len);
                if (IndicNormalizer.decompositions[i][2] >= 0) {
                    len = StemmerUtil.delete(text, pos + 1, len);
                }
                return len;
            }
        }
        return len;
    }
    
    static {
        (scripts = new IdentityHashMap<Character.UnicodeBlock, ScriptData>(9)).put(Character.UnicodeBlock.DEVANAGARI, new ScriptData(1, 2304));
        IndicNormalizer.scripts.put(Character.UnicodeBlock.BENGALI, new ScriptData(2, 2432));
        IndicNormalizer.scripts.put(Character.UnicodeBlock.GURMUKHI, new ScriptData(4, 2560));
        IndicNormalizer.scripts.put(Character.UnicodeBlock.GUJARATI, new ScriptData(8, 2688));
        IndicNormalizer.scripts.put(Character.UnicodeBlock.ORIYA, new ScriptData(16, 2816));
        IndicNormalizer.scripts.put(Character.UnicodeBlock.TAMIL, new ScriptData(32, 2944));
        IndicNormalizer.scripts.put(Character.UnicodeBlock.TELUGU, new ScriptData(64, 3072));
        IndicNormalizer.scripts.put(Character.UnicodeBlock.KANNADA, new ScriptData(128, 3200));
        IndicNormalizer.scripts.put(Character.UnicodeBlock.MALAYALAM, new ScriptData(256, 3328));
        decompositions = new int[][] { { 5, 62, 69, 17, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GUJARATI) }, { 5, 62, 70, 18, flag(Character.UnicodeBlock.DEVANAGARI) }, { 5, 62, 71, 19, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GUJARATI) }, { 5, 62, 72, 20, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GUJARATI) }, { 5, 62, -1, 6, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.BENGALI) | flag(Character.UnicodeBlock.GURMUKHI) | flag(Character.UnicodeBlock.GUJARATI) | flag(Character.UnicodeBlock.ORIYA) }, { 5, 69, -1, 114, flag(Character.UnicodeBlock.DEVANAGARI) }, { 5, 69, -1, 13, flag(Character.UnicodeBlock.GUJARATI) }, { 5, 70, -1, 4, flag(Character.UnicodeBlock.DEVANAGARI) }, { 5, 71, -1, 15, flag(Character.UnicodeBlock.GUJARATI) }, { 5, 72, -1, 16, flag(Character.UnicodeBlock.GURMUKHI) | flag(Character.UnicodeBlock.GUJARATI) }, { 5, 73, -1, 17, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GUJARATI) }, { 5, 74, -1, 18, flag(Character.UnicodeBlock.DEVANAGARI) }, { 5, 75, -1, 19, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GUJARATI) }, { 5, 76, -1, 20, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GURMUKHI) | flag(Character.UnicodeBlock.GUJARATI) }, { 6, 69, -1, 17, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GUJARATI) }, { 6, 70, -1, 18, flag(Character.UnicodeBlock.DEVANAGARI) }, { 6, 71, -1, 19, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GUJARATI) }, { 6, 72, -1, 20, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GUJARATI) }, { 7, 87, -1, 8, flag(Character.UnicodeBlock.MALAYALAM) }, { 9, 65, -1, 10, flag(Character.UnicodeBlock.DEVANAGARI) }, { 9, 87, -1, 10, flag(Character.UnicodeBlock.TAMIL) | flag(Character.UnicodeBlock.MALAYALAM) }, { 14, 70, -1, 16, flag(Character.UnicodeBlock.MALAYALAM) }, { 15, 69, -1, 13, flag(Character.UnicodeBlock.DEVANAGARI) }, { 15, 70, -1, 14, flag(Character.UnicodeBlock.DEVANAGARI) }, { 15, 71, -1, 16, flag(Character.UnicodeBlock.DEVANAGARI) }, { 15, 87, -1, 16, flag(Character.UnicodeBlock.ORIYA) }, { 18, 62, -1, 19, flag(Character.UnicodeBlock.MALAYALAM) }, { 18, 76, -1, 20, flag(Character.UnicodeBlock.TELUGU) | flag(Character.UnicodeBlock.KANNADA) }, { 18, 85, -1, 19, flag(Character.UnicodeBlock.TELUGU) }, { 18, 87, -1, 20, flag(Character.UnicodeBlock.TAMIL) | flag(Character.UnicodeBlock.MALAYALAM) }, { 19, 87, -1, 20, flag(Character.UnicodeBlock.ORIYA) }, { 21, 60, -1, 88, flag(Character.UnicodeBlock.DEVANAGARI) }, { 22, 60, -1, 89, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GURMUKHI) }, { 23, 60, -1, 90, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GURMUKHI) }, { 28, 60, -1, 91, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GURMUKHI) }, { 33, 60, -1, 92, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.BENGALI) | flag(Character.UnicodeBlock.ORIYA) }, { 34, 60, -1, 93, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.BENGALI) | flag(Character.UnicodeBlock.ORIYA) }, { 35, 77, 255, 122, flag(Character.UnicodeBlock.MALAYALAM) }, { 36, 77, 255, 78, flag(Character.UnicodeBlock.BENGALI) }, { 40, 60, -1, 41, flag(Character.UnicodeBlock.DEVANAGARI) }, { 40, 77, 255, 123, flag(Character.UnicodeBlock.MALAYALAM) }, { 43, 60, -1, 94, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GURMUKHI) }, { 47, 60, -1, 95, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.BENGALI) }, { 44, 65, 65, 11, flag(Character.UnicodeBlock.TELUGU) }, { 48, 60, -1, 49, flag(Character.UnicodeBlock.DEVANAGARI) }, { 48, 77, 255, 124, flag(Character.UnicodeBlock.MALAYALAM) }, { 50, 77, 255, 125, flag(Character.UnicodeBlock.MALAYALAM) }, { 51, 60, -1, 52, flag(Character.UnicodeBlock.DEVANAGARI) }, { 51, 77, 255, 126, flag(Character.UnicodeBlock.MALAYALAM) }, { 53, 65, -1, 46, flag(Character.UnicodeBlock.TELUGU) }, { 62, 69, -1, 73, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GUJARATI) }, { 62, 70, -1, 74, flag(Character.UnicodeBlock.DEVANAGARI) }, { 62, 71, -1, 75, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GUJARATI) }, { 62, 72, -1, 76, flag(Character.UnicodeBlock.DEVANAGARI) | flag(Character.UnicodeBlock.GUJARATI) }, { 63, 85, -1, 64, flag(Character.UnicodeBlock.KANNADA) }, { 65, 65, -1, 66, flag(Character.UnicodeBlock.GURMUKHI) }, { 70, 62, -1, 74, flag(Character.UnicodeBlock.TAMIL) | flag(Character.UnicodeBlock.MALAYALAM) }, { 70, 66, 85, 75, flag(Character.UnicodeBlock.KANNADA) }, { 70, 66, -1, 74, flag(Character.UnicodeBlock.KANNADA) }, { 70, 70, -1, 72, flag(Character.UnicodeBlock.MALAYALAM) }, { 70, 85, -1, 71, flag(Character.UnicodeBlock.TELUGU) | flag(Character.UnicodeBlock.KANNADA) }, { 70, 86, -1, 72, flag(Character.UnicodeBlock.TELUGU) | flag(Character.UnicodeBlock.KANNADA) }, { 70, 87, -1, 76, flag(Character.UnicodeBlock.TAMIL) | flag(Character.UnicodeBlock.MALAYALAM) }, { 71, 62, -1, 75, flag(Character.UnicodeBlock.BENGALI) | flag(Character.UnicodeBlock.ORIYA) | flag(Character.UnicodeBlock.TAMIL) | flag(Character.UnicodeBlock.MALAYALAM) }, { 71, 87, -1, 76, flag(Character.UnicodeBlock.BENGALI) | flag(Character.UnicodeBlock.ORIYA) }, { 74, 85, -1, 75, flag(Character.UnicodeBlock.KANNADA) }, { 114, 63, -1, 7, flag(Character.UnicodeBlock.GURMUKHI) }, { 114, 64, -1, 8, flag(Character.UnicodeBlock.GURMUKHI) }, { 114, 71, -1, 15, flag(Character.UnicodeBlock.GURMUKHI) }, { 115, 65, -1, 9, flag(Character.UnicodeBlock.GURMUKHI) }, { 115, 66, -1, 10, flag(Character.UnicodeBlock.GURMUKHI) }, { 115, 75, -1, 19, flag(Character.UnicodeBlock.GURMUKHI) } };
        for (final ScriptData sd : IndicNormalizer.scripts.values()) {
            sd.decompMask = new BitSet(127);
            for (int i = 0; i < IndicNormalizer.decompositions.length; ++i) {
                final int ch = IndicNormalizer.decompositions[i][0];
                final int flags = IndicNormalizer.decompositions[i][4];
                if ((flags & sd.flag) != 0x0) {
                    sd.decompMask.set(ch);
                }
            }
        }
    }
    
    private static class ScriptData
    {
        final int flag;
        final int base;
        BitSet decompMask;
        
        ScriptData(final int flag, final int base) {
            this.flag = flag;
            this.base = base;
        }
    }
}
