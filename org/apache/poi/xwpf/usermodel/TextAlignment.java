package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum TextAlignment
{
    TOP(1), 
    CENTER(2), 
    BASELINE(3), 
    BOTTOM(4), 
    AUTO(5);
    
    private static Map<Integer, TextAlignment> imap;
    private final int value;
    
    private TextAlignment(final int val) {
        this.value = val;
    }
    
    public static TextAlignment valueOf(final int type) {
        final TextAlignment align = TextAlignment.imap.get(type);
        if (align == null) {
            throw new IllegalArgumentException("Unknown text alignment: " + type);
        }
        return align;
    }
    
    public int getValue() {
        return this.value;
    }
    
    static {
        TextAlignment.imap = new HashMap<Integer, TextAlignment>();
        for (final TextAlignment p : values()) {
            TextAlignment.imap.put(p.getValue(), p);
        }
    }
}
