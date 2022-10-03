package org.apache.poi.ss.formula.functions;

import java.util.Iterator;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.RefListEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Rank extends Var2or3ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        try {
            final ValueEval ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            final double result = OperandResolver.coerceValueToDouble(ve);
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                throw new EvaluationException(ErrorEval.NUM_ERROR);
            }
            if (arg1 instanceof RefListEval) {
                return eval(result, (RefListEval)arg1, true);
            }
            final AreaEval aeRange = convertRangeArg(arg1);
            return eval(result, aeRange, true);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        try {
            ValueEval ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            final double result = OperandResolver.coerceValueToDouble(ve);
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                throw new EvaluationException(ErrorEval.NUM_ERROR);
            }
            ve = OperandResolver.getSingleValue(arg2, srcRowIndex, srcColumnIndex);
            final int order_value = OperandResolver.coerceValueToInt(ve);
            boolean order;
            if (order_value == 0) {
                order = true;
            }
            else {
                if (order_value != 1) {
                    throw new EvaluationException(ErrorEval.NUM_ERROR);
                }
                order = false;
            }
            if (arg1 instanceof RefListEval) {
                return eval(result, (RefListEval)arg1, order);
            }
            final AreaEval aeRange = convertRangeArg(arg1);
            return eval(result, aeRange, order);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private static ValueEval eval(final double arg0, final AreaEval aeRange, final boolean descending_order) {
        int rank = 1;
        final int height = aeRange.getHeight();
        final int width = aeRange.getWidth();
        for (int r = 0; r < height; ++r) {
            for (int c = 0; c < width; ++c) {
                final Double value = getValue(aeRange, r, c);
                if (value != null) {
                    if ((descending_order && value > arg0) || (!descending_order && value < arg0)) {
                        ++rank;
                    }
                }
            }
        }
        return new NumberEval(rank);
    }
    
    private static ValueEval eval(final double arg0, final RefListEval aeRange, final boolean descending_order) {
        int rank = 1;
        for (ValueEval ve : aeRange.getList()) {
            if (ve instanceof RefEval) {
                ve = ((RefEval)ve).getInnerValueEval(((RefEval)ve).getFirstSheetIndex());
            }
            if (ve instanceof NumberEval) {
                final double value = ((NumberEval)ve).getNumberValue();
                if ((descending_order || value <= arg0) && (descending_order || value >= arg0)) {
                    continue;
                }
                ++rank;
            }
        }
        return new NumberEval(rank);
    }
    
    private static Double getValue(final AreaEval aeRange, final int relRowIndex, final int relColIndex) {
        final ValueEval addend = aeRange.getRelativeValue(relRowIndex, relColIndex);
        if (addend instanceof NumberEval) {
            return ((NumberEval)addend).getNumberValue();
        }
        return null;
    }
    
    private static AreaEval convertRangeArg(final ValueEval eval) throws EvaluationException {
        if (eval instanceof AreaEval) {
            return (AreaEval)eval;
        }
        if (eval instanceof RefEval) {
            return ((RefEval)eval).offset(0, 0, 0, 0);
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }
}
