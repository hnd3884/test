package sun.awt;

import java.awt.Rectangle;
import java.awt.event.PaintEvent;
import java.awt.Component;

public class PaintEventDispatcher
{
    private static PaintEventDispatcher dispatcher;
    
    public static void setPaintEventDispatcher(final PaintEventDispatcher dispatcher) {
        synchronized (PaintEventDispatcher.class) {
            PaintEventDispatcher.dispatcher = dispatcher;
        }
    }
    
    public static PaintEventDispatcher getPaintEventDispatcher() {
        synchronized (PaintEventDispatcher.class) {
            if (PaintEventDispatcher.dispatcher == null) {
                PaintEventDispatcher.dispatcher = new PaintEventDispatcher();
            }
            return PaintEventDispatcher.dispatcher;
        }
    }
    
    public PaintEvent createPaintEvent(final Component component, final int n, final int n2, final int n3, final int n4) {
        return new PaintEvent(component, 800, new Rectangle(n, n2, n3, n4));
    }
    
    public boolean shouldDoNativeBackgroundErase(final Component component) {
        return true;
    }
    
    public boolean queueSurfaceDataReplacing(final Component component, final Runnable runnable) {
        return false;
    }
}
