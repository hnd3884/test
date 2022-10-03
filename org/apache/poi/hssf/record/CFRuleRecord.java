package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import java.util.Arrays;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.formula.ptg.Ptg;

public final class CFRuleRecord extends CFRuleBase
{
    public static final short sid = 433;
    
    public CFRuleRecord(final CFRuleRecord other) {
        super(other);
    }
    
    private CFRuleRecord(final byte conditionType, final byte comparisonOperation) {
        super(conditionType, comparisonOperation);
        this.setDefaults();
    }
    
    private CFRuleRecord(final byte conditionType, final byte comparisonOperation, final Ptg[] formula1, final Ptg[] formula2) {
        super(conditionType, comparisonOperation, formula1, formula2);
        this.setDefaults();
    }
    
    private void setDefaults() {
        this.formatting_options = CFRuleRecord.modificationBits.setValue(this.formatting_options, -1);
        this.formatting_options = CFRuleRecord.fmtBlockBits.setValue(this.formatting_options, 0);
        this.formatting_options = CFRuleRecord.undocumented.clear(this.formatting_options);
        this.formatting_not_used = -32766;
        this._fontFormatting = null;
        this._borderFormatting = null;
        this._patternFormatting = null;
    }
    
    public static CFRuleRecord create(final HSSFSheet sheet, final String formulaText) {
        final Ptg[] formula1 = CFRuleBase.parseFormula(formulaText, sheet);
        return new CFRuleRecord((byte)2, (byte)0, formula1, null);
    }
    
    public static CFRuleRecord create(final HSSFSheet sheet, final byte comparisonOperation, final String formulaText1, final String formulaText2) {
        final Ptg[] formula1 = CFRuleBase.parseFormula(formulaText1, sheet);
        final Ptg[] formula2 = CFRuleBase.parseFormula(formulaText2, sheet);
        return new CFRuleRecord((byte)1, comparisonOperation, formula1, formula2);
    }
    
    public CFRuleRecord(final RecordInputStream in) {
        this.setConditionType(in.readByte());
        this.setComparisonOperation(in.readByte());
        final int field_3_formula1_len = in.readUShort();
        final int field_4_formula2_len = in.readUShort();
        this.readFormatOptions(in);
        this.setFormula1(Formula.read(field_3_formula1_len, in));
        this.setFormula2(Formula.read(field_4_formula2_len, in));
    }
    
    @Override
    public short getSid() {
        return 433;
    }
    
    public void serialize(final LittleEndianOutput out) {
        final int formula1Len = CFRuleBase.getFormulaSize(this.getFormula1());
        final int formula2Len = CFRuleBase.getFormulaSize(this.getFormula2());
        out.writeByte(this.getConditionType());
        out.writeByte(this.getComparisonOperation());
        out.writeShort(formula1Len);
        out.writeShort(formula2Len);
        this.serializeFormattingBlock(out);
        this.getFormula1().serializeTokens(out);
        this.getFormula2().serializeTokens(out);
    }
    
    @Override
    protected int getDataSize() {
        return 6 + this.getFormattingBlockSize() + CFRuleBase.getFormulaSize(this.getFormula1()) + CFRuleBase.getFormulaSize(this.getFormula2());
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[CFRULE]\n");
        buffer.append("    .condition_type   =").append(this.getConditionType()).append("\n");
        buffer.append("    OPTION FLAGS=0x").append(Integer.toHexString(this.getOptions())).append("\n");
        if (this.containsFontFormattingBlock()) {
            buffer.append(this._fontFormatting).append("\n");
        }
        if (this.containsBorderFormattingBlock()) {
            buffer.append(this._borderFormatting).append("\n");
        }
        if (this.containsPatternFormattingBlock()) {
            buffer.append(this._patternFormatting).append("\n");
        }
        buffer.append("    Formula 1 =").append(Arrays.toString(this.getFormula1().getTokens())).append("\n");
        buffer.append("    Formula 2 =").append(Arrays.toString(this.getFormula2().getTokens())).append("\n");
        buffer.append("[/CFRULE]\n");
        return buffer.toString();
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    @Override
    public CFRuleRecord clone() {
        return this.copy();
    }
    
    @Override
    public CFRuleRecord copy() {
        return new CFRuleRecord(this);
    }
}
