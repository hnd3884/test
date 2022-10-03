package java.awt.image;

public abstract class RGBImageFilter extends ImageFilter
{
    protected ColorModel origmodel;
    protected ColorModel newmodel;
    protected boolean canFilterIndexColorModel;
    
    @Override
    public void setColorModel(final ColorModel colorModel) {
        if (this.canFilterIndexColorModel && colorModel instanceof IndexColorModel) {
            final IndexColorModel filterIndexColorModel = this.filterIndexColorModel((IndexColorModel)colorModel);
            this.substituteColorModel(colorModel, filterIndexColorModel);
            this.consumer.setColorModel(filterIndexColorModel);
        }
        else {
            this.consumer.setColorModel(ColorModel.getRGBdefault());
        }
    }
    
    public void substituteColorModel(final ColorModel origmodel, final ColorModel newmodel) {
        this.origmodel = origmodel;
        this.newmodel = newmodel;
    }
    
    public IndexColorModel filterIndexColorModel(final IndexColorModel indexColorModel) {
        final int mapSize = indexColorModel.getMapSize();
        final byte[] array = new byte[mapSize];
        final byte[] array2 = new byte[mapSize];
        final byte[] array3 = new byte[mapSize];
        final byte[] array4 = new byte[mapSize];
        indexColorModel.getReds(array);
        indexColorModel.getGreens(array2);
        indexColorModel.getBlues(array3);
        indexColorModel.getAlphas(array4);
        final int transparentPixel = indexColorModel.getTransparentPixel();
        boolean b = false;
        for (int i = 0; i < mapSize; ++i) {
            final int filterRGB = this.filterRGB(-1, -1, indexColorModel.getRGB(i));
            array4[i] = (byte)(filterRGB >> 24);
            if (array4[i] != -1 && i != transparentPixel) {
                b = true;
            }
            array[i] = (byte)(filterRGB >> 16);
            array2[i] = (byte)(filterRGB >> 8);
            array3[i] = (byte)(filterRGB >> 0);
        }
        if (b) {
            return new IndexColorModel(indexColorModel.getPixelSize(), mapSize, array, array2, array3, array4);
        }
        return new IndexColorModel(indexColorModel.getPixelSize(), mapSize, array, array2, array3, transparentPixel);
    }
    
    public void filterRGBPixels(final int n, final int n2, final int n3, final int n4, final int[] array, final int n5, final int n6) {
        int n7 = n5;
        for (int i = 0; i < n4; ++i) {
            for (int j = 0; j < n3; ++j) {
                array[n7] = this.filterRGB(n + j, n2 + i, array[n7]);
                ++n7;
            }
            n7 += n6 - n3;
        }
        this.consumer.setPixels(n, n2, n3, n4, ColorModel.getRGBdefault(), array, n5, n6);
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final byte[] array, final int n5, final int n6) {
        if (colorModel == this.origmodel) {
            this.consumer.setPixels(n, n2, n3, n4, this.newmodel, array, n5, n6);
        }
        else {
            final int[] array2 = new int[n3];
            int n7 = n5;
            for (int i = 0; i < n4; ++i) {
                for (int j = 0; j < n3; ++j) {
                    array2[j] = colorModel.getRGB(array[n7] & 0xFF);
                    ++n7;
                }
                n7 += n6 - n3;
                this.filterRGBPixels(n, n2 + i, n3, 1, array2, 0, n3);
            }
        }
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final int[] array, final int n5, final int n6) {
        if (colorModel == this.origmodel) {
            this.consumer.setPixels(n, n2, n3, n4, this.newmodel, array, n5, n6);
        }
        else {
            final int[] array2 = new int[n3];
            int n7 = n5;
            for (int i = 0; i < n4; ++i) {
                for (int j = 0; j < n3; ++j) {
                    array2[j] = colorModel.getRGB(array[n7]);
                    ++n7;
                }
                n7 += n6 - n3;
                this.filterRGBPixels(n, n2 + i, n3, 1, array2, 0, n3);
            }
        }
    }
    
    public abstract int filterRGB(final int p0, final int p1, final int p2);
}
