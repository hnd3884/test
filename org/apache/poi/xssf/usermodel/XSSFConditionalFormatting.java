package org.apache.poi.xssf.usermodel;

import org.apache.xmlbeans.XmlObject;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConditionalFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormatting;

public class XSSFConditionalFormatting implements ConditionalFormatting
{
    private final CTConditionalFormatting _cf;
    private final XSSFSheet _sh;
    
    XSSFConditionalFormatting(final XSSFSheet sh) {
        this._cf = CTConditionalFormatting.Factory.newInstance();
        this._sh = sh;
    }
    
    XSSFConditionalFormatting(final XSSFSheet sh, final CTConditionalFormatting cf) {
        this._cf = cf;
        this._sh = sh;
    }
    
    CTConditionalFormatting getCTConditionalFormatting() {
        return this._cf;
    }
    
    public CellRangeAddress[] getFormattingRanges() {
        final ArrayList<CellRangeAddress> lst = new ArrayList<CellRangeAddress>();
        for (final Object stRef : this._cf.getSqref()) {
            final String[] split;
            final String[] regions = split = stRef.toString().split(" ");
            for (final String region : split) {
                lst.add(CellRangeAddress.valueOf(region));
            }
        }
        return lst.toArray(new CellRangeAddress[0]);
    }
    
    public void setFormattingRanges(final CellRangeAddress[] ranges) {
        if (ranges == null) {
            throw new IllegalArgumentException("cellRanges must not be null");
        }
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final CellRangeAddress range : ranges) {
            if (!first) {
                sb.append(" ");
            }
            else {
                first = false;
            }
            sb.append(range.formatAsString());
        }
        this._cf.setSqref((List)Collections.singletonList(sb.toString()));
    }
    
    public void setRule(final int idx, final ConditionalFormattingRule cfRule) {
        final XSSFConditionalFormattingRule xRule = (XSSFConditionalFormattingRule)cfRule;
        this._cf.getCfRuleArray(idx).set((XmlObject)xRule.getCTCfRule());
    }
    
    public void addRule(final ConditionalFormattingRule cfRule) {
        final XSSFConditionalFormattingRule xRule = (XSSFConditionalFormattingRule)cfRule;
        this._cf.addNewCfRule().set((XmlObject)xRule.getCTCfRule());
    }
    
    public XSSFConditionalFormattingRule getRule(final int idx) {
        return new XSSFConditionalFormattingRule(this._sh, this._cf.getCfRuleArray(idx));
    }
    
    public int getNumberOfRules() {
        return this._cf.sizeOfCfRuleArray();
    }
    
    @Override
    public String toString() {
        return this._cf.toString();
    }
}
