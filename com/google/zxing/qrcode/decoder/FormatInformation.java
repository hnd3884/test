package com.google.zxing.qrcode.decoder;

final class FormatInformation
{
    private static final int FORMAT_INFO_MASK_QR = 21522;
    private static final int[][] FORMAT_INFO_DECODE_LOOKUP;
    private static final int[] BITS_SET_IN_HALF_BYTE;
    private final ErrorCorrectionLevel errorCorrectionLevel;
    private final byte dataMask;
    
    private FormatInformation(final int formatInfo) {
        this.errorCorrectionLevel = ErrorCorrectionLevel.forBits(formatInfo >> 3 & 0x3);
        this.dataMask = (byte)(formatInfo & 0x7);
    }
    
    static int numBitsDiffering(int a, final int b) {
        a ^= b;
        return FormatInformation.BITS_SET_IN_HALF_BYTE[a & 0xF] + FormatInformation.BITS_SET_IN_HALF_BYTE[a >>> 4 & 0xF] + FormatInformation.BITS_SET_IN_HALF_BYTE[a >>> 8 & 0xF] + FormatInformation.BITS_SET_IN_HALF_BYTE[a >>> 12 & 0xF] + FormatInformation.BITS_SET_IN_HALF_BYTE[a >>> 16 & 0xF] + FormatInformation.BITS_SET_IN_HALF_BYTE[a >>> 20 & 0xF] + FormatInformation.BITS_SET_IN_HALF_BYTE[a >>> 24 & 0xF] + FormatInformation.BITS_SET_IN_HALF_BYTE[a >>> 28 & 0xF];
    }
    
    static FormatInformation decodeFormatInformation(final int maskedFormatInfo1, final int maskedFormatInfo2) {
        final FormatInformation formatInfo = doDecodeFormatInformation(maskedFormatInfo1, maskedFormatInfo2);
        if (formatInfo != null) {
            return formatInfo;
        }
        return doDecodeFormatInformation(maskedFormatInfo1 ^ 0x5412, maskedFormatInfo2 ^ 0x5412);
    }
    
    private static FormatInformation doDecodeFormatInformation(final int maskedFormatInfo1, final int maskedFormatInfo2) {
        int bestDifference = Integer.MAX_VALUE;
        int bestFormatInfo = 0;
        for (final int[] decodeInfo : FormatInformation.FORMAT_INFO_DECODE_LOOKUP) {
            final int targetInfo = decodeInfo[0];
            if (targetInfo == maskedFormatInfo1 || targetInfo == maskedFormatInfo2) {
                return new FormatInformation(decodeInfo[1]);
            }
            int bitsDifference = numBitsDiffering(maskedFormatInfo1, targetInfo);
            if (bitsDifference < bestDifference) {
                bestFormatInfo = decodeInfo[1];
                bestDifference = bitsDifference;
            }
            if (maskedFormatInfo1 != maskedFormatInfo2) {
                bitsDifference = numBitsDiffering(maskedFormatInfo2, targetInfo);
                if (bitsDifference < bestDifference) {
                    bestFormatInfo = decodeInfo[1];
                    bestDifference = bitsDifference;
                }
            }
        }
        if (bestDifference <= 3) {
            return new FormatInformation(bestFormatInfo);
        }
        return null;
    }
    
    ErrorCorrectionLevel getErrorCorrectionLevel() {
        return this.errorCorrectionLevel;
    }
    
    byte getDataMask() {
        return this.dataMask;
    }
    
    @Override
    public int hashCode() {
        return this.errorCorrectionLevel.ordinal() << 3 | this.dataMask;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof FormatInformation)) {
            return false;
        }
        final FormatInformation other = (FormatInformation)o;
        return this.errorCorrectionLevel == other.errorCorrectionLevel && this.dataMask == other.dataMask;
    }
    
    static {
        FORMAT_INFO_DECODE_LOOKUP = new int[][] { { 21522, 0 }, { 20773, 1 }, { 24188, 2 }, { 23371, 3 }, { 17913, 4 }, { 16590, 5 }, { 20375, 6 }, { 19104, 7 }, { 30660, 8 }, { 29427, 9 }, { 32170, 10 }, { 30877, 11 }, { 26159, 12 }, { 25368, 13 }, { 27713, 14 }, { 26998, 15 }, { 5769, 16 }, { 5054, 17 }, { 7399, 18 }, { 6608, 19 }, { 1890, 20 }, { 597, 21 }, { 3340, 22 }, { 2107, 23 }, { 13663, 24 }, { 12392, 25 }, { 16177, 26 }, { 14854, 27 }, { 9396, 28 }, { 8579, 29 }, { 11994, 30 }, { 11245, 31 } };
        BITS_SET_IN_HALF_BYTE = new int[] { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4 };
    }
}
