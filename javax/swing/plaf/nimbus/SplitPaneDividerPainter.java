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

final class SplitPaneDividerPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_ENABLED = 1;
    static final int BACKGROUND_FOCUSED = 2;
    static final int FOREGROUND_ENABLED = 3;
    static final int FOREGROUND_ENABLED_VERTICAL = 4;
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
    private Object[] componentColors;
    
    public SplitPaneDividerPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.017358616f, -0.11372548f, 0);
        this.color2 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.102396235f, 0.21960783f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07016757f, 0.12941176f, 0);
        this.color4 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color6 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.048026316f, 0.007843137f, 0);
        this.color7 = this.decodeColor("nimbusBlueGrey", 0.0055555105f, -0.06970999f, 0.21568626f, 0);
        this.color8 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.06704806f, 0.06666666f, 0);
        this.color9 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.019617222f, -0.09803921f, 0);
        this.color10 = this.decodeColor("nimbusBlueGrey", 0.004273474f, -0.03790062f, -0.043137252f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", -0.111111104f, -0.106573746f, 0.24705881f, 0);
        this.color12 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.049301825f, 0.02352941f, 0);
        this.color13 = this.decodeColor("nimbusBlueGrey", -0.006944418f, -0.07399663f, 0.11372548f, 0);
        this.color14 = this.decodeColor("nimbusBlueGrey", -0.018518567f, -0.06998578f, 0.12549019f, 0);
        this.color15 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.050526317f, 0.039215684f, 0);
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
                this.paintBackgroundFocused(graphics2D);
                break;
            }
            case 3: {
                this.paintForegroundEnabled(graphics2D);
                break;
            }
            case 4: {
                this.paintForegroundEnabledAndVertical(graphics2D);
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
    }
    
    private void paintBackgroundFocused(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.decodeGradient2(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintForegroundEnabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private void paintForegroundEnabledAndVertical(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient6(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(0.0f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(3.0f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private RoundRectangle2D decodeRoundRect1() {
        this.roundRect.setRoundRect(this.decodeX(1.05f), this.decodeY(1.3f), this.decodeX(1.95f) - this.decodeX(1.05f), this.decodeY(1.8f) - this.decodeY(1.3f), 3.6666667461395264, 3.6666667461395264);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect2() {
        this.roundRect.setRoundRect(this.decodeX(1.1f), this.decodeY(1.4f), this.decodeX(1.9f) - this.decodeX(1.1f), this.decodeY(1.7f) - this.decodeY(1.4f), 4.0, 4.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect3() {
        this.roundRect.setRoundRect(this.decodeX(1.3f), this.decodeY(1.1428572f), this.decodeX(1.7f) - this.decodeX(1.3f), this.decodeY(1.8214285f) - this.decodeY(1.1428572f), 4.0, 4.0);
        return this.roundRect;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(1.4f), this.decodeY(1.1785715f), this.decodeX(1.6f) - this.decodeX(1.4f), this.decodeY(1.7678571f) - this.decodeY(1.1785715f));
        return this.rect;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.058064517f, 0.08064516f, 0.103225805f, 0.116129026f, 0.12903225f, 0.43387097f, 0.7387097f, 0.77903223f, 0.81935483f, 0.85806453f, 0.8967742f }, new Color[] { this.color1, this.decodeColor(this.color1, this.color2, 0.5f), this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3, this.decodeColor(this.color3, this.color3, 0.5f), this.color3, this.decodeColor(this.color3, this.color2, 0.5f), this.color2, this.decodeColor(this.color2, this.color1, 0.5f), this.color1 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.058064517f, 0.08064516f, 0.103225805f, 0.1166129f, 0.13f, 0.43f, 0.73f, 0.7746774f, 0.81935483f, 0.85806453f, 0.8967742f }, new Color[] { this.color1, this.decodeColor(this.color1, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color3, 0.5f), this.color3, this.decodeColor(this.color3, this.color3, 0.5f), this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color1, 0.5f), this.color1 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.20645161f, 0.5f, 0.7935484f }, new Color[] { this.color1, this.decodeColor(this.color1, this.color5, 0.5f), this.color5 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.090322584f, 0.2951613f, 0.5f, 0.5822581f, 0.66451615f }, new Color[] { this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.75f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.42096773f, 0.84193546f, 0.8951613f, 0.9483871f }, new Color[] { this.color9, this.decodeColor(this.color9, this.color10, 0.5f), this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.08064516f, 0.16129032f, 0.5129032f, 0.86451614f, 0.88548386f, 0.90645164f }, new Color[] { this.color12, this.decodeColor(this.color12, this.color13, 0.5f), this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14, this.decodeColor(this.color14, this.color15, 0.5f), this.color15 });
    }
}
