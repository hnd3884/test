package com.google.zxing.qrcode.encoder;

final class BlockPair
{
    private final byte[] dataBytes;
    private final byte[] errorCorrectionBytes;
    
    BlockPair(final byte[] data, final byte[] errorCorrection) {
        this.dataBytes = data;
        this.errorCorrectionBytes = errorCorrection;
    }
    
    public byte[] getDataBytes() {
        return this.dataBytes;
    }
    
    public byte[] getErrorCorrectionBytes() {
        return this.errorCorrectionBytes;
    }
}
