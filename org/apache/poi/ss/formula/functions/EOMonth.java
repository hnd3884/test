package org.apache.poi.ss.formula.functions;

import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;

public class EOMonth implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            double startDateAsNumber = NumericFunction.singleOperandEvaluate(args[0], ec.getRowIndex(), ec.getColumnIndex());
            final int months = (int)NumericFunction.singleOperandEvaluate(args[1], ec.getRowIndex(), ec.getColumnIndex());
            if (startDateAsNumber >= 0.0 && startDateAsNumber < 1.0) {
                startDateAsNumber = 1.0;
            }
            final Date startDate = DateUtil.getJavaDate(startDateAsNumber, false);
            final Calendar cal = LocaleUtil.getLocaleCalendar();
            cal.setTime(startDate);
            cal.clear(10);
            cal.set(11, 0);
            cal.clear(12);
            cal.clear(13);
            cal.clear(14);
            cal.add(2, months + 1);
            cal.set(5, 1);
            cal.add(5, -1);
            return new NumberEval(DateUtil.getExcelDate(cal.getTime()));
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    static {
        instance = new EOMonth();
    }
}
