package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum UnderlinePatterns
{
    SINGLE(1), 
    WORDS(2), 
    DOUBLE(3), 
    THICK(4), 
    DOTTED(5), 
    DOTTED_HEAVY(6), 
    DASH(7), 
    DASHED_HEAVY(8), 
    DASH_LONG(9), 
    DASH_LONG_HEAVY(10), 
    DOT_DASH(11), 
    DASH_DOT_HEAVY(12), 
    DOT_DOT_DASH(13), 
    DASH_DOT_DOT_HEAVY(14), 
    WAVE(15), 
    WAVY_HEAVY(16), 
    WAVY_DOUBLE(17), 
    NONE(18);
    
    private static Map<Integer, UnderlinePatterns> imap;
    private final int value;
    
    private UnderlinePatterns(final int val) {
        this.value = val;
    }
    
    public static UnderlinePatterns valueOf(final int type) {
        final UnderlinePatterns align = UnderlinePatterns.imap.get(type);
        if (align == null) {
            throw new IllegalArgumentException("Unknown underline pattern: " + type);
        }
        return align;
    }
    
    public int getValue() {
        return this.value;
    }
    
    static {
        UnderlinePatterns.imap = new HashMap<Integer, UnderlinePatterns>();
        for (final UnderlinePatterns p : values()) {
            UnderlinePatterns.imap.put(p.getValue(), p);
        }
    }
}
