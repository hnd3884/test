package javax.imageio.event;

import javax.imageio.ImageReader;
import java.util.EventListener;

public interface IIOReadProgressListener extends EventListener
{
    void sequenceStarted(final ImageReader p0, final int p1);
    
    void sequenceComplete(final ImageReader p0);
    
    void imageStarted(final ImageReader p0, final int p1);
    
    void imageProgress(final ImageReader p0, final float p1);
    
    void imageComplete(final ImageReader p0);
    
    void thumbnailStarted(final ImageReader p0, final int p1, final int p2);
    
    void thumbnailProgress(final ImageReader p0, final float p1);
    
    void thumbnailComplete(final ImageReader p0);
    
    void readAborted(final ImageReader p0);
}
