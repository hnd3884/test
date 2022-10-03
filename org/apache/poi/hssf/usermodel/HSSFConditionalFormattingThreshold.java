package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.CFRuleBase;
import org.apache.poi.hssf.record.cf.Threshold;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;

public final class HSSFConditionalFormattingThreshold implements ConditionalFormattingThreshold
{
    private final Threshold threshold;
    private final HSSFSheet sheet;
    private final HSSFWorkbook workbook;
    
    protected HSSFConditionalFormattingThreshold(final Threshold threshold, final HSSFSheet sheet) {
        this.threshold = threshold;
        this.sheet = sheet;
        this.workbook = sheet.getWorkbook();
    }
    
    protected Threshold getThreshold() {
        return this.threshold;
    }
    
    @Override
    public RangeType getRangeType() {
        return RangeType.byId(this.threshold.getType());
    }
    
    @Override
    public void setRangeType(final RangeType type) {
        this.threshold.setType((byte)type.id);
    }
    
    @Override
    public String getFormula() {
        return HSSFConditionalFormattingRule.toFormulaString(this.threshold.getParsedExpression(), this.workbook);
    }
    
    @Override
    public void setFormula(final String formula) {
        this.threshold.setParsedExpression(CFRuleBase.parseFormula(formula, this.sheet));
    }
    
    @Override
    public Double getValue() {
        return this.threshold.getValue();
    }
    
    @Override
    public void setValue(final Double value) {
        this.threshold.setValue(value);
    }
}
