package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Area;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class Decoration
{
    private static final int VALUES_MASK;
    private static final Decoration PLAIN;
    
    private Decoration() {
    }
    
    public static Decoration getPlainDecoration() {
        return Decoration.PLAIN;
    }
    
    public static Decoration getDecoration(AttributeValues applyIMHighlight) {
        if (applyIMHighlight == null || !applyIMHighlight.anyDefined(Decoration.VALUES_MASK)) {
            return Decoration.PLAIN;
        }
        applyIMHighlight = applyIMHighlight.applyIMHighlight();
        return new DecorationImpl(applyIMHighlight.getForeground(), applyIMHighlight.getBackground(), applyIMHighlight.getSwapColors(), applyIMHighlight.getStrikethrough(), Underline.getUnderline(applyIMHighlight.getUnderline()), Underline.getUnderline(applyIMHighlight.getInputMethodUnderline()));
    }
    
    public static Decoration getDecoration(final Map map) {
        if (map == null) {
            return Decoration.PLAIN;
        }
        return getDecoration(AttributeValues.fromMap(map));
    }
    
    public void drawTextAndDecorations(final Label label, final Graphics2D graphics2D, final float n, final float n2) {
        label.handleDraw(graphics2D, n, n2);
    }
    
    public Rectangle2D getVisualBounds(final Label label) {
        return label.handleGetVisualBounds();
    }
    
    public Rectangle2D getCharVisualBounds(final Label label, final int n) {
        return label.handleGetCharVisualBounds(n);
    }
    
    Shape getOutline(final Label label, final float n, final float n2) {
        return label.handleGetOutline(n, n2);
    }
    
    static {
        VALUES_MASK = AttributeValues.getMask(EAttribute.EFOREGROUND, EAttribute.EBACKGROUND, EAttribute.ESWAP_COLORS, EAttribute.ESTRIKETHROUGH, EAttribute.EUNDERLINE, EAttribute.EINPUT_METHOD_HIGHLIGHT, EAttribute.EINPUT_METHOD_UNDERLINE);
        PLAIN = new Decoration();
    }
    
    private static final class DecorationImpl extends Decoration
    {
        private Paint fgPaint;
        private Paint bgPaint;
        private boolean swapColors;
        private boolean strikethrough;
        private Underline stdUnderline;
        private Underline imUnderline;
        
        DecorationImpl(final Paint fgPaint, final Paint bgPaint, final boolean swapColors, final boolean strikethrough, final Underline stdUnderline, final Underline imUnderline) {
            super(null);
            this.fgPaint = null;
            this.bgPaint = null;
            this.swapColors = false;
            this.strikethrough = false;
            this.stdUnderline = null;
            this.imUnderline = null;
            this.fgPaint = fgPaint;
            this.bgPaint = bgPaint;
            this.swapColors = swapColors;
            this.strikethrough = strikethrough;
            this.stdUnderline = stdUnderline;
            this.imUnderline = imUnderline;
        }
        
        private static boolean areEqual(final Object o, final Object o2) {
            if (o == null) {
                return o2 == null;
            }
            return o.equals(o2);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            DecorationImpl decorationImpl;
            try {
                decorationImpl = (DecorationImpl)o;
            }
            catch (final ClassCastException ex) {
                return false;
            }
            return this.swapColors == decorationImpl.swapColors && this.strikethrough == decorationImpl.strikethrough && areEqual(this.stdUnderline, decorationImpl.stdUnderline) && areEqual(this.fgPaint, decorationImpl.fgPaint) && areEqual(this.bgPaint, decorationImpl.bgPaint) && areEqual(this.imUnderline, decorationImpl.imUnderline);
        }
        
        @Override
        public int hashCode() {
            int n = 1;
            if (this.strikethrough) {
                n |= 0x2;
            }
            if (this.swapColors) {
                n |= 0x4;
            }
            if (this.stdUnderline != null) {
                n += this.stdUnderline.hashCode();
            }
            return n;
        }
        
        private float getUnderlineMaxY(final CoreMetrics coreMetrics) {
            float n = 0.0f;
            if (this.stdUnderline != null) {
                n = Math.max(n, coreMetrics.underlineOffset + this.stdUnderline.getLowerDrawLimit(coreMetrics.underlineThickness));
            }
            if (this.imUnderline != null) {
                n = Math.max(n, coreMetrics.underlineOffset + this.imUnderline.getLowerDrawLimit(coreMetrics.underlineThickness));
            }
            return n;
        }
        
        private void drawTextAndEmbellishments(final Label label, final Graphics2D graphics2D, final float n, final float n2) {
            label.handleDraw(graphics2D, n, n2);
            if (!this.strikethrough && this.stdUnderline == null && this.imUnderline == null) {
                return;
            }
            final float n3 = n + (float)label.getLogicalBounds().getWidth();
            final CoreMetrics coreMetrics = label.getCoreMetrics();
            if (this.strikethrough) {
                final Stroke stroke = graphics2D.getStroke();
                graphics2D.setStroke(new BasicStroke(coreMetrics.strikethroughThickness, 0, 0));
                final float n4 = n2 + coreMetrics.strikethroughOffset;
                graphics2D.draw(new Line2D.Float(n, n4, n3, n4));
                graphics2D.setStroke(stroke);
            }
            final float underlineOffset = coreMetrics.underlineOffset;
            final float underlineThickness = coreMetrics.underlineThickness;
            if (this.stdUnderline != null) {
                this.stdUnderline.drawUnderline(graphics2D, underlineThickness, n, n3, n2 + underlineOffset);
            }
            if (this.imUnderline != null) {
                this.imUnderline.drawUnderline(graphics2D, underlineThickness, n, n3, n2 + underlineOffset);
            }
        }
        
        @Override
        public void drawTextAndDecorations(final Label label, final Graphics2D graphics2D, final float n, final float n2) {
            if (this.fgPaint == null && this.bgPaint == null && !this.swapColors) {
                this.drawTextAndEmbellishments(label, graphics2D, n, n2);
            }
            else {
                final Paint paint = graphics2D.getPaint();
                Paint bgPaint;
                Paint paint2;
                if (this.swapColors) {
                    bgPaint = ((this.fgPaint == null) ? paint : this.fgPaint);
                    if (this.bgPaint == null) {
                        if (bgPaint instanceof Color) {
                            final Color color = (Color)bgPaint;
                            paint2 = ((33 * color.getRed() + 53 * color.getGreen() + 14 * color.getBlue() > 18500) ? Color.BLACK : Color.WHITE);
                        }
                        else {
                            paint2 = Color.WHITE;
                        }
                    }
                    else {
                        paint2 = this.bgPaint;
                    }
                }
                else {
                    paint2 = ((this.fgPaint == null) ? paint : this.fgPaint);
                    bgPaint = this.bgPaint;
                }
                if (bgPaint != null) {
                    final Rectangle2D logicalBounds = label.getLogicalBounds();
                    final Rectangle2D.Float float1 = new Rectangle2D.Float(n + (float)logicalBounds.getX(), n2 + (float)logicalBounds.getY(), (float)logicalBounds.getWidth(), (float)logicalBounds.getHeight());
                    graphics2D.setPaint(bgPaint);
                    graphics2D.fill(float1);
                }
                graphics2D.setPaint(paint2);
                this.drawTextAndEmbellishments(label, graphics2D, n, n2);
                graphics2D.setPaint(paint);
            }
        }
        
        @Override
        public Rectangle2D getVisualBounds(final Label label) {
            final Rectangle2D handleGetVisualBounds = label.handleGetVisualBounds();
            if (this.swapColors || this.bgPaint != null || this.strikethrough || this.stdUnderline != null || this.imUnderline != null) {
                final float n = 0.0f;
                final Rectangle2D logicalBounds = label.getLogicalBounds();
                float n2 = 0.0f;
                float n3 = 0.0f;
                if (this.swapColors || this.bgPaint != null) {
                    n2 = (float)logicalBounds.getY();
                    n3 = n2 + (float)logicalBounds.getHeight();
                }
                handleGetVisualBounds.add(new Rectangle2D.Float(n, n2, (float)logicalBounds.getWidth(), Math.max(n3, this.getUnderlineMaxY(label.getCoreMetrics())) - n2));
            }
            return handleGetVisualBounds;
        }
        
        @Override
        Shape getOutline(final Label label, final float n, final float n2) {
            if (!this.strikethrough && this.stdUnderline == null && this.imUnderline == null) {
                return label.handleGetOutline(n, n2);
            }
            final CoreMetrics coreMetrics = label.getCoreMetrics();
            final float underlineThickness = coreMetrics.underlineThickness;
            final float underlineOffset = coreMetrics.underlineOffset;
            final float n3 = n + (float)label.getLogicalBounds().getWidth();
            Area area = null;
            if (this.stdUnderline != null) {
                area = new Area(this.stdUnderline.getUnderlineShape(underlineThickness, n, n3, n2 + underlineOffset));
            }
            if (this.strikethrough) {
                final BasicStroke basicStroke = new BasicStroke(coreMetrics.strikethroughThickness, 0, 0);
                final float n4 = n2 + coreMetrics.strikethroughOffset;
                final Area area2 = new Area(basicStroke.createStrokedShape(new Line2D.Float(n, n4, n3, n4)));
                if (area == null) {
                    area = area2;
                }
                else {
                    area.add(area2);
                }
            }
            if (this.imUnderline != null) {
                final Area area3 = new Area(this.imUnderline.getUnderlineShape(underlineThickness, n, n3, n2 + underlineOffset));
                if (area == null) {
                    area = area3;
                }
                else {
                    area.add(area3);
                }
            }
            area.add(new Area(label.handleGetOutline(n, n2)));
            return new GeneralPath(area);
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append(super.toString());
            sb.append("[");
            if (this.fgPaint != null) {
                sb.append("fgPaint: " + this.fgPaint);
            }
            if (this.bgPaint != null) {
                sb.append(" bgPaint: " + this.bgPaint);
            }
            if (this.swapColors) {
                sb.append(" swapColors: true");
            }
            if (this.strikethrough) {
                sb.append(" strikethrough: true");
            }
            if (this.stdUnderline != null) {
                sb.append(" stdUnderline: " + this.stdUnderline);
            }
            if (this.imUnderline != null) {
                sb.append(" imUnderline: " + this.imUnderline);
            }
            sb.append("]");
            return sb.toString();
        }
    }
    
    public interface Label
    {
        CoreMetrics getCoreMetrics();
        
        Rectangle2D getLogicalBounds();
        
        void handleDraw(final Graphics2D p0, final float p1, final float p2);
        
        Rectangle2D handleGetCharVisualBounds(final int p0);
        
        Rectangle2D handleGetVisualBounds();
        
        Shape handleGetOutline(final float p0, final float p1);
    }
}
