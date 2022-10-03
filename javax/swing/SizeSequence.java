package javax.swing;

public class SizeSequence
{
    private static int[] emptyArray;
    private int[] a;
    
    public SizeSequence() {
        this.a = SizeSequence.emptyArray;
    }
    
    public SizeSequence(final int n) {
        this(n, 0);
    }
    
    public SizeSequence(final int n, final int n2) {
        this();
        this.insertEntries(0, n, n2);
    }
    
    public SizeSequence(final int[] sizes) {
        this();
        this.setSizes(sizes);
    }
    
    void setSizes(final int n, final int n2) {
        if (this.a.length != n) {
            this.a = new int[n];
        }
        this.setSizes(0, n, n2);
    }
    
    private int setSizes(final int n, final int n2, final int n3) {
        if (n2 <= n) {
            return 0;
        }
        final int n4 = (n + n2) / 2;
        this.a[n4] = n3 + this.setSizes(n, n4, n3);
        return this.a[n4] + this.setSizes(n4 + 1, n2, n3);
    }
    
    public void setSizes(final int[] array) {
        if (this.a.length != array.length) {
            this.a = new int[array.length];
        }
        this.setSizes(0, this.a.length, array);
    }
    
    private int setSizes(final int n, final int n2, final int[] array) {
        if (n2 <= n) {
            return 0;
        }
        final int n3 = (n + n2) / 2;
        this.a[n3] = array[n3] + this.setSizes(n, n3, array);
        return this.a[n3] + this.setSizes(n3 + 1, n2, array);
    }
    
    public int[] getSizes() {
        final int length = this.a.length;
        final int[] array = new int[length];
        this.getSizes(0, length, array);
        return array;
    }
    
    private int getSizes(final int n, final int n2, final int[] array) {
        if (n2 <= n) {
            return 0;
        }
        final int n3 = (n + n2) / 2;
        array[n3] = this.a[n3] - this.getSizes(n, n3, array);
        return this.a[n3] + this.getSizes(n3 + 1, n2, array);
    }
    
    public int getPosition(final int n) {
        return this.getPosition(0, this.a.length, n);
    }
    
    private int getPosition(final int n, final int n2, final int n3) {
        if (n2 <= n) {
            return 0;
        }
        final int n4 = (n + n2) / 2;
        if (n3 <= n4) {
            return this.getPosition(n, n4, n3);
        }
        return this.a[n4] + this.getPosition(n4 + 1, n2, n3);
    }
    
    public int getIndex(final int n) {
        return this.getIndex(0, this.a.length, n);
    }
    
    private int getIndex(final int n, final int n2, final int n3) {
        if (n2 <= n) {
            return n;
        }
        final int n4 = (n + n2) / 2;
        final int n5 = this.a[n4];
        if (n3 < n5) {
            return this.getIndex(n, n4, n3);
        }
        return this.getIndex(n4 + 1, n2, n3 - n5);
    }
    
    public int getSize(final int n) {
        return this.getPosition(n + 1) - this.getPosition(n);
    }
    
    public void setSize(final int n, final int n2) {
        this.changeSize(0, this.a.length, n, n2 - this.getSize(n));
    }
    
    private void changeSize(final int n, final int n2, final int n3, final int n4) {
        if (n2 <= n) {
            return;
        }
        final int n5 = (n + n2) / 2;
        if (n3 <= n5) {
            final int[] a = this.a;
            final int n6 = n5;
            a[n6] += n4;
            this.changeSize(n, n5, n3, n4);
        }
        else {
            this.changeSize(n5 + 1, n2, n3, n4);
        }
    }
    
    public void insertEntries(final int n, final int n2, final int n3) {
        final int[] sizes = this.getSizes();
        final int n4 = n + n2;
        final int n5 = this.a.length + n2;
        this.a = new int[n5];
        for (int i = 0; i < n; ++i) {
            this.a[i] = sizes[i];
        }
        for (int j = n; j < n4; ++j) {
            this.a[j] = n3;
        }
        for (int k = n4; k < n5; ++k) {
            this.a[k] = sizes[k - n2];
        }
        this.setSizes(this.a);
    }
    
    public void removeEntries(final int n, final int n2) {
        final int[] sizes = this.getSizes();
        final int n3 = this.a.length - n2;
        this.a = new int[n3];
        for (int i = 0; i < n; ++i) {
            this.a[i] = sizes[i];
        }
        for (int j = n; j < n3; ++j) {
            this.a[j] = sizes[j + n2];
        }
        this.setSizes(this.a);
    }
    
    static {
        SizeSequence.emptyArray = new int[0];
    }
}
