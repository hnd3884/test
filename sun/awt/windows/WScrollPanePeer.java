package sun.awt.windows;

import sun.awt.AWTAccessor;
import java.awt.ScrollPaneAdjustable;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.Point;
import java.awt.Adjustable;
import java.awt.Insets;
import java.awt.Component;
import sun.util.logging.PlatformLogger;
import java.awt.peer.ScrollPanePeer;

final class WScrollPanePeer extends WPanelPeer implements ScrollPanePeer
{
    private static final PlatformLogger log;
    int scrollbarWidth;
    int scrollbarHeight;
    int prevx;
    int prevy;
    
    static native void initIDs();
    
    @Override
    native void create(final WComponentPeer p0);
    
    native int getOffset(final int p0);
    
    WScrollPanePeer(final Component component) {
        super(component);
        this.scrollbarWidth = this._getVScrollbarWidth();
        this.scrollbarHeight = this._getHScrollbarHeight();
    }
    
    @Override
    void initialize() {
        super.initialize();
        this.setInsets();
        final Insets insets = this.getInsets();
        this.setScrollPosition(-insets.left, -insets.top);
    }
    
    @Override
    public void setUnitIncrement(final Adjustable adjustable, final int n) {
    }
    
    @Override
    public Insets insets() {
        return this.getInsets();
    }
    
    private native void setInsets();
    
    @Override
    public synchronized native void setScrollPosition(final int p0, final int p1);
    
    @Override
    public int getHScrollbarHeight() {
        return this.scrollbarHeight;
    }
    
    private native int _getHScrollbarHeight();
    
    @Override
    public int getVScrollbarWidth() {
        return this.scrollbarWidth;
    }
    
    private native int _getVScrollbarWidth();
    
    public Point getScrollOffset() {
        return new Point(this.getOffset(0), this.getOffset(1));
    }
    
    @Override
    public void childResized(final int n, final int n2) {
        final Dimension size = ((ScrollPane)this.target).getSize();
        this.setSpans(size.width, size.height, n, n2);
        this.setInsets();
    }
    
    synchronized native void setSpans(final int p0, final int p1, final int p2, final int p3);
    
    @Override
    public void setValue(final Adjustable adjustable, final int n) {
        final Component scrollChild = this.getScrollChild();
        if (scrollChild == null) {
            return;
        }
        final Point location = scrollChild.getLocation();
        switch (adjustable.getOrientation()) {
            case 1: {
                this.setScrollPosition(-location.x, n);
                break;
            }
            case 0: {
                this.setScrollPosition(n, -location.y);
                break;
            }
        }
    }
    
    private Component getScrollChild() {
        final ScrollPane scrollPane = (ScrollPane)this.target;
        Component component = null;
        try {
            component = scrollPane.getComponent(0);
        }
        catch (final ArrayIndexOutOfBoundsException ex) {}
        return component;
    }
    
    private void postScrollEvent(final int n, final int n2, final int n3, final boolean b) {
        SunToolkit.executeOnEventHandlerThread(new ScrollEvent(this.target, new Adjustor(n, n2, n3, b)));
    }
    
    static {
        log = PlatformLogger.getLogger("sun.awt.windows.WScrollPanePeer");
        initIDs();
    }
    
    class ScrollEvent extends PeerEvent
    {
        ScrollEvent(final Object o, final Runnable runnable) {
            super(o, runnable, 0L);
        }
        
        @Override
        public PeerEvent coalesceEvents(final PeerEvent peerEvent) {
            if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINEST)) {
                WScrollPanePeer.log.finest("ScrollEvent coalesced: " + peerEvent);
            }
            if (peerEvent instanceof ScrollEvent) {
                return peerEvent;
            }
            return null;
        }
    }
    
    class Adjustor implements Runnable
    {
        int orient;
        int type;
        int pos;
        boolean isAdjusting;
        
        Adjustor(final int orient, final int type, final int pos, final boolean isAdjusting) {
            this.orient = orient;
            this.type = type;
            this.pos = pos;
            this.isAdjusting = isAdjusting;
        }
        
        @Override
        public void run() {
            if (WScrollPanePeer.this.getScrollChild() == null) {
                return;
            }
            final ScrollPane scrollPane = (ScrollPane)WScrollPanePeer.this.target;
            ScrollPaneAdjustable scrollPaneAdjustable = null;
            if (this.orient == 1) {
                scrollPaneAdjustable = (ScrollPaneAdjustable)scrollPane.getVAdjustable();
            }
            else if (this.orient == 0) {
                scrollPaneAdjustable = (ScrollPaneAdjustable)scrollPane.getHAdjustable();
            }
            else if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINE)) {
                WScrollPanePeer.log.fine("Assertion failed: unknown orient");
            }
            if (scrollPaneAdjustable == null) {
                return;
            }
            final int value = scrollPaneAdjustable.getValue();
            int pos = 0;
            switch (this.type) {
                case 2: {
                    pos = value - scrollPaneAdjustable.getUnitIncrement();
                    break;
                }
                case 1: {
                    pos = value + scrollPaneAdjustable.getUnitIncrement();
                    break;
                }
                case 3: {
                    pos = value - scrollPaneAdjustable.getBlockIncrement();
                    break;
                }
                case 4: {
                    pos = value + scrollPaneAdjustable.getBlockIncrement();
                    break;
                }
                case 5: {
                    pos = this.pos;
                    break;
                }
                default: {
                    if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINE)) {
                        WScrollPanePeer.log.fine("Assertion failed: unknown type");
                    }
                    return;
                }
            }
            final int min = Math.min(scrollPaneAdjustable.getMaximum(), Math.max(scrollPaneAdjustable.getMinimum(), pos));
            scrollPaneAdjustable.setValueIsAdjusting(this.isAdjusting);
            AWTAccessor.getScrollPaneAdjustableAccessor().setTypedValue(scrollPaneAdjustable, min, this.type);
            Component component;
            for (component = WScrollPanePeer.this.getScrollChild(); component != null && !(component.getPeer() instanceof WComponentPeer); component = component.getParent()) {}
            if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINE) && component == null) {
                WScrollPanePeer.log.fine("Assertion (hwAncestor != null) failed, couldn't find heavyweight ancestor of scroll pane child");
            }
            ((WComponentPeer)component.getPeer()).paintDamagedAreaImmediately();
        }
    }
}
