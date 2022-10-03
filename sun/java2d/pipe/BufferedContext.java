package sun.java2d.pipe;

import sun.java2d.loops.XORComposite;
import java.awt.AlphaComposite;
import java.awt.Color;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import java.lang.ref.WeakReference;
import java.awt.geom.AffineTransform;
import java.awt.Paint;
import java.awt.Composite;
import sun.java2d.pipe.hw.AccelSurface;
import java.lang.ref.Reference;

public abstract class BufferedContext
{
    public static final int NO_CONTEXT_FLAGS = 0;
    public static final int SRC_IS_OPAQUE = 1;
    public static final int USE_MASK = 2;
    protected RenderQueue rq;
    protected RenderBuffer buf;
    protected static BufferedContext currentContext;
    private Reference<AccelSurface> validSrcDataRef;
    private Reference<AccelSurface> validDstDataRef;
    private Reference<Region> validClipRef;
    private Reference<Composite> validCompRef;
    private Reference<Paint> validPaintRef;
    private boolean isValidatedPaintJustAColor;
    private int validatedRGB;
    private int validatedFlags;
    private boolean xformInUse;
    private AffineTransform transform;
    
    protected BufferedContext(final RenderQueue rq) {
        this.validSrcDataRef = new WeakReference<AccelSurface>(null);
        this.validDstDataRef = new WeakReference<AccelSurface>(null);
        this.validClipRef = new WeakReference<Region>(null);
        this.validCompRef = new WeakReference<Composite>(null);
        this.validPaintRef = new WeakReference<Paint>(null);
        this.rq = rq;
        this.buf = rq.getBuffer();
    }
    
    public static void validateContext(final AccelSurface accelSurface, final AccelSurface accelSurface2, final Region region, final Composite composite, final AffineTransform affineTransform, final Paint paint, final SunGraphics2D sunGraphics2D, final int n) {
        accelSurface2.getContext().validate(accelSurface, accelSurface2, region, composite, affineTransform, paint, sunGraphics2D, n);
    }
    
    public static void validateContext(final AccelSurface accelSurface) {
        validateContext(accelSurface, accelSurface, null, null, null, null, null, 0);
    }
    
    public void validate(final AccelSurface accelSurface, final AccelSurface accelSurface2, final Region clip, final Composite composite, final AffineTransform transform, final Paint paint, final SunGraphics2D sunGraphics2D, final int validatedFlags) {
        boolean b = false;
        boolean b2 = false;
        if (!accelSurface2.isValid() || accelSurface2.isSurfaceLost() || accelSurface.isSurfaceLost()) {
            this.invalidateContext();
            throw new InvalidPipeException("bounds changed or surface lost");
        }
        if (paint instanceof Color) {
            final int rgb = ((Color)paint).getRGB();
            if (this.isValidatedPaintJustAColor) {
                if (rgb != this.validatedRGB) {
                    this.validatedRGB = rgb;
                    b2 = true;
                }
            }
            else {
                this.validatedRGB = rgb;
                b2 = true;
                this.isValidatedPaintJustAColor = true;
            }
        }
        else if (this.validPaintRef.get() != paint) {
            b2 = true;
            this.isValidatedPaintJustAColor = false;
        }
        final AccelSurface accelSurface3 = this.validSrcDataRef.get();
        final AccelSurface accelSurface4 = this.validDstDataRef.get();
        if (BufferedContext.currentContext != this || accelSurface != accelSurface3 || accelSurface2 != accelSurface4) {
            if (accelSurface2 != accelSurface4) {
                b = true;
            }
            if (paint == null) {
                b2 = true;
            }
            this.setSurfaces(accelSurface, accelSurface2);
            BufferedContext.currentContext = this;
            this.validSrcDataRef = new WeakReference<AccelSurface>(accelSurface);
            this.validDstDataRef = new WeakReference<AccelSurface>(accelSurface2);
        }
        final Region region = this.validClipRef.get();
        if (clip != region || b) {
            if (clip != null) {
                if (b || region == null || !region.isRectangular() || !clip.isRectangular() || clip.getLoX() != region.getLoX() || clip.getLoY() != region.getLoY() || clip.getHiX() != region.getHiX() || clip.getHiY() != region.getHiY()) {
                    this.setClip(clip);
                }
            }
            else {
                this.resetClip();
            }
            this.validClipRef = new WeakReference<Region>(clip);
        }
        if (composite != this.validCompRef.get() || validatedFlags != this.validatedFlags) {
            if (composite != null) {
                this.setComposite(composite, validatedFlags);
            }
            else {
                this.resetComposite();
            }
            b2 = true;
            this.validCompRef = new WeakReference<Composite>(composite);
            this.validatedFlags = validatedFlags;
        }
        int n = 0;
        if (transform == null) {
            if (this.xformInUse) {
                this.resetTransform();
                this.xformInUse = false;
                n = 1;
            }
            else if (sunGraphics2D != null && !sunGraphics2D.transform.equals(this.transform)) {
                n = 1;
            }
            if (sunGraphics2D != null && n != 0) {
                this.transform = new AffineTransform(sunGraphics2D.transform);
            }
        }
        else {
            this.setTransform(transform);
            this.xformInUse = true;
            n = 1;
        }
        if (!this.isValidatedPaintJustAColor && n != 0) {
            b2 = true;
        }
        if (b2) {
            if (paint != null) {
                BufferedPaints.setPaint(this.rq, sunGraphics2D, paint, validatedFlags);
            }
            else {
                BufferedPaints.resetPaint(this.rq);
            }
            this.validPaintRef = new WeakReference<Paint>(paint);
        }
        accelSurface2.markDirty();
    }
    
    private void invalidateSurfaces() {
        this.validSrcDataRef.clear();
        this.validDstDataRef.clear();
    }
    
    private void setSurfaces(final AccelSurface accelSurface, final AccelSurface accelSurface2) {
        this.rq.ensureCapacityAndAlignment(20, 4);
        this.buf.putInt(70);
        this.buf.putLong(accelSurface.getNativeOps());
        this.buf.putLong(accelSurface2.getNativeOps());
    }
    
    private void resetClip() {
        this.rq.ensureCapacity(4);
        this.buf.putInt(55);
    }
    
    private void setClip(final Region region) {
        if (region.isRectangular()) {
            this.rq.ensureCapacity(20);
            this.buf.putInt(51);
            this.buf.putInt(region.getLoX()).putInt(region.getLoY());
            this.buf.putInt(region.getHiX()).putInt(region.getHiY());
        }
        else {
            this.rq.ensureCapacity(28);
            this.buf.putInt(52);
            this.buf.putInt(53);
            int n = this.buf.position();
            this.buf.putInt(0);
            int n2 = 0;
            int n3 = this.buf.remaining() / 16;
            final int[] array = new int[4];
            while (region.getSpanIterator().nextSpan(array)) {
                if (n3 == 0) {
                    this.buf.putInt(n, n2);
                    this.rq.flushNow();
                    this.buf.putInt(53);
                    n = this.buf.position();
                    this.buf.putInt(0);
                    n2 = 0;
                    n3 = this.buf.remaining() / 16;
                }
                this.buf.putInt(array[0]);
                this.buf.putInt(array[1]);
                this.buf.putInt(array[2]);
                this.buf.putInt(array[3]);
                ++n2;
                --n3;
            }
            this.buf.putInt(n, n2);
            this.rq.ensureCapacity(4);
            this.buf.putInt(54);
        }
    }
    
    private void resetComposite() {
        this.rq.ensureCapacity(4);
        this.buf.putInt(58);
    }
    
    private void setComposite(final Composite composite, final int n) {
        if (composite instanceof AlphaComposite) {
            final AlphaComposite alphaComposite = (AlphaComposite)composite;
            this.rq.ensureCapacity(16);
            this.buf.putInt(56);
            this.buf.putInt(alphaComposite.getRule());
            this.buf.putFloat(alphaComposite.getAlpha());
            this.buf.putInt(n);
        }
        else {
            if (!(composite instanceof XORComposite)) {
                throw new InternalError("not yet implemented");
            }
            final int xorPixel = ((XORComposite)composite).getXorPixel();
            this.rq.ensureCapacity(8);
            this.buf.putInt(57);
            this.buf.putInt(xorPixel);
        }
    }
    
    private void resetTransform() {
        this.rq.ensureCapacity(4);
        this.buf.putInt(60);
    }
    
    private void setTransform(final AffineTransform affineTransform) {
        this.rq.ensureCapacityAndAlignment(52, 4);
        this.buf.putInt(59);
        this.buf.putDouble(affineTransform.getScaleX());
        this.buf.putDouble(affineTransform.getShearY());
        this.buf.putDouble(affineTransform.getShearX());
        this.buf.putDouble(affineTransform.getScaleY());
        this.buf.putDouble(affineTransform.getTranslateX());
        this.buf.putDouble(affineTransform.getTranslateY());
    }
    
    public void invalidateContext() {
        this.resetTransform();
        this.resetComposite();
        this.resetClip();
        BufferedPaints.resetPaint(this.rq);
        this.invalidateSurfaces();
        this.validCompRef.clear();
        this.validClipRef.clear();
        this.validPaintRef.clear();
        this.isValidatedPaintJustAColor = false;
        this.xformInUse = false;
    }
    
    public abstract RenderQueue getRenderQueue();
    
    public abstract void saveState();
    
    public abstract void restoreState();
}
