package sun.java2d.pipe;

import java.awt.geom.GeneralPath;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import sun.java2d.SunGraphics2D;

public class PixelToShapeConverter implements PixelDrawPipe, PixelFillPipe
{
    ShapeDrawPipe outpipe;
    
    public PixelToShapeConverter(final ShapeDrawPipe outpipe) {
        this.outpipe = outpipe;
    }
    
    @Override
    public void drawLine(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        this.outpipe.draw(sunGraphics2D, new Line2D.Float((float)n, (float)n2, (float)n3, (float)n4));
    }
    
    @Override
    public void drawRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        this.outpipe.draw(sunGraphics2D, new Rectangle(n, n2, n3, n4));
    }
    
    @Override
    public void fillRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        this.outpipe.fill(sunGraphics2D, new Rectangle(n, n2, n3, n4));
    }
    
    @Override
    public void drawRoundRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.outpipe.draw(sunGraphics2D, new RoundRectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6));
    }
    
    @Override
    public void fillRoundRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.outpipe.fill(sunGraphics2D, new RoundRectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6));
    }
    
    @Override
    public void drawOval(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        this.outpipe.draw(sunGraphics2D, new Ellipse2D.Float((float)n, (float)n2, (float)n3, (float)n4));
    }
    
    @Override
    public void fillOval(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        this.outpipe.fill(sunGraphics2D, new Ellipse2D.Float((float)n, (float)n2, (float)n3, (float)n4));
    }
    
    @Override
    public void drawArc(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.outpipe.draw(sunGraphics2D, new Arc2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6, 0));
    }
    
    @Override
    public void fillArc(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.outpipe.fill(sunGraphics2D, new Arc2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6, 2));
    }
    
    private Shape makePoly(final int[] array, final int[] array2, final int n, final boolean b) {
        final GeneralPath generalPath = new GeneralPath(0);
        if (n > 0) {
            generalPath.moveTo((float)array[0], (float)array2[0]);
            for (int i = 1; i < n; ++i) {
                generalPath.lineTo((float)array[i], (float)array2[i]);
            }
            if (b) {
                generalPath.closePath();
            }
        }
        return generalPath;
    }
    
    @Override
    public void drawPolyline(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        this.outpipe.draw(sunGraphics2D, this.makePoly(array, array2, n, false));
    }
    
    @Override
    public void drawPolygon(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        this.outpipe.draw(sunGraphics2D, this.makePoly(array, array2, n, true));
    }
    
    @Override
    public void fillPolygon(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        this.outpipe.fill(sunGraphics2D, this.makePoly(array, array2, n, true));
    }
}
