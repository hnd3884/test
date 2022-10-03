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

final class ScrollBarTrackPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
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
    private Object[] componentColors;
    
    public ScrollBarTrackPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.10016362f, 0.011764705f, 0);
        this.color2 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.100476064f, 0.035294116f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.10606203f, 0.13333333f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", -0.6111111f, -0.110526316f, 0.24705881f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", 0.02222228f, -0.06465475f, -0.31764707f, 0);
        this.color6 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06766917f, -0.19607842f, 0);
        this.color7 = this.decodeColor("nimbusBlueGrey", -0.006944418f, -0.0655825f, -0.04705882f, 0);
        this.color8 = this.decodeColor("nimbusBlueGrey", 0.0138888955f, -0.071117446f, 0.05098039f, 0);
        this.color9 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07016757f, 0.12941176f, 0);
        this.color10 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.05967886f, -0.5137255f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.05967886f, -0.5137255f, -255);
        this.color12 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.07826825f, -0.5019608f, -255);
        this.color13 = this.decodeColor("nimbusBlueGrey", -0.015872955f, -0.06731644f, -0.109803915f, 0);
        this.color14 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06924191f, 0.109803915f, 0);
        this.color15 = this.decodeColor("nimbusBlueGrey", -0.015872955f, -0.06861015f, -0.09019607f, 0);
        this.color16 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06766917f, 0.07843137f, 0);
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
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintBackgroundDisabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.decodeGradient2(this.rect));
        graphics2D.fill(this.rect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient4(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient5(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient6(this.path));
        graphics2D.fill(this.path);
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(0.0f), this.decodeX(3.0f) - this.decodeX(0.0f), this.decodeY(3.0f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.7f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(1.2f));
        this.path.curveTo(this.decodeAnchorX(0.0f, 0.0f), this.decodeAnchorY(1.2f, 0.0f), this.decodeAnchorX(0.3f, -1.0f), this.decodeAnchorY(2.2f, -1.0f), this.decodeX(0.3f), this.decodeY(2.2f));
        this.path.curveTo(this.decodeAnchorX(0.3f, 1.0f), this.decodeAnchorY(2.2f, 1.0f), this.decodeAnchorX(0.6785714f, 0.0f), this.decodeAnchorY(2.8f, 0.0f), this.decodeX(0.6785714f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.7f), this.decodeY(0.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(3.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(2.2222223f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(2.2222223f), this.decodeY(2.8f));
        this.path.curveTo(this.decodeAnchorX(2.2222223f, 0.0f), this.decodeAnchorY(2.8f, 0.0f), this.decodeAnchorX(2.6746032f, -1.0f), this.decodeAnchorY(2.1857142f, 1.0f), this.decodeX(2.6746032f), this.decodeY(2.1857142f));
        this.path.curveTo(this.decodeAnchorX(2.6746032f, 1.0f), this.decodeAnchorY(2.1857142f, -1.0f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(1.2f, 0.0f), this.decodeX(3.0f), this.decodeY(1.2f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(0.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.11428572f), this.decodeY(1.3714286f));
        this.path.curveTo(this.decodeAnchorX(0.11428572f, 0.78571427f), this.decodeAnchorY(1.3714286f, -0.5714286f), this.decodeAnchorX(0.4642857f, -1.3571428f), this.decodeAnchorY(2.0714285f, -1.5714285f), this.decodeX(0.4642857f), this.decodeY(2.0714285f));
        this.path.curveTo(this.decodeAnchorX(0.4642857f, 1.3571428f), this.decodeAnchorY(2.0714285f, 1.5714285f), this.decodeAnchorX(0.8714286f, 0.21428572f), this.decodeAnchorY(2.7285714f, -1.0f), this.decodeX(0.8714286f), this.decodeY(2.7285714f));
        this.path.curveTo(this.decodeAnchorX(0.8714286f, -0.21428572f), this.decodeAnchorY(2.7285714f, 1.0f), this.decodeAnchorX(0.35714287f, 1.5f), this.decodeAnchorY(2.3142858f, 1.6428572f), this.decodeX(0.35714287f), this.decodeY(2.3142858f));
        this.path.curveTo(this.decodeAnchorX(0.35714287f, -1.5f), this.decodeAnchorY(2.3142858f, -1.6428572f), this.decodeAnchorX(0.11428572f, -0.78571427f), this.decodeAnchorY(1.3714286f, 0.5714286f), this.decodeX(0.11428572f), this.decodeY(1.3714286f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(2.1111112f), this.decodeY(2.7f));
        this.path.curveTo(this.decodeAnchorX(2.1111112f, 0.42857143f), this.decodeAnchorY(2.7f, 0.64285713f), this.decodeAnchorX(2.6269841f, -1.5714285f), this.decodeAnchorY(2.2f, 1.6428572f), this.decodeX(2.6269841f), this.decodeY(2.2f));
        this.path.curveTo(this.decodeAnchorX(2.6269841f, 1.5714285f), this.decodeAnchorY(2.2f, -1.6428572f), this.decodeAnchorX(2.84127f, 0.71428573f), this.decodeAnchorY(1.3857143f, 0.64285713f), this.decodeX(2.84127f), this.decodeY(1.3857143f));
        this.path.curveTo(this.decodeAnchorX(2.84127f, -0.71428573f), this.decodeAnchorY(1.3857143f, -0.64285713f), this.decodeAnchorX(2.5238094f, 0.71428573f), this.decodeAnchorY(2.0571427f, -0.85714287f), this.decodeX(2.5238094f), this.decodeY(2.0571427f));
        this.path.curveTo(this.decodeAnchorX(2.5238094f, -0.71428573f), this.decodeAnchorY(2.0571427f, 0.85714287f), this.decodeAnchorX(2.1111112f, -0.42857143f), this.decodeAnchorY(2.7f, -0.64285713f), this.decodeX(2.1111112f), this.decodeY(2.7f));
        this.path.closePath();
        return this.path;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.016129032f, 0.038709678f, 0.061290324f, 0.16091082f, 0.26451612f, 0.4378071f, 0.88387096f }, new Color[] { this.color1, this.decodeColor(this.color1, this.color2, 0.5f), this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.030645162f, 0.061290324f, 0.09677419f, 0.13225806f, 0.22096774f, 0.30967742f, 0.47434634f, 0.82258064f }, new Color[] { this.color5, this.decodeColor(this.color5, this.color6, 0.5f), this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8, this.decodeColor(this.color8, this.color9, 0.5f), this.color9 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.0f * n3 + n, 0.0f * n4 + n2, 0.9285714f * n3 + n, 0.12244898f * n4 + n2, new float[] { 0.0f, 0.1f, 1.0f }, new Color[] { this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(-0.045918368f * n3 + n, 0.18336426f * n4 + n2, 0.872449f * n3 + n, 0.04050711f * n4 + n2, new float[] { 0.0f, 0.87096775f, 1.0f }, new Color[] { this.color12, this.decodeColor(this.color12, this.color10, 0.5f), this.color10 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.12719299f * n3 + n, 0.13157894f * n4 + n2, 0.90789473f * n3 + n, 0.877193f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.86458343f * n3 + n, 0.20952381f * n4 + n2, 0.020833189f * n3 + n, 0.95238096f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color15, this.decodeColor(this.color15, this.color16, 0.5f), this.color16 });
    }
}
