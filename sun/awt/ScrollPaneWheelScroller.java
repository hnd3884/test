package sun.awt;

import java.awt.Insets;
import java.awt.Adjustable;
import java.awt.event.MouseWheelEvent;
import java.awt.ScrollPane;
import sun.util.logging.PlatformLogger;

public abstract class ScrollPaneWheelScroller
{
    private static final PlatformLogger log;
    
    private ScrollPaneWheelScroller() {
    }
    
    public static void handleWheelScrolling(final ScrollPane scrollPane, final MouseWheelEvent mouseWheelEvent) {
        if (ScrollPaneWheelScroller.log.isLoggable(PlatformLogger.Level.FINER)) {
            ScrollPaneWheelScroller.log.finer("x = " + mouseWheelEvent.getX() + ", y = " + mouseWheelEvent.getY() + ", src is " + mouseWheelEvent.getSource());
        }
        if (scrollPane != null && mouseWheelEvent.getScrollAmount() != 0) {
            final Adjustable adjustableToScroll = getAdjustableToScroll(scrollPane);
            if (adjustableToScroll != null) {
                final int incrementFromAdjustable = getIncrementFromAdjustable(adjustableToScroll, mouseWheelEvent);
                if (ScrollPaneWheelScroller.log.isLoggable(PlatformLogger.Level.FINER)) {
                    ScrollPaneWheelScroller.log.finer("increment from adjustable(" + adjustableToScroll.getClass() + ") : " + incrementFromAdjustable);
                }
                scrollAdjustable(adjustableToScroll, incrementFromAdjustable);
            }
        }
    }
    
    public static Adjustable getAdjustableToScroll(final ScrollPane scrollPane) {
        final int scrollbarDisplayPolicy = scrollPane.getScrollbarDisplayPolicy();
        if (scrollbarDisplayPolicy == 1 || scrollbarDisplayPolicy == 2) {
            if (ScrollPaneWheelScroller.log.isLoggable(PlatformLogger.Level.FINER)) {
                ScrollPaneWheelScroller.log.finer("using vertical scrolling due to scrollbar policy");
            }
            return scrollPane.getVAdjustable();
        }
        final Insets insets = scrollPane.getInsets();
        final int vScrollbarWidth = scrollPane.getVScrollbarWidth();
        if (ScrollPaneWheelScroller.log.isLoggable(PlatformLogger.Level.FINER)) {
            ScrollPaneWheelScroller.log.finer("insets: l = " + insets.left + ", r = " + insets.right + ", t = " + insets.top + ", b = " + insets.bottom);
            ScrollPaneWheelScroller.log.finer("vertScrollWidth = " + vScrollbarWidth);
        }
        if (insets.right >= vScrollbarWidth) {
            if (ScrollPaneWheelScroller.log.isLoggable(PlatformLogger.Level.FINER)) {
                ScrollPaneWheelScroller.log.finer("using vertical scrolling because scrollbar is present");
            }
            return scrollPane.getVAdjustable();
        }
        if (insets.bottom >= scrollPane.getHScrollbarHeight()) {
            if (ScrollPaneWheelScroller.log.isLoggable(PlatformLogger.Level.FINER)) {
                ScrollPaneWheelScroller.log.finer("using horiz scrolling because scrollbar is present");
            }
            return scrollPane.getHAdjustable();
        }
        if (ScrollPaneWheelScroller.log.isLoggable(PlatformLogger.Level.FINER)) {
            ScrollPaneWheelScroller.log.finer("using NO scrollbar becsause neither is present");
        }
        return null;
    }
    
    public static int getIncrementFromAdjustable(final Adjustable adjustable, final MouseWheelEvent mouseWheelEvent) {
        if (ScrollPaneWheelScroller.log.isLoggable(PlatformLogger.Level.FINE) && adjustable == null) {
            ScrollPaneWheelScroller.log.fine("Assertion (adj != null) failed");
        }
        int n = 0;
        if (mouseWheelEvent.getScrollType() == 0) {
            n = mouseWheelEvent.getUnitsToScroll() * adjustable.getUnitIncrement();
        }
        else if (mouseWheelEvent.getScrollType() == 1) {
            n = adjustable.getBlockIncrement() * mouseWheelEvent.getWheelRotation();
        }
        return n;
    }
    
    public static void scrollAdjustable(final Adjustable adjustable, final int n) {
        if (ScrollPaneWheelScroller.log.isLoggable(PlatformLogger.Level.FINE)) {
            if (adjustable == null) {
                ScrollPaneWheelScroller.log.fine("Assertion (adj != null) failed");
            }
            if (n == 0) {
                ScrollPaneWheelScroller.log.fine("Assertion (amount != 0) failed");
            }
        }
        final int value = adjustable.getValue();
        final int value2 = adjustable.getMaximum() - adjustable.getVisibleAmount();
        if (ScrollPaneWheelScroller.log.isLoggable(PlatformLogger.Level.FINER)) {
            ScrollPaneWheelScroller.log.finer("doScrolling by " + n);
        }
        if (n > 0 && value < value2) {
            if (value + n < value2) {
                adjustable.setValue(value + n);
                return;
            }
            adjustable.setValue(value2);
        }
        else {
            if (n >= 0 || value <= adjustable.getMinimum()) {
                return;
            }
            if (value + n > adjustable.getMinimum()) {
                adjustable.setValue(value + n);
                return;
            }
            adjustable.setValue(adjustable.getMinimum());
        }
    }
    
    static {
        log = PlatformLogger.getLogger("sun.awt.ScrollPaneWheelScroller");
    }
}
