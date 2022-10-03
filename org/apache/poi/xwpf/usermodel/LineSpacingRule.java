package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum LineSpacingRule
{
    AUTO(1), 
    EXACT(2), 
    AT_LEAST(3);
    
    private static Map<Integer, LineSpacingRule> imap;
    private final int value;
    
    private LineSpacingRule(final int val) {
        this.value = val;
    }
    
    public static LineSpacingRule valueOf(final int type) {
        final LineSpacingRule lineType = LineSpacingRule.imap.get(type);
        if (lineType == null) {
            throw new IllegalArgumentException("Unknown line type: " + type);
        }
        return lineType;
    }
    
    public int getValue() {
        return this.value;
    }
    
    static {
        LineSpacingRule.imap = new HashMap<Integer, LineSpacingRule>();
        for (final LineSpacingRule p : values()) {
            LineSpacingRule.imap.put(p.getValue(), p);
        }
    }
}
