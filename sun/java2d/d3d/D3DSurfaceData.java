package sun.java2d.d3d;

import sun.java2d.pipe.PixelFillPipe;
import java.awt.Window;
import sun.java2d.ScreenUpdateManager;
import sun.java2d.StateTracker;
import sun.awt.image.DataBufferNative;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.RenderQueue;
import sun.awt.image.PixelConverter;
import sun.java2d.pipe.BufferedContext;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;
import sun.awt.SunToolkit;
import java.awt.Component;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.loops.MaskFill;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.TextPipe;
import sun.java2d.loops.CompositeType;
import java.awt.AlphaComposite;
import sun.awt.image.SurfaceManager;
import sun.java2d.SunGraphics2D;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import sun.java2d.InvalidPipeException;
import sun.awt.image.SunVolatileImage;
import java.awt.Rectangle;
import java.awt.BufferCapabilities;
import sun.java2d.SurfaceDataProxy;
import java.awt.image.ColorModel;
import sun.java2d.pipe.ParallelogramPipe;
import sun.java2d.pipe.PixelToParallelogramConverter;
import sun.awt.image.WritableRasterNative;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import java.awt.Image;
import sun.awt.windows.WComponentPeer;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.SurfaceData;

public class D3DSurfaceData extends SurfaceData implements AccelSurface
{
    public static final int D3D_DEVICE_RESOURCE = 100;
    public static final int ST_INT_ARGB = 0;
    public static final int ST_INT_ARGB_PRE = 1;
    public static final int ST_INT_ARGB_BM = 2;
    public static final int ST_INT_RGB = 3;
    public static final int ST_INT_BGR = 4;
    public static final int ST_USHORT_565_RGB = 5;
    public static final int ST_USHORT_555_RGB = 6;
    public static final int ST_BYTE_INDEXED = 7;
    public static final int ST_BYTE_INDEXED_BM = 8;
    public static final int ST_3BYTE_BGR = 9;
    public static final int SWAP_DISCARD = 1;
    public static final int SWAP_FLIP = 2;
    public static final int SWAP_COPY = 3;
    private static final String DESC_D3D_SURFACE = "D3D Surface";
    private static final String DESC_D3D_SURFACE_RTT = "D3D Surface (render-to-texture)";
    private static final String DESC_D3D_TEXTURE = "D3D Texture";
    static final SurfaceType D3DSurface;
    static final SurfaceType D3DSurfaceRTT;
    static final SurfaceType D3DTexture;
    private int type;
    private int width;
    private int height;
    private int nativeWidth;
    private int nativeHeight;
    protected WComponentPeer peer;
    private Image offscreenImage;
    protected D3DGraphicsDevice graphicsDevice;
    private int swapEffect;
    private ExtendedBufferCapabilities.VSyncType syncType;
    private int backBuffersNum;
    private WritableRasterNative wrn;
    protected static D3DRenderer d3dRenderPipe;
    protected static PixelToParallelogramConverter d3dTxRenderPipe;
    protected static ParallelogramPipe d3dAAPgramPipe;
    protected static D3DTextRenderer d3dTextPipe;
    protected static D3DDrawImage d3dImagePipe;
    
    private native boolean initTexture(final long p0, final boolean p1, final boolean p2);
    
    private native boolean initFlipBackbuffer(final long p0, final long p1, final int p2, final int p3, final int p4);
    
    private native boolean initRTSurface(final long p0, final boolean p1);
    
    private native void initOps(final int p0, final int p1, final int p2);
    
    protected D3DSurfaceData(final WComponentPeer peer, final D3DGraphicsConfig d3DGraphicsConfig, final int width, final int height, final Image offscreenImage, final ColorModel colorModel, final int backBuffersNum, final int swapEffect, final ExtendedBufferCapabilities.VSyncType syncType, final int type) {
        super(getCustomSurfaceType(type), colorModel);
        this.graphicsDevice = d3DGraphicsConfig.getD3DDevice();
        this.peer = peer;
        this.type = type;
        this.width = width;
        this.height = height;
        this.offscreenImage = offscreenImage;
        this.backBuffersNum = backBuffersNum;
        this.swapEffect = swapEffect;
        this.syncType = syncType;
        this.initOps(this.graphicsDevice.getScreen(), width, height);
        if (type == 1) {
            this.setSurfaceLost(true);
        }
        else {
            this.initSurface();
        }
        this.setBlitProxyKey(d3DGraphicsConfig.getProxyKey());
    }
    
    @Override
    public SurfaceDataProxy makeProxyFor(final SurfaceData surfaceData) {
        return D3DSurfaceDataProxy.createProxy(surfaceData, (D3DGraphicsConfig)this.graphicsDevice.getDefaultConfiguration());
    }
    
    public static D3DSurfaceData createData(final WComponentPeer wComponentPeer, final Image image) {
        final D3DGraphicsConfig gc = getGC(wComponentPeer);
        if (gc == null || !wComponentPeer.isAccelCapable()) {
            return null;
        }
        final BufferCapabilities backBufferCaps = wComponentPeer.getBackBufferCaps();
        ExtendedBufferCapabilities.VSyncType vSyncType = ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT;
        if (backBufferCaps instanceof ExtendedBufferCapabilities) {
            vSyncType = ((ExtendedBufferCapabilities)backBufferCaps).getVSync();
        }
        final Rectangle bounds = wComponentPeer.getBounds();
        final BufferCapabilities.FlipContents flipContents = backBufferCaps.getFlipContents();
        int n;
        if (flipContents == BufferCapabilities.FlipContents.COPIED) {
            n = 3;
        }
        else if (flipContents == BufferCapabilities.FlipContents.PRIOR) {
            n = 2;
        }
        else {
            n = 1;
        }
        return new D3DSurfaceData(wComponentPeer, gc, bounds.width, bounds.height, image, wComponentPeer.getColorModel(), wComponentPeer.getBackBuffersNum(), n, vSyncType, 4);
    }
    
    public static D3DSurfaceData createData(final WComponentPeer wComponentPeer) {
        final D3DGraphicsConfig gc = getGC(wComponentPeer);
        if (gc == null || !wComponentPeer.isAccelCapable()) {
            return null;
        }
        return new D3DWindowSurfaceData(wComponentPeer, gc);
    }
    
    public static D3DSurfaceData createData(final D3DGraphicsConfig d3DGraphicsConfig, final int n, final int n2, final ColorModel colorModel, final Image image, int n3) {
        if (n3 == 5 && !d3DGraphicsConfig.getD3DDevice().isCapPresent((colorModel.getTransparency() == 1) ? 8 : 4)) {
            n3 = 2;
        }
        D3DSurfaceData d3DSurfaceData = null;
        try {
            d3DSurfaceData = new D3DSurfaceData(null, d3DGraphicsConfig, n, n2, image, colorModel, 0, 1, ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT, n3);
        }
        catch (final InvalidPipeException ex) {
            if (n3 == 5 && ((SunVolatileImage)image).getForcedAccelSurfaceType() != 5) {
                n3 = 2;
                d3DSurfaceData = new D3DSurfaceData(null, d3DGraphicsConfig, n, n2, image, colorModel, 0, 1, ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT, n3);
            }
        }
        return d3DSurfaceData;
    }
    
    private static SurfaceType getCustomSurfaceType(final int n) {
        switch (n) {
            case 3: {
                return D3DSurfaceData.D3DTexture;
            }
            case 5: {
                return D3DSurfaceData.D3DSurfaceRTT;
            }
            default: {
                return D3DSurfaceData.D3DSurface;
            }
        }
    }
    
    private boolean initSurfaceNow() {
        final boolean b = this.getTransparency() == 1;
        switch (this.type) {
            case 2: {
                return this.initRTSurface(this.getNativeOps(), b);
            }
            case 3: {
                return this.initTexture(this.getNativeOps(), false, b);
            }
            case 5: {
                return this.initTexture(this.getNativeOps(), true, b);
            }
            case 1:
            case 4: {
                return this.initFlipBackbuffer(this.getNativeOps(), this.peer.getData(), this.backBuffersNum, this.swapEffect, this.syncType.id());
            }
            default: {
                return false;
            }
        }
    }
    
    protected void initSurface() {
        synchronized (this) {
            this.wrn = null;
        }
        class Status
        {
            boolean success;
            
            Status() {
                this.success = false;
            }
        }
        final Status status = new Status();
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            instance.flushAndInvokeNow(new Runnable() {
                @Override
                public void run() {
                    status.success = D3DSurfaceData.this.initSurfaceNow();
                }
            });
            if (!status.success) {
                throw new InvalidPipeException("Error creating D3DSurface");
            }
        }
        finally {
            instance.unlock();
        }
    }
    
    @Override
    public final D3DContext getContext() {
        return this.graphicsDevice.getContext();
    }
    
    @Override
    public final int getType() {
        return this.type;
    }
    
    private static native int dbGetPixelNative(final long p0, final int p1, final int p2);
    
    private static native void dbSetPixelNative(final long p0, final int p1, final int p2, final int p3);
    
    @Override
    public synchronized Raster getRaster(final int n, final int n2, final int n3, final int n4) {
        if (this.wrn == null) {
            final DirectColorModel directColorModel = (DirectColorModel)this.getColorModel();
            final int width = this.width;
            int n5;
            if (directColorModel.getPixelSize() > 16) {
                n5 = 3;
            }
            else {
                n5 = 1;
            }
            this.wrn = WritableRasterNative.createNativeRaster(new SinglePixelPackedSampleModel(n5, this.width, this.height, width, directColorModel.getMasks()), new D3DDataBufferNative(this, n5, this.width, this.height));
        }
        return this.wrn;
    }
    
    @Override
    public boolean canRenderLCDText(final SunGraphics2D sunGraphics2D) {
        return this.graphicsDevice.isCapPresent(65536) && sunGraphics2D.compositeState <= 0 && sunGraphics2D.paintState <= 0 && sunGraphics2D.surfaceData.getTransparency() == 1;
    }
    
    void disableAccelerationForSurface() {
        if (this.offscreenImage != null) {
            final SurfaceManager manager = SurfaceManager.getManager(this.offscreenImage);
            if (manager instanceof D3DVolatileSurfaceManager) {
                this.setSurfaceLost(true);
                ((D3DVolatileSurfaceManager)manager).setAccelerationEnabled(false);
            }
        }
    }
    
    @Override
    public void validatePipe(final SunGraphics2D sunGraphics2D) {
        int n = 0;
        if (sunGraphics2D.compositeState >= 2) {
            super.validatePipe(sunGraphics2D);
            sunGraphics2D.imagepipe = D3DSurfaceData.d3dImagePipe;
            this.disableAccelerationForSurface();
            return;
        }
        TextPipe textpipe;
        if ((sunGraphics2D.compositeState <= 0 && sunGraphics2D.paintState <= 1) || (sunGraphics2D.compositeState == 1 && sunGraphics2D.paintState <= 1 && ((AlphaComposite)sunGraphics2D.composite).getRule() == 3) || (sunGraphics2D.compositeState == 2 && sunGraphics2D.paintState <= 1)) {
            textpipe = D3DSurfaceData.d3dTextPipe;
        }
        else {
            super.validatePipe(sunGraphics2D);
            textpipe = sunGraphics2D.textpipe;
            n = 1;
        }
        ShapeDrawPipe shapeDrawPipe = null;
        PixelDrawPipe fillpipe = null;
        if (sunGraphics2D.antialiasHint != 2) {
            if (sunGraphics2D.paintState <= 1) {
                if (sunGraphics2D.compositeState <= 2) {
                    shapeDrawPipe = D3DSurfaceData.d3dTxRenderPipe;
                    fillpipe = D3DSurfaceData.d3dRenderPipe;
                }
            }
            else if (sunGraphics2D.compositeState <= 1 && D3DPaints.isValid(sunGraphics2D)) {
                shapeDrawPipe = D3DSurfaceData.d3dTxRenderPipe;
                fillpipe = D3DSurfaceData.d3dRenderPipe;
            }
        }
        else if (sunGraphics2D.paintState <= 1) {
            if (this.graphicsDevice.isCapPresent(524288) && (sunGraphics2D.imageComp == CompositeType.SrcOverNoEa || sunGraphics2D.imageComp == CompositeType.SrcOver)) {
                if (n == 0) {
                    super.validatePipe(sunGraphics2D);
                    n = 1;
                }
                final PixelToParallelogramConverter shapepipe = new PixelToParallelogramConverter(sunGraphics2D.shapepipe, D3DSurfaceData.d3dAAPgramPipe, 0.125, 0.499, false);
                sunGraphics2D.drawpipe = shapepipe;
                sunGraphics2D.fillpipe = shapepipe;
                sunGraphics2D.shapepipe = shapepipe;
            }
            else if (sunGraphics2D.compositeState == 2) {
                shapeDrawPipe = D3DSurfaceData.d3dTxRenderPipe;
                fillpipe = D3DSurfaceData.d3dRenderPipe;
            }
        }
        if (shapeDrawPipe != null) {
            if (sunGraphics2D.transformState >= 3) {
                sunGraphics2D.drawpipe = (PixelDrawPipe)shapeDrawPipe;
                sunGraphics2D.fillpipe = (PixelFillPipe)shapeDrawPipe;
            }
            else if (sunGraphics2D.strokeState != 0) {
                sunGraphics2D.drawpipe = (PixelDrawPipe)shapeDrawPipe;
                sunGraphics2D.fillpipe = (PixelFillPipe)fillpipe;
            }
            else {
                sunGraphics2D.drawpipe = fillpipe;
                sunGraphics2D.fillpipe = (PixelFillPipe)fillpipe;
            }
            sunGraphics2D.shapepipe = shapeDrawPipe;
        }
        else if (n == 0) {
            super.validatePipe(sunGraphics2D);
        }
        sunGraphics2D.textpipe = textpipe;
        sunGraphics2D.imagepipe = D3DSurfaceData.d3dImagePipe;
    }
    
    @Override
    protected MaskFill getMaskFill(final SunGraphics2D sunGraphics2D) {
        if (sunGraphics2D.paintState > 1 && (!D3DPaints.isValid(sunGraphics2D) || !this.graphicsDevice.isCapPresent(16))) {
            return null;
        }
        return super.getMaskFill(sunGraphics2D);
    }
    
    @Override
    public boolean copyArea(final SunGraphics2D sunGraphics2D, int n, int n2, final int n3, final int n4, final int n5, final int n6) {
        if (sunGraphics2D.transformState < 3 && sunGraphics2D.compositeState < 2) {
            n += sunGraphics2D.transX;
            n2 += sunGraphics2D.transY;
            D3DSurfaceData.d3dRenderPipe.copyArea(sunGraphics2D, n, n2, n3, n4, n5, n6);
            return true;
        }
        return false;
    }
    
    @Override
    public void flush() {
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            final RenderBuffer buffer = instance.getBuffer();
            instance.ensureCapacityAndAlignment(12, 4);
            buffer.putInt(72);
            buffer.putLong(this.getNativeOps());
            instance.flushNow();
        }
        finally {
            instance.unlock();
        }
    }
    
    static void dispose(final long n) {
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            final RenderBuffer buffer = instance.getBuffer();
            instance.ensureCapacityAndAlignment(12, 4);
            buffer.putInt(73);
            buffer.putLong(n);
            instance.flushNow();
        }
        finally {
            instance.unlock();
        }
    }
    
    static void swapBuffers(final D3DSurfaceData d3DSurfaceData, final int n, final int n2, final int n3, final int n4) {
        final long nativeOps = d3DSurfaceData.getNativeOps();
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        if (D3DRenderQueue.isRenderQueueThread()) {
            if (!instance.tryLock()) {
                final Component component = (Component)d3DSurfaceData.getPeer().getTarget();
                SunToolkit.executeOnEventHandlerThread(component, new Runnable() {
                    @Override
                    public void run() {
                        component.repaint(n, n2, n3, n4);
                    }
                });
                return;
            }
        }
        else {
            instance.lock();
        }
        try {
            final RenderBuffer buffer = instance.getBuffer();
            instance.ensureCapacityAndAlignment(28, 4);
            buffer.putInt(80);
            buffer.putLong(nativeOps);
            buffer.putInt(n);
            buffer.putInt(n2);
            buffer.putInt(n3);
            buffer.putInt(n4);
            instance.flushNow();
        }
        finally {
            instance.unlock();
        }
    }
    
    @Override
    public Object getDestination() {
        return this.offscreenImage;
    }
    
    @Override
    public Rectangle getBounds() {
        if (this.type == 4 || this.type == 1) {
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
    public Rectangle getNativeBounds() {
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            return new Rectangle(this.nativeWidth, this.nativeHeight);
        }
        finally {
            instance.unlock();
        }
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return this.graphicsDevice.getDefaultConfiguration();
    }
    
    @Override
    public SurfaceData getReplacement() {
        return SurfaceData.restoreContents(this.offscreenImage);
    }
    
    private static D3DGraphicsConfig getGC(final WComponentPeer wComponentPeer) {
        GraphicsConfiguration graphicsConfiguration;
        if (wComponentPeer != null) {
            graphicsConfiguration = wComponentPeer.getGraphicsConfiguration();
        }
        else {
            graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }
        return (graphicsConfiguration instanceof D3DGraphicsConfig) ? ((D3DGraphicsConfig)graphicsConfiguration) : null;
    }
    
    void restoreSurface() {
        this.initSurface();
    }
    
    WComponentPeer getPeer() {
        return this.peer;
    }
    
    @Override
    public void setSurfaceLost(final boolean surfaceLost) {
        super.setSurfaceLost(surfaceLost);
        if (surfaceLost && this.offscreenImage != null) {
            SurfaceManager.getManager(this.offscreenImage).acceleratedSurfaceLost();
        }
    }
    
    private static native long getNativeResourceNative(final long p0, final int p1);
    
    @Override
    public long getNativeResource(final int n) {
        return getNativeResourceNative(this.getNativeOps(), n);
    }
    
    public static native boolean updateWindowAccelImpl(final long p0, final long p1, final int p2, final int p3);
    
    static {
        D3DSurface = SurfaceType.Any.deriveSubType("D3D Surface", PixelConverter.ArgbPre.instance);
        D3DSurfaceRTT = D3DSurfaceData.D3DSurface.deriveSubType("D3D Surface (render-to-texture)");
        D3DTexture = SurfaceType.Any.deriveSubType("D3D Texture");
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        D3DSurfaceData.d3dImagePipe = new D3DDrawImage();
        D3DSurfaceData.d3dTextPipe = new D3DTextRenderer(instance);
        D3DSurfaceData.d3dRenderPipe = new D3DRenderer(instance);
        if (GraphicsPrimitive.tracingEnabled()) {
            D3DSurfaceData.d3dTextPipe = D3DSurfaceData.d3dTextPipe.traceWrap();
            D3DSurfaceData.d3dRenderPipe = D3DSurfaceData.d3dRenderPipe.traceWrap();
        }
        D3DSurfaceData.d3dAAPgramPipe = D3DSurfaceData.d3dRenderPipe.getAAParallelogramPipe();
        D3DSurfaceData.d3dTxRenderPipe = new PixelToParallelogramConverter(D3DSurfaceData.d3dRenderPipe, D3DSurfaceData.d3dRenderPipe, 1.0, 0.25, true);
        D3DBlitLoops.register();
        D3DMaskFill.register();
        D3DMaskBlit.register();
    }
    
    static class D3DDataBufferNative extends DataBufferNative
    {
        int pixel;
        
        protected D3DDataBufferNative(final SurfaceData surfaceData, final int n, final int n2, final int n3) {
            super(surfaceData, n, n2, n3);
        }
        
        @Override
        protected int getElem(final int n, final int n2, final SurfaceData surfaceData) {
            if (surfaceData.isSurfaceLost()) {
                return 0;
            }
            final D3DRenderQueue instance = D3DRenderQueue.getInstance();
            instance.lock();
            int pixel = 0;
            try {
                instance.flushAndInvokeNow(new Runnable() {
                    @Override
                    public void run() {
                        D3DDataBufferNative.this.pixel = dbGetPixelNative(surfaceData.getNativeOps(), n, n2);
                    }
                });
            }
            finally {
                pixel = this.pixel;
                instance.unlock();
            }
            return pixel;
        }
        
        @Override
        protected void setElem(final int n, final int n2, final int n3, final SurfaceData surfaceData) {
            if (surfaceData.isSurfaceLost()) {
                return;
            }
            final D3DRenderQueue instance = D3DRenderQueue.getInstance();
            instance.lock();
            try {
                instance.flushAndInvokeNow(new Runnable() {
                    @Override
                    public void run() {
                        dbSetPixelNative(surfaceData.getNativeOps(), n, n2, n3);
                    }
                });
                surfaceData.markDirty();
            }
            finally {
                instance.unlock();
            }
        }
    }
    
    public static class D3DWindowSurfaceData extends D3DSurfaceData
    {
        StateTracker dirtyTracker;
        
        public D3DWindowSurfaceData(final WComponentPeer wComponentPeer, final D3DGraphicsConfig d3DGraphicsConfig) {
            super(wComponentPeer, d3DGraphicsConfig, wComponentPeer.getBounds().width, wComponentPeer.getBounds().height, null, wComponentPeer.getColorModel(), 1, 3, ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT, 1);
            this.dirtyTracker = this.getStateTracker();
        }
        
        @Override
        public SurfaceData getReplacement() {
            return ScreenUpdateManager.getInstance().getReplacementScreenSurface(this.peer, this);
        }
        
        @Override
        public Object getDestination() {
            return this.peer.getTarget();
        }
        
        @Override
        void disableAccelerationForSurface() {
            this.setSurfaceLost(true);
            this.invalidate();
            this.flush();
            this.peer.disableAcceleration();
            ScreenUpdateManager.getInstance().dropScreenSurface(this);
        }
        
        @Override
        void restoreSurface() {
            if (!this.peer.isAccelCapable()) {
                throw new InvalidPipeException("Onscreen acceleration disabled for this surface");
            }
            final Window fullScreenWindow = this.graphicsDevice.getFullScreenWindow();
            if (fullScreenWindow != null && fullScreenWindow != this.peer.getTarget()) {
                throw new InvalidPipeException("Can't restore onscreen surface when in full-screen mode");
            }
            super.restoreSurface();
            this.setSurfaceLost(false);
            final D3DRenderQueue instance = D3DRenderQueue.getInstance();
            instance.lock();
            try {
                this.getContext().invalidateContext();
            }
            finally {
                instance.unlock();
            }
        }
        
        public boolean isDirty() {
            return !this.dirtyTracker.isCurrent();
        }
        
        public void markClean() {
            this.dirtyTracker = this.getStateTracker();
        }
    }
}
