package sun.java2d.opengl;

import java.awt.GraphicsConfiguration;
import java.awt.image.ColorModel;
import java.awt.Component;
import java.awt.Image;
import java.awt.BufferCapabilities;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import sun.awt.windows.WComponentPeer;
import sun.java2d.SurfaceData;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;

public class WGLVolatileSurfaceManager extends VolatileSurfaceManager
{
    private final boolean accelerationEnabled;
    
    public WGLVolatileSurfaceManager(final SunVolatileImage sunVolatileImage, final Object o) {
        super(sunVolatileImage, o);
        final int transparency = sunVolatileImage.getTransparency();
        this.accelerationEnabled = (((WGLGraphicsConfig)sunVolatileImage.getGraphicsConfig()).isCapPresent(12) && transparency != 2);
    }
    
    @Override
    protected boolean isAccelerationEnabled() {
        return this.accelerationEnabled;
    }
    
    @Override
    protected SurfaceData initAcceleratedSurface() {
        final Component component = this.vImg.getComponent();
        final WComponentPeer wComponentPeer = (component != null) ? ((WComponentPeer)component.getPeer()) : null;
        WGLSurfaceData.WGLOffScreenSurfaceData wglOffScreenSurfaceData;
        try {
            boolean b = false;
            int booleanValue = 0;
            if (this.context instanceof Boolean) {
                booleanValue = (((boolean)this.context) ? 1 : 0);
                if (booleanValue != 0) {
                    final BufferCapabilities backBufferCaps = wComponentPeer.getBackBufferCaps();
                    if (backBufferCaps instanceof ExtendedBufferCapabilities) {
                        final ExtendedBufferCapabilities extendedBufferCapabilities = (ExtendedBufferCapabilities)backBufferCaps;
                        if (extendedBufferCapabilities.getVSync() == ExtendedBufferCapabilities.VSyncType.VSYNC_ON && extendedBufferCapabilities.getFlipContents() == BufferCapabilities.FlipContents.COPIED) {
                            b = true;
                            booleanValue = 0;
                        }
                    }
                }
            }
            if (booleanValue != 0) {
                wglOffScreenSurfaceData = WGLSurfaceData.createData(wComponentPeer, this.vImg, 4);
            }
            else {
                final WGLGraphicsConfig wglGraphicsConfig = (WGLGraphicsConfig)this.vImg.getGraphicsConfig();
                final ColorModel colorModel = wglGraphicsConfig.getColorModel(this.vImg.getTransparency());
                int forcedAccelSurfaceType = this.vImg.getForcedAccelSurfaceType();
                if (forcedAccelSurfaceType == 0) {
                    forcedAccelSurfaceType = 5;
                }
                if (b) {
                    wglOffScreenSurfaceData = WGLSurfaceData.createData(wComponentPeer, this.vImg, forcedAccelSurfaceType);
                }
                else {
                    wglOffScreenSurfaceData = WGLSurfaceData.createData(wglGraphicsConfig, this.vImg.getWidth(), this.vImg.getHeight(), colorModel, this.vImg, forcedAccelSurfaceType);
                }
            }
        }
        catch (final NullPointerException ex) {
            wglOffScreenSurfaceData = null;
        }
        catch (final OutOfMemoryError outOfMemoryError) {
            wglOffScreenSurfaceData = null;
        }
        return wglOffScreenSurfaceData;
    }
    
    @Override
    protected boolean isConfigValid(final GraphicsConfiguration graphicsConfiguration) {
        return graphicsConfiguration == null || (graphicsConfiguration instanceof WGLGraphicsConfig && graphicsConfiguration == this.vImg.getGraphicsConfig());
    }
    
    @Override
    public void initContents() {
        if (this.vImg.getForcedAccelSurfaceType() != 3) {
            super.initContents();
        }
    }
}
