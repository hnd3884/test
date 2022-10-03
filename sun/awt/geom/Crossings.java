package sun.awt.geom;

import java.awt.geom.PathIterator;
import java.util.Enumeration;
import java.util.Vector;

public abstract class Crossings
{
    public static final boolean debug = false;
    int limit;
    double[] yranges;
    double xlo;
    double ylo;
    double xhi;
    double yhi;
    private Vector tmp;
    
    public Crossings(final double xlo, final double ylo, final double xhi, final double yhi) {
        this.limit = 0;
        this.yranges = new double[10];
        this.tmp = new Vector();
        this.xlo = xlo;
        this.ylo = ylo;
        this.xhi = xhi;
        this.yhi = yhi;
    }
    
    public final double getXLo() {
        return this.xlo;
    }
    
    public final double getYLo() {
        return this.ylo;
    }
    
    public final double getXHi() {
        return this.xhi;
    }
    
    public final double getYHi() {
        return this.yhi;
    }
    
    public abstract void record(final double p0, final double p1, final int p2);
    
    public void print() {
        System.out.println("Crossings [");
        System.out.println("  bounds = [" + this.ylo + ", " + this.yhi + "]");
        for (int i = 0; i < this.limit; i += 2) {
            System.out.println("  [" + this.yranges[i] + ", " + this.yranges[i + 1] + "]");
        }
        System.out.println("]");
    }
    
    public final boolean isEmpty() {
        return this.limit == 0;
    }
    
    public abstract boolean covers(final double p0, final double p1);
    
    public static Crossings findCrossings(final Vector vector, final double n, final double n2, final double n3, final double n4) {
        final EvenOdd evenOdd = new EvenOdd(n, n2, n3, n4);
        final Enumeration elements = vector.elements();
        while (elements.hasMoreElements()) {
            if (((Curve)elements.nextElement()).accumulateCrossings(evenOdd)) {
                return null;
            }
        }
        return evenOdd;
    }
    
    public static Crossings findCrossings(final PathIterator pathIterator, final double n, final double n2, final double n3, final double n4) {
        Crossings crossings;
        if (pathIterator.getWindingRule() == 0) {
            crossings = new EvenOdd(n, n2, n3, n4);
        }
        else {
            crossings = new NonZero(n, n2, n3, n4);
        }
        final double[] array = new double[23];
        double n5 = 0.0;
        double n6 = 0.0;
        double n7 = 0.0;
        double n8 = 0.0;
        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(array)) {
                case 0: {
                    if (n6 != n8 && crossings.accumulateLine(n7, n8, n5, n6)) {
                        return null;
                    }
                    n7 = (n5 = array[0]);
                    n8 = (n6 = array[1]);
                    break;
                }
                case 1: {
                    final double n9 = array[0];
                    final double n10 = array[1];
                    if (crossings.accumulateLine(n7, n8, n9, n10)) {
                        return null;
                    }
                    n7 = n9;
                    n8 = n10;
                    break;
                }
                case 2: {
                    final double n11 = array[2];
                    final double n12 = array[3];
                    if (crossings.accumulateQuad(n7, n8, array)) {
                        return null;
                    }
                    n7 = n11;
                    n8 = n12;
                    break;
                }
                case 3: {
                    final double n13 = array[4];
                    final double n14 = array[5];
                    if (crossings.accumulateCubic(n7, n8, array)) {
                        return null;
                    }
                    n7 = n13;
                    n8 = n14;
                    break;
                }
                case 4: {
                    if (n6 != n8 && crossings.accumulateLine(n7, n8, n5, n6)) {
                        return null;
                    }
                    n7 = n5;
                    n8 = n6;
                    break;
                }
            }
            pathIterator.next();
        }
        if (n6 != n8 && crossings.accumulateLine(n7, n8, n5, n6)) {
            return null;
        }
        return crossings;
    }
    
    public boolean accumulateLine(final double n, final double n2, final double n3, final double n4) {
        if (n2 <= n4) {
            return this.accumulateLine(n, n2, n3, n4, 1);
        }
        return this.accumulateLine(n3, n4, n, n2, -1);
    }
    
    public boolean accumulateLine(final double n, final double n2, final double n3, final double n4, final int n5) {
        if (this.yhi <= n2 || this.ylo >= n4) {
            return false;
        }
        if (n >= this.xhi && n3 >= this.xhi) {
            return false;
        }
        if (n2 == n4) {
            return n >= this.xlo || n3 >= this.xlo;
        }
        final double n6 = n3 - n;
        final double n7 = n4 - n2;
        double n8;
        double ylo;
        if (n2 < this.ylo) {
            n8 = n + (this.ylo - n2) * n6 / n7;
            ylo = this.ylo;
        }
        else {
            n8 = n;
            ylo = n2;
        }
        double n9;
        double yhi;
        if (this.yhi < n4) {
            n9 = n + (this.yhi - n2) * n6 / n7;
            yhi = this.yhi;
        }
        else {
            n9 = n3;
            yhi = n4;
        }
        if (n8 >= this.xhi && n9 >= this.xhi) {
            return false;
        }
        if (n8 > this.xlo || n9 > this.xlo) {
            return true;
        }
        this.record(ylo, yhi, n5);
        return false;
    }
    
    public boolean accumulateQuad(final double n, final double n2, final double[] array) {
        if (n2 < this.ylo && array[1] < this.ylo && array[3] < this.ylo) {
            return false;
        }
        if (n2 > this.yhi && array[1] > this.yhi && array[3] > this.yhi) {
            return false;
        }
        if (n > this.xhi && array[0] > this.xhi && array[2] > this.xhi) {
            return false;
        }
        if (n < this.xlo && array[0] < this.xlo && array[2] < this.xlo) {
            if (n2 < array[3]) {
                this.record(Math.max(n2, this.ylo), Math.min(array[3], this.yhi), 1);
            }
            else if (n2 > array[3]) {
                this.record(Math.max(array[3], this.ylo), Math.min(n2, this.yhi), -1);
            }
            return false;
        }
        Curve.insertQuad(this.tmp, n, n2, array);
        final Enumeration elements = this.tmp.elements();
        while (elements.hasMoreElements()) {
            if (((Curve)elements.nextElement()).accumulateCrossings(this)) {
                return true;
            }
        }
        this.tmp.clear();
        return false;
    }
    
    public boolean accumulateCubic(final double n, final double n2, final double[] array) {
        if (n2 < this.ylo && array[1] < this.ylo && array[3] < this.ylo && array[5] < this.ylo) {
            return false;
        }
        if (n2 > this.yhi && array[1] > this.yhi && array[3] > this.yhi && array[5] > this.yhi) {
            return false;
        }
        if (n > this.xhi && array[0] > this.xhi && array[2] > this.xhi && array[4] > this.xhi) {
            return false;
        }
        if (n < this.xlo && array[0] < this.xlo && array[2] < this.xlo && array[4] < this.xlo) {
            if (n2 <= array[5]) {
                this.record(Math.max(n2, this.ylo), Math.min(array[5], this.yhi), 1);
            }
            else {
                this.record(Math.max(array[5], this.ylo), Math.min(n2, this.yhi), -1);
            }
            return false;
        }
        Curve.insertCubic(this.tmp, n, n2, array);
        final Enumeration elements = this.tmp.elements();
        while (elements.hasMoreElements()) {
            if (((Curve)elements.nextElement()).accumulateCrossings(this)) {
                return true;
            }
        }
        this.tmp.clear();
        return false;
    }
    
    public static final class EvenOdd extends Crossings
    {
        public EvenOdd(final double n, final double n2, final double n3, final double n4) {
            super(n, n2, n3, n4);
        }
        
        @Override
        public final boolean covers(final double n, final double n2) {
            return this.limit == 2 && this.yranges[0] <= n && this.yranges[1] >= n2;
        }
        
        @Override
        public void record(double n, double n2, final int n3) {
            if (n >= n2) {
                return;
            }
            int i;
            for (i = 0; i < this.limit && n > this.yranges[i + 1]; i += 2) {}
            int n4 = i;
            while (i < this.limit) {
                final double n5 = this.yranges[i++];
                final double n6 = this.yranges[i++];
                if (n2 < n5) {
                    this.yranges[n4++] = n;
                    this.yranges[n4++] = n2;
                    n = n5;
                    n2 = n6;
                }
                else {
                    double n7;
                    double n8;
                    if (n < n5) {
                        n7 = n;
                        n8 = n5;
                    }
                    else {
                        n7 = n5;
                        n8 = n;
                    }
                    double n9;
                    double n10;
                    if (n2 < n6) {
                        n9 = n2;
                        n10 = n6;
                    }
                    else {
                        n9 = n6;
                        n10 = n2;
                    }
                    if (n8 == n9) {
                        n = n7;
                        n2 = n10;
                    }
                    else {
                        if (n8 > n9) {
                            n = n9;
                            n9 = n8;
                            n8 = n;
                        }
                        if (n7 != n8) {
                            this.yranges[n4++] = n7;
                            this.yranges[n4++] = n8;
                        }
                        n = n9;
                        n2 = n10;
                    }
                    if (n >= n2) {
                        break;
                    }
                    continue;
                }
            }
            if (n4 < i && i < this.limit) {
                System.arraycopy(this.yranges, i, this.yranges, n4, this.limit - i);
            }
            int limit = n4 + (this.limit - i);
            if (n < n2) {
                if (limit >= this.yranges.length) {
                    final double[] yranges = new double[limit + 10];
                    System.arraycopy(this.yranges, 0, yranges, 0, limit);
                    this.yranges = yranges;
                }
                this.yranges[limit++] = n;
                this.yranges[limit++] = n2;
            }
            this.limit = limit;
        }
    }
    
    public static final class NonZero extends Crossings
    {
        private int[] crosscounts;
        
        public NonZero(final double n, final double n2, final double n3, final double n4) {
            super(n, n2, n3, n4);
            this.crosscounts = new int[this.yranges.length / 2];
        }
        
        @Override
        public final boolean covers(double n, final double n2) {
            int i = 0;
            while (i < this.limit) {
                final double n3 = this.yranges[i++];
                final double n4 = this.yranges[i++];
                if (n >= n4) {
                    continue;
                }
                if (n < n3) {
                    return false;
                }
                if (n2 <= n4) {
                    return true;
                }
                n = n4;
            }
            return n >= n2;
        }
        
        public void remove(final int n) {
            this.limit -= 2;
            final int n2 = this.limit - n;
            if (n2 > 0) {
                System.arraycopy(this.yranges, n + 2, this.yranges, n, n2);
                System.arraycopy(this.crosscounts, n / 2 + 1, this.crosscounts, n / 2, n2 / 2);
            }
        }
        
        public void insert(final int n, final double n2, final double n3, final int n4) {
            final int n5 = this.limit - n;
            final double[] yranges = this.yranges;
            final int[] crosscounts = this.crosscounts;
            if (this.limit >= this.yranges.length) {
                System.arraycopy(yranges, 0, this.yranges = new double[this.limit + 10], 0, n);
                System.arraycopy(crosscounts, 0, this.crosscounts = new int[(this.limit + 10) / 2], 0, n / 2);
            }
            if (n5 > 0) {
                System.arraycopy(yranges, n, this.yranges, n + 2, n5);
                System.arraycopy(crosscounts, n / 2, this.crosscounts, n / 2 + 1, n5 / 2);
            }
            this.yranges[n + 0] = n2;
            this.yranges[n + 1] = n3;
            this.crosscounts[n / 2] = n4;
            this.limit += 2;
        }
        
        @Override
        public void record(double n, final double n2, final int n3) {
            if (n >= n2) {
                return;
            }
            int n4;
            for (n4 = 0; n4 < this.limit && n > this.yranges[n4 + 1]; n4 += 2) {}
            if (n4 < this.limit) {
                int n5 = this.crosscounts[n4 / 2];
                double n6 = this.yranges[n4 + 0];
                double n7 = this.yranges[n4 + 1];
                if (n7 == n && n5 == n3) {
                    if (n4 + 2 == this.limit) {
                        this.yranges[n4 + 1] = n2;
                        return;
                    }
                    this.remove(n4);
                    n = n6;
                    n5 = this.crosscounts[n4 / 2];
                    n6 = this.yranges[n4 + 0];
                    n7 = this.yranges[n4 + 1];
                }
                if (n2 < n6) {
                    this.insert(n4, n, n2, n3);
                    return;
                }
                if (n2 == n6 && n5 == n3) {
                    this.yranges[n4] = n;
                    return;
                }
                if (n < n6) {
                    this.insert(n4, n, n6, n3);
                    n4 += 2;
                    n = n6;
                }
                else if (n6 < n) {
                    this.insert(n4, n6, n, n5);
                    n4 += 2;
                }
                final int n8 = n5 + n3;
                final double min = Math.min(n2, n7);
                if (n8 == 0) {
                    this.remove(n4);
                }
                else {
                    this.crosscounts[n4 / 2] = n8;
                    this.yranges[n4++] = n;
                    this.yranges[n4++] = min;
                }
                final double n9 = n = min;
                if (n9 < n7) {
                    this.insert(n4, n9, n7, n5);
                }
            }
            if (n < n2) {
                this.insert(n4, n, n2, n3);
            }
        }
    }
}
