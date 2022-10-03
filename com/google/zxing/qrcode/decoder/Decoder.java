package com.google.zxing.qrcode.decoder;

import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.FormatException;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;

public final class Decoder
{
    private final ReedSolomonDecoder rsDecoder;
    
    public Decoder() {
        this.rsDecoder = new ReedSolomonDecoder(GenericGF.QR_CODE_FIELD_256);
    }
    
    public DecoderResult decode(final boolean[][] image) throws ChecksumException, FormatException {
        return this.decode(image, null);
    }
    
    public DecoderResult decode(final boolean[][] image, final Map<DecodeHintType, ?> hints) throws ChecksumException, FormatException {
        final int dimension = image.length;
        final BitMatrix bits = new BitMatrix(dimension);
        for (int i = 0; i < dimension; ++i) {
            for (int j = 0; j < dimension; ++j) {
                if (image[i][j]) {
                    bits.set(j, i);
                }
            }
        }
        return this.decode(bits, hints);
    }
    
    public DecoderResult decode(final BitMatrix bits) throws ChecksumException, FormatException {
        return this.decode(bits, null);
    }
    
    public DecoderResult decode(final BitMatrix bits, final Map<DecodeHintType, ?> hints) throws FormatException, ChecksumException {
        final BitMatrixParser parser = new BitMatrixParser(bits);
        final Version version = parser.readVersion();
        final ErrorCorrectionLevel ecLevel = parser.readFormatInformation().getErrorCorrectionLevel();
        final byte[] codewords = parser.readCodewords();
        final DataBlock[] dataBlocks = DataBlock.getDataBlocks(codewords, version, ecLevel);
        int totalBytes = 0;
        for (final DataBlock dataBlock : dataBlocks) {
            totalBytes += dataBlock.getNumDataCodewords();
        }
        final byte[] resultBytes = new byte[totalBytes];
        int resultOffset = 0;
        for (final DataBlock dataBlock2 : dataBlocks) {
            final byte[] codewordBytes = dataBlock2.getCodewords();
            final int numDataCodewords = dataBlock2.getNumDataCodewords();
            this.correctErrors(codewordBytes, numDataCodewords);
            for (int i = 0; i < numDataCodewords; ++i) {
                resultBytes[resultOffset++] = codewordBytes[i];
            }
        }
        return DecodedBitStreamParser.decode(resultBytes, version, ecLevel, hints);
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
