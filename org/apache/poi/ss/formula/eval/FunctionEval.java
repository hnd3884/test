package org.apache.poi.ss.formula.eval;

import java.util.Collections;
import java.util.TreeSet;
import java.util.Collection;
import org.apache.poi.ss.formula.atp.AnalysisToolPak;
import org.apache.poi.ss.formula.function.FunctionMetadata;
import org.apache.poi.ss.formula.functions.NotImplementedFunction;
import org.apache.poi.ss.formula.function.FunctionMetadataRegistry;
import org.apache.poi.ss.formula.functions.MinaMaxa;
import org.apache.poi.ss.formula.functions.Hyperlink;
import org.apache.poi.ss.formula.functions.Roman;
import org.apache.poi.ss.formula.functions.Countblank;
import org.apache.poi.ss.formula.functions.Countif;
import org.apache.poi.ss.formula.functions.Sumif;
import org.apache.poi.ss.formula.functions.Subtotal;
import org.apache.poi.ss.formula.functions.Mode;
import org.apache.poi.ss.formula.functions.Slope;
import org.apache.poi.ss.formula.functions.Intercept;
import org.apache.poi.ss.formula.functions.Sumx2py2;
import org.apache.poi.ss.formula.functions.Sumx2my2;
import org.apache.poi.ss.formula.functions.Sumxmy2;
import org.apache.poi.ss.formula.functions.Odd;
import org.apache.poi.ss.formula.functions.Even;
import org.apache.poi.ss.formula.functions.Errortype;
import org.apache.poi.ss.formula.functions.Frequency;
import org.apache.poi.ss.formula.functions.Sumproduct;
import org.apache.poi.ss.formula.functions.Today;
import org.apache.poi.ss.formula.functions.Days360;
import org.apache.poi.ss.formula.functions.Address;
import org.apache.poi.ss.formula.functions.Rank;
import org.apache.poi.ss.formula.functions.Counta;
import org.apache.poi.ss.formula.functions.PPMT;
import org.apache.poi.ss.formula.functions.IPMT;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.formula.functions.Code;
import org.apache.poi.ss.formula.functions.Substitute;
import org.apache.poi.ss.formula.functions.Replace;
import org.apache.poi.ss.formula.functions.Vlookup;
import org.apache.poi.ss.formula.functions.Hlookup;
import org.apache.poi.ss.formula.functions.Choose;
import org.apache.poi.ss.formula.functions.MatrixFunction;
import org.apache.poi.ss.formula.functions.Offset;
import org.apache.poi.ss.formula.functions.Columns;
import org.apache.poi.ss.formula.functions.Rows;
import org.apache.poi.ss.formula.functions.Areas;
import org.apache.poi.ss.formula.functions.Now;
import org.apache.poi.ss.formula.functions.WeekdayFunc;
import org.apache.poi.ss.formula.functions.CalendarFieldFunction;
import org.apache.poi.ss.formula.functions.TimeFunc;
import org.apache.poi.ss.formula.functions.DateFunc;
import org.apache.poi.ss.formula.functions.Match;
import org.apache.poi.ss.formula.functions.Irr;
import org.apache.poi.ss.formula.functions.Mirr;
import org.apache.poi.ss.formula.functions.Rate;
import org.apache.poi.ss.formula.functions.FinanceFunction;
import org.apache.poi.ss.formula.functions.Trend;
import org.apache.poi.ss.formula.functions.DStarRunner;
import org.apache.poi.ss.formula.functions.BooleanFunction;
import org.apache.poi.ss.formula.functions.Value;
import org.apache.poi.ss.formula.functions.TextFunction;
import org.apache.poi.ss.formula.functions.Rept;
import org.apache.poi.ss.formula.functions.Index;
import org.apache.poi.ss.formula.functions.Lookup;
import org.apache.poi.ss.formula.functions.Fixed;
import org.apache.poi.ss.formula.functions.NumericFunction;
import org.apache.poi.ss.formula.functions.Npv;
import org.apache.poi.ss.formula.functions.Na;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.formula.functions.RowFunc;
import org.apache.poi.ss.formula.functions.AggregateFunction;
import org.apache.poi.ss.formula.functions.LogicalFunction;
import org.apache.poi.ss.formula.functions.IfFunc;
import org.apache.poi.ss.formula.functions.Count;
import org.apache.poi.ss.formula.functions.Function;

public final class FunctionEval
{
    protected static final Function[] functions;
    
    private FunctionEval() {
    }
    
    private static Function[] produceFunctions() {
        final Function[] retval = { new Count(), new IfFunc(), LogicalFunction.ISNA, LogicalFunction.ISERROR, AggregateFunction.SUM, AggregateFunction.AVERAGE, AggregateFunction.MIN, AggregateFunction.MAX, new RowFunc(), new Column(), new Na(), new Npv(), AggregateFunction.STDEV, NumericFunction.DOLLAR, new Fixed(), NumericFunction.SIN, NumericFunction.COS, NumericFunction.TAN, NumericFunction.ATAN, NumericFunction.PI, NumericFunction.SQRT, NumericFunction.EXP, NumericFunction.LN, NumericFunction.LOG10, NumericFunction.ABS, NumericFunction.INT, NumericFunction.SIGN, NumericFunction.ROUND, new Lookup(), new Index(), new Rept(), TextFunction.MID, TextFunction.LEN, new Value(), BooleanFunction.TRUE, BooleanFunction.FALSE, BooleanFunction.AND, BooleanFunction.OR, BooleanFunction.NOT, NumericFunction.MOD, null, new DStarRunner(DStarRunner.DStarAlgorithmEnum.DSUM), null, new DStarRunner(DStarRunner.DStarAlgorithmEnum.DMIN), new DStarRunner(DStarRunner.DStarAlgorithmEnum.DMAX), null, AggregateFunction.VAR, null, TextFunction.TEXT, null, new Trend(), null, null, null, null, null, FinanceFunction.PV, FinanceFunction.FV, FinanceFunction.NPER, FinanceFunction.PMT, new Rate(), new Mirr(), new Irr(), NumericFunction.RAND, new Match(), DateFunc.instance, new TimeFunc(), CalendarFieldFunction.DAY, CalendarFieldFunction.MONTH, CalendarFieldFunction.YEAR, WeekdayFunc.instance, CalendarFieldFunction.HOUR, CalendarFieldFunction.MINUTE, CalendarFieldFunction.SECOND, new Now(), new Areas(), new Rows(), new Columns(), new Offset(), null, null, null, TextFunction.SEARCH, MatrixFunction.TRANSPOSE, null, null, null, null, null, null, null, null, null, null, null, null, null, NumericFunction.ATAN2, NumericFunction.ASIN, NumericFunction.ACOS, new Choose(), new Hlookup(), new Vlookup(), null, null, LogicalFunction.ISREF, null, null, null, NumericFunction.LOG, null, TextFunction.CHAR, TextFunction.LOWER, TextFunction.UPPER, TextFunction.PROPER, TextFunction.LEFT, TextFunction.RIGHT, TextFunction.EXACT, TextFunction.TRIM, new Replace(), new Substitute(), new Code(), null, null, TextFunction.FIND, null, LogicalFunction.ISERR, LogicalFunction.ISTEXT, LogicalFunction.ISNUMBER, LogicalFunction.ISBLANK, new T(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, TextFunction.CLEAN, MatrixFunction.MDETERM, MatrixFunction.MINVERSE, MatrixFunction.MMULT, null, new IPMT(), new PPMT(), new Counta(), null, null, null, null, null, null, null, null, null, null, null, null, null, AggregateFunction.PRODUCT, NumericFunction.FACT, null, null, null, null, null, LogicalFunction.ISNONTEXT, null, null, null, AggregateFunction.VARP, null, null, NumericFunction.TRUNC, LogicalFunction.ISLOGICAL, null, null, null, null, null, null, null, null, null, null, null, null, null, NumericFunction.ROUNDUP, NumericFunction.ROUNDDOWN, null, null, new Rank(), null, null, new Address(), new Days360(), new Today(), null, null, null, null, null, AggregateFunction.MEDIAN, new Sumproduct(), NumericFunction.SINH, NumericFunction.COSH, NumericFunction.TANH, NumericFunction.ASINH, NumericFunction.ACOSH, NumericFunction.ATANH, new DStarRunner(DStarRunner.DStarAlgorithmEnum.DGET), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, Frequency.instance, null, null, null, null, null, null, null, null, new Errortype(), null, null, null, null, null, null, null, AggregateFunction.AVEDEV, null, null, null, null, null, null, NumericFunction.COMBIN, null, null, new Even(), null, null, null, null, null, NumericFunction.FLOOR, null, null, NumericFunction.CEILING, null, null, null, null, null, null, null, null, null, new Odd(), null, NumericFunction.POISSON, null, null, new Sumxmy2(), new Sumx2my2(), new Sumx2py2(), null, null, null, null, null, new Intercept(), null, null, null, new Slope(), null, null, AggregateFunction.DEVSQ, AggregateFunction.GEOMEAN, null, AggregateFunction.SUMSQ, null, null, null, AggregateFunction.LARGE, AggregateFunction.SMALL, null, AggregateFunction.PERCENTILE, null, new Mode(), null, null, null, null, null, TextFunction.CONCATENATE, NumericFunction.POWER, null, null, null, null, NumericFunction.RADIANS, NumericFunction.DEGREES, new Subtotal(), new Sumif(), new Countif(), new Countblank(), null, null, null, null, null, null, new Roman(), null, null, null, null, new Hyperlink(), null, null, MinaMaxa.MAXA, MinaMaxa.MINA, null, null, null, null };
        for (int i = 0; i < retval.length; ++i) {
            final Function f = retval[i];
            if (f == null) {
                final FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByIndex(i);
                if (fm != null) {
                    retval[i] = new NotImplementedFunction(fm.getName());
                }
            }
        }
        return retval;
    }
    
    public static Function getBasicFunction(final int functionIndex) {
        switch (functionIndex) {
            case 148:
            case 255: {
                return null;
            }
            default: {
                final Function result = FunctionEval.functions[functionIndex];
                if (result == null) {
                    throw new NotImplementedException("FuncIx=" + functionIndex);
                }
                return result;
            }
        }
    }
    
    public static void registerFunction(final String name, final Function func) {
        final FunctionMetadata metaData = FunctionMetadataRegistry.getFunctionByName(name);
        if (metaData == null) {
            if (AnalysisToolPak.isATPFunction(name)) {
                throw new IllegalArgumentException(name + " is a function from the Excel Analysis Toolpack. Use AnalysisToolpack.registerFunction(String name, FreeRefFunction func) instead.");
            }
            throw new IllegalArgumentException("Unknown function: " + name);
        }
        else {
            final int idx = metaData.getIndex();
            if (FunctionEval.functions[idx] instanceof NotImplementedFunction) {
                FunctionEval.functions[idx] = func;
                return;
            }
            throw new IllegalArgumentException("POI already implememts " + name + ". You cannot override POI's implementations of Excel functions");
        }
    }
    
    public static Collection<String> getSupportedFunctionNames() {
        final Collection<String> lst = new TreeSet<String>();
        for (int i = 0; i < FunctionEval.functions.length; ++i) {
            final Function func = FunctionEval.functions[i];
            final FunctionMetadata metaData = FunctionMetadataRegistry.getFunctionByIndex(i);
            if (func != null && !(func instanceof NotImplementedFunction)) {
                lst.add(metaData.getName());
            }
        }
        lst.add("INDIRECT");
        return Collections.unmodifiableCollection((Collection<? extends String>)lst);
    }
    
    public static Collection<String> getNotSupportedFunctionNames() {
        final Collection<String> lst = new TreeSet<String>();
        for (int i = 0; i < FunctionEval.functions.length; ++i) {
            final Function func = FunctionEval.functions[i];
            if (func != null && func instanceof NotImplementedFunction) {
                final FunctionMetadata metaData = FunctionMetadataRegistry.getFunctionByIndex(i);
                lst.add(metaData.getName());
            }
        }
        lst.remove("INDIRECT");
        return Collections.unmodifiableCollection((Collection<? extends String>)lst);
    }
    
    static {
        functions = produceFunctions();
    }
    
    private static final class FunctionID
    {
        public static final int IF = 1;
        public static final int SUM = 4;
        public static final int OFFSET = 78;
        public static final int CHOOSE = 100;
        public static final int INDIRECT = 148;
        public static final int EXTERNAL_FUNC = 255;
    }
}
