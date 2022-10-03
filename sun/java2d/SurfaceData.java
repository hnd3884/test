package sun.java2d;

import sun.java2d.pipe.DrawImage;
import sun.java2d.pipe.GeneralCompositePipe;
import sun.java2d.pipe.AlphaPaintPipe;
import sun.java2d.pipe.SpanClipRenderer;
import sun.java2d.pipe.TextRenderer;
import sun.java2d.pipe.LCDTextRenderer;
import sun.java2d.pipe.SolidTextRenderer;
import sun.font.FontUtilities;
import sun.java2d.pipe.AATextRenderer;
import sun.java2d.pipe.OutlineTextRenderer;
import java.awt.image.IndexColorModel;
import java.awt.AWTPermission;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.GraphicsConfiguration;
import sun.java2d.loops.DrawGlyphListAA;
import sun.java2d.loops.DrawGlyphList;
import sun.java2d.loops.DrawParallelogram;
import sun.java2d.loops.FillSpans;
import sun.java2d.loops.FillPath;
import sun.java2d.loops.DrawPath;
import sun.java2d.loops.DrawPolygons;
import sun.java2d.loops.DrawRect;
import sun.java2d.loops.FillRect;
import sun.java2d.loops.DrawLine;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.MaskFill;
import sun.java2d.pipe.LoopBasedPipe;
import sun.java2d.loops.FillParallelogram;
import sun.java2d.loops.DrawGlyphListLCD;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.ParallelogramPipe;
import sun.awt.image.SurfaceManager;
import java.awt.Color;
import sun.java2d.loops.CompositeType;
import java.awt.Image;
import java.security.Permission;
import sun.java2d.loops.RenderCache;
import sun.java2d.pipe.DrawImagePipe;
import sun.java2d.pipe.SpanShapeRenderer;
import sun.java2d.pipe.AAShapePipe;
import sun.java2d.pipe.CompositePipe;
import sun.java2d.pipe.PixelToParallelogramConverter;
import sun.java2d.pipe.PixelToShapeConverter;
import sun.java2d.pipe.AlphaColorPipe;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.LoopPipe;
import java.awt.image.ColorModel;
import sun.java2d.loops.SurfaceType;
import java.awt.Transparency;

public abstract class SurfaceData implements Transparency, DisposerTarget, StateTrackable, Surface
{
    private long pData;
    private boolean valid;
    private boolean surfaceLost;
    private SurfaceType surfaceType;
    private ColorModel colorModel;
    private Object disposerReferent;
    private Object blitProxyKey;
    private StateTrackableDelegate stateDelegate;
    protected static final LoopPipe colorPrimitives;
    public static final TextPipe outlineTextRenderer;
    public static final TextPipe solidTextRenderer;
    public static final TextPipe aaTextRenderer;
    public static final TextPipe lcdTextRenderer;
    protected static final AlphaColorPipe colorPipe;
    protected static final PixelToShapeConverter colorViaShape;
    protected static final PixelToParallelogramConverter colorViaPgram;
    protected static final TextPipe colorText;
    protected static final CompositePipe clipColorPipe;
    protected static final TextPipe clipColorText;
    protected static final AAShapePipe AAColorShape;
    protected static final PixelToParallelogramConverter AAColorViaShape;
    protected static final PixelToParallelogramConverter AAColorViaPgram;
    protected static final AAShapePipe AAClipColorShape;
    protected static final PixelToParallelogramConverter AAClipColorViaShape;
    protected static final CompositePipe paintPipe;
    protected static final SpanShapeRenderer paintShape;
    protected static final PixelToShapeConverter paintViaShape;
    protected static final TextPipe paintText;
    protected static final CompositePipe clipPaintPipe;
    protected static final TextPipe clipPaintText;
    protected static final AAShapePipe AAPaintShape;
    protected static final PixelToParallelogramConverter AAPaintViaShape;
    protected static final AAShapePipe AAClipPaintShape;
    protected static final PixelToParallelogramConverter AAClipPaintViaShape;
    protected static final CompositePipe compPipe;
    protected static final SpanShapeRenderer compShape;
    protected static final PixelToShapeConverter compViaShape;
    protected static final TextPipe compText;
    protected static final CompositePipe clipCompPipe;
    protected static final TextPipe clipCompText;
    protected static final AAShapePipe AACompShape;
    protected static final PixelToParallelogramConverter AACompViaShape;
    protected static final AAShapePipe AAClipCompShape;
    protected static final PixelToParallelogramConverter AAClipCompViaShape;
    protected static final DrawImagePipe imagepipe;
    static final int LOOP_UNKNOWN = 0;
    static final int LOOP_FOUND = 1;
    static final int LOOP_NOTFOUND = 2;
    int haveLCDLoop;
    int havePgramXORLoop;
    int havePgramSolidLoop;
    private static RenderCache loopcache;
    static Permission compPermission;
    
    private static native void initIDs();
    
    protected SurfaceData(final SurfaceType surfaceType, final ColorModel colorModel) {
        this(State.STABLE, surfaceType, colorModel);
    }
    
    protected SurfaceData(final State state, final SurfaceType surfaceType, final ColorModel colorModel) {
        this(StateTrackableDelegate.createInstance(state), surfaceType, colorModel);
    }
    
    protected SurfaceData(final StateTrackableDelegate stateDelegate, final SurfaceType surfaceType, final ColorModel colorModel) {
        this.disposerReferent = new Object();
        this.stateDelegate = stateDelegate;
        this.colorModel = colorModel;
        this.surfaceType = surfaceType;
        this.valid = true;
    }
    
    protected SurfaceData(final State state) {
        this.disposerReferent = new Object();
        this.stateDelegate = StateTrackableDelegate.createInstance(state);
        this.valid = true;
    }
    
    protected void setBlitProxyKey(final Object blitProxyKey) {
        if (SurfaceDataProxy.isCachingAllowed()) {
            this.blitProxyKey = blitProxyKey;
        }
    }
    
    public SurfaceData getSourceSurfaceData(final Image image, final int n, final CompositeType compositeType, final Color color) {
        final SurfaceManager manager = SurfaceManager.getManager(image);
        SurfaceData surfaceData = manager.getPrimarySurfaceData();
        if (image.getAccelerationPriority() > 0.0f && this.blitProxyKey != null) {
            SurfaceDataProxy surfaceDataProxy = (SurfaceDataProxy)manager.getCacheData(this.blitProxyKey);
            if (surfaceDataProxy == null || !surfaceDataProxy.isValid()) {
                if (surfaceData.getState() == State.UNTRACKABLE) {
                    surfaceDataProxy = SurfaceDataProxy.UNCACHED;
                }
                else {
                    surfaceDataProxy = this.makeProxyFor(surfaceData);
                }
                manager.setCacheData(this.blitProxyKey, surfaceDataProxy);
            }
            surfaceData = surfaceDataProxy.replaceData(surfaceData, n, compositeType, color);
        }
        return surfaceData;
    }
    
    public SurfaceDataProxy makeProxyFor(final SurfaceData surfaceData) {
        return SurfaceDataProxy.UNCACHED;
    }
    
    public static SurfaceData getPrimarySurfaceData(final Image image) {
        return SurfaceManager.getManager(image).getPrimarySurfaceData();
    }
    
    public static SurfaceData restoreContents(final Image image) {
        return SurfaceManager.getManager(image).restoreContents();
    }
    
    @Override
    public State getState() {
        return this.stateDelegate.getState();
    }
    
    @Override
    public StateTracker getStateTracker() {
        return this.stateDelegate.getStateTracker();
    }
    
    public final void markDirty() {
        this.stateDelegate.markDirty();
    }
    
    public void setSurfaceLost(final boolean surfaceLost) {
        this.surfaceLost = surfaceLost;
        this.stateDelegate.markDirty();
    }
    
    public boolean isSurfaceLost() {
        return this.surfaceLost;
    }
    
    public final boolean isValid() {
        return this.valid;
    }
    
    @Override
    public Object getDisposerReferent() {
        return this.disposerReferent;
    }
    
    public long getNativeOps() {
        return this.pData;
    }
    
    public void invalidate() {
        this.valid = false;
        this.stateDelegate.markDirty();
    }
    
    public abstract SurfaceData getReplacement();
    
    private static PixelToParallelogramConverter makeConverter(final AAShapePipe aaShapePipe, final ParallelogramPipe parallelogramPipe) {
        return new PixelToParallelogramConverter(aaShapePipe, parallelogramPipe, 0.125, 0.499, false);
    }
    
    private static PixelToParallelogramConverter makeConverter(final AAShapePipe aaShapePipe) {
        return makeConverter(aaShapePipe, aaShapePipe);
    }
    
    public boolean canRenderLCDText(final SunGraphics2D sunGraphics2D) {
        if (sunGraphics2D.compositeState <= 0 && sunGraphics2D.paintState <= 1 && sunGraphics2D.clipState <= 1 && sunGraphics2D.surfaceData.getTransparency() == 1) {
            if (this.haveLCDLoop == 0) {
                this.haveLCDLoop = ((DrawGlyphListLCD.locate(SurfaceType.AnyColor, CompositeType.SrcNoEa, this.getSurfaceType()) != null) ? 1 : 2);
            }
            return this.haveLCDLoop == 1;
        }
        return false;
    }
    
    public boolean canRenderParallelograms(final SunGraphics2D sunGraphics2D) {
        if (sunGraphics2D.paintState <= 1) {
            if (sunGraphics2D.compositeState == 2) {
                if (this.havePgramXORLoop == 0) {
                    this.havePgramXORLoop = ((FillParallelogram.locate(SurfaceType.AnyColor, CompositeType.Xor, this.getSurfaceType()) != null) ? 1 : 2);
                }
                return this.havePgramXORLoop == 1;
            }
            if (sunGraphics2D.compositeState <= 0 && sunGraphics2D.antialiasHint != 2 && sunGraphics2D.clipState != 2) {
                if (this.havePgramSolidLoop == 0) {
                    this.havePgramSolidLoop = ((FillParallelogram.locate(SurfaceType.AnyColor, CompositeType.SrcNoEa, this.getSurfaceType()) != null) ? 1 : 2);
                }
                return this.havePgramSolidLoop == 1;
            }
        }
        return false;
    }
    
    public void validatePipe(final SunGraphics2D sunGraphics2D) {
        sunGraphics2D.imagepipe = SurfaceData.imagepipe;
        if (sunGraphics2D.compositeState == 2) {
            if (sunGraphics2D.paintState > 1) {
                sunGraphics2D.drawpipe = SurfaceData.paintViaShape;
                sunGraphics2D.fillpipe = SurfaceData.paintViaShape;
                sunGraphics2D.shapepipe = SurfaceData.paintShape;
                sunGraphics2D.textpipe = SurfaceData.outlineTextRenderer;
            }
            else {
                PixelToShapeConverter drawpipe;
                if (this.canRenderParallelograms(sunGraphics2D)) {
                    drawpipe = SurfaceData.colorViaPgram;
                    sunGraphics2D.shapepipe = SurfaceData.colorViaPgram;
                }
                else {
                    drawpipe = SurfaceData.colorViaShape;
                    sunGraphics2D.shapepipe = SurfaceData.colorPrimitives;
                }
                if (sunGraphics2D.clipState == 2) {
                    sunGraphics2D.drawpipe = drawpipe;
                    sunGraphics2D.fillpipe = drawpipe;
                    sunGraphics2D.textpipe = SurfaceData.outlineTextRenderer;
                }
                else {
                    if (sunGraphics2D.transformState >= 3) {
                        sunGraphics2D.drawpipe = drawpipe;
                        sunGraphics2D.fillpipe = drawpipe;
                    }
                    else {
                        if (sunGraphics2D.strokeState != 0) {
                            sunGraphics2D.drawpipe = drawpipe;
                        }
                        else {
                            sunGraphics2D.drawpipe = SurfaceData.colorPrimitives;
                        }
                        sunGraphics2D.fillpipe = SurfaceData.colorPrimitives;
                    }
                    sunGraphics2D.textpipe = SurfaceData.solidTextRenderer;
                }
            }
        }
        else if (sunGraphics2D.compositeState == 3) {
            if (sunGraphics2D.antialiasHint == 2) {
                if (sunGraphics2D.clipState == 2) {
                    sunGraphics2D.drawpipe = SurfaceData.AAClipCompViaShape;
                    sunGraphics2D.fillpipe = SurfaceData.AAClipCompViaShape;
                    sunGraphics2D.shapepipe = SurfaceData.AAClipCompViaShape;
                    sunGraphics2D.textpipe = SurfaceData.clipCompText;
                }
                else {
                    sunGraphics2D.drawpipe = SurfaceData.AACompViaShape;
                    sunGraphics2D.fillpipe = SurfaceData.AACompViaShape;
                    sunGraphics2D.shapepipe = SurfaceData.AACompViaShape;
                    sunGraphics2D.textpipe = SurfaceData.compText;
                }
            }
            else {
                sunGraphics2D.drawpipe = SurfaceData.compViaShape;
                sunGraphics2D.fillpipe = SurfaceData.compViaShape;
                sunGraphics2D.shapepipe = SurfaceData.compShape;
                if (sunGraphics2D.clipState == 2) {
                    sunGraphics2D.textpipe = SurfaceData.clipCompText;
                }
                else {
                    sunGraphics2D.textpipe = SurfaceData.compText;
                }
            }
        }
        else if (sunGraphics2D.antialiasHint == 2) {
            sunGraphics2D.alphafill = this.getMaskFill(sunGraphics2D);
            if (sunGraphics2D.alphafill != null) {
                if (sunGraphics2D.clipState == 2) {
                    sunGraphics2D.drawpipe = SurfaceData.AAClipColorViaShape;
                    sunGraphics2D.fillpipe = SurfaceData.AAClipColorViaShape;
                    sunGraphics2D.shapepipe = SurfaceData.AAClipColorViaShape;
                    sunGraphics2D.textpipe = SurfaceData.clipColorText;
                }
                else {
                    final PixelToParallelogramConverter shapepipe = sunGraphics2D.alphafill.canDoParallelograms() ? SurfaceData.AAColorViaPgram : SurfaceData.AAColorViaShape;
                    sunGraphics2D.drawpipe = shapepipe;
                    sunGraphics2D.fillpipe = shapepipe;
                    sunGraphics2D.shapepipe = shapepipe;
                    if (sunGraphics2D.paintState > 1 || sunGraphics2D.compositeState > 0) {
                        sunGraphics2D.textpipe = SurfaceData.colorText;
                    }
                    else {
                        sunGraphics2D.textpipe = this.getTextPipe(sunGraphics2D, true);
                    }
                }
            }
            else if (sunGraphics2D.clipState == 2) {
                sunGraphics2D.drawpipe = SurfaceData.AAClipPaintViaShape;
                sunGraphics2D.fillpipe = SurfaceData.AAClipPaintViaShape;
                sunGraphics2D.shapepipe = SurfaceData.AAClipPaintViaShape;
                sunGraphics2D.textpipe = SurfaceData.clipPaintText;
            }
            else {
                sunGraphics2D.drawpipe = SurfaceData.AAPaintViaShape;
                sunGraphics2D.fillpipe = SurfaceData.AAPaintViaShape;
                sunGraphics2D.shapepipe = SurfaceData.AAPaintViaShape;
                sunGraphics2D.textpipe = SurfaceData.paintText;
            }
        }
        else if (sunGraphics2D.paintState > 1 || sunGraphics2D.compositeState > 0 || sunGraphics2D.clipState == 2) {
            sunGraphics2D.drawpipe = SurfaceData.paintViaShape;
            sunGraphics2D.fillpipe = SurfaceData.paintViaShape;
            sunGraphics2D.shapepipe = SurfaceData.paintShape;
            sunGraphics2D.alphafill = this.getMaskFill(sunGraphics2D);
            if (sunGraphics2D.alphafill != null) {
                if (sunGraphics2D.clipState == 2) {
                    sunGraphics2D.textpipe = SurfaceData.clipColorText;
                }
                else {
                    sunGraphics2D.textpipe = SurfaceData.colorText;
                }
            }
            else if (sunGraphics2D.clipState == 2) {
                sunGraphics2D.textpipe = SurfaceData.clipPaintText;
            }
            else {
                sunGraphics2D.textpipe = SurfaceData.paintText;
            }
        }
        else {
            PixelToShapeConverter drawpipe2;
            if (this.canRenderParallelograms(sunGraphics2D)) {
                drawpipe2 = SurfaceData.colorViaPgram;
                sunGraphics2D.shapepipe = SurfaceData.colorViaPgram;
            }
            else {
                drawpipe2 = SurfaceData.colorViaShape;
                sunGraphics2D.shapepipe = SurfaceData.colorPrimitives;
            }
            if (sunGraphics2D.transformState >= 3) {
                sunGraphics2D.drawpipe = drawpipe2;
                sunGraphics2D.fillpipe = drawpipe2;
            }
            else {
                if (sunGraphics2D.strokeState != 0) {
                    sunGraphics2D.drawpipe = drawpipe2;
                }
                else {
                    sunGraphics2D.drawpipe = SurfaceData.colorPrimitives;
                }
                sunGraphics2D.fillpipe = SurfaceData.colorPrimitives;
            }
            sunGraphics2D.textpipe = this.getTextPipe(sunGraphics2D, false);
        }
        if (sunGraphics2D.textpipe instanceof LoopBasedPipe || sunGraphics2D.shapepipe instanceof LoopBasedPipe || sunGraphics2D.fillpipe instanceof LoopBasedPipe || sunGraphics2D.drawpipe instanceof LoopBasedPipe || sunGraphics2D.imagepipe instanceof LoopBasedPipe) {
            sunGraphics2D.loops = this.getRenderLoops(sunGraphics2D);
        }
    }
    
    private TextPipe getTextPipe(final SunGraphics2D sunGraphics2D, final boolean b) {
        switch (sunGraphics2D.textAntialiasHint) {
            case 0: {
                if (b) {
                    return SurfaceData.aaTextRenderer;
                }
                return SurfaceData.solidTextRenderer;
            }
            case 1: {
                return SurfaceData.solidTextRenderer;
            }
            case 2: {
                return SurfaceData.aaTextRenderer;
            }
            default: {
                switch (sunGraphics2D.getFontInfo().aaHint) {
                    case 4:
                    case 6: {
                        return SurfaceData.lcdTextRenderer;
                    }
                    case 2: {
                        return SurfaceData.aaTextRenderer;
                    }
                    case 1: {
                        return SurfaceData.solidTextRenderer;
                    }
                    default: {
                        if (b) {
                            return SurfaceData.aaTextRenderer;
                        }
                        return SurfaceData.solidTextRenderer;
                    }
                }
                break;
            }
        }
    }
    
    private static SurfaceType getPaintSurfaceType(final SunGraphics2D sunGraphics2D) {
        switch (sunGraphics2D.paintState) {
            case 0: {
                return SurfaceType.OpaqueColor;
            }
            case 1: {
                return SurfaceType.AnyColor;
            }
            case 2: {
                if (sunGraphics2D.paint.getTransparency() == 1) {
                    return SurfaceType.OpaqueGradientPaint;
                }
                return SurfaceType.GradientPaint;
            }
            case 3: {
                if (sunGraphics2D.paint.getTransparency() == 1) {
                    return SurfaceType.OpaqueLinearGradientPaint;
                }
                return SurfaceType.LinearGradientPaint;
            }
            case 4: {
                if (sunGraphics2D.paint.getTransparency() == 1) {
                    return SurfaceType.OpaqueRadialGradientPaint;
                }
                return SurfaceType.RadialGradientPaint;
            }
            case 5: {
                if (sunGraphics2D.paint.getTransparency() == 1) {
                    return SurfaceType.OpaqueTexturePaint;
                }
                return SurfaceType.TexturePaint;
            }
            default: {
                return SurfaceType.AnyPaint;
            }
        }
    }
    
    private static CompositeType getFillCompositeType(final SunGraphics2D sunGraphics2D) {
        CompositeType compositeType = sunGraphics2D.imageComp;
        if (sunGraphics2D.compositeState == 0) {
            if (compositeType == CompositeType.SrcOverNoEa) {
                compositeType = CompositeType.OpaqueSrcOverNoEa;
            }
            else {
                compositeType = CompositeType.SrcNoEa;
            }
        }
        return compositeType;
    }
    
    protected MaskFill getMaskFill(final SunGraphics2D sunGraphics2D) {
        return MaskFill.getFromCache(getPaintSurfaceType(sunGraphics2D), getFillCompositeType(sunGraphics2D), this.getSurfaceType());
    }
    
    public RenderLoops getRenderLoops(final SunGraphics2D sunGraphics2D) {
        final SurfaceType paintSurfaceType = getPaintSurfaceType(sunGraphics2D);
        final CompositeType fillCompositeType = getFillCompositeType(sunGraphics2D);
        final SurfaceType surfaceType = sunGraphics2D.getSurfaceData().getSurfaceType();
        final Object value = SurfaceData.loopcache.get(paintSurfaceType, fillCompositeType, surfaceType);
        if (value != null) {
            return (RenderLoops)value;
        }
        final RenderLoops renderLoops = makeRenderLoops(paintSurfaceType, fillCompositeType, surfaceType);
        SurfaceData.loopcache.put(paintSurfaceType, fillCompositeType, surfaceType, renderLoops);
        return renderLoops;
    }
    
    public static RenderLoops makeRenderLoops(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        final RenderLoops renderLoops = new RenderLoops();
        renderLoops.drawLineLoop = DrawLine.locate(surfaceType, compositeType, surfaceType2);
        renderLoops.fillRectLoop = FillRect.locate(surfaceType, compositeType, surfaceType2);
        renderLoops.drawRectLoop = DrawRect.locate(surfaceType, compositeType, surfaceType2);
        renderLoops.drawPolygonsLoop = DrawPolygons.locate(surfaceType, compositeType, surfaceType2);
        renderLoops.drawPathLoop = DrawPath.locate(surfaceType, compositeType, surfaceType2);
        renderLoops.fillPathLoop = FillPath.locate(surfaceType, compositeType, surfaceType2);
        renderLoops.fillSpansLoop = FillSpans.locate(surfaceType, compositeType, surfaceType2);
        renderLoops.fillParallelogramLoop = FillParallelogram.locate(surfaceType, compositeType, surfaceType2);
        renderLoops.drawParallelogramLoop = DrawParallelogram.locate(surfaceType, compositeType, surfaceType2);
        renderLoops.drawGlyphListLoop = DrawGlyphList.locate(surfaceType, compositeType, surfaceType2);
        renderLoops.drawGlyphListAALoop = DrawGlyphListAA.locate(surfaceType, compositeType, surfaceType2);
        renderLoops.drawGlyphListLCDLoop = DrawGlyphListLCD.locate(surfaceType, compositeType, surfaceType2);
        return renderLoops;
    }
    
    public abstract GraphicsConfiguration getDeviceConfiguration();
    
    public final SurfaceType getSurfaceType() {
        return this.surfaceType;
    }
    
    public final ColorModel getColorModel() {
        return this.colorModel;
    }
    
    @Override
    public int getTransparency() {
        return this.getColorModel().getTransparency();
    }
    
    public abstract Raster getRaster(final int p0, final int p1, final int p2, final int p3);
    
    public boolean useTightBBoxes() {
        return true;
    }
    
    public int pixelFor(final int n) {
        return this.surfaceType.pixelFor(n, this.colorModel);
    }
    
    public int pixelFor(final Color color) {
        return this.pixelFor(color.getRGB());
    }
    
    public int rgbFor(final int n) {
        return this.surfaceType.rgbFor(n, this.colorModel);
    }
    
    public abstract Rectangle getBounds();
    
    protected void checkCustomComposite() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            if (SurfaceData.compPermission == null) {
                SurfaceData.compPermission = new AWTPermission("readDisplayPixels");
            }
            securityManager.checkPermission(SurfaceData.compPermission);
        }
    }
    
    protected static native boolean isOpaqueGray(final IndexColorModel p0);
    
    public static boolean isNull(final SurfaceData surfaceData) {
        return surfaceData == null || surfaceData == NullSurfaceData.theInstance;
    }
    
    public boolean copyArea(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        return false;
    }
    
    public void flush() {
    }
    
    public abstract Object getDestination();
    
    public int getDefaultScale() {
        return 1;
    }
    
    static {
        initIDs();
        colorPrimitives = new LoopPipe();
        outlineTextRenderer = new OutlineTextRenderer();
        aaTextRenderer = new AATextRenderer();
        if (FontUtilities.isMacOSX14) {
            solidTextRenderer = SurfaceData.aaTextRenderer;
        }
        else {
            solidTextRenderer = new SolidTextRenderer();
        }
        lcdTextRenderer = new LCDTextRenderer();
        colorPipe = new AlphaColorPipe();
        colorViaShape = new PixelToShapeLoopConverter(SurfaceData.colorPrimitives);
        colorViaPgram = new PixelToPgramLoopConverter(SurfaceData.colorPrimitives, SurfaceData.colorPrimitives, 1.0, 0.25, true);
        colorText = new TextRenderer(SurfaceData.colorPipe);
        clipColorPipe = new SpanClipRenderer(SurfaceData.colorPipe);
        clipColorText = new TextRenderer(SurfaceData.clipColorPipe);
        AAColorShape = new AAShapePipe(SurfaceData.colorPipe);
        AAColorViaShape = makeConverter(SurfaceData.AAColorShape);
        AAColorViaPgram = makeConverter(SurfaceData.AAColorShape, SurfaceData.colorPipe);
        AAClipColorShape = new AAShapePipe(SurfaceData.clipColorPipe);
        AAClipColorViaShape = makeConverter(SurfaceData.AAClipColorShape);
        paintPipe = new AlphaPaintPipe();
        paintShape = new SpanShapeRenderer.Composite(SurfaceData.paintPipe);
        paintViaShape = new PixelToShapeConverter(SurfaceData.paintShape);
        paintText = new TextRenderer(SurfaceData.paintPipe);
        clipPaintPipe = new SpanClipRenderer(SurfaceData.paintPipe);
        clipPaintText = new TextRenderer(SurfaceData.clipPaintPipe);
        AAPaintShape = new AAShapePipe(SurfaceData.paintPipe);
        AAPaintViaShape = makeConverter(SurfaceData.AAPaintShape);
        AAClipPaintShape = new AAShapePipe(SurfaceData.clipPaintPipe);
        AAClipPaintViaShape = makeConverter(SurfaceData.AAClipPaintShape);
        compPipe = new GeneralCompositePipe();
        compShape = new SpanShapeRenderer.Composite(SurfaceData.compPipe);
        compViaShape = new PixelToShapeConverter(SurfaceData.compShape);
        compText = new TextRenderer(SurfaceData.compPipe);
        clipCompPipe = new SpanClipRenderer(SurfaceData.compPipe);
        clipCompText = new TextRenderer(SurfaceData.clipCompPipe);
        AACompShape = new AAShapePipe(SurfaceData.compPipe);
        AACompViaShape = makeConverter(SurfaceData.AACompShape);
        AAClipCompShape = new AAShapePipe(SurfaceData.clipCompPipe);
        AAClipCompViaShape = makeConverter(SurfaceData.AAClipCompShape);
        imagepipe = new DrawImage();
        SurfaceData.loopcache = new RenderCache(30);
    }
    
    static class PixelToShapeLoopConverter extends PixelToShapeConverter implements LoopBasedPipe
    {
        public PixelToShapeLoopConverter(final ShapeDrawPipe shapeDrawPipe) {
            super(shapeDrawPipe);
        }
    }
    
    static class PixelToPgramLoopConverter extends PixelToParallelogramConverter implements LoopBasedPipe
    {
        public PixelToPgramLoopConverter(final ShapeDrawPipe shapeDrawPipe, final ParallelogramPipe parallelogramPipe, final double n, final double n2, final boolean b) {
            super(shapeDrawPipe, parallelogramPipe, n, n2, b);
        }
    }
}
