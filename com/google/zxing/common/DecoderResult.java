package com.google.zxing.common;

import java.util.List;

public final class DecoderResult
{
    private final byte[] rawBytes;
    private final String text;
    private final List<byte[]> byteSegments;
    private final String ecLevel;
    
    public DecoderResult(final byte[] rawBytes, final String text, final List<byte[]> byteSegments, final String ecLevel) {
        this.rawBytes = rawBytes;
        this.text = text;
        this.byteSegments = byteSegments;
        this.ecLevel = ecLevel;
    }
    
    public byte[] getRawBytes() {
        return this.rawBytes;
    }
    
    public String getText() {
        return this.text;
    }
    
    public List<byte[]> getByteSegments() {
        return this.byteSegments;
    }
    
    public String getECLevel() {
        return this.ecLevel;
    }
}
