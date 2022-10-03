package sun.java2d.d3d;

import java.awt.Container;
import sun.awt.AWTAccessor;
import sun.java2d.SunGraphics2D;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import java.awt.Window;
import java.awt.Rectangle;
import sun.java2d.windows.WindowsFlags;
import java.awt.Component;
import sun.java2d.InvalidPipeException;
import sun.java2d.SurfaceData;
import sun.awt.windows.WComponentPeer;
import sun.awt.Win32GraphicsConfig;
import java.security.AccessController;
import sun.misc.ThreadGroupUtils;
import sun.java2d.windows.GDIWindowSurfaceData;
import java.util.HashMap;
import java.util.ArrayList;
import sun.java2d.ScreenUpdateManager;

public class D3DScreenUpdateManager extends ScreenUpdateManager implements Runnable
{
    private static final int MIN_WIN_SIZE = 150;
    private volatile boolean done;
    private volatile Thread screenUpdater;
    private boolean needsUpdateNow;
    private Object runLock;
    private ArrayList<D3DSurfaceData.D3DWindowSurfaceData> d3dwSurfaces;
    private HashMap<D3DSurfaceData.D3DWindowSurfaceData, GDIWindowSurfaceData> gdiSurfaces;
    
    public D3DScreenUpdateManager() {
        this.runLock = new Object();
        this.done = false;
        AccessController.doPrivileged(() -> {
            final Thread thread = new Thread(ThreadGroupUtils.getRootThreadGroup(), () -> {
                this.done = (1 != 0);
                this.wakeUpUpdateThread();
                return;
            });
            thread.setContextClassLoader(null);
            try {
                Runtime.getRuntime().addShutdownHook(thread);
            }
            catch (final Exception ex) {
                this.done = true;
            }
            return null;
        });
    }
    
    @Override
    public SurfaceData createScreenSurface(final Win32GraphicsConfig win32GraphicsConfig, final WComponentPeer wComponentPeer, final int n, final boolean b) {
        if (this.done || !(win32GraphicsConfig instanceof D3DGraphicsConfig)) {
            return super.createScreenSurface(win32GraphicsConfig, wComponentPeer, n, b);
        }
        SurfaceData surfaceData = null;
        if (canUseD3DOnScreen(wComponentPeer, win32GraphicsConfig, n)) {
            try {
                surfaceData = D3DSurfaceData.createData(wComponentPeer);
            }
            catch (final InvalidPipeException ex) {
                surfaceData = null;
            }
        }
        if (surfaceData == null) {
            surfaceData = GDIWindowSurfaceData.createData(wComponentPeer);
        }
        if (b) {
            this.repaintPeerTarget(wComponentPeer);
        }
        return surfaceData;
    }
    
    public static boolean canUseD3DOnScreen(final WComponentPeer wComponentPeer, final Win32GraphicsConfig win32GraphicsConfig, final int n) {
        if (!(win32GraphicsConfig instanceof D3DGraphicsConfig)) {
            return false;
        }
        final D3DGraphicsDevice d3DDevice = ((D3DGraphicsConfig)win32GraphicsConfig).getD3DDevice();
        final String name = wComponentPeer.getClass().getName();
        final Rectangle bounds = wComponentPeer.getBounds();
        final Component component = (Component)wComponentPeer.getTarget();
        final Window fullScreenWindow = d3DDevice.getFullScreenWindow();
        return WindowsFlags.isD3DOnScreenEnabled() && d3DDevice.isD3DEnabledOnDevice() && wComponentPeer.isAccelCapable() && (bounds.width > 150 || bounds.height > 150) && n == 0 && (fullScreenWindow == null || (fullScreenWindow == component && !hasHWChildren(component))) && (name.equals("sun.awt.windows.WCanvasPeer") || name.equals("sun.awt.windows.WDialogPeer") || name.equals("sun.awt.windows.WPanelPeer") || name.equals("sun.awt.windows.WWindowPeer") || name.equals("sun.awt.windows.WFramePeer") || name.equals("sun.awt.windows.WEmbeddedFramePeer"));
    }
    
    @Override
    public Graphics2D createGraphics(SurfaceData gdiSurface, final WComponentPeer wComponentPeer, final Color color, final Color color2, final Font font) {
        if (!this.done && gdiSurface instanceof D3DSurfaceData.D3DWindowSurfaceData) {
            final D3DSurfaceData.D3DWindowSurfaceData d3DWindowSurfaceData = (D3DSurfaceData.D3DWindowSurfaceData)gdiSurface;
            if (!d3DWindowSurfaceData.isSurfaceLost() || this.validate(d3DWindowSurfaceData)) {
                this.trackScreenSurface(d3DWindowSurfaceData);
                return new SunGraphics2D(gdiSurface, color, color2, font);
            }
            gdiSurface = this.getGdiSurface(d3DWindowSurfaceData);
        }
        return super.createGraphics(gdiSurface, wComponentPeer, color, color2, font);
    }
    
    private void repaintPeerTarget(final WComponentPeer wComponentPeer) {
        final Rectangle bounds = AWTAccessor.getComponentAccessor().getBounds((Component)wComponentPeer.getTarget());
        wComponentPeer.handlePaint(0, 0, bounds.width, bounds.height);
    }
    
    private void trackScreenSurface(final SurfaceData surfaceData) {
        if (!this.done && surfaceData instanceof D3DSurfaceData.D3DWindowSurfaceData) {
            synchronized (this) {
                if (this.d3dwSurfaces == null) {
                    this.d3dwSurfaces = new ArrayList<D3DSurfaceData.D3DWindowSurfaceData>();
                }
                final D3DSurfaceData.D3DWindowSurfaceData d3DWindowSurfaceData = (D3DSurfaceData.D3DWindowSurfaceData)surfaceData;
                if (!this.d3dwSurfaces.contains(d3DWindowSurfaceData)) {
                    this.d3dwSurfaces.add(d3DWindowSurfaceData);
                }
            }
            this.startUpdateThread();
        }
    }
    
    @Override
    public synchronized void dropScreenSurface(final SurfaceData surfaceData) {
        if (this.d3dwSurfaces != null && surfaceData instanceof D3DSurfaceData.D3DWindowSurfaceData) {
            final D3DSurfaceData.D3DWindowSurfaceData d3DWindowSurfaceData = (D3DSurfaceData.D3DWindowSurfaceData)surfaceData;
            this.removeGdiSurface(d3DWindowSurfaceData);
            this.d3dwSurfaces.remove(d3DWindowSurfaceData);
        }
    }
    
    @Override
    public SurfaceData getReplacementScreenSurface(final WComponentPeer wComponentPeer, final SurfaceData surfaceData) {
        final SurfaceData replacementScreenSurface = super.getReplacementScreenSurface(wComponentPeer, surfaceData);
        this.trackScreenSurface(replacementScreenSurface);
        return replacementScreenSurface;
    }
    
    private void removeGdiSurface(final D3DSurfaceData.D3DWindowSurfaceData d3DWindowSurfaceData) {
        if (this.gdiSurfaces != null) {
            final GDIWindowSurfaceData gdiWindowSurfaceData = this.gdiSurfaces.get(d3DWindowSurfaceData);
            if (gdiWindowSurfaceData != null) {
                gdiWindowSurfaceData.invalidate();
                this.gdiSurfaces.remove(d3DWindowSurfaceData);
            }
        }
    }
    
    private synchronized void startUpdateThread() {
        if (this.screenUpdater == null) {
            (this.screenUpdater = AccessController.doPrivileged(() -> {
                final Thread thread = new Thread(ThreadGroupUtils.getRootThreadGroup(), this, "D3D Screen Updater");
                thread.setPriority(7);
                thread.setDaemon(true);
                return thread;
            })).start();
        }
        else {
            this.wakeUpUpdateThread();
        }
    }
    
    public void wakeUpUpdateThread() {
        synchronized (this.runLock) {
            this.runLock.notifyAll();
        }
    }
    
    public void runUpdateNow() {
        synchronized (this) {
            if (this.done || this.screenUpdater == null || this.d3dwSurfaces == null || this.d3dwSurfaces.size() == 0) {
                return;
            }
        }
        synchronized (this.runLock) {
            this.needsUpdateNow = true;
            this.runLock.notifyAll();
            while (this.needsUpdateNow) {
                try {
                    this.runLock.wait();
                }
                catch (final InterruptedException ex) {}
            }
        }
    }
    
    @Override
    public void run() {
        while (!this.done) {
            synchronized (this.runLock) {
                final long n = (this.d3dwSurfaces.size() > 0) ? 100L : 0L;
                if (!this.needsUpdateNow) {
                    try {
                        this.runLock.wait(n);
                    }
                    catch (final InterruptedException ex) {}
                }
            }
            D3DSurfaceData.D3DWindowSurfaceData[] array = new D3DSurfaceData.D3DWindowSurfaceData[0];
            synchronized (this) {
                array = this.d3dwSurfaces.toArray(array);
            }
            for (final D3DSurfaceData.D3DWindowSurfaceData d3DWindowSurfaceData : array) {
                if (d3DWindowSurfaceData.isValid() && (d3DWindowSurfaceData.isDirty() || d3DWindowSurfaceData.isSurfaceLost())) {
                    if (!d3DWindowSurfaceData.isSurfaceLost()) {
                        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
                        instance.lock();
                        try {
                            final Rectangle bounds = d3DWindowSurfaceData.getBounds();
                            D3DSurfaceData.swapBuffers(d3DWindowSurfaceData, 0, 0, bounds.width, bounds.height);
                            d3DWindowSurfaceData.markClean();
                        }
                        finally {
                            instance.unlock();
                        }
                    }
                    else if (!this.validate(d3DWindowSurfaceData)) {
                        d3DWindowSurfaceData.getPeer().replaceSurfaceDataLater();
                    }
                }
            }
            synchronized (this.runLock) {
                this.needsUpdateNow = false;
                this.runLock.notifyAll();
            }
        }
    }
    
    private boolean validate(final D3DSurfaceData.D3DWindowSurfaceData d3DWindowSurfaceData) {
        if (d3DWindowSurfaceData.isSurfaceLost()) {
            try {
                d3DWindowSurfaceData.restoreSurface();
                final Color backgroundNoSync = d3DWindowSurfaceData.getPeer().getBackgroundNoSync();
                final SunGraphics2D sunGraphics2D = new SunGraphics2D(d3DWindowSurfaceData, backgroundNoSync, backgroundNoSync, null);
                sunGraphics2D.fillRect(0, 0, d3DWindowSurfaceData.getBounds().width, d3DWindowSurfaceData.getBounds().height);
                sunGraphics2D.dispose();
                d3DWindowSurfaceData.markClean();
                this.repaintPeerTarget(d3DWindowSurfaceData.getPeer());
            }
            catch (final InvalidPipeException ex) {
                return false;
            }
        }
        return true;
    }
    
    private synchronized SurfaceData getGdiSurface(final D3DSurfaceData.D3DWindowSurfaceData d3DWindowSurfaceData) {
        if (this.gdiSurfaces == null) {
            this.gdiSurfaces = new HashMap<D3DSurfaceData.D3DWindowSurfaceData, GDIWindowSurfaceData>();
        }
        GDIWindowSurfaceData data = this.gdiSurfaces.get(d3DWindowSurfaceData);
        if (data == null) {
            data = GDIWindowSurfaceData.createData(d3DWindowSurfaceData.getPeer());
            this.gdiSurfaces.put(d3DWindowSurfaceData, data);
        }
        return data;
    }
    
    private static boolean hasHWChildren(final Component component) {
        if (component instanceof Container) {
            for (final Component component2 : ((Container)component).getComponents()) {
                if (component2.getPeer() instanceof WComponentPeer || hasHWChildren(component2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
