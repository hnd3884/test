package org.apache.poi.sl.draw;

import org.apache.poi.sl.usermodel.Background;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.sl.usermodel.Slide;

public class DrawSlide extends DrawSheet
{
    public DrawSlide(final Slide<?, ?> slide) {
        super(slide);
    }
    
    @Override
    public void draw(final Graphics2D graphics) {
        graphics.setRenderingHint(Drawable.CURRENT_SLIDE, this.sheet);
        final Background<?, ?> bg = this.sheet.getBackground();
        if (bg != null) {
            final DrawFactory drawFact = DrawFactory.getInstance(graphics);
            final Drawable db = drawFact.getDrawable(bg);
            db.draw(graphics);
        }
        super.draw(graphics);
        graphics.setRenderingHint(Drawable.CURRENT_SLIDE, null);
    }
}
