package sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import sun.awt.SunGraphicsCallback;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.peer.PanelPeer;

class WPanelPeer extends WCanvasPeer implements PanelPeer
{
    Insets insets_;
    
    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        SunGraphicsCallback.PaintHeavyweightComponentsCallback.getInstance().runComponents(((Container)this.target).getComponents(), graphics, 3);
    }
    
    @Override
    public void print(final Graphics graphics) {
        super.print(graphics);
        SunGraphicsCallback.PrintHeavyweightComponentsCallback.getInstance().runComponents(((Container)this.target).getComponents(), graphics, 3);
    }
    
    @Override
    public Insets getInsets() {
        return this.insets_;
    }
    
    private static native void initIDs();
    
    WPanelPeer(final Component component) {
        super(component);
    }
    
    @Override
    void initialize() {
        super.initialize();
        this.insets_ = new Insets(0, 0, 0, 0);
        if (((Component)this.target).getBackground() == null) {
            final Color defaultColor = WColor.getDefaultColor(1);
            ((Component)this.target).setBackground(defaultColor);
            this.setBackground(defaultColor);
        }
        if (((Component)this.target).getForeground() == null) {
            final Color defaultColor2 = WColor.getDefaultColor(2);
            ((Component)this.target).setForeground(defaultColor2);
            this.setForeground(defaultColor2);
        }
    }
    
    public Insets insets() {
        return this.getInsets();
    }
    
    static {
        initIDs();
    }
}
