package org.apache.poi.xssf.usermodel.helpers;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfRule;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConditionalFormatting;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import java.util.List;
import org.apache.poi.ss.usermodel.helpers.BaseRowColShifter;
import org.apache.poi.ss.util.CellRangeAddress;
import java.util.ArrayList;
import org.apache.poi.ss.formula.FormulaParseException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellFormulaType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.formula.ptg.Ptg;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;

@Internal
final class XSSFRowColShifter
{
    private static final POILogger logger;
    
    private XSSFRowColShifter() {
    }
    
    static void updateNamedRanges(final Sheet sheet, final FormulaShifter formulaShifter) {
        final Workbook wb = sheet.getWorkbook();
        final XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create((XSSFWorkbook)wb);
        for (final Name name : wb.getAllNames()) {
            final String formula = name.getRefersToFormula();
            final int sheetIndex = name.getSheetIndex();
            final int rowIndex = -1;
            final Ptg[] ptgs = FormulaParser.parse(formula, (FormulaParsingWorkbook)fpb, FormulaType.NAMEDRANGE, sheetIndex, -1);
            if (formulaShifter.adjustFormula(ptgs, sheetIndex)) {
                final String shiftedFmla = FormulaRenderer.toFormulaString((FormulaRenderingWorkbook)fpb, ptgs);
                name.setRefersToFormula(shiftedFmla);
            }
        }
    }
    
    static void updateFormulas(final Sheet sheet, final FormulaShifter formulaShifter) {
        updateSheetFormulas(sheet, formulaShifter);
        final Workbook wb = sheet.getWorkbook();
        for (final Sheet sh : wb) {
            if (sheet == sh) {
                continue;
            }
            updateSheetFormulas(sh, formulaShifter);
        }
    }
    
    static void updateSheetFormulas(final Sheet sh, final FormulaShifter formulashifter) {
        for (final Row r : sh) {
            final XSSFRow row = (XSSFRow)r;
            updateRowFormulas(row, formulashifter);
        }
    }
    
    static void updateRowFormulas(final XSSFRow row, final FormulaShifter formulaShifter) {
        final XSSFSheet sheet = row.getSheet();
        for (final Cell c : row) {
            final XSSFCell cell = (XSSFCell)c;
            final CTCell ctCell = cell.getCTCell();
            if (ctCell.isSetF()) {
                final CTCellFormula f = ctCell.getF();
                final String formula = f.getStringValue();
                if (formula.length() > 0) {
                    final String shiftedFormula = shiftFormula((Row)row, formula, formulaShifter);
                    if (shiftedFormula != null) {
                        f.setStringValue(shiftedFormula);
                        if (f.getT() == STCellFormulaType.SHARED) {
                            final int si = Math.toIntExact(f.getSi());
                            final CTCellFormula sf = sheet.getSharedFormula(si);
                            sf.setStringValue(shiftedFormula);
                            updateRefInCTCellFormula((Row)row, formulaShifter, sf);
                        }
                    }
                }
                updateRefInCTCellFormula((Row)row, formulaShifter, f);
            }
        }
    }
    
    static String shiftFormula(final Row row, final String formula, final FormulaShifter formulaShifter) {
        final Sheet sheet = row.getSheet();
        final Workbook wb = sheet.getWorkbook();
        final int sheetIndex = wb.getSheetIndex(sheet);
        final int rowIndex = row.getRowNum();
        final XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create((XSSFWorkbook)wb);
        try {
            final Ptg[] ptgs = FormulaParser.parse(formula, (FormulaParsingWorkbook)fpb, FormulaType.CELL, sheetIndex, rowIndex);
            String shiftedFmla = null;
            if (formulaShifter.adjustFormula(ptgs, sheetIndex)) {
                shiftedFmla = FormulaRenderer.toFormulaString((FormulaRenderingWorkbook)fpb, ptgs);
            }
            return shiftedFmla;
        }
        catch (final FormulaParseException fpe) {
            XSSFRowColShifter.logger.log(5, new Object[] { "Error shifting formula on row ", row.getRowNum(), fpe });
            return formula;
        }
    }
    
    static void updateRefInCTCellFormula(final Row row, final FormulaShifter formulaShifter, final CTCellFormula f) {
        if (f.isSetRef()) {
            final String ref = f.getRef();
            final String shiftedRef = shiftFormula(row, ref, formulaShifter);
            if (shiftedRef != null) {
                f.setRef(shiftedRef);
            }
        }
    }
    
    static void updateConditionalFormatting(final Sheet sheet, final FormulaShifter formulaShifter) {
        final XSSFSheet xsheet = (XSSFSheet)sheet;
        final XSSFWorkbook wb = xsheet.getWorkbook();
        final int sheetIndex = wb.getSheetIndex(sheet);
        final int rowIndex = -1;
        final XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create(wb);
        final CTWorksheet ctWorksheet = xsheet.getCTWorksheet();
        final CTConditionalFormatting[] conditionalFormattingArray = ctWorksheet.getConditionalFormattingArray();
        for (int j = conditionalFormattingArray.length - 1; j >= 0; --j) {
            final CTConditionalFormatting cf = conditionalFormattingArray[j];
            final ArrayList<CellRangeAddress> cellRanges = new ArrayList<CellRangeAddress>();
            for (final Object stRef : cf.getSqref()) {
                final String[] split;
                final String[] regions = split = stRef.toString().split(" ");
                for (final String region : split) {
                    cellRanges.add(CellRangeAddress.valueOf(region));
                }
            }
            boolean changed = false;
            final List<CellRangeAddress> temp = new ArrayList<CellRangeAddress>();
            for (final CellRangeAddress craOld : cellRanges) {
                final CellRangeAddress craNew = BaseRowColShifter.shiftRange(formulaShifter, craOld, sheetIndex);
                if (craNew == null) {
                    changed = true;
                }
                else {
                    temp.add(craNew);
                    if (craNew == craOld) {
                        continue;
                    }
                    changed = true;
                }
            }
            if (changed) {
                final int nRanges = temp.size();
                if (nRanges == 0) {
                    ctWorksheet.removeConditionalFormatting(j);
                    continue;
                }
                final List<String> refs = new ArrayList<String>();
                for (final CellRangeAddress a : temp) {
                    refs.add(a.formatAsString());
                }
                cf.setSqref((List)refs);
            }
            for (final CTCfRule cfRule : cf.getCfRuleArray()) {
                final String[] formulaArray = cfRule.getFormulaArray();
                for (int i = 0; i < formulaArray.length; ++i) {
                    final String formula = formulaArray[i];
                    final Ptg[] ptgs = FormulaParser.parse(formula, (FormulaParsingWorkbook)fpb, FormulaType.CELL, sheetIndex, -1);
                    if (formulaShifter.adjustFormula(ptgs, sheetIndex)) {
                        final String shiftedFmla = FormulaRenderer.toFormulaString((FormulaRenderingWorkbook)fpb, ptgs);
                        cfRule.setFormulaArray(i, shiftedFmla);
                    }
                }
            }
        }
    }
    
    static void updateHyperlinks(final Sheet sheet, final FormulaShifter formulaShifter) {
        final int sheetIndex = sheet.getWorkbook().getSheetIndex(sheet);
        final List<? extends Hyperlink> hyperlinkList = sheet.getHyperlinkList();
        for (final Hyperlink hyperlink : hyperlinkList) {
            final XSSFHyperlink xhyperlink = (XSSFHyperlink)hyperlink;
            final String cellRef = xhyperlink.getCellRef();
            final CellRangeAddress cra = CellRangeAddress.valueOf(cellRef);
            final CellRangeAddress shiftedRange = BaseRowColShifter.shiftRange(formulaShifter, cra, sheetIndex);
            if (shiftedRange != null && shiftedRange != cra) {
                xhyperlink.setCellReference(shiftedRange.formatAsString());
            }
        }
    }
    
    static {
        logger = POILogFactory.getLogger((Class)XSSFRowColShifter.class);
    }
}
