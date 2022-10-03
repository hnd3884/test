package javax.swing;

import java.awt.Image;
import java.awt.image.ImageObserver;

class DebugGraphicsObserver implements ImageObserver
{
    int lastInfo;
    
    synchronized boolean allBitsPresent() {
        return (this.lastInfo & 0x20) != 0x0;
    }
    
    synchronized boolean imageHasProblem() {
        return (this.lastInfo & 0x40) != 0x0 || (this.lastInfo & 0x80) != 0x0;
    }
    
    @Override
    public synchronized boolean imageUpdate(final Image image, final int lastInfo, final int n, final int n2, final int n3, final int n4) {
        this.lastInfo = lastInfo;
        return true;
    }
}
