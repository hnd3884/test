package com.google.zxing.pdf417.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;

public final class Decoder
{
    private static final int MAX_ERRORS = 3;
    private static final int MAX_EC_CODEWORDS = 512;
    
    public DecoderResult decode(final boolean[][] image) throws FormatException {
        final int dimension = image.length;
        final BitMatrix bits = new BitMatrix(dimension);
        for (int i = 0; i < dimension; ++i) {
            for (int j = 0; j < dimension; ++j) {
                if (image[j][i]) {
                    bits.set(j, i);
                }
            }
        }
        return this.decode(bits);
    }
    
    public DecoderResult decode(final BitMatrix bits) throws FormatException {
        final BitMatrixParser parser = new BitMatrixParser(bits);
        final int[] codewords = parser.readCodewords();
        if (codewords.length == 0) {
            throw FormatException.getFormatInstance();
        }
        final int ecLevel = parser.getECLevel();
        final int numECCodewords = 1 << ecLevel + 1;
        final int[] erasures = parser.getErasures();
        correctErrors(codewords, erasures, numECCodewords);
        verifyCodewordCount(codewords, numECCodewords);
        return DecodedBitStreamParser.decode(codewords);
    }
    
    private static void verifyCodewordCount(final int[] codewords, final int numECCodewords) throws FormatException {
        if (codewords.length < 4) {
            throw FormatException.getFormatInstance();
        }
        final int numberOfCodewords = codewords[0];
        if (numberOfCodewords > codewords.length) {
            throw FormatException.getFormatInstance();
        }
        if (numberOfCodewords == 0) {
            if (numECCodewords >= codewords.length) {
                throw FormatException.getFormatInstance();
            }
            codewords[0] = codewords.length - numECCodewords;
        }
    }
    
    private static int correctErrors(final int[] codewords, final int[] erasures, final int numECCodewords) throws FormatException {
        if (erasures.length > numECCodewords / 2 + 3 || numECCodewords < 0 || numECCodewords > 512) {
            throw FormatException.getFormatInstance();
        }
        final int result = 0;
        int numErasures = erasures.length;
        if (result > 0) {
            numErasures -= result;
        }
        if (numErasures > 3) {
            throw FormatException.getFormatInstance();
        }
        return result;
    }
}
