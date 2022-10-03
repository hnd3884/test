package com.google.zxing.maxicode.decoder;

import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.google.zxing.FormatException;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;

public final class Decoder
{
    private static final int ALL = 0;
    private static final int EVEN = 1;
    private static final int ODD = 2;
    private final ReedSolomonDecoder rsDecoder;
    
    public Decoder() {
        this.rsDecoder = new ReedSolomonDecoder(GenericGF.MAXICODE_FIELD_64);
    }
    
    public DecoderResult decode(final BitMatrix bits) throws ChecksumException, FormatException {
        return this.decode(bits, null);
    }
    
    public DecoderResult decode(final BitMatrix bits, final Map<DecodeHintType, ?> hints) throws FormatException, ChecksumException {
        final BitMatrixParser parser = new BitMatrixParser(bits);
        final byte[] codewords = parser.readCodewords();
        this.correctErrors(codewords, 0, 10, 10, 0);
        final int mode = codewords[0] & 0xF;
        byte[] datawords = null;
        switch (mode) {
            case 2:
            case 3:
            case 4: {
                this.correctErrors(codewords, 20, 84, 40, 1);
                this.correctErrors(codewords, 20, 84, 40, 2);
                datawords = new byte[94];
                break;
            }
            case 5: {
                this.correctErrors(codewords, 20, 68, 56, 1);
                this.correctErrors(codewords, 20, 68, 56, 2);
                datawords = new byte[78];
                break;
            }
            default: {
                throw FormatException.getFormatInstance();
            }
        }
        System.arraycopy(codewords, 0, datawords, 0, 10);
        System.arraycopy(codewords, 20, datawords, 10, datawords.length - 10);
        return DecodedBitStreamParser.decode(datawords, mode);
    }
    
    private void correctErrors(final byte[] codewordBytes, final int start, final int dataCodewords, final int ecCodewords, final int mode) throws ChecksumException {
        final int codewords = dataCodewords + ecCodewords;
        final int divisor = (mode == 0) ? 1 : 2;
        final int[] codewordsInts = new int[codewords / divisor];
        for (int i = 0; i < codewords; ++i) {
            if (mode == 0 || i % 2 == mode - 1) {
                codewordsInts[i / divisor] = (codewordBytes[i + start] & 0xFF);
            }
        }
        try {
            this.rsDecoder.decode(codewordsInts, ecCodewords / divisor);
        }
        catch (final ReedSolomonException rse) {
            throw ChecksumException.getChecksumInstance();
        }
        for (int i = 0; i < dataCodewords; ++i) {
            if (mode == 0 || i % 2 == mode - 1) {
                codewordBytes[i + start] = (byte)codewordsInts[i / divisor];
            }
        }
    }
}
