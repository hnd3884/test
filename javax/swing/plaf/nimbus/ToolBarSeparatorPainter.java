package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Insets;

final class ToolBarSeparatorPainter extends AbstractRegionPainter
{
    private static final int SPACE = 3;
    private static final int INSET = 2;
    
    @Override
    protected PaintContext getPaintContext() {
        return new PaintContext(new Insets(1, 0, 1, 0), new Dimension(38, 7), false, PaintContext.CacheMode.NO_CACHING, 1.0, 1.0);
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] array) {
        graphics2D.setColor(component.getForeground());
        final int n3 = n2 / 2;
        for (int i = 2; i <= n - 2; i += 3) {
            graphics2D.fillRect(i, n3, 1, 1);
        }
    }
}
