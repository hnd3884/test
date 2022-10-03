package org.apache.poi.xssf.usermodel.helpers;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.helpers.ColumnShifter;

public final class XSSFColumnShifter extends ColumnShifter
{
    private static final POILogger logger;
    
    public XSSFColumnShifter(final XSSFSheet sh) {
        super((Sheet)sh);
    }
    
    public void updateNamedRanges(final FormulaShifter formulaShifter) {
        XSSFRowColShifter.updateNamedRanges(this.sheet, formulaShifter);
    }
    
    public void updateFormulas(final FormulaShifter formulaShifter) {
        XSSFRowColShifter.updateFormulas(this.sheet, formulaShifter);
    }
    
    public void updateConditionalFormatting(final FormulaShifter formulaShifter) {
        XSSFRowColShifter.updateConditionalFormatting(this.sheet, formulaShifter);
    }
    
    public void updateHyperlinks(final FormulaShifter formulaShifter) {
        XSSFRowColShifter.updateHyperlinks(this.sheet, formulaShifter);
    }
    
    static {
        logger = POILogFactory.getLogger((Class)XSSFColumnShifter.class);
    }
}
