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

final class DesktopPanePainter extends AbstractRegionPainter
{
    static final int BACKGROUND_ENABLED = 1;
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
    private Object[] componentColors;
    
    public DesktopPanePainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBase", -0.004577577f, -0.12867206f, 0.007843137f, 0);
        this.color2 = this.decodeColor("nimbusBase", -0.0063245893f, -0.08363098f, -0.17254904f, 0);
        this.color3 = this.decodeColor("nimbusBase", -3.6883354E-4f, -0.056766927f, -0.10196081f, 0);
        this.color4 = this.decodeColor("nimbusBase", -0.008954704f, -0.12645501f, -0.12549022f, 0);
        this.color5 = new Color(255, 200, 0, 6);
        this.color6 = this.decodeColor("nimbusBase", -8.028746E-5f, -0.084533215f, -0.05098042f, 0);
        this.color7 = this.decodeColor("nimbusBase", -0.0052053332f, -0.12267083f, -0.09803924f, 0);
        this.color8 = this.decodeColor("nimbusBase", -0.012559712f, -0.13136649f, -0.09803924f, 0);
        this.color9 = this.decodeColor("nimbusBase", -0.009207249f, -0.13984653f, -0.07450983f, 0);
        this.color10 = this.decodeColor("nimbusBase", -0.010750473f, -0.13571429f, -0.12549022f, 0);
        this.color11 = this.decodeColor("nimbusBase", -0.008476257f, -0.1267857f, -0.109803945f, 0);
        this.color12 = this.decodeColor("nimbusBase", -0.0034883022f, -0.042691052f, -0.21176472f, 0);
        this.color13 = this.decodeColor("nimbusBase", -0.012613952f, -0.11610645f, -0.14901963f, 0);
        this.color14 = this.decodeColor("nimbusBase", -0.0038217902f, -0.05238098f, -0.21960786f, 0);
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
        graphics2D.setPaint(this.color5);
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath5();
        graphics2D.setPaint(this.decodeGradient4(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath6();
        graphics2D.setPaint(this.decodeGradient5(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath7();
        graphics2D.setPaint(this.decodeGradient6(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath8();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath9();
        graphics2D.setPaint(this.decodeGradient8(this.path));
        graphics2D.fill(this.path);
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.2716666f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(1.2716666f, 0.0f), this.decodeAnchorY(2.0f, 0.5f), this.decodeAnchorX(1.1283333f, 0.0f), this.decodeAnchorY(1.0f, 0.0f), this.decodeX(1.1283333f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.3516667f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.5866666f), this.decodeY(1.5754311f));
        this.path.lineTo(this.decodeX(1.5416667f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(1.5416667f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeAnchorX(1.2716666f, 0.0f), this.decodeAnchorY(2.0f, -0.5f), this.decodeX(1.2716666f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.7883334f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(1.7883334f, 0.0f), this.decodeAnchorY(2.0f, 0.5f), this.decodeAnchorX(1.6533333f, 0.0f), this.decodeAnchorY(1.7737069f, 0.0f), this.decodeX(1.6533333f), this.decodeY(1.7737069f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.1465517f));
        this.path.curveTo(this.decodeAnchorX(2.0f, 0.0f), this.decodeAnchorY(1.1465517f, 0.0f), this.decodeAnchorX(2.0f, 0.0f), this.decodeAnchorY(2.0f, -0.5f), this.decodeX(2.0f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(2.0f, 0.5f), this.decodeAnchorY(2.0f, 0.5f), this.decodeAnchorX(1.7883334f, 0.0f), this.decodeAnchorY(2.0f, -0.5f), this.decodeX(1.7883334f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.5666666f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.5666666f), this.decodeY(1.5689654f));
        this.path.lineTo(this.decodeX(1.675f), this.decodeY(1.7715517f));
        this.path.curveTo(this.decodeAnchorX(1.675f, 0.0f), this.decodeAnchorY(1.7715517f, 0.0f), this.decodeAnchorX(1.8116667f, -23.5f), this.decodeAnchorY(1.4978448f, 33.5f), this.decodeX(1.8116667f), this.decodeY(1.4978448f));
        this.path.curveTo(this.decodeAnchorX(1.8116667f, 23.5f), this.decodeAnchorY(1.4978448f, -33.5f), this.decodeAnchorX(2.0f, 0.0f), this.decodeAnchorY(1.200431f, 0.0f), this.decodeX(2.0f), this.decodeY(1.200431f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.5666666f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.3383334f), this.decodeY(1.0f));
        this.path.curveTo(this.decodeAnchorX(1.3383334f, 0.0f), this.decodeAnchorY(1.0f, 0.0f), this.decodeAnchorX(1.4416666f, -21.0f), this.decodeAnchorY(1.3103448f, -37.5f), this.decodeX(1.4416666f), this.decodeY(1.3103448f));
        this.path.curveTo(this.decodeAnchorX(1.4416666f, 21.0f), this.decodeAnchorY(1.3103448f, 37.5f), this.decodeAnchorX(1.5733333f, 0.0f), this.decodeAnchorY(1.5840517f, 0.0f), this.decodeX(1.5733333f), this.decodeY(1.5840517f));
        this.path.curveTo(this.decodeAnchorX(1.5733333f, 0.0f), this.decodeAnchorY(1.5840517f, 0.0f), this.decodeAnchorX(1.6066667f, 1.5f), this.decodeAnchorY(1.2413793f, 29.5f), this.decodeX(1.6066667f), this.decodeY(1.2413793f));
        this.path.curveTo(this.decodeAnchorX(1.6066667f, -1.5f), this.decodeAnchorY(1.2413793f, -29.5f), this.decodeAnchorX(1.605f, 0.0f), this.decodeAnchorY(1.0f, 0.0f), this.decodeX(1.605f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.3383334f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath5() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.5683334f), this.decodeY(1.5797414f));
        this.path.curveTo(this.decodeAnchorX(1.5683334f, 0.0f), this.decodeAnchorY(1.5797414f, 0.0f), this.decodeAnchorX(1.575f, 0.0f), this.decodeAnchorY(1.2392242f, 33.0f), this.decodeX(1.575f), this.decodeY(1.2392242f));
        this.path.curveTo(this.decodeAnchorX(1.575f, 0.0f), this.decodeAnchorY(1.2392242f, -33.0f), this.decodeAnchorX(1.5616667f, 0.0f), this.decodeAnchorY(1.0f, 0.0f), this.decodeX(1.5616667f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.0f), this.decodeY(1.1982758f));
        this.path.curveTo(this.decodeAnchorX(2.0f, 0.0f), this.decodeAnchorY(1.1982758f, 0.0f), this.decodeAnchorX(1.8066666f, 27.5f), this.decodeAnchorY(1.5043104f, -38.5f), this.decodeX(1.8066666f), this.decodeY(1.5043104f));
        this.path.curveTo(this.decodeAnchorX(1.8066666f, -27.5f), this.decodeAnchorY(1.5043104f, 38.5f), this.decodeAnchorX(1.6766667f, 0.0f), this.decodeAnchorY(1.7780173f, 0.0f), this.decodeX(1.6766667f), this.decodeY(1.7780173f));
        this.path.lineTo(this.decodeX(1.5683334f), this.decodeY(1.5797414f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath6() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.5216666f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(1.5216666f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeAnchorX(1.5550001f, -2.0f), this.decodeAnchorY(1.7780173f, 22.5f), this.decodeX(1.5550001f), this.decodeY(1.7780173f));
        this.path.curveTo(this.decodeAnchorX(1.5550001f, 2.0f), this.decodeAnchorY(1.7780173f, -22.5f), this.decodeAnchorX(1.5683334f, 0.0f), this.decodeAnchorY(1.5765086f, 0.0f), this.decodeX(1.5683334f), this.decodeY(1.5765086f));
        this.path.lineTo(this.decodeX(1.6775f), this.decodeY(1.7747846f));
        this.path.curveTo(this.decodeAnchorX(1.6775f, 0.0f), this.decodeAnchorY(1.7747846f, 0.0f), this.decodeAnchorX(1.6508334f, 6.0f), this.decodeAnchorY(1.8922414f, -14.0f), this.decodeX(1.6508334f), this.decodeY(1.8922414f));
        this.path.curveTo(this.decodeAnchorX(1.6508334f, -6.0f), this.decodeAnchorY(1.8922414f, 14.0f), this.decodeAnchorX(1.6083333f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeX(1.6083333f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.5216666f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath7() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.6066667f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(1.6066667f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeAnchorX(1.64f, -7.0f), this.decodeAnchorY(1.8814654f, 17.0f), this.decodeX(1.64f), this.decodeY(1.8814654f));
        this.path.curveTo(this.decodeAnchorX(1.64f, 7.0f), this.decodeAnchorY(1.8814654f, -17.0f), this.decodeAnchorX(1.6775f, 0.0f), this.decodeAnchorY(1.7747846f, 0.0f), this.decodeX(1.6775f), this.decodeY(1.7747846f));
        this.path.curveTo(this.decodeAnchorX(1.6775f, 0.0f), this.decodeAnchorY(1.7747846f, 0.0f), this.decodeAnchorX(1.7416667f, -11.0f), this.decodeAnchorY(1.8836207f, -15.0f), this.decodeX(1.7416667f), this.decodeY(1.8836207f));
        this.path.curveTo(this.decodeAnchorX(1.7416667f, 11.0f), this.decodeAnchorY(1.8836207f, 15.0f), this.decodeAnchorX(1.8133333f, 0.0f), this.decodeAnchorY(2.0f, -0.5f), this.decodeX(1.8133333f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(1.8133333f, 0.0f), this.decodeAnchorY(2.0f, 0.5f), this.decodeAnchorX(1.6066667f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeX(1.6066667f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath8() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.2733333f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(1.2733333f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeAnchorX(1.2633333f, 5.0f), this.decodeAnchorY(1.6594827f, 37.0f), this.decodeX(1.2633333f), this.decodeY(1.6594827f));
        this.path.curveTo(this.decodeAnchorX(1.2633333f, -5.0f), this.decodeAnchorY(1.6594827f, -37.0f), this.decodeAnchorX(1.1933334f, 9.0f), this.decodeAnchorY(1.2241379f, 33.5f), this.decodeX(1.1933334f), this.decodeY(1.2241379f));
        this.path.curveTo(this.decodeAnchorX(1.1933334f, -9.0f), this.decodeAnchorY(1.2241379f, -33.5f), this.decodeAnchorX(1.1333333f, 0.0f), this.decodeAnchorY(1.0f, 0.0f), this.decodeX(1.1333333f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(1.6120689f));
        this.path.curveTo(this.decodeAnchorX(1.0f, 0.0f), this.decodeAnchorY(1.6120689f, 0.0f), this.decodeAnchorX(1.15f, 0.0f), this.decodeAnchorY(2.0f, -0.5f), this.decodeX(1.15f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(1.15f, 0.0f), this.decodeAnchorY(2.0f, 0.5f), this.decodeAnchorX(1.2733333f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeX(1.2733333f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath9() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(1.0f), this.decodeY(1.5969827f));
        this.path.curveTo(this.decodeAnchorX(1.0f, 0.0f), this.decodeAnchorY(1.5969827f, 0.0f), this.decodeAnchorX(1.0733334f, -10.0f), this.decodeAnchorY(1.7974138f, -19.5f), this.decodeX(1.0733334f), this.decodeY(1.7974138f));
        this.path.curveTo(this.decodeAnchorX(1.0733334f, 10.0f), this.decodeAnchorY(1.7974138f, 19.5f), this.decodeAnchorX(1.1666666f, 0.0f), this.decodeAnchorY(2.0f, -0.5f), this.decodeX(1.1666666f), this.decodeY(2.0f));
        this.path.curveTo(this.decodeAnchorX(1.1666666f, 0.0f), this.decodeAnchorY(2.0f, 0.5f), this.decodeAnchorX(1.0f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeX(1.0f), this.decodeY(2.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.75f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color1, this.decodeColor(this.color1, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.9567308f * n3 + n, 0.06835443f * n4 + n2, 0.75f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.83536583f * n3 + n, 0.9522059f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.8659696f * n3 + n, 0.011049724f * n4 + n2, 0.24809887f * n3 + n, 0.95027626f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color6, this.decodeColor(this.color6, this.color8, 0.5f), this.color8 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.3511236f * n3 + n, 0.09326425f * n4 + n2, 0.33426967f * n3 + n, 0.9846154f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color9, this.decodeColor(this.color9, this.color10, 0.5f), this.color10 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.3548387f * n3 + n, 0.114285715f * n4 + n2, 0.48387095f * n3 + n, 0.9809524f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color11, this.decodeColor(this.color11, this.color4, 0.5f), this.color4 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.75f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color1, this.decodeColor(this.color1, this.color12, 0.5f), this.color12 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.75f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14 });
    }
}
