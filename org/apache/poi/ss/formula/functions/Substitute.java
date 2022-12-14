package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Substitute extends Var3or4ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        String result;
        try {
            final String oldStr = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
            final String searchStr = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
            final String newStr = TextFunction.evaluateStringArg(arg2, srcRowIndex, srcColumnIndex);
            result = replaceAllOccurrences(oldStr, searchStr, newStr);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
        return new StringEval(result);
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2, final ValueEval arg3) {
        String result;
        try {
            final String oldStr = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
            final String searchStr = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
            final String newStr = TextFunction.evaluateStringArg(arg2, srcRowIndex, srcColumnIndex);
            final int instanceNumber = TextFunction.evaluateIntArg(arg3, srcRowIndex, srcColumnIndex);
            if (instanceNumber < 1) {
                return ErrorEval.VALUE_INVALID;
            }
            result = replaceOneOccurrence(oldStr, searchStr, newStr, instanceNumber);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
        return new StringEval(result);
    }
    
    private static String replaceAllOccurrences(final String oldStr, final String searchStr, final String newStr) {
        if (searchStr.length() < 1) {
            return oldStr;
        }
        final StringBuilder sb = new StringBuilder();
        int startIndex = 0;
        while (true) {
            final int nextMatch = oldStr.indexOf(searchStr, startIndex);
            if (nextMatch < 0) {
                break;
            }
            sb.append(oldStr, startIndex, nextMatch);
            sb.append(newStr);
            startIndex = nextMatch + searchStr.length();
        }
        sb.append(oldStr.substring(startIndex));
        return sb.toString();
    }
    
    private static String replaceOneOccurrence(final String oldStr, final String searchStr, final String newStr, final int instanceNumber) {
        if (searchStr.length() < 1) {
            return oldStr;
        }
        int startIndex = 0;
        int count = 0;
        while (true) {
            final int nextMatch = oldStr.indexOf(searchStr, startIndex);
            if (nextMatch < 0) {
                return oldStr;
            }
            if (++count == instanceNumber) {
                return oldStr.substring(0, nextMatch) + newStr + oldStr.substring(nextMatch + searchStr.length());
            }
            startIndex = nextMatch + searchStr.length();
        }
    }
}
