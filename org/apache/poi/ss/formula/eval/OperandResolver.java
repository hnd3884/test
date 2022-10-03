package org.apache.poi.ss.formula.eval;

import java.time.DateTimeException;
import org.apache.poi.ss.usermodel.DateUtil;
import java.util.regex.Pattern;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.formula.EvaluationCell;

public final class OperandResolver
{
    private static final String Digits = "(\\p{Digit}+)";
    private static final String Exp = "[eE][+-]?(\\p{Digit}+)";
    private static final String fpRegex = "[\\x00-\\x20]*[+-]?(((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.(\\p{Digit}+)([eE][+-]?(\\p{Digit}+))?))[\\x00-\\x20]*";
    
    private OperandResolver() {
    }
    
    public static ValueEval getSingleValue(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        ValueEval result;
        if (arg instanceof RefEval) {
            result = chooseSingleElementFromRef((RefEval)arg);
        }
        else if (arg instanceof AreaEval) {
            result = chooseSingleElementFromArea((AreaEval)arg, srcCellRow, srcCellCol);
        }
        else {
            result = arg;
        }
        if (result instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval)result);
        }
        return result;
    }
    
    public static ValueEval getElementFromArray(final AreaEval ae, final EvaluationCell cell) {
        final CellRangeAddress range = cell.getArrayFormulaRange();
        final int relativeRowIndex = cell.getRowIndex() - range.getFirstRow();
        final int relativeColIndex = cell.getColumnIndex() - range.getFirstColumn();
        if (ae.isColumn()) {
            if (ae.isRow()) {
                return ae.getRelativeValue(0, 0);
            }
            if (relativeRowIndex < ae.getHeight()) {
                return ae.getRelativeValue(relativeRowIndex, 0);
            }
        }
        else {
            if (!ae.isRow() && relativeRowIndex < ae.getHeight() && relativeColIndex < ae.getWidth()) {
                return ae.getRelativeValue(relativeRowIndex, relativeColIndex);
            }
            if (ae.isRow() && relativeColIndex < ae.getWidth()) {
                return ae.getRelativeValue(0, relativeColIndex);
            }
        }
        return ErrorEval.NA;
    }
    
    public static ValueEval chooseSingleElementFromArea(final AreaEval ae, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval result = chooseSingleElementFromAreaInternal(ae, srcCellRow, srcCellCol);
        if (result instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval)result);
        }
        return result;
    }
    
    private static ValueEval chooseSingleElementFromAreaInternal(final AreaEval ae, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        if (ae.isColumn()) {
            if (ae.isRow()) {
                return ae.getRelativeValue(0, 0);
            }
            if (!ae.containsRow(srcCellRow)) {
                throw EvaluationException.invalidValue();
            }
            return ae.getAbsoluteValue(srcCellRow, ae.getFirstColumn());
        }
        else if (!ae.isRow()) {
            if (ae.containsRow(srcCellRow) && ae.containsColumn(srcCellCol)) {
                return ae.getAbsoluteValue(srcCellRow, srcCellCol);
            }
            throw EvaluationException.invalidValue();
        }
        else {
            if (!ae.containsColumn(srcCellCol)) {
                throw EvaluationException.invalidValue();
            }
            return ae.getAbsoluteValue(ae.getFirstRow(), srcCellCol);
        }
    }
    
    private static ValueEval chooseSingleElementFromRef(final RefEval ref) {
        return ref.getInnerValueEval(ref.getFirstSheetIndex());
    }
    
    public static int coerceValueToInt(final ValueEval ev) throws EvaluationException {
        if (ev == BlankEval.instance) {
            return 0;
        }
        final double d = coerceValueToDouble(ev);
        return (int)Math.floor(d);
    }
    
    public static double coerceValueToDouble(final ValueEval ev) throws EvaluationException {
        if (ev == BlankEval.instance) {
            return 0.0;
        }
        if (ev instanceof NumericValueEval) {
            return ((NumericValueEval)ev).getNumberValue();
        }
        if (!(ev instanceof StringEval)) {
            throw new RuntimeException("Unexpected arg eval type (" + ev.getClass().getName() + ")");
        }
        final String sval = ((StringEval)ev).getStringValue();
        Double dd = parseDouble(sval);
        if (dd == null) {
            dd = parseDateTime(sval);
        }
        if (dd == null) {
            throw EvaluationException.invalidValue();
        }
        return dd;
    }
    
    public static Double parseDouble(final String pText) {
        if (Pattern.matches("[\\x00-\\x20]*[+-]?(((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.(\\p{Digit}+)([eE][+-]?(\\p{Digit}+))?))[\\x00-\\x20]*", pText)) {
            try {
                return Double.parseDouble(pText);
            }
            catch (final NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    public static Double parseDateTime(final String pText) {
        try {
            return DateUtil.parseDateTime(pText);
        }
        catch (final DateTimeException e) {
            return null;
        }
    }
    
    public static String coerceValueToString(final ValueEval ve) {
        if (ve instanceof StringValueEval) {
            final StringValueEval sve = (StringValueEval)ve;
            return sve.getStringValue();
        }
        if (ve == BlankEval.instance) {
            return "";
        }
        throw new IllegalArgumentException("Unexpected eval class (" + ve.getClass().getName() + ")");
    }
    
    public static Boolean coerceValueToBoolean(final ValueEval ve, final boolean stringsAreBlanks) throws EvaluationException {
        if (ve == null || ve == BlankEval.instance) {
            return null;
        }
        if (ve instanceof BoolEval) {
            return ((BoolEval)ve).getBooleanValue();
        }
        if (ve instanceof StringEval) {
            if (stringsAreBlanks) {
                return null;
            }
            final String str = ((StringEval)ve).getStringValue();
            if (str.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            }
            if (str.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            }
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        else if (ve instanceof NumericValueEval) {
            final NumericValueEval ne = (NumericValueEval)ve;
            final double d = ne.getNumberValue();
            if (Double.isNaN(d)) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            return d != 0.0;
        }
        else {
            if (ve instanceof ErrorEval) {
                throw new EvaluationException((ErrorEval)ve);
            }
            throw new RuntimeException("Unexpected eval (" + ve.getClass().getName() + ")");
        }
    }
}
