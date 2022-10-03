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

final class ArrowButtonPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_ENABLED = 1;
    static final int FOREGROUND_DISABLED = 2;
    static final int FOREGROUND_ENABLED = 3;
    private int state;
    private PaintContext ctx;
    private Path2D path;
    private Rectangle2D rect;
    private RoundRectangle2D roundRect;
    private Ellipse2D ellipse;
    private Color color1;
    private Color color2;
    private Object[] componentColors;
    
    public ArrowButtonPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBase", 0.027408898f, -0.57391655f, 0.1490196f, 0);
        this.color2 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.37254906f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 2: {
                this.paintForegroundDisabled(graphics2D);
                break;
            }
            case 3: {
                this.paintForegroundEnabled(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintForegroundDisabled(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color2);
        graphics2D.fill(this.path);
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.8f), this.decodeY(1.2f));
        this.path.lineTo(this.decodeX(1.2f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(1.8f), this.decodeY(1.8f));
        this.path.lineTo(this.decodeX(1.8f), this.decodeY(1.2f));
        this.path.closePath();
        return this.path;
    }
}
