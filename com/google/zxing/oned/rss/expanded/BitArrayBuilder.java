package com.google.zxing.oned.rss.expanded;

import com.google.zxing.common.BitArray;
import java.util.List;

final class BitArrayBuilder
{
    private BitArrayBuilder() {
    }
    
    static BitArray buildBitArray(final List<ExpandedPair> pairs) {
        int charNumber = (pairs.size() << 1) - 1;
        if (pairs.get(pairs.size() - 1).getRightChar() == null) {
            --charNumber;
        }
        final int size = 12 * charNumber;
        final BitArray binary = new BitArray(size);
        int accPos = 0;
        final ExpandedPair firstPair = pairs.get(0);
        final int firstValue = firstPair.getRightChar().getValue();
        for (int i = 11; i >= 0; --i) {
            if ((firstValue & 1 << i) != 0x0) {
                binary.set(accPos);
            }
            ++accPos;
        }
        for (int i = 1; i < pairs.size(); ++i) {
            final ExpandedPair currentPair = pairs.get(i);
            final int leftValue = currentPair.getLeftChar().getValue();
            for (int j = 11; j >= 0; --j) {
                if ((leftValue & 1 << j) != 0x0) {
                    binary.set(accPos);
                }
                ++accPos;
            }
            if (currentPair.getRightChar() != null) {
                final int rightValue = currentPair.getRightChar().getValue();
                for (int k = 11; k >= 0; --k) {
                    if ((rightValue & 1 << k) != 0x0) {
                        binary.set(accPos);
                    }
                    ++accPos;
                }
            }
        }
        return binary;
    }
}
