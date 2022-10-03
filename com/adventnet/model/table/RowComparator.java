package com.adventnet.model.table;

import com.adventnet.model.table.internal.CVTableModelRow;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.adventnet.beans.xtable.SortColumn;
import java.util.List;
import java.util.logging.Logger;
import java.io.Serializable;
import java.util.Comparator;

public class RowComparator implements Comparator, Serializable
{
    private static final String CLASS_NAME;
    private static Logger OUT;
    int sortColumnsLen;
    int[] sortColumnIndices;
    boolean[] sortOrderAscending;
    int[] multiplier;
    boolean[] sortOrderSpecified;
    List[] sortOrders;
    boolean[] comparable;
    private static final int ROW1_LESSER_THAN_ROW2 = -1;
    private static final int ROW1_GREATER_THAN_ROW2 = 1;
    private static final int ROW1_EQUALS_ROW2 = 0;
    
    public RowComparator(final SortColumn[] xTableSortColumns, final com.adventnet.ds.query.SortColumn[] sortColumns) {
        this.sortColumnsLen = xTableSortColumns.length;
        this.sortColumnIndices = new int[this.sortColumnsLen];
        this.sortOrderAscending = new boolean[this.sortColumnsLen];
        this.multiplier = new int[this.sortColumnsLen];
        this.sortOrderSpecified = new boolean[this.sortColumnsLen];
        this.sortOrders = new List[this.sortColumnsLen];
        this.comparable = new boolean[this.sortColumnsLen];
        for (int i = 0; i < this.sortColumnsLen; ++i) {
            RowComparator.OUT.log(Level.FINER, "xTableSortColumns[{0}] : {1}", new Object[] { new Integer(i), xTableSortColumns[i] });
            this.sortColumnIndices[i] = xTableSortColumns[i].getColumnIndex();
            RowComparator.OUT.log(Level.FINER, "sortColumnIndices[{0}] : {1}", new Object[] { new Integer(i), new Integer(this.sortColumnIndices[i]) });
            this.sortOrderAscending[i] = xTableSortColumns[i].isAscending();
            RowComparator.OUT.log(Level.FINER, "sortOrderAscending[{0}] : {1}", new Object[] { new Integer(i), new Boolean(this.sortOrderAscending[i]) });
            if (this.sortOrderAscending[i]) {
                this.multiplier[i] = 1;
            }
            else {
                this.multiplier[i] = -1;
            }
            this.sortOrders[i] = sortColumns[i].getSortOrder();
            if (this.sortOrders[i] != null) {
                RowComparator.OUT.log(Level.FINER, "sortOrders[{0}] : {1}", new Object[] { new Integer(i), this.sortOrders[i] });
                this.sortOrderSpecified[i] = true;
            }
            RowComparator.OUT.log(Level.FINER, "multiplier[{0}] : {1}", new Object[] { new Integer(i), new Integer(this.multiplier[i]) });
            final Column col = sortColumns[i].getColumn();
            final int type = col.getType();
            if (type == 16) {
                this.comparable[i] = false;
            }
            else {
                this.comparable[i] = true;
            }
        }
    }
    
    @Override
    public int compare(final Object o1, final Object o2) {
        final CVTableModelRow cvTableModelRow1 = (CVTableModelRow)o1;
        final CVTableModelRow cvTableModelRow2 = (CVTableModelRow)o2;
        final List row1 = cvTableModelRow1.getRowContents();
        final List row2 = cvTableModelRow2.getRowContents();
        for (int i = 0; i < this.sortColumnsLen; ++i) {
            Comparable colFromRow1;
            Comparable colFromRow2;
            if (this.comparable[i]) {
                colFromRow1 = row1.get(this.sortColumnIndices[i]);
                colFromRow2 = row2.get(this.sortColumnIndices[i]);
            }
            else {
                colFromRow1 = String.valueOf(row1.get(this.sortColumnIndices[i]));
                colFromRow2 = String.valueOf(row2.get(this.sortColumnIndices[i]));
            }
            if (colFromRow1 == null) {
                if (colFromRow2 != null) {
                    return 1 * this.multiplier[i];
                }
            }
            else {
                if (colFromRow2 == null) {
                    return -1 * this.multiplier[i];
                }
                int compareValue;
                if (this.sortOrderSpecified[i]) {
                    final int indexOfColInSortOrderFromRow1 = this.sortOrders[i].indexOf(colFromRow1);
                    final int indexOfColInSortOrderFromRow2 = this.sortOrders[i].indexOf(colFromRow2);
                    compareValue = indexOfColInSortOrderFromRow1 - indexOfColInSortOrderFromRow2;
                }
                else {
                    try {
                        compareValue = colFromRow1.compareTo(colFromRow2);
                    }
                    catch (final ClassCastException cce) {
                        RowComparator.OUT.log(Level.FINEST, "type mismatch : {0} is of type {1} and {2} is of type {3}. Corresponding rows are {4}, {5}", new Object[] { colFromRow1, colFromRow1.getClass().getName(), colFromRow2, colFromRow2.getClass().getName(), cvTableModelRow1, cvTableModelRow2 });
                        colFromRow1 = String.valueOf(colFromRow1);
                        colFromRow2 = String.valueOf(colFromRow2);
                        compareValue = colFromRow1.compareTo(colFromRow2);
                    }
                }
                compareValue *= this.multiplier[i];
                if (compareValue != 0) {
                    return compareValue;
                }
            }
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return false;
    }
    
    static {
        CLASS_NAME = RowComparator.class.getName();
        RowComparator.OUT = Logger.getLogger(RowComparator.CLASS_NAME);
    }
}
