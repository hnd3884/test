package sun.java2d.pipe;

import java.awt.geom.AffineTransform;
import sun.java2d.loops.ProcessPath;
import java.awt.geom.Path2D;
import java.awt.Polygon;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import sun.java2d.SunGraphics2D;

public abstract class BufferedRenderPipe implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe, ParallelogramPipe
{
    ParallelogramPipe aapgrampipe;
    static final int BYTES_PER_POLY_POINT = 8;
    static final int BYTES_PER_SCANLINE = 12;
    static final int BYTES_PER_SPAN = 16;
    protected RenderQueue rq;
    protected RenderBuffer buf;
    private BufferedDrawHandler drawHandler;
    
    public BufferedRenderPipe(final RenderQueue rq) {
        this.aapgrampipe = new AAParallelogramPipe();
        this.rq = rq;
        this.buf = rq.getBuffer();
        this.drawHandler = new BufferedDrawHandler();
    }
    
    public ParallelogramPipe getAAParallelogramPipe() {
        return this.aapgrampipe;
    }
    
    protected abstract void validateContext(final SunGraphics2D p0);
    
    protected abstract void validateContextAA(final SunGraphics2D p0);
    
    @Override
    public void drawLine(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        final int transX = sunGraphics2D.transX;
        final int transY = sunGraphics2D.transY;
        this.rq.lock();
        try {
            this.validateContext(sunGraphics2D);
            this.rq.ensureCapacity(20);
            this.buf.putInt(10);
            this.buf.putInt(n + transX);
            this.buf.putInt(n2 + transY);
            this.buf.putInt(n3 + transX);
            this.buf.putInt(n4 + transY);
        }
        finally {
            this.rq.unlock();
        }
    }
    
    @Override
    public void drawRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        this.rq.lock();
        try {
            this.validateContext(sunGraphics2D);
            this.rq.ensureCapacity(20);
            this.buf.putInt(11);
            this.buf.putInt(n + sunGraphics2D.transX);
            this.buf.putInt(n2 + sunGraphics2D.transY);
            this.buf.putInt(n3);
            this.buf.putInt(n4);
        }
        finally {
            this.rq.unlock();
        }
    }
    
    @Override
    public void fillRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        this.rq.lock();
        try {
            this.validateContext(sunGraphics2D);
            this.rq.ensureCapacity(20);
            this.buf.putInt(20);
            this.buf.putInt(n + sunGraphics2D.transX);
            this.buf.putInt(n2 + sunGraphics2D.transY);
            this.buf.putInt(n3);
            this.buf.putInt(n4);
        }
        finally {
            this.rq.unlock();
        }
    }
    
    @Override
    public void drawRoundRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.draw(sunGraphics2D, new RoundRectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6));
    }
    
    @Override
    public void fillRoundRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.fill(sunGraphics2D, new RoundRectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6));
    }
    
    @Override
    public void drawOval(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        this.draw(sunGraphics2D, new Ellipse2D.Float((float)n, (float)n2, (float)n3, (float)n4));
    }
    
    @Override
    public void fillOval(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        this.fill(sunGraphics2D, new Ellipse2D.Float((float)n, (float)n2, (float)n3, (float)n4));
    }
    
    @Override
    public void drawArc(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.draw(sunGraphics2D, new Arc2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6, 0));
    }
    
    @Override
    public void fillArc(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.fill(sunGraphics2D, new Arc2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6, 2));
    }
    
    protected void drawPoly(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n, final boolean b) {
        if (array == null || array2 == null) {
            throw new NullPointerException("coordinate array");
        }
        if (array.length < n || array2.length < n) {
            throw new ArrayIndexOutOfBoundsException("coordinate array");
        }
        if (n < 2) {
            return;
        }
        if (n == 2 && !b) {
            this.drawLine(sunGraphics2D, array[0], array2[0], array[1], array2[1]);
            return;
        }
        this.rq.lock();
        try {
            this.validateContext(sunGraphics2D);
            final int n2 = 20 + n * 8;
            if (n2 <= this.buf.capacity()) {
                if (n2 > this.buf.remaining()) {
                    this.rq.flushNow();
                }
                this.buf.putInt(12);
                this.buf.putInt(n);
                this.buf.putInt(b ? 1 : 0);
                this.buf.putInt(sunGraphics2D.transX);
                this.buf.putInt(sunGraphics2D.transY);
                this.buf.put(array, 0, n);
                this.buf.put(array2, 0, n);
            }
            else {
                this.rq.flushAndInvokeNow(new Runnable() {
                    @Override
                    public void run() {
                        BufferedRenderPipe.this.drawPoly(array, array2, n, b, sunGraphics2D.transX, sunGraphics2D.transY);
                    }
                });
            }
        }
        finally {
            this.rq.unlock();
        }
    }
    
    protected abstract void drawPoly(final int[] p0, final int[] p1, final int p2, final boolean p3, final int p4, final int p5);
    
    @Override
    public void drawPolyline(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        this.drawPoly(sunGraphics2D, array, array2, n, false);
    }
    
    @Override
    public void drawPolygon(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        this.drawPoly(sunGraphics2D, array, array2, n, true);
    }
    
    @Override
    public void fillPolygon(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        this.fill(sunGraphics2D, new Polygon(array, array2, n));
    }
    
    protected void drawPath(final SunGraphics2D sunGraphics2D, final Path2D.Float float1, final int n, final int n2) {
        this.rq.lock();
        try {
            this.validateContext(sunGraphics2D);
            this.drawHandler.validate(sunGraphics2D);
            ProcessPath.drawPath(this.drawHandler, float1, n, n2);
        }
        finally {
            this.rq.unlock();
        }
    }
    
    protected void fillPath(final SunGraphics2D sunGraphics2D, final Path2D.Float float1, final int n, final int n2) {
        this.rq.lock();
        try {
            this.validateContext(sunGraphics2D);
            this.drawHandler.validate(sunGraphics2D);
            this.drawHandler.startFillPath();
            ProcessPath.fillPath(this.drawHandler, float1, n, n2);
            this.drawHandler.endFillPath();
        }
        finally {
            this.rq.unlock();
        }
    }
    
    private native int fillSpans(final RenderQueue p0, final long p1, final int p2, final int p3, final SpanIterator p4, final long p5, final int p6, final int p7);
    
    protected void fillSpans(final SunGraphics2D sunGraphics2D, final SpanIterator spanIterator, final int n, final int n2) {
        this.rq.lock();
        try {
            this.validateContext(sunGraphics2D);
            this.rq.ensureCapacity(24);
            this.buf.position(this.fillSpans(this.rq, this.buf.getAddress(), this.buf.position(), this.buf.capacity(), spanIterator, spanIterator.getNativeIterator(), n, n2));
        }
        finally {
            this.rq.unlock();
        }
    }
    
    @Override
    public void fillParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10) {
        this.rq.lock();
        try {
            this.validateContext(sunGraphics2D);
            this.rq.ensureCapacity(28);
            this.buf.putInt(22);
            this.buf.putFloat((float)n5);
            this.buf.putFloat((float)n6);
            this.buf.putFloat((float)n7);
            this.buf.putFloat((float)n8);
            this.buf.putFloat((float)n9);
            this.buf.putFloat((float)n10);
        }
        finally {
            this.rq.unlock();
        }
    }
    
    @Override
    public void drawParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10, final double n11, final double n12) {
        this.rq.lock();
        try {
            this.validateContext(sunGraphics2D);
            this.rq.ensureCapacity(36);
            this.buf.putInt(15);
            this.buf.putFloat((float)n5);
            this.buf.putFloat((float)n6);
            this.buf.putFloat((float)n7);
            this.buf.putFloat((float)n8);
            this.buf.putFloat((float)n9);
            this.buf.putFloat((float)n10);
            this.buf.putFloat((float)n11);
            this.buf.putFloat((float)n12);
        }
        finally {
            this.rq.unlock();
        }
    }
    
    @Override
    public void draw(final SunGraphics2D sunGraphics2D, final Shape shape) {
        if (sunGraphics2D.strokeState == 0) {
            if (shape instanceof Polygon && sunGraphics2D.transformState < 3) {
                final Polygon polygon = (Polygon)shape;
                this.drawPolygon(sunGraphics2D, polygon.xpoints, polygon.ypoints, polygon.npoints);
                return;
            }
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
            this.drawPath(sunGraphics2D, float1, transX, transY);
        }
        else if (sunGraphics2D.strokeState < 3) {
            final ShapeSpanIterator strokeSpans = LoopPipe.getStrokeSpans(sunGraphics2D, shape);
            try {
                this.fillSpans(sunGraphics2D, strokeSpans, 0, 0);
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
            this.fillPath(sunGraphics2D, float1, transX, transY);
            return;
        }
        AffineTransform transform;
        int transX2;
        int transY2;
        if (sunGraphics2D.transformState <= 1) {
            transform = null;
            transX2 = sunGraphics2D.transX;
            transY2 = sunGraphics2D.transY;
        }
        else {
            transform = sunGraphics2D.transform;
            transY2 = (transX2 = 0);
        }
        final ShapeSpanIterator fillSSI = LoopPipe.getFillSSI(sunGraphics2D);
        try {
            final Region compClip = sunGraphics2D.getCompClip();
            fillSSI.setOutputAreaXYXY(compClip.getLoX() - transX2, compClip.getLoY() - transY2, compClip.getHiX() - transX2, compClip.getHiY() - transY2);
            fillSSI.appendPath(shape.getPathIterator(transform));
            this.fillSpans(sunGraphics2D, fillSSI, transX2, transY2);
        }
        finally {
            fillSSI.dispose();
        }
    }
    
    private class BufferedDrawHandler extends ProcessPath.DrawHandler
    {
        private int scanlineCount;
        private int scanlineCountIndex;
        private int remainingScanlines;
        
        BufferedDrawHandler() {
            super(0, 0, 0, 0);
        }
        
        void validate(final SunGraphics2D sunGraphics2D) {
            final Region compClip = sunGraphics2D.getCompClip();
            this.setBounds(compClip.getLoX(), compClip.getLoY(), compClip.getHiX(), compClip.getHiY(), sunGraphics2D.strokeHint);
        }
        
        @Override
        public void drawLine(final int n, final int n2, final int n3, final int n4) {
            BufferedRenderPipe.this.rq.ensureCapacity(20);
            BufferedRenderPipe.this.buf.putInt(10);
            BufferedRenderPipe.this.buf.putInt(n);
            BufferedRenderPipe.this.buf.putInt(n2);
            BufferedRenderPipe.this.buf.putInt(n3);
            BufferedRenderPipe.this.buf.putInt(n4);
        }
        
        @Override
        public void drawPixel(final int n, final int n2) {
            BufferedRenderPipe.this.rq.ensureCapacity(12);
            BufferedRenderPipe.this.buf.putInt(13);
            BufferedRenderPipe.this.buf.putInt(n);
            BufferedRenderPipe.this.buf.putInt(n2);
        }
        
        private void resetFillPath() {
            BufferedRenderPipe.this.buf.putInt(14);
            this.scanlineCountIndex = BufferedRenderPipe.this.buf.position();
            BufferedRenderPipe.this.buf.putInt(0);
            this.scanlineCount = 0;
            this.remainingScanlines = BufferedRenderPipe.this.buf.remaining() / 12;
        }
        
        private void updateScanlineCount() {
            BufferedRenderPipe.this.buf.putInt(this.scanlineCountIndex, this.scanlineCount);
        }
        
        public void startFillPath() {
            BufferedRenderPipe.this.rq.ensureCapacity(20);
            this.resetFillPath();
        }
        
        @Override
        public void drawScanline(final int n, final int n2, final int n3) {
            if (this.remainingScanlines == 0) {
                this.updateScanlineCount();
                BufferedRenderPipe.this.rq.flushNow();
                this.resetFillPath();
            }
            BufferedRenderPipe.this.buf.putInt(n);
            BufferedRenderPipe.this.buf.putInt(n2);
            BufferedRenderPipe.this.buf.putInt(n3);
            ++this.scanlineCount;
            --this.remainingScanlines;
        }
        
        public void endFillPath() {
            this.updateScanlineCount();
        }
    }
    
    private class AAParallelogramPipe implements ParallelogramPipe
    {
        @Override
        public void fillParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10) {
            BufferedRenderPipe.this.rq.lock();
            try {
                BufferedRenderPipe.this.validateContextAA(sunGraphics2D);
                BufferedRenderPipe.this.rq.ensureCapacity(28);
                BufferedRenderPipe.this.buf.putInt(23);
                BufferedRenderPipe.this.buf.putFloat((float)n5);
                BufferedRenderPipe.this.buf.putFloat((float)n6);
                BufferedRenderPipe.this.buf.putFloat((float)n7);
                BufferedRenderPipe.this.buf.putFloat((float)n8);
                BufferedRenderPipe.this.buf.putFloat((float)n9);
                BufferedRenderPipe.this.buf.putFloat((float)n10);
            }
            finally {
                BufferedRenderPipe.this.rq.unlock();
            }
        }
        
        @Override
        public void drawParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10, final double n11, final double n12) {
            BufferedRenderPipe.this.rq.lock();
            try {
                BufferedRenderPipe.this.validateContextAA(sunGraphics2D);
                BufferedRenderPipe.this.rq.ensureCapacity(36);
                BufferedRenderPipe.this.buf.putInt(16);
                BufferedRenderPipe.this.buf.putFloat((float)n5);
                BufferedRenderPipe.this.buf.putFloat((float)n6);
                BufferedRenderPipe.this.buf.putFloat((float)n7);
                BufferedRenderPipe.this.buf.putFloat((float)n8);
                BufferedRenderPipe.this.buf.putFloat((float)n9);
                BufferedRenderPipe.this.buf.putFloat((float)n10);
                BufferedRenderPipe.this.buf.putFloat((float)n11);
                BufferedRenderPipe.this.buf.putFloat((float)n12);
            }
            finally {
                BufferedRenderPipe.this.rq.unlock();
            }
        }
    }
}
