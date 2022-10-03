package javax.swing;

import sun.security.action.GetBooleanAction;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.awt.AppContext;
import sun.awt.event.IgnorePaintEvent;
import java.awt.Rectangle;
import java.awt.Container;
import sun.awt.SunToolkit;
import java.awt.event.PaintEvent;
import java.awt.Component;
import sun.awt.PaintEventDispatcher;

class SwingPaintEventDispatcher extends PaintEventDispatcher
{
    private static final boolean SHOW_FROM_DOUBLE_BUFFER;
    private static final boolean ERASE_BACKGROUND;
    
    @Override
    public PaintEvent createPaintEvent(final Component component, final int n, final int n2, final int n3, final int n4) {
        if (component instanceof RootPaneContainer) {
            final AppContext targetToAppContext = SunToolkit.targetToAppContext(component);
            final RepaintManager currentManager = RepaintManager.currentManager(targetToAppContext);
            if (!SwingPaintEventDispatcher.SHOW_FROM_DOUBLE_BUFFER || !currentManager.show((Container)component, n, n2, n3, n4)) {
                currentManager.nativeAddDirtyRegion(targetToAppContext, (Container)component, n, n2, n3, n4);
            }
            return new IgnorePaintEvent(component, 800, new Rectangle(n, n2, n3, n4));
        }
        if (component instanceof SwingHeavyWeight) {
            final AppContext targetToAppContext2 = SunToolkit.targetToAppContext(component);
            RepaintManager.currentManager(targetToAppContext2).nativeAddDirtyRegion(targetToAppContext2, (Container)component, n, n2, n3, n4);
            return new IgnorePaintEvent(component, 800, new Rectangle(n, n2, n3, n4));
        }
        return super.createPaintEvent(component, n, n2, n3, n4);
    }
    
    @Override
    public boolean shouldDoNativeBackgroundErase(final Component component) {
        return SwingPaintEventDispatcher.ERASE_BACKGROUND || !(component instanceof RootPaneContainer);
    }
    
    @Override
    public boolean queueSurfaceDataReplacing(final Component component, final Runnable runnable) {
        if (component instanceof RootPaneContainer) {
            final AppContext targetToAppContext = SunToolkit.targetToAppContext(component);
            RepaintManager.currentManager(targetToAppContext).nativeQueueSurfaceDataRunnable(targetToAppContext, component, runnable);
            return true;
        }
        return super.queueSurfaceDataReplacing(component, runnable);
    }
    
    static {
        SHOW_FROM_DOUBLE_BUFFER = "true".equals(AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("swing.showFromDoubleBuffer", "true")));
        ERASE_BACKGROUND = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("swing.nativeErase"));
    }
}
