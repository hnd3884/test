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

final class PopupMenuPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    private int state;
    private PaintContext ctx;
    private Path2D path;
    private Rectangle2D rect;
    private RoundRectangle2D roundRect;
    private Ellipse2D ellipse;
    private Color color1;
    private Color color2;
    private Color color3;
    private Object[] componentColors;
    
    public PopupMenuPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", -0.6111111f, -0.110526316f, -0.39607844f, 0);
        this.color2 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, 0);
        this.color3 = this.decodeColor("nimbusBase", 0.021348298f, -0.6150531f, 0.39999998f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 1: {
                this.paintBackgroundDisabled(graphics2D);
                break;
            }
            case 2: {
                this.paintBackgroundEnabled(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundDisabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(0.0f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(3.0f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(1.0045455f), this.decodeY(0.11111111f), this.decodeX(1.9954545f) - this.decodeX(1.0045455f), this.decodeY(2.909091f) - this.decodeY(0.11111111f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect3() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(0.0f), this.decodeX(3.0f) - this.decodeX(0.0f), this.decodeY(3.0f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect4() {
        this.rect.setRect(this.decodeX(0.5f), this.decodeY(0.09090909f), this.decodeX(2.5f) - this.decodeX(0.5f), this.decodeY(2.909091f) - this.decodeY(0.09090909f));
        return this.rect;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.003f, 0.02f, 0.5f, 0.98f, 0.996f, 1.0f }, new Color[] { this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3, this.decodeColor(this.color3, this.color3, 0.5f), this.color3, this.decodeColor(this.color3, this.color2, 0.5f), this.color2 });
    }
}
