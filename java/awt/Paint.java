package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

public interface Paint extends Transparency
{
    PaintContext createContext(final ColorModel p0, final Rectangle p1, final Rectangle2D p2, final AffineTransform p3, final RenderingHints p4);
}
