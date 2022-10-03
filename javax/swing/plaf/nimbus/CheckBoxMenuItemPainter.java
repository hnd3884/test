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

final class CheckBoxMenuItemPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_MOUSEOVER = 3;
    static final int BACKGROUND_SELECTED_MOUSEOVER = 4;
    static final int CHECKICON_DISABLED_SELECTED = 5;
    static final int CHECKICON_ENABLED_SELECTED = 6;
    static final int CHECKICON_SELECTED_MOUSEOVER = 7;
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
    private Object[] componentColors;
    
    public CheckBoxMenuItemPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusSelection", 0.0f, 0.0f, 0.0f, 0);
        this.color2 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.08983666f, -0.17647058f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.096827686f, -0.45882353f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 3: {
                this.paintBackgroundMouseOver(graphics2D);
                break;
            }
            case 4: {
                this.paintBackgroundSelectedAndMouseOver(graphics2D);
                break;
            }
            case 5: {
                this.paintcheckIconDisabledAndSelected(graphics2D);
                break;
            }
            case 6: {
                this.paintcheckIconEnabledAndSelected(graphics2D);
                break;
            }
            case 7: {
                this.paintcheckIconSelectedAndMouseOver(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundMouseOver(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundSelectedAndMouseOver(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
    }
    
    private void paintcheckIconDisabledAndSelected(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color2);
        graphics2D.fill(this.path);
    }
    
    private void paintcheckIconEnabledAndSelected(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color3);
        graphics2D.fill(this.path);
    }
    
    private void paintcheckIconSelectedAndMouseOver(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.path);
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(1.0f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(2.0f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(0.4292683f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(0.7121951f), this.decodeY(2.4780488f));
        this.path.lineTo(this.decodeX(2.5926828f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(2.8317075f), this.decodeY(0.39512196f));
        this.path.lineTo(this.decodeX(0.8f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.5731707f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(1.5f));
        this.path.closePath();
        return this.path;
    }
}
