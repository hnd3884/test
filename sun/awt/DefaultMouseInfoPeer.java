package sun.awt;

import java.awt.Window;
import java.awt.Point;
import java.awt.peer.MouseInfoPeer;

public class DefaultMouseInfoPeer implements MouseInfoPeer
{
    DefaultMouseInfoPeer() {
    }
    
    @Override
    public native int fillPointWithCoords(final Point p0);
    
    @Override
    public native boolean isWindowUnderMouse(final Window p0);
}
