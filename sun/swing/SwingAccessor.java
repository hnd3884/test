package sun.swing;

import javax.swing.TransferHandler;
import java.awt.Point;
import javax.swing.RepaintManager;
import javax.swing.text.JTextComponent;
import sun.misc.Unsafe;

public final class SwingAccessor
{
    private static final Unsafe unsafe;
    private static JTextComponentAccessor jtextComponentAccessor;
    private static JLightweightFrameAccessor jLightweightFrameAccessor;
    private static RepaintManagerAccessor repaintManagerAccessor;
    
    private SwingAccessor() {
    }
    
    public static void setJTextComponentAccessor(final JTextComponentAccessor jtextComponentAccessor) {
        SwingAccessor.jtextComponentAccessor = jtextComponentAccessor;
    }
    
    public static JTextComponentAccessor getJTextComponentAccessor() {
        if (SwingAccessor.jtextComponentAccessor == null) {
            SwingAccessor.unsafe.ensureClassInitialized(JTextComponent.class);
        }
        return SwingAccessor.jtextComponentAccessor;
    }
    
    public static void setJLightweightFrameAccessor(final JLightweightFrameAccessor jLightweightFrameAccessor) {
        SwingAccessor.jLightweightFrameAccessor = jLightweightFrameAccessor;
    }
    
    public static JLightweightFrameAccessor getJLightweightFrameAccessor() {
        if (SwingAccessor.jLightweightFrameAccessor == null) {
            SwingAccessor.unsafe.ensureClassInitialized(JLightweightFrame.class);
        }
        return SwingAccessor.jLightweightFrameAccessor;
    }
    
    public static void setRepaintManagerAccessor(final RepaintManagerAccessor repaintManagerAccessor) {
        SwingAccessor.repaintManagerAccessor = repaintManagerAccessor;
    }
    
    public static RepaintManagerAccessor getRepaintManagerAccessor() {
        if (SwingAccessor.repaintManagerAccessor == null) {
            SwingAccessor.unsafe.ensureClassInitialized(RepaintManager.class);
        }
        return SwingAccessor.repaintManagerAccessor;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
    }
    
    public interface RepaintManagerAccessor
    {
        void addRepaintListener(final RepaintManager p0, final SwingUtilities2.RepaintListener p1);
        
        void removeRepaintListener(final RepaintManager p0, final SwingUtilities2.RepaintListener p1);
    }
    
    public interface JLightweightFrameAccessor
    {
        void updateCursor(final JLightweightFrame p0);
    }
    
    public interface JTextComponentAccessor
    {
        TransferHandler.DropLocation dropLocationForPoint(final JTextComponent p0, final Point p1);
        
        Object setDropLocation(final JTextComponent p0, final TransferHandler.DropLocation p1, final Object p2, final boolean p3);
    }
}
