package sun.awt.windows;

import java.awt.GraphicsConfiguration;
import java.awt.Dimension;
import java.awt.MenuBar;
import sun.awt.Win32GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.Frame;
import sun.awt.EmbeddedFrame;

public class WEmbeddedFramePeer extends WFramePeer
{
    public WEmbeddedFramePeer(final EmbeddedFrame embeddedFrame) {
        super(embeddedFrame);
    }
    
    @Override
    native void create(final WComponentPeer p0);
    
    @Override
    public void print(final Graphics graphics) {
    }
    
    @Override
    public void updateMinimumSize() {
    }
    
    public void modalDisable(final Dialog dialog, final long n) {
        super.modalDisable(dialog, n);
        ((EmbeddedFrame)this.target).notifyModalBlocked(dialog, true);
    }
    
    public void modalEnable(final Dialog dialog) {
        super.modalEnable(dialog);
        ((EmbeddedFrame)this.target).notifyModalBlocked(dialog, false);
    }
    
    @Override
    public void setBoundsPrivate(final int n, final int n2, final int n3, final int n4) {
        this.setBounds(n, n2, n3, n4, 16387);
    }
    
    @Override
    public native Rectangle getBoundsPrivate();
    
    @Override
    public boolean isAccelCapable() {
        return !Win32GraphicsEnvironment.isDWMCompositionEnabled();
    }
}
