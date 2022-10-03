package org.jfree.chart.block;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.Paint;

public class ColorBlock extends AbstractBlock implements Block
{
    private Paint paint;
    
    public ColorBlock(final Paint paint, final double width, final double height) {
        this.paint = paint;
        this.setWidth(width);
        this.setHeight(height);
    }
    
    public void draw(final Graphics2D g2, final Rectangle2D area) {
        final Rectangle2D bounds = this.getBounds();
        g2.setPaint(this.paint);
        g2.fill(bounds);
    }
    
    public Object draw(final Graphics2D g2, final Rectangle2D area, final Object params) {
        this.draw(g2, area);
        return null;
    }
}
