package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum ParagraphAlignment
{
    LEFT(1), 
    CENTER(2), 
    RIGHT(3), 
    BOTH(4), 
    MEDIUM_KASHIDA(5), 
    DISTRIBUTE(6), 
    NUM_TAB(7), 
    HIGH_KASHIDA(8), 
    LOW_KASHIDA(9), 
    THAI_DISTRIBUTE(10);
    
    private static Map<Integer, ParagraphAlignment> imap;
    private final int value;
    
    private ParagraphAlignment(final int val) {
        this.value = val;
    }
    
    public static ParagraphAlignment valueOf(final int type) {
        final ParagraphAlignment err = ParagraphAlignment.imap.get(type);
        if (err == null) {
            throw new IllegalArgumentException("Unknown paragraph alignment: " + type);
        }
        return err;
    }
    
    public int getValue() {
        return this.value;
    }
    
    static {
        ParagraphAlignment.imap = new HashMap<Integer, ParagraphAlignment>();
        for (final ParagraphAlignment p : values()) {
            ParagraphAlignment.imap.put(p.getValue(), p);
        }
    }
}
