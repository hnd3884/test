package org.apache.poi.ss.formula.functions;

import java.util.function.Supplier;
import org.apache.poi.ss.util.NumberComparer;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.formula.eval.StringValueEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class DStarRunner implements Function3Arg
{
    private final DStarAlgorithmEnum algoType;
    
    public DStarRunner(final DStarAlgorithmEnum algorithm) {
        this.algoType = algorithm;
    }
    
    @Override
    public final ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length == 3) {
            return this.evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2]);
        }
        return ErrorEval.VALUE_INVALID;
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval database, ValueEval filterColumn, final ValueEval conditionDatabase) {
        if (!(database instanceof AreaEval) || !(conditionDatabase instanceof AreaEval)) {
            return ErrorEval.VALUE_INVALID;
        }
        final AreaEval db = (AreaEval)database;
        final AreaEval cdb = (AreaEval)conditionDatabase;
        try {
            filterColumn = OperandResolver.getSingleValue(filterColumn, srcRowIndex, srcColumnIndex);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
        int fc;
        try {
            fc = getColumnForName(filterColumn, db);
        }
        catch (final EvaluationException e2) {
            return ErrorEval.VALUE_INVALID;
        }
        if (fc == -1) {
            return ErrorEval.VALUE_INVALID;
        }
        final IDStarAlgorithm algorithm = this.algoType.newInstance();
        for (int height = db.getHeight(), row = 1; row < height; ++row) {
            boolean matches;
            try {
                matches = fullfillsConditions(db, row, cdb);
            }
            catch (final EvaluationException e3) {
                return ErrorEval.VALUE_INVALID;
            }
            if (matches) {
                final ValueEval currentValueEval = resolveReference(db, row, fc);
                final boolean shouldContinue = algorithm.processMatch(currentValueEval);
                if (!shouldContinue) {
                    break;
                }
            }
        }
        return algorithm.getResult();
    }
    
    private static int getColumnForName(final ValueEval nameValueEval, final AreaEval db) throws EvaluationException {
        if (!(nameValueEval instanceof NumericValueEval)) {
            final String name = OperandResolver.coerceValueToString(nameValueEval);
            return getColumnForString(db, name);
        }
        final int columnNo = OperandResolver.coerceValueToInt(nameValueEval) - 1;
        if (columnNo < 0 || columnNo >= db.getWidth()) {
            return -1;
        }
        return columnNo;
    }
    
    private static int getColumnForString(final AreaEval db, final String name) {
        int resultColumn = -1;
        for (int width = db.getWidth(), column = 0; column < width; ++column) {
            final ValueEval columnNameValueEval = resolveReference(db, 0, column);
            if (!(columnNameValueEval instanceof BlankEval)) {
                if (!(columnNameValueEval instanceof ErrorEval)) {
                    final String columnName = OperandResolver.coerceValueToString(columnNameValueEval);
                    if (name.equalsIgnoreCase(columnName)) {
                        resultColumn = column;
                        break;
                    }
                }
            }
        }
        return resultColumn;
    }
    
    private static boolean fullfillsConditions(final AreaEval db, final int row, final AreaEval cdb) throws EvaluationException {
        for (int height = cdb.getHeight(), conditionRow = 1; conditionRow < height; ++conditionRow) {
            boolean matches = true;
            for (int width = cdb.getWidth(), column = 0; column < width; ++column) {
                boolean columnCondition = true;
                final ValueEval condition = resolveReference(cdb, conditionRow, column);
                if (!(condition instanceof BlankEval)) {
                    final ValueEval targetHeader = resolveReference(cdb, 0, column);
                    if (!(targetHeader instanceof StringValueEval)) {
                        throw new EvaluationException(ErrorEval.VALUE_INVALID);
                    }
                    if (getColumnForName(targetHeader, db) == -1) {
                        columnCondition = false;
                    }
                    if (columnCondition) {
                        final ValueEval value = resolveReference(db, row, getColumnForName(targetHeader, db));
                        if (!testNormalCondition(value, condition)) {
                            matches = false;
                            break;
                        }
                    }
                    else {
                        if (OperandResolver.coerceValueToString(condition).isEmpty()) {
                            throw new EvaluationException(ErrorEval.VALUE_INVALID);
                        }
                        throw new NotImplementedException("D* function with formula conditions");
                    }
                }
            }
            if (matches) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean testNormalCondition(final ValueEval value, final ValueEval condition) throws EvaluationException {
        if (condition instanceof StringEval) {
            final String conditionString = ((StringEval)condition).getStringValue();
            if (conditionString.startsWith("<")) {
                String number = conditionString.substring(1);
                if (number.startsWith("=")) {
                    number = number.substring(1);
                    return testNumericCondition(value, operator.smallerEqualThan, number);
                }
                return testNumericCondition(value, operator.smallerThan, number);
            }
            else if (conditionString.startsWith(">")) {
                String number = conditionString.substring(1);
                if (number.startsWith("=")) {
                    number = number.substring(1);
                    return testNumericCondition(value, operator.largerEqualThan, number);
                }
                return testNumericCondition(value, operator.largerThan, number);
            }
            else if (conditionString.startsWith("=")) {
                final String stringOrNumber = conditionString.substring(1);
                if (stringOrNumber.isEmpty()) {
                    return value instanceof BlankEval;
                }
                boolean itsANumber;
                try {
                    Integer.parseInt(stringOrNumber);
                    itsANumber = true;
                }
                catch (final NumberFormatException e) {
                    try {
                        Double.parseDouble(stringOrNumber);
                        itsANumber = true;
                    }
                    catch (final NumberFormatException e2) {
                        itsANumber = false;
                    }
                }
                if (itsANumber) {
                    return testNumericCondition(value, operator.equal, stringOrNumber);
                }
                final String valueString = (value instanceof BlankEval) ? "" : OperandResolver.coerceValueToString(value);
                return stringOrNumber.equals(valueString);
            }
            else {
                if (conditionString.isEmpty()) {
                    return value instanceof StringEval;
                }
                final String valueString2 = (value instanceof BlankEval) ? "" : OperandResolver.coerceValueToString(value);
                return valueString2.startsWith(conditionString);
            }
        }
        else {
            if (condition instanceof NumericValueEval) {
                final double conditionNumber = ((NumericValueEval)condition).getNumberValue();
                final Double valueNumber = getNumberFromValueEval(value);
                return valueNumber != null && conditionNumber == valueNumber;
            }
            return condition instanceof ErrorEval && value instanceof ErrorEval && ((ErrorEval)condition).getErrorCode() == ((ErrorEval)value).getErrorCode();
        }
    }
    
    private static boolean testNumericCondition(final ValueEval valueEval, final operator op, final String condition) throws EvaluationException {
        if (!(valueEval instanceof NumericValueEval)) {
            return false;
        }
        final double value = ((NumericValueEval)valueEval).getNumberValue();
        double conditionValue;
        try {
            conditionValue = Integer.parseInt(condition);
        }
        catch (final NumberFormatException e) {
            try {
                conditionValue = Double.parseDouble(condition);
            }
            catch (final NumberFormatException e2) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
        }
        final int result = NumberComparer.compare(value, conditionValue);
        switch (op) {
            case largerThan: {
                return result > 0;
            }
            case largerEqualThan: {
                return result >= 0;
            }
            case smallerThan: {
                return result < 0;
            }
            case smallerEqualThan: {
                return result <= 0;
            }
            case equal: {
                return result == 0;
            }
            default: {
                return false;
            }
        }
    }
    
    private static Double getNumberFromValueEval(final ValueEval value) {
        if (value instanceof NumericValueEval) {
            return ((NumericValueEval)value).getNumberValue();
        }
        if (value instanceof StringValueEval) {
            final String stringValue = ((StringValueEval)value).getStringValue();
            try {
                return Double.parseDouble(stringValue);
            }
            catch (final NumberFormatException e2) {
                return null;
            }
        }
        return null;
    }
    
    private static ValueEval resolveReference(final AreaEval db, final int dbRow, final int dbCol) {
        try {
            return OperandResolver.getSingleValue(db.getValue(dbRow, dbCol), db.getFirstRow() + dbRow, db.getFirstColumn() + dbCol);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    public enum DStarAlgorithmEnum
    {
        DGET((Supplier<IDStarAlgorithm>)DGet::new), 
        DMIN((Supplier<IDStarAlgorithm>)DMin::new), 
        DMAX((Supplier<IDStarAlgorithm>)DMax::new), 
        DSUM((Supplier<IDStarAlgorithm>)DSum::new);
        
        private final Supplier<IDStarAlgorithm> implSupplier;
        
        private DStarAlgorithmEnum(final Supplier<IDStarAlgorithm> implSupplier) {
            this.implSupplier = implSupplier;
        }
        
        public IDStarAlgorithm newInstance() {
            return this.implSupplier.get();
        }
    }
    
    private enum operator
    {
        largerThan, 
        largerEqualThan, 
        smallerThan, 
        smallerEqualThan, 
        equal;
    }
}
