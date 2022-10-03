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

final class RadioButtonPainter extends AbstractRegionPainter
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
    private Color color46;
    private Color color47;
    private Color color48;
    private Color color49;
    private Color color50;
    private Color color51;
    private Color color52;
    private Color color53;
    private Color color54;
    private Color color55;
    private Object[] componentColors;
    
    public RadioButtonPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06766917f, 0.07843137f, 0);
        this.color2 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06413457f, 0.015686274f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.08466425f, 0.16470587f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07016757f, 0.12941176f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.070703305f, 0.14117646f, 0);
        this.color6 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07052632f, 0.1372549f, 0);
        this.color7 = this.decodeColor("nimbusBlueGrey", 0.0f, 0.0f, 0.0f, -112);
        this.color8 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.053201474f, -0.12941176f, 0);
        this.color9 = this.decodeColor("nimbusBlueGrey", 0.0f, 0.006356798f, -0.44313726f, 0);
        this.color10 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.10654225f, 0.23921567f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07206477f, 0.17254901f, 0);
        this.color12 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.color13 = this.decodeColor("nimbusBlueGrey", -0.00505054f, -0.027819552f, -0.2235294f, 0);
        this.color14 = this.decodeColor("nimbusBlueGrey", 0.0f, 0.24241486f, -0.6117647f, 0);
        this.color15 = this.decodeColor("nimbusBlueGrey", -0.111111104f, -0.10655806f, 0.24313724f, 0);
        this.color16 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07333623f, 0.20392156f, 0);
        this.color17 = this.decodeColor("nimbusBlueGrey", 0.08585858f, -0.067389056f, 0.25490195f, 0);
        this.color18 = this.decodeColor("nimbusBlueGrey", -0.111111104f, -0.10628903f, 0.18039215f, 0);
        this.color19 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color20 = this.decodeColor("nimbusBlueGrey", 0.055555582f, 0.23947367f, -0.6666667f, 0);
        this.color21 = this.decodeColor("nimbusBlueGrey", -0.0777778f, -0.06815343f, -0.28235295f, 0);
        this.color22 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06866585f, 0.09803921f, 0);
        this.color23 = this.decodeColor("nimbusBlueGrey", -0.0027777553f, -0.0018306673f, -0.02352941f, 0);
        this.color24 = this.decodeColor("nimbusBlueGrey", 0.002924025f, -0.02047892f, 0.082352936f, 0);
        this.color25 = this.decodeColor("nimbusBase", 2.9569864E-4f, -0.36035198f, -0.007843137f, 0);
        this.color26 = this.decodeColor("nimbusBase", 2.9569864E-4f, 0.019458115f, -0.32156867f, 0);
        this.color27 = this.decodeColor("nimbusBase", 0.004681647f, -0.6195853f, 0.4235294f, 0);
        this.color28 = this.decodeColor("nimbusBase", 0.004681647f, -0.56704473f, 0.36470586f, 0);
        this.color29 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.43866998f, 0.24705881f, 0);
        this.color30 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.44879842f, 0.29019606f, 0);
        this.color31 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.07243107f, -0.33333334f, 0);
        this.color32 = this.decodeColor("nimbusBlueGrey", -0.6111111f, -0.110526316f, -0.74509805f, 0);
        this.color33 = this.decodeColor("nimbusBlueGrey", -0.027777791f, 0.07129187f, -0.6156863f, 0);
        this.color34 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.49803925f, 0);
        this.color35 = this.decodeColor("nimbusBase", 0.0030477047f, -0.1257143f, -0.15686277f, 0);
        this.color36 = this.decodeColor("nimbusBase", -0.0017285943f, -0.4367347f, 0.21960783f, 0);
        this.color37 = this.decodeColor("nimbusBase", -0.0010654926f, -0.31349206f, 0.15686274f, 0);
        this.color38 = this.decodeColor("nimbusBase", 0.0f, 0.0f, 0.0f, 0);
        this.color39 = this.decodeColor("nimbusBase", 8.05676E-4f, -0.12380952f, 0.109803915f, 0);
        this.color40 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.080223285f, -0.4862745f, 0);
        this.color41 = this.decodeColor("nimbusBase", -6.374717E-4f, -0.20452163f, -0.12156865f, 0);
        this.color42 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.5058824f, 0);
        this.color43 = this.decodeColor("nimbusBase", -0.011985004f, -0.6157143f, 0.43137252f, 0);
        this.color44 = this.decodeColor("nimbusBase", 0.004681647f, -0.56932425f, 0.3960784f, 0);
        this.color45 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4555341f, 0.3215686f, 0);
        this.color46 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.46550155f, 0.372549f, 0);
        this.color47 = this.decodeColor("nimbusBase", 0.0024294257f, -0.47271872f, 0.34117645f, 0);
        this.color48 = this.decodeColor("nimbusBase", 0.010237217f, -0.56289876f, 0.2588235f, 0);
        this.color49 = this.decodeColor("nimbusBase", 0.016586483f, -0.5620301f, 0.19607842f, 0);
        this.color50 = this.decodeColor("nimbusBase", 0.027408898f, -0.5878882f, 0.35294116f, 0);
        this.color51 = this.decodeColor("nimbusBase", 0.021348298f, -0.56722116f, 0.3098039f, 0);
        this.color52 = this.decodeColor("nimbusBase", 0.021348298f, -0.567841f, 0.31764704f, 0);
        this.color53 = this.decodeColor("nimbusBlueGrey", -0.01111114f, -0.058170296f, 0.0039215684f, 0);
        this.color54 = this.decodeColor("nimbusBlueGrey", -0.013888836f, -0.04195489f, -0.058823526f, 0);
        this.color55 = this.decodeColor("nimbusBlueGrey", 0.009259284f, -0.0147816315f, -0.007843137f, 0);
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
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient1(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient2(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void painticonEnabled(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse3();
        graphics2D.setPaint(this.color7);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient3(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient4(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void painticonFocused(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient3(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient4(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void painticonMouseOver(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse3();
        graphics2D.setPaint(this.color7);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient5(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient6(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void painticonMouseOverAndFocused(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient5(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient6(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void painticonPressed(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse3();
        graphics2D.setPaint(this.color19);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient7(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient8(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void painticonPressedAndFocused(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient7(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient8(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void painticonSelected(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse3();
        graphics2D.setPaint(this.color7);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient9(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient10(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse5();
        graphics2D.setPaint(this.decodeGradient11(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void painticonSelectedAndFocused(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient9(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient10(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse5();
        graphics2D.setPaint(this.decodeGradient11(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void painticonPressedAndSelected(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse3();
        graphics2D.setPaint(this.color19);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient12(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient13(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse5();
        graphics2D.setPaint(this.decodeGradient14(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void painticonPressedAndSelectedAndFocused(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient12(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient13(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse5();
        graphics2D.setPaint(this.decodeGradient14(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void painticonMouseOverAndSelected(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse3();
        graphics2D.setPaint(this.color7);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient15(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient16(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse5();
        graphics2D.setPaint(this.decodeGradient11(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void painticonMouseOverAndSelectedAndFocused(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient15(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient16(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse5();
        graphics2D.setPaint(this.decodeGradient11(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void painticonDisabledAndSelected(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient17(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient18(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse5();
        graphics2D.setPaint(this.decodeGradient19(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private Ellipse2D decodeEllipse1() {
        this.ellipse.setFrame(this.decodeX(0.4f), this.decodeY(0.4f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(2.6f) - this.decodeY(0.4f));
        return this.ellipse;
    }
    
    private Ellipse2D decodeEllipse2() {
        this.ellipse.setFrame(this.decodeX(0.6f), this.decodeY(0.6f), this.decodeX(2.4f) - this.decodeX(0.6f), this.decodeY(2.4f) - this.decodeY(0.6f));
        return this.ellipse;
    }
    
    private Ellipse2D decodeEllipse3() {
        this.ellipse.setFrame(this.decodeX(0.4f), this.decodeY(0.6f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(2.8f) - this.decodeY(0.6f));
        return this.ellipse;
    }
    
    private Ellipse2D decodeEllipse4() {
        this.ellipse.setFrame(this.decodeX(0.120000005f), this.decodeY(0.120000005f), this.decodeX(2.8799999f) - this.decodeX(0.120000005f), this.decodeY(2.8799999f) - this.decodeY(0.120000005f));
        return this.ellipse;
    }
    
    private Ellipse2D decodeEllipse5() {
        this.ellipse.setFrame(this.decodeX(1.125f), this.decodeY(1.125f), this.decodeX(1.875f) - this.decodeX(1.125f), this.decodeY(1.875f) - this.decodeY(1.125f));
        return this.ellipse;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49789914f * n3 + n, -0.004201681f * n4 + n2, 0.5f * n3 + n, 0.9978992f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color1, this.decodeColor(this.color1, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49754903f * n3 + n, 0.004901961f * n4 + n2, 0.50735295f * n3 + n, 1.0f * n4 + n2, new float[] { 0.06344411f, 0.21601209f, 0.36858007f, 0.54833835f, 0.72809666f, 0.77492446f, 0.82175225f, 0.91087615f, 1.0f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5, this.decodeColor(this.color5, this.color6, 0.5f), this.color6 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49789914f * n3 + n, -0.004201681f * n4 + n2, 0.5f * n3 + n, 0.9978992f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color8, this.decodeColor(this.color8, this.color9, 0.5f), this.color9 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49754903f * n3 + n, 0.004901961f * n4 + n2, 0.50735295f * n3 + n, 1.0f * n4 + n2, new float[] { 0.06344411f, 0.25009555f, 0.43674698f, 0.48042166f, 0.52409637f, 0.70481926f, 0.88554215f }, new Color[] { this.color10, this.decodeColor(this.color10, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color11, 0.5f), this.color11 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49789914f * n3 + n, -0.004201681f * n4 + n2, 0.5f * n3 + n, 0.9978992f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49754903f * n3 + n, 0.004901961f * n4 + n2, 0.50735295f * n3 + n, 1.0f * n4 + n2, new float[] { 0.06344411f, 0.21601209f, 0.36858007f, 0.54833835f, 0.72809666f, 0.77492446f, 0.82175225f, 0.91087615f, 1.0f }, new Color[] { this.color15, this.decodeColor(this.color15, this.color16, 0.5f), this.color16, this.decodeColor(this.color16, this.color16, 0.5f), this.color16, this.decodeColor(this.color16, this.color17, 0.5f), this.color17, this.decodeColor(this.color17, this.color18, 0.5f), this.color18 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49789914f * n3 + n, -0.004201681f * n4 + n2, 0.5f * n3 + n, 0.9978992f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color20, this.decodeColor(this.color20, this.color21, 0.5f), this.color21 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49754903f * n3 + n, 0.004901961f * n4 + n2, 0.50735295f * n3 + n, 1.0f * n4 + n2, new float[] { 0.06344411f, 0.20792687f, 0.35240963f, 0.45030123f, 0.5481928f, 0.748494f, 0.9487952f }, new Color[] { this.color22, this.decodeColor(this.color22, this.color23, 0.5f), this.color23, this.decodeColor(this.color23, this.color23, 0.5f), this.color23, this.decodeColor(this.color23, this.color24, 0.5f), this.color24 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49789914f * n3 + n, -0.004201681f * n4 + n2, 0.5f * n3 + n, 0.9978992f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color25, this.decodeColor(this.color25, this.color26, 0.5f), this.color26 });
    }
    
    private Paint decodeGradient10(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49754903f * n3 + n, 0.004901961f * n4 + n2, 0.50735295f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0813253f, 0.100903615f, 0.12048193f, 0.28915662f, 0.45783132f, 0.6159638f, 0.77409637f, 0.82981926f, 0.88554215f }, new Color[] { this.color27, this.decodeColor(this.color27, this.color28, 0.5f), this.color28, this.decodeColor(this.color28, this.color29, 0.5f), this.color29, this.decodeColor(this.color29, this.color29, 0.5f), this.color29, this.decodeColor(this.color29, this.color30, 0.5f), this.color30 });
    }
    
    private Paint decodeGradient11(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.50490195f * n3 + n, 0.0f * n4 + n2, 0.49509802f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.23192771f, 0.46385542f, 0.73192775f, 1.0f }, new Color[] { this.color31, this.decodeColor(this.color31, this.color32, 0.5f), this.color32, this.decodeColor(this.color32, this.color33, 0.5f), this.color33 });
    }
    
    private Paint decodeGradient12(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49789914f * n3 + n, -0.004201681f * n4 + n2, 0.5f * n3 + n, 0.9978992f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color34, this.decodeColor(this.color34, this.color26, 0.5f), this.color26 });
    }
    
    private Paint decodeGradient13(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49754903f * n3 + n, 0.004901961f * n4 + n2, 0.50735295f * n3 + n, 1.0f * n4 + n2, new float[] { 0.039156627f, 0.07831325f, 0.11746988f, 0.2876506f, 0.45783132f, 0.56174695f, 0.66566265f, 0.7756024f, 0.88554215f }, new Color[] { this.color36, this.decodeColor(this.color36, this.color37, 0.5f), this.color37, this.decodeColor(this.color37, this.color38, 0.5f), this.color38, this.decodeColor(this.color38, this.color38, 0.5f), this.color38, this.decodeColor(this.color38, this.color39, 0.5f), this.color39 });
    }
    
    private Paint decodeGradient14(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.50490195f * n3 + n, 0.0f * n4 + n2, 0.49509802f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.23192771f, 0.46385542f, 0.73192775f, 1.0f }, new Color[] { this.color40, this.decodeColor(this.color40, this.color32, 0.5f), this.color32, this.decodeColor(this.color32, this.color33, 0.5f), this.color33 });
    }
    
    private Paint decodeGradient15(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49789914f * n3 + n, -0.004201681f * n4 + n2, 0.5f * n3 + n, 0.9978992f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color41, this.decodeColor(this.color41, this.color42, 0.5f), this.color42 });
    }
    
    private Paint decodeGradient16(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49754903f * n3 + n, 0.004901961f * n4 + n2, 0.50735295f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0813253f, 0.100903615f, 0.12048193f, 0.20180723f, 0.28313252f, 0.49246985f, 0.7018072f, 0.7560241f, 0.810241f, 0.84789157f, 0.88554215f }, new Color[] { this.color43, this.decodeColor(this.color43, this.color44, 0.5f), this.color44, this.decodeColor(this.color44, this.color45, 0.5f), this.color45, this.decodeColor(this.color45, this.color45, 0.5f), this.color45, this.decodeColor(this.color45, this.color46, 0.5f), this.color46, this.decodeColor(this.color46, this.color47, 0.5f), this.color47 });
    }
    
    private Paint decodeGradient17(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49789914f * n3 + n, -0.004201681f * n4 + n2, 0.5f * n3 + n, 0.9978992f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color48, this.decodeColor(this.color48, this.color49, 0.5f), this.color49 });
    }
    
    private Paint decodeGradient18(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.49754903f * n3 + n, 0.004901961f * n4 + n2, 0.50735295f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0813253f, 0.2695783f, 0.45783132f, 0.67168677f, 0.88554215f }, new Color[] { this.color50, this.decodeColor(this.color50, this.color51, 0.5f), this.color51, this.decodeColor(this.color51, this.color52, 0.5f), this.color52 });
    }
    
    private Paint decodeGradient19(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.50490195f * n3 + n, 0.0f * n4 + n2, 0.49509802f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.23192771f, 0.46385542f, 0.73192775f, 1.0f }, new Color[] { this.color53, this.decodeColor(this.color53, this.color54, 0.5f), this.color54, this.decodeColor(this.color54, this.color55, 0.5f), this.color55 });
    }
}
