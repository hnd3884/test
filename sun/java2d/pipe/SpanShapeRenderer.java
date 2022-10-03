package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.BasicStroke;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public abstract class SpanShapeRenderer implements ShapeDrawPipe
{
    static final RenderingEngine RenderEngine;
    public static final int NON_RECTILINEAR_TRANSFORM_MASK = 48;
    
    @Override
    public void draw(final SunGraphics2D sunGraphics2D, final Shape shape) {
        if (sunGraphics2D.stroke instanceof BasicStroke) {
            final ShapeSpanIterator strokeSpans = LoopPipe.getStrokeSpans(sunGraphics2D, shape);
            try {
                this.renderSpans(sunGraphics2D, sunGraphics2D.getCompClip(), shape, strokeSpans);
            }
            finally {
                strokeSpans.dispose();
            }
        }
        else {
            this.fill(sunGraphics2D, sunGraphics2D.stroke.createStrokedShape(shape));
        }
    }
    
    @Override
    public void fill(final SunGraphics2D sunGraphics2D, final Shape shape) {
        if (shape instanceof Rectangle2D && (sunGraphics2D.transform.getType() & 0x30) == 0x0) {
            this.renderRect(sunGraphics2D, (Rectangle2D)shape);
            return;
        }
        final Region compClip = sunGraphics2D.getCompClip();
        final ShapeSpanIterator fillSSI = LoopPipe.getFillSSI(sunGraphics2D);
        try {
            fillSSI.setOutputArea(compClip);
            fillSSI.appendPath(shape.getPathIterator(sunGraphics2D.transform));
            this.renderSpans(sunGraphics2D, compClip, shape, fillSSI);
        }
        finally {
            fillSSI.dispose();
        }
    }
    
    public abstract Object startSequence(final SunGraphics2D p0, final Shape p1, final Rectangle p2, final int[] p3);
    
    public abstract void renderBox(final Object p0, final int p1, final int p2, final int p3, final int p4);
    
    public abstract void endSequence(final Object p0);
    
    public void renderRect(final SunGraphics2D sunGraphics2D, final Rectangle2D rectangle2D) {
        final double[] array2;
        final double[] array = array2 = new double[] { rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight() };
        final int n = 2;
        array2[n] += array[0];
        final double[] array3 = array;
        final int n2 = 3;
        array3[n2] += array[1];
        if (array[2] <= array[0] || array[3] <= array[1]) {
            return;
        }
        sunGraphics2D.transform.transform(array, 0, array, 0, 2);
        if (array[2] < array[0]) {
            final double n3 = array[2];
            array[2] = array[0];
            array[0] = n3;
        }
        if (array[3] < array[1]) {
            final double n4 = array[3];
            array[3] = array[1];
            array[1] = n4;
        }
        final int[] array4 = { (int)array[0], (int)array[1], (int)array[2], (int)array[3] };
        final Rectangle rectangle = new Rectangle(array4[0], array4[1], array4[2] - array4[0], array4[3] - array4[1]);
        final Region compClip = sunGraphics2D.getCompClip();
        compClip.clipBoxToBounds(array4);
        if (array4[0] >= array4[2] || array4[1] >= array4[3]) {
            return;
        }
        final Object startSequence = this.startSequence(sunGraphics2D, rectangle2D, rectangle, array4);
        if (compClip.isRectangular()) {
            this.renderBox(startSequence, array4[0], array4[1], array4[2] - array4[0], array4[3] - array4[1]);
        }
        else {
            while (compClip.getSpanIterator(array4).nextSpan(array4)) {
                this.renderBox(startSequence, array4[0], array4[1], array4[2] - array4[0], array4[3] - array4[1]);
            }
        }
        this.endSequence(startSequence);
    }
    
    public void renderSpans(final SunGraphics2D sunGraphics2D, final Region region, final Shape shape, final ShapeSpanIterator shapeSpanIterator) {
        Object startSequence = null;
        final int[] array = new int[4];
        try {
            shapeSpanIterator.getPathBox(array);
            final Rectangle rectangle = new Rectangle(array[0], array[1], array[2] - array[0], array[3] - array[1]);
            region.clipBoxToBounds(array);
            if (array[0] >= array[2] || array[1] >= array[3]) {
                return;
            }
            shapeSpanIterator.intersectClipBox(array[0], array[1], array[2], array[3]);
            startSequence = this.startSequence(sunGraphics2D, shape, rectangle, array);
            this.spanClipLoop(startSequence, shapeSpanIterator, region, array);
        }
        finally {
            if (startSequence != null) {
                this.endSequence(startSequence);
            }
        }
    }
    
    public void spanClipLoop(final Object o, SpanIterator filter, final Region region, final int[] array) {
        if (!region.isRectangular()) {
            filter = region.filter(filter);
        }
        while (filter.nextSpan(array)) {
            final int n = array[0];
            final int n2 = array[1];
            this.renderBox(o, n, n2, array[2] - n, array[3] - n2);
        }
    }
    
    static {
        RenderEngine = RenderingEngine.getInstance();
    }
    
    public static class Composite extends SpanShapeRenderer
    {
        CompositePipe comppipe;
        
        public Composite(final CompositePipe comppipe) {
            this.comppipe = comppipe;
        }
        
        @Override
        public Object startSequence(final SunGraphics2D sunGraphics2D, final Shape shape, final Rectangle rectangle, final int[] array) {
            return this.comppipe.startSequence(sunGraphics2D, shape, rectangle, array);
        }
        
        @Override
        public void renderBox(final Object o, final int n, final int n2, final int n3, final int n4) {
            this.comppipe.renderPathTile(o, null, 0, n3, n, n2, n3, n4);
        }
        
        @Override
        public void endSequence(final Object o) {
            this.comppipe.endSequence(o);
        }
    }
    
    public static class Simple extends SpanShapeRenderer implements LoopBasedPipe
    {
        @Override
        public Object startSequence(final SunGraphics2D sunGraphics2D, final Shape shape, final Rectangle rectangle, final int[] array) {
            return sunGraphics2D;
        }
        
        @Override
        public void renderBox(final Object o, final int n, final int n2, final int n3, final int n4) {
            final SunGraphics2D sunGraphics2D = (SunGraphics2D)o;
            sunGraphics2D.loops.fillRectLoop.FillRect(sunGraphics2D, sunGraphics2D.getSurfaceData(), n, n2, n3, n4);
        }
        
        @Override
        public void endSequence(final Object o) {
        }
    }
}
