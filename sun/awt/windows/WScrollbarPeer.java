package sun.awt.windows;

import sun.awt.SunToolkit;
import java.awt.AWTEvent;
import java.awt.Adjustable;
import java.awt.event.AdjustmentEvent;
import java.awt.Component;
import java.awt.Scrollbar;
import java.awt.Dimension;
import java.awt.peer.ScrollbarPeer;

final class WScrollbarPeer extends WComponentPeer implements ScrollbarPeer
{
    private boolean dragInProgress;
    
    static native int getScrollbarSize(final int p0);
    
    @Override
    public Dimension getMinimumSize() {
        if (((Scrollbar)this.target).getOrientation() == 1) {
            return new Dimension(getScrollbarSize(1), 50);
        }
        return new Dimension(50, getScrollbarSize(0));
    }
    
    @Override
    public native void setValues(final int p0, final int p1, final int p2, final int p3);
    
    @Override
    public native void setLineIncrement(final int p0);
    
    @Override
    public native void setPageIncrement(final int p0);
    
    WScrollbarPeer(final Scrollbar scrollbar) {
        super(scrollbar);
        this.dragInProgress = false;
    }
    
    @Override
    native void create(final WComponentPeer p0);
    
    @Override
    void initialize() {
        final Scrollbar scrollbar = (Scrollbar)this.target;
        this.setValues(scrollbar.getValue(), scrollbar.getVisibleAmount(), scrollbar.getMinimum(), scrollbar.getMaximum());
        super.initialize();
    }
    
    private void postAdjustmentEvent(final int n, final int n2, final boolean b) {
        final Scrollbar scrollbar = (Scrollbar)this.target;
        SunToolkit.executeOnEventHandlerThread(scrollbar, new Runnable() {
            @Override
            public void run() {
                scrollbar.setValueIsAdjusting(b);
                scrollbar.setValue(n2);
                WScrollbarPeer.this.postEvent(new AdjustmentEvent(scrollbar, 601, n, n2, b));
            }
        });
    }
    
    void lineUp(final int n) {
        this.postAdjustmentEvent(2, n, false);
    }
    
    void lineDown(final int n) {
        this.postAdjustmentEvent(1, n, false);
    }
    
    void pageUp(final int n) {
        this.postAdjustmentEvent(3, n, false);
    }
    
    void pageDown(final int n) {
        this.postAdjustmentEvent(4, n, false);
    }
    
    void warp(final int n) {
        this.postAdjustmentEvent(5, n, false);
    }
    
    void drag(final int n) {
        if (!this.dragInProgress) {
            this.dragInProgress = true;
        }
        this.postAdjustmentEvent(5, n, true);
    }
    
    void dragEnd(final int n) {
        final Scrollbar scrollbar = (Scrollbar)this.target;
        if (!this.dragInProgress) {
            return;
        }
        this.dragInProgress = false;
        SunToolkit.executeOnEventHandlerThread(scrollbar, new Runnable() {
            @Override
            public void run() {
                scrollbar.setValueIsAdjusting(false);
                WScrollbarPeer.this.postEvent(new AdjustmentEvent(scrollbar, 601, 5, n, false));
            }
        });
    }
    
    @Override
    public boolean shouldClearRectBeforePaint() {
        return false;
    }
}
