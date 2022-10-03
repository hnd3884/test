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

final class SliderThumbPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_FOCUSED = 3;
    static final int BACKGROUND_FOCUSED_MOUSEOVER = 4;
    static final int BACKGROUND_FOCUSED_PRESSED = 5;
    static final int BACKGROUND_MOUSEOVER = 6;
    static final int BACKGROUND_PRESSED = 7;
    static final int BACKGROUND_ENABLED_ARROWSHAPE = 8;
    static final int BACKGROUND_DISABLED_ARROWSHAPE = 9;
    static final int BACKGROUND_MOUSEOVER_ARROWSHAPE = 10;
    static final int BACKGROUND_PRESSED_ARROWSHAPE = 11;
    static final int BACKGROUND_FOCUSED_ARROWSHAPE = 12;
    static final int BACKGROUND_FOCUSED_MOUSEOVER_ARROWSHAPE = 13;
    static final int BACKGROUND_FOCUSED_PRESSED_ARROWSHAPE = 14;
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
    private Object[] componentColors;
    
    public SliderThumbPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBase", 0.021348298f, -0.5625436f, 0.25490195f, 0);
        this.color2 = this.decodeColor("nimbusBase", 0.015098333f, -0.55105823f, 0.19215685f, 0);
        this.color3 = this.decodeColor("nimbusBase", 0.021348298f, -0.5924243f, 0.35686272f, 0);
        this.color4 = this.decodeColor("nimbusBase", 0.021348298f, -0.56722116f, 0.3098039f, 0);
        this.color5 = this.decodeColor("nimbusBase", 0.021348298f, -0.56844974f, 0.32549018f, 0);
        this.color6 = this.decodeColor("nimbusBlueGrey", -0.003968239f, 0.0014736876f, -0.25490198f, -156);
        this.color7 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.34585923f, -0.007843137f, 0);
        this.color8 = this.decodeColor("nimbusBase", -0.0017285943f, -0.11571431f, -0.25490198f, 0);
        this.color9 = this.decodeColor("nimbusBase", -0.023096085f, -0.6238095f, 0.43921566f, 0);
        this.color10 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.43866998f, 0.24705881f, 0);
        this.color11 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.45714286f, 0.32941175f, 0);
        this.color12 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.color13 = this.decodeColor("nimbusBase", -0.0038217902f, -0.15532213f, -0.14901963f, 0);
        this.color14 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.54509807f, 0);
        this.color15 = this.decodeColor("nimbusBase", 0.004681647f, -0.62780917f, 0.44313723f, 0);
        this.color16 = this.decodeColor("nimbusBase", 2.9569864E-4f, -0.4653107f, 0.32549018f, 0);
        this.color17 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4563421f, 0.32549018f, 0);
        this.color18 = this.decodeColor("nimbusBase", -0.0017285943f, -0.4732143f, 0.39215684f, 0);
        this.color19 = this.decodeColor("nimbusBase", 0.0015952587f, -0.04875779f, -0.18823531f, 0);
        this.color20 = this.decodeColor("nimbusBase", 2.9569864E-4f, -0.44943976f, 0.25098038f, 0);
        this.color21 = this.decodeColor("nimbusBase", 0.0f, 0.0f, 0.0f, 0);
        this.color22 = this.decodeColor("nimbusBase", 8.9377165E-4f, -0.121094406f, 0.12156862f, 0);
        this.color23 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -121);
        this.color24 = new Color(150, 156, 168, 146);
        this.color25 = this.decodeColor("nimbusBase", -0.0033828616f, -0.40608466f, -0.019607842f, 0);
        this.color26 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.17594418f, -0.20784315f, 0);
        this.color27 = this.decodeColor("nimbusBase", 0.0023007393f, -0.11332625f, -0.28627452f, 0);
        this.color28 = this.decodeColor("nimbusBase", -0.023096085f, -0.62376213f, 0.4352941f, 0);
        this.color29 = this.decodeColor("nimbusBase", 0.004681647f, -0.594392f, 0.39999998f, 0);
        this.color30 = this.decodeColor("nimbusBase", -0.0017285943f, -0.4454704f, 0.25490195f, 0);
        this.color31 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4625541f, 0.35686272f, 0);
        this.color32 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.47442397f, 0.4235294f, 0);
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
                this.paintBackgroundFocusedAndMouseOver(graphics2D);
                break;
            }
            case 5: {
                this.paintBackgroundFocusedAndPressed(graphics2D);
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
                this.paintBackgroundEnabledAndArrowShape(graphics2D);
                break;
            }
            case 9: {
                this.paintBackgroundDisabledAndArrowShape(graphics2D);
                break;
            }
            case 10: {
                this.paintBackgroundMouseOverAndArrowShape(graphics2D);
                break;
            }
            case 11: {
                this.paintBackgroundPressedAndArrowShape(graphics2D);
                break;
            }
            case 12: {
                this.paintBackgroundFocusedAndArrowShape(graphics2D);
                break;
            }
            case 13: {
                this.paintBackgroundFocusedAndMouseOverAndArrowShape(graphics2D);
                break;
            }
            case 14: {
                this.paintBackgroundFocusedAndPressedAndArrowShape(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundDisabled(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient1(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient2(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse3();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient3(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient4(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void paintBackgroundFocused(final Graphics2D graphics2D) {
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
    
    private void paintBackgroundFocusedAndMouseOver(final Graphics2D graphics2D) {
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
    
    private void paintBackgroundFocusedAndPressed(final Graphics2D graphics2D) {
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
    
    private void paintBackgroundMouseOver(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse3();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient5(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient6(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void paintBackgroundPressed(final Graphics2D graphics2D) {
        this.ellipse = this.decodeEllipse3();
        graphics2D.setPaint(this.color23);
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse1();
        graphics2D.setPaint(this.decodeGradient7(this.ellipse));
        graphics2D.fill(this.ellipse);
        this.ellipse = this.decodeEllipse2();
        graphics2D.setPaint(this.decodeGradient8(this.ellipse));
        graphics2D.fill(this.ellipse);
    }
    
    private void paintBackgroundEnabledAndArrowShape(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color24);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient9(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient10(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundDisabledAndArrowShape(final Graphics2D graphics2D) {
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient11(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient12(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundMouseOverAndArrowShape(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color24);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient13(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient14(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundPressedAndArrowShape(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color24);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient15(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient16(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundFocusedAndArrowShape(final Graphics2D graphics2D) {
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient9(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient17(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundFocusedAndMouseOverAndArrowShape(final Graphics2D graphics2D) {
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient13(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient14(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundFocusedAndPressedAndArrowShape(final Graphics2D graphics2D) {
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient15(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient16(this.path));
        graphics2D.fill(this.path);
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
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.8166667f), this.decodeY(0.5007576f));
        this.path.curveTo(this.decodeAnchorX(0.8166667f, 1.5643269f), this.decodeAnchorY(0.5007576f, -0.3097513f), this.decodeAnchorX(2.7925456f, 0.058173586f), this.decodeAnchorY(1.6116884f, -0.4647635f), this.decodeX(2.7925456f), this.decodeY(1.6116884f));
        this.path.curveTo(this.decodeAnchorX(2.7925456f, -0.34086856f), this.decodeAnchorY(1.6116884f, 2.7232852f), this.decodeAnchorX(0.7006364f, 4.568128f), this.decodeAnchorY(2.7693636f, -0.006014915f), this.decodeX(0.7006364f), this.decodeY(2.7693636f));
        this.path.curveTo(this.decodeAnchorX(0.7006364f, -3.5233955f), this.decodeAnchorY(2.7693636f, 0.004639302f), this.decodeAnchorX(0.8166667f, -1.8635255f), this.decodeAnchorY(0.5007576f, 0.36899543f), this.decodeX(0.8166667f), this.decodeY(0.5007576f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.6155303f), this.decodeY(2.5954547f));
        this.path.curveTo(this.decodeAnchorX(0.6155303f, 0.90980893f), this.decodeAnchorY(2.5954547f, 1.3154242f), this.decodeAnchorX(2.6151516f, 0.014588808f), this.decodeAnchorY(1.6112013f, 0.9295521f), this.decodeX(2.6151516f), this.decodeY(1.6112013f));
        this.path.curveTo(this.decodeAnchorX(2.6151516f, -0.01365518f), this.decodeAnchorY(1.6112013f, -0.8700643f), this.decodeAnchorX(0.60923916f, 0.9729935f), this.decodeAnchorY(0.40716404f, -1.4248644f), this.decodeX(0.60923916f), this.decodeY(0.40716404f));
        this.path.curveTo(this.decodeAnchorX(0.60923916f, -0.7485209f), this.decodeAnchorY(0.40716404f, 1.0961438f), this.decodeAnchorX(0.6155303f, -0.74998796f), this.decodeAnchorY(2.5954547f, -1.0843511f), this.decodeX(0.6155303f), this.decodeY(2.5954547f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.8055606f), this.decodeY(0.6009697f));
        this.path.curveTo(this.decodeAnchorX(0.8055606f, 0.50820893f), this.decodeAnchorY(0.6009697f, -0.8490881f), this.decodeAnchorX(2.3692727f, 0.0031846066f), this.decodeAnchorY(1.613117f, -0.60668826f), this.decodeX(2.3692727f), this.decodeY(1.613117f));
        this.path.curveTo(this.decodeAnchorX(2.3692727f, -0.003890196f), this.decodeAnchorY(1.613117f, 0.74110764f), this.decodeAnchorX(0.7945455f, 0.3870974f), this.decodeAnchorY(2.3932729f, 1.240782f), this.decodeX(0.7945455f), this.decodeY(2.3932729f));
        this.path.curveTo(this.decodeAnchorX(0.7945455f, -0.38636583f), this.decodeAnchorY(2.3932729f, -1.2384372f), this.decodeAnchorX(0.8055606f, -0.995154f), this.decodeAnchorY(0.6009697f, 1.6626496f), this.decodeX(0.8055606f), this.decodeY(0.6009697f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.60059524f), this.decodeY(0.11727543f));
        this.path.curveTo(this.decodeAnchorX(0.60059524f, 1.5643269f), this.decodeAnchorY(0.11727543f, -0.3097513f), this.decodeAnchorX(2.7925456f, 0.004405844f), this.decodeAnchorY(1.6116884f, -1.1881162f), this.decodeX(2.7925456f), this.decodeY(1.6116884f));
        this.path.curveTo(this.decodeAnchorX(2.7925456f, -0.007364541f), this.decodeAnchorY(1.6116884f, 1.9859827f), this.decodeAnchorX(0.7006364f, 2.7716863f), this.decodeAnchorY(2.8693638f, -0.008974582f), this.decodeX(0.7006364f), this.decodeY(2.8693638f));
        this.path.curveTo(this.decodeAnchorX(0.7006364f, -3.754899f), this.decodeAnchorY(2.8693638f, 0.012158176f), this.decodeAnchorX(0.60059524f, -1.8635255f), this.decodeAnchorY(0.11727543f, 0.36899543f), this.decodeX(0.60059524f), this.decodeY(0.11727543f));
        this.path.closePath();
        return this.path;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5106101f * n3 + n, -4.553649E-18f * n4 + n2, 0.49933687f * n3 + n, 1.0039787f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color1, this.decodeColor(this.color1, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5023511f * n3 + n, 0.0015673981f * n4 + n2, 0.5023511f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.21256684f, 0.42513368f, 0.71256685f, 1.0f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.51f * n3 + n, -4.553649E-18f * n4 + n2, 0.51f * n3 + n, 1.0039787f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0015673981f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.21256684f, 0.42513368f, 0.56149733f, 0.69786096f, 0.8489305f, 1.0f }, new Color[] { this.color9, this.decodeColor(this.color9, this.color10, 0.5f), this.color10, this.decodeColor(this.color10, this.color10, 0.5f), this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5106101f * n3 + n, -4.553649E-18f * n4 + n2, 0.49933687f * n3 + n, 1.0039787f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5023511f * n3 + n, 0.0015673981f * n4 + n2, 0.5023511f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.21256684f, 0.42513368f, 0.56149733f, 0.69786096f, 0.8489305f, 1.0f }, new Color[] { this.color15, this.decodeColor(this.color15, this.color16, 0.5f), this.color16, this.decodeColor(this.color16, this.color17, 0.5f), this.color17, this.decodeColor(this.color17, this.color18, 0.5f), this.color18 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5106101f * n3 + n, -4.553649E-18f * n4 + n2, 0.49933687f * n3 + n, 1.0039787f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color14, this.decodeColor(this.color14, this.color19, 0.5f), this.color19 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5023511f * n3 + n, 0.0015673981f * n4 + n2, 0.5023511f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.23796791f, 0.47593582f, 0.5360962f, 0.5962567f, 0.79812837f, 1.0f }, new Color[] { this.color20, this.decodeColor(this.color20, this.color21, 0.5f), this.color21, this.decodeColor(this.color21, this.color21, 0.5f), this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.24032257f, 0.48064515f, 0.7403226f, 1.0f }, new Color[] { this.color25, this.decodeColor(this.color25, this.color26, 0.5f), this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27 });
    }
    
    private Paint decodeGradient10(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.061290324f, 0.1016129f, 0.14193548f, 0.3016129f, 0.46129033f, 0.5983871f, 0.7354839f, 0.7935484f, 0.8516129f }, new Color[] { this.color28, this.decodeColor(this.color28, this.color29, 0.5f), this.color29, this.decodeColor(this.color29, this.color30, 0.5f), this.color30, this.decodeColor(this.color30, this.color31, 0.5f), this.color31, this.decodeColor(this.color31, this.color32, 0.5f), this.color32 });
    }
    
    private Paint decodeGradient11(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color1, this.decodeColor(this.color1, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient12(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.21256684f, 0.42513368f, 0.71256685f, 1.0f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5 });
    }
    
    private Paint decodeGradient13(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14 });
    }
    
    private Paint decodeGradient14(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.21256684f, 0.42513368f, 0.56149733f, 0.69786096f, 0.8489305f, 1.0f }, new Color[] { this.color15, this.decodeColor(this.color15, this.color16, 0.5f), this.color16, this.decodeColor(this.color16, this.color17, 0.5f), this.color17, this.decodeColor(this.color17, this.color18, 0.5f), this.color18 });
    }
    
    private Paint decodeGradient15(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color14, this.decodeColor(this.color14, this.color19, 0.5f), this.color19 });
    }
    
    private Paint decodeGradient16(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.23796791f, 0.47593582f, 0.5360962f, 0.5962567f, 0.79812837f, 1.0f }, new Color[] { this.color20, this.decodeColor(this.color20, this.color21, 0.5f), this.color21, this.decodeColor(this.color21, this.color21, 0.5f), this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22 });
    }
    
    private Paint decodeGradient17(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.4925773f * n3 + n, 0.082019866f * n4 + n2, 0.4925773f * n3 + n, 0.91798013f * n4 + n2, new float[] { 0.061290324f, 0.1016129f, 0.14193548f, 0.3016129f, 0.46129033f, 0.5983871f, 0.7354839f, 0.7935484f, 0.8516129f }, new Color[] { this.color28, this.decodeColor(this.color28, this.color29, 0.5f), this.color29, this.decodeColor(this.color29, this.color30, 0.5f), this.color30, this.decodeColor(this.color30, this.color31, 0.5f), this.color31, this.decodeColor(this.color31, this.color32, 0.5f), this.color32 });
    }
}
