package java.awt.image;

import java.util.Hashtable;

public class ReplicateScaleFilter extends ImageFilter
{
    protected int srcWidth;
    protected int srcHeight;
    protected int destWidth;
    protected int destHeight;
    protected int[] srcrows;
    protected int[] srccols;
    protected Object outpixbuf;
    
    public ReplicateScaleFilter(final int destWidth, final int destHeight) {
        if (destWidth == 0 || destHeight == 0) {
            throw new IllegalArgumentException("Width (" + destWidth + ") and height (" + destHeight + ") must be non-zero");
        }
        this.destWidth = destWidth;
        this.destHeight = destHeight;
    }
    
    @Override
    public void setProperties(final Hashtable<?, ?> hashtable) {
        final Hashtable properties = (Hashtable)hashtable.clone();
        final String s = "rescale";
        String s2 = this.destWidth + "x" + this.destHeight;
        final Object value = properties.get(s);
        if (value != null && value instanceof String) {
            s2 = (String)value + ", " + s2;
        }
        properties.put(s, s2);
        super.setProperties(properties);
    }
    
    @Override
    public void setDimensions(final int srcWidth, final int srcHeight) {
        this.srcWidth = srcWidth;
        this.srcHeight = srcHeight;
        if (this.destWidth < 0) {
            if (this.destHeight < 0) {
                this.destWidth = this.srcWidth;
                this.destHeight = this.srcHeight;
            }
            else {
                this.destWidth = this.srcWidth * this.destHeight / this.srcHeight;
            }
        }
        else if (this.destHeight < 0) {
            this.destHeight = this.srcHeight * this.destWidth / this.srcWidth;
        }
        this.consumer.setDimensions(this.destWidth, this.destHeight);
    }
    
    private void calculateMaps() {
        this.srcrows = new int[this.destHeight + 1];
        for (int i = 0; i <= this.destHeight; ++i) {
            this.srcrows[i] = (2 * i * this.srcHeight + this.srcHeight) / (2 * this.destHeight);
        }
        this.srccols = new int[this.destWidth + 1];
        for (int j = 0; j <= this.destWidth; ++j) {
            this.srccols[j] = (2 * j * this.srcWidth + this.srcWidth) / (2 * this.destWidth);
        }
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final byte[] array, final int n5, final int n6) {
        if (this.srcrows == null || this.srccols == null) {
            this.calculateMaps();
        }
        final int n7 = (2 * n * this.destWidth + this.srcWidth - 1) / (2 * this.srcWidth);
        final int n8 = (2 * n2 * this.destHeight + this.srcHeight - 1) / (2 * this.srcHeight);
        byte[] outpixbuf;
        if (this.outpixbuf != null && this.outpixbuf instanceof byte[]) {
            outpixbuf = (byte[])this.outpixbuf;
        }
        else {
            outpixbuf = new byte[this.destWidth];
            this.outpixbuf = outpixbuf;
        }
        int n10;
        for (int n9 = n8; (n10 = this.srcrows[n9]) < n2 + n4; ++n9) {
            final int n11 = n5 + n6 * (n10 - n2);
            int n12;
            int n13;
            for (n12 = n7; (n13 = this.srccols[n12]) < n + n3; ++n12) {
                outpixbuf[n12] = array[n11 + n13 - n];
            }
            if (n12 > n7) {
                this.consumer.setPixels(n7, n9, n12 - n7, 1, colorModel, outpixbuf, n7, this.destWidth);
            }
        }
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final int[] array, final int n5, final int n6) {
        if (this.srcrows == null || this.srccols == null) {
            this.calculateMaps();
        }
        final int n7 = (2 * n * this.destWidth + this.srcWidth - 1) / (2 * this.srcWidth);
        final int n8 = (2 * n2 * this.destHeight + this.srcHeight - 1) / (2 * this.srcHeight);
        int[] outpixbuf;
        if (this.outpixbuf != null && this.outpixbuf instanceof int[]) {
            outpixbuf = (int[])this.outpixbuf;
        }
        else {
            outpixbuf = new int[this.destWidth];
            this.outpixbuf = outpixbuf;
        }
        int n10;
        for (int n9 = n8; (n10 = this.srcrows[n9]) < n2 + n4; ++n9) {
            final int n11 = n5 + n6 * (n10 - n2);
            int n12;
            int n13;
            for (n12 = n7; (n13 = this.srccols[n12]) < n + n3; ++n12) {
                outpixbuf[n12] = array[n11 + n13 - n];
            }
            if (n12 > n7) {
                this.consumer.setPixels(n7, n9, n12 - n7, 1, colorModel, outpixbuf, n7, this.destWidth);
            }
        }
    }
}
