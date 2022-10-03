package sun.awt.windows;

import java.awt.Point;
import java.awt.Cursor;
import java.awt.Component;
import sun.awt.GlobalCursorManager;

final class WGlobalCursorManager extends GlobalCursorManager
{
    private static WGlobalCursorManager manager;
    
    public static GlobalCursorManager getCursorManager() {
        if (WGlobalCursorManager.manager == null) {
            WGlobalCursorManager.manager = new WGlobalCursorManager();
        }
        return WGlobalCursorManager.manager;
    }
    
    public static void nativeUpdateCursor(final Component component) {
        getCursorManager().updateCursorLater(component);
    }
    
    @Override
    protected native void setCursor(final Component p0, final Cursor p1, final boolean p2);
    
    @Override
    protected native void getCursorPos(final Point p0);
    
    @Override
    protected native Component findHeavyweightUnderCursor(final boolean p0);
    
    @Override
    protected native Point getLocationOnScreen(final Component p0);
}
