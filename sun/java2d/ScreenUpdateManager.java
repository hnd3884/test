package sun.java2d;

import sun.java2d.d3d.D3DScreenUpdateManager;
import sun.java2d.windows.WindowsFlags;
import sun.awt.Win32GraphicsConfig;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import sun.awt.windows.WComponentPeer;

public class ScreenUpdateManager
{
    private static ScreenUpdateManager theInstance;
    
    protected ScreenUpdateManager() {
    }
    
    public synchronized Graphics2D createGraphics(final SurfaceData surfaceData, final WComponentPeer wComponentPeer, final Color color, final Color color2, final Font font) {
        return new SunGraphics2D(surfaceData, color, color2, font);
    }
    
    public SurfaceData createScreenSurface(final Win32GraphicsConfig win32GraphicsConfig, final WComponentPeer wComponentPeer, final int n, final boolean b) {
        return win32GraphicsConfig.createSurfaceData(wComponentPeer, n);
    }
    
    public void dropScreenSurface(final SurfaceData surfaceData) {
    }
    
    public SurfaceData getReplacementScreenSurface(final WComponentPeer wComponentPeer, final SurfaceData surfaceData) {
        final SurfaceData surfaceData2 = wComponentPeer.getSurfaceData();
        if (surfaceData2 == null || surfaceData2.isValid()) {
            return surfaceData2;
        }
        wComponentPeer.replaceSurfaceData();
        return wComponentPeer.getSurfaceData();
    }
    
    public static synchronized ScreenUpdateManager getInstance() {
        if (ScreenUpdateManager.theInstance == null) {
            if (WindowsFlags.isD3DEnabled()) {
                ScreenUpdateManager.theInstance = new D3DScreenUpdateManager();
            }
            else {
                ScreenUpdateManager.theInstance = new ScreenUpdateManager();
            }
        }
        return ScreenUpdateManager.theInstance;
    }
}
