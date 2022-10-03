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

final class ToolBarPainter extends AbstractRegionPainter
{
    static final int BORDER_NORTH = 1;
    static final int BORDER_SOUTH = 2;
    static final int BORDER_EAST = 3;
    static final int BORDER_WEST = 4;
    static final int HANDLEICON_ENABLED = 5;
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
    
    public ToolBarPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBorder", 0.0f, 0.0f, 0.0f, 0);
        this.color2 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", -0.006944418f, -0.07399663f, 0.11372548f, 0);
        this.color4 = this.decodeColor("nimbusBorder", 0.0f, -0.029675633f, 0.109803915f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", -0.008547008f, -0.03494492f, -0.07058823f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 1: {
                this.paintBorderNorth(graphics2D);
                break;
            }
            case 2: {
                this.paintBorderSouth(graphics2D);
                break;
            }
            case 3: {
                this.paintBorderEast(graphics2D);
                break;
            }
            case 4: {
                this.paintBorderWest(graphics2D);
                break;
            }
            case 5: {
                this.painthandleIconEnabled(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBorderNorth(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
    }
    
    private void paintBorderSouth(final Graphics2D graphics2D) {
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
    }
    
    private void paintBorderEast(final Graphics2D graphics2D) {
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
    }
    
    private void paintBorderWest(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
    }
    
    private void painthandleIconEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color5);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color5);
        graphics2D.fill(this.path);
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(2.0f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(3.0f) - this.decodeY(2.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(0.0f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(1.0f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect3() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(0.0f), this.decodeX(2.8f) - this.decodeX(0.0f), this.decodeY(3.0f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect4() {
        this.rect.setRect(this.decodeX(2.8f), this.decodeY(0.0f), this.decodeX(3.0f) - this.decodeX(2.8f), this.decodeY(3.0f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.0f * n3 + n, 0.5f * n4 + n2, 1.0f * n3 + n, 0.5f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3 });
    }
}
