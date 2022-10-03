package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Errortype extends Fixed1ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
        try {
            OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            return ErrorEval.NA;
        }
        catch (final EvaluationException e) {
            final int result = this.translateErrorCodeToErrorTypeValue(e.getErrorEval().getErrorCode());
            return new NumberEval(result);
        }
    }
    
    private int translateErrorCodeToErrorTypeValue(final int errorCode) {
        switch (FormulaError.forInt(errorCode)) {
            case NULL: {
                return 1;
            }
            case DIV0: {
                return 2;
            }
            case VALUE: {
                return 3;
            }
            case REF: {
                return 4;
            }
            case NAME: {
                return 5;
            }
            case NUM: {
                return 6;
            }
            case NA: {
                return 7;
            }
            default: {
                throw new IllegalArgumentException("Invalid error code (" + errorCode + ")");
            }
        }
    }
}
