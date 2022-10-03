package sun.awt.image;

import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.ImageConsumer;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;

public class OffScreenImageSource implements ImageProducer
{
    BufferedImage image;
    int width;
    int height;
    Hashtable properties;
    private ImageConsumer theConsumer;
    
    public OffScreenImageSource(final BufferedImage image, final Hashtable properties) {
        this.image = image;
        if (properties != null) {
            this.properties = properties;
        }
        else {
            this.properties = new Hashtable();
        }
        this.width = image.getWidth();
        this.height = image.getHeight();
    }
    
    public OffScreenImageSource(final BufferedImage bufferedImage) {
        this(bufferedImage, null);
    }
    
    @Override
    public synchronized void addConsumer(final ImageConsumer theConsumer) {
        this.theConsumer = theConsumer;
        this.produce();
    }
    
    @Override
    public synchronized boolean isConsumer(final ImageConsumer imageConsumer) {
        return imageConsumer == this.theConsumer;
    }
    
    @Override
    public synchronized void removeConsumer(final ImageConsumer imageConsumer) {
        if (this.theConsumer == imageConsumer) {
            this.theConsumer = null;
        }
    }
    
    @Override
    public void startProduction(final ImageConsumer imageConsumer) {
        this.addConsumer(imageConsumer);
    }
    
    @Override
    public void requestTopDownLeftRightResend(final ImageConsumer imageConsumer) {
    }
    
    private void sendPixels() {
        final ColorModel colorModel = this.image.getColorModel();
        final WritableRaster raster = this.image.getRaster();
        final int numDataElements = raster.getNumDataElements();
        final int dataType = raster.getDataBuffer().getDataType();
        final int[] array = new int[this.width * numDataElements];
        boolean b = true;
        if (colorModel instanceof IndexColorModel) {
            final byte[] array2 = new byte[this.width];
            this.theConsumer.setColorModel(colorModel);
            if (raster instanceof ByteComponentRaster) {
                b = false;
                for (int i = 0; i < this.height; ++i) {
                    raster.getDataElements(0, i, this.width, 1, array2);
                    this.theConsumer.setPixels(0, i, this.width, 1, colorModel, array2, 0, this.width);
                }
            }
            else if (raster instanceof BytePackedRaster) {
                b = false;
                for (int j = 0; j < this.height; ++j) {
                    raster.getPixels(0, j, this.width, 1, array);
                    for (int k = 0; k < this.width; ++k) {
                        array2[k] = (byte)array[k];
                    }
                    this.theConsumer.setPixels(0, j, this.width, 1, colorModel, array2, 0, this.width);
                }
            }
            else if (dataType == 2 || dataType == 3) {
                b = false;
                for (int l = 0; l < this.height; ++l) {
                    raster.getPixels(0, l, this.width, 1, array);
                    this.theConsumer.setPixels(0, l, this.width, 1, colorModel, array, 0, this.width);
                }
            }
        }
        else if (colorModel instanceof DirectColorModel) {
            this.theConsumer.setColorModel(colorModel);
            b = false;
            switch (dataType) {
                case 3: {
                    for (int n = 0; n < this.height; ++n) {
                        raster.getDataElements(0, n, this.width, 1, array);
                        this.theConsumer.setPixels(0, n, this.width, 1, colorModel, array, 0, this.width);
                    }
                    break;
                }
                case 0: {
                    final byte[] array3 = new byte[this.width];
                    for (int n2 = 0; n2 < this.height; ++n2) {
                        raster.getDataElements(0, n2, this.width, 1, array3);
                        for (int n3 = 0; n3 < this.width; ++n3) {
                            array[n3] = (array3[n3] & 0xFF);
                        }
                        this.theConsumer.setPixels(0, n2, this.width, 1, colorModel, array, 0, this.width);
                    }
                    break;
                }
                case 1: {
                    final short[] array4 = new short[this.width];
                    for (int n4 = 0; n4 < this.height; ++n4) {
                        raster.getDataElements(0, n4, this.width, 1, array4);
                        for (int n5 = 0; n5 < this.width; ++n5) {
                            array[n5] = (array4[n5] & 0xFFFF);
                        }
                        this.theConsumer.setPixels(0, n4, this.width, 1, colorModel, array, 0, this.width);
                    }
                    break;
                }
                default: {
                    b = true;
                    break;
                }
            }
        }
        if (b) {
            final ColorModel rgBdefault = ColorModel.getRGBdefault();
            this.theConsumer.setColorModel(rgBdefault);
            for (int n6 = 0; n6 < this.height; ++n6) {
                for (int n7 = 0; n7 < this.width; ++n7) {
                    array[n7] = this.image.getRGB(n7, n6);
                }
                this.theConsumer.setPixels(0, n6, this.width, 1, rgBdefault, array, 0, this.width);
            }
        }
    }
    
    private void produce() {
        try {
            this.theConsumer.setDimensions(this.image.getWidth(), this.image.getHeight());
            this.theConsumer.setProperties(this.properties);
            this.sendPixels();
            this.theConsumer.imageComplete(2);
            this.theConsumer.imageComplete(3);
        }
        catch (final NullPointerException ex) {
            if (this.theConsumer != null) {
                this.theConsumer.imageComplete(1);
            }
        }
    }
}
