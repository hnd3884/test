package sun.awt;

import java.awt.event.WindowEvent;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.awt.Rectangle;
import java.awt.peer.WindowPeer;
import sun.awt.windows.WWindowPeer;
import java.awt.Window;
import java.security.Permission;
import java.awt.GraphicsEnvironment;
import sun.java2d.opengl.WGLGraphicsConfig;
import java.util.Vector;
import sun.java2d.windows.WindowsFlags;
import java.awt.event.WindowListener;
import java.awt.DisplayMode;
import java.awt.AWTPermission;
import java.awt.GraphicsConfiguration;
import java.awt.image.ColorModel;
import java.awt.GraphicsDevice;

public class Win32GraphicsDevice extends GraphicsDevice implements DisplayChangedListener
{
    int screen;
    ColorModel dynamicColorModel;
    ColorModel colorModel;
    protected GraphicsConfiguration[] configs;
    protected GraphicsConfiguration defaultConfig;
    private final String idString;
    protected String descString;
    private boolean valid;
    private SunDisplayChanger topLevels;
    protected static boolean pfDisabled;
    private static AWTPermission fullScreenExclusivePermission;
    private DisplayMode defaultDisplayMode;
    private WindowListener fsWindowListener;
    
    private static native void initIDs();
    
    native void initDevice(final int p0);
    
    public Win32GraphicsDevice(final int screen) {
        this.topLevels = new SunDisplayChanger();
        this.screen = screen;
        this.idString = "\\Display" + this.screen;
        this.descString = "Win32GraphicsDevice[screen=" + this.screen;
        this.valid = true;
        this.initDevice(screen);
    }
    
    @Override
    public int getType() {
        return 0;
    }
    
    public int getScreen() {
        return this.screen;
    }
    
    public boolean isValid() {
        return this.valid;
    }
    
    protected void invalidate(final int screen) {
        this.valid = false;
        this.screen = screen;
    }
    
    @Override
    public String getIDstring() {
        return this.idString;
    }
    
    @Override
    public GraphicsConfiguration[] getConfigurations() {
        if (this.configs == null) {
            if (WindowsFlags.isOGLEnabled() && this.isDefaultDevice()) {
                this.defaultConfig = this.getDefaultConfiguration();
                if (this.defaultConfig != null) {
                    (this.configs = new GraphicsConfiguration[1])[0] = this.defaultConfig;
                    return this.configs.clone();
                }
            }
            final int maxConfigs = this.getMaxConfigs(this.screen);
            final int defaultPixID = this.getDefaultPixID(this.screen);
            final Vector vector = new Vector<Win32GraphicsConfig>(maxConfigs);
            if (defaultPixID == 0) {
                vector.addElement(this.defaultConfig = Win32GraphicsConfig.getConfig(this, defaultPixID));
            }
            else {
                for (int i = 1; i <= maxConfigs; ++i) {
                    if (this.isPixFmtSupported(i, this.screen)) {
                        if (i == defaultPixID) {
                            vector.addElement((Win32GraphicsConfig)(this.defaultConfig = Win32GraphicsConfig.getConfig(this, i)));
                        }
                        else {
                            vector.addElement(Win32GraphicsConfig.getConfig(this, i));
                        }
                    }
                }
            }
            vector.copyInto(this.configs = new GraphicsConfiguration[vector.size()]);
        }
        return this.configs.clone();
    }
    
    protected int getMaxConfigs(final int n) {
        if (Win32GraphicsDevice.pfDisabled) {
            return 1;
        }
        return this.getMaxConfigsImpl(n);
    }
    
    private native int getMaxConfigsImpl(final int p0);
    
    protected native boolean isPixFmtSupported(final int p0, final int p1);
    
    protected int getDefaultPixID(final int n) {
        if (Win32GraphicsDevice.pfDisabled) {
            return 0;
        }
        return this.getDefaultPixIDImpl(n);
    }
    
    private native int getDefaultPixIDImpl(final int p0);
    
    @Override
    public GraphicsConfiguration getDefaultConfiguration() {
        if (this.defaultConfig == null) {
            if (WindowsFlags.isOGLEnabled() && this.isDefaultDevice()) {
                this.defaultConfig = WGLGraphicsConfig.getConfig(this, WGLGraphicsConfig.getDefaultPixFmt(this.screen));
                if (WindowsFlags.isOGLVerbose()) {
                    if (this.defaultConfig != null) {
                        System.out.print("OpenGL pipeline enabled");
                    }
                    else {
                        System.out.print("Could not enable OpenGL pipeline");
                    }
                    System.out.println(" for default config on screen " + this.screen);
                }
            }
            if (this.defaultConfig == null) {
                this.defaultConfig = Win32GraphicsConfig.getConfig(this, 0);
            }
        }
        return this.defaultConfig;
    }
    
    @Override
    public String toString() {
        return this.valid ? (this.descString + "]") : (this.descString + ", removed]");
    }
    
    private boolean isDefaultDevice() {
        return this == GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }
    
    private static boolean isFSExclusiveModeAllowed() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            if (Win32GraphicsDevice.fullScreenExclusivePermission == null) {
                Win32GraphicsDevice.fullScreenExclusivePermission = new AWTPermission("fullScreenExclusive");
            }
            try {
                securityManager.checkPermission(Win32GraphicsDevice.fullScreenExclusivePermission);
            }
            catch (final SecurityException ex) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isFullScreenSupported() {
        return isFSExclusiveModeAllowed();
    }
    
    @Override
    public synchronized void setFullScreenWindow(final Window window) {
        final Window fullScreenWindow = this.getFullScreenWindow();
        if (window == fullScreenWindow) {
            return;
        }
        if (!this.isFullScreenSupported()) {
            super.setFullScreenWindow(window);
            return;
        }
        if (fullScreenWindow != null) {
            if (this.defaultDisplayMode != null) {
                this.setDisplayMode(this.defaultDisplayMode);
                this.defaultDisplayMode = null;
            }
            final WWindowPeer wWindowPeer = (WWindowPeer)fullScreenWindow.getPeer();
            if (wWindowPeer != null) {
                wWindowPeer.setFullScreenExclusiveModeState(false);
                synchronized (wWindowPeer) {
                    this.exitFullScreenExclusive(this.screen, wWindowPeer);
                }
            }
            this.removeFSWindowListener(fullScreenWindow);
        }
        super.setFullScreenWindow(window);
        if (window != null) {
            this.defaultDisplayMode = this.getDisplayMode();
            this.addFSWindowListener(window);
            final WWindowPeer wWindowPeer2 = (WWindowPeer)window.getPeer();
            if (wWindowPeer2 != null) {
                synchronized (wWindowPeer2) {
                    this.enterFullScreenExclusive(this.screen, wWindowPeer2);
                }
                wWindowPeer2.setFullScreenExclusiveModeState(true);
            }
            wWindowPeer2.updateGC();
        }
    }
    
    protected native void enterFullScreenExclusive(final int p0, final WindowPeer p1);
    
    protected native void exitFullScreenExclusive(final int p0, final WindowPeer p1);
    
    @Override
    public boolean isDisplayChangeSupported() {
        return this.isFullScreenSupported() && this.getFullScreenWindow() != null;
    }
    
    @Override
    public synchronized void setDisplayMode(DisplayMode matchingDisplayMode) {
        if (!this.isDisplayChangeSupported()) {
            super.setDisplayMode(matchingDisplayMode);
            return;
        }
        if (matchingDisplayMode == null || (matchingDisplayMode = this.getMatchingDisplayMode(matchingDisplayMode)) == null) {
            throw new IllegalArgumentException("Invalid display mode");
        }
        if (this.getDisplayMode().equals(matchingDisplayMode)) {
            return;
        }
        final Window fullScreenWindow = this.getFullScreenWindow();
        if (fullScreenWindow != null) {
            this.configDisplayMode(this.screen, (WindowPeer)fullScreenWindow.getPeer(), matchingDisplayMode.getWidth(), matchingDisplayMode.getHeight(), matchingDisplayMode.getBitDepth(), matchingDisplayMode.getRefreshRate());
            final Rectangle bounds = this.getDefaultConfiguration().getBounds();
            fullScreenWindow.setBounds(bounds.x, bounds.y, matchingDisplayMode.getWidth(), matchingDisplayMode.getHeight());
            return;
        }
        throw new IllegalStateException("Must be in fullscreen mode in order to set display mode");
    }
    
    protected native DisplayMode getCurrentDisplayMode(final int p0);
    
    protected native void configDisplayMode(final int p0, final WindowPeer p1, final int p2, final int p3, final int p4, final int p5);
    
    protected native void enumDisplayModes(final int p0, final ArrayList p1);
    
    @Override
    public synchronized DisplayMode getDisplayMode() {
        return this.getCurrentDisplayMode(this.screen);
    }
    
    @Override
    public synchronized DisplayMode[] getDisplayModes() {
        final ArrayList list = new ArrayList();
        this.enumDisplayModes(this.screen, list);
        final int size = list.size();
        final DisplayMode[] array = new DisplayMode[size];
        for (int i = 0; i < size; ++i) {
            array[i] = (DisplayMode)list.get(i);
        }
        return array;
    }
    
    protected synchronized DisplayMode getMatchingDisplayMode(final DisplayMode displayMode) {
        if (!this.isDisplayChangeSupported()) {
            return null;
        }
        for (final DisplayMode displayMode2 : this.getDisplayModes()) {
            if (displayMode.equals(displayMode2) || (displayMode.getRefreshRate() == 0 && displayMode.getWidth() == displayMode2.getWidth() && displayMode.getHeight() == displayMode2.getHeight() && displayMode.getBitDepth() == displayMode2.getBitDepth())) {
                return displayMode2;
            }
        }
        return null;
    }
    
    @Override
    public void displayChanged() {
        this.dynamicColorModel = null;
        this.defaultConfig = null;
        this.configs = null;
        this.topLevels.notifyListeners();
    }
    
    @Override
    public void paletteChanged() {
    }
    
    public void addDisplayChangedListener(final DisplayChangedListener displayChangedListener) {
        this.topLevels.add(displayChangedListener);
    }
    
    public void removeDisplayChangedListener(final DisplayChangedListener displayChangedListener) {
        this.topLevels.remove(displayChangedListener);
    }
    
    private native ColorModel makeColorModel(final int p0, final boolean p1);
    
    public ColorModel getDynamicColorModel() {
        if (this.dynamicColorModel == null) {
            this.dynamicColorModel = this.makeColorModel(this.screen, true);
        }
        return this.dynamicColorModel;
    }
    
    public ColorModel getColorModel() {
        if (this.colorModel == null) {
            this.colorModel = this.makeColorModel(this.screen, false);
        }
        return this.colorModel;
    }
    
    protected void addFSWindowListener(final Window window) {
        this.fsWindowListener = new Win32FSWindowAdapter(this);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                window.addWindowListener(Win32GraphicsDevice.this.fsWindowListener);
            }
        });
    }
    
    protected void removeFSWindowListener(final Window window) {
        window.removeWindowListener(this.fsWindowListener);
        this.fsWindowListener = null;
    }
    
    static {
        Win32GraphicsDevice.pfDisabled = (AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.awt.nopixfmt")) != null);
        initIDs();
    }
    
    private static class Win32FSWindowAdapter extends WindowAdapter
    {
        private Win32GraphicsDevice device;
        private DisplayMode dm;
        
        Win32FSWindowAdapter(final Win32GraphicsDevice device) {
            this.device = device;
        }
        
        private void setFSWindowsState(final Window window, final int extendedState) {
            final GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            if (window != null) {
                final GraphicsDevice[] array = screenDevices;
                for (int length = array.length, i = 0; i < length; ++i) {
                    if (window == array[i].getFullScreenWindow()) {
                        return;
                    }
                }
            }
            final GraphicsDevice[] array2 = screenDevices;
            for (int length2 = array2.length, j = 0; j < length2; ++j) {
                final Window fullScreenWindow = array2[j].getFullScreenWindow();
                if (fullScreenWindow instanceof Frame) {
                    ((Frame)fullScreenWindow).setExtendedState(extendedState);
                }
            }
        }
        
        @Override
        public void windowDeactivated(final WindowEvent windowEvent) {
            this.setFSWindowsState(windowEvent.getOppositeWindow(), 1);
        }
        
        @Override
        public void windowActivated(final WindowEvent windowEvent) {
            this.setFSWindowsState(windowEvent.getOppositeWindow(), 0);
        }
        
        @Override
        public void windowIconified(final WindowEvent windowEvent) {
            final DisplayMode access$000 = this.device.defaultDisplayMode;
            if (access$000 != null) {
                this.dm = this.device.getDisplayMode();
                this.device.setDisplayMode(access$000);
            }
        }
        
        @Override
        public void windowDeiconified(final WindowEvent windowEvent) {
            if (this.dm != null) {
                this.device.setDisplayMode(this.dm);
                this.dm = null;
            }
        }
    }
}
