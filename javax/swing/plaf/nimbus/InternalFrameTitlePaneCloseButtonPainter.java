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

final class InternalFrameTitlePaneCloseButtonPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_MOUSEOVER = 3;
    static final int BACKGROUND_PRESSED = 4;
    static final int BACKGROUND_ENABLED_WINDOWNOTFOCUSED = 5;
    static final int BACKGROUND_MOUSEOVER_WINDOWNOTFOCUSED = 6;
    static final int BACKGROUND_PRESSED_WINDOWNOTFOCUSED = 7;
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
    private Object[] componentColors;
    
    public InternalFrameTitlePaneCloseButtonPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusRed", 0.5893519f, -0.75736576f, 0.09411764f, 0);
        this.color2 = this.decodeColor("nimbusRed", 0.5962963f, -0.71005917f, 0.0f, 0);
        this.color3 = this.decodeColor("nimbusRed", 0.6005698f, -0.7200287f, -0.015686274f, -122);
        this.color4 = this.decodeColor("nimbusBlueGrey", 0.0055555105f, -0.062449392f, 0.07058823f, 0);
        this.color5 = this.decodeColor("nimbusBlueGrey", 0.0055555105f, -0.0029994324f, -0.38039216f, -185);
        this.color6 = this.decodeColor("nimbusRed", -0.014814814f, 0.20118344f, -0.4431373f, 0);
        this.color7 = this.decodeColor("nimbusRed", -2.7342606E-4f, 0.13829035f, -0.039215684f, 0);
        this.color8 = this.decodeColor("nimbusRed", 6.890595E-4f, -0.36665577f, 0.11764705f, 0);
        this.color9 = this.decodeColor("nimbusRed", -0.001021713f, 0.101804554f, -0.031372547f, 0);
        this.color10 = this.decodeColor("nimbusRed", -2.7342606E-4f, 0.13243341f, -0.035294116f, 0);
        this.color11 = this.decodeColor("nimbusRed", -2.7342606E-4f, 0.002258718f, 0.06666666f, 0);
        this.color12 = this.decodeColor("nimbusRed", 0.0056530247f, 0.0040003657f, -0.38431373f, -122);
        this.color13 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color14 = this.decodeColor("nimbusRed", -0.014814814f, 0.20118344f, -0.3882353f, 0);
        this.color15 = this.decodeColor("nimbusRed", -0.014814814f, 0.20118344f, -0.13333333f, 0);
        this.color16 = this.decodeColor("nimbusRed", 6.890595E-4f, -0.38929275f, 0.1607843f, 0);
        this.color17 = this.decodeColor("nimbusRed", 2.537202E-5f, 0.012294531f, 0.043137252f, 0);
        this.color18 = this.decodeColor("nimbusRed", -2.7342606E-4f, 0.033585668f, 0.039215684f, 0);
        this.color19 = this.decodeColor("nimbusRed", -2.7342606E-4f, -0.07198727f, 0.14117646f, 0);
        this.color20 = this.decodeColor("nimbusRed", -0.014814814f, 0.20118344f, 0.0039215684f, -122);
        this.color21 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, -140);
        this.color22 = this.decodeColor("nimbusRed", -0.014814814f, 0.20118344f, -0.49411768f, 0);
        this.color23 = this.decodeColor("nimbusRed", -0.014814814f, 0.20118344f, -0.20392159f, 0);
        this.color24 = this.decodeColor("nimbusRed", -0.014814814f, -0.21260965f, 0.019607842f, 0);
        this.color25 = this.decodeColor("nimbusRed", -0.014814814f, 0.17340565f, -0.09803921f, 0);
        this.color26 = this.decodeColor("nimbusRed", -0.014814814f, 0.20118344f, -0.10588235f, 0);
        this.color27 = this.decodeColor("nimbusRed", -0.014814814f, 0.20118344f, -0.04705882f, 0);
        this.color28 = this.decodeColor("nimbusRed", -0.014814814f, 0.20118344f, -0.31764707f, -122);
        this.color29 = this.decodeColor("nimbusRed", 0.5962963f, -0.6994788f, -0.07058823f, 0);
        this.color30 = this.decodeColor("nimbusRed", 0.5962963f, -0.66245294f, -0.23137257f, 0);
        this.color31 = this.decodeColor("nimbusRed", 0.58518517f, -0.77649516f, 0.21568626f, 0);
        this.color32 = this.decodeColor("nimbusRed", 0.5962963f, -0.7372781f, 0.10196078f, 0);
        this.color33 = this.decodeColor("nimbusRed", 0.5962963f, -0.73911506f, 0.12549019f, 0);
        this.color34 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.027957506f, -0.31764707f, 0);
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
                this.paintBackgroundMouseOver(graphics2D);
                break;
            }
            case 4: {
                this.paintBackgroundPressed(graphics2D);
                break;
            }
            case 5: {
                this.paintBackgroundEnabledAndWindowNotFocused(graphics2D);
                break;
            }
            case 6: {
                this.paintBackgroundMouseOverAndWindowNotFocused(graphics2D);
                break;
            }
            case 7: {
                this.paintBackgroundPressedAndWindowNotFocused(graphics2D);
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
        graphics2D.setPaint(this.decodeGradient1(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color3);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.color5);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color12);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundMouseOver(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.color5);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color20);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundPressed(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.color21);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color28);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundEnabledAndWindowNotFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient9(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color34);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundMouseOverAndWindowNotFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.color5);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect4();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color20);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.path);
    }
    
    private void paintBackgroundPressedAndWindowNotFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.color21);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color28);
        graphics2D.fill(this.path);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.color13);
        graphics2D.fill(this.path);
    }
    
    private RoundRectangle2D decodeRoundRect1() {
        this.roundRect.setRoundRect(this.decodeX(1.0f), this.decodeY(1.0f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(1.9444444f) - this.decodeY(1.0f), 8.600000381469727, 8.600000381469727);
        return this.roundRect;
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.25f), this.decodeY(1.7373737f));
        this.path.lineTo(this.decodeX(1.3002392f), this.decodeY(1.794192f));
        this.path.lineTo(this.decodeX(1.5047847f), this.decodeY(1.5909091f));
        this.path.lineTo(this.decodeX(1.6842105f), this.decodeY(1.7954545f));
        this.path.lineTo(this.decodeX(1.7595694f), this.decodeY(1.719697f));
        this.path.lineTo(this.decodeX(1.5956938f), this.decodeY(1.5239899f));
        this.path.lineTo(this.decodeX(1.7535884f), this.decodeY(1.3409091f));
        this.path.lineTo(this.decodeX(1.6830144f), this.decodeY(1.2537879f));
        this.path.lineTo(this.decodeX(1.5083733f), this.decodeY(1.4406565f));
        this.path.lineTo(this.decodeX(1.3301436f), this.decodeY(1.2563131f));
        this.path.lineTo(this.decodeX(1.257177f), this.decodeY(1.3320707f));
        this.path.lineTo(this.decodeX(1.4270334f), this.decodeY(1.5252526f));
        this.path.lineTo(this.decodeX(1.25f), this.decodeY(1.7373737f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.257177f), this.decodeY(1.2828283f));
        this.path.lineTo(this.decodeX(1.3217703f), this.decodeY(1.2133838f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(1.4040405f));
        this.path.lineTo(this.decodeX(1.673445f), this.decodeY(1.2108586f));
        this.path.lineTo(this.decodeX(1.7440192f), this.decodeY(1.2853535f));
        this.path.lineTo(this.decodeX(1.5669856f), this.decodeY(1.4709597f));
        this.path.lineTo(this.decodeX(1.7488039f), this.decodeY(1.6527778f));
        this.path.lineTo(this.decodeX(1.673445f), this.decodeY(1.7398989f));
        this.path.lineTo(this.decodeX(1.4988039f), this.decodeY(1.5416667f));
        this.path.lineTo(this.decodeX(1.3313397f), this.decodeY(1.7424242f));
        this.path.lineTo(this.decodeX(1.2523923f), this.decodeY(1.6565657f));
        this.path.lineTo(this.decodeX(1.4366028f), this.decodeY(1.4722222f));
        this.path.lineTo(this.decodeX(1.257177f), this.decodeY(1.2828283f));
        this.path.closePath();
        return this.path;
    }
    
    private RoundRectangle2D decodeRoundRect2() {
        this.roundRect.setRoundRect(this.decodeX(1.0f), this.decodeY(1.6111112f), this.decodeX(2.0f) - this.decodeX(1.0f), this.decodeY(2.0f) - this.decodeY(1.6111112f), 6.0, 6.0);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect3() {
        this.roundRect.setRoundRect(this.decodeX(1.0526316f), this.decodeY(1.0530303f), this.decodeX(1.9473684f) - this.decodeX(1.0526316f), this.decodeY(1.8863636f) - this.decodeY(1.0530303f), 6.75, 6.75);
        return this.roundRect;
    }
    
    private RoundRectangle2D decodeRoundRect4() {
        this.roundRect.setRoundRect(this.decodeX(1.0526316f), this.decodeY(1.0517677f), this.decodeX(1.9473684f) - this.decodeX(1.0526316f), this.decodeY(1.8851011f) - this.decodeY(1.0517677f), 6.75, 6.75);
        return this.roundRect;
    }
    
    private Paint decodeGradient1(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color1, this.decodeColor(this.color1, this.color2, 0.5f), this.color2 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color6, this.decodeColor(this.color6, this.color7, 0.5f), this.color7 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color8, this.decodeColor(this.color8, this.color9, 0.5f), this.color9, this.decodeColor(this.color9, this.color10, 0.5f), this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color14, this.decodeColor(this.color14, this.color15, 0.5f), this.color15 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.81480503f, 0.97904193f }, new Color[] { this.color16, this.decodeColor(this.color16, this.color17, 0.5f), this.color17, this.decodeColor(this.color17, this.color18, 0.5f), this.color18, this.decodeColor(this.color18, this.color19, 0.5f), this.color19 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color22, this.decodeColor(this.color22, this.color23, 0.5f), this.color23 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.81630206f, 0.98203593f }, new Color[] { this.color24, this.decodeColor(this.color24, this.color25, 0.5f), this.color25, this.decodeColor(this.color25, this.color26, 0.5f), this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color29, this.decodeColor(this.color29, this.color30, 0.5f), this.color30 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.24101797f, 0.48203593f, 0.5838324f, 0.6856288f, 0.8428144f, 1.0f }, new Color[] { this.color31, this.decodeColor(this.color31, this.color32, 0.5f), this.color32, this.decodeColor(this.color32, this.color32, 0.5f), this.color32, this.decodeColor(this.color32, this.color33, 0.5f), this.color33 });
    }
}
