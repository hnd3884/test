package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;

abstract class Baseifs implements FreeRefFunction
{
    protected abstract boolean hasInitialRange();
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        final int firstCriteria;
        final boolean hasInitialRange = (firstCriteria = (this.hasInitialRange() ? 1 : 0)) != 0;
        if (args.length < 2 + firstCriteria || args.length % 2 != firstCriteria) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            AreaEval sumRange = null;
            if (hasInitialRange) {
                sumRange = convertRangeArg(args[0]);
            }
            final AreaEval[] ae = new AreaEval[(args.length - firstCriteria) / 2];
            final CountUtils.I_MatchPredicate[] mp = new CountUtils.I_MatchPredicate[ae.length];
            for (int i = firstCriteria, k = 0; i < args.length; i += 2, ++k) {
                ae[k] = convertRangeArg(args[i]);
                mp[k] = Countif.createCriteriaPredicate(args[i + 1], ec.getRowIndex(), ec.getColumnIndex());
            }
            validateCriteriaRanges(sumRange, ae);
            validateCriteria(mp);
            final double result = aggregateMatchingCells(sumRange, ae, mp);
            return new NumberEval(result);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private static void validateCriteriaRanges(final AreaEval sumRange, final AreaEval[] criteriaRanges) throws EvaluationException {
        final int h = criteriaRanges[0].getHeight();
        final int w = criteriaRanges[0].getWidth();
        if (sumRange != null && (sumRange.getHeight() != h || sumRange.getWidth() != w)) {
            throw EvaluationException.invalidValue();
        }
        for (final AreaEval r : criteriaRanges) {
            if (r.getHeight() != h || r.getWidth() != w) {
                throw EvaluationException.invalidValue();
            }
        }
    }
    
    private static void validateCriteria(final CountUtils.I_MatchPredicate[] criteria) throws EvaluationException {
        for (final CountUtils.I_MatchPredicate predicate : criteria) {
            if (predicate instanceof Countif.ErrorMatcher) {
                throw new EvaluationException(ErrorEval.valueOf(((Countif.ErrorMatcher)predicate).getValue()));
            }
        }
    }
    
    private static double aggregateMatchingCells(final AreaEval sumRange, final AreaEval[] ranges, final CountUtils.I_MatchPredicate[] predicates) {
        final int height = ranges[0].getHeight();
        final int width = ranges[0].getWidth();
        double result = 0.0;
        for (int r = 0; r < height; ++r) {
            for (int c = 0; c < width; ++c) {
                boolean matches = true;
                for (int i = 0; i < ranges.length; ++i) {
                    final AreaEval aeRange = ranges[i];
                    final CountUtils.I_MatchPredicate mp = predicates[i];
                    if (mp == null || !mp.matches(aeRange.getRelativeValue(r, c))) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    result += accumulate(sumRange, r, c);
                }
            }
        }
        return result;
    }
    
    private static double accumulate(final AreaEval sumRange, final int relRowIndex, final int relColIndex) {
        if (sumRange == null) {
            return 1.0;
        }
        final ValueEval addend = sumRange.getRelativeValue(relRowIndex, relColIndex);
        if (addend instanceof NumberEval) {
            return ((NumberEval)addend).getNumberValue();
        }
        return 0.0;
    }
    
    protected static AreaEval convertRangeArg(final ValueEval eval) throws EvaluationException {
        if (eval instanceof AreaEval) {
            return (AreaEval)eval;
        }
        if (eval instanceof RefEval) {
            return ((RefEval)eval).offset(0, 0, 0, 0);
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }
}
