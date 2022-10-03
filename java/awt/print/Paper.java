package java.awt.print;

import java.awt.geom.Rectangle2D;

public class Paper implements Cloneable
{
    private static final int INCH = 72;
    private static final double LETTER_WIDTH = 612.0;
    private static final double LETTER_HEIGHT = 792.0;
    private double mHeight;
    private double mWidth;
    private Rectangle2D mImageableArea;
    
    public Paper() {
        this.mHeight = 792.0;
        this.mWidth = 612.0;
        this.mImageableArea = new Rectangle2D.Double(72.0, 72.0, this.mWidth - 144.0, this.mHeight - 144.0);
    }
    
    public Object clone() {
        Paper paper;
        try {
            paper = (Paper)super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            ex.printStackTrace();
            paper = null;
        }
        return paper;
    }
    
    public double getHeight() {
        return this.mHeight;
    }
    
    public void setSize(final double mWidth, final double mHeight) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }
    
    public double getWidth() {
        return this.mWidth;
    }
    
    public void setImageableArea(final double n, final double n2, final double n3, final double n4) {
        this.mImageableArea = new Rectangle2D.Double(n, n2, n3, n4);
    }
    
    public double getImageableX() {
        return this.mImageableArea.getX();
    }
    
    public double getImageableY() {
        return this.mImageableArea.getY();
    }
    
    public double getImageableWidth() {
        return this.mImageableArea.getWidth();
    }
    
    public double getImageableHeight() {
        return this.mImageableArea.getHeight();
    }
}
