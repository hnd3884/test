package java.awt.geom;

import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import sun.awt.geom.Curve;
import java.awt.Rectangle;
import java.util.Arrays;
import java.awt.Shape;

public abstract class Path2D implements Shape, Cloneable
{
    public static final int WIND_EVEN_ODD = 0;
    public static final int WIND_NON_ZERO = 1;
    private static final byte SEG_MOVETO = 0;
    private static final byte SEG_LINETO = 1;
    private static final byte SEG_QUADTO = 2;
    private static final byte SEG_CUBICTO = 3;
    private static final byte SEG_CLOSE = 4;
    transient byte[] pointTypes;
    transient int numTypes;
    transient int numCoords;
    transient int windingRule;
    static final int INIT_SIZE = 20;
    static final int EXPAND_MAX = 500;
    static final int EXPAND_MAX_COORDS = 1000;
    static final int EXPAND_MIN = 10;
    private static final byte SERIAL_STORAGE_FLT_ARRAY = 48;
    private static final byte SERIAL_STORAGE_DBL_ARRAY = 49;
    private static final byte SERIAL_SEG_FLT_MOVETO = 64;
    private static final byte SERIAL_SEG_FLT_LINETO = 65;
    private static final byte SERIAL_SEG_FLT_QUADTO = 66;
    private static final byte SERIAL_SEG_FLT_CUBICTO = 67;
    private static final byte SERIAL_SEG_DBL_MOVETO = 80;
    private static final byte SERIAL_SEG_DBL_LINETO = 81;
    private static final byte SERIAL_SEG_DBL_QUADTO = 82;
    private static final byte SERIAL_SEG_DBL_CUBICTO = 83;
    private static final byte SERIAL_SEG_CLOSE = 96;
    private static final byte SERIAL_PATH_END = 97;
    
    Path2D() {
    }
    
    Path2D(final int windingRule, final int n) {
        this.setWindingRule(windingRule);
        this.pointTypes = new byte[n];
    }
    
    abstract float[] cloneCoordsFloat(final AffineTransform p0);
    
    abstract double[] cloneCoordsDouble(final AffineTransform p0);
    
    abstract void append(final float p0, final float p1);
    
    abstract void append(final double p0, final double p1);
    
    abstract Point2D getPoint(final int p0);
    
    abstract void needRoom(final boolean p0, final int p1);
    
    abstract int pointCrossings(final double p0, final double p1);
    
    abstract int rectCrossings(final double p0, final double p1, final double p2, final double p3);
    
    static byte[] expandPointTypes(final byte[] array, final int n) {
        final int length = array.length;
        final int n2 = length + n;
        if (n2 < length) {
            throw new ArrayIndexOutOfBoundsException("pointTypes exceeds maximum capacity !");
        }
        int max = length;
        if (max > 500) {
            max = Math.max(500, length >> 3);
        }
        else if (max < 10) {
            max = 10;
        }
        assert max > 0;
        int n3 = length + max;
        if (n3 < n2) {
            n3 = Integer.MAX_VALUE;
        }
        try {
            return Arrays.copyOf(array, n3);
        }
        catch (final OutOfMemoryError outOfMemoryError) {
            if (n3 == n2) {
                throw outOfMemoryError;
            }
            n3 = n2 + (n3 - n2) / 2;
            return Arrays.copyOf(array, n3);
        }
    }
    
    public abstract void moveTo(final double p0, final double p1);
    
    public abstract void lineTo(final double p0, final double p1);
    
    public abstract void quadTo(final double p0, final double p1, final double p2, final double p3);
    
    public abstract void curveTo(final double p0, final double p1, final double p2, final double p3, final double p4, final double p5);
    
    public final synchronized void closePath() {
        if (this.numTypes == 0 || this.pointTypes[this.numTypes - 1] != 4) {
            this.needRoom(true, 0);
            this.pointTypes[this.numTypes++] = 4;
        }
    }
    
    public final void append(final Shape shape, final boolean b) {
        this.append(shape.getPathIterator(null), b);
    }
    
    public abstract void append(final PathIterator p0, final boolean p1);
    
    public final synchronized int getWindingRule() {
        return this.windingRule;
    }
    
    public final void setWindingRule(final int windingRule) {
        if (windingRule != 0 && windingRule != 1) {
            throw new IllegalArgumentException("winding rule must be WIND_EVEN_ODD or WIND_NON_ZERO");
        }
        this.windingRule = windingRule;
    }
    
    public final synchronized Point2D getCurrentPoint() {
        int numCoords = this.numCoords;
        if (this.numTypes < 1 || numCoords < 1) {
            return null;
        }
        Label_0115: {
            if (this.pointTypes[this.numTypes - 1] == 4) {
                for (int i = this.numTypes - 2; i > 0; --i) {
                    switch (this.pointTypes[i]) {
                        case 0: {
                            break Label_0115;
                        }
                        case 1: {
                            numCoords -= 2;
                            break;
                        }
                        case 2: {
                            numCoords -= 4;
                            break;
                        }
                        case 3: {
                            numCoords -= 6;
                            break;
                        }
                    }
                }
            }
        }
        return this.getPoint(numCoords - 2);
    }
    
    public final synchronized void reset() {
        final int n = 0;
        this.numCoords = n;
        this.numTypes = n;
    }
    
    public abstract void transform(final AffineTransform p0);
    
    public final synchronized Shape createTransformedShape(final AffineTransform affineTransform) {
        final Path2D path2D = (Path2D)this.clone();
        if (affineTransform != null) {
            path2D.transform(affineTransform);
        }
        return path2D;
    }
    
    @Override
    public final Rectangle getBounds() {
        return this.getBounds2D().getBounds();
    }
    
    public static boolean contains(final PathIterator pathIterator, final double n, final double n2) {
        return n * 0.0 + n2 * 0.0 == 0.0 && (Curve.pointCrossingsForPath(pathIterator, n, n2) & ((pathIterator.getWindingRule() == 1) ? -1 : 1)) != 0x0;
    }
    
    public static boolean contains(final PathIterator pathIterator, final Point2D point2D) {
        return contains(pathIterator, point2D.getX(), point2D.getY());
    }
    
    @Override
    public final boolean contains(final double n, final double n2) {
        return n * 0.0 + n2 * 0.0 == 0.0 && this.numTypes >= 2 && (this.pointCrossings(n, n2) & ((this.windingRule == 1) ? -1 : 1)) != 0x0;
    }
    
    @Override
    public final boolean contains(final Point2D point2D) {
        return this.contains(point2D.getX(), point2D.getY());
    }
    
    public static boolean contains(final PathIterator pathIterator, final double n, final double n2, final double n3, final double n4) {
        if (java.lang.Double.isNaN(n + n3) || java.lang.Double.isNaN(n2 + n4)) {
            return false;
        }
        if (n3 <= 0.0 || n4 <= 0.0) {
            return false;
        }
        final int n5 = (pathIterator.getWindingRule() == 1) ? -1 : 2;
        final int rectCrossingsForPath = Curve.rectCrossingsForPath(pathIterator, n, n2, n + n3, n2 + n4);
        return rectCrossingsForPath != Integer.MIN_VALUE && (rectCrossingsForPath & n5) != 0x0;
    }
    
    public static boolean contains(final PathIterator pathIterator, final Rectangle2D rectangle2D) {
        return contains(pathIterator, rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    @Override
    public final boolean contains(final double n, final double n2, final double n3, final double n4) {
        if (java.lang.Double.isNaN(n + n3) || java.lang.Double.isNaN(n2 + n4)) {
            return false;
        }
        if (n3 <= 0.0 || n4 <= 0.0) {
            return false;
        }
        final int n5 = (this.windingRule == 1) ? -1 : 2;
        final int rectCrossings = this.rectCrossings(n, n2, n + n3, n2 + n4);
        return rectCrossings != Integer.MIN_VALUE && (rectCrossings & n5) != 0x0;
    }
    
    @Override
    public final boolean contains(final Rectangle2D rectangle2D) {
        return this.contains(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    public static boolean intersects(final PathIterator pathIterator, final double n, final double n2, final double n3, final double n4) {
        if (java.lang.Double.isNaN(n + n3) || java.lang.Double.isNaN(n2 + n4)) {
            return false;
        }
        if (n3 <= 0.0 || n4 <= 0.0) {
            return false;
        }
        final int n5 = (pathIterator.getWindingRule() == 1) ? -1 : 2;
        final int rectCrossingsForPath = Curve.rectCrossingsForPath(pathIterator, n, n2, n + n3, n2 + n4);
        return rectCrossingsForPath == Integer.MIN_VALUE || (rectCrossingsForPath & n5) != 0x0;
    }
    
    public static boolean intersects(final PathIterator pathIterator, final Rectangle2D rectangle2D) {
        return intersects(pathIterator, rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    @Override
    public final boolean intersects(final double n, final double n2, final double n3, final double n4) {
        if (java.lang.Double.isNaN(n + n3) || java.lang.Double.isNaN(n2 + n4)) {
            return false;
        }
        if (n3 <= 0.0 || n4 <= 0.0) {
            return false;
        }
        final int n5 = (this.windingRule == 1) ? -1 : 2;
        final int rectCrossings = this.rectCrossings(n, n2, n + n3, n2 + n4);
        return rectCrossings == Integer.MIN_VALUE || (rectCrossings & n5) != 0x0;
    }
    
    @Override
    public final boolean intersects(final Rectangle2D rectangle2D) {
        return this.intersects(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    @Override
    public final PathIterator getPathIterator(final AffineTransform affineTransform, final double n) {
        return new FlatteningPathIterator(this.getPathIterator(affineTransform), n);
    }
    
    public abstract Object clone();
    
    final void writeObject(final ObjectOutputStream objectOutputStream, final boolean b) throws IOException {
        objectOutputStream.defaultWriteObject();
        double[] doubleCoords;
        float[] floatCoords;
        if (b) {
            doubleCoords = ((Double)this).doubleCoords;
            floatCoords = null;
        }
        else {
            floatCoords = ((Float)this).floatCoords;
            doubleCoords = null;
        }
        final int numTypes = this.numTypes;
        objectOutputStream.writeByte(b ? 49 : 48);
        objectOutputStream.writeInt(numTypes);
        objectOutputStream.writeInt(this.numCoords);
        objectOutputStream.writeByte((byte)this.windingRule);
        int n = 0;
        for (int i = 0; i < numTypes; ++i) {
            int n2 = 0;
            int n3 = 0;
            switch (this.pointTypes[i]) {
                case 0: {
                    n2 = 1;
                    n3 = (b ? 80 : 64);
                    break;
                }
                case 1: {
                    n2 = 1;
                    n3 = (b ? 81 : 65);
                    break;
                }
                case 2: {
                    n2 = 2;
                    n3 = (b ? 82 : 66);
                    break;
                }
                case 3: {
                    n2 = 3;
                    n3 = (b ? 83 : 67);
                    break;
                }
                case 4: {
                    n2 = 0;
                    n3 = 96;
                    break;
                }
                default: {
                    throw new InternalError("unrecognized path type");
                }
            }
            objectOutputStream.writeByte(n3);
            while (--n2 >= 0) {
                if (b) {
                    objectOutputStream.writeDouble(doubleCoords[n++]);
                    objectOutputStream.writeDouble(doubleCoords[n++]);
                }
                else {
                    objectOutputStream.writeFloat(floatCoords[n++]);
                    objectOutputStream.writeFloat(floatCoords[n++]);
                }
            }
        }
        objectOutputStream.writeByte(97);
    }
    
    final void readObject(final ObjectInputStream objectInputStream, final boolean b) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        objectInputStream.readByte();
        final int int1 = objectInputStream.readInt();
        int int2 = objectInputStream.readInt();
        try {
            this.setWindingRule(objectInputStream.readByte());
        }
        catch (final IllegalArgumentException ex) {
            throw new InvalidObjectException(ex.getMessage());
        }
        this.pointTypes = new byte[(int1 < 0 || int1 > 20) ? 20 : int1];
        if (int2 < 0 || int2 > 40) {
            int2 = 40;
        }
        if (b) {
            ((Double)this).doubleCoords = new double[int2];
        }
        else {
            ((Float)this).floatCoords = new float[int2];
        }
    Label_0512:
        for (int n = 0; int1 < 0 || n < int1; ++n) {
            boolean b2 = false;
            int n2 = 0;
            byte b3 = 0;
            switch (objectInputStream.readByte()) {
                case 64: {
                    b2 = false;
                    n2 = 1;
                    b3 = 0;
                    break;
                }
                case 65: {
                    b2 = false;
                    n2 = 1;
                    b3 = 1;
                    break;
                }
                case 66: {
                    b2 = false;
                    n2 = 2;
                    b3 = 2;
                    break;
                }
                case 67: {
                    b2 = false;
                    n2 = 3;
                    b3 = 3;
                    break;
                }
                case 80: {
                    b2 = true;
                    n2 = 1;
                    b3 = 0;
                    break;
                }
                case 81: {
                    b2 = true;
                    n2 = 1;
                    b3 = 1;
                    break;
                }
                case 82: {
                    b2 = true;
                    n2 = 2;
                    b3 = 2;
                    break;
                }
                case 83: {
                    b2 = true;
                    n2 = 3;
                    b3 = 3;
                    break;
                }
                case 96: {
                    b2 = false;
                    n2 = 0;
                    b3 = 4;
                    break;
                }
                case 97: {
                    if (int1 < 0) {
                        break Label_0512;
                    }
                    throw new StreamCorruptedException("unexpected PATH_END");
                }
                default: {
                    throw new StreamCorruptedException("unrecognized path type");
                }
            }
            this.needRoom(b3 != 0, n2 * 2);
            if (b2) {
                while (--n2 >= 0) {
                    this.append(objectInputStream.readDouble(), objectInputStream.readDouble());
                }
            }
            else {
                while (--n2 >= 0) {
                    this.append(objectInputStream.readFloat(), objectInputStream.readFloat());
                }
            }
            this.pointTypes[this.numTypes++] = b3;
        }
        if (int1 >= 0 && objectInputStream.readByte() != 97) {
            throw new StreamCorruptedException("missing PATH_END");
        }
    }
    
    public static class Float extends Path2D implements Serializable
    {
        transient float[] floatCoords;
        private static final long serialVersionUID = 6990832515060788886L;
        
        public Float() {
            this(1, 20);
        }
        
        public Float(final int n) {
            this(n, 20);
        }
        
        public Float(final int n, final int n2) {
            super(n, n2);
            this.floatCoords = new float[n2 * 2];
        }
        
        public Float(final Shape shape) {
            this(shape, null);
        }
        
        public Float(final Shape shape, final AffineTransform affineTransform) {
            if (shape instanceof Path2D) {
                final Path2D path2D = (Path2D)shape;
                this.setWindingRule(path2D.windingRule);
                this.numTypes = path2D.numTypes;
                this.pointTypes = Arrays.copyOf(path2D.pointTypes, path2D.numTypes);
                this.numCoords = path2D.numCoords;
                this.floatCoords = path2D.cloneCoordsFloat(affineTransform);
            }
            else {
                final PathIterator pathIterator = shape.getPathIterator(affineTransform);
                this.setWindingRule(pathIterator.getWindingRule());
                this.pointTypes = new byte[20];
                this.floatCoords = new float[40];
                this.append(pathIterator, false);
            }
        }
        
        @Override
        float[] cloneCoordsFloat(final AffineTransform affineTransform) {
            float[] copy;
            if (affineTransform == null) {
                copy = Arrays.copyOf(this.floatCoords, this.numCoords);
            }
            else {
                copy = new float[this.numCoords];
                affineTransform.transform(this.floatCoords, 0, copy, 0, this.numCoords / 2);
            }
            return copy;
        }
        
        @Override
        double[] cloneCoordsDouble(final AffineTransform affineTransform) {
            final double[] array = new double[this.numCoords];
            if (affineTransform == null) {
                for (int i = 0; i < this.numCoords; ++i) {
                    array[i] = this.floatCoords[i];
                }
            }
            else {
                affineTransform.transform(this.floatCoords, 0, array, 0, this.numCoords / 2);
            }
            return array;
        }
        
        @Override
        void append(final float n, final float n2) {
            this.floatCoords[this.numCoords++] = n;
            this.floatCoords[this.numCoords++] = n2;
        }
        
        @Override
        void append(final double n, final double n2) {
            this.floatCoords[this.numCoords++] = (float)n;
            this.floatCoords[this.numCoords++] = (float)n2;
        }
        
        @Override
        Point2D getPoint(final int n) {
            return new Point2D.Float(this.floatCoords[n], this.floatCoords[n + 1]);
        }
        
        @Override
        void needRoom(final boolean b, final int n) {
            if (this.numTypes == 0 && b) {
                throw new IllegalPathStateException("missing initial moveto in path definition");
            }
            if (this.numTypes >= this.pointTypes.length) {
                this.pointTypes = Path2D.expandPointTypes(this.pointTypes, 1);
            }
            if (this.numCoords > this.floatCoords.length - n) {
                this.floatCoords = expandCoords(this.floatCoords, n);
            }
        }
        
        static float[] expandCoords(final float[] array, final int n) {
            final int length = array.length;
            final int n2 = length + n;
            if (n2 < length) {
                throw new ArrayIndexOutOfBoundsException("coords exceeds maximum capacity !");
            }
            int max = length;
            if (max > 1000) {
                max = Math.max(1000, length >> 3);
            }
            else if (max < 10) {
                max = 10;
            }
            assert max > n;
            int n3 = length + max;
            if (n3 < n2) {
                n3 = Integer.MAX_VALUE;
            }
            try {
                return Arrays.copyOf(array, n3);
            }
            catch (final OutOfMemoryError outOfMemoryError) {
                if (n3 == n2) {
                    throw outOfMemoryError;
                }
                n3 = n2 + (n3 - n2) / 2;
                return Arrays.copyOf(array, n3);
            }
        }
        
        @Override
        public final synchronized void moveTo(final double n, final double n2) {
            if (this.numTypes > 0 && this.pointTypes[this.numTypes - 1] == 0) {
                this.floatCoords[this.numCoords - 2] = (float)n;
                this.floatCoords[this.numCoords - 1] = (float)n2;
            }
            else {
                this.needRoom(false, 2);
                this.pointTypes[this.numTypes++] = 0;
                this.floatCoords[this.numCoords++] = (float)n;
                this.floatCoords[this.numCoords++] = (float)n2;
            }
        }
        
        public final synchronized void moveTo(final float n, final float n2) {
            if (this.numTypes > 0 && this.pointTypes[this.numTypes - 1] == 0) {
                this.floatCoords[this.numCoords - 2] = n;
                this.floatCoords[this.numCoords - 1] = n2;
            }
            else {
                this.needRoom(false, 2);
                this.pointTypes[this.numTypes++] = 0;
                this.floatCoords[this.numCoords++] = n;
                this.floatCoords[this.numCoords++] = n2;
            }
        }
        
        @Override
        public final synchronized void lineTo(final double n, final double n2) {
            this.needRoom(true, 2);
            this.pointTypes[this.numTypes++] = 1;
            this.floatCoords[this.numCoords++] = (float)n;
            this.floatCoords[this.numCoords++] = (float)n2;
        }
        
        public final synchronized void lineTo(final float n, final float n2) {
            this.needRoom(true, 2);
            this.pointTypes[this.numTypes++] = 1;
            this.floatCoords[this.numCoords++] = n;
            this.floatCoords[this.numCoords++] = n2;
        }
        
        @Override
        public final synchronized void quadTo(final double n, final double n2, final double n3, final double n4) {
            this.needRoom(true, 4);
            this.pointTypes[this.numTypes++] = 2;
            this.floatCoords[this.numCoords++] = (float)n;
            this.floatCoords[this.numCoords++] = (float)n2;
            this.floatCoords[this.numCoords++] = (float)n3;
            this.floatCoords[this.numCoords++] = (float)n4;
        }
        
        public final synchronized void quadTo(final float n, final float n2, final float n3, final float n4) {
            this.needRoom(true, 4);
            this.pointTypes[this.numTypes++] = 2;
            this.floatCoords[this.numCoords++] = n;
            this.floatCoords[this.numCoords++] = n2;
            this.floatCoords[this.numCoords++] = n3;
            this.floatCoords[this.numCoords++] = n4;
        }
        
        @Override
        public final synchronized void curveTo(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            this.needRoom(true, 6);
            this.pointTypes[this.numTypes++] = 3;
            this.floatCoords[this.numCoords++] = (float)n;
            this.floatCoords[this.numCoords++] = (float)n2;
            this.floatCoords[this.numCoords++] = (float)n3;
            this.floatCoords[this.numCoords++] = (float)n4;
            this.floatCoords[this.numCoords++] = (float)n5;
            this.floatCoords[this.numCoords++] = (float)n6;
        }
        
        public final synchronized void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.needRoom(true, 6);
            this.pointTypes[this.numTypes++] = 3;
            this.floatCoords[this.numCoords++] = n;
            this.floatCoords[this.numCoords++] = n2;
            this.floatCoords[this.numCoords++] = n3;
            this.floatCoords[this.numCoords++] = n4;
            this.floatCoords[this.numCoords++] = n5;
            this.floatCoords[this.numCoords++] = n6;
        }
        
        @Override
        int pointCrossings(final double n, final double n2) {
            if (this.numTypes == 0) {
                return 0;
            }
            final float[] floatCoords = this.floatCoords;
            double n4;
            double n3 = n4 = floatCoords[0];
            double n6;
            double n5 = n6 = floatCoords[1];
            int n7 = 0;
            int n8 = 2;
            for (int i = 1; i < this.numTypes; ++i) {
                switch (this.pointTypes[i]) {
                    case 0: {
                        if (n6 != n5) {
                            n7 += Curve.pointCrossingsForLine(n, n2, n4, n6, n3, n5);
                        }
                        n4 = (n3 = floatCoords[n8++]);
                        n6 = (n5 = floatCoords[n8++]);
                        break;
                    }
                    case 1: {
                        final int n9 = n7;
                        final double n10 = n4;
                        final double n11 = n6;
                        final double n12 = floatCoords[n8++];
                        final double n13;
                        n7 = n9 + Curve.pointCrossingsForLine(n, n2, n10, n11, n12, n13 = floatCoords[n8++]);
                        n4 = n12;
                        n6 = n13;
                        break;
                    }
                    case 2: {
                        final int n14 = n7;
                        final double n15 = n4;
                        final double n16 = n6;
                        final double n17 = floatCoords[n8++];
                        final double n18 = floatCoords[n8++];
                        final double n19 = floatCoords[n8++];
                        final double n20;
                        n7 = n14 + Curve.pointCrossingsForQuad(n, n2, n15, n16, n17, n18, n19, n20 = floatCoords[n8++], 0);
                        n4 = n19;
                        n6 = n20;
                        break;
                    }
                    case 3: {
                        final int n21 = n7;
                        final double n22 = n4;
                        final double n23 = n6;
                        final double n24 = floatCoords[n8++];
                        final double n25 = floatCoords[n8++];
                        final double n26 = floatCoords[n8++];
                        final double n27 = floatCoords[n8++];
                        final double n28 = floatCoords[n8++];
                        final double n29;
                        n7 = n21 + Curve.pointCrossingsForCubic(n, n2, n22, n23, n24, n25, n26, n27, n28, n29 = floatCoords[n8++], 0);
                        n4 = n28;
                        n6 = n29;
                        break;
                    }
                    case 4: {
                        if (n6 != n5) {
                            n7 += Curve.pointCrossingsForLine(n, n2, n4, n6, n3, n5);
                        }
                        n4 = n3;
                        n6 = n5;
                        break;
                    }
                }
            }
            if (n6 != n5) {
                n7 += Curve.pointCrossingsForLine(n, n2, n4, n6, n3, n5);
            }
            return n7;
        }
        
        @Override
        int rectCrossings(final double n, final double n2, final double n3, final double n4) {
            if (this.numTypes == 0) {
                return 0;
            }
            final float[] floatCoords = this.floatCoords;
            double n6;
            double n5 = n6 = floatCoords[0];
            double n8;
            double n7 = n8 = floatCoords[1];
            int n9 = 0;
            int n10 = 2;
            for (int n11 = 1; n9 != Integer.MIN_VALUE && n11 < this.numTypes; ++n11) {
                switch (this.pointTypes[n11]) {
                    case 0: {
                        if (n6 != n5 || n8 != n7) {
                            n9 = Curve.rectCrossingsForLine(n9, n, n2, n3, n4, n6, n8, n5, n7);
                        }
                        n6 = (n5 = floatCoords[n10++]);
                        n8 = (n7 = floatCoords[n10++]);
                        break;
                    }
                    case 1: {
                        final int n12 = n9;
                        final double n13 = n6;
                        final double n14 = n8;
                        final double n15 = floatCoords[n10++];
                        final double n16;
                        n9 = Curve.rectCrossingsForLine(n12, n, n2, n3, n4, n13, n14, n15, n16 = floatCoords[n10++]);
                        n6 = n15;
                        n8 = n16;
                        break;
                    }
                    case 2: {
                        final int n17 = n9;
                        final double n18 = n6;
                        final double n19 = n8;
                        final double n20 = floatCoords[n10++];
                        final double n21 = floatCoords[n10++];
                        final double n22 = floatCoords[n10++];
                        final double n23;
                        n9 = Curve.rectCrossingsForQuad(n17, n, n2, n3, n4, n18, n19, n20, n21, n22, n23 = floatCoords[n10++], 0);
                        n6 = n22;
                        n8 = n23;
                        break;
                    }
                    case 3: {
                        final int n24 = n9;
                        final double n25 = n6;
                        final double n26 = n8;
                        final double n27 = floatCoords[n10++];
                        final double n28 = floatCoords[n10++];
                        final double n29 = floatCoords[n10++];
                        final double n30 = floatCoords[n10++];
                        final double n31 = floatCoords[n10++];
                        final double n32;
                        n9 = Curve.rectCrossingsForCubic(n24, n, n2, n3, n4, n25, n26, n27, n28, n29, n30, n31, n32 = floatCoords[n10++], 0);
                        n6 = n31;
                        n8 = n32;
                        break;
                    }
                    case 4: {
                        if (n6 != n5 || n8 != n7) {
                            n9 = Curve.rectCrossingsForLine(n9, n, n2, n3, n4, n6, n8, n5, n7);
                        }
                        n6 = n5;
                        n8 = n7;
                        break;
                    }
                }
            }
            if (n9 != Integer.MIN_VALUE && (n6 != n5 || n8 != n7)) {
                n9 = Curve.rectCrossingsForLine(n9, n, n2, n3, n4, n6, n8, n5, n7);
            }
            return n9;
        }
        
        @Override
        public final void append(final PathIterator pathIterator, boolean b) {
            final float[] array = new float[6];
            while (!pathIterator.isDone()) {
                switch (pathIterator.currentSegment(array)) {
                    case 0: {
                        if (!b || this.numTypes < 1 || this.numCoords < 1) {
                            this.moveTo(array[0], array[1]);
                            break;
                        }
                        if (this.pointTypes[this.numTypes - 1] != 4 && this.floatCoords[this.numCoords - 2] == array[0] && this.floatCoords[this.numCoords - 1] == array[1]) {
                            break;
                        }
                        this.lineTo(array[0], array[1]);
                        break;
                    }
                    case 1: {
                        this.lineTo(array[0], array[1]);
                        break;
                    }
                    case 2: {
                        this.quadTo(array[0], array[1], array[2], array[3]);
                        break;
                    }
                    case 3: {
                        this.curveTo(array[0], array[1], array[2], array[3], array[4], array[5]);
                        break;
                    }
                    case 4: {
                        this.closePath();
                        break;
                    }
                }
                pathIterator.next();
                b = false;
            }
        }
        
        @Override
        public final void transform(final AffineTransform affineTransform) {
            affineTransform.transform(this.floatCoords, 0, this.floatCoords, 0, this.numCoords / 2);
        }
        
        @Override
        public final synchronized Rectangle2D getBounds2D() {
            int i = this.numCoords;
            float n2;
            float n;
            float n4;
            float n3;
            if (i > 0) {
                n = (n2 = this.floatCoords[--i]);
                n3 = (n4 = this.floatCoords[--i]);
                while (i > 0) {
                    final float n5 = this.floatCoords[--i];
                    final float n6 = this.floatCoords[--i];
                    if (n6 < n4) {
                        n4 = n6;
                    }
                    if (n5 < n2) {
                        n2 = n5;
                    }
                    if (n6 > n3) {
                        n3 = n6;
                    }
                    if (n5 > n) {
                        n = n5;
                    }
                }
            }
            else {
                n2 = (n4 = (n3 = (n = 0.0f)));
            }
            return new Rectangle2D.Float(n4, n2, n3 - n4, n - n2);
        }
        
        @Override
        public final PathIterator getPathIterator(final AffineTransform affineTransform) {
            if (affineTransform == null) {
                return new CopyIterator(this);
            }
            return new TxIterator(this, affineTransform);
        }
        
        @Override
        public final Object clone() {
            if (this instanceof GeneralPath) {
                return new GeneralPath(this);
            }
            return new Float(this);
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            super.writeObject(objectOutputStream, false);
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
            super.readObject(objectInputStream, false);
        }
        
        static class CopyIterator extends Iterator
        {
            float[] floatCoords;
            
            CopyIterator(final Float float1) {
                super(float1);
                this.floatCoords = float1.floatCoords;
            }
            
            @Override
            public int currentSegment(final float[] array) {
                final byte b = this.path.pointTypes[this.typeIdx];
                final int n = CopyIterator.curvecoords[b];
                if (n > 0) {
                    System.arraycopy(this.floatCoords, this.pointIdx, array, 0, n);
                }
                return b;
            }
            
            @Override
            public int currentSegment(final double[] array) {
                final byte b = this.path.pointTypes[this.typeIdx];
                final int n = CopyIterator.curvecoords[b];
                if (n > 0) {
                    for (int i = 0; i < n; ++i) {
                        array[i] = this.floatCoords[this.pointIdx + i];
                    }
                }
                return b;
            }
        }
        
        static class TxIterator extends Iterator
        {
            float[] floatCoords;
            AffineTransform affine;
            
            TxIterator(final Float float1, final AffineTransform affine) {
                super(float1);
                this.floatCoords = float1.floatCoords;
                this.affine = affine;
            }
            
            @Override
            public int currentSegment(final float[] array) {
                final byte b = this.path.pointTypes[this.typeIdx];
                final int n = TxIterator.curvecoords[b];
                if (n > 0) {
                    this.affine.transform(this.floatCoords, this.pointIdx, array, 0, n / 2);
                }
                return b;
            }
            
            @Override
            public int currentSegment(final double[] array) {
                final byte b = this.path.pointTypes[this.typeIdx];
                final int n = TxIterator.curvecoords[b];
                if (n > 0) {
                    this.affine.transform(this.floatCoords, this.pointIdx, array, 0, n / 2);
                }
                return b;
            }
        }
    }
    
    public static class Double extends Path2D implements Serializable
    {
        transient double[] doubleCoords;
        private static final long serialVersionUID = 1826762518450014216L;
        
        public Double() {
            this(1, 20);
        }
        
        public Double(final int n) {
            this(n, 20);
        }
        
        public Double(final int n, final int n2) {
            super(n, n2);
            this.doubleCoords = new double[n2 * 2];
        }
        
        public Double(final Shape shape) {
            this(shape, null);
        }
        
        public Double(final Shape shape, final AffineTransform affineTransform) {
            if (shape instanceof Path2D) {
                final Path2D path2D = (Path2D)shape;
                this.setWindingRule(path2D.windingRule);
                this.numTypes = path2D.numTypes;
                this.pointTypes = Arrays.copyOf(path2D.pointTypes, path2D.numTypes);
                this.numCoords = path2D.numCoords;
                this.doubleCoords = path2D.cloneCoordsDouble(affineTransform);
            }
            else {
                final PathIterator pathIterator = shape.getPathIterator(affineTransform);
                this.setWindingRule(pathIterator.getWindingRule());
                this.pointTypes = new byte[20];
                this.doubleCoords = new double[40];
                this.append(pathIterator, false);
            }
        }
        
        @Override
        float[] cloneCoordsFloat(final AffineTransform affineTransform) {
            final float[] array = new float[this.numCoords];
            if (affineTransform == null) {
                for (int i = 0; i < this.numCoords; ++i) {
                    array[i] = (float)this.doubleCoords[i];
                }
            }
            else {
                affineTransform.transform(this.doubleCoords, 0, array, 0, this.numCoords / 2);
            }
            return array;
        }
        
        @Override
        double[] cloneCoordsDouble(final AffineTransform affineTransform) {
            double[] copy;
            if (affineTransform == null) {
                copy = Arrays.copyOf(this.doubleCoords, this.numCoords);
            }
            else {
                copy = new double[this.numCoords];
                affineTransform.transform(this.doubleCoords, 0, copy, 0, this.numCoords / 2);
            }
            return copy;
        }
        
        @Override
        void append(final float n, final float n2) {
            this.doubleCoords[this.numCoords++] = n;
            this.doubleCoords[this.numCoords++] = n2;
        }
        
        @Override
        void append(final double n, final double n2) {
            this.doubleCoords[this.numCoords++] = n;
            this.doubleCoords[this.numCoords++] = n2;
        }
        
        @Override
        Point2D getPoint(final int n) {
            return new Point2D.Double(this.doubleCoords[n], this.doubleCoords[n + 1]);
        }
        
        @Override
        void needRoom(final boolean b, final int n) {
            if (this.numTypes == 0 && b) {
                throw new IllegalPathStateException("missing initial moveto in path definition");
            }
            if (this.numTypes >= this.pointTypes.length) {
                this.pointTypes = Path2D.expandPointTypes(this.pointTypes, 1);
            }
            if (this.numCoords > this.doubleCoords.length - n) {
                this.doubleCoords = expandCoords(this.doubleCoords, n);
            }
        }
        
        static double[] expandCoords(final double[] array, final int n) {
            final int length = array.length;
            final int n2 = length + n;
            if (n2 < length) {
                throw new ArrayIndexOutOfBoundsException("coords exceeds maximum capacity !");
            }
            int max = length;
            if (max > 1000) {
                max = Math.max(1000, length >> 3);
            }
            else if (max < 10) {
                max = 10;
            }
            assert max > n;
            int n3 = length + max;
            if (n3 < n2) {
                n3 = Integer.MAX_VALUE;
            }
            try {
                return Arrays.copyOf(array, n3);
            }
            catch (final OutOfMemoryError outOfMemoryError) {
                if (n3 == n2) {
                    throw outOfMemoryError;
                }
                n3 = n2 + (n3 - n2) / 2;
                return Arrays.copyOf(array, n3);
            }
        }
        
        @Override
        public final synchronized void moveTo(final double n, final double n2) {
            if (this.numTypes > 0 && this.pointTypes[this.numTypes - 1] == 0) {
                this.doubleCoords[this.numCoords - 2] = n;
                this.doubleCoords[this.numCoords - 1] = n2;
            }
            else {
                this.needRoom(false, 2);
                this.pointTypes[this.numTypes++] = 0;
                this.doubleCoords[this.numCoords++] = n;
                this.doubleCoords[this.numCoords++] = n2;
            }
        }
        
        @Override
        public final synchronized void lineTo(final double n, final double n2) {
            this.needRoom(true, 2);
            this.pointTypes[this.numTypes++] = 1;
            this.doubleCoords[this.numCoords++] = n;
            this.doubleCoords[this.numCoords++] = n2;
        }
        
        @Override
        public final synchronized void quadTo(final double n, final double n2, final double n3, final double n4) {
            this.needRoom(true, 4);
            this.pointTypes[this.numTypes++] = 2;
            this.doubleCoords[this.numCoords++] = n;
            this.doubleCoords[this.numCoords++] = n2;
            this.doubleCoords[this.numCoords++] = n3;
            this.doubleCoords[this.numCoords++] = n4;
        }
        
        @Override
        public final synchronized void curveTo(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            this.needRoom(true, 6);
            this.pointTypes[this.numTypes++] = 3;
            this.doubleCoords[this.numCoords++] = n;
            this.doubleCoords[this.numCoords++] = n2;
            this.doubleCoords[this.numCoords++] = n3;
            this.doubleCoords[this.numCoords++] = n4;
            this.doubleCoords[this.numCoords++] = n5;
            this.doubleCoords[this.numCoords++] = n6;
        }
        
        @Override
        int pointCrossings(final double n, final double n2) {
            if (this.numTypes == 0) {
                return 0;
            }
            final double[] doubleCoords = this.doubleCoords;
            double n4;
            double n3 = n4 = doubleCoords[0];
            double n6;
            double n5 = n6 = doubleCoords[1];
            int n7 = 0;
            int n8 = 2;
            for (int i = 1; i < this.numTypes; ++i) {
                switch (this.pointTypes[i]) {
                    case 0: {
                        if (n6 != n5) {
                            n7 += Curve.pointCrossingsForLine(n, n2, n4, n6, n3, n5);
                        }
                        n4 = (n3 = doubleCoords[n8++]);
                        n6 = (n5 = doubleCoords[n8++]);
                        break;
                    }
                    case 1: {
                        final int n9 = n7;
                        final double n10 = n4;
                        final double n11 = n6;
                        final double n12 = doubleCoords[n8++];
                        final double n13;
                        n7 = n9 + Curve.pointCrossingsForLine(n, n2, n10, n11, n12, n13 = doubleCoords[n8++]);
                        n4 = n12;
                        n6 = n13;
                        break;
                    }
                    case 2: {
                        final int n14 = n7;
                        final double n15 = n4;
                        final double n16 = n6;
                        final double n17 = doubleCoords[n8++];
                        final double n18 = doubleCoords[n8++];
                        final double n19 = doubleCoords[n8++];
                        final double n20;
                        n7 = n14 + Curve.pointCrossingsForQuad(n, n2, n15, n16, n17, n18, n19, n20 = doubleCoords[n8++], 0);
                        n4 = n19;
                        n6 = n20;
                        break;
                    }
                    case 3: {
                        final int n21 = n7;
                        final double n22 = n4;
                        final double n23 = n6;
                        final double n24 = doubleCoords[n8++];
                        final double n25 = doubleCoords[n8++];
                        final double n26 = doubleCoords[n8++];
                        final double n27 = doubleCoords[n8++];
                        final double n28 = doubleCoords[n8++];
                        final double n29;
                        n7 = n21 + Curve.pointCrossingsForCubic(n, n2, n22, n23, n24, n25, n26, n27, n28, n29 = doubleCoords[n8++], 0);
                        n4 = n28;
                        n6 = n29;
                        break;
                    }
                    case 4: {
                        if (n6 != n5) {
                            n7 += Curve.pointCrossingsForLine(n, n2, n4, n6, n3, n5);
                        }
                        n4 = n3;
                        n6 = n5;
                        break;
                    }
                }
            }
            if (n6 != n5) {
                n7 += Curve.pointCrossingsForLine(n, n2, n4, n6, n3, n5);
            }
            return n7;
        }
        
        @Override
        int rectCrossings(final double n, final double n2, final double n3, final double n4) {
            if (this.numTypes == 0) {
                return 0;
            }
            final double[] doubleCoords = this.doubleCoords;
            double n6;
            double n5 = n6 = doubleCoords[0];
            double n8;
            double n7 = n8 = doubleCoords[1];
            int n9 = 0;
            int n10 = 2;
            for (int n11 = 1; n9 != Integer.MIN_VALUE && n11 < this.numTypes; ++n11) {
                switch (this.pointTypes[n11]) {
                    case 0: {
                        if (n6 != n5 || n8 != n7) {
                            n9 = Curve.rectCrossingsForLine(n9, n, n2, n3, n4, n6, n8, n5, n7);
                        }
                        n6 = (n5 = doubleCoords[n10++]);
                        n8 = (n7 = doubleCoords[n10++]);
                        break;
                    }
                    case 1: {
                        final double n12 = doubleCoords[n10++];
                        final double n13 = doubleCoords[n10++];
                        n9 = Curve.rectCrossingsForLine(n9, n, n2, n3, n4, n6, n8, n12, n13);
                        n6 = n12;
                        n8 = n13;
                        break;
                    }
                    case 2: {
                        final int n14 = n9;
                        final double n15 = n6;
                        final double n16 = n8;
                        final double n17 = doubleCoords[n10++];
                        final double n18 = doubleCoords[n10++];
                        final double n19 = doubleCoords[n10++];
                        final double n20;
                        n9 = Curve.rectCrossingsForQuad(n14, n, n2, n3, n4, n15, n16, n17, n18, n19, n20 = doubleCoords[n10++], 0);
                        n6 = n19;
                        n8 = n20;
                        break;
                    }
                    case 3: {
                        final int n21 = n9;
                        final double n22 = n6;
                        final double n23 = n8;
                        final double n24 = doubleCoords[n10++];
                        final double n25 = doubleCoords[n10++];
                        final double n26 = doubleCoords[n10++];
                        final double n27 = doubleCoords[n10++];
                        final double n28 = doubleCoords[n10++];
                        final double n29;
                        n9 = Curve.rectCrossingsForCubic(n21, n, n2, n3, n4, n22, n23, n24, n25, n26, n27, n28, n29 = doubleCoords[n10++], 0);
                        n6 = n28;
                        n8 = n29;
                        break;
                    }
                    case 4: {
                        if (n6 != n5 || n8 != n7) {
                            n9 = Curve.rectCrossingsForLine(n9, n, n2, n3, n4, n6, n8, n5, n7);
                        }
                        n6 = n5;
                        n8 = n7;
                        break;
                    }
                }
            }
            if (n9 != Integer.MIN_VALUE && (n6 != n5 || n8 != n7)) {
                n9 = Curve.rectCrossingsForLine(n9, n, n2, n3, n4, n6, n8, n5, n7);
            }
            return n9;
        }
        
        @Override
        public final void append(final PathIterator pathIterator, boolean b) {
            final double[] array = new double[6];
            while (!pathIterator.isDone()) {
                switch (pathIterator.currentSegment(array)) {
                    case 0: {
                        if (!b || this.numTypes < 1 || this.numCoords < 1) {
                            this.moveTo(array[0], array[1]);
                            break;
                        }
                        if (this.pointTypes[this.numTypes - 1] != 4 && this.doubleCoords[this.numCoords - 2] == array[0] && this.doubleCoords[this.numCoords - 1] == array[1]) {
                            break;
                        }
                        this.lineTo(array[0], array[1]);
                        break;
                    }
                    case 1: {
                        this.lineTo(array[0], array[1]);
                        break;
                    }
                    case 2: {
                        this.quadTo(array[0], array[1], array[2], array[3]);
                        break;
                    }
                    case 3: {
                        this.curveTo(array[0], array[1], array[2], array[3], array[4], array[5]);
                        break;
                    }
                    case 4: {
                        this.closePath();
                        break;
                    }
                }
                pathIterator.next();
                b = false;
            }
        }
        
        @Override
        public final void transform(final AffineTransform affineTransform) {
            affineTransform.transform(this.doubleCoords, 0, this.doubleCoords, 0, this.numCoords / 2);
        }
        
        @Override
        public final synchronized Rectangle2D getBounds2D() {
            int i = this.numCoords;
            double n2;
            double n;
            double n4;
            double n3;
            if (i > 0) {
                n = (n2 = this.doubleCoords[--i]);
                n3 = (n4 = this.doubleCoords[--i]);
                while (i > 0) {
                    final double n5 = this.doubleCoords[--i];
                    final double n6 = this.doubleCoords[--i];
                    if (n6 < n4) {
                        n4 = n6;
                    }
                    if (n5 < n2) {
                        n2 = n5;
                    }
                    if (n6 > n3) {
                        n3 = n6;
                    }
                    if (n5 > n) {
                        n = n5;
                    }
                }
            }
            else {
                n2 = (n4 = (n3 = (n = 0.0)));
            }
            return new Rectangle2D.Double(n4, n2, n3 - n4, n - n2);
        }
        
        @Override
        public final PathIterator getPathIterator(final AffineTransform affineTransform) {
            if (affineTransform == null) {
                return new CopyIterator(this);
            }
            return new TxIterator(this, affineTransform);
        }
        
        @Override
        public final Object clone() {
            return new Double(this);
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            super.writeObject(objectOutputStream, true);
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
            super.readObject(objectInputStream, true);
        }
        
        static class CopyIterator extends Iterator
        {
            double[] doubleCoords;
            
            CopyIterator(final Double double1) {
                super(double1);
                this.doubleCoords = double1.doubleCoords;
            }
            
            @Override
            public int currentSegment(final float[] array) {
                final byte b = this.path.pointTypes[this.typeIdx];
                final int n = CopyIterator.curvecoords[b];
                if (n > 0) {
                    for (int i = 0; i < n; ++i) {
                        array[i] = (float)this.doubleCoords[this.pointIdx + i];
                    }
                }
                return b;
            }
            
            @Override
            public int currentSegment(final double[] array) {
                final byte b = this.path.pointTypes[this.typeIdx];
                final int n = CopyIterator.curvecoords[b];
                if (n > 0) {
                    System.arraycopy(this.doubleCoords, this.pointIdx, array, 0, n);
                }
                return b;
            }
        }
        
        static class TxIterator extends Iterator
        {
            double[] doubleCoords;
            AffineTransform affine;
            
            TxIterator(final Double double1, final AffineTransform affine) {
                super(double1);
                this.doubleCoords = double1.doubleCoords;
                this.affine = affine;
            }
            
            @Override
            public int currentSegment(final float[] array) {
                final byte b = this.path.pointTypes[this.typeIdx];
                final int n = TxIterator.curvecoords[b];
                if (n > 0) {
                    this.affine.transform(this.doubleCoords, this.pointIdx, array, 0, n / 2);
                }
                return b;
            }
            
            @Override
            public int currentSegment(final double[] array) {
                final byte b = this.path.pointTypes[this.typeIdx];
                final int n = TxIterator.curvecoords[b];
                if (n > 0) {
                    this.affine.transform(this.doubleCoords, this.pointIdx, array, 0, n / 2);
                }
                return b;
            }
        }
    }
    
    abstract static class Iterator implements PathIterator
    {
        int typeIdx;
        int pointIdx;
        Path2D path;
        static final int[] curvecoords;
        
        Iterator(final Path2D path) {
            this.path = path;
        }
        
        @Override
        public int getWindingRule() {
            return this.path.getWindingRule();
        }
        
        @Override
        public boolean isDone() {
            return this.typeIdx >= this.path.numTypes;
        }
        
        @Override
        public void next() {
            this.pointIdx += Iterator.curvecoords[this.path.pointTypes[this.typeIdx++]];
        }
        
        static {
            curvecoords = new int[] { 2, 2, 4, 6, 0 };
        }
    }
}
