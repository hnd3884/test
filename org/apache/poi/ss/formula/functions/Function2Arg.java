package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ValueEval;

public interface Function2Arg extends Function
{
    ValueEval evaluate(final int p0, final int p1, final ValueEval p2, final ValueEval p3);
}
