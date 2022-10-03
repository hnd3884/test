package com.google.zxing.oned.rss;

import com.google.zxing.oned.OneDReader;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import java.util.Iterator;
import com.google.zxing.NotFoundException;
import java.util.Collection;
import com.google.zxing.Result;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.common.BitArray;
import java.util.ArrayList;
import java.util.List;

public final class RSS14Reader extends AbstractRSSReader
{
    private static final int[] OUTSIDE_EVEN_TOTAL_SUBSET;
    private static final int[] INSIDE_ODD_TOTAL_SUBSET;
    private static final int[] OUTSIDE_GSUM;
    private static final int[] INSIDE_GSUM;
    private static final int[] OUTSIDE_ODD_WIDEST;
    private static final int[] INSIDE_ODD_WIDEST;
    private static final int[][] FINDER_PATTERNS;
    private final List<Pair> possibleLeftPairs;
    private final List<Pair> possibleRightPairs;
    
    public RSS14Reader() {
        this.possibleLeftPairs = new ArrayList<Pair>();
        this.possibleRightPairs = new ArrayList<Pair>();
    }
    
    @Override
    public Result decodeRow(final int rowNumber, final BitArray row, final Map<DecodeHintType, ?> hints) throws NotFoundException {
        final Pair leftPair = this.decodePair(row, false, rowNumber, hints);
        addOrTally(this.possibleLeftPairs, leftPair);
        row.reverse();
        final Pair rightPair = this.decodePair(row, true, rowNumber, hints);
        addOrTally(this.possibleRightPairs, rightPair);
        row.reverse();
        for (final Pair left : this.possibleLeftPairs) {
            if (left.getCount() > 1) {
                for (final Pair right : this.possibleRightPairs) {
                    if (right.getCount() > 1 && checkChecksum(left, right)) {
                        return constructResult(left, right);
                    }
                }
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    private static void addOrTally(final Collection<Pair> possiblePairs, final Pair pair) {
        if (pair == null) {
            return;
        }
        boolean found = false;
        for (final Pair other : possiblePairs) {
            if (other.getValue() == pair.getValue()) {
                other.incrementCount();
                found = true;
                break;
            }
        }
        if (!found) {
            possiblePairs.add(pair);
        }
    }
    
    @Override
    public void reset() {
        this.possibleLeftPairs.clear();
        this.possibleRightPairs.clear();
    }
    
    private static Result constructResult(final Pair leftPair, final Pair rightPair) {
        final long symbolValue = 4537077L * leftPair.getValue() + rightPair.getValue();
        final String text = String.valueOf(symbolValue);
        final StringBuilder buffer = new StringBuilder(14);
        for (int i = 13 - text.length(); i > 0; --i) {
            buffer.append('0');
        }
        buffer.append(text);
        int checkDigit = 0;
        for (int j = 0; j < 13; ++j) {
            final int digit = buffer.charAt(j) - '0';
            checkDigit += (((j & 0x1) == 0x0) ? (3 * digit) : digit);
        }
        checkDigit = 10 - checkDigit % 10;
        if (checkDigit == 10) {
            checkDigit = 0;
        }
        buffer.append(checkDigit);
        final ResultPoint[] leftPoints = leftPair.getFinderPattern().getResultPoints();
        final ResultPoint[] rightPoints = rightPair.getFinderPattern().getResultPoints();
        return new Result(String.valueOf(buffer.toString()), null, new ResultPoint[] { leftPoints[0], leftPoints[1], rightPoints[0], rightPoints[1] }, BarcodeFormat.RSS_14);
    }
    
    private static boolean checkChecksum(final Pair leftPair, final Pair rightPair) {
        final int leftFPValue = leftPair.getFinderPattern().getValue();
        final int rightFPValue = rightPair.getFinderPattern().getValue();
        if ((leftFPValue == 0 && rightFPValue == 8) || leftFPValue != 8 || rightFPValue == 0) {}
        final int checkValue = (leftPair.getChecksumPortion() + 16 * rightPair.getChecksumPortion()) % 79;
        int targetCheckValue = 9 * leftPair.getFinderPattern().getValue() + rightPair.getFinderPattern().getValue();
        if (targetCheckValue > 72) {
            --targetCheckValue;
        }
        if (targetCheckValue > 8) {
            --targetCheckValue;
        }
        return checkValue == targetCheckValue;
    }
    
    private Pair decodePair(final BitArray row, final boolean right, final int rowNumber, final Map<DecodeHintType, ?> hints) {
        try {
            final int[] startEnd = this.findFinderPattern(row, 0, right);
            final FinderPattern pattern = this.parseFoundFinderPattern(row, rowNumber, right, startEnd);
            final ResultPointCallback resultPointCallback = (hints == null) ? null : ((ResultPointCallback)hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK));
            if (resultPointCallback != null) {
                float center = (startEnd[0] + startEnd[1]) / 2.0f;
                if (right) {
                    center = row.getSize() - 1 - center;
                }
                resultPointCallback.foundPossibleResultPoint(new ResultPoint(center, (float)rowNumber));
            }
            final DataCharacter outside = this.decodeDataCharacter(row, pattern, true);
            final DataCharacter inside = this.decodeDataCharacter(row, pattern, false);
            return new Pair(1597 * outside.getValue() + inside.getValue(), outside.getChecksumPortion() + 4 * inside.getChecksumPortion(), pattern);
        }
        catch (final NotFoundException re) {
            return null;
        }
    }
    
    private DataCharacter decodeDataCharacter(final BitArray row, final FinderPattern pattern, final boolean outsideChar) throws NotFoundException {
        final int[] counters = this.getDataCharacterCounters();
        counters[1] = (counters[0] = 0);
        counters[3] = (counters[2] = 0);
        counters[5] = (counters[4] = 0);
        counters[7] = (counters[6] = 0);
        if (outsideChar) {
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
        final int numModules = outsideChar ? 16 : 15;
        final float elementWidth = AbstractRSSReader.count(counters) / (float)numModules;
        final int[] oddCounts = this.getOddCounts();
        final int[] evenCounts = this.getEvenCounts();
        final float[] oddRoundingErrors = this.getOddRoundingErrors();
        final float[] evenRoundingErrors = this.getEvenRoundingErrors();
        for (int k = 0; k < counters.length; ++k) {
            final float value = counters[k] / elementWidth;
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
        this.adjustOddEvenCounts(outsideChar, numModules);
        int oddSum = 0;
        int oddChecksumPortion = 0;
        for (int l = oddCounts.length - 1; l >= 0; --l) {
            oddChecksumPortion *= 9;
            oddChecksumPortion += oddCounts[l];
            oddSum += oddCounts[l];
        }
        int evenChecksumPortion = 0;
        int evenSum = 0;
        for (int m = evenCounts.length - 1; m >= 0; --m) {
            evenChecksumPortion *= 9;
            evenChecksumPortion += evenCounts[m];
            evenSum += evenCounts[m];
        }
        final int checksumPortion = oddChecksumPortion + 3 * evenChecksumPortion;
        if (outsideChar) {
            if ((oddSum & 0x1) != 0x0 || oddSum > 12 || oddSum < 4) {
                throw NotFoundException.getNotFoundInstance();
            }
            final int group = (12 - oddSum) / 2;
            final int oddWidest = RSS14Reader.OUTSIDE_ODD_WIDEST[group];
            final int evenWidest = 9 - oddWidest;
            final int vOdd = RSSUtils.getRSSvalue(oddCounts, oddWidest, false);
            final int vEven = RSSUtils.getRSSvalue(evenCounts, evenWidest, true);
            final int tEven = RSS14Reader.OUTSIDE_EVEN_TOTAL_SUBSET[group];
            final int gSum = RSS14Reader.OUTSIDE_GSUM[group];
            return new DataCharacter(vOdd * tEven + vEven + gSum, checksumPortion);
        }
        else {
            if ((evenSum & 0x1) != 0x0 || evenSum > 10 || evenSum < 4) {
                throw NotFoundException.getNotFoundInstance();
            }
            final int group = (10 - evenSum) / 2;
            final int oddWidest = RSS14Reader.INSIDE_ODD_WIDEST[group];
            final int evenWidest = 9 - oddWidest;
            final int vOdd = RSSUtils.getRSSvalue(oddCounts, oddWidest, true);
            final int vEven = RSSUtils.getRSSvalue(evenCounts, evenWidest, false);
            final int tOdd = RSS14Reader.INSIDE_ODD_TOTAL_SUBSET[group];
            final int gSum = RSS14Reader.INSIDE_GSUM[group];
            return new DataCharacter(vEven * tOdd + vOdd + gSum, checksumPortion);
        }
    }
    
    private int[] findFinderPattern(final BitArray row, int rowOffset, final boolean rightFinderPattern) throws NotFoundException {
        final int[] counters = this.getDecodeFinderCounters();
        counters[1] = (counters[0] = 0);
        counters[3] = (counters[2] = 0);
        final int width = row.getSize();
        boolean isWhite = false;
        while (rowOffset < width) {
            isWhite = !row.get(rowOffset);
            if (rightFinderPattern == isWhite) {
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
                    if (AbstractRSSReader.isFinderPattern(counters)) {
                        return new int[] { patternStart, x };
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
    
    private FinderPattern parseFoundFinderPattern(final BitArray row, final int rowNumber, final boolean right, final int[] startEnd) throws NotFoundException {
        boolean firstIsBlack;
        int firstElementStart;
        for (firstIsBlack = row.get(startEnd[0]), firstElementStart = startEnd[0] - 1; firstElementStart >= 0 && (firstIsBlack ^ row.get(firstElementStart)); --firstElementStart) {}
        ++firstElementStart;
        final int firstCounter = startEnd[0] - firstElementStart;
        final int[] counters = this.getDecodeFinderCounters();
        System.arraycopy(counters, 0, counters, 1, counters.length - 1);
        counters[0] = firstCounter;
        final int value = AbstractRSSReader.parseFinderValue(counters, RSS14Reader.FINDER_PATTERNS);
        int start = firstElementStart;
        int end = startEnd[1];
        if (right) {
            start = row.getSize() - 1 - start;
            end = row.getSize() - 1 - end;
        }
        return new FinderPattern(value, new int[] { firstElementStart, startEnd[1] }, start, end, rowNumber);
    }
    
    private void adjustOddEvenCounts(final boolean outsideChar, final int numModules) throws NotFoundException {
        final int oddSum = AbstractRSSReader.count(this.getOddCounts());
        final int evenSum = AbstractRSSReader.count(this.getEvenCounts());
        final int mismatch = oddSum + evenSum - numModules;
        final boolean oddParityBad = (oddSum & 0x1) == (outsideChar ? 1 : 0);
        final boolean evenParityBad = (evenSum & 0x1) == 0x1;
        boolean incrementOdd = false;
        boolean decrementOdd = false;
        boolean incrementEven = false;
        boolean decrementEven = false;
        if (outsideChar) {
            if (oddSum > 12) {
                decrementOdd = true;
            }
            else if (oddSum < 4) {
                incrementOdd = true;
            }
            if (evenSum > 12) {
                decrementEven = true;
            }
            else if (evenSum < 4) {
                incrementEven = true;
            }
        }
        else {
            if (oddSum > 11) {
                decrementOdd = true;
            }
            else if (oddSum < 5) {
                incrementOdd = true;
            }
            if (evenSum > 10) {
                decrementEven = true;
            }
            else if (evenSum < 4) {
                incrementEven = true;
            }
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
        OUTSIDE_EVEN_TOTAL_SUBSET = new int[] { 1, 10, 34, 70, 126 };
        INSIDE_ODD_TOTAL_SUBSET = new int[] { 4, 20, 48, 81 };
        OUTSIDE_GSUM = new int[] { 0, 161, 961, 2015, 2715 };
        INSIDE_GSUM = new int[] { 0, 336, 1036, 1516 };
        OUTSIDE_ODD_WIDEST = new int[] { 8, 6, 4, 3, 1 };
        INSIDE_ODD_WIDEST = new int[] { 2, 4, 6, 8 };
        FINDER_PATTERNS = new int[][] { { 3, 8, 2, 1 }, { 3, 5, 5, 1 }, { 3, 3, 7, 1 }, { 3, 1, 9, 1 }, { 2, 7, 4, 1 }, { 2, 5, 6, 1 }, { 2, 3, 8, 1 }, { 1, 5, 7, 1 }, { 1, 3, 9, 1 } };
    }
}
