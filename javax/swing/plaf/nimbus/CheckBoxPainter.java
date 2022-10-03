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

final class CheckBoxPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int ICON_DISABLED = 3;
    static final int ICON_ENABLED = 4;
    static final int ICON_FOCUSED = 5;
    static final int ICON_MOUSEOVER = 6;
    static final int ICON_MOUSEOVER_FOCUSED = 7;
    static final int ICON_PRESSED = 8;
    static final int ICON_PRESSED_FOCUSED = 9;
    static final int ICON_SELECTED = 10;
    static final int ICON_SELECTED_FOCUSED = 11;
    static final int ICON_PRESSED_SELECTED = 12;
    static final int ICON_PRESSED_SELECTED_FOCUSED = 13;
    static final int ICON_MOUSEOVER_SELECTED = 14;
    static final int ICON_MOUSEOVER_SELECTED_FOCUSED = 15;
    static final int ICON_DISABLED_SELECTED = 16;
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
    private Color color38;
    private Color color39;
    private Color color40;
    private Color color41;
    private Color color42;
    private Color color43;
    private Color color44;
    private Color color45;
    private Object[] componentColors;
    
    public CheckBoxPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06766917f, 0.07843137f, 0);
        this.color2 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06484103f, 0.027450979f, 0);
        this.color3 = this.decodeColor("nimbusBase", 0.032459438f, -0.60996324f, 0.36470586f, 0);
        this.color4 = this.decodeColor("nimbusBase", 0.02551502f, -0.5996783f, 0.3215686f, 0);
        this.color5 = this.decodeColor("nimbusBase", 0.032459438f, -0.59624064f, 0.34509802f, 0);
        this.color6 = this.decodeColor("nimbusBlueGrey", 0.0f, 0.0f, 0.0f, -89);
        this.color7 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.05356429f, -0.12549019f, 0);
        this.color8 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.015789472f, -0.37254903f, 0);
        this.color9 = this.decodeColor("nimbusBase", 0.08801502f, -0.63174605f, 0.43921566f, 0);
        this.color10 = this.decodeColor("nimbusBase", 0.032459438f, -0.5953556f, 0.32549018f, 0);
        this.color11 = this.decodeColor("nimbusBase", 0.032459438f, -0.59942394f, 0.4235294f, 0);
        this.color12 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.color13 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.020974077f, -0.21960783f, 0);
        this.color14 = this.decodeColor("nimbusBlueGrey", 0.01010108f, 0.08947369f, -0.5294118f, 0);
        this.color15 = this.decodeColor("nimbusBase", 0.08801502f, -0.6317773f, 0.4470588f, 0);
        this.color16 = this.decodeColor("nimbusBase", 0.032459438f, -0.5985242f, 0.39999998f, 0);
        this.color17 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, 0);
        this.color18 = this.decodeColor("nimbusBlueGrey", 0.055555582f, 0.8894737f, -0.7176471f, 0);
        this.color19 = this.decodeColor("nimbusBlueGrey", 0.0f, 0.0016232133f, -0.3254902f, 0);
        this.color20 = this.decodeColor("nimbusBase", 0.027408898f, -0.5847884f, 0.2980392f, 0);
        this.color21 = this.decodeColor("nimbusBase", 0.029681683f, -0.52701867f, 0.17254901f, 0);
        this.color22 = this.decodeColor("nimbusBase", 0.029681683f, -0.5376751f, 0.25098038f, 0);
        this.color23 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.34585923f, -0.007843137f, 0);
        this.color24 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.10238093f, -0.25490198f, 0);
        this.color25 = this.decodeColor("nimbusBase", 0.004681647f, -0.6197143f, 0.43137252f, 0);
        this.color26 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.44153953f, 0.2588235f, 0);
        this.color27 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4602757f, 0.34509802f, 0);
        this.color28 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.54901963f, 0);
        this.color29 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color30 = this.decodeColor("nimbusBase", -3.528595E-5f, 0.026785731f, -0.23529413f, 0);
        this.color31 = this.decodeColor("nimbusBase", -4.2033195E-4f, -0.38050595f, 0.20392156f, 0);
        this.color32 = this.decodeColor("nimbusBase", -0.0021489263f, -0.2891234f, 0.14117646f, 0);
        this.color33 = this.decodeColor("nimbusBase", -0.006362498f, -0.016311288f, -0.02352941f, 0);
        this.color34 = this.decodeColor("nimbusBase", 0.0f, -0.17930403f, 0.21568626f, 0);
        this.color35 = this.decodeColor("nimbusBase", 0.0013483167f, -0.1769987f, -0.12156865f, 0);
        this.color36 = this.decodeColor("nimbusBase", 0.05468172f, 0.3642857f, -0.43137258f, 0);
        this.color37 = this.decodeColor("nimbusBase", 0.004681647f, -0.6198413f, 0.43921566f, 0);
        this.color38 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4555341f, 0.3215686f, 0);
        this.color39 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.47377098f, 0.41960782f, 0);
        this.color40 = this.decodeColor("nimbusBlueGrey", -0.01111114f, -0.03771078f, 0.062745094f, 0);
        this.color41 = this.decodeColor("nimbusBlueGrey", -0.02222222f, -0.032806106f, 0.011764705f, 0);
        this.color42 = this.decodeColor("nimbusBase", 0.021348298f, -0.59223604f, 0.35294116f, 0);
        this.color43 = this.decodeColor("nimbusBase", 0.021348298f, -0.56722116f, 0.3098039f, 0);
        this.color44 = this.decodeColor("nimbusBase", 0.021348298f, -0.56875f, 0.32941175f, 0);
        this.color45 = this.decodeColor("nimbusBase", 0.027408898f, -0.5735674f, 0.14509803f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 3: {
                this.painticonDisabled(graphics2D);
                break;
            }
            case 4: {
                this.painticonEnabled(graphics2D);
                break;
            }
            case 5: {
                this.painticonFocused(graphics2D);
                break;
            }
            case 6: {
                this.painticonMouseOver(graphics2D);
                break;
            }
            case 7: {
                this.painticonMouseOverAndFocused(graphics2D);
                break;
            }
            case 8: {
                this.painticonPressed(graphics2D);
                break;
            }
            case 9: {
                this.painticonPressedAndFocused(graphics2D);
                break;
            }
            case 10: {
                this.painticonSelected(graphics2D);
                break;
            }
            case 11: {
                this.painticonSelectedAndFocused(graphics2D);
                break;
            }
            case 12: {
                this.painticonPressedAndSelected(graphics2D);
                break;
            }
            case 13: {
                this.painticonPressedAndSelectedAndFocused(graphics2D);
                break;
            }
            case 14: {
                this.painticonMouseOverAndSelected(graphics2D);
                break;
            }
            case 15: {
                this.painticonMouseOverAndSelectedAndFocused(graphics2D);
                break;
            }
            case 16: {
                this.painticonDisabledAndSelected(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void painticonDisabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient1(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void painticonEnabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void painticonFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void painticonMouseOver(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void painticonMouseOverAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void painticonPressed(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void painticonPressedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void painticonSelected(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient9(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color28);
        graphics2D.fill(this.path);
    }
    
    private void painticonSelectedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient9(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color28);
        graphics2D.fill(this.path);
    }
    
    private void painticonPressedAndSelected(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color29);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient11(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient12(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color28);
        graphics2D.fill(this.path);
    }
    
    private void painticonPressedAndSelectedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient11(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient12(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color28);
        graphics2D.fill(this.path);
    }
    
    private void painticonMouseOverAndSelected(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient13(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient14(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color28);
        graphics2D.fill(this.path);
    }
    
    private void painticonMouseOverAndSelectedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient13(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient14(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color28);
        graphics2D.fill(this.path);
    }
    
    private void painticonDisabledAndSelected(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient15(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient16(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color45);
        graphics2D.fill(this.path);
    }
    
    private RoundRectangle2D decodeRoundRect1() {
        this.roundRect.setRoundRect(this.decodeX(0.4f), this.decodeY(0.4f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(2.6f) - this.decodeY(0.4f), 3.7058823108673096, 3.7058823108673096);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect2() {
        this.roundRect.setRoundRect(this.decodeX(0.6f), this.decodeY(0.6f), this.decodeX(2.4f) - this.decodeX(0.6f), this.decodeY(2.4f) - this.decodeY(0.6f), 3.7647058963775635, 3.7647058963775635);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect3() {
        this.roundRect.setRoundRect(this.decodeX(0.4f), this.decodeY(1.75f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(2.8f) - this.decodeY(1.75f), 5.176470756530762, 5.176470756530762);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect4() {
        this.roundRect.setRoundRect(this.decodeX(0.120000005f), this.decodeY(0.120000005f), this.decodeX(2.8799999f) - this.decodeX(0.120000005f), this.decodeY(2.8799999f) - this.decodeY(0.120000005f), 8.0, 8.0);
        return this.roundRect;
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0036764f), this.decodeY(1.382353f));
        this.path.lineTo(this.decodeX(1.2536764f), this.decodeY(1.382353f));
        this.path.lineTo(this.decodeX(1.430147f), this.decodeY(1.757353f));
        this.path.lineTo(this.decodeX(1.8235294f), this.decodeY(0.62352943f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(0.61764705f));
        this.path.lineTo(this.decodeX(1.492647f), this.decodeY(2.0058823f));
        this.path.lineTo(this.decodeX(1.382353f), this.decodeY(2.0058823f));
        this.path.lineTo(this.decodeX(1.0036764f), this.decodeY(1.382353f));
        this.path.closePath();
        return this.path;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25210086f * n3 + n, 0.9957983f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color1, this.decodeColor(this.color1, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 0.997549f * n4 + n2, new float[] { 0.0f, 0.32228917f, 0.64457834f, 0.82228917f, 1.0f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25210086f * n3 + n, 0.9957983f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 0.997549f * n4 + n2, new float[] { 0.0f, 0.32228917f, 0.64457834f, 0.82228917f, 1.0f }, new Color[] { this.color9, this.decodeColor(this.color9, this.color10, 0.5f), this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25210086f * n3 + n, 0.9957983f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 0.997549f * n4 + n2, new float[] { 0.0f, 0.32228917f, 0.64457834f, 0.82228917f, 1.0f }, new Color[] { this.color15, this.decodeColor(this.color15, this.color16, 0.5f), this.color16, this.decodeColor(this.color16, this.color17, 0.5f), this.color17 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25210086f * n3 + n, 0.9957983f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color18, this.decodeColor(this.color18, this.color19, 0.5f), this.color19 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 0.997549f * n4 + n2, new float[] { 0.0f, 0.32228917f, 0.64457834f, 0.82228917f, 1.0f }, new Color[] { this.color20, this.decodeColor(this.color20, this.color21, 0.5f), this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25210086f * n3 + n, 0.9957983f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color23, this.decodeColor(this.color23, this.color24, 0.5f), this.color24 });
    }
    
    private Paint decodeGradient10(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 0.997549f * n4 + n2, new float[] { 0.0f, 0.32228917f, 0.64457834f, 0.82228917f, 1.0f }, new Color[] { this.color25, this.decodeColor(this.color25, this.color26, 0.5f), this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27 });
    }
    
    private Paint decodeGradient11(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25210086f * n3 + n, 0.9957983f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color28, this.decodeColor(this.color28, this.color30, 0.5f), this.color30 });
    }
    
    private Paint decodeGradient12(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 0.997549f * n4 + n2, new float[] { 0.0f, 0.05775076f, 0.11550152f, 0.38003993f, 0.64457834f, 0.82228917f, 1.0f }, new Color[] { this.color31, this.decodeColor(this.color31, this.color32, 0.5f), this.color32, this.decodeColor(this.color32, this.color33, 0.5f), this.color33, this.decodeColor(this.color33, this.color34, 0.5f), this.color34 });
    }
    
    private Paint decodeGradient13(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25210086f * n3 + n, 0.9957983f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color35, this.decodeColor(this.color35, this.color36, 0.5f), this.color36 });
    }
    
    private Paint decodeGradient14(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 0.997549f * n4 + n2, new float[] { 0.0f, 0.32228917f, 0.64457834f, 0.82228917f, 1.0f }, new Color[] { this.color37, this.decodeColor(this.color37, this.color38, 0.5f), this.color38, this.decodeColor(this.color38, this.color39, 0.5f), this.color39 });
    }
    
    private Paint decodeGradient15(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25210086f * n3 + n, 0.9957983f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color40, this.decodeColor(this.color40, this.color41, 0.5f), this.color41 });
    }
    
    private Paint decodeGradient16(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 0.997549f * n4 + n2, new float[] { 0.0f, 0.32228917f, 0.64457834f, 0.82228917f, 1.0f }, new Color[] { this.color42, this.decodeColor(this.color42, this.color43, 0.5f), this.color43, this.decodeColor(this.color43, this.color44, 0.5f), this.color44 });
    }
}
