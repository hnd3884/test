package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.CacheAreaEval;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class IfFunc extends Var2or3ArgFunction implements ArrayFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        boolean b;
        try {
            b = evaluateFirstArg(arg0, srcRowIndex, srcColumnIndex);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
        if (!b) {
            return BoolEval.FALSE;
        }
        if (arg1 == MissingArgEval.instance) {
            return BlankEval.instance;
        }
        return arg1;
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        boolean b;
        try {
            b = evaluateFirstArg(arg0, srcRowIndex, srcColumnIndex);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
        if (b) {
            if (arg1 == MissingArgEval.instance) {
                return BlankEval.instance;
            }
            return arg1;
        }
        else {
            if (arg2 == MissingArgEval.instance) {
                return BlankEval.instance;
            }
            return arg2;
        }
    }
    
    public static boolean evaluateFirstArg(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        final Boolean b = OperandResolver.coerceValueToBoolean(ve, false);
        return b != null && b;
    }
    
    @Override
    public ValueEval evaluateArray(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length < 2 || args.length > 3) {
            return ErrorEval.VALUE_INVALID;
        }
        final ValueEval arg0 = args[0];
        final ValueEval arg2 = args[1];
        final ValueEval arg3 = (args.length == 2) ? BoolEval.FALSE : args[2];
        return this.evaluateArrayArgs(arg0, arg2, arg3, srcRowIndex, srcColumnIndex);
    }
    
    ValueEval evaluateArrayArgs(final ValueEval arg0, final ValueEval arg1, final ValueEval arg2, final int srcRowIndex, final int srcColumnIndex) {
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
        int a3FirstCol = 0;
        int a3FirstRow = 0;
        if (arg2 instanceof AreaEval) {
            final AreaEval ae3 = (AreaEval)arg2;
            a3FirstCol = ae3.getFirstColumn();
            a3FirstRow = ae3.getFirstRow();
        }
        else if (arg2 instanceof RefEval) {
            final RefEval ref3 = (RefEval)arg2;
            a3FirstCol = ref3.getColumn();
            a3FirstRow = ref3.getRow();
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
                ValueEval vB;
                try {
                    vB = OperandResolver.getSingleValue(arg1, a2FirstRow + i, a2FirstCol + j);
                }
                catch (final FormulaParseException e3) {
                    vB = ErrorEval.NAME_INVALID;
                }
                catch (final EvaluationException e4) {
                    vB = e4.getErrorEval();
                }
                try {
                    final ValueEval vC = OperandResolver.getSingleValue(arg2, a3FirstRow + i, a3FirstCol + j);
                }
                catch (final FormulaParseException e5) {
                    final ValueEval vC = ErrorEval.NAME_INVALID;
                }
                catch (final EvaluationException e6) {
                    final ValueEval vC = e6.getErrorEval();
                }
                if (vA instanceof ErrorEval) {
                    vals[idx++] = vA;
                }
                else if (vB instanceof ErrorEval) {
                    vals[idx++] = vB;
                }
                else {
                    try {
                        final Boolean b = OperandResolver.coerceValueToBoolean(vA, false);
                        final ValueEval vC;
                        vals[idx++] = ((b != null && b) ? vB : vC);
                    }
                    catch (final EvaluationException e7) {
                        vals[idx++] = e7.getErrorEval();
                    }
                }
            }
        }
        if (vals.length == 1) {
            return vals[0];
        }
        return new CacheAreaEval(srcRowIndex, srcColumnIndex, srcRowIndex + height - 1, srcColumnIndex + width - 1, vals);
    }
}
