package sun.awt.image;

import java.awt.ImageCapabilities;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import sun.java2d.InvalidPipeException;
import java.awt.GraphicsConfiguration;
import sun.java2d.SunGraphicsEnvironment;
import java.awt.GraphicsEnvironment;
import sun.java2d.SurfaceData;
import sun.awt.DisplayChangedListener;

public abstract class VolatileSurfaceManager extends SurfaceManager implements DisplayChangedListener
{
    protected SunVolatileImage vImg;
    protected SurfaceData sdAccel;
    protected SurfaceData sdBackup;
    protected SurfaceData sdCurrent;
    protected SurfaceData sdPrevious;
    protected boolean lostSurface;
    protected Object context;
    
    protected VolatileSurfaceManager(final SunVolatileImage vImg, final Object context) {
        this.vImg = vImg;
        this.context = context;
        final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (localGraphicsEnvironment instanceof SunGraphicsEnvironment) {
            ((SunGraphicsEnvironment)localGraphicsEnvironment).addDisplayChangedListener(this);
        }
    }
    
    public void initialize() {
        if (this.isAccelerationEnabled()) {
            this.sdAccel = this.initAcceleratedSurface();
            if (this.sdAccel != null) {
                this.sdCurrent = this.sdAccel;
            }
        }
        if (this.sdCurrent == null && this.vImg.getForcedAccelSurfaceType() == 0) {
            this.sdCurrent = this.getBackupSurface();
        }
    }
    
    @Override
    public SurfaceData getPrimarySurfaceData() {
        return this.sdCurrent;
    }
    
    protected abstract boolean isAccelerationEnabled();
    
    public int validate(final GraphicsConfiguration graphicsConfiguration) {
        int n = 0;
        final boolean lostSurface = this.lostSurface;
        this.lostSurface = false;
        if (this.isAccelerationEnabled()) {
            if (!this.isConfigValid(graphicsConfiguration)) {
                n = 2;
            }
            else if (this.sdAccel == null) {
                this.sdAccel = this.initAcceleratedSurface();
                if (this.sdAccel != null) {
                    this.sdCurrent = this.sdAccel;
                    this.sdBackup = null;
                    n = 1;
                }
                else {
                    this.sdCurrent = this.getBackupSurface();
                }
            }
            else if (this.sdAccel.isSurfaceLost()) {
                try {
                    this.restoreAcceleratedSurface();
                    this.sdCurrent = this.sdAccel;
                    this.sdAccel.setSurfaceLost(false);
                    this.sdBackup = null;
                    n = 1;
                }
                catch (final InvalidPipeException ex) {
                    this.sdCurrent = this.getBackupSurface();
                }
            }
            else if (lostSurface) {
                n = 1;
            }
        }
        else if (this.sdAccel != null) {
            this.sdCurrent = this.getBackupSurface();
            this.sdAccel = null;
            n = 1;
        }
        if (n != 2 && this.sdCurrent != this.sdPrevious) {
            this.sdPrevious = this.sdCurrent;
            n = 1;
        }
        if (n == 1) {
            this.initContents();
        }
        return n;
    }
    
    public boolean contentsLost() {
        return this.lostSurface;
    }
    
    protected abstract SurfaceData initAcceleratedSurface();
    
    protected SurfaceData getBackupSurface() {
        if (this.sdBackup == null) {
            final BufferedImage backupImage = this.vImg.getBackupImage();
            SunWritableRaster.stealTrackable(backupImage.getRaster().getDataBuffer()).setUntrackable();
            this.sdBackup = BufImgSurfaceData.createData(backupImage);
        }
        return this.sdBackup;
    }
    
    public void initContents() {
        if (this.sdCurrent != null) {
            final Graphics2D graphics = this.vImg.createGraphics();
            graphics.clearRect(0, 0, this.vImg.getWidth(), this.vImg.getHeight());
            graphics.dispose();
        }
    }
    
    @Override
    public SurfaceData restoreContents() {
        return this.getBackupSurface();
    }
    
    @Override
    public void acceleratedSurfaceLost() {
        if (this.isAccelerationEnabled() && this.sdCurrent == this.sdAccel) {
            this.lostSurface = true;
        }
    }
    
    protected void restoreAcceleratedSurface() {
    }
    
    @Override
    public void displayChanged() {
        if (!this.isAccelerationEnabled()) {
            return;
        }
        this.lostSurface = true;
        if (this.sdAccel != null) {
            this.sdBackup = null;
            final SurfaceData sdAccel = this.sdAccel;
            this.sdAccel = null;
            sdAccel.invalidate();
            this.sdCurrent = this.getBackupSurface();
        }
        this.vImg.updateGraphicsConfig();
    }
    
    @Override
    public void paletteChanged() {
        this.lostSurface = true;
    }
    
    protected boolean isConfigValid(final GraphicsConfiguration graphicsConfiguration) {
        return graphicsConfiguration == null || graphicsConfiguration.getDevice() == this.vImg.getGraphicsConfig().getDevice();
    }
    
    @Override
    public ImageCapabilities getCapabilities(final GraphicsConfiguration graphicsConfiguration) {
        if (this.isConfigValid(graphicsConfiguration)) {
            return this.isAccelerationEnabled() ? new AcceleratedImageCapabilities() : new ImageCapabilities(false);
        }
        return super.getCapabilities(graphicsConfiguration);
    }
    
    @Override
    public void flush() {
        this.lostSurface = true;
        final SurfaceData sdAccel = this.sdAccel;
        this.sdAccel = null;
        if (sdAccel != null) {
            sdAccel.flush();
        }
    }
    
    private class AcceleratedImageCapabilities extends ImageCapabilities
    {
        AcceleratedImageCapabilities() {
            super(false);
        }
        
        @Override
        public boolean isAccelerated() {
            return VolatileSurfaceManager.this.sdCurrent == VolatileSurfaceManager.this.sdAccel;
        }
        
        @Override
        public boolean isTrueVolatile() {
            return this.isAccelerated();
        }
    }
}
