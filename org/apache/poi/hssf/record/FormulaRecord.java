package org.apache.poi.hssf.record;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.util.BitField;

public final class FormulaRecord extends CellRecord
{
    public static final short sid = 6;
    private static final int FIXED_SIZE = 14;
    private static final BitField alwaysCalc;
    private static final BitField calcOnLoad;
    private static final BitField sharedFormula;
    private double field_4_value;
    private short field_5_options;
    private int field_6_zero;
    private Formula field_8_parsed_expr;
    private FormulaSpecialCachedValue specialCachedValue;
    
    public FormulaRecord() {
        this.field_8_parsed_expr = Formula.create(Ptg.EMPTY_PTG_ARRAY);
    }
    
    public FormulaRecord(final FormulaRecord other) {
        super(other);
        this.field_4_value = other.field_4_value;
        this.field_5_options = other.field_5_options;
        this.field_6_zero = other.field_6_zero;
        this.field_8_parsed_expr = ((other.field_8_parsed_expr == null) ? null : new Formula(other.field_8_parsed_expr));
        this.specialCachedValue = ((other.specialCachedValue == null) ? null : new FormulaSpecialCachedValue(other.specialCachedValue));
    }
    
    public FormulaRecord(final RecordInputStream ris) {
        super(ris);
        final long valueLongBits = ris.readLong();
        this.field_5_options = ris.readShort();
        this.specialCachedValue = FormulaSpecialCachedValue.create(valueLongBits);
        if (this.specialCachedValue == null) {
            this.field_4_value = Double.longBitsToDouble(valueLongBits);
        }
        this.field_6_zero = ris.readInt();
        final int field_7_expression_len = ris.readShort();
        final int nBytesAvailable = ris.available();
        this.field_8_parsed_expr = Formula.read(field_7_expression_len, ris, nBytesAvailable);
    }
    
    public void setValue(final double value) {
        this.field_4_value = value;
        this.specialCachedValue = null;
    }
    
    public void setCachedResultTypeEmptyString() {
        this.specialCachedValue = FormulaSpecialCachedValue.createCachedEmptyValue();
    }
    
    public void setCachedResultTypeString() {
        this.specialCachedValue = FormulaSpecialCachedValue.createForString();
    }
    
    public void setCachedResultErrorCode(final int errorCode) {
        this.specialCachedValue = FormulaSpecialCachedValue.createCachedErrorCode(errorCode);
    }
    
    public void setCachedResultBoolean(final boolean value) {
        this.specialCachedValue = FormulaSpecialCachedValue.createCachedBoolean(value);
    }
    
    public boolean hasCachedResultString() {
        return this.specialCachedValue != null && this.specialCachedValue.getTypeCode() == 0;
    }
    
    public int getCachedResultType() {
        if (this.specialCachedValue == null) {
            return CellType.NUMERIC.getCode();
        }
        return this.specialCachedValue.getValueType();
    }
    
    public boolean getCachedBooleanValue() {
        return this.specialCachedValue.getBooleanValue();
    }
    
    public int getCachedErrorValue() {
        return this.specialCachedValue.getErrorValue();
    }
    
    public void setOptions(final short options) {
        this.field_5_options = options;
    }
    
    public double getValue() {
        return this.field_4_value;
    }
    
    public short getOptions() {
        return this.field_5_options;
    }
    
    public boolean isSharedFormula() {
        return FormulaRecord.sharedFormula.isSet(this.field_5_options);
    }
    
    public void setSharedFormula(final boolean flag) {
        this.field_5_options = FormulaRecord.sharedFormula.setShortBoolean(this.field_5_options, flag);
    }
    
    public boolean isAlwaysCalc() {
        return FormulaRecord.alwaysCalc.isSet(this.field_5_options);
    }
    
    public void setAlwaysCalc(final boolean flag) {
        this.field_5_options = FormulaRecord.alwaysCalc.setShortBoolean(this.field_5_options, flag);
    }
    
    public boolean isCalcOnLoad() {
        return FormulaRecord.calcOnLoad.isSet(this.field_5_options);
    }
    
    public void setCalcOnLoad(final boolean flag) {
        this.field_5_options = FormulaRecord.calcOnLoad.setShortBoolean(this.field_5_options, flag);
    }
    
    public Ptg[] getParsedExpression() {
        return this.field_8_parsed_expr.getTokens();
    }
    
    public Formula getFormula() {
        return this.field_8_parsed_expr;
    }
    
    public void setParsedExpression(final Ptg[] ptgs) {
        this.field_8_parsed_expr = Formula.create(ptgs);
    }
    
    @Override
    public short getSid() {
        return 6;
    }
    
    @Override
    protected int getValueDataSize() {
        return 14 + this.field_8_parsed_expr.getEncodedSize();
    }
    
    @Override
    protected void serializeValue(final LittleEndianOutput out) {
        if (this.specialCachedValue == null) {
            out.writeDouble(this.field_4_value);
        }
        else {
            this.specialCachedValue.serialize(out);
        }
        out.writeShort(this.getOptions());
        out.writeInt(this.field_6_zero);
        this.field_8_parsed_expr.serialize(out);
    }
    
    @Override
    protected String getRecordName() {
        return "FORMULA";
    }
    
    @Override
    protected void appendValueText(final StringBuilder sb) {
        sb.append("  .value\t = ");
        if (this.specialCachedValue == null) {
            sb.append(this.field_4_value).append("\n");
        }
        else {
            sb.append(this.specialCachedValue.formatDebugString()).append("\n");
        }
        sb.append("  .options   = ").append(HexDump.shortToHex(this.getOptions())).append("\n");
        sb.append("    .alwaysCalc= ").append(this.isAlwaysCalc()).append("\n");
        sb.append("    .calcOnLoad= ").append(this.isCalcOnLoad()).append("\n");
        sb.append("    .shared    = ").append(this.isSharedFormula()).append("\n");
        sb.append("  .zero      = ").append(HexDump.intToHex(this.field_6_zero)).append("\n");
        final Ptg[] ptgs = this.field_8_parsed_expr.getTokens();
        for (int k = 0; k < ptgs.length; ++k) {
            if (k > 0) {
                sb.append("\n");
            }
            sb.append("    Ptg[").append(k).append("]=");
            final Ptg ptg = ptgs[k];
            sb.append(ptg).append(ptg.getRVAType());
        }
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public FormulaRecord clone() {
        return this.copy();
    }
    
    @Override
    public FormulaRecord copy() {
        return new FormulaRecord(this);
    }
    
    static {
        alwaysCalc = BitFieldFactory.getInstance(1);
        calcOnLoad = BitFieldFactory.getInstance(2);
        sharedFormula = BitFieldFactory.getInstance(8);
    }
}
