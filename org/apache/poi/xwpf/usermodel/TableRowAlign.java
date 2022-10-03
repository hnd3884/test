package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum TableRowAlign
{
    LEFT(1), 
    CENTER(2), 
    RIGHT(3);
    
    private static Map<Integer, TableRowAlign> imap;
    private final int value;
    
    private TableRowAlign(final int val) {
        this.value = val;
    }
    
    public static TableRowAlign valueOf(final int type) {
        final TableRowAlign err = TableRowAlign.imap.get(type);
        if (err == null) {
            throw new IllegalArgumentException("Unknown table row alignment: " + type);
        }
        return err;
    }
    
    public int getValue() {
        return this.value;
    }
    
    static {
        TableRowAlign.imap = new HashMap<Integer, TableRowAlign>();
        for (final TableRowAlign p : values()) {
            TableRowAlign.imap.put(p.getValue(), p);
        }
    }
}
