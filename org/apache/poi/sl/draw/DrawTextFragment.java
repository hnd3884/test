package org.apache.poi.sl.draw;

import java.text.AttributedCharacterIterator;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.text.AttributedString;
import java.awt.font.TextLayout;

public class DrawTextFragment implements Drawable
{
    final TextLayout layout;
    final AttributedString str;
    double x;
    double y;
    
    public DrawTextFragment(final TextLayout layout, final AttributedString str) {
        this.layout = layout;
        this.str = str;
    }
    
    public void setPosition(final double x, final double y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public void draw(final Graphics2D graphics) {
        if (this.str == null) {
            return;
        }
        final double yBaseline = this.y + this.layout.getAscent();
        final Integer textMode = (Integer)graphics.getRenderingHint(Drawable.TEXT_RENDERING_MODE);
        if (textMode != null && textMode == 2) {
            this.layout.draw(graphics, (float)this.x, (float)yBaseline);
        }
        else {
            graphics.drawString(this.str.getIterator(), (float)this.x, (float)yBaseline);
        }
    }
    
    @Override
    public void applyTransform(final Graphics2D graphics) {
    }
    
    @Override
    public void drawContent(final Graphics2D graphics) {
    }
    
    public TextLayout getLayout() {
        return this.layout;
    }
    
    public AttributedString getAttributedString() {
        return this.str;
    }
    
    public float getHeight() {
        final double h = this.layout.getAscent() + this.layout.getDescent() + this.getLeading();
        return (float)h;
    }
    
    public float getLeading() {
        double l = this.layout.getLeading();
        if (l == 0.0) {
            l = (this.layout.getAscent() + this.layout.getDescent()) * 0.15;
        }
        return (float)l;
    }
    
    public float getWidth() {
        return this.layout.getAdvance();
    }
    
    public String getString() {
        if (this.str == null) {
            return "";
        }
        final AttributedCharacterIterator it = this.str.getIterator();
        final StringBuilder buf = new StringBuilder();
        for (char c = it.first(); c != '\uffff'; c = it.next()) {
            buf.append(c);
        }
        return buf.toString();
    }
    
    @Override
    public String toString() {
        return "[" + this.getClass().getSimpleName() + "] " + this.getString();
    }
}
