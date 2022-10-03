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

final class SliderTrackPainter extends AbstractRegionPainter
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
    private Object[] componentColors;
    
    public SliderTrackPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -245);
        this.color2 = this.decodeColor("nimbusBlueGrey", 0.0055555105f, -0.061265234f, 0.05098039f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", 0.01010108f, -0.059835073f, 0.10588235f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", -0.01111114f, -0.061982628f, 0.062745094f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", -0.00505054f, -0.058639523f, 0.086274505f, 0);
        this.color6 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -111);
        this.color7 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.034093194f, -0.12941176f, 0);
        this.color8 = this.decodeColor("nimbusBlueGrey", 0.01111114f, -0.023821115f, -0.06666666f, 0);
        this.color9 = this.decodeColor("nimbusBlueGrey", -0.008547008f, -0.03314536f, -0.086274505f, 0);
        this.color10 = this.decodeColor("nimbusBlueGrey", 0.004273474f, -0.040256046f, -0.019607842f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.03626889f, 0.04705882f, 0);
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
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.color6);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect5();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
    }
    
    private RoundRectangle2D decodeRoundRect1() {
        this.roundRect.setRoundRect(this.decodeX(0.2f), this.decodeY(1.6f), this.decodeX(2.8f) - this.decodeX(0.2f), this.decodeY(2.8333333f) - this.decodeY(1.6f), 8.70588207244873, 8.70588207244873);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect2() {
        this.roundRect.setRoundRect(this.decodeX(0.0f), this.decodeY(1.0f), this.decodeX(3.0f) - this.decodeX(0.0f), this.decodeY(2.0f) - this.decodeY(1.0f), 4.941176414489746, 4.941176414489746);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect3() {
        this.roundRect.setRoundRect(this.decodeX(0.29411763f), this.decodeY(1.2f), this.decodeX(2.7058823f) - this.decodeX(0.29411763f), this.decodeY(2.0f) - this.decodeY(1.2f), 4.0, 4.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect4() {
        this.roundRect.setRoundRect(this.decodeX(0.2f), this.decodeY(1.6f), this.decodeX(2.8f) - this.decodeX(0.2f), this.decodeY(2.1666667f) - this.decodeY(1.6f), 8.70588207244873, 8.70588207244873);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect5() {
        this.roundRect.setRoundRect(this.decodeX(0.28823528f), this.decodeY(1.2f), this.decodeX(2.7f) - this.decodeX(0.28823528f), this.decodeY(2.0f) - this.decodeY(1.2f), 4.0, 4.0);
        return this.roundRect;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.07647059f * n4 + n2, 0.25f * n3 + n, 0.9117647f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.13770053f, 0.27540106f, 0.63770056f, 1.0f }, new Color[] { this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5, this.decodeColor(this.color5, this.color3, 0.5f), this.color3 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.07647059f * n4 + n2, 0.25f * n3 + n, 0.9117647f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.13770053f, 0.27540106f, 0.4906417f, 0.7058824f }, new Color[] { this.color9, this.decodeColor(this.color9, this.color10, 0.5f), this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11 });
    }
}
