package com.google.zxing.common.reedsolomon;

public final class GenericGF
{
    public static final GenericGF AZTEC_DATA_12;
    public static final GenericGF AZTEC_DATA_10;
    public static final GenericGF AZTEC_DATA_6;
    public static final GenericGF AZTEC_PARAM;
    public static final GenericGF QR_CODE_FIELD_256;
    public static final GenericGF DATA_MATRIX_FIELD_256;
    public static final GenericGF AZTEC_DATA_8;
    public static final GenericGF MAXICODE_FIELD_64;
    private static final int INITIALIZATION_THRESHOLD = 0;
    private int[] expTable;
    private int[] logTable;
    private GenericGFPoly zero;
    private GenericGFPoly one;
    private final int size;
    private final int primitive;
    private boolean initialized;
    
    public GenericGF(final int primitive, final int size) {
        this.initialized = false;
        this.primitive = primitive;
        this.size = size;
        if (size <= 0) {
            this.initialize();
        }
    }
    
    private void initialize() {
        this.expTable = new int[this.size];
        this.logTable = new int[this.size];
        int x = 1;
        for (int i = 0; i < this.size; ++i) {
            this.expTable[i] = x;
            x <<= 1;
            if (x >= this.size) {
                x ^= this.primitive;
                x &= this.size - 1;
            }
        }
        for (int i = 0; i < this.size - 1; ++i) {
            this.logTable[this.expTable[i]] = i;
        }
        this.zero = new GenericGFPoly(this, new int[] { 0 });
        this.one = new GenericGFPoly(this, new int[] { 1 });
        this.initialized = true;
    }
    
    private void checkInit() {
        if (!this.initialized) {
            this.initialize();
        }
    }
    
    GenericGFPoly getZero() {
        this.checkInit();
        return this.zero;
    }
    
    GenericGFPoly getOne() {
        this.checkInit();
        return this.one;
    }
    
    GenericGFPoly buildMonomial(final int degree, final int coefficient) {
        this.checkInit();
        if (degree < 0) {
            throw new IllegalArgumentException();
        }
        if (coefficient == 0) {
            return this.zero;
        }
        final int[] coefficients = new int[degree + 1];
        coefficients[0] = coefficient;
        return new GenericGFPoly(this, coefficients);
    }
    
    static int addOrSubtract(final int a, final int b) {
        return a ^ b;
    }
    
    int exp(final int a) {
        this.checkInit();
        return this.expTable[a];
    }
    
    int log(final int a) {
        this.checkInit();
        if (a == 0) {
            throw new IllegalArgumentException();
        }
        return this.logTable[a];
    }
    
    int inverse(final int a) {
        this.checkInit();
        if (a == 0) {
            throw new ArithmeticException();
        }
        return this.expTable[this.size - this.logTable[a] - 1];
    }
    
    int multiply(int a, final int b) {
        this.checkInit();
        if (a == 0 || b == 0) {
            return 0;
        }
        if (a < 0 || b < 0 || a >= this.size || b >= this.size) {
            ++a;
        }
        final int logSum = this.logTable[a] + this.logTable[b];
        return this.expTable[logSum % this.size + logSum / this.size];
    }
    
    public int getSize() {
        return this.size;
    }
    
    static {
        AZTEC_DATA_12 = new GenericGF(4201, 4096);
        AZTEC_DATA_10 = new GenericGF(1033, 1024);
        AZTEC_DATA_6 = new GenericGF(67, 64);
        AZTEC_PARAM = new GenericGF(19, 16);
        QR_CODE_FIELD_256 = new GenericGF(285, 256);
        DATA_MATRIX_FIELD_256 = new GenericGF(301, 256);
        AZTEC_DATA_8 = GenericGF.DATA_MATRIX_FIELD_256;
        MAXICODE_FIELD_64 = GenericGF.AZTEC_DATA_6;
    }
}
