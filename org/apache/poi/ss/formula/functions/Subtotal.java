package org.apache.poi.ss.formula.functions;

import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.formula.LazyRefEval;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NotImplementedFunctionException;

public class Subtotal implements Function
{
    private static Function findFunction(final int functionCode) throws EvaluationException {
        switch (functionCode) {
            case 1: {
                return AggregateFunction.subtotalInstance(AggregateFunction.AVERAGE, true);
            }
            case 2: {
                return Count.subtotalInstance(true);
            }
            case 3: {
                return Counta.subtotalInstance(true);
            }
            case 4: {
                return AggregateFunction.subtotalInstance(AggregateFunction.MAX, true);
            }
            case 5: {
                return AggregateFunction.subtotalInstance(AggregateFunction.MIN, true);
            }
            case 6: {
                return AggregateFunction.subtotalInstance(AggregateFunction.PRODUCT, true);
            }
            case 7: {
                return AggregateFunction.subtotalInstance(AggregateFunction.STDEV, true);
            }
            case 8: {
                throw new NotImplementedFunctionException("STDEVP");
            }
            case 9: {
                return AggregateFunction.subtotalInstance(AggregateFunction.SUM, true);
            }
            case 10: {
                throw new NotImplementedFunctionException("VAR");
            }
            case 11: {
                throw new NotImplementedFunctionException("VARP");
            }
            case 101: {
                return AggregateFunction.subtotalInstance(AggregateFunction.AVERAGE, false);
            }
            case 102: {
                return Count.subtotalInstance(false);
            }
            case 103: {
                return Counta.subtotalInstance(false);
            }
            case 104: {
                return AggregateFunction.subtotalInstance(AggregateFunction.MAX, false);
            }
            case 105: {
                return AggregateFunction.subtotalInstance(AggregateFunction.MIN, false);
            }
            case 106: {
                return AggregateFunction.subtotalInstance(AggregateFunction.PRODUCT, false);
            }
            case 107: {
                return AggregateFunction.subtotalInstance(AggregateFunction.STDEV, false);
            }
            case 108: {
                throw new NotImplementedFunctionException("STDEVP SUBTOTAL with 'exclude hidden values' option");
            }
            case 109: {
                return AggregateFunction.subtotalInstance(AggregateFunction.SUM, false);
            }
            case 110: {
                throw new NotImplementedFunctionException("VAR SUBTOTAL with 'exclude hidden values' option");
            }
            case 111: {
                throw new NotImplementedFunctionException("VARP SUBTOTAL with 'exclude hidden values' option");
            }
            default: {
                throw EvaluationException.invalidValue();
            }
        }
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        final int nInnerArgs = args.length - 1;
        if (nInnerArgs < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        int functionCode = 0;
        Function innerFunc;
        try {
            final ValueEval ve = OperandResolver.getSingleValue(args[0], srcRowIndex, srcColumnIndex);
            functionCode = OperandResolver.coerceValueToInt(ve);
            innerFunc = findFunction(functionCode);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
        final List<ValueEval> list = new ArrayList<ValueEval>(Arrays.asList(args).subList(1, args.length));
        final Iterator<ValueEval> it = list.iterator();
        while (it.hasNext()) {
            final ValueEval eval = it.next();
            if (eval instanceof LazyRefEval) {
                final LazyRefEval lazyRefEval = (LazyRefEval)eval;
                if (lazyRefEval.isSubTotal()) {
                    it.remove();
                }
                if (functionCode <= 100 || !lazyRefEval.isRowHidden()) {
                    continue;
                }
                it.remove();
            }
        }
        return innerFunc.evaluate(list.toArray(new ValueEval[0]), srcRowIndex, srcColumnIndex);
    }
}
