package javax.swing;

import java.awt.Rectangle;
import java.awt.Graphics;

interface GraphicsWrapper
{
    Graphics subGraphics();
    
    boolean isClipIntersecting(final Rectangle p0);
    
    int getClipX();
    
    int getClipY();
    
    int getClipWidth();
    
    int getClipHeight();
}
