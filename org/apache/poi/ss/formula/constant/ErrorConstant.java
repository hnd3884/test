package org.apache.poi.ss.formula.constant;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.util.POILogger;

public class ErrorConstant
{
    private static final POILogger logger;
    private static final ErrorConstant NULL;
    private static final ErrorConstant DIV_0;
    private static final ErrorConstant VALUE;
    private static final ErrorConstant REF;
    private static final ErrorConstant NAME;
    private static final ErrorConstant NUM;
    private static final ErrorConstant NA;
    private final int _errorCode;
    
    private ErrorConstant(final int errorCode) {
        this._errorCode = errorCode;
    }
    
    public int getErrorCode() {
        return this._errorCode;
    }
    
    public String getText() {
        if (FormulaError.isValidCode(this._errorCode)) {
            return FormulaError.forInt(this._errorCode).getString();
        }
        return "unknown error code (" + this._errorCode + ")";
    }
    
    public static ErrorConstant valueOf(final int errorCode) {
        if (FormulaError.isValidCode(errorCode)) {
            switch (FormulaError.forInt(errorCode)) {
                case NULL: {
                    return ErrorConstant.NULL;
                }
                case DIV0: {
                    return ErrorConstant.DIV_0;
                }
                case VALUE: {
                    return ErrorConstant.VALUE;
                }
                case REF: {
                    return ErrorConstant.REF;
                }
                case NAME: {
                    return ErrorConstant.NAME;
                }
                case NUM: {
                    return ErrorConstant.NUM;
                }
                case NA: {
                    return ErrorConstant.NA;
                }
            }
        }
        ErrorConstant.logger.log(5, "Warning - unexpected error code (" + errorCode + ")");
        return new ErrorConstant(errorCode);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append(this.getText());
        sb.append("]");
        return sb.toString();
    }
    
    static {
        logger = POILogFactory.getLogger(ErrorConstant.class);
        NULL = new ErrorConstant(FormulaError.NULL.getCode());
        DIV_0 = new ErrorConstant(FormulaError.DIV0.getCode());
        VALUE = new ErrorConstant(FormulaError.VALUE.getCode());
        REF = new ErrorConstant(FormulaError.REF.getCode());
        NAME = new ErrorConstant(FormulaError.NAME.getCode());
        NUM = new ErrorConstant(FormulaError.NUM.getCode());
        NA = new ErrorConstant(FormulaError.NA.getCode());
    }
}
