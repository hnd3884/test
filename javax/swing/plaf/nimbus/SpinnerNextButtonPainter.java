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

final class SpinnerNextButtonPainter extends AbstractRegionPainter
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
    private Object[] componentColors;
    
    public SpinnerNextButtonPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBase", 0.021348298f, -0.56289876f, 0.2588235f, 0);
        this.color2 = this.decodeColor("nimbusBase", 0.010237217f, -0.5607143f, 0.2352941f, 0);
        this.color3 = this.decodeColor("nimbusBase", 0.021348298f, -0.59223604f, 0.35294116f, 0);
        this.color4 = this.decodeColor("nimbusBase", 0.016586483f, -0.5723659f, 0.31764704f, 0);
        this.color5 = this.decodeColor("nimbusBase", 0.021348298f, -0.56182265f, 0.24705881f, 0);
        this.color6 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.34585923f, -0.007843137f, 0);
        this.color7 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.27207792f, -0.11764708f, 0);
        this.color8 = this.decodeColor("nimbusBase", 0.004681647f, -0.6197143f, 0.43137252f, 0);
        this.color9 = this.decodeColor("nimbusBase", -0.0012707114f, -0.5078604f, 0.3098039f, 0);
        this.color10 = this.decodeColor("nimbusBase", -0.0028941035f, -0.4800539f, 0.28235292f, 0);
        this.color11 = this.decodeColor("nimbusBase", 0.0023007393f, -0.3622768f, -0.04705882f, 0);
        this.color12 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.color13 = this.decodeColor("nimbusBase", 0.0013483167f, -0.1769987f, -0.12156865f, 0);
        this.color14 = this.decodeColor("nimbusBase", 0.0013483167f, 0.039961398f, -0.25882354f, 0);
        this.color15 = this.decodeColor("nimbusBase", 0.004681647f, -0.6198413f, 0.43921566f, 0);
        this.color16 = this.decodeColor("nimbusBase", -0.0012707114f, -0.51502466f, 0.3607843f, 0);
        this.color17 = this.decodeColor("nimbusBase", 0.0021564364f, -0.49097747f, 0.34509802f, 0);
        this.color18 = this.decodeColor("nimbusBase", 5.2034855E-5f, -0.38743842f, 0.019607842f, 0);
        this.color19 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.54901963f, 0);
        this.color20 = this.decodeColor("nimbusBase", 0.08801502f, 0.3642857f, -0.454902f, 0);
        this.color21 = this.decodeColor("nimbusBase", -4.2033195E-4f, -0.38050595f, 0.20392156f, 0);
        this.color22 = this.decodeColor("nimbusBase", 2.9569864E-4f, -0.15470162f, 0.07058823f, 0);
        this.color23 = this.decodeColor("nimbusBase", -4.6235323E-4f, -0.09571427f, 0.039215684f, 0);
        this.color24 = this.decodeColor("nimbusBase", 0.018363237f, 0.18135887f, -0.227451f, 0);
        this.color25 = new Color(255, 200, 0, 255);
        this.color26 = this.decodeColor("nimbusBase", 0.021348298f, -0.58106947f, 0.16862744f, 0);
        this.color27 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.043137252f, 0);
        this.color28 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.24313727f, 0);
        this.color29 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, 0);
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
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color5);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient4(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color11);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath5();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient5(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color11);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundMouseOverAndFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath5();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient6(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color18);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundPressedAndFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath5();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.path);
        this.path = this.decodePath6();
        graphics2D.setPaint(this.decodeGradient8(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient9(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color24);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundMouseOver(final Graphics2D graphics2D) {
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient6(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient10(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color18);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundPressed(final Graphics2D graphics2D) {
        this.path = this.decodePath6();
        graphics2D.setPaint(this.decodeGradient8(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient11(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color24);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color25);
        graphics2D.fill(this.rect);
    }
    
    private void paintForegroundDisabled(final Graphics2D graphics2D) {
        this.path = this.decodePath7();
        graphics2D.setPaint(this.color26);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath7();
        graphics2D.setPaint(this.decodeGradient12(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath8();
        graphics2D.setPaint(this.decodeGradient12(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundMouseOverAndFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath8();
        graphics2D.setPaint(this.decodeGradient12(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundPressedAndFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath9();
        graphics2D.setPaint(this.color29);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundMouseOver(final Graphics2D graphics2D) {
        this.path = this.decodePath7();
        graphics2D.setPaint(this.decodeGradient12(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundPressed(final Graphics2D graphics2D) {
        this.path = this.decodePath9();
        graphics2D.setPaint(this.color29);
        graphics2D.fill(this.path);
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.2857143f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(0.2857143f, 0.0f), this.decodeAnchorX(2.0f, -3.6363637f), this.decodeAnchorY(0.2857143f, 0.0f), this.decodeX(2.0f), this.decodeY(0.2857143f));
        this.path.curveTo(this.decodeAnchorX(2.0f, 3.6363637f), this.decodeAnchorY(0.2857143f, 0.0f), this.decodeAnchorX(2.7142859f, -0.022727273f), this.decodeAnchorY(1.0f, -3.75f), this.decodeX(2.7142859f), this.decodeY(1.0f));
        this.path.curveTo(this.decodeAnchorX(2.7142859f, 0.022727273f), this.decodeAnchorY(1.0f, 3.75f), this.decodeAnchorX(2.7142859f, 0.0f), this.decodeAnchorY(3.0f, 0.0f), this.decodeX(2.7142859f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(0.42857143f));
        this.path.curveTo(this.decodeAnchorX(1.0f, 0.0f), this.decodeAnchorY(0.42857143f, 0.0f), this.decodeAnchorX(2.0f, -3.0f), this.decodeAnchorY(0.42857143f, 0.0f), this.decodeX(2.0f), this.decodeY(0.42857143f));
        this.path.curveTo(this.decodeAnchorX(2.0f, 3.0f), this.decodeAnchorY(0.42857143f, 0.0f), this.decodeAnchorX(2.5714285f, 0.0f), this.decodeAnchorY(1.0f, -2.0f), this.decodeX(2.5714285f), this.decodeY(1.0f));
        this.path.curveTo(this.decodeAnchorX(2.5714285f, 0.0f), this.decodeAnchorY(1.0f, 2.0f), this.decodeAnchorX(2.5714285f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeX(2.5714285f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(2.0f), this.decodeX(2.5714285f) - this.decodeX(1.0f), this.decodeY(3.0f) - this.decodeY(2.0f));
        return this.rect;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.2857143f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(0.2857143f));
        this.path.curveTo(this.decodeAnchorX(2.0f, 3.6363637f), this.decodeAnchorY(0.2857143f, 0.0f), this.decodeAnchorX(2.7142859f, -0.022727273f), this.decodeAnchorY(1.0f, -3.75f), this.decodeX(2.7142859f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.7142859f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(0.42857143f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(0.42857143f));
        this.path.curveTo(this.decodeAnchorX(2.0f, 3.0f), this.decodeAnchorY(0.42857143f, 0.0f), this.decodeAnchorX(2.5714285f, 0.0f), this.decodeAnchorY(1.0f, -2.0f), this.decodeX(2.5714285f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.5714285f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath5() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.08571429f));
        this.path.lineTo(this.decodeX(2.142857f), this.decodeY(0.08571429f));
        this.path.curveTo(this.decodeAnchorX(2.142857f, 3.4f), this.decodeAnchorY(0.08571429f, 0.0f), this.decodeAnchorX(2.9142857f, 0.0f), this.decodeAnchorY(1.0f, -3.4f), this.decodeX(2.9142857f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.9142857f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath6() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.2857143f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(0.2857143f));
        this.path.curveTo(this.decodeAnchorX(2.0f, 3.4545455f), this.decodeAnchorY(0.2857143f, 0.0f), this.decodeAnchorX(2.7142859f, -0.022727273f), this.decodeAnchorY(1.0f, -3.4772727f), this.decodeX(2.7142859f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.7142859f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(0.0f), this.decodeX(0.0f) - this.decodeX(0.0f), this.decodeY(0.0f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Path2D decodePath7() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.490909f), this.decodeY(1.0284091f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath8() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.490909f), this.decodeY(1.3522727f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath9() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.5045455f), this.decodeY(1.0795455f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(2.0f));
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
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.36497328f, 0.72994655f, 0.8649733f, 1.0f }, new Color[] { this.color8, this.decodeColor(this.color8, this.color9, 0.5f), this.color9, this.decodeColor(this.color9, this.color10, 0.5f), this.color10 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.37566844f, 0.7513369f, 0.8756684f, 1.0f }, new Color[] { this.color8, this.decodeColor(this.color8, this.color9, 0.5f), this.color9, this.decodeColor(this.color9, this.color10, 0.5f), this.color10 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.37967914f, 0.7593583f, 0.87967914f, 1.0f }, new Color[] { this.color15, this.decodeColor(this.color15, this.color16, 0.5f), this.color16, this.decodeColor(this.color16, this.color17, 0.5f), this.color17 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color19, this.decodeColor(this.color19, this.color20, 0.5f), this.color20 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.37165776f, 0.7433155f, 0.8716577f, 1.0f }, new Color[] { this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22, this.decodeColor(this.color22, this.color23, 0.5f), this.color23 });
    }
    
    private Paint decodeGradient10(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.3970588f, 0.7941176f, 0.89705884f, 1.0f }, new Color[] { this.color15, this.decodeColor(this.color15, this.color16, 0.5f), this.color16, this.decodeColor(this.color16, this.color17, 0.5f), this.color17 });
    }
    
    private Paint decodeGradient11(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.4318182f, 0.8636364f, 0.9318182f, 1.0f }, new Color[] { this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22, this.decodeColor(this.color22, this.color23, 0.5f), this.color23 });
    }
    
    private Paint decodeGradient12(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.48636365f * n3 + n, 0.0116959065f * n4 + n2, 0.4909091f * n3 + n, 0.8888889f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color27, this.decodeColor(this.color27, this.color28, 0.5f), this.color28 });
    }
}
