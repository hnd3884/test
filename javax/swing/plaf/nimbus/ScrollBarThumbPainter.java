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

final class ScrollBarThumbPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_FOCUSED = 3;
    static final int BACKGROUND_MOUSEOVER = 4;
    static final int BACKGROUND_PRESSED = 5;
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
    private Object[] componentColors;
    
    public ScrollBarThumbPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBase", 5.1498413E-4f, 0.18061227f, -0.35686278f, 0);
        this.color2 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.21018237f, -0.18039218f, 0);
        this.color3 = this.decodeColor("nimbusBase", 7.13408E-4f, -0.53277314f, 0.25098038f, 0);
        this.color4 = this.decodeColor("nimbusBase", -0.07865167f, -0.6317617f, 0.44313723f, 0);
        this.color5 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.44340658f, 0.26666665f, 0);
        this.color6 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.4669379f, 0.38039213f, 0);
        this.color7 = this.decodeColor("nimbusBase", -0.07865167f, -0.56512606f, 0.45098037f, 0);
        this.color8 = this.decodeColor("nimbusBase", -0.0017285943f, -0.362987f, 0.011764705f, 0);
        this.color9 = this.decodeColor("nimbusBase", 5.2034855E-5f, -0.41753247f, 0.09803921f, -222);
        this.color10 = new Color(255, 200, 0, 255);
        this.color11 = this.decodeColor("nimbusBase", -0.0017285943f, -0.362987f, 0.011764705f, -255);
        this.color12 = this.decodeColor("nimbusBase", 0.010237217f, -0.5621849f, 0.25098038f, 0);
        this.color13 = this.decodeColor("nimbusBase", 0.08801502f, -0.6317773f, 0.4470588f, 0);
        this.color14 = this.decodeColor("nimbusBase", 5.1498413E-4f, -0.45950285f, 0.34117645f, 0);
        this.color15 = this.decodeColor("nimbusBase", -0.0017285943f, -0.48277313f, 0.45098037f, 0);
        this.color16 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, 0);
        this.color17 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.54901963f, 0);
        this.color18 = this.decodeColor("nimbusBase", 0.0013483167f, 0.29021162f, -0.33725494f, 0);
        this.color19 = this.decodeColor("nimbusBase", 0.002908647f, -0.29012606f, -0.015686274f, 0);
        this.color20 = this.decodeColor("nimbusBase", -8.738637E-4f, -0.40612245f, 0.21960783f, 0);
        this.color21 = this.decodeColor("nimbusBase", 0.0f, -0.01765871f, 0.015686274f, 0);
        this.color22 = this.decodeColor("nimbusBase", 0.0f, -0.12714285f, 0.1372549f, 0);
        this.color23 = this.decodeColor("nimbusBase", 0.0018727183f, -0.23116884f, 0.31372547f, 0);
        this.color24 = this.decodeColor("nimbusBase", -8.738637E-4f, -0.3579365f, -0.33725494f, 0);
        this.color25 = this.decodeColor("nimbusBase", 0.004681647f, -0.3857143f, -0.36078435f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 2: {
                this.paintBackgroundEnabled(graphics2D);
                break;
            }
            case 4: {
                this.paintBackgroundMouseOver(graphics2D);
                break;
            }
            case 5: {
                this.paintBackgroundPressed(graphics2D);
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
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.path);
        this.path = this.decodePath5();
        graphics2D.setPaint(this.decodeGradient4(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundMouseOver(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient1(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient5(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.path);
        this.path = this.decodePath5();
        graphics2D.setPaint(this.decodeGradient4(this.path));
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundPressed(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient6(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient8(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.path);
        this.path = this.decodePath6();
        graphics2D.setPaint(this.decodeGradient9(this.path));
        graphics2D.fill(this.path);
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(1.0666667f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(1.0666667f, 6.0f), this.decodeAnchorX(1.0f, -10.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(2.0f, 10.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(1.0666667f, 6.0f), this.decodeX(3.0f), this.decodeY(1.0666667f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.06666667f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(0.06666667f), this.decodeY(1.0666667f));
        this.path.curveTo(this.decodeAnchorX(0.06666667f, -0.045454547f), this.decodeAnchorY(1.0666667f, 8.454545f), this.decodeAnchorX(1.0f, -5.8636365f), this.decodeAnchorY(1.9333334f, 0.0f), this.decodeX(1.0f), this.decodeY(1.9333334f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.9333334f));
        this.path.curveTo(this.decodeAnchorX(2.0f, 5.909091f), this.decodeAnchorY(1.9333334f, -3.5527137E-15f), this.decodeAnchorX(2.9333334f, -0.045454547f), this.decodeAnchorY(1.0666667f, 8.363636f), this.decodeX(2.9333334f), this.decodeY(1.0666667f));
        this.path.lineTo(this.decodeX(2.9333334f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(0.06666667f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.4f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(0.06666667f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(0.16060607f), this.decodeY(1.5090909f));
        this.path.curveTo(this.decodeAnchorX(0.16060607f, 0.0f), this.decodeAnchorY(1.5090909f, 0.0f), this.decodeAnchorX(0.2f, -0.95454544f), this.decodeAnchorY(1.1363636f, 1.5454545f), this.decodeX(0.2f), this.decodeY(1.1363636f));
        this.path.curveTo(this.decodeAnchorX(0.2f, 0.95454544f), this.decodeAnchorY(1.1363636f, -1.5454545f), this.decodeAnchorX(0.4f, 0.0f), this.decodeAnchorY(1.0f, 0.0f), this.decodeX(0.4f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(2.4242425f), this.decodeY(1.5121212f));
        this.path.lineTo(this.decodeX(2.4242425f), this.decodeY(1.5121212f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath5() {
        this.path.reset();
        this.path.moveTo(this.decodeX(2.9363637f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.6030304f), this.decodeY(1.0f));
        this.path.curveTo(this.decodeAnchorX(2.6030304f, 0.0f), this.decodeAnchorY(1.0f, 0.0f), this.decodeAnchorX(2.778788f, -0.6818182f), this.decodeAnchorY(1.1333333f, -1.2272727f), this.decodeX(2.778788f), this.decodeY(1.1333333f));
        this.path.curveTo(this.decodeAnchorX(2.778788f, 0.6818182f), this.decodeAnchorY(1.1333333f, 1.2272727f), this.decodeAnchorX(2.8393939f, 0.0f), this.decodeAnchorY(1.5060606f, 0.0f), this.decodeX(2.8393939f), this.decodeY(1.5060606f));
        this.path.lineTo(this.decodeX(2.9363637f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath6() {
        this.path.reset();
        this.path.moveTo(this.decodeX(2.9363637f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.5563636f), this.decodeY(1.0f));
        this.path.curveTo(this.decodeAnchorX(2.5563636f, 0.0f), this.decodeAnchorY(1.0f, 0.0f), this.decodeAnchorX(2.7587879f, -0.6818182f), this.decodeAnchorY(1.14f, -1.2272727f), this.decodeX(2.7587879f), this.decodeY(1.14f));
        this.path.curveTo(this.decodeAnchorX(2.7587879f, 0.6818182f), this.decodeAnchorY(1.14f, 1.2272727f), this.decodeAnchorX(2.8393939f, 0.0f), this.decodeAnchorY(1.5060606f, 0.0f), this.decodeX(2.8393939f), this.decodeY(1.5060606f));
        this.path.lineTo(this.decodeX(2.9363637f), this.decodeY(1.0f));
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
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.038922157f, 0.0508982f, 0.06287425f, 0.19610777f, 0.32934132f, 0.48952097f, 0.6497006f, 0.8248503f, 1.0f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5, this.decodeColor(this.color5, this.color6, 0.5f), this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.06818182f * n3 + n, -0.005952381f * n4 + n2, 0.3689091f * n3 + n, 0.23929171f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color8, this.decodeColor(this.color8, this.color9, 0.5f), this.color9 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.9409091f * n3 + n, 0.035928145f * n4 + n2, 0.5954546f * n3 + n, 0.26347303f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color8, this.decodeColor(this.color8, this.color11, 0.5f), this.color11 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.038922157f, 0.0508982f, 0.06287425f, 0.19610777f, 0.32934132f, 0.48952097f, 0.6497006f, 0.8248503f, 1.0f }, new Color[] { this.color12, this.decodeColor(this.color12, this.color13, 0.5f), this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14, this.decodeColor(this.color14, this.color15, 0.5f), this.color15, this.decodeColor(this.color15, this.color16, 0.5f), this.color16 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color17, this.decodeColor(this.color17, this.color18, 0.5f), this.color18 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.038922157f, 0.0508982f, 0.06287425f, 0.19610777f, 0.32934132f, 0.48952097f, 0.6497006f, 0.8248503f, 1.0f }, new Color[] { this.color19, this.decodeColor(this.color19, this.color20, 0.5f), this.color20, this.decodeColor(this.color20, this.color21, 0.5f), this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22, this.decodeColor(this.color22, this.color23, 0.5f), this.color23 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.06818182f * n3 + n, -0.005952381f * n4 + n2, 0.3689091f * n3 + n, 0.23929171f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color24, this.decodeColor(this.color24, this.color9, 0.5f), this.color9 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.9409091f * n3 + n, 0.035928145f * n4 + n2, 0.37615633f * n3 + n, 0.34910178f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color25, this.decodeColor(this.color25, this.color11, 0.5f), this.color11 });
    }
}
