package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.RangeCopier;

public class XSSFRangeCopier extends RangeCopier
{
    public XSSFRangeCopier(final Sheet sourceSheet, final Sheet destSheet) {
        super(sourceSheet, destSheet);
    }
    
    protected void adjustCellReferencesInsideFormula(final Cell cell, final Sheet destSheet, final int deltaX, final int deltaY) {
        final XSSFWorkbook hostWorkbook = (XSSFWorkbook)destSheet.getWorkbook();
        final XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create(hostWorkbook);
        final Ptg[] ptgs = FormulaParser.parse(cell.getCellFormula(), (FormulaParsingWorkbook)fpb, FormulaType.CELL, 0);
        final int destSheetIndex = hostWorkbook.getSheetIndex(destSheet);
        if (this.adjustInBothDirections(ptgs, destSheetIndex, deltaX, deltaY)) {
            cell.setCellFormula(FormulaRenderer.toFormulaString((FormulaRenderingWorkbook)fpb, ptgs));
        }
    }
}
