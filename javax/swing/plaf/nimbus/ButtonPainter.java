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

final class ButtonPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DEFAULT = 1;
    static final int BACKGROUND_DEFAULT_FOCUSED = 2;
    static final int BACKGROUND_MOUSEOVER_DEFAULT = 3;
    static final int BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED = 4;
    static final int BACKGROUND_PRESSED_DEFAULT = 5;
    static final int BACKGROUND_PRESSED_DEFAULT_FOCUSED = 6;
    static final int BACKGROUND_DISABLED = 7;
    static final int BACKGROUND_ENABLED = 8;
    static final int BACKGROUND_FOCUSED = 9;
    static final int BACKGROUND_MOUSEOVER = 10;
    static final int BACKGROUND_MOUSEOVER_FOCUSED = 11;
    static final int BACKGROUND_PRESSED = 12;
    static final int BACKGROUND_PRESSED_FOCUSED = 13;
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
    private Object[] componentColors;
    
    public ButtonPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.06885965f, -0.36862746f, -190);
        this.color2 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.34585923f, -0.007843137f, 0);
        this.color3 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.095173776f, -0.25882354f, 0);
        this.color4 = this.decodeColor("nimbusBase", 0.004681647f, -0.6197143f, 0.43137252f, 0);
        this.color5 = this.decodeColor("nimbusBase", 0.004681647f, -0.5766426f, 0.38039213f, 0);
        this.color6 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.43866998f, 0.24705881f, 0);
        this.color7 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.46404046f, 0.36470586f, 0);
        this.color8 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.47761154f, 0.44313723f, 0);
        this.color9 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.color10 = this.decodeColor("nimbusBase", 0.0013483167f, -0.1769987f, -0.12156865f, 0);
        this.color11 = this.decodeColor("nimbusBase", 0.059279382f, 0.3642857f, -0.43529415f, 0);
        this.color12 = this.decodeColor("nimbusBase", 0.004681647f, -0.6198413f, 0.43921566f, 0);
        this.color13 = this.decodeColor("nimbusBase", -0.0017285943f, -0.5822163f, 0.40392154f, 0);
        this.color14 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4555341f, 0.3215686f, 0);
        this.color15 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.47698414f, 0.43921566f, 0);
        this.color16 = this.decodeColor("nimbusBase", -0.06415892f, -0.5455182f, 0.45098037f, 0);
        this.color17 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -95);
        this.color18 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.54901963f, 0);
        this.color19 = this.decodeColor("nimbusBase", -3.528595E-5f, 0.018606722f, -0.23137257f, 0);
        this.color20 = this.decodeColor("nimbusBase", -4.2033195E-4f, -0.38050595f, 0.20392156f, 0);
        this.color21 = this.decodeColor("nimbusBase", 0.001903832f, -0.29863563f, 0.1490196f, 0);
        this.color22 = this.decodeColor("nimbusBase", 0.0f, 0.0f, 0.0f, 0);
        this.color23 = this.decodeColor("nimbusBase", 0.0018727183f, -0.14126986f, 0.15686274f, 0);
        this.color24 = this.decodeColor("nimbusBase", 8.9377165E-4f, -0.20852983f, 0.2588235f, 0);
        this.color25 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.06885965f, -0.36862746f, -232);
        this.color26 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06766917f, 0.07843137f, 0);
        this.color27 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06484103f, 0.027450979f, 0);
        this.color28 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.08477524f, 0.16862744f, 0);
        this.color29 = this.decodeColor("nimbusBlueGrey", -0.015872955f, -0.080091536f, 0.15686274f, 0);
        this.color30 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07016757f, 0.12941176f, 0);
        this.color31 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07052632f, 0.1372549f, 0);
        this.color32 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.070878744f, 0.14509803f, 0);
        this.color33 = this.decodeColor("nimbusBlueGrey", -0.055555522f, -0.05356429f, -0.12549019f, 0);
        this.color34 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.0147816315f, -0.3764706f, 0);
        this.color35 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.10655806f, 0.24313724f, 0);
        this.color36 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.09823123f, 0.2117647f, 0);
        this.color37 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.0749532f, 0.24705881f, 0);
        this.color38 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color39 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.020974077f, -0.21960783f, 0);
        this.color40 = this.decodeColor("nimbusBlueGrey", 0.0f, 0.11169591f, -0.53333336f, 0);
        this.color41 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.10658931f, 0.25098038f, 0);
        this.color42 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.098526314f, 0.2352941f, 0);
        this.color43 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07333623f, 0.20392156f, 0);
        this.color44 = new Color(245, 250, 255, 160);
        this.color45 = this.decodeColor("nimbusBlueGrey", 0.055555582f, 0.8894737f, -0.7176471f, 0);
        this.color46 = this.decodeColor("nimbusBlueGrey", 0.0f, 5.847961E-4f, -0.32156864f, 0);
        this.color47 = this.decodeColor("nimbusBlueGrey", -0.00505054f, -0.05960039f, 0.10196078f, 0);
        this.color48 = this.decodeColor("nimbusBlueGrey", -0.008547008f, -0.04772438f, 0.06666666f, 0);
        this.color49 = this.decodeColor("nimbusBlueGrey", -0.0027777553f, -0.0018306673f, -0.02352941f, 0);
        this.color50 = this.decodeColor("nimbusBlueGrey", -0.0027777553f, -0.0212406f, 0.13333333f, 0);
        this.color51 = this.decodeColor("nimbusBlueGrey", 0.0055555105f, -0.030845039f, 0.23921567f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 1: {
                this.paintBackgroundDefault(graphics2D);
                break;
            }
            case 2: {
                this.paintBackgroundDefaultAndFocused(graphics2D);
                break;
            }
            case 3: {
                this.paintBackgroundMouseOverAndDefault(graphics2D);
                break;
            }
            case 4: {
                this.paintBackgroundMouseOverAndDefaultAndFocused(graphics2D);
                break;
            }
            case 5: {
                this.paintBackgroundPressedAndDefault(graphics2D);
                break;
            }
            case 6: {
                this.paintBackgroundPressedAndDefaultAndFocused(graphics2D);
                break;
            }
            case 7: {
                this.paintBackgroundDisabled(graphics2D);
                break;
            }
            case 8: {
                this.paintBackgroundEnabled(graphics2D);
                break;
            }
            case 9: {
                this.paintBackgroundFocused(graphics2D);
                break;
            }
            case 10: {
                this.paintBackgroundMouseOver(graphics2D);
                break;
            }
            case 11: {
                this.paintBackgroundMouseOverAndFocused(graphics2D);
                break;
            }
            case 12: {
                this.paintBackgroundPressed(graphics2D);
                break;
            }
            case 13: {
                this.paintBackgroundPressedAndFocused(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected Object[] getExtendedCacheKeys(final JComponent component) {
        Object[] array = null;
        switch (this.state) {
            case 1: {
                array = new Object[] { this.getComponentColor(component, "background", this.color4, -0.6197143f, 0.43137252f, 0), this.getComponentColor(component, "background", this.color5, -0.5766426f, 0.38039213f, 0), this.getComponentColor(component, "background", this.color6, -0.43866998f, 0.24705881f, 0), this.getComponentColor(component, "background", this.color7, -0.46404046f, 0.36470586f, 0), this.getComponentColor(component, "background", this.color8, -0.47761154f, 0.44313723f, 0) };
                break;
            }
            case 2: {
                array = new Object[] { this.getComponentColor(component, "background", this.color4, -0.6197143f, 0.43137252f, 0), this.getComponentColor(component, "background", this.color5, -0.5766426f, 0.38039213f, 0), this.getComponentColor(component, "background", this.color6, -0.43866998f, 0.24705881f, 0), this.getComponentColor(component, "background", this.color7, -0.46404046f, 0.36470586f, 0), this.getComponentColor(component, "background", this.color8, -0.47761154f, 0.44313723f, 0) };
                break;
            }
            case 3: {
                array = new Object[] { this.getComponentColor(component, "background", this.color12, -0.6198413f, 0.43921566f, 0), this.getComponentColor(component, "background", this.color13, -0.5822163f, 0.40392154f, 0), this.getComponentColor(component, "background", this.color14, -0.4555341f, 0.3215686f, 0), this.getComponentColor(component, "background", this.color15, -0.47698414f, 0.43921566f, 0), this.getComponentColor(component, "background", this.color16, -0.5455182f, 0.45098037f, 0) };
                break;
            }
            case 4: {
                array = new Object[] { this.getComponentColor(component, "background", this.color12, -0.6198413f, 0.43921566f, 0), this.getComponentColor(component, "background", this.color13, -0.5822163f, 0.40392154f, 0), this.getComponentColor(component, "background", this.color14, -0.4555341f, 0.3215686f, 0), this.getComponentColor(component, "background", this.color15, -0.47698414f, 0.43921566f, 0), this.getComponentColor(component, "background", this.color16, -0.5455182f, 0.45098037f, 0) };
                break;
            }
            case 5: {
                array = new Object[] { this.getComponentColor(component, "background", this.color20, -0.38050595f, 0.20392156f, 0), this.getComponentColor(component, "background", this.color21, -0.29863563f, 0.1490196f, 0), this.getComponentColor(component, "background", this.color22, 0.0f, 0.0f, 0), this.getComponentColor(component, "background", this.color23, -0.14126986f, 0.15686274f, 0), this.getComponentColor(component, "background", this.color24, -0.20852983f, 0.2588235f, 0) };
                break;
            }
            case 6: {
                array = new Object[] { this.getComponentColor(component, "background", this.color20, -0.38050595f, 0.20392156f, 0), this.getComponentColor(component, "background", this.color21, -0.29863563f, 0.1490196f, 0), this.getComponentColor(component, "background", this.color22, 0.0f, 0.0f, 0), this.getComponentColor(component, "background", this.color23, -0.14126986f, 0.15686274f, 0), this.getComponentColor(component, "background", this.color24, -0.20852983f, 0.2588235f, 0) };
                break;
            }
            case 8: {
                array = new Object[] { this.getComponentColor(component, "background", this.color35, -0.10655806f, 0.24313724f, 0), this.getComponentColor(component, "background", this.color36, -0.09823123f, 0.2117647f, 0), this.getComponentColor(component, "background", this.color30, -0.07016757f, 0.12941176f, 0), this.getComponentColor(component, "background", this.color37, -0.0749532f, 0.24705881f, 0), this.getComponentColor(component, "background", this.color38, -0.110526316f, 0.25490195f, 0) };
                break;
            }
            case 9: {
                array = new Object[] { this.getComponentColor(component, "background", this.color35, -0.10655806f, 0.24313724f, 0), this.getComponentColor(component, "background", this.color36, -0.09823123f, 0.2117647f, 0), this.getComponentColor(component, "background", this.color30, -0.07016757f, 0.12941176f, 0), this.getComponentColor(component, "background", this.color37, -0.0749532f, 0.24705881f, 0), this.getComponentColor(component, "background", this.color38, -0.110526316f, 0.25490195f, 0) };
                break;
            }
            case 10: {
                array = new Object[] { this.getComponentColor(component, "background", this.color41, -0.10658931f, 0.25098038f, 0), this.getComponentColor(component, "background", this.color42, -0.098526314f, 0.2352941f, 0), this.getComponentColor(component, "background", this.color43, -0.07333623f, 0.20392156f, 0), this.getComponentColor(component, "background", this.color38, -0.110526316f, 0.25490195f, 0) };
                break;
            }
            case 11: {
                array = new Object[] { this.getComponentColor(component, "background", this.color41, -0.10658931f, 0.25098038f, 0), this.getComponentColor(component, "background", this.color42, -0.098526314f, 0.2352941f, 0), this.getComponentColor(component, "background", this.color43, -0.07333623f, 0.20392156f, 0), this.getComponentColor(component, "background", this.color38, -0.110526316f, 0.25490195f, 0) };
                break;
            }
            case 12: {
                array = new Object[] { this.getComponentColor(component, "background", this.color47, -0.05960039f, 0.10196078f, 0), this.getComponentColor(component, "background", this.color48, -0.04772438f, 0.06666666f, 0), this.getComponentColor(component, "background", this.color49, -0.0018306673f, -0.02352941f, 0), this.getComponentColor(component, "background", this.color50, -0.0212406f, 0.13333333f, 0), this.getComponentColor(component, "background", this.color51, -0.030845039f, 0.23921567f, 0) };
                break;
            }
            case 13: {
                array = new Object[] { this.getComponentColor(component, "background", this.color47, -0.05960039f, 0.10196078f, 0), this.getComponentColor(component, "background", this.color48, -0.04772438f, 0.06666666f, 0), this.getComponentColor(component, "background", this.color49, -0.0018306673f, -0.02352941f, 0), this.getComponentColor(component, "background", this.color50, -0.0212406f, 0.13333333f, 0), this.getComponentColor(component, "background", this.color51, -0.030845039f, 0.23921567f, 0) };
                break;
            }
        }
        return array;
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundDefault(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient1(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundDefaultAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient1(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundMouseOverAndDefault(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect5();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundMouseOverAndDefaultAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundPressedAndDefault(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color17);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundPressedAndDefaultAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundDisabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color25);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundMouseOver(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient9(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundMouseOverAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient9(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundPressed(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color44);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient11(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundPressedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient11(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private RoundRectangle2D decodeRoundRect1() {
        this.roundRect.setRoundRect(this.decodeX(0.2857143f), this.decodeY(0.42857143f), this.decodeX(2.7142859f) - this.decodeX(0.2857143f), this.decodeY(2.857143f) - this.decodeY(0.42857143f), 12.0, 12.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect2() {
        this.roundRect.setRoundRect(this.decodeX(0.2857143f), this.decodeY(0.2857143f), this.decodeX(2.7142859f) - this.decodeX(0.2857143f), this.decodeY(2.7142859f) - this.decodeY(0.2857143f), 9.0, 9.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect3() {
        this.roundRect.setRoundRect(this.decodeX(0.42857143f), this.decodeY(0.42857143f), this.decodeX(2.5714285f) - this.decodeX(0.42857143f), this.decodeY(2.5714285f) - this.decodeY(0.42857143f), 7.0, 7.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect4() {
        this.roundRect.setRoundRect(this.decodeX(0.08571429f), this.decodeY(0.08571429f), this.decodeX(2.914286f) - this.decodeX(0.08571429f), this.decodeY(2.914286f) - this.decodeY(0.08571429f), 11.0, 11.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect5() {
        this.roundRect.setRoundRect(this.decodeX(0.2857143f), this.decodeY(0.42857143f), this.decodeX(2.7142859f) - this.decodeX(0.2857143f), this.decodeY(2.857143f) - this.decodeY(0.42857143f), 9.0, 9.0);
        return this.roundRect;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.05f, 0.5f, 0.95f }, new Color[] { this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.024f, 0.06f, 0.276f, 0.6f, 0.65f, 0.7f, 0.856f, 0.96f, 0.98399997f, 1.0f }, new Color[] { (Color)this.componentColors[0], this.decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5f), (Color)this.componentColors[1], this.decodeColor((Color)this.componentColors[1], (Color)this.componentColors[2], 0.5f), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[2], 0.5f), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[3], 0.5f), (Color)this.componentColors[3], this.decodeColor((Color)this.componentColors[3], (Color)this.componentColors[4], 0.5f), (Color)this.componentColors[4] });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.05f, 0.5f, 0.95f }, new Color[] { this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.05f, 0.5f, 0.95f }, new Color[] { this.color18, this.decodeColor(this.color18, this.color19, 0.5f), this.color19 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.09f, 0.52f, 0.95f }, new Color[] { this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.03f, 0.06f, 0.33f, 0.6f, 0.65f, 0.7f, 0.825f, 0.95f, 0.975f, 1.0f }, new Color[] { this.color28, this.decodeColor(this.color28, this.color29, 0.5f), this.color29, this.decodeColor(this.color29, this.color30, 0.5f), this.color30, this.decodeColor(this.color30, this.color30, 0.5f), this.color30, this.decodeColor(this.color30, this.color31, 0.5f), this.color31, this.decodeColor(this.color31, this.color32, 0.5f), this.color32 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.09f, 0.52f, 0.95f }, new Color[] { this.color33, this.decodeColor(this.color33, this.color34, 0.5f), this.color34 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.03f, 0.06f, 0.33f, 0.6f, 0.65f, 0.7f, 0.825f, 0.95f, 0.975f, 1.0f }, new Color[] { (Color)this.componentColors[0], this.decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5f), (Color)this.componentColors[1], this.decodeColor((Color)this.componentColors[1], (Color)this.componentColors[2], 0.5f), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[2], 0.5f), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[3], 0.5f), (Color)this.componentColors[3], this.decodeColor((Color)this.componentColors[3], (Color)this.componentColors[4], 0.5f), (Color)this.componentColors[4] });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.09f, 0.52f, 0.95f }, new Color[] { this.color39, this.decodeColor(this.color39, this.color40, 0.5f), this.color40 });
    }
    
    private Paint decodeGradient10(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.024f, 0.06f, 0.276f, 0.6f, 0.65f, 0.7f, 0.856f, 0.96f, 0.98f, 1.0f }, new Color[] { (Color)this.componentColors[0], this.decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5f), (Color)this.componentColors[1], this.decodeColor((Color)this.componentColors[1], (Color)this.componentColors[2], 0.5f), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[2], 0.5f), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[3], 0.5f), (Color)this.componentColors[3], this.decodeColor((Color)this.componentColors[3], (Color)this.componentColors[3], 0.5f), (Color)this.componentColors[3] });
    }
    
    private Paint decodeGradient11(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.05f, 0.5f, 0.95f }, new Color[] { this.color45, this.decodeColor(this.color45, this.color46, 0.5f), this.color46 });
    }
}
