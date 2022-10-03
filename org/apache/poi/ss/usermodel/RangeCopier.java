package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.formula.ptg.Ptg;
import java.util.Map;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.formula.FormulaShifter;

public abstract class RangeCopier
{
    private Sheet sourceSheet;
    private Sheet destSheet;
    private FormulaShifter horizontalFormulaShifter;
    private FormulaShifter verticalFormulaShifter;
    
    public RangeCopier(final Sheet sourceSheet, final Sheet destSheet) {
        this.sourceSheet = sourceSheet;
        this.destSheet = destSheet;
    }
    
    public RangeCopier(final Sheet sheet) {
        this(sheet, sheet);
    }
    
    public void copyRange(final CellRangeAddress tilePatternRange, final CellRangeAddress tileDestRange) {
        final Sheet sourceCopy = this.sourceSheet.getWorkbook().cloneSheet(this.sourceSheet.getWorkbook().getSheetIndex(this.sourceSheet));
        final int sourceWidthMinus1 = tilePatternRange.getLastColumn() - tilePatternRange.getFirstColumn();
        final int sourceHeightMinus1 = tilePatternRange.getLastRow() - tilePatternRange.getFirstRow();
        int nextRowIndexToCopy = tileDestRange.getFirstRow();
        do {
            int nextCellIndexInRowToCopy = tileDestRange.getFirstColumn();
            final int heightToCopyMinus1 = Math.min(sourceHeightMinus1, tileDestRange.getLastRow() - nextRowIndexToCopy);
            final int bottomLimitToCopy = tilePatternRange.getFirstRow() + heightToCopyMinus1;
            do {
                final int widthToCopyMinus1 = Math.min(sourceWidthMinus1, tileDestRange.getLastColumn() - nextCellIndexInRowToCopy);
                final int rightLimitToCopy = tilePatternRange.getFirstColumn() + widthToCopyMinus1;
                final CellRangeAddress rangeToCopy = new CellRangeAddress(tilePatternRange.getFirstRow(), bottomLimitToCopy, tilePatternRange.getFirstColumn(), rightLimitToCopy);
                this.copyRange(rangeToCopy, nextCellIndexInRowToCopy - rangeToCopy.getFirstColumn(), nextRowIndexToCopy - rangeToCopy.getFirstRow(), sourceCopy);
                nextCellIndexInRowToCopy += widthToCopyMinus1 + 1;
            } while (nextCellIndexInRowToCopy <= tileDestRange.getLastColumn());
            nextRowIndexToCopy += heightToCopyMinus1 + 1;
        } while (nextRowIndexToCopy <= tileDestRange.getLastRow());
        final int tempCopyIndex = this.sourceSheet.getWorkbook().getSheetIndex(sourceCopy);
        this.sourceSheet.getWorkbook().removeSheetAt(tempCopyIndex);
    }
    
    private void copyRange(final CellRangeAddress sourceRange, final int deltaX, final int deltaY, final Sheet sourceClone) {
        if (deltaX != 0) {
            this.horizontalFormulaShifter = FormulaShifter.createForColumnCopy(this.sourceSheet.getWorkbook().getSheetIndex(this.sourceSheet), this.sourceSheet.getSheetName(), sourceRange.getFirstColumn(), sourceRange.getLastColumn(), deltaX, this.sourceSheet.getWorkbook().getSpreadsheetVersion());
        }
        if (deltaY != 0) {
            this.verticalFormulaShifter = FormulaShifter.createForRowCopy(this.sourceSheet.getWorkbook().getSheetIndex(this.sourceSheet), this.sourceSheet.getSheetName(), sourceRange.getFirstRow(), sourceRange.getLastRow(), deltaY, this.sourceSheet.getWorkbook().getSpreadsheetVersion());
        }
        for (int rowNo = sourceRange.getFirstRow(); rowNo <= sourceRange.getLastRow(); ++rowNo) {
            final Row sourceRow = sourceClone.getRow(rowNo);
            for (int columnIndex = sourceRange.getFirstColumn(); columnIndex <= sourceRange.getLastColumn(); ++columnIndex) {
                final Cell sourceCell = sourceRow.getCell(columnIndex);
                if (sourceCell != null) {
                    Row destRow = this.destSheet.getRow(rowNo + deltaY);
                    if (destRow == null) {
                        destRow = this.destSheet.createRow(rowNo + deltaY);
                    }
                    Cell newCell = destRow.getCell(columnIndex + deltaX);
                    if (newCell == null) {
                        newCell = destRow.createCell(columnIndex + deltaX);
                    }
                    cloneCellContent(sourceCell, newCell, null);
                    if (newCell.getCellType() == CellType.FORMULA) {
                        this.adjustCellReferencesInsideFormula(newCell, this.destSheet, deltaX, deltaY);
                    }
                }
            }
        }
    }
    
    protected abstract void adjustCellReferencesInsideFormula(final Cell p0, final Sheet p1, final int p2, final int p3);
    
    protected boolean adjustInBothDirections(final Ptg[] ptgs, final int sheetIndex, final int deltaX, final int deltaY) {
        boolean adjustSucceeded = true;
        if (deltaY != 0) {
            adjustSucceeded = this.verticalFormulaShifter.adjustFormula(ptgs, sheetIndex);
        }
        if (deltaX != 0) {
            adjustSucceeded = (adjustSucceeded && this.horizontalFormulaShifter.adjustFormula(ptgs, sheetIndex));
        }
        return adjustSucceeded;
    }
    
    public static void cloneCellContent(final Cell srcCell, final Cell destCell, final Map<Integer, CellStyle> styleMap) {
        if (styleMap != null) {
            if (srcCell.getSheet().getWorkbook() == destCell.getSheet().getWorkbook()) {
                destCell.setCellStyle(srcCell.getCellStyle());
            }
            else {
                final int stHashCode = srcCell.getCellStyle().hashCode();
                CellStyle newCellStyle = styleMap.get(stHashCode);
                if (newCellStyle == null) {
                    newCellStyle = destCell.getSheet().getWorkbook().createCellStyle();
                    newCellStyle.cloneStyleFrom(srcCell.getCellStyle());
                    styleMap.put(stHashCode, newCellStyle);
                }
                destCell.setCellStyle(newCellStyle);
            }
        }
        switch (srcCell.getCellType()) {
            case STRING: {
                destCell.setCellValue(srcCell.getStringCellValue());
                break;
            }
            case NUMERIC: {
                destCell.setCellValue(srcCell.getNumericCellValue());
                break;
            }
            case BLANK: {
                destCell.setBlank();
                break;
            }
            case BOOLEAN: {
                destCell.setCellValue(srcCell.getBooleanCellValue());
                break;
            }
            case ERROR: {
                destCell.setCellErrorValue(srcCell.getErrorCellValue());
                break;
            }
            case FORMULA: {
                final String oldFormula = srcCell.getCellFormula();
                destCell.setCellFormula(oldFormula);
                break;
            }
        }
    }
}
