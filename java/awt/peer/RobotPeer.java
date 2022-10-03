package java.awt.peer;

import java.awt.Rectangle;

public interface RobotPeer
{
    void mouseMove(final int p0, final int p1);
    
    void mousePress(final int p0);
    
    void mouseRelease(final int p0);
    
    void mouseWheel(final int p0);
    
    void keyPress(final int p0);
    
    void keyRelease(final int p0);
    
    int getRGBPixel(final int p0, final int p1);
    
    int[] getRGBPixels(final Rectangle p0);
    
    void dispose();
}
