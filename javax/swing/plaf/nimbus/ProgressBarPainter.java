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

final class ProgressBarPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_ENABLED = 1;
    static final int BACKGROUND_DISABLED = 2;
    static final int FOREGROUND_ENABLED = 3;
    static final int FOREGROUND_ENABLED_FINISHED = 4;
    static final int FOREGROUND_ENABLED_INDETERMINATE = 5;
    static final int FOREGROUND_DISABLED = 6;
    static final int FOREGROUND_DISABLED_FINISHED = 7;
    static final int FOREGROUND_DISABLED_INDETERMINATE = 8;
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
    private Object[] componentColors;
    
    public ProgressBarPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.04845735f, -0.17647058f, 0);
        this.color2 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.061345987f, -0.027450979f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.097921275f, 0.18823528f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", 0.0138888955f, -0.0925083f, 0.12549019f, 0);
        this.color6 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.08222443f, 0.086274505f, 0);
        this.color7 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.08477524f, 0.16862744f, 0);
        this.color8 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.086996906f, 0.25490195f, 0);
        this.color9 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.061613273f, -0.02352941f, 0);
        this.color10 = this.decodeColor("nimbusBlueGrey", -0.01111114f, -0.061265234f, 0.05098039f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", 0.0138888955f, -0.09378991f, 0.19215685f, 0);
        this.color12 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.08455229f, 0.1607843f, 0);
        this.color13 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.08362049f, 0.12941176f, 0);
        this.color14 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.07826825f, 0.10588235f, 0);
        this.color15 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.07982456f, 0.1490196f, 0);
        this.color16 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.08099045f, 0.18431371f, 0);
        this.color17 = this.decodeColor("nimbusOrange", 0.0f, 0.0f, 0.0f, -156);
        this.color18 = this.decodeColor("nimbusOrange", -0.015796512f, 0.02094239f, -0.15294117f, 0);
        this.color19 = this.decodeColor("nimbusOrange", -0.004321605f, 0.02094239f, -0.0745098f, 0);
        this.color20 = this.decodeColor("nimbusOrange", -0.008021399f, 0.02094239f, -0.10196078f, 0);
        this.color21 = this.decodeColor("nimbusOrange", -0.011706904f, -0.1790576f, -0.02352941f, 0);
        this.color22 = this.decodeColor("nimbusOrange", -0.048691254f, 0.02094239f, -0.3019608f, 0);
        this.color23 = this.decodeColor("nimbusOrange", 0.003940329f, -0.7375322f, 0.17647058f, 0);
        this.color24 = this.decodeColor("nimbusOrange", 0.005506739f, -0.46764207f, 0.109803915f, 0);
        this.color25 = this.decodeColor("nimbusOrange", 0.0042127445f, -0.18595415f, 0.04705882f, 0);
        this.color26 = this.decodeColor("nimbusOrange", 0.0047626942f, 0.02094239f, 0.0039215684f, 0);
        this.color27 = this.decodeColor("nimbusOrange", 0.0047626942f, -0.15147138f, 0.1607843f, 0);
        this.color28 = this.decodeColor("nimbusOrange", 0.010665476f, -0.27317524f, 0.25098038f, 0);
        this.color29 = this.decodeColor("nimbusBlueGrey", -0.54444444f, -0.08748484f, 0.10588235f, 0);
        this.color30 = this.decodeColor("nimbusOrange", 0.0047626942f, -0.21715283f, 0.23921567f, 0);
        this.color31 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -173);
        this.color32 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -170);
        this.color33 = this.decodeColor("nimbusOrange", 0.024554357f, -0.8873145f, 0.10588235f, -156);
        this.color34 = this.decodeColor("nimbusOrange", -0.023593787f, -0.7963165f, 0.02352941f, 0);
        this.color35 = this.decodeColor("nimbusOrange", -0.010608241f, -0.7760873f, 0.043137252f, 0);
        this.color36 = this.decodeColor("nimbusOrange", -0.015402906f, -0.7840576f, 0.035294116f, 0);
        this.color37 = this.decodeColor("nimbusOrange", -0.017112307f, -0.8091547f, 0.058823526f, 0);
        this.color38 = this.decodeColor("nimbusOrange", -0.07044564f, -0.844649f, -0.019607842f, 0);
        this.color39 = this.decodeColor("nimbusOrange", -0.009704903f, -0.9381485f, 0.11372548f, 0);
        this.color40 = this.decodeColor("nimbusOrange", -4.4563413E-4f, -0.86742973f, 0.09411764f, 0);
        this.color41 = this.decodeColor("nimbusOrange", -4.4563413E-4f, -0.79896283f, 0.07843137f, 0);
        this.color42 = this.decodeColor("nimbusOrange", 0.0013274103f, -0.7530961f, 0.06666666f, 0);
        this.color43 = this.decodeColor("nimbusOrange", 0.0013274103f, -0.7644457f, 0.109803915f, 0);
        this.color44 = this.decodeColor("nimbusOrange", 0.009244293f, -0.78794646f, 0.13333333f, 0);
        this.color45 = this.decodeColor("nimbusBlueGrey", -0.015872955f, -0.0803539f, 0.16470587f, 0);
        this.color46 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.07968931f, 0.14509803f, 0);
        this.color47 = this.decodeColor("nimbusBlueGrey", 0.02222228f, -0.08779904f, 0.11764705f, 0);
        this.color48 = this.decodeColor("nimbusBlueGrey", 0.0138888955f, -0.075128086f, 0.14117646f, 0);
        this.color49 = this.decodeColor("nimbusBlueGrey", 0.0138888955f, -0.07604356f, 0.16470587f, 0);
        this.color50 = this.decodeColor("nimbusOrange", 0.0014062226f, -0.77816474f, 0.12941176f, 0);
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
                this.paintBackgroundDisabled(graphics2D);
                break;
            }
            case 3: {
                this.paintForegroundEnabled(graphics2D);
                break;
            }
            case 4: {
                this.paintForegroundEnabledAndFinished(graphics2D);
                break;
            }
            case 5: {
                this.paintForegroundEnabledAndIndeterminate(graphics2D);
                break;
            }
            case 6: {
                this.paintForegroundDisabled(graphics2D);
                break;
            }
            case 7: {
                this.paintForegroundDisabledAndFinished(graphics2D);
                break;
            }
            case 8: {
                this.paintForegroundDisabledAndIndeterminate(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient2(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundDisabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.decodeGradient3(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient4(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintForegroundEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color17);
        graphics2D.fill(this.path);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient5(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.decodeGradient6(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintForegroundEnabledAndFinished(final Graphics2D graphics2D) {
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color17);
        graphics2D.fill(this.path);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.decodeGradient5(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect6();
        graphics2D.setPaint(this.decodeGradient6(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintForegroundEnabledAndIndeterminate(final Graphics2D graphics2D) {
        this.rect = this.decodeRect7();
        graphics2D.setPaint(this.decodeGradient7(this.rect));
        graphics2D.fill(this.rect);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient8(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect8();
        graphics2D.setPaint(this.color31);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect9();
        graphics2D.setPaint(this.color32);
        graphics2D.fill(this.rect);
    }
    
    private void paintForegroundDisabled(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color33);
        graphics2D.fill(this.path);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient9(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.decodeGradient10(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintForegroundDisabledAndFinished(final Graphics2D graphics2D) {
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color33);
        graphics2D.fill(this.path);
        this.rect = this.decodeRect5();
        graphics2D.setPaint(this.decodeGradient9(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect6();
        graphics2D.setPaint(this.decodeGradient10(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintForegroundDisabledAndIndeterminate(final Graphics2D graphics2D) {
        this.rect = this.decodeRect7();
        graphics2D.setPaint(this.decodeGradient11(this.rect));
        graphics2D.fill(this.rect);
        this.path = this.decodePath5();
        graphics2D.setPaint(this.decodeGradient12(this.path));
        graphics2D.fill(this.path);
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(0.4f), this.decodeY(0.4f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(2.6f) - this.decodeY(0.4f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(0.6f), this.decodeY(0.6f), this.decodeX(2.4f) - this.decodeX(0.6f), this.decodeY(2.4f) - this.decodeY(0.6f));
        return this.rect;
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(0.21111111f));
        this.path.curveTo(this.decodeAnchorX(1.0f, -2.0f), this.decodeAnchorY(0.21111111f, 0.0f), this.decodeAnchorX(0.21111111f, 0.0f), this.decodeAnchorY(1.0f, -2.0f), this.decodeX(0.21111111f), this.decodeY(1.0f));
        this.path.curveTo(this.decodeAnchorX(0.21111111f, 0.0f), this.decodeAnchorY(1.0f, 2.0f), this.decodeAnchorX(0.21111111f, 0.0f), this.decodeAnchorY(2.0f, -2.0f), this.decodeX(0.21111111f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(0.21111111f, 0.0f), this.decodeAnchorY(2.0f, 2.0f), this.decodeAnchorX(1.0f, -2.0f), this.decodeAnchorY(2.8222225f, 0.0f), this.decodeX(1.0f), this.decodeY(2.8222225f));
        this.path.curveTo(this.decodeAnchorX(1.0f, 2.0f), this.decodeAnchorY(2.8222225f, 0.0f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(2.8222225f, 0.0f), this.decodeX(3.0f), this.decodeY(2.8222225f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(2.3333333f));
        this.path.lineTo(this.decodeX(0.6666667f), this.decodeY(2.3333333f));
        this.path.lineTo(this.decodeX(0.6666667f), this.decodeY(0.6666667f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(0.6666667f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(0.2f));
        this.path.curveTo(this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(0.2f, 0.0f), this.decodeAnchorX(1.0f, 2.0f), this.decodeAnchorY(0.21111111f, 0.0f), this.decodeX(1.0f), this.decodeY(0.21111111f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect3() {
        this.rect.setRect(this.decodeX(0.6666667f), this.decodeY(0.6666667f), this.decodeX(3.0f) - this.decodeX(0.6666667f), this.decodeY(2.3333333f) - this.decodeY(0.6666667f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect4() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(1.0f), this.decodeX(2.6666667f) - this.decodeX(1.0f), this.decodeY(2.0f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.9111111f), this.decodeY(0.21111111f));
        this.path.curveTo(this.decodeAnchorX(0.9111111f, -2.0f), this.decodeAnchorY(0.21111111f, 0.0f), this.decodeAnchorX(0.2f, 0.0f), this.decodeAnchorY(1.0025641f, -2.0f), this.decodeX(0.2f), this.decodeY(1.0025641f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(2.0444443f));
        this.path.curveTo(this.decodeAnchorX(0.2f, 0.0f), this.decodeAnchorY(2.0444443f, 2.0f), this.decodeAnchorX(0.9666667f, -2.0f), this.decodeAnchorY(2.8f, 0.0f), this.decodeX(0.9666667f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(2.788889f));
        this.path.curveTo(this.decodeAnchorX(2.0f, 1.9709293f), this.decodeAnchorY(2.788889f, 0.01985704f), this.decodeAnchorX(2.777778f, -0.033333335f), this.decodeAnchorY(2.0555553f, 1.9333333f), this.decodeX(2.777778f), this.decodeY(2.0555553f));
        this.path.lineTo(this.decodeX(2.788889f), this.decodeY(1.8051281f));
        this.path.lineTo(this.decodeX(2.777778f), this.decodeY(1.2794871f));
        this.path.lineTo(this.decodeX(2.777778f), this.decodeY(1.0025641f));
        this.path.curveTo(this.decodeAnchorX(2.777778f, 0.0042173304f), this.decodeAnchorY(1.0025641f, -1.9503378f), this.decodeAnchorX(2.0999997f, 1.9659461f), this.decodeAnchorY(0.22222222f, 0.017122267f), this.decodeX(2.0999997f), this.decodeY(0.22222222f));
        this.path.lineTo(this.decodeX(0.9111111f), this.decodeY(0.21111111f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect5() {
        this.rect.setRect(this.decodeX(0.6666667f), this.decodeY(0.6666667f), this.decodeX(2.3333333f) - this.decodeX(0.6666667f), this.decodeY(2.3333333f) - this.decodeY(0.6666667f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect6() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(1.0f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(2.0f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect7() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(0.0f), this.decodeX(3.0f) - this.decodeX(0.0f), this.decodeY(3.0f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(1.4285715f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 2.6785715f), this.decodeAnchorY(1.4285715f, 8.881784E-16f), this.decodeAnchorX(1.3898809f, -6.214286f), this.decodeAnchorY(0.3452381f, -0.035714287f), this.decodeX(1.3898809f), this.decodeY(0.3452381f));
        this.path.lineTo(this.decodeX(1.5535715f), this.decodeY(0.3452381f));
        this.path.curveTo(this.decodeAnchorX(1.5535715f, 8.32967f), this.decodeAnchorY(0.3452381f, 0.0027472528f), this.decodeAnchorX(2.3333333f, -5.285714f), this.decodeAnchorY(1.4285715f, 0.035714287f), this.decodeX(2.3333333f), this.decodeY(1.4285715f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.4285715f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.5714285f));
        this.path.lineTo(this.decodeX(2.3333333f), this.decodeY(1.5714285f));
        this.path.curveTo(this.decodeAnchorX(2.3333333f, -5.321429f), this.decodeAnchorY(1.5714285f, 0.035714287f), this.decodeAnchorX(1.5535715f, 8.983517f), this.decodeAnchorY(2.6666667f, 0.03846154f), this.decodeX(1.5535715f), this.decodeY(2.6666667f));
        this.path.lineTo(this.decodeX(1.4077381f), this.decodeY(2.6666667f));
        this.path.curveTo(this.decodeAnchorX(1.4077381f, -6.714286f), this.decodeAnchorY(2.6666667f, 0.0f), this.decodeAnchorX(0.0f, 2.607143f), this.decodeAnchorY(1.5714285f, 0.035714287f), this.decodeX(0.0f), this.decodeY(1.5714285f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(1.4285715f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect8() {
        this.rect.setRect(this.decodeX(1.2916666f), this.decodeY(0.0f), this.decodeX(1.3333334f) - this.decodeX(1.2916666f), this.decodeY(3.0f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect9() {
        this.rect.setRect(this.decodeX(1.7083333f), this.decodeY(0.0f), this.decodeX(1.75f) - this.decodeX(1.7083333f), this.decodeY(3.0f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.9888889f), this.decodeY(0.2f));
        this.path.curveTo(this.decodeAnchorX(0.9888889f, -2.0f), this.decodeAnchorY(0.2f, 0.0f), this.decodeAnchorX(0.2f, 0.0f), this.decodeAnchorY(0.9888889f, -2.0f), this.decodeX(0.2f), this.decodeY(0.9888889f));
        this.path.curveTo(this.decodeAnchorX(0.2f, 0.0f), this.decodeAnchorY(0.9888889f, 2.0f), this.decodeAnchorX(0.2f, 0.0f), this.decodeAnchorY(1.9974358f, -2.0f), this.decodeX(0.2f), this.decodeY(1.9974358f));
        this.path.curveTo(this.decodeAnchorX(0.2f, 0.0f), this.decodeAnchorY(1.9974358f, 2.0f), this.decodeAnchorX(0.9888889f, -2.0f), this.decodeAnchorY(2.8111107f, 0.0f), this.decodeX(0.9888889f), this.decodeY(2.8111107f));
        this.path.curveTo(this.decodeAnchorX(0.9888889f, 2.0f), this.decodeAnchorY(2.8111107f, 0.0f), this.decodeAnchorX(2.5f, 0.0f), this.decodeAnchorY(2.8f, 0.0f), this.decodeX(2.5f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.7444446f), this.decodeY(2.488889f));
        this.path.lineTo(this.decodeX(2.7555554f), this.decodeY(1.5794872f));
        this.path.lineTo(this.decodeX(2.7666664f), this.decodeY(1.4358975f));
        this.path.lineTo(this.decodeX(2.7666664f), this.decodeY(0.62222224f));
        this.path.lineTo(this.decodeX(2.5999997f), this.decodeY(0.22222222f));
        this.path.curveTo(this.decodeAnchorX(2.5999997f, 0.0f), this.decodeAnchorY(0.22222222f, 0.0f), this.decodeAnchorX(0.9888889f, 2.0f), this.decodeAnchorY(0.2f, 0.0f), this.decodeX(0.9888889f), this.decodeY(0.2f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath5() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(1.4285715f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 2.6785715f), this.decodeAnchorY(1.4285715f, 8.881784E-16f), this.decodeAnchorX(1.3898809f, -6.357143f), this.decodeAnchorY(0.3452381f, -0.035714287f), this.decodeX(1.3898809f), this.decodeY(0.3452381f));
        this.path.lineTo(this.decodeX(1.5535715f), this.decodeY(0.3452381f));
        this.path.curveTo(this.decodeAnchorX(1.5535715f, 4.0f), this.decodeAnchorY(0.3452381f, 0.0f), this.decodeAnchorX(2.3333333f, -5.285714f), this.decodeAnchorY(1.4285715f, 0.035714287f), this.decodeX(2.3333333f), this.decodeY(1.4285715f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.4285715f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.5714285f));
        this.path.lineTo(this.decodeX(2.3333333f), this.decodeY(1.5714285f));
        this.path.curveTo(this.decodeAnchorX(2.3333333f, -5.321429f), this.decodeAnchorY(1.5714285f, 0.035714287f), this.decodeAnchorX(1.5535715f, 4.0f), this.decodeAnchorY(2.6666667f, 0.0f), this.decodeX(1.5535715f), this.decodeY(2.6666667f));
        this.path.lineTo(this.decodeX(1.4077381f), this.decodeY(2.6666667f));
        this.path.curveTo(this.decodeAnchorX(1.4077381f, -6.571429f), this.decodeAnchorY(2.6666667f, -0.035714287f), this.decodeAnchorX(0.0f, 2.607143f), this.decodeAnchorY(1.5714285f, 0.035714287f), this.decodeX(0.0f), this.decodeY(1.5714285f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(1.4285715f));
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
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.038709678f, 0.05967742f, 0.08064516f, 0.23709677f, 0.3935484f, 0.41612905f, 0.43870968f, 0.67419356f, 0.90967745f, 0.91451615f, 0.91935486f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5, this.decodeColor(this.color5, this.color6, 0.5f), this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.05483871f, 0.5032258f, 0.9516129f }, new Color[] { this.color9, this.decodeColor(this.color9, this.color10, 0.5f), this.color10 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.038709678f, 0.05967742f, 0.08064516f, 0.23709677f, 0.3935484f, 0.41612905f, 0.43870968f, 0.67419356f, 0.90967745f, 0.91612905f, 0.92258066f }, new Color[] { this.color11, this.decodeColor(this.color11, this.color12, 0.5f), this.color12, this.decodeColor(this.color12, this.color13, 0.5f), this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14, this.decodeColor(this.color14, this.color15, 0.5f), this.color15, this.decodeColor(this.color15, this.color16, 0.5f), this.color16 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.038709678f, 0.05483871f, 0.07096774f, 0.28064516f, 0.4903226f, 0.6967742f, 0.9032258f, 0.9241935f, 0.9451613f }, new Color[] { this.color18, this.decodeColor(this.color18, this.color19, 0.5f), this.color19, this.decodeColor(this.color19, this.color20, 0.5f), this.color20, this.decodeColor(this.color20, this.color21, 0.5f), this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.038709678f, 0.061290324f, 0.08387097f, 0.27258065f, 0.46129033f, 0.4903226f, 0.5193548f, 0.71774197f, 0.91612905f, 0.92419356f, 0.93225807f }, new Color[] { this.color23, this.decodeColor(this.color23, this.color24, 0.5f), this.color24, this.decodeColor(this.color24, this.color25, 0.5f), this.color25, this.decodeColor(this.color25, this.color26, 0.5f), this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27, this.decodeColor(this.color27, this.color28, 0.5f), this.color28 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.051612902f, 0.06612903f, 0.08064516f, 0.2935484f, 0.5064516f, 0.6903226f, 0.87419355f, 0.88870966f, 0.9032258f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color29, 0.5f), this.color29, this.decodeColor(this.color29, this.color7, 0.5f), this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.20645161f, 0.41290322f, 0.44193548f, 0.47096774f, 0.7354839f, 1.0f }, new Color[] { this.color24, this.decodeColor(this.color24, this.color25, 0.5f), this.color25, this.decodeColor(this.color25, this.color26, 0.5f), this.color26, this.decodeColor(this.color26, this.color30, 0.5f), this.color30 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.038709678f, 0.05483871f, 0.07096774f, 0.28064516f, 0.4903226f, 0.6967742f, 0.9032258f, 0.9241935f, 0.9451613f }, new Color[] { this.color34, this.decodeColor(this.color34, this.color35, 0.5f), this.color35, this.decodeColor(this.color35, this.color36, 0.5f), this.color36, this.decodeColor(this.color36, this.color37, 0.5f), this.color37, this.decodeColor(this.color37, this.color38, 0.5f), this.color38 });
    }
    
    private Paint decodeGradient10(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.038709678f, 0.061290324f, 0.08387097f, 0.27258065f, 0.46129033f, 0.4903226f, 0.5193548f, 0.71774197f, 0.91612905f, 0.92419356f, 0.93225807f }, new Color[] { this.color39, this.decodeColor(this.color39, this.color40, 0.5f), this.color40, this.decodeColor(this.color40, this.color41, 0.5f), this.color41, this.decodeColor(this.color41, this.color42, 0.5f), this.color42, this.decodeColor(this.color42, this.color43, 0.5f), this.color43, this.decodeColor(this.color43, this.color44, 0.5f), this.color44 });
    }
    
    private Paint decodeGradient11(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.051612902f, 0.06612903f, 0.08064516f, 0.2935484f, 0.5064516f, 0.6903226f, 0.87419355f, 0.88870966f, 0.9032258f }, new Color[] { this.color45, this.decodeColor(this.color45, this.color46, 0.5f), this.color46, this.decodeColor(this.color46, this.color47, 0.5f), this.color47, this.decodeColor(this.color47, this.color48, 0.5f), this.color48, this.decodeColor(this.color48, this.color49, 0.5f), this.color49 });
    }
    
    private Paint decodeGradient12(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.20645161f, 0.41290322f, 0.44193548f, 0.47096774f, 0.7354839f, 1.0f }, new Color[] { this.color40, this.decodeColor(this.color40, this.color41, 0.5f), this.color41, this.decodeColor(this.color41, this.color42, 0.5f), this.color42, this.decodeColor(this.color42, this.color50, 0.5f), this.color50 });
    }
}
