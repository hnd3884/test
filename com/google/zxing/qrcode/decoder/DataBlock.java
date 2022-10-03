package com.google.zxing.qrcode.decoder;

final class DataBlock
{
    private final int numDataCodewords;
    private final byte[] codewords;
    
    private DataBlock(final int numDataCodewords, final byte[] codewords) {
        this.numDataCodewords = numDataCodewords;
        this.codewords = codewords;
    }
    
    static DataBlock[] getDataBlocks(final byte[] rawCodewords, final Version version, final ErrorCorrectionLevel ecLevel) {
        if (rawCodewords.length != version.getTotalCodewords()) {
            throw new IllegalArgumentException();
        }
        final Version.ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
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
                final int numBlockCodewords = ecBlocks.getECCodewordsPerBlock() + numDataCodewords;
                result[numResultBlocks++] = new DataBlock(numDataCodewords, new byte[numBlockCodewords]);
            }
        }
        final int shorterBlocksTotalCodewords = result[0].codewords.length;
        int longerBlocksStartAt;
        for (longerBlocksStartAt = result.length - 1; longerBlocksStartAt >= 0; --longerBlocksStartAt) {
            final int numCodewords = result[longerBlocksStartAt].codewords.length;
            if (numCodewords == shorterBlocksTotalCodewords) {
                break;
            }
        }
        ++longerBlocksStartAt;
        final int shorterBlocksNumDataCodewords = shorterBlocksTotalCodewords - ecBlocks.getECCodewordsPerBlock();
        int rawCodewordsOffset = 0;
        for (int i = 0; i < shorterBlocksNumDataCodewords; ++i) {
            for (int j = 0; j < numResultBlocks; ++j) {
                result[j].codewords[i] = rawCodewords[rawCodewordsOffset++];
            }
        }
        for (int k = longerBlocksStartAt; k < numResultBlocks; ++k) {
            result[k].codewords[shorterBlocksNumDataCodewords] = rawCodewords[rawCodewordsOffset++];
        }
        for (int max = result[0].codewords.length, l = shorterBlocksNumDataCodewords; l < max; ++l) {
            for (int m = 0; m < numResultBlocks; ++m) {
                final int iOffset = (m < longerBlocksStartAt) ? l : (l + 1);
                result[m].codewords[iOffset] = rawCodewords[rawCodewordsOffset++];
            }
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
