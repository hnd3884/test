package com.google.zxing.qrcode.decoder;

public enum ErrorCorrectionLevel
{
    L(1), 
    M(0), 
    Q(3), 
    H(2);
    
    private static final ErrorCorrectionLevel[] FOR_BITS;
    private final int bits;
    
    private ErrorCorrectionLevel(final int bits) {
        this.bits = bits;
    }
    
    public int getBits() {
        return this.bits;
    }
    
    public static ErrorCorrectionLevel forBits(final int bits) {
        if (bits < 0 || bits >= ErrorCorrectionLevel.FOR_BITS.length) {
            throw new IllegalArgumentException();
        }
        return ErrorCorrectionLevel.FOR_BITS[bits];
    }
    
    static {
        FOR_BITS = new ErrorCorrectionLevel[] { ErrorCorrectionLevel.M, ErrorCorrectionLevel.L, ErrorCorrectionLevel.H, ErrorCorrectionLevel.Q };
    }
}
