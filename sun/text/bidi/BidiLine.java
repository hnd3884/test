package sun.text.bidi;

import java.util.Arrays;
import java.text.Bidi;

public final class BidiLine
{
    static void setTrailingWSStart(final BidiBase bidiBase) {
        final byte[] dirProps = bidiBase.dirProps;
        final byte[] levels = bidiBase.levels;
        int length = bidiBase.length;
        final byte paraLevel = bidiBase.paraLevel;
        if (BidiBase.NoContextRTL(dirProps[length - 1]) == 7) {
            bidiBase.trailingWSStart = length;
            return;
        }
        while (length > 0 && (BidiBase.DirPropFlagNC(dirProps[length - 1]) & BidiBase.MASK_WS) != 0x0) {
            --length;
        }
        while (length > 0 && levels[length - 1] == paraLevel) {
            --length;
        }
        bidiBase.trailingWSStart = length;
    }
    
    public static Bidi setLine(final Bidi bidi, final BidiBase paraBidi, final Bidi bidi2, final BidiBase trailingWSStart, final int n, final int n2) {
        final int length = n2 - n;
        trailingWSStart.resultLength = length;
        trailingWSStart.originalLength = length;
        trailingWSStart.length = length;
        final int trailingWSStart2 = length;
        trailingWSStart.text = new char[trailingWSStart2];
        System.arraycopy(paraBidi.text, n, trailingWSStart.text, 0, trailingWSStart2);
        trailingWSStart.paraLevel = paraBidi.GetParaLevelAt(n);
        trailingWSStart.paraCount = paraBidi.paraCount;
        trailingWSStart.runs = new BidiRun[0];
        if (paraBidi.controlCount > 0) {
            for (int i = n; i < n2; ++i) {
                if (BidiBase.IsBidiControlChar(paraBidi.text[i])) {
                    ++trailingWSStart.controlCount;
                }
            }
            trailingWSStart.resultLength -= trailingWSStart.controlCount;
        }
        trailingWSStart.getDirPropsMemory(trailingWSStart2);
        trailingWSStart.dirProps = trailingWSStart.dirPropsMemory;
        System.arraycopy(paraBidi.dirProps, n, trailingWSStart.dirProps, 0, trailingWSStart2);
        trailingWSStart.getLevelsMemory(trailingWSStart2);
        trailingWSStart.levels = trailingWSStart.levelsMemory;
        System.arraycopy(paraBidi.levels, n, trailingWSStart.levels, 0, trailingWSStart2);
        trailingWSStart.runCount = -1;
        if (paraBidi.direction != 2) {
            trailingWSStart.direction = paraBidi.direction;
            if (paraBidi.trailingWSStart <= n) {
                trailingWSStart.trailingWSStart = 0;
            }
            else if (paraBidi.trailingWSStart < n2) {
                trailingWSStart.trailingWSStart = paraBidi.trailingWSStart - n;
            }
            else {
                trailingWSStart.trailingWSStart = trailingWSStart2;
            }
        }
        else {
            final byte[] levels = trailingWSStart.levels;
            setTrailingWSStart(trailingWSStart);
            final int trailingWSStart3 = trailingWSStart.trailingWSStart;
            Label_0413: {
                if (trailingWSStart3 == 0) {
                    trailingWSStart.direction = (byte)(trailingWSStart.paraLevel & 0x1);
                }
                else {
                    final byte direction = (byte)(levels[0] & 0x1);
                    if (trailingWSStart3 < trailingWSStart2 && (trailingWSStart.paraLevel & 0x1) != direction) {
                        trailingWSStart.direction = 2;
                    }
                    else {
                        for (int j = 1; j != trailingWSStart3; ++j) {
                            if ((levels[j] & 0x1) != direction) {
                                trailingWSStart.direction = 2;
                                break Label_0413;
                            }
                        }
                        trailingWSStart.direction = direction;
                    }
                }
            }
            switch (trailingWSStart.direction) {
                case 0: {
                    trailingWSStart.paraLevel = (byte)(trailingWSStart.paraLevel + 1 & 0xFFFFFFFE);
                    trailingWSStart.trailingWSStart = 0;
                    break;
                }
                case 1: {
                    trailingWSStart.paraLevel |= 0x1;
                    trailingWSStart.trailingWSStart = 0;
                    break;
                }
            }
        }
        trailingWSStart.paraBidi = paraBidi;
        return bidi2;
    }
    
    static byte getLevelAt(final BidiBase bidiBase, final int n) {
        if (bidiBase.direction != 2 || n >= bidiBase.trailingWSStart) {
            return bidiBase.GetParaLevelAt(n);
        }
        return bidiBase.levels[n];
    }
    
    static byte[] getLevels(final BidiBase bidiBase) {
        final int trailingWSStart = bidiBase.trailingWSStart;
        final int length = bidiBase.length;
        if (trailingWSStart != length) {
            Arrays.fill(bidiBase.levels, trailingWSStart, length, bidiBase.paraLevel);
            bidiBase.trailingWSStart = length;
        }
        if (length < bidiBase.levels.length) {
            final byte[] array = new byte[length];
            System.arraycopy(bidiBase.levels, 0, array, 0, length);
            return array;
        }
        return bidiBase.levels;
    }
    
    static BidiRun getLogicalRun(final BidiBase bidiBase, final int n) {
        final BidiRun bidiRun = new BidiRun();
        getRuns(bidiBase);
        final int runCount = bidiBase.runCount;
        int limit = 0;
        int limit2 = 0;
        BidiRun bidiRun2 = bidiBase.runs[0];
        for (int i = 0; i < runCount; ++i) {
            bidiRun2 = bidiBase.runs[i];
            limit2 = bidiRun2.start + bidiRun2.limit - limit;
            if (n >= bidiRun2.start && n < limit2) {
                break;
            }
            limit = bidiRun2.limit;
        }
        bidiRun.start = bidiRun2.start;
        bidiRun.limit = limit2;
        bidiRun.level = bidiRun2.level;
        return bidiRun;
    }
    
    private static void getSingleRun(final BidiBase bidiBase, final byte b) {
        bidiBase.runs = bidiBase.simpleRuns;
        bidiBase.runCount = 1;
        bidiBase.runs[0] = new BidiRun(0, bidiBase.length, b);
    }
    
    private static void reorderLine(final BidiBase bidiBase, final byte b, byte b2) {
        if (b2 <= (b | 0x1)) {
            return;
        }
        final byte b3 = (byte)(b + 1);
        final BidiRun[] runs = bidiBase.runs;
        final byte[] levels = bidiBase.levels;
        int runCount = bidiBase.runCount;
        if (bidiBase.trailingWSStart < bidiBase.length) {
            --runCount;
        }
        while (true) {
            --b2;
            if (b2 < b3) {
                break;
            }
            int i = 0;
            while (true) {
                if (i < runCount && levels[runs[i].start] < b2) {
                    ++i;
                }
                else {
                    if (i >= runCount) {
                        break;
                    }
                    int n = i;
                    while (++n < runCount && levels[runs[n].start] >= b2) {}
                    for (int n2 = n - 1; i < n2; ++i, --n2) {
                        final BidiRun bidiRun = runs[i];
                        runs[i] = runs[n2];
                        runs[n2] = bidiRun;
                    }
                    if (n == runCount) {
                        break;
                    }
                    i = n + 1;
                }
            }
        }
        if ((b3 & 0x1) == 0x0) {
            int j = 0;
            if (bidiBase.trailingWSStart == bidiBase.length) {
                --runCount;
            }
            while (j < runCount) {
                final BidiRun bidiRun2 = runs[j];
                runs[j] = runs[runCount];
                runs[runCount] = bidiRun2;
                ++j;
                --runCount;
            }
        }
    }
    
    static int getRunFromLogicalIndex(final BidiBase bidiBase, final int n) {
        final BidiRun[] runs = bidiBase.runs;
        final int runCount = bidiBase.runCount;
        int n2 = 0;
        for (int i = 0; i < runCount; ++i) {
            final int n3 = runs[i].limit - n2;
            final int start = runs[i].start;
            if (n >= start && n < start + n3) {
                return i;
            }
            n2 += n3;
        }
        throw new IllegalStateException("Internal ICU error in getRunFromLogicalIndex");
    }
    
    static void getRuns(final BidiBase bidiBase) {
        if (bidiBase.runCount >= 0) {
            return;
        }
        if (bidiBase.direction != 2) {
            getSingleRun(bidiBase, bidiBase.paraLevel);
        }
        else {
            final int length = bidiBase.length;
            final byte[] levels = bidiBase.levels;
            byte b = 126;
            final int trailingWSStart = bidiBase.trailingWSStart;
            int runCount = 0;
            for (int i = 0; i < trailingWSStart; ++i) {
                if (levels[i] != b) {
                    ++runCount;
                    b = levels[i];
                }
            }
            if (runCount == 1 && trailingWSStart == length) {
                getSingleRun(bidiBase, levels[0]);
            }
            else {
                byte paraLevel = 62;
                byte b2 = 0;
                if (trailingWSStart < length) {
                    ++runCount;
                }
                bidiBase.getRunsMemory(runCount);
                final BidiRun[] runsMemory = bidiBase.runsMemory;
                int n = 0;
                int j = 0;
                do {
                    final int n2 = j;
                    final byte b3 = levels[j];
                    if (b3 < paraLevel) {
                        paraLevel = b3;
                    }
                    if (b3 > b2) {
                        b2 = b3;
                    }
                    while (++j < trailingWSStart && levels[j] == b3) {}
                    runsMemory[n] = new BidiRun(n2, j - n2, b3);
                    ++n;
                } while (j < trailingWSStart);
                if (trailingWSStart < length) {
                    runsMemory[n] = new BidiRun(trailingWSStart, length - trailingWSStart, bidiBase.paraLevel);
                    if (bidiBase.paraLevel < paraLevel) {
                        paraLevel = bidiBase.paraLevel;
                    }
                }
                bidiBase.runs = runsMemory;
                bidiBase.runCount = runCount;
                reorderLine(bidiBase, paraLevel, b2);
                int n3 = 0;
                for (int k = 0; k < runCount; ++k) {
                    runsMemory[k].level = levels[runsMemory[k].start];
                    final BidiRun bidiRun = runsMemory[k];
                    final int limit = bidiRun.limit + n3;
                    bidiRun.limit = limit;
                    n3 = limit;
                }
                if (n < runCount) {
                    runsMemory[((bidiBase.paraLevel & 0x1) != 0x0) ? 0 : n].level = bidiBase.paraLevel;
                }
            }
        }
        if (bidiBase.insertPoints.size > 0) {
            for (int l = 0; l < bidiBase.insertPoints.size; ++l) {
                final BidiBase.Point point = bidiBase.insertPoints.points[l];
                final BidiRun bidiRun2 = bidiBase.runs[getRunFromLogicalIndex(bidiBase, point.pos)];
                bidiRun2.insertRemove |= point.flag;
            }
        }
        if (bidiBase.controlCount > 0) {
            for (int n4 = 0; n4 < bidiBase.length; ++n4) {
                if (BidiBase.IsBidiControlChar(bidiBase.text[n4])) {
                    final BidiRun bidiRun3 = bidiBase.runs[getRunFromLogicalIndex(bidiBase, n4)];
                    --bidiRun3.insertRemove;
                }
            }
        }
    }
    
    static int[] prepareReorder(final byte[] array, final byte[] array2, final byte[] array3) {
        if (array == null || array.length <= 0) {
            return null;
        }
        byte b = 62;
        byte b2 = 0;
        int i = array.length;
        while (i > 0) {
            final byte b3 = array[--i];
            if (b3 > 62) {
                return null;
            }
            if (b3 < b) {
                b = b3;
            }
            if (b3 <= b2) {
                continue;
            }
            b2 = b3;
        }
        array2[0] = b;
        array3[0] = b2;
        final int[] array4 = new int[array.length];
        for (int j = array.length; j > 0; --j, array4[j] = j) {}
        return array4;
    }
    
    static int[] reorderVisual(final byte[] array) {
        final byte[] array2 = { 0 };
        final byte[] array3 = { 0 };
        final int[] prepareReorder = prepareReorder(array, array2, array3);
        if (prepareReorder == null) {
            return null;
        }
        final byte b = array2[0];
        byte b2 = array3[0];
        if (b == b2 && (b & 0x1) == 0x0) {
            return prepareReorder;
        }
        do {
            int i = 0;
            while (true) {
                if (i < array.length && array[i] < b2) {
                    ++i;
                }
                else {
                    if (i >= array.length) {
                        break;
                    }
                    int n = i;
                    while (++n < array.length && array[n] >= b2) {}
                    for (int n2 = n - 1; i < n2; ++i, --n2) {
                        final int n3 = prepareReorder[i];
                        prepareReorder[i] = prepareReorder[n2];
                        prepareReorder[n2] = n3;
                    }
                    if (n == array.length) {
                        break;
                    }
                    i = n + 1;
                }
            }
            --b2;
        } while (b2 >= (byte)(b | 0x1));
        return prepareReorder;
    }
    
    static int[] getVisualMap(final BidiBase bidiBase) {
        final BidiRun[] runs = bidiBase.runs;
        final int n = (bidiBase.length > bidiBase.resultLength) ? bidiBase.length : bidiBase.resultLength;
        final int[] array = new int[n];
        int n2 = 0;
        int n3 = 0;
        for (int i = 0; i < bidiBase.runCount; ++i) {
            int start = runs[i].start;
            final int limit = runs[i].limit;
            if (runs[i].isEvenRun()) {
                do {
                    array[n3++] = start++;
                } while (++n2 < limit);
            }
            else {
                int n4 = start + (limit - n2);
                do {
                    array[n3++] = --n4;
                } while (++n2 < limit);
            }
        }
        if (bidiBase.insertPoints.size > 0) {
            int n5 = 0;
            final int runCount = bidiBase.runCount;
            final BidiRun[] runs2 = bidiBase.runs;
            for (int j = 0; j < runCount; ++j) {
                final int insertRemove = runs2[j].insertRemove;
                if ((insertRemove & 0x5) > 0) {
                    ++n5;
                }
                if ((insertRemove & 0xA) > 0) {
                    ++n5;
                }
            }
            int resultLength = bidiBase.resultLength;
            for (int n6 = runCount - 1; n6 >= 0 && n5 > 0; --n6) {
                final int insertRemove2 = runs2[n6].insertRemove;
                if ((insertRemove2 & 0xA) > 0) {
                    array[--resultLength] = -1;
                    --n5;
                }
                for (int n7 = (n6 > 0) ? runs2[n6 - 1].limit : 0, n8 = runs2[n6].limit - 1; n8 >= n7 && n5 > 0; --n8) {
                    array[--resultLength] = array[n8];
                }
                if ((insertRemove2 & 0x5) > 0) {
                    array[--resultLength] = -1;
                    --n5;
                }
            }
        }
        else if (bidiBase.controlCount > 0) {
            final int runCount2 = bidiBase.runCount;
            final BidiRun[] runs3 = bidiBase.runs;
            int n9 = 0;
            int n10 = 0;
            int n11;
            for (int k = 0; k < runCount2; ++k, n9 += n11) {
                n11 = runs3[k].limit - n9;
                final int insertRemove3 = runs3[k].insertRemove;
                if (insertRemove3 == 0 && n10 == n9) {
                    n10 += n11;
                }
                else if (insertRemove3 == 0) {
                    for (int limit2 = runs3[k].limit, l = n9; l < limit2; ++l) {
                        array[n10++] = array[l];
                    }
                }
                else {
                    final int start2 = runs3[k].start;
                    final boolean evenRun = runs3[k].isEvenRun();
                    final int n12 = start2 + n11 - 1;
                    for (int n13 = 0; n13 < n11; ++n13) {
                        final int n14 = evenRun ? (start2 + n13) : (n12 - n13);
                        if (!BidiBase.IsBidiControlChar(bidiBase.text[n14])) {
                            array[n10++] = n14;
                        }
                    }
                }
            }
        }
        if (n == bidiBase.resultLength) {
            return array;
        }
        final int[] array2 = new int[bidiBase.resultLength];
        System.arraycopy(array, 0, array2, 0, bidiBase.resultLength);
        return array2;
    }
}
