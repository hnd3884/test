package sun.java2d.opengl;

import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.hw.AccelDeviceEventNotifier;
import sun.java2d.pipe.hw.AccelDeviceEventListener;
import sun.java2d.Surface;
import sun.java2d.pipe.hw.AccelSurface;
import java.awt.GraphicsConfiguration;
import sun.java2d.pipe.hw.AccelTypedVolatileImage;
import java.awt.Graphics;
import java.awt.Font;
import sun.java2d.SunGraphics2D;
import java.awt.Color;
import java.awt.image.ImageObserver;
import sun.awt.image.SurfaceManager;
import sun.awt.image.SunVolatileImage;
import java.awt.image.VolatileImage;
import java.awt.AWTException;
import java.awt.Component;
import sun.java2d.windows.GDIWindowSurfaceData;
import sun.awt.windows.WComponentPeer;
import java.awt.color.ColorSpace;
import java.awt.image.DirectColorModel;
import java.awt.image.ColorModel;
import java.awt.Image;
import sun.java2d.SurfaceData;
import sun.java2d.DisposerRecord;
import sun.java2d.Disposer;
import sun.java2d.pipe.RenderQueue;
import java.awt.GraphicsDevice;
import sun.awt.Win32GraphicsDevice;
import sun.java2d.pipe.hw.ContextCapabilities;
import java.awt.BufferCapabilities;
import java.awt.ImageCapabilities;
import sun.awt.Win32GraphicsConfig;

public class WGLGraphicsConfig extends Win32GraphicsConfig implements OGLGraphicsConfig
{
    protected static boolean wglAvailable;
    private static ImageCapabilities imageCaps;
    private BufferCapabilities bufferCaps;
    private long pConfigInfo;
    private ContextCapabilities oglCaps;
    private OGLContext context;
    private Object disposerReferent;
    
    public static native int getDefaultPixFmt(final int p0);
    
    private static native boolean initWGL();
    
    private static native long getWGLConfigInfo(final int p0, final int p1);
    
    private static native int getOGLCapabilities(final long p0);
    
    protected WGLGraphicsConfig(final Win32GraphicsDevice win32GraphicsDevice, final int n, final long pConfigInfo, final ContextCapabilities oglCaps) {
        super(win32GraphicsDevice, n);
        this.disposerReferent = new Object();
        this.pConfigInfo = pConfigInfo;
        this.oglCaps = oglCaps;
        this.context = new OGLContext(OGLRenderQueue.getInstance(), this);
        Disposer.addRecord(this.disposerReferent, new WGLGCDisposerRecord(this.pConfigInfo, win32GraphicsDevice.getScreen()));
    }
    
    @Override
    public Object getProxyKey() {
        return this;
    }
    
    @Override
    public SurfaceData createManagedSurface(final int n, final int n2, final int n3) {
        return WGLSurfaceData.createData(this, n, n2, this.getColorModel(n3), null, 3);
    }
    
    public static WGLGraphicsConfig getConfig(final Win32GraphicsDevice win32GraphicsDevice, final int n) {
        if (!WGLGraphicsConfig.wglAvailable) {
            return null;
        }
        long configInfo = 0L;
        final String[] array = { null };
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        instance.lock();
        try {
            OGLContext.invalidateCurrentContext();
            final WGLGetConfigInfo wglGetConfigInfo = new WGLGetConfigInfo(win32GraphicsDevice.getScreen(), n);
            instance.flushAndInvokeNow(wglGetConfigInfo);
            configInfo = wglGetConfigInfo.getConfigInfo();
            if (configInfo != 0L) {
                OGLContext.setScratchSurface(configInfo);
                instance.flushAndInvokeNow(new Runnable() {
                    @Override
                    public void run() {
                        array[0] = OGLContext.getOGLIdString();
                    }
                });
            }
        }
        finally {
            instance.unlock();
        }
        if (configInfo == 0L) {
            return null;
        }
        return new WGLGraphicsConfig(win32GraphicsDevice, n, configInfo, new OGLContext.OGLContextCaps(getOGLCapabilities(configInfo), array[0]));
    }
    
    public static boolean isWGLAvailable() {
        return WGLGraphicsConfig.wglAvailable;
    }
    
    @Override
    public final boolean isCapPresent(final int n) {
        return (this.oglCaps.getCaps() & n) != 0x0;
    }
    
    @Override
    public final long getNativeConfigInfo() {
        return this.pConfigInfo;
    }
    
    @Override
    public final OGLContext getContext() {
        return this.context;
    }
    
    @Override
    public synchronized void displayChanged() {
        super.displayChanged();
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        instance.lock();
        try {
            OGLContext.invalidateCurrentContext();
        }
        finally {
            instance.unlock();
        }
    }
    
    @Override
    public ColorModel getColorModel(final int n) {
        switch (n) {
            case 1: {
                return new DirectColorModel(24, 16711680, 65280, 255);
            }
            case 2: {
                return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
            }
            case 3: {
                return new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, true, 3);
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public String toString() {
        return "WGLGraphicsConfig[dev=" + this.screen + ",pixfmt=" + this.visual + "]";
    }
    
    @Override
    public SurfaceData createSurfaceData(final WComponentPeer wComponentPeer, final int n) {
        SurfaceData surfaceData = WGLSurfaceData.createData(wComponentPeer);
        if (surfaceData == null) {
            surfaceData = GDIWindowSurfaceData.createData(wComponentPeer);
        }
        return surfaceData;
    }
    
    @Override
    public void assertOperationSupported(final Component component, final int n, final BufferCapabilities bufferCapabilities) throws AWTException {
        if (n > 2) {
            throw new AWTException("Only double or single buffering is supported");
        }
        if (!this.getBufferCapabilities().isPageFlipping()) {
            throw new AWTException("Page flipping is not supported");
        }
        if (bufferCapabilities.getFlipContents() == BufferCapabilities.FlipContents.PRIOR) {
            throw new AWTException("FlipContents.PRIOR is not supported");
        }
    }
    
    @Override
    public VolatileImage createBackBuffer(final WComponentPeer wComponentPeer) {
        final Component component = (Component)wComponentPeer.getTarget();
        return new SunVolatileImage(component, Math.max(1, component.getWidth()), Math.max(1, component.getHeight()), Boolean.TRUE);
    }
    
    @Override
    public void flip(final WComponentPeer wComponentPeer, final Component component, final VolatileImage volatileImage, final int n, final int n2, final int n3, final int n4, final BufferCapabilities.FlipContents flipContents) {
        if (flipContents == BufferCapabilities.FlipContents.COPIED) {
            final SurfaceData primarySurfaceData = SurfaceManager.getManager(volatileImage).getPrimarySurfaceData();
            if (!(primarySurfaceData instanceof WGLSurfaceData.WGLVSyncOffScreenSurfaceData)) {
                final Graphics graphics = wComponentPeer.getGraphics();
                try {
                    graphics.drawImage(volatileImage, n, n2, n3, n4, n, n2, n3, n4, null);
                }
                finally {
                    graphics.dispose();
                }
                return;
            }
            final SunGraphics2D sunGraphics2D = new SunGraphics2D(((WGLSurfaceData.WGLVSyncOffScreenSurfaceData)primarySurfaceData).getFlipSurface(), Color.black, Color.white, null);
            try {
                sunGraphics2D.drawImage(volatileImage, 0, 0, null);
            }
            finally {
                sunGraphics2D.dispose();
            }
        }
        else if (flipContents == BufferCapabilities.FlipContents.PRIOR) {
            return;
        }
        OGLSurfaceData.swapBuffers(wComponentPeer.getData());
        if (flipContents == BufferCapabilities.FlipContents.BACKGROUND) {
            final Graphics graphics2 = volatileImage.getGraphics();
            try {
                graphics2.setColor(component.getBackground());
                graphics2.fillRect(0, 0, volatileImage.getWidth(), volatileImage.getHeight());
            }
            finally {
                graphics2.dispose();
            }
        }
    }
    
    @Override
    public BufferCapabilities getBufferCapabilities() {
        if (this.bufferCaps == null) {
            this.bufferCaps = new WGLBufferCaps(this.isCapPresent(65536));
        }
        return this.bufferCaps;
    }
    
    @Override
    public ImageCapabilities getImageCapabilities() {
        return WGLGraphicsConfig.imageCaps;
    }
    
    @Override
    public VolatileImage createCompatibleVolatileImage(final int n, final int n2, final int n3, final int n4) {
        if ((n4 != 5 && n4 != 3) || n3 == 2 || (n4 == 5 && !this.isCapPresent(12))) {
            return null;
        }
        AccelTypedVolatileImage accelTypedVolatileImage = new AccelTypedVolatileImage(this, n, n2, n3, n4);
        final Surface destSurface = accelTypedVolatileImage.getDestSurface();
        if (!(destSurface instanceof AccelSurface) || ((AccelSurface)destSurface).getType() != n4) {
            accelTypedVolatileImage.flush();
            accelTypedVolatileImage = null;
        }
        return accelTypedVolatileImage;
    }
    
    @Override
    public ContextCapabilities getContextCapabilities() {
        return this.oglCaps;
    }
    
    @Override
    public void addDeviceEventListener(final AccelDeviceEventListener accelDeviceEventListener) {
        AccelDeviceEventNotifier.addListener(accelDeviceEventListener, this.screen.getScreen());
    }
    
    @Override
    public void removeDeviceEventListener(final AccelDeviceEventListener accelDeviceEventListener) {
        AccelDeviceEventNotifier.removeListener(accelDeviceEventListener);
    }
    
    static {
        WGLGraphicsConfig.imageCaps = new WGLImageCaps();
        WGLGraphicsConfig.wglAvailable = initWGL();
    }
    
    private static class WGLGetConfigInfo implements Runnable
    {
        private int screen;
        private int pixfmt;
        private long cfginfo;
        
        private WGLGetConfigInfo(final int screen, final int pixfmt) {
            this.screen = screen;
            this.pixfmt = pixfmt;
        }
        
        @Override
        public void run() {
            this.cfginfo = getWGLConfigInfo(this.screen, this.pixfmt);
        }
        
        public long getConfigInfo() {
            return this.cfginfo;
        }
    }
    
    private static class WGLGCDisposerRecord implements DisposerRecord
    {
        private long pCfgInfo;
        private int screen;
        
        public WGLGCDisposerRecord(final long pCfgInfo, final int n) {
            this.pCfgInfo = pCfgInfo;
        }
        
        @Override
        public void dispose() {
            final OGLRenderQueue instance = OGLRenderQueue.getInstance();
            instance.lock();
            try {
                instance.flushAndInvokeNow(new Runnable() {
                    @Override
                    public void run() {
                        AccelDeviceEventNotifier.eventOccured(WGLGCDisposerRecord.this.screen, 0);
                        AccelDeviceEventNotifier.eventOccured(WGLGCDisposerRecord.this.screen, 1);
                    }
                });
            }
            finally {
                instance.unlock();
            }
            if (this.pCfgInfo != 0L) {
                OGLRenderQueue.disposeGraphicsConfig(this.pCfgInfo);
                this.pCfgInfo = 0L;
            }
        }
    }
    
    private static class WGLBufferCaps extends BufferCapabilities
    {
        public WGLBufferCaps(final boolean b) {
            super(WGLGraphicsConfig.imageCaps, WGLGraphicsConfig.imageCaps, b ? FlipContents.UNDEFINED : null);
        }
    }
    
    private static class WGLImageCaps extends ImageCapabilities
    {
        private WGLImageCaps() {
            super(true);
        }
        
        @Override
        public boolean isTrueVolatile() {
            return true;
        }
    }
}
