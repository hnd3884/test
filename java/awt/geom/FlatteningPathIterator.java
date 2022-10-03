package java.awt.geom;

import java.util.NoSuchElementException;

public class FlatteningPathIterator implements PathIterator
{
    static final int GROW_SIZE = 24;
    PathIterator src;
    double squareflat;
    int limit;
    double[] hold;
    double curx;
    double cury;
    double movx;
    double movy;
    int holdType;
    int holdEnd;
    int holdIndex;
    int[] levels;
    int levelIndex;
    boolean done;
    
    public FlatteningPathIterator(final PathIterator pathIterator, final double n) {
        this(pathIterator, n, 10);
    }
    
    public FlatteningPathIterator(final PathIterator src, final double n, final int limit) {
        this.hold = new double[14];
        if (n < 0.0) {
            throw new IllegalArgumentException("flatness must be >= 0");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be >= 0");
        }
        this.src = src;
        this.squareflat = n * n;
        this.limit = limit;
        this.levels = new int[limit + 1];
        this.next(false);
    }
    
    public double getFlatness() {
        return Math.sqrt(this.squareflat);
    }
    
    public int getRecursionLimit() {
        return this.limit;
    }
    
    @Override
    public int getWindingRule() {
        return this.src.getWindingRule();
    }
    
    @Override
    public boolean isDone() {
        return this.done;
    }
    
    void ensureHoldCapacity(final int n) {
        if (this.holdIndex - n < 0) {
            final int n2 = this.hold.length - this.holdIndex;
            final double[] hold = new double[this.hold.length + 24];
            System.arraycopy(this.hold, this.holdIndex, hold, this.holdIndex + 24, n2);
            this.hold = hold;
            this.holdIndex += 24;
            this.holdEnd += 24;
        }
    }
    
    @Override
    public void next() {
        this.next(true);
    }
    
    private void next(final boolean b) {
        if (this.holdIndex >= this.holdEnd) {
            if (b) {
                this.src.next();
            }
            if (this.src.isDone()) {
                this.done = true;
                return;
            }
            this.holdType = this.src.currentSegment(this.hold);
            this.levelIndex = 0;
            this.levels[0] = 0;
        }
        switch (this.holdType) {
            case 0:
            case 1: {
                this.curx = this.hold[0];
                this.cury = this.hold[1];
                if (this.holdType == 0) {
                    this.movx = this.curx;
                    this.movy = this.cury;
                }
                this.holdIndex = 0;
                this.holdEnd = 0;
                break;
            }
            case 4: {
                this.curx = this.movx;
                this.cury = this.movy;
                this.holdIndex = 0;
                this.holdEnd = 0;
                break;
            }
            case 2: {
                if (this.holdIndex >= this.holdEnd) {
                    this.holdIndex = this.hold.length - 6;
                    this.holdEnd = this.hold.length - 2;
                    this.hold[this.holdIndex + 0] = this.curx;
                    this.hold[this.holdIndex + 1] = this.cury;
                    this.hold[this.holdIndex + 2] = this.hold[0];
                    this.hold[this.holdIndex + 3] = this.hold[1];
                    this.hold[this.holdIndex + 4] = (this.curx = this.hold[2]);
                    this.hold[this.holdIndex + 5] = (this.cury = this.hold[3]);
                }
                for (int n = this.levels[this.levelIndex]; n < this.limit && QuadCurve2D.getFlatnessSq(this.hold, this.holdIndex) >= this.squareflat; ++n, this.levels[this.levelIndex] = n, ++this.levelIndex, this.levels[this.levelIndex] = n) {
                    this.ensureHoldCapacity(4);
                    QuadCurve2D.subdivide(this.hold, this.holdIndex, this.hold, this.holdIndex - 4, this.hold, this.holdIndex);
                    this.holdIndex -= 4;
                }
                this.holdIndex += 4;
                --this.levelIndex;
                break;
            }
            case 3: {
                if (this.holdIndex >= this.holdEnd) {
                    this.holdIndex = this.hold.length - 8;
                    this.holdEnd = this.hold.length - 2;
                    this.hold[this.holdIndex + 0] = this.curx;
                    this.hold[this.holdIndex + 1] = this.cury;
                    this.hold[this.holdIndex + 2] = this.hold[0];
                    this.hold[this.holdIndex + 3] = this.hold[1];
                    this.hold[this.holdIndex + 4] = this.hold[2];
                    this.hold[this.holdIndex + 5] = this.hold[3];
                    this.hold[this.holdIndex + 6] = (this.curx = this.hold[4]);
                    this.hold[this.holdIndex + 7] = (this.cury = this.hold[5]);
                }
                for (int n2 = this.levels[this.levelIndex]; n2 < this.limit && CubicCurve2D.getFlatnessSq(this.hold, this.holdIndex) >= this.squareflat; ++n2, this.levels[this.levelIndex] = n2, ++this.levelIndex, this.levels[this.levelIndex] = n2) {
                    this.ensureHoldCapacity(6);
                    CubicCurve2D.subdivide(this.hold, this.holdIndex, this.hold, this.holdIndex - 6, this.hold, this.holdIndex);
                    this.holdIndex -= 6;
                }
                this.holdIndex += 6;
                --this.levelIndex;
                break;
            }
        }
    }
    
    @Override
    public int currentSegment(final float[] array) {
        if (this.isDone()) {
            throw new NoSuchElementException("flattening iterator out of bounds");
        }
        int holdType = this.holdType;
        if (holdType != 4) {
            array[0] = (float)this.hold[this.holdIndex + 0];
            array[1] = (float)this.hold[this.holdIndex + 1];
            if (holdType != 0) {
                holdType = 1;
            }
        }
        return holdType;
    }
    
    @Override
    public int currentSegment(final double[] array) {
        if (this.isDone()) {
            throw new NoSuchElementException("flattening iterator out of bounds");
        }
        int holdType = this.holdType;
        if (holdType != 4) {
            array[0] = this.hold[this.holdIndex + 0];
            array[1] = this.hold[this.holdIndex + 1];
            if (holdType != 0) {
                holdType = 1;
            }
        }
        return holdType;
    }
}
