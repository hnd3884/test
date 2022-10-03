package com.google.zxing.qrcode.encoder;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Mode;

public final class QRCode
{
    public static final int NUM_MASK_PATTERNS = 8;
    private Mode mode;
    private ErrorCorrectionLevel ecLevel;
    private int version;
    private int matrixWidth;
    private int maskPattern;
    private int numTotalBytes;
    private int numDataBytes;
    private int numECBytes;
    private int numRSBlocks;
    private ByteMatrix matrix;
    
    public QRCode() {
        this.mode = null;
        this.ecLevel = null;
        this.version = -1;
        this.matrixWidth = -1;
        this.maskPattern = -1;
        this.numTotalBytes = -1;
        this.numDataBytes = -1;
        this.numECBytes = -1;
        this.numRSBlocks = -1;
        this.matrix = null;
    }
    
    public Mode getMode() {
        return this.mode;
    }
    
    public ErrorCorrectionLevel getECLevel() {
        return this.ecLevel;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public int getMatrixWidth() {
        return this.matrixWidth;
    }
    
    public int getMaskPattern() {
        return this.maskPattern;
    }
    
    public int getNumTotalBytes() {
        return this.numTotalBytes;
    }
    
    public int getNumDataBytes() {
        return this.numDataBytes;
    }
    
    public int getNumECBytes() {
        return this.numECBytes;
    }
    
    public int getNumRSBlocks() {
        return this.numRSBlocks;
    }
    
    public ByteMatrix getMatrix() {
        return this.matrix;
    }
    
    public int at(final int x, final int y) {
        final int value = this.matrix.get(x, y);
        if (value != 0 && value != 1) {
            throw new IllegalStateException("Bad value");
        }
        return value;
    }
    
    public boolean isValid() {
        return this.mode != null && this.ecLevel != null && this.version != -1 && this.matrixWidth != -1 && this.maskPattern != -1 && this.numTotalBytes != -1 && this.numDataBytes != -1 && this.numECBytes != -1 && this.numRSBlocks != -1 && isValidMaskPattern(this.maskPattern) && this.numTotalBytes == this.numDataBytes + this.numECBytes && this.matrix != null && this.matrixWidth == this.matrix.getWidth() && this.matrix.getWidth() == this.matrix.getHeight();
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(200);
        result.append("<<\n");
        result.append(" mode: ");
        result.append(this.mode);
        result.append("\n ecLevel: ");
        result.append(this.ecLevel);
        result.append("\n version: ");
        result.append(this.version);
        result.append("\n matrixWidth: ");
        result.append(this.matrixWidth);
        result.append("\n maskPattern: ");
        result.append(this.maskPattern);
        result.append("\n numTotalBytes: ");
        result.append(this.numTotalBytes);
        result.append("\n numDataBytes: ");
        result.append(this.numDataBytes);
        result.append("\n numECBytes: ");
        result.append(this.numECBytes);
        result.append("\n numRSBlocks: ");
        result.append(this.numRSBlocks);
        if (this.matrix == null) {
            result.append("\n matrix: null\n");
        }
        else {
            result.append("\n matrix:\n");
            result.append(this.matrix.toString());
        }
        result.append(">>\n");
        return result.toString();
    }
    
    public void setMode(final Mode value) {
        this.mode = value;
    }
    
    public void setECLevel(final ErrorCorrectionLevel value) {
        this.ecLevel = value;
    }
    
    public void setVersion(final int value) {
        this.version = value;
    }
    
    public void setMatrixWidth(final int value) {
        this.matrixWidth = value;
    }
    
    public void setMaskPattern(final int value) {
        this.maskPattern = value;
    }
    
    public void setNumTotalBytes(final int value) {
        this.numTotalBytes = value;
    }
    
    public void setNumDataBytes(final int value) {
        this.numDataBytes = value;
    }
    
    public void setNumECBytes(final int value) {
        this.numECBytes = value;
    }
    
    public void setNumRSBlocks(final int value) {
        this.numRSBlocks = value;
    }
    
    public void setMatrix(final ByteMatrix value) {
        this.matrix = value;
    }
    
    public static boolean isValidMaskPattern(final int maskPattern) {
        return maskPattern >= 0 && maskPattern < 8;
    }
}
