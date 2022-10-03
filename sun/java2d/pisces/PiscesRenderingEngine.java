package sun.java2d.pisces;

import sun.java2d.pipe.AATileGenerator;
import sun.java2d.pipe.Region;
import java.awt.geom.PathIterator;
import java.util.Arrays;
import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import sun.awt.geom.PathConsumer2D;
import java.awt.geom.Path2D;
import java.awt.Shape;
import sun.java2d.pipe.RenderingEngine;

public class PiscesRenderingEngine extends RenderingEngine
{
    @Override
    public Shape createStrokedShape(final Shape shape, final float n, final int n2, final int n3, final float n4, final float[] array, final float n5) {
        final Path2D.Float float1 = new Path2D.Float();
        this.strokeTo(shape, null, n, NormMode.OFF, n2, n3, n4, array, n5, new PathConsumer2D() {
            @Override
            public void moveTo(final float n, final float n2) {
                float1.moveTo(n, n2);
            }
            
            @Override
            public void lineTo(final float n, final float n2) {
                float1.lineTo(n, n2);
            }
            
            @Override
            public void closePath() {
                float1.closePath();
            }
            
            @Override
            public void pathDone() {
            }
            
            @Override
            public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
                float1.curveTo(n, n2, n3, n4, n5, n6);
            }
            
            @Override
            public void quadTo(final float n, final float n2, final float n3, final float n4) {
                float1.quadTo(n, n2, n3, n4);
            }
            
            @Override
            public long getNativeConsumer() {
                throw new InternalError("Not using a native peer");
            }
        });
        return float1;
    }
    
    @Override
    public void strokeTo(final Shape shape, final AffineTransform affineTransform, final BasicStroke basicStroke, final boolean b, final boolean b2, final boolean b3, final PathConsumer2D pathConsumer2D) {
        this.strokeTo(shape, affineTransform, basicStroke, b, b2 ? (b3 ? NormMode.ON_WITH_AA : NormMode.ON_NO_AA) : NormMode.OFF, b3, pathConsumer2D);
    }
    
    void strokeTo(final Shape shape, final AffineTransform affineTransform, final BasicStroke basicStroke, final boolean b, final NormMode normMode, final boolean b2, final PathConsumer2D pathConsumer2D) {
        float n;
        if (b) {
            if (b2) {
                n = this.userSpaceLineWidth(affineTransform, 0.5f);
            }
            else {
                n = this.userSpaceLineWidth(affineTransform, 1.0f);
            }
        }
        else {
            n = basicStroke.getLineWidth();
        }
        this.strokeTo(shape, affineTransform, n, normMode, basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(), basicStroke.getDashArray(), basicStroke.getDashPhase(), pathConsumer2D);
    }
    
    private float userSpaceLineWidth(final AffineTransform affineTransform, final float n) {
        double n2;
        if ((affineTransform.getType() & 0x24) != 0x0) {
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
        return (float)(n / n2);
    }
    
    void strokeTo(final Shape shape, final AffineTransform affineTransform, float n, final NormMode normMode, final int n2, final int n3, final float n4, float[] copy, float n5, PathConsumer2D pathConsumer2D) {
        AffineTransform affineTransform2 = null;
        AffineTransform affineTransform3 = null;
        PathIterator pathIterator;
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
            if (nearZero(scaleX * shearX + shearY * scaleY, 2) && nearZero(scaleX * scaleX + shearY * shearY - (shearX * shearX + scaleY * scaleY), 2)) {
                final double sqrt = Math.sqrt(scaleX * scaleX + shearY * shearY);
                if (copy != null) {
                    copy = Arrays.copyOf(copy, copy.length);
                    for (int i = 0; i < copy.length; ++i) {
                        copy[i] *= (float)sqrt;
                    }
                    n5 *= (float)sqrt;
                }
                n *= (float)sqrt;
                pathIterator = shape.getPathIterator(affineTransform);
                if (normMode != NormMode.OFF) {
                    pathIterator = new NormalizingPathIterator(pathIterator, normMode);
                }
            }
            else if (normMode != NormMode.OFF) {
                affineTransform2 = affineTransform;
                pathIterator = new NormalizingPathIterator(shape.getPathIterator(affineTransform), normMode);
            }
            else {
                affineTransform3 = affineTransform;
                pathIterator = shape.getPathIterator(null);
            }
        }
        else {
            pathIterator = shape.getPathIterator(null);
            if (normMode != NormMode.OFF) {
                pathIterator = new NormalizingPathIterator(pathIterator, normMode);
            }
        }
        pathConsumer2D = TransformingPathConsumer2D.transformConsumer(pathConsumer2D, affineTransform3);
        pathConsumer2D = TransformingPathConsumer2D.deltaTransformConsumer(pathConsumer2D, affineTransform2);
        PathConsumer2D pathConsumer2D2 = new Stroker(pathConsumer2D, n, n2, n3, n4);
        if (copy != null) {
            pathConsumer2D2 = new Dasher(pathConsumer2D2, copy, n5);
        }
        pathConsumer2D = TransformingPathConsumer2D.inverseDeltaTransformConsumer(pathConsumer2D2, affineTransform2);
        pathTo(pathIterator, pathConsumer2D);
    }
    
    private static boolean nearZero(final double n, final int n2) {
        return Math.abs(n) < n2 * Math.ulp(n);
    }
    
    static void pathTo(final PathIterator pathIterator, final PathConsumer2D pathConsumer2D) {
        RenderingEngine.feedConsumer(pathIterator, pathConsumer2D);
        pathConsumer2D.pathDone();
    }
    
    @Override
    public AATileGenerator getAATileGenerator(final Shape shape, final AffineTransform affineTransform, final Region region, final BasicStroke basicStroke, final boolean b, final boolean b2, final int[] array) {
        final NormMode normMode = b2 ? NormMode.ON_WITH_AA : NormMode.OFF;
        Renderer renderer;
        if (basicStroke == null) {
            PathIterator pathIterator;
            if (b2) {
                pathIterator = new NormalizingPathIterator(shape.getPathIterator(affineTransform), normMode);
            }
            else {
                pathIterator = shape.getPathIterator(affineTransform);
            }
            renderer = new Renderer(3, 3, region.getLoX(), region.getLoY(), region.getWidth(), region.getHeight(), pathIterator.getWindingRule());
            pathTo(pathIterator, renderer);
        }
        else {
            renderer = new Renderer(3, 3, region.getLoX(), region.getLoY(), region.getWidth(), region.getHeight(), 1);
            this.strokeTo(shape, affineTransform, basicStroke, b, normMode, true, renderer);
        }
        renderer.endRendering();
        final PiscesTileGenerator piscesTileGenerator = new PiscesTileGenerator(renderer, renderer.MAX_AA_ALPHA);
        piscesTileGenerator.getBbox(array);
        return piscesTileGenerator;
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
        final Renderer renderer = new Renderer(3, 3, region.getLoX(), region.getLoY(), region.getWidth(), region.getHeight(), 0);
        renderer.moveTo((float)n, (float)n2);
        renderer.lineTo((float)(n + n3), (float)(n2 + n4));
        renderer.lineTo((float)(n + n3 + n5), (float)(n2 + n4 + n6));
        renderer.lineTo((float)(n + n5), (float)(n2 + n6));
        renderer.closePath();
        if (n9 != 0) {
            n += n10 + n12;
            n2 += n11 + n13;
            n3 -= 2.0 * n10;
            n4 -= 2.0 * n11;
            n5 -= 2.0 * n12;
            n6 -= 2.0 * n13;
            renderer.moveTo((float)n, (float)n2);
            renderer.lineTo((float)(n + n3), (float)(n2 + n4));
            renderer.lineTo((float)(n + n3 + n5), (float)(n2 + n4 + n6));
            renderer.lineTo((float)(n + n5), (float)(n2 + n6));
            renderer.closePath();
        }
        renderer.pathDone();
        renderer.endRendering();
        final PiscesTileGenerator piscesTileGenerator = new PiscesTileGenerator(renderer, renderer.MAX_AA_ALPHA);
        piscesTileGenerator.getBbox(array);
        return piscesTileGenerator;
    }
    
    @Override
    public float getMinimumAAPenSize() {
        return 0.5f;
    }
    
    private enum NormMode
    {
        OFF, 
        ON_NO_AA, 
        ON_WITH_AA;
    }
    
    private static class NormalizingPathIterator implements PathIterator
    {
        private final PathIterator src;
        private float curx_adjust;
        private float cury_adjust;
        private float movx_adjust;
        private float movy_adjust;
        private final float lval;
        private final float rval;
        
        NormalizingPathIterator(final PathIterator src, final NormMode normMode) {
            this.src = src;
            switch (normMode) {
                case ON_NO_AA: {
                    final float n = 0.25f;
                    this.rval = n;
                    this.lval = n;
                    break;
                }
                case ON_WITH_AA: {
                    this.lval = 0.0f;
                    this.rval = 0.5f;
                    break;
                }
                case OFF: {
                    throw new InternalError("A NormalizingPathIterator should not be created if no normalization is being done");
                }
                default: {
                    throw new InternalError("Unrecognized normalization mode");
                }
            }
        }
        
        @Override
        public int currentSegment(final float[] array) {
            final int currentSegment = this.src.currentSegment(array);
            int n = 0;
            switch (currentSegment) {
                case 3: {
                    n = 4;
                    break;
                }
                case 2: {
                    n = 2;
                    break;
                }
                case 0:
                case 1: {
                    n = 0;
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
            final float n2 = (float)Math.floor(array[n] + this.lval) + this.rval - array[n];
            final float n3 = (float)Math.floor(array[n + 1] + this.lval) + this.rval - array[n + 1];
            final int n4 = n;
            array[n4] += n2;
            final int n5 = n + 1;
            array[n5] += n3;
            switch (currentSegment) {
                case 3: {
                    final int n6 = 0;
                    array[n6] += this.curx_adjust;
                    final int n7 = 1;
                    array[n7] += this.cury_adjust;
                    final int n8 = 2;
                    array[n8] += n2;
                    final int n9 = 3;
                    array[n9] += n3;
                    break;
                }
                case 2: {
                    final int n10 = 0;
                    array[n10] += (this.curx_adjust + n2) / 2.0f;
                    final int n11 = 1;
                    array[n11] += (this.cury_adjust + n3) / 2.0f;
                }
                case 0: {
                    this.movx_adjust = n2;
                    this.movy_adjust = n3;
                    break;
                }
                case 4: {
                    throw new InternalError("This should be handled earlier.");
                }
            }
            this.curx_adjust = n2;
            this.cury_adjust = n3;
            return currentSegment;
        }
        
        @Override
        public int currentSegment(final double[] array) {
            final float[] array2 = new float[6];
            final int currentSegment = this.currentSegment(array2);
            for (int i = 0; i < 6; ++i) {
                array[i] = array2[i];
            }
            return currentSegment;
        }
        
        @Override
        public int getWindingRule() {
            return this.src.getWindingRule();
        }
        
        @Override
        public boolean isDone() {
            return this.src.isDone();
        }
        
        @Override
        public void next() {
            this.src.next();
        }
    }
}
