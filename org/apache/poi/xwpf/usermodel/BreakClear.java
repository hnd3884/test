package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum BreakClear
{
    NONE(1), 
    LEFT(2), 
    RIGHT(3), 
    ALL(4);
    
    private static Map<Integer, BreakClear> imap;
    private final int value;
    
    private BreakClear(final int val) {
        this.value = val;
    }
    
    public static BreakClear valueOf(final int type) {
        final BreakClear bType = BreakClear.imap.get(type);
        if (bType == null) {
            throw new IllegalArgumentException("Unknown break clear type: " + type);
        }
        return bType;
    }
    
    public int getValue() {
        return this.value;
    }
    
    static {
        BreakClear.imap = new HashMap<Integer, BreakClear>();
        for (final BreakClear p : values()) {
            BreakClear.imap.put(p.getValue(), p);
        }
    }
}
