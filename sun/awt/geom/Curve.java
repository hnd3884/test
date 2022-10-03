package sun.awt.geom;

import java.awt.geom.Rectangle2D;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.PathIterator;
import java.util.Vector;

public abstract class Curve
{
    public static final int INCREASING = 1;
    public static final int DECREASING = -1;
    protected int direction;
    public static final int RECT_INTERSECTS = Integer.MIN_VALUE;
    public static final double TMIN = 0.001;
    
    public static void insertMove(final Vector vector, final double n, final double n2) {
        vector.add(new Order0(n, n2));
    }
    
    public static void insertLine(final Vector vector, final double n, final double n2, final double n3, final double n4) {
        if (n2 < n4) {
            vector.add(new Order1(n, n2, n3, n4, 1));
        }
        else if (n2 > n4) {
            vector.add(new Order1(n3, n4, n, n2, -1));
        }
    }
    
    public static void insertQuad(final Vector vector, final double n, final double n2, final double[] array) {
        final double n3 = array[3];
        if (n2 > n3) {
            Order2.insert(vector, array, array[2], n3, array[0], array[1], n, n2, -1);
        }
        else {
            if (n2 == n3 && n2 == array[1]) {
                return;
            }
            Order2.insert(vector, array, n, n2, array[0], array[1], array[2], n3, 1);
        }
    }
    
    public static void insertCubic(final Vector vector, final double n, final double n2, final double[] array) {
        final double n3 = array[5];
        if (n2 > n3) {
            Order3.insert(vector, array, array[4], n3, array[2], array[3], array[0], array[1], n, n2, -1);
        }
        else {
            if (n2 == n3 && n2 == array[1] && n2 == array[3]) {
                return;
            }
            Order3.insert(vector, array, n, n2, array[0], array[1], array[2], array[3], array[4], n3, 1);
        }
    }
    
    public static int pointCrossingsForPath(final PathIterator pathIterator, final double n, final double n2) {
        if (pathIterator.isDone()) {
            return 0;
        }
        final double[] array = new double[6];
        if (pathIterator.currentSegment(array) != 0) {
            throw new IllegalPathStateException("missing initial moveto in path definition");
        }
        pathIterator.next();
        double n3 = array[0];
        double n4 = array[1];
        double n5 = n3;
        double n6 = n4;
        int n7 = 0;
        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(array)) {
                case 0: {
                    if (n6 != n4) {
                        n7 += pointCrossingsForLine(n, n2, n5, n6, n3, n4);
                    }
                    n5 = (n3 = array[0]);
                    n6 = (n4 = array[1]);
                    break;
                }
                case 1: {
                    final double n8 = array[0];
                    final double n9 = array[1];
                    n7 += pointCrossingsForLine(n, n2, n5, n6, n8, n9);
                    n5 = n8;
                    n6 = n9;
                    break;
                }
                case 2: {
                    final double n10 = array[2];
                    final double n11 = array[3];
                    n7 += pointCrossingsForQuad(n, n2, n5, n6, array[0], array[1], n10, n11, 0);
                    n5 = n10;
                    n6 = n11;
                    break;
                }
                case 3: {
                    final double n12 = array[4];
                    final double n13 = array[5];
                    n7 += pointCrossingsForCubic(n, n2, n5, n6, array[0], array[1], array[2], array[3], n12, n13, 0);
                    n5 = n12;
                    n6 = n13;
                    break;
                }
                case 4: {
                    if (n6 != n4) {
                        n7 += pointCrossingsForLine(n, n2, n5, n6, n3, n4);
                    }
                    n5 = n3;
                    n6 = n4;
                    break;
                }
            }
            pathIterator.next();
        }
        if (n6 != n4) {
            n7 += pointCrossingsForLine(n, n2, n5, n6, n3, n4);
        }
        return n7;
    }
    
    public static int pointCrossingsForLine(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        if (n2 < n4 && n2 < n6) {
            return 0;
        }
        if (n2 >= n4 && n2 >= n6) {
            return 0;
        }
        if (n >= n3 && n >= n5) {
            return 0;
        }
        if (n < n3 && n < n5) {
            return (n4 < n6) ? 1 : -1;
        }
        if (n >= n3 + (n2 - n4) * (n5 - n3) / (n6 - n4)) {
            return 0;
        }
        return (n4 < n6) ? 1 : -1;
    }
    
    public static int pointCrossingsForQuad(final double n, final double n2, final double n3, final double n4, double n5, double n6, final double n7, final double n8, final int n9) {
        if (n2 < n4 && n2 < n6 && n2 < n8) {
            return 0;
        }
        if (n2 >= n4 && n2 >= n6 && n2 >= n8) {
            return 0;
        }
        if (n >= n3 && n >= n5 && n >= n7) {
            return 0;
        }
        if (n < n3 && n < n5 && n < n7) {
            if (n2 >= n4) {
                if (n2 < n8) {
                    return 1;
                }
            }
            else if (n2 >= n8) {
                return -1;
            }
            return 0;
        }
        if (n9 > 52) {
            return pointCrossingsForLine(n, n2, n3, n4, n7, n8);
        }
        final double n10 = (n3 + n5) / 2.0;
        final double n11 = (n4 + n6) / 2.0;
        final double n12 = (n5 + n7) / 2.0;
        final double n13 = (n6 + n8) / 2.0;
        n5 = (n10 + n12) / 2.0;
        n6 = (n11 + n13) / 2.0;
        if (Double.isNaN(n5) || Double.isNaN(n6)) {
            return 0;
        }
        return pointCrossingsForQuad(n, n2, n3, n4, n10, n11, n5, n6, n9 + 1) + pointCrossingsForQuad(n, n2, n5, n6, n12, n13, n7, n8, n9 + 1);
    }
    
    public static int pointCrossingsForCubic(final double n, final double n2, final double n3, final double n4, double n5, double n6, double n7, double n8, final double n9, final double n10, final int n11) {
        if (n2 < n4 && n2 < n6 && n2 < n8 && n2 < n10) {
            return 0;
        }
        if (n2 >= n4 && n2 >= n6 && n2 >= n8 && n2 >= n10) {
            return 0;
        }
        if (n >= n3 && n >= n5 && n >= n7 && n >= n9) {
            return 0;
        }
        if (n < n3 && n < n5 && n < n7 && n < n9) {
            if (n2 >= n4) {
                if (n2 < n10) {
                    return 1;
                }
            }
            else if (n2 >= n10) {
                return -1;
            }
            return 0;
        }
        if (n11 > 52) {
            return pointCrossingsForLine(n, n2, n3, n4, n9, n10);
        }
        final double n12 = (n5 + n7) / 2.0;
        final double n13 = (n6 + n8) / 2.0;
        n5 = (n3 + n5) / 2.0;
        n6 = (n4 + n6) / 2.0;
        n7 = (n7 + n9) / 2.0;
        n8 = (n8 + n10) / 2.0;
        final double n14 = (n5 + n12) / 2.0;
        final double n15 = (n6 + n13) / 2.0;
        final double n16 = (n12 + n7) / 2.0;
        final double n17 = (n13 + n8) / 2.0;
        final double n18 = (n14 + n16) / 2.0;
        final double n19 = (n15 + n17) / 2.0;
        if (Double.isNaN(n18) || Double.isNaN(n19)) {
            return 0;
        }
        return pointCrossingsForCubic(n, n2, n3, n4, n5, n6, n14, n15, n18, n19, n11 + 1) + pointCrossingsForCubic(n, n2, n18, n19, n16, n17, n7, n8, n9, n10, n11 + 1);
    }
    
    public static int rectCrossingsForPath(final PathIterator pathIterator, final double n, final double n2, final double n3, final double n4) {
        if (n3 <= n || n4 <= n2) {
            return 0;
        }
        if (pathIterator.isDone()) {
            return 0;
        }
        final double[] array = new double[6];
        if (pathIterator.currentSegment(array) != 0) {
            throw new IllegalPathStateException("missing initial moveto in path definition");
        }
        pathIterator.next();
        double n6;
        double n5 = n6 = array[0];
        double n8;
        double n7 = n8 = array[1];
        int n9 = 0;
        while (n9 != Integer.MIN_VALUE && !pathIterator.isDone()) {
            switch (pathIterator.currentSegment(array)) {
                case 0: {
                    if (n6 != n5 || n8 != n7) {
                        n9 = rectCrossingsForLine(n9, n, n2, n3, n4, n6, n8, n5, n7);
                    }
                    n6 = (n5 = array[0]);
                    n8 = (n7 = array[1]);
                    break;
                }
                case 1: {
                    final double n10 = array[0];
                    final double n11 = array[1];
                    n9 = rectCrossingsForLine(n9, n, n2, n3, n4, n6, n8, n10, n11);
                    n6 = n10;
                    n8 = n11;
                    break;
                }
                case 2: {
                    final double n12 = array[2];
                    final double n13 = array[3];
                    n9 = rectCrossingsForQuad(n9, n, n2, n3, n4, n6, n8, array[0], array[1], n12, n13, 0);
                    n6 = n12;
                    n8 = n13;
                    break;
                }
                case 3: {
                    final double n14 = array[4];
                    final double n15 = array[5];
                    n9 = rectCrossingsForCubic(n9, n, n2, n3, n4, n6, n8, array[0], array[1], array[2], array[3], n14, n15, 0);
                    n6 = n14;
                    n8 = n15;
                    break;
                }
                case 4: {
                    if (n6 != n5 || n8 != n7) {
                        n9 = rectCrossingsForLine(n9, n, n2, n3, n4, n6, n8, n5, n7);
                    }
                    n6 = n5;
                    n8 = n7;
                    break;
                }
            }
            pathIterator.next();
        }
        if (n9 != Integer.MIN_VALUE && (n6 != n5 || n8 != n7)) {
            n9 = rectCrossingsForLine(n9, n, n2, n3, n4, n6, n8, n5, n7);
        }
        return n9;
    }
    
    public static int rectCrossingsForLine(int n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9) {
        if (n7 >= n5 && n9 >= n5) {
            return n;
        }
        if (n7 <= n3 && n9 <= n3) {
            return n;
        }
        if (n6 <= n2 && n8 <= n2) {
            return n;
        }
        if (n6 >= n4 && n8 >= n4) {
            if (n7 < n9) {
                if (n7 <= n3) {
                    ++n;
                }
                if (n9 >= n5) {
                    ++n;
                }
            }
            else if (n9 < n7) {
                if (n9 <= n3) {
                    --n;
                }
                if (n7 >= n5) {
                    --n;
                }
            }
            return n;
        }
        if ((n6 > n2 && n6 < n4 && n7 > n3 && n7 < n5) || (n8 > n2 && n8 < n4 && n9 > n3 && n9 < n5)) {
            return Integer.MIN_VALUE;
        }
        double n10 = n6;
        if (n7 < n3) {
            n10 += (n3 - n7) * (n8 - n6) / (n9 - n7);
        }
        else if (n7 > n5) {
            n10 += (n5 - n7) * (n8 - n6) / (n9 - n7);
        }
        double n11 = n8;
        if (n9 < n3) {
            n11 += (n3 - n9) * (n6 - n8) / (n7 - n9);
        }
        else if (n9 > n5) {
            n11 += (n5 - n9) * (n6 - n8) / (n7 - n9);
        }
        if (n10 <= n2 && n11 <= n2) {
            return n;
        }
        if (n10 >= n4 && n11 >= n4) {
            if (n7 < n9) {
                if (n7 <= n3) {
                    ++n;
                }
                if (n9 >= n5) {
                    ++n;
                }
            }
            else if (n9 < n7) {
                if (n9 <= n3) {
                    --n;
                }
                if (n7 >= n5) {
                    --n;
                }
            }
            return n;
        }
        return Integer.MIN_VALUE;
    }
    
    public static int rectCrossingsForQuad(int n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, double n8, double n9, final double n10, final double n11, final int n12) {
        if (n7 >= n5 && n9 >= n5 && n11 >= n5) {
            return n;
        }
        if (n7 <= n3 && n9 <= n3 && n11 <= n3) {
            return n;
        }
        if (n6 <= n2 && n8 <= n2 && n10 <= n2) {
            return n;
        }
        if (n6 >= n4 && n8 >= n4 && n10 >= n4) {
            if (n7 < n11) {
                if (n7 <= n3 && n11 > n3) {
                    ++n;
                }
                if (n7 < n5 && n11 >= n5) {
                    ++n;
                }
            }
            else if (n11 < n7) {
                if (n11 <= n3 && n7 > n3) {
                    --n;
                }
                if (n11 < n5 && n7 >= n5) {
                    --n;
                }
            }
            return n;
        }
        if ((n6 < n4 && n6 > n2 && n7 < n5 && n7 > n3) || (n10 < n4 && n10 > n2 && n11 < n5 && n11 > n3)) {
            return Integer.MIN_VALUE;
        }
        if (n12 > 52) {
            return rectCrossingsForLine(n, n2, n3, n4, n5, n6, n7, n10, n11);
        }
        final double n13 = (n6 + n8) / 2.0;
        final double n14 = (n7 + n9) / 2.0;
        final double n15 = (n8 + n10) / 2.0;
        final double n16 = (n9 + n11) / 2.0;
        n8 = (n13 + n15) / 2.0;
        n9 = (n14 + n16) / 2.0;
        if (Double.isNaN(n8) || Double.isNaN(n9)) {
            return 0;
        }
        n = rectCrossingsForQuad(n, n2, n3, n4, n5, n6, n7, n13, n14, n8, n9, n12 + 1);
        if (n != Integer.MIN_VALUE) {
            n = rectCrossingsForQuad(n, n2, n3, n4, n5, n8, n9, n15, n16, n10, n11, n12 + 1);
        }
        return n;
    }
    
    public static int rectCrossingsForCubic(int n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, double n8, double n9, double n10, double n11, final double n12, final double n13, final int n14) {
        if (n7 >= n5 && n9 >= n5 && n11 >= n5 && n13 >= n5) {
            return n;
        }
        if (n7 <= n3 && n9 <= n3 && n11 <= n3 && n13 <= n3) {
            return n;
        }
        if (n6 <= n2 && n8 <= n2 && n10 <= n2 && n12 <= n2) {
            return n;
        }
        if (n6 >= n4 && n8 >= n4 && n10 >= n4 && n12 >= n4) {
            if (n7 < n13) {
                if (n7 <= n3 && n13 > n3) {
                    ++n;
                }
                if (n7 < n5 && n13 >= n5) {
                    ++n;
                }
            }
            else if (n13 < n7) {
                if (n13 <= n3 && n7 > n3) {
                    --n;
                }
                if (n13 < n5 && n7 >= n5) {
                    --n;
                }
            }
            return n;
        }
        if ((n6 > n2 && n6 < n4 && n7 > n3 && n7 < n5) || (n12 > n2 && n12 < n4 && n13 > n3 && n13 < n5)) {
            return Integer.MIN_VALUE;
        }
        if (n14 > 52) {
            return rectCrossingsForLine(n, n2, n3, n4, n5, n6, n7, n12, n13);
        }
        final double n15 = (n8 + n10) / 2.0;
        final double n16 = (n9 + n11) / 2.0;
        n8 = (n6 + n8) / 2.0;
        n9 = (n7 + n9) / 2.0;
        n10 = (n10 + n12) / 2.0;
        n11 = (n11 + n13) / 2.0;
        final double n17 = (n8 + n15) / 2.0;
        final double n18 = (n9 + n16) / 2.0;
        final double n19 = (n15 + n10) / 2.0;
        final double n20 = (n16 + n11) / 2.0;
        final double n21 = (n17 + n19) / 2.0;
        final double n22 = (n18 + n20) / 2.0;
        if (Double.isNaN(n21) || Double.isNaN(n22)) {
            return 0;
        }
        n = rectCrossingsForCubic(n, n2, n3, n4, n5, n6, n7, n8, n9, n17, n18, n21, n22, n14 + 1);
        if (n != Integer.MIN_VALUE) {
            n = rectCrossingsForCubic(n, n2, n3, n4, n5, n21, n22, n19, n20, n10, n11, n12, n13, n14 + 1);
        }
        return n;
    }
    
    public Curve(final int direction) {
        this.direction = direction;
    }
    
    public final int getDirection() {
        return this.direction;
    }
    
    public final Curve getWithDirection(final int n) {
        return (this.direction == n) ? this : this.getReversedCurve();
    }
    
    public static double round(final double n) {
        return n;
    }
    
    public static int orderof(final double n, final double n2) {
        if (n < n2) {
            return -1;
        }
        if (n > n2) {
            return 1;
        }
        return 0;
    }
    
    public static long signeddiffbits(final double n, final double n2) {
        return Double.doubleToLongBits(n) - Double.doubleToLongBits(n2);
    }
    
    public static long diffbits(final double n, final double n2) {
        return Math.abs(Double.doubleToLongBits(n) - Double.doubleToLongBits(n2));
    }
    
    public static double prev(final double n) {
        return Double.longBitsToDouble(Double.doubleToLongBits(n) - 1L);
    }
    
    public static double next(final double n) {
        return Double.longBitsToDouble(Double.doubleToLongBits(n) + 1L);
    }
    
    @Override
    public String toString() {
        return "Curve[" + this.getOrder() + ", " + "(" + round(this.getX0()) + ", " + round(this.getY0()) + "), " + this.controlPointString() + "(" + round(this.getX1()) + ", " + round(this.getY1()) + "), " + ((this.direction == 1) ? "D" : "U") + "]";
    }
    
    public String controlPointString() {
        return "";
    }
    
    public abstract int getOrder();
    
    public abstract double getXTop();
    
    public abstract double getYTop();
    
    public abstract double getXBot();
    
    public abstract double getYBot();
    
    public abstract double getXMin();
    
    public abstract double getXMax();
    
    public abstract double getX0();
    
    public abstract double getY0();
    
    public abstract double getX1();
    
    public abstract double getY1();
    
    public abstract double XforY(final double p0);
    
    public abstract double TforY(final double p0);
    
    public abstract double XforT(final double p0);
    
    public abstract double YforT(final double p0);
    
    public abstract double dXforT(final double p0, final int p1);
    
    public abstract double dYforT(final double p0, final int p1);
    
    public abstract double nextVertical(final double p0, final double p1);
    
    public int crossingsFor(final double n, final double n2) {
        if (n2 >= this.getYTop() && n2 < this.getYBot() && n < this.getXMax() && (n < this.getXMin() || n < this.XforY(n2))) {
            return 1;
        }
        return 0;
    }
    
    public boolean accumulateCrossings(final Crossings crossings) {
        final double xHi = crossings.getXHi();
        if (this.getXMin() >= xHi) {
            return false;
        }
        final double xLo = crossings.getXLo();
        final double yLo = crossings.getYLo();
        final double yHi = crossings.getYHi();
        final double yTop = this.getYTop();
        final double yBot = this.getYBot();
        double n;
        double n2;
        if (yTop < yLo) {
            if (yBot <= yLo) {
                return false;
            }
            n = yLo;
            n2 = this.TforY(yLo);
        }
        else {
            if (yTop >= yHi) {
                return false;
            }
            n = yTop;
            n2 = 0.0;
        }
        double n3;
        double tforY;
        if (yBot > yHi) {
            n3 = yHi;
            tforY = this.TforY(yHi);
        }
        else {
            n3 = yBot;
            tforY = 1.0;
        }
        boolean b = false;
        int n4 = 0;
        while (true) {
            final double xforT = this.XforT(n2);
            if (xforT < xHi) {
                if (n4 != 0 || xforT > xLo) {
                    return true;
                }
                b = true;
            }
            else {
                if (b) {
                    return true;
                }
                n4 = 1;
            }
            if (n2 >= tforY) {
                if (b) {
                    crossings.record(n, n3, this.direction);
                }
                return false;
            }
            n2 = this.nextVertical(n2, tforY);
        }
    }
    
    public abstract void enlarge(final Rectangle2D p0);
    
    public Curve getSubCurve(final double n, final double n2) {
        return this.getSubCurve(n, n2, this.direction);
    }
    
    public abstract Curve getReversedCurve();
    
    public abstract Curve getSubCurve(final double p0, final double p1, final int p2);
    
    public int compareTo(final Curve curve, final double[] array) {
        final double n = array[0];
        final double min = Math.min(Math.min(array[1], this.getYBot()), curve.getYBot());
        if (min <= array[0]) {
            System.err.println("this == " + this);
            System.err.println("that == " + curve);
            System.out.println("target range = " + array[0] + "=>" + array[1]);
            throw new InternalError("backstepping from " + array[0] + " to " + min);
        }
        array[1] = min;
        if (this.getXMax() <= curve.getXMin()) {
            if (this.getXMin() == curve.getXMax()) {
                return 0;
            }
            return -1;
        }
        else {
            if (this.getXMin() >= curve.getXMax()) {
                return 1;
            }
            double n2 = this.TforY(n);
            double n3 = this.YforT(n2);
            if (n3 < n) {
                n2 = this.refineTforY(n2, n3, n);
                n3 = this.YforT(n2);
            }
            double n4 = this.TforY(min);
            if (this.YforT(n4) < n) {
                n4 = this.refineTforY(n4, this.YforT(n4), n);
            }
            double n5 = curve.TforY(n);
            double n6 = curve.YforT(n5);
            if (n6 < n) {
                n5 = curve.refineTforY(n5, n6, n);
                n6 = curve.YforT(n5);
            }
            double n7 = curve.TforY(min);
            if (curve.YforT(n7) < n) {
                n7 = curve.refineTforY(n7, curve.YforT(n7), n);
            }
            double xforT = this.XforT(n2);
            double xforT2 = curve.XforT(n5);
            final double max = Math.max(Math.max(Math.abs(n), Math.abs(min)) * 1.0E-14, 1.0E-300);
            if (this.fairlyClose(xforT, xforT2)) {
                double n8 = max;
                final double min2 = Math.min(max * 1.0E13, (min - n) * 0.1);
                double n9;
                for (n9 = n + n8; n9 <= min; n9 += n8) {
                    if (!this.fairlyClose(this.XforY(n9), curve.XforY(n9))) {
                        n9 -= n8;
                        while (true) {
                            n8 /= 2.0;
                            final double n10 = n9 + n8;
                            if (n10 <= n9) {
                                break;
                            }
                            if (!this.fairlyClose(this.XforY(n10), curve.XforY(n10))) {
                                continue;
                            }
                            n9 = n10;
                        }
                        break;
                    }
                    if ((n8 *= 2.0) > min2) {
                        n8 = min2;
                    }
                }
                if (n9 > n) {
                    if (n9 < min) {
                        array[1] = n9;
                    }
                    return 0;
                }
            }
            if (max <= 0.0) {
                System.out.println("ymin = " + max);
            }
            while (n2 < n4 && n5 < n7) {
                final double nextVertical = this.nextVertical(n2, n4);
                final double xforT3 = this.XforT(nextVertical);
                final double yforT = this.YforT(nextVertical);
                final double nextVertical2 = curve.nextVertical(n5, n7);
                final double xforT4 = curve.XforT(nextVertical2);
                final double yforT2 = curve.YforT(nextVertical2);
                try {
                    if (this.findIntersect(curve, array, max, 0, 0, n2, xforT, n3, nextVertical, xforT3, yforT, n5, xforT2, n6, nextVertical2, xforT4, yforT2)) {
                        break;
                    }
                }
                catch (final Throwable t) {
                    System.err.println("Error: " + t);
                    System.err.println("y range was " + array[0] + "=>" + array[1]);
                    System.err.println("s y range is " + n3 + "=>" + yforT);
                    System.err.println("t y range is " + n6 + "=>" + yforT2);
                    System.err.println("ymin is " + max);
                    return 0;
                }
                if (yforT < yforT2) {
                    if (yforT > array[0]) {
                        if (yforT < array[1]) {
                            array[1] = yforT;
                            break;
                        }
                        break;
                    }
                    else {
                        n2 = nextVertical;
                        xforT = xforT3;
                        n3 = yforT;
                    }
                }
                else if (yforT2 > array[0]) {
                    if (yforT2 < array[1]) {
                        array[1] = yforT2;
                        break;
                    }
                    break;
                }
                else {
                    n5 = nextVertical2;
                    xforT2 = xforT4;
                    n6 = yforT2;
                }
            }
            final double n11 = (array[0] + array[1]) / 2.0;
            return orderof(this.XforY(n11), curve.XforY(n11));
        }
    }
    
    public boolean findIntersect(final Curve curve, final double[] array, final double n, final int n2, final int n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10, final double n11, final double n12, final double n13, final double n14, final double n15) {
        if (n6 > n15 || n12 > n9) {
            return false;
        }
        if (Math.min(n5, n8) > Math.max(n11, n14) || Math.max(n5, n8) < Math.min(n11, n14)) {
            return false;
        }
        if (n7 - n4 > 0.001) {
            final double n16 = (n4 + n7) / 2.0;
            final double xforT = this.XforT(n16);
            final double yforT = this.YforT(n16);
            if (n16 == n4 || n16 == n7) {
                System.out.println("s0 = " + n4);
                System.out.println("s1 = " + n7);
                throw new InternalError("no s progress!");
            }
            if (n13 - n10 > 0.001) {
                final double n17 = (n10 + n13) / 2.0;
                final double xforT2 = curve.XforT(n17);
                final double yforT2 = curve.YforT(n17);
                if (n17 == n10 || n17 == n13) {
                    System.out.println("t0 = " + n10);
                    System.out.println("t1 = " + n13);
                    throw new InternalError("no t progress!");
                }
                if (yforT >= n12 && yforT2 >= n6 && this.findIntersect(curve, array, n, n2 + 1, n3 + 1, n4, n5, n6, n16, xforT, yforT, n10, n11, n12, n17, xforT2, yforT2)) {
                    return true;
                }
                if (yforT >= yforT2 && this.findIntersect(curve, array, n, n2 + 1, n3 + 1, n4, n5, n6, n16, xforT, yforT, n17, xforT2, yforT2, n13, n14, n15)) {
                    return true;
                }
                if (yforT2 >= yforT && this.findIntersect(curve, array, n, n2 + 1, n3 + 1, n16, xforT, yforT, n7, n8, n9, n10, n11, n12, n17, xforT2, yforT2)) {
                    return true;
                }
                if (n9 >= yforT2 && n15 >= yforT && this.findIntersect(curve, array, n, n2 + 1, n3 + 1, n16, xforT, yforT, n7, n8, n9, n17, xforT2, yforT2, n13, n14, n15)) {
                    return true;
                }
            }
            else {
                if (yforT >= n12 && this.findIntersect(curve, array, n, n2 + 1, n3, n4, n5, n6, n16, xforT, yforT, n10, n11, n12, n13, n14, n15)) {
                    return true;
                }
                if (n15 >= yforT && this.findIntersect(curve, array, n, n2 + 1, n3, n16, xforT, yforT, n7, n8, n9, n10, n11, n12, n13, n14, n15)) {
                    return true;
                }
            }
        }
        else if (n13 - n10 > 0.001) {
            final double n18 = (n10 + n13) / 2.0;
            final double xforT3 = curve.XforT(n18);
            final double yforT3 = curve.YforT(n18);
            if (n18 == n10 || n18 == n13) {
                System.out.println("t0 = " + n10);
                System.out.println("t1 = " + n13);
                throw new InternalError("no t progress!");
            }
            if (yforT3 >= n6 && this.findIntersect(curve, array, n, n2, n3 + 1, n4, n5, n6, n7, n8, n9, n10, n11, n12, n18, xforT3, yforT3)) {
                return true;
            }
            if (n9 >= yforT3 && this.findIntersect(curve, array, n, n2, n3 + 1, n4, n5, n6, n7, n8, n9, n18, xforT3, yforT3, n13, n14, n15)) {
                return true;
            }
        }
        else {
            final double n19 = n8 - n5;
            final double n20 = n9 - n6;
            final double n21 = n14 - n11;
            final double n22 = n15 - n12;
            final double n23 = n11 - n5;
            final double n24 = n12 - n6;
            final double n25 = n21 * n20 - n22 * n19;
            if (n25 != 0.0) {
                final double n26 = 1.0 / n25;
                final double n27 = (n21 * n24 - n22 * n23) * n26;
                final double n28 = (n19 * n24 - n20 * n23) * n26;
                if (n27 >= 0.0 && n27 <= 1.0 && n28 >= 0.0 && n28 <= 1.0) {
                    final double n29 = n4 + n27 * (n7 - n4);
                    final double n30 = n10 + n28 * (n13 - n10);
                    if (n29 < 0.0 || n29 > 1.0 || n30 < 0.0 || n30 > 1.0) {
                        System.out.println("Uh oh!");
                    }
                    final double n31 = (this.YforT(n29) + curve.YforT(n30)) / 2.0;
                    if (n31 <= array[1] && n31 > array[0]) {
                        array[1] = n31;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public double refineTforY(double n, double n2, final double n3) {
        double n4 = 1.0;
        while (true) {
            final double n5 = (n + n4) / 2.0;
            if (n5 == n || n5 == n4) {
                return n4;
            }
            final double yforT = this.YforT(n5);
            if (yforT < n3) {
                n = n5;
                n2 = yforT;
            }
            else {
                if (yforT <= n3) {
                    return n4;
                }
                n4 = n5;
            }
        }
    }
    
    public boolean fairlyClose(final double n, final double n2) {
        return Math.abs(n - n2) < Math.max(Math.abs(n), Math.abs(n2)) * 1.0E-10;
    }
    
    public abstract int getSegment(final double[] p0);
}
