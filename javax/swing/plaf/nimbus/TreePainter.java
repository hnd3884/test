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

final class TreePainter extends AbstractRegionPainter
{
    static final int BACKGROUND_DISABLED = 1;
    static final int BACKGROUND_ENABLED = 2;
    static final int BACKGROUND_ENABLED_SELECTED = 3;
    static final int LEAFICON_ENABLED = 4;
    static final int CLOSEDICON_ENABLED = 5;
    static final int OPENICON_ENABLED = 6;
    static final int COLLAPSEDICON_ENABLED = 7;
    static final int COLLAPSEDICON_ENABLED_SELECTED = 8;
    static final int EXPANDEDICON_ENABLED = 9;
    static final int EXPANDEDICON_ENABLED_SELECTED = 10;
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
    private Object[] componentColors;
    
    public TreePainter(final PaintContext ctx, final int state) {
        this.path = new Path2D.Float();
        this.rect = new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.roundRect = new RoundRectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        this.ellipse = new Ellipse2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
        this.color1 = this.decodeColor("nimbusBlueGrey", 0.007936537f, -0.065654516f, -0.13333333f, 0);
        this.color2 = new Color(97, 98, 102, 255);
        this.color3 = this.decodeColor("nimbusBlueGrey", -0.032679737f, -0.043332636f, 0.24705881f, 0);
        this.color4 = this.decodeColor("nimbusBlueGrey", 0.0f, -0.110526316f, 0.25490195f, 0);
        this.color5 = this.decodeColor("nimbusBase", 0.0077680945f, -0.51781034f, 0.3490196f, 0);
        this.color6 = this.decodeColor("nimbusBase", 0.013940871f, -0.599277f, 0.41960782f, 0);
        this.color7 = this.decodeColor("nimbusBase", 0.004681647f, -0.4198052f, 0.14117646f, 0);
        this.color8 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, -127);
        this.color9 = this.decodeColor("nimbusBlueGrey", 0.0f, 0.0f, -0.21f, -99);
        this.color10 = this.decodeColor("nimbusBase", 2.9569864E-4f, -0.45978838f, 0.2980392f, 0);
        this.color11 = this.decodeColor("nimbusBase", 0.0015952587f, -0.34848025f, 0.18823528f, 0);
        this.color12 = this.decodeColor("nimbusBase", 0.0015952587f, -0.30844158f, 0.09803921f, 0);
        this.color13 = this.decodeColor("nimbusBase", 0.0015952587f, -0.27329817f, 0.035294116f, 0);
        this.color14 = this.decodeColor("nimbusBase", 0.004681647f, -0.6198413f, 0.43921566f, 0);
        this.color15 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, -125);
        this.color16 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, -50);
        this.color17 = this.decodeColor("nimbusBase", 0.0f, -0.6357143f, 0.45098037f, -100);
        this.color18 = this.decodeColor("nimbusBase", 0.0012094378f, -0.23571429f, -0.0784314f, 0);
        this.color19 = this.decodeColor("nimbusBase", 2.9569864E-4f, -0.115166366f, -0.2627451f, 0);
        this.color20 = this.decodeColor("nimbusBase", 0.0027436614f, -0.335015f, 0.011764705f, 0);
        this.color21 = this.decodeColor("nimbusBase", 0.0024294257f, -0.3857143f, 0.031372547f, 0);
        this.color22 = this.decodeColor("nimbusBase", 0.0018081069f, -0.3595238f, -0.13725492f, 0);
        this.color23 = new Color(255, 200, 0, 255);
        this.color24 = this.decodeColor("nimbusBase", 0.004681647f, -0.33496243f, -0.027450979f, 0);
        this.color25 = this.decodeColor("nimbusBase", 0.0019934773f, -0.361378f, -0.10588238f, 0);
        this.color26 = this.decodeColor("nimbusBlueGrey", -0.6111111f, -0.110526316f, -0.34509805f, 0);
        this.state = state;
        this.ctx = ctx;
    }
    
    @Override
    protected void doPaint(final Graphics2D graphics2D, final JComponent component, final int n, final int n2, final Object[] componentColors) {
        this.componentColors = componentColors;
        switch (this.state) {
            case 4: {
                this.paintleafIconEnabled(graphics2D);
                break;
            }
            case 5: {
                this.paintclosedIconEnabled(graphics2D);
                break;
            }
            case 6: {
                this.paintopenIconEnabled(graphics2D);
                break;
            }
            case 7: {
                this.paintcollapsedIconEnabled(graphics2D);
                break;
            }
            case 8: {
                this.paintcollapsedIconEnabledAndSelected(graphics2D);
                break;
            }
            case 9: {
                this.paintexpandedIconEnabled(graphics2D);
                break;
            }
            case 10: {
                this.paintexpandedIconEnabledAndSelected(graphics2D);
                break;
            }
        }
    }
    
    @Override
    protected final PaintContext getPaintContext() {
        return this.ctx;
    }
    
    private void paintleafIconEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath1();
        graphics2D.setPaint(this.color1);
        graphics2D.fill(this.path);
        this.rect = this.decodeRect1();
        graphics2D.setPaint(this.color2);
        graphics2D.fill(this.rect);
        this.path = this.decodePath2();
        graphics2D.setPaint(this.decodeGradient1(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath3();
        graphics2D.setPaint(this.decodeGradient2(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath4();
        graphics2D.setPaint(this.color7);
        graphics2D.fill(this.path);
        this.path = this.decodePath5();
        graphics2D.setPaint(this.color8);
        graphics2D.fill(this.path);
    }
    
    private void paintclosedIconEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath6();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.path);
        this.path = this.decodePath7();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath8();
        graphics2D.setPaint(this.decodeGradient4(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color15);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color17);
        graphics2D.fill(this.rect);
        this.path = this.decodePath9();
        graphics2D.setPaint(this.decodeGradient5(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath10();
        graphics2D.setPaint(this.decodeGradient6(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath11();
        graphics2D.setPaint(this.color23);
        graphics2D.fill(this.path);
    }
    
    private void paintopenIconEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath6();
        graphics2D.setPaint(this.color9);
        graphics2D.fill(this.path);
        this.path = this.decodePath12();
        graphics2D.setPaint(this.decodeGradient3(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath13();
        graphics2D.setPaint(this.decodeGradient4(this.path));
        graphics2D.fill(this.path);
        this.rect = this.decodeRect2();
        graphics2D.setPaint(this.color15);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect3();
        graphics2D.setPaint(this.color16);
        graphics2D.fill(this.rect);
        this.rect = this.decodeRect4();
        graphics2D.setPaint(this.color17);
        graphics2D.fill(this.rect);
        this.path = this.decodePath14();
        graphics2D.setPaint(this.decodeGradient5(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath15();
        graphics2D.setPaint(this.decodeGradient7(this.path));
        graphics2D.fill(this.path);
        this.path = this.decodePath11();
        graphics2D.setPaint(this.color23);
        graphics2D.fill(this.path);
    }
    
    private void paintcollapsedIconEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath16();
        graphics2D.setPaint(this.color26);
        graphics2D.fill(this.path);
    }
    
    private void paintcollapsedIconEnabledAndSelected(final Graphics2D graphics2D) {
        this.path = this.decodePath16();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.path);
    }
    
    private void paintexpandedIconEnabled(final Graphics2D graphics2D) {
        this.path = this.decodePath17();
        graphics2D.setPaint(this.color26);
        graphics2D.fill(this.path);
    }
    
    private void paintexpandedIconEnabledAndSelected(final Graphics2D graphics2D) {
        this.path = this.decodePath17();
        graphics2D.setPaint(this.color4);
        graphics2D.fill(this.path);
    }
    
    private Path2D decodePath1() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.2f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.9197531f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(0.9f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(0.88888896f));
        this.path.lineTo(this.decodeX(1.9537036f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect1() {
        this.rect.setRect(this.decodeX(0.4f), this.decodeY(2.8f), this.decodeX(2.6f) - this.decodeX(0.4f), this.decodeY(3.0f) - this.decodeY(2.8f));
        return this.rect;
    }
    
    private Path2D decodePath2() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath3() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath4() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.6234567f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(1.6296296f), this.decodeY(1.2037038f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.2006173f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath5() {
        this.path.reset();
        this.path.moveTo(this.decodeX(1.8333333f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(1.8333333f), this.decodeY(0.4f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath6() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(2.4f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(2.4f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.4f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath7() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6037037f), this.decodeY(1.8425925f));
        this.path.lineTo(this.decodeX(0.8f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.6f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath8() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.2f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.40833336f), this.decodeY(1.8645833f));
        this.path.lineTo(this.decodeX(0.79583335f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(2.6f));
        this.path.closePath();
        return this.path;
    }
    
    private Rectangle2D decodeRect2() {
        this.rect.setRect(this.decodeX(0.2f), this.decodeY(0.6f), this.decodeX(0.4f) - this.decodeX(0.2f), this.decodeY(0.8f) - this.decodeY(0.6f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect3() {
        this.rect.setRect(this.decodeX(0.6f), this.decodeY(0.2f), this.decodeX(1.3333334f) - this.decodeX(0.6f), this.decodeY(0.4f) - this.decodeY(0.2f));
        return this.rect;
    }
    
    private Rectangle2D decodeRect4() {
        this.rect.setRect(this.decodeX(1.5f), this.decodeY(0.6f), this.decodeX(2.4f) - this.decodeX(1.5f), this.decodeY(0.8f) - this.decodeY(0.6f));
        return this.rect;
    }
    
    private Path2D decodePath9() {
        this.path.reset();
        this.path.moveTo(this.decodeX(3.0f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.5888889f), this.decodeY(0.20370372f));
        this.path.lineTo(this.decodeX(0.5962963f), this.decodeY(0.34814817f));
        this.path.lineTo(this.decodeX(0.34814817f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(2.774074f), this.decodeY(1.1604939f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.8925927f), this.decodeY(1.1882716f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.65185183f));
        this.path.lineTo(this.decodeX(0.63703704f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(1.5925925f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(0.8f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath10() {
        this.path.reset();
        this.path.moveTo(this.decodeX(2.4f), this.decodeY(1.0f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(0.74814814f), this.decodeY(0.8f));
        this.path.lineTo(this.decodeX(0.4037037f), this.decodeY(1.8425925f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.5925926f), this.decodeY(2.225926f));
        this.path.lineTo(this.decodeX(0.916f), this.decodeY(0.996f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath11() {
        this.path.reset();
        this.path.moveTo(this.decodeX(2.2f), this.decodeY(2.2f));
        this.path.lineTo(this.decodeX(2.2f), this.decodeY(2.2f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath12() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.2f));
        this.path.lineTo(this.decodeX(0.8f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.6666667f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.6f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath13() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.2f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(0.8f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(2.6f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath14() {
        this.path.reset();
        this.path.moveTo(this.decodeX(3.0f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.5f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.2f));
        this.path.lineTo(this.decodeX(0.5888889f), this.decodeY(0.20370372f));
        this.path.lineTo(this.decodeX(0.5962963f), this.decodeY(0.34814817f));
        this.path.lineTo(this.decodeX(0.34814817f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.8333333f));
        this.path.lineTo(this.decodeX(2.916f), this.decodeY(1.3533334f));
        this.path.lineTo(this.decodeX(2.98f), this.decodeY(1.3766667f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(1.8333333f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(2.8f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.2f), this.decodeY(2.8f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.65185183f));
        this.path.lineTo(this.decodeX(0.63703704f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(1.3333334f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(1.5925925f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(0.4f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(0.6f));
        this.path.lineTo(this.decodeX(2.6f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(3.0f), this.decodeY(1.1666666f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath15() {
        this.path.reset();
        this.path.moveTo(this.decodeX(2.4f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(0.74f), this.decodeY(1.1666666f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.0f));
        this.path.lineTo(this.decodeX(0.4f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.6f), this.decodeY(2.6f));
        this.path.lineTo(this.decodeX(0.5925926f), this.decodeY(2.225926f));
        this.path.lineTo(this.decodeX(0.8f), this.decodeY(1.3333334f));
        this.path.lineTo(this.decodeX(2.4f), this.decodeY(1.3333334f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath16() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(1.2397541f), this.decodeY(0.70163935f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(3.0f));
        this.path.lineTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.closePath();
        return this.path;
    }
    
    private Path2D decodePath17() {
        this.path.reset();
        this.path.moveTo(this.decodeX(0.0f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(1.25f), this.decodeY(0.0f));
        this.path.lineTo(this.decodeX(0.70819676f), this.decodeY(2.9901638f));
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
        return this.decodeGradient(0.046296295f * n3 + n, 0.9675926f * n4 + n2, 0.4861111f * n3 + n, 0.5324074f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color3, this.decodeColor(this.color3, this.color4, 0.5f), this.color4 });
    }
    
    private Paint decodeGradient2(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color5, this.decodeColor(this.color5, this.color6, 0.5f), this.color6 });
    }
    
    private Paint decodeGradient3(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.04191617f, 0.10329342f, 0.16467066f, 0.24550897f, 0.3263473f, 0.6631737f, 1.0f }, new Color[] { this.color10, this.decodeColor(this.color10, this.color11, 0.5f), this.color11, this.decodeColor(this.color11, this.color12, 0.5f), this.color12, this.decodeColor(this.color12, this.color13, 0.5f), this.color13 });
    }
    
    private Paint decodeGradient4(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color5, this.decodeColor(this.color5, this.color14, 0.5f), this.color14 });
    }
    
    private Paint decodeGradient5(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color18, this.decodeColor(this.color18, this.color19, 0.5f), this.color19 });
    }
    
    private Paint decodeGradient6(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.12724552f, 0.25449103f, 0.62724555f, 1.0f }, new Color[] { this.color20, this.decodeColor(this.color20, this.color21, 0.5f), this.color21, this.decodeColor(this.color21, this.color22, 0.5f), this.color22 });
    }
    
    private Paint decodeGradient7(final Shape shape) {
        final Rectangle2D bounds2D = shape.getBounds2D();
        final float n = (float)bounds2D.getX();
        final float n2 = (float)bounds2D.getY();
        final float n3 = (float)bounds2D.getWidth();
        final float n4 = (float)bounds2D.getHeight();
        return this.decodeGradient(0.5f * n3 + n, 0.0f * n4 + n2, 0.5f * n3 + n, 1.0f * n4 + n2, new float[] { 0.0f, 0.5f, 1.0f }, new Color[] { this.color24, this.decodeColor(this.color24, this.color25, 0.5f), this.color25 });
    }
}
