package org.apache.poi.sl.draw;

import org.apache.poi.sl.usermodel.PictureShape;
import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.GraphicalFrame;

public class DrawGraphicalFrame extends DrawShape
{
    public DrawGraphicalFrame(final GraphicalFrame<?, ?> shape) {
        super(shape);
    }
    
    @Override
    public void draw(final Graphics2D context) {
        final PictureShape<?, ?> ps = ((GraphicalFrame)this.getShape()).getFallbackPicture();
        if (ps == null) {
            return;
        }
        final DrawPictureShape dps = DrawFactory.getInstance(context).getDrawable(ps);
        dps.draw(context);
    }
}
