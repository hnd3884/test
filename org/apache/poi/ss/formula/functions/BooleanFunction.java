package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public abstract class BooleanFunction implements Function, ArrayFunction
{
    public static final Function AND;
    public static final Function OR;
    public static final Function FALSE;
    public static final Function TRUE;
    public static final Function NOT;
    
    @Override
    public final ValueEval evaluate(final ValueEval[] args, final int srcRow, final int srcCol) {
        if (args.length < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        boolean boolResult;
        try {
            boolResult = this.calculate(args);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
        return BoolEval.valueOf(boolResult);
    }
    
    private boolean calculate(final ValueEval[] args) throws EvaluationException {
        boolean result = this.getInitialResultValue();
        boolean atLeastOneNonBlank = false;
        for (final ValueEval arg : args) {
            if (arg instanceof TwoDEval) {
                final TwoDEval ae = (TwoDEval)arg;
                final int height = ae.getHeight();
                final int width = ae.getWidth();
                for (int rrIx = 0; rrIx < height; ++rrIx) {
                    for (int rcIx = 0; rcIx < width; ++rcIx) {
                        final ValueEval ve = ae.getValue(rrIx, rcIx);
                        final Boolean tempVe = OperandResolver.coerceValueToBoolean(ve, true);
                        if (tempVe != null) {
                            result = this.partialEvaluate(result, tempVe);
                            atLeastOneNonBlank = true;
                        }
                    }
                }
            }
            else if (arg instanceof RefEval) {
                final RefEval re = (RefEval)arg;
                final int firstSheetIndex = re.getFirstSheetIndex();
                for (int lastSheetIndex = re.getLastSheetIndex(), sIx = firstSheetIndex; sIx <= lastSheetIndex; ++sIx) {
                    final ValueEval ve2 = re.getInnerValueEval(sIx);
                    final Boolean tempVe = OperandResolver.coerceValueToBoolean(ve2, true);
                    if (tempVe != null) {
                        result = this.partialEvaluate(result, tempVe);
                        atLeastOneNonBlank = true;
                    }
                }
            }
            else {
                Boolean tempVe;
                if (arg == MissingArgEval.instance) {
                    tempVe = false;
                }
                else {
                    tempVe = OperandResolver.coerceValueToBoolean(arg, false);
                }
                if (tempVe != null) {
                    result = this.partialEvaluate(result, tempVe);
                    atLeastOneNonBlank = true;
                }
            }
        }
        if (!atLeastOneNonBlank) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        return result;
    }
    
    protected abstract boolean getInitialResultValue();
    
    protected abstract boolean partialEvaluate(final boolean p0, final boolean p1);
    
    @Override
    public ValueEval evaluateArray(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length != 1) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluateOneArrayArg(args[0], srcRowIndex, srcColumnIndex, vA -> this.evaluate(new ValueEval[] { vA }, srcRowIndex, srcColumnIndex));
    }
    
    static {
        AND = new BooleanFunction() {
            @Override
            protected boolean getInitialResultValue() {
                return true;
            }
            
            @Override
            protected boolean partialEvaluate(final boolean cumulativeResult, final boolean currentValue) {
                return cumulativeResult && currentValue;
            }
        };
        OR = new BooleanFunction() {
            @Override
            protected boolean getInitialResultValue() {
                return false;
            }
            
            @Override
            protected boolean partialEvaluate(final boolean cumulativeResult, final boolean currentValue) {
                return cumulativeResult || currentValue;
            }
        };
        FALSE = new Fixed0ArgFunction() {
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex) {
                return BoolEval.FALSE;
            }
        };
        TRUE = new Fixed0ArgFunction() {
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex) {
                return BoolEval.TRUE;
            }
        };
        NOT = new Boolean1ArgFunction() {
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
                boolean boolArgVal;
                try {
                    final ValueEval ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
                    final Boolean b = OperandResolver.coerceValueToBoolean(ve, false);
                    boolArgVal = (b != null && b);
                }
                catch (final EvaluationException e) {
                    return e.getErrorEval();
                }
                return BoolEval.valueOf(!boolArgVal);
            }
        };
    }
    
    abstract static class Boolean1ArgFunction extends Fixed1ArgFunction implements ArrayFunction
    {
        @Override
        public ValueEval evaluateArray(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
            if (args.length != 1) {
                return ErrorEval.VALUE_INVALID;
            }
            return this.evaluateOneArrayArg(args[0], srcRowIndex, srcColumnIndex, vA -> this.evaluate(srcRowIndex, srcColumnIndex, vA));
        }
    }
}
