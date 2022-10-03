package java.awt.image;

import java.awt.Rectangle;
import java.util.Hashtable;

public class CropImageFilter extends ImageFilter
{
    int cropX;
    int cropY;
    int cropW;
    int cropH;
    
    public CropImageFilter(final int cropX, final int cropY, final int cropW, final int cropH) {
        this.cropX = cropX;
        this.cropY = cropY;
        this.cropW = cropW;
        this.cropH = cropH;
    }
    
    @Override
    public void setProperties(final Hashtable<?, ?> hashtable) {
        final Hashtable properties = (Hashtable)hashtable.clone();
        properties.put("croprect", new Rectangle(this.cropX, this.cropY, this.cropW, this.cropH));
        super.setProperties(properties);
    }
    
    @Override
    public void setDimensions(final int n, final int n2) {
        this.consumer.setDimensions(this.cropW, this.cropH);
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final byte[] array, final int n5, final int n6) {
        int cropX = n;
        if (cropX < this.cropX) {
            cropX = this.cropX;
        }
        int addWithoutOverflow = this.addWithoutOverflow(n, n3);
        if (addWithoutOverflow > this.cropX + this.cropW) {
            addWithoutOverflow = this.cropX + this.cropW;
        }
        int cropY = n2;
        if (cropY < this.cropY) {
            cropY = this.cropY;
        }
        int addWithoutOverflow2 = this.addWithoutOverflow(n2, n4);
        if (addWithoutOverflow2 > this.cropY + this.cropH) {
            addWithoutOverflow2 = this.cropY + this.cropH;
        }
        if (cropX >= addWithoutOverflow || cropY >= addWithoutOverflow2) {
            return;
        }
        this.consumer.setPixels(cropX - this.cropX, cropY - this.cropY, addWithoutOverflow - cropX, addWithoutOverflow2 - cropY, colorModel, array, n5 + (cropY - n2) * n6 + (cropX - n), n6);
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final int[] array, final int n5, final int n6) {
        int cropX = n;
        if (cropX < this.cropX) {
            cropX = this.cropX;
        }
        int addWithoutOverflow = this.addWithoutOverflow(n, n3);
        if (addWithoutOverflow > this.cropX + this.cropW) {
            addWithoutOverflow = this.cropX + this.cropW;
        }
        int cropY = n2;
        if (cropY < this.cropY) {
            cropY = this.cropY;
        }
        int addWithoutOverflow2 = this.addWithoutOverflow(n2, n4);
        if (addWithoutOverflow2 > this.cropY + this.cropH) {
            addWithoutOverflow2 = this.cropY + this.cropH;
        }
        if (cropX >= addWithoutOverflow || cropY >= addWithoutOverflow2) {
            return;
        }
        this.consumer.setPixels(cropX - this.cropX, cropY - this.cropY, addWithoutOverflow - cropX, addWithoutOverflow2 - cropY, colorModel, array, n5 + (cropY - n2) * n6 + (cropX - n), n6);
    }
    
    private int addWithoutOverflow(final int n, final int n2) {
        int n3 = n + n2;
        if (n > 0 && n2 > 0 && n3 < 0) {
            n3 = Integer.MAX_VALUE;
        }
        else if (n < 0 && n2 < 0 && n3 > 0) {
            n3 = Integer.MIN_VALUE;
        }
        return n3;
    }
}
