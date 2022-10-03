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

final class ComboBoxArrowButtonPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_ENABLED_MOUSEOVER = 3;
    static final int BACKGROUND_ENABLED_PRESSED = 4;
    static final int BACKGROUND_DISABLED_EDITABLE = 5;
    static final int BACKGROUND_ENABLED_EDITABLE = 6;
    static final int BACKGROUND_MOUSEOVER_EDITABLE = 7;
    static final int BACKGROUND_PRESSED_EDITABLE = 8;
    static final int BACKGROUND_SELECTED_EDITABLE = 9;
    static final int FOREGROUND_ENABLED = 10;
    static final int FOREGROUND_MOUSEOVER = 11;
    static final int FOREGROUND_DISABLED = 12;
    static final int FOREGROUND_PRESSED = 13;
    static final int FOREGROUND_SELECTED = 14;
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
    private Color color29;
    private Color color30;
    private Color color31;
    private Object[] componentColors;
    
    public ComboBoxArrowButtonPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", -0.6111111f, -0.110526316f, -0.74509805f, -247);
        this.color2 = this.decodeColor("nimbusBase", 0.021348298f, -0.56289876f, 0.2588235f, 0);
        this.color3 = this.decodeColor("nimbusBase", 0.010237217f, -0.55799407f, 0.20784312f, 0);
        this.color4 = new Color(255, 200, 0, 255);
        this.color5 = this.decodeColor("nimbusBase", 0.021348298f, -0.59223604f, 0.35294116f, 0);
        this.color6 = this.decodeColor("nimbusBase", 0.02391243f, -0.5774183f, 0.32549018f, 0);
        this.color7 = this.decodeColor("nimbusBase", 0.021348298f, -0.56722116f, 0.3098039f, 0);
        this.color8 = this.decodeColor("nimbusBase", 0.021348298f, -0.567841f, 0.31764704f, 0);
        this.color9 = this.decodeColor("nimbusBlueGrey", -0.6111111f, -0.110526316f, -0.74509805f, -191);
        this.color10 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.34585923f, -0.007843137f, 0);
        this.color11 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.095173776f, -0.25882354f, 0);
        this.color12 = this.decodeColor("nimbusBase", 0.004681647f, -0.6197143f, 0.43137252f, 0);
        this.color13 = this.decodeColor("nimbusBase", 0.0023007393f, -0.46825016f, 0.27058822f, 0);
        this.color14 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.43866998f, 0.24705881f, 0);
        this.color15 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4625541f, 0.35686272f, 0);
        this.color16 = this.decodeColor("nimbusBase", 0.0013483167f, -0.1769987f, -0.12156865f, 0);
        this.color17 = this.decodeColor("nimbusBase", 0.059279382f, 0.3642857f, -0.43529415f, 0);
        this.color18 = this.decodeColor("nimbusBase", 0.004681647f, -0.6198413f, 0.43921566f, 0);
        this.color19 = this.decodeColor("nimbusBase", 0.0023007393f, -0.48084703f, 0.33725488f, 0);
        this.color20 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4555341f, 0.3215686f, 0);
        this.color21 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4757143f, 0.43137252f, 0);
        this.color22 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.54901963f, 0);
        this.color23 = this.decodeColor("nimbusBase", -3.528595E-5f, 0.018606722f, -0.23137257f, 0);
        this.color24 = this.decodeColor("nimbusBase", -4.2033195E-4f, -0.38050595f, 0.20392156f, 0);
        this.color25 = this.decodeColor("nimbusBase", 7.13408E-4f, -0.064285696f, 0.027450979f, 0);
        this.color26 = this.decodeColor("nimbusBase", 0.0f, -0.00895375f, 0.007843137f, 0);
        this.color27 = this.decodeColor("nimbusBase", 8.9377165E-4f, -0.13853917f, 0.14509803f, 0);
        this.color28 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.37254906f, 0);
        this.color29 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.5254902f, 0);
        this.color30 = this.decodeColor("nimbusBase", 0.027408898f, -0.57391655f, 0.1490196f, 0);
        this.color31 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 5: {
                this.paintBackgroundDisabledAndEditable(graphics2D);
                break;
            }
            case 6: {
                this.paintBackgroundEnabledAndEditable(graphics2D);
                break;
            }
            case 7: {
                this.paintBackgroundMouseOverAndEditable(graphics2D);
                break;
            }
            case 8: {
                this.paintBackgroundPressedAndEditable(graphics2D);
                break;
            }
            case 9: {
                this.paintBackgroundSelectedAndEditable(graphics2D);
                break;
            }
            case 10: {
                this.paintForegroundEnabled(graphics2D);
                break;
            }
            case 11: {
                this.paintForegroundMouseOver(graphics2D);
                break;
            }
            case 12: {
                this.paintForegroundDisabled(graphics2D);
                break;
            }
            case 13: {
                this.paintForegroundPressed(graphics2D);
                break;
            }
            case 14: {
                this.paintForegroundSelected(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundDisabledAndEditable(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient1(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient2(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundEnabledAndEditable(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient4(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundMouseOverAndEditable(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient5(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient6(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundPressedAndEditable(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient8(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundSelectedAndEditable(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient8(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath5();
        graphics2D.setPaint(this.decodeGradient9(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundMouseOver(final Graphics2D graphics2D) {
        this.path = this.decodePath6();
        graphics2D.setPaint(this.decodeGradient9(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundDisabled(final Graphics2D graphics2D) {
        this.path = this.decodePath7();
        graphics2D.setPaint(this.color30);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundPressed(final Graphics2D graphics2D) {
        this.path = this.decodePath8();
        graphics2D.setPaint(this.color31);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundSelected(final Graphics2D graphics2D) {
        this.path = this.decodePath7();
        graphics2D.setPaint(this.color31);
        graphics2D.fill(this.path);
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(2.75f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(2.75f), this.decodeY(2.25f));
        this.path.curveTo(this.decodeAnchorX(2.75f, 0.0f), this.decodeAnchorY(2.25f, 4.0f), this.decodeAnchorX(2.125f, 3.0f), this.decodeAnchorY(2.875f, 0.0f), this.decodeX(2.125f), this.decodeY(2.875f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.875f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.25f));
        this.path.lineTo(this.decodeX(2.125f), this.decodeY(0.25f));
        this.path.curveTo(this.decodeAnchorX(2.125f, 3.0f), this.decodeAnchorY(0.25f, 0.0f), this.decodeAnchorX(2.75f, 0.0f), this.decodeAnchorY(0.875f, -3.0f), this.decodeX(2.75f), this.decodeY(0.875f));
        this.path.lineTo(this.decodeX(2.75f), this.decodeY(2.125f));
        this.path.curveTo(this.decodeAnchorX(2.75f, 0.0f), this.decodeAnchorY(2.125f, 3.0f), this.decodeAnchorX(2.125f, 3.0f), this.decodeAnchorY(2.75f, 0.0f), this.decodeX(2.125f), this.decodeY(2.75f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.75f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.25f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.85294116f), this.decodeY(2.639706f));
        this.path.lineTo(this.decodeX(0.85294116f), this.decodeY(2.639706f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(0.375f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(0.375f));
        this.path.curveTo(this.decodeAnchorX(2.0f, 4.0f), this.decodeAnchorY(0.375f, 0.0f), this.decodeAnchorX(2.625f, 0.0f), this.decodeAnchorY(1.0f, -4.0f), this.decodeX(2.625f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.625f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(2.625f, 0.0f), this.decodeAnchorY(2.0f, 4.0f), this.decodeAnchorX(2.0f, 4.0f), this.decodeAnchorY(2.625f, 0.0f), this.decodeX(2.0f), this.decodeY(2.625f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(2.625f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(0.375f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath5() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.9995915f), this.decodeY(1.3616071f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(0.8333333f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.8571429f));
        this.path.lineTo(this.decodeX(0.9995915f), this.decodeY(1.3616071f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath6() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.00625f), this.decodeY(1.3526785f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(0.8333333f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.8571429f));
        this.path.lineTo(this.decodeX(1.00625f), this.decodeY(1.3526785f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath7() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0117648f), this.decodeY(1.3616071f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(0.8333333f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.8571429f));
        this.path.lineTo(this.decodeX(1.0117648f), this.decodeY(1.3616071f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath8() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0242647f), this.decodeY(1.3526785f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(0.8333333f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.8571429f));
        this.path.lineTo(this.decodeX(1.0242647f), this.decodeY(1.3526785f));
        this.path.closePath();
        return this.path;
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
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.171875f, 0.34375f, 0.4815341f, 0.6193182f, 0.8096591f, 1.0f }, new Color[] { this.color5, this.decodeColor(this.color5, this.color6, 0.5f), this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.12299465f, 0.44652405f, 0.5441176f, 0.64171124f, 0.8208556f, 1.0f }, new Color[] { this.color12, this.decodeColor(this.color12, this.color13, 0.5f), this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14, this.decodeColor(this.color14, this.color15, 0.5f), this.color15 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color16, this.decodeColor(this.color16, this.color17, 0.5f), this.color17 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.12299465f, 0.44652405f, 0.5441176f, 0.64171124f, 0.81283426f, 0.98395723f }, new Color[] { this.color18, this.decodeColor(this.color18, this.color19, 0.5f), this.color19, this.decodeColor(this.color19, this.color20, 0.5f), this.color20, this.decodeColor(this.color20, this.color21, 0.5f), this.color21 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color22, this.decodeColor(this.color22, this.color23, 0.5f), this.color23 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.12299465f, 0.44652405f, 0.5441176f, 0.64171124f, 0.8208556f, 1.0f }, new Color[] { this.color24, this.decodeColor(this.color24, this.color25, 0.5f), this.color25, this.decodeColor(this.color25, this.color26, 0.5f), this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(1.0f * n3 + n, 0.5f * n4 + n2, 0.0f * n3 + n, 0.5f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color28, this.decodeColor(this.color28, this.color29, 0.5f), this.color29 });
    }
}
