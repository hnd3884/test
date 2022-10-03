package sun.font;

import java.awt.font.LineMetrics;

public final class CoreMetrics
{
    public final float ascent;
    public final float descent;
    public final float leading;
    public final float height;
    public final int baselineIndex;
    public final float[] baselineOffsets;
    public final float strikethroughOffset;
    public final float strikethroughThickness;
    public final float underlineOffset;
    public final float underlineThickness;
    public final float ssOffset;
    public final float italicAngle;
    
    public CoreMetrics(final float ascent, final float descent, final float leading, final float height, final int baselineIndex, final float[] baselineOffsets, final float strikethroughOffset, final float strikethroughThickness, final float underlineOffset, final float underlineThickness, final float ssOffset, final float italicAngle) {
        this.ascent = ascent;
        this.descent = descent;
        this.leading = leading;
        this.height = height;
        this.baselineIndex = baselineIndex;
        this.baselineOffsets = baselineOffsets;
        this.strikethroughOffset = strikethroughOffset;
        this.strikethroughThickness = strikethroughThickness;
        this.underlineOffset = underlineOffset;
        this.underlineThickness = underlineThickness;
        this.ssOffset = ssOffset;
        this.italicAngle = italicAngle;
    }
    
    public static CoreMetrics get(final LineMetrics lineMetrics) {
        return ((FontLineMetrics)lineMetrics).cm;
    }
    
    @Override
    public final int hashCode() {
        return Float.floatToIntBits(this.ascent + this.ssOffset);
    }
    
    @Override
    public final boolean equals(final Object o) {
        try {
            return this.equals((CoreMetrics)o);
        }
        catch (final ClassCastException ex) {
            return false;
        }
    }
    
    public final boolean equals(final CoreMetrics coreMetrics) {
        return coreMetrics != null && (this == coreMetrics || (this.ascent == coreMetrics.ascent && this.descent == coreMetrics.descent && this.leading == coreMetrics.leading && this.baselineIndex == coreMetrics.baselineIndex && this.baselineOffsets[0] == coreMetrics.baselineOffsets[0] && this.baselineOffsets[1] == coreMetrics.baselineOffsets[1] && this.baselineOffsets[2] == coreMetrics.baselineOffsets[2] && this.strikethroughOffset == coreMetrics.strikethroughOffset && this.strikethroughThickness == coreMetrics.strikethroughThickness && this.underlineOffset == coreMetrics.underlineOffset && this.underlineThickness == coreMetrics.underlineThickness && this.ssOffset == coreMetrics.ssOffset && this.italicAngle == coreMetrics.italicAngle));
    }
    
    public final float effectiveBaselineOffset(final float[] array) {
        switch (this.baselineIndex) {
            case -1: {
                return array[4] + this.ascent;
            }
            case -2: {
                return array[3] - this.descent;
            }
            default: {
                return array[this.baselineIndex];
            }
        }
    }
}
