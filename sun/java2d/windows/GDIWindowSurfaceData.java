package sun.java2d.windows;

import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.XORComposite;
import sun.java2d.pipe.Region;
import sun.java2d.loops.CompositeType;
import java.awt.Rectangle;
import sun.java2d.ScreenUpdateManager;
import sun.awt.Win32GraphicsDevice;
import java.awt.GraphicsConfiguration;
import sun.java2d.SunGraphics2D;
import java.awt.image.Raster;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.InvalidPipeException;
import java.awt.image.IndexColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ColorModel;
import sun.java2d.pipe.PixelToShapeConverter;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.RenderLoops;
import sun.awt.Win32GraphicsConfig;
import sun.awt.windows.WComponentPeer;
import sun.java2d.SurfaceData;

public class GDIWindowSurfaceData extends SurfaceData
{
    private WComponentPeer peer;
    private Win32GraphicsConfig graphicsConfig;
    private RenderLoops solidloops;
    public static final String DESC_GDI = "GDI";
    public static final SurfaceType AnyGdi;
    public static final SurfaceType IntRgbGdi;
    public static final SurfaceType Ushort565RgbGdi;
    public static final SurfaceType Ushort555RgbGdi;
    public static final SurfaceType ThreeByteBgrGdi;
    protected static GDIRenderer gdiPipe;
    protected static PixelToShapeConverter gdiTxPipe;
    
    private static native void initIDs(final Class p0);
    
    public static SurfaceType getSurfaceType(final ColorModel colorModel) {
        switch (colorModel.getPixelSize()) {
            case 24:
            case 32: {
                if (!(colorModel instanceof DirectColorModel)) {
                    return GDIWindowSurfaceData.ThreeByteBgrGdi;
                }
                if (((DirectColorModel)colorModel).getRedMask() == 16711680) {
                    return GDIWindowSurfaceData.IntRgbGdi;
                }
                return SurfaceType.IntRgbx;
            }
            case 15: {
                return GDIWindowSurfaceData.Ushort555RgbGdi;
            }
            case 16: {
                if (colorModel instanceof DirectColorModel && ((DirectColorModel)colorModel).getBlueMask() == 62) {
                    return SurfaceType.Ushort555Rgbx;
                }
                return GDIWindowSurfaceData.Ushort565RgbGdi;
            }
            case 8: {
                if (colorModel.getColorSpace().getType() == 6 && colorModel instanceof ComponentColorModel) {
                    return SurfaceType.ByteGray;
                }
                if (colorModel instanceof IndexColorModel && SurfaceData.isOpaqueGray((IndexColorModel)colorModel)) {
                    return SurfaceType.Index8Gray;
                }
                return SurfaceType.ByteIndexedOpaque;
            }
            default: {
                throw new InvalidPipeException("Unsupported bit depth: " + colorModel.getPixelSize());
            }
        }
    }
    
    public static GDIWindowSurfaceData createData(final WComponentPeer wComponentPeer) {
        return new GDIWindowSurfaceData(wComponentPeer, getSurfaceType(wComponentPeer.getDeviceColorModel()));
    }
    
    @Override
    public SurfaceDataProxy makeProxyFor(final SurfaceData surfaceData) {
        return SurfaceDataProxy.UNCACHED;
    }
    
    @Override
    public Raster getRaster(final int n, final int n2, final int n3, final int n4) {
        throw new InternalError("not implemented yet");
    }
    
    @Override
    public void validatePipe(final SunGraphics2D sunGraphics2D) {
        if (sunGraphics2D.antialiasHint != 2 && sunGraphics2D.paintState <= 1 && (sunGraphics2D.compositeState <= 0 || sunGraphics2D.compositeState == 2)) {
            Label_0175: {
                if (sunGraphics2D.clipState == 2) {
                    super.validatePipe(sunGraphics2D);
                }
                else {
                    switch (sunGraphics2D.textAntialiasHint) {
                        case 0:
                        case 1: {
                            sunGraphics2D.textpipe = GDIWindowSurfaceData.solidTextRenderer;
                            break;
                        }
                        case 2: {
                            sunGraphics2D.textpipe = GDIWindowSurfaceData.aaTextRenderer;
                            break;
                        }
                        default: {
                            switch (sunGraphics2D.getFontInfo().aaHint) {
                                case 4:
                                case 6: {
                                    sunGraphics2D.textpipe = GDIWindowSurfaceData.lcdTextRenderer;
                                    break Label_0175;
                                }
                                case 2: {
                                    sunGraphics2D.textpipe = GDIWindowSurfaceData.aaTextRenderer;
                                    break Label_0175;
                                }
                                default: {
                                    sunGraphics2D.textpipe = GDIWindowSurfaceData.solidTextRenderer;
                                    break Label_0175;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            sunGraphics2D.imagepipe = GDIWindowSurfaceData.imagepipe;
            if (sunGraphics2D.transformState >= 3) {
                sunGraphics2D.drawpipe = GDIWindowSurfaceData.gdiTxPipe;
                sunGraphics2D.fillpipe = GDIWindowSurfaceData.gdiTxPipe;
            }
            else if (sunGraphics2D.strokeState != 0) {
                sunGraphics2D.drawpipe = GDIWindowSurfaceData.gdiTxPipe;
                sunGraphics2D.fillpipe = GDIWindowSurfaceData.gdiPipe;
            }
            else {
                sunGraphics2D.drawpipe = GDIWindowSurfaceData.gdiPipe;
                sunGraphics2D.fillpipe = GDIWindowSurfaceData.gdiPipe;
            }
            sunGraphics2D.shapepipe = GDIWindowSurfaceData.gdiPipe;
            if (sunGraphics2D.loops == null) {
                sunGraphics2D.loops = this.getRenderLoops(sunGraphics2D);
            }
        }
        else {
            super.validatePipe(sunGraphics2D);
        }
    }
    
    @Override
    public RenderLoops getRenderLoops(final SunGraphics2D sunGraphics2D) {
        if (sunGraphics2D.paintState <= 1 && sunGraphics2D.compositeState <= 0) {
            return this.solidloops;
        }
        return super.getRenderLoops(sunGraphics2D);
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return this.graphicsConfig;
    }
    
    private native void initOps(final WComponentPeer p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    private GDIWindowSurfaceData(final WComponentPeer peer, final SurfaceType surfaceType) {
        super(surfaceType, peer.getDeviceColorModel());
        final ColorModel deviceColorModel = peer.getDeviceColorModel();
        this.peer = peer;
        int redMask = 0;
        int greenMask = 0;
        int blueMask = 0;
        int pixelSize = 0;
        switch (deviceColorModel.getPixelSize()) {
            case 24:
            case 32: {
                if (deviceColorModel instanceof DirectColorModel) {
                    pixelSize = 32;
                    break;
                }
                pixelSize = 24;
                break;
            }
            default: {
                pixelSize = deviceColorModel.getPixelSize();
                break;
            }
        }
        if (deviceColorModel instanceof DirectColorModel) {
            final DirectColorModel directColorModel = (DirectColorModel)deviceColorModel;
            redMask = directColorModel.getRedMask();
            greenMask = directColorModel.getGreenMask();
            blueMask = directColorModel.getBlueMask();
        }
        this.graphicsConfig = (Win32GraphicsConfig)peer.getGraphicsConfiguration();
        this.solidloops = this.graphicsConfig.getSolidLoops(surfaceType);
        this.initOps(peer, pixelSize, redMask, greenMask, blueMask, ((Win32GraphicsDevice)this.graphicsConfig.getDevice()).getScreen());
        this.setBlitProxyKey(this.graphicsConfig.getProxyKey());
    }
    
    @Override
    public SurfaceData getReplacement() {
        return ScreenUpdateManager.getInstance().getReplacementScreenSurface(this.peer, this);
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
    public boolean copyArea(final SunGraphics2D sunGraphics2D, int n, int n2, final int n3, final int n4, final int n5, final int n6) {
        final CompositeType imageComp = sunGraphics2D.imageComp;
        if (sunGraphics2D.transformState < 3 && sunGraphics2D.clipState != 2 && (CompositeType.SrcOverNoEa.equals(imageComp) || CompositeType.SrcNoEa.equals(imageComp))) {
            n += sunGraphics2D.transX;
            n2 += sunGraphics2D.transY;
            int loX = n + n5;
            int loY = n2 + n6;
            int hiX = loX + n3;
            int hiY = loY + n4;
            final Region compClip = sunGraphics2D.getCompClip();
            if (loX < compClip.getLoX()) {
                loX = compClip.getLoX();
            }
            if (loY < compClip.getLoY()) {
                loY = compClip.getLoY();
            }
            if (hiX > compClip.getHiX()) {
                hiX = compClip.getHiX();
            }
            if (hiY > compClip.getHiY()) {
                hiY = compClip.getHiY();
            }
            if (loX < hiX && loY < hiY) {
                GDIWindowSurfaceData.gdiPipe.devCopyArea(this, loX - n5, loY - n6, n5, n6, hiX - loX, hiY - loY);
            }
            return true;
        }
        return false;
    }
    
    private native void invalidateSD();
    
    @Override
    public void invalidate() {
        if (this.isValid()) {
            this.invalidateSD();
            super.invalidate();
        }
    }
    
    @Override
    public Object getDestination() {
        return this.peer.getTarget();
    }
    
    public WComponentPeer getPeer() {
        return this.peer;
    }
    
    static {
        AnyGdi = SurfaceType.IntRgb.deriveSubType("GDI");
        IntRgbGdi = SurfaceType.IntRgb.deriveSubType("GDI");
        Ushort565RgbGdi = SurfaceType.Ushort565Rgb.deriveSubType("GDI");
        Ushort555RgbGdi = SurfaceType.Ushort555Rgb.deriveSubType("GDI");
        ThreeByteBgrGdi = SurfaceType.ThreeByteBgr.deriveSubType("GDI");
        initIDs(XORComposite.class);
        if (WindowsFlags.isGdiBlitEnabled()) {
            GDIBlitLoops.register();
        }
        GDIWindowSurfaceData.gdiPipe = new GDIRenderer();
        if (GraphicsPrimitive.tracingEnabled()) {
            GDIWindowSurfaceData.gdiPipe = GDIWindowSurfaceData.gdiPipe.traceWrap();
        }
        GDIWindowSurfaceData.gdiTxPipe = new PixelToShapeConverter(GDIWindowSurfaceData.gdiPipe);
    }
}
