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

final class SpinnerPreviousButtonPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_FOCUSED = 3;
    static final int BACKGROUND_MOUSEOVER_FOCUSED = 4;
    static final int BACKGROUND_PRESSED_FOCUSED = 5;
    static final int BACKGROUND_MOUSEOVER = 6;
    static final int BACKGROUND_PRESSED = 7;
    static final int FOREGROUND_DISABLED = 8;
    static final int FOREGROUND_ENABLED = 9;
    static final int FOREGROUND_FOCUSED = 10;
    static final int FOREGROUND_MOUSEOVER_FOCUSED = 11;
    static final int FOREGROUND_PRESSED_FOCUSED = 12;
    static final int FOREGROUND_MOUSEOVER = 13;
    static final int FOREGROUND_PRESSED = 14;
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
    private Object[] componentColors;
    
    public SpinnerPreviousButtonPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBase", 0.015098333f, -0.5557143f, 0.2352941f, 0);
        this.color2 = this.decodeColor("nimbusBase", 0.010237217f, -0.55799407f, 0.20784312f, 0);
        this.color3 = this.decodeColor("nimbusBase", 0.018570602f, -0.5821429f, 0.32941175f, 0);
        this.color4 = this.decodeColor("nimbusBase", 0.021348298f, -0.56722116f, 0.3098039f, 0);
        this.color5 = this.decodeColor("nimbusBase", 0.021348298f, -0.567841f, 0.31764704f, 0);
        this.color6 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.0033834577f, -0.30588236f, -148);
        this.color7 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.2583558f, -0.13333336f, 0);
        this.color8 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.095173776f, -0.25882354f, 0);
        this.color9 = this.decodeColor("nimbusBase", 0.004681647f, -0.5383692f, 0.33725488f, 0);
        this.color10 = this.decodeColor("nimbusBase", -0.0017285943f, -0.44453782f, 0.25098038f, 0);
        this.color11 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.43866998f, 0.24705881f, 0);
        this.color12 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4625541f, 0.35686272f, 0);
        this.color13 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.color14 = this.decodeColor("nimbusBase", 0.0013483167f, 0.088923395f, -0.2784314f, 0);
        this.color15 = this.decodeColor("nimbusBase", 0.059279382f, 0.3642857f, -0.43529415f, 0);
        this.color16 = this.decodeColor("nimbusBase", 0.0010585189f, -0.541452f, 0.4078431f, 0);
        this.color17 = this.decodeColor("nimbusBase", 0.00254488f, -0.4608264f, 0.32549018f, 0);
        this.color18 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4555341f, 0.3215686f, 0);
        this.color19 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4757143f, 0.43137252f, 0);
        this.color20 = this.decodeColor("nimbusBase", 0.061133325f, 0.3642857f, -0.427451f, 0);
        this.color21 = this.decodeColor("nimbusBase", -3.528595E-5f, 0.018606722f, -0.23137257f, 0);
        this.color22 = this.decodeColor("nimbusBase", 8.354783E-4f, -0.2578073f, 0.12549019f, 0);
        this.color23 = this.decodeColor("nimbusBase", 8.9377165E-4f, -0.01599598f, 0.007843137f, 0);
        this.color24 = this.decodeColor("nimbusBase", 0.0f, -0.00895375f, 0.007843137f, 0);
        this.color25 = this.decodeColor("nimbusBase", 8.9377165E-4f, -0.13853917f, 0.14509803f, 0);
        this.color26 = this.decodeColor("nimbusBlueGrey", -0.6111111f, -0.110526316f, -0.63529414f, -179);
        this.color27 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -186);
        this.color28 = this.decodeColor("nimbusBase", 0.018570602f, -0.56714284f, 0.1372549f, 0);
        this.color29 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.54901963f, 0);
        this.color30 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, 0);
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
                this.paintBackgroundMouseOverAndFocused(graphics2D);
                break;
            }
            case 5: {
                this.paintBackgroundPressedAndFocused(graphics2D);
                break;
            }
            case 6: {
                this.paintBackgroundMouseOver(graphics2D);
                break;
            }
            case 7: {
                this.paintBackgroundPressed(graphics2D);
                break;
            }
            case 8: {
                this.paintForegroundDisabled(graphics2D);
                break;
            }
            case 9: {
                this.paintForegroundEnabled(graphics2D);
                break;
            }
            case 10: {
                this.paintForegroundFocused(graphics2D);
                break;
            }
            case 11: {
                this.paintForegroundMouseOverAndFocused(graphics2D);
                break;
            }
            case 12: {
                this.paintForegroundPressedAndFocused(graphics2D);
                break;
            }
            case 13: {
                this.paintForegroundMouseOver(graphics2D);
                break;
            }
            case 14: {
                this.paintForegroundPressed(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundDisabled(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient1(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient2(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.path);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient4(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.path);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient4(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundMouseOverAndFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath5();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.path);
        this.path = this.decodePath6();
        graphics2D.setPaint(this.decodeGradient5(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath7();
        graphics2D.setPaint(this.decodeGradient6(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundPressedAndFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.path);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient8(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundMouseOver(final Graphics2D graphics2D) {
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color26);
        graphics2D.fill(this.path);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient5(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient6(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundPressed(final Graphics2D graphics2D) {
        this.path = this.decodePath8();
        graphics2D.setPaint(this.color27);
        graphics2D.fill(this.path);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient8(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundDisabled(final Graphics2D graphics2D) {
        this.path = this.decodePath9();
        graphics2D.setPaint(this.color28);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath9();
        graphics2D.setPaint(this.color29);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath9();
        graphics2D.setPaint(this.color29);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundMouseOverAndFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath9();
        graphics2D.setPaint(this.color29);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundPressedAndFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath9();
        graphics2D.setPaint(this.color30);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundMouseOver(final Graphics2D graphics2D) {
        this.path = this.decodePath9();
        graphics2D.setPaint(this.color29);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundPressed(final Graphics2D graphics2D) {
        this.path = this.decodePath9();
        graphics2D.setPaint(this.color30);
        graphics2D.fill(this.path);
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.6666667f));
        this.path.lineTo(this.decodeX(2.142857f), this.decodeY(2.6666667f));
        this.path.curveTo(this.decodeAnchorX(2.142857f, 3.0f), this.decodeAnchorY(2.6666667f, 0.0f), this.decodeAnchorX(2.7142859f, 0.0f), this.decodeAnchorY(2.0f, 2.0f), this.decodeX(2.7142859f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(2.7142859f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(2.5f));
        this.path.lineTo(this.decodeX(2.142857f), this.decodeY(2.5f));
        this.path.curveTo(this.decodeAnchorX(2.142857f, 2.0f), this.decodeAnchorY(2.5f, 0.0f), this.decodeAnchorX(2.5714285f, 0.0f), this.decodeAnchorY(2.0f, 1.0f), this.decodeX(2.5714285f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(2.5714285f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(2.6666667f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.8333333f));
        this.path.lineTo(this.decodeX(2.0324676f), this.decodeY(2.8333333f));
        this.path.curveTo(this.decodeAnchorX(2.0324676f, 2.1136363f), this.decodeAnchorY(2.8333333f, 0.0f), this.decodeAnchorX(2.7142859f, 0.0f), this.decodeAnchorY(2.0f, 3.0f), this.decodeX(2.7142859f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.6666667f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.8999999f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(2.8999999f));
        this.path.curveTo(this.decodeAnchorX(2.2f, 3.0f), this.decodeAnchorY(2.8999999f, 0.0f), this.decodeAnchorX(2.9142857f, 0.0f), this.decodeAnchorY(2.2333333f, 3.0f), this.decodeX(2.9142857f), this.decodeY(2.2333333f));
        this.path.lineTo(this.decodeX(2.9142857f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath5() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.8999999f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(2.8999999f));
        this.path.curveTo(this.decodeAnchorX(2.2f, 3.0f), this.decodeAnchorY(2.8999999f, 0.0f), this.decodeAnchorX(2.9142857f, 0.0f), this.decodeAnchorY(2.2333333f, 3.0f), this.decodeX(2.9142857f), this.decodeY(2.2333333f));
        this.path.lineTo(this.decodeX(2.9142857f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath6() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.6666667f));
        this.path.lineTo(this.decodeX(2.142857f), this.decodeY(2.6666667f));
        this.path.curveTo(this.decodeAnchorX(2.142857f, 3.0f), this.decodeAnchorY(2.6666667f, 0.0f), this.decodeAnchorX(2.7142859f, 0.0f), this.decodeAnchorY(2.0f, 2.0f), this.decodeX(2.7142859f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(2.7142859f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath7() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(2.5f));
        this.path.lineTo(this.decodeX(2.142857f), this.decodeY(2.5f));
        this.path.curveTo(this.decodeAnchorX(2.142857f, 2.0f), this.decodeAnchorY(2.5f, 0.0f), this.decodeAnchorX(2.5714285f, 0.0f), this.decodeAnchorY(2.0f, 1.0f), this.decodeX(2.5714285f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(2.5714285f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(0.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath8() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(2.6666667f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.8333333f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(2.8333333f, 0.0f), this.decodeAnchorX(2.0324676f, -2.1136363f), this.decodeAnchorY(2.8333333f, 0.0f), this.decodeX(2.0324676f), this.decodeY(2.8333333f));
        this.path.curveTo(this.decodeAnchorX(2.0324676f, 2.1136363f), this.decodeAnchorY(2.8333333f, 0.0f), this.decodeAnchorX(2.7142859f, 0.0f), this.decodeAnchorY(2.0f, 3.0f), this.decodeX(2.7142859f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.6666667f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath9() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.5045455f), this.decodeY(1.9943181f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.0f));
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
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.05748663f, 0.11497326f, 0.55748665f, 1.0f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.05748663f, 0.11497326f, 0.2419786f, 0.36898395f, 0.684492f, 1.0f }, new Color[] { this.color9, this.decodeColor(this.color9, this.color10, 0.5f), this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11, this.decodeColor(this.color11, this.color12, 0.5f), this.color12 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color14, this.decodeColor(this.color14, this.color15, 0.5f), this.color15 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.05748663f, 0.11497326f, 0.2419786f, 0.36898395f, 0.684492f, 1.0f }, new Color[] { this.color16, this.decodeColor(this.color16, this.color17, 0.5f), this.color17, this.decodeColor(this.color17, this.color18, 0.5f), this.color18, this.decodeColor(this.color18, this.color19, 0.5f), this.color19 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color20, this.decodeColor(this.color20, this.color21, 0.5f), this.color21 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.05748663f, 0.11497326f, 0.2419786f, 0.36898395f, 0.684492f, 1.0f }, new Color[] { this.color22, this.decodeColor(this.color22, this.color23, 0.5f), this.color23, this.decodeColor(this.color23, this.color24, 0.5f), this.color24, this.decodeColor(this.color24, this.color25, 0.5f), this.color25 });
    }
}
