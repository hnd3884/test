package sun.awt.windows;

import java.awt.GraphicsConfiguration;
import java.awt.Dimension;
import sun.awt.Graphics2Delegate;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import sun.awt.PaintEventDispatcher;
import sun.awt.SunToolkit;
import java.awt.Component;
import java.awt.peer.CanvasPeer;

class WCanvasPeer extends WComponentPeer implements CanvasPeer
{
    private boolean eraseBackground;
    
    WCanvasPeer(final Component component) {
        super(component);
    }
    
    @Override
    native void create(final WComponentPeer p0);
    
    @Override
    void initialize() {
        this.eraseBackground = !SunToolkit.getSunAwtNoerasebackground();
        final boolean sunAwtErasebackgroundonresize = SunToolkit.getSunAwtErasebackgroundonresize();
        if (!PaintEventDispatcher.getPaintEventDispatcher().shouldDoNativeBackgroundErase((Component)this.target)) {
            this.eraseBackground = false;
        }
        this.setNativeBackgroundErase(this.eraseBackground, sunAwtErasebackgroundonresize);
        super.initialize();
        final Color background = ((Component)this.target).getBackground();
        if (background != null) {
            this.setBackground(background);
        }
    }
    
    @Override
    public void paint(final Graphics graphics) {
        final Dimension size = ((Component)this.target).getSize();
        if (graphics instanceof Graphics2D || graphics instanceof Graphics2Delegate) {
            graphics.clearRect(0, 0, size.width, size.height);
        }
        else {
            graphics.setColor(((Component)this.target).getBackground());
            graphics.fillRect(0, 0, size.width, size.height);
            graphics.setColor(((Component)this.target).getForeground());
        }
        super.paint(graphics);
    }
    
    @Override
    public boolean shouldClearRectBeforePaint() {
        return this.eraseBackground;
    }
    
    void disableBackgroundErase() {
        this.setNativeBackgroundErase(this.eraseBackground = false, false);
    }
    
    private native void setNativeBackgroundErase(final boolean p0, final boolean p1);
    
    @Override
    public GraphicsConfiguration getAppropriateGraphicsConfiguration(final GraphicsConfiguration graphicsConfiguration) {
        return graphicsConfiguration;
    }
}
