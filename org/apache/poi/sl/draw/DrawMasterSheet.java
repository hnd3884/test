package org.apache.poi.sl.draw;

import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.SimpleShape;
import java.awt.RenderingHints;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.Shape;
import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.sl.usermodel.MasterSheet;

public class DrawMasterSheet extends DrawSheet
{
    public DrawMasterSheet(final MasterSheet<?, ?> sheet) {
        super(sheet);
    }
    
    @Override
    protected boolean canDraw(final Graphics2D graphics, final Shape<?, ?> shape) {
        final Slide<?, ?> slide = (Slide<?, ?>)graphics.getRenderingHint(Drawable.CURRENT_SLIDE);
        if (shape instanceof SimpleShape) {
            final Placeholder ph = ((SimpleShape)shape).getPlaceholder();
            if (ph != null) {
                return slide.getDisplayPlaceholder(ph);
            }
        }
        return slide.getFollowMasterGraphics();
    }
}
