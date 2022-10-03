package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.DocAttribute;

public final class MediaPrintableArea implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
    private int x;
    private int y;
    private int w;
    private int h;
    private int units;
    private static final long serialVersionUID = -1597171464050795793L;
    public static final int INCH = 25400;
    public static final int MM = 1000;
    
    public MediaPrintableArea(final float n, final float n2, final float n3, final float n4, final int n5) {
        if (n < 0.0 || n2 < 0.0 || n3 <= 0.0 || n4 <= 0.0 || n5 < 1) {
            throw new IllegalArgumentException("0 or negative value argument");
        }
        this.x = (int)(n * n5 + 0.5f);
        this.y = (int)(n2 * n5 + 0.5f);
        this.w = (int)(n3 * n5 + 0.5f);
        this.h = (int)(n4 * n5 + 0.5f);
    }
    
    public MediaPrintableArea(final int n, final int n2, final int n3, final int n4, final int n5) {
        if (n < 0 || n2 < 0 || n3 <= 0 || n4 <= 0 || n5 < 1) {
            throw new IllegalArgumentException("0 or negative value argument");
        }
        this.x = n * n5;
        this.y = n2 * n5;
        this.w = n3 * n5;
        this.h = n4 * n5;
    }
    
    public float[] getPrintableArea(final int n) {
        return new float[] { this.getX(n), this.getY(n), this.getWidth(n), this.getHeight(n) };
    }
    
    public float getX(final int n) {
        return convertFromMicrometers(this.x, n);
    }
    
    public float getY(final int n) {
        return convertFromMicrometers(this.y, n);
    }
    
    public float getWidth(final int n) {
        return convertFromMicrometers(this.w, n);
    }
    
    public float getHeight(final int n) {
        return convertFromMicrometers(this.h, n);
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = false;
        if (o instanceof MediaPrintableArea) {
            final MediaPrintableArea mediaPrintableArea = (MediaPrintableArea)o;
            if (this.x == mediaPrintableArea.x && this.y == mediaPrintableArea.y && this.w == mediaPrintableArea.w && this.h == mediaPrintableArea.h) {
                b = true;
            }
        }
        return b;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return MediaPrintableArea.class;
    }
    
    @Override
    public final String getName() {
        return "media-printable-area";
    }
    
    public String toString(final int n, String s) {
        if (s == null) {
            s = "";
        }
        final float[] printableArea = this.getPrintableArea(n);
        return "(" + printableArea[0] + "," + printableArea[1] + ")->(" + printableArea[2] + "," + printableArea[3] + ")" + s;
    }
    
    @Override
    public String toString() {
        return this.toString(1000, "mm");
    }
    
    @Override
    public int hashCode() {
        return this.x + 37 * this.y + 43 * this.w + 47 * this.h;
    }
    
    private static float convertFromMicrometers(final int n, final int n2) {
        if (n2 < 1) {
            throw new IllegalArgumentException("units is < 1");
        }
        return n / (float)n2;
    }
}
