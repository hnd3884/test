package javax.swing.text;

import java.awt.Shape;
import java.awt.Graphics;

public abstract class LayeredHighlighter implements Highlighter
{
    public abstract void paintLayeredHighlights(final Graphics p0, final int p1, final int p2, final Shape p3, final JTextComponent p4, final View p5);
    
    public abstract static class LayerPainter implements HighlightPainter
    {
        public abstract Shape paintLayer(final Graphics p0, final int p1, final int p2, final Shape p3, final JTextComponent p4, final View p5);
    }
}
