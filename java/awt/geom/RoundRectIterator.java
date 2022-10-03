package java.awt.geom;

import java.util.NoSuchElementException;

class RoundRectIterator implements PathIterator
{
    double x;
    double y;
    double w;
    double h;
    double aw;
    double ah;
    AffineTransform affine;
    int index;
    private static final double angle = 0.7853981633974483;
    private static final double a;
    private static final double b;
    private static final double c;
    private static final double cv;
    private static final double acv;
    private static double[][] ctrlpts;
    private static int[] types;
    
    RoundRectIterator(final RoundRectangle2D roundRectangle2D, final AffineTransform affine) {
        this.x = roundRectangle2D.getX();
        this.y = roundRectangle2D.getY();
        this.w = roundRectangle2D.getWidth();
        this.h = roundRectangle2D.getHeight();
        this.aw = Math.min(this.w, Math.abs(roundRectangle2D.getArcWidth()));
        this.ah = Math.min(this.h, Math.abs(roundRectangle2D.getArcHeight()));
        this.affine = affine;
        if (this.aw < 0.0 || this.ah < 0.0) {
            this.index = RoundRectIterator.ctrlpts.length;
        }
    }
    
    @Override
    public int getWindingRule() {
        return 1;
    }
    
    @Override
    public boolean isDone() {
        return this.index >= RoundRectIterator.ctrlpts.length;
    }
    
    @Override
    public void next() {
        ++this.index;
    }
    
    @Override
    public int currentSegment(final float[] array) {
        if (this.isDone()) {
            throw new NoSuchElementException("roundrect iterator out of bounds");
        }
        final double[] array2 = RoundRectIterator.ctrlpts[this.index];
        int n = 0;
        for (int i = 0; i < array2.length; i += 4) {
            array[n++] = (float)(this.x + array2[i + 0] * this.w + array2[i + 1] * this.aw);
            array[n++] = (float)(this.y + array2[i + 2] * this.h + array2[i + 3] * this.ah);
        }
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, n / 2);
        }
        return RoundRectIterator.types[this.index];
    }
    
    @Override
    public int currentSegment(final double[] array) {
        if (this.isDone()) {
            throw new NoSuchElementException("roundrect iterator out of bounds");
        }
        final double[] array2 = RoundRectIterator.ctrlpts[this.index];
        int n = 0;
        for (int i = 0; i < array2.length; i += 4) {
            array[n++] = this.x + array2[i + 0] * this.w + array2[i + 1] * this.aw;
            array[n++] = this.y + array2[i + 2] * this.h + array2[i + 3] * this.ah;
        }
        if (this.affine != null) {
            this.affine.transform(array, 0, array, 0, n / 2);
        }
        return RoundRectIterator.types[this.index];
    }
    
    static {
        a = 1.0 - Math.cos(0.7853981633974483);
        b = Math.tan(0.7853981633974483);
        c = Math.sqrt(1.0 + RoundRectIterator.b * RoundRectIterator.b) - 1.0 + RoundRectIterator.a;
        cv = 1.3333333333333333 * RoundRectIterator.a * RoundRectIterator.b / RoundRectIterator.c;
        acv = (1.0 - RoundRectIterator.cv) / 2.0;
        RoundRectIterator.ctrlpts = new double[][] { { 0.0, 0.0, 0.0, 0.5 }, { 0.0, 0.0, 1.0, -0.5 }, { 0.0, 0.0, 1.0, -RoundRectIterator.acv, 0.0, RoundRectIterator.acv, 1.0, 0.0, 0.0, 0.5, 1.0, 0.0 }, { 1.0, -0.5, 1.0, 0.0 }, { 1.0, -RoundRectIterator.acv, 1.0, 0.0, 1.0, 0.0, 1.0, -RoundRectIterator.acv, 1.0, 0.0, 1.0, -0.5 }, { 1.0, 0.0, 0.0, 0.5 }, { 1.0, 0.0, 0.0, RoundRectIterator.acv, 1.0, -RoundRectIterator.acv, 0.0, 0.0, 1.0, -0.5, 0.0, 0.0 }, { 0.0, 0.5, 0.0, 0.0 }, { 0.0, RoundRectIterator.acv, 0.0, 0.0, 0.0, 0.0, 0.0, RoundRectIterator.acv, 0.0, 0.0, 0.0, 0.5 }, new double[0] };
        RoundRectIterator.types = new int[] { 0, 1, 3, 1, 3, 1, 3, 1, 3, 4 };
    }
}
