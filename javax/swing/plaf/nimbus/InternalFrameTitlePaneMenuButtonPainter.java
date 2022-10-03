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

final class InternalFrameTitlePaneMenuButtonPainter extends AbstractRegionPainter
{
    static final int ICON_ENABLED = 1;
    static final int ICON_DISABLED = 2;
    static final int ICON_MOUSEOVER = 3;
    static final int ICON_PRESSED = 4;
    static final int ICON_ENABLED_WINDOWNOTFOCUSED = 5;
    static final int ICON_MOUSEOVER_WINDOWNOTFOCUSED = 6;
    static final int ICON_PRESSED_WINDOWNOTFOCUSED = 7;
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
    private Object[] componentColors;
    
    public InternalFrameTitlePaneMenuButtonPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", 0.0055555105f, -0.0029994324f, -0.38039216f, -185);
        this.color2 = this.decodeColor("nimbusBase", 0.08801502f, 0.3642857f, -0.5019608f, 0);
        this.color3 = this.decodeColor("nimbusBase", 0.030543745f, -0.3835404f, -0.09803924f, 0);
        this.color4 = this.decodeColor("nimbusBase", 0.029191494f, -0.53801316f, 0.13333333f, 0);
        this.color5 = this.decodeColor("nimbusBase", 0.030543745f, -0.3857143f, -0.09411767f, 0);
        this.color6 = this.decodeColor("nimbusBase", 0.030543745f, -0.43148893f, 0.007843137f, 0);
        this.color7 = this.decodeColor("nimbusBase", 0.029191494f, -0.24935067f, -0.20392159f, -132);
        this.color8 = this.decodeColor("nimbusBase", 0.029191494f, -0.24935067f, -0.20392159f, 0);
        this.color9 = this.decodeColor("nimbusBase", 0.029191494f, -0.24935067f, -0.20392159f, -123);
        this.color10 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, 0);
        this.color11 = this.decodeColor("nimbusBlueGrey", 0.0055555105f, -0.0029994324f, -0.38039216f, -208);
        this.color12 = this.decodeColor("nimbusBase", 0.02551502f, -0.5942635f, 0.20784312f, 0);
        this.color13 = this.decodeColor("nimbusBase", 0.032459438f, -0.5490091f, 0.12941176f, 0);
        this.color14 = this.decodeColor("nimbusBase", 0.032459438f, -0.5469569f, 0.11372548f, 0);
        this.color15 = this.decodeColor("nimbusBase", 0.032459438f, -0.5760128f, 0.23921567f, 0);
        this.color16 = this.decodeColor("nimbusBase", 0.08801502f, 0.3642857f, -0.4901961f, 0);
        this.color17 = this.decodeColor("nimbusBase", 0.032459438f, -0.1857143f, -0.23529413f, 0);
        this.color18 = this.decodeColor("nimbusBase", 0.029191494f, -0.5438224f, 0.17647058f, 0);
        this.color19 = this.decodeColor("nimbusBase", 0.030543745f, -0.41929638f, -0.02352941f, 0);
        this.color20 = this.decodeColor("nimbusBase", 0.030543745f, -0.45559007f, 0.082352936f, 0);
        this.color21 = this.decodeColor("nimbusBase", 0.03409344f, -0.329408f, -0.11372551f, -132);
        this.color22 = this.decodeColor("nimbusBase", 0.03409344f, -0.329408f, -0.11372551f, 0);
        this.color23 = this.decodeColor("nimbusBase", 0.03409344f, -0.329408f, -0.11372551f, -123);
        this.color24 = this.decodeColor("nimbusBase", -0.57865167f, -0.6357143f, -0.54901963f, 0);
        this.color25 = this.decodeColor("nimbusBase", 0.031104386f, 0.12354499f, -0.33725494f, 0);
        this.color26 = this.decodeColor("nimbusBase", 0.032459438f, -0.4592437f, -0.015686274f, 0);
        this.color27 = this.decodeColor("nimbusBase", 0.029191494f, -0.2579365f, -0.19607845f, 0);
        this.color28 = this.decodeColor("nimbusBase", 0.03409344f, -0.3149596f, -0.13333336f, 0);
        this.color29 = this.decodeColor("nimbusBase", 0.029681683f, 0.07857144f, -0.3294118f, -132);
        this.color30 = this.decodeColor("nimbusBase", 0.029681683f, 0.07857144f, -0.3294118f, 0);
        this.color31 = this.decodeColor("nimbusBase", 0.029681683f, 0.07857144f, -0.3294118f, -123);
        this.color32 = this.decodeColor("nimbusBase", 0.032459438f, -0.53637654f, 0.043137252f, 0);
        this.color33 = this.decodeColor("nimbusBase", 0.032459438f, -0.49935067f, -0.11764708f, 0);
        this.color34 = this.decodeColor("nimbusBase", 0.021348298f, -0.6133929f, 0.32941175f, 0);
        this.color35 = this.decodeColor("nimbusBase", 0.042560518f, -0.5804379f, 0.23137254f, 0);
        this.color36 = this.decodeColor("nimbusBase", 0.032459438f, -0.57417583f, 0.21568626f, 0);
        this.color37 = this.decodeColor("nimbusBase", 0.027408898f, -0.5784226f, 0.20392156f, -132);
        this.color38 = this.decodeColor("nimbusBase", 0.042560518f, -0.5665319f, 0.0745098f, 0);
        this.color39 = this.decodeColor("nimbusBase", 0.036732912f, -0.5642857f, 0.16470587f, -123);
        this.color40 = this.decodeColor("nimbusBase", 0.021348298f, -0.54480517f, -0.11764708f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 1: {
                this.painticonEnabled(graphics2D);
                break;
            }
            case 2: {
                this.painticonDisabled(graphics2D);
                break;
            }
            case 3: {
                this.painticonMouseOver(graphics2D);
                break;
            }
            case 4: {
                this.painticonPressed(graphics2D);
                break;
            }
            case 5: {
                this.painticonEnabledAndWindowNotFocused(graphics2D);
                break;
            }
            case 6: {
                this.painticonMouseOverAndWindowNotFocused(graphics2D);
                break;
            }
            case 7: {
                this.painticonPressedAndWindowNotFocused(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void painticonEnabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient1(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.path);
    }
    
    private void painticonDisabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color11);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color15);
        graphics2D.fill(this.path);
    }
    
    private void painticonMouseOver(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.path);
    }
    
    private void painticonPressed(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient9(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient10(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.path);
    }
    
    private void painticonEnabledAndWindowNotFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient11(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient12(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient13(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color40);
        graphics2D.fill(this.path);
    }
    
    private void painticonMouseOverAndWindowNotFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.path);
    }
    
    private void painticonPressedAndWindowNotFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient9(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.decodeGradient10(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color10);
        graphics2D.fill(this.path);
    }
    
    private RoundRectangle2D decodeRoundRect1() {
        this.roundRect.setRoundRect(this.decodeX(1.0f), this.decodeY(1.6111112f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(2.0f) - this.decodeY(1.6111112f), 6.0, 6.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect2() {
        this.roundRect.setRoundRect(this.decodeX(1.0f), this.decodeY(1.0f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(1.9444444f) - this.decodeY(1.0f), 8.600000381469727, 8.600000381469727);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect3() {
        this.roundRect.setRoundRect(this.decodeX(1.0526316f), this.decodeY(1.0555556f), this.decodeX(1.9473684f) - this.decodeX(1.0526316f), this.decodeY(1.8888888f) - this.decodeY(1.0555556f), 6.75, 6.75);
        return this.roundRect;
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.3157895f), this.decodeY(1.4444444f));
        this.path.lineTo(this.decodeX(1.6842105f), this.decodeY(1.4444444f));
        this.path.lineTo(this.decodeX(1.5013158f), this.decodeY(1.7208333f));
        this.path.lineTo(this.decodeX(1.3157895f), this.decodeY(1.4444444f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.3157895f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(1.6842105f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(1.6083333f));
        this.path.lineTo(this.decodeX(1.3157895f), this.decodeY(1.3333334f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.3157895f), this.decodeY(1.3888888f));
        this.path.lineTo(this.decodeX(1.6842105f), this.decodeY(1.3888888f));
        this.path.lineTo(this.decodeX(1.4952153f), this.decodeY(1.655303f));
        this.path.lineTo(this.decodeX(1.3157895f), this.decodeY(1.3888888f));
        this.path.closePath();
        return this.path;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color2, this.decodeColor(this.color2, this.color3, 0.5f), this.color3 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color4, this.decodeColor(this.color4, this.color5, 0.5f), this.color5, this.decodeColor(this.color5, this.color3, 0.5f), this.color3, this.decodeColor(this.color3, this.color6, 0.5f), this.color6 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.50714284f * n3 + n, 0.095f * n4 + n2, 0.49285713f * n3 + n, 0.91f * n4 + n2, new float[] { 0.0f, 0.24289773f, 0.48579547f, 0.74289775f, 1.0f }, new Color[] { this.color7, this.decodeColor(this.color7, this.color8, 0.5f), this.color8, this.decodeColor(this.color8, this.color9, 0.5f), this.color9 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.31107953f, 0.62215906f, 0.8110795f, 1.0f }, new Color[] { this.color12, this.decodeColor(this.color12, this.color13, 0.5f), this.color13, this.decodeColor(this.color13, this.color14, 0.5f), this.color14 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color16, this.decodeColor(this.color16, this.color17, 0.5f), this.color17 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color18, this.decodeColor(this.color18, this.color19, 0.5f), this.color19, this.decodeColor(this.color19, this.color19, 0.5f), this.color19, this.decodeColor(this.color19, this.color20, 0.5f), this.color20 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.50714284f * n3 + n, 0.095f * n4 + n2, 0.49285713f * n3 + n, 0.91f * n4 + n2, new float[] { 0.0f, 0.24289773f, 0.48579547f, 0.74289775f, 1.0f }, new Color[] { this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22, this.decodeColor(this.color22, this.color23, 0.5f), this.color23 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color24, this.decodeColor(this.color24, this.color25, 0.5f), this.color25 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27, this.decodeColor(this.color27, this.color27, 0.5f), this.color27, this.decodeColor(this.color27, this.color28, 0.5f), this.color28 });
    }
    
    private Paint decodeGradient10(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.50714284f * n3 + n, 0.095f * n4 + n2, 0.49285713f * n3 + n, 0.91f * n4 + n2, new float[] { 0.0f, 0.24289773f, 0.48579547f, 0.74289775f, 1.0f }, new Color[] { this.color29, this.decodeColor(this.color29, this.color30, 0.5f), this.color30, this.decodeColor(this.color30, this.color31, 0.5f), this.color31 });
    }
    
    private Paint decodeGradient11(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color32, this.decodeColor(this.color32, this.color33, 0.5f), this.color33 });
    }
    
    private Paint decodeGradient12(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color34, this.decodeColor(this.color34, this.color35, 0.5f), this.color35, this.decodeColor(this.color35, this.color36, 0.5f), this.color36, this.decodeColor(this.color36, this.color15, 0.5f), this.color15 });
    }
    
    private Paint decodeGradient13(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.50714284f * n3 + n, 0.095f * n4 + n2, 0.49285713f * n3 + n, 0.91f * n4 + n2, new float[] { 0.0f, 0.24289773f, 0.48579547f, 0.74289775f, 1.0f }, new Color[] { this.color37, this.decodeColor(this.color37, this.color38, 0.5f), this.color38, this.decodeColor(this.color38, this.color39, 0.5f), this.color39 });
    }
}
