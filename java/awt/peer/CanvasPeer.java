package java.awt.peer;

import java.awt.GraphicsConfiguration;

public interface CanvasPeer extends ComponentPeer
{
    GraphicsConfiguration getAppropriateGraphicsConfiguration(final GraphicsConfiguration p0);
}
