package org.apache.poi.common.usermodel.fonts;

public enum FontFamily
{
    FF_DONTCARE(0), 
    FF_ROMAN(1), 
    FF_SWISS(2), 
    FF_MODERN(3), 
    FF_SCRIPT(4), 
    FF_DECORATIVE(5);
    
    private int nativeId;
    
    private FontFamily(final int nativeId) {
        this.nativeId = nativeId;
    }
    
    public int getFlag() {
        return this.nativeId;
    }
    
    public static FontFamily valueOf(final int nativeId) {
        for (final FontFamily ff : values()) {
            if (ff.nativeId == nativeId) {
                return ff;
            }
        }
        return null;
    }
    
    public static FontFamily valueOfPitchFamily(final byte pitchAndFamily) {
        return valueOf(pitchAndFamily >>> 4);
    }
}
