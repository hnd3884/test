package sun.java2d.opengl;

import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.RenderQueue;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.awt.GraphicsEnvironment;
import sun.awt.image.PixelConverter;
import sun.java2d.pipe.BufferedContext;
import java.awt.Rectangle;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.loops.MaskFill;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.TextPipe;
import sun.java2d.loops.CompositeType;
import java.awt.AlphaComposite;
import java.awt.Composite;
import sun.java2d.SunGraphics2D;
import java.awt.image.Raster;
import sun.java2d.SurfaceDataProxy;
import java.awt.image.ColorModel;
import sun.java2d.pipe.ParallelogramPipe;
import sun.java2d.pipe.PixelToParallelogramConverter;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.SurfaceData;

public abstract class OGLSurfaceData extends SurfaceData implements AccelSurface
{
    public static final int FBOBJECT = 5;
    public static final int PF_INT_ARGB = 0;
    public static final int PF_INT_ARGB_PRE = 1;
    public static final int PF_INT_RGB = 2;
    public static final int PF_INT_RGBX = 3;
    public static final int PF_INT_BGR = 4;
    public static final int PF_INT_BGRX = 5;
    public static final int PF_USHORT_565_RGB = 6;
    public static final int PF_USHORT_555_RGB = 7;
    public static final int PF_USHORT_555_RGBX = 8;
    public static final int PF_BYTE_GRAY = 9;
    public static final int PF_USHORT_GRAY = 10;
    public static final int PF_3BYTE_BGR = 11;
    private static final String DESC_OPENGL_SURFACE = "OpenGL Surface";
    private static final String DESC_OPENGL_SURFACE_RTT = "OpenGL Surface (render-to-texture)";
    private static final String DESC_OPENGL_TEXTURE = "OpenGL Texture";
    static final SurfaceType OpenGLSurface;
    static final SurfaceType OpenGLSurfaceRTT;
    static final SurfaceType OpenGLTexture;
    private static boolean isFBObjectEnabled;
    private static boolean isLCDShaderEnabled;
    private static boolean isBIOpShaderEnabled;
    private static boolean isGradShaderEnabled;
    private OGLGraphicsConfig graphicsConfig;
    protected int type;
    private int nativeWidth;
    private int nativeHeight;
    protected static OGLRenderer oglRenderPipe;
    protected static PixelToParallelogramConverter oglTxRenderPipe;
    protected static ParallelogramPipe oglAAPgramPipe;
    protected static OGLTextRenderer oglTextPipe;
    protected static OGLDrawImage oglImagePipe;
    
    protected native boolean initTexture(final long p0, final boolean p1, final boolean p2, final boolean p3, final int p4, final int p5);
    
    protected native boolean initFBObject(final long p0, final boolean p1, final boolean p2, final boolean p3, final int p4, final int p5);
    
    protected native boolean initFlipBackbuffer(final long p0);
    
    private native int getTextureTarget(final long p0);
    
    private native int getTextureID(final long p0);
    
    protected OGLSurfaceData(final OGLGraphicsConfig graphicsConfig, final ColorModel colorModel, final int type) {
        super(getCustomSurfaceType(type), colorModel);
        this.graphicsConfig = graphicsConfig;
        this.type = type;
        this.setBlitProxyKey(graphicsConfig.getProxyKey());
    }
    
    @Override
    public SurfaceDataProxy makeProxyFor(final SurfaceData surfaceData) {
        return OGLSurfaceDataProxy.createProxy(surfaceData, this.graphicsConfig);
    }
    
    private static SurfaceType getCustomSurfaceType(final int n) {
        switch (n) {
            case 3: {
                return OGLSurfaceData.OpenGLTexture;
            }
            case 5: {
                return OGLSurfaceData.OpenGLSurfaceRTT;
            }
            default: {
                return OGLSurfaceData.OpenGLSurface;
            }
        }
    }
    
    private void initSurfaceNow(final int n, final int n2) {
        final boolean b = this.getTransparency() == 1;
        boolean b2 = false;
        switch (this.type) {
            case 3: {
                b2 = this.initTexture(this.getNativeOps(), b, this.isTexNonPow2Available(), this.isTexRectAvailable(), n, n2);
                break;
            }
            case 5: {
                b2 = this.initFBObject(this.getNativeOps(), b, this.isTexNonPow2Available(), this.isTexRectAvailable(), n, n2);
                break;
            }
            case 4: {
                b2 = this.initFlipBackbuffer(this.getNativeOps());
                break;
            }
        }
        if (!b2) {
            throw new OutOfMemoryError("can't create offscreen surface");
        }
    }
    
    protected void initSurface(final int n, final int n2) {
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        instance.lock();
        try {
            switch (this.type) {
                case 3:
                case 5: {
                    OGLContext.setScratchSurface(this.graphicsConfig);
                    break;
                }
            }
            instance.flushAndInvokeNow(new Runnable() {
                @Override
                public void run() {
                    OGLSurfaceData.this.initSurfaceNow(n, n2);
                }
            });
        }
        finally {
            instance.unlock();
        }
    }
    
    @Override
    public final OGLContext getContext() {
        return this.graphicsConfig.getContext();
    }
    
    final OGLGraphicsConfig getOGLGraphicsConfig() {
        return this.graphicsConfig;
    }
    
    @Override
    public final int getType() {
        return this.type;
    }
    
    public final int getTextureTarget() {
        return this.getTextureTarget(this.getNativeOps());
    }
    
    public final int getTextureID() {
        return this.getTextureID(this.getNativeOps());
    }
    
    @Override
    public long getNativeResource(final int n) {
        if (n == 3) {
            return this.getTextureID();
        }
        return 0L;
    }
    
    @Override
    public Raster getRaster(final int n, final int n2, final int n3, final int n4) {
        throw new InternalError("not implemented yet");
    }
    
    @Override
    public boolean canRenderLCDText(final SunGraphics2D sunGraphics2D) {
        return this.graphicsConfig.isCapPresent(131072) && sunGraphics2D.surfaceData.getTransparency() == 1 && sunGraphics2D.paintState <= 0 && (sunGraphics2D.compositeState <= 0 || (sunGraphics2D.compositeState <= 1 && this.canHandleComposite(sunGraphics2D.composite)));
    }
    
    private boolean canHandleComposite(final Composite composite) {
        if (composite instanceof AlphaComposite) {
            final AlphaComposite alphaComposite = (AlphaComposite)composite;
            return alphaComposite.getRule() == 3 && alphaComposite.getAlpha() >= 1.0f;
        }
        return false;
    }
    
    @Override
    public void validatePipe(final SunGraphics2D sunGraphics2D) {
        int n = 0;
        TextPipe textpipe;
        if ((sunGraphics2D.compositeState <= 0 && sunGraphics2D.paintState <= 1) || (sunGraphics2D.compositeState == 1 && sunGraphics2D.paintState <= 1 && ((AlphaComposite)sunGraphics2D.composite).getRule() == 3) || (sunGraphics2D.compositeState == 2 && sunGraphics2D.paintState <= 1)) {
            textpipe = OGLSurfaceData.oglTextPipe;
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
                    shapeDrawPipe = OGLSurfaceData.oglTxRenderPipe;
                    fillpipe = OGLSurfaceData.oglRenderPipe;
                }
            }
            else if (sunGraphics2D.compositeState <= 1 && OGLPaints.isValid(sunGraphics2D)) {
                shapeDrawPipe = OGLSurfaceData.oglTxRenderPipe;
                fillpipe = OGLSurfaceData.oglRenderPipe;
            }
        }
        else if (sunGraphics2D.paintState <= 1) {
            if (this.graphicsConfig.isCapPresent(256) && (sunGraphics2D.imageComp == CompositeType.SrcOverNoEa || sunGraphics2D.imageComp == CompositeType.SrcOver)) {
                if (n == 0) {
                    super.validatePipe(sunGraphics2D);
                    n = 1;
                }
                final PixelToParallelogramConverter shapepipe = new PixelToParallelogramConverter(sunGraphics2D.shapepipe, OGLSurfaceData.oglAAPgramPipe, 0.125, 0.499, false);
                sunGraphics2D.drawpipe = shapepipe;
                sunGraphics2D.fillpipe = shapepipe;
                sunGraphics2D.shapepipe = shapepipe;
            }
            else if (sunGraphics2D.compositeState == 2) {
                shapeDrawPipe = OGLSurfaceData.oglTxRenderPipe;
                fillpipe = OGLSurfaceData.oglRenderPipe;
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
        sunGraphics2D.imagepipe = OGLSurfaceData.oglImagePipe;
    }
    
    @Override
    protected MaskFill getMaskFill(final SunGraphics2D sunGraphics2D) {
        if (sunGraphics2D.paintState > 1 && (!OGLPaints.isValid(sunGraphics2D) || !this.graphicsConfig.isCapPresent(16))) {
            return null;
        }
        return super.getMaskFill(sunGraphics2D);
    }
    
    @Override
    public boolean copyArea(final SunGraphics2D sunGraphics2D, int n, int n2, final int n3, final int n4, final int n5, final int n6) {
        if (sunGraphics2D.transformState < 3 && sunGraphics2D.compositeState < 2) {
            n += sunGraphics2D.transX;
            n2 += sunGraphics2D.transY;
            OGLSurfaceData.oglRenderPipe.copyArea(sunGraphics2D, n, n2, n3, n4, n5, n6);
            return true;
        }
        return false;
    }
    
    @Override
    public void flush() {
        this.invalidate();
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        instance.lock();
        try {
            OGLContext.setScratchSurface(this.graphicsConfig);
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
    
    static void dispose(final long n, final OGLGraphicsConfig scratchSurface) {
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        instance.lock();
        try {
            OGLContext.setScratchSurface(scratchSurface);
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
    
    static void swapBuffers(final long n) {
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        instance.lock();
        try {
            final RenderBuffer buffer = instance.getBuffer();
            instance.ensureCapacityAndAlignment(12, 4);
            buffer.putInt(80);
            buffer.putLong(n);
            instance.flushNow();
        }
        finally {
            instance.unlock();
        }
    }
    
    boolean isTexNonPow2Available() {
        return this.graphicsConfig.isCapPresent(32);
    }
    
    boolean isTexRectAvailable() {
        return this.graphicsConfig.isCapPresent(1048576);
    }
    
    @Override
    public Rectangle getNativeBounds() {
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        instance.lock();
        try {
            return new Rectangle(this.nativeWidth, this.nativeHeight);
        }
        finally {
            instance.unlock();
        }
    }
    
    boolean isOnScreen() {
        return this.getType() == 1;
    }
    
    static {
        OpenGLSurface = SurfaceType.Any.deriveSubType("OpenGL Surface", PixelConverter.ArgbPre.instance);
        OpenGLSurfaceRTT = OGLSurfaceData.OpenGLSurface.deriveSubType("OpenGL Surface (render-to-texture)");
        OpenGLTexture = SurfaceType.Any.deriveSubType("OpenGL Texture");
        if (!GraphicsEnvironment.isHeadless()) {
            OGLSurfaceData.isFBObjectEnabled = !"false".equals(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.opengl.fbobject")));
            OGLSurfaceData.isLCDShaderEnabled = !"false".equals(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.opengl.lcdshader")));
            OGLSurfaceData.isBIOpShaderEnabled = !"false".equals(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.opengl.biopshader")));
            OGLSurfaceData.isGradShaderEnabled = !"false".equals(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.opengl.gradshader")));
            final OGLRenderQueue instance = OGLRenderQueue.getInstance();
            OGLSurfaceData.oglImagePipe = new OGLDrawImage();
            OGLSurfaceData.oglTextPipe = new OGLTextRenderer(instance);
            OGLSurfaceData.oglRenderPipe = new OGLRenderer(instance);
            if (GraphicsPrimitive.tracingEnabled()) {
                OGLSurfaceData.oglTextPipe = OGLSurfaceData.oglTextPipe.traceWrap();
            }
            OGLSurfaceData.oglAAPgramPipe = OGLSurfaceData.oglRenderPipe.getAAParallelogramPipe();
            OGLSurfaceData.oglTxRenderPipe = new PixelToParallelogramConverter(OGLSurfaceData.oglRenderPipe, OGLSurfaceData.oglRenderPipe, 1.0, 0.25, true);
            OGLBlitLoops.register();
            OGLMaskFill.register();
            OGLMaskBlit.register();
        }
    }
}
