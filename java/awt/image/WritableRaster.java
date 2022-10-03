package java.awt.image;

import java.awt.Rectangle;
import java.awt.Point;

public class WritableRaster extends Raster
{
    protected WritableRaster(final SampleModel sampleModel, final Point point) {
        this(sampleModel, sampleModel.createDataBuffer(), new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    protected WritableRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Point point) {
        this(sampleModel, dataBuffer, new Rectangle(point.x, point.y, sampleModel.getWidth(), sampleModel.getHeight()), point, null);
    }
    
    protected WritableRaster(final SampleModel sampleModel, final DataBuffer dataBuffer, final Rectangle rectangle, final Point point, final WritableRaster writableRaster) {
        super(sampleModel, dataBuffer, rectangle, point, writableRaster);
    }
    
    public WritableRaster getWritableParent() {
        return (WritableRaster)this.parent;
    }
    
    public WritableRaster createWritableTranslatedChild(final int n, final int n2) {
        return this.createWritableChild(this.minX, this.minY, this.width, this.height, n, n2, null);
    }
    
    public WritableRaster createWritableChild(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int[] array) {
        if (n < this.minX) {
            throw new RasterFormatException("parentX lies outside raster");
        }
        if (n2 < this.minY) {
            throw new RasterFormatException("parentY lies outside raster");
        }
        if (n + n3 < n || n + n3 > this.width + this.minX) {
            throw new RasterFormatException("(parentX + width) is outside raster");
        }
        if (n2 + n4 < n2 || n2 + n4 > this.height + this.minY) {
            throw new RasterFormatException("(parentY + height) is outside raster");
        }
        SampleModel sampleModel;
        if (array != null) {
            sampleModel = this.sampleModel.createSubsetSampleModel(array);
        }
        else {
            sampleModel = this.sampleModel;
        }
        return new WritableRaster(sampleModel, this.getDataBuffer(), new Rectangle(n5, n6, n3, n4), new Point(this.sampleModelTranslateX + (n5 - n), this.sampleModelTranslateY + (n6 - n2)), this);
    }
    
    public void setDataElements(final int n, final int n2, final Object o) {
        this.sampleModel.setDataElements(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, o, this.dataBuffer);
    }
    
    public void setDataElements(final int n, final int n2, final Raster raster) {
        final int n3 = n + raster.getMinX();
        final int n4 = n2 + raster.getMinY();
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        if (n3 < this.minX || n4 < this.minY || n3 + width > this.minX + this.width || n4 + height > this.minY + this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int minX = raster.getMinX();
        final int minY = raster.getMinY();
        Object dataElements = null;
        for (int i = 0; i < height; ++i) {
            dataElements = raster.getDataElements(minX, minY + i, width, 1, dataElements);
            this.setDataElements(n3, n4 + i, width, 1, dataElements);
        }
    }
    
    public void setDataElements(final int n, final int n2, final int n3, final int n4, final Object o) {
        this.sampleModel.setDataElements(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, o, this.dataBuffer);
    }
    
    public void setRect(final Raster raster) {
        this.setRect(0, 0, raster);
    }
    
    public void setRect(final int n, final int n2, final Raster raster) {
        int width = raster.getWidth();
        int height = raster.getHeight();
        int minX = raster.getMinX();
        int minY = raster.getMinY();
        int minX2 = n + minX;
        int minY2 = n2 + minY;
        if (minX2 < this.minX) {
            final int n3 = this.minX - minX2;
            width -= n3;
            minX += n3;
            minX2 = this.minX;
        }
        if (minY2 < this.minY) {
            final int n4 = this.minY - minY2;
            height -= n4;
            minY += n4;
            minY2 = this.minY;
        }
        if (minX2 + width > this.minX + this.width) {
            width = this.minX + this.width - minX2;
        }
        if (minY2 + height > this.minY + this.height) {
            height = this.minY + this.height - minY2;
        }
        if (width <= 0 || height <= 0) {
            return;
        }
        switch (raster.getSampleModel().getDataType()) {
            case 0:
            case 1:
            case 2:
            case 3: {
                int[] pixels = null;
                for (int i = 0; i < height; ++i) {
                    pixels = raster.getPixels(minX, minY + i, width, 1, pixels);
                    this.setPixels(minX2, minY2 + i, width, 1, pixels);
                }
                break;
            }
            case 4: {
                float[] pixels2 = null;
                for (int j = 0; j < height; ++j) {
                    pixels2 = raster.getPixels(minX, minY + j, width, 1, pixels2);
                    this.setPixels(minX2, minY2 + j, width, 1, pixels2);
                }
                break;
            }
            case 5: {
                double[] pixels3 = null;
                for (int k = 0; k < height; ++k) {
                    pixels3 = raster.getPixels(minX, minY + k, width, 1, pixels3);
                    this.setPixels(minX2, minY2 + k, width, 1, pixels3);
                }
                break;
            }
        }
    }
    
    public void setPixel(final int n, final int n2, final int[] array) {
        this.sampleModel.setPixel(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, array, this.dataBuffer);
    }
    
    public void setPixel(final int n, final int n2, final float[] array) {
        this.sampleModel.setPixel(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, array, this.dataBuffer);
    }
    
    public void setPixel(final int n, final int n2, final double[] array) {
        this.sampleModel.setPixel(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, array, this.dataBuffer);
    }
    
    public void setPixels(final int n, final int n2, final int n3, final int n4, final int[] array) {
        this.sampleModel.setPixels(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, array, this.dataBuffer);
    }
    
    public void setPixels(final int n, final int n2, final int n3, final int n4, final float[] array) {
        this.sampleModel.setPixels(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, array, this.dataBuffer);
    }
    
    public void setPixels(final int n, final int n2, final int n3, final int n4, final double[] array) {
        this.sampleModel.setPixels(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, array, this.dataBuffer);
    }
    
    public void setSample(final int n, final int n2, final int n3, final int n4) {
        this.sampleModel.setSample(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, this.dataBuffer);
    }
    
    public void setSample(final int n, final int n2, final int n3, final float n4) {
        this.sampleModel.setSample(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, this.dataBuffer);
    }
    
    public void setSample(final int n, final int n2, final int n3, final double n4) {
        this.sampleModel.setSample(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, this.dataBuffer);
    }
    
    public void setSamples(final int n, final int n2, final int n3, final int n4, final int n5, final int[] array) {
        this.sampleModel.setSamples(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, n5, array, this.dataBuffer);
    }
    
    public void setSamples(final int n, final int n2, final int n3, final int n4, final int n5, final float[] array) {
        this.sampleModel.setSamples(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, n5, array, this.dataBuffer);
    }
    
    public void setSamples(final int n, final int n2, final int n3, final int n4, final int n5, final double[] array) {
        this.sampleModel.setSamples(n - this.sampleModelTranslateX, n2 - this.sampleModelTranslateY, n3, n4, n5, array, this.dataBuffer);
    }
}
