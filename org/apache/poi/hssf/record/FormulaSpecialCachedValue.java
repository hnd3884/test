package org.apache.poi.hssf.record;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.Internal;

@Internal
public final class FormulaSpecialCachedValue
{
    private static final long BIT_MARKER = -281474976710656L;
    private static final int VARIABLE_DATA_LENGTH = 6;
    private static final int DATA_INDEX = 2;
    public static final int STRING = 0;
    public static final int BOOLEAN = 1;
    public static final int ERROR_CODE = 2;
    public static final int EMPTY = 3;
    private final byte[] _variableData;
    
    FormulaSpecialCachedValue(final FormulaSpecialCachedValue other) {
        this._variableData = (byte[])((other._variableData == null) ? null : ((byte[])other._variableData.clone()));
    }
    
    private FormulaSpecialCachedValue(final byte[] data) {
        this._variableData = data;
    }
    
    public int getTypeCode() {
        return this._variableData[0];
    }
    
    public static FormulaSpecialCachedValue create(final long valueLongBits) {
        if ((0xFFFF000000000000L & valueLongBits) != 0xFFFF000000000000L) {
            return null;
        }
        final byte[] result = new byte[6];
        long x = valueLongBits;
        for (int i = 0; i < 6; ++i) {
            result[i] = (byte)x;
            x >>= 8;
        }
        switch (result[0]) {
            case 0:
            case 1:
            case 2:
            case 3: {
                return new FormulaSpecialCachedValue(result);
            }
            default: {
                throw new RecordFormatException("Bad special value code (" + result[0] + ")");
            }
        }
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.write(this._variableData);
        out.writeShort(65535);
    }
    
    public String formatDebugString() {
        return this.formatValue() + ' ' + HexDump.toHex(this._variableData);
    }
    
    private String formatValue() {
        final int typeCode = this.getTypeCode();
        switch (typeCode) {
            case 0: {
                return "<string>";
            }
            case 1: {
                return (this.getDataValue() == 0) ? "FALSE" : "TRUE";
            }
            case 2: {
                return ErrorEval.getText(this.getDataValue());
            }
            case 3: {
                return "<empty>";
            }
            default: {
                return "#error(type=" + typeCode + ")#";
            }
        }
    }
    
    private int getDataValue() {
        return this._variableData[2];
    }
    
    public static FormulaSpecialCachedValue createCachedEmptyValue() {
        return create(3, 0);
    }
    
    public static FormulaSpecialCachedValue createForString() {
        return create(0, 0);
    }
    
    public static FormulaSpecialCachedValue createCachedBoolean(final boolean b) {
        return create(1, b ? 1 : 0);
    }
    
    public static FormulaSpecialCachedValue createCachedErrorCode(final int errorCode) {
        return create(2, errorCode);
    }
    
    private static FormulaSpecialCachedValue create(final int code, final int data) {
        final byte[] vd = { (byte)code, 0, (byte)data, 0, 0, 0 };
        return new FormulaSpecialCachedValue(vd);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + '[' + this.formatValue() + ']';
    }
    
    public int getValueType() {
        final int typeCode = this.getTypeCode();
        switch (typeCode) {
            case 0:
            case 3: {
                return CellType.STRING.getCode();
            }
            case 1: {
                return CellType.BOOLEAN.getCode();
            }
            case 2: {
                return CellType.ERROR.getCode();
            }
            default: {
                throw new IllegalStateException("Unexpected type id (" + typeCode + ")");
            }
        }
    }
    
    public boolean getBooleanValue() {
        if (this.getTypeCode() != 1) {
            throw new IllegalStateException("Not a boolean cached value - " + this.formatValue());
        }
        return this.getDataValue() != 0;
    }
    
    public int getErrorValue() {
        if (this.getTypeCode() != 2) {
            throw new IllegalStateException("Not an error cached value - " + this.formatValue());
        }
        return this.getDataValue();
    }
}
