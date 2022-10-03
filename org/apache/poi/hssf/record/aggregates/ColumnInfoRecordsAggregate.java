package org.apache.poi.hssf.record.aggregates;

import java.util.Iterator;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.Removal;
import java.util.Comparator;
import org.apache.poi.hssf.model.RecordStream;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.ArrayList;
import org.apache.poi.hssf.record.ColumnInfoRecord;
import java.util.List;
import org.apache.poi.common.Duplicatable;

public final class ColumnInfoRecordsAggregate extends RecordAggregate implements Duplicatable
{
    private final List<ColumnInfoRecord> records;
    
    public ColumnInfoRecordsAggregate() {
        this.records = new ArrayList<ColumnInfoRecord>();
    }
    
    public ColumnInfoRecordsAggregate(final ColumnInfoRecordsAggregate other) {
        this.records = new ArrayList<ColumnInfoRecord>();
        other.records.stream().map((Function<? super Object, ?>)ColumnInfoRecord::copy).forEach(this.records::add);
    }
    
    public ColumnInfoRecordsAggregate(final RecordStream rs) {
        this();
        boolean isInOrder = true;
        ColumnInfoRecord cirPrev = null;
        while (rs.peekNextClass() == ColumnInfoRecord.class) {
            final ColumnInfoRecord cir = (ColumnInfoRecord)rs.getNext();
            this.records.add(cir);
            if (cirPrev != null && compareColInfos(cirPrev, cir) > 0) {
                isInOrder = false;
            }
            cirPrev = cir;
        }
        if (this.records.size() < 1) {
            throw new RuntimeException("No column info records found");
        }
        if (!isInOrder) {
            this.records.sort(ColumnInfoRecordsAggregate::compareColInfos);
        }
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public ColumnInfoRecordsAggregate clone() {
        return this.copy();
    }
    
    @Override
    public ColumnInfoRecordsAggregate copy() {
        return new ColumnInfoRecordsAggregate(this);
    }
    
    public void insertColumn(final ColumnInfoRecord col) {
        this.records.add(col);
        this.records.sort(ColumnInfoRecordsAggregate::compareColInfos);
    }
    
    private void insertColumn(final int idx, final ColumnInfoRecord col) {
        this.records.add(idx, col);
    }
    
    int getNumColumns() {
        return this.records.size();
    }
    
    @Override
    public void visitContainedRecords(final RecordVisitor rv) {
        final int nItems = this.records.size();
        if (nItems < 1) {
            return;
        }
        ColumnInfoRecord cirPrev = null;
        for (final ColumnInfoRecord cir : this.records) {
            rv.visitRecord(cir);
            if (cirPrev != null && compareColInfos(cirPrev, cir) > 0) {
                throw new RuntimeException("Column info records are out of order");
            }
            cirPrev = cir;
        }
    }
    
    private int findStartOfColumnOutlineGroup(final int pIdx) {
        ColumnInfoRecord columnInfo = this.records.get(pIdx);
        final int level = columnInfo.getOutlineLevel();
        int idx;
        ColumnInfoRecord prevColumnInfo;
        for (idx = pIdx; idx != 0; --idx, columnInfo = prevColumnInfo) {
            prevColumnInfo = this.records.get(idx - 1);
            if (!prevColumnInfo.isAdjacentBefore(columnInfo)) {
                break;
            }
            if (prevColumnInfo.getOutlineLevel() < level) {
                break;
            }
        }
        return idx;
    }
    
    private int findEndOfColumnOutlineGroup(final int colInfoIndex) {
        ColumnInfoRecord columnInfo = this.records.get(colInfoIndex);
        final int level = columnInfo.getOutlineLevel();
        int idx;
        ColumnInfoRecord nextColumnInfo;
        for (idx = colInfoIndex; idx < this.records.size() - 1; ++idx, columnInfo = nextColumnInfo) {
            nextColumnInfo = this.records.get(idx + 1);
            if (!columnInfo.isAdjacentBefore(nextColumnInfo)) {
                break;
            }
            if (nextColumnInfo.getOutlineLevel() < level) {
                break;
            }
        }
        return idx;
    }
    
    private ColumnInfoRecord getColInfo(final int idx) {
        return this.records.get(idx);
    }
    
    private boolean isColumnGroupCollapsed(final int idx) {
        final int endOfOutlineGroupIdx = this.findEndOfColumnOutlineGroup(idx);
        final int nextColInfoIx = endOfOutlineGroupIdx + 1;
        if (nextColInfoIx >= this.records.size()) {
            return false;
        }
        final ColumnInfoRecord nextColInfo = this.getColInfo(nextColInfoIx);
        return this.getColInfo(endOfOutlineGroupIdx).isAdjacentBefore(nextColInfo) && nextColInfo.getCollapsed();
    }
    
    private boolean isColumnGroupHiddenByParent(final int idx) {
        int endLevel = 0;
        boolean endHidden = false;
        final int endOfOutlineGroupIdx = this.findEndOfColumnOutlineGroup(idx);
        if (endOfOutlineGroupIdx < this.records.size()) {
            final ColumnInfoRecord nextInfo = this.getColInfo(endOfOutlineGroupIdx + 1);
            if (this.getColInfo(endOfOutlineGroupIdx).isAdjacentBefore(nextInfo)) {
                endLevel = nextInfo.getOutlineLevel();
                endHidden = nextInfo.getHidden();
            }
        }
        int startLevel = 0;
        boolean startHidden = false;
        final int startOfOutlineGroupIdx = this.findStartOfColumnOutlineGroup(idx);
        if (startOfOutlineGroupIdx > 0) {
            final ColumnInfoRecord prevInfo = this.getColInfo(startOfOutlineGroupIdx - 1);
            if (prevInfo.isAdjacentBefore(this.getColInfo(startOfOutlineGroupIdx))) {
                startLevel = prevInfo.getOutlineLevel();
                startHidden = prevInfo.getHidden();
            }
        }
        if (endLevel > startLevel) {
            return endHidden;
        }
        return startHidden;
    }
    
    public void collapseColumn(final int columnIndex) {
        final int colInfoIx = this.findColInfoIdx(columnIndex, 0);
        if (colInfoIx == -1) {
            return;
        }
        final int groupStartColInfoIx = this.findStartOfColumnOutlineGroup(colInfoIx);
        final ColumnInfoRecord columnInfo = this.getColInfo(groupStartColInfoIx);
        final int lastColIx = this.setGroupHidden(groupStartColInfoIx, columnInfo.getOutlineLevel(), true);
        this.setColumn(lastColIx + 1, null, null, null, null, Boolean.TRUE);
    }
    
    private int setGroupHidden(final int pIdx, final int level, final boolean hidden) {
        int idx = pIdx;
        ColumnInfoRecord columnInfo = this.getColInfo(idx);
        while (idx < this.records.size()) {
            columnInfo.setHidden(hidden);
            if (idx + 1 < this.records.size()) {
                final ColumnInfoRecord nextColumnInfo = this.getColInfo(idx + 1);
                if (!columnInfo.isAdjacentBefore(nextColumnInfo)) {
                    break;
                }
                if (nextColumnInfo.getOutlineLevel() < level) {
                    break;
                }
                columnInfo = nextColumnInfo;
            }
            ++idx;
        }
        return columnInfo.getLastColumn();
    }
    
    public void expandColumn(final int columnIndex) {
        final int idx = this.findColInfoIdx(columnIndex, 0);
        if (idx == -1) {
            return;
        }
        if (!this.isColumnGroupCollapsed(idx)) {
            return;
        }
        final int startIdx = this.findStartOfColumnOutlineGroup(idx);
        final int endIdx = this.findEndOfColumnOutlineGroup(idx);
        final ColumnInfoRecord columnInfo = this.getColInfo(endIdx);
        if (!this.isColumnGroupHiddenByParent(idx)) {
            final int outlineLevel = columnInfo.getOutlineLevel();
            for (int i = startIdx; i <= endIdx; ++i) {
                final ColumnInfoRecord ci = this.getColInfo(i);
                if (outlineLevel == ci.getOutlineLevel()) {
                    ci.setHidden(false);
                }
            }
        }
        this.setColumn(columnInfo.getLastColumn() + 1, null, null, null, null, Boolean.FALSE);
    }
    
    private static ColumnInfoRecord copyColInfo(final ColumnInfoRecord ci) {
        return ci.copy();
    }
    
    public void setColumn(final int targetColumnIx, final Short xfIndex, final Integer width, final Integer level, final Boolean hidden, final Boolean collapsed) {
        ColumnInfoRecord ci = null;
        int k;
        for (k = 0; k < this.records.size(); ++k) {
            final ColumnInfoRecord tci = this.records.get(k);
            if (tci.containsColumn(targetColumnIx)) {
                ci = tci;
                break;
            }
            if (tci.getFirstColumn() > targetColumnIx) {
                break;
            }
        }
        if (ci == null) {
            final ColumnInfoRecord nci = new ColumnInfoRecord();
            nci.setFirstColumn(targetColumnIx);
            nci.setLastColumn(targetColumnIx);
            setColumnInfoFields(nci, xfIndex, width, level, hidden, collapsed);
            this.insertColumn(k, nci);
            this.attemptMergeColInfoRecords(k);
            return;
        }
        final boolean styleChanged = xfIndex != null && ci.getXFIndex() != xfIndex;
        final boolean widthChanged = width != null && ci.getColumnWidth() != width.shortValue();
        final boolean levelChanged = level != null && ci.getOutlineLevel() != level;
        final boolean hiddenChanged = hidden != null && ci.getHidden() != hidden;
        final boolean collapsedChanged = collapsed != null && ci.getCollapsed() != collapsed;
        final boolean columnChanged = styleChanged || widthChanged || levelChanged || hiddenChanged || collapsedChanged;
        if (!columnChanged) {
            return;
        }
        if (ci.getFirstColumn() == targetColumnIx && ci.getLastColumn() == targetColumnIx) {
            setColumnInfoFields(ci, xfIndex, width, level, hidden, collapsed);
            this.attemptMergeColInfoRecords(k);
            return;
        }
        if (ci.getFirstColumn() == targetColumnIx || ci.getLastColumn() == targetColumnIx) {
            if (ci.getFirstColumn() == targetColumnIx) {
                ci.setFirstColumn(targetColumnIx + 1);
            }
            else {
                ci.setLastColumn(targetColumnIx - 1);
                ++k;
            }
            final ColumnInfoRecord nci2 = copyColInfo(ci);
            nci2.setFirstColumn(targetColumnIx);
            nci2.setLastColumn(targetColumnIx);
            setColumnInfoFields(nci2, xfIndex, width, level, hidden, collapsed);
            this.insertColumn(k, nci2);
            this.attemptMergeColInfoRecords(k);
        }
        else {
            final ColumnInfoRecord ciMid = copyColInfo(ci);
            final ColumnInfoRecord ciEnd = copyColInfo(ci);
            final int lastcolumn = ci.getLastColumn();
            ci.setLastColumn(targetColumnIx - 1);
            ciMid.setFirstColumn(targetColumnIx);
            ciMid.setLastColumn(targetColumnIx);
            setColumnInfoFields(ciMid, xfIndex, width, level, hidden, collapsed);
            this.insertColumn(++k, ciMid);
            ciEnd.setFirstColumn(targetColumnIx + 1);
            ciEnd.setLastColumn(lastcolumn);
            this.insertColumn(++k, ciEnd);
        }
    }
    
    private static void setColumnInfoFields(final ColumnInfoRecord ci, final Short xfStyle, final Integer width, final Integer level, final Boolean hidden, final Boolean collapsed) {
        if (xfStyle != null) {
            ci.setXFIndex(xfStyle);
        }
        if (width != null) {
            ci.setColumnWidth(width);
        }
        if (level != null) {
            ci.setOutlineLevel(level.shortValue());
        }
        if (hidden != null) {
            ci.setHidden(hidden);
        }
        if (collapsed != null) {
            ci.setCollapsed(collapsed);
        }
    }
    
    private int findColInfoIdx(final int columnIx, final int fromColInfoIdx) {
        if (columnIx < 0) {
            throw new IllegalArgumentException("column parameter out of range: " + columnIx);
        }
        if (fromColInfoIdx < 0) {
            throw new IllegalArgumentException("fromIdx parameter out of range: " + fromColInfoIdx);
        }
        for (int k = fromColInfoIdx; k < this.records.size(); ++k) {
            final ColumnInfoRecord ci = this.getColInfo(k);
            if (ci.containsColumn(columnIx)) {
                return k;
            }
            if (ci.getFirstColumn() > columnIx) {
                break;
            }
        }
        return -1;
    }
    
    private void attemptMergeColInfoRecords(final int colInfoIx) {
        final int nRecords = this.records.size();
        if (colInfoIx < 0 || colInfoIx >= nRecords) {
            throw new IllegalArgumentException("colInfoIx " + colInfoIx + " is out of range (0.." + (nRecords - 1) + ")");
        }
        final ColumnInfoRecord currentCol = this.getColInfo(colInfoIx);
        final int nextIx = colInfoIx + 1;
        if (nextIx < nRecords && mergeColInfoRecords(currentCol, this.getColInfo(nextIx))) {
            this.records.remove(nextIx);
        }
        if (colInfoIx > 0 && mergeColInfoRecords(this.getColInfo(colInfoIx - 1), currentCol)) {
            this.records.remove(colInfoIx);
        }
    }
    
    private static boolean mergeColInfoRecords(final ColumnInfoRecord ciA, final ColumnInfoRecord ciB) {
        if (ciA.isAdjacentBefore(ciB) && ciA.formatMatches(ciB)) {
            ciA.setLastColumn(ciB.getLastColumn());
            return true;
        }
        return false;
    }
    
    public void groupColumnRange(final int fromColumnIx, final int toColumnIx, final boolean indent) {
        int colInfoSearchStartIdx = 0;
        for (int i = fromColumnIx; i <= toColumnIx; ++i) {
            int level = 1;
            final int colInfoIdx = this.findColInfoIdx(i, colInfoSearchStartIdx);
            if (colInfoIdx != -1) {
                level = this.getColInfo(colInfoIdx).getOutlineLevel();
                if (indent) {
                    ++level;
                }
                else {
                    --level;
                }
                level = Math.max(0, level);
                level = Math.min(7, level);
                colInfoSearchStartIdx = Math.max(0, colInfoIdx - 1);
            }
            this.setColumn(i, null, null, level, null, null);
        }
    }
    
    public ColumnInfoRecord findColumnInfo(final int columnIndex) {
        for (int nInfos = this.records.size(), i = 0; i < nInfos; ++i) {
            final ColumnInfoRecord ci = this.getColInfo(i);
            if (ci.containsColumn(columnIndex)) {
                return ci;
            }
        }
        return null;
    }
    
    public int getMaxOutlineLevel() {
        int result = 0;
        for (int count = this.records.size(), i = 0; i < count; ++i) {
            final ColumnInfoRecord columnInfoRecord = this.getColInfo(i);
            result = Math.max(columnInfoRecord.getOutlineLevel(), result);
        }
        return result;
    }
    
    public int getOutlineLevel(final int columnIndex) {
        final ColumnInfoRecord ci = this.findColumnInfo(columnIndex);
        if (ci != null) {
            return ci.getOutlineLevel();
        }
        return 0;
    }
    
    public int getMinColumnIndex() {
        if (this.records.isEmpty()) {
            return 0;
        }
        int minIndex = Integer.MAX_VALUE;
        for (int nInfos = this.records.size(), i = 0; i < nInfos; ++i) {
            final ColumnInfoRecord ci = this.getColInfo(i);
            minIndex = Math.min(minIndex, ci.getFirstColumn());
        }
        return minIndex;
    }
    
    public int getMaxColumnIndex() {
        if (this.records.isEmpty()) {
            return 0;
        }
        int maxIndex = 0;
        for (int nInfos = this.records.size(), i = 0; i < nInfos; ++i) {
            final ColumnInfoRecord ci = this.getColInfo(i);
            maxIndex = Math.max(maxIndex, ci.getLastColumn());
        }
        return maxIndex;
    }
    
    private static int compareColInfos(final ColumnInfoRecord a, final ColumnInfoRecord b) {
        return a.getFirstColumn() - b.getFirstColumn();
    }
}
