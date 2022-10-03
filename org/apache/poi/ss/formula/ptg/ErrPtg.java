package org.apache.poi.ss.formula.ptg;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.usermodel.FormulaError;

public final class ErrPtg extends ScalarConstantPtg
{
    public static final ErrPtg NULL_INTERSECTION;
    public static final ErrPtg DIV_ZERO;
    public static final ErrPtg VALUE_INVALID;
    public static final ErrPtg REF_INVALID;
    public static final ErrPtg NAME_INVALID;
    public static final ErrPtg NUM_ERROR;
    public static final ErrPtg N_A;
    public static final short sid = 28;
    private static final int SIZE = 2;
    private final int field_1_error_code;
    
    private ErrPtg(final int errorCode) {
        if (!FormulaError.isValidCode(errorCode)) {
            throw new IllegalArgumentException("Invalid error code (" + errorCode + ")");
        }
        this.field_1_error_code = errorCode;
    }
    
    public static ErrPtg read(final LittleEndianInput in) {
        return valueOf(in.readByte());
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(28 + this.getPtgClass());
        out.writeByte(this.field_1_error_code);
    }
    
    @Override
    public String toFormulaString() {
        return FormulaError.forInt(this.field_1_error_code).getString();
    }
    
    @Override
    public int getSize() {
        return 2;
    }
    
    public int getErrorCode() {
        return this.field_1_error_code;
    }
    
    public static ErrPtg valueOf(final int code) {
        switch (FormulaError.forInt(code)) {
            case DIV0: {
                return ErrPtg.DIV_ZERO;
            }
            case NA: {
                return ErrPtg.N_A;
            }
            case NAME: {
                return ErrPtg.NAME_INVALID;
            }
            case NULL: {
                return ErrPtg.NULL_INTERSECTION;
            }
            case NUM: {
                return ErrPtg.NUM_ERROR;
            }
            case REF: {
                return ErrPtg.REF_INVALID;
            }
            case VALUE: {
                return ErrPtg.VALUE_INVALID;
            }
            default: {
                throw new RuntimeException("Unexpected error code (" + code + ")");
            }
        }
    }
    
    @Override
    public ErrPtg copy() {
        return this;
    }
    
    static {
        NULL_INTERSECTION = new ErrPtg(FormulaError.NULL.getCode());
        DIV_ZERO = new ErrPtg(FormulaError.DIV0.getCode());
        VALUE_INVALID = new ErrPtg(FormulaError.VALUE.getCode());
        REF_INVALID = new ErrPtg(FormulaError.REF.getCode());
        NAME_INVALID = new ErrPtg(FormulaError.NAME.getCode());
        NUM_ERROR = new ErrPtg(FormulaError.NUM.getCode());
        N_A = new ErrPtg(FormulaError.NA.getCode());
    }
}
