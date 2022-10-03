package org.jfree.chart.axis;

import org.jfree.text.TextFragment;
import org.jfree.text.TextLine;
import org.jfree.text.TextBlock;
import java.awt.Graphics2D;
import org.jfree.ui.RectangleEdge;
import java.awt.Color;
import java.util.HashMap;
import java.awt.Paint;
import java.awt.Font;
import java.util.Map;

public class ExtendedCategoryAxis extends CategoryAxis
{
    private Map sublabels;
    private Font sublabelFont;
    private Paint sublabelPaint;
    
    public ExtendedCategoryAxis(final String label) {
        super(label);
        this.sublabels = new HashMap();
        this.sublabelFont = new Font("SansSerif", 0, 10);
        this.sublabelPaint = Color.black;
    }
    
    public Font getSubLabelFont() {
        return this.sublabelFont;
    }
    
    public void setSubLabelFont(final Font font) {
        this.sublabelFont = font;
    }
    
    public Paint getSubLabelPaint() {
        return this.sublabelPaint;
    }
    
    public void setSubLabelPaint(final Paint paint) {
        this.sublabelPaint = paint;
    }
    
    public void addSubLabel(final Comparable category, final String label) {
        this.sublabels.put(category, label);
    }
    
    protected TextBlock createLabel(final Comparable category, final float width, final RectangleEdge edge, final Graphics2D g2) {
        final TextBlock label = super.createLabel(category, width, edge, g2);
        final String s = this.sublabels.get(category);
        if (s != null) {
            if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                final TextLine line = new TextLine(s, this.sublabelFont, this.sublabelPaint);
                label.addLine(line);
            }
            else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                final TextLine line = label.getLastLine();
                if (line != null) {
                    line.addFragment(new TextFragment("  " + s, this.sublabelFont, this.sublabelPaint));
                }
            }
        }
        return label;
    }
}
