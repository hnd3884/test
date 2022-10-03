package sun.awt.geom;

import java.awt.geom.Rectangle2D;

final class Order1 extends Curve
{
    private double x0;
    private double y0;
    private double x1;
    private double y1;
    private double xmin;
    private double xmax;
    
    public Order1(final double xmax, final double y0, final double xmin, final double y2, final int n) {
        super(n);
        this.x0 = xmax;
        this.y0 = y0;
        this.x1 = xmin;
        this.y1 = y2;
        if (xmax < xmin) {
            this.xmin = xmax;
            this.xmax = xmin;
        }
        else {
            this.xmin = xmin;
            this.xmax = xmax;
        }
    }
    
    @Override
    public int getOrder() {
        return 1;
    }
    
    @Override
    public double getXTop() {
        return this.x0;
    }
    
    @Override
    public double getYTop() {
        return this.y0;
    }
    
    @Override
    public double getXBot() {
        return this.x1;
    }
    
    @Override
    public double getYBot() {
        return this.y1;
    }
    
    @Override
    public double getXMin() {
        return this.xmin;
    }
    
    @Override
    public double getXMax() {
        return this.xmax;
    }
    
    @Override
    public double getX0() {
        return (this.direction == 1) ? this.x0 : this.x1;
    }
    
    @Override
    public double getY0() {
        return (this.direction == 1) ? this.y0 : this.y1;
    }
    
    @Override
    public double getX1() {
        return (this.direction == -1) ? this.x0 : this.x1;
    }
    
    @Override
    public double getY1() {
        return (this.direction == -1) ? this.y0 : this.y1;
    }
    
    @Override
    public double XforY(final double n) {
        if (this.x0 == this.x1 || n <= this.y0) {
            return this.x0;
        }
        if (n >= this.y1) {
            return this.x1;
        }
        return this.x0 + (n - this.y0) * (this.x1 - this.x0) / (this.y1 - this.y0);
    }
    
    @Override
    public double TforY(final double n) {
        if (n <= this.y0) {
            return 0.0;
        }
        if (n >= this.y1) {
            return 1.0;
        }
        return (n - this.y0) / (this.y1 - this.y0);
    }
    
    @Override
    public double XforT(final double n) {
        return this.x0 + n * (this.x1 - this.x0);
    }
    
    @Override
    public double YforT(final double n) {
        return this.y0 + n * (this.y1 - this.y0);
    }
    
    @Override
    public double dXforT(final double n, final int n2) {
        switch (n2) {
            case 0: {
                return this.x0 + n * (this.x1 - this.x0);
            }
            case 1: {
                return this.x1 - this.x0;
            }
            default: {
                return 0.0;
            }
        }
    }
    
    @Override
    public double dYforT(final double n, final int n2) {
        switch (n2) {
            case 0: {
                return this.y0 + n * (this.y1 - this.y0);
            }
            case 1: {
                return this.y1 - this.y0;
            }
            default: {
                return 0.0;
            }
        }
    }
    
    @Override
    public double nextVertical(final double n, final double n2) {
        return n2;
    }
    
    @Override
    public boolean accumulateCrossings(final Crossings crossings) {
        final double xLo = crossings.getXLo();
        final double yLo = crossings.getYLo();
        final double xHi = crossings.getXHi();
        final double yHi = crossings.getYHi();
        if (this.xmin >= xHi) {
            return false;
        }
        double y0;
        double n;
        if (this.y0 < yLo) {
            if (this.y1 <= yLo) {
                return false;
            }
            y0 = yLo;
            n = this.XforY(yLo);
        }
        else {
            if (this.y0 >= yHi) {
                return false;
            }
            y0 = this.y0;
            n = this.x0;
        }
        double y2;
        double n2;
        if (this.y1 > yHi) {
            y2 = yHi;
            n2 = this.XforY(yHi);
        }
        else {
            y2 = this.y1;
            n2 = this.x1;
        }
        if (n >= xHi && n2 >= xHi) {
            return false;
        }
        if (n > xLo || n2 > xLo) {
            return true;
        }
        crossings.record(y0, y2, this.direction);
        return false;
    }
    
    @Override
    public void enlarge(final Rectangle2D rectangle2D) {
        rectangle2D.add(this.x0, this.y0);
        rectangle2D.add(this.x1, this.y1);
    }
    
    @Override
    public Curve getSubCurve(final double n, final double n2, final int n3) {
        if (n == this.y0 && n2 == this.y1) {
            return this.getWithDirection(n3);
        }
        if (this.x0 == this.x1) {
            return new Order1(this.x0, n, this.x1, n2, n3);
        }
        final double n4 = this.x0 - this.x1;
        final double n5 = this.y0 - this.y1;
        return new Order1(this.x0 + (n - this.y0) * n4 / n5, n, this.x0 + (n2 - this.y0) * n4 / n5, n2, n3);
    }
    
    @Override
    public Curve getReversedCurve() {
        return new Order1(this.x0, this.y0, this.x1, this.y1, -this.direction);
    }
    
    @Override
    public int compareTo(final Curve curve, final double[] array) {
        if (!(curve instanceof Order1)) {
            return super.compareTo(curve, array);
        }
        final Order1 order1 = (Order1)curve;
        if (array[1] <= array[0]) {
            throw new InternalError("yrange already screwed up...");
        }
        array[1] = Math.min(Math.min(array[1], this.y1), order1.y1);
        if (array[1] <= array[0]) {
            throw new InternalError("backstepping from " + array[0] + " to " + array[1]);
        }
        if (this.xmax <= order1.xmin) {
            return (this.xmin == order1.xmax) ? 0 : -1;
        }
        if (this.xmin >= order1.xmax) {
            return 1;
        }
        final double n = this.x1 - this.x0;
        final double n2 = this.y1 - this.y0;
        final double n3 = order1.x1 - order1.x0;
        final double n4 = order1.y1 - order1.y0;
        final double n5 = n3 * n2 - n * n4;
        double n7;
        if (n5 != 0.0) {
            final double n6 = ((this.x0 - order1.x0) * n2 * n4 - this.y0 * n * n4 + order1.y0 * n3 * n2) / n5;
            if (n6 <= array[0]) {
                n7 = Math.min(this.y1, order1.y1);
            }
            else {
                if (n6 < array[1]) {
                    array[1] = n6;
                }
                n7 = Math.max(this.y0, order1.y0);
            }
        }
        else {
            n7 = Math.max(this.y0, order1.y0);
        }
        return Curve.orderof(this.XforY(n7), order1.XforY(n7));
    }
    
    @Override
    public int getSegment(final double[] array) {
        if (this.direction == 1) {
            array[0] = this.x1;
            array[1] = this.y1;
        }
        else {
            array[0] = this.x0;
            array[1] = this.y0;
        }
        return 1;
    }
}
