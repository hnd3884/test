package javax.imageio.spi;

import javax.imageio.ImageReader;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

public abstract class ImageReaderSpi extends ImageReaderWriterSpi
{
    @Deprecated
    public static final Class[] STANDARD_INPUT_TYPE;
    protected Class[] inputTypes;
    protected String[] writerSpiNames;
    private Class readerClass;
    
    protected ImageReaderSpi() {
        this.inputTypes = null;
        this.writerSpiNames = null;
        this.readerClass = null;
    }
    
    public ImageReaderSpi(final String s, final String s2, final String[] array, final String[] array2, final String[] array3, final String s3, final Class[] array4, final String[] array5, final boolean b, final String s4, final String s5, final String[] array6, final String[] array7, final boolean b2, final String s6, final String s7, final String[] array8, final String[] array9) {
        super(s, s2, array, array2, array3, s3, b, s4, s5, array6, array7, b2, s6, s7, array8, array9);
        this.inputTypes = null;
        this.writerSpiNames = null;
        this.readerClass = null;
        if (array4 == null) {
            throw new IllegalArgumentException("inputTypes == null!");
        }
        if (array4.length == 0) {
            throw new IllegalArgumentException("inputTypes.length == 0!");
        }
        this.inputTypes = ((array4 == ImageReaderSpi.STANDARD_INPUT_TYPE) ? new Class[] { ImageInputStream.class } : array4.clone());
        if (array5 != null && array5.length > 0) {
            this.writerSpiNames = array5.clone();
        }
    }
    
    public Class[] getInputTypes() {
        return this.inputTypes.clone();
    }
    
    public abstract boolean canDecodeInput(final Object p0) throws IOException;
    
    public ImageReader createReaderInstance() throws IOException {
        return this.createReaderInstance(null);
    }
    
    public abstract ImageReader createReaderInstance(final Object p0) throws IOException;
    
    public boolean isOwnReader(final ImageReader imageReader) {
        if (imageReader == null) {
            throw new IllegalArgumentException("reader == null!");
        }
        return imageReader.getClass().getName().equals(this.pluginClassName);
    }
    
    public String[] getImageWriterSpiNames() {
        return (String[])((this.writerSpiNames == null) ? null : ((String[])this.writerSpiNames.clone()));
    }
    
    static {
        STANDARD_INPUT_TYPE = new Class[] { ImageInputStream.class };
    }
}
