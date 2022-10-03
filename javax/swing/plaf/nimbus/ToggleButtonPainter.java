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

final class ToggleButtonPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_FOCUSED = 3;
    static final int BACKGROUND_MOUSEOVER = 4;
    static final int BACKGROUND_MOUSEOVER_FOCUSED = 5;
    static final int BACKGROUND_PRESSED = 6;
    static final int BACKGROUND_PRESSED_FOCUSED = 7;
    static final int BACKGROUND_SELECTED = 8;
    static final int BACKGROUND_SELECTED_FOCUSED = 9;
    static final int BACKGROUND_PRESSED_SELECTED = 10;
    static final int BACKGROUND_PRESSED_SELECTED_FOCUSED = 11;
    static final int BACKGROUND_MOUSEOVER_SELECTED = 12;
    static final int BACKGROUND_MOUSEOVER_SELECTED_FOCUSED = 13;
    static final int BACKGROUND_DISABLED_SELECTED = 14;
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
    private Object[] componentColors;
    
    public ToggleButtonPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.06885965f, -0.36862746f, -232);
        this.color2 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06766917f, 0.07843137f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06484103f, 0.027450979f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.08477524f, 0.16862744f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", -0.015872955f, -0.080091536f, 0.15686274f, 0);
        this.color6 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07016757f, 0.12941176f, 0);
        this.color7 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07052632f, 0.1372549f, 0);
        this.color8 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.070878744f, 0.14509803f, 0);
        this.color9 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.06885965f, -0.36862746f, -190);
        this.color10 = this.decodeColor("nimbusBlueGrey", -0.055555522f, -0.05356429f, -0.12549019f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.0147816315f, -0.3764706f, 0);
        this.color12 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.10655806f, 0.24313724f, 0);
        this.color13 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.09823123f, 0.2117647f, 0);
        this.color14 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.0749532f, 0.24705881f, 0);
        this.color15 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color16 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.color17 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.020974077f, -0.21960783f, 0);
        this.color18 = this.decodeColor("nimbusBlueGrey", 0.0f, 0.11169591f, -0.53333336f, 0);
        this.color19 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.10658931f, 0.25098038f, 0);
        this.color20 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.098526314f, 0.2352941f, 0);
        this.color21 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07333623f, 0.20392156f, 0);
        this.color22 = new Color(245, 250, 255, 160);
        this.color23 = this.decodeColor("nimbusBlueGrey", 0.055555582f, 0.8894737f, -0.7176471f, 0);
        this.color24 = this.decodeColor("nimbusBlueGrey", 0.0f, 5.847961E-4f, -0.32156864f, 0);
        this.color25 = this.decodeColor("nimbusBlueGrey", -0.00505054f, -0.05960039f, 0.10196078f, 0);
        this.color26 = this.decodeColor("nimbusBlueGrey", -0.008547008f, -0.04772438f, 0.06666666f, 0);
        this.color27 = this.decodeColor("nimbusBlueGrey", -0.0027777553f, -0.0018306673f, -0.02352941f, 0);
        this.color28 = this.decodeColor("nimbusBlueGrey", -0.0027777553f, -0.0212406f, 0.13333333f, 0);
        this.color29 = this.decodeColor("nimbusBlueGrey", 0.0055555105f, -0.030845039f, 0.23921567f, 0);
        this.color30 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -86);
        this.color31 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06472479f, -0.23137254f, 0);
        this.color32 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.06959064f, -0.0745098f, 0);
        this.color33 = this.decodeColor("nimbusBlueGrey", 0.0138888955f, -0.06401469f, -0.07058823f, 0);
        this.color34 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06530018f, 0.035294116f, 0);
        this.color35 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06507177f, 0.031372547f, 0);
        this.color36 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.05338346f, -0.47058824f, 0);
        this.color37 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.049301825f, -0.36078432f, 0);
        this.color38 = this.decodeColor("nimbusBlueGrey", -0.018518567f, -0.03909774f, -0.2509804f, 0);
        this.color39 = this.decodeColor("nimbusBlueGrey", -0.00505054f, -0.040013492f, -0.13333333f, 0);
        this.color40 = this.decodeColor("nimbusBlueGrey", 0.01010108f, -0.039558575f, -0.1372549f, 0);
        this.color41 = this.decodeColor("nimbusBlueGrey", -0.01111114f, -0.060526315f, -0.3529412f, 0);
        this.color42 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.064372465f, -0.2352941f, 0);
        this.color43 = this.decodeColor("nimbusBlueGrey", -0.006944418f, -0.0595709f, -0.12941176f, 0);
        this.color44 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.061075766f, -0.031372547f, 0);
        this.color45 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06080256f, -0.035294116f, 0);
        this.color46 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -220);
        this.color47 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.066408664f, 0.054901958f, 0);
        this.color48 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06807348f, 0.086274505f, 0);
        this.color49 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06924191f, 0.109803915f, 0);
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
                this.paintBackgroundMouseOver(graphics2D);
                break;
            }
            case 5: {
                this.paintBackgroundMouseOverAndFocused(graphics2D);
                break;
            }
            case 6: {
                this.paintBackgroundPressed(graphics2D);
                break;
            }
            case 7: {
                this.paintBackgroundPressedAndFocused(graphics2D);
                break;
            }
            case 8: {
                this.paintBackgroundSelected(graphics2D);
                break;
            }
            case 9: {
                this.paintBackgroundSelectedAndFocused(graphics2D);
                break;
            }
            case 10: {
                this.paintBackgroundPressedAndSelected(graphics2D);
                break;
            }
            case 11: {
                this.paintBackgroundPressedAndSelectedAndFocused(graphics2D);
                break;
            }
            case 12: {
                this.paintBackgroundMouseOverAndSelected(graphics2D);
                break;
            }
            case 13: {
                this.paintBackgroundMouseOverAndSelectedAndFocused(graphics2D);
                break;
            }
            case 14: {
                this.paintBackgroundDisabledAndSelected(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected Object[] getExtendedCacheKeys(final JComponent component) {
        Object[] array = null;
        switch (this.state) {
            case 2: {
                array = new Object[] { this.getComponentColor(component, "background", this.color12, -0.10655806f, 0.24313724f, 0), this.getComponentColor(component, "background", this.color13, -0.09823123f, 0.2117647f, 0), this.getComponentColor(component, "background", this.color6, -0.07016757f, 0.12941176f, 0), this.getComponentColor(component, "background", this.color14, -0.0749532f, 0.24705881f, 0), this.getComponentColor(component, "background", this.color15, -0.110526316f, 0.25490195f, 0) };
                break;
            }
            case 3: {
                array = new Object[] { this.getComponentColor(component, "background", this.color12, -0.10655806f, 0.24313724f, 0), this.getComponentColor(component, "background", this.color13, -0.09823123f, 0.2117647f, 0), this.getComponentColor(component, "background", this.color6, -0.07016757f, 0.12941176f, 0), this.getComponentColor(component, "background", this.color14, -0.0749532f, 0.24705881f, 0), this.getComponentColor(component, "background", this.color15, -0.110526316f, 0.25490195f, 0) };
                break;
            }
            case 4: {
                array = new Object[] { this.getComponentColor(component, "background", this.color19, -0.10658931f, 0.25098038f, 0), this.getComponentColor(component, "background", this.color20, -0.098526314f, 0.2352941f, 0), this.getComponentColor(component, "background", this.color21, -0.07333623f, 0.20392156f, 0), this.getComponentColor(component, "background", this.color15, -0.110526316f, 0.25490195f, 0) };
                break;
            }
            case 5: {
                array = new Object[] { this.getComponentColor(component, "background", this.color19, -0.10658931f, 0.25098038f, 0), this.getComponentColor(component, "background", this.color20, -0.098526314f, 0.2352941f, 0), this.getComponentColor(component, "background", this.color21, -0.07333623f, 0.20392156f, 0), this.getComponentColor(component, "background", this.color15, -0.110526316f, 0.25490195f, 0) };
                break;
            }
            case 6: {
                array = new Object[] { this.getComponentColor(component, "background", this.color25, -0.05960039f, 0.10196078f, 0), this.getComponentColor(component, "background", this.color26, -0.04772438f, 0.06666666f, 0), this.getComponentColor(component, "background", this.color27, -0.0018306673f, -0.02352941f, 0), this.getComponentColor(component, "background", this.color28, -0.0212406f, 0.13333333f, 0), this.getComponentColor(component, "background", this.color29, -0.030845039f, 0.23921567f, 0) };
                break;
            }
            case 7: {
                array = new Object[] { this.getComponentColor(component, "background", this.color25, -0.05960039f, 0.10196078f, 0), this.getComponentColor(component, "background", this.color26, -0.04772438f, 0.06666666f, 0), this.getComponentColor(component, "background", this.color27, -0.0018306673f, -0.02352941f, 0), this.getComponentColor(component, "background", this.color28, -0.0212406f, 0.13333333f, 0), this.getComponentColor(component, "background", this.color29, -0.030845039f, 0.23921567f, 0) };
                break;
            }
            case 8: {
                array = new Object[] { this.getComponentColor(component, "background", this.color33, -0.06401469f, -0.07058823f, 0), this.getComponentColor(component, "background", this.color34, -0.06530018f, 0.035294116f, 0), this.getComponentColor(component, "background", this.color35, -0.06507177f, 0.031372547f, 0) };
                break;
            }
            case 9: {
                array = new Object[] { this.getComponentColor(component, "background", this.color33, -0.06401469f, -0.07058823f, 0), this.getComponentColor(component, "background", this.color34, -0.06530018f, 0.035294116f, 0), this.getComponentColor(component, "background", this.color35, -0.06507177f, 0.031372547f, 0) };
                break;
            }
            case 10: {
                array = new Object[] { this.getComponentColor(component, "background", this.color38, -0.03909774f, -0.2509804f, 0), this.getComponentColor(component, "background", this.color39, -0.040013492f, -0.13333333f, 0), this.getComponentColor(component, "background", this.color40, -0.039558575f, -0.1372549f, 0) };
                break;
            }
            case 11: {
                array = new Object[] { this.getComponentColor(component, "background", this.color38, -0.03909774f, -0.2509804f, 0), this.getComponentColor(component, "background", this.color39, -0.040013492f, -0.13333333f, 0), this.getComponentColor(component, "background", this.color40, -0.039558575f, -0.1372549f, 0) };
                break;
            }
            case 12: {
                array = new Object[] { this.getComponentColor(component, "background", this.color43, -0.0595709f, -0.12941176f, 0), this.getComponentColor(component, "background", this.color44, -0.061075766f, -0.031372547f, 0), this.getComponentColor(component, "background", this.color45, -0.06080256f, -0.035294116f, 0) };
                break;
            }
            case 13: {
                array = new Object[] { this.getComponentColor(component, "background", this.color43, -0.0595709f, -0.12941176f, 0), this.getComponentColor(component, "background", this.color44, -0.061075766f, -0.031372547f, 0), this.getComponentColor(component, "background", this.color45, -0.06080256f, -0.035294116f, 0) };
                break;
            }
        }
        return array;
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundDisabled(final Graphics2D graphics2D) {
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
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundMouseOver(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundMouseOverAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundPressed(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color22);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundPressedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundSelected(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect5();
        graphics2D.setPaint(this.color30);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient9(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundSelectedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect6();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient9(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundPressedAndSelected(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect5();
        graphics2D.setPaint(this.color30);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient11(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundPressedAndSelectedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect6();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient11(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundMouseOverAndSelected(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect5();
        graphics2D.setPaint(this.color30);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient12(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundMouseOverAndSelectedAndFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect6();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient12(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintBackgroundDisabledAndSelected(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect5();
        graphics2D.setPaint(this.color46);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient13(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient14(this.roundRect));
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
    
    private RoundRectangle2D decodeRoundRect6() {
        this.roundRect.setRoundRect(this.decodeX(0.08571429f), this.decodeY(0.08571429f), this.decodeX(2.914286f) - this.decodeX(0.08571429f), this.decodeY(2.9142857f) - this.decodeY(0.08571429f), 11.0, 11.0);
        return this.roundRect;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.09f, 0.52f, 0.95f }, new Color[] { this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.03f, 0.06f, 0.33f, 0.6f, 0.65f, 0.7f, 0.825f, 0.95f, 0.975f, 1.0f }, new Color[] { this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5, this.decodeColor(this.color5, this.color6, 0.5f), this.color6, this.decodeColor(this.color6, this.color6, 0.5f), this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.09f, 0.52f, 0.95f }, new Color[] { this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.024f, 0.06f, 0.276f, 0.6f, 0.65f, 0.7f, 0.856f, 0.96f, 0.98399997f, 1.0f }, new Color[] { (Color)this.componentColors[0], this.decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5f), (Color)this.componentColors[1], this.decodeColor((Color)this.componentColors[1], (Color)this.componentColors[2], 0.5f), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[2], 0.5f), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[3], 0.5f), (Color)this.componentColors[3], this.decodeColor((Color)this.componentColors[3], (Color)this.componentColors[4], 0.5f), (Color)this.componentColors[4] });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.03f, 0.06f, 0.33f, 0.6f, 0.65f, 0.7f, 0.825f, 0.95f, 0.975f, 1.0f }, new Color[] { (Color)this.componentColors[0], this.decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5f), (Color)this.componentColors[1], this.decodeColor((Color)this.componentColors[1], (Color)this.componentColors[2], 0.5f), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[2], 0.5f), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[3], 0.5f), (Color)this.componentColors[3], this.decodeColor((Color)this.componentColors[3], (Color)this.componentColors[4], 0.5f), (Color)this.componentColors[4] });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.09f, 0.52f, 0.95f }, new Color[] { this.color17, this.decodeColor(this.color17, this.color18, 0.5f), this.color18 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.024f, 0.06f, 0.276f, 0.6f, 0.65f, 0.7f, 0.856f, 0.96f, 0.98f, 1.0f }, new Color[] { (Color)this.componentColors[0], this.decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5f), (Color)this.componentColors[1], this.decodeColor((Color)this.componentColors[1], (Color)this.componentColors[2], 0.5f), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[2], 0.5f), (Color)this.componentColors[2], this.decodeColor((Color)this.componentColors[2], (Color)this.componentColors[3], 0.5f), (Color)this.componentColors[3], this.decodeColor((Color)this.componentColors[3], (Color)this.componentColors[3], 0.5f), (Color)this.componentColors[3] });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.05f, 0.5f, 0.95f }, new Color[] { this.color23, this.decodeColor(this.color23, this.color24, 0.5f), this.color24 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color31, this.decodeColor(this.color31, this.color32, 0.5f), this.color32 });
    }
    
    private Paint decodeGradient10(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.06684492f, 0.13368984f, 0.56684494f, 1.0f }, new Color[] { (Color)this.componentColors[0], this.decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5f), (Color)this.componentColors[1], this.decodeColor((Color)this.componentColors[1], (Color)this.componentColors[2], 0.5f), (Color)this.componentColors[2] });
    }
    
    private Paint decodeGradient11(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color36, this.decodeColor(this.color36, this.color37, 0.5f), this.color37 });
    }
    
    private Paint decodeGradient12(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color41, this.decodeColor(this.color41, this.color42, 0.5f), this.color42 });
    }
    
    private Paint decodeGradient13(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color47, this.decodeColor(this.color47, this.color48, 0.5f), this.color48 });
    }
    
    private Paint decodeGradient14(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.06684492f, 0.13368984f, 0.56684494f, 1.0f }, new Color[] { this.color48, this.decodeColor(this.color48, this.color49, 0.5f), this.color49, this.decodeColor(this.color49, this.color49, 0.5f), this.color49 });
    }
}
