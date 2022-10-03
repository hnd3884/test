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

final class InternalFrameTitlePaneMaximizeButtonPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED_WINDOWMAXIMIZED = 1;
    static final int BACKGROUND_ENABLED_WINDOWMAXIMIZED = 2;
    static final int BACKGROUND_MOUSEOVER_WINDOWMAXIMIZED = 3;
    static final int BACKGROUND_PRESSED_WINDOWMAXIMIZED = 4;
    static final int BACKGROUND_ENABLED_WINDOWNOTFOCUSED_WINDOWMAXIMIZED = 5;
    static final int BACKGROUND_MOUSEOVER_WINDOWNOTFOCUSED_WINDOWMAXIMIZED = 6;
    static final int BACKGROUND_PRESSED_WINDOWNOTFOCUSED_WINDOWMAXIMIZED = 7;
    static final int BACKGROUND_DISABLED = 8;
    static final int BACKGROUND_ENABLED = 9;
    static final int BACKGROUND_MOUSEOVER = 10;
    static final int BACKGROUND_PRESSED = 11;
    static final int BACKGROUND_ENABLED_WINDOWNOTFOCUSED = 12;
    static final int BACKGROUND_MOUSEOVER_WINDOWNOTFOCUSED = 13;
    static final int BACKGROUND_PRESSED_WINDOWNOTFOCUSED = 14;
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
    private Color color56;
    private Color color57;
    private Color color58;
    private Color color59;
    private Color color60;
    private Color color61;
    private Color color62;
    private Color color63;
    private Color color64;
    private Color color65;
    private Color color66;
    private Color color67;
    private Color color68;
    private Color color69;
    private Color color70;
    private Color color71;
    private Color color72;
    private Object[] componentColors;
    
    public InternalFrameTitlePaneMaximizeButtonPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusGreen", 0.43362403f, -0.6792196f, 0.054901958f, 0);
        this.color2 = this.decodeColor("nimbusGreen", 0.44056845f, -0.631913f, -0.039215684f, 0);
        this.color3 = this.decodeColor("nimbusGreen", 0.44056845f, -0.67475206f, 0.06666666f, 0);
        this.color4 = new Color(255, 200, 0, 255);
        this.color5 = this.decodeColor("nimbusGreen", 0.4355179f, -0.6581704f, -0.011764705f, 0);
        this.color6 = this.decodeColor("nimbusGreen", 0.44484192f, -0.644647f, -0.031372547f, 0);
        this.color7 = this.decodeColor("nimbusGreen", 0.44484192f, -0.6480447f, 0.0f, 0);
        this.color8 = this.decodeColor("nimbusGreen", 0.4366002f, -0.6368381f, -0.04705882f, 0);
        this.color9 = this.decodeColor("nimbusGreen", 0.44484192f, -0.6423572f, -0.05098039f, 0);
        this.color10 = this.decodeColor("nimbusBlueGrey", 0.0055555105f, -0.062449392f, 0.07058823f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", -0.008547008f, -0.04174325f, -0.0039215684f, -13);
        this.color12 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.049920253f, 0.031372547f, 0);
        this.color13 = this.decodeColor("nimbusBlueGrey", 0.0055555105f, -0.0029994324f, -0.38039216f, -185);
        this.color14 = this.decodeColor("nimbusGreen", 0.1627907f, 0.2793296f, -0.6431373f, 0);
        this.color15 = this.decodeColor("nimbusGreen", 0.025363803f, 0.2454313f, -0.2392157f, 0);
        this.color16 = this.decodeColor("nimbusGreen", 0.02642706f, -0.3456704f, -0.011764705f, 0);
        this.color17 = this.decodeColor("nimbusGreen", 0.025363803f, 0.2373128f, -0.23529413f, 0);
        this.color18 = this.decodeColor("nimbusGreen", 0.025363803f, 0.0655365f, -0.13333333f, 0);
        this.color19 = this.decodeColor("nimbusGreen", -0.0087068975f, -0.009330213f, -0.32156864f, 0);
        this.color20 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -13);
        this.color21 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -33);
        this.color22 = this.decodeColor("nimbusGreen", 0.1627907f, 0.2793296f, -0.627451f, 0);
        this.color23 = this.decodeColor("nimbusGreen", 0.04572721f, 0.2793296f, -0.37254903f, 0);
        this.color24 = this.decodeColor("nimbusGreen", 0.009822637f, -0.34243205f, 0.054901958f, 0);
        this.color25 = this.decodeColor("nimbusGreen", 0.010559708f, 0.13167858f, -0.11764705f, 0);
        this.color26 = this.decodeColor("nimbusGreen", 0.010559708f, 0.12599629f, -0.11372548f, 0);
        this.color27 = this.decodeColor("nimbusGreen", 0.010559708f, 9.2053413E-4f, -0.011764705f, 0);
        this.color28 = this.decodeColor("nimbusGreen", 0.015249729f, 0.2793296f, -0.22352943f, -49);
        this.color29 = this.decodeColor("nimbusGreen", 0.01279068f, 0.2793296f, -0.19215685f, 0);
        this.color30 = this.decodeColor("nimbusGreen", 0.013319805f, 0.2793296f, -0.20784315f, 0);
        this.color31 = this.decodeColor("nimbusGreen", 0.009604409f, 0.2793296f, -0.16862744f, 0);
        this.color32 = this.decodeColor("nimbusGreen", 0.011600211f, 0.2793296f, -0.15294117f, 0);
        this.color33 = this.decodeColor("nimbusGreen", 0.011939123f, 0.2793296f, -0.16470587f, 0);
        this.color34 = this.decodeColor("nimbusGreen", 0.009506017f, 0.257901f, -0.15294117f, 0);
        this.color35 = this.decodeColor("nimbusGreen", -0.17054264f, -0.7206704f, -0.7019608f, 0);
        this.color36 = this.decodeColor("nimbusGreen", 0.07804492f, 0.2793296f, -0.47058827f, 0);
        this.color37 = this.decodeColor("nimbusGreen", 0.03592503f, -0.23865601f, -0.15686274f, 0);
        this.color38 = this.decodeColor("nimbusGreen", 0.035979107f, 0.23766291f, -0.3254902f, 0);
        this.color39 = this.decodeColor("nimbusGreen", 0.03690417f, 0.2793296f, -0.33333334f, 0);
        this.color40 = this.decodeColor("nimbusGreen", 0.09681849f, 0.2793296f, -0.5137255f, 0);
        this.color41 = this.decodeColor("nimbusGreen", 0.06535478f, 0.2793296f, -0.44705883f, 0);
        this.color42 = this.decodeColor("nimbusGreen", 0.0675526f, 0.2793296f, -0.454902f, 0);
        this.color43 = this.decodeColor("nimbusGreen", 0.060800627f, 0.2793296f, -0.4392157f, 0);
        this.color44 = this.decodeColor("nimbusGreen", 0.06419912f, 0.2793296f, -0.42352942f, 0);
        this.color45 = this.decodeColor("nimbusGreen", 0.06375685f, 0.2793296f, -0.43137255f, 0);
        this.color46 = this.decodeColor("nimbusGreen", 0.048207358f, 0.2793296f, -0.3882353f, 0);
        this.color47 = this.decodeColor("nimbusGreen", 0.057156876f, 0.2793296f, -0.42352942f, 0);
        this.color48 = this.decodeColor("nimbusGreen", 0.44056845f, -0.62133265f, -0.109803915f, 0);
        this.color49 = this.decodeColor("nimbusGreen", 0.44056845f, -0.5843068f, -0.27058825f, 0);
        this.color50 = this.decodeColor("nimbusGreen", 0.4294573f, -0.698349f, 0.17647058f, 0);
        this.color51 = this.decodeColor("nimbusGreen", 0.45066953f, -0.665394f, 0.07843137f, 0);
        this.color52 = this.decodeColor("nimbusGreen", 0.44056845f, -0.65913194f, 0.062745094f, 0);
        this.color53 = this.decodeColor("nimbusGreen", 0.44056845f, -0.6609689f, 0.086274505f, 0);
        this.color54 = this.decodeColor("nimbusGreen", 0.44056845f, -0.6578432f, 0.04705882f, 0);
        this.color55 = this.decodeColor("nimbusGreen", 0.4355179f, -0.6633787f, 0.05098039f, 0);
        this.color56 = this.decodeColor("nimbusGreen", 0.4355179f, -0.664548f, 0.06666666f, 0);
        this.color57 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.029445238f, -0.30980393f, -13);
        this.color58 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.027957506f, -0.31764707f, -33);
        this.color59 = this.decodeColor("nimbusGreen", 0.43202144f, -0.64722407f, -0.007843137f, 0);
        this.color60 = this.decodeColor("nimbusGreen", 0.44056845f, -0.6339652f, -0.02352941f, 0);
        this.color61 = new Color(165, 169, 176, 255);
        this.color62 = this.decodeColor("nimbusBlueGrey", -0.00505054f, -0.057128258f, 0.062745094f, 0);
        this.color63 = this.decodeColor("nimbusBlueGrey", -0.003968239f, -0.035257496f, -0.015686274f, 0);
        this.color64 = new Color(64, 88, 0, 255);
        this.color65 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color66 = this.decodeColor("nimbusBlueGrey", 0.004830897f, -0.00920473f, 0.14509803f, -101);
        this.color67 = this.decodeColor("nimbusGreen", 0.009564877f, 0.100521624f, -0.109803915f, 0);
        this.color68 = new Color(113, 125, 0, 255);
        this.color69 = this.decodeColor("nimbusBlueGrey", 0.0025252104f, -0.0067527294f, 0.086274505f, -65);
        this.color70 = this.decodeColor("nimbusGreen", 0.03129223f, 0.2793296f, -0.27450982f, 0);
        this.color71 = new Color(19, 48, 0, 255);
        this.color72 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.029445238f, -0.30980393f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 1: {
                this.paintBackgroundDisabledAndWindowMaximized(graphics2D);
                break;
            }
            case 2: {
                this.paintBackgroundEnabledAndWindowMaximized(graphics2D);
                break;
            }
            case 3: {
                this.paintBackgroundMouseOverAndWindowMaximized(graphics2D);
                break;
            }
            case 4: {
                this.paintBackgroundPressedAndWindowMaximized(graphics2D);
                break;
            }
            case 5: {
                this.paintBackgroundEnabledAndWindowNotFocusedAndWindowMaximized(graphics2D);
                break;
            }
            case 6: {
                this.paintBackgroundMouseOverAndWindowNotFocusedAndWindowMaximized(graphics2D);
                break;
            }
            case 7: {
                this.paintBackgroundPressedAndWindowNotFocusedAndWindowMaximized(graphics2D);
                break;
            }
            case 8: {
                this.paintBackgroundDisabled(graphics2D);
                break;
            }
            case 9: {
                this.paintBackgroundEnabled(graphics2D);
                break;
            }
            case 10: {
                this.paintBackgroundMouseOver(graphics2D);
                break;
            }
            case 11: {
                this.paintBackgroundPressed(graphics2D);
                break;
            }
            case 12: {
                this.paintBackgroundEnabledAndWindowNotFocused(graphics2D);
                break;
            }
            case 13: {
                this.paintBackgroundMouseOverAndWindowNotFocused(graphics2D);
                break;
            }
            case 14: {
                this.paintBackgroundPressedAndWindowNotFocused(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundDisabledAndWindowMaximized(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient1(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color5);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color7);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect6();
        graphics2D.setPaint(this.color8);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect7();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect8();
        graphics2D.setPaint(this.color7);
        graphics2D.fill(this.rect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundEnabledAndWindowMaximized(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color19);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color19);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color19);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color19);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect9();
        graphics2D.setPaint(this.color19);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect7();
        graphics2D.setPaint(this.color19);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect10();
        graphics2D.setPaint(this.color19);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect8();
        graphics2D.setPaint(this.color19);
        graphics2D.fill(this.rect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color20);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color21);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundMouseOverAndWindowMaximized(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color28);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color29);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color30);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color31);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect9();
        graphics2D.setPaint(this.color32);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect7();
        graphics2D.setPaint(this.color33);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect10();
        graphics2D.setPaint(this.color34);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect8();
        graphics2D.setPaint(this.color31);
        graphics2D.fill(this.rect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color20);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color21);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundPressedAndWindowMaximized(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient9(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color40);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color41);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color42);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color43);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect6();
        graphics2D.setPaint(this.color44);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect7();
        graphics2D.setPaint(this.color45);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect10();
        graphics2D.setPaint(this.color46);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect8();
        graphics2D.setPaint(this.color47);
        graphics2D.fill(this.rect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color20);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color21);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundEnabledAndWindowNotFocusedAndWindowMaximized(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient11(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color54);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color55);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect8();
        graphics2D.setPaint(this.color56);
        graphics2D.fill(this.rect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color57);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color58);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundMouseOverAndWindowNotFocusedAndWindowMaximized(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color28);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color29);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color30);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color31);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect9();
        graphics2D.setPaint(this.color32);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect7();
        graphics2D.setPaint(this.color33);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect10();
        graphics2D.setPaint(this.color34);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect8();
        graphics2D.setPaint(this.color31);
        graphics2D.fill(this.rect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color20);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color21);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundPressedAndWindowNotFocusedAndWindowMaximized(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient9(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color40);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color41);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color42);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.color43);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect6();
        graphics2D.setPaint(this.color44);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect7();
        graphics2D.setPaint(this.color45);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect10();
        graphics2D.setPaint(this.color46);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect8();
        graphics2D.setPaint(this.color47);
        graphics2D.fill(this.rect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color20);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color21);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundDisabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient1(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient12(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color61);
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient13(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color64);
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color65);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundMouseOver(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color66);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient14(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color68);
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color65);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundPressed(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color69);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient15(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color71);
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color65);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundEnabledAndWindowNotFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient16(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color72);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundMouseOverAndWindowNotFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color66);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient14(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color68);
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color65);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundPressedAndWindowNotFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.color69);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient15(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.rect);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color71);
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color65);
        graphics2D.fill(this.path);
    }
    
    private RoundRectangle2D decodeRoundRect1() {
        this.roundRect.setRoundRect(this.decodeX(1.0f), this.decodeY(1.0f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(1.9444444f) - this.decodeY(1.0f), 8.600000381469727, 8.600000381469727);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect2() {
        this.roundRect.setRoundRect(this.decodeX(1.0526316f), this.decodeY(1.0555556f), this.decodeX(1.9473684f) - this.decodeX(1.0526316f), this.decodeY(1.8888888f) - this.decodeY(1.0555556f), 6.75, 6.75);
        return this.roundRect;
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(1.0f), this.decodeX(1.0f) - this.decodeX(1.0f), this.decodeY(1.0f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(1.2165072f), this.decodeY(1.2790405f), this.decodeX(1.6746411f) - this.decodeX(1.2165072f), this.decodeY(1.3876263f) - this.decodeY(1.2790405f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect3() {
        this.rect.setRect(this.decodeX(1.2212919f), this.decodeY(1.6047981f), this.decodeX(1.270335f) - this.decodeX(1.2212919f), this.decodeY(1.3876263f) - this.decodeY(1.6047981f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect4() {
        this.rect.setRect(this.decodeX(1.2643541f), this.decodeY(1.5542929f), this.decodeX(1.6315789f) - this.decodeX(1.2643541f), this.decodeY(1.5997474f) - this.decodeY(1.5542929f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect5() {
        this.rect.setRect(this.decodeX(1.6267943f), this.decodeY(1.3888888f), this.decodeX(1.673445f) - this.decodeX(1.6267943f), this.decodeY(1.6085858f) - this.decodeY(1.3888888f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect6() {
        this.rect.setRect(this.decodeX(1.3684211f), this.decodeY(1.6111112f), this.decodeX(1.4210527f) - this.decodeX(1.3684211f), this.decodeY(1.7777778f) - this.decodeY(1.6111112f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect7() {
        this.rect.setRect(this.decodeX(1.4389952f), this.decodeY(1.7209597f), this.decodeX(1.7882775f) - this.decodeX(1.4389952f), this.decodeY(1.7765152f) - this.decodeY(1.7209597f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect8() {
        this.rect.setRect(this.decodeX(1.5645933f), this.decodeY(1.4078283f), this.decodeX(1.7870812f) - this.decodeX(1.5645933f), this.decodeY(1.5239899f) - this.decodeY(1.4078283f));
        return this.rect;
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.2105263f), this.decodeY(1.2222222f));
        this.path.lineTo(this.decodeX(1.6315789f), this.decodeY(1.2222222f));
        this.path.lineTo(this.decodeX(1.6315789f), this.decodeY(1.5555556f));
        this.path.lineTo(this.decodeX(1.2105263f), this.decodeY(1.5555556f));
        this.path.lineTo(this.decodeX(1.2105263f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(1.2631578f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(1.2631578f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(1.5789473f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(1.5789473f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(1.2105263f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(1.2105263f), this.decodeY(1.2222222f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.6842105f), this.decodeY(1.3888888f));
        this.path.lineTo(this.decodeX(1.6842105f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(1.7368422f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(1.7368422f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(1.4210527f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(1.4210527f), this.decodeY(1.6111112f));
        this.path.lineTo(this.decodeX(1.3684211f), this.decodeY(1.6111112f));
        this.path.lineTo(this.decodeX(1.3684211f), this.decodeY(1.7222222f));
        this.path.lineTo(this.decodeX(1.7894738f), this.decodeY(1.7222222f));
        this.path.lineTo(this.decodeX(1.7894738f), this.decodeY(1.3888888f));
        this.path.lineTo(this.decodeX(1.6842105f), this.decodeY(1.3888888f));
        this.path.closePath();
        return this.path;
    }
    
    private RoundRectangle2D decodeRoundRect3() {
        this.roundRect.setRoundRect(this.decodeX(1.0f), this.decodeY(1.6111112f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(2.0f) - this.decodeY(1.6111112f), 6.0, 6.0);
        return this.roundRect;
    }
    
    private Rectangle2D decodeRect9() {
        this.rect.setRect(this.decodeX(1.3815789f), this.decodeY(1.6111112f), this.decodeX(1.4366028f) - this.decodeX(1.3815789f), this.decodeY(1.7739899f) - this.decodeY(1.6111112f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect10() {
        this.rect.setRect(this.decodeX(1.7918661f), this.decodeY(1.7752526f), this.decodeX(1.8349283f) - this.decodeX(1.7918661f), this.decodeY(1.4217172f) - this.decodeY(1.7752526f));
        return this.rect;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.1913875f), this.decodeY(1.2916666f));
        this.path.lineTo(this.decodeX(1.1925838f), this.decodeY(1.7462121f));
        this.path.lineTo(this.decodeX(1.8157895f), this.decodeY(1.7449496f));
        this.path.lineTo(this.decodeX(1.819378f), this.decodeY(1.2916666f));
        this.path.lineTo(this.decodeX(1.722488f), this.decodeY(1.2916666f));
        this.path.lineTo(this.decodeX(1.7320573f), this.decodeY(1.669192f));
        this.path.lineTo(this.decodeX(1.2799044f), this.decodeY(1.6565657f));
        this.path.lineTo(this.decodeX(1.284689f), this.decodeY(1.3863636f));
        this.path.lineTo(this.decodeX(1.7260766f), this.decodeY(1.385101f));
        this.path.lineTo(this.decodeX(1.722488f), this.decodeY(1.2904041f));
        this.path.lineTo(this.decodeX(1.1913875f), this.decodeY(1.2916666f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.2105263f), this.decodeY(1.2222222f));
        this.path.lineTo(this.decodeX(1.2105263f), this.decodeY(1.7222222f));
        this.path.lineTo(this.decodeX(1.7894738f), this.decodeY(1.7222222f));
        this.path.lineTo(this.decodeX(1.7894738f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(1.7368422f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(1.7368422f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(1.2631578f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(1.2631578f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(1.7894738f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(1.7894738f), this.decodeY(1.2222222f));
        this.path.lineTo(this.decodeX(1.2105263f), this.decodeY(1.2222222f));
        this.path.closePath();
        return this.path;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color1, this.decodeColor(this.color1, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color2, 0.5f), this.color2 });
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
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color14, this.decodeColor(this.color14, this.color15, 0.5f), this.color15 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color16, this.decodeColor(this.color16, this.color15, 0.5f), this.color15, this.decodeColor(this.color15, this.color17, 0.5f), this.color17, this.decodeColor(this.color17, this.color18, 0.5f), this.color18 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color22, this.decodeColor(this.color22, this.color23, 0.5f), this.color23 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color24, this.decodeColor(this.color24, this.color25, 0.5f), this.color25, this.decodeColor(this.color25, this.color26, 0.5f), this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color35, this.decodeColor(this.color35, this.color36, 0.5f), this.color36 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color37, this.decodeColor(this.color37, this.color38, 0.5f), this.color38, this.decodeColor(this.color38, this.color39, 0.5f), this.color39, this.decodeColor(this.color39, this.color18, 0.5f), this.color18 });
    }
    
    private Paint decodeGradient10(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color48, this.decodeColor(this.color48, this.color49, 0.5f), this.color49 });
    }
    
    private Paint decodeGradient11(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color50, this.decodeColor(this.color50, this.color51, 0.5f), this.color51, this.decodeColor(this.color51, this.color52, 0.5f), this.color52, this.decodeColor(this.color52, this.color53, 0.5f), this.color53 });
    }
    
    private Paint decodeGradient12(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.6082097f, 0.6766467f, 0.83832335f, 1.0f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color59, 0.5f), this.color59, this.decodeColor(this.color59, this.color60, 0.5f), this.color60, this.decodeColor(this.color60, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient13(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.26047903f, 0.6302395f, 1.0f }, new Color[] { this.color62, this.decodeColor(this.color62, this.color63, 0.5f), this.color63 });
    }
    
    private Paint decodeGradient14(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color24, this.decodeColor(this.color24, this.color67, 0.5f), this.color67, this.decodeColor(this.color67, this.color25, 0.5f), this.color25, this.decodeColor(this.color25, this.color27, 0.5f), this.color27 });
    }
    
    private Paint decodeGradient15(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.66659296f, 0.79341316f, 0.8967066f, 1.0f }, new Color[] { this.color37, this.decodeColor(this.color37, this.color38, 0.5f), this.color38, this.decodeColor(this.color38, this.color39, 0.5f), this.color39, this.decodeColor(this.color39, this.color70, 0.5f), this.color70 });
    }
    
    private Paint decodeGradient16(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.6291678f, 0.7185629f, 0.8592814f, 1.0f }, new Color[] { this.color50, this.decodeColor(this.color50, this.color52, 0.5f), this.color52, this.decodeColor(this.color52, this.color52, 0.5f), this.color52, this.decodeColor(this.color52, this.color53, 0.5f), this.color53 });
    }
}
