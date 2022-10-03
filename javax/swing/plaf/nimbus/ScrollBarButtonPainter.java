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

final class ScrollBarButtonPainter extends AbstractRegionPainter
{
    static final int FOREGROUND_ENABLED = 1;
    static final int FOREGROUND_DISABLED = 2;
    static final int FOREGROUND_MOUSEOVER = 3;
    static final int FOREGROUND_PRESSED = 4;
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
    private Object[] componentColors;
    
    public ScrollBarButtonPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = new Color(255, 200, 0, 255);
        this.color2 = this.decodeColor("nimbusBlueGrey", -0.01111114f, -0.07763158f, -0.1490196f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", -0.111111104f, -0.10580933f, 0.086274505f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.102261856f, 0.20392156f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", -0.039682567f, -0.079276316f, 0.13333333f, 0);
        this.color6 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.07382907f, 0.109803915f, 0);
        this.color7 = this.decodeColor("nimbusBlueGrey", -0.039682567f, -0.08241387f, 0.23137254f, 0);
        this.color8 = this.decodeColor("nimbusBlueGrey", -0.055555522f, -0.08443936f, -0.29411766f, -136);
        this.color9 = this.decodeColor("nimbusBlueGrey", -0.055555522f, -0.09876161f, 0.25490195f, -178);
        this.color10 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.08878718f, -0.5647059f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.080223285f, -0.4862745f, 0);
        this.color12 = this.decodeColor("nimbusBlueGrey", -0.111111104f, -0.09525914f, -0.23137254f, 0);
        this.color13 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -165);
        this.color14 = this.decodeColor("nimbusBlueGrey", -0.04444444f, -0.080223285f, -0.09803921f, 0);
        this.color15 = this.decodeColor("nimbusBlueGrey", -0.6111111f, -0.110526316f, 0.10588235f, 0);
        this.color16 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color17 = this.decodeColor("nimbusBlueGrey", -0.039682567f, -0.081719734f, 0.20784312f, 0);
        this.color18 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.07677104f, 0.18431371f, 0);
        this.color19 = this.decodeColor("nimbusBlueGrey", -0.04444444f, -0.080223285f, -0.09803921f, -69);
        this.color20 = this.decodeColor("nimbusBlueGrey", -0.055555522f, -0.09876161f, 0.25490195f, -39);
        this.color21 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.0951417f, -0.49019608f, 0);
        this.color22 = this.decodeColor("nimbusBlueGrey", -0.027777791f, -0.086996906f, -0.4117647f, 0);
        this.color23 = this.decodeColor("nimbusBlueGrey", -0.111111104f, -0.09719298f, -0.15686274f, 0);
        this.color24 = this.decodeColor("nimbusBlueGrey", -0.037037015f, -0.043859646f, -0.21568626f, 0);
        this.color25 = this.decodeColor("nimbusBlueGrey", -0.06349206f, -0.07309316f, -0.011764705f, 0);
        this.color26 = this.decodeColor("nimbusBlueGrey", -0.048611104f, -0.07296763f, 0.09019607f, 0);
        this.color27 = this.decodeColor("nimbusBlueGrey", -0.03535354f, -0.05497076f, 0.031372547f, 0);
        this.color28 = this.decodeColor("nimbusBlueGrey", -0.034188032f, -0.043168806f, 0.011764705f, 0);
        this.color29 = this.decodeColor("nimbusBlueGrey", -0.03535354f, -0.0600676f, 0.109803915f, 0);
        this.color30 = this.decodeColor("nimbusBlueGrey", -0.037037015f, -0.043859646f, -0.21568626f, -44);
        this.color31 = this.decodeColor("nimbusBlueGrey", -0.6111111f, -0.110526316f, -0.74509805f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 1: {
                this.paintForegroundEnabled(graphics2D);
                break;
            }
            case 2: {
                this.paintForegroundDisabled(graphics2D);
                break;
            }
            case 3: {
                this.paintForegroundMouseOver(graphics2D);
                break;
            }
            case 4: {
                this.paintForegroundPressed(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintForegroundEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient1(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient2(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath5();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundDisabled(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundMouseOver(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color1);
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
        this.path = this.decodePath5();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.path);
    }
    
    private void paintForegroundPressed(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient8(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color31);
        graphics2D.fill(this.path);
        this.path = this.decodePath5();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.path);
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(3.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(3.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(1.6956522f), this.decodeY(0.0f));
        this.path.curveTo(this.decodeAnchorX(1.6956522f, 0.0f), this.decodeAnchorY(0.0f, 0.0f), this.decodeAnchorX(1.6956522f, -0.7058824f), this.decodeAnchorY(1.3076923f, -3.0294118f), this.decodeX(1.6956522f), this.decodeY(1.3076923f));
        this.path.curveTo(this.decodeAnchorX(1.6956522f, 0.7058824f), this.decodeAnchorY(1.3076923f, 3.0294118f), this.decodeAnchorX(1.826087f, -2.0f), this.decodeAnchorY(1.7692308f, -1.9411764f), this.decodeX(1.826087f), this.decodeY(1.7692308f));
        this.path.curveTo(this.decodeAnchorX(1.826087f, 2.0f), this.decodeAnchorY(1.7692308f, 1.9411764f), this.decodeAnchorX(3.0f, 0.0f), this.decodeAnchorY(2.0f, 0.0f), this.decodeX(3.0f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(1.0022625f));
        this.path.lineTo(this.decodeX(0.9705882f), this.decodeY(1.0384616f));
        this.path.lineTo(this.decodeX(1.0409207f), this.decodeY(1.0791855f));
        this.path.lineTo(this.decodeX(1.0409207f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(1.0022625f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.4782609f), this.decodeY(1.2307693f));
        this.path.lineTo(this.decodeX(1.4782609f), this.decodeY(1.7692308f));
        this.path.lineTo(this.decodeX(1.1713555f), this.decodeY(1.5f));
        this.path.lineTo(this.decodeX(1.4782609f), this.decodeY(1.2307693f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath5() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.6713555f), this.decodeY(1.0769231f));
        this.path.curveTo(this.decodeAnchorX(1.6713555f, 0.7352941f), this.decodeAnchorY(1.0769231f, 0.0f), this.decodeAnchorX(1.7186701f, -0.9117647f), this.decodeAnchorY(1.4095023f, -2.2058823f), this.decodeX(1.7186701f), this.decodeY(1.4095023f));
        this.path.curveTo(this.decodeAnchorX(1.7186701f, 0.9117647f), this.decodeAnchorY(1.4095023f, 2.2058823f), this.decodeAnchorX(1.8439897f, -2.3529413f), this.decodeAnchorY(1.7941177f, -1.8529412f), this.decodeX(1.8439897f), this.decodeY(1.7941177f));
        this.path.curveTo(this.decodeAnchorX(1.8439897f, 2.3529413f), this.decodeAnchorY(1.7941177f, 1.8529412f), this.decodeAnchorX(2.5f, 0.0f), this.decodeAnchorY(2.2352943f, 0.0f), this.decodeX(2.5f), this.decodeY(2.2352943f));
        this.path.lineTo(this.decodeX(2.3529415f), this.decodeY(2.8235292f));
        this.path.curveTo(this.decodeAnchorX(2.3529415f, 0.0f), this.decodeAnchorY(2.8235292f, 0.0f), this.decodeAnchorX(1.8184143f, 1.5588236f), this.decodeAnchorY(1.8438914f, 1.382353f), this.decodeX(1.8184143f), this.decodeY(1.8438914f));
        this.path.curveTo(this.decodeAnchorX(1.8184143f, -1.5588236f), this.decodeAnchorY(1.8438914f, -1.382353f), this.decodeAnchorX(1.6943734f, 0.7941176f), this.decodeAnchorY(1.4841628f, 2.0f), this.decodeX(1.6943734f), this.decodeY(1.4841628f));
        this.path.curveTo(this.decodeAnchorX(1.6943734f, -0.7941176f), this.decodeAnchorY(1.4841628f, -2.0f), this.decodeAnchorX(1.6713555f, -0.7352941f), this.decodeAnchorY(1.0769231f, 0.0f), this.decodeX(1.6713555f), this.decodeY(1.0769231f));
        this.path.closePath();
        return this.path;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.032934133f, 0.065868266f, 0.089820355f, 0.11377245f, 0.23053892f, 0.3473054f, 0.494012f, 0.6407186f, 0.78443116f, 0.92814374f }, new Color[] { this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5, this.decodeColor(this.color5, this.color6, 0.5f), this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.0f * n3 + n, 0.5f * n4 + n2, 0.5735294f * n3 + n, 0.5f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color8, this.decodeColor(this.color8, this.color9, 0.5f), this.color9 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.925f * n3 + n, 0.9285714f * n4 + n2, 0.925f * n3 + n, 0.004201681f * n4 + n2, new float[] { 0.0f, 0.2964072f, 0.5928144f, 0.79341316f, 0.994012f }, new Color[] { this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11, this.decodeColor(this.color11, this.color12, 0.5f), this.color12 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.032934133f, 0.065868266f, 0.089820355f, 0.11377245f, 0.23053892f, 0.3473054f, 0.494012f, 0.6407186f, 0.78443116f, 0.92814374f }, new Color[] { this.color14, this.decodeColor(this.color14, this.color15, 0.5f), this.color15, this.decodeColor(this.color15, this.color16, 0.5f), this.color16, this.decodeColor(this.color16, this.color17, 0.5f), this.color17, this.decodeColor(this.color17, this.color18, 0.5f), this.color18, this.decodeColor(this.color18, this.color16, 0.5f), this.color16 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.0f * n3 + n, 0.5f * n4 + n2, 0.5735294f * n3 + n, 0.5f * n4 + n2, new float[] { 0.19518717f, 0.5975936f, 1.0f }, new Color[] { this.color19, this.decodeColor(this.color19, this.color20, 0.5f), this.color20 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.925f * n3 + n, 0.9285714f * n4 + n2, 0.925f * n3 + n, 0.004201681f * n4 + n2, new float[] { 0.0f, 0.2964072f, 0.5928144f, 0.79341316f, 0.994012f }, new Color[] { this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22, this.decodeColor(this.color22, this.color23, 0.5f), this.color23 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.032934133f, 0.065868266f, 0.089820355f, 0.11377245f, 0.23053892f, 0.3473054f, 0.494012f, 0.6407186f, 0.78443116f, 0.92814374f }, new Color[] { this.color24, this.decodeColor(this.color24, this.color25, 0.5f), this.color25, this.decodeColor(this.color25, this.color26, 0.5f), this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27, this.decodeColor(this.color27, this.color28, 0.5f), this.color28, this.decodeColor(this.color28, this.color29, 0.5f), this.color29 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.0f * n3 + n, 0.5f * n4 + n2, 0.5735294f * n3 + n, 0.5f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color30, this.decodeColor(this.color30, this.color9, 0.5f), this.color9 });
    }
}
