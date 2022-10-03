package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.CFRuleBase;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.hssf.record.aggregates.CFRecordsAggregate;
import org.apache.poi.ss.usermodel.ConditionalFormatting;

public final class HSSFConditionalFormatting implements ConditionalFormatting
{
    private final HSSFSheet sheet;
    private final CFRecordsAggregate cfAggregate;
    
    HSSFConditionalFormatting(final HSSFSheet sheet, final CFRecordsAggregate cfAggregate) {
        if (sheet == null) {
            throw new IllegalArgumentException("sheet must not be null");
        }
        if (cfAggregate == null) {
            throw new IllegalArgumentException("cfAggregate must not be null");
        }
        this.sheet = sheet;
        this.cfAggregate = cfAggregate;
    }
    
    CFRecordsAggregate getCFRecordsAggregate() {
        return this.cfAggregate;
    }
    
    @Override
    public CellRangeAddress[] getFormattingRanges() {
        return this.cfAggregate.getHeader().getCellRanges();
    }
    
    @Override
    public void setFormattingRanges(final CellRangeAddress[] ranges) {
        this.cfAggregate.getHeader().setCellRanges(ranges);
    }
    
    public void setRule(final int idx, final HSSFConditionalFormattingRule cfRule) {
        this.cfAggregate.setRule(idx, cfRule.getCfRuleRecord());
    }
    
    @Override
    public void setRule(final int idx, final ConditionalFormattingRule cfRule) {
        this.setRule(idx, (HSSFConditionalFormattingRule)cfRule);
    }
    
    public void addRule(final HSSFConditionalFormattingRule cfRule) {
        this.cfAggregate.addRule(cfRule.getCfRuleRecord());
    }
    
    @Override
    public void addRule(final ConditionalFormattingRule cfRule) {
        this.addRule((HSSFConditionalFormattingRule)cfRule);
    }
    
    @Override
    public HSSFConditionalFormattingRule getRule(final int idx) {
        final CFRuleBase ruleRecord = this.cfAggregate.getRule(idx);
        return new HSSFConditionalFormattingRule(this.sheet, ruleRecord);
    }
    
    @Override
    public int getNumberOfRules() {
        return this.cfAggregate.getNumberOfRules();
    }
    
    @Override
    public String toString() {
        return this.cfAggregate.toString();
    }
}
