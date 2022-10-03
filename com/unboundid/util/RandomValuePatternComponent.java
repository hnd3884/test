package com.unboundid.util;

import java.text.DecimalFormat;
import java.util.Random;

final class RandomValuePatternComponent extends ValuePatternComponent
{
    private static final long serialVersionUID = -670528378158953667L;
    private final long lowerBound;
    private final long span;
    private final Random seedRandom;
    private final String formatString;
    private final ThreadLocal<DecimalFormat> decimalFormat;
    private final ThreadLocal<Random> random;
    
    RandomValuePatternComponent(final long lowerBound, final long upperBound, final long seed, final String formatString) {
        if (lowerBound == upperBound) {
            this.lowerBound = lowerBound;
            this.span = 1L;
        }
        else if (lowerBound > upperBound) {
            this.lowerBound = upperBound;
            this.span = lowerBound - upperBound + 1L;
        }
        else {
            this.lowerBound = lowerBound;
            this.span = upperBound - lowerBound + 1L;
        }
        this.seedRandom = new Random(seed);
        this.random = new ThreadLocal<Random>();
        this.formatString = formatString;
        this.decimalFormat = new ThreadLocal<DecimalFormat>();
    }
    
    @Override
    void append(final StringBuilder buffer) {
        Random r = this.random.get();
        if (r == null) {
            r = new Random(this.seedRandom.nextLong());
            this.random.set(r);
        }
        final long value = (r.nextLong() & 0x7FFFFFFFL) % this.span + this.lowerBound;
        if (this.formatString == null) {
            buffer.append(value);
        }
        else {
            DecimalFormat f = this.decimalFormat.get();
            if (f == null) {
                f = new DecimalFormat(this.formatString);
                this.decimalFormat.set(f);
            }
            buffer.append(f.format(value));
        }
    }
    
    @Override
    boolean supportsBackReference() {
        return true;
    }
}
