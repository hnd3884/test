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

final class ToolBarButtonPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_ENABLED = 1;
    static final int BACKGROUND_FOCUSED = 2;
    static final int BACKGROUND_MOUSEOVER = 3;
    static final int BACKGROUND_MOUSEOVER_FOCUSED = 4;
    static final int BACKGROUND_PRESSED = 5;
    static final int BACKGROUND_PRESSED_FOCUSED = 6;
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
    private Object[] componentColors;
    
    public ToolBarButtonPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.color2 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.06885965f, -0.36862746f, -153);
        this.color3 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.020974077f, -0.21960783f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", 0.0f, 0.11169591f, -0.53333336f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.10658931f, 0.25098038f, 0);
        this.color6 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.098526314f, 0.2352941f, 0);
        this.color7 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07333623f, 0.20392156f, 0);
        this.color8 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color9 = this.decodeColor("nimbusBlueGrey", -0.00505054f, -0.05960039f, 0.10196078f, 0);
        this.color10 = this.decodeColor("nimbusBlueGrey", -0.008547008f, -0.04772438f, 0.06666666f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", -0.0027777553f, -0.0018306673f, -0.02352941f, 0);
        this.color12 = this.decodeColor("nimbusBlueGrey", -0.0027777553f, -0.0212406f, 0.13333333f, 0);
        this.color13 = this.decodeColor("nimbusBlueGrey", 0.0055555105f, -0.030845039f, 0.23921567f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 2: {
                this.paintBackgroundFocused(graphics2D);
                break;
            }
            case 3: {
                this.paintBackgroundMouseOver(graphics2D);
                break;
            }
            case 4: {
                this.paintBackgroundMouseOverAndFocused(graphics2D);
                break;
            }
            case 5: {
                this.paintBackgroundPressed(graphics2D);
                break;
            }
            case 6: {
                this.paintBackgroundPressedAndFocused(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundMouseOver(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color2);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient1(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundMouseOverAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient1(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundPressed(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color2);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient1(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundPressedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient1(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.4133738f), this.decodeY(0.120000005f));
        this.path.lineTo(this.decodeX(1.9893618f), this.decodeY(0.120000005f));
        this.path.curveTo(this.decodeAnchorX(1.9893618f, 3.0f), this.decodeAnchorY(0.120000005f, 0.0f), this.decodeAnchorX(2.8857148f, 0.0f), this.decodeAnchorY(1.0434783f, -3.0f), this.decodeX(2.8857148f), this.decodeY(1.0434783f));
        this.path.lineTo(this.decodeX(2.9f), this.decodeY(1.9565217f));
        this.path.curveTo(this.decodeAnchorX(2.9f, 0.0f), this.decodeAnchorY(1.9565217f, 3.0f), this.decodeAnchorX(1.9893618f, 3.0f), this.decodeAnchorY(2.8714287f, 0.0f), this.decodeX(1.9893618f), this.decodeY(2.8714287f));
        this.path.lineTo(this.decodeX(1.0106384f), this.decodeY(2.8714287f));
        this.path.curveTo(this.decodeAnchorX(1.0106384f, -3.0f), this.decodeAnchorY(2.8714287f, 0.0f), this.decodeAnchorX(0.120000005f, 0.0f), this.decodeAnchorY(1.9565217f, 3.0f), this.decodeX(0.120000005f), this.decodeY(1.9565217f));
        this.path.lineTo(this.decodeX(0.120000005f), this.decodeY(1.0465839f));
        this.path.curveTo(this.decodeAnchorX(0.120000005f, 0.0f), this.decodeAnchorY(1.0465839f, -3.0f), this.decodeAnchorX(1.0106384f, -3.0f), this.decodeAnchorY(0.120000005f, 0.0f), this.decodeX(1.0106384f), this.decodeY(0.120000005f));
        this.path.lineTo(this.decodeX(1.4148936f), this.decodeY(0.120000005f));
        this.path.lineTo(this.decodeX(1.4148936f), this.decodeY(0.4857143f));
        this.path.lineTo(this.decodeX(1.0106384f), this.decodeY(0.4857143f));
        this.path.curveTo(this.decodeAnchorX(1.0106384f, -1.9285715f), this.decodeAnchorY(0.4857143f, 0.0f), this.decodeAnchorX(0.47142857f, -0.044279482f), this.decodeAnchorY(1.0403726f, -2.429218f), this.decodeX(0.47142857f), this.decodeY(1.0403726f));
        this.path.lineTo(this.decodeX(0.47142857f), this.decodeY(1.9565217f));
        this.path.curveTo(this.decodeAnchorX(0.47142857f, 0.0f), this.decodeAnchorY(1.9565217f, 2.2142856f), this.decodeAnchorX(1.0106384f, -1.7857143f), this.decodeAnchorY(2.5142856f, 0.0f), this.decodeX(1.0106384f), this.decodeY(2.5142856f));
        this.path.lineTo(this.decodeX(1.9893618f), this.decodeY(2.5142856f));
        this.path.curveTo(this.decodeAnchorX(1.9893618f, 2.0714285f), this.decodeAnchorY(2.5142856f, 0.0f), this.decodeAnchorX(2.5f, 0.0f), this.decodeAnchorY(1.9565217f, 2.2142856f), this.decodeX(2.5f), this.decodeY(1.9565217f));
        this.path.lineTo(this.decodeX(2.5142853f), this.decodeY(1.0434783f));
        this.path.curveTo(this.decodeAnchorX(2.5142853f, 0.0f), this.decodeAnchorY(1.0434783f, -2.142857f), this.decodeAnchorX(1.9901216f, 2.142857f), this.decodeAnchorY(0.47142857f, 0.0f), this.decodeX(1.9901216f), this.decodeY(0.47142857f));
        this.path.lineTo(this.decodeX(1.4148936f), this.decodeY(0.4857143f));
        this.path.lineTo(this.decodeX(1.4133738f), this.decodeY(0.120000005f));
        this.path.closePath();
        return this.path;
    }
    
    private RoundRectangle2D decodeRoundRect1() {
        this.roundRect.setRoundRect(this.decodeX(0.4f), this.decodeY(0.6f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(2.8f) - this.decodeY(0.6f), 12.0, 12.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect2() {
        this.roundRect.setRoundRect(this.decodeX(0.4f), this.decodeY(0.4f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(2.6f) - this.decodeY(0.4f), 12.0, 12.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect3() {
        this.roundRect.setRoundRect(this.decodeX(0.6f), this.decodeY(0.6f), this.decodeX(2.4f) - this.decodeX(0.6f), this.decodeY(2.4f) - this.decodeY(0.6f), 9.0, 9.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect4() {
        this.roundRect.setRoundRect(this.decodeX(0.120000005f), this.decodeY(0.120000005f), this.decodeX(2.8800004f) - this.decodeX(0.120000005f), this.decodeY(2.8800004f) - this.decodeY(0.120000005f), 13.0, 13.0);
        return this.roundRect;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.09f, 0.52f, 0.95f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.03f, 0.06f, 0.33f, 0.6f, 0.65f, 0.7f, 0.825f, 0.95f, 0.975f, 1.0f }, new Color[] { this.color5, this.decodeColor(this.color5, this.color6, 0.5f), this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7, this.decodeColor(this.color7, this.color7, 0.5f), this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8, this.decodeColor(this.color8, this.color8, 0.5f), this.color8 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.03f, 0.06f, 0.33f, 0.6f, 0.65f, 0.7f, 0.825f, 0.95f, 0.975f, 1.0f }, new Color[] { this.color9, this.decodeColor(this.color9, this.color10, 0.5f), this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11, this.decodeColor(this.color11, this.color11, 0.5f), this.color11, this.decodeColor(this.color11, this.color12, 0.5f), this.color12, this.decodeColor(this.color12, this.color13, 0.5f), this.color13 });
    }
}
