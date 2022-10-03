package org.apache.poi.hssf.usermodel.helpers;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.helpers.RowShifter;

public final class HSSFRowShifter extends RowShifter
{
    private static final POILogger logger;
    
    public HSSFRowShifter(final HSSFSheet sh) {
        super(sh);
    }
    
    @NotImplemented
    @Override
    public void updateNamedRanges(final FormulaShifter formulaShifter) {
        throw new NotImplementedException("HSSFRowShifter.updateNamedRanges");
    }
    
    @NotImplemented
    @Override
    public void updateFormulas(final FormulaShifter formulaShifter) {
        throw new NotImplementedException("updateFormulas");
    }
    
    @NotImplemented
    @Override
    public void updateConditionalFormatting(final FormulaShifter formulaShifter) {
        throw new NotImplementedException("updateConditionalFormatting");
    }
    
    @NotImplemented
    @Override
    public void updateHyperlinks(final FormulaShifter formulaShifter) {
        throw new NotImplementedException("updateHyperlinks");
    }
    
    static {
        logger = POILogFactory.getLogger(HSSFRowShifter.class);
    }
}
