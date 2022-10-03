package com.adventnet.tools.prevalent;

import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.applet.Applet;

public class ImageLabel extends ImageComponent
{
    public ImageLabel() {
        this.setImageName("bean.jpg");
    }
    
    public ImageLabel(final Applet app) {
        super(app);
        this.setImageName("bean.jpg");
    }
    
    public void paintComponent(final Graphics g) {
        final Graphics2D g2D = (Graphics2D)g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        if (this.wid != this.getSize().width || this.hgt != this.getSize().height) {
            this.wid = this.getSize().width;
            this.hgt = this.getSize().height;
            this.buffer = this.createImage(this.wid, this.hgt);
            this.gbuffer = this.buffer.getGraphics();
            if (this.img != null) {
                this.gbuffer.setColor(this.bgColor);
                this.gbuffer.fillRect(0, 0, this.wid, this.hgt);
                this.gbuffer.drawImage(this.img, 0, 0, this.wid - 1, this.hgt - 1, this);
            }
            this.repaint();
        }
        if (this.image) {
            if (this.buffer != null) {
                g.drawImage(this.buffer, 0, 0, this.wid, this.hgt, this);
            }
        }
        else {
            g2D.setColor(this.bgColor);
            g2D.fill(new Rectangle2D.Double(0.0, 0.0, this.wid, this.hgt));
            g2D.setColor(Color.black);
            g2D.draw(new Rectangle2D.Double(0.0, 0.0, this.wid - 1, this.hgt - 1));
            g2D.setColor(this.fgColor);
            g2D.setFont(this.labelFont);
            final FontMetrics fm = g2D.getFontMetrics();
            final int labelwid = fm.stringWidth(this.labelText);
            final int labelht = fm.getHeight();
            g2D.drawString(this.labelText, this.wid / 2 - labelwid / 2, this.hgt / 2 + labelht / 2);
        }
    }
}
