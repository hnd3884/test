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

final class SpinnerPanelSpinnerFormattedTextFieldPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_FOCUSED = 3;
    static final int BACKGROUND_SELECTED = 4;
    static final int BACKGROUND_SELECTED_FOCUSED = 5;
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
    private Color color13;
    private Color color14;
    private Color color15;
    private Object[] componentColors;
    
    public SpinnerPanelSpinnerFormattedTextFieldPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", -0.6111111f, -0.110526316f, -0.74509805f, -237);
        this.color2 = this.decodeColor("nimbusBlueGrey", -0.006944418f, -0.07187897f, 0.06666666f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.07703349f, 0.0745098f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.07968931f, 0.14509803f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.07856284f, 0.11372548f, 0);
        this.color6 = this.decodeColor("nimbusBase", 0.040395975f, -0.60315615f, 0.29411763f, 0);
        this.color7 = this.decodeColor("nimbusBase", 0.016586483f, -0.6051466f, 0.3490196f, 0);
        this.color8 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.0965403f, -0.18431371f, 0);
        this.color9 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.1048766f, -0.08f, 0);
        this.color10 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.105624355f, 0.054901958f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color12 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.105344966f, 0.011764705f, 0);
        this.color13 = this.decodeColor("nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        this.color14 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.color15 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.1048766f, -0.05098039f, 0);
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
                this.paintBackgroundFocused(graphics2D);
                break;
            }
            case 4: {
                this.paintBackgroundSelected(graphics2D);
                break;
            }
            case 5: {
                this.paintBackgroundSelectedAndFocused(graphics2D);
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
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient2(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color7);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient3(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient4(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundFocused(final Graphics2D graphics2D) {
        this.rect = this.decodeRect6();
        graphics2D.setPaint(this.color14);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient5(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient4(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundSelected(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient3(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient4(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundSelectedAndFocused(final Graphics2D graphics2D) {
        this.rect = this.decodeRect6();
        graphics2D.setPaint(this.color14);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient5(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient4(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.rect);
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(0.6666667f), this.decodeY(2.3333333f), this.decodeX(3.0f) - this.decodeX(0.6666667f), this.decodeY(2.6666667f) - this.decodeY(2.3333333f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(0.6666667f), this.decodeY(0.4f), this.decodeX(3.0f) - this.decodeX(0.6666667f), this.decodeY(1.0f) - this.decodeY(0.4f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect3() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(0.6f), this.decodeX(3.0f) - this.decodeX(1.0f), this.decodeY(1.0f) - this.decodeY(0.6f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect4() {
        this.rect.setRect(this.decodeX(0.6666667f), this.decodeY(1.0f), this.decodeX(3.0f) - this.decodeX(0.6666667f), this.decodeY(2.3333333f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect5() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(1.0f), this.decodeX(3.0f) - this.decodeX(1.0f), this.decodeY(2.0f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect6() {
        this.rect.setRect(this.decodeX(0.22222222f), this.decodeY(0.13333334f), this.decodeX(2.916668f) - this.decodeX(0.22222222f), this.decodeY(2.75f) - this.decodeY(0.13333334f));
        return this.rect;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 1.0f * n4 + n2, 0.5f * n3 + n, 0.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.49573863f, 0.99147725f }, new Color[] { this.color8, this.decodeColor(this.color8, this.color9, 0.5f), this.color9 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.1684492f, 1.0f }, new Color[] { this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.49573863f, 0.99147725f }, new Color[] { this.color8, this.decodeColor(this.color8, this.color15, 0.5f), this.color15 });
    }
}
