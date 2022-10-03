package java.awt;

import java.awt.image.ColorModel;

public interface Composite
{
    CompositeContext createContext(final ColorModel p0, final ColorModel p1, final RenderingHints p2);
}
