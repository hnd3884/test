package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.RangeCopier;

public class HSSFRangeCopier extends RangeCopier
{
    public HSSFRangeCopier(final Sheet sourceSheet, final Sheet destSheet) {
        super(sourceSheet, destSheet);
    }
    
    @Override
    protected void adjustCellReferencesInsideFormula(final Cell cell, final Sheet destSheet, final int deltaX, final int deltaY) {
        final FormulaRecordAggregate fra = (FormulaRecordAggregate)((HSSFCell)cell).getCellValueRecord();
        final int destSheetIndex = destSheet.getWorkbook().getSheetIndex(destSheet);
        final Ptg[] ptgs = fra.getFormulaTokens();
        if (this.adjustInBothDirections(ptgs, destSheetIndex, deltaX, deltaY)) {
            fra.setParsedExpression(ptgs);
        }
    }
}
