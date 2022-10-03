package HTTPClient;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;

class Separator extends Panel
{
    public void paint(final Graphics g) {
        final int w = this.getSize().width;
        final int h = this.getSize().height / 2;
        g.setColor(Color.darkGray);
        g.drawLine(2, h - 1, w - 2, h - 1);
        g.setColor(Color.white);
        g.drawLine(2, h, w - 2, h);
    }
    
    public Dimension getMinimumSize() {
        return new Dimension(4, 2);
    }
}
