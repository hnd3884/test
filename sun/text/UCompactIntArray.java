package sun.text;

public final class UCompactIntArray implements Cloneable
{
    private static final int PLANEMASK = 196608;
    private static final int PLANESHIFT = 16;
    private static final int PLANECOUNT = 16;
    private static final int CODEPOINTMASK = 65535;
    private static final int UNICODECOUNT = 65536;
    private static final int BLOCKSHIFT = 7;
    private static final int BLOCKCOUNT = 128;
    private static final int INDEXSHIFT = 9;
    private static final int INDEXCOUNT = 512;
    private static final int BLOCKMASK = 127;
    private int defaultValue;
    private int[][] values;
    private short[][] indices;
    private boolean isCompact;
    private boolean[][] blockTouched;
    private boolean[] planeTouched;
    
    public UCompactIntArray() {
        this.values = new int[16][];
        this.indices = new short[16][];
        this.blockTouched = new boolean[16][];
        this.planeTouched = new boolean[16];
    }
    
    public UCompactIntArray(final int defaultValue) {
        this();
        this.defaultValue = defaultValue;
    }
    
    public int elementAt(int n) {
        final int n2 = (n & 0x30000) >> 16;
        if (!this.planeTouched[n2]) {
            return this.defaultValue;
        }
        n &= 0xFFFF;
        return this.values[n2][(this.indices[n2][n >> 7] & 0xFFFF) + (n & 0x7F)];
    }
    
    public void setElementAt(int n, final int n2) {
        if (this.isCompact) {
            this.expand();
        }
        final int n3 = (n & 0x30000) >> 16;
        if (!this.planeTouched[n3]) {
            this.initPlane(n3);
        }
        n &= 0xFFFF;
        this.values[n3][n] = n2;
        this.blockTouched[n3][n >> 7] = true;
    }
    
    public void compact() {
        if (this.isCompact) {
            return;
        }
        for (int i = 0; i < 16; ++i) {
            if (this.planeTouched[i]) {
                int n = 0;
                int n2 = 0;
                short n3 = -1;
                for (int j = 0; j < this.indices[i].length; ++j, n2 += 128) {
                    this.indices[i][j] = -1;
                    if (!this.blockTouched[i][j] && n3 != -1) {
                        this.indices[i][j] = n3;
                    }
                    else {
                        final int n4 = n * 128;
                        if (j > n) {
                            System.arraycopy(this.values[i], n2, this.values[i], n4, 128);
                        }
                        if (!this.blockTouched[i][j]) {
                            n3 = (short)n4;
                        }
                        this.indices[i][j] = (short)n4;
                        ++n;
                    }
                }
                final int n5 = n * 128;
                final int[] array = new int[n5];
                System.arraycopy(this.values[i], 0, array, 0, n5);
                this.values[i] = array;
                this.blockTouched[i] = null;
            }
        }
        this.isCompact = true;
    }
    
    private void expand() {
        if (this.isCompact) {
            for (int i = 0; i < 16; ++i) {
                if (this.planeTouched[i]) {
                    this.blockTouched[i] = new boolean[512];
                    final int[] array = new int[65536];
                    for (int j = 0; j < 65536; ++j) {
                        array[j] = this.values[i][this.indices[i][j >> 7] & 65535 + (j & 0x7F)];
                        this.blockTouched[i][j >> 7] = true;
                    }
                    for (int k = 0; k < 512; ++k) {
                        this.indices[i][k] = (short)(k << 7);
                    }
                    this.values[i] = array;
                }
            }
            this.isCompact = false;
        }
    }
    
    private void initPlane(final int n) {
        this.values[n] = new int[65536];
        this.indices[n] = new short[512];
        this.blockTouched[n] = new boolean[512];
        this.planeTouched[n] = true;
        if (this.planeTouched[0] && n != 0) {
            System.arraycopy(this.indices[0], 0, this.indices[n], 0, 512);
        }
        else {
            for (int i = 0; i < 512; ++i) {
                this.indices[n][i] = (short)(i << 7);
            }
        }
        for (int j = 0; j < 65536; ++j) {
            this.values[n][j] = this.defaultValue;
        }
    }
    
    public int getKSize() {
        int n = 0;
        for (int i = 0; i < 16; ++i) {
            if (this.planeTouched[i]) {
                n += this.values[i].length * 4 + this.indices[i].length * 2;
            }
        }
        return n / 1024;
    }
}
