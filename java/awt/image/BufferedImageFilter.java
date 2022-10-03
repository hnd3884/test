package java.awt.image;

import java.util.Hashtable;
import java.awt.Point;

public class BufferedImageFilter extends ImageFilter implements Cloneable
{
    BufferedImageOp bufferedImageOp;
    ColorModel model;
    int width;
    int height;
    byte[] bytePixels;
    int[] intPixels;
    
    public BufferedImageFilter(final BufferedImageOp bufferedImageOp) {
        if (bufferedImageOp == null) {
            throw new NullPointerException("Operation cannot be null");
        }
        this.bufferedImageOp = bufferedImageOp;
    }
    
    public BufferedImageOp getBufferedImageOp() {
        return this.bufferedImageOp;
    }
    
    @Override
    public void setDimensions(final int width, final int height) {
        if (width <= 0 || height <= 0) {
            this.imageComplete(3);
            return;
        }
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void setColorModel(final ColorModel model) {
        this.model = model;
    }
    
    private void convertToRGB() {
        final int n = this.width * this.height;
        final int[] intPixels = new int[n];
        if (this.bytePixels != null) {
            for (int i = 0; i < n; ++i) {
                intPixels[i] = this.model.getRGB(this.bytePixels[i] & 0xFF);
            }
        }
        else if (this.intPixels != null) {
            for (int j = 0; j < n; ++j) {
                intPixels[j] = this.model.getRGB(this.intPixels[j]);
            }
        }
        this.bytePixels = null;
        this.intPixels = intPixels;
        this.model = ColorModel.getRGBdefault();
    }
    
    @Override
    public void setPixels(int n, int n2, int n3, int n4, final ColorModel model, final byte[] array, int n5, final int n6) {
        if (n3 < 0 || n4 < 0) {
            throw new IllegalArgumentException("Width (" + n3 + ") and height (" + n4 + ") must be > 0");
        }
        if (n3 == 0 || n4 == 0) {
            return;
        }
        if (n2 < 0) {
            final int n7 = -n2;
            if (n7 >= n4) {
                return;
            }
            n5 += n6 * n7;
            n2 += n7;
            n4 -= n7;
        }
        if (n2 + n4 > this.height) {
            n4 = this.height - n2;
            if (n4 <= 0) {
                return;
            }
        }
        if (n < 0) {
            final int n8 = -n;
            if (n8 >= n3) {
                return;
            }
            n5 += n8;
            n += n8;
            n3 -= n8;
        }
        if (n + n3 > this.width) {
            n3 = this.width - n;
            if (n3 <= 0) {
                return;
            }
        }
        int n9 = n2 * this.width + n;
        if (this.intPixels == null) {
            if (this.bytePixels == null) {
                this.bytePixels = new byte[this.width * this.height];
                this.model = model;
            }
            else if (this.model != model) {
                this.convertToRGB();
            }
            if (this.bytePixels != null) {
                for (int i = n4; i > 0; --i) {
                    System.arraycopy(array, n5, this.bytePixels, n9, n3);
                    n5 += n6;
                    n9 += this.width;
                }
            }
        }
        if (this.intPixels != null) {
            final int n10 = this.width - n3;
            final int n11 = n6 - n3;
            for (int j = n4; j > 0; --j) {
                for (int k = n3; k > 0; --k) {
                    this.intPixels[n9++] = model.getRGB(array[n5++] & 0xFF);
                }
                n5 += n11;
                n9 += n10;
            }
        }
    }
    
    @Override
    public void setPixels(int n, int n2, int n3, int n4, final ColorModel model, final int[] array, int n5, final int n6) {
        if (n3 < 0 || n4 < 0) {
            throw new IllegalArgumentException("Width (" + n3 + ") and height (" + n4 + ") must be > 0");
        }
        if (n3 == 0 || n4 == 0) {
            return;
        }
        if (n2 < 0) {
            final int n7 = -n2;
            if (n7 >= n4) {
                return;
            }
            n5 += n6 * n7;
            n2 += n7;
            n4 -= n7;
        }
        if (n2 + n4 > this.height) {
            n4 = this.height - n2;
            if (n4 <= 0) {
                return;
            }
        }
        if (n < 0) {
            final int n8 = -n;
            if (n8 >= n3) {
                return;
            }
            n5 += n8;
            n += n8;
            n3 -= n8;
        }
        if (n + n3 > this.width) {
            n3 = this.width - n;
            if (n3 <= 0) {
                return;
            }
        }
        if (this.intPixels == null) {
            if (this.bytePixels == null) {
                this.intPixels = new int[this.width * this.height];
                this.model = model;
            }
            else {
                this.convertToRGB();
            }
        }
        int n9 = n2 * this.width + n;
        if (this.model == model) {
            for (int i = n4; i > 0; --i) {
                System.arraycopy(array, n5, this.intPixels, n9, n3);
                n5 += n6;
                n9 += this.width;
            }
        }
        else {
            if (this.model != ColorModel.getRGBdefault()) {
                this.convertToRGB();
            }
            final int n10 = this.width - n3;
            final int n11 = n6 - n3;
            for (int j = n4; j > 0; --j) {
                for (int k = n3; k > 0; --k) {
                    this.intPixels[n9++] = model.getRGB(array[n5++]);
                }
                n5 += n11;
                n9 += n10;
            }
        }
    }
    
    @Override
    public void imageComplete(final int n) {
        switch (n) {
            case 1:
            case 4: {
                this.model = null;
                this.width = -1;
                this.height = -1;
                this.intPixels = null;
                this.bytePixels = null;
                break;
            }
            case 2:
            case 3: {
                if (this.width <= 0) {
                    break;
                }
                if (this.height <= 0) {
                    break;
                }
                WritableRaster writableRaster;
                if (this.model instanceof DirectColorModel) {
                    if (this.intPixels == null) {
                        break;
                    }
                    writableRaster = this.createDCMraster();
                }
                else if (this.model instanceof IndexColorModel) {
                    final int[] array = { 0 };
                    if (this.bytePixels == null) {
                        break;
                    }
                    writableRaster = Raster.createInterleavedRaster(new DataBufferByte(this.bytePixels, this.width * this.height), this.width, this.height, this.width, 1, array, null);
                }
                else {
                    this.convertToRGB();
                    if (this.intPixels == null) {
                        break;
                    }
                    writableRaster = this.createDCMraster();
                }
                final BufferedImage filter = this.bufferedImageOp.filter(new BufferedImage(this.model, writableRaster, this.model.isAlphaPremultiplied(), null), null);
                final WritableRaster raster = filter.getRaster();
                final ColorModel colorModel = filter.getColorModel();
                final int width = raster.getWidth();
                final int height = raster.getHeight();
                this.consumer.setDimensions(width, height);
                this.consumer.setColorModel(colorModel);
                if (colorModel instanceof DirectColorModel) {
                    this.consumer.setPixels(0, 0, width, height, colorModel, ((DataBufferInt)raster.getDataBuffer()).getData(), 0, width);
                    break;
                }
                if (colorModel instanceof IndexColorModel) {
                    this.consumer.setPixels(0, 0, width, height, colorModel, ((DataBufferByte)raster.getDataBuffer()).getData(), 0, width);
                    break;
                }
                throw new InternalError("Unknown color model " + colorModel);
            }
        }
        this.consumer.imageComplete(n);
    }
    
    private final WritableRaster createDCMraster() {
        final DirectColorModel directColorModel = (DirectColorModel)this.model;
        final int hasAlpha = this.model.hasAlpha() ? 1 : 0;
        final int[] array = new int[3 + hasAlpha];
        array[0] = directColorModel.getRedMask();
        array[1] = directColorModel.getGreenMask();
        array[2] = directColorModel.getBlueMask();
        if (hasAlpha != 0) {
            array[3] = directColorModel.getAlphaMask();
        }
        return Raster.createPackedRaster(new DataBufferInt(this.intPixels, this.width * this.height), this.width, this.height, this.width, array, null);
    }
}
