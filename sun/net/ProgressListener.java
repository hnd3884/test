package sun.net;

import java.util.EventListener;

public interface ProgressListener extends EventListener
{
    void progressStart(final ProgressEvent p0);
    
    void progressUpdate(final ProgressEvent p0);
    
    void progressFinish(final ProgressEvent p0);
}
