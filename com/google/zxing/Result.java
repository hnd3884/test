package com.google.zxing;

import java.util.EnumMap;
import java.util.Map;

public final class Result
{
    private final String text;
    private final byte[] rawBytes;
    private ResultPoint[] resultPoints;
    private final BarcodeFormat format;
    private Map<ResultMetadataType, Object> resultMetadata;
    private final long timestamp;
    
    public Result(final String text, final byte[] rawBytes, final ResultPoint[] resultPoints, final BarcodeFormat format) {
        this(text, rawBytes, resultPoints, format, System.currentTimeMillis());
    }
    
    public Result(final String text, final byte[] rawBytes, final ResultPoint[] resultPoints, final BarcodeFormat format, final long timestamp) {
        this.text = text;
        this.rawBytes = rawBytes;
        this.resultPoints = resultPoints;
        this.format = format;
        this.resultMetadata = null;
        this.timestamp = timestamp;
    }
    
    public String getText() {
        return this.text;
    }
    
    public byte[] getRawBytes() {
        return this.rawBytes;
    }
    
    public ResultPoint[] getResultPoints() {
        return this.resultPoints;
    }
    
    public BarcodeFormat getBarcodeFormat() {
        return this.format;
    }
    
    public Map<ResultMetadataType, Object> getResultMetadata() {
        return this.resultMetadata;
    }
    
    public void putMetadata(final ResultMetadataType type, final Object value) {
        if (this.resultMetadata == null) {
            this.resultMetadata = new EnumMap<ResultMetadataType, Object>(ResultMetadataType.class);
        }
        this.resultMetadata.put(type, value);
    }
    
    public void putAllMetadata(final Map<ResultMetadataType, Object> metadata) {
        if (metadata != null) {
            if (this.resultMetadata == null) {
                this.resultMetadata = metadata;
            }
            else {
                this.resultMetadata.putAll(metadata);
            }
        }
    }
    
    public void addResultPoints(final ResultPoint[] newPoints) {
        final ResultPoint[] oldPoints = this.resultPoints;
        if (oldPoints == null) {
            this.resultPoints = newPoints;
        }
        else if (newPoints != null && newPoints.length > 0) {
            final ResultPoint[] allPoints = new ResultPoint[oldPoints.length + newPoints.length];
            System.arraycopy(oldPoints, 0, allPoints, 0, oldPoints.length);
            System.arraycopy(newPoints, 0, allPoints, oldPoints.length, newPoints.length);
            this.resultPoints = allPoints;
        }
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    @Override
    public String toString() {
        return this.text;
    }
}
