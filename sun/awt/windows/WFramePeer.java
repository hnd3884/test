package sun.awt.windows;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.awt.Component;
import sun.awt.im.InputMethodManager;
import java.awt.Window;
import java.awt.MenuBar;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Frame;
import sun.awt.AWTAccessor;
import java.awt.peer.FramePeer;

class WFramePeer extends WWindowPeer implements FramePeer
{
    private static final boolean keepOnMinimize;
    
    private static native void initIDs();
    
    @Override
    public native void setState(final int p0);
    
    @Override
    public native int getState();
    
    public void setExtendedState(final int n) {
        AWTAccessor.getFrameAccessor().setExtendedState((Frame)this.target, n);
    }
    
    public int getExtendedState() {
        return AWTAccessor.getFrameAccessor().getExtendedState((Frame)this.target);
    }
    
    private native void setMaximizedBounds(final int p0, final int p1, final int p2, final int p3);
    
    private native void clearMaximizedBounds();
    
    @Override
    public void setMaximizedBounds(final Rectangle rectangle) {
        if (rectangle == null) {
            this.clearMaximizedBounds();
        }
        else {
            final Rectangle rectangle2 = (Rectangle)rectangle.clone();
            this.adjustMaximizedBounds(rectangle2);
            this.setMaximizedBounds(rectangle2.x, rectangle2.y, rectangle2.width, rectangle2.height);
        }
    }
    
    private void adjustMaximizedBounds(final Rectangle rectangle) {
        final GraphicsConfiguration graphicsConfiguration = this.getGraphicsConfiguration();
        final GraphicsConfiguration defaultConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        if (graphicsConfiguration != null && graphicsConfiguration != defaultConfiguration) {
            final Rectangle bounds = graphicsConfiguration.getBounds();
            final Rectangle bounds2 = defaultConfiguration.getBounds();
            if (bounds.width - bounds2.width > 0 || bounds.height - bounds2.height > 0) {
                rectangle.width -= bounds.width - bounds2.width;
                rectangle.height -= bounds.height - bounds2.height;
            }
        }
    }
    
    @Override
    public boolean updateGraphicsData(final GraphicsConfiguration graphicsConfiguration) {
        final boolean updateGraphicsData = super.updateGraphicsData(graphicsConfiguration);
        final Rectangle maximizedBounds = AWTAccessor.getFrameAccessor().getMaximizedBounds((Frame)this.target);
        if (maximizedBounds != null) {
            this.setMaximizedBounds(maximizedBounds);
        }
        return updateGraphicsData;
    }
    
    @Override
    boolean isTargetUndecorated() {
        return ((Frame)this.target).isUndecorated();
    }
    
    @Override
    public void reshape(final int n, final int n2, final int n3, final int n4) {
        if (((Frame)this.target).isUndecorated()) {
            super.reshape(n, n2, n3, n4);
        }
        else {
            this.reshapeFrame(n, n2, n3, n4);
        }
    }
    
    @Override
    public Dimension getMinimumSize() {
        final Dimension dimension = new Dimension();
        if (!((Frame)this.target).isUndecorated()) {
            dimension.setSize(WWindowPeer.getSysMinWidth(), WWindowPeer.getSysMinHeight());
        }
        if (((Frame)this.target).getMenuBar() != null) {
            final Dimension dimension2 = dimension;
            dimension2.height += getSysMenuHeight();
        }
        return dimension;
    }
    
    @Override
    public void setMenuBar(final MenuBar menuBar) {
        WMenuBarPeer menuBar2 = (WMenuBarPeer)WToolkit.targetToPeer(menuBar);
        if (menuBar2 != null) {
            if (menuBar2.framePeer != this) {
                menuBar.removeNotify();
                menuBar.addNotify();
                menuBar2 = (WMenuBarPeer)WToolkit.targetToPeer(menuBar);
                if (menuBar2 != null && menuBar2.framePeer != this) {
                    throw new IllegalStateException("Wrong parent peer");
                }
            }
            if (menuBar2 != null) {
                this.addChildPeer(menuBar2);
            }
        }
        this.setMenuBar0(menuBar2);
        this.updateInsets(this.insets_);
    }
    
    private native void setMenuBar0(final WMenuBarPeer p0);
    
    WFramePeer(final Frame frame) {
        super(frame);
        final String triggerMenuString = InputMethodManager.getInstance().getTriggerMenuString();
        if (triggerMenuString != null) {
            this.pSetIMMOption(triggerMenuString);
        }
    }
    
    native void createAwtFrame(final WComponentPeer p0);
    
    @Override
    void create(final WComponentPeer wComponentPeer) {
        this.preCreate(wComponentPeer);
        this.createAwtFrame(wComponentPeer);
    }
    
    @Override
    void initialize() {
        super.initialize();
        final Frame frame = (Frame)this.target;
        if (frame.getTitle() != null) {
            this.setTitle(frame.getTitle());
        }
        this.setResizable(frame.isResizable());
        this.setState(frame.getExtendedState());
    }
    
    private static native int getSysMenuHeight();
    
    native void pSetIMMOption(final String p0);
    
    void notifyIMMOptionChange() {
        InputMethodManager.getInstance().notifyChangeRequest((Component)this.target);
    }
    
    @Override
    public void setBoundsPrivate(final int n, final int n2, final int n3, final int n4) {
        this.setBounds(n, n2, n3, n4, 3);
    }
    
    @Override
    public Rectangle getBoundsPrivate() {
        return this.getBounds();
    }
    
    @Override
    public void emulateActivation(final boolean b) {
        this.synthesizeWmActivate(b);
    }
    
    private native void synthesizeWmActivate(final boolean p0);
    
    static {
        initIDs();
        keepOnMinimize = "true".equals(AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("sun.awt.keepWorkingSetOnMinimize")));
    }
}
