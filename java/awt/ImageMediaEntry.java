package java.awt;

import java.io.Serializable;
import java.awt.image.ImageObserver;

class ImageMediaEntry extends MediaEntry implements ImageObserver, Serializable
{
    Image image;
    int width;
    int height;
    private static final long serialVersionUID = 4739377000350280650L;
    
    ImageMediaEntry(final MediaTracker mediaTracker, final Image image, final int n, final int width, final int height) {
        super(mediaTracker, n);
        this.image = image;
        this.width = width;
        this.height = height;
    }
    
    boolean matches(final Image image, final int n, final int n2) {
        return this.image == image && this.width == n && this.height == n2;
    }
    
    @Override
    Object getMedia() {
        return this.image;
    }
    
    @Override
    synchronized int getStatus(final boolean b, final boolean b2) {
        if (b2) {
            final int parseflags = this.parseflags(this.tracker.target.checkImage(this.image, this.width, this.height, null));
            if (parseflags == 0) {
                if ((this.status & 0xC) != 0x0) {
                    this.setStatus(2);
                }
            }
            else if (parseflags != this.status) {
                this.setStatus(parseflags);
            }
        }
        return super.getStatus(b, b2);
    }
    
    @Override
    void startLoad() {
        if (this.tracker.target.prepareImage(this.image, this.width, this.height, this)) {
            this.setStatus(8);
        }
    }
    
    int parseflags(final int n) {
        if ((n & 0x40) != 0x0) {
            return 4;
        }
        if ((n & 0x80) != 0x0) {
            return 2;
        }
        if ((n & 0x30) != 0x0) {
            return 8;
        }
        return 0;
    }
    
    @Override
    public boolean imageUpdate(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
        if (this.cancelled) {
            return false;
        }
        final int parseflags = this.parseflags(n);
        if (parseflags != 0 && parseflags != this.status) {
            this.setStatus(parseflags);
        }
        return (this.status & 0x1) != 0x0;
    }
}
