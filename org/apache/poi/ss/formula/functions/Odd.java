package org.apache.poi.ss.formula.functions;

public final class Odd extends NumericFunction.OneArg
{
    private static final long PARITY_MASK = -2L;
    
    @Override
    protected double evaluate(final double d) {
        if (d == 0.0) {
            return 1.0;
        }
        return (d > 0.0) ? ((double)calcOdd(d)) : ((double)(-calcOdd(-d)));
    }
    
    private static long calcOdd(final double d) {
        final double dpm1 = d + 1.0;
        final long x = (long)dpm1 & 0xFFFFFFFFFFFFFFFEL;
        return (Double.compare((double)x, dpm1) == 0) ? (x - 1L) : (x + 1L);
    }
}
