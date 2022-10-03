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

final class InternalFramePainter extends AbstractRegionPainter
{
    static final int BACKGROUND_ENABLED = 1;
    static final int BACKGROUND_ENABLED_WINDOWFOCUSED = 2;
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
    private Object[] componentColors;
    
    public InternalFramePainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBase", 0.032459438f, -0.53637654f, 0.043137252f, 0);
        this.color2 = this.decodeColor("nimbusBlueGrey", 0.004273474f, -0.039488062f, -0.027450979f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", -0.00505054f, -0.056339122f, 0.05098039f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", -0.01111114f, -0.06357796f, 0.09019607f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.023821115f, -0.06666666f, 0);
        this.color6 = this.decodeColor("control", 0.0f, 0.0f, 0.0f, 0);
        this.color7 = this.decodeColor("nimbusBlueGrey", -0.006944418f, -0.07399663f, 0.11372548f, 0);
        this.color8 = this.decodeColor("nimbusBase", 0.02551502f, -0.47885156f, -0.34901965f, 0);
        this.color9 = new Color(255, 200, 0, 255);
        this.color10 = this.decodeColor("nimbusBase", 0.004681647f, -0.6274498f, 0.39999998f, 0);
        this.color11 = this.decodeColor("nimbusBase", 0.032459438f, -0.5934608f, 0.2862745f, 0);
        this.color12 = new Color(204, 207, 213, 255);
        this.color13 = this.decodeColor("nimbusBase", 0.032459438f, -0.55506915f, 0.18039215f, 0);
        this.color14 = this.decodeColor("nimbusBase", 0.004681647f, -0.52792984f, 0.10588235f, 0);
        this.color15 = this.decodeColor("nimbusBase", 0.03801495f, -0.4794643f, -0.04705882f, 0);
        this.color16 = this.decodeColor("nimbusBase", 0.021348298f, -0.61416256f, 0.3607843f, 0);
        this.color17 = this.decodeColor("nimbusBase", 0.032459438f, -0.5546332f, 0.17647058f, 0);
        this.color18 = new Color(235, 236, 238, 255);
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
                this.paintBackgroundEnabledAndWindowFocused(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient1(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color3);
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color5);
        graphics2D.fill(this.path);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color7);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundEnabledAndWindowFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.color8);
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath5();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.path);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient2(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath6();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.path);
        this.path = this.decodePath7();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.path);
        this.path = this.decodePath8();
        graphics2D.setPaint(this.color14);
        graphics2D.fill(this.path);
        this.path = this.decodePath9();
        graphics2D.setPaint(this.color15);
        graphics2D.fill(this.path);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.decodeGradient3(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color18);
        graphics2D.fill(this.rect);
    }
    
    private RoundRectangle2D decodeRoundRect1() {
        this.roundRect.setRoundRect(this.decodeX(0.0f), this.decodeY(0.0f), this.decodeX(3.0f) - this.decodeX(0.0f), this.decodeY(3.0f) - this.decodeY(0.0f), 4.666666507720947, 4.666666507720947);
        return this.roundRect;
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.16666667f), this.decodeY(0.12f));
        this.path.curveTo(this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(0.12f, -1.0f), this.decodeAnchorX(0.5f, -1.0f), this.decodeAnchorY(0.04f, 0.0f), this.decodeX(0.5f), this.decodeY(0.04f));
        this.path.curveTo(this.decodeAnchorX(0.5f, 1.0f), this.decodeAnchorY(0.04f, 0.0f), this.decodeAnchorX(2.5f, -1.0f), this.decodeAnchorY(0.04f, 0.0f), this.decodeX(2.5f), this.decodeY(0.04f));
        this.path.curveTo(this.decodeAnchorX(2.5f, 1.0f), this.decodeAnchorY(0.04f, 0.0f), this.decodeAnchorX(2.8333333f, 0.0f), this.decodeAnchorY(0.12f, -1.0f), this.decodeX(2.8333333f), this.decodeY(0.12f));
        this.path.curveTo(this.decodeAnchorX(2.8333333f, 0.0f), this.decodeAnchorY(0.12f, 1.0f), this.decodeAnchorX(2.8333333f, 0.0f), this.decodeAnchorY(0.96f, 0.0f), this.decodeX(2.8333333f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(0.16666667f), this.decodeY(0.96f));
        this.path.curveTo(this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(0.96f, 0.0f), this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(0.12f, 1.0f), this.decodeX(0.16666667f), this.decodeY(0.12f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.6666667f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(0.16666667f), this.decodeY(0.96f));
        this.path.curveTo(this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(0.96f, 0.0f), this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(2.5f, -1.0f), this.decodeX(0.16666667f), this.decodeY(2.5f));
        this.path.curveTo(this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(2.5f, 1.0f), this.decodeAnchorX(0.5f, -1.0f), this.decodeAnchorY(2.8333333f, 0.0f), this.decodeX(0.5f), this.decodeY(2.8333333f));
        this.path.curveTo(this.decodeAnchorX(0.5f, 1.0f), this.decodeAnchorY(2.8333333f, 0.0f), this.decodeAnchorX(2.5f, -1.0f), this.decodeAnchorY(2.8333333f, 0.0f), this.decodeX(2.5f), this.decodeY(2.8333333f));
        this.path.curveTo(this.decodeAnchorX(2.5f, 1.0f), this.decodeAnchorY(2.8333333f, 0.0f), this.decodeAnchorX(2.8333333f, 0.0f), this.decodeAnchorY(2.5f, 1.0f), this.decodeX(2.8333333f), this.decodeY(2.5f));
        this.path.curveTo(this.decodeAnchorX(2.8333333f, 0.0f), this.decodeAnchorY(2.5f, -1.0f), this.decodeAnchorX(2.8333333f, 0.0f), this.decodeAnchorY(0.96f, 0.0f), this.decodeX(2.8333333f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(2.3333333f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(2.3333333f), this.decodeY(2.3333333f));
        this.path.lineTo(this.decodeX(0.6666667f), this.decodeY(2.3333333f));
        this.path.lineTo(this.decodeX(0.6666667f), this.decodeY(0.96f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.8333333f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(0.6666667f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(0.6666667f), this.decodeY(2.3333333f));
        this.path.lineTo(this.decodeX(2.3333333f), this.decodeY(2.3333333f));
        this.path.lineTo(this.decodeX(2.3333333f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(2.1666667f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(2.1666667f), this.decodeY(2.1666667f));
        this.path.lineTo(this.decodeX(0.8333333f), this.decodeY(2.1666667f));
        this.path.lineTo(this.decodeX(0.8333333f), this.decodeY(0.96f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(2.1666667f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.1666667f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.1666667f), this.decodeY(2.1666667f));
        this.path.lineTo(this.decodeX(0.8333333f), this.decodeY(2.1666667f));
        this.path.lineTo(this.decodeX(0.8333333f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(2.1666667f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(2.1666667f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(1.0f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(2.0f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(0.33333334f), this.decodeY(2.6666667f), this.decodeX(2.6666667f) - this.decodeX(0.33333334f), this.decodeY(2.8333333f) - this.decodeY(2.6666667f));
        return this.rect;
    }
    
    private RoundRectangle2D decodeRoundRect2() {
        this.roundRect.setRoundRect(this.decodeX(0.0f), this.decodeY(0.0f), this.decodeX(3.0f) - this.decodeX(0.0f), this.decodeY(3.0f) - this.decodeY(0.0f), 4.833333492279053, 4.833333492279053);
        return this.roundRect;
    }
    
    private Path2D decodePath5() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.16666667f), this.decodeY(0.08f));
        this.path.curveTo(this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(0.08f, 1.0f), this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(0.08f, -1.0f), this.decodeX(0.16666667f), this.decodeY(0.08f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath6() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.5f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(0.16666667f), this.decodeY(0.96f));
        this.path.curveTo(this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(0.96f, 0.0f), this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(2.5f, -1.0f), this.decodeX(0.16666667f), this.decodeY(2.5f));
        this.path.curveTo(this.decodeAnchorX(0.16666667f, 0.0f), this.decodeAnchorY(2.5f, 1.0f), this.decodeAnchorX(0.5f, -1.0f), this.decodeAnchorY(2.8333333f, 0.0f), this.decodeX(0.5f), this.decodeY(2.8333333f));
        this.path.curveTo(this.decodeAnchorX(0.5f, 1.0f), this.decodeAnchorY(2.8333333f, 0.0f), this.decodeAnchorX(2.5f, -1.0f), this.decodeAnchorY(2.8333333f, 0.0f), this.decodeX(2.5f), this.decodeY(2.8333333f));
        this.path.curveTo(this.decodeAnchorX(2.5f, 1.0f), this.decodeAnchorY(2.8333333f, 0.0f), this.decodeAnchorX(2.8333333f, 0.0f), this.decodeAnchorY(2.5f, 1.0f), this.decodeX(2.8333333f), this.decodeY(2.5f));
        this.path.curveTo(this.decodeAnchorX(2.8333333f, 0.0f), this.decodeAnchorY(2.5f, -1.0f), this.decodeAnchorX(2.8333333f, 0.0f), this.decodeAnchorY(0.96f, 0.0f), this.decodeX(2.8333333f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(2.5f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(2.5f), this.decodeY(2.5f));
        this.path.lineTo(this.decodeX(0.5f), this.decodeY(2.5f));
        this.path.lineTo(this.decodeX(0.5f), this.decodeY(0.96f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath7() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.6666667f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(0.33333334f), this.decodeY(0.96f));
        this.path.curveTo(this.decodeAnchorX(0.33333334f, 0.0f), this.decodeAnchorY(0.96f, 0.0f), this.decodeAnchorX(0.33333334f, 0.0f), this.decodeAnchorY(2.3333333f, -1.0f), this.decodeX(0.33333334f), this.decodeY(2.3333333f));
        this.path.curveTo(this.decodeAnchorX(0.33333334f, 0.0f), this.decodeAnchorY(2.3333333f, 1.0f), this.decodeAnchorX(0.6666667f, -1.0f), this.decodeAnchorY(2.6666667f, 0.0f), this.decodeX(0.6666667f), this.decodeY(2.6666667f));
        this.path.curveTo(this.decodeAnchorX(0.6666667f, 1.0f), this.decodeAnchorY(2.6666667f, 0.0f), this.decodeAnchorX(2.3333333f, -1.0f), this.decodeAnchorY(2.6666667f, 0.0f), this.decodeX(2.3333333f), this.decodeY(2.6666667f));
        this.path.curveTo(this.decodeAnchorX(2.3333333f, 1.0f), this.decodeAnchorY(2.6666667f, 0.0f), this.decodeAnchorX(2.6666667f, 0.0f), this.decodeAnchorY(2.3333333f, 1.0f), this.decodeX(2.6666667f), this.decodeY(2.3333333f));
        this.path.curveTo(this.decodeAnchorX(2.6666667f, 0.0f), this.decodeAnchorY(2.3333333f, -1.0f), this.decodeAnchorX(2.6666667f, 0.0f), this.decodeAnchorY(0.96f, 0.0f), this.decodeX(2.6666667f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(2.3333333f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(2.3333333f), this.decodeY(2.3333333f));
        this.path.lineTo(this.decodeX(0.6666667f), this.decodeY(2.3333333f));
        this.path.lineTo(this.decodeX(0.6666667f), this.decodeY(0.96f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath8() {
        this.path.reset();
        this.path.moveTo(this.decodeX(2.3333333f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(2.1666667f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(2.1666667f), this.decodeY(2.1666667f));
        this.path.lineTo(this.decodeX(0.8333333f), this.decodeY(2.1666667f));
        this.path.lineTo(this.decodeX(0.8333333f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(0.6666667f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(0.6666667f), this.decodeY(2.3333333f));
        this.path.lineTo(this.decodeX(2.3333333f), this.decodeY(2.3333333f));
        this.path.lineTo(this.decodeX(2.3333333f), this.decodeY(0.96f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath9() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.8333333f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(0.8333333f), this.decodeY(2.1666667f));
        this.path.lineTo(this.decodeX(2.1666667f), this.decodeY(2.1666667f));
        this.path.lineTo(this.decodeX(2.1666667f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(0.8333333f), this.decodeY(0.96f));
        this.path.lineTo(this.decodeX(0.8333333f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(0.8333333f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect3() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(0.0f), this.decodeX(0.0f) - this.decodeX(0.0f), this.decodeY(0.0f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect4() {
        this.rect.setRect(this.decodeX(0.33333334f), this.decodeY(0.08f), this.decodeX(2.6666667f) - this.decodeX(0.33333334f), this.decodeY(0.96f) - this.decodeY(0.08f));
        return this.rect;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.3203593f, 1.0f }, new Color[] { this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.24251497f, 1.0f }, new Color[] { this.color16, this.decodeColor(this.color16, this.color17, 0.5f), this.color17 });
    }
}
