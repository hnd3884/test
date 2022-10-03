package sun.net;

import java.net.URL;

public interface ProgressMeteringPolicy
{
    boolean shouldMeterInput(final URL p0, final String p1);
    
    int getProgressUpdateThreshold();
}
