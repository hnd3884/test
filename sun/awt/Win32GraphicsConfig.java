package sun.awt;

import java.awt.Graphics;
import java.awt.image.ImageObserver;
import sun.awt.image.SunVolatileImage;
import java.awt.image.VolatileImage;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import sun.awt.image.OffScreenImage;
import java.awt.Image;
import java.awt.Component;
import sun.java2d.windows.GDIWindowSurfaceData;
import sun.awt.windows.WComponentPeer;
import java.awt.Rectangle;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.DirectColorModel;
import java.awt.image.ColorModel;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import java.awt.GraphicsDevice;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.RenderLoops;
import sun.awt.image.SurfaceManager;
import java.awt.GraphicsConfiguration;

public class Win32GraphicsConfig extends GraphicsConfiguration implements DisplayChangedListener, SurfaceManager.ProxiedGraphicsConfig
{
    protected Win32GraphicsDevice screen;
    protected int visual;
    protected RenderLoops solidloops;
    private SurfaceType sTypeOrig;
    
    private static native void initIDs();
    
    public static Win32GraphicsConfig getConfig(final Win32GraphicsDevice win32GraphicsDevice, final int n) {
        return new Win32GraphicsConfig(win32GraphicsDevice, n);
    }
    
    @Deprecated
    public Win32GraphicsConfig(final GraphicsDevice graphicsDevice, final int visual) {
        this.sTypeOrig = null;
        this.screen = (Win32GraphicsDevice)graphicsDevice;
        this.visual = visual;
        ((Win32GraphicsDevice)graphicsDevice).addDisplayChangedListener(this);
    }
    
    @Override
    public GraphicsDevice getDevice() {
        return this.screen;
    }
    
    public int getVisual() {
        return this.visual;
    }
    
    @Override
    public Object getProxyKey() {
        return this.screen;
    }
    
    public synchronized RenderLoops getSolidLoops(final SurfaceType sTypeOrig) {
        if (this.solidloops == null || this.sTypeOrig != sTypeOrig) {
            this.solidloops = SurfaceData.makeRenderLoops(SurfaceType.OpaqueColor, CompositeType.SrcNoEa, sTypeOrig);
            this.sTypeOrig = sTypeOrig;
        }
        return this.solidloops;
    }
    
    @Override
    public synchronized ColorModel getColorModel() {
        return this.screen.getColorModel();
    }
    
    public ColorModel getDeviceColorModel() {
        return this.screen.getDynamicColorModel();
    }
    
    @Override
    public ColorModel getColorModel(final int n) {
        switch (n) {
            case 1: {
                return this.getColorModel();
            }
            case 2: {
                return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
            }
            case 3: {
                return ColorModel.getRGBdefault();
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public AffineTransform getDefaultTransform() {
        return new AffineTransform();
    }
    
    @Override
    public AffineTransform getNormalizingTransform() {
        final Win32GraphicsEnvironment win32GraphicsEnvironment = (Win32GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
        return new AffineTransform(win32GraphicsEnvironment.getXResolution() / 72.0, 0.0, 0.0, win32GraphicsEnvironment.getYResolution() / 72.0, 0.0, 0.0);
    }
    
    @Override
    public String toString() {
        return super.toString() + "[dev=" + this.screen + ",pixfmt=" + this.visual + "]";
    }
    
    private native Rectangle getBounds(final int p0);
    
    @Override
    public Rectangle getBounds() {
        return this.getBounds(this.screen.getScreen());
    }
    
    @Override
    public synchronized void displayChanged() {
        this.solidloops = null;
    }
    
    @Override
    public void paletteChanged() {
    }
    
    public SurfaceData createSurfaceData(final WComponentPeer wComponentPeer, final int n) {
        return GDIWindowSurfaceData.createData(wComponentPeer);
    }
    
    public Image createAcceleratedImage(final Component component, final int n, final int n2) {
        final ColorModel colorModel = this.getColorModel(1);
        return new OffScreenImage(component, colorModel, colorModel.createCompatibleWritableRaster(n, n2), colorModel.isAlphaPremultiplied());
    }
    
    public void assertOperationSupported(final Component component, final int n, final BufferCapabilities bufferCapabilities) throws AWTException {
        throw new AWTException("The operation requested is not supported");
    }
    
    public VolatileImage createBackBuffer(final WComponentPeer wComponentPeer) {
        final Component component = (Component)wComponentPeer.getTarget();
        return new SunVolatileImage(component, component.getWidth(), component.getHeight(), Boolean.TRUE);
    }
    
    public void flip(final WComponentPeer wComponentPeer, final Component component, final VolatileImage volatileImage, final int n, final int n2, final int n3, final int n4, final BufferCapabilities.FlipContents flipContents) {
        if (flipContents == BufferCapabilities.FlipContents.COPIED || flipContents == BufferCapabilities.FlipContents.UNDEFINED) {
            final Graphics graphics = wComponentPeer.getGraphics();
            try {
                graphics.drawImage(volatileImage, n, n2, n3, n4, n, n2, n3, n4, null);
            }
            finally {
                graphics.dispose();
            }
        }
        else if (flipContents == BufferCapabilities.FlipContents.BACKGROUND) {
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
    public boolean isTranslucencyCapable() {
        return true;
    }
    
    static {
        initIDs();
    }
}
