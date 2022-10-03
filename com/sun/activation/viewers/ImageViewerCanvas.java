package com.sun.activation.viewers;

import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Canvas;

public class ImageViewerCanvas extends Canvas
{
    private Image canvas_image;
    
    public ImageViewerCanvas() {
        this.canvas_image = null;
    }
    
    public Dimension getPreferredSize() {
        Dimension dimension;
        if (this.canvas_image == null) {
            dimension = new Dimension(200, 200);
        }
        else {
            dimension = new Dimension(this.canvas_image.getWidth(this), this.canvas_image.getHeight(this));
        }
        return dimension;
    }
    
    public void paint(final Graphics graphics) {
        if (this.canvas_image != null) {
            graphics.drawImage(this.canvas_image, 0, 0, this);
        }
    }
    
    public void setImage(final Image canvas_image) {
        this.canvas_image = canvas_image;
        this.invalidate();
        this.repaint();
    }
}
