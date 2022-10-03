package javax.swing.plaf.nimbus;

import javax.swing.plaf.UIResource;
import javax.swing.UIManager;
import java.awt.Color;

class DerivedColor extends Color
{
    private final String uiDefaultParentName;
    private final float hOffset;
    private final float sOffset;
    private final float bOffset;
    private final int aOffset;
    private int argbValue;
    
    DerivedColor(final String uiDefaultParentName, final float hOffset, final float sOffset, final float bOffset, final int aOffset) {
        super(0);
        this.uiDefaultParentName = uiDefaultParentName;
        this.hOffset = hOffset;
        this.sOffset = sOffset;
        this.bOffset = bOffset;
        this.aOffset = aOffset;
    }
    
    public String getUiDefaultParentName() {
        return this.uiDefaultParentName;
    }
    
    public float getHueOffset() {
        return this.hOffset;
    }
    
    public float getSaturationOffset() {
        return this.sOffset;
    }
    
    public float getBrightnessOffset() {
        return this.bOffset;
    }
    
    public int getAlphaOffset() {
        return this.aOffset;
    }
    
    public void rederiveColor() {
        final Color color = UIManager.getColor(this.uiDefaultParentName);
        if (color != null) {
            final float[] rgBtoHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            rgBtoHSB[0] = this.clamp(rgBtoHSB[0] + this.hOffset);
            rgBtoHSB[1] = this.clamp(rgBtoHSB[1] + this.sOffset);
            rgBtoHSB[2] = this.clamp(rgBtoHSB[2] + this.bOffset);
            this.argbValue = ((Color.HSBtoRGB(rgBtoHSB[0], rgBtoHSB[1], rgBtoHSB[2]) & 0xFFFFFF) | this.clamp(color.getAlpha() + this.aOffset) << 24);
        }
        else {
            final float[] array = { this.clamp(this.hOffset), this.clamp(this.sOffset), this.clamp(this.bOffset) };
            this.argbValue = ((Color.HSBtoRGB(array[0], array[1], array[2]) & 0xFFFFFF) | this.clamp(this.aOffset) << 24);
        }
    }
    
    @Override
    public int getRGB() {
        return this.argbValue;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DerivedColor)) {
            return false;
        }
        final DerivedColor derivedColor = (DerivedColor)o;
        return this.aOffset == derivedColor.aOffset && Float.compare(derivedColor.bOffset, this.bOffset) == 0 && Float.compare(derivedColor.hOffset, this.hOffset) == 0 && Float.compare(derivedColor.sOffset, this.sOffset) == 0 && this.uiDefaultParentName.equals(derivedColor.uiDefaultParentName);
    }
    
    @Override
    public int hashCode() {
        return 31 * ((31 * ((31 * ((31 * this.uiDefaultParentName.hashCode() + this.hOffset != 0.0f) ? Float.floatToIntBits(this.hOffset) : 0) + this.sOffset != 0.0f) ? Float.floatToIntBits(this.sOffset) : 0) + this.bOffset != 0.0f) ? Float.floatToIntBits(this.bOffset) : 0) + this.aOffset;
    }
    
    private float clamp(float n) {
        if (n < 0.0f) {
            n = 0.0f;
        }
        else if (n > 1.0f) {
            n = 1.0f;
        }
        return n;
    }
    
    private int clamp(int n) {
        if (n < 0) {
            n = 0;
        }
        else if (n > 255) {
            n = 255;
        }
        return n;
    }
    
    @Override
    public String toString() {
        final Color color = UIManager.getColor(this.uiDefaultParentName);
        final String string = "DerivedColor(color=" + this.getRed() + "," + this.getGreen() + "," + this.getBlue() + " parent=" + this.uiDefaultParentName + " offsets=" + this.getHueOffset() + "," + this.getSaturationOffset() + "," + this.getBrightnessOffset() + "," + this.getAlphaOffset();
        return (color == null) ? string : (string + " pColor=" + color.getRed() + "," + color.getGreen() + "," + color.getBlue());
    }
    
    static class UIResource extends DerivedColor implements javax.swing.plaf.UIResource
    {
        UIResource(final String s, final float n, final float n2, final float n3, final int n4) {
            super(s, n, n2, n3, n4);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof UIResource && super.equals(o);
        }
        
        @Override
        public int hashCode() {
            return super.hashCode() + 7;
        }
    }
}
