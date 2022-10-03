package sun.awt.windows;

import java.awt.Image;
import java.awt.Component;

public final class WBufferStrategy
{
    private static native void initIDs(final Class<?> p0);
    
    public static native Image getDrawBuffer(final Component p0);
    
    static {
        initIDs(Component.class);
    }
}
