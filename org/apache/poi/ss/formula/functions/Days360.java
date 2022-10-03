package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.util.LocaleUtil;
import java.util.Calendar;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Days360 extends Var2or3ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        try {
            final double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            final double d2 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            return new NumberEval(evaluate(d0, d2, false));
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        try {
            final double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            final double d2 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            final ValueEval ve = OperandResolver.getSingleValue(arg2, srcRowIndex, srcColumnIndex);
            final Boolean method = OperandResolver.coerceValueToBoolean(ve, false);
            return new NumberEval(evaluate(d0, d2, method != null && method));
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private static double evaluate(final double d0, final double d1, final boolean method) {
        final Calendar realStart = getDate(d0);
        final Calendar realEnd = getDate(d1);
        final int[] startingDate = getStartingDate(realStart, method);
        final int[] endingDate = getEndingDate(realEnd, startingDate, method);
        return endingDate[0] * 360.0 + endingDate[1] * 30.0 + endingDate[2] - (startingDate[0] * 360.0 + startingDate[1] * 30.0 + startingDate[2]);
    }
    
    private static Calendar getDate(final double date) {
        final Calendar processedDate = LocaleUtil.getLocaleCalendar();
        processedDate.setTime(DateUtil.getJavaDate(date, false));
        return processedDate;
    }
    
    private static int[] getStartingDate(final Calendar realStart, final boolean method) {
        final int yyyy = realStart.get(1);
        final int mm = realStart.get(2);
        int dd = Math.min(30, realStart.get(5));
        if (!method && isLastDayOfMonth(realStart)) {
            dd = 30;
        }
        return new int[] { yyyy, mm, dd };
    }
    
    private static int[] getEndingDate(final Calendar realEnd, final int[] startingDate, final boolean method) {
        int yyyy = realEnd.get(1);
        int mm = realEnd.get(2);
        int dd = Math.min(30, realEnd.get(5));
        if (!method && realEnd.get(5) == 31) {
            if (startingDate[2] < 30) {
                realEnd.set(5, 1);
                realEnd.add(2, 1);
                yyyy = realEnd.get(1);
                mm = realEnd.get(2);
                dd = 1;
            }
            else {
                dd = 30;
            }
        }
        return new int[] { yyyy, mm, dd };
    }
    
    private static boolean isLastDayOfMonth(final Calendar date) {
        final int dayOfMonth = date.get(5);
        final int lastDayOfMonth = date.getActualMaximum(5);
        return dayOfMonth == lastDayOfMonth;
    }
}
