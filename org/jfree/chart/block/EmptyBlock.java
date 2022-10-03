package org.jfree.chart.block;

import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class EmptyBlock extends AbstractBlock implements Block, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -4083197869412648579L;
    
    public EmptyBlock(final double width, final double height) {
        this.setWidth(width);
        this.setHeight(height);
    }
    
    public void draw(final Graphics2D g2, final Rectangle2D area) {
    }
    
    public Object draw(final Graphics2D g2, final Rectangle2D area, final Object params) {
        return null;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
