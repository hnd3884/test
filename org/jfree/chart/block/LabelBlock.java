package org.jfree.chart.block;

import java.awt.Color;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.text.TextBlockAnchor;
import java.awt.Shape;
import org.jfree.chart.entity.StandardEntityCollection;
import java.awt.geom.Rectangle2D;
import org.jfree.ui.Size2D;
import java.awt.Graphics2D;
import org.jfree.text.TextUtilities;
import java.awt.Paint;
import java.awt.Font;
import org.jfree.text.TextBlock;
import org.jfree.util.PublicCloneable;

public class LabelBlock extends AbstractBlock implements Block, PublicCloneable
{
    private String text;
    private TextBlock label;
    private Font font;
    private String toolTipText;
    private String urlText;
    public static final Paint DEFAULT_PAINT;
    private Paint paint;
    
    public LabelBlock(final String label) {
        this(label, new Font("SansSerif", 0, 10), LabelBlock.DEFAULT_PAINT);
    }
    
    public LabelBlock(final String text, final Font font) {
        this(text, font, LabelBlock.DEFAULT_PAINT);
    }
    
    public LabelBlock(final String text, final Font font, final Paint paint) {
        this.text = text;
        this.paint = paint;
        this.label = TextUtilities.createTextBlock(text, font, this.paint);
        this.font = font;
        this.toolTipText = null;
        this.urlText = null;
    }
    
    public Font getFont() {
        return this.font;
    }
    
    public void setFont(final Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.font = font;
        this.label = TextUtilities.createTextBlock(this.text, font, this.paint);
    }
    
    public Paint getPaint() {
        return this.paint;
    }
    
    public void setPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
        this.label = TextUtilities.createTextBlock(this.text, this.font, this.paint);
    }
    
    public String getToolTipText() {
        return this.toolTipText;
    }
    
    public void setToolTipText(final String text) {
        this.toolTipText = text;
    }
    
    public String getURLText() {
        return this.urlText;
    }
    
    public void setURLText(final String text) {
        this.urlText = text;
    }
    
    public Size2D arrange(final Graphics2D g2, final RectangleConstraint constraint) {
        g2.setFont(this.font);
        final Size2D s = this.label.calculateDimensions(g2);
        return new Size2D(this.calculateTotalWidth(s.getWidth()), this.calculateTotalHeight(s.getHeight()));
    }
    
    public void draw(final Graphics2D g2, final Rectangle2D area) {
        this.draw(g2, area, null);
    }
    
    public Object draw(final Graphics2D g2, Rectangle2D area, final Object params) {
        area = this.trimMargin(area);
        this.drawBorder(g2, area);
        area = this.trimBorder(area);
        area = this.trimPadding(area);
        EntityBlockParams ebp = null;
        StandardEntityCollection sec = null;
        Shape entityArea = null;
        if (params instanceof EntityBlockParams) {
            ebp = (EntityBlockParams)params;
            if (ebp.getGenerateEntities()) {
                sec = new StandardEntityCollection();
                entityArea = (Shape)area.clone();
            }
        }
        g2.setPaint(this.paint);
        g2.setFont(this.font);
        this.label.draw(g2, (float)area.getX(), (float)area.getY(), TextBlockAnchor.TOP_LEFT);
        BlockResult result = null;
        if (ebp != null && sec != null && (this.toolTipText != null || this.urlText != null)) {
            final ChartEntity entity = new ChartEntity(entityArea, this.toolTipText, this.urlText);
            sec.add(entity);
            result = new BlockResult();
            result.setEntityCollection(sec);
        }
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (!(obj instanceof LabelBlock)) {
            return false;
        }
        final LabelBlock that = (LabelBlock)obj;
        return this.text.equals(that.text) && this.font.equals(that.font) && PaintUtilities.equal(this.paint, that.paint) && ObjectUtilities.equal((Object)this.toolTipText, (Object)that.toolTipText) && ObjectUtilities.equal((Object)this.urlText, (Object)that.urlText) && super.equals(obj);
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    static {
        DEFAULT_PAINT = Color.black;
    }
}
