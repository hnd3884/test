package org.apache.poi.sl.draw;

import java.util.Iterator;
import org.apache.poi.sl.usermodel.MasterSheet;
import java.awt.Dimension;
import org.apache.poi.sl.usermodel.Shape;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.Sheet;

public class DrawSheet implements Drawable
{
    protected final Sheet<?, ?> sheet;
    
    public DrawSheet(final Sheet<?, ?> sheet) {
        this.sheet = sheet;
    }
    
    @Override
    public void draw(final Graphics2D graphics) {
        final Dimension dim = this.sheet.getSlideShow().getPageSize();
        final Color whiteTrans = new Color(1.0f, 1.0f, 1.0f, 0.0f);
        graphics.setColor(whiteTrans);
        graphics.fillRect(0, 0, (int)dim.getWidth(), (int)dim.getHeight());
        final DrawFactory drawFact = DrawFactory.getInstance(graphics);
        final MasterSheet<?, ?> master = this.sheet.getMasterSheet();
        if (this.sheet.getFollowMasterGraphics() && master != null) {
            final Drawable drawer = drawFact.getDrawable(master);
            drawer.draw(graphics);
        }
        graphics.setRenderingHint(Drawable.GROUP_TRANSFORM, new AffineTransform());
        for (final Shape<?, ?> shape : this.sheet.getShapes()) {
            if (!this.canDraw(graphics, shape)) {
                continue;
            }
            final AffineTransform at = graphics.getTransform();
            graphics.setRenderingHint(Drawable.GSAVE, true);
            final Drawable drawer2 = drawFact.getDrawable(shape);
            drawer2.applyTransform(graphics);
            drawer2.draw(graphics);
            graphics.setTransform(at);
            graphics.setRenderingHint(Drawable.GRESTORE, true);
        }
    }
    
    @Override
    public void applyTransform(final Graphics2D context) {
    }
    
    @Override
    public void drawContent(final Graphics2D context) {
    }
    
    protected boolean canDraw(final Graphics2D graphics, final Shape<?, ?> shape) {
        return true;
    }
}
