package org.apache.poi.sl.draw;

import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.Shape;

public class DrawNothing implements Drawable
{
    protected final Shape<?, ?> shape;
    
    public DrawNothing(final Shape<?, ?> shape) {
        this.shape = shape;
    }
    
    @Override
    public void applyTransform(final Graphics2D graphics) {
    }
    
    @Override
    public void draw(final Graphics2D graphics) {
    }
    
    @Override
    public void drawContent(final Graphics2D context) {
    }
}
