package sun.font;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.geom.GeneralPath;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Formatter;
import java.awt.font.LayoutPath;

public abstract class LayoutPathImpl extends LayoutPath
{
    private static final boolean LOGMAP = false;
    private static final Formatter LOG;
    
    public Point2D pointToPath(final double n, final double n2) {
        final Point2D.Double double1 = new Point2D.Double(n, n2);
        this.pointToPath(double1, double1);
        return double1;
    }
    
    public Point2D pathToPoint(final double n, final double n2, final boolean b) {
        final Point2D.Double double1 = new Point2D.Double(n, n2);
        this.pathToPoint(double1, b, double1);
        return double1;
    }
    
    public void pointToPath(final double n, final double n2, final Point2D point2D) {
        point2D.setLocation(n, n2);
        this.pointToPath(point2D, point2D);
    }
    
    public void pathToPoint(final double n, final double n2, final boolean b, final Point2D point2D) {
        point2D.setLocation(n, n2);
        this.pathToPoint(point2D, b, point2D);
    }
    
    public abstract double start();
    
    public abstract double end();
    
    public abstract double length();
    
    public abstract Shape mapShape(final Shape p0);
    
    public static LayoutPathImpl getPath(final EndType endType, final double... array) {
        if ((array.length & 0x1) != 0x0) {
            throw new IllegalArgumentException("odd number of points not allowed");
        }
        return SegmentPath.get(endType, array);
    }
    
    static {
        LOG = new Formatter(System.out);
    }
    
    public enum EndType
    {
        PINNED, 
        EXTENDED, 
        CLOSED;
        
        public boolean isPinned() {
            return this == EndType.PINNED;
        }
        
        public boolean isExtended() {
            return this == EndType.EXTENDED;
        }
        
        public boolean isClosed() {
            return this == EndType.CLOSED;
        }
    }
    
    public static final class SegmentPathBuilder
    {
        private double[] data;
        private int w;
        private double px;
        private double py;
        private double a;
        private boolean pconnect;
        
        public void reset(final int n) {
            if (this.data == null || n > this.data.length) {
                this.data = new double[n];
            }
            else if (n == 0) {
                this.data = null;
            }
            this.w = 0;
            final double n2 = 0.0;
            this.py = n2;
            this.px = n2;
            this.pconnect = false;
        }
        
        public SegmentPath build(final EndType endType, final double... array) {
            assert array.length % 2 == 0;
            this.reset(array.length / 2 * 3);
            for (int i = 0; i < array.length; i += 2) {
                this.nextPoint(array[i], array[i + 1], i != 0);
            }
            return this.complete(endType);
        }
        
        public void moveTo(final double n, final double n2) {
            this.nextPoint(n, n2, false);
        }
        
        public void lineTo(final double n, final double n2) {
            this.nextPoint(n, n2, true);
        }
        
        private void nextPoint(final double n, final double n2, final boolean pconnect) {
            if (n == this.px && n2 == this.py) {
                return;
            }
            if (this.w == 0) {
                if (this.data == null) {
                    this.data = new double[6];
                }
                if (pconnect) {
                    this.w = 3;
                }
            }
            if (this.w != 0 && !pconnect && !this.pconnect) {
                this.data[this.w - 3] = (this.px = n);
                this.data[this.w - 2] = (this.py = n2);
                return;
            }
            if (this.w == this.data.length) {
                final double[] data = new double[this.w * 2];
                System.arraycopy(this.data, 0, data, 0, this.w);
                this.data = data;
            }
            if (pconnect) {
                final double n3 = n - this.px;
                final double n4 = n2 - this.py;
                this.a += Math.sqrt(n3 * n3 + n4 * n4);
            }
            this.data[this.w++] = n;
            this.data[this.w++] = n2;
            this.data[this.w++] = this.a;
            this.px = n;
            this.py = n2;
            this.pconnect = pconnect;
        }
        
        public SegmentPath complete() {
            return this.complete(EndType.EXTENDED);
        }
        
        public SegmentPath complete(final EndType endType) {
            if (this.data == null || this.w < 6) {
                return null;
            }
            SegmentPath segmentPath;
            if (this.w == this.data.length) {
                segmentPath = new SegmentPath(this.data, endType);
                this.reset(0);
            }
            else {
                final double[] array = new double[this.w];
                System.arraycopy(this.data, 0, array, 0, this.w);
                segmentPath = new SegmentPath(array, endType);
                this.reset(2);
            }
            return segmentPath;
        }
    }
    
    public static final class SegmentPath extends LayoutPathImpl
    {
        private double[] data;
        EndType etype;
        
        public static SegmentPath get(final EndType endType, final double... array) {
            return new SegmentPathBuilder().build(endType, array);
        }
        
        SegmentPath(final double[] data, final EndType etype) {
            this.data = data;
            this.etype = etype;
        }
        
        @Override
        public void pathToPoint(final Point2D point2D, final boolean b, final Point2D point2D2) {
            this.locateAndGetIndex(point2D, b, point2D2);
        }
        
        @Override
        public boolean pointToPath(final Point2D point2D, final Point2D point2D2) {
            final double x = point2D.getX();
            final double y = point2D.getY();
            double n = this.data[0];
            double n2 = this.data[1];
            double n3 = this.data[2];
            double n4 = Double.MAX_VALUE;
            double n5 = 0.0;
            double n6 = 0.0;
            double n7 = 0.0;
            int n8 = 0;
            for (int i = 3; i < this.data.length; i += 3) {
                final double n9 = this.data[i];
                final double n10 = this.data[i + 1];
                final double n11 = this.data[i + 2];
                final double n12 = n9 - n;
                final double n13 = n10 - n2;
                final double n14 = n11 - n3;
                final double n15 = n12 * (x - n) + n13 * (y - n2);
                Label_0358: {
                    double n16;
                    double n17;
                    double n18;
                    int length;
                    if (n14 == 0.0 || (n15 < 0.0 && (!this.etype.isExtended() || i != 3))) {
                        n16 = n;
                        n17 = n2;
                        n18 = n3;
                        length = i;
                    }
                    else {
                        final double n19 = n14 * n14;
                        if (n15 <= n19 || (this.etype.isExtended() && i == this.data.length - 3)) {
                            final double n20 = n15 / n19;
                            n16 = n + n20 * n12;
                            n17 = n2 + n20 * n13;
                            n18 = n3 + n20 * n14;
                            length = i;
                        }
                        else {
                            if (i != this.data.length - 3) {
                                break Label_0358;
                            }
                            n16 = n9;
                            n17 = n10;
                            n18 = n11;
                            length = this.data.length;
                        }
                    }
                    final double n21 = x - n16;
                    final double n22 = y - n17;
                    final double n23 = n21 * n21 + n22 * n22;
                    if (n23 <= n4) {
                        n4 = n23;
                        n5 = n16;
                        n6 = n17;
                        n7 = n18;
                        n8 = length;
                    }
                }
                n = n9;
                n2 = n10;
                n3 = n11;
            }
            final double n24 = this.data[n8 - 3];
            final double n25 = this.data[n8 - 2];
            if (n5 != n24 || n6 != n25) {
                final double n26 = this.data[n8];
                final double n27 = this.data[n8 + 1];
                double sqrt = Math.sqrt(n4);
                if ((x - n5) * (n27 - n25) > (y - n6) * (n26 - n24)) {
                    sqrt = -sqrt;
                }
                point2D2.setLocation(n7, sqrt);
                return false;
            }
            final boolean b = n8 != 3 && this.data[n8 - 1] != this.data[n8 - 4];
            final boolean b2 = n8 != this.data.length && this.data[n8 - 1] != this.data[n8 + 2];
            final boolean b3 = this.etype.isExtended() && (n8 == 3 || n8 == this.data.length);
            if (b && b2) {
                final Point2D.Double location = new Point2D.Double(x, y);
                this.calcoffset(n8 - 3, b3, location);
                final Point2D.Double location2 = new Point2D.Double(x, y);
                this.calcoffset(n8, b3, location2);
                if (Math.abs(location.y) > Math.abs(location2.y)) {
                    point2D2.setLocation(location);
                    return true;
                }
                point2D2.setLocation(location2);
                return false;
            }
            else {
                if (b) {
                    point2D2.setLocation(x, y);
                    this.calcoffset(n8 - 3, b3, point2D2);
                    return true;
                }
                point2D2.setLocation(x, y);
                this.calcoffset(n8, b3, point2D2);
                return false;
            }
        }
        
        private void calcoffset(final int n, final boolean b, final Point2D point2D) {
            final double n2 = this.data[n - 3];
            final double n3 = this.data[n - 2];
            final double n4 = point2D.getX() - n2;
            final double n5 = point2D.getY() - n3;
            final double n6 = this.data[n] - n2;
            final double n7 = this.data[n + 1] - n3;
            final double n8 = this.data[n + 2] - this.data[n - 1];
            double n9 = (n4 * n6 + n5 * n7) / n8;
            final double n10 = (n4 * -n7 + n5 * n6) / n8;
            if (!b) {
                if (n9 < 0.0) {
                    n9 = 0.0;
                }
                else if (n9 > n8) {
                    n9 = n8;
                }
            }
            point2D.setLocation(n9 + this.data[n - 1], n10);
        }
        
        @Override
        public Shape mapShape(final Shape shape) {
            return new Mapper().mapShape(shape);
        }
        
        @Override
        public double start() {
            return this.data[2];
        }
        
        @Override
        public double end() {
            return this.data[this.data.length - 1];
        }
        
        @Override
        public double length() {
            return this.data[this.data.length - 1] - this.data[2];
        }
        
        private double getClosedAdvance(double n, final boolean b) {
            if (this.etype.isClosed()) {
                n -= this.data[2];
                n -= (int)(n / this.length()) * this.length();
                if (n < 0.0 || (n == 0.0 && b)) {
                    n += this.length();
                }
                n += this.data[2];
            }
            return n;
        }
        
        private int getSegmentIndexForAdvance(double closedAdvance, final boolean b) {
            closedAdvance = this.getClosedAdvance(closedAdvance, b);
            int i;
            for (i = 5; i < this.data.length - 1; i += 3) {
                final double n = this.data[i];
                if (closedAdvance < n) {
                    break;
                }
                if (closedAdvance == n && b) {
                    break;
                }
            }
            return i - 2;
        }
        
        private void map(final int n, double n2, final double n3, final Point2D point2D) {
            final double n4 = this.data[n] - this.data[n - 3];
            final double n5 = this.data[n + 1] - this.data[n - 2];
            final double n6 = this.data[n + 2] - this.data[n - 1];
            final double n7 = n4 / n6;
            final double n8 = n5 / n6;
            n2 -= this.data[n - 1];
            point2D.setLocation(this.data[n - 3] + n2 * n7 - n3 * n8, this.data[n - 2] + n2 * n8 + n3 * n7);
        }
        
        private int locateAndGetIndex(final Point2D point2D, final boolean b, final Point2D point2D2) {
            final double x = point2D.getX();
            final double y = point2D.getY();
            final int segmentIndexForAdvance = this.getSegmentIndexForAdvance(x, b);
            this.map(segmentIndexForAdvance, x, y, point2D2);
            return segmentIndexForAdvance;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append(this.etype.toString());
            sb.append(" ");
            for (int i = 0; i < this.data.length; i += 3) {
                if (i > 0) {
                    sb.append(",");
                }
                final float n = (int)(this.data[i] * 100.0) / 100.0f;
                final float n2 = (int)(this.data[i + 1] * 100.0) / 100.0f;
                final float n3 = (int)(this.data[i + 2] * 10.0) / 10.0f;
                sb.append("{");
                sb.append(n);
                sb.append(",");
                sb.append(n2);
                sb.append(",");
                sb.append(n3);
                sb.append("}");
            }
            sb.append("}");
            return sb.toString();
        }
        
        class LineInfo
        {
            double sx;
            double sy;
            double lx;
            double ly;
            double m;
            
            void set(final double sx, final double sy, final double lx, final double ly) {
                this.sx = sx;
                this.sy = sy;
                this.lx = lx;
                this.ly = ly;
                final double n = lx - sx;
                if (n == 0.0) {
                    this.m = 0.0;
                }
                else {
                    this.m = (ly - sy) / n;
                }
            }
            
            void set(final LineInfo lineInfo) {
                this.sx = lineInfo.sx;
                this.sy = lineInfo.sy;
                this.lx = lineInfo.lx;
                this.ly = lineInfo.ly;
                this.m = lineInfo.m;
            }
            
            boolean pin(final double n, final double n2, final LineInfo lineInfo) {
                lineInfo.set(this);
                if (this.lx >= this.sx) {
                    if (this.sx < n2 && this.lx >= n) {
                        if (this.sx < n) {
                            if (this.m != 0.0) {
                                lineInfo.sy = this.sy + this.m * (n - this.sx);
                            }
                            lineInfo.sx = n;
                        }
                        if (this.lx > n2) {
                            if (this.m != 0.0) {
                                lineInfo.ly = this.ly + this.m * (n2 - this.lx);
                            }
                            lineInfo.lx = n2;
                        }
                        return true;
                    }
                }
                else if (this.lx < n2 && this.sx >= n) {
                    if (this.lx < n) {
                        if (this.m != 0.0) {
                            lineInfo.ly = this.ly + this.m * (n - this.lx);
                        }
                        lineInfo.lx = n;
                    }
                    if (this.sx > n2) {
                        if (this.m != 0.0) {
                            lineInfo.sy = this.sy + this.m * (n2 - this.sx);
                        }
                        lineInfo.sx = n2;
                    }
                    return true;
                }
                return false;
            }
            
            boolean pin(final int n, final LineInfo lineInfo) {
                double n2 = SegmentPath.this.data[n - 1];
                double n3 = SegmentPath.this.data[n + 2];
                switch (SegmentPath.this.etype) {
                    case EXTENDED: {
                        if (n == 3) {
                            n2 = Double.NEGATIVE_INFINITY;
                        }
                        if (n == SegmentPath.this.data.length - 3) {
                            n3 = Double.POSITIVE_INFINITY;
                            break;
                        }
                        break;
                    }
                }
                return this.pin(n2, n3, lineInfo);
            }
        }
        
        class Segment
        {
            final int ix;
            final double ux;
            final double uy;
            final LineInfo temp;
            boolean broken;
            double cx;
            double cy;
            GeneralPath gp;
            
            Segment(final int ix) {
                this.ix = ix;
                final double n = SegmentPath.this.data[ix + 2] - SegmentPath.this.data[ix - 1];
                this.ux = (SegmentPath.this.data[ix] - SegmentPath.this.data[ix - 3]) / n;
                this.uy = (SegmentPath.this.data[ix + 1] - SegmentPath.this.data[ix - 2]) / n;
                this.temp = new LineInfo();
            }
            
            void init() {
                this.broken = true;
                final double n = Double.MIN_VALUE;
                this.cy = n;
                this.cx = n;
                this.gp = new GeneralPath();
            }
            
            void move() {
                this.broken = true;
            }
            
            void close() {
                if (!this.broken) {
                    this.gp.closePath();
                }
            }
            
            void line(final LineInfo lineInfo) {
                if (lineInfo.pin(this.ix, this.temp)) {
                    final LineInfo temp = this.temp;
                    temp.sx -= SegmentPath.this.data[this.ix - 1];
                    final double n = SegmentPath.this.data[this.ix - 3] + this.temp.sx * this.ux - this.temp.sy * this.uy;
                    final double n2 = SegmentPath.this.data[this.ix - 2] + this.temp.sx * this.uy + this.temp.sy * this.ux;
                    final LineInfo temp2 = this.temp;
                    temp2.lx -= SegmentPath.this.data[this.ix - 1];
                    final double cx = SegmentPath.this.data[this.ix - 3] + this.temp.lx * this.ux - this.temp.ly * this.uy;
                    final double cy = SegmentPath.this.data[this.ix - 2] + this.temp.lx * this.uy + this.temp.ly * this.ux;
                    if (n != this.cx || n2 != this.cy) {
                        if (this.broken) {
                            this.gp.moveTo((float)n, (float)n2);
                        }
                        else {
                            this.gp.lineTo((float)n, (float)n2);
                        }
                    }
                    this.gp.lineTo((float)cx, (float)cy);
                    this.broken = false;
                    this.cx = cx;
                    this.cy = cy;
                }
            }
        }
        
        class Mapper
        {
            final LineInfo li;
            final ArrayList<Segment> segments;
            final Point2D.Double mpt;
            final Point2D.Double cpt;
            boolean haveMT;
            
            Mapper() {
                this.li = new LineInfo();
                this.segments = new ArrayList<Segment>();
                for (int i = 3; i < SegmentPath.this.data.length; i += 3) {
                    if (SegmentPath.this.data[i + 2] != SegmentPath.this.data[i - 1]) {
                        this.segments.add(new Segment(i));
                    }
                }
                this.mpt = new Point2D.Double();
                this.cpt = new Point2D.Double();
            }
            
            void init() {
                this.haveMT = false;
                final Iterator<Segment> iterator = this.segments.iterator();
                while (iterator.hasNext()) {
                    iterator.next().init();
                }
            }
            
            void moveTo(final double x, final double y) {
                this.mpt.x = x;
                this.mpt.y = y;
                this.haveMT = true;
            }
            
            void lineTo(final double x, final double y) {
                if (this.haveMT) {
                    this.cpt.x = this.mpt.x;
                    this.cpt.y = this.mpt.y;
                }
                if (x == this.cpt.x && y == this.cpt.y) {
                    return;
                }
                if (this.haveMT) {
                    this.haveMT = false;
                    final Iterator<Segment> iterator = this.segments.iterator();
                    while (iterator.hasNext()) {
                        iterator.next().move();
                    }
                }
                this.li.set(this.cpt.x, this.cpt.y, x, y);
                final Iterator<Segment> iterator2 = this.segments.iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().line(this.li);
                }
                this.cpt.x = x;
                this.cpt.y = y;
            }
            
            void close() {
                this.lineTo(this.mpt.x, this.mpt.y);
                final Iterator<Segment> iterator = this.segments.iterator();
                while (iterator.hasNext()) {
                    iterator.next().close();
                }
            }
            
            public Shape mapShape(final Shape shape) {
                final PathIterator pathIterator = shape.getPathIterator(null, 1.0);
                this.init();
                final double[] array = new double[2];
                while (!pathIterator.isDone()) {
                    switch (pathIterator.currentSegment(array)) {
                        case 4: {
                            this.close();
                            break;
                        }
                        case 0: {
                            this.moveTo(array[0], array[1]);
                            break;
                        }
                        case 1: {
                            this.lineTo(array[0], array[1]);
                            break;
                        }
                    }
                    pathIterator.next();
                }
                final GeneralPath generalPath = new GeneralPath();
                final Iterator<Segment> iterator = this.segments.iterator();
                while (iterator.hasNext()) {
                    generalPath.append(iterator.next().gp, false);
                }
                return generalPath;
            }
        }
    }
    
    public static class EmptyPath extends LayoutPathImpl
    {
        private AffineTransform tx;
        
        public EmptyPath(final AffineTransform tx) {
            this.tx = tx;
        }
        
        @Override
        public void pathToPoint(final Point2D location, final boolean b, final Point2D point2D) {
            if (this.tx != null) {
                this.tx.transform(location, point2D);
            }
            else {
                point2D.setLocation(location);
            }
        }
        
        @Override
        public boolean pointToPath(final Point2D location, final Point2D point2D) {
            point2D.setLocation(location);
            if (this.tx != null) {
                try {
                    this.tx.inverseTransform(location, point2D);
                }
                catch (final NoninvertibleTransformException ex) {}
            }
            return point2D.getX() > 0.0;
        }
        
        @Override
        public double start() {
            return 0.0;
        }
        
        @Override
        public double end() {
            return 0.0;
        }
        
        @Override
        public double length() {
            return 0.0;
        }
        
        @Override
        public Shape mapShape(final Shape shape) {
            if (this.tx != null) {
                return this.tx.createTransformedShape(shape);
            }
            return shape;
        }
    }
}
