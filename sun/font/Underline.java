package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.font.TextAttribute;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.util.concurrent.ConcurrentHashMap;

abstract class Underline
{
    private static final float DEFAULT_THICKNESS = 1.0f;
    private static final boolean USE_THICKNESS = true;
    private static final boolean IGNORE_THICKNESS = false;
    private static final ConcurrentHashMap<Object, Underline> UNDERLINES;
    private static final Underline[] UNDERLINE_LIST;
    
    abstract void drawUnderline(final Graphics2D p0, final float p1, final float p2, final float p3, final float p4);
    
    abstract float getLowerDrawLimit(final float p0);
    
    abstract Shape getUnderlineShape(final float p0, final float p1, final float p2, final float p3);
    
    static Underline getUnderline(final Object o) {
        if (o == null) {
            return null;
        }
        return Underline.UNDERLINES.get(o);
    }
    
    static Underline getUnderline(final int n) {
        return (n < 0) ? null : Underline.UNDERLINE_LIST[n];
    }
    
    static {
        UNDERLINES = new ConcurrentHashMap<Object, Underline>(6);
        final Underline[] underline_LIST = new Underline[6];
        underline_LIST[0] = new StandardUnderline(0.0f, 1.0f, null, true);
        Underline.UNDERLINES.put(TextAttribute.UNDERLINE_ON, underline_LIST[0]);
        underline_LIST[1] = new StandardUnderline(1.0f, 1.0f, null, false);
        Underline.UNDERLINES.put(TextAttribute.UNDERLINE_LOW_ONE_PIXEL, underline_LIST[1]);
        underline_LIST[2] = new StandardUnderline(1.0f, 2.0f, null, false);
        Underline.UNDERLINES.put(TextAttribute.UNDERLINE_LOW_TWO_PIXEL, underline_LIST[2]);
        underline_LIST[3] = new StandardUnderline(1.0f, 1.0f, new float[] { 1.0f, 1.0f }, false);
        Underline.UNDERLINES.put(TextAttribute.UNDERLINE_LOW_DOTTED, underline_LIST[3]);
        underline_LIST[4] = new IMGrayUnderline();
        Underline.UNDERLINES.put(TextAttribute.UNDERLINE_LOW_GRAY, underline_LIST[4]);
        underline_LIST[5] = new StandardUnderline(1.0f, 1.0f, new float[] { 4.0f, 4.0f }, false);
        Underline.UNDERLINES.put(TextAttribute.UNDERLINE_LOW_DASHED, underline_LIST[5]);
        UNDERLINE_LIST = underline_LIST;
    }
    
    private static final class StandardUnderline extends Underline
    {
        private float shift;
        private float thicknessMultiplier;
        private float[] dashPattern;
        private boolean useThickness;
        private BasicStroke cachedStroke;
        
        StandardUnderline(final float shift, final float thicknessMultiplier, final float[] dashPattern, final boolean useThickness) {
            this.shift = shift;
            this.thicknessMultiplier = thicknessMultiplier;
            this.dashPattern = dashPattern;
            this.useThickness = useThickness;
            this.cachedStroke = null;
        }
        
        private BasicStroke createStroke(final float n) {
            if (this.dashPattern == null) {
                return new BasicStroke(n, 0, 0);
            }
            return new BasicStroke(n, 0, 0, 10.0f, this.dashPattern, 0.0f);
        }
        
        private float getLineThickness(final float n) {
            if (this.useThickness) {
                return n * this.thicknessMultiplier;
            }
            return 1.0f * this.thicknessMultiplier;
        }
        
        private Stroke getStroke(final float n) {
            final float lineThickness = this.getLineThickness(n);
            BasicStroke cachedStroke = this.cachedStroke;
            if (cachedStroke == null || cachedStroke.getLineWidth() != lineThickness) {
                cachedStroke = this.createStroke(lineThickness);
                this.cachedStroke = cachedStroke;
            }
            return cachedStroke;
        }
        
        @Override
        void drawUnderline(final Graphics2D graphics2D, final float n, final float n2, final float n3, final float n4) {
            final Stroke stroke = graphics2D.getStroke();
            graphics2D.setStroke(this.getStroke(n));
            graphics2D.draw(new Line2D.Float(n2, n4 + this.shift, n3, n4 + this.shift));
            graphics2D.setStroke(stroke);
        }
        
        @Override
        float getLowerDrawLimit(final float n) {
            return this.shift + this.getLineThickness(n);
        }
        
        @Override
        Shape getUnderlineShape(final float n, final float n2, final float n3, final float n4) {
            return this.getStroke(n).createStrokedShape(new Line2D.Float(n2, n4 + this.shift, n3, n4 + this.shift));
        }
    }
    
    private static class IMGrayUnderline extends Underline
    {
        private BasicStroke stroke;
        
        IMGrayUnderline() {
            this.stroke = new BasicStroke(1.0f, 0, 0, 10.0f, new float[] { 1.0f, 1.0f }, 0.0f);
        }
        
        @Override
        void drawUnderline(final Graphics2D graphics2D, final float n, final float n2, final float n3, final float n4) {
            final Stroke stroke = graphics2D.getStroke();
            graphics2D.setStroke(this.stroke);
            final Line2D.Float float1 = new Line2D.Float(n2, n4, n3, n4);
            graphics2D.draw(float1);
            final Line2D.Float float2 = float1;
            ++float2.y1;
            final Line2D.Float float3 = float1;
            ++float3.y2;
            final Line2D.Float float4 = float1;
            ++float4.x1;
            graphics2D.draw(float1);
            graphics2D.setStroke(stroke);
        }
        
        @Override
        float getLowerDrawLimit(final float n) {
            return 2.0f;
        }
        
        @Override
        Shape getUnderlineShape(final float n, final float n2, final float n3, final float n4) {
            final GeneralPath generalPath = new GeneralPath();
            final Line2D.Float float1 = new Line2D.Float(n2, n4, n3, n4);
            generalPath.append(this.stroke.createStrokedShape(float1), false);
            final Line2D.Float float2 = float1;
            ++float2.y1;
            final Line2D.Float float3 = float1;
            ++float3.y2;
            final Line2D.Float float4 = float1;
            ++float4.x1;
            generalPath.append(this.stroke.createStrokedShape(float1), false);
            return generalPath;
        }
    }
}
