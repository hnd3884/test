package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

final class WorkdayFunction implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    private ArgumentsEvaluator evaluator;
    
    private WorkdayFunction(final ArgumentsEvaluator anEvaluator) {
        this.evaluator = anEvaluator;
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length < 2 || args.length > 3) {
            return ErrorEval.VALUE_INVALID;
        }
        final int srcCellRow = ec.getRowIndex();
        final int srcCellCol = ec.getColumnIndex();
        try {
            final double start = this.evaluator.evaluateDateArg(args[0], srcCellRow, srcCellCol);
            final int days = (int)Math.floor(this.evaluator.evaluateNumberArg(args[1], srcCellRow, srcCellCol));
            final ValueEval holidaysCell = (args.length == 3) ? args[2] : null;
            final double[] holidays = this.evaluator.evaluateDatesArg(holidaysCell, srcCellRow, srcCellCol);
            return new NumberEval(DateUtil.getExcelDate(WorkdayCalculator.instance.calculateWorkdays(start, days, holidays)));
        }
        catch (final EvaluationException e) {
            return ErrorEval.VALUE_INVALID;
        }
    }
    
    static {
        instance = new WorkdayFunction(ArgumentsEvaluator.instance);
    }
}
