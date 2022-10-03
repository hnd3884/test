package com.google.zxing.datamatrix.decoder;

import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;

public final class Decoder
{
    private final ReedSolomonDecoder rsDecoder;
    
    public Decoder() {
        this.rsDecoder = new ReedSolomonDecoder(GenericGF.DATA_MATRIX_FIELD_256);
    }
    
    public DecoderResult decode(final boolean[][] image) throws FormatException, ChecksumException {
        final int dimension = image.length;
        final BitMatrix bits = new BitMatrix(dimension);
        for (int i = 0; i < dimension; ++i) {
            for (int j = 0; j < dimension; ++j) {
                if (image[i][j]) {
                    bits.set(j, i);
                }
            }
        }
        return this.decode(bits);
    }
    
    public DecoderResult decode(final BitMatrix bits) throws FormatException, ChecksumException {
        final BitMatrixParser parser = new BitMatrixParser(bits);
        final Version version = parser.getVersion();
        final byte[] codewords = parser.readCodewords();
        final DataBlock[] dataBlocks = DataBlock.getDataBlocks(codewords, version);
        final int dataBlocksCount = dataBlocks.length;
        int totalBytes = 0;
        for (int i = 0; i < dataBlocksCount; ++i) {
            totalBytes += dataBlocks[i].getNumDataCodewords();
        }
        final byte[] resultBytes = new byte[totalBytes];
        for (int j = 0; j < dataBlocksCount; ++j) {
            final DataBlock dataBlock = dataBlocks[j];
            final byte[] codewordBytes = dataBlock.getCodewords();
            final int numDataCodewords = dataBlock.getNumDataCodewords();
            this.correctErrors(codewordBytes, numDataCodewords);
            for (int k = 0; k < numDataCodewords; ++k) {
                resultBytes[k * dataBlocksCount + j] = codewordBytes[k];
            }
        }
        return DecodedBitStreamParser.decode(resultBytes);
    }
    
    private void correctErrors(final byte[] codewordBytes, final int numDataCodewords) throws ChecksumException {
        final int numCodewords = codewordBytes.length;
        final int[] codewordsInts = new int[numCodewords];
        for (int i = 0; i < numCodewords; ++i) {
            codewordsInts[i] = (codewordBytes[i] & 0xFF);
        }
        final int numECCodewords = codewordBytes.length - numDataCodewords;
        try {
            this.rsDecoder.decode(codewordsInts, numECCodewords);
        }
        catch (final ReedSolomonException rse) {
            throw ChecksumException.getChecksumInstance();
        }
        for (int j = 0; j < numDataCodewords; ++j) {
            codewordBytes[j] = (byte)codewordsInts[j];
        }
    }
}
