package org.apache.poi.ss.usermodel.helpers;

import org.apache.poi.ss.formula.ptg.AreaErrPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.util.CellRangeAddress;
import java.util.List;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.util.Internal;

@Internal
public abstract class BaseRowColShifter
{
    public abstract void updateNamedRanges(final FormulaShifter p0);
    
    public abstract void updateFormulas(final FormulaShifter p0);
    
    public abstract List<CellRangeAddress> shiftMergedRegions(final int p0, final int p1, final int p2);
    
    public abstract void updateConditionalFormatting(final FormulaShifter p0);
    
    public abstract void updateHyperlinks(final FormulaShifter p0);
    
    public static CellRangeAddress shiftRange(final FormulaShifter formulaShifter, final CellRangeAddress cra, final int currentExternSheetIx) {
        final AreaPtg aptg = new AreaPtg(cra.getFirstRow(), cra.getLastRow(), cra.getFirstColumn(), cra.getLastColumn(), false, false, false, false);
        final Ptg[] ptgs = { aptg };
        if (!formulaShifter.adjustFormula(ptgs, currentExternSheetIx)) {
            return cra;
        }
        final Ptg ptg0 = ptgs[0];
        if (ptg0 instanceof AreaPtg) {
            final AreaPtg bptg = (AreaPtg)ptg0;
            return new CellRangeAddress(bptg.getFirstRow(), bptg.getLastRow(), bptg.getFirstColumn(), bptg.getLastColumn());
        }
        if (ptg0 instanceof AreaErrPtg) {
            return null;
        }
        throw new IllegalStateException("Unexpected shifted ptg class (" + ptg0.getClass().getName() + ")");
    }
}
