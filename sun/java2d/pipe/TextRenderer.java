package sun.java2d.pipe;

import java.awt.Shape;
import java.awt.Rectangle;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public class TextRenderer extends GlyphListPipe
{
    CompositePipe outpipe;
    
    public TextRenderer(final CompositePipe outpipe) {
        this.outpipe = outpipe;
    }
    
    @Override
    protected void drawGlyphList(final SunGraphics2D sunGraphics2D, final GlyphList list) {
        final int numGlyphs = list.getNumGlyphs();
        final Region compClip = sunGraphics2D.getCompClip();
        final int loX = compClip.getLoX();
        final int loY = compClip.getLoY();
        final int hiX = compClip.getHiX();
        final int hiY = compClip.getHiY();
        Object startSequence = null;
        try {
            final int[] bounds = list.getBounds();
            final Rectangle rectangle = new Rectangle(bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1]);
            startSequence = this.outpipe.startSequence(sunGraphics2D, sunGraphics2D.untransformShape(rectangle), rectangle, bounds);
            for (int i = 0; i < numGlyphs; ++i) {
                list.setGlyphIndex(i);
                final int[] metrics = list.getMetrics();
                int n = metrics[0];
                int n2 = metrics[1];
                final int n3 = metrics[2];
                int n4 = n + n3;
                int n5 = n2 + metrics[3];
                int n6 = 0;
                if (n < loX) {
                    n6 = loX - n;
                    n = loX;
                }
                if (n2 < loY) {
                    n6 += (loY - n2) * n3;
                    n2 = loY;
                }
                if (n4 > hiX) {
                    n4 = hiX;
                }
                if (n5 > hiY) {
                    n5 = hiY;
                }
                if (n4 > n && n5 > n2 && this.outpipe.needTile(startSequence, n, n2, n4 - n, n5 - n2)) {
                    this.outpipe.renderPathTile(startSequence, list.getGrayBits(), n6, n3, n, n2, n4 - n, n5 - n2);
                }
                else {
                    this.outpipe.skipTile(startSequence, n, n2);
                }
            }
        }
        finally {
            if (startSequence != null) {
                this.outpipe.endSequence(startSequence);
            }
        }
    }
}
