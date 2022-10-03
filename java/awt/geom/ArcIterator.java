package java.awt.geom;

import java.util.NoSuchElementException;

class ArcIterator implements PathIterator
{
    double x;
    double y;
    double w;
    double h;
    double angStRad;
    double increment;
    double cv;
    AffineTransform affine;
    int index;
    int arcSegs;
    int lineSegs;
    
    ArcIterator(final Arc2D arc2D, final AffineTransform affine) {
        this.w = arc2D.getWidth() / 2.0;
        this.h = arc2D.getHeight() / 2.0;
        this.x = arc2D.getX() + this.w;
        this.y = arc2D.getY() + this.h;
        this.angStRad = -Math.toRadians(arc2D.getAngleStart());
        this.affine = affine;
        final double n = -arc2D.getAngleExtent();
        if (n >= 360.0 || n <= -360.0) {
            this.arcSegs = 4;
            this.increment = 1.5707963267948966;
            this.cv = 0.5522847498307933;
            if (n < 0.0) {
                this.increment = -this.increment;
                this.cv = -this.cv;
            }
        }
        else {
            this.arcSegs = (int)Math.ceil(Math.abs(n) / 90.0);
            this.increment = Math.toRadians(n / this.arcSegs);
            this.cv = btan(this.increment);
            if (this.cv == 0.0) {
                this.arcSegs = 0;
            }
        }
        switch (arc2D.getArcType()) {
            case 0: {
                this.lineSegs = 0;
                break;
            }
            case 1: {
                this.lineSegs = 1;
                break;
            }
            case 2: {
                this.lineSegs = 2;
                break;
            }
        }
        if (this.w < 0.0 || this.h < 0.0) {
            final int n2 = -1;
            this.lineSegs = n2;
            this.arcSegs = n2;
        }
    }
    
    @Override
    public int getWindingRule() {
        return 1;
    }
    
    @Override
    public boolean isDone() {
        return this.index > this.arcSegs + this.lineSegs;
    }
    
    @Override
    public void next() {
        ++this.index;
    }
    
    private static double btan(double n) {
        n /= 2.0;
        return 1.3333333333333333 * Math.sin(n) / (1.0 + Math.cos(n));
    }
    
    @Override
    public int currentSegment(final float[] array) {
        if (this.isDone()) {
            throw new NoSuchElementException("arc iterator out of bounds");
        }
        final double angStRad = this.angStRad;
        if (this.index == 0) {
            array[0] = (float)(this.x + Math.cos(angStRad) * this.w);
            array[1] = (float)(this.y + Math.sin(angStRad) * this.h);
            if (this.affine != null) {
                this.affine.transform(array, 0, array, 0, 1);
            }
            return 0;
        }
        if (this.index <= this.arcSegs) {
            final double n = angStRad + this.increment * (this.index - 1);
            final double cos = Math.cos(n);
            final double sin = Math.sin(n);
            array[0] = (float)(this.x + (cos - this.cv * sin) * this.w);
            array[1] = (float)(this.y + (sin + this.cv * cos) * this.h);
            final double n2 = n + this.increment;
            final double cos2 = Math.cos(n2);
            final double sin2 = Math.sin(n2);
            array[2] = (float)(this.x + (cos2 + this.cv * sin2) * this.w);
            array[3] = (float)(this.y + (sin2 - this.cv * cos2) * this.h);
            array[4] = (float)(this.x + cos2 * this.w);
            array[5] = (float)(this.y + sin2 * this.h);
            if (this.affine != null) {
                this.affine.transform(array, 0, array, 0, 3);
            }
            return 3;
        }
        if (this.index == this.arcSegs + this.lineSegs) {
            return 4;
        }
        array[0] = (float)this.x;
        array[1] = (float)this.y;
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, 1);
        }
        return 1;
    }
    
    @Override
    public int currentSegment(final double[] array) {
        if (this.isDone()) {
            throw new NoSuchElementException("arc iterator out of bounds");
        }
        final double angStRad = this.angStRad;
        if (this.index == 0) {
            array[0] = this.x + Math.cos(angStRad) * this.w;
            array[1] = this.y + Math.sin(angStRad) * this.h;
            if (this.affine != null) {
                this.affine.transform(array, 0, array, 0, 1);
            }
            return 0;
        }
        if (this.index <= this.arcSegs) {
            final double n = angStRad + this.increment * (this.index - 1);
            final double cos = Math.cos(n);
            final double sin = Math.sin(n);
            array[0] = this.x + (cos - this.cv * sin) * this.w;
            array[1] = this.y + (sin + this.cv * cos) * this.h;
            final double n2 = n + this.increment;
            final double cos2 = Math.cos(n2);
            final double sin2 = Math.sin(n2);
            array[2] = this.x + (cos2 + this.cv * sin2) * this.w;
            array[3] = this.y + (sin2 - this.cv * cos2) * this.h;
            array[4] = this.x + cos2 * this.w;
            array[5] = this.y + sin2 * this.h;
            if (this.affine != null) {
                this.affine.transform(array, 0, array, 0, 3);
            }
            return 3;
        }
        if (this.index == this.arcSegs + this.lineSegs) {
            return 4;
        }
        array[0] = this.x;
        array[1] = this.y;
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, 1);
        }
        return 1;
    }
}
