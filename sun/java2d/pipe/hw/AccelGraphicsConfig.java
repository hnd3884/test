package sun.java2d.pipe.hw;

import java.awt.image.VolatileImage;

public interface AccelGraphicsConfig extends BufferedContextProvider
{
    VolatileImage createCompatibleVolatileImage(final int p0, final int p1, final int p2, final int p3);
    
    ContextCapabilities getContextCapabilities();
    
    void addDeviceEventListener(final AccelDeviceEventListener p0);
    
    void removeDeviceEventListener(final AccelDeviceEventListener p0);
}
