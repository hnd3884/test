package sun.awt;

import java.awt.Graphics;
import java.util.Hashtable;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.color.ColorSpace;
import sun.awt.image.ImageRepresentation;
import java.awt.image.ImageObserver;
import sun.awt.image.ToolkitImage;
import java.util.Arrays;
import java.awt.Image;

public class IconInfo
{
    private int[] intIconData;
    private long[] longIconData;
    private Image image;
    private final int width;
    private final int height;
    private int scaledWidth;
    private int scaledHeight;
    private int rawLength;
    
    public IconInfo(final int[] array) {
        this.intIconData = (int[])((null == array) ? null : Arrays.copyOf(array, array.length));
        this.width = array[0];
        this.height = array[1];
        this.scaledWidth = this.width;
        this.scaledHeight = this.height;
        this.rawLength = this.width * this.height + 2;
    }
    
    public IconInfo(final long[] array) {
        this.longIconData = (long[])((null == array) ? null : Arrays.copyOf(array, array.length));
        this.width = (int)array[0];
        this.height = (int)array[1];
        this.scaledWidth = this.width;
        this.scaledHeight = this.height;
        this.rawLength = this.width * this.height + 2;
    }
    
    public IconInfo(final Image image) {
        this.image = image;
        if (image instanceof ToolkitImage) {
            final ImageRepresentation imageRep = ((ToolkitImage)image).getImageRep();
            imageRep.reconstruct(32);
            this.width = imageRep.getWidth();
            this.height = imageRep.getHeight();
        }
        else {
            this.width = image.getWidth(null);
            this.height = image.getHeight(null);
        }
        this.scaledWidth = this.width;
        this.scaledHeight = this.height;
        this.rawLength = this.width * this.height + 2;
    }
    
    public void setScaledSize(final int scaledWidth, final int scaledHeight) {
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;
        this.rawLength = scaledWidth * scaledHeight + 2;
    }
    
    public boolean isValid() {
        return this.width > 0 && this.height > 0;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public String toString() {
        return "IconInfo[w=" + this.width + ",h=" + this.height + ",sw=" + this.scaledWidth + ",sh=" + this.scaledHeight + "]";
    }
    
    public int getRawLength() {
        return this.rawLength;
    }
    
    public int[] getIntData() {
        if (this.intIconData == null) {
            if (this.longIconData != null) {
                this.intIconData = longArrayToIntArray(this.longIconData);
            }
            else if (this.image != null) {
                this.intIconData = imageToIntArray(this.image, this.scaledWidth, this.scaledHeight);
            }
        }
        return this.intIconData;
    }
    
    public long[] getLongData() {
        if (this.longIconData == null) {
            if (this.intIconData != null) {
                this.longIconData = intArrayToLongArray(this.intIconData);
            }
            else if (this.image != null) {
                this.longIconData = intArrayToLongArray(imageToIntArray(this.image, this.scaledWidth, this.scaledHeight));
            }
        }
        return this.longIconData;
    }
    
    public Image getImage() {
        if (this.image == null) {
            if (this.intIconData != null) {
                this.image = intArrayToImage(this.intIconData);
            }
            else if (this.longIconData != null) {
                this.image = intArrayToImage(longArrayToIntArray(this.longIconData));
            }
        }
        return this.image;
    }
    
    private static int[] longArrayToIntArray(final long[] array) {
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (int)array[i];
        }
        return array2;
    }
    
    private static long[] intArrayToLongArray(final int[] array) {
        final long[] array2 = new long[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[i];
        }
        return array2;
    }
    
    static Image intArrayToImage(final int[] array) {
        return new BufferedImage(new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, false, 3), Raster.createPackedRaster(new DataBufferInt(array, array.length - 2, 2), array[0], array[1], array[0], new int[] { 16711680, 65280, 255, -16777216 }, null), false, null);
    }
    
    static int[] imageToIntArray(final Image image, final int n, final int n2) {
        if (n <= 0 || n2 <= 0) {
            return null;
        }
        final DirectColorModel directColorModel = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, false, 3);
        final DataBufferInt dataBufferInt = new DataBufferInt(n * n2);
        final Graphics graphics = new BufferedImage(directColorModel, Raster.createPackedRaster(dataBufferInt, n, n2, n, new int[] { 16711680, 65280, 255, -16777216 }, null), false, null).getGraphics();
        graphics.drawImage(image, 0, 0, n, n2, null);
        graphics.dispose();
        final int[] data = dataBufferInt.getData();
        final int[] array = new int[n * n2 + 2];
        array[0] = n;
        System.arraycopy(data, 0, array, 2, n * (array[1] = n2));
        return array;
    }
}
