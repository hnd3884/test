package java.awt.print;

public class PageFormat implements Cloneable
{
    public static final int LANDSCAPE = 0;
    public static final int PORTRAIT = 1;
    public static final int REVERSE_LANDSCAPE = 2;
    private Paper mPaper;
    private int mOrientation;
    
    public PageFormat() {
        this.mOrientation = 1;
        this.mPaper = new Paper();
    }
    
    public Object clone() {
        PageFormat pageFormat;
        try {
            pageFormat = (PageFormat)super.clone();
            pageFormat.mPaper = (Paper)this.mPaper.clone();
        }
        catch (final CloneNotSupportedException ex) {
            ex.printStackTrace();
            pageFormat = null;
        }
        return pageFormat;
    }
    
    public double getWidth() {
        double n;
        if (this.getOrientation() == 1) {
            n = this.mPaper.getWidth();
        }
        else {
            n = this.mPaper.getHeight();
        }
        return n;
    }
    
    public double getHeight() {
        double n;
        if (this.getOrientation() == 1) {
            n = this.mPaper.getHeight();
        }
        else {
            n = this.mPaper.getWidth();
        }
        return n;
    }
    
    public double getImageableX() {
        double n = 0.0;
        switch (this.getOrientation()) {
            case 0: {
                n = this.mPaper.getHeight() - (this.mPaper.getImageableY() + this.mPaper.getImageableHeight());
                break;
            }
            case 1: {
                n = this.mPaper.getImageableX();
                break;
            }
            case 2: {
                n = this.mPaper.getImageableY();
                break;
            }
            default: {
                throw new InternalError("unrecognized orientation");
            }
        }
        return n;
    }
    
    public double getImageableY() {
        double n = 0.0;
        switch (this.getOrientation()) {
            case 0: {
                n = this.mPaper.getImageableX();
                break;
            }
            case 1: {
                n = this.mPaper.getImageableY();
                break;
            }
            case 2: {
                n = this.mPaper.getWidth() - (this.mPaper.getImageableX() + this.mPaper.getImageableWidth());
                break;
            }
            default: {
                throw new InternalError("unrecognized orientation");
            }
        }
        return n;
    }
    
    public double getImageableWidth() {
        double n;
        if (this.getOrientation() == 1) {
            n = this.mPaper.getImageableWidth();
        }
        else {
            n = this.mPaper.getImageableHeight();
        }
        return n;
    }
    
    public double getImageableHeight() {
        double n;
        if (this.getOrientation() == 1) {
            n = this.mPaper.getImageableHeight();
        }
        else {
            n = this.mPaper.getImageableWidth();
        }
        return n;
    }
    
    public Paper getPaper() {
        return (Paper)this.mPaper.clone();
    }
    
    public void setPaper(final Paper paper) {
        this.mPaper = (Paper)paper.clone();
    }
    
    public void setOrientation(final int mOrientation) throws IllegalArgumentException {
        if (0 <= mOrientation && mOrientation <= 2) {
            this.mOrientation = mOrientation;
            return;
        }
        throw new IllegalArgumentException();
    }
    
    public int getOrientation() {
        return this.mOrientation;
    }
    
    public double[] getMatrix() {
        final double[] array = new double[6];
        switch (this.mOrientation) {
            case 0: {
                array[0] = 0.0;
                array[1] = -1.0;
                array[2] = 1.0;
                array[4] = (array[3] = 0.0);
                array[5] = this.mPaper.getHeight();
                break;
            }
            case 1: {
                array[0] = 1.0;
                array[2] = (array[1] = 0.0);
                array[3] = 1.0;
                array[5] = (array[4] = 0.0);
                break;
            }
            case 2: {
                array[0] = 0.0;
                array[1] = 1.0;
                array[2] = -1.0;
                array[3] = 0.0;
                array[4] = this.mPaper.getWidth();
                array[5] = 0.0;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return array;
    }
}
