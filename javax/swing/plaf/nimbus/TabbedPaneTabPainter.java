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

final class TabbedPaneTabPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_ENABLED = 1;
    static final int BACKGROUND_ENABLED_MOUSEOVER = 2;
    static final int BACKGROUND_ENABLED_PRESSED = 3;
    static final int BACKGROUND_DISABLED = 4;
    static final int BACKGROUND_SELECTED_DISABLED = 5;
    static final int BACKGROUND_SELECTED = 6;
    static final int BACKGROUND_SELECTED_MOUSEOVER = 7;
    static final int BACKGROUND_SELECTED_PRESSED = 8;
    static final int BACKGROUND_SELECTED_FOCUSED = 9;
    static final int BACKGROUND_SELECTED_MOUSEOVER_FOCUSED = 10;
    static final int BACKGROUND_SELECTED_PRESSED_FOCUSED = 11;
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
    private Color color32;
    private Color color33;
    private Color color34;
    private Color color35;
    private Color color36;
    private Color color37;
    private Object[] componentColors;
    
    public TabbedPaneTabPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBase", 0.032459438f, -0.55535716f, -0.109803945f, 0);
        this.color2 = this.decodeColor("nimbusBase", 0.08801502f, 0.3642857f, -0.4784314f, 0);
        this.color3 = this.decodeColor("nimbusBase", 0.08801502f, -0.63174605f, 0.43921566f, 0);
        this.color4 = this.decodeColor("nimbusBase", 0.05468172f, -0.6145278f, 0.37647057f, 0);
        this.color5 = this.decodeColor("nimbusBase", 0.032459438f, -0.5953556f, 0.32549018f, 0);
        this.color6 = this.decodeColor("nimbusBase", 0.032459438f, -0.54616207f, -0.02352941f, 0);
        this.color7 = this.decodeColor("nimbusBase", 0.08801502f, -0.6317773f, 0.4470588f, 0);
        this.color8 = this.decodeColor("nimbusBase", 0.021348298f, -0.61547136f, 0.41960782f, 0);
        this.color9 = this.decodeColor("nimbusBase", 0.032459438f, -0.5985242f, 0.39999998f, 0);
        this.color10 = this.decodeColor("nimbusBase", 0.08801502f, 0.3642857f, -0.52156866f, 0);
        this.color11 = this.decodeColor("nimbusBase", 0.027408898f, -0.5847884f, 0.2980392f, 0);
        this.color12 = this.decodeColor("nimbusBase", 0.035931647f, -0.5553123f, 0.23137254f, 0);
        this.color13 = this.decodeColor("nimbusBase", 0.029681683f, -0.5281874f, 0.18039215f, 0);
        this.color14 = this.decodeColor("nimbusBase", 0.03801495f, -0.5456242f, 0.3215686f, 0);
        this.color15 = this.decodeColor("nimbusBase", 0.032459438f, -0.59181184f, 0.25490195f, 0);
        this.color16 = this.decodeColor("nimbusBase", 0.05468172f, -0.58308274f, 0.19607842f, 0);
        this.color17 = this.decodeColor("nimbusBase", 0.046348333f, -0.6006266f, 0.34509802f, 0);
        this.color18 = this.decodeColor("nimbusBase", 0.046348333f, -0.60015875f, 0.3333333f, 0);
        this.color19 = this.decodeColor("nimbusBase", 0.004681647f, -0.6197143f, 0.43137252f, 0);
        this.color20 = this.decodeColor("nimbusBase", 7.13408E-4f, -0.543609f, 0.34509802f, 0);
        this.color21 = this.decodeColor("nimbusBase", -0.0020751357f, -0.45610264f, 0.2588235f, 0);
        this.color22 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.43866998f, 0.24705881f, 0);
        this.color23 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.44879842f, 0.29019606f, 0);
        this.color24 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.08776909f, -0.2627451f, 0);
        this.color25 = this.decodeColor("nimbusBase", 0.06332368f, 0.3642857f, -0.4431373f, 0);
        this.color26 = this.decodeColor("nimbusBase", 0.004681647f, -0.6198413f, 0.43921566f, 0);
        this.color27 = this.decodeColor("nimbusBase", -0.0022627711f, -0.5335866f, 0.372549f, 0);
        this.color28 = this.decodeColor("nimbusBase", -0.0017285943f, -0.4608264f, 0.32549018f, 0);
        this.color29 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4555341f, 0.3215686f, 0);
        this.color30 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.46404046f, 0.36470586f, 0);
        this.color31 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.54901963f, 0);
        this.color32 = this.decodeColor("nimbusBase", -4.2033195E-4f, -0.38050595f, 0.20392156f, 0);
        this.color33 = this.decodeColor("nimbusBase", 0.0013483167f, -0.16401619f, 0.0745098f, 0);
        this.color34 = this.decodeColor("nimbusBase", -0.0010001659f, -0.01599598f, 0.007843137f, 0);
        this.color35 = this.decodeColor("nimbusBase", 0.0f, 0.0f, 0.0f, 0);
        this.color36 = this.decodeColor("nimbusBase", 0.0018727183f, -0.038398862f, 0.035294116f, 0);
        this.color37 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 1: {
                this.paintBackgroundEnabled(graphics2D);
                break;
            }
            case 2: {
                this.paintBackgroundEnabledAndMouseOver(graphics2D);
                break;
            }
            case 3: {
                this.paintBackgroundEnabledAndPressed(graphics2D);
                break;
            }
            case 4: {
                this.paintBackgroundDisabled(graphics2D);
                break;
            }
            case 5: {
                this.paintBackgroundSelectedAndDisabled(graphics2D);
                break;
            }
            case 6: {
                this.paintBackgroundSelected(graphics2D);
                break;
            }
            case 7: {
                this.paintBackgroundSelectedAndMouseOver(graphics2D);
                break;
            }
            case 8: {
                this.paintBackgroundSelectedAndPressed(graphics2D);
                break;
            }
            case 9: {
                this.paintBackgroundSelectedAndFocused(graphics2D);
                break;
            }
            case 10: {
                this.paintBackgroundSelectedAndMouseOverAndFocused(graphics2D);
                break;
            }
            case 11: {
                this.paintBackgroundSelectedAndPressedAndFocused(graphics2D);
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
        graphics2D.setPaint(this.decodeGradient1(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient2(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundEnabledAndMouseOver(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient4(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundEnabledAndPressed(final Graphics2D graphics2D) {
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient5(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient6(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundDisabled(final Graphics2D graphics2D) {
        this.path = this.decodePath5();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath6();
        graphics2D.setPaint(this.decodeGradient8(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundSelectedAndDisabled(final Graphics2D graphics2D) {
        this.path = this.decodePath7();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient9(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundSelected(final Graphics2D graphics2D) {
        this.path = this.decodePath7();
        graphics2D.setPaint(this.decodeGradient10(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient9(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundSelectedAndMouseOver(final Graphics2D graphics2D) {
        this.path = this.decodePath8();
        graphics2D.setPaint(this.decodeGradient11(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath9();
        graphics2D.setPaint(this.decodeGradient12(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundSelectedAndPressed(final Graphics2D graphics2D) {
        this.path = this.decodePath8();
        graphics2D.setPaint(this.decodeGradient13(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath9();
        graphics2D.setPaint(this.decodeGradient14(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundSelectedAndFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient10(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath10();
        graphics2D.setPaint(this.decodeGradient9(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath11();
        graphics2D.setPaint(this.color37);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundSelectedAndMouseOverAndFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath12();
        graphics2D.setPaint(this.decodeGradient11(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath13();
        graphics2D.setPaint(this.decodeGradient12(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath14();
        graphics2D.setPaint(this.color37);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundSelectedAndPressedAndFocused(final Graphics2D graphics2D) {
        this.path = this.decodePath12();
        graphics2D.setPaint(this.decodeGradient13(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath13();
        graphics2D.setPaint(this.decodeGradient14(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath14();
        graphics2D.setPaint(this.color37);
        graphics2D.fill(this.path);
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.71428573f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(0.71428573f, -3.0f), this.decodeAnchorX(0.71428573f, -3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeX(0.71428573f), this.decodeY(0.0f));
        this.path.curveTo(this.decodeAnchorX(0.71428573f, 3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeAnchorX(2.2857144f, -3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeX(2.2857144f), this.decodeY(0.0f));
        this.path.curveTo(this.decodeAnchorX(2.2857144f, 3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(0.71428573f, -3.0f), this.decodeX(3.0f), this.decodeY(0.71428573f));
        this.path.curveTo(this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(0.71428573f, 3.0f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(3.0f, 0.0f), this.decodeX(3.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(3.0f, 0.0f), this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(0.71428573f, 3.0f), this.decodeX(0.0f), this.decodeY(0.71428573f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.14285715f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(0.14285715f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeAnchorX(0.14285715f, 0.0f), this.decodeAnchorY(0.85714287f, 3.5555556f), this.decodeX(0.14285715f), this.decodeY(0.85714287f));
        this.path.curveTo(this.decodeAnchorX(0.14285715f, 0.0f), this.decodeAnchorY(0.85714287f, -3.5555556f), this.decodeAnchorX(0.85714287f, -3.4444444f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeX(0.85714287f), this.decodeY(0.14285715f));
        this.path.curveTo(this.decodeAnchorX(0.85714287f, 3.4444444f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeAnchorX(2.142857f, -3.3333333f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeX(2.142857f), this.decodeY(0.14285715f));
        this.path.curveTo(this.decodeAnchorX(2.142857f, 3.3333333f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeAnchorX(2.857143f, 0.0f), this.decodeAnchorY(0.85714287f, -3.2777777f), this.decodeX(2.857143f), this.decodeY(0.85714287f));
        this.path.curveTo(this.decodeAnchorX(2.857143f, 0.0f), this.decodeAnchorY(0.85714287f, 3.2777777f), this.decodeAnchorX(2.857143f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeX(2.857143f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(0.14285715f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.71428573f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.055555556f), this.decodeAnchorY(0.71428573f, 2.6111112f), this.decodeAnchorX(0.8333333f, -2.5f), this.decodeAnchorY(0.0f, 0.0f), this.decodeX(0.8333333f), this.decodeY(0.0f));
        this.path.curveTo(this.decodeAnchorX(0.8333333f, 2.5f), this.decodeAnchorY(0.0f, 0.0f), this.decodeAnchorX(2.2857144f, -2.7222223f), this.decodeAnchorY(0.0f, 0.0f), this.decodeX(2.2857144f), this.decodeY(0.0f));
        this.path.curveTo(this.decodeAnchorX(2.2857144f, 2.7222223f), this.decodeAnchorY(0.0f, 0.0f), this.decodeAnchorX(3.0f, -0.055555556f), this.decodeAnchorY(0.71428573f, -2.7222223f), this.decodeX(3.0f), this.decodeY(0.71428573f));
        this.path.curveTo(this.decodeAnchorX(3.0f, 0.055555556f), this.decodeAnchorY(0.71428573f, 2.7222223f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(3.0f, 0.0f), this.decodeX(3.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(3.0f, 0.0f), this.decodeAnchorX(0.0f, -0.055555556f), this.decodeAnchorY(0.71428573f, -2.6111112f), this.decodeX(0.0f), this.decodeY(0.71428573f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.16666667f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(0.85714287f, 3.6666667f), this.decodeX(0.16666667f), this.decodeY(0.85714287f));
        this.path.curveTo(this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(0.85714287f, -3.6666667f), this.decodeAnchorX(1.0f, -3.5555556f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeX(1.0f), this.decodeY(0.14285715f));
        this.path.curveTo(this.decodeAnchorX(1.0f, 3.5555556f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeAnchorX(2.142857f, -3.5f), this.decodeAnchorY(0.14285715f, 0.055555556f), this.decodeX(2.142857f), this.decodeY(0.14285715f));
        this.path.curveTo(this.decodeAnchorX(2.142857f, 3.5f), this.decodeAnchorY(0.14285715f, -0.055555556f), this.decodeAnchorX(2.857143f, 0.055555556f), this.decodeAnchorY(0.85714287f, -3.6666667f), this.decodeX(2.857143f), this.decodeY(0.85714287f));
        this.path.curveTo(this.decodeAnchorX(2.857143f, -0.055555556f), this.decodeAnchorY(0.85714287f, 3.6666667f), this.decodeAnchorX(2.857143f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeX(2.857143f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(0.16666667f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath5() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.8333333f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(0.8333333f, -3.0f), this.decodeAnchorX(0.71428573f, -3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeX(0.71428573f), this.decodeY(0.0f));
        this.path.curveTo(this.decodeAnchorX(0.71428573f, 3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeAnchorX(2.2857144f, -3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeX(2.2857144f), this.decodeY(0.0f));
        this.path.curveTo(this.decodeAnchorX(2.2857144f, 3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(0.8333333f, -3.0f), this.decodeX(3.0f), this.decodeY(0.8333333f));
        this.path.curveTo(this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(0.8333333f, 3.0f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(3.0f, 0.0f), this.decodeX(3.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(3.0f, 0.0f), this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(0.8333333f, 3.0f), this.decodeX(0.0f), this.decodeY(0.8333333f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath6() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.14285715f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(0.14285715f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeAnchorX(0.14285715f, 0.0f), this.decodeAnchorY(1.0f, 3.5555556f), this.decodeX(0.14285715f), this.decodeY(1.0f));
        this.path.curveTo(this.decodeAnchorX(0.14285715f, 0.0f), this.decodeAnchorY(1.0f, -3.5555556f), this.decodeAnchorX(0.85714287f, -3.4444444f), this.decodeAnchorY(0.16666667f, 0.0f), this.decodeX(0.85714287f), this.decodeY(0.16666667f));
        this.path.curveTo(this.decodeAnchorX(0.85714287f, 3.4444444f), this.decodeAnchorY(0.16666667f, 0.0f), this.decodeAnchorX(2.142857f, -3.3333333f), this.decodeAnchorY(0.16666667f, 0.0f), this.decodeX(2.142857f), this.decodeY(0.16666667f));
        this.path.curveTo(this.decodeAnchorX(2.142857f, 3.3333333f), this.decodeAnchorY(0.16666667f, 0.0f), this.decodeAnchorX(2.857143f, 0.0f), this.decodeAnchorY(1.0f, -3.2777777f), this.decodeX(2.857143f), this.decodeY(1.0f));
        this.path.curveTo(this.decodeAnchorX(2.857143f, 0.0f), this.decodeAnchorY(1.0f, 3.2777777f), this.decodeAnchorX(2.857143f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeX(2.857143f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(0.14285715f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath7() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.71428573f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(0.71428573f, -3.0f), this.decodeAnchorX(0.71428573f, -3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeX(0.71428573f), this.decodeY(0.0f));
        this.path.curveTo(this.decodeAnchorX(0.71428573f, 3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeAnchorX(2.2857144f, -3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeX(2.2857144f), this.decodeY(0.0f));
        this.path.curveTo(this.decodeAnchorX(2.2857144f, 3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(0.71428573f, -3.0f), this.decodeX(3.0f), this.decodeY(0.71428573f));
        this.path.curveTo(this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(0.71428573f, 3.0f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeX(3.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(0.71428573f, 3.0f), this.decodeX(0.0f), this.decodeY(0.71428573f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath8() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.71428573f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(0.71428573f, -3.0f), this.decodeAnchorX(0.5555556f, -3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeX(0.5555556f), this.decodeY(0.0f));
        this.path.curveTo(this.decodeAnchorX(0.5555556f, 3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeAnchorX(2.4444444f, -3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeX(2.4444444f), this.decodeY(0.0f));
        this.path.curveTo(this.decodeAnchorX(2.4444444f, 3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(0.71428573f, -3.0f), this.decodeX(3.0f), this.decodeY(0.71428573f));
        this.path.curveTo(this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(0.71428573f, 3.0f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeX(3.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(0.71428573f, 3.0f), this.decodeX(0.0f), this.decodeY(0.71428573f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath9() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.11111111f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(0.11111111f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeAnchorX(0.11111111f, 0.0f), this.decodeAnchorY(0.85714287f, 3.5555556f), this.decodeX(0.11111111f), this.decodeY(0.85714287f));
        this.path.curveTo(this.decodeAnchorX(0.11111111f, 0.0f), this.decodeAnchorY(0.85714287f, -3.5555556f), this.decodeAnchorX(0.6666667f, -3.4444444f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeX(0.6666667f), this.decodeY(0.14285715f));
        this.path.curveTo(this.decodeAnchorX(0.6666667f, 3.4444444f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeAnchorX(2.3333333f, -3.3333333f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeX(2.3333333f), this.decodeY(0.14285715f));
        this.path.curveTo(this.decodeAnchorX(2.3333333f, 3.3333333f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeAnchorX(2.8888888f, 0.0f), this.decodeAnchorY(0.85714287f, -3.2777777f), this.decodeX(2.8888888f), this.decodeY(0.85714287f));
        this.path.curveTo(this.decodeAnchorX(2.8888888f, 0.0f), this.decodeAnchorY(0.85714287f, 3.2777777f), this.decodeAnchorX(2.8888888f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeX(2.8888888f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(0.11111111f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath10() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.14285715f), this.decodeY(3.0f));
        this.path.curveTo(this.decodeAnchorX(0.14285715f, 0.0f), this.decodeAnchorY(3.0f, 0.0f), this.decodeAnchorX(0.14285715f, 0.0f), this.decodeAnchorY(0.85714287f, 3.5555556f), this.decodeX(0.14285715f), this.decodeY(0.85714287f));
        this.path.curveTo(this.decodeAnchorX(0.14285715f, 0.0f), this.decodeAnchorY(0.85714287f, -3.5555556f), this.decodeAnchorX(0.85714287f, -3.4444444f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeX(0.85714287f), this.decodeY(0.14285715f));
        this.path.curveTo(this.decodeAnchorX(0.85714287f, 3.4444444f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeAnchorX(2.142857f, -3.3333333f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeX(2.142857f), this.decodeY(0.14285715f));
        this.path.curveTo(this.decodeAnchorX(2.142857f, 3.3333333f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeAnchorX(2.857143f, 0.0f), this.decodeAnchorY(0.85714287f, -3.2777777f), this.decodeX(2.857143f), this.decodeY(0.85714287f));
        this.path.curveTo(this.decodeAnchorX(2.857143f, 0.0f), this.decodeAnchorY(0.85714287f, 3.2777777f), this.decodeAnchorX(2.857143f, 0.0f), this.decodeAnchorY(3.0f, 0.0f), this.decodeX(2.857143f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.14285715f), this.decodeY(3.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath11() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.4638889f), this.decodeY(2.25f));
        this.path.lineTo(this.decodeX(1.4652778f), this.decodeY(2.777778f));
        this.path.lineTo(this.decodeX(0.3809524f), this.decodeY(2.777778f));
        this.path.lineTo(this.decodeX(0.375f), this.decodeY(0.88095236f));
        this.path.curveTo(this.decodeAnchorX(0.375f, 0.0f), this.decodeAnchorY(0.88095236f, -2.25f), this.decodeAnchorX(0.8452381f, -1.9166666f), this.decodeAnchorY(0.3809524f, 0.0f), this.decodeX(0.8452381f), this.decodeY(0.3809524f));
        this.path.lineTo(this.decodeX(2.1011903f), this.decodeY(0.3809524f));
        this.path.curveTo(this.decodeAnchorX(2.1011903f, 2.125f), this.decodeAnchorY(0.3809524f, 0.0f), this.decodeAnchorX(2.6309526f, 0.0f), this.decodeAnchorY(0.8630952f, -2.5833333f), this.decodeX(2.6309526f), this.decodeY(0.8630952f));
        this.path.lineTo(this.decodeX(2.625f), this.decodeY(2.7638886f));
        this.path.lineTo(this.decodeX(1.4666667f), this.decodeY(2.777778f));
        this.path.lineTo(this.decodeX(1.4638889f), this.decodeY(2.2361114f));
        this.path.lineTo(this.decodeX(2.3869045f), this.decodeY(2.222222f));
        this.path.lineTo(this.decodeX(2.375f), this.decodeY(0.86904764f));
        this.path.curveTo(this.decodeAnchorX(2.375f, -7.1054274E-15f), this.decodeAnchorY(0.86904764f, -0.9166667f), this.decodeAnchorX(2.0952382f, 1.0833334f), this.decodeAnchorY(0.60714287f, -1.7763568E-15f), this.decodeX(2.0952382f), this.decodeY(0.60714287f));
        this.path.lineTo(this.decodeX(0.8333334f), this.decodeY(0.6130952f));
        this.path.curveTo(this.decodeAnchorX(0.8333334f, -1.0f), this.decodeAnchorY(0.6130952f, 0.0f), this.decodeAnchorX(0.625f, 0.041666668f), this.decodeAnchorY(0.86904764f, -0.9583333f), this.decodeX(0.625f), this.decodeY(0.86904764f));
        this.path.lineTo(this.decodeX(0.6130952f), this.decodeY(2.2361114f));
        this.path.lineTo(this.decodeX(1.4638889f), this.decodeY(2.25f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath12() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.71428573f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(0.71428573f, -3.0f), this.decodeAnchorX(0.5555556f, -3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeX(0.5555556f), this.decodeY(0.0f));
        this.path.curveTo(this.decodeAnchorX(0.5555556f, 3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeAnchorX(2.4444444f, -3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeX(2.4444444f), this.decodeY(0.0f));
        this.path.curveTo(this.decodeAnchorX(2.4444444f, 3.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(0.71428573f, -3.0f), this.decodeX(3.0f), this.decodeY(0.71428573f));
        this.path.curveTo(this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(0.71428573f, 3.0f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(3.0f, 0.0f), this.decodeX(3.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(3.0f, 0.0f), this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(0.71428573f, 3.0f), this.decodeX(0.0f), this.decodeY(0.71428573f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath13() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.11111111f), this.decodeY(3.0f));
        this.path.curveTo(this.decodeAnchorX(0.11111111f, 0.0f), this.decodeAnchorY(3.0f, 0.0f), this.decodeAnchorX(0.11111111f, 0.0f), this.decodeAnchorY(0.85714287f, 3.5555556f), this.decodeX(0.11111111f), this.decodeY(0.85714287f));
        this.path.curveTo(this.decodeAnchorX(0.11111111f, 0.0f), this.decodeAnchorY(0.85714287f, -3.5555556f), this.decodeAnchorX(0.6666667f, -3.4444444f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeX(0.6666667f), this.decodeY(0.14285715f));
        this.path.curveTo(this.decodeAnchorX(0.6666667f, 3.4444444f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeAnchorX(2.3333333f, -3.3333333f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeX(2.3333333f), this.decodeY(0.14285715f));
        this.path.curveTo(this.decodeAnchorX(2.3333333f, 3.3333333f), this.decodeAnchorY(0.14285715f, 0.0f), this.decodeAnchorX(2.8888888f, 0.0f), this.decodeAnchorY(0.85714287f, -3.2777777f), this.decodeX(2.8888888f), this.decodeY(0.85714287f));
        this.path.curveTo(this.decodeAnchorX(2.8888888f, 0.0f), this.decodeAnchorY(0.85714287f, 3.2777777f), this.decodeAnchorX(2.8888888f, 0.0f), this.decodeAnchorY(3.0f, 0.0f), this.decodeX(2.8888888f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.11111111f), this.decodeY(3.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath14() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.4583333f), this.decodeY(2.25f));
        this.path.lineTo(this.decodeX(1.4599359f), this.decodeY(2.777778f));
        this.path.lineTo(this.decodeX(0.2962963f), this.decodeY(2.777778f));
        this.path.lineTo(this.decodeX(0.29166666f), this.decodeY(0.88095236f));
        this.path.curveTo(this.decodeAnchorX(0.29166666f, 0.0f), this.decodeAnchorY(0.88095236f, -2.25f), this.decodeAnchorX(0.6574074f, -1.9166666f), this.decodeAnchorY(0.3809524f, 0.0f), this.decodeX(0.6574074f), this.decodeY(0.3809524f));
        this.path.lineTo(this.decodeX(2.3009257f), this.decodeY(0.3809524f));
        this.path.curveTo(this.decodeAnchorX(2.3009257f, 2.125f), this.decodeAnchorY(0.3809524f, 0.0f), this.decodeAnchorX(2.712963f, 0.0f), this.decodeAnchorY(0.8630952f, -2.5833333f), this.decodeX(2.712963f), this.decodeY(0.8630952f));
        this.path.lineTo(this.decodeX(2.7083333f), this.decodeY(2.7638886f));
        this.path.lineTo(this.decodeX(1.4615384f), this.decodeY(2.777778f));
        this.path.lineTo(this.decodeX(1.4583333f), this.decodeY(2.2361114f));
        this.path.lineTo(this.decodeX(2.523148f), this.decodeY(2.222222f));
        this.path.lineTo(this.decodeX(2.5138888f), this.decodeY(0.86904764f));
        this.path.curveTo(this.decodeAnchorX(2.5138888f, -7.1054274E-15f), this.decodeAnchorY(0.86904764f, -0.9166667f), this.decodeAnchorX(2.2962964f, 1.0833334f), this.decodeAnchorY(0.60714287f, -1.7763568E-15f), this.decodeX(2.2962964f), this.decodeY(0.60714287f));
        this.path.lineTo(this.decodeX(0.6481482f), this.decodeY(0.6130952f));
        this.path.curveTo(this.decodeAnchorX(0.6481482f, -1.0f), this.decodeAnchorY(0.6130952f, 0.0f), this.decodeAnchorX(0.4861111f, 0.041666668f), this.decodeAnchorY(0.86904764f, -0.9583333f), this.decodeX(0.4861111f), this.decodeY(0.86904764f));
        this.path.lineTo(this.decodeX(0.47685182f), this.decodeY(2.2361114f));
        this.path.lineTo(this.decodeX(1.4583333f), this.decodeY(2.25f));
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
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.1f, 0.2f, 0.6f, 1.0f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color6, this.decodeColor(this.color6, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.1f, 0.2f, 0.6f, 1.0f }, new Color[] { this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8, this.decodeColor(this.color8, this.color9, 0.5f), this.color9 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color10, this.decodeColor(this.color10, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.1f, 0.2f, 0.42096776f, 0.64193547f, 0.82096773f, 1.0f }, new Color[] { this.color11, this.decodeColor(this.color11, this.color12, 0.5f), this.color12, this.decodeColor(this.color12, this.color13, 0.5f), this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color15, this.decodeColor(this.color15, this.color16, 0.5f), this.color16 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.1f, 0.2f, 0.6f, 1.0f }, new Color[] { this.color17, this.decodeColor(this.color17, this.color18, 0.5f), this.color18, this.decodeColor(this.color18, this.color5, 0.5f), this.color5 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.12419355f, 0.2483871f, 0.42580646f, 0.6032258f, 0.6854839f, 0.7677419f, 0.88387096f, 1.0f }, new Color[] { this.color19, this.decodeColor(this.color19, this.color20, 0.5f), this.color20, this.decodeColor(this.color20, this.color21, 0.5f), this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22, this.decodeColor(this.color22, this.color23, 0.5f), this.color23 });
    }
    
    private Paint decodeGradient10(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color24, this.decodeColor(this.color24, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient11(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color25, this.decodeColor(this.color25, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient12(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.12419355f, 0.2483871f, 0.42580646f, 0.6032258f, 0.6854839f, 0.7677419f, 0.86774194f, 0.9677419f }, new Color[] { this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27, this.decodeColor(this.color27, this.color28, 0.5f), this.color28, this.decodeColor(this.color28, this.color29, 0.5f), this.color29, this.decodeColor(this.color29, this.color30, 0.5f), this.color30 });
    }
    
    private Paint decodeGradient13(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color25, this.decodeColor(this.color25, this.color31, 0.5f), this.color31 });
    }
    
    private Paint decodeGradient14(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.12419355f, 0.2483871f, 0.42580646f, 0.6032258f, 0.6854839f, 0.7677419f, 0.8548387f, 0.9419355f }, new Color[] { this.color32, this.decodeColor(this.color32, this.color33, 0.5f), this.color33, this.decodeColor(this.color33, this.color34, 0.5f), this.color34, this.decodeColor(this.color34, this.color35, 0.5f), this.color35, this.decodeColor(this.color35, this.color36, 0.5f), this.color36 });
    }
}
