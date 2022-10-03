package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCfvoType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;

public class XSSFConditionalFormattingThreshold implements ConditionalFormattingThreshold
{
    private CTCfvo cfvo;
    
    protected XSSFConditionalFormattingThreshold(final CTCfvo cfvo) {
        this.cfvo = cfvo;
    }
    
    protected CTCfvo getCTCfvo() {
        return this.cfvo;
    }
    
    public ConditionalFormattingThreshold.RangeType getRangeType() {
        return ConditionalFormattingThreshold.RangeType.byName(this.cfvo.getType().toString());
    }
    
    public void setRangeType(final ConditionalFormattingThreshold.RangeType type) {
        final STCfvoType.Enum xtype = STCfvoType.Enum.forString(type.name);
        this.cfvo.setType(xtype);
    }
    
    public String getFormula() {
        if (this.cfvo.getType() == STCfvoType.FORMULA) {
            return this.cfvo.getVal();
        }
        return null;
    }
    
    public void setFormula(final String formula) {
        this.cfvo.setVal(formula);
    }
    
    public Double getValue() {
        if (this.cfvo.getType() == STCfvoType.FORMULA || this.cfvo.getType() == STCfvoType.MIN || this.cfvo.getType() == STCfvoType.MAX) {
            return null;
        }
        if (this.cfvo.isSetVal()) {
            return Double.parseDouble(this.cfvo.getVal());
        }
        return null;
    }
    
    public void setValue(final Double value) {
        if (value == null) {
            this.cfvo.unsetVal();
        }
        else {
            this.cfvo.setVal(value.toString());
        }
    }
}
