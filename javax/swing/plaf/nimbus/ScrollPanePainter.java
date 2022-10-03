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

final class ScrollPanePainter extends AbstractRegionPainter
{
    static final int BACKGROUND_ENABLED = 1;
    static final int BORDER_ENABLED_FOCUSED = 2;
    static final int BORDER_ENABLED = 3;
    private int state;
    private PaintContext ctx;
    private Path2D path;
    private Rectangle2D rect;
    private RoundRectangle2D roundRect;
    private Ellipse2D ellipse;
    private Color color1;
    private Color color2;
    private Object[] componentColors;
    
    public ScrollPanePainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBorder", 0.0f, 0.0f, 0.0f, 0);
        this.color2 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 2: {
                this.paintBorderEnabledAndFocused(graphics2D);
                break;
            }
            case 3: {
                this.paintBorderEnabled(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBorderEnabledAndFocused(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color2);
        graphics2D.fill(this.path);
    }
    
    private void paintBorderEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(0.6f), this.decodeY(0.4f), this.decodeX(2.4f) - this.decodeX(0.6f), this.decodeY(0.6f) - this.decodeY(0.4f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(0.4f), this.decodeY(0.4f), this.decodeX(0.6f) - this.decodeX(0.4f), this.decodeY(2.6f) - this.decodeY(0.4f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect3() {
        this.rect.setRect(this.decodeX(2.4f), this.decodeY(0.4f), this.decodeX(2.6f) - this.decodeX(2.4f), this.decodeY(2.6f) - this.decodeY(0.4f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect4() {
        this.rect.setRect(this.decodeX(0.6f), this.decodeY(2.4f), this.decodeX(2.4f) - this.decodeX(0.6f), this.decodeY(2.6f) - this.decodeY(2.4f));
        return this.rect;
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.4f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(0.4f));
        this.path.curveTo(this.decodeAnchorX(2.6f, 0.0f), this.decodeAnchorY(0.4f, 0.0f), this.decodeAnchorX(2.8800004f, 0.1f), this.decodeAnchorY(0.4f, 0.0f), this.decodeX(2.8800004f), this.decodeY(0.4f));
        this.path.curveTo(this.decodeAnchorX(2.8800004f, 0.1f), this.decodeAnchorY(0.4f, 0.0f), this.decodeAnchorX(2.8800004f, 0.0f), this.decodeAnchorY(2.8799999f, 0.0f), this.decodeX(2.8800004f), this.decodeY(2.8799999f));
        this.path.lineTo(this.decodeX(0.120000005f), this.decodeY(2.8799999f));
        this.path.lineTo(this.decodeX(0.120000005f), this.decodeY(0.120000005f));
        this.path.lineTo(this.decodeX(2.8800004f), this.decodeY(0.120000005f));
        this.path.lineTo(this.decodeX(2.8800004f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.4f));
        this.path.closePath();
        return this.path;
    }
}
