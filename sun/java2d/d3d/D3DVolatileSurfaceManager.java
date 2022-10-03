package sun.java2d.d3d;

import sun.awt.Win32GraphicsConfig;
import sun.awt.image.SurfaceManager;
import sun.java2d.windows.GDIWindowSurfaceData;
import java.awt.GraphicsConfiguration;
import java.awt.image.ColorModel;
import java.awt.Component;
import sun.java2d.InvalidPipeException;
import java.awt.Image;
import sun.awt.windows.WComponentPeer;
import sun.java2d.SurfaceData;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;

public class D3DVolatileSurfaceManager extends VolatileSurfaceManager
{
    private boolean accelerationEnabled;
    private int restoreCountdown;
    
    public D3DVolatileSurfaceManager(final SunVolatileImage sunVolatileImage, final Object o) {
        super(sunVolatileImage, o);
        final int transparency = sunVolatileImage.getTransparency();
        final D3DGraphicsDevice d3DGraphicsDevice = (D3DGraphicsDevice)sunVolatileImage.getGraphicsConfig().getDevice();
        this.accelerationEnabled = (transparency == 1 || (transparency == 3 && (d3DGraphicsDevice.isCapPresent(2) || d3DGraphicsDevice.isCapPresent(4))));
    }
    
    @Override
    protected boolean isAccelerationEnabled() {
        return this.accelerationEnabled;
    }
    
    public void setAccelerationEnabled(final boolean accelerationEnabled) {
        this.accelerationEnabled = accelerationEnabled;
    }
    
    @Override
    protected SurfaceData initAcceleratedSurface() {
        final Component component = this.vImg.getComponent();
        final WComponentPeer wComponentPeer = (component != null) ? ((WComponentPeer)component.getPeer()) : null;
        D3DSurfaceData d3DSurfaceData;
        try {
            boolean booleanValue = false;
            if (this.context instanceof Boolean) {
                booleanValue = (boolean)this.context;
            }
            if (booleanValue) {
                d3DSurfaceData = D3DSurfaceData.createData(wComponentPeer, this.vImg);
            }
            else {
                final D3DGraphicsConfig d3DGraphicsConfig = (D3DGraphicsConfig)this.vImg.getGraphicsConfig();
                final ColorModel colorModel = d3DGraphicsConfig.getColorModel(this.vImg.getTransparency());
                int forcedAccelSurfaceType = this.vImg.getForcedAccelSurfaceType();
                if (forcedAccelSurfaceType == 0) {
                    forcedAccelSurfaceType = 5;
                }
                d3DSurfaceData = D3DSurfaceData.createData(d3DGraphicsConfig, this.vImg.getWidth(), this.vImg.getHeight(), colorModel, this.vImg, forcedAccelSurfaceType);
            }
        }
        catch (final NullPointerException ex) {
            d3DSurfaceData = null;
        }
        catch (final OutOfMemoryError outOfMemoryError) {
            d3DSurfaceData = null;
        }
        catch (final InvalidPipeException ex2) {
            d3DSurfaceData = null;
        }
        return d3DSurfaceData;
    }
    
    @Override
    protected boolean isConfigValid(final GraphicsConfiguration graphicsConfiguration) {
        return graphicsConfiguration == null || graphicsConfiguration == this.vImg.getGraphicsConfig();
    }
    
    private synchronized void setRestoreCountdown(final int restoreCountdown) {
        this.restoreCountdown = restoreCountdown;
    }
    
    @Override
    protected void restoreAcceleratedSurface() {
        synchronized (this) {
            if (this.restoreCountdown > 0) {
                --this.restoreCountdown;
                throw new InvalidPipeException("Will attempt to restore surface  in " + this.restoreCountdown);
            }
        }
        final SurfaceData initAcceleratedSurface = this.initAcceleratedSurface();
        if (initAcceleratedSurface != null) {
            this.sdAccel = initAcceleratedSurface;
            return;
        }
        throw new InvalidPipeException("could not restore surface");
    }
    
    @Override
    public SurfaceData restoreContents() {
        this.acceleratedSurfaceLost();
        return super.restoreContents();
    }
    
    static void handleVItoScreenOp(final SurfaceData surfaceData, final SurfaceData surfaceData2) {
        if (surfaceData instanceof D3DSurfaceData && surfaceData2 instanceof GDIWindowSurfaceData) {
            final D3DSurfaceData d3DSurfaceData = (D3DSurfaceData)surfaceData;
            final SurfaceManager manager = SurfaceManager.getManager((Image)d3DSurfaceData.getDestination());
            if (manager instanceof D3DVolatileSurfaceManager) {
                final D3DVolatileSurfaceManager d3DVolatileSurfaceManager = (D3DVolatileSurfaceManager)manager;
                if (d3DVolatileSurfaceManager != null) {
                    d3DSurfaceData.setSurfaceLost(true);
                    final WComponentPeer peer = ((GDIWindowSurfaceData)surfaceData2).getPeer();
                    if (D3DScreenUpdateManager.canUseD3DOnScreen(peer, (Win32GraphicsConfig)peer.getGraphicsConfiguration(), peer.getBackBuffersNum())) {
                        d3DVolatileSurfaceManager.setRestoreCountdown(10);
                    }
                    else {
                        d3DVolatileSurfaceManager.setAccelerationEnabled(false);
                    }
                }
            }
        }
    }
    
    @Override
    public void initContents() {
        if (this.vImg.getForcedAccelSurfaceType() != 3) {
            super.initContents();
        }
    }
}
