package sun.java2d.loops;

import sun.font.FontStrike;
import sun.font.Font2D;
import java.awt.Font;

public class FontInfo implements Cloneable
{
    public Font font;
    public Font2D font2D;
    public FontStrike fontStrike;
    public double[] devTx;
    public double[] glyphTx;
    public int pixelHeight;
    public float originX;
    public float originY;
    public int aaHint;
    public boolean lcdRGBOrder;
    public boolean lcdSubPixPos;
    
    public String mtx(final double[] array) {
        return "[" + array[0] + ", " + array[1] + ", " + array[2] + ", " + array[3] + "]";
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return "FontInfo[font=" + this.font + ", devTx=" + this.mtx(this.devTx) + ", glyphTx=" + this.mtx(this.glyphTx) + ", pixelHeight=" + this.pixelHeight + ", origin=(" + this.originX + "," + this.originY + "), aaHint=" + this.aaHint + ", lcdRGBOrder=" + (this.lcdRGBOrder ? "RGB" : "BGR") + "lcdSubPixPos=" + this.lcdSubPixPos + "]";
    }
}
