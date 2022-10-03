package sun.awt;

import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.Rectangle;
import java.awt.peer.ComponentPeer;
import java.awt.Component;
import java.awt.peer.FramePeer;
import java.awt.Toolkit;
import java.awt.MenuComponent;
import java.awt.MenuBar;
import java.util.List;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Container;
import java.awt.Frame;

public abstract class LightweightFrame extends Frame
{
    private int hostX;
    private int hostY;
    private int hostW;
    private int hostH;
    
    public LightweightFrame() {
        this.setUndecorated(true);
        this.setResizable(true);
        this.setEnabled(true);
    }
    
    @Override
    public final Container getParent() {
        return null;
    }
    
    @Override
    public Graphics getGraphics() {
        return null;
    }
    
    @Override
    public final boolean isResizable() {
        return true;
    }
    
    @Override
    public final void setTitle(final String s) {
    }
    
    @Override
    public final void setIconImage(final Image image) {
    }
    
    @Override
    public final void setIconImages(final List<? extends Image> list) {
    }
    
    @Override
    public final void setMenuBar(final MenuBar menuBar) {
    }
    
    @Override
    public final void setResizable(final boolean b) {
    }
    
    @Override
    public final void remove(final MenuComponent menuComponent) {
    }
    
    @Override
    public final void toFront() {
    }
    
    @Override
    public final void toBack() {
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.getPeer() == null) {
                final SunToolkit sunToolkit = (SunToolkit)Toolkit.getDefaultToolkit();
                try {
                    this.setPeer(sunToolkit.createLightweightFrame(this));
                }
                catch (final Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            super.addNotify();
        }
    }
    
    private void setPeer(final FramePeer framePeer) {
        AWTAccessor.getComponentAccessor().setPeer(this, framePeer);
    }
    
    public void emulateActivation(final boolean b) {
        ((FramePeer)this.getPeer()).emulateActivation(b);
    }
    
    public abstract void grabFocus();
    
    public abstract void ungrabFocus();
    
    public abstract int getScaleFactor();
    
    public abstract void notifyDisplayChanged(final int p0);
    
    public Rectangle getHostBounds() {
        if (this.hostX == 0 && this.hostY == 0 && this.hostW == 0 && this.hostH == 0) {
            return this.getBounds();
        }
        return new Rectangle(this.hostX, this.hostY, this.hostW, this.hostH);
    }
    
    public void setHostBounds(final int hostX, final int hostY, final int hostW, final int hostH) {
        this.hostX = hostX;
        this.hostY = hostY;
        this.hostW = hostW;
        this.hostH = hostH;
    }
    
    public abstract <T extends DragGestureRecognizer> T createDragGestureRecognizer(final Class<T> p0, final DragSource p1, final Component p2, final int p3, final DragGestureListener p4);
    
    public abstract DragSourceContextPeer createDragSourceContextPeer(final DragGestureEvent p0) throws InvalidDnDOperationException;
    
    public abstract void addDropTarget(final DropTarget p0);
    
    public abstract void removeDropTarget(final DropTarget p0);
}
