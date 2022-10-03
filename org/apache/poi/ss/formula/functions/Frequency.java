package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.CacheAreaEval;
import java.util.Arrays;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Frequency extends Fixed2ArgFunction
{
    public static final Function instance;
    
    private Frequency() {
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        final MatrixFunction.MutableValueCollector collector = new MatrixFunction.MutableValueCollector(false, false);
        double[] values;
        double[] bins;
        try {
            values = collector.collectValues(arg0);
            bins = collector.collectValues(arg1);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
        final int[] histogram = histogram(values, bins);
        final NumberEval[] result = Arrays.stream(histogram).boxed().map((java.util.function.Function<? super Integer, ?>)NumberEval::new).toArray(NumberEval[]::new);
        return new CacheAreaEval(srcRowIndex, srcColumnIndex, srcRowIndex + result.length - 1, srcColumnIndex, result);
    }
    
    static int findBin(final double value, final double[] bins) {
        final int idx = Arrays.binarySearch(bins, value);
        return (idx >= 0) ? (idx + 1) : (-idx);
    }
    
    static int[] histogram(final double[] values, final double[] bins) {
        final int[] histogram = new int[bins.length + 1];
        for (final double val : values) {
            final int[] array = histogram;
            final int n = findBin(val, bins) - 1;
            ++array[n];
        }
        return histogram;
    }
    
    static {
        instance = new Frequency();
    }
}
