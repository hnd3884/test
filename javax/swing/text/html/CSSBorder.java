package javax.swing.text.html;

import java.awt.Rectangle;
import java.util.HashMap;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Component;
import java.awt.Polygon;
import java.awt.Color;
import java.util.Map;
import javax.swing.text.AttributeSet;
import javax.swing.border.AbstractBorder;

class CSSBorder extends AbstractBorder
{
    static final int COLOR = 0;
    static final int STYLE = 1;
    static final int WIDTH = 2;
    static final int TOP = 0;
    static final int RIGHT = 1;
    static final int BOTTOM = 2;
    static final int LEFT = 3;
    static final CSS.Attribute[][] ATTRIBUTES;
    static final CSS.CssValue[] PARSERS;
    static final Object[] DEFAULTS;
    final AttributeSet attrs;
    static Map<CSS.Value, BorderPainter> borderPainters;
    
    CSSBorder(final AttributeSet attrs) {
        this.attrs = attrs;
    }
    
    private Color getBorderColor(final int n) {
        final Object attribute = this.attrs.getAttribute(CSSBorder.ATTRIBUTES[0][n]);
        CSS.ColorValue colorValue;
        if (attribute instanceof CSS.ColorValue) {
            colorValue = (CSS.ColorValue)attribute;
        }
        else {
            colorValue = (CSS.ColorValue)this.attrs.getAttribute(CSS.Attribute.COLOR);
            if (colorValue == null) {
                colorValue = (CSS.ColorValue)CSSBorder.PARSERS[0].parseCssValue(CSS.Attribute.COLOR.getDefaultValue());
            }
        }
        return colorValue.getValue();
    }
    
    private int getBorderWidth(final int n) {
        int n2 = 0;
        final CSS.BorderStyle borderStyle = (CSS.BorderStyle)this.attrs.getAttribute(CSSBorder.ATTRIBUTES[1][n]);
        if (borderStyle != null && borderStyle.getValue() != CSS.Value.NONE) {
            CSS.LengthValue lengthValue = (CSS.LengthValue)this.attrs.getAttribute(CSSBorder.ATTRIBUTES[2][n]);
            if (lengthValue == null) {
                lengthValue = (CSS.LengthValue)CSSBorder.DEFAULTS[2];
            }
            n2 = (int)lengthValue.getValue(true);
        }
        return n2;
    }
    
    private int[] getWidths() {
        final int[] array = new int[4];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.getBorderWidth(i);
        }
        return array;
    }
    
    private CSS.Value getBorderStyle(final int n) {
        CSS.BorderStyle borderStyle = (CSS.BorderStyle)this.attrs.getAttribute(CSSBorder.ATTRIBUTES[1][n]);
        if (borderStyle == null) {
            borderStyle = (CSS.BorderStyle)CSSBorder.DEFAULTS[1];
        }
        return borderStyle.getValue();
    }
    
    private Polygon getBorderShape(final int n) {
        Polygon polygon = null;
        final int[] widths = this.getWidths();
        if (widths[n] != 0) {
            polygon = new Polygon(new int[4], new int[4], 0);
            polygon.addPoint(0, 0);
            polygon.addPoint(-widths[(n + 3) % 4], -widths[n]);
            polygon.addPoint(widths[(n + 1) % 4], -widths[n]);
            polygon.addPoint(0, 0);
        }
        return polygon;
    }
    
    private BorderPainter getBorderPainter(final int n) {
        return CSSBorder.borderPainters.get(this.getBorderStyle(n));
    }
    
    static Color getAdjustedColor(final Color color, final double n) {
        final double n2 = 1.0 - Math.min(Math.abs(n), 1.0);
        final double n3 = (n > 0.0) ? (255.0 * (1.0 - n2)) : 0.0;
        return new Color((int)(color.getRed() * n2 + n3), (int)(color.getGreen() * n2 + n3), (int)(color.getBlue() * n2 + n3));
    }
    
    @Override
    public Insets getBorderInsets(final Component component, final Insets insets) {
        final int[] widths = this.getWidths();
        insets.set(widths[0], widths[3], widths[2], widths[1]);
        return insets;
    }
    
    @Override
    public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        if (!(graphics instanceof Graphics2D)) {
            return;
        }
        final Graphics2D graphics2D = (Graphics2D)graphics.create();
        final int[] widths = this.getWidths();
        final int n5 = n + widths[3];
        final int n6 = n2 + widths[0];
        final int n7 = n3 - (widths[1] + widths[3]);
        final int n8 = n4 - (widths[0] + widths[2]);
        final int[][] array = { { n5, n6 }, { n5 + n7, n6 }, { n5 + n7, n6 + n8 }, { n5, n6 + n8 } };
        for (int i = 0; i < 4; ++i) {
            final CSS.Value borderStyle = this.getBorderStyle(i);
            final Polygon borderShape = this.getBorderShape(i);
            if (borderStyle != CSS.Value.NONE && borderShape != null) {
                final int n9 = (i % 2 == 0) ? n7 : n8;
                final int[] xpoints = borderShape.xpoints;
                final int n10 = 2;
                xpoints[n10] += n9;
                final int[] xpoints2 = borderShape.xpoints;
                final int n11 = 3;
                xpoints2[n11] += n9;
                final Color borderColor = this.getBorderColor(i);
                final BorderPainter borderPainter = this.getBorderPainter(i);
                final double n12 = i * 3.141592653589793 / 2.0;
                graphics2D.setClip(graphics.getClip());
                graphics2D.translate(array[i][0], array[i][1]);
                graphics2D.rotate(n12);
                graphics2D.clip(borderShape);
                borderPainter.paint(borderShape, graphics2D, borderColor, i);
                graphics2D.rotate(-n12);
                graphics2D.translate(-array[i][0], -array[i][1]);
            }
        }
        graphics2D.dispose();
    }
    
    static void registerBorderPainter(final CSS.Value value, final BorderPainter borderPainter) {
        CSSBorder.borderPainters.put(value, borderPainter);
    }
    
    static {
        ATTRIBUTES = new CSS.Attribute[][] { { CSS.Attribute.BORDER_TOP_COLOR, CSS.Attribute.BORDER_RIGHT_COLOR, CSS.Attribute.BORDER_BOTTOM_COLOR, CSS.Attribute.BORDER_LEFT_COLOR }, { CSS.Attribute.BORDER_TOP_STYLE, CSS.Attribute.BORDER_RIGHT_STYLE, CSS.Attribute.BORDER_BOTTOM_STYLE, CSS.Attribute.BORDER_LEFT_STYLE }, { CSS.Attribute.BORDER_TOP_WIDTH, CSS.Attribute.BORDER_RIGHT_WIDTH, CSS.Attribute.BORDER_BOTTOM_WIDTH, CSS.Attribute.BORDER_LEFT_WIDTH } };
        PARSERS = new CSS.CssValue[] { new CSS.ColorValue(), new CSS.BorderStyle(), new CSS.BorderWidthValue(null, 0) };
        DEFAULTS = new Object[] { CSS.Attribute.BORDER_COLOR, CSSBorder.PARSERS[1].parseCssValue(CSS.Attribute.BORDER_STYLE.getDefaultValue()), CSSBorder.PARSERS[2].parseCssValue(CSS.Attribute.BORDER_WIDTH.getDefaultValue()) };
        CSSBorder.borderPainters = new HashMap<CSS.Value, BorderPainter>();
        registerBorderPainter(CSS.Value.NONE, new NullPainter());
        registerBorderPainter(CSS.Value.HIDDEN, new NullPainter());
        registerBorderPainter(CSS.Value.SOLID, new SolidPainter());
        registerBorderPainter(CSS.Value.DOUBLE, new DoublePainter());
        registerBorderPainter(CSS.Value.DOTTED, new DottedDashedPainter(1));
        registerBorderPainter(CSS.Value.DASHED, new DottedDashedPainter(3));
        registerBorderPainter(CSS.Value.GROOVE, new GrooveRidgePainter(CSS.Value.GROOVE));
        registerBorderPainter(CSS.Value.RIDGE, new GrooveRidgePainter(CSS.Value.RIDGE));
        registerBorderPainter(CSS.Value.INSET, new InsetOutsetPainter(CSS.Value.INSET));
        registerBorderPainter(CSS.Value.OUTSET, new InsetOutsetPainter(CSS.Value.OUTSET));
    }
    
    static class NullPainter implements BorderPainter
    {
        @Override
        public void paint(final Polygon polygon, final Graphics graphics, final Color color, final int n) {
        }
    }
    
    static class SolidPainter implements BorderPainter
    {
        @Override
        public void paint(final Polygon polygon, final Graphics graphics, final Color color, final int n) {
            graphics.setColor(color);
            graphics.fillPolygon(polygon);
        }
    }
    
    abstract static class StrokePainter implements BorderPainter
    {
        void paintStrokes(final Rectangle rectangle, final Graphics graphics, final int n, final int[] array, final Color[] array2) {
            final boolean b = n == 0;
            int i = 0;
            final int n2 = b ? rectangle.width : rectangle.height;
            while (i < n2) {
                int n4;
                for (int n3 = 0; n3 < array.length && i < n2; i += n4, ++n3) {
                    n4 = array[n3];
                    final Color color = array2[n3];
                    if (color != null) {
                        final int n5 = rectangle.x + (b ? i : 0);
                        final int n6 = rectangle.y + (b ? 0 : i);
                        final int n7 = b ? n4 : rectangle.width;
                        final int n8 = b ? rectangle.height : n4;
                        graphics.setColor(color);
                        graphics.fillRect(n5, n6, n7, n8);
                    }
                }
            }
        }
    }
    
    static class DoublePainter extends StrokePainter
    {
        @Override
        public void paint(final Polygon polygon, final Graphics graphics, final Color color, final int n) {
            final Rectangle bounds = polygon.getBounds();
            final int max = Math.max(bounds.height / 3, 1);
            this.paintStrokes(bounds, graphics, 1, new int[] { max, max }, new Color[] { color, null });
        }
    }
    
    static class DottedDashedPainter extends StrokePainter
    {
        final int factor;
        
        DottedDashedPainter(final int factor) {
            this.factor = factor;
        }
        
        @Override
        public void paint(final Polygon polygon, final Graphics graphics, final Color color, final int n) {
            final Rectangle bounds = polygon.getBounds();
            final int n2 = bounds.height * this.factor;
            this.paintStrokes(bounds, graphics, 0, new int[] { n2, n2 }, new Color[] { color, null });
        }
    }
    
    abstract static class ShadowLightPainter extends StrokePainter
    {
        static Color getShadowColor(final Color color) {
            return CSSBorder.getAdjustedColor(color, -0.3);
        }
        
        static Color getLightColor(final Color color) {
            return CSSBorder.getAdjustedColor(color, 0.7);
        }
    }
    
    static class GrooveRidgePainter extends ShadowLightPainter
    {
        final CSS.Value type;
        
        GrooveRidgePainter(final CSS.Value type) {
            this.type = type;
        }
        
        @Override
        public void paint(final Polygon polygon, final Graphics graphics, final Color color, final int n) {
            final Rectangle bounds = polygon.getBounds();
            final int max = Math.max(bounds.height / 2, 1);
            this.paintStrokes(bounds, graphics, 1, new int[] { max, max }, ((n + 1) % 4 < 2 == (this.type == CSS.Value.GROOVE)) ? new Color[] { ShadowLightPainter.getShadowColor(color), ShadowLightPainter.getLightColor(color) } : new Color[] { ShadowLightPainter.getLightColor(color), ShadowLightPainter.getShadowColor(color) });
        }
    }
    
    static class InsetOutsetPainter extends ShadowLightPainter
    {
        CSS.Value type;
        
        InsetOutsetPainter(final CSS.Value type) {
            this.type = type;
        }
        
        @Override
        public void paint(final Polygon polygon, final Graphics graphics, final Color color, final int n) {
            graphics.setColor(((n + 1) % 4 < 2 == (this.type == CSS.Value.INSET)) ? ShadowLightPainter.getShadowColor(color) : ShadowLightPainter.getLightColor(color));
            graphics.fillPolygon(polygon);
        }
    }
    
    interface BorderPainter
    {
        void paint(final Polygon p0, final Graphics p1, final Color p2, final int p3);
    }
}
