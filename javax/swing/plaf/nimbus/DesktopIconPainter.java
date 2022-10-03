package javax.swing.plaf.nimbus;

import java.awt.Shape;
import java.awt.Paint;
import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D;

final class DesktopIconPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_ENABLED = 1;
    private int state;
    private PaintContext ctx;
    private Path2D path;
    private Rectangle2D rect;
    private RoundRectangle2D roundRect;
    private Ellipse2D ellipse;
    private Color color1;
    private Color color2;
    private Color color3;
    private Color color4;
    private Color color5;
    private Object[] componentColors;
    
    public DesktopIconPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBase", 0.02551502f, -0.47885156f, -0.34901965f, 0);
        this.color2 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.102261856f, 0.20392156f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.0682728f, 0.09019607f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", -0.01111114f, -0.088974595f, 0.16470587f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.029445238f, -0.019607842f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 1: {
                this.paintBackgroundEnabled(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient1(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.decodeGradient2(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private RoundRectangle2D decodeRoundRect1() {
        this.roundRect.setRoundRect(this.decodeX(0.4f), this.decodeY(0.0f), this.decodeX(2.8f) - this.decodeX(0.4f), this.decodeY(2.6f) - this.decodeY(0.0f), 4.833333492279053, 4.833333492279053);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect2() {
        this.roundRect.setRoundRect(this.decodeX(0.6f), this.decodeY(0.2f), this.decodeX(2.8f) - this.decodeX(0.6f), this.decodeY(2.4f) - this.decodeY(0.2f), 3.0999999046325684, 3.0999999046325684);
        return this.roundRect;
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(0.8f), this.decodeY(0.4f), this.decodeX(2.4f) - this.decodeX(0.8f), this.decodeY(2.2f) - this.decodeY(0.4f));
        return this.rect;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.24f, 1.0f }, new Color[] { this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5 });
    }
}
