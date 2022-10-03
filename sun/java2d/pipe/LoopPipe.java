package sun.java2d.pipe;

import sun.java2d.SurfaceData;
import sun.java2d.loops.FillSpans;
import sun.awt.geom.PathConsumer2D;
import java.awt.BasicStroke;
import java.awt.geom.Path2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import sun.java2d.SunGraphics2D;

public class LoopPipe implements PixelDrawPipe, PixelFillPipe, ParallelogramPipe, ShapeDrawPipe, LoopBasedPipe
{
    static final RenderingEngine RenderEngine;
    
    @Override
    public void drawLine(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        final int transX = sunGraphics2D.transX;
        final int transY = sunGraphics2D.transY;
        sunGraphics2D.loops.drawLineLoop.DrawLine(sunGraphics2D, sunGraphics2D.getSurfaceData(), n + transX, n2 + transY, n3 + transX, n4 + transY);
    }
    
    @Override
    public void drawRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        sunGraphics2D.loops.drawRectLoop.DrawRect(sunGraphics2D, sunGraphics2D.getSurfaceData(), n + sunGraphics2D.transX, n2 + sunGraphics2D.transY, n3, n4);
    }
    
    @Override
    public void drawRoundRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        sunGraphics2D.shapepipe.draw(sunGraphics2D, new RoundRectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6));
    }
    
    @Override
    public void drawOval(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        sunGraphics2D.shapepipe.draw(sunGraphics2D, new Ellipse2D.Float((float)n, (float)n2, (float)n3, (float)n4));
    }
    
    @Override
    public void drawArc(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        sunGraphics2D.shapepipe.draw(sunGraphics2D, new Arc2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6, 0));
    }
    
    @Override
    public void drawPolyline(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        sunGraphics2D.loops.drawPolygonsLoop.DrawPolygons(sunGraphics2D, sunGraphics2D.getSurfaceData(), array, array2, new int[] { n }, 1, sunGraphics2D.transX, sunGraphics2D.transY, false);
    }
    
    @Override
    public void drawPolygon(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        sunGraphics2D.loops.drawPolygonsLoop.DrawPolygons(sunGraphics2D, sunGraphics2D.getSurfaceData(), array, array2, new int[] { n }, 1, sunGraphics2D.transX, sunGraphics2D.transY, true);
    }
    
    @Override
    public void fillRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        sunGraphics2D.loops.fillRectLoop.FillRect(sunGraphics2D, sunGraphics2D.getSurfaceData(), n + sunGraphics2D.transX, n2 + sunGraphics2D.transY, n3, n4);
    }
    
    @Override
    public void fillRoundRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        sunGraphics2D.shapepipe.fill(sunGraphics2D, new RoundRectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6));
    }
    
    @Override
    public void fillOval(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        sunGraphics2D.shapepipe.fill(sunGraphics2D, new Ellipse2D.Float((float)n, (float)n2, (float)n3, (float)n4));
    }
    
    @Override
    public void fillArc(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        sunGraphics2D.shapepipe.fill(sunGraphics2D, new Arc2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6, 2));
    }
    
    @Override
    public void fillPolygon(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        final ShapeSpanIterator fillSSI = getFillSSI(sunGraphics2D);
        try {
            fillSSI.setOutputArea(sunGraphics2D.getCompClip());
            fillSSI.appendPoly(array, array2, n, sunGraphics2D.transX, sunGraphics2D.transY);
            fillSpans(sunGraphics2D, fillSSI);
        }
        finally {
            fillSSI.dispose();
        }
    }
    
    @Override
    public void draw(final SunGraphics2D sunGraphics2D, final Shape shape) {
        if (sunGraphics2D.strokeState == 0) {
            Path2D.Float float1;
            int transX;
            int transY;
            if (sunGraphics2D.transformState <= 1) {
                if (shape instanceof Path2D.Float) {
                    float1 = (Path2D.Float)shape;
                }
                else {
                    float1 = new Path2D.Float(shape);
                }
                transX = sunGraphics2D.transX;
                transY = sunGraphics2D.transY;
            }
            else {
                float1 = new Path2D.Float(shape, sunGraphics2D.transform);
                transX = 0;
                transY = 0;
            }
            sunGraphics2D.loops.drawPathLoop.DrawPath(sunGraphics2D, sunGraphics2D.getSurfaceData(), transX, transY, float1);
            return;
        }
        if (sunGraphics2D.strokeState == 3) {
            this.fill(sunGraphics2D, sunGraphics2D.stroke.createStrokedShape(shape));
            return;
        }
        final ShapeSpanIterator strokeSpans = getStrokeSpans(sunGraphics2D, shape);
        try {
            fillSpans(sunGraphics2D, strokeSpans);
        }
        finally {
            strokeSpans.dispose();
        }
    }
    
    public static ShapeSpanIterator getFillSSI(final SunGraphics2D sunGraphics2D) {
        return new ShapeSpanIterator(sunGraphics2D.stroke instanceof BasicStroke && sunGraphics2D.strokeHint != 2);
    }
    
    public static ShapeSpanIterator getStrokeSpans(final SunGraphics2D sunGraphics2D, final Shape shape) {
        final ShapeSpanIterator shapeSpanIterator = new ShapeSpanIterator(false);
        try {
            shapeSpanIterator.setOutputArea(sunGraphics2D.getCompClip());
            shapeSpanIterator.setRule(1);
            LoopPipe.RenderEngine.strokeTo(shape, sunGraphics2D.transform, (BasicStroke)sunGraphics2D.stroke, sunGraphics2D.strokeState <= 1, sunGraphics2D.strokeHint != 2, false, shapeSpanIterator);
        }
        catch (final Throwable t) {
            shapeSpanIterator.dispose();
            throw new InternalError("Unable to Stroke shape (" + t.getMessage() + ")", t);
        }
        return shapeSpanIterator;
    }
    
    @Override
    public void fill(final SunGraphics2D sunGraphics2D, final Shape shape) {
        if (sunGraphics2D.strokeState == 0) {
            Path2D.Float float1;
            int transX;
            int transY;
            if (sunGraphics2D.transformState <= 1) {
                if (shape instanceof Path2D.Float) {
                    float1 = (Path2D.Float)shape;
                }
                else {
                    float1 = new Path2D.Float(shape);
                }
                transX = sunGraphics2D.transX;
                transY = sunGraphics2D.transY;
            }
            else {
                float1 = new Path2D.Float(shape, sunGraphics2D.transform);
                transX = 0;
                transY = 0;
            }
            sunGraphics2D.loops.fillPathLoop.FillPath(sunGraphics2D, sunGraphics2D.getSurfaceData(), transX, transY, float1);
            return;
        }
        final ShapeSpanIterator fillSSI = getFillSSI(sunGraphics2D);
        try {
            fillSSI.setOutputArea(sunGraphics2D.getCompClip());
            fillSSI.appendPath(shape.getPathIterator((sunGraphics2D.transformState == 0) ? null : sunGraphics2D.transform));
            fillSpans(sunGraphics2D, fillSSI);
        }
        finally {
            fillSSI.dispose();
        }
    }
    
    private static void fillSpans(final SunGraphics2D sunGraphics2D, SpanIterator filter) {
        if (sunGraphics2D.clipState == 2) {
            filter = sunGraphics2D.clipRegion.filter(filter);
        }
        else {
            final FillSpans fillSpansLoop = sunGraphics2D.loops.fillSpansLoop;
            if (fillSpansLoop != null) {
                fillSpansLoop.FillSpans(sunGraphics2D, sunGraphics2D.getSurfaceData(), filter);
                return;
            }
        }
        final int[] array = new int[4];
        final SurfaceData surfaceData = sunGraphics2D.getSurfaceData();
        while (filter.nextSpan(array)) {
            final int n = array[0];
            final int n2 = array[1];
            sunGraphics2D.loops.fillRectLoop.FillRect(sunGraphics2D, surfaceData, n, n2, array[2] - n, array[3] - n2);
        }
    }
    
    @Override
    public void fillParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10) {
        sunGraphics2D.loops.fillParallelogramLoop.FillParallelogram(sunGraphics2D, sunGraphics2D.getSurfaceData(), n5, n6, n7, n8, n9, n10);
    }
    
    @Override
    public void drawParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10, final double n11, final double n12) {
        sunGraphics2D.loops.drawParallelogramLoop.DrawParallelogram(sunGraphics2D, sunGraphics2D.getSurfaceData(), n5, n6, n7, n8, n9, n10, n11, n12);
    }
    
    static {
        RenderEngine = RenderingEngine.getInstance();
    }
}
