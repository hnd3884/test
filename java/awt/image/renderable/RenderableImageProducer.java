package java.awt.image.renderable;

import java.util.Enumeration;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.util.Vector;
import java.awt.image.ImageProducer;

public class RenderableImageProducer implements ImageProducer, Runnable
{
    RenderableImage rdblImage;
    RenderContext rc;
    Vector ics;
    
    public RenderableImageProducer(final RenderableImage rdblImage, final RenderContext rc) {
        this.ics = new Vector();
        this.rdblImage = rdblImage;
        this.rc = rc;
    }
    
    public synchronized void setRenderContext(final RenderContext rc) {
        this.rc = rc;
    }
    
    @Override
    public synchronized void addConsumer(final ImageConsumer imageConsumer) {
        if (!this.ics.contains(imageConsumer)) {
            this.ics.addElement(imageConsumer);
        }
    }
    
    @Override
    public synchronized boolean isConsumer(final ImageConsumer imageConsumer) {
        return this.ics.contains(imageConsumer);
    }
    
    @Override
    public synchronized void removeConsumer(final ImageConsumer imageConsumer) {
        this.ics.removeElement(imageConsumer);
    }
    
    @Override
    public synchronized void startProduction(final ImageConsumer imageConsumer) {
        this.addConsumer(imageConsumer);
        new Thread(this, "RenderableImageProducer Thread").start();
    }
    
    @Override
    public void requestTopDownLeftRightResend(final ImageConsumer imageConsumer) {
    }
    
    @Override
    public void run() {
        RenderedImage renderedImage;
        if (this.rc != null) {
            renderedImage = this.rdblImage.createRendering(this.rc);
        }
        else {
            renderedImage = this.rdblImage.createDefaultRendering();
        }
        ColorModel colorModel = renderedImage.getColorModel();
        final Raster data = renderedImage.getData();
        final SampleModel sampleModel = data.getSampleModel();
        final DataBuffer dataBuffer = data.getDataBuffer();
        if (colorModel == null) {
            colorModel = ColorModel.getRGBdefault();
        }
        data.getMinX();
        data.getMinY();
        final int width = data.getWidth();
        final int height = data.getHeight();
        final Enumeration elements = this.ics.elements();
        while (elements.hasMoreElements()) {
            final ImageConsumer imageConsumer = (ImageConsumer)elements.nextElement();
            imageConsumer.setDimensions(width, height);
            imageConsumer.setHints(30);
        }
        final int[] array = new int[width];
        final int[] array2 = new int[sampleModel.getNumBands()];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                sampleModel.getPixel(j, i, array2, dataBuffer);
                array[j] = colorModel.getDataElement(array2, 0);
            }
            final Enumeration elements2 = this.ics.elements();
            while (elements2.hasMoreElements()) {
                ((ImageConsumer)elements2.nextElement()).setPixels(0, i, width, 1, colorModel, array, 0, width);
            }
        }
        final Enumeration elements3 = this.ics.elements();
        while (elements3.hasMoreElements()) {
            ((ImageConsumer)elements3.nextElement()).imageComplete(3);
        }
    }
}
