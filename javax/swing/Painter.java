package javax.swing;

import java.awt.Graphics2D;

public interface Painter<T>
{
    void paint(final Graphics2D p0, final T p1, final int p2, final int p3);
}
