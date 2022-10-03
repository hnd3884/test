package sun.java2d.opengl;

import sun.java2d.SurfaceData;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Image;
import sun.awt.SunToolkit;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.image.ColorModel;
import sun.awt.windows.WComponentPeer;

public abstract class WGLSurfaceData extends OGLSurfaceData
{
    protected WComponentPeer peer;
    private WGLGraphicsConfig graphicsConfig;
    
    private native void initOps(final OGLGraphicsConfig p0, final long p1, final WComponentPeer p2, final long p3);
    
    protected WGLSurfaceData(final WComponentPeer peer, final WGLGraphicsConfig graphicsConfig, final ColorModel colorModel, final int n) {
        super(graphicsConfig, colorModel, n);
        this.peer = peer;
        this.initOps(this.graphicsConfig = graphicsConfig, graphicsConfig.getNativeConfigInfo(), peer, (peer != null) ? peer.getHWnd() : 0L);
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return this.graphicsConfig;
    }
    
    public static WGLWindowSurfaceData createData(final WComponentPeer wComponentPeer) {
        if (!wComponentPeer.isAccelCapable() || !SunToolkit.isContainingTopLevelOpaque((Component)wComponentPeer.getTarget())) {
            return null;
        }
        return new WGLWindowSurfaceData(wComponentPeer, getGC(wComponentPeer));
    }
    
    public static WGLOffScreenSurfaceData createData(final WComponentPeer wComponentPeer, final Image image, final int n) {
        if (!wComponentPeer.isAccelCapable() || !SunToolkit.isContainingTopLevelOpaque((Component)wComponentPeer.getTarget())) {
            return null;
        }
        final WGLGraphicsConfig gc = getGC(wComponentPeer);
        final Rectangle bounds = wComponentPeer.getBounds();
        if (n == 4) {
            return new WGLOffScreenSurfaceData(wComponentPeer, gc, bounds.width, bounds.height, image, wComponentPeer.getColorModel(), n);
        }
        return new WGLVSyncOffScreenSurfaceData(wComponentPeer, gc, bounds.width, bounds.height, image, wComponentPeer.getColorModel(), n);
    }
    
    public static WGLOffScreenSurfaceData createData(final WGLGraphicsConfig wglGraphicsConfig, final int n, final int n2, final ColorModel colorModel, final Image image, final int n3) {
        return new WGLOffScreenSurfaceData(null, wglGraphicsConfig, n, n2, image, colorModel, n3);
    }
    
    public static WGLGraphicsConfig getGC(final WComponentPeer wComponentPeer) {
        if (wComponentPeer != null) {
            return (WGLGraphicsConfig)wComponentPeer.getGraphicsConfiguration();
        }
        return (WGLGraphicsConfig)GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
    
    public static native boolean updateWindowAccelImpl(final long p0, final WComponentPeer p1, final int p2, final int p3);
    
    public static class WGLWindowSurfaceData extends WGLSurfaceData
    {
        public WGLWindowSurfaceData(final WComponentPeer wComponentPeer, final WGLGraphicsConfig wglGraphicsConfig) {
            super(wComponentPeer, wglGraphicsConfig, wComponentPeer.getColorModel(), 1);
        }
        
        @Override
        public SurfaceData getReplacement() {
            return this.peer.getSurfaceData();
        }
        
        @Override
        public Rectangle getBounds() {
            final Rectangle bounds;
            final Rectangle rectangle = bounds = this.peer.getBounds();
            final int n = 0;
            rectangle.y = n;
            bounds.x = n;
            return rectangle;
        }
        
        @Override
        public Object getDestination() {
            return this.peer.getTarget();
        }
    }
    
    public static class WGLVSyncOffScreenSurfaceData extends WGLOffScreenSurfaceData
    {
        private WGLOffScreenSurfaceData flipSurface;
        
        public WGLVSyncOffScreenSurfaceData(final WComponentPeer wComponentPeer, final WGLGraphicsConfig wglGraphicsConfig, final int n, final int n2, final Image image, final ColorModel colorModel, final int n3) {
            super(wComponentPeer, wglGraphicsConfig, n, n2, image, colorModel, n3);
            this.flipSurface = WGLSurfaceData.createData(wComponentPeer, image, 4);
        }
        
        public SurfaceData getFlipSurface() {
            return this.flipSurface;
        }
        
        @Override
        public void flush() {
            this.flipSurface.flush();
            super.flush();
        }
    }
    
    public static class WGLOffScreenSurfaceData extends WGLSurfaceData
    {
        private Image offscreenImage;
        private int width;
        private int height;
        
        public WGLOffScreenSurfaceData(final WComponentPeer wComponentPeer, final WGLGraphicsConfig wglGraphicsConfig, final int width, final int height, final Image offscreenImage, final ColorModel colorModel, final int n) {
            super(wComponentPeer, wglGraphicsConfig, colorModel, n);
            this.width = width;
            this.height = height;
            this.offscreenImage = offscreenImage;
            this.initSurface(width, height);
        }
        
        @Override
        public SurfaceData getReplacement() {
            return SurfaceData.restoreContents(this.offscreenImage);
        }
        
        @Override
        public Rectangle getBounds() {
            if (this.type == 4) {
                final Rectangle bounds;
                final Rectangle rectangle = bounds = this.peer.getBounds();
                final int n = 0;
                rectangle.y = n;
                bounds.x = n;
                return rectangle;
            }
            return new Rectangle(this.width, this.height);
        }
        
        @Override
        public Object getDestination() {
            return this.offscreenImage;
        }
    }
}
