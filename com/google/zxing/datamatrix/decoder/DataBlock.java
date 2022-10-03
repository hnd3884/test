package com.google.zxing.datamatrix.decoder;

final class DataBlock
{
    private final int numDataCodewords;
    private final byte[] codewords;
    
    private DataBlock(final int numDataCodewords, final byte[] codewords) {
        this.numDataCodewords = numDataCodewords;
        this.codewords = codewords;
    }
    
    static DataBlock[] getDataBlocks(final byte[] rawCodewords, final Version version) {
        final Version.ECBlocks ecBlocks = version.getECBlocks();
        int totalBlocks = 0;
        final Version.ECB[] arr$;
        final Version.ECB[] ecBlockArray = arr$ = ecBlocks.getECBlocks();
        for (final Version.ECB ecBlock : arr$) {
            totalBlocks += ecBlock.getCount();
        }
        final DataBlock[] result = new DataBlock[totalBlocks];
        int numResultBlocks = 0;
        for (final Version.ECB ecBlock2 : ecBlockArray) {
            for (int i = 0; i < ecBlock2.getCount(); ++i) {
                final int numDataCodewords = ecBlock2.getDataCodewords();
                final int numBlockCodewords = ecBlocks.getECCodewords() + numDataCodewords;
                result[numResultBlocks++] = new DataBlock(numDataCodewords, new byte[numBlockCodewords]);
            }
        }
        final int longerBlocksTotalCodewords = result[0].codewords.length;
        final int longerBlocksNumDataCodewords = longerBlocksTotalCodewords - ecBlocks.getECCodewords();
        final int shorterBlocksNumDataCodewords = longerBlocksNumDataCodewords - 1;
        int rawCodewordsOffset = 0;
        for (int i = 0; i < shorterBlocksNumDataCodewords; ++i) {
            for (int j = 0; j < numResultBlocks; ++j) {
                result[j].codewords[i] = rawCodewords[rawCodewordsOffset++];
            }
        }
        final boolean specialVersion = version.getVersionNumber() == 24;
        for (int numLongerBlocks = specialVersion ? 8 : numResultBlocks, k = 0; k < numLongerBlocks; ++k) {
            result[k].codewords[longerBlocksNumDataCodewords - 1] = rawCodewords[rawCodewordsOffset++];
        }
        for (int max = result[0].codewords.length, l = longerBlocksNumDataCodewords; l < max; ++l) {
            for (int m = 0; m < numResultBlocks; ++m) {
                final int iOffset = (specialVersion && m > 7) ? (l - 1) : l;
                result[m].codewords[iOffset] = rawCodewords[rawCodewordsOffset++];
            }
        }
        if (rawCodewordsOffset != rawCodewords.length) {
            throw new IllegalArgumentException();
        }
        return result;
    }
    
    int getNumDataCodewords() {
        return this.numDataCodewords;
    }
    
    byte[] getCodewords() {
        return this.codewords;
    }
}
