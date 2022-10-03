package sun.java2d.pipe;

import java.awt.geom.PathIterator;
import sun.awt.geom.PathConsumer2D;
import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import sun.security.action.GetPropertyAction;
import java.security.AccessController;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.security.PrivilegedAction;

public abstract class RenderingEngine
{
    private static RenderingEngine reImpl;
    
    public static synchronized RenderingEngine getInstance() {
        if (RenderingEngine.reImpl != null) {
            return RenderingEngine.reImpl;
        }
        RenderingEngine.reImpl = AccessController.doPrivileged((PrivilegedAction<RenderingEngine>)new PrivilegedAction<RenderingEngine>() {
            @Override
            public RenderingEngine run() {
                final String property = System.getProperty("sun.java2d.renderer");
                Label_0037: {
                    if (property != null) {
                        if (!property.equals("sun.dc.DuctusRenderingEngine")) {
                            break Label_0037;
                        }
                    }
                    try {
                        return (RenderingEngine)Class.forName("sun.dc.DuctusRenderingEngine").newInstance();
                    }
                    catch (final ReflectiveOperationException ex) {}
                }
                final ServiceLoader<RenderingEngine> loadInstalled = ServiceLoader.loadInstalled(RenderingEngine.class);
                RenderingEngine renderingEngine = null;
                RenderingEngine renderingEngine2 = null;
                final Iterator<RenderingEngine> iterator = loadInstalled.iterator();
                while (iterator.hasNext()) {
                    final String name = (renderingEngine = iterator.next()).getClass().getName();
                    if (name.equals(property)) {
                        return renderingEngine;
                    }
                    if (!name.equals("sun.java2d.marlin.DMarlinRenderingEngine")) {
                        continue;
                    }
                    renderingEngine2 = renderingEngine;
                }
                if (renderingEngine2 != null) {
                    return renderingEngine2;
                }
                return renderingEngine;
            }
        });
        if (RenderingEngine.reImpl == null) {
            throw new InternalError("No RenderingEngine module found");
        }
        if (AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.renderer.trace")) != null) {
            RenderingEngine.reImpl = new Tracer(RenderingEngine.reImpl);
        }
        return RenderingEngine.reImpl;
    }
    
    public abstract Shape createStrokedShape(final Shape p0, final float p1, final int p2, final int p3, final float p4, final float[] p5, final float p6);
    
    public abstract void strokeTo(final Shape p0, final AffineTransform p1, final BasicStroke p2, final boolean p3, final boolean p4, final boolean p5, final PathConsumer2D p6);
    
    public abstract AATileGenerator getAATileGenerator(final Shape p0, final AffineTransform p1, final Region p2, final BasicStroke p3, final boolean p4, final boolean p5, final int[] p6);
    
    public abstract AATileGenerator getAATileGenerator(final double p0, final double p1, final double p2, final double p3, final double p4, final double p5, final double p6, final double p7, final Region p8, final int[] p9);
    
    public abstract float getMinimumAAPenSize();
    
    public static void feedConsumer(final PathIterator pathIterator, final PathConsumer2D pathConsumer2D) {
        final float[] array = new float[6];
        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(array)) {
                case 0: {
                    pathConsumer2D.moveTo(array[0], array[1]);
                    break;
                }
                case 1: {
                    pathConsumer2D.lineTo(array[0], array[1]);
                    break;
                }
                case 2: {
                    pathConsumer2D.quadTo(array[0], array[1], array[2], array[3]);
                    break;
                }
                case 3: {
                    pathConsumer2D.curveTo(array[0], array[1], array[2], array[3], array[4], array[5]);
                    break;
                }
                case 4: {
                    pathConsumer2D.closePath();
                    break;
                }
            }
            pathIterator.next();
        }
    }
    
    static class Tracer extends RenderingEngine
    {
        RenderingEngine target;
        String name;
        
        public Tracer(final RenderingEngine target) {
            this.target = target;
            this.name = target.getClass().getName();
        }
        
        @Override
        public Shape createStrokedShape(final Shape shape, final float n, final int n2, final int n3, final float n4, final float[] array, final float n5) {
            System.out.println(this.name + ".createStrokedShape(" + shape.getClass().getName() + ", width = " + n + ", caps = " + n2 + ", join = " + n3 + ", miter = " + n4 + ", dashes = " + array + ", dashphase = " + n5 + ")");
            return this.target.createStrokedShape(shape, n, n2, n3, n4, array, n5);
        }
        
        @Override
        public void strokeTo(final Shape shape, final AffineTransform affineTransform, final BasicStroke basicStroke, final boolean b, final boolean b2, final boolean b3, final PathConsumer2D pathConsumer2D) {
            System.out.println(this.name + ".strokeTo(" + shape.getClass().getName() + ", " + affineTransform + ", " + basicStroke + ", " + (b ? "thin" : "wide") + ", " + (b2 ? "normalized" : "pure") + ", " + (b3 ? "AA" : "non-AA") + ", " + pathConsumer2D.getClass().getName() + ")");
            this.target.strokeTo(shape, affineTransform, basicStroke, b, b2, b3, pathConsumer2D);
        }
        
        @Override
        public float getMinimumAAPenSize() {
            System.out.println(this.name + ".getMinimumAAPenSize()");
            return this.target.getMinimumAAPenSize();
        }
        
        @Override
        public AATileGenerator getAATileGenerator(final Shape shape, final AffineTransform affineTransform, final Region region, final BasicStroke basicStroke, final boolean b, final boolean b2, final int[] array) {
            System.out.println(this.name + ".getAATileGenerator(" + shape.getClass().getName() + ", " + affineTransform + ", " + region + ", " + basicStroke + ", " + (b ? "thin" : "wide") + ", " + (b2 ? "normalized" : "pure") + ")");
            return this.target.getAATileGenerator(shape, affineTransform, region, basicStroke, b, b2, array);
        }
        
        @Override
        public AATileGenerator getAATileGenerator(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final Region region, final int[] array) {
            System.out.println(this.name + ".getAATileGenerator(" + n + ", " + n2 + ", " + n3 + ", " + n4 + ", " + n5 + ", " + n6 + ", " + n7 + ", " + n8 + ", " + region + ")");
            return this.target.getAATileGenerator(n, n2, n3, n4, n5, n6, n7, n8, region, array);
        }
    }
}
