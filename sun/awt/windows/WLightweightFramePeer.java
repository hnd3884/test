package sun.awt.windows;

import java.awt.GraphicsConfiguration;
import java.awt.Dimension;
import java.awt.MenuBar;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import sun.swing.JLightweightFrame;
import sun.swing.SwingAccessor;
import java.awt.AWTEvent;
import java.awt.event.ComponentEvent;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Frame;
import sun.awt.LightweightFrame;
import sun.awt.OverrideNativeWindowHandle;

public class WLightweightFramePeer extends WFramePeer implements OverrideNativeWindowHandle
{
    public WLightweightFramePeer(final LightweightFrame lightweightFrame) {
        super(lightweightFrame);
    }
    
    private LightweightFrame getLwTarget() {
        return (LightweightFrame)this.target;
    }
    
    @Override
    public Graphics getGraphics() {
        return this.getLwTarget().getGraphics();
    }
    
    private native void overrideNativeHandle(final long p0);
    
    @Override
    public void overrideWindowHandle(final long n) {
        this.overrideNativeHandle(n);
    }
    
    @Override
    public void show() {
        super.show();
        this.postEvent(new ComponentEvent((Component)this.getTarget(), 102));
    }
    
    public void hide() {
        super.hide();
        this.postEvent(new ComponentEvent((Component)this.getTarget(), 103));
    }
    
    @Override
    public void reshape(final int n, final int n2, final int n3, final int n4) {
        super.reshape(n, n2, n3, n4);
        this.postEvent(new ComponentEvent((Component)this.getTarget(), 100));
        this.postEvent(new ComponentEvent((Component)this.getTarget(), 101));
    }
    
    @Override
    public void handleEvent(final AWTEvent awtEvent) {
        if (awtEvent.getID() == 501) {
            this.emulateActivation(true);
        }
        super.handleEvent(awtEvent);
    }
    
    @Override
    public void grab() {
        this.getLwTarget().grabFocus();
    }
    
    @Override
    public void ungrab() {
        this.getLwTarget().ungrabFocus();
    }
    
    @Override
    public void updateCursorImmediately() {
        SwingAccessor.getJLightweightFrameAccessor().updateCursor((JLightweightFrame)this.getLwTarget());
    }
    
    @Override
    public boolean isLightweightFramePeer() {
        return true;
    }
    
    @Override
    public void addDropTarget(final DropTarget dropTarget) {
        this.getLwTarget().addDropTarget(dropTarget);
    }
    
    @Override
    public void removeDropTarget(final DropTarget dropTarget) {
        this.getLwTarget().removeDropTarget(dropTarget);
    }
}
