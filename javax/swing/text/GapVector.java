package javax.swing.text;

import java.io.Serializable;

abstract class GapVector implements Serializable
{
    private Object array;
    private int g0;
    private int g1;
    
    public GapVector() {
        this(10);
    }
    
    public GapVector(final int g1) {
        this.array = this.allocateArray(g1);
        this.g0 = 0;
        this.g1 = g1;
    }
    
    protected abstract Object allocateArray(final int p0);
    
    protected abstract int getArrayLength();
    
    protected final Object getArray() {
        return this.array;
    }
    
    protected final int getGapStart() {
        return this.g0;
    }
    
    protected final int getGapEnd() {
        return this.g1;
    }
    
    protected void replace(final int n, final int n2, final Object o, int n3) {
        final int n4 = 0;
        if (n3 == 0) {
            this.close(n, n2);
            return;
        }
        if (n2 > n3) {
            this.close(n + n3, n2 - n3);
        }
        else {
            final int n5 = n3 - n2;
            System.arraycopy(o, n2, this.array, this.open(n + n2, n5), n5);
            n3 = n2;
        }
        System.arraycopy(o, n4, this.array, n, n3);
    }
    
    void close(final int n, final int n2) {
        if (n2 == 0) {
            return;
        }
        final int n3 = n + n2;
        final int n4 = this.g1 - this.g0 + n2;
        if (n3 <= this.g0) {
            if (this.g0 != n3) {
                this.shiftGap(n3);
            }
            this.shiftGapStartDown(this.g0 - n2);
        }
        else if (n >= this.g0) {
            if (this.g0 != n) {
                this.shiftGap(n);
            }
            this.shiftGapEndUp(this.g0 + n4);
        }
        else {
            this.shiftGapStartDown(n);
            this.shiftGapEndUp(this.g0 + n4);
        }
    }
    
    int open(int n, final int n2) {
        final int n3 = this.g1 - this.g0;
        if (n2 == 0) {
            if (n > this.g0) {
                n += n3;
            }
            return n;
        }
        this.shiftGap(n);
        if (n2 >= n3) {
            this.shiftEnd(this.getArrayLength() - n3 + n2);
            final int n4 = this.g1 - this.g0;
        }
        this.g0 += n2;
        return n;
    }
    
    void resize(final int n) {
        final Object allocateArray = this.allocateArray(n);
        System.arraycopy(this.array, 0, allocateArray, 0, Math.min(n, this.getArrayLength()));
        this.array = allocateArray;
    }
    
    protected void shiftEnd(final int n) {
        final int arrayLength = this.getArrayLength();
        final int g1 = this.g1;
        final int n2 = arrayLength - g1;
        final int newArraySize = this.getNewArraySize(n);
        final int g2 = newArraySize - n2;
        this.resize(newArraySize);
        this.g1 = g2;
        if (n2 != 0) {
            System.arraycopy(this.array, g1, this.array, g2, n2);
        }
    }
    
    int getNewArraySize(final int n) {
        return (n + 1) * 2;
    }
    
    protected void shiftGap(final int g0) {
        if (g0 == this.g0) {
            return;
        }
        final int g2 = this.g0;
        final int n = g0 - g2;
        final int g3 = this.g1;
        final int g4 = g3 + n;
        this.g0 = g0;
        this.g1 = g4;
        if (n > 0) {
            System.arraycopy(this.array, g3, this.array, g2, n);
        }
        else if (n < 0) {
            System.arraycopy(this.array, g0, this.array, g4, -n);
        }
    }
    
    protected void shiftGapStartDown(final int g0) {
        this.g0 = g0;
    }
    
    protected void shiftGapEndUp(final int g1) {
        this.g1 = g1;
    }
}
