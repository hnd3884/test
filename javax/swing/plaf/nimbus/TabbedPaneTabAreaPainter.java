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

final class TabbedPaneTabAreaPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_ENABLED = 1;
    static final int BACKGROUND_DISABLED = 2;
    static final int BACKGROUND_ENABLED_MOUSEOVER = 3;
    static final int BACKGROUND_ENABLED_PRESSED = 4;
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
    private Color color6;
    private Color color7;
    private Color color8;
    private Color color9;
    private Color color10;
    private Object[] componentColors;
    
    public TabbedPaneTabAreaPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = new Color(255, 200, 0, 255);
        this.color2 = this.decodeColor("nimbusBase", 0.08801502f, 0.3642857f, -0.4784314f, 0);
        this.color3 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.45471883f, 0.31764704f, 0);
        this.color4 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4633005f, 0.3607843f, 0);
        this.color5 = this.decodeColor("nimbusBase", 0.05468172f, -0.58308274f, 0.19607842f, 0);
        this.color6 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.54901963f, 0);
        this.color7 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4690476f, 0.39215684f, 0);
        this.color8 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.47635174f, 0.4352941f, 0);
        this.color9 = this.decodeColor("nimbusBase", 0.0f, -0.05401492f, 0.05098039f, 0);
        this.color10 = this.decodeColor("nimbusBase", 0.0f, -0.09303135f, 0.09411764f, 0);
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
            case 2: {
                this.paintBackgroundDisabled(graphics2D);
                break;
            }
            case 3: {
                this.paintBackgroundEnabledAndMouseOver(graphics2D);
                break;
            }
            case 4: {
                this.paintBackgroundEnabledAndPressed(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundDisabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient2(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundEnabledAndMouseOver(final Graphics2D graphics2D) {
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient3(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundEnabledAndPressed(final Graphics2D graphics2D) {
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient4(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(1.0f), this.decodeX(0.0f) - this.decodeX(0.0f), this.decodeY(1.0f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(2.1666667f), this.decodeX(3.0f) - this.decodeX(0.0f), this.decodeY(3.0f) - this.decodeY(2.1666667f));
        return this.rect;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.08387097f, 0.09677419f, 0.10967742f, 0.43709677f, 0.7645161f, 0.7758064f, 0.7870968f }, new Color[] { this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.08387097f, 0.09677419f, 0.10967742f, 0.43709677f, 0.7645161f, 0.7758064f, 0.7870968f }, new Color[] { this.color5, this.decodeColor(this.color5, this.color3, 0.5f), this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.08387097f, 0.09677419f, 0.10967742f, 0.43709677f, 0.7645161f, 0.7758064f, 0.7870968f }, new Color[] { this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8, this.decodeColor(this.color8, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.08387097f, 0.09677419f, 0.10967742f, 0.43709677f, 0.7645161f, 0.7758064f, 0.7870968f }, new Color[] { this.color2, this.decodeColor(this.color2, this.color9, 0.5f), this.color9, this.decodeColor(this.color9, this.color10, 0.5f), this.color10, this.decodeColor(this.color10, this.color2, 0.5f), this.color2 });
    }
}
