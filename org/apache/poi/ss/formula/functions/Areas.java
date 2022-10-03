package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.ptg.NumberPtg;
import org.apache.poi.ss.formula.eval.RefListEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Areas implements Function
{
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length == 0) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            final ValueEval valueEval = args[0];
            int result = 1;
            if (valueEval instanceof RefListEval) {
                final RefListEval refListEval = (RefListEval)valueEval;
                result = refListEval.getList().size();
            }
            final NumberEval numberEval = new NumberEval(new NumberPtg(result));
            return numberEval;
        }
        catch (final Exception e) {
            return ErrorEval.VALUE_INVALID;
        }
    }
}
