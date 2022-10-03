package com.google.zxing.oned.rss.expanded;

import com.google.zxing.oned.rss.RSSUtils;
import com.google.zxing.oned.OneDReader;
import com.google.zxing.oned.rss.FinderPattern;
import com.google.zxing.oned.rss.DataCharacter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.oned.rss.expanded.decoders.AbstractExpandedDecoder;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.common.BitArray;
import java.util.ArrayList;
import java.util.List;
import com.google.zxing.oned.rss.AbstractRSSReader;

public final class RSSExpandedReader extends AbstractRSSReader
{
    private static final int[] SYMBOL_WIDEST;
    private static final int[] EVEN_TOTAL_SUBSET;
    private static final int[] GSUM;
    private static final int[][] FINDER_PATTERNS;
    private static final int[][] WEIGHTS;
    private static final int FINDER_PAT_A = 0;
    private static final int FINDER_PAT_B = 1;
    private static final int FINDER_PAT_C = 2;
    private static final int FINDER_PAT_D = 3;
    private static final int FINDER_PAT_E = 4;
    private static final int FINDER_PAT_F = 5;
    private static final int[][] FINDER_PATTERN_SEQUENCES;
    private static final int LONGEST_SEQUENCE_SIZE;
    private static final int MAX_PAIRS = 11;
    private final List<ExpandedPair> pairs;
    private final int[] startEnd;
    private final int[] currentSequence;
    
    public RSSExpandedReader() {
        this.pairs = new ArrayList<ExpandedPair>(11);
        this.startEnd = new int[2];
        this.currentSequence = new int[RSSExpandedReader.LONGEST_SEQUENCE_SIZE];
    }
    
    @Override
    public Result decodeRow(final int rowNumber, final BitArray row, final Map<DecodeHintType, ?> hints) throws NotFoundException {
        this.reset();
        this.decodeRow2pairs(rowNumber, row);
        return constructResult(this.pairs);
    }
    
    @Override
    public void reset() {
        this.pairs.clear();
    }
    
    List<ExpandedPair> decodeRow2pairs(final int rowNumber, final BitArray row) throws NotFoundException {
        while (true) {
            final ExpandedPair nextPair = this.retrieveNextPair(row, this.pairs, rowNumber);
            this.pairs.add(nextPair);
            if (nextPair.mayBeLast()) {
                if (this.checkChecksum()) {
                    return this.pairs;
                }
                if (nextPair.mustBeLast()) {
                    throw NotFoundException.getNotFoundInstance();
                }
                continue;
            }
        }
    }
    
    private static Result constructResult(final List<ExpandedPair> pairs) throws NotFoundException {
        final BitArray binary = BitArrayBuilder.buildBitArray(pairs);
        final AbstractExpandedDecoder decoder = AbstractExpandedDecoder.createDecoder(binary);
        final String resultingString = decoder.parseInformation();
        final ResultPoint[] firstPoints = pairs.get(0).getFinderPattern().getResultPoints();
        final ResultPoint[] lastPoints = pairs.get(pairs.size() - 1).getFinderPattern().getResultPoints();
        return new Result(resultingString, null, new ResultPoint[] { firstPoints[0], firstPoints[1], lastPoints[0], lastPoints[1] }, BarcodeFormat.RSS_EXPANDED);
    }
    
    private boolean checkChecksum() {
        final ExpandedPair firstPair = this.pairs.get(0);
        final DataCharacter checkCharacter = firstPair.getLeftChar();
        final DataCharacter firstCharacter = firstPair.getRightChar();
        int checksum = firstCharacter.getChecksumPortion();
        int s = 2;
        for (int i = 1; i < this.pairs.size(); ++i) {
            final ExpandedPair currentPair = this.pairs.get(i);
            checksum += currentPair.getLeftChar().getChecksumPortion();
            ++s;
            final DataCharacter currentRightChar = currentPair.getRightChar();
            if (currentRightChar != null) {
                checksum += currentRightChar.getChecksumPortion();
                ++s;
            }
        }
        checksum %= 211;
        final int checkCharacterValue = 211 * (s - 4) + checksum;
        return checkCharacterValue == checkCharacter.getValue();
    }
    
    private static int getNextSecondBar(final BitArray row, final int initialPos) {
        int currentPos;
        if (row.get(initialPos)) {
            currentPos = row.getNextUnset(initialPos);
            currentPos = row.getNextSet(currentPos);
        }
        else {
            currentPos = row.getNextSet(initialPos);
            currentPos = row.getNextUnset(currentPos);
        }
        return currentPos;
    }
    
    ExpandedPair retrieveNextPair(final BitArray row, final List<ExpandedPair> previousPairs, final int rowNumber) throws NotFoundException {
        final boolean isOddPattern = previousPairs.size() % 2 == 0;
        boolean keepFinding = true;
        int forcedOffset = -1;
        FinderPattern pattern;
        do {
            this.findNextPair(row, previousPairs, forcedOffset);
            pattern = this.parseFoundFinderPattern(row, rowNumber, isOddPattern);
            if (pattern == null) {
                forcedOffset = getNextSecondBar(row, this.startEnd[0]);
            }
            else {
                keepFinding = false;
            }
        } while (keepFinding);
        final boolean mayBeLast = this.checkPairSequence(previousPairs, pattern);
        final DataCharacter leftChar = this.decodeDataCharacter(row, pattern, isOddPattern, true);
        DataCharacter rightChar;
        try {
            rightChar = this.decodeDataCharacter(row, pattern, isOddPattern, false);
        }
        catch (final NotFoundException nfe) {
            if (!mayBeLast) {
                throw nfe;
            }
            rightChar = null;
        }
        return new ExpandedPair(leftChar, rightChar, pattern, mayBeLast);
    }
    
    private boolean checkPairSequence(final List<ExpandedPair> previousPairs, final FinderPattern pattern) throws NotFoundException {
        final int currentSequenceLength = previousPairs.size() + 1;
        if (currentSequenceLength > this.currentSequence.length) {
            throw NotFoundException.getNotFoundInstance();
        }
        for (int pos = 0; pos < previousPairs.size(); ++pos) {
            this.currentSequence[pos] = previousPairs.get(pos).getFinderPattern().getValue();
        }
        this.currentSequence[currentSequenceLength - 1] = pattern.getValue();
        for (final int[] validSequence : RSSExpandedReader.FINDER_PATTERN_SEQUENCES) {
            if (validSequence.length >= currentSequenceLength) {
                boolean valid = true;
                for (int pos2 = 0; pos2 < currentSequenceLength; ++pos2) {
                    if (this.currentSequence[pos2] != validSequence[pos2]) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    return currentSequenceLength == validSequence.length;
                }
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    private void findNextPair(final BitArray row, final List<ExpandedPair> previousPairs, final int forcedOffset) throws NotFoundException {
        final int[] counters = this.getDecodeFinderCounters();
        counters[1] = (counters[0] = 0);
        counters[3] = (counters[2] = 0);
        final int width = row.getSize();
        int rowOffset;
        if (forcedOffset >= 0) {
            rowOffset = forcedOffset;
        }
        else if (previousPairs.isEmpty()) {
            rowOffset = 0;
        }
        else {
            final ExpandedPair lastPair = previousPairs.get(previousPairs.size() - 1);
            rowOffset = lastPair.getFinderPattern().getStartEnd()[1];
        }
        final boolean searchingEvenPair = previousPairs.size() % 2 != 0;
        boolean isWhite = false;
        while (rowOffset < width) {
            isWhite = !row.get(rowOffset);
            if (!isWhite) {
                break;
            }
            ++rowOffset;
        }
        int counterPosition = 0;
        int patternStart = rowOffset;
        for (int x = rowOffset; x < width; ++x) {
            if (row.get(x) ^ isWhite) {
                final int[] array = counters;
                final int n = counterPosition;
                ++array[n];
            }
            else {
                if (counterPosition == 3) {
                    if (searchingEvenPair) {
                        reverseCounters(counters);
                    }
                    if (AbstractRSSReader.isFinderPattern(counters)) {
                        this.startEnd[0] = patternStart;
                        this.startEnd[1] = x;
                        return;
                    }
                    if (searchingEvenPair) {
                        reverseCounters(counters);
                    }
                    patternStart += counters[0] + counters[1];
                    counters[0] = counters[2];
                    counters[1] = counters[3];
                    counters[3] = (counters[2] = 0);
                    --counterPosition;
                }
                else {
                    ++counterPosition;
                }
                counters[counterPosition] = 1;
                isWhite = !isWhite;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    private static void reverseCounters(final int[] counters) {
        for (int length = counters.length, i = 0; i < length / 2; ++i) {
            final int tmp = counters[i];
            counters[i] = counters[length - i - 1];
            counters[length - i - 1] = tmp;
        }
    }
    
    private FinderPattern parseFoundFinderPattern(final BitArray row, final int rowNumber, final boolean oddPattern) {
        int firstCounter;
        int start;
        int end;
        if (oddPattern) {
            int firstElementStart;
            for (firstElementStart = this.startEnd[0] - 1; firstElementStart >= 0 && !row.get(firstElementStart); --firstElementStart) {}
            ++firstElementStart;
            firstCounter = this.startEnd[0] - firstElementStart;
            start = firstElementStart;
            end = this.startEnd[1];
        }
        else {
            start = this.startEnd[0];
            final int firstElementStart = end = row.getNextUnset(this.startEnd[1] + 1);
            firstCounter = end - this.startEnd[1];
        }
        final int[] counters = this.getDecodeFinderCounters();
        System.arraycopy(counters, 0, counters, 1, counters.length - 1);
        counters[0] = firstCounter;
        int value;
        try {
            value = AbstractRSSReader.parseFinderValue(counters, RSSExpandedReader.FINDER_PATTERNS);
        }
        catch (final NotFoundException nfe) {
            return null;
        }
        return new FinderPattern(value, new int[] { start, end }, start, end, rowNumber);
    }
    
    DataCharacter decodeDataCharacter(final BitArray row, final FinderPattern pattern, final boolean isOddPattern, final boolean leftChar) throws NotFoundException {
        final int[] counters = this.getDataCharacterCounters();
        counters[1] = (counters[0] = 0);
        counters[3] = (counters[2] = 0);
        counters[5] = (counters[4] = 0);
        counters[7] = (counters[6] = 0);
        if (leftChar) {
            OneDReader.recordPatternInReverse(row, pattern.getStartEnd()[0], counters);
        }
        else {
            OneDReader.recordPattern(row, pattern.getStartEnd()[1] + 1, counters);
            for (int i = 0, j = counters.length - 1; i < j; ++i, --j) {
                final int temp = counters[i];
                counters[i] = counters[j];
                counters[j] = temp;
            }
        }
        final int numModules = 17;
        final float elementWidth = AbstractRSSReader.count(counters) / (float)numModules;
        final int[] oddCounts = this.getOddCounts();
        final int[] evenCounts = this.getEvenCounts();
        final float[] oddRoundingErrors = this.getOddRoundingErrors();
        final float[] evenRoundingErrors = this.getEvenRoundingErrors();
        for (int k = 0; k < counters.length; ++k) {
            final float value = 1.0f * counters[k] / elementWidth;
            int count = (int)(value + 0.5f);
            if (count < 1) {
                count = 1;
            }
            else if (count > 8) {
                count = 8;
            }
            final int offset = k >> 1;
            if ((k & 0x1) == 0x0) {
                oddCounts[offset] = count;
                oddRoundingErrors[offset] = value - count;
            }
            else {
                evenCounts[offset] = count;
                evenRoundingErrors[offset] = value - count;
            }
        }
        this.adjustOddEvenCounts(numModules);
        final int weightRowNumber = 4 * pattern.getValue() + (isOddPattern ? 0 : 2) + (leftChar ? 0 : 1) - 1;
        int oddSum = 0;
        int oddChecksumPortion = 0;
        for (int l = oddCounts.length - 1; l >= 0; --l) {
            if (isNotA1left(pattern, isOddPattern, leftChar)) {
                final int weight = RSSExpandedReader.WEIGHTS[weightRowNumber][2 * l];
                oddChecksumPortion += oddCounts[l] * weight;
            }
            oddSum += oddCounts[l];
        }
        int evenChecksumPortion = 0;
        int evenSum = 0;
        for (int m = evenCounts.length - 1; m >= 0; --m) {
            if (isNotA1left(pattern, isOddPattern, leftChar)) {
                final int weight2 = RSSExpandedReader.WEIGHTS[weightRowNumber][2 * m + 1];
                evenChecksumPortion += evenCounts[m] * weight2;
            }
            evenSum += evenCounts[m];
        }
        final int checksumPortion = oddChecksumPortion + evenChecksumPortion;
        if ((oddSum & 0x1) != 0x0 || oddSum > 13 || oddSum < 4) {
            throw NotFoundException.getNotFoundInstance();
        }
        final int group = (13 - oddSum) / 2;
        final int oddWidest = RSSExpandedReader.SYMBOL_WIDEST[group];
        final int evenWidest = 9 - oddWidest;
        final int vOdd = RSSUtils.getRSSvalue(oddCounts, oddWidest, true);
        final int vEven = RSSUtils.getRSSvalue(evenCounts, evenWidest, false);
        final int tEven = RSSExpandedReader.EVEN_TOTAL_SUBSET[group];
        final int gSum = RSSExpandedReader.GSUM[group];
        final int value2 = vOdd * tEven + vEven + gSum;
        return new DataCharacter(value2, checksumPortion);
    }
    
    private static boolean isNotA1left(final FinderPattern pattern, final boolean isOddPattern, final boolean leftChar) {
        return pattern.getValue() != 0 || !isOddPattern || !leftChar;
    }
    
    private void adjustOddEvenCounts(final int numModules) throws NotFoundException {
        final int oddSum = AbstractRSSReader.count(this.getOddCounts());
        final int evenSum = AbstractRSSReader.count(this.getEvenCounts());
        final int mismatch = oddSum + evenSum - numModules;
        final boolean oddParityBad = (oddSum & 0x1) == 0x1;
        final boolean evenParityBad = (evenSum & 0x1) == 0x0;
        boolean incrementOdd = false;
        boolean decrementOdd = false;
        if (oddSum > 13) {
            decrementOdd = true;
        }
        else if (oddSum < 4) {
            incrementOdd = true;
        }
        boolean incrementEven = false;
        boolean decrementEven = false;
        if (evenSum > 13) {
            decrementEven = true;
        }
        else if (evenSum < 4) {
            incrementEven = true;
        }
        if (mismatch == 1) {
            if (oddParityBad) {
                if (evenParityBad) {
                    throw NotFoundException.getNotFoundInstance();
                }
                decrementOdd = true;
            }
            else {
                if (!evenParityBad) {
                    throw NotFoundException.getNotFoundInstance();
                }
                decrementEven = true;
            }
        }
        else if (mismatch == -1) {
            if (oddParityBad) {
                if (evenParityBad) {
                    throw NotFoundException.getNotFoundInstance();
                }
                incrementOdd = true;
            }
            else {
                if (!evenParityBad) {
                    throw NotFoundException.getNotFoundInstance();
                }
                incrementEven = true;
            }
        }
        else {
            if (mismatch != 0) {
                throw NotFoundException.getNotFoundInstance();
            }
            if (oddParityBad) {
                if (!evenParityBad) {
                    throw NotFoundException.getNotFoundInstance();
                }
                if (oddSum < evenSum) {
                    incrementOdd = true;
                    decrementEven = true;
                }
                else {
                    decrementOdd = true;
                    incrementEven = true;
                }
            }
            else if (evenParityBad) {
                throw NotFoundException.getNotFoundInstance();
            }
        }
        if (incrementOdd) {
            if (decrementOdd) {
                throw NotFoundException.getNotFoundInstance();
            }
            AbstractRSSReader.increment(this.getOddCounts(), this.getOddRoundingErrors());
        }
        if (decrementOdd) {
            AbstractRSSReader.decrement(this.getOddCounts(), this.getOddRoundingErrors());
        }
        if (incrementEven) {
            if (decrementEven) {
                throw NotFoundException.getNotFoundInstance();
            }
            AbstractRSSReader.increment(this.getEvenCounts(), this.getOddRoundingErrors());
        }
        if (decrementEven) {
            AbstractRSSReader.decrement(this.getEvenCounts(), this.getEvenRoundingErrors());
        }
    }
    
    static {
        SYMBOL_WIDEST = new int[] { 7, 5, 4, 3, 1 };
        EVEN_TOTAL_SUBSET = new int[] { 4, 20, 52, 104, 204 };
        GSUM = new int[] { 0, 348, 1388, 2948, 3988 };
        FINDER_PATTERNS = new int[][] { { 1, 8, 4, 1 }, { 3, 6, 4, 1 }, { 3, 4, 6, 1 }, { 3, 2, 8, 1 }, { 2, 6, 5, 1 }, { 2, 2, 9, 1 } };
        WEIGHTS = new int[][] { { 1, 3, 9, 27, 81, 32, 96, 77 }, { 20, 60, 180, 118, 143, 7, 21, 63 }, { 189, 145, 13, 39, 117, 140, 209, 205 }, { 193, 157, 49, 147, 19, 57, 171, 91 }, { 62, 186, 136, 197, 169, 85, 44, 132 }, { 185, 133, 188, 142, 4, 12, 36, 108 }, { 113, 128, 173, 97, 80, 29, 87, 50 }, { 150, 28, 84, 41, 123, 158, 52, 156 }, { 46, 138, 203, 187, 139, 206, 196, 166 }, { 76, 17, 51, 153, 37, 111, 122, 155 }, { 43, 129, 176, 106, 107, 110, 119, 146 }, { 16, 48, 144, 10, 30, 90, 59, 177 }, { 109, 116, 137, 200, 178, 112, 125, 164 }, { 70, 210, 208, 202, 184, 130, 179, 115 }, { 134, 191, 151, 31, 93, 68, 204, 190 }, { 148, 22, 66, 198, 172, 94, 71, 2 }, { 6, 18, 54, 162, 64, 192, 154, 40 }, { 120, 149, 25, 75, 14, 42, 126, 167 }, { 79, 26, 78, 23, 69, 207, 199, 175 }, { 103, 98, 83, 38, 114, 131, 182, 124 }, { 161, 61, 183, 127, 170, 88, 53, 159 }, { 55, 165, 73, 8, 24, 72, 5, 15 }, { 45, 135, 194, 160, 58, 174, 100, 89 } };
        FINDER_PATTERN_SEQUENCES = new int[][] { { 0, 0 }, { 0, 1, 1 }, { 0, 2, 1, 3 }, { 0, 4, 1, 3, 2 }, { 0, 4, 1, 3, 3, 5 }, { 0, 4, 1, 3, 4, 5, 5 }, { 0, 0, 1, 1, 2, 2, 3, 3 }, { 0, 0, 1, 1, 2, 2, 3, 4, 4 }, { 0, 0, 1, 1, 2, 2, 3, 4, 5, 5 }, { 0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5 } };
        LONGEST_SEQUENCE_SIZE = RSSExpandedReader.FINDER_PATTERN_SEQUENCES[RSSExpandedReader.FINDER_PATTERN_SEQUENCES.length - 1].length;
    }
}
