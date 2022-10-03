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

final class TextFieldPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_SELECTED = 3;
    static final int BORDER_DISABLED = 4;
    static final int BORDER_FOCUSED = 5;
    static final int BORDER_ENABLED = 6;
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
    private Color color11;
    private Color color12;
    private Object[] componentColors;
    
    public TextFieldPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", -0.015872955f, -0.07995863f, 0.15294117f, 0);
        this.color2 = this.decodeColor("nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", -0.006944418f, -0.07187897f, 0.06666666f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.07826825f, 0.10588235f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.07856284f, 0.11372548f, 0);
        this.color6 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.07796818f, 0.09803921f, 0);
        this.color7 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.0965403f, -0.18431371f, 0);
        this.color8 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.1048766f, -0.05098039f, 0);
        this.color9 = this.decodeColor("nimbusLightBackground", 0.6666667f, 0.004901961f, -0.19999999f, 0);
        this.color10 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.10512091f, -0.019607842f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.105344966f, 0.011764705f, 0);
        this.color12 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
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
            case 3: {
                this.paintBackgroundSelected(graphics2D);
                break;
            }
            case 4: {
                this.paintBorderDisabled(graphics2D);
                break;
            }
            case 5: {
                this.paintBorderFocused(graphics2D);
                break;
            }
            case 6: {
                this.paintBorderEnabled(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected Object[] getExtendedCacheKeys(final JComponent component) {
        Object[] array = null;
        switch (this.state) {
            case 2: {
                array = new Object[] { this.getComponentColor(component, "background", this.color2, 0.0f, 0.0f, 0) };
                break;
            }
            case 5: {
                array = new Object[] { this.getComponentColor(component, "background", this.color9, 0.004901961f, -0.19999999f, 0), this.getComponentColor(component, "background", this.color2, 0.0f, 0.0f, 0) };
                break;
            }
            case 6: {
                array = new Object[] { this.getComponentColor(component, "background", this.color9, 0.004901961f, -0.19999999f, 0), this.getComponentColor(component, "background", this.color2, 0.0f, 0.0f, 0) };
                break;
            }
        }
        return array;
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundDisabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint((Paint)this.componentColors[0]);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundSelected(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color2);
        graphics2D.fill(this.rect);
    }
    
    private void paintBorderDisabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient2(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect6();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
    }
    
    private void paintBorderFocused(final Graphics2D graphics2D) {
        this.rect = this.decodeRect7();
        graphics2D.setPaint(this.decodeGradient3(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect8();
        graphics2D.setPaint(this.decodeGradient4(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect9();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect10();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect11();
        graphics2D.setPaint(this.color11);
        graphics2D.fill(this.rect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.path);
    }
    
    private void paintBorderEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect7();
        graphics2D.setPaint(this.decodeGradient5(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect8();
        graphics2D.setPaint(this.decodeGradient4(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect9();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect10();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect11();
        graphics2D.setPaint(this.color11);
        graphics2D.fill(this.rect);
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(0.4f), this.decodeY(0.4f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(2.6f) - this.decodeY(0.4f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(0.6666667f), this.decodeY(0.4f), this.decodeX(2.3333333f) - this.decodeX(0.6666667f), this.decodeY(1.0f) - this.decodeY(0.4f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect3() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(0.6f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(1.0f) - this.decodeY(0.6f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect4() {
        this.rect.setRect(this.decodeX(0.6666667f), this.decodeY(1.0f), this.decodeX(1.0f) - this.decodeX(0.6666667f), this.decodeY(2.0f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect5() {
        this.rect.setRect(this.decodeX(0.6666667f), this.decodeY(2.3333333f), this.decodeX(2.3333333f) - this.decodeX(0.6666667f), this.decodeY(2.0f) - this.decodeY(2.3333333f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect6() {
        this.rect.setRect(this.decodeX(2.0f), this.decodeY(1.0f), this.decodeX(2.3333333f) - this.decodeX(2.0f), this.decodeY(2.0f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect7() {
        this.rect.setRect(this.decodeX(0.4f), this.decodeY(0.4f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(1.0f) - this.decodeY(0.4f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect8() {
        this.rect.setRect(this.decodeX(0.6f), this.decodeY(0.6f), this.decodeX(2.4f) - this.decodeX(0.6f), this.decodeY(1.0f) - this.decodeY(0.6f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect9() {
        this.rect.setRect(this.decodeX(0.4f), this.decodeY(1.0f), this.decodeX(0.6f) - this.decodeX(0.4f), this.decodeY(2.6f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect10() {
        this.rect.setRect(this.decodeX(2.4f), this.decodeY(1.0f), this.decodeX(2.6f) - this.decodeX(2.4f), this.decodeY(2.6f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect11() {
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
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color5, this.decodeColor(this.color5, this.color1, 0.5f), this.color1 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 0.1625f * n4 + n2, new float[] { 0.1f, 0.49999997f, 0.9f }, new Color[] { this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.1f, 0.49999997f, 0.9f }, new Color[] { (Color)this.componentColors[0], this.decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5f), (Color)this.componentColors[1] });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.1f, 0.49999997f, 0.9f }, new Color[] { this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8 });
    }
}
