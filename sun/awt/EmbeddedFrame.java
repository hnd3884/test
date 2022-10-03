package sun.awt;

import java.awt.Dialog;
import java.applet.Applet;
import java.awt.peer.FramePeer;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.peer.ComponentPeer;
import java.awt.MenuComponent;
import java.awt.MenuBar;
import java.util.List;
import java.awt.Image;
import java.awt.Window;
import java.awt.Component;
import java.awt.AWTKeyStroke;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.awt.Container;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeListener;
import java.awt.KeyEventDispatcher;
import java.awt.Frame;

public abstract class EmbeddedFrame extends Frame implements KeyEventDispatcher, PropertyChangeListener
{
    private boolean isCursorAllowed;
    private boolean supportsXEmbed;
    private KeyboardFocusManager appletKFM;
    private static final long serialVersionUID = 2967042741780317130L;
    protected static final boolean FORWARD = true;
    protected static final boolean BACKWARD = false;
    
    public boolean supportsXEmbed() {
        return this.supportsXEmbed && SunToolkit.needsXEmbed();
    }
    
    protected EmbeddedFrame(final boolean b) {
        this(0L, b);
    }
    
    protected EmbeddedFrame() {
        this(0L);
    }
    
    @Deprecated
    protected EmbeddedFrame(final int n) {
        this((long)n);
    }
    
    protected EmbeddedFrame(final long n) {
        this(n, false);
    }
    
    protected EmbeddedFrame(final long n, final boolean supportsXEmbed) {
        this.isCursorAllowed = true;
        this.supportsXEmbed = false;
        this.supportsXEmbed = supportsXEmbed;
        this.registerListeners();
    }
    
    @Override
    public Container getParent() {
        return null;
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (!propertyChangeEvent.getPropertyName().equals("managingFocus")) {
            return;
        }
        if (propertyChangeEvent.getNewValue() == Boolean.TRUE) {
            return;
        }
        this.removeTraversingOutListeners((KeyboardFocusManager)propertyChangeEvent.getSource());
        this.appletKFM = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        if (this.isVisible()) {
            this.addTraversingOutListeners(this.appletKFM);
        }
    }
    
    private void addTraversingOutListeners(final KeyboardFocusManager keyboardFocusManager) {
        keyboardFocusManager.addKeyEventDispatcher(this);
        keyboardFocusManager.addPropertyChangeListener("managingFocus", this);
    }
    
    private void removeTraversingOutListeners(final KeyboardFocusManager keyboardFocusManager) {
        keyboardFocusManager.removeKeyEventDispatcher(this);
        keyboardFocusManager.removePropertyChangeListener("managingFocus", this);
    }
    
    public void registerListeners() {
        if (this.appletKFM != null) {
            this.removeTraversingOutListeners(this.appletKFM);
        }
        this.appletKFM = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        if (this.isVisible()) {
            this.addTraversingOutListeners(this.appletKFM);
        }
    }
    
    @Override
    public void show() {
        if (this.appletKFM != null) {
            this.addTraversingOutListeners(this.appletKFM);
        }
        super.show();
    }
    
    @Override
    public void hide() {
        if (this.appletKFM != null) {
            this.removeTraversingOutListeners(this.appletKFM);
        }
        super.hide();
    }
    
    @Override
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        if (this != AWTAccessor.getKeyboardFocusManagerAccessor().getCurrentFocusCycleRoot()) {
            return false;
        }
        if (keyEvent.getID() == 400) {
            return false;
        }
        if (!this.getFocusTraversalKeysEnabled() || keyEvent.isConsumed()) {
            return false;
        }
        final AWTKeyStroke awtKeyStrokeForEvent = AWTKeyStroke.getAWTKeyStrokeForEvent(keyEvent);
        final Component component = keyEvent.getComponent();
        if (this.getFocusTraversalKeys(0).contains(awtKeyStrokeForEvent)) {
            final Component lastComponent = this.getFocusTraversalPolicy().getLastComponent(this);
            if ((component == lastComponent || lastComponent == null) && this.traverseOut(true)) {
                keyEvent.consume();
                return true;
            }
        }
        if (this.getFocusTraversalKeys(1).contains(awtKeyStrokeForEvent)) {
            final Component firstComponent = this.getFocusTraversalPolicy().getFirstComponent(this);
            if ((component == firstComponent || firstComponent == null) && this.traverseOut(false)) {
                keyEvent.consume();
                return true;
            }
        }
        return false;
    }
    
    public boolean traverseIn(final boolean b) {
        Component component;
        if (b) {
            component = this.getFocusTraversalPolicy().getFirstComponent(this);
        }
        else {
            component = this.getFocusTraversalPolicy().getLastComponent(this);
        }
        if (component != null) {
            AWTAccessor.getKeyboardFocusManagerAccessor().setMostRecentFocusOwner(this, component);
            this.synthesizeWindowActivation(true);
        }
        return null != component;
    }
    
    protected boolean traverseOut(final boolean b) {
        return false;
    }
    
    @Override
    public void setTitle(final String s) {
    }
    
    @Override
    public void setIconImage(final Image image) {
    }
    
    @Override
    public void setIconImages(final List<? extends Image> list) {
    }
    
    @Override
    public void setMenuBar(final MenuBar menuBar) {
    }
    
    @Override
    public void setResizable(final boolean b) {
    }
    
    @Override
    public void remove(final MenuComponent menuComponent) {
    }
    
    @Override
    public boolean isResizable() {
        return true;
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.getPeer() == null) {
                this.setPeer(new NullEmbeddedFramePeer());
            }
            super.addNotify();
        }
    }
    
    public void setCursorAllowed(final boolean isCursorAllowed) {
        this.isCursorAllowed = isCursorAllowed;
        this.getPeer().updateCursorImmediately();
    }
    
    public boolean isCursorAllowed() {
        return this.isCursorAllowed;
    }
    
    @Override
    public Cursor getCursor() {
        return this.isCursorAllowed ? super.getCursor() : Cursor.getPredefinedCursor(0);
    }
    
    protected void setPeer(final ComponentPeer componentPeer) {
        AWTAccessor.getComponentAccessor().setPeer(this, componentPeer);
    }
    
    public void synthesizeWindowActivation(final boolean b) {
    }
    
    protected void setLocationPrivate(final int n, final int n2) {
        final Dimension size = this.getSize();
        this.setBoundsPrivate(n, n2, size.width, size.height);
    }
    
    protected Point getLocationPrivate() {
        final Rectangle boundsPrivate = this.getBoundsPrivate();
        return new Point(boundsPrivate.x, boundsPrivate.y);
    }
    
    protected void setBoundsPrivate(final int n, final int n2, final int n3, final int n4) {
        final FramePeer framePeer = (FramePeer)this.getPeer();
        if (framePeer != null) {
            framePeer.setBoundsPrivate(n, n2, n3, n4);
        }
    }
    
    protected Rectangle getBoundsPrivate() {
        final FramePeer framePeer = (FramePeer)this.getPeer();
        if (framePeer != null) {
            return framePeer.getBoundsPrivate();
        }
        return this.getBounds();
    }
    
    @Override
    public void toFront() {
    }
    
    @Override
    public void toBack() {
    }
    
    public abstract void registerAccelerator(final AWTKeyStroke p0);
    
    public abstract void unregisterAccelerator(final AWTKeyStroke p0);
    
    public static Applet getAppletIfAncestorOf(final Component component) {
        Container container = component.getParent();
        Applet applet = null;
        while (container != null && !(container instanceof EmbeddedFrame)) {
            if (container instanceof Applet) {
                applet = (Applet)container;
            }
            container = container.getParent();
        }
        return (container == null) ? null : applet;
    }
    
    public void notifyModalBlocked(final Dialog dialog, final boolean b) {
    }
    
    private static class NullEmbeddedFramePeer extends NullComponentPeer implements FramePeer
    {
        @Override
        public void setTitle(final String s) {
        }
        
        public void setIconImage(final Image image) {
        }
        
        @Override
        public void updateIconImages() {
        }
        
        @Override
        public void setMenuBar(final MenuBar menuBar) {
        }
        
        @Override
        public void setResizable(final boolean b) {
        }
        
        @Override
        public void setState(final int n) {
        }
        
        @Override
        public int getState() {
            return 0;
        }
        
        @Override
        public void setMaximizedBounds(final Rectangle rectangle) {
        }
        
        @Override
        public void toFront() {
        }
        
        @Override
        public void toBack() {
        }
        
        @Override
        public void updateFocusableWindowState() {
        }
        
        public void updateAlwaysOnTop() {
        }
        
        @Override
        public void updateAlwaysOnTopState() {
        }
        
        public Component getGlobalHeavyweightFocusOwner() {
            return null;
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
        public void setModalBlocked(final Dialog dialog, final boolean b) {
        }
        
        public void restack() {
            throw new UnsupportedOperationException();
        }
        
        public boolean isRestackSupported() {
            return false;
        }
        
        public boolean requestWindowFocus() {
            return false;
        }
        
        @Override
        public void updateMinimumSize() {
        }
        
        @Override
        public void setOpacity(final float n) {
        }
        
        @Override
        public void setOpaque(final boolean b) {
        }
        
        @Override
        public void updateWindow() {
        }
        
        @Override
        public void repositionSecurityWarning() {
        }
        
        @Override
        public void emulateActivation(final boolean b) {
        }
    }
}
