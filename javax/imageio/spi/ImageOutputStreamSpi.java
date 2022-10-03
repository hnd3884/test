package javax.imageio.spi;

import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;
import java.io.File;

public abstract class ImageOutputStreamSpi extends IIOServiceProvider
{
    protected Class<?> outputClass;
    
    protected ImageOutputStreamSpi() {
    }
    
    public ImageOutputStreamSpi(final String s, final String s2, final Class<?> outputClass) {
        super(s, s2);
        this.outputClass = outputClass;
    }
    
    public Class<?> getOutputClass() {
        return this.outputClass;
    }
    
    public boolean canUseCacheFile() {
        return false;
    }
    
    public boolean needsCacheFile() {
        return false;
    }
    
    public abstract ImageOutputStream createOutputStreamInstance(final Object p0, final boolean p1, final File p2) throws IOException;
    
    public ImageOutputStream createOutputStreamInstance(final Object o) throws IOException {
        return this.createOutputStreamInstance(o, true, null);
    }
}
