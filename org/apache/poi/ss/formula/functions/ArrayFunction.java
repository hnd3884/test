package org.apache.poi.ss.formula.functions;

import java.util.function.Function;
import org.apache.poi.ss.formula.CacheAreaEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.AreaEval;
import java.util.function.BiFunction;
import org.apache.poi.ss.formula.eval.ValueEval;

public interface ArrayFunction
{
    ValueEval evaluateArray(final ValueEval[] p0, final int p1, final int p2);
    
    default ValueEval evaluateTwoArrayArgs(final ValueEval arg0, final ValueEval arg1, final int srcRowIndex, final int srcColumnIndex, final BiFunction<ValueEval, ValueEval, ValueEval> evalFunc) {
        int a1FirstCol = 0;
        int a1FirstRow = 0;
        int w1;
        int h1;
        if (arg0 instanceof AreaEval) {
            final AreaEval ae = (AreaEval)arg0;
            w1 = ae.getWidth();
            h1 = ae.getHeight();
            a1FirstCol = ae.getFirstColumn();
            a1FirstRow = ae.getFirstRow();
        }
        else if (arg0 instanceof RefEval) {
            final RefEval ref = (RefEval)arg0;
            w1 = 1;
            h1 = 1;
            a1FirstCol = ref.getColumn();
            a1FirstRow = ref.getRow();
        }
        else {
            w1 = 1;
            h1 = 1;
        }
        int a2FirstCol = 0;
        int a2FirstRow = 0;
        int w2;
        int h2;
        if (arg1 instanceof AreaEval) {
            final AreaEval ae2 = (AreaEval)arg1;
            w2 = ae2.getWidth();
            h2 = ae2.getHeight();
            a2FirstCol = ae2.getFirstColumn();
            a2FirstRow = ae2.getFirstRow();
        }
        else if (arg1 instanceof RefEval) {
            final RefEval ref2 = (RefEval)arg1;
            w2 = 1;
            h2 = 1;
            a2FirstCol = ref2.getColumn();
            a2FirstRow = ref2.getRow();
        }
        else {
            w2 = 1;
            h2 = 1;
        }
        final int width = Math.max(w1, w2);
        final int height = Math.max(h1, h2);
        final ValueEval[] vals = new ValueEval[height * width];
        int idx = 0;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                ValueEval vA;
                try {
                    vA = OperandResolver.getSingleValue(arg0, a1FirstRow + i, a1FirstCol + j);
                }
                catch (final FormulaParseException e) {
                    vA = ErrorEval.NAME_INVALID;
                }
                catch (final EvaluationException e2) {
                    vA = e2.getErrorEval();
                }
                catch (final RuntimeException e3) {
                    if (!e3.getMessage().startsWith("Don't know how to evaluate name")) {
                        throw e3;
                    }
                    vA = ErrorEval.NAME_INVALID;
                }
                ValueEval vB;
                try {
                    vB = OperandResolver.getSingleValue(arg1, a2FirstRow + i, a2FirstCol + j);
                }
                catch (final FormulaParseException e4) {
                    vB = ErrorEval.NAME_INVALID;
                }
                catch (final EvaluationException e5) {
                    vB = e5.getErrorEval();
                }
                catch (final RuntimeException e6) {
                    if (!e6.getMessage().startsWith("Don't know how to evaluate name")) {
                        throw e6;
                    }
                    vB = ErrorEval.NAME_INVALID;
                }
                if (vA instanceof ErrorEval) {
                    vals[idx++] = vA;
                }
                else if (vB instanceof ErrorEval) {
                    vals[idx++] = vB;
                }
                else {
                    vals[idx++] = evalFunc.apply(vA, vB);
                }
            }
        }
        if (vals.length == 1) {
            return vals[0];
        }
        return new CacheAreaEval(srcRowIndex, srcColumnIndex, srcRowIndex + height - 1, srcColumnIndex + width - 1, vals);
    }
    
    default ValueEval evaluateOneArrayArg(final ValueEval arg0, final int srcRowIndex, final int srcColumnIndex, final Function<ValueEval, ValueEval> evalFunc) {
        int a1FirstCol = 0;
        int a1FirstRow = 0;
        int w1;
        int h1;
        if (arg0 instanceof AreaEval) {
            final AreaEval ae = (AreaEval)arg0;
            w1 = ae.getWidth();
            h1 = ae.getHeight();
            a1FirstCol = ae.getFirstColumn();
            a1FirstRow = ae.getFirstRow();
        }
        else if (arg0 instanceof RefEval) {
            final RefEval ref = (RefEval)arg0;
            w1 = 1;
            h1 = 1;
            a1FirstCol = ref.getColumn();
            a1FirstRow = ref.getRow();
        }
        else {
            w1 = 1;
            h1 = 1;
        }
        final int w2 = 1;
        final int h2 = 1;
        final int width = Math.max(w1, w2);
        final int height = Math.max(h1, h2);
        final ValueEval[] vals = new ValueEval[height * width];
        int idx = 0;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                ValueEval vA;
                try {
                    vA = OperandResolver.getSingleValue(arg0, a1FirstRow + i, a1FirstCol + j);
                }
                catch (final FormulaParseException e) {
                    vA = ErrorEval.NAME_INVALID;
                }
                catch (final EvaluationException e2) {
                    vA = e2.getErrorEval();
                }
                catch (final RuntimeException e3) {
                    if (!e3.getMessage().startsWith("Don't know how to evaluate name")) {
                        throw e3;
                    }
                    vA = ErrorEval.NAME_INVALID;
                }
                vals[idx++] = evalFunc.apply(vA);
            }
        }
        if (vals.length == 1) {
            return vals[0];
        }
        return new CacheAreaEval(srcRowIndex, srcColumnIndex, srcRowIndex + height - 1, srcColumnIndex + width - 1, vals);
    }
}
