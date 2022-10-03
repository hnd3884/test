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

final class ToolBarToggleButtonPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_ENABLED = 1;
    static final int BACKGROUND_FOCUSED = 2;
    static final int BACKGROUND_MOUSEOVER = 3;
    static final int BACKGROUND_MOUSEOVER_FOCUSED = 4;
    static final int BACKGROUND_PRESSED = 5;
    static final int BACKGROUND_PRESSED_FOCUSED = 6;
    static final int BACKGROUND_SELECTED = 7;
    static final int BACKGROUND_SELECTED_FOCUSED = 8;
    static final int BACKGROUND_PRESSED_SELECTED = 9;
    static final int BACKGROUND_PRESSED_SELECTED_FOCUSED = 10;
    static final int BACKGROUND_MOUSEOVER_SELECTED = 11;
    static final int BACKGROUND_MOUSEOVER_SELECTED_FOCUSED = 12;
    static final int BACKGROUND_DISABLED_SELECTED = 13;
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
    private Color color16;
    private Color color17;
    private Color color18;
    private Color color19;
    private Color color20;
    private Color color21;
    private Color color22;
    private Color color23;
    private Color color24;
    private Color color25;
    private Color color26;
    private Color color27;
    private Color color28;
    private Object[] componentColors;
    
    public ToolBarToggleButtonPainter(final PaintContext ctx, final int state) {
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
        this.color9 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -86);
        this.color10 = this.decodeColor("nimbusBlueGrey", -0.01111114f, -0.060526315f, -0.3529412f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.064372465f, -0.2352941f, 0);
        this.color12 = this.decodeColor("nimbusBlueGrey", -0.006944418f, -0.0595709f, -0.12941176f, 0);
        this.color13 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.061075766f, -0.031372547f, 0);
        this.color14 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06080256f, -0.035294116f, 0);
        this.color15 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06472479f, -0.23137254f, 0);
        this.color16 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.06959064f, -0.0745098f, 0);
        this.color17 = this.decodeColor("nimbusBlueGrey", 0.0138888955f, -0.06401469f, -0.07058823f, 0);
        this.color18 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06530018f, 0.035294116f, 0);
        this.color19 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06507177f, 0.031372547f, 0);
        this.color20 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.05338346f, -0.47058824f, 0);
        this.color21 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.049301825f, -0.36078432f, 0);
        this.color22 = this.decodeColor("nimbusBlueGrey", -0.018518567f, -0.03909774f, -0.2509804f, 0);
        this.color23 = this.decodeColor("nimbusBlueGrey", -0.00505054f, -0.040013492f, -0.13333333f, 0);
        this.color24 = this.decodeColor("nimbusBlueGrey", 0.01010108f, -0.039558575f, -0.1372549f, 0);
        this.color25 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -220);
        this.color26 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.066408664f, 0.054901958f, 0);
        this.color27 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06807348f, 0.086274505f, 0);
        this.color28 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06924191f, 0.109803915f, 0);
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
            case 7: {
                this.paintBackgroundSelected(graphics2D);
                break;
            }
            case 8: {
                this.paintBackgroundSelectedAndFocused(graphics2D);
                break;
            }
            case 9: {
                this.paintBackgroundPressedAndSelected(graphics2D);
                break;
            }
            case 10: {
                this.paintBackgroundPressedAndSelectedAndFocused(graphics2D);
                break;
            }
            case 11: {
                this.paintBackgroundMouseOverAndSelected(graphics2D);
                break;
            }
            case 12: {
                this.paintBackgroundMouseOverAndSelectedAndFocused(graphics2D);
                break;
            }
            case 13: {
                this.paintBackgroundDisabledAndSelected(graphics2D);
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
        this.roundRect = this.decodeRoundRect5();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect6();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect7();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundPressedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect8();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect6();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect7();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundSelected(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect5();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect6();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect7();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundSelectedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect8();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect6();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect7();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundPressedAndSelected(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect5();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect6();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect7();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundPressedAndSelectedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect8();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect6();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect7();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundMouseOverAndSelected(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect5();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect6();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect7();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundMouseOverAndSelectedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect8();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect6();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect7();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundDisabledAndSelected(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect5();
        graphics2D.setPaint(this.color25);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect6();
        graphics2D.setPaint(this.decodeGradient9(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect7();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.4133738f), this.decodeY(0.120000005f));
        this.path.lineTo(this.decodeX(1.9893618f), this.decodeY(0.120000005f));
        this.path.curveTo(this.decodeAnchorX(1.9893618f, 3.0f), this.decodeAnchorY(0.120000005f, 0.0f), this.decodeAnchorX(2.8857148f, 0.0f), this.decodeAnchorY(1.0416666f, -3.0f), this.decodeX(2.8857148f), this.decodeY(1.0416666f));
        this.path.lineTo(this.decodeX(2.9f), this.decodeY(1.9166667f));
        this.path.curveTo(this.decodeAnchorX(2.9f, 0.0f), this.decodeAnchorY(1.9166667f, 3.0f), this.decodeAnchorX(1.9893618f, 3.0f), this.decodeAnchorY(2.6714287f, 0.0f), this.decodeX(1.9893618f), this.decodeY(2.6714287f));
        this.path.lineTo(this.decodeX(1.0106384f), this.decodeY(2.6714287f));
        this.path.curveTo(this.decodeAnchorX(1.0106384f, -3.0f), this.decodeAnchorY(2.6714287f, 0.0f), this.decodeAnchorX(0.120000005f, 0.0f), this.decodeAnchorY(1.9166667f, 3.0f), this.decodeX(0.120000005f), this.decodeY(1.9166667f));
        this.path.lineTo(this.decodeX(0.120000005f), this.decodeY(1.0446429f));
        this.path.curveTo(this.decodeAnchorX(0.120000005f, 0.0f), this.decodeAnchorY(1.0446429f, -3.0f), this.decodeAnchorX(1.0106384f, -3.0f), this.decodeAnchorY(0.120000005f, 0.0f), this.decodeX(1.0106384f), this.decodeY(0.120000005f));
        this.path.lineTo(this.decodeX(1.4148936f), this.decodeY(0.120000005f));
        this.path.lineTo(this.decodeX(1.4148936f), this.decodeY(0.4857143f));
        this.path.lineTo(this.decodeX(1.0106384f), this.decodeY(0.4857143f));
        this.path.curveTo(this.decodeAnchorX(1.0106384f, -1.9285715f), this.decodeAnchorY(0.4857143f, 0.0f), this.decodeAnchorX(0.47142857f, -0.044279482f), this.decodeAnchorY(1.0386904f, -2.429218f), this.decodeX(0.47142857f), this.decodeY(1.0386904f));
        this.path.lineTo(this.decodeX(0.47142857f), this.decodeY(1.9166667f));
        this.path.curveTo(this.decodeAnchorX(0.47142857f, 0.0f), this.decodeAnchorY(1.9166667f, 2.2142856f), this.decodeAnchorX(1.0106384f, -1.7857143f), this.decodeAnchorY(2.3142858f, 0.0f), this.decodeX(1.0106384f), this.decodeY(2.3142858f));
        this.path.lineTo(this.decodeX(1.9893618f), this.decodeY(2.3142858f));
        this.path.curveTo(this.decodeAnchorX(1.9893618f, 2.0714285f), this.decodeAnchorY(2.3142858f, 0.0f), this.decodeAnchorX(2.5f, 0.0f), this.decodeAnchorY(1.9166667f, 2.2142856f), this.decodeX(2.5f), this.decodeY(1.9166667f));
        this.path.lineTo(this.decodeX(2.5142853f), this.decodeY(1.0416666f));
        this.path.curveTo(this.decodeAnchorX(2.5142853f, 0.0f), this.decodeAnchorY(1.0416666f, -2.142857f), this.decodeAnchorX(1.9901216f, 2.142857f), this.decodeAnchorY(0.47142857f, 0.0f), this.decodeX(1.9901216f), this.decodeY(0.47142857f));
        this.path.lineTo(this.decodeX(1.4148936f), this.decodeY(0.4857143f));
        this.path.lineTo(this.decodeX(1.4133738f), this.decodeY(0.120000005f));
        this.path.closePath();
        return this.path;
    }
    
    private RoundRectangle2D decodeRoundRect1() {
        this.roundRect.setRoundRect(this.decodeX(0.4f), this.decodeY(0.6f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(2.6f) - this.decodeY(0.6f), 12.0, 12.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect2() {
        this.roundRect.setRoundRect(this.decodeX(0.4f), this.decodeY(0.4f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(2.4f) - this.decodeY(0.4f), 12.0, 12.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect3() {
        this.roundRect.setRoundRect(this.decodeX(0.6f), this.decodeY(0.6f), this.decodeX(2.4f) - this.decodeX(0.6f), this.decodeY(2.2f) - this.decodeY(0.6f), 9.0, 9.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect4() {
        this.roundRect.setRoundRect(this.decodeX(0.120000005f), this.decodeY(0.120000005f), this.decodeX(2.8800004f) - this.decodeX(0.120000005f), this.decodeY(2.6800003f) - this.decodeY(0.120000005f), 13.0, 13.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect5() {
        this.roundRect.setRoundRect(this.decodeX(0.4f), this.decodeY(0.6f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(2.6f) - this.decodeY(0.6f), 10.0, 10.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect6() {
        this.roundRect.setRoundRect(this.decodeX(0.4f), this.decodeY(0.4f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(2.4f) - this.decodeY(0.4f), 10.0, 10.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect7() {
        this.roundRect.setRoundRect(this.decodeX(0.6f), this.decodeY(0.6f), this.decodeX(2.4f) - this.decodeX(0.6f), this.decodeY(2.2f) - this.decodeY(0.6f), 8.0, 8.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect8() {
        this.roundRect.setRoundRect(this.decodeX(0.120000005f), this.decodeY(0.120000005f), this.decodeX(2.8800004f) - this.decodeX(0.120000005f), this.decodeY(2.6799998f) - this.decodeY(0.120000005f), 13.0, 13.0);
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
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 1.0041667f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25126263f * n3 + n, 1.0092592f * n4 + n2, new float[] { 0.0f, 0.06684492f, 0.13368984f, 0.56684494f, 1.0f }, new Color[] { this.color12, this.decodeColor(this.color12, this.color13, 0.5f), this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 1.0041667f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color15, this.decodeColor(this.color15, this.color16, 0.5f), this.color16 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25126263f * n3 + n, 1.0092592f * n4 + n2, new float[] { 0.0f, 0.06684492f, 0.13368984f, 0.56684494f, 1.0f }, new Color[] { this.color17, this.decodeColor(this.color17, this.color18, 0.5f), this.color18, this.decodeColor(this.color18, this.color19, 0.5f), this.color19 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 1.0041667f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color20, this.decodeColor(this.color20, this.color21, 0.5f), this.color21 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25126263f * n3 + n, 1.0092592f * n4 + n2, new float[] { 0.0f, 0.06684492f, 0.13368984f, 0.56684494f, 1.0f }, new Color[] { this.color22, this.decodeColor(this.color22, this.color23, 0.5f), this.color23, this.decodeColor(this.color23, this.color24, 0.5f), this.color24 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 1.0041667f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27 });
    }
    
    private Paint decodeGradient10(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25126263f * n3 + n, 1.0092592f * n4 + n2, new float[] { 0.0f, 0.06684492f, 0.13368984f, 0.56684494f, 1.0f }, new Color[] { this.color27, this.decodeColor(this.color27, this.color28, 0.5f), this.color28, this.decodeColor(this.color28, this.color28, 0.5f), this.color28 });
    }
}
