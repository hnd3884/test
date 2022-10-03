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

final class TreeCellEditorPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_ENABLED_FOCUSED = 3;
    static final int BACKGROUND_SELECTED = 4;
    private int state;
    private PaintContext ctx;
    private Path2D path;
    private Rectangle2D rect;
    private RoundRectangle2D roundRect;
    private Ellipse2D ellipse;
    private Color color1;
    private Color color2;
    private Object[] componentColors;
    
    public TreeCellEditorPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.017358616f, -0.11372548f, 0);
        this.color2 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 2: {
                this.paintBackgroundEnabled(graphics2D);
                break;
            }
            case 3: {
                this.paintBackgroundEnabledAndFocused(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundEnabledAndFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color2);
        graphics2D.fill(this.path);
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.24000001f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.24000001f), this.decodeY(0.24000001f));
        this.path.lineTo(this.decodeX(2.7600007f), this.decodeY(0.24000001f));
        this.path.lineTo(this.decodeX(2.7600007f), this.decodeY(2.7599998f));
        this.path.lineTo(this.decodeX(0.24000001f), this.decodeY(2.7599998f));
        this.path.lineTo(this.decodeX(0.24000001f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.closePath();
        return this.path;
    }
}
