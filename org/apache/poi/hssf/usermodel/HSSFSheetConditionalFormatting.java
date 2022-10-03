package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.hssf.record.aggregates.CFRecordsAggregate;
import org.apache.poi.ss.usermodel.ExtendedColor;
import org.apache.poi.hssf.record.CFRule12Record;
import org.apache.poi.ss.usermodel.IconMultiStateFormatting;
import org.apache.poi.hssf.record.CFRuleBase;
import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.hssf.record.aggregates.ConditionalFormattingTable;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;

public final class HSSFSheetConditionalFormatting implements SheetConditionalFormatting
{
    private final HSSFSheet _sheet;
    private final ConditionalFormattingTable _conditionalFormattingTable;
    
    HSSFSheetConditionalFormatting(final HSSFSheet sheet) {
        this._sheet = sheet;
        this._conditionalFormattingTable = sheet.getSheet().getConditionalFormattingTable();
    }
    
    @Override
    public HSSFConditionalFormattingRule createConditionalFormattingRule(final byte comparisonOperation, final String formula1, final String formula2) {
        final CFRuleRecord rr = CFRuleRecord.create(this._sheet, comparisonOperation, formula1, formula2);
        return new HSSFConditionalFormattingRule(this._sheet, rr);
    }
    
    @Override
    public HSSFConditionalFormattingRule createConditionalFormattingRule(final byte comparisonOperation, final String formula1) {
        final CFRuleRecord rr = CFRuleRecord.create(this._sheet, comparisonOperation, formula1, null);
        return new HSSFConditionalFormattingRule(this._sheet, rr);
    }
    
    @Override
    public HSSFConditionalFormattingRule createConditionalFormattingRule(final String formula) {
        final CFRuleRecord rr = CFRuleRecord.create(this._sheet, formula);
        return new HSSFConditionalFormattingRule(this._sheet, rr);
    }
    
    @Override
    public HSSFConditionalFormattingRule createConditionalFormattingRule(final IconMultiStateFormatting.IconSet iconSet) {
        final CFRule12Record rr = CFRule12Record.create(this._sheet, iconSet);
        return new HSSFConditionalFormattingRule(this._sheet, rr);
    }
    
    public HSSFConditionalFormattingRule createConditionalFormattingRule(final HSSFExtendedColor color) {
        final CFRule12Record rr = CFRule12Record.create(this._sheet, color.getExtendedColor());
        return new HSSFConditionalFormattingRule(this._sheet, rr);
    }
    
    @Override
    public HSSFConditionalFormattingRule createConditionalFormattingRule(final ExtendedColor color) {
        return this.createConditionalFormattingRule((HSSFExtendedColor)color);
    }
    
    @Override
    public HSSFConditionalFormattingRule createConditionalFormattingColorScaleRule() {
        final CFRule12Record rr = CFRule12Record.createColorScale(this._sheet);
        return new HSSFConditionalFormattingRule(this._sheet, rr);
    }
    
    public int addConditionalFormatting(final HSSFConditionalFormatting cf) {
        final CFRecordsAggregate cfraClone = cf.getCFRecordsAggregate().cloneCFAggregate();
        return this._conditionalFormattingTable.add(cfraClone);
    }
    
    @Override
    public int addConditionalFormatting(final ConditionalFormatting cf) {
        return this.addConditionalFormatting((HSSFConditionalFormatting)cf);
    }
    
    public int addConditionalFormatting(final CellRangeAddress[] regions, final HSSFConditionalFormattingRule[] cfRules) {
        if (regions == null) {
            throw new IllegalArgumentException("regions must not be null");
        }
        for (final CellRangeAddress range : regions) {
            range.validate(SpreadsheetVersion.EXCEL97);
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
        final CFRuleBase[] rules = new CFRuleBase[cfRules.length];
        for (int i = 0; i != cfRules.length; ++i) {
            rules[i] = cfRules[i].getCfRuleRecord();
        }
        final CFRecordsAggregate cfra = new CFRecordsAggregate(regions, rules);
        return this._conditionalFormattingTable.add(cfra);
    }
    
    @Override
    public int addConditionalFormatting(final CellRangeAddress[] regions, final ConditionalFormattingRule[] cfRules) {
        HSSFConditionalFormattingRule[] hfRules;
        if (cfRules instanceof HSSFConditionalFormattingRule[]) {
            hfRules = (HSSFConditionalFormattingRule[])cfRules;
        }
        else {
            hfRules = new HSSFConditionalFormattingRule[cfRules.length];
            System.arraycopy(cfRules, 0, hfRules, 0, hfRules.length);
        }
        return this.addConditionalFormatting(regions, hfRules);
    }
    
    public int addConditionalFormatting(final CellRangeAddress[] regions, final HSSFConditionalFormattingRule rule1) {
        return this.addConditionalFormatting(regions, (HSSFConditionalFormattingRule[])((rule1 == null) ? null : new HSSFConditionalFormattingRule[] { rule1 }));
    }
    
    @Override
    public int addConditionalFormatting(final CellRangeAddress[] regions, final ConditionalFormattingRule rule1) {
        return this.addConditionalFormatting(regions, (HSSFConditionalFormattingRule)rule1);
    }
    
    public int addConditionalFormatting(final CellRangeAddress[] regions, final HSSFConditionalFormattingRule rule1, final HSSFConditionalFormattingRule rule2) {
        return this.addConditionalFormatting(regions, new HSSFConditionalFormattingRule[] { rule1, rule2 });
    }
    
    @Override
    public int addConditionalFormatting(final CellRangeAddress[] regions, final ConditionalFormattingRule rule1, final ConditionalFormattingRule rule2) {
        return this.addConditionalFormatting(regions, (HSSFConditionalFormattingRule)rule1, (HSSFConditionalFormattingRule)rule2);
    }
    
    @Override
    public HSSFConditionalFormatting getConditionalFormattingAt(final int index) {
        final CFRecordsAggregate cf = this._conditionalFormattingTable.get(index);
        if (cf == null) {
            return null;
        }
        return new HSSFConditionalFormatting(this._sheet, cf);
    }
    
    @Override
    public int getNumConditionalFormattings() {
        return this._conditionalFormattingTable.size();
    }
    
    @Override
    public void removeConditionalFormatting(final int index) {
        this._conditionalFormattingTable.remove(index);
    }
}
