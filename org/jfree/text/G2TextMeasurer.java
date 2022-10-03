package org.jfree.text;

import java.awt.geom.Rectangle2D;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class G2TextMeasurer implements TextMeasurer
{
    private Graphics2D g2;
    
    public G2TextMeasurer(final Graphics2D g2) {
        this.g2 = g2;
    }
    
    public float getStringWidth(final String text, final int start, final int end) {
        final FontMetrics fm = this.g2.getFontMetrics();
        final Rectangle2D bounds = TextUtilities.getTextBounds(text.substring(start, end), this.g2, fm);
        final float result = (float)bounds.getWidth();
        return result;
    }
}
