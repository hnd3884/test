package java.awt.peer;

import java.awt.Window;
import java.awt.Point;

public interface MouseInfoPeer
{
    int fillPointWithCoords(final Point p0);
    
    boolean isWindowUnderMouse(final Window p0);
}
