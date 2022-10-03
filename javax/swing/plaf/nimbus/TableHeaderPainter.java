package javax.swing.plaf.nimbus;

import java.awt.Paint;
import java.awt.Shape;
import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D;

final class TableHeaderPainter extends AbstractRegionPainter
{
    static final int ASCENDINGSORTICON_ENABLED = 1;
    static final int DESCENDINGSORTICON_ENABLED = 2;
    private int state;
    private PaintContext ctx;
    private Path2D path;
    private Rectangle2D rect;
    private RoundRectangle2D roundRect;
    private Ellipse2D ellipse;
    private Color color1;
    private Color color2;
    private Object[] componentColors;
    
    public TableHeaderPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBase", 0.0057927966f, -0.21904764f, 0.15686274f, 0);
        this.color2 = this.decodeColor("nimbusBase", 0.0038565993f, 0.02012986f, 0.054901958f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 1: {
                this.paintascendingSortIconEnabled(graphics2D);
                break;
            }
            case 2: {
                this.paintdescendingSortIconEnabled(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintascendingSortIconEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient1(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintdescendingSortIconEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient1(this.path));
        graphics2D.fill(this.path);
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.7070175f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.5025063f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color1, this.decodeColor(this.color1, this.color2, 0.5f), this.color2 });
    }
}
