package sun.java2d.marlin;

import sun.java2d.ReentrantContextProviderCLQ;
import sun.java2d.ReentrantContext;
import sun.java2d.ReentrantContextProviderTL;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.Arrays;
import sun.java2d.pipe.AATileGenerator;
import sun.java2d.pipe.Region;
import java.awt.geom.PathIterator;
import sun.awt.geom.PathConsumer2D;
import java.awt.BasicStroke;
import java.awt.geom.Path2D;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import sun.java2d.ReentrantContextProvider;
import sun.java2d.pipe.RenderingEngine;

public final class MarlinRenderingEngine extends RenderingEngine implements MarlinConst
{
    static final boolean DISABLE_2ND_STROKER_CLIPPING = true;
    static final boolean DO_TRACE_PATH = false;
    static final boolean DO_CLIP;
    static final boolean DO_CLIP_FILL = true;
    static final boolean DO_CLIP_RUNTIME_ENABLE;
    private static final float MIN_PEN_SIZE;
    static final float UPPER_BND = 1.7014117E38f;
    static final float LOWER_BND = -1.7014117E38f;
    private static final boolean USE_THREAD_LOCAL;
    static final int REF_TYPE;
    private static final ReentrantContextProvider<RendererContext> RDR_CTX_PROVIDER;
    private static boolean SETTINGS_LOGGED;
    
    public MarlinRenderingEngine() {
        logSettings(MarlinRenderingEngine.class.getName());
    }
    
    @Override
    public Shape createStrokedShape(final Shape shape, final float n, final int n2, final int n3, final float n4, final float[] array, final float n5) {
        final RendererContext rendererContext = getRendererContext();
        try {
            final Path2D.Float path2D = rendererContext.getPath2D();
            this.strokeTo(rendererContext, shape, null, n, NormMode.OFF, n2, n3, n4, array, n5, rendererContext.transformerPC2D.wrapPath2D(path2D));
            return new Path2D.Float(path2D);
        }
        finally {
            returnRendererContext(rendererContext);
        }
    }
    
    @Override
    public void strokeTo(final Shape shape, final AffineTransform affineTransform, final BasicStroke basicStroke, final boolean b, final boolean b2, final boolean b3, final PathConsumer2D pathConsumer2D) {
        final NormMode normMode = b2 ? (b3 ? NormMode.ON_WITH_AA : NormMode.ON_NO_AA) : NormMode.OFF;
        final RendererContext rendererContext = getRendererContext();
        try {
            this.strokeTo(rendererContext, shape, affineTransform, basicStroke, b, normMode, b3, pathConsumer2D);
        }
        finally {
            returnRendererContext(rendererContext);
        }
    }
    
    void strokeTo(final RendererContext rendererContext, final Shape shape, final AffineTransform affineTransform, final BasicStroke basicStroke, final boolean b, final NormMode normMode, final boolean b2, final PathConsumer2D pathConsumer2D) {
        float n;
        if (b) {
            if (b2) {
                n = this.userSpaceLineWidth(affineTransform, MarlinRenderingEngine.MIN_PEN_SIZE);
            }
            else {
                n = this.userSpaceLineWidth(affineTransform, 1.0f);
            }
        }
        else {
            n = basicStroke.getLineWidth();
        }
        this.strokeTo(rendererContext, shape, affineTransform, n, normMode, basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(), basicStroke.getDashArray(), basicStroke.getDashPhase(), pathConsumer2D);
    }
    
    private final float userSpaceLineWidth(final AffineTransform affineTransform, final float n) {
        float n2;
        if (affineTransform == null) {
            n2 = 1.0f;
        }
        else if ((affineTransform.getType() & 0x24) != 0x0) {
            n2 = (float)Math.sqrt(affineTransform.getDeterminant());
        }
        else {
            final double scaleX = affineTransform.getScaleX();
            final double shearX = affineTransform.getShearX();
            final double shearY = affineTransform.getShearY();
            final double scaleY = affineTransform.getScaleY();
            final double n3 = scaleX * scaleX + shearY * shearY;
            final double n4 = 2.0 * (scaleX * shearX + shearY * scaleY);
            final double n5 = shearX * shearX + scaleY * scaleY;
            n2 = (float)Math.sqrt((n3 + n5 + Math.sqrt(n4 * n4 + (n3 - n5) * (n3 - n5))) / 2.0);
        }
        return n / n2;
    }
    
    void strokeTo(final RendererContext rendererContext, final Shape shape, AffineTransform affineTransform, float n, final NormMode normMode, final int n2, final int n3, final float n4, float[] copyDashArray, float n5, PathConsumer2D pathConsumer2D) {
        AffineTransform affineTransform2 = null;
        int n6 = -1;
        boolean b = false;
        if (affineTransform != null && !affineTransform.isIdentity()) {
            final double scaleX = affineTransform.getScaleX();
            final double shearX = affineTransform.getShearX();
            final double shearY = affineTransform.getShearY();
            final double scaleY = affineTransform.getScaleY();
            if (Math.abs(scaleX * scaleY - shearY * shearX) <= 2.802596928649634E-45) {
                pathConsumer2D.moveTo(0.0f, 0.0f);
                pathConsumer2D.pathDone();
                return;
            }
            if (nearZero(scaleX * shearX + shearY * scaleY) && nearZero(scaleX * scaleX + shearY * shearY - (shearX * shearX + scaleY * scaleY))) {
                final float n7 = (float)Math.sqrt(scaleX * scaleX + shearY * shearY);
                if (copyDashArray != null) {
                    b = true;
                    n6 = copyDashArray.length;
                    copyDashArray = rendererContext.dasher.copyDashArray(copyDashArray);
                    for (int i = 0; i < n6; ++i) {
                        final float[] array = copyDashArray;
                        final int n8 = i;
                        array[n8] *= n7;
                    }
                    n5 *= n7;
                }
                n *= n7;
            }
            else {
                affineTransform2 = affineTransform;
            }
        }
        else {
            affineTransform = null;
        }
        final TransformingPathConsumer2D transformerPC2D = rendererContext.transformerPC2D;
        if (MarlinRenderingEngine.USE_SIMPLIFIER) {
            pathConsumer2D = rendererContext.simplifier.init(pathConsumer2D);
        }
        pathConsumer2D = transformerPC2D.deltaTransformConsumer(pathConsumer2D, affineTransform2);
        pathConsumer2D = rendererContext.stroker.init(pathConsumer2D, n, n2, n3, n4, copyDashArray == null);
        rendererContext.monotonizer.init(n);
        if (copyDashArray != null) {
            if (!b) {
                n6 = copyDashArray.length;
            }
            pathConsumer2D = rendererContext.dasher.init(pathConsumer2D, copyDashArray, n6, n5, b);
            rendererContext.stroker.disableClipping();
        }
        else if (rendererContext.doClip && n2 != 0) {
            pathConsumer2D = transformerPC2D.detectClosedPath(pathConsumer2D);
        }
        pathConsumer2D = transformerPC2D.inverseDeltaTransformConsumer(pathConsumer2D, affineTransform2);
        pathTo(rendererContext, normMode.getNormalizingPathIterator(rendererContext, shape.getPathIterator(affineTransform)), pathConsumer2D);
    }
    
    private static boolean nearZero(final double n) {
        return Math.abs(n) < 2.0 * Math.ulp(n);
    }
    
    private static void pathTo(final RendererContext rendererContext, final PathIterator pathIterator, PathConsumer2D init) {
        if (MarlinRenderingEngine.USE_PATH_SIMPLIFIER) {
            init = rendererContext.pathSimplifier.init(init);
        }
        rendererContext.dirty = true;
        pathToLoop(rendererContext.float6, pathIterator, init);
        rendererContext.dirty = false;
    }
    
    private static void pathToLoop(final float[] array, final PathIterator pathIterator, final PathConsumer2D pathConsumer2D) {
        int n = 0;
        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(array)) {
                case 0: {
                    if (array[0] < 1.7014117E38f && array[0] > -1.7014117E38f && array[1] < 1.7014117E38f && array[1] > -1.7014117E38f) {
                        pathConsumer2D.moveTo(array[0], array[1]);
                        n = 1;
                        break;
                    }
                    break;
                }
                case 1: {
                    if (array[0] >= 1.7014117E38f || array[0] <= -1.7014117E38f || array[1] >= 1.7014117E38f || array[1] <= -1.7014117E38f) {
                        break;
                    }
                    if (n != 0) {
                        pathConsumer2D.lineTo(array[0], array[1]);
                        break;
                    }
                    pathConsumer2D.moveTo(array[0], array[1]);
                    n = 1;
                    break;
                }
                case 2: {
                    if (array[2] >= 1.7014117E38f || array[2] <= -1.7014117E38f || array[3] >= 1.7014117E38f || array[3] <= -1.7014117E38f) {
                        break;
                    }
                    if (n == 0) {
                        pathConsumer2D.moveTo(array[2], array[3]);
                        n = 1;
                        break;
                    }
                    if (array[0] < 1.7014117E38f && array[0] > -1.7014117E38f && array[1] < 1.7014117E38f && array[1] > -1.7014117E38f) {
                        pathConsumer2D.quadTo(array[0], array[1], array[2], array[3]);
                        break;
                    }
                    pathConsumer2D.lineTo(array[2], array[3]);
                    break;
                }
                case 3: {
                    if (array[4] >= 1.7014117E38f || array[4] <= -1.7014117E38f || array[5] >= 1.7014117E38f || array[5] <= -1.7014117E38f) {
                        break;
                    }
                    if (n == 0) {
                        pathConsumer2D.moveTo(array[4], array[5]);
                        n = 1;
                        break;
                    }
                    if (array[0] < 1.7014117E38f && array[0] > -1.7014117E38f && array[1] < 1.7014117E38f && array[1] > -1.7014117E38f && array[2] < 1.7014117E38f && array[2] > -1.7014117E38f && array[3] < 1.7014117E38f && array[3] > -1.7014117E38f) {
                        pathConsumer2D.curveTo(array[0], array[1], array[2], array[3], array[4], array[5]);
                        break;
                    }
                    pathConsumer2D.lineTo(array[4], array[5]);
                    break;
                }
                case 4: {
                    if (n != 0) {
                        pathConsumer2D.closePath();
                        break;
                    }
                    break;
                }
            }
            pathIterator.next();
        }
        pathConsumer2D.pathDone();
    }
    
    @Override
    public AATileGenerator getAATileGenerator(final Shape shape, final AffineTransform affineTransform, final Region region, final BasicStroke basicStroke, final boolean b, final boolean b2, final int[] array) {
        MarlinTileGenerator init = null;
        PathConsumer2D init2 = null;
        final RendererContext rendererContext = getRendererContext();
        try {
            if (MarlinRenderingEngine.DO_CLIP || (MarlinRenderingEngine.DO_CLIP_RUNTIME_ENABLE && MarlinProperties.isDoClipAtRuntime())) {
                final float[] clipRect = rendererContext.clipRect;
                final float rdr_OFFSET_X = Renderer.RDR_OFFSET_X;
                final float rdr_OFFSET_Y = Renderer.RDR_OFFSET_Y;
                clipRect[0] = region.getLoY() - 0.001f + rdr_OFFSET_Y;
                clipRect[1] = region.getLoY() + region.getHeight() + 0.001f + rdr_OFFSET_Y;
                clipRect[2] = region.getLoX() - 0.001f + rdr_OFFSET_X;
                clipRect[3] = region.getLoX() + region.getWidth() + 0.001f + rdr_OFFSET_X;
                if (MarlinConst.DO_LOG_CLIP) {
                    MarlinUtils.logInfo("clipRect (clip): " + Arrays.toString(rendererContext.clipRect));
                }
                rendererContext.doClip = true;
            }
            final AffineTransform affineTransform2 = (affineTransform != null && !affineTransform.isIdentity()) ? affineTransform : null;
            final NormMode normMode = b2 ? NormMode.ON_WITH_AA : NormMode.OFF;
            if (basicStroke == null) {
                final PathIterator normalizingPathIterator = normMode.getNormalizingPathIterator(rendererContext, shape.getPathIterator(affineTransform2));
                PathConsumer2D pathConsumer2D;
                init2 = (pathConsumer2D = rendererContext.renderer.init(region.getLoX(), region.getLoY(), region.getWidth(), region.getHeight(), normalizingPathIterator.getWindingRule()));
                if (rendererContext.doClip) {
                    pathConsumer2D = rendererContext.transformerPC2D.pathClipper(pathConsumer2D);
                }
                pathTo(rendererContext, normalizingPathIterator, pathConsumer2D);
            }
            else {
                init2 = rendererContext.renderer.init(region.getLoX(), region.getLoY(), region.getWidth(), region.getHeight(), 1);
                this.strokeTo(rendererContext, shape, affineTransform2, basicStroke, b, normMode, true, init2);
            }
            if (((Renderer)init2).endRendering()) {
                init = rendererContext.ptg.init();
                init.getBbox(array);
                init2 = null;
            }
        }
        finally {
            if (init2 != null) {
                ((Renderer)init2).dispose();
            }
        }
        return init;
    }
    
    @Override
    public AATileGenerator getAATileGenerator(double n, double n2, double n3, double n4, double n5, double n6, final double n7, final double n8, final Region region, final int[] array) {
        int n9 = (n7 > 0.0 && n8 > 0.0) ? 1 : 0;
        double n10;
        double n11;
        double n12;
        double n13;
        if (n9 != 0) {
            n10 = n3 * n7;
            n11 = n4 * n7;
            n12 = n5 * n8;
            n13 = n6 * n8;
            n -= (n10 + n12) / 2.0;
            n2 -= (n11 + n13) / 2.0;
            n3 += n10;
            n4 += n11;
            n5 += n12;
            n6 += n13;
            if (n7 > 1.0 && n8 > 1.0) {
                n9 = 0;
            }
        }
        else {
            n11 = (n10 = (n12 = (n13 = 0.0)));
        }
        MarlinTileGenerator init = null;
        Renderer init2 = null;
        final RendererContext rendererContext = getRendererContext();
        try {
            init2 = rendererContext.renderer.init(region.getLoX(), region.getLoY(), region.getWidth(), region.getHeight(), 0);
            init2.moveTo((float)n, (float)n2);
            init2.lineTo((float)(n + n3), (float)(n2 + n4));
            init2.lineTo((float)(n + n3 + n5), (float)(n2 + n4 + n6));
            init2.lineTo((float)(n + n5), (float)(n2 + n6));
            init2.closePath();
            if (n9 != 0) {
                n += n10 + n12;
                n2 += n11 + n13;
                n3 -= 2.0 * n10;
                n4 -= 2.0 * n11;
                n5 -= 2.0 * n12;
                n6 -= 2.0 * n13;
                init2.moveTo((float)n, (float)n2);
                init2.lineTo((float)(n + n3), (float)(n2 + n4));
                init2.lineTo((float)(n + n3 + n5), (float)(n2 + n4 + n6));
                init2.lineTo((float)(n + n5), (float)(n2 + n6));
                init2.closePath();
            }
            init2.pathDone();
            if (init2.endRendering()) {
                init = rendererContext.ptg.init();
                init.getBbox(array);
                init2 = null;
            }
        }
        finally {
            if (init2 != null) {
                init2.dispose();
            }
        }
        return init;
    }
    
    @Override
    public float getMinimumAAPenSize() {
        return MarlinRenderingEngine.MIN_PEN_SIZE;
    }
    
    private static void logSettings(final String s) {
        if (MarlinRenderingEngine.SETTINGS_LOGGED) {
            return;
        }
        MarlinRenderingEngine.SETTINGS_LOGGED = true;
        String s2 = null;
        switch (MarlinRenderingEngine.REF_TYPE) {
            default: {
                s2 = "hard";
                break;
            }
            case 1: {
                s2 = "soft";
                break;
            }
            case 2: {
                s2 = "weak";
                break;
            }
        }
        MarlinUtils.logInfo("===============================================================================");
        MarlinUtils.logInfo("Marlin software rasterizer           = ENABLED");
        MarlinUtils.logInfo("Version                              = [" + Version.getVersion() + "]");
        MarlinUtils.logInfo("sun.java2d.renderer                  = " + s);
        MarlinUtils.logInfo("sun.java2d.renderer.useThreadLocal   = " + MarlinRenderingEngine.USE_THREAD_LOCAL);
        MarlinUtils.logInfo("sun.java2d.renderer.useRef           = " + s2);
        MarlinUtils.logInfo("sun.java2d.renderer.edges            = " + MarlinConst.INITIAL_EDGES_COUNT);
        MarlinUtils.logInfo("sun.java2d.renderer.pixelWidth       = " + MarlinConst.INITIAL_PIXEL_WIDTH);
        MarlinUtils.logInfo("sun.java2d.renderer.pixelHeight      = " + MarlinConst.INITIAL_PIXEL_HEIGHT);
        MarlinUtils.logInfo("sun.java2d.renderer.subPixel_log2_X  = " + MarlinConst.SUBPIXEL_LG_POSITIONS_X);
        MarlinUtils.logInfo("sun.java2d.renderer.subPixel_log2_Y  = " + MarlinConst.SUBPIXEL_LG_POSITIONS_Y);
        MarlinUtils.logInfo("sun.java2d.renderer.tileSize_log2    = " + MarlinConst.TILE_H_LG);
        MarlinUtils.logInfo("sun.java2d.renderer.tileWidth_log2   = " + MarlinConst.TILE_W_LG);
        MarlinUtils.logInfo("sun.java2d.renderer.blockSize_log2   = " + MarlinConst.BLOCK_SIZE_LG);
        MarlinUtils.logInfo("sun.java2d.renderer.forceRLE         = " + MarlinProperties.isForceRLE());
        MarlinUtils.logInfo("sun.java2d.renderer.forceNoRLE       = " + MarlinProperties.isForceNoRLE());
        MarlinUtils.logInfo("sun.java2d.renderer.useTileFlags     = " + MarlinProperties.isUseTileFlags());
        MarlinUtils.logInfo("sun.java2d.renderer.useTileFlags.useHeuristics = " + MarlinProperties.isUseTileFlagsWithHeuristics());
        MarlinUtils.logInfo("sun.java2d.renderer.rleMinWidth      = " + MarlinCache.RLE_MIN_WIDTH);
        MarlinUtils.logInfo("sun.java2d.renderer.useSimplifier    = " + MarlinConst.USE_SIMPLIFIER);
        MarlinUtils.logInfo("sun.java2d.renderer.usePathSimplifier= " + MarlinConst.USE_PATH_SIMPLIFIER);
        MarlinUtils.logInfo("sun.java2d.renderer.pathSimplifier.pixTol = " + MarlinProperties.getPathSimplifierPixelTolerance());
        MarlinUtils.logInfo("sun.java2d.renderer.clip             = " + MarlinProperties.isDoClip());
        MarlinUtils.logInfo("sun.java2d.renderer.clip.runtime.enable = " + MarlinProperties.isDoClipRuntimeFlag());
        MarlinUtils.logInfo("sun.java2d.renderer.clip.subdivider  = " + MarlinProperties.isDoClipSubdivider());
        MarlinUtils.logInfo("sun.java2d.renderer.clip.subdivider.minLength = " + MarlinProperties.getSubdividerMinLength());
        MarlinUtils.logInfo("sun.java2d.renderer.doStats          = " + MarlinConst.DO_STATS);
        MarlinUtils.logInfo("sun.java2d.renderer.doMonitors       = false");
        MarlinUtils.logInfo("sun.java2d.renderer.doChecks         = " + MarlinConst.DO_CHECKS);
        MarlinUtils.logInfo("sun.java2d.renderer.useLogger        = " + MarlinConst.USE_LOGGER);
        MarlinUtils.logInfo("sun.java2d.renderer.logCreateContext = " + MarlinConst.LOG_CREATE_CONTEXT);
        MarlinUtils.logInfo("sun.java2d.renderer.logUnsafeMalloc  = " + MarlinConst.LOG_UNSAFE_MALLOC);
        MarlinUtils.logInfo("sun.java2d.renderer.curve_len_err    = " + MarlinProperties.getCurveLengthError());
        MarlinUtils.logInfo("sun.java2d.renderer.cubic_dec_d2     = " + MarlinProperties.getCubicDecD2());
        MarlinUtils.logInfo("sun.java2d.renderer.cubic_inc_d1     = " + MarlinProperties.getCubicIncD1());
        MarlinUtils.logInfo("sun.java2d.renderer.quad_dec_d2      = " + MarlinProperties.getQuadDecD2());
        MarlinUtils.logInfo("Renderer settings:");
        MarlinUtils.logInfo("CUB_DEC_BND  = " + Renderer.CUB_DEC_BND);
        MarlinUtils.logInfo("CUB_INC_BND  = " + Renderer.CUB_INC_BND);
        MarlinUtils.logInfo("QUAD_DEC_BND = " + Renderer.QUAD_DEC_BND);
        MarlinUtils.logInfo("INITIAL_EDGES_CAPACITY               = " + MarlinConst.INITIAL_EDGES_CAPACITY);
        MarlinUtils.logInfo("INITIAL_CROSSING_COUNT               = " + Renderer.INITIAL_CROSSING_COUNT);
        MarlinUtils.logInfo("===============================================================================");
    }
    
    static RendererContext getRendererContext() {
        return MarlinRenderingEngine.RDR_CTX_PROVIDER.acquire();
    }
    
    static void returnRendererContext(final RendererContext rendererContext) {
        rendererContext.dispose();
        MarlinRenderingEngine.RDR_CTX_PROVIDER.release(rendererContext);
    }
    
    static {
        DO_CLIP = MarlinProperties.isDoClip();
        DO_CLIP_RUNTIME_ENABLE = MarlinProperties.isDoClipRuntimeFlag();
        MIN_PEN_SIZE = 1.0f / MarlinRenderingEngine.MIN_SUBPIXELS;
        USE_THREAD_LOCAL = MarlinProperties.isUseThreadLocal();
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.renderer.useRef", "soft"));
        switch (s) {
            default: {
                REF_TYPE = 1;
                break;
            }
            case "weak": {
                REF_TYPE = 2;
                break;
            }
            case "hard": {
                REF_TYPE = 0;
                break;
            }
        }
        if (MarlinRenderingEngine.USE_THREAD_LOCAL) {
            RDR_CTX_PROVIDER = new ReentrantContextProviderTL<RendererContext>(MarlinRenderingEngine.REF_TYPE) {
                @Override
                protected RendererContext newContext() {
                    return RendererContext.createContext();
                }
            };
        }
        else {
            RDR_CTX_PROVIDER = new ReentrantContextProviderCLQ<RendererContext>(MarlinRenderingEngine.REF_TYPE) {
                @Override
                protected RendererContext newContext() {
                    return RendererContext.createContext();
                }
            };
        }
        MarlinRenderingEngine.SETTINGS_LOGGED = !MarlinRenderingEngine.ENABLE_LOGS;
    }
    
    private enum NormMode
    {
        ON_WITH_AA {
            @Override
            PathIterator getNormalizingPathIterator(final RendererContext rendererContext, final PathIterator pathIterator) {
                return rendererContext.nPCPathIterator.init(pathIterator);
            }
        }, 
        ON_NO_AA {
            @Override
            PathIterator getNormalizingPathIterator(final RendererContext rendererContext, final PathIterator pathIterator) {
                return rendererContext.nPQPathIterator.init(pathIterator);
            }
        }, 
        OFF {
            @Override
            PathIterator getNormalizingPathIterator(final RendererContext rendererContext, final PathIterator pathIterator) {
                return pathIterator;
            }
        };
        
        abstract PathIterator getNormalizingPathIterator(final RendererContext p0, final PathIterator p1);
    }
    
    abstract static class NormalizingPathIterator implements PathIterator
    {
        private PathIterator src;
        private float curx_adjust;
        private float cury_adjust;
        private float movx_adjust;
        private float movy_adjust;
        private final float[] tmp;
        
        NormalizingPathIterator(final float[] tmp) {
            this.tmp = tmp;
        }
        
        final NormalizingPathIterator init(final PathIterator src) {
            this.src = src;
            return this;
        }
        
        final void dispose() {
            this.src = null;
        }
        
        @Override
        public final int currentSegment(final float[] array) {
            final int currentSegment = this.src.currentSegment(array);
            int n = 0;
            switch (currentSegment) {
                case 0:
                case 1: {
                    n = 0;
                    break;
                }
                case 2: {
                    n = 2;
                    break;
                }
                case 3: {
                    n = 4;
                    break;
                }
                case 4: {
                    this.curx_adjust = this.movx_adjust;
                    this.cury_adjust = this.movy_adjust;
                    return currentSegment;
                }
                default: {
                    throw new InternalError("Unrecognized curve type");
                }
            }
            final float n2 = array[n];
            final float normCoord = this.normCoord(n2);
            array[n] = normCoord;
            final float n3 = normCoord - n2;
            final float n4 = array[n + 1];
            final float normCoord2 = this.normCoord(n4);
            array[n + 1] = normCoord2;
            final float n5 = normCoord2 - n4;
            switch (currentSegment) {
                case 0: {
                    this.movx_adjust = n3;
                    this.movy_adjust = n5;
                }
                case 2: {
                    final int n6 = 0;
                    array[n6] += (this.curx_adjust + n3) / 2.0f;
                    final int n7 = 1;
                    array[n7] += (this.cury_adjust + n5) / 2.0f;
                    break;
                }
                case 3: {
                    final int n8 = 0;
                    array[n8] += this.curx_adjust;
                    final int n9 = 1;
                    array[n9] += this.cury_adjust;
                    final int n10 = 2;
                    array[n10] += n3;
                    final int n11 = 3;
                    array[n11] += n5;
                    break;
                }
            }
            this.curx_adjust = n3;
            this.cury_adjust = n5;
            return currentSegment;
        }
        
        abstract float normCoord(final float p0);
        
        @Override
        public final int currentSegment(final double[] array) {
            final float[] tmp = this.tmp;
            final int currentSegment = this.currentSegment(tmp);
            for (int i = 0; i < 6; ++i) {
                array[i] = tmp[i];
            }
            return currentSegment;
        }
        
        @Override
        public final int getWindingRule() {
            return this.src.getWindingRule();
        }
        
        @Override
        public final boolean isDone() {
            if (this.src.isDone()) {
                this.dispose();
                return true;
            }
            return false;
        }
        
        @Override
        public final void next() {
            this.src.next();
        }
        
        static final class NearestPixelCenter extends NormalizingPathIterator
        {
            NearestPixelCenter(final float[] array) {
                super(array);
            }
            
            @Override
            float normCoord(final float n) {
                return FloatMath.floor_f(n) + 0.5f;
            }
        }
        
        static final class NearestPixelQuarter extends NormalizingPathIterator
        {
            NearestPixelQuarter(final float[] array) {
                super(array);
            }
            
            @Override
            float normCoord(final float n) {
                return FloatMath.floor_f(n + 0.25f) + 0.25f;
            }
        }
    }
}
