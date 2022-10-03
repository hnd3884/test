package org.apache.poi.common.usermodel.fonts;

public enum FontPitch
{
    DEFAULT(0), 
    FIXED(1), 
    VARIABLE(2);
    
    private int nativeId;
    
    private FontPitch(final int nativeId) {
        this.nativeId = nativeId;
    }
    
    public int getNativeId() {
        return this.nativeId;
    }
    
    public static FontPitch valueOf(final int flag) {
        for (final FontPitch fp : values()) {
            if (fp.nativeId == flag) {
                return fp;
            }
        }
        return null;
    }
    
    public static byte getNativeId(final FontPitch pitch, final FontFamily family) {
        return (byte)(pitch.getNativeId() | family.getFlag() << 4);
    }
    
    public static FontPitch valueOfPitchFamily(final byte pitchAndFamily) {
        return valueOf(pitchAndFamily & 0x3);
    }
}
