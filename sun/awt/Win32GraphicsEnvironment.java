package sun.awt;

import sun.java2d.SurfaceManagerFactory;
import sun.java2d.WindowsSurfaceManagerFactory;
import java.awt.GraphicsConfiguration;
import java.awt.peer.ComponentPeer;
import sun.java2d.d3d.D3DGraphicsDevice;
import sun.java2d.windows.WindowsFlags;
import java.util.ListIterator;
import sun.awt.windows.WToolkit;
import java.awt.AWTError;
import java.awt.GraphicsDevice;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import sun.java2d.SunGraphicsEnvironment;

public class Win32GraphicsEnvironment extends SunGraphicsEnvironment
{
    private static boolean displayInitialized;
    private ArrayList<WeakReference<Win32GraphicsDevice>> oldDevices;
    private static volatile boolean isDWMCompositionEnabled;
    
    private static native void initDisplay();
    
    public static void initDisplayWrapper() {
        if (!Win32GraphicsEnvironment.displayInitialized) {
            Win32GraphicsEnvironment.displayInitialized = true;
            initDisplay();
        }
    }
    
    @Override
    protected native int getNumScreens();
    
    protected native int getDefaultScreen();
    
    @Override
    public GraphicsDevice getDefaultScreenDevice() {
        final GraphicsDevice[] screenDevices = this.getScreenDevices();
        if (screenDevices.length == 0) {
            throw new AWTError("no screen devices");
        }
        final int defaultScreen = this.getDefaultScreen();
        return screenDevices[(0 < defaultScreen && defaultScreen < screenDevices.length) ? defaultScreen : false];
    }
    
    public native int getXResolution();
    
    public native int getYResolution();
    
    @Override
    public void displayChanged() {
        final GraphicsDevice[] screens = new GraphicsDevice[this.getNumScreens()];
        final GraphicsDevice[] screens2 = this.screens;
        if (screens2 != null) {
            for (int i = 0; i < screens2.length; ++i) {
                if (!(this.screens[i] instanceof Win32GraphicsDevice)) {
                    assert false : screens2[i];
                }
                else {
                    final Win32GraphicsDevice win32GraphicsDevice = (Win32GraphicsDevice)screens2[i];
                    if (!win32GraphicsDevice.isValid()) {
                        if (this.oldDevices == null) {
                            this.oldDevices = new ArrayList<WeakReference<Win32GraphicsDevice>>();
                        }
                        this.oldDevices.add(new WeakReference<Win32GraphicsDevice>(win32GraphicsDevice));
                    }
                    else if (i < screens.length) {
                        screens[i] = win32GraphicsDevice;
                    }
                }
            }
        }
        for (int j = 0; j < screens.length; ++j) {
            if (screens[j] == null) {
                screens[j] = this.makeScreenDevice(j);
            }
        }
        this.screens = screens;
        for (final GraphicsDevice graphicsDevice : this.screens) {
            if (graphicsDevice instanceof DisplayChangedListener) {
                ((DisplayChangedListener)graphicsDevice).displayChanged();
            }
        }
        if (this.oldDevices != null) {
            final int defaultScreen = this.getDefaultScreen();
            final ListIterator<WeakReference<Win32GraphicsDevice>> listIterator = this.oldDevices.listIterator();
            while (listIterator.hasNext()) {
                final Win32GraphicsDevice win32GraphicsDevice2 = listIterator.next().get();
                if (win32GraphicsDevice2 != null) {
                    win32GraphicsDevice2.invalidate(defaultScreen);
                    win32GraphicsDevice2.displayChanged();
                }
                else {
                    listIterator.remove();
                }
            }
        }
        WToolkit.resetGC();
        this.displayChanger.notifyListeners();
    }
    
    @Override
    protected GraphicsDevice makeScreenDevice(final int n) {
        GraphicsDevice device = null;
        if (WindowsFlags.isD3DEnabled()) {
            device = D3DGraphicsDevice.createDevice(n);
        }
        if (device == null) {
            device = new Win32GraphicsDevice(n);
        }
        return device;
    }
    
    @Override
    public boolean isDisplayLocal() {
        return true;
    }
    
    @Override
    public boolean isFlipStrategyPreferred(final ComponentPeer componentPeer) {
        final GraphicsConfiguration graphicsConfiguration;
        if (componentPeer != null && (graphicsConfiguration = componentPeer.getGraphicsConfiguration()) != null) {
            final GraphicsDevice device = graphicsConfiguration.getDevice();
            if (device instanceof D3DGraphicsDevice) {
                return ((D3DGraphicsDevice)device).isD3DEnabledOnDevice();
            }
        }
        return false;
    }
    
    public static boolean isDWMCompositionEnabled() {
        return Win32GraphicsEnvironment.isDWMCompositionEnabled;
    }
    
    private static void dwmCompositionChanged(final boolean isDWMCompositionEnabled) {
        Win32GraphicsEnvironment.isDWMCompositionEnabled = isDWMCompositionEnabled;
    }
    
    public static native boolean isVistaOS();
    
    static {
        WToolkit.loadLibraries();
        WindowsFlags.initFlags();
        initDisplayWrapper();
        SurfaceManagerFactory.setInstance(new WindowsSurfaceManagerFactory());
    }
}
