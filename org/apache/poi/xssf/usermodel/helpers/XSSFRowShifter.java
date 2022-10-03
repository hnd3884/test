package org.apache.poi.xssf.usermodel.helpers;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.helpers.RowShifter;

public final class XSSFRowShifter extends RowShifter
{
    private static final POILogger logger;
    
    public XSSFRowShifter(final XSSFSheet sh) {
        super((Sheet)sh);
    }
    
    public void updateNamedRanges(final FormulaShifter formulaShifter) {
        XSSFRowColShifter.updateNamedRanges(this.sheet, formulaShifter);
    }
    
    public void updateFormulas(final FormulaShifter formulaShifter) {
        XSSFRowColShifter.updateFormulas(this.sheet, formulaShifter);
    }
    
    @Internal(since = "3.15 beta 2")
    public void updateRowFormulas(final XSSFRow row, final FormulaShifter formulaShifter) {
        XSSFRowColShifter.updateRowFormulas(row, formulaShifter);
    }
    
    public void updateConditionalFormatting(final FormulaShifter formulaShifter) {
        XSSFRowColShifter.updateConditionalFormatting(this.sheet, formulaShifter);
    }
    
    public void updateHyperlinks(final FormulaShifter formulaShifter) {
        XSSFRowColShifter.updateHyperlinks(this.sheet, formulaShifter);
    }
    
    static {
        logger = POILogFactory.getLogger((Class)XSSFRowShifter.class);
    }
}
