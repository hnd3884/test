package javax.imageio.event;

import javax.imageio.ImageWriter;
import java.util.EventListener;

public interface IIOWriteProgressListener extends EventListener
{
    void imageStarted(final ImageWriter p0, final int p1);
    
    void imageProgress(final ImageWriter p0, final float p1);
    
    void imageComplete(final ImageWriter p0);
    
    void thumbnailStarted(final ImageWriter p0, final int p1, final int p2);
    
    void thumbnailProgress(final ImageWriter p0, final float p1);
    
    void thumbnailComplete(final ImageWriter p0);
    
    void writeAborted(final ImageWriter p0);
}
