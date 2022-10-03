package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.ptg.Deleted3DPxg;
import org.apache.poi.ss.formula.ptg.DeletedArea3DPtg;
import org.apache.poi.ss.formula.ptg.AreaErrPtg;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.DeletedRef3DPtg;
import org.apache.poi.ss.formula.ptg.RefErrorPtg;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Area2DPtgBase;
import org.apache.poi.ss.formula.ptg.Ref3DPxg;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.SpreadsheetVersion;

public final class FormulaShifter
{
    private final int _externSheetIndex;
    private final String _sheetName;
    private final int _firstMovedIndex;
    private final int _lastMovedIndex;
    private final int _amountToMove;
    private final int _srcSheetIndex;
    private final int _dstSheetIndex;
    private final SpreadsheetVersion _version;
    private final ShiftMode _mode;
    
    private FormulaShifter(final int externSheetIndex, final String sheetName, final int firstMovedIndex, final int lastMovedIndex, final int amountToMove, final ShiftMode mode, final SpreadsheetVersion version) {
        if (amountToMove == 0) {
            throw new IllegalArgumentException("amountToMove must not be zero");
        }
        if (firstMovedIndex > lastMovedIndex) {
            throw new IllegalArgumentException("firstMovedIndex, lastMovedIndex out of order");
        }
        this._externSheetIndex = externSheetIndex;
        this._sheetName = sheetName;
        this._firstMovedIndex = firstMovedIndex;
        this._lastMovedIndex = lastMovedIndex;
        this._amountToMove = amountToMove;
        this._mode = mode;
        this._version = version;
        final int n = -1;
        this._dstSheetIndex = n;
        this._srcSheetIndex = n;
    }
    
    private FormulaShifter(final int srcSheetIndex, final int dstSheetIndex) {
        final int n = -1;
        this._amountToMove = n;
        this._lastMovedIndex = n;
        this._firstMovedIndex = n;
        this._externSheetIndex = n;
        this._sheetName = null;
        this._version = null;
        this._srcSheetIndex = srcSheetIndex;
        this._dstSheetIndex = dstSheetIndex;
        this._mode = ShiftMode.SheetMove;
    }
    
    public static FormulaShifter createForRowShift(final int externSheetIndex, final String sheetName, final int firstMovedRowIndex, final int lastMovedRowIndex, final int numberOfRowsToMove, final SpreadsheetVersion version) {
        return new FormulaShifter(externSheetIndex, sheetName, firstMovedRowIndex, lastMovedRowIndex, numberOfRowsToMove, ShiftMode.RowMove, version);
    }
    
    public static FormulaShifter createForRowCopy(final int externSheetIndex, final String sheetName, final int firstMovedRowIndex, final int lastMovedRowIndex, final int numberOfRowsToMove, final SpreadsheetVersion version) {
        return new FormulaShifter(externSheetIndex, sheetName, firstMovedRowIndex, lastMovedRowIndex, numberOfRowsToMove, ShiftMode.RowCopy, version);
    }
    
    public static FormulaShifter createForColumnShift(final int externSheetIndex, final String sheetName, final int firstMovedColumnIndex, final int lastMovedColumnIndex, final int numberOfColumnsToMove, final SpreadsheetVersion version) {
        return new FormulaShifter(externSheetIndex, sheetName, firstMovedColumnIndex, lastMovedColumnIndex, numberOfColumnsToMove, ShiftMode.ColumnMove, version);
    }
    
    public static FormulaShifter createForColumnCopy(final int externSheetIndex, final String sheetName, final int firstMovedColumnIndex, final int lastMovedColumnIndex, final int numberOfColumnsToMove, final SpreadsheetVersion version) {
        return new FormulaShifter(externSheetIndex, sheetName, firstMovedColumnIndex, lastMovedColumnIndex, numberOfColumnsToMove, ShiftMode.ColumnCopy, version);
    }
    
    public static FormulaShifter createForSheetShift(final int srcSheetIndex, final int dstSheetIndex) {
        return new FormulaShifter(srcSheetIndex, dstSheetIndex);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + " [" + this._firstMovedIndex + this._lastMovedIndex + this._amountToMove + "]";
    }
    
    public boolean adjustFormula(final Ptg[] ptgs, final int currentExternSheetIx) {
        boolean refsWereChanged = false;
        for (int i = 0; i < ptgs.length; ++i) {
            final Ptg newPtg = this.adjustPtg(ptgs[i], currentExternSheetIx);
            if (newPtg != null) {
                refsWereChanged = true;
                ptgs[i] = newPtg;
            }
        }
        return refsWereChanged;
    }
    
    private Ptg adjustPtg(final Ptg ptg, final int currentExternSheetIx) {
        switch (this._mode) {
            case RowMove: {
                return this.adjustPtgDueToRowMove(ptg, currentExternSheetIx);
            }
            case RowCopy: {
                return this.adjustPtgDueToRowCopy(ptg);
            }
            case ColumnMove: {
                return this.adjustPtgDueToColumnMove(ptg, currentExternSheetIx);
            }
            case ColumnCopy: {
                return this.adjustPtgDueToColumnCopy(ptg);
            }
            case SheetMove: {
                return this.adjustPtgDueToSheetMove(ptg);
            }
            default: {
                throw new IllegalStateException("Unsupported shift mode: " + this._mode);
            }
        }
    }
    
    private Ptg adjustPtgDueToMove(final Ptg ptg, final int currentExternSheetIx, final boolean isRowMove) {
        if (ptg instanceof RefPtg) {
            if (currentExternSheetIx != this._externSheetIndex) {
                return null;
            }
            final RefPtg rptg = (RefPtg)ptg;
            return isRowMove ? this.rowMoveRefPtg(rptg) : this.columnMoveRefPtg(rptg);
        }
        else if (ptg instanceof Ref3DPtg) {
            final Ref3DPtg rptg2 = (Ref3DPtg)ptg;
            if (this._externSheetIndex != rptg2.getExternSheetIndex()) {
                return null;
            }
            return isRowMove ? this.rowMoveRefPtg(rptg2) : this.columnMoveRefPtg(rptg2);
        }
        else if (ptg instanceof Ref3DPxg) {
            final Ref3DPxg rpxg = (Ref3DPxg)ptg;
            if (rpxg.getExternalWorkbookNumber() > 0 || !this._sheetName.equalsIgnoreCase(rpxg.getSheetName())) {
                return null;
            }
            return isRowMove ? this.rowMoveRefPtg(rpxg) : this.columnMoveRefPtg(rpxg);
        }
        else if (ptg instanceof Area2DPtgBase) {
            if (currentExternSheetIx != this._externSheetIndex) {
                return ptg;
            }
            final Area2DPtgBase aptg = (Area2DPtgBase)ptg;
            return isRowMove ? this.rowMoveAreaPtg(aptg) : this.columnMoveAreaPtg(aptg);
        }
        else if (ptg instanceof Area3DPtg) {
            final Area3DPtg aptg2 = (Area3DPtg)ptg;
            if (this._externSheetIndex != aptg2.getExternSheetIndex()) {
                return null;
            }
            return isRowMove ? this.rowMoveAreaPtg(aptg2) : this.columnMoveAreaPtg(aptg2);
        }
        else {
            if (!(ptg instanceof Area3DPxg)) {
                return null;
            }
            final Area3DPxg apxg = (Area3DPxg)ptg;
            if (apxg.getExternalWorkbookNumber() > 0 || !this._sheetName.equalsIgnoreCase(apxg.getSheetName())) {
                return null;
            }
            return isRowMove ? this.rowMoveAreaPtg(apxg) : this.columnMoveAreaPtg(apxg);
        }
    }
    
    private Ptg adjustPtgDueToRowMove(final Ptg ptg, final int currentExternSheetIx) {
        return this.adjustPtgDueToMove(ptg, currentExternSheetIx, true);
    }
    
    private Ptg adjustPtgDueToColumnMove(final Ptg ptg, final int currentExternSheetIx) {
        return this.adjustPtgDueToMove(ptg, currentExternSheetIx, false);
    }
    
    private Ptg adjustPtgDueToCopy(final Ptg ptg, final boolean isRowCopy) {
        if (ptg instanceof RefPtg) {
            final RefPtg rptg = (RefPtg)ptg;
            return isRowCopy ? this.rowCopyRefPtg(rptg) : this.columnCopyRefPtg(rptg);
        }
        if (ptg instanceof Ref3DPtg) {
            final Ref3DPtg rptg2 = (Ref3DPtg)ptg;
            return isRowCopy ? this.rowCopyRefPtg(rptg2) : this.columnCopyRefPtg(rptg2);
        }
        if (ptg instanceof Ref3DPxg) {
            final Ref3DPxg rpxg = (Ref3DPxg)ptg;
            return isRowCopy ? this.rowCopyRefPtg(rpxg) : this.columnCopyRefPtg(rpxg);
        }
        if (ptg instanceof Area2DPtgBase) {
            final Area2DPtgBase aptg = (Area2DPtgBase)ptg;
            return isRowCopy ? this.rowCopyAreaPtg(aptg) : this.columnCopyAreaPtg(aptg);
        }
        if (ptg instanceof Area3DPtg) {
            final Area3DPtg aptg2 = (Area3DPtg)ptg;
            return isRowCopy ? this.rowCopyAreaPtg(aptg2) : this.columnCopyAreaPtg(aptg2);
        }
        if (ptg instanceof Area3DPxg) {
            final Area3DPxg apxg = (Area3DPxg)ptg;
            return isRowCopy ? this.rowCopyAreaPtg(apxg) : this.columnCopyAreaPtg(apxg);
        }
        return null;
    }
    
    private Ptg adjustPtgDueToRowCopy(final Ptg ptg) {
        return this.adjustPtgDueToCopy(ptg, true);
    }
    
    private Ptg adjustPtgDueToColumnCopy(final Ptg ptg) {
        return this.adjustPtgDueToCopy(ptg, false);
    }
    
    private Ptg adjustPtgDueToSheetMove(final Ptg ptg) {
        if (ptg instanceof Ref3DPtg) {
            final Ref3DPtg ref = (Ref3DPtg)ptg;
            final int oldSheetIndex = ref.getExternSheetIndex();
            if (oldSheetIndex < this._srcSheetIndex && oldSheetIndex < this._dstSheetIndex) {
                return null;
            }
            if (oldSheetIndex > this._srcSheetIndex && oldSheetIndex > this._dstSheetIndex) {
                return null;
            }
            if (oldSheetIndex == this._srcSheetIndex) {
                ref.setExternSheetIndex(this._dstSheetIndex);
                return ref;
            }
            if (this._dstSheetIndex < this._srcSheetIndex) {
                ref.setExternSheetIndex(oldSheetIndex + 1);
                return ref;
            }
            if (this._dstSheetIndex > this._srcSheetIndex) {
                ref.setExternSheetIndex(oldSheetIndex - 1);
                return ref;
            }
        }
        return null;
    }
    
    private Ptg rowMoveRefPtg(final RefPtgBase rptg) {
        final int refRow = rptg.getRow();
        if (this._firstMovedIndex <= refRow && refRow <= this._lastMovedIndex) {
            rptg.setRow(refRow + this._amountToMove);
            return rptg;
        }
        final int destFirstRowIndex = this._firstMovedIndex + this._amountToMove;
        final int destLastRowIndex = this._lastMovedIndex + this._amountToMove;
        if (destLastRowIndex < refRow || refRow < destFirstRowIndex) {
            return null;
        }
        if (destFirstRowIndex <= refRow && refRow <= destLastRowIndex) {
            return createDeletedRef(rptg);
        }
        throw new IllegalStateException("Situation not covered: (" + this._firstMovedIndex + ", " + this._lastMovedIndex + ", " + this._amountToMove + ", " + refRow + ", " + refRow + ")");
    }
    
    private Ptg rowMoveAreaPtg(final AreaPtgBase aptg) {
        final int aFirstRow = aptg.getFirstRow();
        final int aLastRow = aptg.getLastRow();
        if (this._firstMovedIndex <= aFirstRow && aLastRow <= this._lastMovedIndex) {
            aptg.setFirstRow(aFirstRow + this._amountToMove);
            aptg.setLastRow(aLastRow + this._amountToMove);
            return aptg;
        }
        final int destFirstRowIndex = this._firstMovedIndex + this._amountToMove;
        final int destLastRowIndex = this._lastMovedIndex + this._amountToMove;
        if (aFirstRow < this._firstMovedIndex && this._lastMovedIndex < aLastRow) {
            if (destFirstRowIndex < aFirstRow && aFirstRow <= destLastRowIndex) {
                aptg.setFirstRow(destLastRowIndex + 1);
                return aptg;
            }
            if (destFirstRowIndex <= aLastRow && aLastRow < destLastRowIndex) {
                aptg.setLastRow(destFirstRowIndex - 1);
                return aptg;
            }
            return null;
        }
        else if (this._firstMovedIndex <= aFirstRow && aFirstRow <= this._lastMovedIndex) {
            if (this._amountToMove < 0) {
                aptg.setFirstRow(aFirstRow + this._amountToMove);
                return aptg;
            }
            if (destFirstRowIndex > aLastRow) {
                return null;
            }
            int newFirstRowIx = aFirstRow + this._amountToMove;
            if (destLastRowIndex < aLastRow) {
                aptg.setFirstRow(newFirstRowIx);
                return aptg;
            }
            final int areaRemainingTopRowIx = this._lastMovedIndex + 1;
            if (destFirstRowIndex > areaRemainingTopRowIx) {
                newFirstRowIx = areaRemainingTopRowIx;
            }
            aptg.setFirstRow(newFirstRowIx);
            aptg.setLastRow(Math.max(aLastRow, destLastRowIndex));
            return aptg;
        }
        else if (this._firstMovedIndex <= aLastRow && aLastRow <= this._lastMovedIndex) {
            if (this._amountToMove > 0) {
                aptg.setLastRow(aLastRow + this._amountToMove);
                return aptg;
            }
            if (destLastRowIndex < aFirstRow) {
                return null;
            }
            int newLastRowIx = aLastRow + this._amountToMove;
            if (destFirstRowIndex > aFirstRow) {
                aptg.setLastRow(newLastRowIx);
                return aptg;
            }
            final int areaRemainingBottomRowIx = this._firstMovedIndex - 1;
            if (destLastRowIndex < areaRemainingBottomRowIx) {
                newLastRowIx = areaRemainingBottomRowIx;
            }
            aptg.setFirstRow(Math.min(aFirstRow, destFirstRowIndex));
            aptg.setLastRow(newLastRowIx);
            return aptg;
        }
        else {
            if (destLastRowIndex < aFirstRow || aLastRow < destFirstRowIndex) {
                return null;
            }
            if (destFirstRowIndex <= aFirstRow && aLastRow <= destLastRowIndex) {
                return createDeletedRef(aptg);
            }
            if (aFirstRow <= destFirstRowIndex && destLastRowIndex <= aLastRow) {
                return null;
            }
            if (destFirstRowIndex < aFirstRow && aFirstRow <= destLastRowIndex) {
                aptg.setFirstRow(destLastRowIndex + 1);
                return aptg;
            }
            if (destFirstRowIndex <= aLastRow && aLastRow < destLastRowIndex) {
                aptg.setLastRow(destFirstRowIndex - 1);
                return aptg;
            }
            throw new IllegalStateException("Situation not covered: (" + this._firstMovedIndex + ", " + this._lastMovedIndex + ", " + this._amountToMove + ", " + aFirstRow + ", " + aLastRow + ")");
        }
    }
    
    private Ptg rowCopyRefPtg(final RefPtgBase rptg) {
        final int refRow = rptg.getRow();
        if (!rptg.isRowRelative()) {
            return null;
        }
        final int destRowIndex = this._firstMovedIndex + this._amountToMove;
        if (destRowIndex < 0 || this._version.getLastRowIndex() < destRowIndex) {
            return createDeletedRef(rptg);
        }
        final int newRowIndex = refRow + this._amountToMove;
        if (newRowIndex < 0 || this._version.getLastRowIndex() < newRowIndex) {
            return createDeletedRef(rptg);
        }
        rptg.setRow(newRowIndex);
        return rptg;
    }
    
    private Ptg rowCopyAreaPtg(final AreaPtgBase aptg) {
        boolean changed = false;
        final int aFirstRow = aptg.getFirstRow();
        final int aLastRow = aptg.getLastRow();
        if (aptg.isFirstRowRelative()) {
            final int destFirstRowIndex = aFirstRow + this._amountToMove;
            if (destFirstRowIndex < 0 || this._version.getLastRowIndex() < destFirstRowIndex) {
                return createDeletedRef(aptg);
            }
            aptg.setFirstRow(destFirstRowIndex);
            changed = true;
        }
        if (aptg.isLastRowRelative()) {
            final int destLastRowIndex = aLastRow + this._amountToMove;
            if (destLastRowIndex < 0 || this._version.getLastRowIndex() < destLastRowIndex) {
                return createDeletedRef(aptg);
            }
            aptg.setLastRow(destLastRowIndex);
            changed = true;
        }
        if (changed) {
            aptg.sortTopLeftToBottomRight();
        }
        return changed ? aptg : null;
    }
    
    private Ptg columnMoveRefPtg(final RefPtgBase rptg) {
        final int refColumn = rptg.getColumn();
        if (this._firstMovedIndex <= refColumn && refColumn <= this._lastMovedIndex) {
            rptg.setColumn(refColumn + this._amountToMove);
            return rptg;
        }
        final int destFirstColumnIndex = this._firstMovedIndex + this._amountToMove;
        final int destLastColumnIndex = this._lastMovedIndex + this._amountToMove;
        if (destLastColumnIndex < refColumn || refColumn < destFirstColumnIndex) {
            return null;
        }
        if (destFirstColumnIndex <= refColumn && refColumn <= destLastColumnIndex) {
            return createDeletedRef(rptg);
        }
        throw new IllegalStateException("Situation not covered: (" + this._firstMovedIndex + ", " + this._lastMovedIndex + ", " + this._amountToMove + ", " + refColumn + ", " + refColumn + ")");
    }
    
    private Ptg columnMoveAreaPtg(final AreaPtgBase aptg) {
        final int aFirstColumn = aptg.getFirstColumn();
        final int aLastColumn = aptg.getLastColumn();
        if (this._firstMovedIndex <= aFirstColumn && aLastColumn <= this._lastMovedIndex) {
            aptg.setFirstColumn(aFirstColumn + this._amountToMove);
            aptg.setLastColumn(aLastColumn + this._amountToMove);
            return aptg;
        }
        final int destFirstColumnIndex = this._firstMovedIndex + this._amountToMove;
        final int destLastColumnIndex = this._lastMovedIndex + this._amountToMove;
        if (aFirstColumn < this._firstMovedIndex && this._lastMovedIndex < aLastColumn) {
            if (destFirstColumnIndex < aFirstColumn && aFirstColumn <= destLastColumnIndex) {
                aptg.setFirstColumn(destLastColumnIndex + 1);
                return aptg;
            }
            if (destFirstColumnIndex <= aLastColumn && aLastColumn < destLastColumnIndex) {
                aptg.setLastColumn(destFirstColumnIndex - 1);
                return aptg;
            }
            return null;
        }
        else if (this._firstMovedIndex <= aFirstColumn && aFirstColumn <= this._lastMovedIndex) {
            if (this._amountToMove < 0) {
                aptg.setFirstColumn(aFirstColumn + this._amountToMove);
                return aptg;
            }
            if (destFirstColumnIndex > aLastColumn) {
                return null;
            }
            int newFirstColumnIx = aFirstColumn + this._amountToMove;
            if (destLastColumnIndex < aLastColumn) {
                aptg.setFirstColumn(newFirstColumnIx);
                return aptg;
            }
            final int areaRemainingTopColumnIx = this._lastMovedIndex + 1;
            if (destFirstColumnIndex > areaRemainingTopColumnIx) {
                newFirstColumnIx = areaRemainingTopColumnIx;
            }
            aptg.setFirstColumn(newFirstColumnIx);
            aptg.setLastColumn(Math.max(aLastColumn, destLastColumnIndex));
            return aptg;
        }
        else if (this._firstMovedIndex <= aLastColumn && aLastColumn <= this._lastMovedIndex) {
            if (this._amountToMove > 0) {
                aptg.setLastColumn(aLastColumn + this._amountToMove);
                return aptg;
            }
            if (destLastColumnIndex < aFirstColumn) {
                return null;
            }
            int newLastColumnIx = aLastColumn + this._amountToMove;
            if (destFirstColumnIndex > aFirstColumn) {
                aptg.setLastColumn(newLastColumnIx);
                return aptg;
            }
            final int areaRemainingBottomColumnIx = this._firstMovedIndex - 1;
            if (destLastColumnIndex < areaRemainingBottomColumnIx) {
                newLastColumnIx = areaRemainingBottomColumnIx;
            }
            aptg.setFirstColumn(Math.min(aFirstColumn, destFirstColumnIndex));
            aptg.setLastColumn(newLastColumnIx);
            return aptg;
        }
        else {
            if (destLastColumnIndex < aFirstColumn || aLastColumn < destFirstColumnIndex) {
                return null;
            }
            if (destFirstColumnIndex <= aFirstColumn && aLastColumn <= destLastColumnIndex) {
                return createDeletedRef(aptg);
            }
            if (aFirstColumn <= destFirstColumnIndex && destLastColumnIndex <= aLastColumn) {
                return null;
            }
            if (destFirstColumnIndex < aFirstColumn && aFirstColumn <= destLastColumnIndex) {
                aptg.setFirstColumn(destLastColumnIndex + 1);
                return aptg;
            }
            if (destFirstColumnIndex <= aLastColumn && aLastColumn < destLastColumnIndex) {
                aptg.setLastColumn(destFirstColumnIndex - 1);
                return aptg;
            }
            throw new IllegalStateException("Situation not covered: (" + this._firstMovedIndex + ", " + this._lastMovedIndex + ", " + this._amountToMove + ", " + aFirstColumn + ", " + aLastColumn + ")");
        }
    }
    
    private Ptg columnCopyRefPtg(final RefPtgBase rptg) {
        final int refColumn = rptg.getColumn();
        if (!rptg.isColRelative()) {
            return null;
        }
        final int destColumnIndex = this._firstMovedIndex + this._amountToMove;
        if (destColumnIndex < 0 || this._version.getLastColumnIndex() < destColumnIndex) {
            return createDeletedRef(rptg);
        }
        final int newColumnIndex = refColumn + this._amountToMove;
        if (newColumnIndex < 0 || this._version.getLastColumnIndex() < newColumnIndex) {
            return createDeletedRef(rptg);
        }
        rptg.setColumn(newColumnIndex);
        return rptg;
    }
    
    private Ptg columnCopyAreaPtg(final AreaPtgBase aptg) {
        boolean changed = false;
        final int aFirstColumn = aptg.getFirstColumn();
        final int aLastColumn = aptg.getLastColumn();
        if (aptg.isFirstColRelative()) {
            final int destFirstColumnIndex = aFirstColumn + this._amountToMove;
            if (destFirstColumnIndex < 0 || this._version.getLastColumnIndex() < destFirstColumnIndex) {
                return createDeletedRef(aptg);
            }
            aptg.setFirstColumn(destFirstColumnIndex);
            changed = true;
        }
        if (aptg.isLastColRelative()) {
            final int destLastColumnIndex = aLastColumn + this._amountToMove;
            if (destLastColumnIndex < 0 || this._version.getLastColumnIndex() < destLastColumnIndex) {
                return createDeletedRef(aptg);
            }
            aptg.setLastColumn(destLastColumnIndex);
            changed = true;
        }
        if (changed) {
            aptg.sortTopLeftToBottomRight();
        }
        return changed ? aptg : null;
    }
    
    private static Ptg createDeletedRef(final Ptg ptg) {
        if (ptg instanceof RefPtg) {
            return new RefErrorPtg();
        }
        if (ptg instanceof Ref3DPtg) {
            final Ref3DPtg rptg = (Ref3DPtg)ptg;
            return new DeletedRef3DPtg(rptg.getExternSheetIndex());
        }
        if (ptg instanceof AreaPtg) {
            return new AreaErrPtg();
        }
        if (ptg instanceof Area3DPtg) {
            final Area3DPtg area3DPtg = (Area3DPtg)ptg;
            return new DeletedArea3DPtg(area3DPtg.getExternSheetIndex());
        }
        if (ptg instanceof Ref3DPxg) {
            final Ref3DPxg pxg = (Ref3DPxg)ptg;
            return new Deleted3DPxg(pxg.getExternalWorkbookNumber(), pxg.getSheetName());
        }
        if (ptg instanceof Area3DPxg) {
            final Area3DPxg pxg2 = (Area3DPxg)ptg;
            return new Deleted3DPxg(pxg2.getExternalWorkbookNumber(), pxg2.getSheetName());
        }
        throw new IllegalArgumentException("Unexpected ref ptg class (" + ptg.getClass().getName() + ")");
    }
    
    private enum ShiftMode
    {
        RowMove, 
        RowCopy, 
        ColumnMove, 
        ColumnCopy, 
        SheetMove;
    }
}
