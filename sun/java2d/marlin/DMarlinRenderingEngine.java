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

public final class DMarlinRenderingEngine extends RenderingEngine implements MarlinConst
{
    static final boolean DISABLE_2ND_STROKER_CLIPPING = true;
    static final boolean DO_TRACE_PATH = false;
    static final boolean DO_CLIP;
    static final boolean DO_CLIP_FILL = true;
    static final boolean DO_CLIP_RUNTIME_ENABLE;
    private static final float MIN_PEN_SIZE;
    static final double UPPER_BND = 1.7014117331926443E38;
    static final double LOWER_BND = -1.7014117331926443E38;
    private static final boolean USE_THREAD_LOCAL;
    static final int REF_TYPE;
    private static final ReentrantContextProvider<DRendererContext> RDR_CTX_PROVIDER;
    private static boolean SETTINGS_LOGGED;
    
    public DMarlinRenderingEngine() {
        logSettings(DMarlinRenderingEngine.class.getName());
    }
    
    @Override
    public Shape createStrokedShape(final Shape shape, final float n, final int n2, final int n3, final float n4, final float[] array, final float n5) {
        final DRendererContext rendererContext = getRendererContext();
        try {
            final Path2D.Double path2D = rendererContext.getPath2D();
            this.strokeTo(rendererContext, shape, null, n, NormMode.OFF, n2, n3, n4, array, n5, rendererContext.transformerPC2D.wrapPath2D(path2D));
            return new Path2D.Double(path2D);
        }
        finally {
            returnRendererContext(rendererContext);
        }
    }
    
    @Override
    public void strokeTo(final Shape shape, final AffineTransform affineTransform, final BasicStroke basicStroke, final boolean b, final boolean b2, final boolean b3, final PathConsumer2D pathConsumer2D) {
        final NormMode normMode = b2 ? (b3 ? NormMode.ON_WITH_AA : NormMode.ON_NO_AA) : NormMode.OFF;
        final DRendererContext rendererContext = getRendererContext();
        try {
            this.strokeTo(rendererContext, shape, affineTransform, basicStroke, b, normMode, b3, rendererContext.p2dAdapter.init(pathConsumer2D));
        }
        finally {
            returnRendererContext(rendererContext);
        }
    }
    
    void strokeTo(final DRendererContext dRendererContext, final Shape shape, final AffineTransform affineTransform, final BasicStroke basicStroke, final boolean b, final NormMode normMode, final boolean b2, final DPathConsumer2D dPathConsumer2D) {
        double n;
        if (b) {
            if (b2) {
                n = this.userSpaceLineWidth(affineTransform, DMarlinRenderingEngine.MIN_PEN_SIZE);
            }
            else {
                n = this.userSpaceLineWidth(affineTransform, 1.0);
            }
        }
        else {
            n = basicStroke.getLineWidth();
        }
        this.strokeTo(dRendererContext, shape, affineTransform, n, normMode, basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(), basicStroke.getDashArray(), basicStroke.getDashPhase(), dPathConsumer2D);
    }
    
    private final double userSpaceLineWidth(final AffineTransform affineTransform, final double n) {
        double n2;
        if (affineTransform == null) {
            n2 = 1.0;
        }
        else if ((affineTransform.getType() & 0x24) != 0x0) {
            n2 = Math.sqrt(affineTransform.getDeterminant());
        }
        else {
            final double scaleX = affineTransform.getScaleX();
            final double shearX = affineTransform.getShearX();
            final double shearY = affineTransform.getShearY();
            final double scaleY = affineTransform.getScaleY();
            final double n3 = scaleX * scaleX + shearY * shearY;
            final double n4 = 2.0 * (scaleX * shearX + shearY * scaleY);
            final double n5 = shearX * shearX + scaleY * scaleY;
            n2 = Math.sqrt((n3 + n5 + Math.sqrt(n4 * n4 + (n3 - n5) * (n3 - n5))) / 2.0);
        }
        return n / n2;
    }
    
    void strokeTo(final DRendererContext dRendererContext, final Shape shape, AffineTransform affineTransform, double n, final NormMode normMode, final int n2, final int n3, final float n4, final float[] array, float n5, DPathConsumer2D dPathConsumer2D) {
        AffineTransform affineTransform2 = null;
        int length = -1;
        boolean b = false;
        double[] copyDashArray = null;
        if (array != null) {
            b = true;
            length = array.length;
            copyDashArray = dRendererContext.dasher.copyDashArray(array);
        }
        if (affineTransform != null && !affineTransform.isIdentity()) {
            final double scaleX = affineTransform.getScaleX();
            final double shearX = affineTransform.getShearX();
            final double shearY = affineTransform.getShearY();
            final double scaleY = affineTransform.getScaleY();
            if (Math.abs(scaleX * scaleY - shearY * shearX) <= 1.0E-323) {
                dPathConsumer2D.moveTo(0.0, 0.0);
                dPathConsumer2D.pathDone();
                return;
            }
            if (nearZero(scaleX * shearX + shearY * scaleY) && nearZero(scaleX * scaleX + shearY * shearY - (shearX * shearX + scaleY * scaleY))) {
                final double sqrt = Math.sqrt(scaleX * scaleX + shearY * shearY);
                if (copyDashArray != null) {
                    for (int i = 0; i < length; ++i) {
                        final double[] array2 = copyDashArray;
                        final int n6 = i;
                        array2[n6] *= sqrt;
                    }
                    n5 *= (float)sqrt;
                }
                n *= sqrt;
            }
            else {
                affineTransform2 = affineTransform;
            }
        }
        else {
            affineTransform = null;
        }
        final DTransformingPathConsumer2D transformerPC2D = dRendererContext.transformerPC2D;
        if (DMarlinRenderingEngine.USE_SIMPLIFIER) {
            dPathConsumer2D = dRendererContext.simplifier.init(dPathConsumer2D);
        }
        dPathConsumer2D = transformerPC2D.deltaTransformConsumer(dPathConsumer2D, affineTransform2);
        dPathConsumer2D = dRendererContext.stroker.init(dPathConsumer2D, n, n2, n3, n4, copyDashArray == null);
        dRendererContext.monotonizer.init(n);
        if (copyDashArray != null) {
            dPathConsumer2D = dRendererContext.dasher.init(dPathConsumer2D, copyDashArray, length, n5, b);
            dRendererContext.stroker.disableClipping();
        }
        else if (dRendererContext.doClip && n2 != 0) {
            dPathConsumer2D = transformerPC2D.detectClosedPath(dPathConsumer2D);
        }
        dPathConsumer2D = transformerPC2D.inverseDeltaTransformConsumer(dPathConsumer2D, affineTransform2);
        pathTo(dRendererContext, normMode.getNormalizingPathIterator(dRendererContext, shape.getPathIterator(affineTransform)), dPathConsumer2D);
    }
    
    private static boolean nearZero(final double n) {
        return Math.abs(n) < 2.0 * Math.ulp(n);
    }
    
    private static void pathTo(final DRendererContext dRendererContext, final PathIterator pathIterator, DPathConsumer2D init) {
        if (DMarlinRenderingEngine.USE_PATH_SIMPLIFIER) {
            init = dRendererContext.pathSimplifier.init(init);
        }
        dRendererContext.dirty = true;
        pathToLoop(dRendererContext.double6, pathIterator, init);
        dRendererContext.dirty = false;
    }
    
    private static void pathToLoop(final double[] array, final PathIterator pathIterator, final DPathConsumer2D dPathConsumer2D) {
        int n = 0;
        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(array)) {
                case 0: {
                    if (array[0] < 1.7014117331926443E38 && array[0] > -1.7014117331926443E38 && array[1] < 1.7014117331926443E38 && array[1] > -1.7014117331926443E38) {
                        dPathConsumer2D.moveTo(array[0], array[1]);
                        n = 1;
                        break;
                    }
                    break;
                }
                case 1: {
                    if (array[0] >= 1.7014117331926443E38 || array[0] <= -1.7014117331926443E38 || array[1] >= 1.7014117331926443E38 || array[1] <= -1.7014117331926443E38) {
                        break;
                    }
                    if (n != 0) {
                        dPathConsumer2D.lineTo(array[0], array[1]);
                        break;
                    }
                    dPathConsumer2D.moveTo(array[0], array[1]);
                    n = 1;
                    break;
                }
                case 2: {
                    if (array[2] >= 1.7014117331926443E38 || array[2] <= -1.7014117331926443E38 || array[3] >= 1.7014117331926443E38 || array[3] <= -1.7014117331926443E38) {
                        break;
                    }
                    if (n == 0) {
                        dPathConsumer2D.moveTo(array[2], array[3]);
                        n = 1;
                        break;
                    }
                    if (array[0] < 1.7014117331926443E38 && array[0] > -1.7014117331926443E38 && array[1] < 1.7014117331926443E38 && array[1] > -1.7014117331926443E38) {
                        dPathConsumer2D.quadTo(array[0], array[1], array[2], array[3]);
                        break;
                    }
                    dPathConsumer2D.lineTo(array[2], array[3]);
                    break;
                }
                case 3: {
                    if (array[4] >= 1.7014117331926443E38 || array[4] <= -1.7014117331926443E38 || array[5] >= 1.7014117331926443E38 || array[5] <= -1.7014117331926443E38) {
                        break;
                    }
                    if (n == 0) {
                        dPathConsumer2D.moveTo(array[4], array[5]);
                        n = 1;
                        break;
                    }
                    if (array[0] < 1.7014117331926443E38 && array[0] > -1.7014117331926443E38 && array[1] < 1.7014117331926443E38 && array[1] > -1.7014117331926443E38 && array[2] < 1.7014117331926443E38 && array[2] > -1.7014117331926443E38 && array[3] < 1.7014117331926443E38 && array[3] > -1.7014117331926443E38) {
                        dPathConsumer2D.curveTo(array[0], array[1], array[2], array[3], array[4], array[5]);
                        break;
                    }
                    dPathConsumer2D.lineTo(array[4], array[5]);
                    break;
                }
                case 4: {
                    if (n != 0) {
                        dPathConsumer2D.closePath();
                        break;
                    }
                    break;
                }
            }
            pathIterator.next();
        }
        dPathConsumer2D.pathDone();
    }
    
    @Override
    public AATileGenerator getAATileGenerator(final Shape shape, final AffineTransform affineTransform, final Region region, final BasicStroke basicStroke, final boolean b, final boolean b2, final int[] array) {
        MarlinTileGenerator init = null;
        DPathConsumer2D init2 = null;
        final DRendererContext rendererContext = getRendererContext();
        try {
            if (DMarlinRenderingEngine.DO_CLIP || (DMarlinRenderingEngine.DO_CLIP_RUNTIME_ENABLE && MarlinProperties.isDoClipAtRuntime())) {
                final double[] clipRect = rendererContext.clipRect;
                final double rdr_OFFSET_X = DRenderer.RDR_OFFSET_X;
                final double rdr_OFFSET_Y = DRenderer.RDR_OFFSET_Y;
                clipRect[0] = region.getLoY() - 0.001 + rdr_OFFSET_Y;
                clipRect[1] = region.getLoY() + region.getHeight() + 0.001 + rdr_OFFSET_Y;
                clipRect[2] = region.getLoX() - 0.001 + rdr_OFFSET_X;
                clipRect[3] = region.getLoX() + region.getWidth() + 0.001 + rdr_OFFSET_X;
                if (MarlinConst.DO_LOG_CLIP) {
                    MarlinUtils.logInfo("clipRect (clip): " + Arrays.toString(rendererContext.clipRect));
                }
                rendererContext.doClip = true;
            }
            final AffineTransform affineTransform2 = (affineTransform != null && !affineTransform.isIdentity()) ? affineTransform : null;
            final NormMode normMode = b2 ? NormMode.ON_WITH_AA : NormMode.OFF;
            if (basicStroke == null) {
                final PathIterator normalizingPathIterator = normMode.getNormalizingPathIterator(rendererContext, shape.getPathIterator(affineTransform2));
                DPathConsumer2D dPathConsumer2D;
                init2 = (dPathConsumer2D = rendererContext.renderer.init(region.getLoX(), region.getLoY(), region.getWidth(), region.getHeight(), normalizingPathIterator.getWindingRule()));
                if (rendererContext.doClip) {
                    dPathConsumer2D = rendererContext.transformerPC2D.pathClipper(dPathConsumer2D);
                }
                pathTo(rendererContext, normalizingPathIterator, dPathConsumer2D);
            }
            else {
                init2 = rendererContext.renderer.init(region.getLoX(), region.getLoY(), region.getWidth(), region.getHeight(), 1);
                this.strokeTo(rendererContext, shape, affineTransform2, basicStroke, b, normMode, true, init2);
            }
            if (((DRenderer)init2).endRendering()) {
                init = rendererContext.ptg.init();
                init.getBbox(array);
                init2 = null;
            }
        }
        finally {
            if (init2 != null) {
                ((DRenderer)init2).dispose();
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
        DRenderer init2 = null;
        final DRendererContext rendererContext = getRendererContext();
        try {
            init2 = rendererContext.renderer.init(region.getLoX(), region.getLoY(), region.getWidth(), region.getHeight(), 0);
            init2.moveTo(n, n2);
            init2.lineTo(n + n3, n2 + n4);
            init2.lineTo(n + n3 + n5, n2 + n4 + n6);
            init2.lineTo(n + n5, n2 + n6);
            init2.closePath();
            if (n9 != 0) {
                n += n10 + n12;
                n2 += n11 + n13;
                n3 -= 2.0 * n10;
                n4 -= 2.0 * n11;
                n5 -= 2.0 * n12;
                n6 -= 2.0 * n13;
                init2.moveTo(n, n2);
                init2.lineTo(n + n3, n2 + n4);
                init2.lineTo(n + n3 + n5, n2 + n4 + n6);
                init2.lineTo(n + n5, n2 + n6);
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
        return DMarlinRenderingEngine.MIN_PEN_SIZE;
    }
    
    private static void logSettings(final String s) {
        if (DMarlinRenderingEngine.SETTINGS_LOGGED) {
            return;
        }
        DMarlinRenderingEngine.SETTINGS_LOGGED = true;
        String s2 = null;
        switch (DMarlinRenderingEngine.REF_TYPE) {
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
        MarlinUtils.logInfo("sun.java2d.renderer.useThreadLocal   = " + DMarlinRenderingEngine.USE_THREAD_LOCAL);
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
        MarlinUtils.logInfo("CUB_DEC_BND  = " + DRenderer.CUB_DEC_BND);
        MarlinUtils.logInfo("CUB_INC_BND  = " + DRenderer.CUB_INC_BND);
        MarlinUtils.logInfo("QUAD_DEC_BND = " + DRenderer.QUAD_DEC_BND);
        MarlinUtils.logInfo("INITIAL_EDGES_CAPACITY               = " + MarlinConst.INITIAL_EDGES_CAPACITY);
        MarlinUtils.logInfo("INITIAL_CROSSING_COUNT               = " + DRenderer.INITIAL_CROSSING_COUNT);
        MarlinUtils.logInfo("===============================================================================");
    }
    
    static DRendererContext getRendererContext() {
        return DMarlinRenderingEngine.RDR_CTX_PROVIDER.acquire();
    }
    
    static void returnRendererContext(final DRendererContext dRendererContext) {
        dRendererContext.dispose();
        DMarlinRenderingEngine.RDR_CTX_PROVIDER.release(dRendererContext);
    }
    
    static {
        DO_CLIP = MarlinProperties.isDoClip();
        DO_CLIP_RUNTIME_ENABLE = MarlinProperties.isDoClipRuntimeFlag();
        MIN_PEN_SIZE = 1.0f / DMarlinRenderingEngine.MIN_SUBPIXELS;
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
        if (DMarlinRenderingEngine.USE_THREAD_LOCAL) {
            RDR_CTX_PROVIDER = new ReentrantContextProviderTL<DRendererContext>(DMarlinRenderingEngine.REF_TYPE) {
                @Override
                protected DRendererContext newContext() {
                    return DRendererContext.createContext();
                }
            };
        }
        else {
            RDR_CTX_PROVIDER = new ReentrantContextProviderCLQ<DRendererContext>(DMarlinRenderingEngine.REF_TYPE) {
                @Override
                protected DRendererContext newContext() {
                    return DRendererContext.createContext();
                }
            };
        }
        DMarlinRenderingEngine.SETTINGS_LOGGED = !DMarlinRenderingEngine.ENABLE_LOGS;
    }
    
    private enum NormMode
    {
        ON_WITH_AA {
            @Override
            PathIterator getNormalizingPathIterator(final DRendererContext dRendererContext, final PathIterator pathIterator) {
                return dRendererContext.nPCPathIterator.init(pathIterator);
            }
        }, 
        ON_NO_AA {
            @Override
            PathIterator getNormalizingPathIterator(final DRendererContext dRendererContext, final PathIterator pathIterator) {
                return dRendererContext.nPQPathIterator.init(pathIterator);
            }
        }, 
        OFF {
            @Override
            PathIterator getNormalizingPathIterator(final DRendererContext dRendererContext, final PathIterator pathIterator) {
                return pathIterator;
            }
        };
        
        abstract PathIterator getNormalizingPathIterator(final DRendererContext p0, final PathIterator p1);
    }
    
    abstract static class NormalizingPathIterator implements PathIterator
    {
        private PathIterator src;
        private double curx_adjust;
        private double cury_adjust;
        private double movx_adjust;
        private double movy_adjust;
        private final double[] tmp;
        
        NormalizingPathIterator(final double[] tmp) {
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
        public final int currentSegment(final double[] array) {
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
            final double n2 = array[n];
            final double normCoord = this.normCoord(n2);
            array[n] = normCoord;
            final double n3 = normCoord - n2;
            final double n4 = array[n + 1];
            final double normCoord2 = this.normCoord(n4);
            array[n + 1] = normCoord2;
            final double n5 = normCoord2 - n4;
            switch (currentSegment) {
                case 0: {
                    this.movx_adjust = n3;
                    this.movy_adjust = n5;
                }
                case 2: {
                    final int n6 = 0;
                    array[n6] += (this.curx_adjust + n3) / 2.0;
                    final int n7 = 1;
                    array[n7] += (this.cury_adjust + n5) / 2.0;
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
        
        abstract double normCoord(final double p0);
        
        @Override
        public final int currentSegment(final float[] array) {
            final double[] tmp = this.tmp;
            final int currentSegment = this.currentSegment(tmp);
            for (int i = 0; i < 6; ++i) {
                array[i] = (float)tmp[i];
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
            NearestPixelCenter(final double[] array) {
                super(array);
            }
            
            @Override
            double normCoord(final double n) {
                return Math.floor(n) + 0.5;
            }
        }
        
        static final class NearestPixelQuarter extends NormalizingPathIterator
        {
            NearestPixelQuarter(final double[] array) {
                super(array);
            }
            
            @Override
            double normCoord(final double n) {
                return Math.floor(n + 0.25) + 0.25;
            }
        }
    }
}
