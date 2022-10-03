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

final class ToolTipPainter extends AbstractRegionPainter
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
    private Object[] componentColors;
    
    public ToolTipPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBorder", 0.0f, 0.0f, 0.0f, 0);
        this.color2 = this.decodeColor("info", 0.0f, 0.0f, 0.0f, 0);
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
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color2);
        graphics2D.fill(this.rect);
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(2.0f), this.decodeY(1.0f), this.decodeX(3.0f) - this.decodeX(2.0f), this.decodeY(2.0f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(1.0f), this.decodeX(1.0f) - this.decodeX(0.0f), this.decodeY(2.0f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect3() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(2.0f), this.decodeX(3.0f) - this.decodeX(0.0f), this.decodeY(3.0f) - this.decodeY(2.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect4() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(0.0f), this.decodeX(3.0f) - this.decodeX(0.0f), this.decodeY(1.0f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect5() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(1.0f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(2.0f) - this.decodeY(1.0f));
        return this.rect;
    }
}
