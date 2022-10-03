package javax.imageio.spi;

import java.io.IOException;
import javax.imageio.ImageWriter;
import java.awt.image.RenderedImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageOutputStream;

public abstract class ImageWriterSpi extends ImageReaderWriterSpi
{
    @Deprecated
    public static final Class[] STANDARD_OUTPUT_TYPE;
    protected Class[] outputTypes;
    protected String[] readerSpiNames;
    private Class writerClass;
    
    protected ImageWriterSpi() {
        this.outputTypes = null;
        this.readerSpiNames = null;
        this.writerClass = null;
    }
    
    public ImageWriterSpi(final String s, final String s2, final String[] array, final String[] array2, final String[] array3, final String s3, final Class[] array4, final String[] array5, final boolean b, final String s4, final String s5, final String[] array6, final String[] array7, final boolean b2, final String s6, final String s7, final String[] array8, final String[] array9) {
        super(s, s2, array, array2, array3, s3, b, s4, s5, array6, array7, b2, s6, s7, array8, array9);
        this.outputTypes = null;
        this.readerSpiNames = null;
        this.writerClass = null;
        if (array4 == null) {
            throw new IllegalArgumentException("outputTypes == null!");
        }
        if (array4.length == 0) {
            throw new IllegalArgumentException("outputTypes.length == 0!");
        }
        this.outputTypes = ((array4 == ImageWriterSpi.STANDARD_OUTPUT_TYPE) ? new Class[] { ImageOutputStream.class } : array4.clone());
        if (array5 != null && array5.length > 0) {
            this.readerSpiNames = array5.clone();
        }
    }
    
    public boolean isFormatLossless() {
        return true;
    }
    
    public Class[] getOutputTypes() {
        return this.outputTypes.clone();
    }
    
    public abstract boolean canEncodeImage(final ImageTypeSpecifier p0);
    
    public boolean canEncodeImage(final RenderedImage renderedImage) {
        return this.canEncodeImage(ImageTypeSpecifier.createFromRenderedImage(renderedImage));
    }
    
    public ImageWriter createWriterInstance() throws IOException {
        return this.createWriterInstance(null);
    }
    
    public abstract ImageWriter createWriterInstance(final Object p0) throws IOException;
    
    public boolean isOwnWriter(final ImageWriter imageWriter) {
        if (imageWriter == null) {
            throw new IllegalArgumentException("writer == null!");
        }
        return imageWriter.getClass().getName().equals(this.pluginClassName);
    }
    
    public String[] getImageReaderSpiNames() {
        return (String[])((this.readerSpiNames == null) ? null : ((String[])this.readerSpiNames.clone()));
    }
    
    static {
        STANDARD_OUTPUT_TYPE = new Class[] { ImageOutputStream.class };
    }
}
