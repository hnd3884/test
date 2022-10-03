package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Lookup extends Var2or3ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        try {
            final ValueEval lookupValue = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            final TwoDEval lookupArray = LookupUtils.resolveTableArrayArg(arg1);
            LookupUtils.ValueVector lookupVector;
            LookupUtils.ValueVector resultVector;
            if (lookupArray.getWidth() > lookupArray.getHeight()) {
                lookupVector = createVector(lookupArray.getRow(0));
                resultVector = createVector(lookupArray.getRow(lookupArray.getHeight() - 1));
            }
            else {
                lookupVector = createVector(lookupArray.getColumn(0));
                resultVector = createVector(lookupArray.getColumn(lookupArray.getWidth() - 1));
            }
            assert lookupVector.getSize() == resultVector.getSize();
            final int index = LookupUtils.lookupIndexOfValue(lookupValue, lookupVector, true);
            return resultVector.getItem(index);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        try {
            final ValueEval lookupValue = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            final TwoDEval aeLookupVector = LookupUtils.resolveTableArrayArg(arg1);
            final TwoDEval aeResultVector = LookupUtils.resolveTableArrayArg(arg2);
            final LookupUtils.ValueVector lookupVector = createVector(aeLookupVector);
            final LookupUtils.ValueVector resultVector = createVector(aeResultVector);
            if (lookupVector.getSize() > resultVector.getSize()) {
                throw new RuntimeException("Lookup vector and result vector of differing sizes not supported yet");
            }
            final int index = LookupUtils.lookupIndexOfValue(lookupValue, lookupVector, true);
            return resultVector.getItem(index);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private static LookupUtils.ValueVector createVector(final TwoDEval ae) {
        final LookupUtils.ValueVector result = LookupUtils.createVector(ae);
        if (result != null) {
            return result;
        }
        throw new RuntimeException("non-vector lookup or result areas not supported yet");
    }
}
