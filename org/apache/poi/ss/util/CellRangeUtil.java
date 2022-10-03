package org.apache.poi.ss.util;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public final class CellRangeUtil
{
    public static final int NO_INTERSECTION = 1;
    public static final int OVERLAP = 2;
    public static final int INSIDE = 3;
    public static final int ENCLOSES = 4;
    
    private CellRangeUtil() {
    }
    
    public static int intersect(final CellRangeAddress crA, final CellRangeAddress crB) {
        final int firstRow = crB.getFirstRow();
        final int lastRow = crB.getLastRow();
        final int firstCol = crB.getFirstColumn();
        final int lastCol = crB.getLastColumn();
        if (gt(crA.getFirstRow(), lastRow) || lt(crA.getLastRow(), firstRow) || gt(crA.getFirstColumn(), lastCol) || lt(crA.getLastColumn(), firstCol)) {
            return 1;
        }
        if (contains(crA, crB)) {
            return 3;
        }
        if (contains(crB, crA)) {
            return 4;
        }
        return 2;
    }
    
    public static CellRangeAddress[] mergeCellRanges(final CellRangeAddress[] cellRanges) {
        if (cellRanges.length < 1) {
            return new CellRangeAddress[0];
        }
        final List<CellRangeAddress> list = toList(cellRanges);
        final List<CellRangeAddress> temp = mergeCellRanges(list);
        return toArray(temp);
    }
    
    private static List<CellRangeAddress> mergeCellRanges(final List<CellRangeAddress> cellRangeList) {
        while (cellRangeList.size() > 1) {
            boolean somethingGotMerged = false;
            for (int i = 0; i < cellRangeList.size(); ++i) {
                CellRangeAddress range1 = cellRangeList.get(i);
                for (int j = i + 1; j < cellRangeList.size(); ++j) {
                    final CellRangeAddress range2 = cellRangeList.get(j);
                    final CellRangeAddress[] mergeResult = mergeRanges(range1, range2);
                    if (mergeResult != null) {
                        somethingGotMerged = true;
                        range1 = mergeResult[0];
                        cellRangeList.set(i, mergeResult[0]);
                        cellRangeList.remove(j--);
                        for (int k = 1; k < mergeResult.length; ++k) {
                            ++j;
                            cellRangeList.add(j, mergeResult[k]);
                        }
                    }
                }
            }
            if (!somethingGotMerged) {
                break;
            }
        }
        return cellRangeList;
    }
    
    private static CellRangeAddress[] mergeRanges(final CellRangeAddress range1, final CellRangeAddress range2) {
        final int x = intersect(range1, range2);
        switch (x) {
            case 1: {
                if (hasExactSharedBorder(range1, range2)) {
                    return new CellRangeAddress[] { createEnclosingCellRange(range1, range2) };
                }
                return null;
            }
            case 2: {
                return null;
            }
            case 3: {
                return new CellRangeAddress[] { range1 };
            }
            case 4: {
                return new CellRangeAddress[] { range2 };
            }
            default: {
                throw new RuntimeException("unexpected intersection result (" + x + ")");
            }
        }
    }
    
    private static CellRangeAddress[] toArray(final List<CellRangeAddress> temp) {
        final CellRangeAddress[] result = new CellRangeAddress[temp.size()];
        temp.toArray(result);
        return result;
    }
    
    private static List<CellRangeAddress> toList(final CellRangeAddress[] temp) {
        final List<CellRangeAddress> result = new ArrayList<CellRangeAddress>(temp.length);
        Collections.addAll(result, temp);
        return result;
    }
    
    public static boolean contains(final CellRangeAddress crA, final CellRangeAddress crB) {
        return le(crA.getFirstRow(), crB.getFirstRow()) && ge(crA.getLastRow(), crB.getLastRow()) && le(crA.getFirstColumn(), crB.getFirstColumn()) && ge(crA.getLastColumn(), crB.getLastColumn());
    }
    
    public static boolean hasExactSharedBorder(final CellRangeAddress crA, final CellRangeAddress crB) {
        final int oFirstRow = crB.getFirstRow();
        final int oLastRow = crB.getLastRow();
        final int oFirstCol = crB.getFirstColumn();
        final int oLastCol = crB.getLastColumn();
        if ((crA.getFirstRow() > 0 && crA.getFirstRow() - 1 == oLastRow) || (oFirstRow > 0 && oFirstRow - 1 == crA.getLastRow())) {
            return crA.getFirstColumn() == oFirstCol && crA.getLastColumn() == oLastCol;
        }
        return ((crA.getFirstColumn() > 0 && crA.getFirstColumn() - 1 == oLastCol) || (oFirstCol > 0 && crA.getLastColumn() == oFirstCol - 1)) && crA.getFirstRow() == oFirstRow && crA.getLastRow() == oLastRow;
    }
    
    public static CellRangeAddress createEnclosingCellRange(final CellRangeAddress crA, final CellRangeAddress crB) {
        if (crB == null) {
            return crA.copy();
        }
        final int minRow = lt(crB.getFirstRow(), crA.getFirstRow()) ? crB.getFirstRow() : crA.getFirstRow();
        final int maxRow = gt(crB.getLastRow(), crA.getLastRow()) ? crB.getLastRow() : crA.getLastRow();
        final int minCol = lt(crB.getFirstColumn(), crA.getFirstColumn()) ? crB.getFirstColumn() : crA.getFirstColumn();
        final int maxCol = gt(crB.getLastColumn(), crA.getLastColumn()) ? crB.getLastColumn() : crA.getLastColumn();
        return new CellRangeAddress(minRow, maxRow, minCol, maxCol);
    }
    
    private static boolean lt(final int a, final int b) {
        return a != -1 && (b == -1 || a < b);
    }
    
    private static boolean le(final int a, final int b) {
        return a == b || lt(a, b);
    }
    
    private static boolean gt(final int a, final int b) {
        return lt(b, a);
    }
    
    private static boolean ge(final int a, final int b) {
        return !lt(a, b);
    }
}
