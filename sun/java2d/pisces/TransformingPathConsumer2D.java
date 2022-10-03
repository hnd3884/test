package sun.java2d.pisces;

import java.awt.geom.AffineTransform;
import sun.awt.geom.PathConsumer2D;

final class TransformingPathConsumer2D
{
    public static PathConsumer2D transformConsumer(final PathConsumer2D pathConsumer2D, final AffineTransform affineTransform) {
        if (affineTransform == null) {
            return pathConsumer2D;
        }
        final float n = (float)affineTransform.getScaleX();
        final float n2 = (float)affineTransform.getShearX();
        final float n3 = (float)affineTransform.getTranslateX();
        final float n4 = (float)affineTransform.getShearY();
        final float n5 = (float)affineTransform.getScaleY();
        final float n6 = (float)affineTransform.getTranslateY();
        if (n2 == 0.0f && n4 == 0.0f) {
            if (n == 1.0f && n5 == 1.0f) {
                if (n3 == 0.0f && n6 == 0.0f) {
                    return pathConsumer2D;
                }
                return new TranslateFilter(pathConsumer2D, n3, n6);
            }
            else {
                if (n3 == 0.0f && n6 == 0.0f) {
                    return new DeltaScaleFilter(pathConsumer2D, n, n5);
                }
                return new ScaleFilter(pathConsumer2D, n, n5, n3, n6);
            }
        }
        else {
            if (n3 == 0.0f && n6 == 0.0f) {
                return new DeltaTransformFilter(pathConsumer2D, n, n2, n4, n5);
            }
            return new TransformFilter(pathConsumer2D, n, n2, n3, n4, n5, n6);
        }
    }
    
    public static PathConsumer2D deltaTransformConsumer(final PathConsumer2D pathConsumer2D, final AffineTransform affineTransform) {
        if (affineTransform == null) {
            return pathConsumer2D;
        }
        final float n = (float)affineTransform.getScaleX();
        final float n2 = (float)affineTransform.getShearX();
        final float n3 = (float)affineTransform.getShearY();
        final float n4 = (float)affineTransform.getScaleY();
        if (n2 != 0.0f || n3 != 0.0f) {
            return new DeltaTransformFilter(pathConsumer2D, n, n2, n3, n4);
        }
        if (n == 1.0f && n4 == 1.0f) {
            return pathConsumer2D;
        }
        return new DeltaScaleFilter(pathConsumer2D, n, n4);
    }
    
    public static PathConsumer2D inverseDeltaTransformConsumer(final PathConsumer2D pathConsumer2D, final AffineTransform affineTransform) {
        if (affineTransform == null) {
            return pathConsumer2D;
        }
        final float n = (float)affineTransform.getScaleX();
        final float n2 = (float)affineTransform.getShearX();
        final float n3 = (float)affineTransform.getShearY();
        final float n4 = (float)affineTransform.getScaleY();
        if (n2 != 0.0f || n3 != 0.0f) {
            final float n5 = n * n4 - n2 * n3;
            return new DeltaTransformFilter(pathConsumer2D, n4 / n5, -n2 / n5, -n3 / n5, n / n5);
        }
        if (n == 1.0f && n4 == 1.0f) {
            return pathConsumer2D;
        }
        return new DeltaScaleFilter(pathConsumer2D, 1.0f / n, 1.0f / n4);
    }
    
    static final class TranslateFilter implements PathConsumer2D
    {
        private final PathConsumer2D out;
        private final float tx;
        private final float ty;
        
        TranslateFilter(final PathConsumer2D out, final float tx, final float ty) {
            this.out = out;
            this.tx = tx;
            this.ty = ty;
        }
        
        @Override
        public void moveTo(final float n, final float n2) {
            this.out.moveTo(n + this.tx, n2 + this.ty);
        }
        
        @Override
        public void lineTo(final float n, final float n2) {
            this.out.lineTo(n + this.tx, n2 + this.ty);
        }
        
        @Override
        public void quadTo(final float n, final float n2, final float n3, final float n4) {
            this.out.quadTo(n + this.tx, n2 + this.ty, n3 + this.tx, n4 + this.ty);
        }
        
        @Override
        public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.out.curveTo(n + this.tx, n2 + this.ty, n3 + this.tx, n4 + this.ty, n5 + this.tx, n6 + this.ty);
        }
        
        @Override
        public void closePath() {
            this.out.closePath();
        }
        
        @Override
        public void pathDone() {
            this.out.pathDone();
        }
        
        @Override
        public long getNativeConsumer() {
            return 0L;
        }
    }
    
    static final class ScaleFilter implements PathConsumer2D
    {
        private final PathConsumer2D out;
        private final float sx;
        private final float sy;
        private final float tx;
        private final float ty;
        
        ScaleFilter(final PathConsumer2D out, final float sx, final float sy, final float tx, final float ty) {
            this.out = out;
            this.sx = sx;
            this.sy = sy;
            this.tx = tx;
            this.ty = ty;
        }
        
        @Override
        public void moveTo(final float n, final float n2) {
            this.out.moveTo(n * this.sx + this.tx, n2 * this.sy + this.ty);
        }
        
        @Override
        public void lineTo(final float n, final float n2) {
            this.out.lineTo(n * this.sx + this.tx, n2 * this.sy + this.ty);
        }
        
        @Override
        public void quadTo(final float n, final float n2, final float n3, final float n4) {
            this.out.quadTo(n * this.sx + this.tx, n2 * this.sy + this.ty, n3 * this.sx + this.tx, n4 * this.sy + this.ty);
        }
        
        @Override
        public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.out.curveTo(n * this.sx + this.tx, n2 * this.sy + this.ty, n3 * this.sx + this.tx, n4 * this.sy + this.ty, n5 * this.sx + this.tx, n6 * this.sy + this.ty);
        }
        
        @Override
        public void closePath() {
            this.out.closePath();
        }
        
        @Override
        public void pathDone() {
            this.out.pathDone();
        }
        
        @Override
        public long getNativeConsumer() {
            return 0L;
        }
    }
    
    static final class TransformFilter implements PathConsumer2D
    {
        private final PathConsumer2D out;
        private final float Mxx;
        private final float Mxy;
        private final float Mxt;
        private final float Myx;
        private final float Myy;
        private final float Myt;
        
        TransformFilter(final PathConsumer2D out, final float mxx, final float mxy, final float mxt, final float myx, final float myy, final float myt) {
            this.out = out;
            this.Mxx = mxx;
            this.Mxy = mxy;
            this.Mxt = mxt;
            this.Myx = myx;
            this.Myy = myy;
            this.Myt = myt;
        }
        
        @Override
        public void moveTo(final float n, final float n2) {
            this.out.moveTo(n * this.Mxx + n2 * this.Mxy + this.Mxt, n * this.Myx + n2 * this.Myy + this.Myt);
        }
        
        @Override
        public void lineTo(final float n, final float n2) {
            this.out.lineTo(n * this.Mxx + n2 * this.Mxy + this.Mxt, n * this.Myx + n2 * this.Myy + this.Myt);
        }
        
        @Override
        public void quadTo(final float n, final float n2, final float n3, final float n4) {
            this.out.quadTo(n * this.Mxx + n2 * this.Mxy + this.Mxt, n * this.Myx + n2 * this.Myy + this.Myt, n3 * this.Mxx + n4 * this.Mxy + this.Mxt, n3 * this.Myx + n4 * this.Myy + this.Myt);
        }
        
        @Override
        public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.out.curveTo(n * this.Mxx + n2 * this.Mxy + this.Mxt, n * this.Myx + n2 * this.Myy + this.Myt, n3 * this.Mxx + n4 * this.Mxy + this.Mxt, n3 * this.Myx + n4 * this.Myy + this.Myt, n5 * this.Mxx + n6 * this.Mxy + this.Mxt, n5 * this.Myx + n6 * this.Myy + this.Myt);
        }
        
        @Override
        public void closePath() {
            this.out.closePath();
        }
        
        @Override
        public void pathDone() {
            this.out.pathDone();
        }
        
        @Override
        public long getNativeConsumer() {
            return 0L;
        }
    }
    
    static final class DeltaScaleFilter implements PathConsumer2D
    {
        private final float sx;
        private final float sy;
        private final PathConsumer2D out;
        
        public DeltaScaleFilter(final PathConsumer2D out, final float sx, final float sy) {
            this.sx = sx;
            this.sy = sy;
            this.out = out;
        }
        
        @Override
        public void moveTo(final float n, final float n2) {
            this.out.moveTo(n * this.sx, n2 * this.sy);
        }
        
        @Override
        public void lineTo(final float n, final float n2) {
            this.out.lineTo(n * this.sx, n2 * this.sy);
        }
        
        @Override
        public void quadTo(final float n, final float n2, final float n3, final float n4) {
            this.out.quadTo(n * this.sx, n2 * this.sy, n3 * this.sx, n4 * this.sy);
        }
        
        @Override
        public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.out.curveTo(n * this.sx, n2 * this.sy, n3 * this.sx, n4 * this.sy, n5 * this.sx, n6 * this.sy);
        }
        
        @Override
        public void closePath() {
            this.out.closePath();
        }
        
        @Override
        public void pathDone() {
            this.out.pathDone();
        }
        
        @Override
        public long getNativeConsumer() {
            return 0L;
        }
    }
    
    static final class DeltaTransformFilter implements PathConsumer2D
    {
        private PathConsumer2D out;
        private final float Mxx;
        private final float Mxy;
        private final float Myx;
        private final float Myy;
        
        DeltaTransformFilter(final PathConsumer2D out, final float mxx, final float mxy, final float myx, final float myy) {
            this.out = out;
            this.Mxx = mxx;
            this.Mxy = mxy;
            this.Myx = myx;
            this.Myy = myy;
        }
        
        @Override
        public void moveTo(final float n, final float n2) {
            this.out.moveTo(n * this.Mxx + n2 * this.Mxy, n * this.Myx + n2 * this.Myy);
        }
        
        @Override
        public void lineTo(final float n, final float n2) {
            this.out.lineTo(n * this.Mxx + n2 * this.Mxy, n * this.Myx + n2 * this.Myy);
        }
        
        @Override
        public void quadTo(final float n, final float n2, final float n3, final float n4) {
            this.out.quadTo(n * this.Mxx + n2 * this.Mxy, n * this.Myx + n2 * this.Myy, n3 * this.Mxx + n4 * this.Mxy, n3 * this.Myx + n4 * this.Myy);
        }
        
        @Override
        public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.out.curveTo(n * this.Mxx + n2 * this.Mxy, n * this.Myx + n2 * this.Myy, n3 * this.Mxx + n4 * this.Mxy, n3 * this.Myx + n4 * this.Myy, n5 * this.Mxx + n6 * this.Mxy, n5 * this.Myx + n6 * this.Myy);
        }
        
        @Override
        public void closePath() {
            this.out.closePath();
        }
        
        @Override
        public void pathDone() {
            this.out.pathDone();
        }
        
        @Override
        public long getNativeConsumer() {
            return 0L;
        }
    }
}
