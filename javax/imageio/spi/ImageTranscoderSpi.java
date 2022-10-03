package javax.imageio.spi;

import javax.imageio.ImageTranscoder;

public abstract class ImageTranscoderSpi extends IIOServiceProvider
{
    protected ImageTranscoderSpi() {
    }
    
    public ImageTranscoderSpi(final String s, final String s2) {
        super(s, s2);
    }
    
    public abstract String getReaderServiceProviderName();
    
    public abstract String getWriterServiceProviderName();
    
    public abstract ImageTranscoder createTranscoderInstance();
}
