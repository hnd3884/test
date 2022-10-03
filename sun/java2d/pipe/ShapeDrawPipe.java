package sun.java2d.pipe;

import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public interface ShapeDrawPipe
{
    void draw(final SunGraphics2D p0, final Shape p1);
    
    void fill(final SunGraphics2D p0, final Shape p1);
}
