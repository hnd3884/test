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

final class InternalFrameTitlePaneIconifyButtonPainter extends AbstractRegionPainter
{
    static final int BACKGROUND_ENABLED = 1;
    static final int BACKGROUND_DISABLED = 2;
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
    private Color color35;
    private Color color36;
    private Color color37;
    private Object[] componentColors;
    
    public InternalFrameTitlePaneIconifyButtonPainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", 0.0055555105f, -0.0029994324f, -0.38039216f, -185);
        this.color2 = this.decodeColor("nimbusOrange", -0.08377897f, 0.02094239f, -0.40392157f, 0);
        this.color3 = this.decodeColor("nimbusOrange", 0.0f, 0.0f, 0.0f, 0);
        this.color4 = this.decodeColor("nimbusOrange", -4.4563413E-4f, -0.48364475f, 0.10588235f, 0);
        this.color5 = this.decodeColor("nimbusOrange", 0.0f, -0.0050992966f, 0.0039215684f, 0);
        this.color6 = this.decodeColor("nimbusOrange", 0.0f, -0.12125945f, 0.10588235f, 0);
        this.color7 = this.decodeColor("nimbusOrange", -0.08377897f, 0.02094239f, -0.40392157f, -106);
        this.color8 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color9 = this.decodeColor("nimbusOrange", 0.5203877f, -0.9376068f, 0.007843137f, 0);
        this.color10 = this.decodeColor("nimbusOrange", 0.5273321f, -0.8903002f, -0.086274505f, 0);
        this.color11 = this.decodeColor("nimbusOrange", 0.5273321f, -0.93313926f, 0.019607842f, 0);
        this.color12 = this.decodeColor("nimbusOrange", 0.53526866f, -0.8995122f, -0.058823526f, 0);
        this.color13 = this.decodeColor("nimbusOrange", 0.5233639f, -0.8971863f, -0.07843137f, 0);
        this.color14 = this.decodeColor("nimbusBlueGrey", -0.0808081f, 0.015910469f, -0.40392157f, -216);
        this.color15 = this.decodeColor("nimbusBlueGrey", -0.003968239f, -0.03760965f, 0.007843137f, 0);
        this.color16 = new Color(255, 200, 0, 255);
        this.color17 = this.decodeColor("nimbusOrange", -0.08377897f, 0.02094239f, -0.31764707f, 0);
        this.color18 = this.decodeColor("nimbusOrange", -0.02758849f, 0.02094239f, -0.062745094f, 0);
        this.color19 = this.decodeColor("nimbusOrange", -4.4563413E-4f, -0.5074419f, 0.1490196f, 0);
        this.color20 = this.decodeColor("nimbusOrange", 9.745359E-6f, -0.11175901f, 0.07843137f, 0);
        this.color21 = this.decodeColor("nimbusOrange", 0.0f, -0.09280169f, 0.07843137f, 0);
        this.color22 = this.decodeColor("nimbusOrange", 0.0f, -0.19002807f, 0.18039215f, 0);
        this.color23 = this.decodeColor("nimbusOrange", -0.025772434f, 0.02094239f, 0.05098039f, 0);
        this.color24 = this.decodeColor("nimbusOrange", -0.08377897f, 0.02094239f, -0.4f, 0);
        this.color25 = this.decodeColor("nimbusOrange", -0.053104125f, 0.02094239f, -0.109803915f, 0);
        this.color26 = this.decodeColor("nimbusOrange", -0.017887495f, -0.33726656f, 0.039215684f, 0);
        this.color27 = this.decodeColor("nimbusOrange", -0.018038228f, 0.02094239f, -0.043137252f, 0);
        this.color28 = this.decodeColor("nimbusOrange", -0.015844189f, 0.02094239f, -0.027450979f, 0);
        this.color29 = this.decodeColor("nimbusOrange", -0.010274701f, 0.02094239f, 0.015686274f, 0);
        this.color30 = this.decodeColor("nimbusOrange", -0.08377897f, 0.02094239f, -0.14509803f, -91);
        this.color31 = this.decodeColor("nimbusOrange", 0.5273321f, -0.87971985f, -0.15686274f, 0);
        this.color32 = this.decodeColor("nimbusOrange", 0.5273321f, -0.842694f, -0.31764707f, 0);
        this.color33 = this.decodeColor("nimbusOrange", 0.516221f, -0.9567362f, 0.12941176f, 0);
        this.color34 = this.decodeColor("nimbusOrange", 0.5222816f, -0.9229352f, 0.019607842f, 0);
        this.color35 = this.decodeColor("nimbusOrange", 0.5273321f, -0.91751915f, 0.015686274f, 0);
        this.color36 = this.decodeColor("nimbusOrange", 0.5273321f, -0.9193561f, 0.039215684f, 0);
        this.color37 = this.decodeColor("nimbusBlueGrey", -0.01111114f, -0.017933726f, -0.32156864f, 0);
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
                this.paintBackgroundDisabled(graphics2D);
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
    
    private void paintBackgroundEnabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient1(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient2(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color7);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color8);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundDisabled(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient3(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient4(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color14);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color15);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundMouseOver(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color23);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color8);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundPressed(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color30);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color8);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundEnabledAndWindowNotFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient9(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient10(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color14);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color37);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundMouseOverAndWindowNotFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient5(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient6(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color23);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color8);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
    }
    
    private void paintBackgroundPressedAndWindowNotFocused(final Graphics2D graphics2D) {
        this.roundRect = this.decodeRoundRect1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect2();
        graphics2D.setPaint(this.decodeGradient7(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.roundRect = this.decodeRoundRect3();
        graphics2D.setPaint(this.decodeGradient8(this.roundRect));
        graphics2D.fill(this.roundRect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color30);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color8);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
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
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(1.25f), this.decodeY(1.6628788f), this.decodeX(1.75f) - this.decodeX(1.25f), this.decodeY(1.7487373f) - this.decodeY(1.6628788f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(1.2870814f), this.decodeY(1.6123737f), this.decodeX(1.7165072f) - this.decodeX(1.2870814f), this.decodeY(1.7222222f) - this.decodeY(1.6123737f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect3() {
        this.rect.setRect(this.decodeX(1.0f), this.decodeY(1.0f), this.decodeX(1.0f) - this.decodeX(1.0f), this.decodeY(1.0f) - this.decodeY(1.0f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect4() {
        this.rect.setRect(this.decodeX(1.25f), this.decodeY(1.6527778f), this.decodeX(1.7511961f) - this.decodeX(1.25f), this.decodeY(1.7828283f) - this.decodeY(1.6527778f));
        return this.rect;
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
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color4, this.decodeColor(this.color4, this.color3, 0.5f), this.color3, this.decodeColor(this.color3, this.color5, 0.5f), this.color5, this.decodeColor(this.color5, this.color6, 0.5f), this.color6 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color9, this.decodeColor(this.color9, this.color10, 0.5f), this.color10 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color11, this.decodeColor(this.color11, this.color12, 0.5f), this.color12, this.decodeColor(this.color12, this.color13, 0.5f), this.color13, this.decodeColor(this.color13, this.color10, 0.5f), this.color10 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color17, this.decodeColor(this.color17, this.color18, 0.5f), this.color18 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color19, this.decodeColor(this.color19, this.color20, 0.5f), this.color20, this.decodeColor(this.color20, this.color21, 0.5f), this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color24, this.decodeColor(this.color24, this.color25, 0.5f), this.color25 });
    }
    
    private Paint decodeGradient8(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.8252841f, 1.0f }, new Color[] { this.color26, this.decodeColor(this.color26, this.color27, 0.5f), this.color27, this.decodeColor(this.color27, this.color28, 0.5f), this.color28, this.decodeColor(this.color28, this.color29, 0.5f), this.color29 });
    }
    
    private Paint decodeGradient9(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.24868421f * n3 + n, 0.0014705883f * n4 + n2, 0.24868421f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color31, this.decodeColor(this.color31, this.color32, 0.5f), this.color32 });
    }
    
    private Paint decodeGradient10(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.25f * n3 + n, 0.0f * n4 + n2, 0.25441176f * n3 + n, 1.0016667f * n4 + n2, new float[] { 0.0f, 0.26988637f, 0.53977275f, 0.5951705f, 0.6505682f, 0.78336793f, 0.9161677f }, new Color[] { this.color33, this.decodeColor(this.color33, this.color34, 0.5f), this.color34, this.decodeColor(this.color34, this.color35, 0.5f), this.color35, this.decodeColor(this.color35, this.color36, 0.5f), this.color36 });
    }
}
