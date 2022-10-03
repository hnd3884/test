package org.apache.poi.ddf;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.BitField;

public class EscherColorRef
{
    private int opid;
    private int colorRef;
    private static final BitField FLAG_SYS_INDEX;
    private static final BitField FLAG_SCHEME_INDEX;
    private static final BitField FLAG_SYSTEM_RGB;
    private static final BitField FLAG_PALETTE_RGB;
    private static final BitField FLAG_PALETTE_INDEX;
    private static final BitField FLAG_BLUE;
    private static final BitField FLAG_GREEN;
    private static final BitField FLAG_RED;
    
    public EscherColorRef(final int colorRef) {
        this.opid = -1;
        this.colorRef = colorRef;
    }
    
    public EscherColorRef(final byte[] source, final int start, final int len) {
        this.opid = -1;
        assert len == 6;
        int offset = start;
        if (len == 6) {
            this.opid = LittleEndian.getUShort(source, offset);
            offset += 2;
        }
        this.colorRef = LittleEndian.getInt(source, offset);
    }
    
    public boolean hasSysIndexFlag() {
        return EscherColorRef.FLAG_SYS_INDEX.isSet(this.colorRef);
    }
    
    public void setSysIndexFlag(final boolean flag) {
        this.colorRef = EscherColorRef.FLAG_SYS_INDEX.setBoolean(this.colorRef, flag);
    }
    
    public boolean hasSchemeIndexFlag() {
        return EscherColorRef.FLAG_SCHEME_INDEX.isSet(this.colorRef);
    }
    
    public void setSchemeIndexFlag(final boolean flag) {
        this.colorRef = EscherColorRef.FLAG_SCHEME_INDEX.setBoolean(this.colorRef, flag);
    }
    
    public boolean hasSystemRGBFlag() {
        return EscherColorRef.FLAG_SYSTEM_RGB.isSet(this.colorRef);
    }
    
    public void setSystemRGBFlag(final boolean flag) {
        this.colorRef = EscherColorRef.FLAG_SYSTEM_RGB.setBoolean(this.colorRef, flag);
    }
    
    public boolean hasPaletteRGBFlag() {
        return EscherColorRef.FLAG_PALETTE_RGB.isSet(this.colorRef);
    }
    
    public void setPaletteRGBFlag(final boolean flag) {
        this.colorRef = EscherColorRef.FLAG_PALETTE_RGB.setBoolean(this.colorRef, flag);
    }
    
    public boolean hasPaletteIndexFlag() {
        return EscherColorRef.FLAG_PALETTE_INDEX.isSet(this.colorRef);
    }
    
    public void setPaletteIndexFlag(final boolean flag) {
        this.colorRef = EscherColorRef.FLAG_PALETTE_INDEX.setBoolean(this.colorRef, flag);
    }
    
    public int[] getRGB() {
        return new int[] { EscherColorRef.FLAG_RED.getValue(this.colorRef), EscherColorRef.FLAG_GREEN.getValue(this.colorRef), EscherColorRef.FLAG_BLUE.getValue(this.colorRef) };
    }
    
    public SysIndexSource getSysIndexSource() {
        if (!this.hasSysIndexFlag()) {
            return null;
        }
        final int val = EscherColorRef.FLAG_RED.getValue(this.colorRef);
        for (final SysIndexSource sis : SysIndexSource.values()) {
            if (sis.value == val) {
                return sis;
            }
        }
        return null;
    }
    
    public SysIndexProcedure getSysIndexProcedure() {
        if (!this.hasSysIndexFlag()) {
            return null;
        }
        final int val = EscherColorRef.FLAG_GREEN.getValue(this.colorRef);
        for (final SysIndexProcedure sip : SysIndexProcedure.values()) {
            if (sip != SysIndexProcedure.INVERT_AFTER) {
                if (sip != SysIndexProcedure.INVERT_HIGHBIT_AFTER) {
                    if (sip.mask.isSet(val)) {
                        return sip;
                    }
                }
            }
        }
        return null;
    }
    
    public int getSysIndexInvert() {
        if (!this.hasSysIndexFlag()) {
            return 0;
        }
        final int val = EscherColorRef.FLAG_GREEN.getValue(this.colorRef);
        if (SysIndexProcedure.INVERT_AFTER.mask.isSet(val)) {
            return 1;
        }
        if (SysIndexProcedure.INVERT_HIGHBIT_AFTER.mask.isSet(val)) {
            return 2;
        }
        return 0;
    }
    
    public int getSchemeIndex() {
        if (!this.hasSchemeIndexFlag()) {
            return -1;
        }
        return EscherColorRef.FLAG_RED.getValue(this.colorRef);
    }
    
    public int getPaletteIndex() {
        return this.hasPaletteIndexFlag() ? this.getIndex() : -1;
    }
    
    public int getSysIndex() {
        return this.hasSysIndexFlag() ? this.getIndex() : -1;
    }
    
    private int getIndex() {
        return EscherColorRef.FLAG_GREEN.getValue(this.colorRef) << 8 | EscherColorRef.FLAG_RED.getValue(this.colorRef);
    }
    
    static {
        FLAG_SYS_INDEX = new BitField(268435456);
        FLAG_SCHEME_INDEX = new BitField(134217728);
        FLAG_SYSTEM_RGB = new BitField(67108864);
        FLAG_PALETTE_RGB = new BitField(33554432);
        FLAG_PALETTE_INDEX = new BitField(16777216);
        FLAG_BLUE = new BitField(16711680);
        FLAG_GREEN = new BitField(65280);
        FLAG_RED = new BitField(255);
    }
    
    public enum SysIndexSource
    {
        FILL_COLOR(240), 
        LINE_OR_FILL_COLOR(241), 
        LINE_COLOR(242), 
        SHADOW_COLOR(243), 
        CURRENT_OR_LAST_COLOR(244), 
        FILL_BACKGROUND_COLOR(245), 
        LINE_BACKGROUND_COLOR(246), 
        FILL_OR_LINE_COLOR(247);
        
        private int value;
        
        private SysIndexSource(final int value) {
            this.value = value;
        }
    }
    
    public enum SysIndexProcedure
    {
        DARKEN_COLOR(1), 
        LIGHTEN_COLOR(2), 
        ADD_GRAY_LEVEL(3), 
        SUB_GRAY_LEVEL(4), 
        REVERSE_GRAY_LEVEL(5), 
        THRESHOLD(6), 
        INVERT_AFTER(32), 
        INVERT_HIGHBIT_AFTER(64);
        
        private BitField mask;
        
        private SysIndexProcedure(final int mask) {
            this.mask = new BitField(mask);
        }
    }
}
