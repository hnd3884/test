package org.apache.poi.hssf.model;

import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.Internal;

@Internal
public final class HSSFFormulaParser
{
    private static FormulaParsingWorkbook createParsingWorkbook(final HSSFWorkbook book) {
        return HSSFEvaluationWorkbook.create(book);
    }
    
    private HSSFFormulaParser() {
    }
    
    public static Ptg[] parse(final String formula, final HSSFWorkbook workbook) throws FormulaParseException {
        return parse(formula, workbook, FormulaType.CELL);
    }
    
    public static Ptg[] parse(final String formula, final HSSFWorkbook workbook, final FormulaType formulaType) throws FormulaParseException {
        return parse(formula, workbook, formulaType, -1);
    }
    
    public static Ptg[] parse(final String formula, final HSSFWorkbook workbook, final FormulaType formulaType, final int sheetIndex) throws FormulaParseException {
        return FormulaParser.parse(formula, createParsingWorkbook(workbook), formulaType, sheetIndex);
    }
    
    public static String toFormulaString(final HSSFWorkbook book, final Ptg[] ptgs) {
        return FormulaRenderer.toFormulaString(HSSFEvaluationWorkbook.create(book), ptgs);
    }
}
