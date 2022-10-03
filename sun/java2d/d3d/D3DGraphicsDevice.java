package sun.java2d.d3d;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import sun.misc.PerfCounter;
import java.awt.Toolkit;
import java.awt.GraphicsConfiguration;
import java.util.ArrayList;
import java.awt.DisplayMode;
import java.awt.Dialog;
import sun.awt.windows.WWindowPeer;
import java.awt.peer.WindowPeer;
import java.awt.Frame;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.windows.WindowsFlags;
import java.awt.event.WindowListener;
import java.awt.Window;
import java.awt.Rectangle;
import sun.java2d.pipe.hw.ContextCapabilities;
import sun.awt.Win32GraphicsDevice;

public class D3DGraphicsDevice extends Win32GraphicsDevice
{
    private D3DContext context;
    private static boolean d3dAvailable;
    private ContextCapabilities d3dCaps;
    private boolean fsStatus;
    private Rectangle ownerOrigBounds;
    private boolean ownerWasVisible;
    private Window realFSWindow;
    private WindowListener fsWindowListener;
    private boolean fsWindowWasAlwaysOnTop;
    
    private static native boolean initD3D();
    
    public static D3DGraphicsDevice createDevice(final int n) {
        if (!D3DGraphicsDevice.d3dAvailable) {
            return null;
        }
        final ContextCapabilities deviceCaps = getDeviceCaps(n);
        if ((deviceCaps.getCaps() & 0x40000) == 0x0) {
            if (WindowsFlags.isD3DVerbose()) {
                System.out.println("Could not enable Direct3D pipeline on screen " + n);
            }
            return null;
        }
        if (WindowsFlags.isD3DVerbose()) {
            System.out.println("Direct3D pipeline enabled on screen " + n);
        }
        return new D3DGraphicsDevice(n, deviceCaps);
    }
    
    private static native int getDeviceCapsNative(final int p0);
    
    private static native String getDeviceIdNative(final int p0);
    
    private static ContextCapabilities getDeviceCaps(final int n) {
        D3DContext.D3DContextCaps d3DContextCaps = null;
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            class Result
            {
                int caps;
                String id;
            }
            final Result result = new Result();
            instance.flushAndInvokeNow(new Runnable() {
                @Override
                public void run() {
                    result.caps = getDeviceCapsNative(n);
                    result.id = getDeviceIdNative(n);
                }
            });
            d3DContextCaps = new D3DContext.D3DContextCaps(result.caps, result.id);
        }
        finally {
            instance.unlock();
        }
        return (d3DContextCaps != null) ? d3DContextCaps : new D3DContext.D3DContextCaps(0, null);
    }
    
    public final boolean isCapPresent(final int n) {
        return (this.d3dCaps.getCaps() & n) != 0x0;
    }
    
    private D3DGraphicsDevice(final int n, final ContextCapabilities d3dCaps) {
        super(n);
        this.ownerOrigBounds = null;
        this.descString = "D3DGraphicsDevice[screen=" + n;
        this.d3dCaps = d3dCaps;
        this.context = new D3DContext(D3DRenderQueue.getInstance(), this);
    }
    
    public boolean isD3DEnabledOnDevice() {
        return this.isValid() && this.isCapPresent(262144);
    }
    
    public static boolean isD3DAvailable() {
        return D3DGraphicsDevice.d3dAvailable;
    }
    
    private Frame getToplevelOwner(final Window window) {
        Window owner = window;
        while (owner != null) {
            owner = owner.getOwner();
            if (owner instanceof Frame) {
                return (Frame)owner;
            }
        }
        return null;
    }
    
    private static native boolean enterFullScreenExclusiveNative(final int p0, final long p1);
    
    @Override
    protected void enterFullScreenExclusive(final int n, final WindowPeer windowPeer) {
        final WWindowPeer wWindowPeer = (WWindowPeer)this.realFSWindow.getPeer();
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            instance.flushAndInvokeNow(new Runnable() {
                @Override
                public void run() {
                    final long hWnd = wWindowPeer.getHWnd();
                    if (hWnd == 0L) {
                        D3DGraphicsDevice.this.fsStatus = false;
                        return;
                    }
                    D3DGraphicsDevice.this.fsStatus = enterFullScreenExclusiveNative(n, hWnd);
                }
            });
        }
        finally {
            instance.unlock();
        }
        if (!this.fsStatus) {
            super.enterFullScreenExclusive(n, windowPeer);
        }
    }
    
    private static native boolean exitFullScreenExclusiveNative(final int p0);
    
    @Override
    protected void exitFullScreenExclusive(final int n, final WindowPeer windowPeer) {
        if (this.fsStatus) {
            final D3DRenderQueue instance = D3DRenderQueue.getInstance();
            instance.lock();
            try {
                instance.flushAndInvokeNow(new Runnable() {
                    @Override
                    public void run() {
                        exitFullScreenExclusiveNative(n);
                    }
                });
            }
            finally {
                instance.unlock();
            }
        }
        else {
            super.exitFullScreenExclusive(n, windowPeer);
        }
    }
    
    @Override
    protected void addFSWindowListener(final Window realFSWindow) {
        if (!(realFSWindow instanceof Frame) && !(realFSWindow instanceof Dialog) && (this.realFSWindow = this.getToplevelOwner(realFSWindow)) != null) {
            this.ownerOrigBounds = this.realFSWindow.getBounds();
            final WWindowPeer wWindowPeer = (WWindowPeer)this.realFSWindow.getPeer();
            this.ownerWasVisible = this.realFSWindow.isVisible();
            final Rectangle bounds = realFSWindow.getBounds();
            wWindowPeer.reshape(bounds.x, bounds.y, bounds.width, bounds.height);
            wWindowPeer.setVisible(true);
        }
        else {
            this.realFSWindow = realFSWindow;
        }
        this.fsWindowWasAlwaysOnTop = this.realFSWindow.isAlwaysOnTop();
        ((WWindowPeer)this.realFSWindow.getPeer()).setAlwaysOnTop(true);
        this.fsWindowListener = new D3DFSWindowAdapter();
        this.realFSWindow.addWindowListener(this.fsWindowListener);
    }
    
    @Override
    protected void removeFSWindowListener(final Window window) {
        this.realFSWindow.removeWindowListener(this.fsWindowListener);
        this.fsWindowListener = null;
        final WWindowPeer wWindowPeer = (WWindowPeer)this.realFSWindow.getPeer();
        if (wWindowPeer != null) {
            if (this.ownerOrigBounds != null) {
                if (this.ownerOrigBounds.width == 0) {
                    this.ownerOrigBounds.width = 1;
                }
                if (this.ownerOrigBounds.height == 0) {
                    this.ownerOrigBounds.height = 1;
                }
                wWindowPeer.reshape(this.ownerOrigBounds.x, this.ownerOrigBounds.y, this.ownerOrigBounds.width, this.ownerOrigBounds.height);
                if (!this.ownerWasVisible) {
                    wWindowPeer.setVisible(false);
                }
                this.ownerOrigBounds = null;
            }
            if (!this.fsWindowWasAlwaysOnTop) {
                wWindowPeer.setAlwaysOnTop(false);
            }
        }
        this.realFSWindow = null;
    }
    
    private static native DisplayMode getCurrentDisplayModeNative(final int p0);
    
    @Override
    protected DisplayMode getCurrentDisplayMode(final int n) {
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            class Result
            {
                DisplayMode dm;
                
                Result() {
                    this.dm = null;
                }
            }
            final Result result = new Result();
            instance.flushAndInvokeNow(new Runnable() {
                @Override
                public void run() {
                    result.dm = getCurrentDisplayModeNative(n);
                }
            });
            if (result.dm == null) {
                return super.getCurrentDisplayMode(n);
            }
            return result.dm;
        }
        finally {
            instance.unlock();
        }
    }
    
    private static native void configDisplayModeNative(final int p0, final long p1, final int p2, final int p3, final int p4, final int p5);
    
    @Override
    protected void configDisplayMode(final int n, final WindowPeer windowPeer, final int n2, final int n3, final int n4, final int n5) {
        if (!this.fsStatus) {
            super.configDisplayMode(n, windowPeer, n2, n3, n4, n5);
            return;
        }
        final WWindowPeer wWindowPeer = (WWindowPeer)this.realFSWindow.getPeer();
        if (this.getFullScreenWindow() != this.realFSWindow) {
            final Rectangle bounds = this.getDefaultConfiguration().getBounds();
            wWindowPeer.reshape(bounds.x, bounds.y, n2, n3);
        }
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            instance.flushAndInvokeNow(new Runnable() {
                @Override
                public void run() {
                    final long hWnd = wWindowPeer.getHWnd();
                    if (hWnd == 0L) {
                        return;
                    }
                    configDisplayModeNative(n, hWnd, n2, n3, n4, n5);
                }
            });
        }
        finally {
            instance.unlock();
        }
    }
    
    private static native void enumDisplayModesNative(final int p0, final ArrayList p1);
    
    @Override
    protected void enumDisplayModes(final int n, final ArrayList list) {
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            instance.flushAndInvokeNow(new Runnable() {
                @Override
                public void run() {
                    enumDisplayModesNative(n, list);
                }
            });
            if (list.size() == 0) {
                list.add(getCurrentDisplayModeNative(n));
            }
        }
        finally {
            instance.unlock();
        }
    }
    
    private static native long getAvailableAcceleratedMemoryNative(final int p0);
    
    @Override
    public int getAvailableAcceleratedMemory() {
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            class Result
            {
                long mem;
                
                Result() {
                    this.mem = 0L;
                }
            }
            final Result result = new Result();
            instance.flushAndInvokeNow(new Runnable() {
                @Override
                public void run() {
                    result.mem = getAvailableAcceleratedMemoryNative(D3DGraphicsDevice.this.getScreen());
                }
            });
            return (int)result.mem;
        }
        finally {
            instance.unlock();
        }
    }
    
    @Override
    public GraphicsConfiguration[] getConfigurations() {
        if (this.configs == null && this.isD3DEnabledOnDevice()) {
            this.defaultConfig = this.getDefaultConfiguration();
            if (this.defaultConfig != null) {
                (this.configs = new GraphicsConfiguration[1])[0] = this.defaultConfig;
                return this.configs.clone();
            }
        }
        return super.getConfigurations();
    }
    
    @Override
    public GraphicsConfiguration getDefaultConfiguration() {
        if (this.defaultConfig == null) {
            if (this.isD3DEnabledOnDevice()) {
                this.defaultConfig = new D3DGraphicsConfig(this);
            }
            else {
                this.defaultConfig = super.getDefaultConfiguration();
            }
        }
        return this.defaultConfig;
    }
    
    private static native boolean isD3DAvailableOnDeviceNative(final int p0);
    
    public static boolean isD3DAvailableOnDevice(final int n) {
        if (!D3DGraphicsDevice.d3dAvailable) {
            return false;
        }
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            class Result
            {
                boolean avail;
                
                Result() {
                    this.avail = false;
                }
            }
            final Result result = new Result();
            instance.flushAndInvokeNow(new Runnable() {
                @Override
                public void run() {
                    result.avail = isD3DAvailableOnDeviceNative(n);
                }
            });
            return result.avail;
        }
        finally {
            instance.unlock();
        }
    }
    
    D3DContext getContext() {
        return this.context;
    }
    
    ContextCapabilities getContextCapabilities() {
        return this.d3dCaps;
    }
    
    @Override
    public void displayChanged() {
        super.displayChanged();
        if (D3DGraphicsDevice.d3dAvailable) {
            this.d3dCaps = getDeviceCaps(this.getScreen());
        }
    }
    
    @Override
    protected void invalidate(final int n) {
        super.invalidate(n);
        this.d3dCaps = new D3DContext.D3DContextCaps(0, null);
    }
    
    static {
        Toolkit.getDefaultToolkit();
        D3DGraphicsDevice.d3dAvailable = initD3D();
        if (D3DGraphicsDevice.d3dAvailable) {
            D3DGraphicsDevice.pfDisabled = true;
            PerfCounter.getD3DAvailable().set(1L);
        }
        else {
            PerfCounter.getD3DAvailable().set(0L);
        }
    }
    
    private static class D3DFSWindowAdapter extends WindowAdapter
    {
        @Override
        public void windowDeactivated(final WindowEvent windowEvent) {
            D3DRenderQueue.getInstance();
            D3DRenderQueue.restoreDevices();
        }
        
        @Override
        public void windowActivated(final WindowEvent windowEvent) {
            D3DRenderQueue.getInstance();
            D3DRenderQueue.restoreDevices();
        }
    }
}
