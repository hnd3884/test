package sun.java2d.d3d;

import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.hw.AccelDeviceEventNotifier;
import sun.java2d.pipe.hw.AccelDeviceEventListener;
import sun.java2d.pipe.hw.ContextCapabilities;
import sun.java2d.Surface;
import sun.java2d.pipe.hw.AccelSurface;
import java.awt.GraphicsConfiguration;
import sun.java2d.pipe.hw.AccelTypedVolatileImage;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import sun.awt.image.SurfaceManager;
import sun.awt.image.SunVolatileImage;
import java.awt.image.VolatileImage;
import java.awt.AWTException;
import java.awt.Component;
import sun.awt.windows.WComponentPeer;
import java.awt.color.ColorSpace;
import java.awt.image.DirectColorModel;
import java.awt.image.ColorModel;
import java.awt.Image;
import sun.java2d.SurfaceData;
import java.awt.GraphicsDevice;
import java.awt.BufferCapabilities;
import java.awt.ImageCapabilities;
import sun.java2d.pipe.hw.AccelGraphicsConfig;
import sun.awt.Win32GraphicsConfig;

public class D3DGraphicsConfig extends Win32GraphicsConfig implements AccelGraphicsConfig
{
    private static ImageCapabilities imageCaps;
    private BufferCapabilities bufferCaps;
    private D3DGraphicsDevice device;
    
    protected D3DGraphicsConfig(final D3DGraphicsDevice device) {
        super(device, 0);
        this.device = device;
    }
    
    public SurfaceData createManagedSurface(final int n, final int n2, final int n3) {
        return D3DSurfaceData.createData(this, n, n2, this.getColorModel(n3), null, 3);
    }
    
    @Override
    public synchronized void displayChanged() {
        super.displayChanged();
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            D3DContext.invalidateCurrentContext();
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
        return "D3DGraphicsConfig[dev=" + this.screen + ",pixfmt=" + this.visual + "]";
    }
    
    @Override
    public SurfaceData createSurfaceData(final WComponentPeer wComponentPeer, final int n) {
        return super.createSurfaceData(wComponentPeer, n);
    }
    
    @Override
    public void assertOperationSupported(final Component component, final int n, final BufferCapabilities bufferCapabilities) throws AWTException {
        if (n < 2 || n > 4) {
            throw new AWTException("Only 2-4 buffers supported");
        }
        if (bufferCapabilities.getFlipContents() == BufferCapabilities.FlipContents.COPIED && n != 2) {
            throw new AWTException("FlipContents.COPIED is onlysupported for 2 buffers");
        }
    }
    
    @Override
    public VolatileImage createBackBuffer(final WComponentPeer wComponentPeer) {
        final Component component = (Component)wComponentPeer.getTarget();
        return new SunVolatileImage(component, Math.max(1, component.getWidth()), Math.max(1, component.getHeight()), Boolean.TRUE);
    }
    
    @Override
    public void flip(final WComponentPeer wComponentPeer, final Component component, final VolatileImage volatileImage, final int n, final int n2, final int n3, final int n4, final BufferCapabilities.FlipContents flipContents) {
        final SurfaceData primarySurfaceData = SurfaceManager.getManager(volatileImage).getPrimarySurfaceData();
        if (primarySurfaceData instanceof D3DSurfaceData) {
            D3DSurfaceData.swapBuffers((D3DSurfaceData)primarySurfaceData, n, n2, n3, n4);
        }
        else {
            final Graphics graphics = wComponentPeer.getGraphics();
            try {
                graphics.drawImage(volatileImage, n, n2, n3, n4, n, n2, n3, n4, null);
            }
            finally {
                graphics.dispose();
            }
        }
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
            this.bufferCaps = new D3DBufferCaps();
        }
        return this.bufferCaps;
    }
    
    @Override
    public ImageCapabilities getImageCapabilities() {
        return D3DGraphicsConfig.imageCaps;
    }
    
    D3DGraphicsDevice getD3DDevice() {
        return this.device;
    }
    
    @Override
    public D3DContext getContext() {
        return this.device.getContext();
    }
    
    @Override
    public VolatileImage createCompatibleVolatileImage(final int n, final int n2, final int n3, final int n4) {
        if (n4 == 4 || n4 == 1 || n4 == 0 || n3 == 2) {
            return null;
        }
        final boolean b = n3 == 1;
        if (n4 == 5) {
            if (!this.device.isCapPresent(b ? 8 : 4)) {
                return null;
            }
        }
        else if (n4 == 2 && !b && !this.device.isCapPresent(2)) {
            return null;
        }
        SunVolatileImage sunVolatileImage = new AccelTypedVolatileImage(this, n, n2, n3, n4);
        final Surface destSurface = sunVolatileImage.getDestSurface();
        if (!(destSurface instanceof AccelSurface) || ((AccelSurface)destSurface).getType() != n4) {
            sunVolatileImage.flush();
            sunVolatileImage = null;
        }
        return sunVolatileImage;
    }
    
    @Override
    public ContextCapabilities getContextCapabilities() {
        return this.device.getContextCapabilities();
    }
    
    @Override
    public void addDeviceEventListener(final AccelDeviceEventListener accelDeviceEventListener) {
        AccelDeviceEventNotifier.addListener(accelDeviceEventListener, this.device.getScreen());
    }
    
    @Override
    public void removeDeviceEventListener(final AccelDeviceEventListener accelDeviceEventListener) {
        AccelDeviceEventNotifier.removeListener(accelDeviceEventListener);
    }
    
    static {
        D3DGraphicsConfig.imageCaps = new D3DImageCaps();
    }
    
    private static class D3DBufferCaps extends BufferCapabilities
    {
        public D3DBufferCaps() {
            super(D3DGraphicsConfig.imageCaps, D3DGraphicsConfig.imageCaps, FlipContents.UNDEFINED);
        }
        
        @Override
        public boolean isMultiBufferAvailable() {
            return true;
        }
    }
    
    private static class D3DImageCaps extends ImageCapabilities
    {
        private D3DImageCaps() {
            super(true);
        }
        
        @Override
        public boolean isTrueVolatile() {
            return true;
        }
    }
}
