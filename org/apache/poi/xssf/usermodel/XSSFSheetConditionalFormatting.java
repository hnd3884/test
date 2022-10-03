package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConditionalFormatting;
import org.apache.xmlbeans.XmlObject;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.ss.util.CellRangeUtil;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.IconMultiStateFormatting;
import org.apache.poi.ss.usermodel.ExtendedColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfRule;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STConditionalFormattingOperator;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCfType;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;

public class XSSFSheetConditionalFormatting implements SheetConditionalFormatting
{
    protected static final String CF_EXT_2009_NS_X14 = "http://schemas.microsoft.com/office/spreadsheetml/2009/9/main";
    private final XSSFSheet _sheet;
    
    XSSFSheetConditionalFormatting(final XSSFSheet sheet) {
        this._sheet = sheet;
    }
    
    public XSSFConditionalFormattingRule createConditionalFormattingRule(final byte comparisonOperation, final String formula1, final String formula2) {
        final XSSFConditionalFormattingRule rule = new XSSFConditionalFormattingRule(this._sheet);
        final CTCfRule cfRule = rule.getCTCfRule();
        cfRule.addFormula(formula1);
        if (formula2 != null) {
            cfRule.addFormula(formula2);
        }
        cfRule.setType(STCfType.CELL_IS);
        STConditionalFormattingOperator.Enum operator = null;
        switch (comparisonOperation) {
            case 1: {
                operator = STConditionalFormattingOperator.BETWEEN;
                break;
            }
            case 2: {
                operator = STConditionalFormattingOperator.NOT_BETWEEN;
                break;
            }
            case 6: {
                operator = STConditionalFormattingOperator.LESS_THAN;
                break;
            }
            case 8: {
                operator = STConditionalFormattingOperator.LESS_THAN_OR_EQUAL;
                break;
            }
            case 5: {
                operator = STConditionalFormattingOperator.GREATER_THAN;
                break;
            }
            case 7: {
                operator = STConditionalFormattingOperator.GREATER_THAN_OR_EQUAL;
                break;
            }
            case 3: {
                operator = STConditionalFormattingOperator.EQUAL;
                break;
            }
            case 4: {
                operator = STConditionalFormattingOperator.NOT_EQUAL;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown comparison operator: " + comparisonOperation);
            }
        }
        cfRule.setOperator(operator);
        return rule;
    }
    
    public XSSFConditionalFormattingRule createConditionalFormattingRule(final byte comparisonOperation, final String formula) {
        return this.createConditionalFormattingRule(comparisonOperation, formula, null);
    }
    
    public XSSFConditionalFormattingRule createConditionalFormattingRule(final String formula) {
        final XSSFConditionalFormattingRule rule = new XSSFConditionalFormattingRule(this._sheet);
        final CTCfRule cfRule = rule.getCTCfRule();
        cfRule.addFormula(formula);
        cfRule.setType(STCfType.EXPRESSION);
        return rule;
    }
    
    public XSSFConditionalFormattingRule createConditionalFormattingRule(final XSSFColor color) {
        final XSSFConditionalFormattingRule rule = new XSSFConditionalFormattingRule(this._sheet);
        rule.createDataBarFormatting(color);
        return rule;
    }
    
    public XSSFConditionalFormattingRule createConditionalFormattingRule(final ExtendedColor color) {
        return this.createConditionalFormattingRule((XSSFColor)color);
    }
    
    public XSSFConditionalFormattingRule createConditionalFormattingRule(final IconMultiStateFormatting.IconSet iconSet) {
        final XSSFConditionalFormattingRule rule = new XSSFConditionalFormattingRule(this._sheet);
        rule.createMultiStateFormatting(iconSet);
        return rule;
    }
    
    public XSSFConditionalFormattingRule createConditionalFormattingColorScaleRule() {
        final XSSFConditionalFormattingRule rule = new XSSFConditionalFormattingRule(this._sheet);
        rule.createColorScaleFormatting();
        return rule;
    }
    
    public int addConditionalFormatting(final CellRangeAddress[] regions, final ConditionalFormattingRule[] cfRules) {
        if (regions == null) {
            throw new IllegalArgumentException("regions must not be null");
        }
        for (final CellRangeAddress range : regions) {
            range.validate(SpreadsheetVersion.EXCEL2007);
        }
        if (cfRules == null) {
            throw new IllegalArgumentException("cfRules must not be null");
        }
        if (cfRules.length == 0) {
            throw new IllegalArgumentException("cfRules must not be empty");
        }
        if (cfRules.length > 3) {
            throw new IllegalArgumentException("Number of rules must not exceed 3");
        }
        final CellRangeAddress[] mergeCellRanges = CellRangeUtil.mergeCellRanges(regions);
        final CTConditionalFormatting cf = this._sheet.getCTWorksheet().addNewConditionalFormatting();
        final List<String> refs = new ArrayList<String>();
        for (final CellRangeAddress a : mergeCellRanges) {
            refs.add(a.formatAsString());
        }
        cf.setSqref((List)refs);
        int priority = 1;
        for (final CTConditionalFormatting c : this._sheet.getCTWorksheet().getConditionalFormattingArray()) {
            priority += c.sizeOfCfRuleArray();
        }
        for (final ConditionalFormattingRule rule : cfRules) {
            final XSSFConditionalFormattingRule xRule = (XSSFConditionalFormattingRule)rule;
            xRule.getCTCfRule().setPriority(priority++);
            cf.addNewCfRule().set((XmlObject)xRule.getCTCfRule());
        }
        return this._sheet.getCTWorksheet().sizeOfConditionalFormattingArray() - 1;
    }
    
    public int addConditionalFormatting(final CellRangeAddress[] regions, final ConditionalFormattingRule rule1) {
        return this.addConditionalFormatting(regions, (ConditionalFormattingRule[])((rule1 == null) ? null : new XSSFConditionalFormattingRule[] { (XSSFConditionalFormattingRule)rule1 }));
    }
    
    public int addConditionalFormatting(final CellRangeAddress[] regions, final ConditionalFormattingRule rule1, final ConditionalFormattingRule rule2) {
        return this.addConditionalFormatting(regions, (ConditionalFormattingRule[])((rule1 == null) ? null : new XSSFConditionalFormattingRule[] { (XSSFConditionalFormattingRule)rule1, (XSSFConditionalFormattingRule)rule2 }));
    }
    
    public int addConditionalFormatting(final ConditionalFormatting cf) {
        final XSSFConditionalFormatting xcf = (XSSFConditionalFormatting)cf;
        final CTWorksheet sh = this._sheet.getCTWorksheet();
        sh.addNewConditionalFormatting().set(xcf.getCTConditionalFormatting().copy());
        return sh.sizeOfConditionalFormattingArray() - 1;
    }
    
    public XSSFConditionalFormatting getConditionalFormattingAt(final int index) {
        this.checkIndex(index);
        final CTConditionalFormatting cf = this._sheet.getCTWorksheet().getConditionalFormattingArray(index);
        return new XSSFConditionalFormatting(this._sheet, cf);
    }
    
    public int getNumConditionalFormattings() {
        return this._sheet.getCTWorksheet().sizeOfConditionalFormattingArray();
    }
    
    public void removeConditionalFormatting(final int index) {
        this.checkIndex(index);
        this._sheet.getCTWorksheet().removeConditionalFormatting(index);
    }
    
    private void checkIndex(final int index) {
        final int cnt = this.getNumConditionalFormattings();
        if (index < 0 || index >= cnt) {
            throw new IllegalArgumentException("Specified CF index " + index + " is outside the allowable range (0.." + (cnt - 1) + ")");
        }
    }
}
