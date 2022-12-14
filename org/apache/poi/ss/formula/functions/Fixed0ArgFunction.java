package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public abstract class Fixed0ArgFunction implements Function0Arg
{
    @Override
    public final ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length != 0) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluate(srcRowIndex, srcColumnIndex);
    }
}
