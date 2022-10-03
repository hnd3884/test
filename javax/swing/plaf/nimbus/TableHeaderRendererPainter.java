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

final class TableHeaderRendererPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_ENABLED_FOCUSED = 3;
    static final int BACKGROUND_MOUSEOVER = 4;
    static final int BACKGROUND_PRESSED = 5;
    static final int BACKGROUND_ENABLED_SORTED = 6;
    static final int BACKGROUND_ENABLED_FOCUSED_SORTED = 7;
    static final int BACKGROUND_DISABLED_SORTED = 8;
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
    private Object[] componentColors;
    
    public TableHeaderRendererPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBorder", -0.013888836f, 5.823001E-4f, -0.12941176f, 0);
        this.color2 = this.decodeColor("nimbusBlueGrey", -0.01111114f, -0.08625447f, 0.062745094f, 0);
        this.color3 = this.decodeColor("nimbusBlueGrey", -0.013888836f, -0.028334536f, -0.17254901f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", -0.013888836f, -0.029445238f, -0.16470587f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", -0.02020204f, -0.053531498f, 0.011764705f, 0);
        this.color6 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.10655806f, 0.24313724f, 0);
        this.color7 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.08455229f, 0.1607843f, 0);
        this.color8 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07016757f, 0.12941176f, 0);
        this.color9 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07466974f, 0.23921567f, 0);
        this.color10 = this.decodeColor("nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", 0.055555582f, -0.10658931f, 0.25098038f, 0);
        this.color12 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.08613607f, 0.21960783f, 0);
        this.color13 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.07333623f, 0.20392156f, 0);
        this.color14 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color15 = this.decodeColor("nimbusBlueGrey", -0.00505054f, -0.05960039f, 0.10196078f, 0);
        this.color16 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.017742813f, 0.015686274f, 0);
        this.color17 = this.decodeColor("nimbusBlueGrey", -0.0027777553f, -0.0018306673f, -0.02352941f, 0);
        this.color18 = this.decodeColor("nimbusBlueGrey", 0.0055555105f, -0.020436227f, 0.12549019f, 0);
        this.color19 = this.decodeColor("nimbusBase", -0.023096085f, -0.62376213f, 0.4352941f, 0);
        this.color20 = this.decodeColor("nimbusBase", -0.0012707114f, -0.50901747f, 0.31764704f, 0);
        this.color21 = this.decodeColor("nimbusBase", -0.002461195f, -0.47139505f, 0.2862745f, 0);
        this.color22 = this.decodeColor("nimbusBase", -0.0051222444f, -0.49103343f, 0.372549f, 0);
        this.color23 = this.decodeColor("nimbusBase", -8.738637E-4f, -0.49872798f, 0.3098039f, 0);
        this.color24 = this.decodeColor("nimbusBase", -2.2029877E-4f, -0.4916465f, 0.37647057f, 0);
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
                this.paintBackgroundEnabledAndFocused(graphics2D);
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
            case 6: {
                this.paintBackgroundEnabledAndSorted(graphics2D);
                break;
            }
            case 7: {
                this.paintBackgroundEnabledAndFocusedAndSorted(graphics2D);
                break;
            }
            case 8: {
                this.paintBackgroundDisabledAndSorted(graphics2D);
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
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient2(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient2(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundEnabledAndFocused(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient2(this.rect));
        graphics2D.fill(this.rect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundMouseOver(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient3(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundPressed(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient4(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundEnabledAndSorted(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient5(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundEnabledAndFocusedAndSorted(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient6(this.rect));
        graphics2D.fill(this.rect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundDisabledAndSorted(final Graphics2D graphics2D) {
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.decodeGradient1(this.rect));
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.decodeGradient2(this.rect));
        graphics2D.fill(this.rect);
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(2.8f), this.decodeX(3.0f) - this.decodeX(0.0f), this.decodeY(3.0f) - this.decodeY(2.8f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(2.8f), this.decodeY(0.0f), this.decodeX(3.0f) - this.decodeX(2.8f), this.decodeY(2.8f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect3() {
        this.rect.setRect(this.decodeX(0.0f), this.decodeY(0.0f), this.decodeX(2.8f) - this.decodeX(0.0f), this.decodeY(2.8f) - this.decodeY(0.0f));
        return this.rect;
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.24000001f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.24000001f), this.decodeY(0.24000001f));
        this.path.lineTo(this.decodeX(2.7599998f), this.decodeY(0.24000001f));
        this.path.lineTo(this.decodeX(2.7599998f), this.decodeY(2.7599998f));
        this.path.lineTo(this.decodeX(0.24000001f), this.decodeY(2.7599998f));
        this.path.lineTo(this.decodeX(0.24000001f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.14441223f, 0.43703705f, 0.59444445f, 0.75185186f, 0.8759259f, 1.0f }, new Color[] { this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.07147767f, 0.2888889f, 0.5490909f, 0.7037037f, 0.8518518f, 1.0f }, new Color[] { this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8, this.decodeColor(this.color8, this.color9, 0.5f), this.color9 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.07147767f, 0.2888889f, 0.5490909f, 0.7037037f, 0.7919203f, 0.88013697f }, new Color[] { this.color11, this.decodeColor(this.color11, this.color12, 0.5f), this.color12, this.decodeColor(this.color12, this.color13, 0.5f), this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.07147767f, 0.2888889f, 0.5490909f, 0.7037037f, 0.8518518f, 1.0f }, new Color[] { this.color15, this.decodeColor(this.color15, this.color16, 0.5f), this.color16, this.decodeColor(this.color16, this.color17, 0.5f), this.color17, this.decodeColor(this.color17, this.color18, 0.5f), this.color18 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.08049711f, 0.32534248f, 0.56267816f, 0.7037037f, 0.83986557f, 0.97602737f }, new Color[] { this.color19, this.decodeColor(this.color19, this.color20, 0.5f), this.color20, this.decodeColor(this.color20, this.color21, 0.5f), this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.07147767f, 0.2888889f, 0.5490909f, 0.7037037f, 0.8518518f, 1.0f }, new Color[] { this.color19, this.decodeColor(this.color19, this.color23, 0.5f), this.color23, this.decodeColor(this.color23, this.color21, 0.5f), this.color21, this.decodeColor(this.color21, this.color24, 0.5f), this.color24 });
    }
}
