package javax.imageio.spi;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import java.io.File;

public abstract class ImageInputStreamSpi extends IIOServiceProvider
{
    protected Class<?> inputClass;
    
    protected ImageInputStreamSpi() {
    }
    
    public ImageInputStreamSpi(final String s, final String s2, final Class<?> inputClass) {
        super(s, s2);
        this.inputClass = inputClass;
    }
    
    public Class<?> getInputClass() {
        return this.inputClass;
    }
    
    public boolean canUseCacheFile() {
        return false;
    }
    
    public boolean needsCacheFile() {
        return false;
    }
    
    public abstract ImageInputStream createInputStreamInstance(final Object p0, final boolean p1, final File p2) throws IOException;
    
    public ImageInputStream createInputStreamInstance(final Object o) throws IOException {
        return this.createInputStreamInstance(o, true, null);
    }
}
