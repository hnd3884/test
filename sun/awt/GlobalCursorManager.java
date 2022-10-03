package sun.awt;

import java.awt.AWTEvent;
import java.awt.IllegalComponentStateException;
import java.awt.Container;
import java.awt.Window;
import java.awt.Point;
import java.awt.Cursor;
import java.awt.event.InvocationEvent;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.event.InputEvent;

public abstract class GlobalCursorManager
{
    private final NativeUpdater nativeUpdater;
    private long lastUpdateMillis;
    private final Object lastUpdateLock;
    
    public void updateCursorImmediately() {
        synchronized (this.nativeUpdater) {
            this.nativeUpdater.pending = false;
        }
        this._updateCursor(false);
    }
    
    public void updateCursorImmediately(final InputEvent inputEvent) {
        final boolean b;
        synchronized (this.lastUpdateLock) {
            b = (inputEvent.getWhen() >= this.lastUpdateMillis);
        }
        if (b) {
            this._updateCursor(true);
        }
    }
    
    public void updateCursorLater(final Component component) {
        this.nativeUpdater.postIfNotPending(component, new InvocationEvent(Toolkit.getDefaultToolkit(), this.nativeUpdater));
    }
    
    protected GlobalCursorManager() {
        this.nativeUpdater = new NativeUpdater();
        this.lastUpdateLock = new Object();
    }
    
    protected abstract void setCursor(final Component p0, final Cursor p1, final boolean p2);
    
    protected abstract void getCursorPos(final Point p0);
    
    protected abstract Point getLocationOnScreen(final Component p0);
    
    protected abstract Component findHeavyweightUnderCursor(final boolean p0);
    
    private void _updateCursor(final boolean b) {
        synchronized (this.lastUpdateLock) {
            this.lastUpdateMillis = System.currentTimeMillis();
        }
        Point point = null;
        try {
            Component heavyweightUnderCursor = this.findHeavyweightUnderCursor(b);
            if (heavyweightUnderCursor == null) {
                this.updateCursorOutOfJava();
                return;
            }
            if (heavyweightUnderCursor instanceof Window) {
                point = AWTAccessor.getComponentAccessor().getLocation(heavyweightUnderCursor);
            }
            else if (heavyweightUnderCursor instanceof Container) {
                point = this.getLocationOnScreen(heavyweightUnderCursor);
            }
            if (point != null) {
                final Point point2 = new Point();
                this.getCursorPos(point2);
                final Component component = AWTAccessor.getContainerAccessor().findComponentAt((Container)heavyweightUnderCursor, point2.x - point.x, point2.y - point.y, false);
                if (component != null) {
                    heavyweightUnderCursor = component;
                }
            }
            this.setCursor(heavyweightUnderCursor, AWTAccessor.getComponentAccessor().getCursor(heavyweightUnderCursor), b);
        }
        catch (final IllegalComponentStateException ex) {}
    }
    
    protected void updateCursorOutOfJava() {
    }
    
    class NativeUpdater implements Runnable
    {
        boolean pending;
        
        NativeUpdater() {
            this.pending = false;
        }
        
        @Override
        public void run() {
            boolean b = false;
            synchronized (this) {
                if (this.pending) {
                    this.pending = false;
                    b = true;
                }
            }
            if (b) {
                GlobalCursorManager.this._updateCursor(false);
            }
        }
        
        public void postIfNotPending(final Component component, final InvocationEvent invocationEvent) {
            int n = 0;
            synchronized (this) {
                if (!this.pending) {
                    n = ((this.pending = true) ? 1 : 0);
                }
            }
            if (n != 0) {
                SunToolkit.postEvent(SunToolkit.targetToAppContext(component), invocationEvent);
            }
        }
    }
}
