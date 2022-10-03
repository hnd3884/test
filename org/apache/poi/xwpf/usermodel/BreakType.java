package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum BreakType
{
    PAGE(1), 
    COLUMN(2), 
    TEXT_WRAPPING(3);
    
    private static Map<Integer, BreakType> imap;
    private final int value;
    
    private BreakType(final int val) {
        this.value = val;
    }
    
    public static BreakType valueOf(final int type) {
        final BreakType bType = BreakType.imap.get(type);
        if (bType == null) {
            throw new IllegalArgumentException("Unknown break type: " + type);
        }
        return bType;
    }
    
    public int getValue() {
        return this.value;
    }
    
    static {
        BreakType.imap = new HashMap<Integer, BreakType>();
        for (final BreakType p : values()) {
            BreakType.imap.put(p.getValue(), p);
        }
    }
}
