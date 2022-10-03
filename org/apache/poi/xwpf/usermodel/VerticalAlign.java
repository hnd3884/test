package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum VerticalAlign
{
    BASELINE(1), 
    SUPERSCRIPT(2), 
    SUBSCRIPT(3);
    
    private static Map<Integer, VerticalAlign> imap;
    private final int value;
    
    private VerticalAlign(final int val) {
        this.value = val;
    }
    
    public static VerticalAlign valueOf(final int type) {
        final VerticalAlign align = VerticalAlign.imap.get(type);
        if (align == null) {
            throw new IllegalArgumentException("Unknown vertical alignment: " + type);
        }
        return align;
    }
    
    public int getValue() {
        return this.value;
    }
    
    static {
        VerticalAlign.imap = new HashMap<Integer, VerticalAlign>();
        for (final VerticalAlign p : values()) {
            VerticalAlign.imap.put(p.getValue(), p);
        }
    }
}
