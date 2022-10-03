package org.jfree.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.Serializable;
import java.awt.image.ImageObserver;

public class WaitingImageObserver implements ImageObserver, Serializable, Cloneable
{
    static final long serialVersionUID = -807204410581383550L;
    private boolean lock;
    private Image image;
    private boolean error;
    
    public WaitingImageObserver(final Image image) {
        if (image == null) {
            throw new NullPointerException();
        }
        this.image = image;
        this.lock = true;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public synchronized boolean imageUpdate(final Image img, final int infoflags, final int x, final int y, final int width, final int height) {
        if ((infoflags & 0x20) == 0x20) {
            this.lock = false;
            this.error = false;
            this.notifyAll();
            return false;
        }
        if ((infoflags & 0x80) == 0x80 || (infoflags & 0x40) == 0x40) {
            this.lock = false;
            this.error = true;
            this.notifyAll();
            return false;
        }
        return true;
    }
    
    public boolean isError() {
        return this.error;
    }
    
    public boolean isLoadingComplete() {
        return !this.lock;
    }
    
    public synchronized void waitImageLoaded() {
        if (!this.lock) {
            return;
        }
        final BufferedImage img = new BufferedImage(1, 1, 1);
        final Graphics g = img.getGraphics();
        while (this.lock) {
            if (g.drawImage(this.image, 0, 0, img.getWidth(this), img.getHeight(this), this)) {
                return;
            }
            try {
                this.wait(500L);
            }
            catch (final InterruptedException e) {
                Log.info("WaitingImageObserver.waitImageLoaded(): InterruptedException thrown", e);
            }
        }
    }
}
