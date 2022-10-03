package org.apache.poi.common.usermodel.fonts;

import java.util.TreeMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

public enum FontGroup
{
    LATIN, 
    EAST_ASIAN, 
    SYMBOL, 
    COMPLEX_SCRIPT;
    
    private static NavigableMap<Integer, Range> UCS_RANGES;
    
    public static List<FontGroupRange> getFontGroupRanges(final String runText) {
        final List<FontGroupRange> ttrList = new ArrayList<FontGroupRange>();
        if (runText == null || runText.isEmpty()) {
            return ttrList;
        }
        FontGroupRange ttrLast = null;
        int charCount;
        for (int rlen = runText.length(), i = 0; i < rlen; i += charCount) {
            final int cp = runText.codePointAt(i);
            charCount = Character.charCount(cp);
            FontGroup tt;
            if (ttrLast != null && " \n\r".indexOf(cp) > -1) {
                tt = ttrLast.fontGroup;
            }
            else {
                tt = lookup(cp);
            }
            if (ttrLast == null || ttrLast.fontGroup != tt) {
                ttrLast = new FontGroupRange();
                ttrLast.fontGroup = tt;
                ttrList.add(ttrLast);
            }
            final FontGroupRange fontGroupRange = ttrLast;
            fontGroupRange.len += charCount;
        }
        return ttrList;
    }
    
    public static FontGroup getFontGroupFirst(final String runText) {
        return (runText == null || runText.isEmpty()) ? FontGroup.LATIN : lookup(runText.codePointAt(0));
    }
    
    private static FontGroup lookup(final int codepoint) {
        final Map.Entry<Integer, Range> entry = FontGroup.UCS_RANGES.floorEntry(codepoint);
        final Range range = (entry != null) ? entry.getValue() : null;
        return (range != null && codepoint <= range.upper) ? range.fontGroup : FontGroup.EAST_ASIAN;
    }
    
    static {
        (FontGroup.UCS_RANGES = new TreeMap<Integer, Range>()).put(0, new Range(127, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(128, new Range(166, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(169, new Range(175, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(178, new Range(179, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(181, new Range(214, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(216, new Range(246, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(248, new Range(1423, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(1424, new Range(1871, FontGroup.COMPLEX_SCRIPT));
        FontGroup.UCS_RANGES.put(1920, new Range(1983, FontGroup.COMPLEX_SCRIPT));
        FontGroup.UCS_RANGES.put(2304, new Range(4255, FontGroup.COMPLEX_SCRIPT));
        FontGroup.UCS_RANGES.put(4256, new Range(4351, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(4608, new Range(4991, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(5024, new Range(6015, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(7424, new Range(7551, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(7680, new Range(8191, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(6016, new Range(6319, FontGroup.COMPLEX_SCRIPT));
        FontGroup.UCS_RANGES.put(8192, new Range(8203, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(8204, new Range(8207, FontGroup.COMPLEX_SCRIPT));
        FontGroup.UCS_RANGES.put(8208, new Range(8233, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(8234, new Range(8239, FontGroup.COMPLEX_SCRIPT));
        FontGroup.UCS_RANGES.put(8240, new Range(8262, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(8266, new Range(9311, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(9840, new Range(9841, FontGroup.COMPLEX_SCRIPT));
        FontGroup.UCS_RANGES.put(10176, new Range(11263, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(12441, new Range(12442, FontGroup.EAST_ASIAN));
        FontGroup.UCS_RANGES.put(55349, new Range(55349, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(61440, new Range(61695, FontGroup.SYMBOL));
        FontGroup.UCS_RANGES.put(64256, new Range(64279, FontGroup.LATIN));
        FontGroup.UCS_RANGES.put(64285, new Range(64335, FontGroup.COMPLEX_SCRIPT));
        FontGroup.UCS_RANGES.put(65104, new Range(65135, FontGroup.LATIN));
    }
    
    public static class FontGroupRange
    {
        private int len;
        private FontGroup fontGroup;
        
        public int getLength() {
            return this.len;
        }
        
        public FontGroup getFontGroup() {
            return this.fontGroup;
        }
    }
    
    private static class Range
    {
        int upper;
        FontGroup fontGroup;
        
        Range(final int upper, final FontGroup fontGroup) {
            this.upper = upper;
            this.fontGroup = fontGroup;
        }
    }
}
